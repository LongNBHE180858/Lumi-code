import React from 'react';
import { Modal, Button, Badge } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCheckCircle, faCircleQuestion } from '@fortawesome/free-solid-svg-icons';
import { IQuestionDetailDTO } from '../../course-builder.reducer';

interface QuestionDetailModalProps {
  show: boolean;
  question: IQuestionDetailDTO | null;
  onClose: () => void;
}

export const QuestionDetailModal = ({ show, question, onClose }: QuestionDetailModalProps) => {
  if (!question) return null;

  const difficultyText = (diff: any) => {
    const s = String(diff ?? '').toUpperCase();
    if (s === '1' || s === 'EASY') return { label: 'Dễ', bg: 'success' };
    if (s === '5' || s === 'HARD') return { label: 'Khó', bg: 'danger' };
    return { label: 'Trung bình', bg: 'warning' };
  };

  const diffInfo = difficultyText(question.difficulty);

  return (
    <Modal show={show} onHide={onClose} size="lg" centered>
      <Modal.Header closeButton>
        <Modal.Title className="fw-bold h5 mb-0">
          <FontAwesomeIcon icon={faCircleQuestion} className="text-primary me-2" />
          Chi tiết câu hỏi
        </Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <div className="mb-3 d-flex flex-wrap gap-2 align-items-center">
          <Badge bg={diffInfo.bg} className="px-2 py-1">
            Độ khó: {diffInfo.label}
          </Badge>
          {question.score != null && (
            <Badge bg="secondary" className="px-2 py-1">
              Điểm: {question.score}
            </Badge>
          )}
        </div>

        <div className="p-3 bg-light rounded-3 mb-3 border">
          <div className="text-muted small fw-semibold mb-1">Nội dung câu hỏi:</div>
          <div className="fw-semibold text-dark fs-6" style={{ whiteSpace: 'pre-line' }}>
            {question.content}
          </div>
        </div>

        <div className="mb-3">
          <div className="fw-semibold small text-muted mb-2">Các phương án trả lời:</div>
          <div className="d-flex flex-column gap-2">
            {question.options?.map((opt, idx) => (
              <div
                key={opt.id || idx}
                className={`p-2 px-3 rounded-3 border d-flex justify-content-between align-items-center ${
                  opt.isCorrect ? 'bg-success-subtle border-success text-success-emphasis fw-semibold' : 'bg-white'
                }`}
              >
                <div className="d-flex align-items-center gap-2">
                  <span className="badge rounded-pill bg-light text-dark border">{String.fromCharCode(65 + idx)}</span>
                  <span>{opt.content}</span>
                </div>
                {opt.isCorrect && (
                  <Badge bg="success" className="px-2 py-1 d-flex align-items-center gap-1">
                    <FontAwesomeIcon icon={faCheckCircle} /> Đáp án đúng
                  </Badge>
                )}
              </div>
            ))}
          </div>
        </div>

        {question.explanation && (
          <div className="p-3 bg-info-subtle text-info-emphasis rounded-3 border border-info-subtle">
            <div className="fw-bold small mb-1">Giải thích đáp án:</div>
            <div className="small" style={{ whiteSpace: 'pre-line' }}>
              {question.explanation}
            </div>
          </div>
        )}
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={onClose}>
          Đóng
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default QuestionDetailModal;
