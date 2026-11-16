import './course-review.scss';

import React, { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router';
import { Modal, Form, Button } from 'react-bootstrap';
import { toast } from 'react-toastify';
import { RefreshCw, Search, X, Eye, CheckCircle2, XCircle, Clock, Layers, AlertCircle } from 'lucide-react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBookOpen } from '@fortawesome/free-solid-svg-icons';
import axios from 'axios';

import AdminLayout from '../../layout/admin-layout';
import {
  courseReviewApi,
  type ICourseReviewRequest,
  type CourseReviewRequestStatus,
  reviewStatusLabels,
  getSubjectGradient,
  getSubjectClass,
  formatPrice,
  accessDurationText,
  formatDateTime,
  getUserDisplayName,
} from './course-review-api';

const pageSize = 10;

const CourseThumbnail = ({
  courseId,
  thumbnailUrl,
  subjectName,
  title,
}: {
  courseId: number;
  thumbnailUrl?: string | null;
  subjectName?: string;
  title: string;
}) => {
  const [url, setUrl] = useState<string | null>(null);

  useEffect(() => {
    let active = true;
    if (thumbnailUrl) {
      if (thumbnailUrl.startsWith('http://') || thumbnailUrl.startsWith('https://')) {
        setUrl(thumbnailUrl);
      } else {
        courseReviewApi.getThumbnailUrl(courseId).then(resolvedUrl => {
          if (active && resolvedUrl) setUrl(resolvedUrl);
        });
      }
    }
    return () => {
      active = false;
    };
  }, [courseId, thumbnailUrl]);

  return url ? (
    <img src={url} alt={title} onError={() => setUrl(null)} />
  ) : (
    <div className="thumb-placeholder" style={{ background: getSubjectGradient(subjectName) }}>
      <FontAwesomeIcon icon={faBookOpen} />
    </div>
  );
};

export const CourseReviewList = () => {
  const [statusTab, setStatusTab] = useState<CourseReviewRequestStatus | 'ALL'>('PENDING');
  const [page, setPage] = useState(0);
  const [query, setQuery] = useState('');
  const [subjectFilter, setSubjectFilter] = useState('ALL');
  const [sortBy, setSortBy] = useState('submittedAt,desc');
  const [items, setItems] = useState<ICourseReviewRequest[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [refreshKey, setRefreshKey] = useState(0);

  // Tab counts
  const [tabCounts, setTabCounts] = useState<{
    all: number;
    pending: number;
    approved: number;
    rejected: number;
  }>({ all: 0, pending: 0, approved: 0, rejected: 0 });

  // Quick action modal state
  const [selectedRequest, setSelectedRequest] = useState<ICourseReviewRequest | null>(null);
  const [dialogMode, setDialogMode] = useState<'approve' | 'reject' | null>(null);
  const [feedbackNote, setFeedbackNote] = useState('');
  const [actionLoading, setActionLoading] = useState(false);
  const [actionError, setActionError] = useState<string | null>(null);

  const refresh = useCallback(() => setRefreshKey(v => v + 1), []);

  // 1. Fetch counts
  useEffect(() => {
    let active = true;
    Promise.all([
      courseReviewApi.count(),
      courseReviewApi.count('PENDING'),
      courseReviewApi.count('APPROVED'),
      courseReviewApi.count('REJECTED'),
    ])
      .then(([all, pending, approved, rejected]) => {
        if (!active) return;
        setTabCounts({
          all: all.data ?? 0,
          pending: pending.data ?? 0,
          approved: approved.data ?? 0,
          rejected: rejected.data ?? 0,
        });
      })
      .catch(() => {
        /* Keep existing or 0 */
      });
    return () => {
      active = false;
    };
  }, [refreshKey]);

  // 2. Fetch list
  useEffect(() => {
    let active = true;
    setLoading(true);
    setLoadError(null);

    courseReviewApi
      .list({
        page,
        size: pageSize,
        status: statusTab,
        sort: sortBy,
      })
      .then(response => {
        if (!active) return;
        setItems(response.data);
        const totalCount = Number(response.headers['x-total-count'] ?? response.data.length);
        setTotal(totalCount);
      })
      .catch(() => {
        if (!active) return;
        setItems([]);
        setTotal(0);
        setLoadError('Không thể tải danh sách yêu cầu duyệt khóa học. Vui lòng thử lại.');
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => {
      active = false;
    };
  }, [statusTab, page, sortBy, refreshKey]);

  // Client-side search and subject filtering
  const trimmedQuery = query.trim().toLowerCase();
  const visibleItems = items.filter(item => {
    if (subjectFilter !== 'ALL' && item.course?.subject?.id?.toString() !== subjectFilter) {
      return false;
    }
    if (!trimmedQuery) return true;
    const titleMatch = item.course?.title?.toLowerCase().includes(trimmedQuery);
    const authorMatch = getUserDisplayName(item.submittedBy).toLowerCase().includes(trimmedQuery);
    const idMatch = item.id.toString().includes(trimmedQuery);
    return Boolean(titleMatch || authorMatch || idMatch);
  });

  const pageCount = Math.max(1, Math.ceil(total / pageSize));

  // Quick Action Handler
  const handleOpenAction = (item: ICourseReviewRequest, mode: 'approve' | 'reject') => {
    setSelectedRequest(item);
    setDialogMode(mode);
    setFeedbackNote(mode === 'approve' ? 'Nội dung và học phí đã đáp ứng tiêu chuẩn.' : '');
    setActionError(null);
  };

  const handleCloseAction = () => {
    if (actionLoading) return;
    setSelectedRequest(null);
    setDialogMode(null);
    setFeedbackNote('');
    setActionError(null);
  };

  const handleConfirmAction = async () => {
    if (!selectedRequest || !dialogMode || actionLoading) return;
    if (dialogMode === 'reject' && !feedbackNote.trim()) {
      setActionError('Vui lòng nhập lý do từ chối để giảng viên có thể chỉnh sửa.');
      return;
    }

    setActionLoading(true);
    setActionError(null);

    try {
      if (dialogMode === 'approve') {
        await courseReviewApi.approve(selectedRequest, feedbackNote.trim());
        toast.success(`Đã phê duyệt khóa học "${selectedRequest.course.title}".`);
      } else {
        await courseReviewApi.reject(selectedRequest, feedbackNote.trim());
        toast.success(`Đã từ chối yêu cầu duyệt khóa học "${selectedRequest.course.title}".`);
      }
      handleCloseAction();
      refresh();
    } catch (err) {
      if (axios.isAxiosError(err) && err.response?.data?.message) {
        setActionError(err.response.data.message);
      } else {
        setActionError('Không thể thực hiện thao tác. Vui lòng thử lại.');
      }
    } finally {
      setActionLoading(false);
    }
  };

  // Distinct subjects in items for filter
  const subjects = Array.from(
    new Map(
      items
        .map(i => i.course?.subject)
        .filter((s): s is NonNullable<typeof s> => Boolean(s?.id && s?.name))
        .map(s => [s.id, s]),
    ).values(),
  );

  return (
    <AdminLayout searchValue={query} onSearchChange={setQuery} searchLabel="Tìm theo tên khóa học hoặc giảng viên...">
      <main className="admin-page-content">
        <div className="course-review-card">
          {/* Header */}
          <div className="course-review-heading">
            <div>
              <h1>
                Duyệt khóa học
                <span className="count-pill">{total} yêu cầu</span>
              </h1>
              <p>Xem và xử lý các yêu cầu phê duyệt khóa học từ giảng viên trước khi xuất bản.</p>
            </div>
            <button type="button" className="course-review-button" onClick={refresh} disabled={loading}>
              <RefreshCw size={16} className={loading ? 'spin' : ''} aria-hidden="true" /> Làm mới
            </button>
          </div>

          {/* Stat Cards (inspired by course-list.tsx) */}
          <div className="review-stat-grid">
            <div className="stat-card card-all">
              <div className="stat-info">
                <div className="stat-label">Tổng yêu cầu</div>
                <div className="stat-value">{tabCounts.all}</div>
                <div className="stat-desc">Tất cả các lần gửi duyệt</div>
              </div>
              <div className="stat-icon">
                <Layers size={20} />
              </div>
            </div>

            <div className="stat-card card-pending">
              <div className="stat-info">
                <div className="stat-label">Đang chờ duyệt</div>
                <div className="stat-value">{tabCounts.pending}</div>
                <div className="stat-desc">Cần xem xét và thẩm định</div>
              </div>
              <div className="stat-icon">
                <Clock size={20} />
              </div>
            </div>

            <div className="stat-card card-approved">
              <div className="stat-info">
                <div className="stat-label">Đã phê duyệt</div>
                <div className="stat-value">{tabCounts.approved}</div>
                <div className="stat-desc">Đã mở xuất bản cho học viên</div>
              </div>
              <div className="stat-icon">
                <CheckCircle2 size={20} />
              </div>
            </div>

            <div className="stat-card card-rejected">
              <div className="stat-info">
                <div className="stat-label">Cần bổ sung / Từ chối</div>
                <div className="stat-value">{tabCounts.rejected}</div>
                <div className="stat-desc">Đã gửi phản hồi chỉnh sửa</div>
              </div>
              <div className="stat-icon">
                <XCircle size={20} />
              </div>
            </div>
          </div>

          {/* Status Filter Tabs */}
          <div className="course-review-tabs" role="group" aria-label="Lọc theo trạng thái duyệt">
            <button
              type="button"
              className={statusTab === 'ALL' ? 'active' : ''}
              onClick={() => {
                setStatusTab('ALL');
                setPage(0);
              }}
            >
              Tất cả <span className="tab-badge">{tabCounts.all}</span>
            </button>
            <button
              type="button"
              className={statusTab === 'PENDING' ? 'active' : ''}
              onClick={() => {
                setStatusTab('PENDING');
                setPage(0);
              }}
            >
              Chờ duyệt <span className="tab-badge">{tabCounts.pending}</span>
            </button>
            <button
              type="button"
              className={statusTab === 'APPROVED' ? 'active' : ''}
              onClick={() => {
                setStatusTab('APPROVED');
                setPage(0);
              }}
            >
              Đã duyệt <span className="tab-badge">{tabCounts.approved}</span>
            </button>
            <button
              type="button"
              className={statusTab === 'REJECTED' ? 'active' : ''}
              onClick={() => {
                setStatusTab('REJECTED');
                setPage(0);
              }}
            >
              Đã từ chối <span className="tab-badge">{tabCounts.rejected}</span>
            </button>
            <button
              type="button"
              className={statusTab === 'WITHDRAWN' ? 'active' : ''}
              onClick={() => {
                setStatusTab('WITHDRAWN');
                setPage(0);
              }}
            >
              Đã rút lại
            </button>
          </div>

          {/* Search & Filter Toolbar */}
          <div className="course-review-toolbar">
            <div className="toolbar-row">
              <div className="search-box">
                <Search size={16} className="search-icon" aria-hidden="true" />
                <input
                  type="text"
                  placeholder="Tìm theo tên khóa học, mã yêu cầu, giảng viên..."
                  value={query}
                  onChange={e => {
                    setQuery(e.target.value);
                    setPage(0);
                  }}
                />
                {query && (
                  <button type="button" className="clear-btn" onClick={() => setQuery('')} title="Xóa tìm kiếm">
                    <X size={15} />
                  </button>
                )}
              </div>

              {subjects.length > 0 && (
                <select
                  className="filter-select"
                  value={subjectFilter}
                  onChange={e => {
                    setSubjectFilter(e.target.value);
                    setPage(0);
                  }}
                  aria-label="Lọc theo môn học"
                >
                  <option value="ALL">Tất cả môn học</option>
                  {subjects.map(s => (
                    <option key={s.id} value={s.id.toString()}>
                      {s.name}
                    </option>
                  ))}
                </select>
              )}

              <select
                className="filter-select"
                value={sortBy}
                onChange={e => {
                  setSortBy(e.target.value);
                  setPage(0);
                }}
                aria-label="Sắp xếp danh sách"
              >
                <option value="submittedAt,desc">Mới gửi nhất</option>
                <option value="submittedAt,asc">Cũ nhất</option>
                <option value="id,desc">Mã yêu cầu giảm dần</option>
              </select>
            </div>
          </div>

          {loadError && (
            <div className="alert alert-danger d-flex align-items-center gap-2 mb-4" role="alert">
              <AlertCircle size={18} />
              <span>{loadError}</span>
              <Button size="sm" variant="link" onClick={refresh}>
                Thử lại
              </Button>
            </div>
          )}

          {/* Course Review Table */}
          <div className="course-review-table">
            <div className="table-head">
              <div>Khóa học</div>
              <div>Giảng viên gửi</div>
              <div>Phiên bản & Ngày gửi</div>
              <div>Trạng thái</div>
              <div>Thao tác</div>
            </div>

            {loading ? (
              <div className="review-empty-state">
                <RefreshCw size={28} className="spin mb-2 text-primary" />
                <p>Đang tải danh sách yêu cầu phê duyệt...</p>
              </div>
            ) : visibleItems.length === 0 ? (
              <div className="review-empty-state">
                <div className="empty-icon">
                  <Search size={36} />
                </div>
                <h4>Không tìm thấy yêu cầu duyệt phù hợp</h4>
                <p>
                  {query || subjectFilter !== 'ALL'
                    ? 'Thử thay đổi từ khóa hoặc bộ lọc để tìm lại.'
                    : 'Chưa có yêu cầu nào ở trạng thái này.'}
                </p>
                {(query || subjectFilter !== 'ALL') && (
                  <button
                    type="button"
                    className="course-review-button"
                    onClick={() => {
                      setQuery('');
                      setSubjectFilter('ALL');
                    }}
                  >
                    Xóa bộ lọc
                  </button>
                )}
              </div>
            ) : (
              visibleItems.map(item => {
                const course = item.course;
                return (
                  <div key={item.id} className="table-row">
                    {/* 1. Khóa học */}
                    <div className="course-cell">
                      <div className="course-thumb-box">
                        <CourseThumbnail
                          courseId={course.id}
                          thumbnailUrl={course.thumbnailUrl}
                          subjectName={course.subject?.name}
                          title={course.title}
                        />
                      </div>
                      <div className="course-info">
                        <Link to={`/admin/course-review/${item.id}`} className="course-title" title={course.title}>
                          {course.title || 'Khóa học chưa đặt tên'}
                        </Link>
                        <div className="course-tags">
                          <span className={`tag ${getSubjectClass(course.subject?.name)}`}>{course.subject?.name || 'Chưa chọn môn'}</span>
                          <span className="tag tag-grade">{course.grade?.name || 'Chưa chọn lớp'}</span>
                          {course.bookSeries && <span className="tag tag-book">{course.bookSeries}</span>}
                        </div>
                        <div className="course-stats-line">
                          <span>{formatPrice(course.price)}</span>
                          <span>•</span>
                          <span>{accessDurationText(course)}</span>
                        </div>
                      </div>
                    </div>

                    {/* 2. Giảng viên gửi */}
                    <div className="submitter-cell">
                      <div className="submitter-name">{getUserDisplayName(item.submittedBy, course.authorDisplayName)}</div>
                      <div className="submitter-email">{item.submittedBy?.email || item.submittedBy?.login || '—'}</div>
                    </div>

                    {/* 3. Phiên bản & Ngày gửi */}
                    <div className="date-cell">
                      <div className="d-flex align-items-center gap-2">
                        <span className="version-tag">v{item.courseVersion}</span>
                        <span className="date-value">{formatDateTime(item.submittedAt)}</span>
                      </div>
                      {item.submissionNote && (
                        <div className="note-preview" title={item.submissionNote}>
                          💬 “{item.submissionNote}”
                        </div>
                      )}
                    </div>

                    {/* 4. Trạng thái */}
                    <div>
                      <div className={`status-pill status-${item.status.toLowerCase()}`}>
                        <span className="dot" />
                        <span>{reviewStatusLabels[item.status] || item.status}</span>
                      </div>
                    </div>

                    {/* 5. Thao tác */}
                    <div className="actions-cell">
                      <Link to={`/admin/course-review/${item.id}`} className="btn-action-view" title="Xem chi tiết nội dung & thẩm định">
                        <Eye size={14} /> Chi tiết
                      </Link>

                      {item.status === 'PENDING' && (
                        <>
                          <button
                            type="button"
                            className="course-review-button success"
                            style={{ minHeight: '30px', padding: '4px 10px', fontSize: '12px' }}
                            onClick={() => handleOpenAction(item, 'approve')}
                            title="Phê duyệt nhanh"
                          >
                            Duyệt
                          </button>
                          <button
                            type="button"
                            className="course-review-button danger"
                            style={{ minHeight: '30px', padding: '4px 10px', fontSize: '12px' }}
                            onClick={() => handleOpenAction(item, 'reject')}
                            title="Từ chối / Yêu cầu sửa"
                          >
                            Từ chối
                          </button>
                        </>
                      )}
                    </div>
                  </div>
                );
              })
            )}
          </div>

          {/* Pagination */}
          {total > pageSize && (
            <div className="review-pagination">
              <div className="pagination-info">
                Hiển thị <strong>{page * pageSize + 1}</strong> đến <strong>{Math.min((page + 1) * pageSize, total)}</strong> trên{' '}
                <strong>{total}</strong> yêu cầu
              </div>
              <div className="pagination-nav">
                <button type="button" disabled={page === 0} onClick={() => setPage(p => Math.max(0, p - 1))}>
                  ← Trước
                </button>
                {Array.from({ length: Math.min(5, pageCount) }, (_, i) => {
                  const targetPage = Math.max(0, Math.min(page - 2, pageCount - 5)) + i;
                  if (targetPage >= pageCount) return null;
                  return (
                    <button
                      key={targetPage}
                      type="button"
                      className={page === targetPage ? 'active' : ''}
                      onClick={() => setPage(targetPage)}
                    >
                      {targetPage + 1}
                    </button>
                  );
                })}
                <button type="button" disabled={page >= pageCount - 1} onClick={() => setPage(p => Math.min(pageCount - 1, p + 1))}>
                  Sau →
                </button>
              </div>
            </div>
          )}
        </div>
      </main>

      {/* Quick Action Confirmation Modal */}
      {dialogMode && selectedRequest && (
        <Modal show onHide={handleCloseAction} centered backdrop="static">
          <Modal.Header closeButton={!actionLoading}>
            <Modal.Title>{dialogMode === 'approve' ? 'Xác nhận phê duyệt khóa học' : 'Từ chối / Yêu cầu bổ sung nội dung'}</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <p>
              Khóa học: <strong>{selectedRequest.course.title}</strong> (Phiên bản v{selectedRequest.courseVersion})
            </p>
            <p className="small text-muted mb-3">
              Giảng viên: <strong>{getUserDisplayName(selectedRequest.submittedBy, selectedRequest.course.authorDisplayName)}</strong> • Gửi
              ngày {formatDateTime(selectedRequest.submittedAt)}
            </p>

            <Form.Group className="mb-3" controlId="quick-review-feedback">
              <Form.Label className="fw-semibold">
                {dialogMode === 'approve' ? 'Lời nhắn gửi giảng viên (tùy chọn):' : 'Lý do từ chối / cần bổ sung (bắt buộc):'}
              </Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                placeholder={
                  dialogMode === 'approve'
                    ? 'Nhận xét tích cực hoặc lưu ý cho giảng viên...'
                    : 'Ghi rõ các mục cần chỉnh sửa (video, đề cương, bản quyền, học phí)...'
                }
                value={feedbackNote}
                onChange={e => setFeedbackNote(e.target.value)}
                disabled={actionLoading}
                maxLength={5000}
              />
            </Form.Group>

            {actionError && (
              <div className="alert alert-danger py-2 small mb-0" role="alert">
                {actionError}
              </div>
            )}
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={handleCloseAction} disabled={actionLoading}>
              Đóng
            </Button>
            <Button variant={dialogMode === 'approve' ? 'success' : 'danger'} onClick={handleConfirmAction} disabled={actionLoading}>
              {actionLoading ? 'Đang xử lý...' : dialogMode === 'approve' ? 'Xác nhận phê duyệt' : 'Xác nhận từ chối'}
            </Button>
          </Modal.Footer>
        </Modal>
      )}
    </AdminLayout>
  );
};

export default CourseReviewList;
