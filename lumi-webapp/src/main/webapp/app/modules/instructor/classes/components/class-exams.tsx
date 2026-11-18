import React, { useEffect, useState } from 'react';
import { classroomError, getInstructorClassMembers, type ClassroomMaterial, type ClassroomMember } from 'app/shared/util/classroom-api';
import {
  createExamFromMatrix,
  generateExamPreview,
  getInstructorExams,
  updateClassExamStatus,
  type ExamSummary,
  type MatrixRow,
  type PickedQuestion,
} from 'app/shared/util/class-exam-api';
import { getBankTopics, getQuestionBanks, type BankSummary, type TopicSummary } from 'app/shared/util/question-bank-api';

interface Props {
  classId: number;
  lessons: ClassroomMaterial[];
  initialPracticeMaterialId?: number | null;
}

const difficultyLabels: Record<number, string> = {
  1: '1 - Nhận biết',
  2: '2 - Thông hiểu',
  3: '3 - Vận dụng',
  4: '4 - Vận dụng cao',
  5: '5 - Nâng cao',
};

const localDateTime = (date: Date): string => new Date(date.getTime() - date.getTimezoneOffset() * 60_000).toISOString().slice(0, 16);

const initialClosesAt = (): string => localDateTime(new Date(Date.now() + 7 * 24 * 60 * 60 * 1000));

export const ClassExams: React.FC<Props> = ({ classId, lessons, initialPracticeMaterialId = null }) => {
  const [exams, setExams] = useState<ExamSummary[]>([]);
  const [students, setStudents] = useState<ClassroomMember[]>([]);
  const [banks, setBanks] = useState<BankSummary[]>([]);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);

  // Chế độ tạo đề theo ma trận
  const [isCreating, setIsCreating] = useState(initialPracticeMaterialId != null);
  const [step, setStep] = useState<1 | 2 | 3 | 4>(1);

  // Bước 1: Thông tin bài kiểm tra
  const [title, setTitle] = useState('');
  const [materialId, setMaterialId] = useState<number | null>(initialPracticeMaterialId);
  const [durationMinutes, setDurationMinutes] = useState(15);
  const [passScore, setPassScore] = useState(50);
  const [assignedStudentId, setAssignedStudentId] = useState<number | null>(null);
  const [assessmentType, setAssessmentType] = useState<'EXAM' | 'PRACTICE'>(initialPracticeMaterialId == null ? 'EXAM' : 'PRACTICE');
  const [maxAttempt, setMaxAttempt] = useState(1);
  const [unlimitedAttempts, setUnlimitedAttempts] = useState(false);
  const [resultReleaseMode, setResultReleaseMode] = useState<'IMMEDIATE' | 'AFTER_CLOSE'>('IMMEDIATE');
  const [answerReleaseMode, setAnswerReleaseMode] = useState<'AFTER_SUBMIT' | 'AFTER_CLOSE' | 'NEVER'>('AFTER_SUBMIT');
  const [shuffleQuestions, setShuffleQuestions] = useState(false);
  const [openAt, setOpenAt] = useState(() => localDateTime(new Date()));
  const [closesAt, setClosesAt] = useState(initialClosesAt);

  // Bước 2: Chọn ngân hàng
  const [selectedBankId, setSelectedBankId] = useState<number | null>(null);
  const [bankTopics, setBankTopics] = useState<TopicSummary[]>([]);

  // Bước 3: Lập ma trận đề
  const [matrixRows, setMatrixRows] = useState<MatrixRow[]>([{ topic: 'ALL', difficulty: undefined, count: 5, points: 1 }]);

  // Bước 4: Xem trước câu hỏi
  const [previewQuestions, setPreviewQuestions] = useState<PickedQuestion[]>([]);
  const [generatingPreview, setGeneratingPreview] = useState(false);

  // Tải danh sách đề thi và học sinh
  useEffect(() => {
    let active = true;
    void Promise.all([getInstructorExams(classId), getInstructorClassMembers(classId, 0), getQuestionBanks(classId)])
      .then(async ([examList, firstMemberPage, bankList]) => {
        const pages = await Promise.all(
          Array.from({ length: Math.max(0, firstMemberPage.totalPages - 1) }, (_, index) => getInstructorClassMembers(classId, index + 1)),
        );
        if (active) {
          setExams(examList);
          setStudents([firstMemberPage, ...pages].flatMap(p => p.items).filter(s => s.status === 'ACTIVE'));
          setBanks(bankList);
          if (bankList.length > 0 && !selectedBankId) {
            setSelectedBankId(bankList[0].id);
          }
        }
      })
      .catch(cause => active && setError(classroomError(cause)));

    return () => {
      active = false;
    };
  }, [classId]);

  // Khi đổi ngân hàng, tải danh sách chủ đề của ngân hàng đó
  useEffect(() => {
    if (selectedBankId) {
      void getBankTopics(selectedBankId)
        .then((t: TopicSummary[]) => setBankTopics(t))
        .catch((cause: unknown) => setError(classroomError(cause)));
    } else {
      setBankTopics([]);
    }
  }, [selectedBankId]);

  const resetWizard = () => {
    setIsCreating(false);
    setStep(1);
    setTitle('');
    setMaterialId(null);
    setDurationMinutes(15);
    setPassScore(50);
    setAssignedStudentId(null);
    setAssessmentType('EXAM');
    setMaxAttempt(1);
    setUnlimitedAttempts(false);
    setResultReleaseMode('IMMEDIATE');
    setAnswerReleaseMode('AFTER_SUBMIT');
    setShuffleQuestions(false);
    setOpenAt(localDateTime(new Date()));
    setClosesAt(initialClosesAt());
    setMatrixRows([{ topic: 'ALL', difficulty: undefined, count: 5, points: 1 }]);
    setPreviewQuestions([]);
  };

  // Chuyển trạng thái Đang mở / Lưu trữ
  const toggleStatus = async (exam: ExamSummary) => {
    setSaving(true);
    setError(null);
    try {
      await updateClassExamStatus(classId, exam.id, exam.status === 'ACTIVE' ? 'ARCHIVED' : 'ACTIVE');
      setExams(await getInstructorExams(classId));
      setNotice(`Đã chuyển trạng thái bài kiểm tra "${exam.title}".`);
    } catch (cause) {
      setError(classroomError(cause));
    } finally {
      setSaving(false);
    }
  };

  // Thêm dòng ma trận
  const addMatrixRow = () => {
    setMatrixRows(prev => [...prev, { topic: 'ALL', difficulty: undefined, count: 5, points: 1 }]);
  };

  // Xóa dòng ma trận
  const removeMatrixRow = (index: number) => {
    setMatrixRows(prev => prev.filter((_, i) => i !== index));
  };

  // Sửa dòng ma trận
  const updateMatrixRow = (index: number, update: Partial<MatrixRow>) => {
    setMatrixRows(prev => prev.map((row, i) => (i === index ? { ...row, ...update } : row)));
  };

  // Tính số câu khả dụng trong ngân hàng cho một điều kiện ma trận
  const getAvailableCount = (topic?: string, difficulty?: number): number => {
    if (!bankTopics || bankTopics.length === 0) return 0;
    const filterTopics = !topic || topic === 'ALL' ? bankTopics : bankTopics.filter(t => t.topic === topic);
    let total = 0;
    for (const t of filterTopics) {
      if (!difficulty) {
        total += t.totalQuestions;
      } else if (difficulty === 1) total += t.difficulty1;
      else if (difficulty === 2) total += t.difficulty2;
      else if (difficulty === 3) total += t.difficulty3;
      else if (difficulty === 4) total += t.difficulty4;
      else if (difficulty === 5) total += t.difficulty5;
    }
    return total;
  };

  // Sinh xem trước bộ câu hỏi từ ma trận
  const handleGeneratePreview = async () => {
    if (!selectedBankId) {
      setError('Vui lòng chọn ngân hàng câu hỏi.');
      return;
    }
    if (matrixRows.length === 0) {
      setError('Vui lòng thêm ít nhất một dòng ma trận đề.');
      return;
    }
    setGeneratingPreview(true);
    setError(null);
    try {
      const picked = await generateExamPreview(classId, {
        bankId: selectedBankId,
        matrix: matrixRows.map(r => ({
          topic: r.topic === 'ALL' ? undefined : r.topic,
          difficulty: r.difficulty || undefined,
          count: r.count,
          points: r.points || 1,
        })),
      });
      setPreviewQuestions(picked);
      setStep(4);
    } catch (cause) {
      setError(classroomError(cause));
    } finally {
      setGeneratingPreview(false);
    }
  };

  // Lưu tạo đề thi từ câu hỏi đã xem trước
  const handleConfirmCreate = async () => {
    if (!selectedBankId) return;
    if (previewQuestions.length === 0) {
      setError('Chưa có câu hỏi nào trong bộ đề.');
      return;
    }
    const opensAtTime = new Date(openAt).getTime();
    const closesAtTime = new Date(closesAt).getTime();
    if (!Number.isFinite(opensAtTime) || !Number.isFinite(closesAtTime) || opensAtTime >= closesAtTime) {
      setError('Giờ đóng đề phải sau giờ mở đề.');
      return;
    }
    if (assessmentType === 'EXAM' && !unlimitedAttempts && (!Number.isInteger(maxAttempt) || maxAttempt < 1 || maxAttempt > 10)) {
      setError('Số lượt làm phải từ 1 đến 10.');
      return;
    }
    setSaving(true);
    setError(null);
    try {
      await createExamFromMatrix(classId, {
        title: title.trim(),
        durationMinutes,
        passScore,
        assignedStudentId,
        materialId: materialId || null,
        bankId: selectedBankId,
        questionIds: previewQuestions.map(q => q.questionId),
        pointsPerQuestion: 1,
        assessmentType,
        maxAttempt: assessmentType === 'PRACTICE' || unlimitedAttempts ? null : maxAttempt,
        resultReleaseMode,
        answerReleaseMode,
        shuffleQuestions,
        openAt: new Date(opensAtTime).toISOString(),
        closesAt: new Date(closesAtTime).toISOString(),
      });
      setExams(await getInstructorExams(classId));
      setNotice(`Đã tạo thành công bài kiểm tra "${title}".`);
      resetWizard();
    } catch (cause) {
      setError(classroomError(cause));
    } finally {
      setSaving(false);
    }
  };

  return (
    <section className="classroom-panel">
      <div className="classroom-header" style={{ marginBottom: '1.25rem' }}>
        <div>
          <h2>Bài kiểm tra theo ma trận</h2>
          <p style={{ color: '#64748b', fontSize: '0.9rem' }}>
            Hệ thống tự động chọn câu hỏi từ ngân hàng dựa trên ma trận chủ đề và độ khó, đảm bảo tính khách quan và khoa học.
          </p>
        </div>
        {!isCreating && (
          <button
            type="button"
            className="btn-pill-primary"
            onClick={() => {
              setError(null);
              setNotice(null);
              setIsCreating(true);
              setStep(1);
            }}
          >
            + Tạo bài kiểm tra từ ma trận
          </button>
        )}
      </div>

      {error && (
        <div className="classroom-error" role="alert" style={{ marginBottom: '1rem' }}>
          {error}
        </div>
      )}
      {notice && (
        <div className="classroom-notice" role="status" style={{ marginBottom: '1rem' }}>
          {notice}
        </div>
      )}

      {/* DANH SÁCH BÀI KIỂM TRA HIỆN TẠI (Khi không ở chế độ tạo đề) */}
      {!isCreating && (
        <>
          {exams.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '2.5rem', background: '#f8fafc', borderRadius: '8px', color: '#64748b' }}>
              <p style={{ fontWeight: 500, margin: 0 }}>Chưa có bài kiểm tra nào trong lớp này.</p>
              <p style={{ fontSize: '0.85rem', margin: '0.5rem 0 0 0' }}>
                Nhấn &quot;+ Tạo bài kiểm tra từ ma trận&quot; để tạo đề kiểm tra đầu tiên từ ngân hàng câu hỏi.
              </p>
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              {exams.map(exam => (
                <div
                  key={exam.id}
                  className="classroom-row"
                  style={{
                    padding: '1rem',
                    background: '#fff',
                    borderRadius: '8px',
                    border: '1px solid #e2e8f0',
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                  }}
                >
                  <div>
                    <strong style={{ fontSize: '1.05rem', color: '#1e293b' }}>{exam.title}</strong>
                    <div style={{ color: '#64748b', fontSize: '0.85rem', marginTop: '0.25rem' }}>
                      {exam.materialId ? (lessons.find(l => l.id === exam.materialId)?.title ?? 'Bài học') : 'Bài kiểm tra chung'} ·{' '}
                      <strong>{exam.questionCount}</strong> câu hỏi · ⏱ <strong>{exam.durationMinutes}</strong> phút · Điểm đạt:{' '}
                      <strong>{exam.passScore}%</strong> ·{' '}
                      <span
                        style={{
                          display: 'inline-block',
                          padding: '0.1rem 0.5rem',
                          borderRadius: '4px',
                          fontSize: '0.75rem',
                          fontWeight: 600,
                          background: exam.status === 'ACTIVE' ? '#dcfce7' : '#f1f5f9',
                          color: exam.status === 'ACTIVE' ? '#15803d' : '#475569',
                        }}
                      >
                        {exam.status === 'ACTIVE' ? 'Đang mở' : 'Đã lưu trữ'}
                      </span>
                    </div>
                  </div>
                  <div className="classroom-member-actions">
                    <button type="button" className="classroom-secondary-action" disabled={saving} onClick={() => void toggleStatus(exam)}>
                      {exam.status === 'ACTIVE' ? 'Lưu trữ' : 'Mở lại'}
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </>
      )}

      {/* WIZARD TẠO ĐỀ THEO MA TRẬN */}
      {isCreating && (
        <div style={{ background: '#fff', border: '1.5px solid #cbd5e1', borderRadius: '10px', padding: '1.5rem', marginTop: '1rem' }}>
          {/* Progress Indicator 4 bước */}
          <div
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              marginBottom: '1.75rem',
              borderBottom: '1px solid #e2e8f0',
              paddingBottom: '1rem',
            }}
          >
            {[
              { num: 1, label: 'Thông tin bài kiểm tra' },
              { num: 2, label: 'Chọn ngân hàng' },
              { num: 3, label: 'Lập ma trận đề' },
              { num: 4, label: 'Xem trước & Tạo đề' },
            ].map(s => (
              <div
                key={s.num}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '0.5rem',
                  color: step === s.num ? '#0284c7' : step > s.num ? '#16a34a' : '#94a3b8',
                  fontWeight: step === s.num ? 700 : 500,
                  fontSize: '0.9rem',
                }}
              >
                <span
                  style={{
                    width: '24px',
                    height: '24px',
                    borderRadius: '50%',
                    display: 'inline-flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    background: step === s.num ? '#0284c7' : step > s.num ? '#16a34a' : '#e2e8f0',
                    color: '#fff',
                    fontSize: '0.8rem',
                  }}
                >
                  {step > s.num ? '✓' : s.num}
                </span>
                <span>{s.label}</span>
              </div>
            ))}
          </div>

          {/* BƯỚC 1: THÔNG TIN BÀI KIỂM TRA */}
          {step === 1 && (
            <div>
              <h3 style={{ marginTop: 0, marginBottom: '1rem', color: '#0f172a', fontSize: '1.2rem' }}>
                Bước 1: Thiết lập thông tin bài kiểm tra
              </h3>

              <div
                style={{
                  display: 'grid',
                  gridTemplateColumns: 'repeat(auto-fit, minmax(340px, 1fr))',
                  gap: '1.25rem',
                  alignItems: 'start',
                  marginBottom: '1.25rem',
                }}
              >
                {/* CỘT TRÁI: THÔNG TIN CHUNG */}
                <div
                  style={{
                    display: 'grid',
                    gap: '0.9rem',
                    padding: '1.1rem',
                    background: '#f8fafc',
                    border: '1px solid #e2e8f0',
                    borderRadius: '10px',
                  }}
                >
                  <label style={{ display: 'flex', flexDirection: 'column', gap: '0.35rem', fontWeight: 600, color: '#1e293b' }}>
                    <span>Tên bài kiểm tra (*):</span>
                    <input
                      type="text"
                      required
                      placeholder="VD: Kiểm tra 15 phút: Đại số chương 1"
                      style={{
                        width: '100%',
                        padding: '0.55rem 0.75rem',
                        borderRadius: '8px',
                        border: '1px solid #cbd5e1',
                        background: '#fff',
                        fontSize: '0.92rem',
                        boxSizing: 'border-box',
                      }}
                      value={title}
                      onChange={e => setTitle(e.target.value)}
                    />
                  </label>

                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, minmax(0, 1fr))', gap: '0.85rem' }}>
                    <label style={{ display: 'flex', flexDirection: 'column', gap: '0.35rem', fontWeight: 600, color: '#1e293b' }}>
                      <span>Gắn với bài học:</span>
                      <select
                        style={{
                          width: '100%',
                          padding: '0.55rem 0.75rem',
                          borderRadius: '8px',
                          border: '1px solid #cbd5e1',
                          background: '#fff',
                          fontSize: '0.92rem',
                          boxSizing: 'border-box',
                        }}
                        value={materialId ?? ''}
                        onChange={e => setMaterialId(e.target.value ? Number(e.target.value) : null)}
                      >
                        <option value="">Bài kiểm tra chung của lớp</option>
                        {lessons.map(l => (
                          <option key={l.id} value={l.id}>
                            {l.title} ({l.fileType})
                          </option>
                        ))}
                      </select>
                    </label>

                    <label style={{ display: 'flex', flexDirection: 'column', gap: '0.35rem', fontWeight: 600, color: '#1e293b' }}>
                      <span>Giao cho học sinh:</span>
                      <select
                        style={{
                          width: '100%',
                          padding: '0.55rem 0.75rem',
                          borderRadius: '8px',
                          border: '1px solid #cbd5e1',
                          background: '#fff',
                          fontSize: '0.92rem',
                          boxSizing: 'border-box',
                        }}
                        value={assignedStudentId ?? ''}
                        onChange={e => setAssignedStudentId(e.target.value ? Number(e.target.value) : null)}
                      >
                        <option value="">Cả lớp ({students.length} học sinh)</option>
                        {students.map(s => (
                          <option key={s.studentId} value={s.studentId}>
                            {s.name} ({s.email})
                          </option>
                        ))}
                      </select>
                    </label>
                  </div>

                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, minmax(0, 1fr))', gap: '0.85rem' }}>
                    <label style={{ display: 'flex', flexDirection: 'column', gap: '0.35rem', fontWeight: 600, color: '#1e293b' }}>
                      <span>Thời gian làm bài (phút):</span>
                      <input
                        type="number"
                        min={1}
                        max={180}
                        style={{
                          width: '100%',
                          padding: '0.55rem 0.75rem',
                          borderRadius: '8px',
                          border: '1px solid #cbd5e1',
                          background: '#fff',
                          fontSize: '0.92rem',
                          boxSizing: 'border-box',
                        }}
                        value={durationMinutes}
                        onChange={e => setDurationMinutes(Number(e.target.value))}
                      />
                    </label>
                    <label style={{ display: 'flex', flexDirection: 'column', gap: '0.35rem', fontWeight: 600, color: '#1e293b' }}>
                      <span>Điểm đạt (%):</span>
                      <input
                        type="number"
                        min={0}
                        max={100}
                        style={{
                          width: '100%',
                          padding: '0.55rem 0.75rem',
                          borderRadius: '8px',
                          border: '1px solid #cbd5e1',
                          background: '#fff',
                          fontSize: '0.92rem',
                          boxSizing: 'border-box',
                        }}
                        value={passScore}
                        onChange={e => setPassScore(Number(e.target.value))}
                      />
                    </label>
                  </div>

                  <label
                    style={{
                      display: 'inline-flex',
                      alignItems: 'center',
                      gap: '0.6rem',
                      padding: '0.6rem 0.8rem',
                      background: '#ffffff',
                      border: '1px solid #cbd5e1',
                      borderRadius: '8px',
                      fontWeight: 600,
                      color: '#1e293b',
                      cursor: 'pointer',
                    }}
                  >
                    <input
                      type="checkbox"
                      style={{ width: '17px', height: '17px', accentColor: '#0284c7', margin: 0, cursor: 'pointer' }}
                      checked={shuffleQuestions}
                      onChange={e => setShuffleQuestions(e.target.checked)}
                    />
                    <span>Trộn câu hỏi ngẫu nhiên giữa các lượt làm</span>
                  </label>
                </div>

                {/* CỘT PHẢI: QUY TẮC & THỜI GIAN MỞ/ĐÓNG */}
                <div
                  style={{
                    display: 'grid',
                    gap: '0.9rem',
                    padding: '1.1rem',
                    background: '#f8fafc',
                    border: '1px solid #e2e8f0',
                    borderRadius: '10px',
                  }}
                >
                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, minmax(0, 1fr))', gap: '0.85rem' }}>
                    <label
                      style={{
                        display: 'flex',
                        flexDirection: 'column',
                        gap: '0.35rem',
                        fontWeight: 600,
                        color: '#1e293b',
                        gridColumn: assessmentType === 'EXAM' ? 'auto' : '1 / -1',
                      }}
                    >
                      <span>Loại bài:</span>
                      <select
                        style={{
                          width: '100%',
                          padding: '0.55rem 0.75rem',
                          borderRadius: '8px',
                          border: '1px solid #cbd5e1',
                          background: '#fff',
                          fontSize: '0.92rem',
                          boxSizing: 'border-box',
                        }}
                        value={assessmentType}
                        onChange={e => {
                          const type = e.target.value as 'EXAM' | 'PRACTICE';
                          setAssessmentType(type);
                          setResultReleaseMode('IMMEDIATE');
                          setAnswerReleaseMode('AFTER_SUBMIT');
                        }}
                      >
                        <option value="EXAM">Kiểm tra</option>
                        <option value="PRACTICE">Luyện tập</option>
                      </select>
                    </label>
                    {assessmentType === 'EXAM' && (
                      <div style={{ display: 'flex', flexDirection: 'column', gap: '0.35rem', fontWeight: 600, color: '#1e293b' }}>
                        <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontWeight: 500 }}>
                          <input type="checkbox" checked={unlimitedAttempts} onChange={e => setUnlimitedAttempts(e.target.checked)} />
                          Không giới hạn lượt làm
                        </label>
                        <label style={{ display: 'flex', flexDirection: 'column', gap: '0.35rem' }}>
                          Số lượt tối đa:
                          <input
                            type="number"
                            min={1}
                            max={10}
                            disabled={unlimitedAttempts}
                            style={{
                              width: '100%',
                              padding: '0.55rem 0.75rem',
                              borderRadius: '8px',
                              border: '1px solid #cbd5e1',
                              background: '#fff',
                              fontSize: '0.92rem',
                              boxSizing: 'border-box',
                            }}
                            value={unlimitedAttempts ? '' : maxAttempt}
                            onChange={e => setMaxAttempt(Number(e.target.value))}
                          />
                        </label>
                      </div>
                    )}
                  </div>

                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, minmax(0, 1fr))', gap: '0.85rem' }}>
                    <label style={{ display: 'flex', flexDirection: 'column', gap: '0.35rem', fontWeight: 600, color: '#1e293b' }}>
                      <span>Mở đề lúc:</span>
                      <input
                        type="datetime-local"
                        style={{
                          width: '100%',
                          padding: '0.55rem 0.75rem',
                          borderRadius: '8px',
                          border: '1px solid #cbd5e1',
                          background: '#fff',
                          fontSize: '0.92rem',
                          boxSizing: 'border-box',
                        }}
                        value={openAt}
                        onChange={e => setOpenAt(e.target.value)}
                      />
                    </label>
                    <label style={{ display: 'flex', flexDirection: 'column', gap: '0.35rem', fontWeight: 600, color: '#1e293b' }}>
                      <span>Đóng đề lúc:</span>
                      <input
                        type="datetime-local"
                        style={{
                          width: '100%',
                          padding: '0.55rem 0.75rem',
                          borderRadius: '8px',
                          border: '1px solid #cbd5e1',
                          background: '#fff',
                          fontSize: '0.92rem',
                          boxSizing: 'border-box',
                        }}
                        value={closesAt}
                        onChange={e => setClosesAt(e.target.value)}
                      />
                    </label>
                  </div>

                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(2, minmax(0, 1fr))', gap: '0.85rem' }}>
                    <label style={{ display: 'flex', flexDirection: 'column', gap: '0.35rem', fontWeight: 600, color: '#1e293b' }}>
                      <span>Công bố điểm:</span>
                      <select
                        style={{
                          width: '100%',
                          padding: '0.55rem 0.75rem',
                          borderRadius: '8px',
                          border: '1px solid #cbd5e1',
                          background: '#fff',
                          fontSize: '0.92rem',
                          boxSizing: 'border-box',
                        }}
                        value={resultReleaseMode}
                        onChange={e => setResultReleaseMode(e.target.value as 'IMMEDIATE' | 'AFTER_CLOSE')}
                      >
                        <option value="IMMEDIATE">Ngay sau khi nộp</option>
                        {assessmentType === 'EXAM' && <option value="AFTER_CLOSE">Sau khi đóng đề</option>}
                      </select>
                    </label>
                    <label style={{ display: 'flex', flexDirection: 'column', gap: '0.35rem', fontWeight: 600, color: '#1e293b' }}>
                      <span>Công bố đáp án:</span>
                      <select
                        style={{
                          width: '100%',
                          padding: '0.55rem 0.75rem',
                          borderRadius: '8px',
                          border: '1px solid #cbd5e1',
                          background: '#fff',
                          fontSize: '0.92rem',
                          boxSizing: 'border-box',
                        }}
                        value={answerReleaseMode}
                        onChange={e => setAnswerReleaseMode(e.target.value as 'AFTER_SUBMIT' | 'AFTER_CLOSE' | 'NEVER')}
                      >
                        <option value="AFTER_SUBMIT">Ngay sau khi nộp</option>
                        {assessmentType === 'EXAM' && <option value="AFTER_CLOSE">Sau khi đóng đề</option>}
                        {assessmentType === 'EXAM' && <option value="NEVER">Không công bố</option>}
                      </select>
                    </label>
                  </div>
                </div>
              </div>

              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <button type="button" className="classroom-secondary-action" onClick={resetWizard}>
                  Hủy bỏ
                </button>
                <button type="button" className="btn-pill-primary" disabled={!title.trim()} onClick={() => setStep(2)}>
                  Tiếp tục: Chọn ngân hàng câu hỏi →
                </button>
              </div>
            </div>
          )}

          {/* BƯỚC 2: CHỌN NGÂN HÀNG */}
          {step === 2 && (
            <div style={{ maxWidth: '640px' }}>
              <h3 style={{ marginTop: 0 }}>Bước 2: Chọn ngân hàng câu hỏi nguồn</h3>
              <p style={{ color: '#64748b', fontSize: '0.875rem' }}>
                Chọn ngân hàng chứa các câu hỏi bạn muốn hệ thống tự động bốc ngẫu nhiên theo ma trận.
              </p>

              {banks.length === 0 ? (
                <div
                  style={{
                    padding: '1.5rem',
                    background: '#fef2f2',
                    border: '1px solid #fecaca',
                    borderRadius: '8px',
                    marginBottom: '1.5rem',
                  }}
                >
                  <p style={{ color: '#b91c1c', margin: 0, fontWeight: 600 }}>Chưa có ngân hàng câu hỏi nào!</p>
                  <p style={{ color: '#991b1b', margin: '0.5rem 0 0 0', fontSize: '0.875rem' }}>
                    Vui lòng qua tab &quot;Ngân hàng câu hỏi&quot; để tạo ngân hàng và thêm hoặc nhập câu hỏi từ Excel trước khi lập ma trận
                    đề.
                  </p>
                </div>
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', marginBottom: '1.5rem' }}>
                  {banks.map(b => (
                    <label
                      key={b.id}
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '1rem',
                        padding: '1rem',
                        borderRadius: '8px',
                        border: selectedBankId === b.id ? '2px solid #0284c7' : '1px solid #cbd5e1',
                        background: selectedBankId === b.id ? '#f0f9ff' : '#fff',
                        cursor: 'pointer',
                      }}
                    >
                      <input type="radio" name="selectedBank" checked={selectedBankId === b.id} onChange={() => setSelectedBankId(b.id)} />
                      <div style={{ flex: 1 }}>
                        <strong style={{ fontSize: '1rem', color: '#1e293b' }}>{b.name}</strong>
                        <div style={{ color: '#64748b', fontSize: '0.85rem' }}>
                          Số câu hỏi có sẵn: <strong>{b.questionCount}</strong> câu · Phạm vi:{' '}
                          {b.classId ? `Riêng lớp "${b.className}"` : 'Dùng chung cho các lớp'}
                        </div>
                      </div>
                    </label>
                  ))}
                </div>
              )}

              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <button type="button" className="classroom-secondary-action" onClick={() => setStep(1)}>
                  ← Quay lại Bước 1
                </button>
                <button
                  type="button"
                  className="btn-pill-primary"
                  disabled={!selectedBankId || banks.length === 0}
                  onClick={() => setStep(3)}
                >
                  Tiếp tục: Lập ma trận đề →
                </button>
              </div>
            </div>
          )}

          {/* BƯỚC 3: LẬP MA TRẬN ĐỀ */}
          {step === 3 && (
            <div>
              <h3 style={{ marginTop: 0 }}>Bước 3: Lập ma trận cấu trúc đề thi</h3>
              <p style={{ color: '#64748b', fontSize: '0.875rem' }}>
                Quy định số lượng câu hỏi theo từng chủ đề và độ khó. Hệ thống sẽ kiểm tra số câu khả dụng trong ngân hàng và tự động bốc
                câu hỏi ngẫu nhiên.
              </p>

              <div style={{ overflowX: 'auto', marginBottom: '1rem' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                  <thead>
                    <tr style={{ background: '#f8fafc', borderBottom: '2px solid #cbd5e1' }}>
                      <th style={{ padding: '0.75rem', fontWeight: 600 }}>Chủ đề</th>
                      <th style={{ padding: '0.75rem', fontWeight: 600 }}>Độ khó</th>
                      <th style={{ padding: '0.75rem', fontWeight: 600 }}>Khả dụng trong kho</th>
                      <th style={{ padding: '0.75rem', fontWeight: 600 }}>Số câu cần lấy (*)</th>
                      <th style={{ padding: '0.75rem', fontWeight: 600, width: '60px' }}>Xóa</th>
                    </tr>
                  </thead>
                  <tbody>
                    {matrixRows.map((row, idx) => {
                      const avail = getAvailableCount(row.topic, row.difficulty);
                      const isOver = row.count > avail;
                      return (
                        <tr key={idx} style={{ borderBottom: '1px solid #e2e8f0' }}>
                          <td style={{ padding: '0.75rem' }}>
                            <select
                              style={{ padding: '0.45rem', borderRadius: '4px', border: '1px solid #cbd5e1', width: '100%' }}
                              value={row.topic || 'ALL'}
                              onChange={e => updateMatrixRow(idx, { topic: e.target.value })}
                            >
                              <option value="ALL">Tất cả chủ đề</option>
                              {bankTopics.map(t => (
                                <option key={t.topic} value={t.topic}>
                                  {t.topic} ({t.totalQuestions} câu)
                                </option>
                              ))}
                            </select>
                          </td>
                          <td style={{ padding: '0.75rem' }}>
                            <select
                              style={{ padding: '0.45rem', borderRadius: '4px', border: '1px solid #cbd5e1', width: '100%' }}
                              value={row.difficulty ?? ''}
                              onChange={e => updateMatrixRow(idx, { difficulty: e.target.value ? Number(e.target.value) : undefined })}
                            >
                              <option value="">Bất kỳ độ khó nào</option>
                              <option value="1">1 - Nhận biết</option>
                              <option value="2">2 - Thông hiểu</option>
                              <option value="3">3 - Vận dụng</option>
                              <option value="4">4 - Vận dụng cao</option>
                              <option value="5">5 - Nâng cao</option>
                            </select>
                          </td>
                          <td style={{ padding: '0.75rem' }}>
                            <span
                              style={{
                                fontWeight: 700,
                                color: avail === 0 ? '#ef4444' : '#16a34a',
                              }}
                            >
                              {avail} câu
                            </span>
                          </td>
                          <td style={{ padding: '0.75rem' }}>
                            <input
                              type="number"
                              min={1}
                              max={Math.max(1, avail)}
                              style={{
                                width: '90px',
                                padding: '0.45rem',
                                borderRadius: '4px',
                                border: isOver ? '1.5px solid #ef4444' : '1px solid #cbd5e1',
                                background: isOver ? '#fef2f2' : '#fff',
                              }}
                              value={row.count}
                              onChange={e => updateMatrixRow(idx, { count: Math.max(1, Number(e.target.value)) })}
                            />
                            {isOver && (
                              <div style={{ color: '#ef4444', fontSize: '0.75rem', marginTop: '0.2rem' }}>Vượt quá kho ({avail} câu)</div>
                            )}
                          </td>
                          <td style={{ padding: '0.75rem' }}>
                            {matrixRows.length > 1 && (
                              <button
                                type="button"
                                style={{ color: '#ef4444', background: 'none', border: 'none', cursor: 'pointer', fontSize: '1.1rem' }}
                                onClick={() => removeMatrixRow(idx)}
                                title="Xóa dòng"
                              >
                                ✕
                              </button>
                            )}
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>

              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
                <button type="button" className="classroom-secondary-action" onClick={addMatrixRow}>
                  + Thêm dòng ma trận
                </button>
                <div style={{ fontSize: '0.95rem', fontWeight: 600, color: '#334155' }}>
                  Tổng số câu dự kiến: <strong>{matrixRows.reduce((sum, r) => sum + (r.count || 0), 0)}</strong> câu
                </div>
              </div>

              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <button type="button" className="classroom-secondary-action" onClick={() => setStep(2)}>
                  ← Quay lại Bước 2
                </button>
                <button
                  type="button"
                  className="btn-pill-primary"
                  disabled={
                    generatingPreview ||
                    matrixRows.some(r => r.count > getAvailableCount(r.topic, r.difficulty)) ||
                    matrixRows.reduce((sum, r) => sum + (r.count || 0), 0) === 0
                  }
                  onClick={() => void handleGeneratePreview()}
                >
                  {generatingPreview ? 'Đang bốc câu hỏi từ ngân hàng...' : 'Xem trước bộ câu hỏi →'}
                </button>
              </div>
            </div>
          )}

          {/* BƯỚC 4: XEM TRƯỚC VÀ XÁC NHẬN TẠO ĐỀ */}
          {step === 4 && (
            <div>
              <div
                style={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  marginBottom: '1rem',
                  flexWrap: 'wrap',
                  gap: '0.5rem',
                }}
              >
                <div>
                  <h3 style={{ margin: 0 }}>Bước 4: Xem trước câu hỏi đã bốc</h3>
                  <p style={{ color: '#64748b', fontSize: '0.85rem', margin: '0.25rem 0 0 0' }}>
                    Tổng cộng: <strong>{previewQuestions.length}</strong> câu hỏi đã được hệ thống chọn ngẫu nhiên thỏa mãn ma trận.
                  </p>
                </div>
                <button
                  type="button"
                  className="classroom-secondary-action"
                  disabled={generatingPreview || saving}
                  onClick={() => void handleGeneratePreview()}
                  title="Bốc ngẫu nhiên một bộ câu hỏi khác"
                >
                  {generatingPreview ? 'Đang lấy bộ khác...' : '🔄 Lấy lại bộ câu hỏi khác'}
                </button>
              </div>

              {/* Danh sách câu hỏi xem trước */}
              <div
                style={{
                  display: 'flex',
                  flexDirection: 'column',
                  gap: '0.75rem',
                  maxHeight: '55vh',
                  overflowY: 'auto',
                  marginBottom: '1.5rem',
                  paddingRight: '0.5rem',
                }}
              >
                {previewQuestions.map((q, idx) => (
                  <div
                    key={q.questionId}
                    style={{
                      border: '1px solid #e2e8f0',
                      borderRadius: '8px',
                      padding: '0.85rem 1rem',
                      background: '#f8fafc',
                    }}
                  >
                    <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center', marginBottom: '0.4rem' }}>
                      <strong style={{ color: '#0284c7' }}>Câu {idx + 1}.</strong>
                      <span
                        style={{
                          background: '#e0f2fe',
                          color: '#0369a1',
                          padding: '0.1rem 0.4rem',
                          borderRadius: '4px',
                          fontSize: '0.75rem',
                          fontWeight: 600,
                        }}
                      >
                        {q.topic}
                      </span>
                      <span
                        style={{
                          background: '#f1f5f9',
                          color: '#475569',
                          padding: '0.1rem 0.4rem',
                          borderRadius: '4px',
                          fontSize: '0.75rem',
                        }}
                      >
                        {difficultyLabels[q.difficulty] || `Độ khó ${q.difficulty}`}
                      </span>
                    </div>

                    <p style={{ margin: '0.3rem 0 0.6rem 0', fontWeight: 500, color: '#1e293b' }}>{q.content}</p>

                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '0.4rem' }}>
                      {q.options.map((opt, oIdx) => {
                        const letter = String.fromCharCode(65 + oIdx);
                        const isCorrect = oIdx === q.correctIndex;
                        return (
                          <div
                            key={oIdx}
                            style={{
                              padding: '0.35rem 0.6rem',
                              borderRadius: '4px',
                              border: isCorrect ? '1.5px solid #16a34a' : '1px solid #e2e8f0',
                              background: isCorrect ? '#f0fdf4' : '#fff',
                              color: isCorrect ? '#15803d' : '#334155',
                              fontSize: '0.85rem',
                              display: 'flex',
                              alignItems: 'center',
                              gap: '0.35rem',
                            }}
                          >
                            <strong>{letter}.</strong>
                            <span>{opt}</span>
                            {isCorrect && <span style={{ marginLeft: 'auto', fontWeight: 700 }}>✓</span>}
                          </div>
                        );
                      })}
                    </div>
                  </div>
                ))}
              </div>

              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <button type="button" className="classroom-secondary-action" disabled={saving} onClick={() => setStep(3)}>
                  ← Quay lại sửa ma trận
                </button>
                <div style={{ display: 'flex', gap: '0.5rem' }}>
                  <button type="button" className="classroom-secondary-action" disabled={saving} onClick={resetWizard}>
                    Hủy bỏ
                  </button>
                  <button
                    type="button"
                    className="btn-pill-primary"
                    disabled={saving || previewQuestions.length === 0}
                    onClick={() => void handleConfirmCreate()}
                  >
                    {saving ? 'Đang tạo bài kiểm tra...' : 'Xác nhận tạo bài kiểm tra'}
                  </button>
                </div>
              </div>
            </div>
          )}
        </div>
      )}
    </section>
  );
};

export default ClassExams;
