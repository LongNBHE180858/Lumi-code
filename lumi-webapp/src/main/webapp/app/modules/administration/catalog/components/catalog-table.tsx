import React from 'react';
import { Check, ChevronLeft, ChevronRight, ChevronsLeft, ChevronsRight, Pencil, X } from 'lucide-react';
import type { CatalogItem, CatalogKind } from '../catalog-api';

interface CatalogTableProps {
  kind: CatalogKind;
  items: CatalogItem[];
  pageItems: CatalogItem[];
  filtered: CatalogItem[];
  loading: boolean;
  busyId: number | null;
  columnCount: number;
  currentPage: number;
  pageCount: number;
  pageSize: number;
  visiblePages: number[];
  openForm: (item: CatalogItem) => void;
  setActive: (item: CatalogItem) => void;
  setPage: (page: number) => void;
}

const CatalogTable = ({
  kind,
  items,
  pageItems,
  filtered,
  loading,
  busyId,
  columnCount,
  currentPage,
  pageCount,
  pageSize,
  visiblePages,
  openForm,
  setActive,
  setPage,
}: CatalogTableProps) => (
  <>
    <div className="admin-catalog-table-wrap">
      <table>
        <thead>
          <tr>
            <th>Tên</th>
            {kind === 'categories' && <th>Danh mục cha</th>}
            {kind === 'grades' && <th>Số lớp</th>}
            {(kind === 'subjects' || kind === 'categories') && <th>Mô tả</th>}
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          {!loading &&
            pageItems.map(item => (
              <tr key={item.id}>
                <td>
                  <strong>{item.name}</strong>
                </td>
                {kind === 'categories' && <td>{items.find(parent => parent.id === item.parentId)?.name ?? '—'}</td>}
                {kind === 'grades' && <td>Lớp {item.level}</td>}
                {(kind === 'subjects' || kind === 'categories') && <td>{item.description || '—'}</td>}
                <td>
                  <span className={`admin-catalog-status${item.active ? ' active' : ''}`}>{item.active ? 'Hoạt động' : 'Tạm ngưng'}</span>
                </td>
                <td>
                  <div className="admin-catalog-row-actions">
                    <button type="button" onClick={() => openForm(item)} aria-label={`Sửa ${item.name}`}>
                      <Pencil size={15} aria-hidden="true" /> Sửa
                    </button>
                    <button
                      type="button"
                      className={item.active ? 'danger' : ''}
                      disabled={busyId === item.id}
                      onClick={() => setActive(item)}
                      aria-label={`${item.active ? 'Tạm ngưng' : 'Kích hoạt'} ${item.name}`}
                    >
                      {item.active ? <X size={15} aria-hidden="true" /> : <Check size={15} aria-hidden="true" />}
                      {item.active ? 'Tạm ngưng' : 'Kích hoạt'}
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          {!loading && pageItems.length === 0 && (
            <tr>
              <td className="admin-catalog-empty" colSpan={columnCount}>
                Không có mục phù hợp.
              </td>
            </tr>
          )}
          {loading && (
            <tr>
              <td className="admin-catalog-empty" colSpan={columnCount}>
                Đang tải danh sách...
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>

    {filtered.length > 0 && (
      <div className="admin-catalog-pagination-footer">
        <span>
          Hiển thị {currentPage * pageSize + 1}–{Math.min((currentPage + 1) * pageSize, filtered.length)} trong tổng số {filtered.length}{' '}
          mục.
        </span>
        <nav className="admin-catalog-pagination" aria-label="Phân trang danh mục">
          <button type="button" aria-label="Trang đầu" disabled={currentPage === 0} onClick={() => setPage(0)}>
            <ChevronsLeft size={16} />
          </button>
          <button type="button" aria-label="Trang trước" disabled={currentPage === 0} onClick={() => setPage(currentPage - 1)}>
            <ChevronLeft size={16} />
          </button>
          {visiblePages.map(visiblePage => (
            <button
              key={visiblePage}
              type="button"
              className={visiblePage === currentPage ? 'active' : ''}
              aria-label={`Trang ${visiblePage + 1}`}
              aria-current={visiblePage === currentPage ? 'page' : undefined}
              onClick={() => setPage(visiblePage)}
            >
              {visiblePage + 1}
            </button>
          ))}
          <button type="button" aria-label="Trang sau" disabled={currentPage + 1 >= pageCount} onClick={() => setPage(currentPage + 1)}>
            <ChevronRight size={16} />
          </button>
          <button type="button" aria-label="Trang cuối" disabled={currentPage + 1 >= pageCount} onClick={() => setPage(pageCount - 1)}>
            <ChevronsRight size={16} />
          </button>
        </nav>
      </div>
    )}
  </>
);

export default CatalogTable;
