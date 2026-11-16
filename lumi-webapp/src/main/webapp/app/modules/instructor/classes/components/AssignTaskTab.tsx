import React, { useState } from 'react';
import type { ClassItem, AttachmentFile, AssignTaskFormValues } from '../class-management.model';

interface AssignTaskTabProps {
  currentClass: ClassItem;
  allClasses: ClassItem[];
  onAssignSuccess: (formValues: AssignTaskFormValues) => void;
  onCancel?: () => void;
}

export const AssignTaskTab: React.FC<AssignTaskTabProps> = ({ currentClass, allClasses, onAssignSuccess, onCancel }) => {
  const [selectedClassId, setSelectedClassId] = useState(currentClass.id);
  const [taskTitle, setTaskTitle] = useState('Luyện tập: Ước chung và bội chung');
  const [instructions, setInstructions] = useState(
    'Hoàn thành bài 1–5 trong phiếu bài tập đính kèm. Trình bày đầy đủ các bước giải, sau đó tải ảnh hoặc tệp PDF bài làm lên lớp.',
  );
  const [attachments, setAttachments] = useState<AttachmentFile[]>([
    {
      name: 'Phieu-bai-tap_UCLN-BCNN.pdf',
      size: '248 KB',
      type: 'PDF · 5 bài tập',
    },
  ]);
  const [submissionType, setSubmissionType] = useState('Tải tệp hoặc ảnh bài làm lên lớp');
  const [assignTime, setAssignTime] = useState('Ngay sau khi xác nhận');
  const [dueDate, setDueDate] = useState('2026-11-11T23:59');
  const [allowLateSubmission, setAllowLateSubmission] = useState(false);
  const [notifyStudents, setNotifyStudents] = useState(true);
  const [isChangingClass, setIsChangingClass] = useState(false);

  const targetClass = allClasses.find(c => c.id === selectedClassId) || currentClass;

  const handleRemoveAttachment = (index: number) => {
    setAttachments(prev => prev.filter((_, i) => i !== index));
  };

  const handleAddSampleAttachment = () => {
    const newFile: AttachmentFile = {
      name: `Tai-lieu-tham-khao-${Date.now().toString().slice(-4)}.pdf`,
      size: '315 KB',
      type: 'PDF · Tài liệu bổ trợ',
    };
    setAttachments(prev => [...prev, newFile]);
  };

  const handleSubmit = (isDraft = false) => {
    if (!taskTitle.trim()) {
      alert('Vui lòng nhập tên công việc.');
      return;
    }
    // format due date
    const formattedDate = dueDate.includes('T')
      ? `${dueDate.split('T')[0].split('-').reverse().join('/')} ${dueDate.split('T')[1]}`
      : dueDate;

    onAssignSuccess({
      title: taskTitle.trim(),
      classId: targetClass.id,
      instructions: instructions.trim(),
      attachments,
      submissionType,
      assignTime,
      dueDate: formattedDate,
      allowLateSubmission,
      notifyStudents: isDraft ? false : notifyStudents,
    });
  };

  return (
    <div className="assign-task-container fade-in">
      {/* Form & Summary Two-Column Layout */}
      <div className="assign-task-two-cols">
        {/* Left Column: Form Details */}
        <div className="assign-task-form-card">
          <div className="form-card-head">
            <h2 className="form-head-title">Giao việc cho lớp</h2>
            <p className="form-head-desc">Tạo công việc và giao đồng thời cho toàn bộ học viên đã tham gia lớp.</p>
          </div>

          <div className="assign-form-body">
            {/* Task Title */}
            <div className="form-group-item">
              <label className="form-item-label">
                Tên công việc <span className="required-star">*</span>
              </label>
              <input
                type="text"
                className="form-item-input"
                placeholder="VD: Luyện tập: Ước chung và bội chung"
                value={taskTitle}
                onChange={e => setTaskTitle(e.target.value)}
                required
              />
            </div>

            {/* Instructions */}
            <div className="form-group-item">
              <label className="form-item-label">
                Hướng dẫn thực hiện <span className="required-star">*</span>
              </label>
              <textarea
                className="form-item-textarea"
                rows={4}
                placeholder="Nhập hướng dẫn chi tiết yêu cầu nộp bài, các bước làm bài cho học sinh..."
                value={instructions}
                onChange={e => setInstructions(e.target.value)}
                required
              />
            </div>

            {/* Attachments & Documents */}
            <div className="form-group-item">
              <label className="form-item-label">Nội dung & tài liệu đính kèm</label>
              <div className="attachments-list">
                {attachments.map((file, idx) => (
                  <div key={idx} className="attachment-file-row">
                    <div className="attachment-file-info">
                      <span className="file-icon">📄</span>
                      <div>
                        <p className="file-name">{file.name}</p>
                        <p className="file-meta">
                          {file.type} · {file.size}
                        </p>
                      </div>
                    </div>
                    <button type="button" className="btn-remove-attachment" onClick={() => handleRemoveAttachment(idx)}>
                      Gỡ
                    </button>
                  </div>
                ))}

                <div className="attachment-actions-row">
                  <button type="button" className="btn-add-attachment" onClick={handleAddSampleAttachment}>
                    + Thêm tài liệu (PDF / Word)
                  </button>
                  <button type="button" className="btn-select-existing-bank" onClick={() => alert('Mở ngân hàng bài tập LUMI')}>
                    📚 Chọn bài tập có sẵn trong thư viện
                  </button>
                </div>
              </div>
            </div>

            {/* Submission Type */}
            <div className="form-group-item">
              <label className="form-item-label">Hình thức nộp bài</label>
              <select className="form-item-select" value={submissionType} onChange={e => setSubmissionType(e.target.value)}>
                <option value="Tải tệp hoặc ảnh bài làm lên lớp">Tải tệp hoặc ảnh bài làm lên lớp</option>
                <option value="Trắc nghiệm trực tuyến trên LUMI">Trắc nghiệm trực tuyến trên LUMI</option>
                <option value="Nhập văn bản trả lời trực tiếp">Nhập văn bản trả lời trực tiếp</option>
                <option value="Không yêu cầu nộp (chỉ đọc tài liệu)">Không yêu cầu nộp (chỉ đọc tài liệu)</option>
              </select>
              <span className="field-hint-small">
                Học sinh nộp bài tại công việc này. Giáo viên có thể xem tiến độ và phản hồi sau khi giao.
              </span>
            </div>
          </div>
        </div>

        {/* Right Column: Class Target & Schedule */}
        <div className="assign-task-side-card">
          {/* Target Class Section */}
          <div className="side-section-box">
            <h3 className="side-section-title">Phạm vi giao việc</h3>
            <div className="target-class-badge-card">
              <div className="class-badge-avatar">🏫</div>
              <div className="class-badge-texts">
                <span className="target-class-name">{targetClass.name}</span>
                <span className="target-class-sub">
                  {targetClass.code} · {targetClass.course}
                </span>
              </div>
              <button type="button" className="btn-link-change-class" onClick={() => setIsChangingClass(!isChangingClass)}>
                {isChangingClass ? 'Đóng' : 'Đổi lớp'}
              </button>
            </div>

            {isChangingClass && (
              <div className="change-class-dropdown fade-in">
                <label className="field-hint-small">Chọn lớp muốn giao:</label>
                <select
                  className="form-item-select"
                  value={selectedClassId}
                  onChange={e => {
                    setSelectedClassId(e.target.value);
                    setIsChangingClass(false);
                  }}
                >
                  {allClasses.map(c => (
                    <option key={c.id} value={c.id}>
                      {c.name} ({c.code}) - {c.studentCount} học viên
                    </option>
                  ))}
                </select>
              </div>
            )}

            <div className="student-recipient-info">
              <span className="student-recipient-tag">👥 Toàn bộ lớp · {targetClass.studentCount} học viên</span>
              <p className="field-hint-small">
                Chỉ giao cho học viên đã tham gia lớp. Yêu cầu đang chờ duyệt không nằm trong danh sách nhận.
              </p>
            </div>
          </div>

          {/* Schedule & Due Date Section */}
          <div className="side-section-box">
            <h3 className="side-section-title">Lịch giao và hạn nộp</h3>

            <div className="form-group-item">
              <label className="form-item-label">Thời điểm giao</label>
              <select className="form-item-select" value={assignTime} onChange={e => setAssignTime(e.target.value)}>
                <option value="Ngay sau khi xác nhận">Ngay sau khi xác nhận</option>
                <option value="Hẹn giờ giao sau">Hẹn giờ giao sau</option>
              </select>
            </div>

            <div className="form-group-item">
              <div className="label-with-hint">
                <label className="form-item-label">
                  Hạn nộp <span className="required-star">*</span>
                </label>
                <span className="label-hint-text">Giờ Việt Nam (GMT+7)</span>
              </div>
              <input
                type="datetime-local"
                className="form-item-input"
                value={dueDate}
                onChange={e => setDueDate(e.target.value)}
                required
              />
            </div>

            {/* Checkbox: Late Submission */}
            <label className="custom-checkbox-row">
              <input type="checkbox" checked={allowLateSubmission} onChange={e => setAllowLateSubmission(e.target.checked)} />
              <span className="checkbox-label-text">Cho phép nộp muộn sau hạn chót</span>
            </label>

            {/* Checkbox: Notify Students */}
            <label className="custom-checkbox-row">
              <input type="checkbox" checked={notifyStudents} onChange={e => setNotifyStudents(e.target.checked)} />
              <div>
                <span className="checkbox-label-text">Gửi thông báo cho học sinh khi giao</span>
                <p className="field-hint-small">Công việc sẽ xuất hiện trong danh sách việc cần làm của học sinh.</p>
              </div>
            </label>
          </div>

          {/* Action Confirmation Box */}
          <div className="side-actions-box">
            <div className="confirm-summary-text">
              <span className="confirm-icon">📢</span>
              <span>
                Chưa giao · <strong>{targetClass.studentCount} học viên</strong> sẽ nhận công việc.
              </span>
            </div>

            <div className="actions-button-row">
              {onCancel && (
                <button type="button" className="btn-pill-outline" onClick={onCancel}>
                  Hủy
                </button>
              )}
              <button type="button" className="btn-pill-outline" onClick={() => handleSubmit(true)}>
                Lưu nháp
              </button>
              <button type="button" className="btn-pill-primary btn-submit-assign" onClick={() => handleSubmit(false)}>
                Giao cho lớp
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AssignTaskTab;
