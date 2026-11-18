import React, { useEffect, useRef, useState } from 'react';
import { Alert, Row, Col, Card, Form, Button, Modal, Spinner } from 'react-bootstrap';
import { toast } from 'react-toastify';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import {
  getCourseBuilder,
  submitCourseReview,
  withdrawCourseReview,
  publishSystemCourse,
  unpublishSystemCourse,
} from '../course-builder.reducer';
import { isBuilderReadOnly } from '../course-lifecycle';
import { builderErrorText } from '../course-builder-error';
import { ReviewChecklist, ReviewCourseSummary, ReviewRequestStatus, type ReviewEditTab } from './review-sections';
import './course-review.scss';

interface CourseReviewSubmitProps {
  onBack: () => void;
  onNavigateTab?: (tab: ReviewEditTab) => void;
  onSubmitSuccess?: () => void;
  isSystemCourse?: boolean;
}

interface WithdrawModalProps {
  show: boolean;
  withdrawing: boolean;
  withdrawError: boolean;
  onClose: () => void;
  onConfirm: () => void;
}

const WithdrawModal = ({ show, withdrawing, withdrawError, onClose, onConfirm }: WithdrawModalProps) => (
  <Modal show={show} centered backdrop="static" onHide={onClose} keyboard={!withdrawing}>
    <Modal.Header closeButton={!withdrawing}>
      <Modal.Title>Rút lại yêu cầu duyệt?</Modal.Title>
    </Modal.Header>
    <Modal.Body>
      Khóa học sẽ quay về bản nháp để bạn chỉnh sửa. Lần gửi duyệt này vẫn được giữ trong lịch sử.
      {withdrawError && (
        <Alert variant="danger" className="mt-3 mb-0">
          Không thể rút duyệt. Hãy đóng hộp thoại và tải lại dữ liệu; yêu cầu có thể đã được xử lý hoặc phiên bản đã thay đổi.
        </Alert>
      )}
    </Modal.Body>
    <Modal.Footer>
      <Button variant="secondary" disabled={withdrawing} onClick={onClose}>
        Hủy
      </Button>
      <Button
        variant="warning"
        disabled={withdrawing || withdrawError}
        onClick={() => {
          void onConfirm();
        }}
      >
        {withdrawing ? 'Đang rút duyệt...' : 'Xác nhận rút duyệt'}
      </Button>
    </Modal.Footer>
  </Modal>
);

interface ConfirmSubmitModalProps {
  show: boolean;
  courseTitle?: string;
  saving: boolean;
  canSubmit: boolean;
  onClose: () => void;
  onConfirm: () => void;
}

const ConfirmSubmitModal = ({ show, courseTitle, saving, canSubmit, onClose, onConfirm }: ConfirmSubmitModalProps) => (
  <Modal
    show={show}
    onHide={() => {
      if (!saving) onClose();
    }}
    backdrop="static"
    centered
  >
    <Modal.Header closeButton={!saving}>
      <Modal.Title>Xác nhận gửi duyệt</Modal.Title>
    </Modal.Header>
    <Modal.Body>
      <p>
        Bạn sắp gửi khóa học <strong>{courseTitle}</strong> để phê duyệt.
      </p>
      <p className="mb-0">Khóa học sẽ được khóa chỉnh sửa trong lúc chờ duyệt. Thao tác này chưa xuất bản hoặc mở đăng ký khóa học.</p>
    </Modal.Body>
    <Modal.Footer>
      <Button variant="secondary" disabled={saving} onClick={onClose}>
        Kiểm tra lại
      </Button>
      <Button
        disabled={!canSubmit}
        onClick={() => {
          void onConfirm();
        }}
      >
        {saving ? 'Đang gửi...' : 'Xác nhận gửi duyệt'}
      </Button>
    </Modal.Footer>
  </Modal>
);

interface ReviewNotesAndRightsProps {
  note: string;
  setNote: (note: string) => void;
  confirmedRights: boolean;
  setConfirmedRights: (checked: boolean) => void;
  saving: boolean;
  blocked: boolean;
}

const ReviewNotesAndRights = ({ note, setNote, confirmedRights, setConfirmedRights, saving, blocked }: ReviewNotesAndRightsProps) => (
  <>
    <Card className="review-card p-4">
      <h5 className="fw-bold">Ghi chú khi gửi duyệt</h5>
      <Form.Group controlId="review-note">
        <Form.Label className="small text-muted">Lời nhắn kèm yêu cầu (không bắt buộc)</Form.Label>
        <Form.Control
          as="textarea"
          rows={4}
          value={note}
          disabled={saving}
          maxLength={5000}
          onChange={event => setNote(event.target.value)}
          placeholder="Thông tin bạn muốn người duyệt lưu ý..."
        />
      </Form.Group>
      <div className="small text-muted mt-2">
        {note.length}/5000 ký tự • Ghi chú được giữ trong phiên trình duyệt và chỉ gửi lên khi bạn xác nhận.
      </div>
    </Card>
    <Card className="review-card p-4">
      <h5 className="fw-bold mb-3">Xác nhận quyền sử dụng nội dung</h5>
      <Form.Check
        id="confirm-rights-checkbox"
        type="checkbox"
        checked={confirmedRights}
        disabled={blocked}
        onChange={event => setConfirmedRights(event.target.checked)}
        label="Tôi xác nhận có quyền sử dụng video, hình ảnh và tài liệu trong khóa học, và đã kiểm tra nội dung trước khi gửi yêu cầu phê duyệt."
      />
    </Card>
  </>
);

interface ReviewSubmitActionCardProps {
  readOnly: boolean;
  canSubmit: boolean;
  canSubmitReview?: boolean;
  saving: boolean;
  isPendingRequest: boolean;
  onOpenConfirmation: () => void;
}

const ReviewSubmitActionCard = ({
  readOnly,
  canSubmit,
  canSubmitReview,
  saving,
  isPendingRequest,
  onOpenConfirmation,
}: ReviewSubmitActionCardProps) => {
  const title = readOnly ? 'Theo dõi yêu cầu phê duyệt' : 'Sẵn sàng gửi bản nháp?';
  const subtitle = canSubmitReview
    ? 'Hoàn tất xác nhận quyền nội dung để gửi yêu cầu.'
    : 'Kiểm tra trạng thái và hoàn tất các mục bắt buộc trước khi gửi.';
  const submitText = saving ? 'Đang gửi...' : isPendingRequest ? 'Đã gửi duyệt' : 'Gửi duyệt';

  return (
    <Card className="review-card p-4 d-flex flex-row flex-wrap justify-content-between align-items-center gap-3">
      <div>
        <h5 className="fw-bold mb-1">{title}</h5>
        <div className="text-muted small">{subtitle}</div>
      </div>
      <Button disabled={!canSubmit} onClick={onOpenConfirmation}>
        {submitText}
      </Button>
    </Card>
  );
};

const readNote = (key: string) => {
  try {
    return sessionStorage.getItem(key) || '';
  } catch {
    return '';
  }
};

const useReviewNote = (courseId?: number) => {
  const key = `course-review-note-${courseId}`;
  const [note, setNote] = useState(() => readNote(key));
  useEffect(() => {
    try {
      if (note) sessionStorage.setItem(key, note);
      else sessionStorage.removeItem(key);
    } catch {
      /* Storage may be unavailable. The form still retains the note. */
    }
  }, [key, note]);
  return [note, setNote] as const;
};

const getReviewErrorMessage = (errorStatus: number | null | undefined, errorMessage: string | null | undefined) => {
  if (!errorMessage) return null;
  if (errorStatus === 409) {
    return 'Dữ liệu hoặc trạng thái khóa học đã thay đổi. Tải lại để kiểm tra trước khi gửi tiếp; ghi chú của bạn được giữ lại.';
  }
  return errorMessage;
};

export const CourseReviewSubmit = ({ onBack, onNavigateTab, onSubmitSuccess, isSystemCourse }: CourseReviewSubmitProps) => {
  const dispatch = useAppDispatch();
  const { data, loading, saving, errorStatus, errorMessage } = useAppSelector(state => state.courseBuilder);
  const courseId = data?.course.id;
  const isSystem = isSystemCourse || Boolean(data?.course?.isSystemCourse);
  const [note, setNote] = useReviewNote(courseId);
  const [confirmedRights, setConfirmedRights] = useState(false);
  const [confirmationVersion, setConfirmationVersion] = useState<number | null>(null);
  const requestRef = useRef(false);
  const [showWithdraw, setShowWithdraw] = useState(false);
  const [withdrawing, setWithdrawing] = useState(false);
  const [withdrawError, setWithdrawError] = useState(false);

  const handlePublishSystem = async () => {
    if (!data || requestRef.current) return;
    requestRef.current = true;
    try {
      await dispatch(publishSystemCourse({ courseId: data.course.id, version: data.course.version })).unwrap();
      toast.success('Xuất bản khóa học hệ thống thành công! Khóa học đã mở cho học sinh.');
      onSubmitSuccess?.();
    } catch (err: any) {
      toast.error(err?.message || 'Không thể xuất bản khóa học.');
    } finally {
      requestRef.current = false;
    }
  };

  const handleUnpublishSystem = async () => {
    if (!data || requestRef.current) return;
    requestRef.current = true;
    try {
      await dispatch(unpublishSystemCourse({ courseId: data.course.id, version: data.course.version })).unwrap();
      toast.success('Đã tạm ẩn khóa học hệ thống.');
      onSubmitSuccess?.();
    } catch (err: any) {
      toast.error(err?.message || 'Không thể tạm ẩn khóa học.');
    } finally {
      requestRef.current = false;
    }
  };

  const closeWithdraw = () => {
    if (!requestRef.current) setShowWithdraw(false);
  };

  const confirmWithdraw = async () => {
    if (!data || requestRef.current) return;
    requestRef.current = true;
    setWithdrawing(true);
    setWithdrawError(false);
    try {
      await dispatch(withdrawCourseReview({ courseId: data.course.id, version: data.course.version })).unwrap();
      setShowWithdraw(false);
      toast.success('Đã rút duyệt. Bạn có thể chỉnh sửa bản nháp.');
    } catch {
      setWithdrawError(true);
    } finally {
      requestRef.current = false;
      setWithdrawing(false);
    }
  };

  useEffect(() => {
    setConfirmedRights(false);
    setConfirmationVersion(null);
  }, [data?.course.version]);

  useEffect(() => {
    if (!courseId) return;
    const request = dispatch(getCourseBuilder(courseId));
    return () => request.abort();
  }, [courseId, dispatch]);

  const reload = () => {
    setConfirmedRights(false);
    setConfirmationVersion(null);
    if (courseId) void dispatch(getCourseBuilder(courseId));
  };

  const edit = (tab: ReviewEditTab) => onNavigateTab?.(tab);
  const review = data?.review;
  const readOnly = isBuilderReadOnly(data);
  const blocked = loading || saving || readOnly || [403, 404, 409].includes(errorStatus!);
  const canSubmit = Boolean(
    review?.canSubmit && review.version === data?.course.version && confirmedRights && note.length <= 5000 && !blocked,
  );

  const submit = async () => {
    if (!canSubmit || confirmationVersion == null || !courseId || requestRef.current) return;
    requestRef.current = true;
    try {
      await dispatch(submitCourseReview({ courseId, data: { version: confirmationVersion, note, confirmedRights } })).unwrap();
      setNote('');
      setConfirmedRights(false);
      setConfirmationVersion(null);
      toast.success('Đã gửi yêu cầu phê duyệt khóa học.');
      onSubmitSuccess?.();
    } catch (error) {
      setConfirmationVersion(null);
      toast.error(builderErrorText(error, 'Không thể gửi duyệt. Ghi chú được giữ lại; hãy tải lại danh sách kiểm tra trước khi thử lại.'));
    } finally {
      requestRef.current = false;
    }
  };

  if (!data) return <Alert variant="info">Hãy lưu bản nháp khóa học trước khi gửi duyệt.</Alert>;
  if (errorStatus === 403 || errorStatus === 404)
    return (
      <Alert variant="danger">
        Không thể truy cập khóa học để gửi duyệt.
        <Button variant="link" onClick={reload}>
          Tải lại dữ liệu
        </Button>
      </Alert>
    );

  const alertError = getReviewErrorMessage(errorStatus, errorMessage);

  return (
    <div className="course-review d-flex flex-column gap-4">
      <Alert variant="info" className="review-intro mb-0 d-flex flex-wrap justify-content-between align-items-center gap-3">
        <div>
          <strong>Kiểm tra lần cuối trước khi gửi</strong>
          <div className="small mt-1">Gửi duyệt tạo yêu cầu phê duyệt. Khóa học chưa được xuất bản hoặc mở đăng ký.</div>
        </div>
        <Button variant="outline-primary" size="sm" disabled={saving || loading} onClick={reload}>
          Tải lại danh sách kiểm tra
        </Button>
      </Alert>
      {alertError && <Alert variant="danger">{alertError}</Alert>}
      {review?.request && <ReviewRequestStatus request={review.request} />}
      {data.course.status === 'PENDING' && review?.request?.status === 'PENDING' && (
        <Button
          variant="outline-warning"
          disabled={loading || saving || withdrawing || [403, 404, 409].includes(errorStatus!)}
          onClick={() => {
            setWithdrawError(false);
            setShowWithdraw(true);
          }}
        >
          Rút lại gửi duyệt
        </Button>
      )}
      {loading && (
        <div role="status">
          <Spinner size="sm" className="me-2" />
          Đang kiểm tra bản nháp đã lưu...
        </div>
      )}
      <Row className="g-4 align-items-start">
        <Col lg={8}>
          <div className="d-flex flex-column gap-4">
            {review ? (
              <ReviewChecklist review={review} edit={edit} disabled={saving || loading} />
            ) : (
              <Alert variant="warning">Chưa tải được danh sách kiểm tra. Hãy tải lại trước khi gửi.</Alert>
            )}
            {!readOnly && !isSystem && (
              <ReviewNotesAndRights
                note={note}
                setNote={setNote}
                confirmedRights={confirmedRights}
                setConfirmedRights={setConfirmedRights}
                saving={saving}
                blocked={blocked}
              />
            )}
          </div>
        </Col>
        <Col lg={4}>
          <ReviewCourseSummary data={data} preview={() => edit('preview')} />
        </Col>
      </Row>
      {isSystem ? (
        <Card className="review-card p-4 d-flex flex-row flex-wrap justify-content-between align-items-center gap-3">
          <div>
            <h5 className="fw-bold mb-1">
              {data.course.status === 'ACTIVE' ? 'Khóa học đang hoạt động trên hệ thống' : 'Xuất bản khóa học hệ thống'}
            </h5>
            <div className="text-muted small">
              {data.course.status === 'ACTIVE'
                ? 'Khóa học hiện đang mở cho học sinh có gói học kỳ truy cập học tập.'
                : review?.canSubmit
                  ? 'Tất cả điều kiện bắt buộc đã hoàn tất. Bạn có thể xuất bản ngay mà không cần gửi duyệt.'
                  : 'Hãy hoàn thiện các mục bắt buộc trong danh sách kiểm tra trước khi xuất bản.'}
            </div>
          </div>
          <div className="d-flex gap-2">
            {data.course.status === 'ACTIVE' ? (
              <Button variant="outline-warning" disabled={saving} onClick={handleUnpublishSystem}>
                {saving ? 'Đang xử lý...' : 'Tạm ẩn khóa học'}
              </Button>
            ) : (
              <Button variant="success" disabled={!review?.canSubmit || saving} onClick={handlePublishSystem}>
                {saving ? 'Đang xuất bản...' : 'Xuất bản khóa học ngay'}
              </Button>
            )}
          </div>
        </Card>
      ) : (
        <ReviewSubmitActionCard
          readOnly={readOnly}
          canSubmit={canSubmit}
          canSubmitReview={review?.canSubmit}
          saving={saving}
          isPendingRequest={review?.request?.status === 'PENDING'}
          onOpenConfirmation={() => setConfirmationVersion(data.course.version)}
        />
      )}
      <Button variant="link" className="text-start px-0" disabled={saving} onClick={onBack}>
        ← Quay lại Xem trước
      </Button>
      <WithdrawModal
        show={showWithdraw}
        withdrawing={withdrawing}
        withdrawError={withdrawError}
        onClose={closeWithdraw}
        onConfirm={confirmWithdraw}
      />
      <ConfirmSubmitModal
        show={confirmationVersion !== null}
        courseTitle={data.course.title}
        saving={saving}
        canSubmit={canSubmit}
        onClose={() => setConfirmationVersion(null)}
        onConfirm={submit}
      />
    </div>
  );
};

export default CourseReviewSubmit;
