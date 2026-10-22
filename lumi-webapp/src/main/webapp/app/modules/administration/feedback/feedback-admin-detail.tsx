import React, { useEffect, useState } from 'react';
import { Button, Form, Modal } from 'react-bootstrap';
import { TextFormat, Translate, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faArrowLeft, faCheck, faSave, faTrash } from '@fortawesome/free-solid-svg-icons';
import { toast } from 'react-toastify';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { FeedbackStatus, FeedbackType } from 'app/shared/model/feedback.model';
import { deleteFeedback, getFeedback, updateFeedbackNotes, updateFeedbackStatus } from 'app/entities/feedback/feedback.reducer';
import './feedback-admin.scss';

// Valid state transitions per state machine rules
export const getAvailableTransitions = (currentStatus?: FeedbackStatus): FeedbackStatus[] => {
  switch (currentStatus) {
    case FeedbackStatus.NEW:
      return [FeedbackStatus.IN_REVIEW];
    case FeedbackStatus.IN_REVIEW:
      return [FeedbackStatus.RESOLVED, FeedbackStatus.DISMISSED];
    case FeedbackStatus.RESOLVED:
      return [FeedbackStatus.IN_REVIEW, FeedbackStatus.DISMISSED];
    case FeedbackStatus.DISMISSED:
      return [FeedbackStatus.IN_REVIEW, FeedbackStatus.RESOLVED];
    default:
      return [];
  }
};

export const FeedbackAdminDetail = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const feedback = useAppSelector(state => state.feedback.entity);
  const loading = useAppSelector(state => state.feedback.loading);
  const updating = useAppSelector(state => state.feedback.updating);

  const [adminNotes, setAdminNotes] = useState('');
  const [selectedStatus, setSelectedStatus] = useState<FeedbackStatus | ''>('');
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);

  useEffect(() => {
    if (id) {
      dispatch(getFeedback(id));
    }
  }, [id, dispatch]);

  useEffect(() => {
    if (feedback?.id) {
      setAdminNotes(feedback.adminNotes || '');
    }
  }, [feedback]);

  const handleSaveNotes = () => {
    if (feedback?.id) {
      if (adminNotes.length > 5000) {
        toast.error('Ghi chú không được vượt quá 5.000 ký tự');
        return;
      }
      dispatch(
        updateFeedbackNotes({
          id: feedback.id,
          adminNotes: adminNotes.trim() ? adminNotes.trim() : null,
        }),
      ).then(res => {
        if (!('error' in res)) {
          toast.success(translate('lumiApp.feedback.admin.notesSaved') || 'Đã lưu ghi chú thành công');
        }
      });
    }
  };

  const handleStatusChange = () => {
    if (feedback?.id && selectedStatus && selectedStatus !== feedback.status) {
      dispatch(
        updateFeedbackStatus({
          id: feedback.id,
          status: selectedStatus as FeedbackStatus,
        }),
      ).then(res => {
        if (!('error' in res)) {
          toast.success(translate('lumiApp.feedback.admin.statusUpdated') || 'Đã cập nhật trạng thái');
          setSelectedStatus('');
        }
      });
    }
  };

  const handleDelete = () => {
    if (feedback?.id) {
      dispatch(deleteFeedback(feedback.id)).then(res => {
        if (!('error' in res)) {
          toast.success(translate('lumiApp.feedback.admin.deleted') || 'Đã xóa phản hồi');
          navigate('/admin/feedback');
        }
      });
    }
  };

  const statusLabel = (st?: FeedbackStatus) => {
    if (!st) return '';
    const key = `lumiApp.feedback.statuses.${st}`;
    return translate(key);
  };

  const typeLabel = (tp?: FeedbackType) => {
    if (!tp) return '';
    const key = `lumiApp.feedback.types.${tp}`;
    return translate(key);
  };

  if (loading) {
    return <div className="p-4 text-center">Đang tải chi tiết phản hồi...</div>;
  }

  if (!feedback?.id) {
    return (
      <div className="p-4 text-center text-muted">
        Không tìm thấy thông tin phản hồi.
        <div className="mt-3">
          <Link to="/admin/feedback" className="btn btn-outline-primary">
            Quay lại danh sách
          </Link>
        </div>
      </div>
    );
  }

  const transitions = getAvailableTransitions(feedback.status);

  return (
    <div className="feedback-detail-page">
      <div className="detail-header">
        <div>
          <Link to="/admin/feedback" className="btn btn-outline-secondary btn-sm mb-2" data-cy="backBtn">
            <FontAwesomeIcon icon={faArrowLeft} /> &nbsp; Quay lại danh sách
          </Link>
          <h2 data-cy="feedbackDetailHeading">Chi tiết phản hồi #{feedback.id}</h2>
        </div>
        <div>
          <Button variant="danger" size="sm" onClick={() => setDeleteModalOpen(true)} data-cy="deleteFeedbackDetailBtn">
            <FontAwesomeIcon icon={faTrash} /> &nbsp; Xóa phản hồi
          </Button>
        </div>
      </div>

      {/* Group A: Basic Information */}
      <div className="detail-card">
        <div className="card-title-bar">
          <Translate contentKey="lumiApp.feedback.detail.sectionBasic">Thông tin phản hồi</Translate>
        </div>
        <div className="card-body-content">
          <div className="field-row">
            <span className="field-label">Mã phản hồi:</span>
            <span className="field-value fw-bold">#{feedback.id}</span>
          </div>
          <div className="field-row">
            <span className="field-label">Loại phản hồi:</span>
            <span className="field-value">
              <span className="badge-type">{typeLabel(feedback.type)}</span>
            </span>
          </div>
          <div className="field-row">
            <span className="field-label">Tiêu đề:</span>
            <span className="field-value fw-semibold">{feedback.title}</span>
          </div>
          <div className="field-row">
            <span className="field-label">Người gửi:</span>
            <span className="field-value">
              {feedback.senderIdentity ? (
                feedback.senderIdentity === 'Tài khoản đã bị xóa' ? (
                  <span className="deleted-user-label">{translate('lumiApp.feedback.admin.deletedAccount')}</span>
                ) : (
                  <span>{feedback.senderIdentity}</span>
                )
              ) : (
                <span className="deleted-user-label">{translate('lumiApp.feedback.admin.deletedAccount')}</span>
              )}
            </span>
          </div>
          <div className="field-row">
            <span className="field-label">Email liên hệ:</span>
            <span className="field-value fw-semibold">{feedback.contactEmail}</span>
          </div>
          <div className="field-row">
            <span className="field-label">Thời gian gửi:</span>
            <span className="field-value">
              {feedback.createdAt && <TextFormat value={feedback.createdAt} type="date" format={APP_DATE_FORMAT} blankOnInvalid />}
            </span>
          </div>
          <div className="field-row">
            <span className="field-label">Cập nhật lần cuối:</span>
            <span className="field-value">
              {feedback.updatedAt && <TextFormat value={feedback.updatedAt} type="date" format={APP_DATE_FORMAT} blankOnInvalid />}
            </span>
          </div>
          <div className="mt-3">
            <span className="field-label d-block mb-1">Nội dung phản hồi:</span>
            <div className="content-box" data-cy="feedbackDetailContent">
              {feedback.content}
            </div>
          </div>
        </div>
      </div>

      {/* Group B: Learning Context */}
      <div className="detail-card">
        <div className="card-title-bar">
          <Translate contentKey="lumiApp.feedback.detail.sectionContext">Ngữ cảnh học tập</Translate>
        </div>
        <div className="card-body-content">
          <div className="field-row">
            <span className="field-label">Khóa học liên quan:</span>
            <span className="field-value">
              {feedback.course ? (
                <span>
                  <strong>#{feedback.course.id}</strong> — {feedback.course.title}
                </span>
              ) : (
                <span className="text-muted">Không gắn với khóa học</span>
              )}
            </span>
          </div>
          <div className="field-row">
            <span className="field-label">Bài học liên quan:</span>
            <span className="field-value">
              {feedback.lesson ? (
                <span>
                  <strong>#{feedback.lesson.id}</strong> — {feedback.lesson.title}
                </span>
              ) : (
                <span className="text-muted">Không gắn với bài học</span>
              )}
            </span>
          </div>
        </div>
      </div>

      {/* Group C: Processing & Administration */}
      <div className="detail-card">
        <div className="card-title-bar">
          <Translate contentKey="lumiApp.feedback.detail.sectionProcessing">Xử lý & Quản trị</Translate>
        </div>
        <div className="card-body-content">
          {/* Status and State Machine */}
          <div className="field-row align-items-center">
            <span className="field-label">Trạng thái hiện tại:</span>
            <span className="field-value">
              <span className={`badge-status status-${feedback.status}`} data-cy="currentStatusBadge">
                {statusLabel(feedback.status)}
              </span>
            </span>
          </div>

          <div className="field-row align-items-center mt-3">
            <span className="field-label">Chuyển trạng thái:</span>
            <div className="field-value d-flex gap-2 align-items-center flex-wrap">
              {transitions.length > 0 ? (
                <>
                  <Form.Select
                    size="sm"
                    style={{ width: '200px' }}
                    value={selectedStatus}
                    onChange={e => setSelectedStatus(e.target.value as FeedbackStatus)}
                    data-cy="nextStatusSelect"
                  >
                    <option value="">-- Chọn trạng thái mới --</option>
                    {transitions.map(st => (
                      <option key={st} value={st}>
                        {statusLabel(st)}
                      </option>
                    ))}
                  </Form.Select>
                  <Button
                    variant="primary"
                    size="sm"
                    disabled={!selectedStatus || updating}
                    onClick={handleStatusChange}
                    data-cy="updateStatusBtn"
                  >
                    <FontAwesomeIcon icon={faCheck} /> &nbsp; Cập nhật
                  </Button>
                </>
              ) : (
                <span className="text-muted">Không có trạng thái kế tiếp khả dụng</span>
              )}
            </div>
          </div>

          {/* Admin Notes */}
          <div className="mt-4">
            <span className="field-label d-block mb-1">Ghi chú nội bộ (Admin Notes):</span>
            <Form.Control
              as="textarea"
              rows={4}
              maxLength={5000}
              value={adminNotes}
              onChange={e => setAdminNotes(e.target.value)}
              placeholder="Nhập ghi chú xử lý nội bộ..."
              data-cy="adminNotesTextarea"
            />
            <div className="d-flex justify-content-between align-items-center mt-2">
              <span className="text-muted small">{adminNotes.length}/5000 ký tự</span>
              <Button variant="success" size="sm" onClick={handleSaveNotes} disabled={updating} data-cy="saveNotesBtn">
                <FontAwesomeIcon icon={faSave} /> &nbsp; Lưu ghi chú
              </Button>
            </div>
          </div>
        </div>
      </div>

      {/* Delete Confirmation Modal */}
      <Modal show={deleteModalOpen} onHide={() => setDeleteModalOpen(false)} centered>
        <Modal.Header closeButton>
          <Modal.Title>Xác nhận xóa phản hồi</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Translate contentKey="lumiApp.feedback.admin.deleteConfirm" interpolate={{ id: feedback.id }}>
            {`Bạn có chắc chắn muốn xóa phản hồi #${feedback.id} này không?`}
          </Translate>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setDeleteModalOpen(false)}>
            Hủy
          </Button>
          <Button variant="danger" onClick={handleDelete} disabled={updating} data-cy="confirmDeleteBtn">
            {updating ? 'Đang xóa...' : 'Xóa phản hồi'}
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default FeedbackAdminDetail;
