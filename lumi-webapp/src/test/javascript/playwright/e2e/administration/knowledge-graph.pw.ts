import { expect, test } from '@playwright/test';

test.describe('UC-14: Manage Knowledge Graph E2E Tests', () => {
  const subjectsMock = [
    { id: 101, name: 'Mathematics', description: 'Mathematics Subject' },
    { id: 102, name: 'History', description: 'History Subject' },
  ];

  const mathGraphMock = {
    subjectId: 101,
    subjectName: 'Mathematics',
    nodes: [
      { id: 1, title: 'Số học cơ bản', type: 'CONCEPT', subjectId: 101 },
      { id: 2, title: 'Phân số', type: 'CONCEPT', subjectId: 101 },
    ],
    edges: [{ id: 10, fromNodeId: 1, toNodeId: 2, relationshipType: 'PREREQUISITE', subjectId: 101 }],
  };

  const historyGraphMock = {
    subjectId: 102,
    subjectName: 'History',
    nodes: [{ id: 3, title: 'Thời kỳ đồ đá', type: 'TOPIC', subjectId: 102 }],
    edges: [],
  };

  test.beforeEach(async ({ page }) => {
    // Mock user account session as Admin
    await page.route('**/api/account', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          login: 'admin',
          firstName: 'Administrator',
          lastName: 'Lumi',
          email: 'admin@lumi.edu.vn',
          activated: true,
          langKey: 'vi',
          authorities: ['ROLE_ADMIN', 'ROLE_USER'],
        }),
      });
    });

    // Provide dynamic subjects mock
    await page.route('**/api/admin/knowledge-graph/subjects', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify(subjectsMock),
      });
    });

    // Mock graphs for each subject
    await page.route('**/api/admin/knowledge-graph/101', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify(mathGraphMock),
      });
    });

    await page.route('**/api/admin/knowledge-graph/102', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify(historyGraphMock),
      });
    });
  });

  test('D1: Admin page access should load Knowledge Graph page without runtime errors', async ({ page }) => {
    const consoleErrors: string[] = [];
    page.on('console', msg => {
      if (msg.type() === 'error') {
        consoleErrors.push(msg.text());
      }
    });

    await page.goto('/admin/knowledge-graph');

    // Heading verification
    const heading = page.locator('.kg-title-area h1');
    await expect(heading).toBeVisible();
    await expect(heading).toContainText('Quản lý Cây tri thức');

    // Canvas and workspace container verification
    await expect(page.locator('#kg-canvas-viewport')).toBeVisible();
    await expect(page.locator('.kg-sidebar-panel')).toBeVisible();

    // Verify no critical JavaScript runtime errors occurred (ignore FontAwesome icon registration warnings)
    const runtimeErrors = consoleErrors.filter(err => !err.includes('favicon') && !err.includes('404') && !err.includes('FontAwesomeIcon'));
    expect(runtimeErrors).toHaveLength(0);
  });

  test('D2: Subject switching between Mathematics and History should update graphs and counts dynamically', async ({ page }) => {
    await page.goto('/admin/knowledge-graph');

    // Confirm Mathematics is initially loaded
    const mathPill = page.locator('#subject-pill-101');
    await expect(mathPill).toBeVisible();
    await expect(mathPill).toHaveClass(/active/);

    // Verify initial node and edge counts for Mathematics (2 nodes, 1 edge)
    await expect(page.locator('.canvas-meta .meta-pill').first()).toContainText('Nút: 2');
    await expect(page.locator('.canvas-meta .meta-pill').last()).toContainText('Cạnh tiên quyết: 1');
    await expect(page.locator('#node-card-1')).toBeVisible();
    await expect(page.locator('#node-card-2')).toBeVisible();

    // Switch to History (ID: 102)
    const historyPill = page.locator('#subject-pill-102');
    await historyPill.click();

    await expect(historyPill).toHaveClass(/active/);
    await expect(mathPill).not.toHaveClass(/active/);

    // Verify History data (1 node, 0 edges)
    await expect(page.locator('.canvas-meta .meta-pill').first()).toContainText('Nút: 1');
    await expect(page.locator('.canvas-meta .meta-pill').last()).toContainText('Cạnh tiên quyết: 0');
    await expect(page.locator('#node-card-3')).toBeVisible();
    await expect(page.locator('#node-card-1')).not.toBeVisible();

    // Switch back to Mathematics
    await mathPill.click();
    await expect(mathPill).toHaveClass(/active/);
    await expect(page.locator('.canvas-meta .meta-pill').first()).toContainText('Nút: 2');
    await expect(page.locator('#node-card-1')).toBeVisible();
  });

  test('D3: Draft editing allows adding new nodes and prerequisite edges in draft state', async ({ page }) => {
    await page.goto('/admin/knowledge-graph');

    // Add a new node to the draft
    await page.locator('#node-title-input').fill('Đại số tuyến tính');
    await page.locator('#node-type-select').selectOption('SKILL');
    await page.locator('#btn-add-node').click();

    // Verify node count increases to 3 and the new node card appears
    await expect(page.locator('.canvas-meta .meta-pill').first()).toContainText('Nút: 3');
    await expect(page.locator('.node-title', { hasText: 'Đại số tuyến tính' })).toBeVisible();

    // Connect node 2 (Phân số) -> new draft node
    await page.locator('#from-node-select').selectOption({ label: 'Phân số' });
    await page.locator('#to-node-select').selectOption({ label: 'Đại số tuyến tính' });
    await page.locator('#btn-add-edge').click();

    // Verify edge count increases to 2
    await expect(page.locator('.canvas-meta .meta-pill').last()).toContainText('Cạnh tiên quyết: 2');
    await expect(page.locator('#kg-edge-list')).toContainText('Phân số');
    await expect(page.locator('#kg-edge-list')).toContainText('Đại số tuyến tính');
  });

  test('D4: Cycle validation detects cycle, displays cycle path, preserves draft, and succeeds after recovery', async ({ page }) => {
    let saveAttempt = 0;

    await page.route('**/api/admin/knowledge-graph/101', async route => {
      if (route.request().method() === 'PUT') {
        saveAttempt++;
        if (saveAttempt === 1) {
          // Reject cycle with actionable cyclePath
          await route.fulfill({
            status: 400,
            contentType: 'application/problem+json',
            body: JSON.stringify({
              type: 'https://www.jhipster.tech/problem/cycle-detected',
              title: 'Cycle detected in knowledge graph',
              status: 400,
              detail: 'Cây kiến thức không được chứa chu trình: 2 -> 1 -> 2',
              cyclePath: '2 -> 1 -> 2',
              cycleNodeIds: [2, 1, 2],
            }),
          });
        } else {
          // Success on resubmission after recovery
          await route.fulfill({
            status: 200,
            contentType: 'application/json',
            body: JSON.stringify({
              subjectId: 101,
              subjectName: 'Mathematics',
              nodes: [
                { id: 1, title: 'Số học cơ bản', type: 'CONCEPT', subjectId: 101 },
                { id: 2, title: 'Phân số', type: 'CONCEPT', subjectId: 101 },
              ],
              edges: [{ id: 10, fromNodeId: 1, toNodeId: 2, relationshipType: 'PREREQUISITE', subjectId: 101 }],
            }),
          });
        }
      } else {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify(mathGraphMock),
        });
      }
    });

    await page.goto('/admin/knowledge-graph');

    // Currently: 1 -> 2. Now add reverse edge 2 -> 1 to form a cycle: 1 -> 2 -> 1
    await page.locator('#from-node-select').selectOption({ label: 'Phân số' }); // Node 2
    await page.locator('#to-node-select').selectOption({ label: 'Số học cơ bản' }); // Node 1
    await page.locator('#btn-add-edge').click();

    // Submit save
    await page.locator('#btn-save-graph').click();

    // Verify error banner is displayed with cycle path
    const errorAlert = page.locator('#kg-error-alert');
    await expect(errorAlert).toBeVisible();
    await expect(errorAlert).toContainText('2 -> 1 -> 2');
    await expect(page.locator('#kg-success-alert')).not.toBeVisible();

    // Verify draft is preserved (both edges still in list)
    await expect(page.locator('#kg-edge-list .edge-item')).toHaveCount(2);

    // RECOVERY: Delete the offending edge (2 -> 1)
    const deleteButtons = page.locator('#kg-edge-list .delete-edge-btn');
    await deleteButtons.last().click();

    // Now edge list has 1 valid edge (1 -> 2)
    await expect(page.locator('#kg-edge-list .edge-item')).toHaveCount(1);

    // Resubmit save
    await page.locator('#btn-save-graph').click();

    // Verify success banner appears and error alert disappears
    await expect(page.locator('#kg-success-alert')).toBeVisible();
    await expect(page.locator('#kg-success-alert')).toContainText('Lưu thành công!');
    await expect(errorAlert).not.toBeVisible();
  });

  test('D5: Persistence reconciles generated IDs and reload updates from server', async ({ page }) => {
    let savedPayload: any = null;

    await page.route('**/api/admin/knowledge-graph/101', async route => {
      if (route.request().method() === 'PUT') {
        savedPayload = JSON.parse(route.request().postData() || '{}');
        // Return graph with newly persisted node ID 999
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            subjectId: 101,
            subjectName: 'Mathematics',
            nodes: [
              { id: 1, title: 'Số học cơ bản', type: 'CONCEPT', subjectId: 101 },
              { id: 2, title: 'Phân số', type: 'CONCEPT', subjectId: 101 },
              { id: 999, title: 'Hàm số', type: 'CONCEPT', subjectId: 101 },
            ],
            edges: [
              { id: 10, fromNodeId: 1, toNodeId: 2, relationshipType: 'PREREQUISITE', subjectId: 101 },
              { id: 11, fromNodeId: 2, toNodeId: 999, relationshipType: 'PREREQUISITE', subjectId: 101 },
            ],
          }),
        });
      } else {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify(mathGraphMock),
        });
      }
    });

    await page.goto('/admin/knowledge-graph');

    // Add new node in draft (client assigns temporary negative ID)
    await page.locator('#node-title-input').fill('Hàm số');
    await page.locator('#btn-add-node').click();

    // Connect node 2 -> Hàm số
    await page.locator('#from-node-select').selectOption({ label: 'Phân số' });
    await page.locator('#to-node-select').selectOption({ label: 'Hàm số' });
    await page.locator('#btn-add-edge').click();

    // Save
    await page.locator('#btn-save-graph').click();

    // Confirm success alert appears only after backend confirms save
    await expect(page.locator('#kg-success-alert')).toBeVisible();

    // Verify reconciled persistent node ID 999 is in the DOM
    await expect(page.locator('#node-card-999')).toBeVisible();

    // Verify payload sent candidate graph with 3 nodes and 2 edges
    expect(savedPayload.nodes).toHaveLength(3);
    expect(savedPayload.edges).toHaveLength(2);
  });

  test('D6: Failure handling does not display false success alert on API error', async ({ page }) => {
    await page.route('**/api/admin/knowledge-graph/101', async route => {
      if (route.request().method() === 'PUT') {
        await route.fulfill({
          status: 500,
          contentType: 'application/problem+json',
          body: JSON.stringify({
            title: 'Internal Server Error',
            detail: 'Database lock timeout',
          }),
        });
      } else {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify(mathGraphMock),
        });
      }
    });

    await page.goto('/admin/knowledge-graph');

    // Add a draft node
    await page.locator('#node-title-input').fill('Xác suất thống kê');
    await page.locator('#btn-add-node').click();

    // Attempt save
    await page.locator('#btn-save-graph').click();

    // Assert error alert is visible
    await expect(page.locator('#kg-error-alert')).toBeVisible();

    // Assert NO false success alert is displayed
    await expect(page.locator('#kg-success-alert')).not.toBeVisible();

    // Assert draft node remains intact
    await expect(page.locator('.node-title', { hasText: 'Xác suất thống kê' })).toBeVisible();
  });

  test('D7: Responsive viewport and UI layout check', async ({ page }) => {
    // Test desktop viewport
    await page.setViewportSize({ width: 1280, height: 800 });
    await page.goto('/admin/knowledge-graph');
    await expect(page.locator('.kg-admin-container')).toBeVisible();
    await expect(page.locator('.kg-canvas-card')).toBeVisible();
    await expect(page.locator('.kg-sidebar-panel')).toBeVisible();

    // Test tablet viewport
    await page.setViewportSize({ width: 768, height: 1024 });
    await expect(page.locator('.kg-admin-container')).toBeVisible();
    await expect(page.locator('#kg-canvas-viewport')).toBeVisible();
  });
});
