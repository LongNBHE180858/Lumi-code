import React, { useCallback, useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router';
import './class-management.scss';
import {
  createInstructorClass,
  getInstructorClasses,
  updateInstructorClass,
  type ClassFormValues,
  type ClassStatus,
  type InstructorClass,
} from './class-management.api';
import ClassFormModal from './components/class-form-modal';
import InstructorPagination from './components/instructor-pagination';

const statusLabels: Record<ClassStatus, string> = {
  DRAFT: 'Bản nháp',
  PENDING: 'Chờ duyệt',
  ACTIVE: 'Đang hoạt động',
  HIDDEN: 'Đang ẩn',
  ARCHIVED: 'Đã lưu trữ',
  BLOCKED: 'Đã khóa',
};

const errorMessage = (error: unknown): string =>
  axios.isAxiosError<{ message?: string }>(error)
    ? error.response?.data?.message || 'Không kết nối được máy chủ. Vui lòng thử lại.'
    : error instanceof Error
      ? error.message
      : 'Có lỗi xảy ra. Vui lòng thử lại.';

export const ClassList: React.FC = () => {
  const navigate = useNavigate();
  const [classes, setClasses] = useState<InstructorClass[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [loading, setLoading] = useState(true);
  const [listError, setListError] = useState<string | null>(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingClass, setEditingClass] = useState<InstructorClass | null>(null);
  const [saving, setSaving] = useState(false);
  const [modalError, setModalError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);
  const [copiedCode, setCopiedCode] = useState<string | null>(null);

  const loadClasses = useCallback(async (requestedPage: number, signal?: AbortSignal) => {
    setLoading(true);
    setListError(null);
    try {
      const result = await getInstructorClasses(requestedPage, signal);
      if (signal?.aborted) return;
      setClasses(result.items);
      setPage(result.page);
      setTotalPages(result.totalPages);
      setTotalElements(result.totalElements);
      setSelectedId(current => (result.items.some(item => item.id === current) ? current : (result.items[0]?.id ?? null)));
    } catch (error) {
      if (!axios.isCancel(error) && !signal?.aborted) setListError(errorMessage(error));
    } finally {
      if (!signal?.aborted) setLoading(false);
    }
  }, []);

  useEffect(() => {
    const controller = new AbortController();
    void loadClasses(0, controller.signal);
    return () => controller.abort();
  }, [loadClasses]);

  const selectedClass = classes.find(item => item.id === selectedId) ?? null;
  const openCreate = () => {
    setEditingClass(null);
    setModalError(null);
    setModalOpen(true);
  };
  const openEdit = (item: InstructorClass) => {
    setEditingClass(item);
    setModalError(null);
    setModalOpen(true);
  };

  const saveClass = async (values: ClassFormValues) => {
    if (saving) return;
    setSaving(true);
    setModalError(null);
    try {
      const saved = editingClass ? await updateInstructorClass(editingClass.id, values) : await createInstructorClass(values);
      await loadClasses(editingClass ? page : 0);
      setSelectedId(saved.id);
      setNotice(editingClass ? `Đã cập nhật lớp "${saved.name}".` : `Đã tạo lớp "${saved.name}". Mã lớp: ${saved.joinCode}`);
      setModalOpen(false);
      setEditingClass(null);
    } catch (error) {
      setModalError(errorMessage(error));
    } finally {
      setSaving(false);
    }
  };

  const copyCode = async (code: string) => {
    try {
      await navigator.clipboard.writeText(code);
      setCopiedCode(code);
      setNotice(`Đã sao chép mã lớp ${code}.`);
    } catch {
      setNotice('Không sao chép được mã. Vui lòng chọn và sao chép thủ công.');
    }
  };

  const copyJoinLink = async (code: string) => {
    try {
      await navigator.clipboard.writeText(`${window.location.origin}/student/classes?code=${encodeURIComponent(code)}`);
      setNotice('Đã sao chép liên kết tham gia lớp.');
    } catch {
      setNotice('Không sao chép được liên kết. Vui lòng thử lại.');
    }
  };

  const changeJoinMode = async (item: InstructorClass) => {
    setSaving(true);
    setListError(null);
    try {
      const nextMode = item.joinMode === 'INVITE_ONLY' ? 'CODE_APPROVAL' : 'INVITE_ONLY';
      await updateInstructorClass(item.id, {
        name: item.name,
        description: item.description ?? '',
        joinMode: nextMode,
        status: item.status,
        maxStudents: item.maxStudents,
      });
      await loadClasses(page);
      setSelectedId(item.id);
      setNotice(nextMode === 'INVITE_ONLY' ? 'Đã tạm dừng tham gia bằng mã lớp.' : 'Đã mở lại tham gia bằng mã, học sinh cần được duyệt.');
    } catch (cause) {
      setListError(errorMessage(cause));
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="class-management-page-wrapper">
      <main className="class-management-main">
        <div className="class-page-container">
          <div className="class-page-header">
            <div>
              <h1 className="class-page-title">Quản lý lớp học</h1>
              <p className="class-page-desc">Xem sĩ số, cập nhật lớp và chia sẻ mã tham gia với học sinh.</p>
            </div>
            <button type="button" className="btn-primary-create" onClick={openCreate}>
              ＋ Tạo lớp mới
            </button>
          </div>
          {notice && (
            <div className="lumi-toast-banner" role="status">
              <span>{notice}</span>
              <button type="button" className="btn-toast-close" onClick={() => setNotice(null)} aria-label="Đóng thông báo">
                ✕
              </button>
            </div>
          )}
          <div className="class-stats-grid">
            <div className="class-stat-card">
              <div className="stat-card-content">
                <span className="stat-card-label">Tổng số lớp</span>
                <strong className="stat-card-value">{totalElements}</strong>
              </div>
            </div>
            <div className="class-stat-card">
              <div className="stat-card-content">
                <span className="stat-card-label">Sĩ số lớp đang chọn</span>
                <strong className="stat-card-value">{selectedClass?.studentCount ?? 0}</strong>
              </div>
            </div>
          </div>
          {listError && (
            <p className="class-page-message" role="alert">
              {listError}{' '}
              <button type="button" onClick={() => void loadClasses(page)}>
                Thử lại
              </button>
            </p>
          )}
          {loading && (
            <p className="class-page-message" role="status">
              Đang tải danh sách lớp...
            </p>
          )}
          {!loading && !listError && classes.length === 0 && (
            <div className="table-empty-state">
              <p className="empty-title">Bạn chưa có lớp học nào</p>
              <p className="empty-desc">Tạo lớp đầu tiên để nhận mã tham gia và chia sẻ với học sinh.</p>
            </div>
          )}
          {!listError && classes.length > 0 && (
            <div className="class-table-container">
              <table className="class-data-table">
                <thead>
                  <tr>
                    <th>Tên lớp</th>
                    <th>Mã lớp</th>
                    <th>Sĩ số</th>
                    <th>Trạng thái</th>
                    <th>Thao tác</th>
                  </tr>
                </thead>
                <tbody>
                  {classes.map(item => (
                    <tr key={item.id} className={item.id === selectedId ? 'row-selected' : ''} onClick={() => setSelectedId(item.id)}>
                      <td>
                        <button type="button" className="class-name-button" onClick={() => setSelectedId(item.id)}>
                          {item.name}
                        </button>
                        {item.description && <div className="cell-class-subtext">{item.description}</div>}
                      </td>
                      <td>
                        <div className="cell-code-wrapper">
                          <code className="cell-code-tag">{item.joinCode || 'Chưa có mã'}</code>
                          {item.joinCode && (
                            <button
                              type="button"
                              className="btn-copy-code"
                              onClick={() => void copyCode(item.joinCode ?? '')}
                              aria-label={`Sao chép mã lớp ${item.joinCode}`}
                              title="Sao chép mã lớp"
                            >
                              {copiedCode === item.joinCode ? '✓' : '▢'}
                            </button>
                          )}
                        </div>
                      </td>
                      <td>
                        <span className="cell-student-number">{item.studentCount}</span> học sinh
                      </td>
                      <td>
                        <span className={`status-pill ${item.status === 'ACTIVE' ? 'pill-active' : 'pill-archived'}`}>
                          {statusLabels[item.status]}
                        </span>
                      </td>
                      <td>
                        <button
                          type="button"
                          className="btn-table-action btn-action-edit"
                          onClick={() => openEdit(item)}
                          aria-label={`Sửa lớp ${item.name}`}
                        >
                          ✎
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
          {totalPages > 1 && !listError && (
            <InstructorPagination
              page={page}
              totalPages={totalPages}
              totalItems={totalElements}
              itemLabel="lớp"
              label="Phân trang lớp học"
              disabled={loading}
              onPageChange={next => void loadClasses(next)}
            />
          )}
          {selectedClass && !listError && (
            <section className="inline-class-detail-card" aria-label={`Chi tiết lớp ${selectedClass.name}`}>
              <div className="detail-card-head">
                <div className="detail-title-group">
                  <span className="detail-title-icon">📘</span>
                  <div>
                    <h2 className="detail-title">{selectedClass.name}</h2>
                    <p className="detail-subtitle">{selectedClass.description || 'Chưa có mô tả.'}</p>
                  </div>
                </div>
                <div className="class-page-actions">
                  <button type="button" className="btn-pill-primary" onClick={() => navigate(`/instructor/classes/${selectedClass.id}`)}>
                    Vào lớp
                  </button>
                  <button type="button" className="btn-pill-primary" onClick={() => openEdit(selectedClass)}>
                    Chỉnh sửa lớp
                  </button>
                </div>
              </div>
              <div className="overview-stats-grid">
                <div className="ov-stat-box">
                  <span className="ov-stat-label">Sĩ số</span>
                  <strong className="ov-stat-value">{selectedClass.studentCount}</strong>
                </div>
                <div className="ov-stat-box">
                  <span className="ov-stat-label">Mã vào lớp</span>
                  <strong className="ov-stat-value text-code">{selectedClass.joinCode || 'Chưa có mã'}</strong>
                  {selectedClass.joinCode && (
                    <button
                      type="button"
                      className="btn-copy-code"
                      onClick={() => void copyCode(selectedClass.joinCode ?? '')}
                      aria-label={`Sao chép mã lớp ${selectedClass.joinCode}`}
                    >
                      Sao chép mã
                    </button>
                  )}
                  {selectedClass.status === 'ACTIVE' && selectedClass.joinMode !== 'INVITE_ONLY' && selectedClass.joinCode && (
                    <button type="button" className="btn-copy-code" onClick={() => void copyJoinLink(selectedClass.joinCode ?? '')}>
                      Sao chép liên kết
                    </button>
                  )}
                </div>
                <div className="ov-stat-box">
                  <span className="ov-stat-label">Sĩ số tối đa</span>
                  <strong className="ov-stat-value">{selectedClass.maxStudents ?? 'Không giới hạn'}</strong>
                </div>
              </div>
              <div className="class-code-controls">
                <div>
                  <strong>Kiểm soát mã tham gia</strong>
                  <p>
                    {selectedClass.joinMode === 'INVITE_ONLY'
                      ? 'Học sinh tạm thời không thể dùng mã để vào lớp.'
                      : 'Học sinh có thể dùng mã hoặc liên kết để vào lớp.'}
                  </p>
                </div>
                <button
                  type="button"
                  className="classroom-secondary-action"
                  disabled={saving}
                  onClick={() => void changeJoinMode(selectedClass)}
                >
                  {selectedClass.joinMode === 'INVITE_ONLY' ? 'Mở lại bằng mã' : 'Tạm dừng dùng mã'}
                </button>
                <button type="button" className="classroom-secondary-action" disabled title="Máy chủ chưa hỗ trợ tạo lại mã lớp">
                  Tạo lại mã (chưa hỗ trợ)
                </button>
              </div>
            </section>
          )}
        </div>
      </main>
      {modalOpen && (
        <ClassFormModal
          classItem={editingClass}
          saving={saving}
          error={modalError}
          onClose={() => setModalOpen(false)}
          onSubmit={values => void saveClass(values)}
        />
      )}
    </div>
  );
};

export default ClassList;
