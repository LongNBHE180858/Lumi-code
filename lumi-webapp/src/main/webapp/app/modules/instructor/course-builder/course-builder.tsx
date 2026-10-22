import { toast } from 'react-toastify';
import React, { useState, useEffect, useRef } from 'react';
import { Alert, Button, Card, Badge, Nav, Spinner } from 'react-bootstrap';
import { Link, useNavigate, useParams, useSearchParams } from 'react-router';
import { useStore } from 'react-redux';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faArrowLeft,
  faCheck,
  faEye,
  faFloppyDisk,
  faBook,
  faGraduationCap,
  faBookmark,
  faLayerGroup,
  faBookOpen,
  faVideo,
} from '@fortawesome/free-solid-svg-icons';

import { useAppDispatch, useAppSelector, type IRootState } from 'app/config/store';
import { getCourseBuilder, resetCourseBuilder } from './course-builder.reducer';

import CourseBasicInfo from './basic-info/course-basic-info';
import CurriculumTab, { CurriculumNavigation } from './curriculum/curriculum-tab';
import CoursePricing, { PricingNavigation } from './pricing/course-pricing';
import CoursePreview from './preview/course-preview';
import CourseReviewSubmit from './review/course-review-submit';
import { courseLifecycle, isBuilderReadOnly } from './course-lifecycle';

export type BuilderTabType = 'basic-info' | 'curriculum' | 'pricing' | 'preview' | 'review';

const initialBuilderTab = (step: string | null): BuilderTabType => {
  if (step === 'curriculum' || step === 'pricing' || step === 'preview' || step === 'review') return step;
  return 'basic-info';
};

const BuilderActions = ({
  activeTab,
  saving,
  readOnly,
  onPreview,
}: {
  activeTab: BuilderTabType;
  saving: boolean;
  readOnly: boolean;
  onPreview: () => void;
}) => (
  <div className="d-flex gap-2">
    <Button variant="outline-secondary" className="rounded-pill px-4" onClick={onPreview} disabled={saving}>
      <FontAwesomeIcon icon={faEye} className="me-2" /> Xem trước
    </Button>
    <Button
      type="submit"
      form={activeTab === 'pricing' ? 'course-pricing-form' : 'course-basic-info-form'}
      name="intent"
      value="stay"
      variant="primary"
      className="rounded-pill px-4"
      disabled={saving || readOnly || (activeTab !== 'basic-info' && activeTab !== 'pricing')}
    >
      <FontAwesomeIcon icon={faFloppyDisk} className="me-2" />
      {saving ? 'Đang lưu...' : 'Lưu nháp'}
    </Button>
  </div>
);

export const CourseBuilder = () => {
  const { id } = useParams<{ id: string }>();
  const courseId = id ? parseInt(id, 10) : undefined;

  const dispatch = useAppDispatch();
  const store = useStore<IRootState>();
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  // Lấy state trực tiếp từ Redux Store
  const { data: builderData, loading, saving, lastSavedTime } = useAppSelector(state => state.courseBuilder);
  const course = builderData?.course;
  const lifecycle = courseLifecycle(builderData);
  const readOnly = isBuilderReadOnly(builderData);

  const [activeTab, setActiveTab] = useState<BuilderTabType>(() => initialBuilderTab(searchParams.get('step')));
  useEffect(() => {
    if (searchParams.get('step') === activeTab) return;
    setSearchParams(
      previous => {
        const next = new URLSearchParams(previous);
        next.set('step', activeTab);
        return next;
      },
      { replace: true },
    );
  }, [activeTab, searchParams, setSearchParams]);
  const [hasUnsavedInfo, setHasUnsavedInfo] = useState(false);
  const curriculumNavigationRef = useRef<CurriculumNavigation>(null);
  const pricingNavigationRef = useRef<PricingNavigation>(null);
  const requestTabChange = (tab: BuilderTabType) => {
    if (tab === activeTab) return;
    if (activeTab === 'curriculum' && curriculumNavigationRef.current) {
      curriculumNavigationRef.current.requestLeave(() => setActiveTab(tab));
    } else if (activeTab === 'pricing' && pricingNavigationRef.current) {
      pricingNavigationRef.current.requestLeave(() => setActiveTab(tab));
    } else {
      setActiveTab(tab);
    }
  };
  const routeKey = courseId ? String(courseId) : 'new';
  const [preparedRoute, setPreparedRoute] = useState<string | null>(null);
  const justSavedCourseIdRef = useRef<number | null>(null);

  // Only load on a course route change. Media responses must not reset the form.
  useEffect(() => {
    if (!courseId) {
      dispatch(resetCourseBuilder());
      setPreparedRoute('new');
      return;
    }
    // POST/PUT already supplied this aggregate when /new becomes /:id/builder.
    setPreparedRoute(String(courseId));
    if (justSavedCourseIdRef.current === courseId && store.getState().courseBuilder.data?.course.id === courseId) {
      justSavedCourseIdRef.current = null;
      return;
    }
    const request = dispatch(getCourseBuilder(courseId));
    request.then(action => {
      if (getCourseBuilder.rejected.match(action) && !action.meta.aborted) {
        toast.error(action.error.message || 'Khóa học không tồn tại hoặc bạn không có quyền truy cập.');
        navigate('/instructor/courses');
      }
    });
    return () => {
      request.abort();
    };
  }, [courseId, dispatch, store]);

  const handleBasicInfoSaved = (savedCourseId: number, goNext: boolean) => {
    const nextTab = goNext ? 'curriculum' : 'basic-info';
    setHasUnsavedInfo(false);
    setActiveTab(nextTab);
    if (courseId !== savedCourseId) {
      justSavedCourseIdRef.current = savedCourseId;
      navigate(`/instructor/courses/${savedCourseId}/builder?step=${nextTab}`, { replace: true });
    }
  };

  if ((preparedRoute !== routeKey && justSavedCourseIdRef.current !== courseId) || (loading && course?.id !== courseId)) {
    return (
      <div className="p-5 text-center">
        <Spinner animation="border" variant="primary" />
        <div className="mt-2 text-muted">Đang tải dữ liệu khóa học...</div>
      </div>
    );
  }

  return (
    <div className="course-builder-container p-4">
      {/* 1. Breadcrumbs */}
      <div className="text-muted small mb-2">
        Trang chủ › Không gian giảng viên ›{' '}
        <Link
          to="/instructor/courses"
          onClick={event => {
            if (activeTab === 'curriculum' && curriculumNavigationRef.current) {
              event.preventDefault();
              curriculumNavigationRef.current.requestLeave(() => navigate('/instructor/courses'));
            } else if (activeTab === 'pricing' && pricingNavigationRef.current) {
              event.preventDefault();
              pricingNavigationRef.current.requestLeave(() => navigate('/instructor/courses'));
            }
          }}
          className="text-decoration-none"
        >
          Khóa học của tôi
        </Link>{' '}
        › Quản lý khóa học
      </div>

      {/* 2. Tiêu đề */}
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold mb-1">Xây dựng khóa học</h2>
          <span className="text-muted small">Tạo nội dung, thiết lập học phí và gửi duyệt trong cùng một không gian.</span>
        </div>
        <Link
          to="/instructor/courses"
          onClick={event => {
            if (activeTab === 'curriculum' && curriculumNavigationRef.current) {
              event.preventDefault();
              curriculumNavigationRef.current.requestLeave(() => navigate('/instructor/courses'));
            } else if (activeTab === 'pricing' && pricingNavigationRef.current) {
              event.preventDefault();
              pricingNavigationRef.current.requestLeave(() => navigate('/instructor/courses'));
            }
          }}
          className="text-decoration-none fw-semibold"
        >
          <FontAwesomeIcon icon={faArrowLeft} className="me-1" /> Khóa học của tôi
        </Link>
      </div>

      {/* 3. Ngữ cảnh khóa học (Context Card trên Header) */}
      <Card className="mb-4 border-0 shadow-sm p-4 rounded-4" style={{ backgroundColor: '#F8FAFC' }}>
        <div className="d-flex justify-content-between align-items-start mb-3">
          <div>
            <div className="d-flex align-items-center gap-2 mb-2">
              <Badge bg="info" className="text-primary bg-info-subtle px-3 py-2 rounded-pill fw-bold">
                {lifecycle.label}
              </Badge>
              <span className="text-muted small">{lifecycle.description}</span>
            </div>
            <h3 className="fw-bold mb-0 text-dark">{course?.title || 'Khóa học chưa đặt tên'}</h3>
          </div>
          <BuilderActions activeTab={activeTab} saving={saving} readOnly={readOnly} onPreview={() => requestTabChange('preview')} />
        </div>

        <div className="d-flex flex-wrap justify-content-between align-items-center gap-3 border-top pt-3">
          <div className="d-flex flex-wrap align-items-center gap-2">
            {/* Nhãn phân loại môn, lớp, bộ sách */}
            <div className="d-flex flex-wrap align-items-center gap-1">
              <span
                className="badge rounded-pill d-inline-flex align-items-center gap-1 py-1 px-2 fw-medium border"
                style={{ backgroundColor: 'rgba(13, 110, 253, 0.08)', color: '#0d6efd', borderColor: 'rgba(13, 110, 253, 0.2)' }}
              >
                <FontAwesomeIcon icon={faBook} style={{ fontSize: '10px' }} />
                <span>{course?.subject?.name || 'Chưa chọn môn'}</span>
              </span>

              <span
                className="badge rounded-pill d-inline-flex align-items-center gap-1 py-1 px-2 fw-medium border"
                style={{ backgroundColor: 'rgba(253, 126, 20, 0.08)', color: '#fd7e14', borderColor: 'rgba(253, 126, 20, 0.25)' }}
              >
                <FontAwesomeIcon icon={faGraduationCap} style={{ fontSize: '10px' }} />
                <span>{course?.grade?.name || 'Chưa chọn lớp'}</span>
              </span>

              <span
                className="badge rounded-pill d-inline-flex align-items-center gap-1 py-1 px-2 fw-medium border"
                style={{ backgroundColor: 'rgba(108, 117, 125, 0.08)', color: '#495057', borderColor: 'rgba(108, 117, 125, 0.2)' }}
              >
                <FontAwesomeIcon icon={faBookmark} style={{ fontSize: '10px' }} />
                <span>{course?.bookSeries || 'Chưa chọn bộ sách'}</span>
              </span>
            </div>

            {/* Vạch ngăn cách dọc */}
            <div className="d-none d-md-block vr mx-1 opacity-25" style={{ height: '18px' }} />

            {/* Thống kê quy mô khóa học */}
            <div className="d-flex align-items-center gap-2 text-secondary small py-1 px-2.5 rounded-pill bg-light border border-light-subtle">
              <span className="d-inline-flex align-items-center gap-1">
                <FontAwesomeIcon icon={faLayerGroup} className="text-primary opacity-75" style={{ fontSize: '11px' }} />
                <span>
                  <strong className="text-dark fw-semibold">{builderData?.chapters?.length || 0}</strong> chương
                </span>
              </span>

              <span className="text-muted opacity-50">•</span>

              <span className="d-inline-flex align-items-center gap-1">
                <FontAwesomeIcon icon={faBookOpen} className="text-info opacity-75" style={{ fontSize: '11px' }} />
                <span>
                  <strong className="text-dark fw-semibold">{builderData?.totalLessons || 0}</strong> bài học
                </span>
              </span>

              <span className="text-muted opacity-50">•</span>

              <span className="d-inline-flex align-items-center gap-1">
                <FontAwesomeIcon icon={faVideo} className="text-danger opacity-75" style={{ fontSize: '11px' }} />
                <span>
                  <strong className="text-dark fw-semibold">{builderData?.totalVideos || 0}</strong> video
                </span>
              </span>
            </div>
          </div>

          {/* Trạng thái lưu dữ liệu */}
          {hasUnsavedInfo ? (
            <div className="badge rounded-pill bg-warning-subtle text-warning-emphasis border border-warning-subtle px-3 py-1.5 fw-semibold d-inline-flex align-items-center gap-1.5">
              <span className="spinner-grow spinner-grow-sm" style={{ width: '7px', height: '7px' }} />
              Thông tin có thay đổi chưa lưu
            </div>
          ) : (
            lastSavedTime && (
              <div className="badge rounded-pill bg-success-subtle text-success border border-success-subtle px-3 py-1.5 fw-semibold d-inline-flex align-items-center gap-1.5">
                <FontAwesomeIcon icon={faCheck} />
                Đã lưu lúc {lastSavedTime}
              </div>
            )
          )}
        </div>
      </Card>

      {readOnly && (
        <Alert variant="info" className="rounded-4">
          Khóa học hiện chỉ có thể xem. Kiểm tra trạng thái và yêu cầu phê duyệt tại tab Gửi duyệt.
        </Alert>
      )}

      {/* 4. Thanh 5 Tabs điều hướng */}
      <Nav variant="underline" className="mb-4 border-bottom">
        <Nav.Item>
          <Nav.Link
            disabled={saving}
            active={activeTab === 'basic-info'}
            onClick={() => requestTabChange('basic-info')}
            className="fw-bold px-4 py-3"
          >
            Thông tin khóa học
          </Nav.Link>
        </Nav.Item>
        <Nav.Item>
          <Nav.Link
            disabled={saving}
            active={activeTab === 'curriculum'}
            onClick={() => requestTabChange('curriculum')}
            className="fw-bold px-4 py-3"
          >
            Nội dung bài học
          </Nav.Link>
        </Nav.Item>
        <Nav.Item>
          <Nav.Link
            disabled={saving}
            active={activeTab === 'pricing'}
            onClick={() => requestTabChange('pricing')}
            className="fw-bold px-4 py-3"
          >
            Học phí
          </Nav.Link>
        </Nav.Item>
        <Nav.Item>
          <Nav.Link
            disabled={saving}
            active={activeTab === 'preview'}
            onClick={() => requestTabChange('preview')}
            className="fw-bold px-4 py-3"
          >
            Xem trước
          </Nav.Link>
        </Nav.Item>
        <Nav.Item>
          <Nav.Link
            disabled={saving}
            active={activeTab === 'review'}
            onClick={() => requestTabChange('review')}
            className="fw-bold px-4 py-3"
          >
            Gửi duyệt
          </Nav.Link>
        </Nav.Item>
      </Nav>

      {/* 5. Nội dung Tab hiển thị */}
      <div hidden={activeTab !== 'basic-info'}>
        <CourseBasicInfo onSaved={handleBasicInfoSaved} onDirtyChange={setHasUnsavedInfo} />
      </div>
      {activeTab === 'curriculum' && (
        <CurriculumTab
          navigationRef={curriculumNavigationRef}
          onBack={() => setActiveTab('basic-info')}
          onNext={() => setActiveTab('pricing')}
        />
      )}
      {activeTab === 'pricing' && (
        <CoursePricing
          key={course?.id ?? 'new'}
          navigationRef={pricingNavigationRef}
          onBack={() => setActiveTab('curriculum')}
          onNext={() => setActiveTab('preview')}
        />
      )}
      {activeTab === 'preview' && (
        <CoursePreview
          onBack={() => setActiveTab('pricing')}
          onNext={() => setActiveTab('review')}
          onNavigateTab={tab => setActiveTab(tab)}
        />
      )}
      {activeTab === 'review' && (
        <CourseReviewSubmit key={course?.id ?? 'new'} onBack={() => setActiveTab('preview')} onNavigateTab={tab => setActiveTab(tab)} />
      )}
    </div>
  );
};

export default CourseBuilder;
