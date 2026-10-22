import React from 'react';
import { Check, ChevronLeft, ChevronRight, ChevronsLeft, ChevronsRight, Eye, X } from 'lucide-react';
import type { InstructorApplication } from '../instructor-applications-api';
import { formatDate, fullName, statusLabel, type InstructorDialogMode } from '../instructor-application-format';

interface ApplicationTableProps {
  visibleItems: InstructorApplication[];
  loading: boolean;
  search: string;
  total: number;
  page: number;
  pageSize: number;
  pageCount: number;
  visiblePages: number[];
  setPage: (page: number) => void;
  openDialog: (item: InstructorApplication, dialog: InstructorDialogMode) => void;
}

const ApplicationTable = ({
  visibleItems,
  loading,
  search,
  total,
  page,
  pageSize,
  pageCount,
  visiblePages,
  setPage,
  openDialog,
}: ApplicationTableProps) => (
  <>
    <div className="instructor-applications-table-wrap">
      <table>
        <thead>
          <tr>
            <th>Giảng viên</th>
            <th>Liên hệ</th>
            <th>Trường học</th>
            <th>Ngày đăng ký</th>
            <th>Trạng thái</th>
            <th>Thao tác</th>
          </tr>
        </thead>
        <tbody>
          {visibleItems.map(item => (
            <tr key={item.userId}>
              <td>
                <strong>{fullName(item)}</strong>
                <small>@{item.login}</small>
              </td>
              <td>
                {item.email || '—'}
                <small>{item.phoneNumber || ''}</small>
              </td>
              <td>{item.schoolName || '—'}</td>
              <td>{formatDate(item.submittedAt)}</td>
              <td>
                <span className={`instructor-applications-status ${item.status.toLowerCase()}`}>{statusLabel(item.status)}</span>
              </td>
              <td>
                <div className="instructor-applications-row-actions">
                  <button type="button" onClick={() => openDialog(item, 'details')} aria-label={`Xem hồ sơ ${fullName(item)}`}>
                    <Eye size={16} /> Xem
                  </button>
                  {item.status === 'PENDING' && (
                    <>
                      <button type="button" onClick={() => openDialog(item, 'approve')} aria-label={`Duyệt ${fullName(item)}`}>
                        <Check size={16} /> Duyệt
                      </button>
                      <button
                        type="button"
                        className="danger"
                        onClick={() => openDialog(item, 'reject')}
                        aria-label={`Từ chối ${fullName(item)}`}
                      >
                        <X size={16} /> Từ chối
                      </button>
                    </>
                  )}
                </div>
              </td>
            </tr>
          ))}
          {!loading && !visibleItems.length && (
            <tr>
              <td colSpan={6} className="instructor-applications-empty">
                {search ? 'Không có kết quả trên trang này.' : 'Chưa có hồ sơ ở trạng thái này.'}
              </td>
            </tr>
          )}
          {loading && (
            <tr>
              <td colSpan={6} className="instructor-applications-empty">
                Đang tải hồ sơ...
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
    {!search && total > 0 && (
      <div className="instructor-applications-footer">
        <span>
          Hiển thị {page * pageSize + 1}–{Math.min((page + 1) * pageSize, total)} trong tổng số {total} hồ sơ.
        </span>
        <nav className="instructor-applications-pagination" aria-label="Phân trang giảng viên">
          <button type="button" aria-label="Trang đầu" disabled={page === 0 || loading} onClick={() => setPage(0)}>
            <ChevronsLeft size={16} aria-hidden="true" />
          </button>
          <button type="button" aria-label="Trang trước" disabled={page === 0 || loading} onClick={() => setPage(page - 1)}>
            <ChevronLeft size={16} aria-hidden="true" />
          </button>
          {visiblePages.map(visiblePage => (
            <button
              key={visiblePage}
              type="button"
              className={visiblePage === page ? 'active' : undefined}
              aria-label={`Trang ${visiblePage + 1}`}
              aria-current={visiblePage === page ? 'page' : undefined}
              disabled={loading}
              onClick={() => setPage(visiblePage)}
            >
              {visiblePage + 1}
            </button>
          ))}
          <button type="button" aria-label="Trang sau" disabled={page + 1 >= pageCount || loading} onClick={() => setPage(page + 1)}>
            <ChevronRight size={16} aria-hidden="true" />
          </button>
          <button type="button" aria-label="Trang cuối" disabled={page + 1 >= pageCount || loading} onClick={() => setPage(pageCount - 1)}>
            <ChevronsRight size={16} aria-hidden="true" />
          </button>
        </nav>
      </div>
    )}
  </>
);

export default ApplicationTable;
