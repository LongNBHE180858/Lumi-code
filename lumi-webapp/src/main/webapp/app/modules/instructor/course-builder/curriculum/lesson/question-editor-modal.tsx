import React, { useState, useEffect } from 'react';
import { Modal, Button, Form, Row, Col, Badge } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faPlus, faTrash, faCheckCircle } from '@fortawesome/free-solid-svg-icons';
import { toast } from 'react-toastify';
import { ICreateQuestionInput, IQuestionDetailDTO } from '../../course-builder.reducer';

export interface QuestionEditorModalProps {
  show: boolean;
  title?: string;
  question?: IQuestionDetailDTO | null;
  hideDifficulty?: boolean;
  hideScore?: boolean;
  hideExplanation?: boolean;
  onClose: () => void;
  onSave: (input: ICreateQuestionInput) => Promise<void>;
}

export const QuestionEditorModal = ({
  show,
  title,
  question,
  hideDifficulty = false,
  hideScore = false,
  hideExplanation = false,
  onClose,
  onSave,
}: QuestionEditorModalProps) => {
  const [content, setContent] = useState('');
  const [difficulty, setDifficulty] = useState('MEDIUM');
  const [explanation, setExplanation] = useState('');
  const [score, setScore] = useState(1);
  const [options, setOptions] = useState<{ content: string; isCorrect: boolean }[]>([
    { content: '', isCorrect: true },
    { content: '', isCorrect: false },
    { content: '', isCorrect: false },
    { content: '', isCorrect: false },
  ]);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (question) {
      setContent(question.content || '');
      const diffStr = String(question.difficulty ?? '').toUpperCase();
      const diffVal = diffStr === '1' || diffStr === 'EASY' ? 'EASY' : diffStr === '5' || diffStr === 'HARD' ? 'HARD' : 'MEDIUM';
      setDifficulty(diffVal);
      setExplanation(question.explanation || '');
      setScore(question.score || 1);
      setOptions(
        question.options?.length
          ? question.options.map(o => ({ content: o.content, isCorrect: o.isCorrect }))
          : [
              { content: '', isCorrect: true },
              { content: '', isCorrect: false },
            ],
      );
    } else {
      setContent('');
      setDifficulty('MEDIUM');
      setExplanation('');
      setScore(1);
      setOptions([
        { content: '', isCorrect: true },
        { content: '', isCorrect: false },
        { content: '', isCorrect: false },
        { content: '', isCorrect: false },
      ]);
    }
  }, [question, show]);

  const handleOptionContentChange = (index: number, val: string) => {
    const updated = [...options];
    updated[index].content = val;
    setOptions(updated);
  };

  const handleSetCorrect = (index: number) => {
    const updated = options.map((opt, i) => ({
      ...opt,
      isCorrect: i === index,
    }));
    setOptions(updated);
  };

  const handleAddOption = () => {
    setOptions([...options, { content: '', isCorrect: false }]);
  };

  const handleRemoveOption = (index: number) => {
    if (options.length <= 2) {
      toast.error('Câu hỏi cần tối thiểu 2 phương án trả lời.');
      return;
    }
    const updated = options.filter((_, i) => i !== index);
    if (!updated.some(o => o.isCorrect) && updated.length > 0) {
      updated[0].isCorrect = true;
    }
    setOptions(updated);
  };

  const handleSubmit = async () => {
    if (!content.trim()) {
      toast.error('Vui lòng nhập nội dung câu hỏi.');
      return;
    }
    if (options.some(o => !o.content.trim())) {
      toast.error('Vui lòng điền nội dung cho tất cả các phương án trả lời.');
      return;
    }
    if (!options.some(o => o.isCorrect)) {
      toast.error('Vui lòng đánh dấu ít nhất 1 đáp án đúng.');
      return;
    }

    setSaving(true);
    try {
      await onSave({
        content: content.trim(),
        difficulty: hideDifficulty ? 'MEDIUM' : difficulty,
        explanation: hideExplanation ? '' : explanation.trim(),
        score: hideScore ? 1 : score,
        orderIndex: 0,
        options: options.map((o, idx) => ({ ...o, orderIndex: idx })),
      });
      onClose();
    } catch (err: any) {
      toast.error(err?.response?.data?.message || 'Có lỗi xảy ra khi lưu câu hỏi.');
    } finally {
      setSaving(false);
    }
  };

  const modalTitle = title || (question ? 'Chỉnh sửa câu hỏi' : 'Thêm câu hỏi mới');

  return (
    <Modal show={show} onHide={onClose} size="lg" centered backdrop="static">
      <Modal.Header closeButton={!saving}>
        <Modal.Title className="fw-bold">{modalTitle}</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Form.Group className="mb-3">
          <Form.Label className="fw-semibold small">
            Nội dung câu hỏi <span className="text-danger">*</span>
          </Form.Label>
          <Form.Control
            as="textarea"
            rows={3}
            placeholder="Nhập nội dung câu hỏi..."
            value={content}
            onChange={e => setContent(e.target.value)}
            disabled={saving}
          />
        </Form.Group>

        {(!hideDifficulty || !hideScore) && (
          <Row className="mb-3">
            {!hideDifficulty && (
              <Col md={hideScore ? 12 : 6}>
                <Form.Group>
                  <Form.Label className="fw-semibold small">Mức độ</Form.Label>
                  <Form.Select value={difficulty} onChange={e => setDifficulty(e.target.value)} disabled={saving}>
                    <option value="EASY">Dễ</option>
                    <option value="MEDIUM">Trung bình</option>
                    <option value="HARD">Khó</option>
                  </Form.Select>
                </Form.Group>
              </Col>
            )}
            {!hideScore && (
              <Col md={hideDifficulty ? 12 : 6}>
                <Form.Group>
                  <Form.Label className="fw-semibold small">Điểm số</Form.Label>
                  <Form.Control
                    type="number"
                    min={0.5}
                    step={0.5}
                    value={score}
                    onChange={e => setScore(parseFloat(e.target.value) || 1)}
                    disabled={saving}
                  />
                </Form.Group>
              </Col>
            )}
          </Row>
        )}

        <Form.Label className="fw-semibold small mb-2">Các phương án trả lời (chọn đáp án đúng)</Form.Label>
        {options.map((opt, idx) => (
          <div key={idx} className="d-flex align-items-center gap-2 mb-2">
            <Form.Check
              type="radio"
              name="correctOption"
              id={`opt-${idx}`}
              checked={opt.isCorrect}
              onChange={() => handleSetCorrect(idx)}
              disabled={saving}
              title="Đánh dấu đáp án đúng"
            />
            <Form.Control
              type="text"
              placeholder={`Lựa chọn ${idx + 1}`}
              value={opt.content}
              onChange={e => handleOptionContentChange(idx, e.target.value)}
              disabled={saving}
            />
            {opt.isCorrect && (
              <Badge bg="success" className="px-2 py-1">
                <FontAwesomeIcon icon={faCheckCircle} /> Đúng
              </Badge>
            )}
            <Button variant="outline-danger" size="sm" disabled={saving || options.length <= 2} onClick={() => handleRemoveOption(idx)}>
              <FontAwesomeIcon icon={faTrash} />
            </Button>
          </div>
        ))}

        <Button variant="outline-primary" size="sm" className="mt-1" onClick={handleAddOption} disabled={saving}>
          <FontAwesomeIcon icon={faPlus} className="me-1" /> Thêm phương án
        </Button>

        {!hideExplanation && (
          <Form.Group className="mt-3">
            <Form.Label className="fw-semibold small">Giải thích đáp án (tùy chọn)</Form.Label>
            <Form.Control
              as="textarea"
              rows={2}
              placeholder="Giải thích tại sao đáp án này đúng..."
              value={explanation}
              onChange={e => setExplanation(e.target.value)}
              disabled={saving}
            />
          </Form.Group>
        )}
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={onClose} disabled={saving}>
          Hủy
        </Button>
        <Button variant="primary" onClick={handleSubmit} disabled={saving}>
          {saving ? 'Đang lưu...' : 'Lưu câu hỏi'}
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default QuestionEditorModal;
