import React, { useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router';
import {
  approveClassMember,
  classroomError,
  createClassTask,
  getInstructorClassMembers,
  getInstructorClassroom,
  rejectClassMember,
  reorderClassMaterials,
  removeClassMember,
  uploadClassMaterial,
  type Classroom,
  type ClassroomMemberPage,
} from 'app/shared/util/classroom-api';
import 'app/modules/student/classroom.scss';
import ClassExams from './components/class-exams';
import ClassProgress from './components/class-progress';
import ClassAssignmentReview from './components/class-assignment-review';
import QuestionBankView from './components/question-bank';
import InstructorPagination from './components/instructor-pagination';
import ClassroomMediaPreview from 'app/shared/components/classroom-media-preview';
import { getInstructorExams, type ExamSummary } from 'app/shared/util/class-exam-api';

type Tab = 'overview' | 'students' | 'lessons' | 'assignments' | 'exams' | 'banks' | 'progress';

export const InstructorClassroom: React.FC = () => {
  const { id } = useParams();
  const classId = Number(id);
  const navigate = useNavigate();
  const [room, setRoom] = useState<Classroom | null>(null);
  const [members, setMembers] = useState<ClassroomMemberPage | null>(null);
  const [memberPage, setMemberPage] = useState(0);
  const [tab, setTab] = useState<Tab>('overview');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [pendingRemoval, setPendingRemoval] = useState<{ id: number; pending: boolean } | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [title, setTitle] = useState('');
  const [instructions, setInstructions] = useState('');
  const [dueAt, setDueAt] = useState('');
  const [assignmentFile, setAssignmentFile] = useState<File | null>(null);
  const [lessonTitle, setLessonTitle] = useState('');
  const [lessonFile, setLessonFile] = useState<File | null>(null);
  const [selectedAssignmentId, setSelectedAssignmentId] = useState<number | null>(null);
  const [practiceMaterialId, setPracticeMaterialId] = useState<number | null>(null);
  const [lessonPractices, setLessonPractices] = useState<ExamSummary[]>([]);
  const [practiceError, setPracticeError] = useState<string | null>(null);

  const load = useCallback(
    async (requestedPage: number) => {
      if (!Number.isSafeInteger(classId) || classId <= 0) {
        setError('Mã lớp không hợp lệ.');
        setLoading(false);
        return;
      }
      setLoading(true);
      try {
        const [classroom, memberResult] = await Promise.all([
          getInstructorClassroom(classId),
          getInstructorClassMembers(classId, requestedPage),
        ]);
        setRoom(classroom);
        if (requestedPage > 0 && memberResult.items.length === 0) {
          const previous = await getInstructorClassMembers(classId, requestedPage - 1);
          setMembers(previous);
          setMemberPage(previous.page);
        } else {
          setMembers(memberResult);
          setMemberPage(memberResult.page);
        }
        setError(null);
      } catch (cause) {
        setError(classroomError(cause));
      } finally {
        setLoading(false);
      }
    },
    [classId],
  );

  useEffect(() => {
    void load(0);
  }, [load]);

  useEffect(() => {
    if (tab !== 'lessons' || !Number.isSafeInteger(classId) || classId <= 0) return;
    let active = true;
    void getInstructorExams(classId)
      .then(exams => {
        if (active) {
          setLessonPractices(exams.filter(exam => exam.assessmentType === 'PRACTICE' && exam.materialId != null));
          setPracticeError(null);
        }
      })
      .catch(cause => active && setPracticeError(classroomError(cause)));
    return () => {
      active = false;
    };
  }, [classId, tab]);

  const approve = async (memberId: number) => {
    setSaving(true);
    try {
      await approveClassMember(classId, memberId);
      await load(memberPage);
    } catch (cause) {
      setError(classroomError(cause));
    } finally {
      setSaving(false);
    }
  };

  const remove = async (memberId: number, pending: boolean) => {
    setSaving(true);
    try {
      if (pending) await rejectClassMember(classId, memberId);
      else await removeClassMember(classId, memberId);
      setPendingRemoval(null);
      await load(memberPage);
    } catch (cause) {
      setError(classroomError(cause));
    } finally {
      setSaving(false);
    }
  };

  const assign = async (event: React.FormEvent) => {
    event.preventDefault();
    const form = event.currentTarget as HTMLFormElement;
    if (!title.trim()) return;
    if (assignmentFile) {
      const limit = /\.pdf$/i.test(assignmentFile.name) ? 25 : /\.mp4$/i.test(assignmentFile.name) ? 500 : 0;
      if (!limit || !assignmentFile.size || assignmentFile.size > limit * 1024 * 1024) {
        setError('Tệp bài tập cần là PDF tối đa 25 MB hoặc MP4 tối đa 500 MB.');
        return;
      }
    }
    setSaving(true);
    try {
      await createClassTask(
        classId,
        {
          title: title.trim(),
          instructions: instructions.trim(),
          dueAt: dueAt ? new Date(dueAt).toISOString() : null,
        },
        assignmentFile,
      );
      setTitle('');
      setInstructions('');
      setDueAt('');
      setAssignmentFile(null);
      form.reset();
      await load(memberPage);
      setTab('assignments');
    } catch (cause) {
      setError(classroomError(cause));
    } finally {
      setSaving(false);
    }
  };

  const uploadLesson = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!lessonTitle.trim() || !lessonFile) return;
    const limit = /\.pdf$/i.test(lessonFile.name) ? 25 : /\.mp4$/i.test(lessonFile.name) ? 500 : 0;
    if (!limit || !lessonFile.size || lessonFile.size > limit * 1024 * 1024) {
      setError('Bài học cần là PDF tối đa 25 MB hoặc MP4 tối đa 500 MB.');
      return;
    }
    setSaving(true);
    try {
      await uploadClassMaterial(classId, lessonTitle.trim(), lessonFile);
      setLessonTitle('');
      setLessonFile(null);
      await load(memberPage);
    } catch (cause) {
      setError(classroomError(cause));
    } finally {
      setSaving(false);
    }
  };

  const moveLesson = async (index: number, offset: number) => {
    if (!room) return;
    const ids = room.lessons.map(item => item.id);
    [ids[index], ids[index + offset]] = [ids[index + offset], ids[index]];
    setSaving(true);
    try {
      await reorderClassMaterials(classId, ids);
      await load(memberPage);
    } catch (cause) {
      setError(classroomError(cause));
    } finally {
      setSaving(false);
    }
  };

  return (
    <main className="classroom-page">
      <button type="button" className="classroom-back" onClick={() => navigate('/instructor/classes')}>
        ← Danh sách lớp
      </button>
      {loading && <p>Đang tải lớp học...</p>}
      {error && (
        <p className="classroom-error" role="alert">
          {error}
        </p>
      )}
      {room && (
        <>
          <header className="classroom-header classroom-hero">
            <div>
              <span className="classroom-eyebrow">KHÔNG GIAN GIẢNG DẠY</span>
              <h1>{room.name}</h1>
              <p>{room.description || 'Chưa có mô tả.'}</p>
            </div>
            <div className="classroom-hero-code">
              <span>MÃ THAM GIA</span>
              <strong>{room.joinCode || 'Chưa có mã'}</strong>
            </div>
          </header>
          <nav className="classroom-tabs classroom-main-tabs" aria-label="Nội dung lớp">
            <button type="button" className={tab === 'overview' ? 'active' : ''} onClick={() => setTab('overview')}>
              Tổng quan
            </button>
            <button type="button" className={tab === 'students' ? 'active' : ''} onClick={() => setTab('students')}>
              Học sinh ({members?.activeCount ?? 0})
            </button>
            <button type="button" className={tab === 'lessons' ? 'active' : ''} onClick={() => setTab('lessons')}>
              Danh sách bài ({room.lessons.length})
            </button>
            <button type="button" className={tab === 'assignments' ? 'active' : ''} onClick={() => setTab('assignments')}>
              Giao việc ({room.assignments.length})
            </button>
            <button
              type="button"
              className={tab === 'exams' ? 'active' : ''}
              onClick={() => {
                setPracticeMaterialId(null);
                setTab('exams');
              }}
            >
              Bài kiểm tra
            </button>
            <button type="button" className={tab === 'banks' ? 'active' : ''} onClick={() => setTab('banks')}>
              Ngân hàng câu hỏi
            </button>
            <button type="button" className={tab === 'progress' ? 'active' : ''} onClick={() => setTab('progress')}>
              Tiến độ
            </button>
          </nav>
          {tab === 'overview' && (
            <>
              <div className="classroom-metric-grid">
                <div className="classroom-metric-card">
                  <span>Học sinh</span>
                  <strong>{members?.activeCount ?? 0}</strong>
                  <small>{members?.pendingCount ?? 0} đang chờ duyệt</small>
                </div>
                <div className="classroom-metric-card">
                  <span>Bài học</span>
                  <strong>{room.lessons.length}</strong>
                  <small>Video và tài liệu PDF</small>
                </div>
                <div className="classroom-metric-card">
                  <span>Bài được giao</span>
                  <strong>{room.assignments.length}</strong>
                  <small>Trong lớp học này</small>
                </div>
              </div>
              <section className="classroom-panel">
                <div className="classroom-section-heading">
                  <div>
                    <h2>Quản lý lớp nhanh</h2>
                    <p>Chọn nội dung bạn muốn cập nhật hôm nay.</p>
                  </div>
                </div>
                <div className="classroom-shortcuts">
                  <button type="button" onClick={() => setTab('students')}>
                    <span>01</span>
                    <strong>Học sinh</strong>
                    <small>Duyệt và quản lý thành viên</small>
                  </button>
                  <button type="button" onClick={() => setTab('lessons')}>
                    <span>02</span>
                    <strong>Bài học</strong>
                    <small>Tải lên và sắp xếp tài liệu</small>
                  </button>
                  <button type="button" onClick={() => setTab('assignments')}>
                    <span>03</span>
                    <strong>Bài tập</strong>
                    <small>Giao việc và đặt hạn nộp</small>
                  </button>
                  <button type="button" onClick={() => setTab('progress')}>
                    <span>04</span>
                    <strong>Tiến độ</strong>
                    <small>Xem điểm và học sinh cần hỗ trợ</small>
                  </button>
                </div>
              </section>
              <div className="classroom-overview-grid">
                <section className="classroom-panel">
                  <h2>Bài học gần đây</h2>
                  {room.lessons.length === 0 && <p>Chưa có bài học.</p>}
                  {room.lessons
                    .slice(-3)
                    .reverse()
                    .map(lesson => (
                      <div className="classroom-row" key={lesson.id}>
                        <div>
                          <strong>{lesson.title}</strong>
                          <small>{lesson.fileType}</small>
                        </div>
                      </div>
                    ))}
                </section>
                <section className="classroom-panel">
                  <h2>Bài tập đã giao</h2>
                  {room.assignments.length === 0 && <p>Chưa có bài tập.</p>}
                  {room.assignments.slice(0, 3).map(task => (
                    <div className="classroom-row" key={task.id}>
                      <div>
                        <strong>{task.title}</strong>
                        <small>{task.dueAt ? `Hạn ${new Date(task.dueAt).toLocaleString('vi-VN')}` : 'Không có hạn'}</small>
                      </div>
                    </div>
                  ))}
                </section>
              </div>
            </>
          )}
          {tab === 'students' && (
            <section className="classroom-panel">
              <h2>Học sinh trong lớp</h2>
              <p className="classroom-member-summary">
                {members?.activeCount ?? 0} đang học · {members?.pendingCount ?? 0} chờ duyệt
              </p>
              {members?.totalElements === 0 && <p>Chưa có học sinh tham gia.</p>}
              {members?.items.map(member => (
                <div className="classroom-row" key={member.id}>
                  <div>
                    <strong>{member.name}</strong>
                    <small>{member.email}</small>
                  </div>
                  <div className="classroom-member-actions">
                    <span>{member.status === 'ACTIVE' ? 'Đã tham gia' : 'Chờ duyệt'}</span>
                    {member.status === 'PENDING' && (
                      <>
                        <button type="button" disabled={saving} onClick={() => void approve(member.id)}>
                          Duyệt
                        </button>
                        {pendingRemoval?.id === member.id && pendingRemoval.pending ? (
                          <>
                            <button
                              type="button"
                              className="classroom-danger-action"
                              disabled={saving}
                              onClick={() => void remove(member.id, true)}
                            >
                              Xác nhận từ chối
                            </button>
                            <button
                              type="button"
                              className="classroom-secondary-action"
                              disabled={saving}
                              onClick={() => setPendingRemoval(null)}
                            >
                              Hủy
                            </button>
                          </>
                        ) : (
                          <button
                            type="button"
                            className="classroom-secondary-action"
                            disabled={saving}
                            onClick={() => setPendingRemoval({ id: member.id, pending: true })}
                          >
                            Từ chối
                          </button>
                        )}
                      </>
                    )}
                    {member.status === 'ACTIVE' &&
                      (pendingRemoval?.id === member.id && !pendingRemoval.pending ? (
                        <>
                          <button
                            type="button"
                            className="classroom-danger-action"
                            disabled={saving}
                            onClick={() => void remove(member.id, false)}
                          >
                            Xác nhận đưa ra
                          </button>
                          <button
                            type="button"
                            className="classroom-secondary-action"
                            disabled={saving}
                            onClick={() => setPendingRemoval(null)}
                          >
                            Hủy
                          </button>
                        </>
                      ) : (
                        <button
                          type="button"
                          className="classroom-danger-action"
                          disabled={saving}
                          onClick={() => setPendingRemoval({ id: member.id, pending: false })}
                        >
                          Đưa ra khỏi lớp
                        </button>
                      ))}
                  </div>
                </div>
              ))}
              {members && members.totalPages > 1 && (
                <InstructorPagination
                  page={memberPage}
                  totalPages={members.totalPages}
                  totalItems={members.totalElements}
                  itemLabel="học sinh"
                  label="Phân trang học sinh"
                  disabled={loading || saving}
                  onPageChange={next => void load(next)}
                />
              )}
            </section>
          )}
          {tab === 'lessons' && (
            <section className="classroom-panel">
              <h2>Danh sách bài</h2>
              {practiceError && (
                <p className="classroom-error" role="alert">
                  {practiceError}
                </p>
              )}
              <form className="classroom-task-form" onSubmit={event => void uploadLesson(event)}>
                <label>
                  Tên bài học
                  <input value={lessonTitle} maxLength={255} required onChange={event => setLessonTitle(event.target.value)} />
                </label>
                <label>
                  Video MP4 hoặc tài liệu PDF
                  <input
                    type="file"
                    accept="video/mp4,application/pdf,.mp4,.pdf"
                    required
                    onChange={event => setLessonFile(event.target.files?.[0] ?? null)}
                  />
                </label>
                <button type="submit" disabled={saving || !lessonFile}>
                  {saving ? 'Đang tải lên...' : 'Tải bài học lên'}
                </button>
              </form>
              {room.lessons.length === 0 && <p>Chưa có tài liệu bài học trong lớp.</p>}
              {room.lessons.map((lesson, index) => (
                <div className="classroom-row" key={lesson.id}>
                  <div>
                    <strong>
                      {index + 1}. {lesson.title}
                    </strong>
                    <small>{lesson.fileType}</small>
                  </div>
                  <div className="classroom-member-actions">
                    <button type="button" disabled={saving || index === 0} onClick={() => void moveLesson(index, -1)}>
                      ↑
                    </button>
                    <button type="button" disabled={saving || index + 1 === room.lessons.length} onClick={() => void moveLesson(index, 1)}>
                      ↓
                    </button>
                  </div>
                  <ClassroomMediaPreview
                    classId={classId}
                    itemId={lesson.id}
                    kind="lesson"
                    fileType={lesson.fileType}
                    title={lesson.title}
                  />
                  <div className="classroom-lesson-practice">
                    <div className="classroom-lesson-practice-heading">
                      <strong>Bài tập sau bài học</strong>
                      <button
                        type="button"
                        className="classroom-secondary-action"
                        onClick={() => {
                          setPracticeMaterialId(lesson.id);
                          setTab('exams');
                        }}
                      >
                        + Tạo bài luyện
                      </button>
                    </div>
                    {lessonPractices
                      .filter(exam => exam.materialId === lesson.id)
                      .map(exam => (
                        <div className="classroom-lesson-practice-item" key={exam.id}>
                          <span>
                            {exam.title} · {exam.questionCount} câu
                          </span>
                          <small>{exam.status === 'ACTIVE' ? 'Đang mở' : 'Đã lưu trữ'}</small>
                        </div>
                      ))}
                    {!practiceError && !lessonPractices.some(exam => exam.materialId === lesson.id) && (
                      <small>Chưa có bài tập cho bài học này.</small>
                    )}
                  </div>
                </div>
              ))}
            </section>
          )}
          {tab === 'assignments' && (
            <section className="classroom-panel">
              <div className="classroom-section-heading">
                <div>
                  <h2>Giao bài tập cho lớp</h2>
                  <p>Tạo nhiệm vụ mới và theo dõi bài đã giao.</p>
                </div>
              </div>
              <form className="classroom-task-form" onSubmit={event => void assign(event)}>
                <label>
                  Tên bài tập
                  <input value={title} maxLength={255} required onChange={event => setTitle(event.target.value)} />
                </label>
                <label>
                  Hướng dẫn
                  <textarea value={instructions} maxLength={5000} onChange={event => setInstructions(event.target.value)} />
                </label>
                <label>
                  Hạn nộp
                  <input type="datetime-local" value={dueAt} onChange={event => setDueAt(event.target.value)} />
                </label>
                <label>
                  Tệp bài tập (PDF hoặc MP4, tùy chọn)
                  <input
                    type="file"
                    accept=".pdf,.mp4,application/pdf,video/mp4"
                    onChange={event => setAssignmentFile(event.target.files?.[0] ?? null)}
                  />
                </label>
                <button type="submit" disabled={saving}>
                  {saving ? 'Đang giao...' : 'Giao cho cả lớp'}
                </button>
              </form>
              <h2>Bài đã giao</h2>
              {room.assignments.length === 0 && <p>Chưa có bài tập nào.</p>}
              {room.assignments.map(task => (
                <div className="classroom-row" key={task.id}>
                  <div>
                    <strong>{task.title}</strong>
                    <small>{task.instructions}</small>
                    {task.fileName && <small>Đính kèm: {task.fileName}</small>}
                  </div>
                  <div className="classroom-member-actions">
                    <span>{task.dueAt ? `Hạn: ${new Date(task.dueAt).toLocaleString('vi-VN')}` : 'Không có hạn'}</span>
                    <button type="button" className="classroom-secondary-action" onClick={() => setSelectedAssignmentId(task.id)}>
                      Xem bài nộp
                    </button>
                  </div>
                </div>
              ))}
              {room.assignments
                .filter(task => task.id === selectedAssignmentId)
                .map(task => (
                  <div className="classroom-task-workspace" key={task.id}>
                    <div className="classroom-section-heading">
                      <div>
                        <h3>Bài nộp: {task.title}</h3>
                        <p>{task.instructions || 'Không có hướng dẫn thêm.'}</p>
                        {task.fileType && (
                          <ClassroomMediaPreview
                            classId={classId}
                            itemId={task.id}
                            kind="assignment"
                            fileType={task.fileType}
                            title={task.title}
                          />
                        )}
                      </div>
                      <button type="button" className="classroom-outline-button" onClick={() => setSelectedAssignmentId(null)}>
                        Đóng
                      </button>
                    </div>
                    <ClassAssignmentReview classId={classId} assignmentId={task.id} />
                  </div>
                ))}
            </section>
          )}
          {tab === 'exams' && <ClassExams classId={classId} lessons={room.lessons} initialPracticeMaterialId={practiceMaterialId} />}
          {tab === 'banks' && <QuestionBankView classId={classId} />}
          {tab === 'progress' && <ClassProgress classId={classId} />}
        </>
      )}
    </main>
  );
};

export default InstructorClassroom;
