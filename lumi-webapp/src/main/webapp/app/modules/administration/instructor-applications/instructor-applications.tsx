import './instructor-applications.scss';

import React, { useCallback, useEffect, useState } from 'react';
import axios from 'axios';
import { RefreshCw } from 'lucide-react';

import AdminLayout from '../layout/admin-layout';
import { instructorApplicationsApi, type InstructorApplication, type InstructorApplicationStatus } from './instructor-applications-api';
import { statuses, fullName, type InstructorDialogMode } from './instructor-application-format';
import ApplicationTable from './components/application-table';
import ApplicationDialog from './components/application-dialog';

const pageSize = 10;
const errorMessage = (error: unknown) => {
  if (axios.isAxiosError(error)) {
    if (error.response?.status === 409) return 'Hồ sơ đã được xử lý hoặc tài khoản chưa xác minh email. Hãy tải lại danh sách.';
    if (error.response?.status === 404) return 'Không tìm thấy hồ sơ giảng viên. Hãy tải lại danh sách.';
  }
  return 'Không thể hoàn tất yêu cầu. Vui lòng thử lại.';
};

const InstructorApplications = () => {
  const [status, setStatus] = useState<InstructorApplicationStatus>('PENDING');
  const [page, setPage] = useState(0);
  const [query, setQuery] = useState('');
  const [items, setItems] = useState<InstructorApplication[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState('');
  const [refreshKey, setRefreshKey] = useState(0);
  const [selected, setSelected] = useState<InstructorApplication | null>(null);
  const [dialog, setDialog] = useState<InstructorDialogMode | null>(null);
  const [reason, setReason] = useState('');
  const [actionError, setActionError] = useState('');
  const [acting, setActing] = useState(false);
  const [detailReady, setDetailReady] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');

  const refresh = useCallback(() => setRefreshKey(value => value + 1), []);

  useEffect(() => {
    let active = true;
    setLoading(true);
    setLoadError('');
    instructorApplicationsApi
      .list(status, page, pageSize)
      .then(response => {
        if (!active) return;
        setItems(response.data);
        setTotal(Number(response.headers['x-total-count'] ?? response.data.length));
      })
      .catch(() => {
        if (!active) return;
        setItems([]);
        setTotal(0);
        setLoadError('Không tải được danh sách giảng viên. Vui lòng thử lại.');
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, [status, page, refreshKey]);

  const openDialog = async (item: InstructorApplication, nextDialog: InstructorDialogMode) => {
    setSelected(item);
    setDialog(nextDialog);
    setReason('');
    setActionError('');
    setDetailReady(false);
    try {
      const response = await instructorApplicationsApi.get(item.userId);
      setSelected(response.data);
      setDetailReady(true);
    } catch (error) {
      setActionError(errorMessage(error));
    }
  };

  const closeDialog = () => {
    if (acting) return;
    setDialog(null);
    setSelected(null);
    setActionError('');
    setDetailReady(false);
  };

  const decide = async () => {
    if (!selected || !dialog || dialog === 'details' || acting || !detailReady) return;
    const trimmedReason = reason.trim();
    if (dialog === 'reject' && (!trimmedReason || trimmedReason.length > 2000)) {
      setActionError('Vui lòng nhập lý do từ chối, tối đa 2000 ký tự.');
      return;
    }
    setActing(true);
    setActionError('');
    try {
      if (dialog === 'approve') await instructorApplicationsApi.approve(selected.userId);
      else await instructorApplicationsApi.reject(selected.userId, trimmedReason);
      setSuccessMessage(dialog === 'approve' ? 'Đã duyệt hồ sơ giảng viên.' : 'Đã từ chối hồ sơ giảng viên.');
      setDialog(null);
      setSelected(null);
      if (items.length === 1 && page > 0) setPage(page - 1);
      else refresh();
    } catch (error) {
      setActionError(errorMessage(error));
    } finally {
      setActing(false);
    }
  };

  const search = query.trim().toLocaleLowerCase('vi');
  const visibleItems = search
    ? items.filter(item =>
        [fullName(item), item.login, item.email, item.schoolName].some(value => value?.toLocaleLowerCase('vi').includes(search)),
      )
    : items;
  const pageCount = Math.ceil(total / pageSize);
  const visiblePageCount = Math.min(5, pageCount);
  const firstVisiblePage = Math.max(0, Math.min(page - 2, pageCount - visiblePageCount));
  const visiblePages = Array.from({ length: visiblePageCount }, (_, index) => firstVisiblePage + index);

  return (
    <AdminLayout searchValue={query} onSearchChange={setQuery} searchLabel="Tìm trong trang giảng viên hiện tại">
      <main className="admin-page-content instructor-applications">
        <div className="instructor-applications-card">
          <div className="instructor-applications-heading">
            <div>
              <h1>Giảng viên</h1>
              <p>Xem và xử lý hồ sơ đăng ký giảng viên.</p>
            </div>
            <button type="button" className="instructor-applications-button" onClick={refresh} disabled={loading}>
              <RefreshCw size={16} aria-hidden="true" /> Làm mới
            </button>
          </div>

          <div className="instructor-applications-tabs" role="group" aria-label="Lọc theo trạng thái">
            {statuses.map(item => (
              <button
                key={item.value}
                type="button"
                className={status === item.value ? 'active' : ''}
                aria-pressed={status === item.value}
                onClick={() => {
                  setStatus(item.value);
                  setPage(0);
                  setSuccessMessage('');
                }}
              >
                {item.label}
              </button>
            ))}
          </div>

          {loadError && (
            <div className="instructor-applications-error" role="alert">
              {loadError}
            </div>
          )}
          {successMessage && (
            <div className="instructor-applications-success" role="status">
              {successMessage}
            </div>
          )}
          {search && <p className="instructor-applications-search-note">Tìm trong trang hiện tại: “{query.trim()}”</p>}
          <ApplicationTable
            visibleItems={visibleItems}
            loading={loading}
            search={search}
            total={total}
            page={page}
            pageSize={pageSize}
            pageCount={pageCount}
            visiblePages={visiblePages}
            setPage={setPage}
            openDialog={openDialog}
          />
        </div>
      </main>

      {dialog && selected && (
        <ApplicationDialog
          selected={selected}
          dialog={dialog}
          reason={reason}
          setReason={setReason}
          actionError={actionError}
          acting={acting}
          detailReady={detailReady}
          closeDialog={closeDialog}
          decide={decide}
        />
      )}
    </AdminLayout>
  );
};

export default InstructorApplications;
