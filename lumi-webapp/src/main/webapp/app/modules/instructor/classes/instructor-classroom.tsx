import React, { useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router';
import {
  approveClassMember,
  classroomError,
  createClassTask,
  getInstructorClassMembers,
  getInstructorClassroom,
  openClassMaterial,
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
import QuestionBankView from './components/question-bank';

type Tab = 'students' | 'lessons' | 'assignments' | 'exams' | 'banks' | 'progress';

export const InstructorClassroom: React.FC = () => {
  const { id } = useParams();
  const classId = Number(id);
  const navigate = useNavigate();
  const [room, setRoom] = useState<Classroom | null>(null);
  const [members, setMembers] = useState<ClassroomMemberPage | null>(null);
  const [memberPage, setMemberPage] = useState(0);
  const [tab, setTab] = useState<Tab>('students');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [pendingRemoval, setPendingRemoval] = useState<{ id: number; pending: boolean } | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [title, setTitle] = useState('');
  const [instructions, setInstructions] = useState('');
  const [dueAt, setDueAt] = useState('');
  const [lessonTitle, setLessonTitle] = useState('');
  const [lessonFile, setLessonFile] = useState<File | null>(null);

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
    if (!title.trim()) return;
    setSaving(true);
    try {
      await createClassTask(classId, {
        title: title.trim(),
        instructions: instructions.trim(),
        dueAt: dueAt ? new Date(dueAt).toISOString() : null,
      });
      setTitle('');
      setInstructions('');
      setDueAt('');
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

  const openLesson = async (url: string) => {
    try {
      if (url.startsWith('/api/classes/')) await openClassMaterial(url);
      else window.open(url, '_blank', 'noopener,noreferrer');
    } catch (cause) {
      setError(classroomError(cause));
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
          <header className="classroom-header">
            <div>
              <h1>{room.name}</h1>
              <p>{room.description || 'Chưa có mô tả.'}</p>
            </div>
            <span>
              Mã lớp: <strong>{room.joinCode}</strong>
            </span>
          </header>
          <nav className="classroom-tabs" aria-label="Nội dung lớp">
            <button type="button" className={tab === 'students' ? 'active' : ''} onClick={() => setTab('students')}>
              Học sinh ({members?.activeCount ?? 0})
            </button>
            <button type="button" className={tab === 'lessons' ? 'active' : ''} onClick={() => setTab('lessons')}>
              Danh sách bài ({room.lessons.length})
            </button>
            <button type="button" className={tab === 'assignments' ? 'active' : ''} onClick={() => setTab('assignments')}>
              Giao việc ({room.assignments.length})
            </button>
            <button type="button" className={tab === 'exams' ? 'active' : ''} onClick={() => setTab('exams')}>
              Bài kiểm tra
            </button>
            <button type="button" className={tab === 'banks' ? 'active' : ''} onClick={() => setTab('banks')}>
              Ngân hàng câu hỏi
            </button>
            <button type="button" className={tab === 'progress' ? 'active' : ''} onClick={() => setTab('progress')}>
              Tiến độ
            </button>
          </nav>
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
                <nav className="classroom-pagination" aria-label="Phân trang học sinh">
                  <button type="button" disabled={loading || saving || memberPage === 0} onClick={() => void load(memberPage - 1)}>
                    Trang trước
                  </button>
                  <span>
                    Trang {memberPage + 1} / {members.totalPages} · {members.totalElements} học sinh
                  </span>
                  <button
                    type="button"
                    disabled={loading || saving || memberPage + 1 >= members.totalPages}
                    onClick={() => void load(memberPage + 1)}
                  >
                    Trang sau
                  </button>
                </nav>
              )}
            </section>
          )}
          {tab === 'lessons' && (
            <section className="classroom-panel">
              <h2>Danh sách bài</h2>
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
                    <button type="button" onClick={() => void openLesson(lesson.fileUrl)}>
                      Mở bài học
                    </button>
                  </div>
                </div>
              ))}
            </section>
          )}
          {tab === 'assignments' && (
            <section className="classroom-panel">
              <h2>Giao bài tập cho lớp</h2>
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
                  </div>
                  <span>{task.dueAt ? `Hạn: ${new Date(task.dueAt).toLocaleString('vi-VN')}` : 'Không có hạn'}</span>
                </div>
              ))}
            </section>
          )}
          {tab === 'exams' && <ClassExams classId={classId} lessons={room.lessons} />}
          {tab === 'banks' && <QuestionBankView classId={classId} />}
          {tab === 'progress' && <ClassProgress classId={classId} />}
        </>
      )}
    </main>
  );
};

export default InstructorClassroom;
