import React, { useEffect, useState } from 'react';
import {
  classroomError,
  getClassAssignmentSubmissions,
  gradeClassAssignment,
  type ClassAssignmentSubmission,
  type ClassAssignmentSubmissionList,
} from 'app/shared/util/classroom-api';
import ClassroomMediaPreview from 'app/shared/components/classroom-media-preview';

export const ClassAssignmentReview: React.FC<{ classId: number; assignmentId: number }> = ({ classId, assignmentId }) => {
  const [data, setData] = useState<ClassAssignmentSubmissionList | null>(null);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [score, setScore] = useState('');
  const [feedback, setFeedback] = useState('');
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);

  useEffect(() => {
    let active = true;
    setData(null);
    setSelectedId(null);
    void getClassAssignmentSubmissions(classId, assignmentId)
      .then(result => active && setData(result))
      .catch(cause => active && setError(classroomError(cause)));
    return () => {
      active = false;
    };
  }, [classId, assignmentId]);

  const selected: ClassAssignmentSubmission | undefined = data?.items.find(item => item.id === selectedId);
  const choose = (item: ClassAssignmentSubmission) => {
    setSelectedId(item.id);
    setScore(item.score == null ? '' : String(item.score));
    setFeedback(item.feedback ?? '');
    setNotice(null);
  };

  const grade = async () => {
    if (!selected || score === '' || !Number.isFinite(Number(score)) || Number(score) < 0 || Number(score) > 100) {
      setError('Điểm phải từ 0 đến 100.');
      return;
    }
    setSaving(true);
    setError(null);
    try {
      const updated = await gradeClassAssignment(classId, assignmentId, selected.id, Number(score), feedback.trim(), selected.version);
      setData(current => current && { ...current, items: current.items.map(item => (item.id === updated.id ? updated : item)) });
      setNotice('Đã lưu điểm và nhận xét.');
    } catch (cause) {
      setError(classroomError(cause));
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="classroom-review-grid">
      <div>
        <h4>
          Danh sách bài nộp ({data?.items.length ?? 0}/{data?.assignedCount ?? 0})
        </h4>
        {!data && !error && <p>Đang tải bài nộp...</p>}
        {data?.items.length === 0 && <p>Chưa có học sinh nộp bài.</p>}
        {data?.items.map(item => (
          <button type="button" className="classroom-row" key={item.id} onClick={() => choose(item)}>
            <strong>{item.studentName}</strong>
            <small>{item.status === 'GRADED' ? `${item.score}/100 điểm` : 'Chờ chấm'}</small>
            <small>{new Date(item.submittedAt).toLocaleString('vi-VN')}</small>
          </button>
        ))}
      </div>
      <div>
        <h4>Chấm và nhận xét</h4>
        {selected && <p>{selected.content || 'Học sinh chỉ nộp tệp đính kèm.'}</p>}
        {selected?.attachments?.map(file => (
          <div key={file.fileName}>
            <strong>Tệp bài làm: {file.fileName}</strong>
            <ClassroomMediaPreview
              classId={classId}
              assignmentId={assignmentId}
              itemId={selected.id}
              kind="submission"
              fileType={file.fileType}
              title={file.fileName}
            />
          </div>
        ))}
        <label>
          Điểm
          <input
            type="number"
            min="0"
            max="100"
            step="0.01"
            disabled={!selected || saving}
            value={score}
            onChange={event => setScore(event.target.value)}
            placeholder="Chọn bài nộp"
          />
        </label>
        <label>
          Nhận xét
          <textarea disabled={!selected || saving} maxLength={2000} value={feedback} onChange={event => setFeedback(event.target.value)} />
        </label>
        <button type="button" className="classroom-outline-button" disabled={!selected || saving} onClick={() => void grade()}>
          {saving ? 'Đang lưu...' : 'Lưu kết quả'}
        </button>
        {notice && <p role="status">{notice}</p>}
        {error && (
          <p className="classroom-error" role="alert">
            {error}
          </p>
        )}
      </div>
    </div>
  );
};

export default ClassAssignmentReview;
