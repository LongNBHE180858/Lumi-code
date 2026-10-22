import React, { useState } from 'react';
import { Accordion, Badge, Button, Card, Col, Modal, Row } from 'react-bootstrap';
import type { IBuilderAggregate, IChapterNode, ICourseData, ILessonNode } from '../course-builder.reducer';
import { chapterDuration, formatDuration, lessonSeconds } from '../curriculum/video-duration';
import { savedPricing } from '../pricing/saved-pricing';
import { PreviewMedia } from './preview-media';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faBook,
  faBookOpen,
  faBookmark,
  faCheck,
  faClock,
  faFileLines,
  faFilePdf,
  faGraduationCap,
  faImage,
  faLock,
  faPen,
  faShieldHalved,
  faChevronLeft,
  faChevronRight,
  faUserGraduate,
  faDesktopAlt,
} from '@fortawesome/free-solid-svg-icons';

export type PreviewEditTab = 'basic-info' | 'curriculum' | 'pricing' | 'review';
export type EditPreview = (tab: PreviewEditTab) => void;

export const PreviewHeader = ({
  course,
  edit,
  isStudentView = false,
}: {
  course: ICourseData;
  edit: EditPreview;
  isStudentView?: boolean;
}) => {
  const author =
    course.authorDisplayName ||
    [course.author?.firstName, course.author?.lastName].filter(Boolean).join(' ') ||
    course.author?.login ||
    'Chưa có thông tin giảng viên';

  return (
    <Card className="preview-hero rounded-4 border-0">
      <div className="d-flex justify-content-between flex-wrap gap-2 mb-3">
        <div className="d-flex flex-wrap gap-2 align-items-center">
          <Badge bg="info" className="text-primary bg-info-subtle rounded-pill px-3 py-2 d-inline-flex align-items-center gap-1.5">
            <FontAwesomeIcon icon={faBook} className="opacity-75" />
            <span>{course.subject?.name || 'Chưa chọn môn'}</span>
          </Badge>
          <Badge bg="info" className="text-primary bg-info-subtle rounded-pill px-3 py-2 d-inline-flex align-items-center gap-1.5">
            <FontAwesomeIcon icon={faGraduationCap} className="opacity-75" />
            <span>{course.grade?.name || 'Chưa chọn lớp'}</span>
          </Badge>
          <Badge bg="info" className="text-primary bg-info-subtle rounded-pill px-3 py-2 d-inline-flex align-items-center gap-1.5">
            <FontAwesomeIcon icon={faBookmark} className="opacity-75" />
            <span>{course.bookSeries || 'Chưa chọn bộ sách'}</span>
          </Badge>
        </div>
        {!isStudentView && (
          <Button variant="outline-primary" size="sm" className="preview-edit-shortcut" onClick={() => edit('basic-info')}>
            <FontAwesomeIcon icon={faPen} />
            <span>Chỉnh sửa → Thông tin khóa học</span>
          </Button>
        )}
      </div>
      <h3 className="preview-title">{course.title || 'Khóa học chưa đặt tên'}</h3>
      <p className="preview-description" style={{ whiteSpace: 'pre-wrap' }}>
        {course.shortDescription || 'Chưa có mô tả ngắn.'}
      </p>
      <div className="preview-author">
        <span className="preview-avatar" aria-hidden="true">
          {author.charAt(0).toUpperCase()}
        </span>
        <span className="preview-author-name">Giảng viên: {author}</span>
      </div>
      <div className="preview-cover">
        {course.thumbnailUrl ? (
          <PreviewMedia
            target={{ courseId: course.id, kind: 'thumbnail', source: course.thumbnailUrl }}
            label={`Ảnh bìa ${course.title || 'khóa học'}`}
          />
        ) : (
          <div className="preview-empty preview-cover-empty">
            <FontAwesomeIcon icon={faImage} />
            <span>Chưa có ảnh bìa.</span>
          </div>
        )}
      </div>
    </Card>
  );
};

export const PreviewDetails = ({
  course,
  edit,
  isStudentView = false,
}: {
  course: ICourseData;
  edit: EditPreview;
  isStudentView?: boolean;
}) => (
  <>
    <Card className="preview-panel p-4 rounded-4 border-0">
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h5>
          <FontAwesomeIcon icon={faBookOpen} className="me-2 text-primary" />
          Mô tả khóa học
        </h5>
        {!isStudentView && (
          <Button variant="outline-primary" size="sm" className="preview-edit-shortcut" onClick={() => edit('basic-info')}>
            <FontAwesomeIcon icon={faPen} />
            <span>Chỉnh sửa mô tả</span>
          </Button>
        )}
      </div>
      <div className="preview-long-description text-secondary" style={{ whiteSpace: 'pre-wrap' }}>
        {course.description || 'Chưa có mô tả đầy đủ.'}
      </div>
    </Card>
    <Row className="g-4">
      <Col lg={7}>
        <Card className="preview-panel preview-outcomes p-4 rounded-4 border-0 h-100">
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h5>
              <FontAwesomeIcon icon={faGraduationCap} className="me-2 text-primary" />
              Bạn sẽ học được gì?
            </h5>
            {!isStudentView && (
              <Button variant="outline-primary" size="sm" className="preview-edit-shortcut" onClick={() => edit('basic-info')}>
                <FontAwesomeIcon icon={faPen} />
                <span>Chỉnh sửa mục tiêu</span>
              </Button>
            )}
          </div>
          {course.learningObjectives?.length ? (
            <div className="preview-objectives-grid">
              {course.learningObjectives.map((item, index) => (
                <div key={index} className="preview-objective-item d-flex align-items-start gap-2">
                  <FontAwesomeIcon icon={faCheck} className="text-success mt-1 flex-shrink-0" aria-hidden="true" />
                  <span className="text-dark">{item}</span>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-muted">Chưa có mục tiêu học tập.</div>
          )}
        </Card>
      </Col>
      <Col lg={5}>
        <Card className="preview-panel p-4 rounded-4 border-0 h-100 d-flex flex-column gap-3">
          <div>
            <h5 className="mb-2">
              <FontAwesomeIcon icon={faUserGraduate} className="me-2 text-primary" />
              Khóa học dành cho ai?
            </h5>
            <p className="text-secondary mb-0" style={{ whiteSpace: 'pre-wrap' }}>
              {course.targetAudience || 'Chưa có thông tin đối tượng học.'}
            </p>
          </div>
          <hr className="my-2 border-secondary-subtle" />
          <div>
            <h6 className="mt-0 mb-2">Điều kiện học</h6>
            <div className="text-secondary" style={{ whiteSpace: 'pre-wrap' }}>
              {course.prerequisites || 'Chưa có thông tin điều kiện học.'}
            </div>
          </div>
        </Card>
      </Col>
    </Row>
  </>
);

export const PreviewPricing = ({
  data,
  edit,
  isStudentView = false,
}: {
  data: IBuilderAggregate;
  edit: EditPreview;
  isStudentView?: boolean;
}) => {
  const pricing = savedPricing(data.course);
  const lessons = data.chapters.flatMap(chapter => chapter.lessons.map(node => node.lesson));
  return (
    <Card className="preview-pricing preview-panel p-4 rounded-4 border-0 shadow-sm">
      <div className="d-flex justify-content-between align-items-center mb-2">
        <h5 className="preview-eyebrow mb-0">Học phí trọn khóa</h5>
        {!isStudentView && (
          <Button variant="outline-primary" size="sm" className="preview-edit-shortcut" onClick={() => edit('pricing')}>
            <FontAwesomeIcon icon={faPen} />
            <span>Chỉnh sửa → Học phí</span>
          </Button>
        )}
      </div>
      <div className="preview-price">{pricing.priceText}</div>
      <div className="text-muted small mb-3">
        {[pricing.paymentText, pricing.durationValid ? `Truy cập ${pricing.durationText}` : pricing.durationText]
          .filter(Boolean)
          .join(' • ')}
      </div>
      <hr />
      <div className="preview-benefits-list d-flex flex-column gap-2 mb-3">
        <div className="preview-benefit">
          <FontAwesomeIcon icon={faBookOpen} />
          <span>
            {data.totalVideos} video • {chapterDuration(lessons)}
          </span>
        </div>
        <div className="preview-benefit">
          <FontAwesomeIcon icon={faFileLines} />
          <span>{data.totalMaterials} tài liệu đính kèm</span>
        </div>
        {pricing.durationValid && (
          <div className="preview-benefit">
            <FontAwesomeIcon icon={faClock} />
            <span>{pricing.durationText} kể từ khi đăng ký thành công</span>
          </div>
        )}
        {/* <div className="preview-benefit text-muted">
          <FontAwesomeIcon icon={faCertificate} />
          <span>Cấp giấy chứng nhận sau khi hoàn thành</span>
        </div> */}
        <div className="preview-benefit text-muted">
          <FontAwesomeIcon icon={faDesktopAlt} />
          <span>Học tập tốt nhất trên google chrome</span>
        </div>
      </div>
      <Button disabled variant="secondary" className="w-100 rounded-pill py-2.5 fw-bold preview-enroll-button">
        Đăng ký học
      </Button>
      <div className="preview-enrollment-note mt-3">
        <FontAwesomeIcon icon={faShieldHalved} className="text-primary opacity-75" />
        <span>Chưa mở đăng ký. Đây là bản xem trước của khóa học chưa xuất bản.</span>
      </div>
    </Card>
  );
};

const LessonRow = ({ node, select }: { node: ILessonNode; select: (id: number) => void }) => {
  const seconds = lessonSeconds(node.lesson);
  return (
    <div className="preview-lesson-row d-flex flex-wrap justify-content-between align-items-center gap-2 py-3 border-top">
      <div>
        <div className="fw-semibold text-dark">{node.lesson.title || 'Bài học chưa đặt tên'}</div>
        <div className="small text-muted">
          {node.lesson.videoUrl ? (seconds == null ? 'Video chưa xác định thời lượng' : formatDuration(seconds)) : 'Chưa có video'} •{' '}
          {node.materials.length} tài liệu
        </div>
      </div>
      {node.lesson.isPreview ? (
        <Button size="sm" variant="outline-primary" className="rounded-pill px-3" onClick={() => select(node.lesson.id)}>
          Xem thử: {node.lesson.title || 'Bài học'}
        </Button>
      ) : (
        <Badge bg="light" className="preview-locked rounded-pill">
          <FontAwesomeIcon icon={faLock} className="me-1" />
          Chưa bật xem thử
        </Badge>
      )}
    </div>
  );
};

export const PreviewCurriculum = ({
  chapters,
  edit,
  select,
  isStudentView = false,
}: {
  chapters: IChapterNode[];
  edit: EditPreview;
  select: (id: number) => void;
  isStudentView?: boolean;
}) => {
  const lessons = chapters.flatMap(chapter => chapter.lessons.map(node => node.lesson));
  return (
    <Card className="preview-panel preview-curriculum p-4 rounded-4 border-0">
      <div className="d-flex flex-wrap justify-content-between align-items-center mb-2">
        <h5 className="mb-0">Đề cương khóa học</h5>
        {!isStudentView && (
          <Button variant="outline-primary" size="sm" className="preview-edit-shortcut" onClick={() => edit('curriculum')}>
            <FontAwesomeIcon icon={faPen} />
            <span>Chỉnh sửa → Nội dung bài học</span>
          </Button>
        )}
      </div>
      <div className="small text-muted mb-3">
        {chapters.length} chương • {lessons.length} bài học • {chapterDuration(lessons)}
      </div>
      {!chapters.length && <div className="text-muted">Chưa có chương hoặc bài học.</div>}
      <Accordion alwaysOpen>
        {chapters.map(({ chapter, lessons: nodes }) => (
          <Accordion.Item key={chapter.id} eventKey={String(chapter.id)}>
            <Accordion.Header>
              {chapter.title || 'Chương chưa đặt tên'} • {nodes.length} bài học
            </Accordion.Header>
            <Accordion.Body>
              <div className="small text-muted mb-2">{chapterDuration(nodes.map(node => node.lesson))}</div>
              {nodes.length ? (
                nodes.map(node => <LessonRow key={node.lesson.id} node={node} select={select} />)
              ) : (
                <div className="text-muted small">Chương chưa có bài học.</div>
              )}
            </Accordion.Body>
          </Accordion.Item>
        ))}
      </Accordion>
    </Card>
  );
};

const PreviewMaterial = ({
  courseId,
  lessonId,
  material,
}: {
  courseId: number;
  lessonId: number;
  material: ILessonNode['materials'][number];
}) => {
  const [open, setOpen] = useState(false);
  const name = material.displayName || material.fileName || 'Tài liệu';
  const isPdf = name.toLowerCase().endsWith('.pdf');
  if (!material.fileUrl || material.id == null) return <div className="text-muted small py-1">{name} — Chưa có tệp.</div>;
  return (
    <div className="py-1">
      {open ? (
        <PreviewMedia target={{ courseId, lessonId, materialId: material.id, kind: 'material', source: material.fileUrl }} label={name} />
      ) : (
        <Button size="sm" variant="outline-primary" className="rounded-pill px-3" onClick={() => setOpen(true)}>
          <FontAwesomeIcon icon={isPdf ? faFilePdf : faFileLines} className="me-1.5" />
          Xem tài liệu: {name}
        </Button>
      )}
    </div>
  );
};

export const PreviewLesson = ({
  courseId,
  node,
  allPreviewLessons = [],
  onSelectLesson,
  close,
}: {
  courseId: number;
  node: ILessonNode;
  allPreviewLessons?: ILessonNode[];
  onSelectLesson?: (id: number) => void;
  close: () => void;
}) => {
  const currentIndex = allPreviewLessons.findIndex(item => item.lesson.id === node.lesson.id);
  const hasPrev = currentIndex > 0;
  const hasNext = currentIndex >= 0 && currentIndex < allPreviewLessons.length - 1;

  return (
    <Modal show onHide={close} size="lg" centered contentClassName="preview-lesson-modal shadow-lg">
      <Modal.Header closeButton className="d-flex align-items-center">
        <div>
          <span className="badge bg-primary-subtle text-primary rounded-pill mb-1">Bài học xem thử miễn phí</span>
          <Modal.Title className="fw-bold">{node.lesson.title || 'Bài học xem thử'}</Modal.Title>
        </div>
      </Modal.Header>
      <Modal.Body className="p-4">
        <p className="text-secondary mb-3" style={{ whiteSpace: 'pre-wrap' }}>
          {node.lesson.description || 'Chưa có mô tả bài học.'}
        </p>
        {node.lesson.videoUrl ? (
          <div className="mb-3">
            <PreviewMedia
              target={{ courseId, lessonId: node.lesson.id, kind: 'video', source: node.lesson.videoUrl }}
              label={`Video ${node.lesson.title || 'bài học'}`}
            />
          </div>
        ) : (
          <div className="alert alert-light border text-muted">Bài học chưa có video.</div>
        )}
        <h6 className="fw-bold mt-4 mb-2">Tài liệu bài học</h6>
        {node.materials.length ? (
          <div className="d-flex flex-wrap gap-2">
            {node.materials.map(material => (
              <PreviewMaterial key={material.id} courseId={courseId} lessonId={node.lesson.id} material={material} />
            ))}
          </div>
        ) : (
          <div className="text-muted small">Chưa có tài liệu đính kèm.</div>
        )}
      </Modal.Body>
      <Modal.Footer className="d-flex justify-content-between">
        <div className="d-flex gap-2">
          {hasPrev && onSelectLesson && (
            <Button
              size="sm"
              variant="outline-secondary"
              className="rounded-pill"
              onClick={() => onSelectLesson(allPreviewLessons[currentIndex - 1].lesson.id)}
            >
              <FontAwesomeIcon icon={faChevronLeft} className="me-1" />
              Bài trước
            </Button>
          )}
          {hasNext && onSelectLesson && (
            <Button
              size="sm"
              variant="outline-primary"
              className="rounded-pill"
              onClick={() => onSelectLesson(allPreviewLessons[currentIndex + 1].lesson.id)}
            >
              Bài tiếp theo
              <FontAwesomeIcon icon={faChevronRight} className="ms-1" />
            </Button>
          )}
        </div>
        <Button variant="secondary" className="rounded-pill px-4" onClick={close}>
          Đóng bài xem thử
        </Button>
      </Modal.Footer>
    </Modal>
  );
};
