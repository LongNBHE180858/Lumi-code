import { beforeEach, describe, expect, it } from 'vitest';
import React from 'react';
import { MemoryRouter } from 'react-router';

import { render, waitFor } from '@testing-library/react';

import { AccountMenu } from './account';

describe('AccountMenu', () => {
  let mountedWrapper: string | undefined;

  const authenticatedWrapper = async () => {
    if (!mountedWrapper) {
      const { container } = render(
        <MemoryRouter>
          <AccountMenu isAuthenticated />
        </MemoryRouter>,
      );
      await waitFor(() => expect(container.querySelector('.dropdown-menu')).not.toBeNull());
      mountedWrapper = container.innerHTML;
    }
    return mountedWrapper;
  };
  const guestWrapper = async () => {
    if (!mountedWrapper) {
      const { container } = render(
        <MemoryRouter>
          <AccountMenu />
        </MemoryRouter>,
      );
      await waitFor(() => expect(container.querySelector('#login-item')).not.toBeNull());
      mountedWrapper = container.innerHTML;
    }
    return mountedWrapper;
  };

  beforeEach(() => {
    mountedWrapper = undefined;
  });

  it('should render an authenticated AccountMenu component', async () => {
    const html = await authenticatedWrapper();

    expect(html).not.toContain('/login');
    expect(html).toContain('/logout');
  });

  it('should render a guest AccountMenu component', async () => {
    const html = await guestWrapper();

    expect(html).toContain('/login');
    expect(html).not.toContain('/logout');
  });

  it('should show Reviewer role and course review link', async () => {
    const { container } = render(
      <MemoryRouter>
        <AccountMenu isAuthenticated account={{ authorities: ['ROLE_USER', 'ROLE_REVIEWER'] }} />
      </MemoryRouter>,
    );

    await waitFor(() => expect(container.querySelector('[data-cy="reviewerManagement"]')).not.toBeNull());
    expect(container.querySelector('.lumi-user-role')?.textContent).toBe('Kiểm duyệt viên');
    expect(container.querySelector('[data-cy="reviewerManagement"]')?.getAttribute('href')).toBe('/admin/course-review');
    expect(container.querySelector('[data-cy="adminManagement"]')).toBeNull();
  });

  it('should only highlight "Tạo khóa học mới" and not "Khóa học của tôi" when at /instructor/courses/new', async () => {
    const { container } = render(
      <MemoryRouter initialEntries={['/instructor/courses/new']}>
        <AccountMenu isAuthenticated account={{ authorities: ['ROLE_INSTRUCTOR'] }} />
      </MemoryRouter>,
    );

    await waitFor(() => expect(container.querySelector('[data-cy="instructorCourses"]')).not.toBeNull());

    const instructorCoursesLink = container.querySelector('[data-cy="instructorCourses"]');
    const builderCoursesLink = container.querySelector('[data-cy="builderCourses"]');

    expect(instructorCoursesLink).not.toBeNull();
    expect(builderCoursesLink).not.toBeNull();
    expect(instructorCoursesLink?.classList.contains('active')).toBe(false);
    expect(builderCoursesLink?.classList.contains('active')).toBe(true);
  });

  it('should only highlight "Khóa học của tôi" and not "Tạo khóa học mới" when at /instructor/courses', async () => {
    const { container } = render(
      <MemoryRouter initialEntries={['/instructor/courses']}>
        <AccountMenu isAuthenticated account={{ authorities: ['ROLE_INSTRUCTOR'] }} />
      </MemoryRouter>,
    );

    await waitFor(() => expect(container.querySelector('[data-cy="instructorCourses"]')).not.toBeNull());

    const instructorCoursesLink = container.querySelector('[data-cy="instructorCourses"]');
    const builderCoursesLink = container.querySelector('[data-cy="builderCourses"]');

    expect(instructorCoursesLink).not.toBeNull();
    expect(builderCoursesLink).not.toBeNull();
    expect(instructorCoursesLink?.classList.contains('active')).toBe(true);
    expect(builderCoursesLink?.classList.contains('active')).toBe(false);
  });
});
