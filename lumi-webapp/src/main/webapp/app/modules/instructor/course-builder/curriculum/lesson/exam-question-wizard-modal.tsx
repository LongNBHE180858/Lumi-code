import React, { useState, useEffect } from 'react';
import { Modal, Button, Form, Row, Col, Badge, ProgressBar } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faPlus,
  faTrash,
  faCheckCircle,
  faArrowRight,
  faArrowLeft,
  faCheck,
  faInfoCircle,
  faChevronLeft,
  faTriangleExclamation,
} from '@fortawesome/free-solid-svg-icons';
import { toast } from 'react-toastify';
import { useAppDispatch } from 'app/config/store';
import {
  getExamDetail,
  addExamQuestion,
  updateExamQuestion,
  deleteExamQuestion,
  IQuestionDetailDTO,
  ICreateQuestionInput,
} from '../../course-builder.reducer';

interface ExamQuestionWizardModalProps {
  show: boolean;
  courseId: number;
  examId: number;
  examTitle: string;
  totalQuestions: number;
  maxScore: number;
  onClose: () => void;
  onBackToExamModal?: () => void;
  onFinish: () => void;
}

export const ExamQuestionWizardModal = ({
  show,
  courseId,
  examId,
  examTitle,
  totalQuestions,
  maxScore,
  onClose,
  onBackToExamModal,
  onFinish,
}: ExamQuestionWizardModalProps) => {
  const dispatch = useAppDispatch();
  const [questions, setQuestions] = useState<IQuestionDetailDTO[]>([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  // Form state cho câu hỏi tại currentIndex
  const [content, setContent] = useState('');
  const [difficulty, setDifficulty] = useState('MEDIUM');
  const [score, setScore] = useState<number>(1);
  const [options, setOptions] = useState<{ id?: number; content: string; isCorrect: boolean }[]>([
    { content: '', isCorrect: true },
    { content: '', isCorrect: false },
    { content: '', isCorrect: false },
    { content: '', isCorrect: false },
  ]);

  // Đổ dữ liệu câu hỏi vào form
  const populateFormForIndex = (idx: number, list: IQuestionDetailDTO[]) => {
    if (list[idx]) {
      const q = list[idx];
      setContent(q.content || '');
      const diffStr = String(q.difficulty ?? '').toUpperCase();
      const diffVal = diffStr === '1' || diffStr === 'EASY' ? 'EASY' : diffStr === '5' || diffStr === 'HARD' ? 'HARD' : 'MEDIUM';
      setDifficulty(diffVal);
      setScore(q.score != null ? q.score : 1);
      setOptions(
        q.options?.length
          ? q.options.map(o => ({ id: o.id, content: o.content, isCorrect: o.isCorrect }))
          : [
              { content: '', isCorrect: true },
              { content: '', isCorrect: false },
              { content: '', isCorrect: false },
              { content: '', isCorrect: false },
            ],
      );
    } else {
      setContent('');
      setDifficulty('MEDIUM');
      const otherScores = list.reduce((sum, item, i) => (i !== idx ? sum + (item.score || 0) : sum), 0);
      const remainingSlots = Math.max(1, totalQuestions - idx);
      const remainingScore = maxScore - otherScores;
      const suggested = Number(Math.max(0.5, remainingScore / remainingSlots).toFixed(1));
      setScore(suggested > 0 ? suggested : 1);
      setOptions([
        { content: '', isCorrect: true },
        { content: '', isCorrect: false },
        { content: '', isCorrect: false },
        { content: '', isCorrect: false },
      ]);
    }
  };

  // Tải chi tiết bài kiểm tra cùng danh sách câu hỏi đã lưu khi modal hiển thị
  useEffect(() => {
    if (!show || !courseId || !examId) return;
    let isMounted = true;
    const fetchExam = async () => {
      setLoading(true);
      try {
        const detail = await dispatch(getExamDetail({ courseId, examId })).unwrap();
        if (isMounted) {
          const loadedQuestions = detail.questions || [];
          setQuestions(loadedQuestions);
          // Mặc định ở câu đầu tiên hoặc câu tiếp theo cần soạn
          const initialIdx = Math.min(loadedQuestions.length, Math.max(0, totalQuestions - 1));
          setCurrentIndex(initialIdx);
          populateFormForIndex(initialIdx, loadedQuestions);
        }
      } catch {
        toast.error('Không thể tải danh sách câu hỏi của bài kiểm tra.');
      } finally {
        if (isMounted) setLoading(false);
      }
    };
    void fetchExam();
    return () => {
      isMounted = false;
    };
  }, [show, courseId, examId]);

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

  // Tính tổng điểm các câu hỏi khác (ngoại trừ câu đang soạn)
  const otherQuestionsScore = Number(
    questions.reduce((sum, item, i) => (i !== currentIndex ? sum + (item.score || 0) : sum), 0).toFixed(2),
  );
  const currentTotalWithScore = Number((otherQuestionsScore + (score || 0)).toFixed(2));
  const isLast = currentIndex === totalQuestions - 1;
  const remainingForLast = Number((maxScore - otherQuestionsScore).toFixed(2));
  const isMaxScoreReached = Math.abs(currentTotalWithScore - maxScore) < 0.001;
  const hasExcessQuestions = questions.length > totalQuestions;
  const isCurrentQuestionSaved = Boolean(questions[currentIndex]?.id);

  // Chuyển về câu trước
  const handlePreviousQuestion = () => {
    if (currentIndex > 0) {
      const prevIdx = currentIndex - 1;
      setCurrentIndex(prevIdx);
      populateFormForIndex(prevIdx, questions);
    }
  };

  // Xóa câu hỏi hiện tại
  const handleDeleteCurrentQuestion = async () => {
    const targetQ = questions[currentIndex];
    if (!targetQ?.id) {
      // Nếu là câu chưa lưu thì chỉ cần reset form
      populateFormForIndex(currentIndex, questions);
      return;
    }

    setSaving(true);
    try {
      const updatedExam = await dispatch(
        deleteExamQuestion({
          courseId,
          examId,
          questionId: targetQ.id,
        }),
      ).unwrap();

      toast.success('Đã xóa câu hỏi.');
      const updatedList = updatedExam.questions || questions.filter(q => q.id !== targetQ.id);
      setQuestions(updatedList);

      const nextIdx = Math.max(0, Math.min(currentIndex, updatedList.length > 0 ? updatedList.length - 1 : 0));
      setCurrentIndex(nextIdx);
      populateFormForIndex(nextIdx, updatedList);
    } catch {
      toast.error('Không thể xóa câu hỏi.');
    } finally {
      setSaving(false);
    }
  };

  // Lưu câu hiện tại và chuyển sang câu kế tiếp (hoặc hoàn tất)
  const handleNextOrFinish = async () => {
    if (!content.trim()) {
      toast.error('Vui lòng nhập nội dung câu hỏi.');
      return;
    }
    if (options.some(o => !o.content.trim())) {
      toast.error('Vui lòng điền nội dung cho tất cả các phương án.');
      return;
    }
    if (!options.some(o => o.isCorrect)) {
      toast.error('Vui lòng đánh dấu ít nhất 1 đáp án đúng.');
      return;
    }
    if (score <= 0) {
      toast.error('Điểm của câu hỏi phải lớn hơn 0.');
      return;
    }

    if (currentTotalWithScore > maxScore) {
      toast.error(`Tổng điểm (${currentTotalWithScore}) vượt quá số điểm tối đa quy định (${maxScore})!`);
      return;
    }

    if (isLast && !isMaxScoreReached) {
      toast.error(
        `Tổng điểm cần đạt đúng ${maxScore} điểm mới có thể hoàn tất bài kiểm tra (Hiện tại: ${currentTotalWithScore}/${maxScore}).`,
      );
      return;
    }

    if (hasExcessQuestions) {
      toast.error(
        `Bài kiểm tra hiện có ${questions.length} câu hỏi, nhiều hơn số câu quy định (${totalQuestions}). Vui lòng xóa bớt câu thừa trước khi hoàn tất.`,
      );
      return;
    }

    setSaving(true);
    try {
      const questionPayload: ICreateQuestionInput = {
        content: content.trim(),
        difficulty,
        score,
        explanation: '', // Không cần phần giải thích cho bài kiểm tra
        orderIndex: currentIndex,
        options: options.map((o, idx) => ({ ...o, orderIndex: idx })),
      };

      let updatedExam: any;
      if (questions[currentIndex]?.id) {
        // Cập nhật câu hỏi đã có
        updatedExam = await dispatch(
          updateExamQuestion({
            courseId,
            examId,
            questionId: questions[currentIndex].id,
            input: questionPayload,
          }),
        ).unwrap();
      } else {
        // Thêm câu hỏi mới
        updatedExam = await dispatch(
          addExamQuestion({
            courseId,
            examId,
            input: questionPayload,
          }),
        ).unwrap();
      }

      const updatedList: IQuestionDetailDTO[] = updatedExam.questions || [];
      setQuestions(updatedList);

      if (!isLast) {
        toast.success(`Đã lưu câu ${currentIndex + 1}.`);
        const nextIdx = currentIndex + 1;
        setCurrentIndex(nextIdx);
        populateFormForIndex(nextIdx, updatedList);
      } else {
        toast.success(`Đã hoàn tất soạn ${totalQuestions} câu hỏi đạt chuẩn ${maxScore} điểm cho bài kiểm tra!`);
        onFinish();
      }
    } catch {
      toast.error('Không thể lưu câu hỏi. Vui lòng thử lại.');
    } finally {
      setSaving(false);
    }
  };

  const displayTotalQuestions = Math.max(totalQuestions, questions.length);
  const progressPercent = Math.round(((currentIndex + 1) / totalQuestions) * 100);

  return (
    <Modal show={show} onHide={onClose} size="lg" centered backdrop="static">
      <Modal.Header closeButton={!saving}>
        <div className="w-100 me-2">
          <div className="d-flex justify-content-between align-items-center mb-1">
            <Modal.Title className="fw-bold h5 mb-0">Soạn câu hỏi bài kiểm tra: {examTitle}</Modal.Title>
            {onBackToExamModal && (
              <Button
                variant="outline-secondary"
                size="sm"
                className="py-1 px-2"
                onClick={onBackToExamModal}
                disabled={saving}
                title="Quay lại chỉnh sửa thông tin đề (thời gian, điểm, số câu)"
              >
                <FontAwesomeIcon icon={faChevronLeft} className="me-1" />
                Sửa thông tin đề
              </Button>
            )}
          </div>
          <div className="d-flex flex-wrap align-items-center gap-2 mt-2">
            <Badge bg="primary">
              Câu {currentIndex + 1} / {displayTotalQuestions}
            </Badge>
            <Badge bg={isMaxScoreReached ? 'success' : 'warning'} className="text-dark">
              Tổng điểm: {currentTotalWithScore} / {maxScore} điểm
            </Badge>
            {hasExcessQuestions && (
              <Badge bg="danger">
                <FontAwesomeIcon icon={faTriangleExclamation} className="me-1" />
                Dư {questions.length - totalQuestions} câu (Quy định: {totalQuestions})
              </Badge>
            )}
          </div>
        </div>
      </Modal.Header>

      <div className="px-3 pt-2">
        <ProgressBar now={Math.min(100, progressPercent)} variant={isMaxScoreReached ? 'success' : 'primary'} style={{ height: 6 }} />
      </div>

      <Modal.Body>
        {hasExcessQuestions && (
          <div className="alert alert-danger py-2 small mb-3 d-flex align-items-center justify-content-between">
            <div>
              <FontAwesomeIcon icon={faTriangleExclamation} className="me-2" />
              Đề thi hiện đang có <strong>{questions.length} câu hỏi</strong>, nhiều hơn số lượng quy định (
              <strong>{totalQuestions} câu</strong>). Vui lòng chuyển đến các câu thừa và bấm <strong>Xóa câu này</strong>.
            </div>
          </div>
        )}

        <Form.Group className="mb-3">
          <div className="d-flex justify-content-between align-items-center mb-1">
            <Form.Label className="fw-semibold small mb-0">
              Nội dung câu {currentIndex + 1} <span className="text-danger">*</span>
            </Form.Label>
            {isCurrentQuestionSaved && (
              <Badge bg="light" text="dark" className="border">
                Đã lưu trong đề
              </Badge>
            )}
          </div>
          <Form.Control
            as="textarea"
            rows={3}
            placeholder={`Nhập nội dung cho câu hỏi ${currentIndex + 1}...`}
            value={content}
            onChange={e => setContent(e.target.value)}
            disabled={saving || loading}
          />
        </Form.Group>

        <Row className="mb-3">
          <Col md={6}>
            <Form.Group>
              <Form.Label className="fw-semibold small">Mức độ khó</Form.Label>
              <Form.Select value={difficulty} onChange={e => setDifficulty(e.target.value)} disabled={saving || loading}>
                <option value="EASY">Dễ</option>
                <option value="MEDIUM">Trung bình</option>
                <option value="HARD">Khó</option>
              </Form.Select>
            </Form.Group>
          </Col>
          <Col md={6}>
            <Form.Group>
              <div className="d-flex justify-content-between align-items-center">
                <Form.Label className="fw-semibold small mb-1">
                  Điểm số <span className="text-danger">*</span>
                </Form.Label>
                {isLast && remainingForLast > 0 && score !== remainingForLast && (
                  <Button
                    size="sm"
                    variant="link"
                    className="p-0 text-primary small text-decoration-none"
                    onClick={() => setScore(remainingForLast)}
                  >
                    Gợi ý: chọn {remainingForLast}đ để đủ {maxScore}đ
                  </Button>
                )}
              </div>
              <Form.Control
                type="number"
                min={0.5}
                step={0.5}
                value={score}
                onChange={e => setScore(parseFloat(e.target.value) || 0)}
                disabled={saving || loading}
              />
            </Form.Group>
          </Col>
        </Row>

        <Form.Label className="fw-semibold small mb-2">Các phương án trả lời (chọn đáp án đúng)</Form.Label>
        {options.map((opt, idx) => (
          <div key={idx} className="d-flex align-items-center gap-2 mb-2">
            <Form.Check
              type="radio"
              name={`wizard-correct-${currentIndex}`}
              id={`wizard-opt-${idx}`}
              checked={opt.isCorrect}
              onChange={() => handleSetCorrect(idx)}
              disabled={saving || loading}
              title="Đánh dấu đáp án đúng"
            />
            <Form.Control
              type="text"
              placeholder={`Lựa chọn ${idx + 1}`}
              value={opt.content}
              onChange={e => handleOptionContentChange(idx, e.target.value)}
              disabled={saving || loading}
            />
            {opt.isCorrect && (
              <Badge bg="success" className="px-2 py-1">
                <FontAwesomeIcon icon={faCheckCircle} /> Đúng
              </Badge>
            )}
            <Button
              variant="outline-danger"
              size="sm"
              disabled={saving || loading || options.length <= 2}
              onClick={() => handleRemoveOption(idx)}
            >
              <FontAwesomeIcon icon={faTrash} />
            </Button>
          </div>
        ))}

        <Button variant="outline-primary" size="sm" className="mt-1" onClick={handleAddOption} disabled={saving || loading}>
          <FontAwesomeIcon icon={faPlus} className="me-1" /> Thêm phương án
        </Button>

        {isLast && !isMaxScoreReached && (
          <div className="alert alert-warning py-2 small mt-3 mb-0 d-flex align-items-center gap-2">
            <FontAwesomeIcon icon={faInfoCircle} />
            <span>
              Cần đạt đúng <strong>{maxScore} điểm</strong> mới có thể hoàn tất tạo bài kiểm tra (Hiện tại: {currentTotalWithScore} /{' '}
              {maxScore} điểm). Hãy điều chỉnh điểm câu này thành <strong>{remainingForLast}</strong>.
            </span>
          </div>
        )}
      </Modal.Body>

      <Modal.Footer className="d-flex justify-content-between align-items-center">
        <div className="d-flex align-items-center gap-2">
          {onBackToExamModal && (
            <Button variant="outline-secondary" onClick={onBackToExamModal} disabled={saving}>
              <FontAwesomeIcon icon={faChevronLeft} className="me-1" />
              Sửa thông tin đề
            </Button>
          )}
          {isCurrentQuestionSaved && (
            <Button
              variant="outline-danger"
              onClick={handleDeleteCurrentQuestion}
              disabled={saving || loading}
              title="Xóa câu hỏi này khỏi đề"
            >
              <FontAwesomeIcon icon={faTrash} className="me-1" />
              Xóa câu này
            </Button>
          )}
        </div>

        <div className="d-flex align-items-center gap-2">
          {currentIndex > 0 && (
            <Button variant="secondary" onClick={handlePreviousQuestion} disabled={saving || loading}>
              <FontAwesomeIcon icon={faArrowLeft} className="me-1" />
              Câu trước
            </Button>
          )}
          <Button
            variant={isLast ? 'success' : 'primary'}
            onClick={handleNextOrFinish}
            disabled={saving || loading || (isLast && !isMaxScoreReached) || hasExcessQuestions}
          >
            {saving ? (
              'Đang lưu...'
            ) : isLast ? (
              <>
                <FontAwesomeIcon icon={faCheck} className="me-1" /> Hoàn tất bài kiểm tra
              </>
            ) : (
              <>
                Tiếp tục (Câu {currentIndex + 2}) <FontAwesomeIcon icon={faArrowRight} className="ms-1" />
              </>
            )}
          </Button>
        </div>
      </Modal.Footer>
    </Modal>
  );
};

export default ExamQuestionWizardModal;
