import React, { useState, useEffect } from 'react';
import { Card, Button, Form, Badge, Modal, Row, Col } from 'react-bootstrap';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faPlus,
  faTrash,
  faQuestionCircle,
  faListCheck,
  faEye,
  faPen,
  faWandMagicSparkles,
  faList,
  faChevronUp,
} from '@fortawesome/free-solid-svg-icons';
import { toast } from 'react-toastify';
import {
  getLessonExams,
  createLessonExam,
  updateExam,
  addExamQuestion,
  updateExamQuestion,
  deleteExamQuestion,
  getExamDetail,
  deleteExam,
  IExamDetailDTO,
  ICreateQuestionInput,
  IQuestionDetailDTO,
} from '../../course-builder.reducer';
import LessonMedia from './lesson-media';
import QuestionEditorModal from './question-editor-modal';
import QuestionDetailModal from './question-detail-modal';
import ExamQuestionWizardModal from './exam-question-wizard-modal';

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

  const dispatch = useAppDispatch();
  const courseId = useAppSelector(state => state.courseBuilder.data?.course.id);

  // 1) Câu hỏi luyện tập (Bắt buộc)
  const [practiceExam, setPracticeExam] = useState<IExamDetailDTO | null>(null);
  // 2) Danh sách bài kiểm tra bài học (Tùy chọn)
  const [lessonExams, setLessonExams] = useState<IExamDetailDTO[]>([]);

  // Modal tạo / sửa câu hỏi đơn (cho câu hỏi luyện tập)
  const [questionModalConfig, setQuestionModalConfig] = useState<{
    show: boolean;
    examId: number;
    question?: IQuestionDetailDTO | null;
    hideDifficulty?: boolean;
    hideScore?: boolean;
    hideExplanation?: boolean;
    title?: string;
  } | null>(null);

  // Modal xem chi tiết câu hỏi
  const [viewQuestion, setViewQuestion] = useState<IQuestionDetailDTO | null>(null);

  // Modal tạo / chỉnh sửa thông tin bài kiểm tra bài học (Bước 1)
  const [lessonExamModal, setLessonExamModal] = useState<{
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
    duration: 10,
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

  // Modal xác nhận xóa câu hỏi
  const [deleteQuestionTarget, setDeleteQuestionTarget] = useState<{ examId: number; questionId: number } | null>(null);
  // Modal xác nhận xóa bài kiểm tra
  const [deleteExamTarget, setDeleteExamTarget] = useState<IExamDetailDTO | null>(null);

  // Trạng thái mở rộng/thu gọn danh sách câu hỏi xem trước của từng đề thi
  const [expandedExamIds, setExpandedExamIds] = useState<Set<number>>(new Set());

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

  const loadLessonAssessments = async () => {
    if (!courseId || !lesson.id) return;
    try {
      const exams = await dispatch(getLessonExams({ courseId, lessonId: lesson.id })).unwrap();
      const practice = exams.find(e => e.assessmentType === 'PRACTICE');
      const quizzes = exams.filter(e => e.assessmentType === 'LESSON_QUIZ');

      if (practice) {
        const detail = await dispatch(getExamDetail({ courseId, examId: practice.id })).unwrap();
        setPracticeExam(detail);
      } else {
        setPracticeExam(null);
      }

      if (quizzes.length > 0) {
        const details = await Promise.all(quizzes.map(q => dispatch(getExamDetail({ courseId, examId: q.id })).unwrap()));
        setLessonExams(details);
      } else {
        setLessonExams([]);
      }
    } catch {
      /* ignore */
    }
  };

  useEffect(() => {
    void loadLessonAssessments();
  }, [lesson.id, courseId]);

  // Tạo đề luyện tập mặc định nếu chưa có
  const handleCreatePracticeExam = async () => {
    if (!courseId) return;
    try {
      const created = await dispatch(
        createLessonExam({
          courseId,
          lessonId: lesson.id,
          input: { title: `Luyện tập: ${lesson.title}`, passScore: 60 },
          isPractice: true,
        }),
      ).unwrap();
      toast.success('Đã tạo bộ câu hỏi luyện tập.');
      setPracticeExam(created);
    } catch {
      toast.error('Không thể tạo bộ câu hỏi luyện tập.');
    }
  };

  // Lưu câu hỏi (Thêm mới hoặc Cập nhật)
  const handleSaveQuestion = async (input: ICreateQuestionInput) => {
    if (!courseId || !questionModalConfig) return;
    try {
      if (questionModalConfig.question?.id) {
        await dispatch(
          updateExamQuestion({
            courseId,
            examId: questionModalConfig.examId,
            questionId: questionModalConfig.question.id,
            input,
          }),
        ).unwrap();
        toast.success('Đã cập nhật câu hỏi thành công.');
      } else {
        await dispatch(addExamQuestion({ courseId, examId: questionModalConfig.examId, input })).unwrap();
        toast.success('Đã lưu câu hỏi mới.');
      }
      setQuestionModalConfig(null);
      void loadLessonAssessments();
    } catch {
      toast.error('Không thể lưu câu hỏi.');
    }
  };

  // Xóa câu hỏi khỏi đề thi / luyện tập
  const handleConfirmDeleteQuestion = async () => {
    if (!courseId || !deleteQuestionTarget) return;
    try {
      await dispatch(
        deleteExamQuestion({
          courseId,
          examId: deleteQuestionTarget.examId,
          questionId: deleteQuestionTarget.questionId,
        }),
      ).unwrap();
      toast.success('Đã xóa câu hỏi.');
      void loadLessonAssessments();
    } catch {
      toast.error('Không thể xóa câu hỏi.');
    } finally {
      setDeleteQuestionTarget(null);
    }
  };

  // Mở modal tạo đề mới
  const handleOpenCreateExam = () => {
    setLessonExamModal({
      show: true,
      mode: 'create',
      title: `Kiểm tra: ${title}`,
      duration: 10,
      passScore: 60,
      questionCount: 10,
      maxScore: 10,
    });
  };

  // Mở modal sửa đề đã có
  const handleOpenEditExam = (exam: IExamDetailDTO) => {
    const qCount = exam.questions?.length || 1;
    const mScore = exam.totalScore && exam.totalScore > 0 ? exam.totalScore : 10;
    setLessonExamModal({
      show: true,
      mode: 'edit',
      examId: exam.id,
      title: exam.title,
      duration: exam.durationMinutes,
      passScore: exam.passScore,
      questionCount: qCount,
      maxScore: mScore,
    });
  };

  // Lưu thông tin đề thi ở Bước 1 và chuyển sang Wizard soạn câu hỏi
  const handleSubmitLessonExamModal = async () => {
    if (!courseId) return;
    if (!lessonExamModal.title.trim()) {
      toast.error('Vui lòng nhập tên bài kiểm tra.');
      return;
    }
    const qCount = lessonExamModal.questionCount || 10;
    const mScore = lessonExamModal.maxScore || 10;

    try {
      let targetExamId: number;
      let targetExamTitle: string;

      if (lessonExamModal.mode === 'create') {
        const created = await dispatch(
          createLessonExam({
            courseId,
            lessonId: lesson.id,
            input: {
              title: lessonExamModal.title.trim(),
              durationMinutes: lessonExamModal.duration,
              passScore: lessonExamModal.passScore,
            },
            isPractice: false,
          }),
        ).unwrap();
        targetExamId = created.id;
        targetExamTitle = created.title;
        toast.success('Đã tạo thông tin bài kiểm tra. Tiếp tục soạn câu hỏi.');
      } else {
        // Mode edit -> gọi api edit exam
        const updated = await dispatch(
          updateExam({
            courseId,
            examId: lessonExamModal.examId!,
            input: {
              title: lessonExamModal.title.trim(),
              durationMinutes: lessonExamModal.duration,
              passScore: lessonExamModal.passScore,
            },
          }),
        ).unwrap();
        targetExamId = updated.id;
        targetExamTitle = updated.title;
        toast.success('Đã cập nhật thông tin bài kiểm tra. Tiếp tục soạn câu hỏi.');
      }

      setLessonExamModal(prev => ({ ...prev, show: false }));
      // Mở wizard nhập tuần tự câu hỏi
      setWizardConfig({
        show: true,
        examId: targetExamId,
        examTitle: targetExamTitle,
        totalQuestions: qCount,
        maxScore: mScore,
      });
      void loadLessonAssessments();
    } catch {
      toast.error('Không thể lưu thông tin bài kiểm tra.');
    }
  };

  // Quay lại từ Wizard về Modal thông tin đề (Bước 1)
  const handleWizardBackToExamModal = () => {
    if (!wizardConfig) return;
    const currentExam = lessonExams.find(e => e.id === wizardConfig.examId);
    setWizardConfig(null);
    setLessonExamModal({
      show: true,
      mode: 'edit',
      examId: wizardConfig.examId,
      title: currentExam?.title || wizardConfig.examTitle,
      duration: currentExam?.durationMinutes || 10,
      passScore: currentExam?.passScore || 60,
      questionCount: wizardConfig.totalQuestions,
      maxScore: wizardConfig.maxScore,
    });
  };

  // Nhờ AI tạo đề (Người dùng yêu cầu: chỉ thêm button, chưa làm logic)
  const handleAiGenerateNotice = () => {
    toast.info('Tính năng tạo đề bằng AI đang được phát triển!');
  };

  // Xóa bài kiểm tra bài học
  const handleConfirmDeleteLessonExam = async () => {
    if (!courseId || !deleteExamTarget) return;
    try {
      await dispatch(deleteExam({ courseId, examId: deleteExamTarget.id })).unwrap();
      toast.success('Đã xóa bài kiểm tra.');
      setDeleteExamTarget(null);
      void loadLessonAssessments();
    } catch {
      toast.error('Không thể xóa bài kiểm tra.');
    }
  };

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

      {/* 2. Media (Tài liệu & Video bài học kèm Cue-point tương tác trực tiếp) */}
      <LessonMedia lessonId={lesson.id} disabled={saving || disabled} />

      {/* 3. CARD: Câu hỏi ôn tập & luyện tập (Bắt buộc) */}
      <Card className="p-4 rounded-4 border-0 shadow-sm border-start border-4 border-warning">
        <div className="d-flex justify-content-between align-items-center mb-2">
          <div>
            <h5 className="fw-bold mb-0">
              <FontAwesomeIcon icon={faQuestionCircle} className="text-warning me-2" />
              Câu hỏi ôn tập & luyện tập <span className="text-danger">*</span>
            </h5>
            <span className="text-muted small">Bắt buộc phải có câu hỏi để học viên ôn tập kiến thức sau bài học.</span>
          </div>
          {!practiceExam ? (
            <Button size="sm" variant="outline-warning" onClick={handleCreatePracticeExam} disabled={disabled}>
              <FontAwesomeIcon icon={faPlus} className="me-1" /> Tạo bộ câu hỏi luyện tập
            </Button>
          ) : (
            <Button
              size="sm"
              variant="warning"
              onClick={() =>
                setQuestionModalConfig({
                  show: true,
                  examId: practiceExam.id,
                  question: null,
                  hideDifficulty: true,
                  hideScore: true,
                  title: 'Thêm câu hỏi luyện tập',
                })
              }
              disabled={disabled}
            >
              <FontAwesomeIcon icon={faPlus} className="me-1" /> Thêm câu hỏi
            </Button>
          )}
        </div>

        {practiceExam?.questions?.length ? (
          <div className="d-flex flex-column gap-2 mt-3">
            {practiceExam.questions.map((q, idx) => (
              <div key={q.id} className="p-2 px-3 border rounded-3 bg-light d-flex justify-content-between align-items-center">
                <span className="text-truncate me-2" style={{ maxWidth: '75%' }}>
                  <strong>Câu {idx + 1}:</strong> {q.content}
                </span>
                <div className="d-flex align-items-center gap-2">
                  {/* Nút Xem chi tiết */}
                  <Button
                    size="sm"
                    variant="link"
                    className="text-info p-0"
                    title="Xem chi tiết câu hỏi"
                    onClick={() => setViewQuestion(q)}
                  >
                    <FontAwesomeIcon icon={faEye} />
                  </Button>
                  {/* Nút Chỉnh sửa */}
                  <Button
                    size="sm"
                    variant="link"
                    className="text-primary p-0"
                    title="Chỉnh sửa câu hỏi"
                    disabled={disabled}
                    onClick={() =>
                      setQuestionModalConfig({
                        show: true,
                        examId: practiceExam.id,
                        question: q,
                        hideDifficulty: true,
                        hideScore: true,
                        title: 'Chỉnh sửa câu hỏi luyện tập',
                      })
                    }
                  >
                    <FontAwesomeIcon icon={faPen} />
                  </Button>
                  {/* Nút Xóa */}
                  <Button
                    size="sm"
                    variant="link"
                    className="text-danger p-0"
                    title="Xóa câu hỏi"
                    disabled={disabled}
                    onClick={() => setDeleteQuestionTarget({ examId: practiceExam.id, questionId: q.id })}
                  >
                    <FontAwesomeIcon icon={faTrash} />
                  </Button>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="p-3 border rounded-3 bg-light text-muted small mt-3">
            Chưa có câu hỏi luyện tập nào cho bài học này. Bấm nút <strong>+ Thêm câu hỏi</strong> ở trên để bổ sung.
          </div>
        )}
      </Card>

      {/* 4. CARD: Bài kiểm tra bài học (Tùy chọn) */}
      <Card className="p-4 rounded-4 border-0 shadow-sm border-start border-4 border-primary">
        <div className="d-flex justify-content-between align-items-center mb-2">
          <div>
            <h5 className="fw-bold mb-0">
              <FontAwesomeIcon icon={faListCheck} className="text-primary me-2" />
              Bài kiểm tra bài học (Tùy chọn)
            </h5>
            <span className="text-muted small">Các bài kiểm tra tính điểm riêng cho bài học này.</span>
          </div>
          <Button size="sm" variant="outline-primary" disabled={disabled} onClick={handleOpenCreateExam}>
            <FontAwesomeIcon icon={faPlus} className="me-1" /> Tạo bài kiểm tra
          </Button>
        </div>

        {lessonExams.length > 0 ? (
          <div className="d-flex flex-column gap-3 mt-3">
            {lessonExams.map(exam => {
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
                        onClick={() => setDeleteExamTarget(exam)}
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
        ) : (
          <div className="p-3 border rounded-3 bg-light text-muted small mt-3">
            Chưa có bài kiểm tra nào cho bài học này. Bấm nút <strong>+ Tạo bài kiểm tra</strong> ở trên để tạo đề.
          </div>
        )}
      </Card>

      {/* 5. Xem thử miễn phí */}
      <Card className="p-4 rounded-4 border-0 shadow-sm">
        <h5 className="fw-bold mb-3">Xem thử miễn phí</h5>
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

      {/* Modal soạn câu hỏi đơn (Tạo mới hoặc Sửa) */}
      {questionModalConfig && (
        <QuestionEditorModal
          show={questionModalConfig.show}
          title={questionModalConfig.title}
          question={questionModalConfig.question}
          hideDifficulty={questionModalConfig.hideDifficulty}
          hideScore={questionModalConfig.hideScore}
          hideExplanation={questionModalConfig.hideExplanation}
          onClose={() => setQuestionModalConfig(null)}
          onSave={handleSaveQuestion}
        />
      )}

      {/* Modal xem chi tiết câu hỏi (View) */}
      <QuestionDetailModal show={Boolean(viewQuestion)} question={viewQuestion} onClose={() => setViewQuestion(null)} />

      {/* Modal Wizard nhập câu hỏi tuần tự (Multi-step) */}
      {wizardConfig && (
        <ExamQuestionWizardModal
          show={wizardConfig.show}
          courseId={courseId!}
          examId={wizardConfig.examId}
          examTitle={wizardConfig.examTitle}
          totalQuestions={wizardConfig.totalQuestions}
          maxScore={wizardConfig.maxScore}
          onClose={() => {
            setWizardConfig(null);
            void loadLessonAssessments();
          }}
          onBackToExamModal={handleWizardBackToExamModal}
          onFinish={() => {
            setWizardConfig(null);
            void loadLessonAssessments();
          }}
        />
      )}

      {/* Modal tạo / chỉnh sửa thông tin bài kiểm tra bài học */}
      <Modal show={lessonExamModal.show} onHide={() => setLessonExamModal(prev => ({ ...prev, show: false }))} centered backdrop="static">
        <Modal.Header closeButton>
          <Modal.Title className="h6 fw-bold">
            {lessonExamModal.mode === 'create' ? 'Tạo bài kiểm tra cho bài học' : 'Chỉnh sửa thông tin bài kiểm tra'}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Form.Group className="mb-3">
            <Form.Label className="small fw-semibold">Tên bài kiểm tra *</Form.Label>
            <Form.Control
              type="text"
              value={lessonExamModal.title}
              onChange={e => setLessonExamModal(prev => ({ ...prev, title: e.target.value }))}
              placeholder="VD: Kiểm tra kiến thức bài 1"
            />
          </Form.Group>
          <Row>
            <Col md={6}>
              <Form.Group className="mb-3">
                <Form.Label className="small fw-semibold">Thời gian (phút)</Form.Label>
                <Form.Control
                  type="number"
                  min={1}
                  value={lessonExamModal.duration}
                  onChange={e => setLessonExamModal(prev => ({ ...prev, duration: parseInt(e.target.value, 10) || 10 }))}
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
                  value={lessonExamModal.passScore}
                  onChange={e => setLessonExamModal(prev => ({ ...prev, passScore: parseInt(e.target.value, 10) || 60 }))}
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
                  max={50}
                  value={lessonExamModal.questionCount}
                  onChange={e => setLessonExamModal(prev => ({ ...prev, questionCount: parseInt(e.target.value, 10) || 10 }))}
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
                  value={lessonExamModal.maxScore}
                  onChange={e => setLessonExamModal(prev => ({ ...prev, maxScore: parseFloat(e.target.value) || 10 }))}
                />
              </Form.Group>
            </Col>
          </Row>
          <Form.Text className="text-muted small">
            {lessonExamModal.mode === 'create'
              ? 'Bạn có thể soạn từng câu hỏi thủ công hoặc sử dụng AI để tạo đề tự động.'
              : 'Sau khi cập nhật thông tin, bạn sẽ được chuyển đến màn hình soạn/sửa các câu hỏi.'}
          </Form.Text>
        </Modal.Body>
        <Modal.Footer className="d-flex justify-content-between align-items-center">
          <Button variant="secondary" size="sm" onClick={() => setLessonExamModal(prev => ({ ...prev, show: false }))}>
            Hủy
          </Button>
          <div className="d-flex gap-2">
            <Button variant="primary" size="sm" onClick={handleSubmitLessonExamModal}>
              <FontAwesomeIcon icon={faPen} className="me-1" />
              {lessonExamModal.mode === 'create' ? 'Tạo đề thủ công' : 'Tiếp tục soạn câu hỏi'}
            </Button>
            {lessonExamModal.mode === 'create' && (
              <Button variant="outline-primary" size="sm" onClick={handleAiGenerateNotice}>
                <FontAwesomeIcon icon={faWandMagicSparkles} className="me-1" />
                Nhờ AI tạo đề
              </Button>
            )}
          </div>
        </Modal.Footer>
      </Modal>

      {/* Modal xác nhận xóa câu hỏi */}
      <Modal show={Boolean(deleteQuestionTarget)} onHide={() => setDeleteQuestionTarget(null)} centered>
        <Modal.Header closeButton>
          <Modal.Title className="h6 fw-bold">Xác nhận xóa câu hỏi</Modal.Title>
        </Modal.Header>
        <Modal.Body>Bạn có chắc muốn xóa câu hỏi này không?</Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" size="sm" onClick={() => setDeleteQuestionTarget(null)}>
            Hủy
          </Button>
          <Button variant="danger" size="sm" onClick={handleConfirmDeleteQuestion}>
            Xác nhận xóa
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Modal xác nhận xóa bài kiểm tra */}
      <Modal show={Boolean(deleteExamTarget)} onHide={() => setDeleteExamTarget(null)} centered>
        <Modal.Header closeButton>
          <Modal.Title className="h6 fw-bold">Xác nhận xóa bài kiểm tra</Modal.Title>
        </Modal.Header>
        <Modal.Body>Bạn có chắc muốn xóa bài kiểm tra &quot;{deleteExamTarget?.title}&quot; cùng tất cả câu hỏi của nó không?</Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" size="sm" onClick={() => setDeleteExamTarget(null)}>
            Hủy
          </Button>
          <Button variant="danger" size="sm" onClick={handleConfirmDeleteLessonExam}>
            Xác nhận xóa đề
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default LessonEditor;
