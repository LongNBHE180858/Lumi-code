import React, { useState } from 'react';
import { Alert, Button, ButtonGroup, Card, Col, Row, Spinner } from 'react-bootstrap';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faEye,
  faRotateRight,
  faCirclePlay,
  faPlay,
  faArrowRight,
  faSliders,
  faUser,
  faCircleCheck,
  faLayerGroup,
  faVideo,
  faPen,
} from '@fortawesome/free-solid-svg-icons';
import './course-preview.scss';
import { getCourseBuilder } from '../course-builder.reducer';
import { PreviewMedia } from './preview-media';
import { PreviewHeader, PreviewDetails, PreviewPricing, PreviewCurriculum, PreviewLesson, type PreviewEditTab } from './preview-sections';

interface CoursePreviewProps {
  onBack: () => void;
  onNext: () => void;
  onNavigateTab?: (tab: PreviewEditTab) => void;
}

export const CoursePreview = ({ onBack, onNext, onNavigateTab }: CoursePreviewProps) => {
  const dispatch = useAppDispatch();
  const { data, loading, saving, errorStatus, errorMessage } = useAppSelector(state => state.courseBuilder);
  const [selectedLesson, setSelectedLesson] = useState<number | null>(null);
  const [viewMode, setViewMode] = useState<'review' | 'student'>('review');

  const edit = (tab: PreviewEditTab) => onNavigateTab?.(tab);

  if (loading)
    return (
      <div role="status" className="p-5 text-center preview-loading-state">
        <Spinner className="me-2 text-primary" />
        <span className="fw-semibold text-secondary">Đang tải bản xem trước...</span>
      </div>
    );

  if (errorStatus === 403 || errorStatus === 404)
    return <Alert variant="danger">Không thể truy cập khóa học. Khóa học không còn tồn tại hoặc bạn không có quyền xem.</Alert>;

  if (!data)
    return (
      <Alert variant="info">
        Hãy lưu thông tin khóa học trước khi xem trước.
        <Button variant="link" onClick={() => edit('basic-info')}>
          Thông tin khóa học
        </Button>
      </Alert>
    );

  const course = data.course;
  const previewLessons = data.chapters.flatMap(chapter => chapter.lessons).filter(node => node.lesson.isPreview);
  const selected = previewLessons.find(node => node.lesson.id === selectedLesson);
  const isStudentView = viewMode === 'student';

  return (
    <div className="course-preview d-flex flex-column gap-4">
      {/* 1. Thanh thông báo & chuyển đổi góc nhìn */}
      <Alert variant="info" className="preview-notice d-flex flex-wrap justify-content-between align-items-center gap-3 mb-0">
        <div className="d-flex align-items-center gap-2">
          <FontAwesomeIcon icon={faEye} className="text-primary fs-5" />
          <div>
            <div className="fw-bold">Bản xem trước chưa xuất bản — chỉ dùng để kiểm tra bản nháp đã lưu.</div>
            <div className="small text-muted d-none d-md-block">
              {isStudentView
                ? 'Đang ở góc nhìn học viên: đã ẩn các phím tắt biên tập.'
                : 'Đang ở chế độ soát lỗi: hiển thị các phím tắt chỉnh sửa.'}
            </div>
          </div>
        </div>

        <div className="d-flex flex-wrap align-items-center gap-2 ms-auto">
          <ButtonGroup size="sm" className="preview-view-toggle">
            <Button
              variant={viewMode === 'review' ? 'primary' : 'outline-primary'}
              className="fw-semibold px-3"
              onClick={() => setViewMode('review')}
            >
              <FontAwesomeIcon icon={faSliders} className="me-1.5" />
              Soát lỗi
            </Button>
            <Button
              variant={viewMode === 'student' ? 'primary' : 'outline-primary'}
              className="fw-semibold px-3"
              onClick={() => setViewMode('student')}
            >
              <FontAwesomeIcon icon={faUser} className="me-1.5" />
              Học viên
            </Button>
          </ButtonGroup>

          <Button
            size="sm"
            variant="outline-primary"
            className="fw-semibold"
            disabled={saving}
            onClick={() => {
              void dispatch(getCourseBuilder(course.id));
            }}
          >
            <FontAwesomeIcon icon={faRotateRight} className="me-2" />
            Tải lại bản xem trước
          </Button>
        </div>
      </Alert>

      {/* 2. Thanh tóm tắt nhanh độ sẵn sàng của khóa học */}
      <div className="preview-readiness-bar d-flex flex-wrap align-items-center justify-content-between gap-3 p-3 rounded-4 bg-white border">
        <div className="d-flex flex-wrap align-items-center gap-3 small">
          <span className="fw-semibold text-secondary d-flex align-items-center gap-1.5">
            <FontAwesomeIcon icon={faCircleCheck} className="text-success" />
            Tình trạng bản nháp:
          </span>
          <span className="badge rounded-pill bg-light text-secondary border px-3 py-1.5">
            <FontAwesomeIcon icon={faLayerGroup} className="me-1 text-primary" />
            {data.chapters.length} chương • {data.totalLessons} bài học
          </span>
          <span className="badge rounded-pill bg-light text-secondary border px-3 py-1.5">
            <FontAwesomeIcon icon={faVideo} className="me-1 text-danger" />
            {data.totalVideos} video • {previewLessons.length} bài học thử
          </span>
        </div>
        <div className="small text-muted">
          Kiểm tra cẩn thận trước khi bấm <strong>Gửi duyệt</strong>.
        </div>
      </div>

      {errorMessage && <Alert variant="warning">{errorMessage}</Alert>}

      {/* 3. Hero Header của khóa học */}
      <PreviewHeader key={course.id} course={course} edit={edit} isStudentView={isStudentView} />

      {/* 4. Nội dung chi tiết & Cột học phí */}
      <Row className="g-4 align-items-start">
        <Col lg={8}>
          <div className="d-flex flex-column gap-4">
            {/* Khối 1: Video giới thiệu */}
            <Card className="preview-panel preview-video-panel p-4 rounded-4 border-0">
              <div className="preview-section-heading d-flex justify-content-between align-items-center mb-3">
                <div className="d-flex align-items-center gap-3">
                  <span className="preview-section-icon">
                    <FontAwesomeIcon icon={faCirclePlay} />
                  </span>
                  <div>
                    <div className="preview-eyebrow">KHÁM PHÁ KHÓA HỌC</div>
                    <h5 className="mb-0">Video giới thiệu</h5>
                  </div>
                </div>
                {!isStudentView && (
                  <Button variant="outline-primary" size="sm" className="preview-edit-shortcut" onClick={() => edit('basic-info')}>
                    <FontAwesomeIcon icon={faPen} />
                    <span>Chỉnh sửa video giới thiệu → Thông tin</span>
                  </Button>
                )}
              </div>

              {course.introVideoUrl ? (
                <PreviewMedia target={{ courseId: course.id, kind: 'intro', source: course.introVideoUrl }} label="Video giới thiệu" />
              ) : (
                <div className="preview-empty preview-video-empty">
                  <FontAwesomeIcon icon={faCirclePlay} />
                  <span>Chưa có video giới thiệu.</span>
                </div>
              )}
            </Card>

            {/* Khối 2: Bài học xem thử miễn phí */}
            <Card className="preview-panel preview-trial-panel p-4 rounded-4 border-0">
              <div className="preview-trial-heading d-flex justify-content-between align-items-center mb-3">
                <div>
                  <div className="preview-eyebrow">HỌC THỬ TRẢI NGHIỆM</div>
                  <h5 className="mb-0">Bài học xem thử miễn phí</h5>
                </div>
                <span className="preview-count">{previewLessons.length} bài học</span>
              </div>

              {previewLessons.length ? (
                <div className="d-flex flex-column gap-2">
                  {previewLessons.map(node => (
                    <Button
                      key={node.lesson.id}
                      variant="outline-primary"
                      className="preview-trial-button text-start d-flex align-items-center"
                      onClick={() => setSelectedLesson(node.lesson.id)}
                    >
                      <span className="preview-play-icon">
                        <FontAwesomeIcon icon={faPlay} />
                      </span>
                      <span>Xem bài học: {node.lesson.title || 'Bài học chưa đặt tên'}</span>
                      <FontAwesomeIcon icon={faArrowRight} className="ms-auto" />
                    </Button>
                  ))}
                </div>
              ) : (
                <div className="text-muted">Chưa có bài học bật xem thử.</div>
              )}

              {!isStudentView && (
                <div className="mt-3">
                  <Button variant="outline-primary" size="sm" className="preview-edit-shortcut" onClick={() => edit('curriculum')}>
                    <FontAwesomeIcon icon={faPen} />
                    <span>Chỉnh sửa bài xem thử → Nội dung bài học</span>
                  </Button>
                </div>
              )}
            </Card>

            {/* Khối 3: Mô tả & Mục tiêu học tập */}
            <PreviewDetails course={course} edit={edit} isStudentView={isStudentView} />

            {/* Khối 4: Đề cương chương trình học */}
            <PreviewCurriculum chapters={data.chapters} edit={edit} select={setSelectedLesson} isStudentView={isStudentView} />
          </div>
        </Col>

        {/* Cột phải: Card học phí sticky */}
        <Col lg={4} className="preview-pricing-column">
          <PreviewPricing data={data} edit={edit} isStudentView={isStudentView} />
        </Col>
      </Row>

      {/* 5. Chân trang điều hướng */}
      <div className="preview-footer d-flex flex-wrap justify-content-between gap-3 shadow-sm">
        <Button variant="outline-secondary" className="rounded-pill px-4" onClick={onBack} disabled={saving}>
          ← Học phí
        </Button>
        <Button variant="primary" className="rounded-pill px-4" onClick={onNext} disabled={saving}>
          Tiếp: Gửi duyệt →
        </Button>
      </div>

      {/* 6. Modal xem bài học thử */}
      {selected && (
        <PreviewLesson
          key={`${course.id}-${selected.lesson.id}`}
          courseId={course.id}
          node={selected}
          allPreviewLessons={previewLessons}
          onSelectLesson={setSelectedLesson}
          close={() => setSelectedLesson(null)}
        />
      )}
    </div>
  );
};

export default CoursePreview;
