import React, { useState, useEffect } from 'react';
import { Card, Button, Form, Badge, Modal, Row, Col, Spinner } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faListCheck, faTrash, faEye, faPen, faFloppyDisk } from '@fortawesome/free-solid-svg-icons';
import { toast } from 'react-toastify';
import { useAppDispatch } from 'app/config/store';
import { getExamDetail, updateExam, deleteExam, IExamDetailDTO, IQuestionDetailDTO } from '../course-builder.reducer';
import QuestionDetailModal from './lesson/question-detail-modal';
import ExamQuestionWizardModal from './lesson/exam-question-wizard-modal';

interface ChapterExamEditorProps {
  courseId: number;
  chapterId: number;
  chapterTitle: string;
  examId: number;
  disabled?: boolean;
  onDeleteExam: () => void;
  onExamUpdated?: () => void;
}

export const ChapterExamEditor = ({
  courseId,
  chapterId,
  chapterTitle,
  examId,
  disabled = false,
  onDeleteExam,
  onExamUpdated,
}: ChapterExamEditorProps) => {
  const dispatch = useAppDispatch();
  const [exam, setExam] = useState<IExamDetailDTO | null>(null);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  // Form thông tin cơ bản
  const [title, setTitle] = useState('');
  const [duration, setDuration] = useState(15);
  const [passScore, setPassScore] = useState(60);
  const [questionCount, setQuestionCount] = useState(10);
  const [maxScore, setMaxScore] = useState(10);

  // Modals
  const [showWizard, setShowWizard] = useState(false);
  const [viewQuestion, setViewQuestion] = useState<IQuestionDetailDTO | null>(null);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);

  const loadExam = async () => {
    if (!courseId || !examId) return;
    setLoading(true);
    try {
      const detail = await dispatch(getExamDetail({ courseId, examId })).unwrap();
      setExam(detail);
      setTitle(detail.title || '');
      setDuration(detail.durationMinutes || 15);
      setPassScore(Number(detail.passScore) || 60);
      setQuestionCount(detail.questions?.length ? detail.questions.length : 10);
      const computedScore = detail.questions?.reduce((sum, q) => sum + (Number(q.score) || 0), 0) || 0;
      setMaxScore(
        computedScore > 0 ? Number(computedScore.toFixed(2)) : detail.totalScore && detail.totalScore > 0 ? detail.totalScore : 10,
      );
    } catch {
      toast.error('Không thể tải chi tiết bài kiểm tra chương.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void loadExam();
  }, [courseId, examId]);

  const hasBasicInfoChanges = Boolean(
    exam && (title.trim() !== exam.title || duration !== exam.durationMinutes || passScore !== Number(exam.passScore)),
  );

  const handleSaveBasicInfo = async () => {
    if (!title.trim()) {
      toast.error('Vui lòng nhập tên bài kiểm tra.');
      return;
    }
    setSaving(true);
    try {
      const updated = await dispatch(
        updateExam({
          courseId,
          examId,
          input: {
            title: title.trim(),
            durationMinutes: duration,
            passScore,
          },
        }),
      ).unwrap();
      setExam(updated);
      toast.success('Đã lưu thông tin bài kiểm tra.');
      onExamUpdated?.();
    } catch {
      toast.error('Không thể cập nhật bài kiểm tra.');
    } finally {
      setSaving(false);
    }
  };

  const handleConfirmDelete = async () => {
    try {
      await dispatch(deleteExam({ courseId, examId })).unwrap();
      toast.success('Đã xóa bài kiểm tra chương.');
      setShowDeleteConfirm(false);
      onDeleteExam();
    } catch {
      toast.error('Không thể xóa bài kiểm tra.');
    }
  };

  const currentQuestionsCount = exam?.questions?.length || 0;
  const currentTotalScore =
    exam?.totalScore != null && exam.totalScore > 0 ? exam.totalScore : exam?.questions?.reduce((sum, q) => sum + (q.score || 0), 0) || 0;

  if (loading && !exam) {
    return (
      <Card className="p-5 rounded-4 border-0 shadow-sm text-center">
        <Spinner animation="border" variant="primary" className="mx-auto mb-2" />
        <span className="text-muted small">Đang tải thông tin bài kiểm tra...</span>
      </Card>
    );
  }

  return (
    <div className="d-flex flex-column gap-4">
      {/* 1. THÔNG TIN BÀI KIỂM TRA CHƯƠNG */}
      <Card className="p-4 rounded-4 border-0 shadow-sm border-start border-4 border-success">
        <div className="d-flex justify-content-between align-items-center mb-3">
          <div>
            <div className="d-flex align-items-center gap-2">
              <Badge bg="success" className="px-2 py-1">
                <FontAwesomeIcon icon={faListCheck} className="me-1" />
                Bài kiểm tra chương
              </Badge>
              <span className="text-muted small">Chương: {chapterTitle}</span>
            </div>
            <h5 className="fw-bold mb-0 text-dark mt-1">{title || 'Bài kiểm tra chương'}</h5>
          </div>
          <div className="d-flex align-items-center gap-2">
            <Button
              variant="primary"
              size="sm"
              disabled={disabled || saving || !hasBasicInfoChanges || !title.trim()}
              onClick={handleSaveBasicInfo}
            >
              <FontAwesomeIcon icon={faFloppyDisk} className="me-1" />
              {saving ? 'Đang lưu...' : 'Lưu thông tin'}
            </Button>
            <Button
              variant="outline-danger"
              size="sm"
              disabled={disabled || saving}
              onClick={() => setShowDeleteConfirm(true)}
              title="Xóa bài kiểm tra này"
            >
              <FontAwesomeIcon icon={faTrash} className="me-1" />
              Xóa đề
            </Button>
          </div>
        </div>

        <Form.Group className="mb-3">
          <Form.Label className="small fw-semibold">Tên bài kiểm tra *</Form.Label>
          <Form.Control
            type="text"
            value={title}
            onChange={e => setTitle(e.target.value)}
            placeholder="VD: Kiểm tra cuối chương 1"
            disabled={disabled || saving}
          />
        </Form.Group>

        <Row className="mb-3">
          <Col md={6}>
            <Form.Group>
              <Form.Label className="small fw-semibold">Thời gian làm bài (phút)</Form.Label>
              <Form.Control
                type="number"
                min={1}
                value={duration}
                onChange={e => setDuration(parseInt(e.target.value, 10) || 15)}
                disabled={disabled || saving}
              />
            </Form.Group>
          </Col>
          <Col md={6}>
            <Form.Group>
              <Form.Label className="small fw-semibold">Điểm đạt (%)</Form.Label>
              <Form.Control
                type="number"
                min={0}
                max={100}
                value={passScore}
                onChange={e => setPassScore(parseInt(e.target.value, 10) || 60)}
                disabled={disabled || saving}
              />
            </Form.Group>
          </Col>
        </Row>

        <Row>
          <Col md={6}>
            <Form.Group>
              <Form.Label className="small fw-semibold">Số lượng câu hỏi mục tiêu</Form.Label>
              <Form.Control
                type="number"
                min={1}
                max={100}
                value={questionCount}
                onChange={e => setQuestionCount(parseInt(e.target.value, 10) || 10)}
                disabled={disabled || saving}
              />
              <Form.Text className="text-muted small">Dùng làm số lượng câu khi mở wizard soạn câu hỏi.</Form.Text>
            </Form.Group>
          </Col>
          <Col md={6}>
            <Form.Group>
              <Form.Label className="small fw-semibold">Điểm tối đa của đề thi *</Form.Label>
              <Form.Control
                type="number"
                min={1}
                max={1000}
                value={maxScore}
                onChange={e => setMaxScore(parseFloat(e.target.value) || 10)}
                disabled={disabled || saving}
              />
              <Form.Text className="text-muted small">Tổng điểm các câu hỏi phải đạt đúng mức này.</Form.Text>
            </Form.Group>
          </Col>
        </Row>
      </Card>

      {/* 2. CARD: DANH SÁCH CÂU HỎI VÀ NÚT SOẠN CÂU HỎI */}
      <Card className="p-4 rounded-4 border-0 shadow-sm border-start border-4 border-primary">
        <div className="d-flex flex-wrap justify-content-between align-items-center mb-3 gap-2">
          <div>
            <h5 className="fw-bold mb-1">
              <FontAwesomeIcon icon={faListCheck} className="text-primary me-2" />
              Danh sách câu hỏi đề thi
            </h5>
            <div className="d-flex align-items-center gap-2">
              <Badge bg="primary">
                {currentQuestionsCount} / {questionCount} câu hỏi
              </Badge>
              <Badge bg={Math.abs(currentTotalScore - maxScore) < 0.001 ? 'success' : 'warning'} className="text-dark">
                Tổng điểm: {currentTotalScore} / {maxScore} điểm
              </Badge>
            </div>
          </div>
          <Button variant="primary" size="sm" disabled={disabled || saving} onClick={() => setShowWizard(true)}>
            <FontAwesomeIcon icon={faPen} className="me-1" />
            Soạn câu hỏi bài kiểm tra
          </Button>
        </div>

        {exam?.questions && exam.questions.length > 0 ? (
          <div className="d-flex flex-column gap-2 mt-2">
            {exam.questions.map((q, idx) => (
              <div key={q.id} className="p-2 px-3 border rounded-3 bg-white d-flex justify-content-between align-items-center">
                <span className="text-truncate me-2" style={{ maxWidth: '75%' }}>
                  <strong>Câu {idx + 1}:</strong> {q.content}
                </span>
                <div className="d-flex align-items-center gap-2">
                  <Badge bg="secondary" className="px-2 py-1">
                    {q.score} điểm
                  </Badge>
                  <Button
                    size="sm"
                    variant="link"
                    className="text-info p-0"
                    title="Xem chi tiết câu hỏi"
                    onClick={() => setViewQuestion(q)}
                  >
                    <FontAwesomeIcon icon={faEye} />
                  </Button>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="text-muted small p-4 bg-light rounded-3 text-center border">
            Bài kiểm tra chưa có câu hỏi nào. Bấm nút <strong>Soạn câu hỏi bài kiểm tra</strong> ở trên để bắt đầu thêm câu hỏi.
          </div>
        )}
      </Card>

      {/* Modal Wizard soạn câu hỏi tuần tự */}
      {showWizard && (
        <ExamQuestionWizardModal
          show={showWizard}
          courseId={courseId}
          examId={examId}
          examTitle={title}
          totalQuestions={questionCount}
          maxScore={maxScore}
          onClose={() => {
            setShowWizard(false);
            void loadExam();
            onExamUpdated?.();
          }}
          onFinish={() => {
            setShowWizard(false);
            void loadExam();
            onExamUpdated?.();
          }}
        />
      )}

      {/* Modal xem chi tiết câu hỏi */}
      <QuestionDetailModal show={Boolean(viewQuestion)} question={viewQuestion} onClose={() => setViewQuestion(null)} />

      {/* Modal xác nhận xóa đề thi */}
      <Modal show={showDeleteConfirm} onHide={() => setShowDeleteConfirm(false)} centered>
        <Modal.Header closeButton>
          <Modal.Title className="h6 fw-bold">Xác nhận xóa bài kiểm tra</Modal.Title>
        </Modal.Header>
        <Modal.Body>Bạn có chắc muốn xóa bài kiểm tra &quot;{title}&quot; cùng toàn bộ câu hỏi của nó không?</Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" size="sm" onClick={() => setShowDeleteConfirm(false)}>
            Hủy
          </Button>
          <Button variant="danger" size="sm" onClick={handleConfirmDelete}>
            Xác nhận xóa đề
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default ChapterExamEditor;
