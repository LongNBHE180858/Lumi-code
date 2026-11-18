import './course-review.scss';

import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router';
import { Row, Col, Modal, Form, Button, Spinner, Alert, Badge } from 'react-bootstrap';
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
  Play,
  HelpCircle,
  ChevronDown,
  ChevronUp,
  Award,
  Eye,
  EyeOff,
  Edit3,
} from 'lucide-react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faBookOpen, faCheck } from '@fortawesome/free-solid-svg-icons';
import axios from 'axios';
import { notificationWebSocketService } from 'app/shared/notification/notification-websocket.service';

import AdminLayout from '../../layout/admin-layout';
import MaterialPreviewModal, {
  type IMaterialPreviewState,
} from 'app/modules/instructor/course-builder/curriculum/lesson/material-preview-modal';
import {
  courseReviewApi,
  type ICourseReviewRequest,
  type ICourseReviewChapter,
  type ICourseReviewLesson,
  type ICourseReviewMaterial,
  type IReviewExamSummary,
  type IReviewExamDetail,
  reviewStatusLabels,
  courseStatusLabels,
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
  const [courseExams, setCourseExams] = useState<IReviewExamSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Expanded lessons for review
  const [expandedLessonIds, setExpandedLessonIds] = useState<Set<number>>(new Set());

  // Review decision states
  const [feedback, setFeedback] = useState('');
  const [dialogMode, setDialogMode] = useState<'approve' | 'reject' | 'hide' | 'unhide' | 'revision' | null>(null);
  const [actionLoading, setActionLoading] = useState(false);
  const [actionError, setActionError] = useState<string | null>(null);

  // Exam Inspection Modal
  const [examModal, setExamModal] = useState<{
    show: boolean;
    loading: boolean;
    detail: IReviewExamDetail | null;
    error: string | null;
  }>({
    show: false,
    loading: false,
    detail: null,
    error: null,
  });

  // Video Inspection Modal
  const [videoModal, setVideoModal] = useState<{
    show: boolean;
    loading: boolean;
    title: string;
    url: string | null;
    error: string | null;
  }>({
    show: false,
    loading: false,
    title: '',
    url: null,
    error: null,
  });

  // Material Inspection Modal
  const [materialModal, setMaterialModal] = useState<IMaterialPreviewState>({
    isOpen: false,
    name: '',
    fileType: '',
    url: '',
    loading: false,
    error: '',
  });

  const toggleLesson = (lessonId: number) => {
    setExpandedLessonIds(prev => {
      const next = new Set(prev);
      if (next.has(lessonId)) {
        next.delete(lessonId);
      } else {
        next.add(lessonId);
      }
      return next;
    });
  };

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

        const courseId = reqData.course.id;

        // Fetch curriculum data (builder chapters & lessons)
        try {
          const [builderData, cExams] = await Promise.all([
            courseReviewApi.getBuilderData(courseId),
            courseReviewApi.getCourseExams(courseId),
          ]);

          if (!active) return;
          if (cExams) setCourseExams(cExams);

          if (builderData && builderData.chapters) {
            // Load chapter & lesson exams in parallel
            const mappedChapters: ICourseReviewChapter[] = await Promise.all(
              builderData.chapters.map(
                async (ch: {
                  chapter: { id: number; title: string; description?: string; orderIndex: number };
                  lessons: { lesson: ICourseReviewLesson; materials: ICourseReviewMaterial[] }[];
                }) => {
                  const [chExams, ...lessonExamResults] = await Promise.all([
                    courseReviewApi.getChapterExams(courseId, ch.chapter.id),
                    ...ch.lessons.map(async ln => {
                      const [lExams, lCue] = await Promise.all([
                        courseReviewApi.getLessonExams(courseId, ln.lesson.id),
                        courseReviewApi.getLessonCuePoints(courseId, ln.lesson.id),
                      ]);
                      return { lessonId: ln.lesson.id, exams: lExams, cuePoints: lCue };
                    }),
                  ]);

                  const lessonExamMap = new Map(lessonExamResults.map(r => [r.lessonId, r]));

                  return {
                    id: ch.chapter.id,
                    title: ch.chapter.title,
                    description: ch.chapter.description,
                    orderIndex: ch.chapter.orderIndex,
                    exams: chExams || [],
                    lessons: ch.lessons.map(ln => {
                      const examInfo = lessonExamMap.get(ln.lesson.id);
                      return {
                        ...ln.lesson,
                        materials: ln.materials || [],
                        exams: examInfo?.exams || [],
                        cuePoints: examInfo?.cuePoints || [],
                      };
                    }),
                  };
                },
              ),
            );

            if (active) {
              setChapters(mappedChapters);
              // Auto-expand first 2 lessons for convenience
              const firstLessons = mappedChapters.flatMap(c => c.lessons).slice(0, 2);
              setExpandedLessonIds(new Set(firstLessons.map(l => l.id)));
            }
            return;
          }
        } catch {
          /* Fallback if builder fails */
        }

        try {
          const [lessonsRes, cExams] = await Promise.all([courseReviewApi.getLessons(courseId), courseReviewApi.getCourseExams(courseId)]);
          if (!active) return;
          if (cExams) setCourseExams(cExams);

          const lessonList = lessonsRes.data || [];
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
          /* Empty curriculum */
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

  // Listen for real-time review status changes (e.g. author withdrew review)
  useEffect(() => {
    if (!id) return;

    const unsubscribe = notificationWebSocketService.subscribeTopic(`/topic/course-review/${id}`, (data: any) => {
      if (data && data.status === 'WITHDRAWN') {
        setRequest(prev => (prev ? { ...prev, status: 'WITHDRAWN' } : prev));
        setDialogMode(null);
        toast.warning('⚠️ Tác giả vừa rút lại yêu cầu duyệt khóa học này để chỉnh sửa thêm.', {
          autoClose: 8000,
        });
      }
    });

    return () => {
      unsubscribe();
    };
  }, [id]);

  // Listen for real-time course lifecycle status changes (hide, unhide, request revision, request edit)
  useEffect(() => {
    const courseId = request?.course?.id;
    if (!courseId) return;

    const unsubscribe = notificationWebSocketService.subscribeTopic(`/topic/course-review/course/${courseId}`, (data: any) => {
      if (!data) return;
      if (data.action === 'HIDE') {
        setRequest(prev => (prev ? { ...prev, course: { ...prev.course, status: 'HIDDEN' } } : prev));
        toast.info(`Khóa học đã được tạm ẩn (${data.reason || 'Lý do hệ thống'}).`);
      } else if (data.action === 'UNHIDE') {
        setRequest(prev => (prev ? { ...prev, course: { ...prev.course, status: 'ACTIVE' } } : prev));
        toast.info('Khóa học đã được mở lại thành công.');
      } else if (data.action === 'REQUEST_REVISION' || data.action === 'REQUEST_EDIT') {
        setRequest(prev => (prev ? { ...prev, course: { ...prev.course, status: 'DRAFT' } } : prev));
        toast.info(`Khóa học đã chuyển về bản nháp để chỉnh sửa (${data.reason || 'Yêu cầu chỉnh sửa'}).`);
      }
    });

    return () => {
      unsubscribe();
    };
  }, [request?.course?.id]);

  const handleOpenApprove = () => {
    setDialogMode('approve');
    setActionError(null);
  };

  const handleOpenReject = () => {
    setDialogMode('reject');
    setActionError(null);
  };

  const handleOpenHide = () => {
    setDialogMode('hide');
    setFeedback('');
    setActionError(null);
  };

  const handleOpenUnhide = () => {
    setDialogMode('unhide');
    setActionError(null);
  };

  const handleOpenRevision = () => {
    setDialogMode('revision');
    setFeedback('');
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

    if ((dialogMode === 'reject' || dialogMode === 'hide' || dialogMode === 'revision') && !trimmedFeedback) {
      const msg =
        dialogMode === 'hide'
          ? 'Vui lòng nhập lý do tạm ẩn khóa học.'
          : dialogMode === 'revision'
            ? 'Vui lòng nhập lý do yêu cầu chỉnh sửa.'
            : 'Vui lòng nhập lý do từ chối / yêu cầu chỉnh sửa để giảng viên biết cần hoàn thiện mục nào.';
      setActionError(msg);
      return;
    }

    setActionLoading(true);
    setActionError(null);

    try {
      if (dialogMode === 'approve') {
        const updated = await courseReviewApi.approve(request, trimmedFeedback || 'Đã kiểm tra và phê duyệt khóa học.');
        setRequest(updated);
        toast.success(`Đã phê duyệt khóa học "${request.course?.title || ''}".`);
      } else if (dialogMode === 'reject') {
        const updated = await courseReviewApi.reject(request, trimmedFeedback);
        setRequest(updated);
        toast.success(`Đã gửi phản hồi từ chối / yêu cầu chỉnh sửa cho khóa học "${request.course?.title || ''}".`);
      } else if (dialogMode === 'hide') {
        await courseReviewApi.hideCourse(request.course.id, trimmedFeedback);
        setRequest(prev => (prev ? { ...prev, course: { ...prev.course, status: 'HIDDEN' } } : prev));
        toast.success(`Đã tạm ẩn khóa học "${request.course?.title || ''}".`);
      } else if (dialogMode === 'unhide') {
        await courseReviewApi.unhideCourse(request.course.id);
        setRequest(prev => (prev ? { ...prev, course: { ...prev.course, status: 'ACTIVE' } } : prev));
        toast.success(`Đã mở lại khóa học "${request.course?.title || ''}".`);
      } else if (dialogMode === 'revision') {
        await courseReviewApi.requestRevision(request.course.id, trimmedFeedback);
        setRequest(prev => (prev ? { ...prev, course: { ...prev.course, status: 'DRAFT' } } : prev));
        toast.success(`Đã yêu cầu giảng viên chỉnh sửa khóa học "${request.course?.title || ''}".`);
      }
      setDialogMode(null);
    } catch (err) {
      if (axios.isAxiosError(err)) {
        if (err.response?.status === 400 && id) {
          courseReviewApi
            .get(id)
            .then(res => setRequest(res.data))
            .catch(() => {});
        }
        setActionError(err.response?.data?.message || 'Có lỗi xảy ra hoặc yêu cầu đã được xử lý bởi tác giả.');
      } else {
        setActionError('Có lỗi xảy ra trong quá trình xử lý. Vui lòng thử lại.');
      }
    } finally {
      setActionLoading(false);
    }
  };

  const handleOpenExam = async (examId: number) => {
    if (!request?.course?.id) return;
    setExamModal({
      show: true,
      loading: true,
      detail: null,
      error: null,
    });
    try {
      const detail = await courseReviewApi.getExamDetail(request.course.id, examId);
      if (detail) {
        setExamModal({
          show: true,
          loading: false,
          detail,
          error: null,
        });
      } else {
        setExamModal({
          show: true,
          loading: false,
          detail: null,
          error: 'Không tìm thấy nội dung bài kiểm tra.',
        });
      }
    } catch {
      setExamModal({
        show: true,
        loading: false,
        detail: null,
        error: 'Không thể tải đề bài và câu hỏi của bài kiểm tra.',
      });
    }
  };

  const handleOpenVideo = async (lesson: ICourseReviewLesson) => {
    if (!request?.course?.id) return;
    setVideoModal({
      show: true,
      loading: true,
      title: lesson.title,
      url: null,
      error: null,
    });
    try {
      const url = await courseReviewApi.getVideoPlayback(request.course.id, lesson.id);
      if (url) {
        setVideoModal({
          show: true,
          loading: false,
          title: lesson.title,
          url,
          error: null,
        });
      } else {
        setVideoModal({
          show: true,
          loading: false,
          title: lesson.title,
          url: null,
          error: 'Không tìm thấy liên kết phát video bài học.',
        });
      }
    } catch {
      setVideoModal({
        show: true,
        loading: false,
        title: lesson.title,
        url: null,
        error: 'Không thể tải video bài học hoặc chưa có video đính kèm.',
      });
    }
  };

  const handleOpenMaterial = async (material: ICourseReviewMaterial, lessonId: number) => {
    if (!request?.course?.id) return;
    const name = material.displayName || material.fileName || material.title || 'Tài liệu';
    setMaterialModal({
      isOpen: true,
      name,
      fileType: material.fileType || '',
      url: '',
      loading: true,
      error: '',
    });
    try {
      const url = await courseReviewApi.getMaterialPreview(request.course.id, lessonId, material.id);
      if (url) {
        setMaterialModal(prev => ({
          ...prev,
          url,
          loading: false,
        }));
      } else {
        setMaterialModal(prev => ({
          ...prev,
          loading: false,
          error: 'Không tìm thấy đường dẫn xem trước tài liệu.',
        }));
      }
    } catch {
      setMaterialModal(prev => ({
        ...prev,
        loading: false,
        error: 'Không thể tải tài liệu xem trước.',
      }));
    }
  };

  const handleDownloadMaterial = async (material: ICourseReviewMaterial, lessonId: number) => {
    if (!request?.course?.id) return;
    try {
      const url = await courseReviewApi.getMaterialDownload(request.course.id, lessonId, material.id);
      if (url) {
        window.open(url, '_blank');
      } else {
        toast.error('Không tìm thấy đường dẫn tải tài liệu.');
      }
    } catch {
      toast.error('Không thể tải tài liệu này.');
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
            {!isPending && course.status === 'ACTIVE' && (
              <>
                <button type="button" className="course-review-button warning" onClick={handleOpenHide}>
                  <EyeOff size={16} /> Tạm ẩn khóa học
                </button>
                <button type="button" className="course-review-button danger" onClick={handleOpenRevision}>
                  <Edit3 size={16} /> Yêu cầu sửa
                </button>
              </>
            )}
            {!isPending && course.status === 'HIDDEN' && (
              <>
                <button type="button" className="course-review-button success" onClick={handleOpenUnhide}>
                  <Eye size={16} /> Mở lại khóa học
                </button>
                <button type="button" className="course-review-button danger" onClick={handleOpenRevision}>
                  <Edit3 size={16} /> Yêu cầu sửa
                </button>
              </>
            )}
          </div>
        </div>

        {request.status === 'WITHDRAWN' && (
          <Alert variant="warning" className="d-flex align-items-center gap-3 my-4 py-3 shadow-sm border-warning">
            <AlertCircle size={24} className="text-warning flex-shrink-0" />
            <div>
              <div className="fw-bold fs-6 text-dark">Yêu cầu duyệt đã được tác giả rút lại</div>
              <div className="text-muted small">
                Giảng viên đã đưa khóa học về bản nháp để tiếp tục hoàn thiện nội dung. Các thao tác phê duyệt hoặc từ chối đối với yêu cầu
                này đã được khóa.
              </div>
            </div>
          </Alert>
        )}

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
                  <div className="info-val">{request.submittedBy?.email || request.submittedBy?.login || '—'}</div>
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

            {/* 3. Curriculum / Chapters, Lessons, Exams */}
            <div className="detail-section-card">
              <div className="d-flex justify-content-between align-items-center mb-3">
                <h3 className="mb-0">
                  <Layers size={18} className="text-primary" /> Đề cương khóa học & Thẩm định nội dung
                </h3>
                <span className="badge bg-light text-secondary border">
                  {chapters.length} chương • {totalLessons} bài học {courseExams.length > 0 ? `• ${courseExams.length} đề thi khóa` : ''}
                </span>
              </div>

              {/* Course-level Exams */}
              {courseExams.length > 0 && (
                <div className="course-exams-container">
                  <div className="fw-bold small text-muted text-uppercase mb-2 d-flex align-items-center gap-1.5">
                    <Award size={16} className="text-primary" />
                    <span>Bài kiểm tra tổng kết khóa học ({courseExams.length})</span>
                  </div>
                  <div className="d-flex flex-column gap-2">
                    {courseExams.map(ex => (
                      <div key={ex.id} className="course-exam-item">
                        <div>
                          <div className="fw-semibold text-dark">🎯 {ex.title}</div>
                          <div className="small text-muted">
                            {ex.durationMinutes ? `${ex.durationMinutes} phút` : 'Không giới hạn thời gian'} • {ex.questionCount || 0} câu
                            hỏi • Điểm đạt: {ex.passScore ?? 60}%
                          </div>
                        </div>
                        <button
                          type="button"
                          className="course-review-button primary"
                          style={{ minHeight: '32px', padding: '4px 12px', fontSize: '12px' }}
                          onClick={() => handleOpenExam(ex.id)}
                        >
                          <Eye size={13} /> Xem đề thi & đáp án
                        </button>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {chapters.length === 0 ? (
                <p className="text-muted small">Khóa học chưa có chương hoặc bài học nào được tạo.</p>
              ) : (
                <div className="curriculum-accordion">
                  {chapters.map((ch, idx) => (
                    <div key={ch.id || idx} className="chapter-card">
                      <div className="chapter-header">
                        <div>
                          <strong>
                            Chương {idx + 1}: {ch.title}
                          </strong>
                          {ch.description && <div className="small text-muted mt-0.5">{ch.description}</div>}
                        </div>
                        <span className="chapter-meta">{ch.lessons.length} bài học</span>
                      </div>

                      {/* Chapter-level Exam if present */}
                      {ch.exams && ch.exams.length > 0 && (
                        <div>
                          {ch.exams.map(ex => (
                            <div key={ex.id} className="chapter-exam-box">
                              <div>
                                <div className="fw-semibold text-success-emphasis">📝 Bài kiểm tra chương: {ex.title}</div>
                                <div className="small text-muted">
                                  {ex.durationMinutes ? `${ex.durationMinutes} phút` : 'Không giới hạn thời gian'} • {ex.questionCount || 0}{' '}
                                  câu hỏi • Điểm đạt: {ex.passScore ?? 60}%
                                </div>
                              </div>
                              <button
                                type="button"
                                className="course-review-button success"
                                style={{ minHeight: '30px', padding: '3px 10px', fontSize: '12px' }}
                                onClick={() => handleOpenExam(ex.id)}
                              >
                                <Eye size={13} /> Xem đề bài & đáp án
                              </button>
                            </div>
                          ))}
                        </div>
                      )}

                      <div className="lesson-list">
                        {ch.lessons.map((ls, lIdx) => {
                          const isExpanded = expandedLessonIds.has(ls.id);
                          return (
                            <div key={ls.id || lIdx} className="lesson-item flex-column align-items-stretch">
                              <div
                                className="d-flex justify-content-between align-items-center w-100"
                                style={{ cursor: 'pointer' }}
                                onClick={() => toggleLesson(ls.id)}
                              >
                                <div className="lesson-left">
                                  <span className="lesson-icon">
                                    {ls.contentType === 'VIDEO' ? <Video size={15} /> : <FileText size={15} />}
                                  </span>
                                  <span className="lesson-title">
                                    {idx + 1}.{lIdx + 1} {ls.title}
                                  </span>
                                  {ls.isPreview && <span className="preview-tag">Xem thử</span>}

                                  {ls.materials && ls.materials.length > 0 && (
                                    <span className="badge bg-light text-secondary border small">{ls.materials.length} tài liệu</span>
                                  )}
                                  {ls.exams && ls.exams.length > 0 && (
                                    <span className="badge bg-light text-success border small">{ls.exams.length} bài tập</span>
                                  )}
                                  {ls.cuePoints && ls.cuePoints.length > 0 && (
                                    <span className="badge bg-light text-warning-emphasis border small">
                                      {ls.cuePoints.length} câu hỏi video
                                    </span>
                                  )}
                                </div>
                                <div className="d-flex align-items-center gap-2">
                                  <span className="lesson-dur">
                                    {ls.durationMinutes
                                      ? `${ls.durationMinutes} phút`
                                      : ls.durationSeconds
                                        ? `${Math.round(ls.durationSeconds / 60)} phút`
                                        : '—'}
                                  </span>
                                  <span className="text-muted">{isExpanded ? <ChevronUp size={16} /> : <ChevronDown size={16} />}</span>
                                </div>
                              </div>

                              {/* Expanded Lesson Content Details for Reviewer */}
                              {isExpanded && (
                                <div className="lesson-detail-expand">
                                  {ls.description ? (
                                    <div className="lesson-desc">
                                      <strong>Mô tả:</strong> {ls.description}
                                    </div>
                                  ) : (
                                    <div className="lesson-desc text-muted fst-italic">Bài học này không có mô tả chi tiết.</div>
                                  )}

                                  {/* Video Content */}
                                  {(ls.contentType === 'VIDEO' || ls.videoUrl) && (
                                    <div className="media-action-bar">
                                      <div className="d-flex align-items-center gap-2">
                                        <Video size={16} className="text-primary" />
                                        <span className="fw-semibold">Video bài giảng</span>
                                        {ls.videoFileName && <span className="text-muted small">({ls.videoFileName})</span>}
                                      </div>
                                      <button
                                        type="button"
                                        className="course-review-button primary"
                                        style={{ minHeight: '30px', padding: '4px 12px', fontSize: '12px' }}
                                        onClick={() => handleOpenVideo(ls)}
                                      >
                                        <Play size={13} /> Phát & Thẩm định video
                                      </button>
                                    </div>
                                  )}

                                  {/* Lesson Materials */}
                                  {ls.materials && ls.materials.length > 0 && (
                                    <div>
                                      <div className="lesson-section-title">
                                        <FileText size={14} /> Tài liệu bài học ({ls.materials.length}):
                                      </div>
                                      <div className="materials-list">
                                        {ls.materials.map(mat => (
                                          <div key={mat.id} className="material-item">
                                            <span className="text-truncate" style={{ maxWidth: '60%' }}>
                                              📎 {mat.displayName || mat.fileName || mat.title || 'Tài liệu'}
                                            </span>
                                            <div className="d-flex gap-2">
                                              <button
                                                type="button"
                                                className="course-review-button"
                                                style={{ minHeight: '26px', padding: '2px 8px', fontSize: '11px' }}
                                                onClick={() => handleOpenMaterial(mat, ls.id)}
                                              >
                                                Xem trước
                                              </button>
                                              <button
                                                type="button"
                                                className="course-review-button"
                                                style={{ minHeight: '26px', padding: '2px 8px', fontSize: '11px' }}
                                                onClick={() => handleDownloadMaterial(mat, ls.id)}
                                              >
                                                Tải về
                                              </button>
                                            </div>
                                          </div>
                                        ))}
                                      </div>
                                    </div>
                                  )}

                                  {/* Lesson Quizzes */}
                                  {ls.exams && ls.exams.length > 0 && (
                                    <div>
                                      <div className="lesson-section-title">
                                        <HelpCircle size={14} /> Bài tập ôn tập cuối bài ({ls.exams.length}):
                                      </div>
                                      <div className="exams-sublist">
                                        {ls.exams.map(ex => (
                                          <div key={ex.id} className="exam-item">
                                            <div>
                                              <strong>{ex.title}</strong>
                                              <span className="text-muted small ms-2">({ex.questionCount || 0} câu hỏi)</span>
                                            </div>
                                            <button
                                              type="button"
                                              className="course-review-button"
                                              style={{ minHeight: '26px', padding: '2px 8px', fontSize: '11px' }}
                                              onClick={() => handleOpenExam(ex.id)}
                                            >
                                              <Eye size={12} /> Xem đề & đáp án
                                            </button>
                                          </div>
                                        ))}
                                      </div>
                                    </div>
                                  )}

                                  {/* Lesson Cue Points */}
                                  {ls.cuePoints && ls.cuePoints.length > 0 && (
                                    <div>
                                      <div className="lesson-section-title">
                                        <Clock size={14} /> Câu hỏi dừng video tương tác ({ls.cuePoints.length}):
                                      </div>
                                      <div className="exams-sublist">
                                        {ls.cuePoints.map(cp => (
                                          <div key={cp.id} className="exam-item">
                                            <div>
                                              <span className="badge bg-warning text-dark me-2">
                                                Dừng tại {cp.videoTriggerSeconds ?? 0}s
                                              </span>
                                              <strong>{cp.title}</strong>
                                            </div>
                                            <button
                                              type="button"
                                              className="course-review-button"
                                              style={{ minHeight: '26px', padding: '2px 8px', fontSize: '11px' }}
                                              onClick={() => handleOpenExam(cp.id)}
                                            >
                                              <Eye size={12} /> Xem câu hỏi
                                            </button>
                                          </div>
                                        ))}
                                      </div>
                                    </div>
                                  )}
                                </div>
                              )}
                            </div>
                          );
                        })}
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
                  <strong className="text-dark">{courseStatusLabels[course.status] || course.status}</strong>
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
              ) : request.status === 'WITHDRAWN' ? (
                <div className="alert alert-warning mb-0 small">
                  <strong>Tác giả đã rút lại:</strong> Khóa học đã được đưa về trạng thái bản nháp. Chức năng thẩm định đã được khóa.
                </div>
              ) : course.status === 'ACTIVE' ? (
                <div className="d-flex flex-column gap-3">
                  <div className="alert alert-success mb-0 small">
                    <strong>Khóa học đang hoạt động:</strong> Khóa học đang xuất bản công khai. Bạn có thể tạm ẩn hoặc yêu cầu giảng viên
                    chỉnh sửa khi cần thiết.
                  </div>
                  <div className="d-flex flex-column gap-2">
                    <button type="button" className="course-review-button warning w-100 justify-content-center" onClick={handleOpenHide}>
                      <EyeOff size={16} /> Tạm ẩn khóa học
                    </button>
                    <button type="button" className="course-review-button danger w-100 justify-content-center" onClick={handleOpenRevision}>
                      <Edit3 size={16} /> Yêu cầu chỉnh sửa nội dung
                    </button>
                  </div>
                  {request.reviewedAt && (
                    <div className="text-muted small border-top pt-2">Thời gian phê duyệt: {formatDateTime(request.reviewedAt)}</div>
                  )}
                </div>
              ) : course.status === 'HIDDEN' ? (
                <div className="d-flex flex-column gap-3">
                  <div className="alert alert-warning mb-0 small">
                    <strong>Khóa học đang tạm ẩn:</strong> Khóa học không xuất hiện trên sàn khám phá. Học viên đã mua vẫn tiếp tục học bình
                    thường.
                  </div>
                  <div className="d-flex flex-column gap-2">
                    <button type="button" className="course-review-button success w-100 justify-content-center" onClick={handleOpenUnhide}>
                      <Eye size={16} /> Mở lại khóa học công khai
                    </button>
                    <button type="button" className="course-review-button danger w-100 justify-content-center" onClick={handleOpenRevision}>
                      <Edit3 size={16} /> Yêu cầu chỉnh sửa nội dung
                    </button>
                  </div>
                </div>
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

      {/* Exam Inspection Modal */}
      <Modal show={examModal.show} onHide={() => setExamModal(prev => ({ ...prev, show: false }))} size="lg" centered scrollable>
        <Modal.Header closeButton>
          <Modal.Title className="h5 fw-bold mb-0">📝 Thẩm định đề thi: {examModal.detail?.title || 'Chi tiết bài kiểm tra'}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {examModal.loading ? (
            <div className="text-center py-5">
              <Spinner animation="border" variant="primary" />
              <div className="mt-2 text-muted">Đang tải đề bài và câu hỏi...</div>
            </div>
          ) : examModal.error ? (
            <Alert variant="danger">{examModal.error}</Alert>
          ) : examModal.detail ? (
            <div>
              <div className="d-flex flex-wrap gap-2 mb-3 pb-3 border-bottom">
                <Badge bg="primary">Loại: {examModal.detail.assessmentType}</Badge>
                <Badge bg="info">
                  Thời gian: {examModal.detail.durationMinutes ? `${examModal.detail.durationMinutes} phút` : 'Không giới hạn'}
                </Badge>
                <Badge bg="success">Điểm đạt: {examModal.detail.passScore ?? 60}%</Badge>
                <Badge bg="secondary">Tổng số câu hỏi: {examModal.detail.questions?.length || 0}</Badge>
              </div>

              {!examModal.detail.questions || examModal.detail.questions.length === 0 ? (
                <p className="text-muted text-center py-4">Đề thi này chưa có câu hỏi nào được thêm.</p>
              ) : (
                <div className="d-flex flex-column gap-3">
                  {examModal.detail.questions.map((q, idx) => (
                    <div key={q.id || idx} className="p-3 bg-light rounded-3 border">
                      <div className="d-flex justify-content-between align-items-center mb-2">
                        <span className="fw-bold text-primary">Câu hỏi {idx + 1}:</span>
                        <div className="d-flex gap-2">
                          <span className="badge bg-secondary">Điểm: {q.score ?? 1}</span>
                          <span className="badge bg-light text-dark border">Độ khó: {q.difficulty || 'Trung bình'}</span>
                        </div>
                      </div>

                      <div className="fw-semibold text-dark mb-3" style={{ whiteSpace: 'pre-line' }}>
                        {q.content}
                      </div>

                      <div className="d-flex flex-column gap-1.5 mb-2">
                        {q.options?.map((opt, oIdx) => (
                          <div
                            key={opt.id || oIdx}
                            className={`p-2 px-3 rounded-2 border d-flex justify-content-between align-items-center ${
                              opt.isCorrect ? 'bg-success-subtle border-success text-success-emphasis fw-bold' : 'bg-white'
                            }`}
                          >
                            <span>
                              <strong>{String.fromCharCode(65 + oIdx)}.</strong> {opt.content}
                            </span>
                            {opt.isCorrect && (
                              <span className="badge bg-success d-flex align-items-center gap-1">
                                <CheckCircle2 size={12} /> Đáp án đúng
                              </span>
                            )}
                          </div>
                        ))}
                      </div>

                      {q.explanation && (
                        <div className="p-2 bg-info-subtle text-info-emphasis rounded-2 small mt-2">
                          💡 <strong>Lời giải thích:</strong> {q.explanation}
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              )}
            </div>
          ) : null}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setExamModal(prev => ({ ...prev, show: false }))}>
            Đóng
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Video Player Modal */}
      <Modal show={videoModal.show} onHide={() => setVideoModal(prev => ({ ...prev, show: false, url: null }))} size="lg" centered>
        <Modal.Header closeButton>
          <Modal.Title className="h5 fw-bold mb-0">
            <Video size={18} className="text-primary me-2" />
            {videoModal.title}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body className="p-3 text-center bg-black">
          {videoModal.loading ? (
            <div className="py-5 text-light">
              <Spinner animation="border" variant="light" />
              <div className="mt-2">Đang tải video bài học...</div>
            </div>
          ) : videoModal.error ? (
            <Alert variant="danger" className="mb-0">
              {videoModal.error}
            </Alert>
          ) : videoModal.url ? (
            <video src={videoModal.url} controls autoPlay playsInline style={{ width: '100%', maxHeight: '65vh', borderRadius: '8px' }}>
              Trình duyệt không hỗ trợ phát thẻ video.
            </video>
          ) : (
            <p className="text-white-50 py-4 mb-0">Không tìm thấy video bài học.</p>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setVideoModal(prev => ({ ...prev, show: false, url: null }))}>
            Đóng
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Material Preview Modal */}
      <MaterialPreviewModal
        preview={materialModal}
        onClose={() => setMaterialModal(prev => ({ ...prev, isOpen: false, url: '' }))}
        onDownload={() => {
          if (materialModal.url) window.open(materialModal.url, '_blank');
        }}
      />

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

      {/* Hide Course Modal */}
      {dialogMode === 'hide' && (
        <Modal show onHide={handleCloseModal} centered backdrop="static">
          <Modal.Header closeButton={!actionLoading}>
            <Modal.Title className="fw-bold">Tạm ẩn khóa học</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <p>
              Bạn sắp tạm ẩn khóa học <strong>{course.title}</strong> khỏi danh mục khóa học công khai.
            </p>
            <Alert variant="warning" className="small py-2 mb-3">
              Khóa học sẽ không xuất hiện trên sàn khám phá cho học viên mới. Học viên đã mua khóa học trước đây vẫn có thể vào học bình
              thường.
            </Alert>
            <Form.Group className="mb-3">
              <Form.Label className="small fw-bold text-danger">Lý do tạm ẩn (bắt buộc):</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                value={feedback}
                onChange={e => setFeedback(e.target.value)}
                placeholder="Nhập lý do tạm ẩn khóa học (phản ánh từ học sinh, lỗi nội dung, chờ cập nhật tài liệu)..."
                disabled={actionLoading}
              />
            </Form.Group>
            {actionError && <div className="alert alert-danger py-2 small mb-0">{actionError}</div>}
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={handleCloseModal} disabled={actionLoading}>
              Hủy
            </Button>
            <Button variant="warning" onClick={handleConfirmDecision} disabled={actionLoading || !feedback.trim()}>
              {actionLoading ? 'Đang tạm ẩn...' : 'Xác nhận tạm ẩn'}
            </Button>
          </Modal.Footer>
        </Modal>
      )}

      {/* Unhide Course Modal */}
      {dialogMode === 'unhide' && (
        <Modal show onHide={handleCloseModal} centered backdrop="static">
          <Modal.Header closeButton={!actionLoading}>
            <Modal.Title className="fw-bold">Mở lại khóa học</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <p>
              Bạn muốn mở lại khóa học <strong>{course.title}</strong> để hiển thị công khai trên hệ thống?
            </p>
            <p className="small text-muted mb-0">
              Khóa học sẽ trở về trạng thái <strong>ACTIVE (Đã xuất bản)</strong> và cho phép học viên mới tìm kiếm và đăng ký.
            </p>
            {actionError && <div className="alert alert-danger py-2 small mt-3 mb-0">{actionError}</div>}
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={handleCloseModal} disabled={actionLoading}>
              Hủy
            </Button>
            <Button variant="success" onClick={handleConfirmDecision} disabled={actionLoading}>
              {actionLoading ? 'Đang mở lại...' : 'Xác nhận mở lại'}
            </Button>
          </Modal.Footer>
        </Modal>
      )}

      {/* Request Revision Modal */}
      {dialogMode === 'revision' && (
        <Modal show onHide={handleCloseModal} centered backdrop="static">
          <Modal.Header closeButton={!actionLoading}>
            <Modal.Title className="fw-bold">Yêu cầu giảng viên chỉnh sửa nội dung</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <p>
              Khóa học <strong>{course.title}</strong> sẽ được chuyển sang trạng thái <strong>Bản nháp (DRAFT)</strong> để giảng viên cập
              nhật nội dung bài giảng, tài liệu hoặc học phí và gửi duyệt lại.
            </p>
            <Alert variant="info" className="small py-2 mb-3">
              Học viên đã mua khóa học trước đây vẫn tiếp tục được học bình thường.
            </Alert>
            <Form.Group className="mb-3">
              <Form.Label className="small fw-bold text-danger">Lý do & nội dung cần chỉnh sửa (bắt buộc):</Form.Label>
              <Form.Control
                as="textarea"
                rows={4}
                value={feedback}
                onChange={e => setFeedback(e.target.value)}
                placeholder="Ghi rõ các bài học, tài liệu hoặc nội dung cần chỉnh sửa/bổ sung..."
                disabled={actionLoading}
              />
            </Form.Group>
            {actionError && <div className="alert alert-danger py-2 small mb-0">{actionError}</div>}
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={handleCloseModal} disabled={actionLoading}>
              Hủy
            </Button>
            <Button variant="danger" onClick={handleConfirmDecision} disabled={actionLoading || !feedback.trim()}>
              {actionLoading ? 'Đang gửi...' : 'Xác nhận yêu cầu sửa'}
            </Button>
          </Modal.Footer>
        </Modal>
      )}
    </AdminLayout>
  );
};

export default CourseReviewDetail;
