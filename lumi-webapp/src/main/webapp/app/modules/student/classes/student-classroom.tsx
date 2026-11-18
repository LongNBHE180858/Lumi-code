import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router';
import {
  classroomError,
  getStudentClassroom,
  getMyClassAssignmentSubmission,
  submitClassAssignment,
  type Classroom,
  type ClassAssignmentSubmission,
} from 'app/shared/util/classroom-api';
import '../classroom.scss';
import { useAppSelector } from 'app/config/store';
import { getStudentExams, getStudentExamResults, type ExamResult, type ExamSummary } from 'app/shared/util/class-exam-api';
import ClassroomMediaPreview from 'app/shared/components/classroom-media-preview';

export const StudentClassroom: React.FC = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const [room, setRoom] = useState<Classroom | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [taskDrafts, setTaskDrafts] = useState<Record<number, string>>({});
  const [taskSubmissions, setTaskSubmissions] = useState<Record<number, ClassAssignmentSubmission>>({});
  const [loadingTaskSubmissions, setLoadingTaskSubmissions] = useState(true);
  const [submittingTask, setSubmittingTask] = useState(false);
  const [selectedTaskId, setSelectedTaskId] = useState<number | null>(null);
  const [draftNotice, setDraftNotice] = useState<string | null>(null);
  const [taskFile, setTaskFile] = useState<File | null>(null);
  const [fileInputKey, setFileInputKey] = useState(0);
  const [resultUpdates, setResultUpdates] = useState<{ id: string; title: string; score: number; date: string }[]>([]);
  const [inProgressExams, setInProgressExams] = useState<ExamSummary[]>([]);
  const [lessonPractices, setLessonPractices] = useState<ExamSummary[]>([]);
  const [practiceResults, setPracticeResults] = useState<Record<number, ExamResult[] | null>>({});
  const [practiceError, setPracticeError] = useState<string | null>(null);
  const [readUpdateIds, setReadUpdateIds] = useState<string[]>([]);
  const [tab, setTab] = useState<'overview' | 'lessons' | 'assignments' | 'updates'>('overview');
  const [now, setNow] = useState(() => Date.now());
  const accountId = useAppSelector(state => state.authentication.account.id);
  const taskKey = `classroom-task-drafts:${accountId}:${id}`;
  const readUpdatesKey = `classroom-read-updates:${accountId}:${id}`;

  useEffect(() => {
    const timer = window.setInterval(() => setNow(Date.now()), 60_000);
    return () => window.clearInterval(timer);
  }, []);

  useEffect(() => {
    try {
      const drafts = JSON.parse(window.localStorage.getItem(taskKey) ?? '{}');
      const read = JSON.parse(window.localStorage.getItem(readUpdatesKey) ?? '[]');
      setTaskDrafts(drafts && typeof drafts === 'object' && !Array.isArray(drafts) ? drafts : {});
      setReadUpdateIds(Array.isArray(read) ? read.filter((value): value is string => typeof value === 'string') : []);
    } catch {
      setTaskDrafts({});
      setReadUpdateIds([]);
    }
  }, [taskKey, readUpdatesKey]);

  useEffect(() => {
    if (!room) return;
    let active = true;
    void getStudentExams(room.id)
      .then(async exams => {
        if (active) {
          setInProgressExams(exams.filter(exam => exam.inProgressAttemptId != null && exam.status === 'AVAILABLE'));
          setLessonPractices(exams.filter(exam => exam.assessmentType === 'PRACTICE' && exam.materialId != null));
          setPracticeError(null);
        }
        const results = await Promise.allSettled(exams.map(exam => getStudentExamResults(room.id, exam.id)));
        if (!active) return;
        const nextPracticeResults: Record<number, ExamResult[] | null> = {};
        exams.forEach((exam, index) => {
          if (exam.assessmentType === 'PRACTICE' && exam.materialId != null) {
            nextPracticeResults[exam.id] = results[index].status === 'fulfilled' ? results[index].value : null;
          }
        });
        setPracticeResults(nextPracticeResults);
        setResultUpdates(
          results
            .flatMap((entry, index) =>
              entry.status === 'fulfilled'
                ? entry.value.flatMap(item =>
                    item.score === null
                      ? []
                      : [{ id: `result-${item.attemptId}`, title: exams[index].title, score: item.score, date: item.submittedAt }],
                  )
                : [],
            )
            .sort((left, right) => Date.parse(right.date) - Date.parse(left.date))
            .slice(0, 5),
        );
      })
      .catch(() => {
        if (active) {
          setResultUpdates([]);
          setInProgressExams([]);
          setLessonPractices([]);
          setPracticeError('Không tải được bài tập sau bài học. Vui lòng thử tải lại trang.');
        }
      });
    return () => {
      active = false;
    };
  }, [room?.id]);

  const saveTaskDraft = (taskId: number) => {
    window.localStorage.setItem(taskKey, JSON.stringify(taskDrafts));
    setDraftNotice(`Đã lưu bản nháp cho bài số ${taskId} trên trình duyệt này.`);
  };

  useEffect(() => {
    if (!room) return;
    let active = true;
    setLoadingTaskSubmissions(true);
    void Promise.all(room.assignments.map(task => getMyClassAssignmentSubmission(room.id, task.id)))
      .then(results => {
        if (!active) return;
        const found: Record<number, ClassAssignmentSubmission> = {};
        results.forEach((result, index) => {
          if (result) found[room.assignments[index].id] = result;
        });
        setTaskSubmissions(found);
      })
      .catch(cause => active && setError(classroomError(cause)))
      .finally(() => active && setLoadingTaskSubmissions(false));
    return () => {
      active = false;
    };
  }, [room?.id]);

  const submitTask = async (taskId: number) => {
    if (!room) return;
    const content = (taskDrafts[taskId] ?? '').trim();
    if (!content && !taskFile) {
      setDraftNotice('Hãy nhập câu trả lời hoặc chọn tệp bài làm trước khi nộp.');
      return;
    }
    if (taskFile) {
      const name = taskFile.name.toLowerCase();
      const maxBytes = 25 * 1024 * 1024;
      if (!['.pdf', '.docx'].some(extension => name.endsWith(extension)) || taskFile.size > maxBytes) {
        setDraftNotice('Chỉ hỗ trợ tệp PDF hoặc DOCX tối đa 25 MB.');
        return;
      }
    }
    setSubmittingTask(true);
    try {
      const saved = taskFile
        ? await submitClassAssignment(room.id, taskId, content, taskFile)
        : await submitClassAssignment(room.id, taskId, content);
      setTaskSubmissions(current => ({ ...current, [taskId]: saved }));
      setTaskFile(null);
      setFileInputKey(value => value + 1);
      setDraftNotice('Đã nộp bài cho giáo viên.');
    } catch (cause) {
      setDraftNotice(classroomError(cause));
    } finally {
      setSubmittingTask(false);
    }
  };

  const markUpdateRead = (updateId: string) => {
    if (readUpdateIds.includes(updateId)) return;
    const updated = [...readUpdateIds, updateId];
    setReadUpdateIds(updated);
    window.localStorage.setItem(readUpdatesKey, JSON.stringify(updated));
  };

  useEffect(() => {
    const classId = Number(id);
    if (!Number.isSafeInteger(classId) || classId <= 0) {
      setError('Mã lớp không hợp lệ.');
      return;
    }
    void getStudentClassroom(classId)
      .then(setRoom)
      .catch(cause => setError(classroomError(cause)));
  }, [id]);

  const upcoming = (room?.assignments ?? [])
    .filter(task => task.dueAt && new Date(task.dueAt).getTime() >= now)
    .sort((left, right) => new Date(left.dueAt ?? 0).getTime() - new Date(right.dueAt ?? 0).getTime())
    .slice(0, 4);
  const undatedTasks = (room?.assignments ?? []).filter(task => !task.dueAt).slice(0, 4);
  const newLessons = (room?.lessons ?? []).filter(lesson => lesson.uploadedAt && now - new Date(lesson.uploadedAt).getTime() < 604800000);
  const activeCompletedTaskIds = (room?.assignments ?? []).filter(task => Boolean(taskSubmissions[task.id])).map(task => task.id);
  const taskProgress = room?.assignments.length ? Math.round((activeCompletedTaskIds.length / room.assignments.length) * 100) : 0;
  const selectedTask = room?.assignments.find(task => task.id === selectedTaskId);
  const recentOverviewUpdates = [
    ...upcoming.map(task => ({
      key: `task-${task.id}`,
      isNew: false,
      title: task.title,
      subtitle: `Hạn nộp ${new Date(task.dueAt ?? '').toLocaleString('vi-VN')}`,
      timestamp: Date.parse(task.dueAt ?? '1970-01-01'),
    })),
    ...undatedTasks.map(task => ({
      key: `task-${task.id}`,
      isNew: false,
      title: `Bài được giao: ${task.title}`,
      subtitle: 'Không có hạn nộp',
      timestamp: 0,
    })),
    ...[...newLessons]
      .sort((left, right) => Date.parse(right.uploadedAt ?? '1970-01-01') - Date.parse(left.uploadedAt ?? '1970-01-01'))
      .map(lesson => ({
        key: `lesson-${lesson.id}`,
        isNew: true,
        title: `Bài học mới: ${lesson.title}`,
        subtitle: lesson.uploadedAt ? new Date(lesson.uploadedAt).toLocaleDateString('vi-VN') : '',
        timestamp: Date.parse(lesson.uploadedAt ?? '1970-01-01'),
      })),
  ]
    .sort((left, right) => right.timestamp - left.timestamp)
    .slice(0, 4);
  const recentLessons = [...(room?.lessons ?? [])]
    .sort((left, right) => Date.parse(right.uploadedAt ?? '1970-01-01') - Date.parse(left.uploadedAt ?? '1970-01-01'))
    .slice(0, 4);
  const updates = [
    ...(room?.assignments ?? []).map(task => ({
      id: `task-${task.id}`,
      title: task.title,
      detail: task.dueAt ? `Hạn nộp ${new Date(task.dueAt).toLocaleString('vi-VN')}` : 'Bài được giao trong lớp',
      date: task.dueAt ?? '',
      target: 'assignments' as const,
    })),
    ...newLessons.map(lesson => ({
      id: `lesson-${lesson.id}`,
      title: lesson.title,
      detail: 'Tài liệu bài học mới',
      date: lesson.uploadedAt ?? '',
      target: 'lessons' as const,
    })),
    ...resultUpdates.map(item => ({
      id: item.id,
      title: item.title,
      detail: `Kết quả kiểm tra: ${item.score}%`,
      date: item.date,
      target: 'exams' as const,
    })),
  ].sort((left, right) => Date.parse(right.date || '1970-01-01') - Date.parse(left.date || '1970-01-01'));
  const unreadCount = updates.filter(item => !readUpdateIds.includes(item.id)).length;

  return (
    <main className="classroom-page">
      <button type="button" className="classroom-back" onClick={() => navigate('/student/classes')}>
        ← Lớp học của tôi
      </button>
      {error && (
        <p className="classroom-error" role="alert">
          {error}
        </p>
      )}
      {!room && !error && <p>Đang tải lớp học...</p>}
      {room && (
        <>
          <header className="classroom-header classroom-hero">
            <div>
              <span className="classroom-eyebrow">KHÔNG GIAN HỌC TẬP</span>
              <h1>{room.name}</h1>
              <p>{room.description || 'Chưa có mô tả.'}</p>
            </div>
            <div className="classroom-hero-progress">
              <strong>{taskProgress}%</strong>
              <span>Bài được giao đã nộp</span>
            </div>
          </header>
          <nav className="classroom-tabs classroom-main-tabs" aria-label="Các mục trong lớp">
            <button type="button" className={tab === 'overview' ? 'active' : ''} onClick={() => setTab('overview')}>
              Tổng quan
            </button>
            <button type="button" className={tab === 'lessons' ? 'active' : ''} onClick={() => setTab('lessons')}>
              Bài học <span>{room.lessons.length}</span>
            </button>
            <button type="button" className={tab === 'assignments' ? 'active' : ''} onClick={() => setTab('assignments')}>
              Bài được giao <span>{room.assignments.length}</span>
            </button>
            <button type="button" onClick={() => navigate(`/student/classes/${room.id}/exams`)}>
              Kiểm tra & ôn tập ↗
            </button>
          </nav>
          {tab === 'overview' && (
            <>
              <div className="classroom-metric-grid">
                <div className="classroom-metric-card">
                  <span>Bài học</span>
                  <strong>{room.lessons.length}</strong>
                  <small>Tài liệu trong lớp</small>
                </div>
                <div className="classroom-metric-card">
                  <span>Bài được giao</span>
                  <strong>{room.assignments.length}</strong>
                  <small>
                    {activeCompletedTaskIds.length} đã nộp · {upcoming.length} sắp đến hạn
                  </small>
                </div>
                <div className="classroom-metric-card">
                  <span>Tài liệu mới</span>
                  <strong>{newLessons.length}</strong>
                  <small>Được thêm trong 7 ngày qua</small>
                </div>
              </div>
              {inProgressExams.length > 0 && (
                <section className="classroom-panel" aria-label="Bài kiểm tra đang làm">
                  <div className="classroom-section-heading">
                    <div>
                      <h2>Tiếp tục bài đang làm</h2>
                      <p>Lượt làm đang mở của bạn.</p>
                    </div>
                  </div>
                  {inProgressExams.map(exam => (
                    <div className="classroom-row" key={exam.id}>
                      <div>
                        <strong>{exam.title}</strong>
                        <small>
                          {exam.assessmentType === 'PRACTICE' ? 'Luyện tập' : 'Kiểm tra'} · {exam.questionCount} câu hỏi
                        </small>
                      </div>
                      <button type="button" onClick={() => navigate(`/student/classes/${room.id}/exams/${exam.id}/take`)}>
                        {exam.assessmentType === 'PRACTICE' ? 'Tiếp tục luyện tập' : 'Tiếp tục kiểm tra'}
                      </button>
                    </div>
                  ))}
                </section>
              )}
              <div className="classroom-overview-grid">
                <section className="classroom-panel">
                  <div className="classroom-section-heading">
                    <div>
                      <h2>Học tiếp từ đây</h2>
                      <p>Bài học được sắp theo thứ tự của giáo viên.</p>
                    </div>
                  </div>
                  {room.lessons.length === 0 && <p>Giáo viên chưa thêm bài học.</p>}
                  {recentLessons.map(lesson => (
                    <div className="classroom-row" key={lesson.id}>
                      <div>
                        <strong>{lesson.title}</strong>
                        <small>{lesson.fileType}</small>
                      </div>
                      <button type="button" onClick={() => setTab('lessons')}>
                        Xem bài
                      </button>
                    </div>
                  ))}
                  <button type="button" className="classroom-text-button" onClick={() => setTab('lessons')}>
                    Xem tất cả bài học →
                  </button>
                </section>
                <section className="classroom-panel">
                  <div className="classroom-section-heading">
                    <div>
                      <h2>Cập nhật & hạn nộp</h2>
                      <p>Tổng hợp từ nội dung lớp, chưa có thông báo đẩy.</p>
                    </div>
                  </div>
                  {recentOverviewUpdates.length === 0 && <p>Chưa có cập nhật trong lớp.</p>}
                  {recentOverviewUpdates.map(item => (
                    <div className="classroom-update-item" key={item.key}>
                      <span className={`classroom-update-dot${item.isNew ? ' is-new' : ''}`} />
                      <div>
                        <strong>{item.title}</strong>
                        <small>{item.subtitle}</small>
                      </div>
                    </div>
                  ))}
                </section>
              </div>
              <section className="classroom-panel">
                <div className="classroom-section-heading">
                  <div>
                    <h2>Mọi nội dung trong lớp</h2>
                    <p>Truy cập nhanh bài học, bài được giao và bài kiểm tra.</p>
                  </div>
                </div>
                <div className="classroom-shortcuts">
                  <button type="button" onClick={() => setTab('lessons')}>
                    <span>01</span>
                    <strong>Bài học & tài liệu</strong>
                    <small>{room.lessons.length} nội dung</small>
                  </button>
                  <button type="button" onClick={() => setTab('assignments')}>
                    <span>02</span>
                    <strong>Bài được giao</strong>
                    <small>{room.assignments.length} bài</small>
                  </button>
                  <button type="button" onClick={() => navigate(`/student/classes/${room.id}/exams`)}>
                    <span>03</span>
                    <strong>Kiểm tra & ôn tập</strong>
                    <small>Xem đề và kết quả</small>
                  </button>
                </div>
              </section>
            </>
          )}
          {tab === 'assignments' && (
            <section className="classroom-panel">
              <div className="classroom-section-heading">
                <div>
                  <h2>Bài được giao</h2>
                  <p>Xem yêu cầu, soạn câu trả lời và nộp cho giáo viên.</p>
                </div>
                <span className="classroom-count-badge">{room.assignments.length} bài</span>
              </div>
              {room.assignments.length === 0 && <p>Giáo viên chưa giao bài tập.</p>}
              {room.assignments.map(task => {
                const dueTime = task.dueAt ? Date.parse(task.dueAt) : NaN;
                const pending = !taskSubmissions[task.id];
                const overdue = pending && Number.isFinite(dueTime) && dueTime <= now;
                const dueSoon = pending && Number.isFinite(dueTime) && dueTime > now && dueTime - now <= 24 * 60 * 60 * 1000;
                return (
                  <div className={`classroom-row${overdue ? ' is-overdue' : dueSoon ? ' is-due-soon' : ''}`} key={task.id}>
                    <div>
                      <strong>{task.title}</strong>
                      <small>{task.instructions || 'Không có hướng dẫn thêm.'}</small>
                      <small>
                        {taskSubmissions[task.id]
                          ? taskSubmissions[task.id].status === 'GRADED'
                            ? `Đã chấm: ${taskSubmissions[task.id].score}/100 điểm`
                            : 'Đã nộp, chờ chấm'
                          : taskDrafts[task.id]
                            ? 'Có bản nháp trên thiết bị'
                            : 'Chưa bắt đầu'}
                      </small>
                    </div>
                    <div className="classroom-member-actions">
                      <span className={`classroom-due-label${overdue ? ' is-overdue' : dueSoon ? ' is-due-soon' : ''}`}>
                        {task.dueAt
                          ? `${overdue ? 'Quá hạn' : dueSoon ? 'Sắp hết hạn' : 'Hạn'}: ${new Date(task.dueAt).toLocaleString('vi-VN')}`
                          : 'Không có hạn'}
                      </span>
                      <button
                        type="button"
                        className="classroom-secondary-action"
                        onClick={() => {
                          setSelectedTaskId(task.id);
                          setDraftNotice(null);
                          setTaskFile(null);
                          setFileInputKey(value => value + 1);
                        }}
                      >
                        Soạn bài
                      </button>
                    </div>
                  </div>
                );
              })}
              {selectedTask && (
                <div className="classroom-task-workspace">
                  <div className="classroom-section-heading">
                    <div>
                      <h3>{selectedTask.title}</h3>
                      <p>{selectedTask.instructions || 'Giáo viên chưa thêm hướng dẫn.'}</p>
                      {selectedTask.fileName && <p>Tệp đính kèm: {selectedTask.fileName}</p>}
                    </div>
                    <button type="button" className="classroom-outline-button" onClick={() => setSelectedTaskId(null)}>
                      Đóng
                    </button>
                  </div>
                  {selectedTask.fileType && (
                    <ClassroomMediaPreview
                      classId={room.id}
                      itemId={selectedTask.id}
                      kind="assignment"
                      fileType={selectedTask.fileType}
                      title={selectedTask.title}
                    />
                  )}
                  <label htmlFor="classroom-task-answer">Câu trả lời hoặc ghi chú của bạn</label>
                  <textarea
                    id="classroom-task-answer"
                    value={taskDrafts[selectedTask.id] ?? ''}
                    disabled={Boolean(taskSubmissions[selectedTask.id])}
                    onChange={event => {
                      setTaskDrafts(current => ({ ...current, [selectedTask.id]: event.target.value }));
                      setDraftNotice(null);
                    }}
                    placeholder="Viết câu trả lời của bạn ở đây..."
                  />
                  <label htmlFor="classroom-task-file">Tệp bài làm (tùy chọn)</label>
                  <input
                    key={`${selectedTask.id}-${fileInputKey}`}
                    id="classroom-task-file"
                    type="file"
                    accept=".pdf,.docx"
                    disabled={Boolean(taskSubmissions[selectedTask.id]) || submittingTask}
                    onChange={event => {
                      setTaskFile(event.target.files?.[0] ?? null);
                      setDraftNotice(null);
                    }}
                  />
                  <small>PDF hoặc DOCX tối đa 25 MB. Tệp chỉ được tải lên khi bạn nộp bài.</small>
                  {taskFile && (
                    <button
                      type="button"
                      className="classroom-outline-button"
                      onClick={() => {
                        setTaskFile(null);
                        setFileInputKey(value => value + 1);
                      }}
                    >
                      Bỏ tệp đã chọn: {taskFile.name}
                    </button>
                  )}
                  <div className="classroom-member-actions">
                    <button
                      type="button"
                      disabled={Boolean(taskSubmissions[selectedTask.id])}
                      onClick={() => saveTaskDraft(selectedTask.id)}
                    >
                      Lưu bản nháp
                    </button>
                    <button
                      type="button"
                      className="classroom-secondary-action"
                      disabled={loadingTaskSubmissions || submittingTask || Boolean(taskSubmissions[selectedTask.id])}
                      onClick={() => void submitTask(selectedTask.id)}
                    >
                      {submittingTask ? 'Đang nộp...' : 'Nộp cho giáo viên'}
                    </button>
                  </div>
                  {taskSubmissions[selectedTask.id] && (
                    <>
                      <p className="classroom-notice">
                        {taskSubmissions[selectedTask.id].status === 'GRADED'
                          ? `Điểm: ${taskSubmissions[selectedTask.id].score}/100. Nhận xét: ${taskSubmissions[selectedTask.id].feedback || 'Không có'}`
                          : 'Bài đã nộp, đang chờ giáo viên chấm.'}
                      </p>
                      {taskSubmissions[selectedTask.id].attachments?.map(file => (
                        <div key={file.fileName}>
                          <small>Tệp đã nộp: {file.fileName}</small>
                          <ClassroomMediaPreview
                            classId={room.id}
                            assignmentId={selectedTask.id}
                            itemId={taskSubmissions[selectedTask.id].id}
                            kind="submission"
                            fileType={file.fileType}
                            title={file.fileName}
                          />
                        </div>
                      ))}
                    </>
                  )}
                  {draftNotice && (
                    <p className="classroom-notice" role="status">
                      {draftNotice}
                    </p>
                  )}
                  <p className="classroom-soft-note">Bản nháp chỉ lưu trên trình duyệt này. Bài đã nộp và điểm được lưu trên máy chủ.</p>
                </div>
              )}
              {!selectedTask && room.assignments.length > 0 && (
                <p className="classroom-soft-note">Bạn có thể soạn bản nháp trên thiết bị, sau đó nộp bài cho giáo viên.</p>
              )}
            </section>
          )}
          {tab === 'updates' && (
            <section className="classroom-panel">
              <div className="classroom-section-heading">
                <div>
                  <h2>Thông báo của lớp</h2>
                  <p>Bài được giao, tài liệu mới và kết quả kiểm tra tổng hợp từ dữ liệu hiện có.</p>
                </div>
                <span className="classroom-count-badge">{unreadCount} chưa đọc</span>
              </div>
              {updates.length === 0 && <p>Chưa có cập nhật trong lớp.</p>}
              {updates.map(item => (
                <button
                  type="button"
                  className={`classroom-notification${readUpdateIds.includes(item.id) ? ' is-read' : ''}`}
                  key={item.id}
                  onClick={() => {
                    markUpdateRead(item.id);
                    if (item.target === 'exams') navigate(`/student/classes/${room.id}/exams`);
                    else setTab(item.target);
                  }}
                >
                  <span className="classroom-update-dot" />
                  <span>
                    <strong>{item.title}</strong>
                    <small>
                      {item.detail}
                      {item.date ? ` · ${new Date(item.date).toLocaleDateString('vi-VN')}` : ''}
                    </small>
                  </span>
                  <span className="classroom-notification-arrow">→</span>
                </button>
              ))}
              <p className="classroom-soft-note">Thông báo được tổng hợp khi mở lớp; trạng thái đã đọc chỉ lưu trên trình duyệt này.</p>
            </section>
          )}
          {tab === 'lessons' && (
            <section className="classroom-panel">
              <div className="classroom-section-heading">
                <div>
                  <h2>Bài học & tài liệu</h2>
                  <p>Theo đúng trình tự giáo viên đã sắp xếp.</p>
                </div>
                <span className="classroom-count-badge">{room.lessons.length} tài liệu</span>
              </div>
              {room.lessons.length === 0 && <p>Chưa có tài liệu bài học.</p>}
              {practiceError && (
                <p className="classroom-error" role="alert">
                  {practiceError}
                </p>
              )}
              {room.lessons.map((lesson, index) => {
                const linkedPractices = lessonPractices.filter(exam => exam.materialId === lesson.id);
                return (
                  <div className="classroom-row classroom-lesson-row" key={lesson.id}>
                    <div className="classroom-lesson-main">
                      <span className="classroom-lesson-number">{String(index + 1).padStart(2, '0')}</span>
                      <div className="classroom-lesson-info">
                        <strong>{lesson.title}</strong>
                        <small className={lesson.fileType === 'VIDEO' ? 'is-video' : 'is-pdf'}>{lesson.fileType}</small>
                      </div>
                      <ClassroomMediaPreview
                        classId={room.id}
                        itemId={lesson.id}
                        kind="lesson"
                        fileType={lesson.fileType}
                        title={lesson.title}
                      />
                    </div>
                    {linkedPractices.length > 0 && (
                      <div className="classroom-lesson-practice">
                        <div className="classroom-lesson-practice-heading">
                          <span className="classroom-lesson-practice-tag">Luyện tập</span>
                          <small>Củng cố kiến thức bài học</small>
                        </div>
                        <div className="classroom-lesson-practice-list">
                          {linkedPractices.map(exam => {
                            const results = practiceResults[exam.id];
                            const latestResult = results?.reduce<ExamResult | null>(
                              (latest, item) => (!latest || Date.parse(item.submittedAt) > Date.parse(latest.submittedAt) ? item : latest),
                              null,
                            );
                            const canStart = exam.status === 'AVAILABLE' || exam.status === 'ACTIVE';
                            const status =
                              exam.inProgressAttemptId != null
                                ? 'Đang làm'
                                : {
                                    AVAILABLE: 'Có thể làm',
                                    ACTIVE: 'Có thể làm',
                                    UPCOMING: 'Sắp mở',
                                    COMPLETED: 'Đã hết lượt',
                                    CLOSED: 'Đã đóng',
                                    ARCHIVED: 'Đã lưu trữ',
                                  }[exam.status];
                            return (
                              <div className="classroom-lesson-practice-item" key={exam.id}>
                                <div className="classroom-lesson-practice-meta">
                                  <strong>{exam.title}</strong>
                                  <div className="classroom-lesson-practice-chips">
                                    <span>{exam.questionCount} câu</span>
                                    <span>{exam.durationMinutes ? `${exam.durationMinutes} phút` : 'Không giới hạn giờ'}</span>
                                    <span className={`practice-status-dot${canStart ? ' is-open' : ''}`}>{status}</span>
                                    {latestResult?.score != null && (
                                      <span className="practice-score-pill">Điểm gần nhất: {latestResult.score}%</span>
                                    )}
                                  </div>
                                  {results === null && <small>Không tải được kết quả luyện tập.</small>}
                                </div>
                                <div className="classroom-lesson-practice-actions">
                                  {canStart && (
                                    <button
                                      type="button"
                                      className="btn-pill-primary"
                                      onClick={() => navigate(`/student/classes/${room.id}/exams/${exam.id}/take`)}
                                    >
                                      {exam.inProgressAttemptId != null ? 'Tiếp tục làm' : 'Luyện tập'}
                                    </button>
                                  )}
                                  {latestResult && (
                                    <button
                                      type="button"
                                      className="classroom-secondary-action"
                                      onClick={() =>
                                        navigate(`/student/classes/${room.id}/exams/${exam.id}/attempts/${latestResult.attemptId}/result`)
                                      }
                                    >
                                      Xem kết quả
                                    </button>
                                  )}
                                </div>
                              </div>
                            );
                          })}
                        </div>
                      </div>
                    )}
                  </div>
                );
              })}
            </section>
          )}
        </>
      )}
    </main>
  );
};

export default StudentClassroom;
