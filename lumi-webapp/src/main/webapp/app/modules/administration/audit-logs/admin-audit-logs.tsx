import './admin-audit-logs.scss';

import axios from 'axios';
import React, { useEffect, useState } from 'react';
import { ChevronLeft, ChevronRight, ChevronsLeft, ChevronsRight, Download, Search, X } from 'lucide-react';
import AdminLayout from '../layout/admin-layout';
import { auditLogPageSize, auditLogsApi, type AuditLogFilters, type AuditLogPage } from './audit-logs-api';

const emptyFilters: AuditLogFilters = { actor: '', action: '', targetEntity: '', targetId: '', from: '', to: '' };

const getDetail = (details: string | null) => {
  if (!details) return null;
  let content: string;
  try {
    const value = JSON.parse(details) as { userAgent?: string };
    if (!value.userAgent) return details.slice(0, 80);
    content = value.userAgent;
  } catch {
    content = details;
  }

  if (content === 'Unknown Device') return 'Thiết bị không xác định';
  if (!/Edg\/|Firefox\/|Chrome\/|Safari\/|Mozilla\//.test(content)) return content.slice(0, 80);

  const browser = content.includes('Edg/')
    ? 'Microsoft Edge'
    : content.includes('Firefox/')
      ? 'Firefox'
      : content.includes('Chrome/')
        ? 'Chrome'
        : content.includes('Safari/')
          ? 'Safari'
          : 'Trình duyệt không xác định';
  const system = /iPhone/i.test(content)
    ? 'iPhone'
    : /iPad/i.test(content)
      ? 'iPad'
      : /Android/i.test(content)
        ? 'Android'
        : /Macintosh|Mac OS/i.test(content)
          ? 'macOS'
          : /Windows/i.test(content)
            ? 'Windows'
            : /Linux/i.test(content)
              ? 'Linux'
              : 'Hệ điều hành không xác định';
  return `${browser} · ${system}`;
};

const actionLabel = (action: string) => {
  switch (action) {
    case 'LOGIN_SUCCESS':
      return 'Đăng nhập thành công';
    default:
      return action;
  }
};

const formatDate = (value: string | null) => (value ? new Date(value).toLocaleString('vi-VN') : '—');

const AdminAuditLogs = () => {
  const [draft, setDraft] = useState<AuditLogFilters>(emptyFilters);
  const [filters, setFilters] = useState<AuditLogFilters>(emptyFilters);
  const [page, setPage] = useState(0);
  const [result, setResult] = useState<AuditLogPage | null>(null);
  const [loading, setLoading] = useState(false);
  const [exporting, setExporting] = useState(false);
  const [exportOpen, setExportOpen] = useState(false);
  const [exportMode, setExportMode] = useState<'count' | 'range'>('count');
  const [exportCount, setExportCount] = useState('100');
  const [exportFrom, setExportFrom] = useState('');
  const [exportTo, setExportTo] = useState('');
  const [exportError, setExportError] = useState('');
  const [error, setError] = useState('');
  const totalItems = result?.totalElements ?? 0;
  const totalPages = result?.totalPages ?? 0;
  const visiblePageCount = Math.min(5, totalPages);
  const firstVisiblePage = Math.max(0, Math.min(page - 2, totalPages - visiblePageCount));
  const visiblePages = Array.from({ length: visiblePageCount }, (_, index) => firstVisiblePage + index);

  useEffect(() => {
    if (!exportOpen) return;
    const closeOnEscape = (event: KeyboardEvent) => {
      if (event.key === 'Escape' && !exporting) setExportOpen(false);
    };
    window.addEventListener('keydown', closeOnEscape);
    return () => window.removeEventListener('keydown', closeOnEscape);
  }, [exportOpen, exporting]);

  useEffect(() => {
    let active = true;
    setLoading(true);
    setError('');
    auditLogsApi
      .list(filters, page)
      .then(response => {
        if (active) setResult(response.data);
      })
      .catch(() => {
        if (active) setError('Không tải được nhật ký hoạt động. Vui lòng thử lại.');
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, [filters, page]);

  const update = (field: keyof AuditLogFilters, value: string) => setDraft(current => ({ ...current, [field]: value }));

  const search = (event: React.FormEvent) => {
    event.preventDefault();
    if (draft.from && draft.to && draft.from > draft.to) {
      setError('Thời gian bắt đầu phải trước thời gian kết thúc.');
      return;
    }
    setPage(0);
    setFilters({ ...draft });
  };

  const exportExcel = async (event: React.FormEvent) => {
    event.preventDefault();
    const count = Number(exportCount);
    if (exportMode === 'count' && (!Number.isInteger(count) || count < 1 || count > 10000)) {
      setExportError('Nhập số dòng từ 1 đến 10.000.');
      return;
    }
    if (exportMode === 'range' && (!exportFrom || !exportTo || exportFrom > exportTo)) {
      setExportError('Chọn thời điểm bắt đầu và kết thúc hợp lệ.');
      return;
    }
    setExporting(true);
    setExportError('');
    try {
      const response = await auditLogsApi.export(
        filters,
        exportMode === 'count' ? { mode: 'count', limit: count } : { mode: 'range', from: exportFrom, to: exportTo },
      );
      const url = URL.createObjectURL(response.data);
      const anchor = document.createElement('a');
      anchor.href = url;
      anchor.download = 'lumi-audit-logs.xlsx';
      document.body.appendChild(anchor);
      anchor.click();
      anchor.remove();
      URL.revokeObjectURL(url);
      setExportOpen(false);
    } catch (cause) {
      setExportError(
        axios.isAxiosError(cause) && cause.response?.status === 413
          ? 'Khoảng thời gian có hơn 10.000 bản ghi. Hãy chọn khoảng ngắn hơn.'
          : 'Không xuất được Excel. Vui lòng thử lại.',
      );
    } finally {
      setExporting(false);
    }
  };

  return (
    <AdminLayout>
      <main className="admin-page-content admin-audit-logs">
        <div className="admin-audit-logs-heading">
          <div>
            <h1>Nhật ký hoạt động</h1>
            <p>Lịch sử đăng nhập và các hành động quan trọng trong hệ thống.</p>
          </div>
          <button
            type="button"
            onClick={() => {
              setExportFrom(filters.from);
              setExportTo(filters.to);
              setExportError('');
              setExportOpen(true);
            }}
            disabled={exporting}
          >
            <Download size={18} aria-hidden="true" /> Xuất Excel
          </button>
        </div>
        <form className="admin-audit-logs-filters" onSubmit={search}>
          <div className="admin-audit-logs-main-filters">
            <label>
              Tài khoản
              <input value={draft.actor} onChange={event => update('actor', event.target.value)} placeholder="Tên đăng nhập" />
            </label>
            <label>
              Hành động
              <select value={draft.action} onChange={event => update('action', event.target.value)}>
                <option value="">Tất cả hành động</option>
                <option value="LOGIN_SUCCESS">Đăng nhập thành công</option>
              </select>
            </label>
            <label>
              Từ thời điểm
              <input type="datetime-local" value={draft.from} onChange={event => update('from', event.target.value)} />
            </label>
            <label>
              Đến thời điểm
              <input type="datetime-local" value={draft.to} onChange={event => update('to', event.target.value)} />
            </label>
          </div>
          <details className="admin-audit-logs-advanced">
            <summary>Lọc nâng cao</summary>
            <div className="admin-audit-logs-advanced-fields">
              <label>
                Loại đối tượng
                <input
                  value={draft.targetEntity}
                  onChange={event => update('targetEntity', event.target.value)}
                  placeholder="Ví dụ: User"
                />
              </label>
              <label>
                ID đối tượng
                <input value={draft.targetId} onChange={event => update('targetId', event.target.value)} />
              </label>
            </div>
          </details>
          <div className="admin-audit-logs-filter-actions">
            <button type="submit">
              <Search size={17} aria-hidden="true" /> Tìm kiếm
            </button>
            <button
              type="button"
              className="secondary"
              onClick={() => {
                setDraft(emptyFilters);
                setFilters(emptyFilters);
                setPage(0);
              }}
            >
              Xóa bộ lọc
            </button>
          </div>
        </form>
        {error && (
          <p className="admin-audit-logs-error" role="alert">
            {error}
          </p>
        )}
        <div className="admin-audit-logs-table-wrap">
          <table>
            <thead>
              <tr>
                <th>Thời điểm</th>
                <th>Tài khoản</th>
                <th>Hành động</th>
                <th>Đối tượng</th>
                <th>IP</th>
                <th>Chi tiết</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={6}>Đang tải...</td>
                </tr>
              ) : result?.content.length ? (
                result.content.map(item => {
                  return (
                    <tr key={item.id}>
                      <td>{formatDate(item.occurredAt)}</td>
                      <td>{item.actorLogin}</td>
                      <td title={item.action}>{actionLabel(item.action)}</td>
                      <td>
                        {item.targetEntity} #{item.targetId}
                      </td>
                      <td>{item.ipAddress || '—'}</td>
                      <td className="admin-audit-logs-device">{getDetail(item.details) || '—'}</td>
                    </tr>
                  );
                })
              ) : (
                <tr>
                  <td colSpan={6}>Không có bản ghi phù hợp.</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
        {totalItems > 0 && (
          <div className="admin-audit-logs-pagination-footer">
            <span>
              Hiển thị {page * auditLogPageSize + 1}–{Math.min((page + 1) * auditLogPageSize, totalItems)} trong tổng số {totalItems} bản
              ghi.
            </span>
            <nav className="admin-audit-logs-pagination" aria-label="Phân trang nhật ký hoạt động">
              <button type="button" aria-label="Trang đầu" disabled={loading || page === 0} onClick={() => setPage(0)}>
                <ChevronsLeft size={16} aria-hidden="true" />
              </button>
              <button type="button" aria-label="Trang trước" disabled={loading || page === 0} onClick={() => setPage(page - 1)}>
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
              <button type="button" aria-label="Trang sau" disabled={loading || page + 1 >= totalPages} onClick={() => setPage(page + 1)}>
                <ChevronRight size={16} aria-hidden="true" />
              </button>
              <button
                type="button"
                aria-label="Trang cuối"
                disabled={loading || page + 1 >= totalPages}
                onClick={() => setPage(totalPages - 1)}
              >
                <ChevronsRight size={16} aria-hidden="true" />
              </button>
            </nav>
          </div>
        )}
        {exportOpen && (
          <div className="admin-audit-logs-overlay">
            <div className="admin-audit-logs-dialog" role="dialog" aria-modal="true" aria-labelledby="audit-export-title">
              <button
                type="button"
                className="admin-audit-logs-close"
                aria-label="Đóng"
                disabled={exporting}
                onClick={() => setExportOpen(false)}
              >
                <X size={20} aria-hidden="true" />
              </button>
              <h2 id="audit-export-title">Xuất nhật ký ra Excel</h2>
              <p>Bộ lọc tài khoản, hành động và đối tượng đang áp dụng sẽ được giữ lại.</p>
              <form onSubmit={exportExcel}>
                <label className="admin-audit-logs-export-choice">
                  <input
                    type="radio"
                    name="audit-export-mode"
                    checked={exportMode === 'count'}
                    onChange={() => setExportMode('count')}
                    autoFocus
                  />
                  Xuất số dòng mới nhất
                </label>
                {exportMode === 'count' && (
                  <label className="admin-audit-logs-export-field">
                    Số dòng (tối đa 10.000)
                    <input
                      type="number"
                      min="1"
                      max="10000"
                      step="1"
                      required
                      value={exportCount}
                      onChange={event => setExportCount(event.target.value)}
                    />
                  </label>
                )}
                <label className="admin-audit-logs-export-choice">
                  <input type="radio" name="audit-export-mode" checked={exportMode === 'range'} onChange={() => setExportMode('range')} />
                  Xuất theo khoảng thời gian
                </label>
                {exportMode === 'range' && (
                  <div className="admin-audit-logs-export-range">
                    <label className="admin-audit-logs-export-field">
                      Từ thời điểm
                      <input type="datetime-local" required value={exportFrom} onChange={event => setExportFrom(event.target.value)} />
                    </label>
                    <label className="admin-audit-logs-export-field">
                      Đến thời điểm
                      <input type="datetime-local" required value={exportTo} onChange={event => setExportTo(event.target.value)} />
                    </label>
                  </div>
                )}
                {exportError && (
                  <p className="admin-audit-logs-error" role="alert">
                    {exportError}
                  </p>
                )}
                <div className="admin-audit-logs-export-actions">
                  <button type="button" className="secondary" disabled={exporting} onClick={() => setExportOpen(false)}>
                    Hủy
                  </button>
                  <button type="submit" disabled={exporting}>
                    <Download size={17} aria-hidden="true" /> {exporting ? 'Đang xuất...' : 'Xuất Excel'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </main>
    </AdminLayout>
  );
};

export default AdminAuditLogs;
