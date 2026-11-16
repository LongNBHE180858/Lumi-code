import React, { useState, useEffect } from 'react';
import { Card, Button, Modal, Form, Row, Col, Spinner, Badge } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faPlus, faTrash, faPen, faWandMagicSparkles, faList, faChevronUp, faEye } from '@fortawesome/free-solid-svg-icons';
import { useAppDispatch } from 'app/config/store';
import { toast } from 'react-toastify';
import {
  getCourseExams,
  createCourseExam,
  updateExam,
  deleteExam,
  getExamDetail,
  IExamDetailDTO,
  IQuestionDetailDTO,
} from '../course-builder.reducer';
import QuestionDetailModal from '../curriculum/lesson/question-detail-modal';
import ExamQuestionWizardModal from '../curriculum/lesson/exam-question-wizard-modal';

interface CourseExamsTabProps {
  courseId: number;
  onBack: () => void;
  onNext: () => void;
  disabled?: boolean;
}

export const CourseExamsTab = ({ courseId, onBack, onNext, disabled }: CourseExamsTabProps) => {
  const dispatch = useAppDispatch();
  const [exams, setExams] = useState<IExamDetailDTO[]>([]);
  const [loading, setLoading] = useState(false);

  // Mở rộng/thu gọn danh sách câu hỏi xem trước của từng đề thi
  const [expandedExamIds, setExpandedExamIds] = useState<Set<number>>(new Set());

  // Modal tạo / chỉnh sửa thông tin bài kiểm tra (Bước 1)
  const [examModal, setExamModal] = useState<{
    show: boolean;
    mode: 'create' | 'edit';
    examId?: number;
    title: string;
    duration: number;
    passScore: number;
    questionCount: number;
    maxScore: number;
  }>({
    show: false,
    mode: 'create',
    title: '',
    duration: 45,
    passScore: 60,
    questionCount: 10,
    maxScore: 10,
  });

  // Modal Wizard nhập câu hỏi tuần tự (Bước 2)
  const [wizardConfig, setWizardConfig] = useState<{
    show: boolean;
    examId: number;
    examTitle: string;
    totalQuestions: number;
    maxScore: number;
  } | null>(null);

  // Modal xem chi tiết câu hỏi
  const [viewQuestion, setViewQuestion] = useState<IQuestionDetailDTO | null>(null);

  // Modal xác nhận xóa bài kiểm tra
  const [deleteTarget, setDeleteTarget] = useState<{ examId: number; title: string } | null>(null);

  const toggleExamQuestions = (examId: number) => {
    setExpandedExamIds(prev => {
      const next = new Set(prev);
      if (next.has(examId)) {
        next.delete(examId);
      } else {
        next.add(examId);
      }
      return next;
    });
  };

  const fetchExams = async () => {
    setLoading(true);
    try {
      const res = await dispatch(getCourseExams(courseId)).unwrap();
      if (res.length > 0) {
        const details = await Promise.all(res.map(e => dispatch(getExamDetail({ courseId, examId: e.id })).unwrap()));
        setExams(details);
      } else {
        setExams([]);
      }
    } catch {
      toast.error('Không thể tải danh sách bài kiểm tra khóa học.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (courseId) void fetchExams();
  }, [courseId]);

  // Mở modal tạo đề mới
  const handleOpenCreateExam = () => {
    setExamModal({
      show: true,
      mode: 'create',
      title: 'Kiểm tra cuối khóa',
      duration: 45,
      passScore: 60,
      questionCount: 10,
      maxScore: 10,
    });
  };

  // Mở modal sửa đề đã có
  const handleOpenEditExam = (exam: IExamDetailDTO) => {
    const qCount = exam.questions?.length || 10;
    const computedScore = exam.questions?.reduce((sum, q) => sum + (Number(q.score) || 0), 0) || 0;
    const mScore = computedScore > 0 ? Number(computedScore.toFixed(2)) : exam.totalScore && exam.totalScore > 0 ? exam.totalScore : 10;

    setExamModal({
      show: true,
      mode: 'edit',
      examId: exam.id,
      title: exam.title,
      duration: exam.durationMinutes || 45,
      passScore: Number(exam.passScore) || 60,
      questionCount: qCount,
      maxScore: mScore,
    });
  };

  // Lưu thông tin đề thi ở Bước 1 và chuyển sang Wizard soạn câu hỏi
  const handleSubmitExamModal = async () => {
    if (!examModal.title.trim()) {
      toast.error('Vui lòng nhập tên bài kiểm tra.');
      return;
    }
    const qCount = examModal.questionCount || 10;
    const mScore = examModal.maxScore || 10;

    try {
      let targetExamId: number;
      let targetExamTitle: string;

      if (examModal.mode === 'create') {
        const created = await dispatch(
          createCourseExam({
            courseId,
            input: {
              title: examModal.title.trim(),
              durationMinutes: examModal.duration,
              passScore: examModal.passScore,
              maxAttempts: 1,
              shuffleQuestions: true,
            },
          }),
        ).unwrap();
        targetExamId = created.id;
        targetExamTitle = created.title;
        toast.success('Đã tạo thông tin bài kiểm tra. Tiếp tục soạn câu hỏi.');
      } else {
        const updated = await dispatch(
          updateExam({
            courseId,
            examId: examModal.examId!,
            input: {
              title: examModal.title.trim(),
              durationMinutes: examModal.duration,
              passScore: examModal.passScore,
            },
          }),
        ).unwrap();
        targetExamId = updated.id;
        targetExamTitle = updated.title;
        toast.success('Đã cập nhật thông tin bài kiểm tra. Tiếp tục soạn câu hỏi.');
      }

      setExamModal(prev => ({ ...prev, show: false }));
      setWizardConfig({
        show: true,
        examId: targetExamId,
        examTitle: targetExamTitle,
        totalQuestions: qCount,
        maxScore: mScore,
      });
      void fetchExams();
    } catch {
      toast.error('Không thể lưu thông tin bài kiểm tra.');
    }
  };

  // Quay lại từ Wizard về Modal thông tin đề (Bước 1)
  const handleWizardBackToExamModal = () => {
    if (!wizardConfig) return;
    const currentExam = exams.find(e => e.id === wizardConfig.examId);
    setWizardConfig(null);
    setExamModal({
      show: true,
      mode: 'edit',
      examId: wizardConfig.examId,
      title: currentExam?.title || wizardConfig.examTitle,
      duration: currentExam?.durationMinutes || 45,
      passScore: Number(currentExam?.passScore) || 60,
      questionCount: wizardConfig.totalQuestions,
      maxScore: wizardConfig.maxScore,
    });
  };

  // Hoàn tất Wizard
  const handleWizardFinish = () => {
    setWizardConfig(null);
    void fetchExams();
  };

  // Thông báo AI tạo đề
  const handleAiGenerateNotice = () => {
    toast.info('Tính năng tạo đề bằng AI đang được phát triển!');
  };

  // Xác nhận xóa bài kiểm tra
  const handleConfirmDeleteExam = async () => {
    if (!deleteTarget) return;
    try {
      await dispatch(deleteExam({ courseId, examId: deleteTarget.examId })).unwrap();
      toast.success('Đã xóa bài kiểm tra.');
      void fetchExams();
    } catch {
      toast.error('Xóa bài kiểm tra thất bại.');
    } finally {
      setDeleteTarget(null);
    }
  };

  return (
    <div className="d-flex flex-column gap-4">
      <Card className="p-4 rounded-4 border-0 shadow-sm">
        <div className="d-flex justify-content-between align-items-center mb-3">
          <div>
            <h5 className="fw-bold mb-1">Đề kiểm tra cấp khóa học</h5>
            <span className="text-muted small">Tạo các bài kiểm tra đánh giá chung (Giữa kỳ, Cuối kỳ) cho cả khóa học.</span>
          </div>
          <Button variant="primary" disabled={disabled} onClick={handleOpenCreateExam}>
            <FontAwesomeIcon icon={faPlus} className="me-2" /> Tạo bài kiểm tra
          </Button>
        </div>

        {loading ? (
          <div className="text-center py-4">
            <Spinner animation="border" />
          </div>
        ) : exams.length === 0 ? (
          <div className="text-center py-4 text-muted bg-light rounded-3">Chưa có bài kiểm tra cấp khóa học.</div>
        ) : (
          <div className="d-flex flex-column gap-3">
            {exams.map(exam => {
              const isExpanded = expandedExamIds.has(exam.id);
              const totalScoreVal =
                exam.questions && exam.questions.length > 0
                  ? Number(exam.questions.reduce((sum, q) => sum + (Number(q.score) || 0), 0).toFixed(2))
                  : Number((exam.totalScore || 0).toFixed(2));

              return (
                <div key={exam.id} className="p-3 border rounded-3 bg-light">
                  <div className="d-flex flex-wrap justify-content-between align-items-center mb-2 gap-2">
                    <div>
                      <strong className="text-dark fs-6">{exam.title}</strong>
                      <span className="text-muted small ms-2">
                        ({exam.durationMinutes} phút • Đạt: {exam.passScore}%)
                      </span>
                    </div>
                    <div className="d-flex align-items-center gap-2">
                      <Badge bg="primary" className="px-2 py-1">
                        {exam.questions?.length || 0} câu hỏi
                      </Badge>
                      <Badge bg="success" className="px-2 py-1">
                        {totalScoreVal} điểm
                      </Badge>
                      <Button
                        size="sm"
                        variant={isExpanded ? 'primary' : 'outline-primary'}
                        className="py-1 px-2"
                        disabled={disabled}
                        title={isExpanded ? 'Ẩn danh sách câu hỏi' : 'Xem danh sách câu hỏi'}
                        onClick={() => toggleExamQuestions(exam.id)}
                      >
                        <FontAwesomeIcon icon={isExpanded ? faChevronUp : faList} className="me-1" />
                        {isExpanded ? 'Ẩn câu hỏi' : 'Câu hỏi'}
                      </Button>
                      <Button
                        size="sm"
                        variant="outline-primary"
                        className="py-1 px-2"
                        disabled={disabled}
                        title="Chỉnh sửa bài kiểm tra"
                        onClick={() => handleOpenEditExam(exam)}
                      >
                        <FontAwesomeIcon icon={faPen} className="me-1" /> Sửa đề
                      </Button>
                      <Button
                        size="sm"
                        variant="outline-danger"
                        className="py-1 px-2"
                        disabled={disabled}
                        title="Xóa bài kiểm tra"
                        onClick={() => setDeleteTarget({ examId: exam.id, title: exam.title })}
                      >
                        <FontAwesomeIcon icon={faTrash} />
                      </Button>
                    </div>
                  </div>

                  {/* Danh sách câu hỏi xem trước (chỉ hiển thị khi bấm nút Câu hỏi) */}
                  {isExpanded &&
                    (exam.questions?.length ? (
                      <div className="d-flex flex-column gap-2 mt-3 pt-2 border-top">
                        {exam.questions.map((q, qIdx) => (
                          <div key={q.id} className="p-2 px-3 border rounded-3 bg-white d-flex justify-content-between align-items-center">
                            <span className="text-truncate me-2" style={{ maxWidth: '75%' }}>
                              <strong>Câu {qIdx + 1}:</strong> {q.content}
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
                      <div className="text-muted small p-2 bg-white rounded border text-center mt-3">
                        Đề chưa có câu hỏi nào. Bấm <strong>Sửa đề</strong> để bắt đầu soạn câu hỏi.
                      </div>
                    ))}
                </div>
              );
            })}
          </div>
        )}
      </Card>

      {/* Điều hướng chân trang */}
      <div className="d-flex justify-content-between mt-3">
        <Button variant="outline-secondary" onClick={onBack}>
          ← Nội dung bài học
        </Button>
        <Button variant="primary" onClick={onNext}>
          Tiếp: Học phí →
        </Button>
      </div>

      {/* Modal tạo / chỉnh sửa thông tin bài kiểm tra khóa học (Bước 1) */}
      <Modal show={examModal.show} onHide={() => setExamModal(prev => ({ ...prev, show: false }))} centered backdrop="static">
        <Modal.Header closeButton>
          <Modal.Title className="h6 fw-bold">
            {examModal.mode === 'create' ? 'Tạo bài kiểm tra khóa học' : 'Chỉnh sửa thông tin bài kiểm tra'}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form.Group className="mb-3">
            <Form.Label className="small fw-semibold">Tên bài kiểm tra *</Form.Label>
            <Form.Control
              type="text"
              value={examModal.title}
              onChange={e => setExamModal(prev => ({ ...prev, title: e.target.value }))}
              placeholder="VD: Bài kiểm tra cuối khóa"
            />
          </Form.Group>
          <Row>
            <Col md={6}>
              <Form.Group className="mb-3">
                <Form.Label className="small fw-semibold">Thời gian (phút)</Form.Label>
                <Form.Control
                  type="number"
                  min={1}
                  value={examModal.duration}
                  onChange={e => setExamModal(prev => ({ ...prev, duration: parseInt(e.target.value, 10) || 45 }))}
                />
              </Form.Group>
            </Col>
            <Col md={6}>
              <Form.Group className="mb-3">
                <Form.Label className="small fw-semibold">Điểm đạt (%)</Form.Label>
                <Form.Control
                  type="number"
                  min={0}
                  max={100}
                  value={examModal.passScore}
                  onChange={e => setExamModal(prev => ({ ...prev, passScore: parseInt(e.target.value, 10) || 60 }))}
                />
              </Form.Group>
            </Col>
          </Row>
          <Row>
            <Col md={6}>
              <Form.Group className="mb-3">
                <Form.Label className="small fw-semibold">Số lượng câu hỏi</Form.Label>
                <Form.Control
                  type="number"
                  min={1}
                  max={100}
                  value={examModal.questionCount}
                  onChange={e => setExamModal(prev => ({ ...prev, questionCount: parseInt(e.target.value, 10) || 10 }))}
                />
              </Form.Group>
            </Col>
            <Col md={6}>
              <Form.Group className="mb-3">
                <Form.Label className="small fw-semibold">Điểm tối đa của bài kiểm tra *</Form.Label>
                <Form.Control
                  type="number"
                  min={1}
                  max={1000}
                  value={examModal.maxScore}
                  onChange={e => setExamModal(prev => ({ ...prev, maxScore: parseFloat(e.target.value) || 10 }))}
                />
              </Form.Group>
            </Col>
          </Row>
          <Form.Text className="text-muted small">
            {examModal.mode === 'create'
              ? 'Bạn có thể soạn từng câu hỏi thủ công hoặc sử dụng AI để tạo đề tự động.'
              : 'Sau khi cập nhật thông tin, bạn sẽ được chuyển đến màn hình soạn/sửa các câu hỏi.'}
          </Form.Text>
        </Modal.Body>
        <Modal.Footer className="d-flex justify-content-between align-items-center">
          <Button variant="secondary" size="sm" onClick={() => setExamModal(prev => ({ ...prev, show: false }))}>
            Hủy
          </Button>
          <div className="d-flex gap-2">
            <Button variant="primary" size="sm" onClick={handleSubmitExamModal}>
              <FontAwesomeIcon icon={faPen} className="me-1" />
              {examModal.mode === 'create' ? 'Tạo đề thủ công' : 'Tiếp tục soạn câu hỏi'}
            </Button>
            {examModal.mode === 'create' && (
              <Button variant="outline-primary" size="sm" onClick={handleAiGenerateNotice}>
                <FontAwesomeIcon icon={faWandMagicSparkles} className="me-1" />
                Nhờ AI tạo đề
              </Button>
            )}
          </div>
        </Modal.Footer>
      </Modal>

      {/* Modal Wizard nhập câu hỏi tuần tự (Bước 2) */}
      {wizardConfig && (
        <ExamQuestionWizardModal
          show={wizardConfig.show}
          courseId={courseId}
          examId={wizardConfig.examId}
          examTitle={wizardConfig.examTitle}
          totalQuestions={wizardConfig.totalQuestions}
          maxScore={wizardConfig.maxScore}
          onClose={() => setWizardConfig(null)}
          onBackToExamModal={handleWizardBackToExamModal}
          onFinish={handleWizardFinish}
        />
      )}

      {/* Modal xem chi tiết câu hỏi */}
      <QuestionDetailModal show={Boolean(viewQuestion)} question={viewQuestion} onClose={() => setViewQuestion(null)} />

      {/* Modal xác nhận xóa bài kiểm tra */}
      <Modal show={Boolean(deleteTarget)} onHide={() => setDeleteTarget(null)} centered>
        <Modal.Header closeButton>
          <Modal.Title className="h6 fw-bold">Xác nhận xóa bài kiểm tra</Modal.Title>
        </Modal.Header>
        <Modal.Body>Bạn có chắc muốn xóa bài kiểm tra &quot;{deleteTarget?.title}&quot; cùng tất cả câu hỏi của nó không?</Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" size="sm" onClick={() => setDeleteTarget(null)}>
            Hủy
          </Button>
          <Button variant="danger" size="sm" onClick={handleConfirmDeleteExam}>
            Xác nhận xóa đề
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default CourseExamsTab;
