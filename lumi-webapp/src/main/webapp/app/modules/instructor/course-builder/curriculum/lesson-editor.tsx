import LessonMedia from './lesson-media';
import React from 'react';
import { Card, Button, Form, Badge } from 'react-bootstrap';

export interface ILessonData {
  id: number;
  title: string;
  duration: string;
  description: string;
  videoFileName: string;
  videoSize: string;
  videoStatus: string;
  isPreview: boolean;
  attachments: {
    id: number;
    name: string;
    size: string;
    type: string;
    status: string;
  }[];
}

interface LessonEditorProps {
  lesson: ILessonData;
  chapterTitle: string;
  lessonIndex: number;
  totalLessonsInChapter: number;
  saving: boolean;
  disabled?: boolean;
  hasChanges: boolean;
  titleError: string;
  onUpdateLesson: (lesson: ILessonData) => void;
  onSave: () => void;
}

export const LessonEditor = ({
  lesson,
  chapterTitle,
  lessonIndex,
  totalLessonsInChapter,
  saving,
  disabled = false,
  hasChanges,
  titleError,
  onUpdateLesson,
  onSave,
}: LessonEditorProps) => {
  const { title, description, isPreview } = lesson;

  return (
    <div className="d-flex flex-column gap-4">
      {/* 1. Thông tin bài học cơ bản */}
      <Card className="p-4 rounded-4 border-0 shadow-sm">
        <div className="d-flex justify-content-between align-items-center mb-2">
          <h5 className="fw-bold mb-0 text-dark">Bài học: {title}</h5>
          <Button variant="primary" disabled={saving || disabled || !hasChanges || Boolean(titleError)} onClick={onSave}>
            {saving ? 'Đang lưu...' : 'Lưu bài học'}
          </Button>
        </div>

        <div className="d-flex justify-content-between align-items-center mb-3">
          <span className="text-muted small">
            {chapterTitle} • Bài {lessonIndex} / {totalLessonsInChapter}
          </span>
          <Badge bg="info" className="text-primary bg-info-subtle px-2 py-1 rounded-pill">
            {lesson.videoStatus}
          </Badge>
        </div>

        <Form.Group className="mb-3">
          <Form.Label className="fw-semibold small">
            Tên bài học <span className="text-danger">*</span>
          </Form.Label>
          <Form.Control
            type="text"
            value={title}
            maxLength={255}
            disabled={saving || disabled}
            isInvalid={Boolean(titleError)}
            onChange={event => onUpdateLesson({ ...lesson, title: event.target.value })}
            className="rounded-3 py-2"
          />
          <Form.Control.Feedback type="invalid">{titleError}</Form.Control.Feedback>
        </Form.Group>

        <Form.Group>
          <Form.Label className="fw-semibold small">Mô tả bài học</Form.Label>
          <Form.Control
            as="textarea"
            rows={3}
            value={description}
            disabled={saving || disabled}
            onChange={event => onUpdateLesson({ ...lesson, description: event.target.value })}
            className="rounded-3"
          />
        </Form.Group>
      </Card>

      <LessonMedia lessonId={lesson.id} disabled={saving || disabled} />
      <Card className="p-4 rounded-4 border-0 shadow-sm">
        <h5 className="fw-bold mb-3">Xem thử miễn phí</h5>
        {/* Checkbox Xem thử miễn phí */}
        <div className="d-flex align-items-center gap-2 mt-2">
          <Form.Check
            type="checkbox"
            id="is-preview-checkbox"
            checked={isPreview}
            disabled={saving || disabled}
            onChange={event => onUpdateLesson({ ...lesson, isPreview: event.target.checked })}
            label={<span className="fw-semibold text-dark small">Cho học viên xem thử miễn phí bài này</span>}
          />
        </div>
        <span className="text-muted small ms-4">Bài “{title}” sẽ xuất hiện trong phần xem thử trên trang khóa học.</span>
      </Card>
    </div>
  );
};

export default LessonEditor;
