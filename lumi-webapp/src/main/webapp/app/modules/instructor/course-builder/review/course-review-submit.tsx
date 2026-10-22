import React, { useEffect, useRef, useState } from 'react';
import { Alert, Row, Col, Card, Form, Button, Modal, Spinner } from 'react-bootstrap';
import { toast } from 'react-toastify';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getCourseBuilder, submitCourseReview } from '../course-builder.reducer';
import { isBuilderReadOnly } from '../course-lifecycle';
import { builderErrorText } from '../course-builder-error';
import { ReviewChecklist, ReviewCourseSummary, ReviewRequestStatus, type ReviewEditTab } from './review-sections';
import './course-review.scss';

interface CourseReviewSubmitProps {
  onBack: () => void;
  onNavigateTab?: (tab: ReviewEditTab) => void;
  onSubmitSuccess?: () => void;
}

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

export const CourseReviewSubmit = ({ onBack, onNavigateTab, onSubmitSuccess }: CourseReviewSubmitProps) => {
  const dispatch = useAppDispatch();
  const { data, loading, saving, errorStatus, errorMessage } = useAppSelector(state => state.courseBuilder);
  const courseId = data?.course.id;
  const [note, setNote] = useReviewNote(courseId);
  const [confirmedRights, setConfirmedRights] = useState(false);
  const [confirmationVersion, setConfirmationVersion] = useState<number | null>(null);
  const requestRef = useRef(false);
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
      {errorMessage && (
        <Alert variant="danger">
          {errorStatus === 409
            ? 'Dữ liệu hoặc trạng thái khóa học đã thay đổi. Tải lại để kiểm tra trước khi gửi tiếp; ghi chú của bạn được giữ lại.'
            : errorMessage}
        </Alert>
      )}
      {review?.request && <ReviewRequestStatus request={review.request} />}
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
            {!readOnly && (
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
            )}
          </div>
        </Col>
        <Col lg={4}>
          <ReviewCourseSummary data={data} preview={() => edit('preview')} />
        </Col>
      </Row>
      <Card className="review-card p-4 d-flex flex-row flex-wrap justify-content-between align-items-center gap-3">
        <div>
          <h5 className="fw-bold mb-1">{readOnly ? 'Theo dõi yêu cầu phê duyệt' : 'Sẵn sàng gửi bản nháp?'}</h5>
          <div className="text-muted small">
            {review?.canSubmit
              ? 'Hoàn tất xác nhận quyền nội dung để gửi yêu cầu.'
              : 'Kiểm tra trạng thái và hoàn tất các mục bắt buộc trước khi gửi.'}
          </div>
        </div>
        <Button disabled={!canSubmit} onClick={() => setConfirmationVersion(data.course.version)}>
          {saving ? 'Đang gửi...' : review?.request?.status === 'PENDING' ? 'Đã gửi duyệt' : 'Gửi duyệt'}
        </Button>
      </Card>
      <Button variant="link" className="text-start px-0" disabled={saving} onClick={onBack}>
        ← Quay lại Xem trước
      </Button>
      <Modal
        show={confirmationVersion !== null}
        onHide={() => {
          if (!saving) setConfirmationVersion(null);
        }}
        backdrop="static"
        centered
      >
        <Modal.Header closeButton={!saving}>
          <Modal.Title>Xác nhận gửi duyệt</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p>
            Bạn sắp gửi khóa học <strong>{data.course.title}</strong> để phê duyệt.
          </p>
          <p className="mb-0">Khóa học sẽ được khóa chỉnh sửa trong lúc chờ duyệt. Thao tác này chưa xuất bản hoặc mở đăng ký khóa học.</p>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" disabled={saving} onClick={() => setConfirmationVersion(null)}>
            Kiểm tra lại
          </Button>
          <Button
            disabled={!canSubmit}
            onClick={() => {
              void submit();
            }}
          >
            {saving ? 'Đang gửi...' : 'Xác nhận gửi duyệt'}
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default CourseReviewSubmit;
