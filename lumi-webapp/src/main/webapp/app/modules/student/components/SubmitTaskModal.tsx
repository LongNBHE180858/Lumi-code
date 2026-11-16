import React, { useState } from 'react';
import type { AssignmentItem } from '../types';

interface SubmitTaskModalProps {
  isOpen: boolean;
  assignment: AssignmentItem | null;
  onClose: () => void;
  onSubmitWork: (taskId: string, fileName: string, comment: string) => void;
}

export const SubmitTaskModal: React.FC<SubmitTaskModalProps> = ({ isOpen, assignment, onClose, onSubmitWork }) => {
  const [selectedFileName, setSelectedFileName] = useState('');
  const [studentComment, setStudentComment] = useState('');

  if (!isOpen || !assignment) return null;

  const isAlreadySubmitted = assignment.status === 'submitted' || assignment.status === 'graded';

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      setSelectedFileName(e.target.files[0].name);
    }
  };

  const handleSimulateSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const finalFile = selectedFileName || 'NguyenMinhAnh_BaiLam.pdf';
    onSubmitWork(assignment.id, finalFile, studentComment);
    onClose();
  };

  return (
    <div className="lumi-modal-overlay" onClick={onClose}>
      <div
        className="lumi-modal-dialog submit-task-modal-dialog fade-in"
        onClick={e => e.stopPropagation()}
        role="dialog"
        aria-modal="true"
      >
        <div className="lumi-modal-head">
          <div className="modal-title-wrap">
            <span className="modal-badge-icon">📝</span>
            <div>
              <h2 className="lumi-modal-title">{assignment.title}</h2>
              <span className="modal-sub-class">
                {assignment.className} · Giáo viên: {assignment.teacherName}
              </span>
            </div>
          </div>
          <button type="button" className="lumi-modal-close" onClick={onClose}>
            ✕
          </button>
        </div>

        {/* Due Date & Category Banner */}
        <div className="task-meta-banner">
          <div className="task-meta-item">
            <span className="meta-label">Hạn nộp:</span>
            <span className="meta-val" style={{ color: '#ef4444' }}>
              ⏰ {assignment.dueDate}
            </span>
          </div>
          <div className="task-meta-item">
            <span className="meta-label">Hình thức:</span>
            <span className="meta-val">{assignment.submissionType}</span>
          </div>
          <div className="task-meta-item">
            <span className="meta-label">Trạng thái:</span>
            <span className="meta-val">
              {assignment.status === 'graded' && (
                <span className="status-pill status-active">
                  Đã chấm · Điểm: <strong>{assignment.grade}</strong>
                </span>
              )}
              {assignment.status === 'submitted' && <span className="status-pill status-active">Đã nộp bài</span>}
              {assignment.status === 'overdue' && (
                <span className="status-pill status-pending" style={{ color: '#ef4444' }}>
                  Quá hạn · Chưa nộp
                </span>
              )}
              {assignment.status === 'not_started' && <span className="status-pill status-pending">Chưa làm</span>}
              {assignment.status === 'in_progress' && (
                <span className="status-pill status-pending" style={{ color: '#2563eb' }}>
                  Đang làm
                </span>
              )}
            </span>
          </div>
        </div>

        {/* Instructions */}
        <div className="task-instruction-box">
          <h4 className="instruction-heading">Hướng dẫn từ giáo viên:</h4>
          <p className="instruction-content">{assignment.instructions}</p>
        </div>

        {/* Attachments from teacher */}
        {assignment.attachments.length > 0 && (
          <div className="task-attachment-section">
            <h4 className="instruction-heading">Tài liệu đính kèm:</h4>
            {assignment.attachments.map((att, i) => (
              <div key={i} className="download-attachment-card">
                <span className="doc-icon">📑</span>
                <div style={{ flex: 1 }}>
                  <p className="doc-name">{att.name}</p>
                  <p className="doc-meta">
                    {att.type} · {att.size}
                  </p>
                </div>
                <button type="button" className="btn-download-doc" onClick={() => alert(`Tải xuống tệp: ${att.name}`)}>
                  Tải xuống
                </button>
              </div>
            ))}
          </div>
        )}

        {/* Submission Section */}
        {isAlreadySubmitted ? (
          <div className="submitted-work-box fade-in">
            <h4 className="instruction-heading">Bài làm của bạn:</h4>
            <div className="submitted-file-row">
              <span>📄 {assignment.studentSubmission?.fileName || 'BaiLam_HocSinh.pdf'}</span>
              <span className="submitted-timestamp">Nộp lúc: {assignment.studentSubmission?.submittedDate || 'Vừa xong'}</span>
            </div>
            {assignment.studentSubmission?.comment && (
              <p className="submitted-comment">
                <strong>Lời nhắn:</strong> {assignment.studentSubmission.comment}
              </p>
            )}
            {assignment.grade && (
              <div className="grade-result-callout">
                <span className="grade-score">Điểm: {assignment.grade}</span>
                <p>Giáo viên đã nhận xét và chấm điểm bài làm của bạn.</p>
              </div>
            )}
          </div>
        ) : (
          <form onSubmit={handleSimulateSubmit} className="task-submission-form">
            <h4 className="instruction-heading">Tải lên bài nộp của bạn:</h4>

            <div className="upload-dropzone">
              <input type="file" id="student-file-input" className="hidden-file-input" onChange={handleFileChange} />
              <label htmlFor="student-file-input" className="dropzone-label">
                <span className="upload-cloud-icon">☁️</span>
                <span className="upload-main-text">{selectedFileName ? selectedFileName : 'Kéo thả tệp hoặc bấm vào đây để tải lên'}</span>
                <span className="upload-sub-text">Hỗ trợ PDF, Word, Ảnh JPG/PNG (Tối đa 25MB)</span>
              </label>
            </div>

            <div className="form-group-item" style={{ marginTop: '12px' }}>
              <label className="form-item-label">Ghi chú hoặc lời nhắn cho giáo viên (tùy chọn):</label>
              <textarea
                className="form-item-textarea"
                rows={2}
                placeholder="VD: Em đã làm đầy đủ 5 câu trong phiếu bài tập..."
                value={studentComment}
                onChange={e => setStudentComment(e.target.value)}
              />
            </div>

            <div className="modal-actions-bar">
              <button type="button" className="btn-pill-outline" onClick={onClose}>
                Đóng
              </button>
              <button type="submit" className="btn-pill-primary">
                Nộp bài tập
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
};

export default SubmitTaskModal;
