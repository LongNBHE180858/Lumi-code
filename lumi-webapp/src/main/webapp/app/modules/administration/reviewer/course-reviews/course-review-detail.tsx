import './course-review.scss';

import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router';
import { Row, Col, Modal, Form, Button, Spinner, Alert } from 'react-bootstrap';
import { toast } from 'react-toastify';
import {
  ArrowLeft,
  CheckCircle2,
  XCircle,
  Clock,
  BookOpen,
  ShieldCheck,
  FileText,
  Video,
  ExternalLink,
  Layers,
  AlertCircle,
} from 'lucide-react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBookOpen, faCheck } from '@fortawesome/free-solid-svg-icons';
import axios from 'axios';

import AdminLayout from '../../layout/admin-layout';
import {
  courseReviewApi,
  type ICourseReviewRequest,
  type ICourseReviewChapter,
  reviewStatusLabels,
  getSubjectGradient,
  getSubjectClass,
  formatPrice,
  accessDurationText,
  formatDateTime,
  getUserDisplayName,
} from './course-review-api';

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
    <div className="placeholder" style={{ background: getSubjectGradient(subjectName) }}>
      <FontAwesomeIcon icon={faBookOpen} />
    </div>
  );
};

export const CourseReviewDetail = () => {
  const { id } = useParams<{ id: string }>();

  const [request, setRequest] = useState<ICourseReviewRequest | null>(null);
  const [chapters, setChapters] = useState<ICourseReviewChapter[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Review decision states
  const [feedback, setFeedback] = useState('');
  const [dialogMode, setDialogMode] = useState<'approve' | 'reject' | null>(null);
  const [actionLoading, setActionLoading] = useState(false);
  const [actionError, setActionError] = useState<string | null>(null);

  useEffect(() => {
    if (!id) return;
    let active = true;
    setLoading(true);
    setError(null);

    courseReviewApi
      .get(id)
      .then(async res => {
        if (!active) return;
        const reqData = res.data;
        setRequest(reqData);
        setFeedback(reqData.reviewerFeedback || '');

        // Fetch curriculum (lessons / builder chapters)
        try {
          const builderData = await courseReviewApi.getBuilderData(reqData.course.id);
          if (builderData && builderData.chapters) {
            const mappedChapters: ICourseReviewChapter[] = builderData.chapters.map(
              (ch: { chapter: { id: number; title: string; description?: string; orderIndex: number }; lessons: any[] }) => ({
                id: ch.chapter.id,
                title: ch.chapter.title,
                description: ch.chapter.description,
                orderIndex: ch.chapter.orderIndex,
                lessons: ch.lessons.map(ln => ln.lesson),
              }),
            );
            if (active) setChapters(mappedChapters);
            return;
          }
        } catch {
          /* Fallback to lessons */
        }

        try {
          const lessonsRes = await courseReviewApi.getLessons(reqData.course.id);
          if (!active) return;
          const lessonList = lessonsRes.data || [];
          // Group lessons by chapter
          const chapterMap = new Map<number, ICourseReviewChapter>();
          lessonList.forEach(ls => {
            const chId = ls.chapter?.id || 0;
            if (!chapterMap.has(chId)) {
              chapterMap.set(chId, {
                id: chId,
                title: ls.chapter?.title || 'Chương chung',
                description: ls.chapter?.description,
                orderIndex: ls.chapter?.orderIndex || 0,
                lessons: [],
              });
            }
            chapterMap.get(chId)!.lessons.push(ls);
          });
          setChapters(Array.from(chapterMap.values()));
        } catch {
          /* If no lessons or error, chapters remain empty */
        }
      })
      .catch(() => {
        if (!active) return;
        setError('Không tìm thấy thông tin yêu cầu phê duyệt hoặc phiên làm việc đã hết hạn.');
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => {
      active = false;
    };
  }, [id]);

  const handleOpenApprove = () => {
    setDialogMode('approve');
    setActionError(null);
  };

  const handleOpenReject = () => {
    setDialogMode('reject');
    setActionError(null);
  };

  const handleCloseModal = () => {
    if (actionLoading) return;
    setDialogMode(null);
    setActionError(null);
  };

  const handleConfirmDecision = async () => {
    if (!request || !dialogMode || actionLoading) return;
    const trimmedFeedback = feedback.trim();

    if (dialogMode === 'reject' && !trimmedFeedback) {
      setActionError('Vui lòng nhập lý do từ chối / yêu cầu chỉnh sửa để giảng viên biết cần hoàn thiện mục nào.');
      return;
    }

    setActionLoading(true);
    setActionError(null);

    try {
      if (dialogMode === 'approve') {
        const updated = await courseReviewApi.approve(request, trimmedFeedback || 'Đã kiểm tra và phê duyệt khóa học.');
        setRequest(updated);
        toast.success(`Đã phê duyệt khóa học "${request.course.title}".`);
      } else {
        const updated = await courseReviewApi.reject(request, trimmedFeedback);
        setRequest(updated);
        toast.success(`Đã gửi phản hồi từ chối / yêu cầu chỉnh sửa cho khóa học "${request.course.title}".`);
      }
      setDialogMode(null);
    } catch (err) {
      if (axios.isAxiosError(err) && err.response?.data?.message) {
        setActionError(err.response.data.message);
      } else {
        setActionError('Có lỗi xảy ra trong quá trình xử lý. Vui lòng thử lại.');
      }
    } finally {
      setActionLoading(false);
    }
  };

  if (loading) {
    return (
      <AdminLayout>
        <main className="admin-page-content">
          <div className="course-review-card text-center py-5">
            <Spinner animation="border" variant="primary" className="mb-3" />
            <p className="text-muted">Đang tải thông tin chi tiết yêu cầu phê duyệt...</p>
          </div>
        </main>
      </AdminLayout>
    );
  }

  if (error || !request) {
    return (
      <AdminLayout>
        <main className="admin-page-content">
          <div className="course-review-card">
            <Alert variant="danger">
              <AlertCircle size={18} className="me-2" />
              {error || 'Không tìm thấy yêu cầu duyệt.'}
            </Alert>
            <Link to="/admin/course-review" className="course-review-button">
              <ArrowLeft size={16} /> Quay lại danh sách
            </Link>
          </div>
        </main>
      </AdminLayout>
    );
  }

  const course = request.course;
  const isPending = request.status === 'PENDING';
  const totalLessons = chapters.reduce((sum, ch) => sum + ch.lessons.length, 0);

  return (
    <AdminLayout searchLabel="Tìm trong chi tiết khóa học...">
      <main className="admin-page-content course-review-detail">
        {/* Breadcrumb Navigation */}
        <div className="detail-breadcrumb">
          <Link to="/admin/dashboard">Tổng quan</Link>
          <span className="separator">/</span>
          <Link to="/admin/course-review">Duyệt khóa học</Link>
          <span className="separator">/</span>
          <span>Yêu cầu #{request.id}</span>
        </div>

        {/* Hero Card */}
        <div className="detail-hero-card">
          <div>
            <div className="hero-meta">
              <span className="req-badge">Yêu cầu #{request.id}</span>
              <span className="badge bg-light text-primary border">Phiên bản v{request.courseVersion}</span>
              <div className={`status-pill status-${request.status.toLowerCase()}`}>
                <span className="dot" />
                <span>{reviewStatusLabels[request.status] || request.status}</span>
              </div>
            </div>
            <h1>{course.title || 'Khóa học chưa đặt tên'}</h1>
            <div className="hero-author">
              Giảng viên: <strong>{getUserDisplayName(request.submittedBy, course.authorDisplayName)}</strong> • Gửi ngày{' '}
              {formatDateTime(request.submittedAt)}
            </div>
          </div>

          <div className="hero-actions">
            <Link to="/admin/course-review" className="course-review-button">
              <ArrowLeft size={16} /> Quay lại
            </Link>
            {isPending && (
              <>
                <button type="button" className="course-review-button success" onClick={handleOpenApprove}>
                  <CheckCircle2 size={16} /> Phê duyệt khóa học
                </button>
                <button type="button" className="course-review-button danger" onClick={handleOpenReject}>
                  <XCircle size={16} /> Yêu cầu sửa / Từ chối
                </button>
              </>
            )}
          </div>
        </div>

        {/* Main Content Grid */}
        <Row className="g-4">
          {/* Left Column (8 cols): Submission Details, Course Info, Curriculum */}
          <Col lg={8}>
            {/* 1. Submission Details Card */}
            <div className="detail-section-card">
              <h3>
                <Clock size={18} className="text-primary" /> Thông tin lần gửi duyệt
              </h3>

              <div className="info-grid">
                <div className="info-item">
                  <div className="info-label">Mã yêu cầu</div>
                  <div className="info-val">#{request.id}</div>
                </div>

                <div className="info-item">
                  <div className="info-label">Phiên bản gửi duyệt</div>
                  <div className="info-val">Phiên bản {request.courseVersion}</div>
                </div>

                <div className="info-item">
                  <div className="info-label">Thời gian gửi</div>
                  <div className="info-val">{formatDateTime(request.submittedAt)}</div>
                </div>

                <div className="info-item">
                  <div className="info-label">Cam kết bản quyền</div>
                  <div className="info-val text-success d-flex align-items-center gap-1">
                    <ShieldCheck size={16} /> Đã xác nhận ({formatDateTime(request.rightsConfirmedAt)})
                  </div>
                </div>

                <div className="info-item">
                  <div className="info-label">Giảng viên nộp</div>
                  <div className="info-val">{getUserDisplayName(request.submittedBy, course.authorDisplayName)}</div>
                </div>

                <div className="info-item">
                  <div className="info-label">Email tài khoản</div>
                  <div className="info-val">{request.submittedBy.email || request.submittedBy.login}</div>
                </div>
              </div>

              {/* Submission Note */}
              <div className="note-box">
                <div className="note-label">Lời nhắn kèm theo từ giảng viên:</div>
                <div className="note-text">{request.submissionNote || 'Không có ghi chú khi gửi duyệt.'}</div>
              </div>

              {/* Review History / Result if already reviewed */}
              {request.reviewedAt && (
                <div className={`feedback-box ${request.status === 'APPROVED' ? 'feedback-approved' : 'feedback-rejected'}`}>
                  <div className="feedback-title">
                    {request.status === 'APPROVED' ? 'Đã được phê duyệt lúc:' : 'Đã phản hồi yêu cầu bổ sung lúc:'}{' '}
                    {formatDateTime(request.reviewedAt)} {request.reviewedBy ? `bởi ${request.reviewedBy.login}` : ''}
                  </div>
                  <div className="feedback-content">{request.reviewerFeedback || 'Không có phản hồi thêm.'}</div>
                </div>
              )}
            </div>

            {/* 2. Course Content Overview */}
            <div className="detail-section-card">
              <h3>
                <BookOpen size={18} className="text-primary" /> Nội dung & Giới thiệu khóa học
              </h3>

              {course.shortDescription && (
                <div className="mb-3">
                  <div className="fw-bold small text-muted text-uppercase mb-1">Giới thiệu ngắn:</div>
                  <p className="lead fs-6 text-secondary mb-0">{course.shortDescription}</p>
                </div>
              )}

              {course.description && (
                <div className="mb-4">
                  <div className="fw-bold small text-muted text-uppercase mb-1">Mô tả chi tiết:</div>
                  <div className="text-dark bg-light p-3 rounded-3" style={{ whiteSpace: 'pre-wrap', lineHeight: '1.6' }}>
                    {course.description}
                  </div>
                </div>
              )}

              {/* Learning Objectives */}
              {course.learningObjectives && course.learningObjectives.length > 0 && (
                <div className="mb-4">
                  <div className="fw-bold small text-muted text-uppercase mb-2">Mục tiêu đạt được sau khóa học:</div>
                  <ul className="objectives-list">
                    {course.learningObjectives.map((obj, i) => (
                      <li key={i}>
                        <FontAwesomeIcon icon={faCheck} className="text-success mt-1" />
                        <span>{obj}</span>
                      </li>
                    ))}
                  </ul>
                </div>
              )}

              {/* Prerequisites & Target Audience */}
              <div className="info-grid mt-3">
                {course.targetAudience && (
                  <div className="info-item">
                    <div className="info-label">Đối tượng học viên</div>
                    <div className="info-val">{course.targetAudience}</div>
                  </div>
                )}
                {course.prerequisites && (
                  <div className="info-item">
                    <div className="info-label">Yêu cầu tiên quyết</div>
                    <div className="info-val">{course.prerequisites}</div>
                  </div>
                )}
              </div>

              {/* Intro Video URL */}
              {course.introVideoUrl && (
                <div className="mt-3 p-3 bg-light rounded-3 d-flex align-items-center justify-content-between">
                  <div className="d-flex align-items-center gap-2">
                    <Video size={18} className="text-primary" />
                    <span className="fw-semibold">Video giới thiệu khóa học</span>
                  </div>
                  <a
                    href={course.introVideoUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="btn btn-sm btn-outline-primary d-inline-flex align-items-center gap-1"
                  >
                    Xem video <ExternalLink size={14} />
                  </a>
                </div>
              )}
            </div>

            {/* 3. Curriculum / Chapters & Lessons */}
            <div className="detail-section-card">
              <div className="d-flex justify-content-between align-items-center mb-3">
                <h3 className="mb-0">
                  <Layers size={18} className="text-primary" /> Đề cương khóa học
                </h3>
                <span className="badge bg-light text-secondary border">
                  {chapters.length} chương • {totalLessons} bài học
                </span>
              </div>

              {chapters.length === 0 ? (
                <p className="text-muted small">Khóa học chưa có chương hoặc bài học nào được tạo.</p>
              ) : (
                <div className="curriculum-accordion">
                  {chapters.map((ch, idx) => (
                    <div key={ch.id || idx} className="chapter-card">
                      <div className="chapter-header">
                        <span>
                          Chương {idx + 1}: {ch.title}
                        </span>
                        <span className="chapter-meta">{ch.lessons.length} bài học</span>
                      </div>
                      <div className="lesson-list">
                        {ch.lessons.map((ls, lIdx) => (
                          <div key={ls.id || lIdx} className="lesson-item">
                            <div className="lesson-left">
                              <span className="lesson-icon">
                                {ls.contentType === 'VIDEO' ? <Video size={15} /> : <FileText size={15} />}
                              </span>
                              <span className="lesson-title">
                                {idx + 1}.{lIdx + 1} {ls.title}
                              </span>
                              {ls.isPreview && <span className="preview-tag">Xem thử</span>}
                            </div>
                            <div className="lesson-dur">
                              {ls.durationMinutes
                                ? `${ls.durationMinutes} phút`
                                : ls.durationSeconds
                                  ? `${Math.round(ls.durationSeconds / 60)} phút`
                                  : '—'}
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </Col>

          {/* Right Column (4 cols): Course Summary & Decision Panel */}
          <Col lg={4}>
            {/* Course Summary Card */}
            <div className="detail-section-card course-summary-card">
              <div className="detail-thumb">
                <CourseThumbnail
                  courseId={course.id}
                  thumbnailUrl={course.thumbnailUrl}
                  subjectName={course.subject?.name}
                  title={course.title}
                />
              </div>

              <div className="detail-price">{formatPrice(course.price)}</div>
              <div className="detail-duration">{accessDurationText(course)}</div>

              <div className="d-flex flex-wrap gap-1 mb-3">
                <span className={`tag ${getSubjectClass(course.subject?.name)}`}>{course.subject?.name || 'Chưa phân loại môn'}</span>
                <span className="tag tag-grade">{course.grade?.name || 'Chưa phân loại lớp'}</span>
                {course.bookSeries && <span className="tag tag-book">{course.bookSeries}</span>}
              </div>

              <hr />

              <div className="small text-muted d-flex flex-column gap-2">
                <div className="d-flex justify-content-between">
                  <span>Trạng thái khóa học:</span>
                  <strong className="text-dark">{course.status}</strong>
                </div>
                <div className="d-flex justify-content-between">
                  <span>Ngày tạo:</span>
                  <span>{course.createdAt ? new Date(course.createdAt).toLocaleDateString('vi-VN') : '—'}</span>
                </div>
                <div className="d-flex justify-content-between">
                  <span>Cập nhật gần nhất:</span>
                  <span>{course.updatedAt ? new Date(course.updatedAt).toLocaleDateString('vi-VN') : '—'}</span>
                </div>
              </div>
            </div>

            {/* Review Decision Panel */}
            <div className="detail-section-card decision-card">
              <h3>Xét duyệt yêu cầu</h3>

              {isPending ? (
                <>
                  <p className="decision-prompt">
                    Kiểm tra kỹ nội dung bài giảng, bản quyền hình ảnh/video và thông tin học phí trước khi quyết định. Nhập nhận xét hoặc
                    yêu cầu chỉnh sửa bên dưới:
                  </p>

                  <Form.Group className="mb-3">
                    <Form.Control
                      as="textarea"
                      rows={4}
                      placeholder="Nhập nhận xét hoặc lý do yêu cầu chỉnh sửa (bắt buộc nếu từ chối)..."
                      value={feedback}
                      onChange={e => setFeedback(e.target.value)}
                      maxLength={5000}
                    />
                    <div className="small text-muted text-end mt-1">{feedback.length}/5000 ký tự</div>
                  </Form.Group>

                  <div className="decision-btns">
                    <button type="button" className="course-review-button success" onClick={handleOpenApprove}>
                      <CheckCircle2 size={16} /> Phê duyệt
                    </button>
                    <button type="button" className="course-review-button danger" onClick={handleOpenReject}>
                      <XCircle size={16} /> Từ chối
                    </button>
                  </div>
                </>
              ) : (
                <div className="text-muted small">
                  <p>
                    Yêu cầu này đã được xử lý với kết quả <strong>{reviewStatusLabels[request.status]}</strong>.
                  </p>
                  {request.reviewedAt && <p>Thời gian thẩm định: {formatDateTime(request.reviewedAt)}</p>}
                </div>
              )}
            </div>
          </Col>
        </Row>
      </main>

      {/* Approve Modal */}
      {dialogMode === 'approve' && (
        <Modal show onHide={handleCloseModal} centered backdrop="static">
          <Modal.Header closeButton={!actionLoading}>
            <Modal.Title>Xác nhận phê duyệt khóa học</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <p>
              Bạn sắp phê duyệt khóa học <strong>{course.title}</strong> (Phiên bản v{request.courseVersion}).
            </p>
            <p className="small text-muted mb-3">
              Sau khi phê duyệt, khóa học sẽ chuyển sang trạng thái sẵn sàng xuất bản (ACTIVE). Giảng viên sẽ nhận được thông báo chấp
              thuận.
            </p>

            <Form.Group className="mb-3">
              <Form.Label className="small fw-bold">Nhận xét gửi giảng viên (tùy chọn):</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                value={feedback}
                onChange={e => setFeedback(e.target.value)}
                placeholder="Lời khen hoặc hướng dẫn thêm..."
                disabled={actionLoading}
              />
            </Form.Group>

            {actionError && <div className="alert alert-danger py-2 small mb-0">{actionError}</div>}
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={handleCloseModal} disabled={actionLoading}>
              Hủy
            </Button>
            <Button variant="success" onClick={handleConfirmDecision} disabled={actionLoading}>
              {actionLoading ? 'Đang phê duyệt...' : 'Xác nhận phê duyệt'}
            </Button>
          </Modal.Footer>
        </Modal>
      )}

      {/* Reject Modal */}
      {dialogMode === 'reject' && (
        <Modal show onHide={handleCloseModal} centered backdrop="static">
          <Modal.Header closeButton={!actionLoading}>
            <Modal.Title>Yêu cầu chỉnh sửa / Từ chối khóa học</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <p>
              Khóa học <strong>{course.title}</strong> sẽ được trả về trạng thái bản nháp (DRAFT) để giảng viên chỉnh sửa và gửi lại sau.
            </p>

            <Form.Group className="mb-3">
              <Form.Label className="small fw-bold text-danger">Lý do từ chối / cần bổ sung (bắt buộc):</Form.Label>
              <Form.Control
                as="textarea"
                rows={4}
                value={feedback}
                onChange={e => setFeedback(e.target.value)}
                placeholder="Ghi rõ các điểm cần sửa (chất lượng video, vi phạm bản quyền, học phí chưa hợp lý, đề cương thiếu bài học)..."
                disabled={actionLoading}
              />
            </Form.Group>

            {actionError && <div className="alert alert-danger py-2 small mb-0">{actionError}</div>}
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={handleCloseModal} disabled={actionLoading}>
              Hủy
            </Button>
            <Button variant="danger" onClick={handleConfirmDecision} disabled={actionLoading}>
              {actionLoading ? 'Đang gửi...' : 'Xác nhận từ chối'}
            </Button>
          </Modal.Footer>
        </Modal>
      )}
    </AdminLayout>
  );
};

export default CourseReviewDetail;
