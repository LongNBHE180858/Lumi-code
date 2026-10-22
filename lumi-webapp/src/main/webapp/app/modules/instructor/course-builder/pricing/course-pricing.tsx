import React, { useEffect, useImperativeHandle, useRef, useState } from 'react';
import { Alert, Row, Col, Card, Form, Button, Modal, InputGroup } from 'react-bootstrap';
import { useStore } from 'react-redux';
import { toast } from 'react-toastify';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faArrowLeft,
  faArrowRight,
  faCircleCheck,
  faCircleInfo,
  faClock,
  faCoins,
  faFloppyDisk,
  faGift,
} from '@fortawesome/free-solid-svg-icons';
import { useAppDispatch, useAppSelector, type IRootState } from 'app/config/store';
import {
  getCourseBuilder,
  updatePricing,
  type AccessDurationUnit,
  type ICourseData,
  type IBuilderAggregate,
} from '../course-builder.reducer';
import { builderErrorText } from '../course-builder-error';
import { chapterDuration } from '../curriculum/video-duration';
import PricingSummaryCard from './pricing-summary-card';
import { durationLabels } from './saved-pricing';
import { isBuilderReadOnly } from '../course-lifecycle';

export interface PricingNavigation {
  requestLeave: (action: () => void) => void;
}

interface CoursePricingProps {
  onBack: () => void;
  onNext: () => void;
  navigationRef?: React.Ref<PricingNavigation>;
}

const initialPricing = (course?: ICourseData) => ({
  isPaid: course?.price == null || course.price > 0,
  price: course?.price == null ? '' : String(course.price),
  duration: String(course?.accessDurationValue ?? (course?.accessDurationUnit == null ? course?.validDurationDays : undefined) ?? ''),
  unit: course?.accessDurationUnit ?? ('DAY' as AccessDurationUnit),
});

const validatePricing = (form: ReturnType<typeof initialPricing>) => {
  const price = form.isPaid ? Number(form.price) : 0;
  const duration = Number(form.duration);
  const priceError =
    form.isPaid && (!/^\d+$/.test(form.price) || !Number.isSafeInteger(price) || price <= 0)
      ? 'Nhập học phí nguyên lớn hơn 0, tối đa 999.999.999 ₫.'
      : '';
  const durationError =
    !/^\d+$/.test(form.duration) || !Number.isInteger(duration) || duration < 1 || duration > 365
      ? 'Nhập thời hạn nguyên từ 1 đến 365.'
      : '';
  const valid = !priceError && !durationError;
  return { price, duration, priceError, durationError, valid };
};

const pricingSummary = (data: IBuilderAggregate | null) => {
  const course = data?.course;
  const lessons = (data?.chapters ?? []).flatMap(chapter => chapter.lessons.map(node => node.lesson));
  const instructorName =
    [course?.author?.firstName, course?.author?.lastName].filter(Boolean).join(' ') ||
    course?.author?.login ||
    'Chưa có thông tin giảng viên';
  return {
    title: course?.title || 'Khóa học chưa đặt tên',
    instructorName,
    totalChapters: data?.chapters.length ?? 0,
    totalLessons: lessons.length,
    totalVideos: data?.totalVideos ?? 0,
    durationSummary: chapterDuration(lessons),
    totalMaterials: data?.totalMaterials ?? 0,
  };
};

export const CoursePricing = ({ onBack, onNext, navigationRef }: CoursePricingProps) => {
  const dispatch = useAppDispatch();
  const store = useStore<IRootState>();
  const { data, saving, loading, errorStatus, errorMessage } = useAppSelector(state => state.courseBuilder);
  const course = data?.course;
  const [form, setForm] = useState(() => initialPricing(course));
  const [baseline, setBaseline] = useState(() => initialPricing(course));
  const [attempted, setAttempted] = useState(false);
  const [pendingAction, setPendingAction] = useState<(() => void) | null>(null);
  const requestRef = useRef(false);
  const dirty = JSON.stringify(form) !== JSON.stringify(baseline);
  const { price, duration, priceError, durationError, valid } = validatePricing(form);
  const locked = !course || isBuilderReadOnly(data) || [403, 404, 409].includes(errorStatus!);
  const disabled = saving || loading || locked;

  useEffect(() => {
    const warn = (event: BeforeUnloadEvent) => {
      if (dirty || requestRef.current) {
        event.preventDefault();
        event.returnValue = '';
      }
    };
    window.addEventListener('beforeunload', warn);
    return () => window.removeEventListener('beforeunload', warn);
  }, [dirty]);

  const requestLeave = (action: () => void) => {
    if (saving || requestRef.current) return;
    if (dirty) setPendingAction(() => action);
    else action();
  };
  useImperativeHandle(navigationRef, () => ({ requestLeave }));

  const save = async (after?: () => void) => {
    setAttempted(true);
    if (!valid || disabled || requestRef.current) return;
    const current = store.getState().courseBuilder.data?.course;
    if (!current || current.id !== course?.id) return;
    requestRef.current = true;
    try {
      const result = await dispatch(
        updatePricing({
          courseId: current.id,
          data: {
            version: current.version,
            price,
            accessDurationValue: duration,
            accessDurationUnit: form.unit,
          },
        }),
      ).unwrap();
      const next = initialPricing(result.course);
      setForm(next);
      setBaseline(next);
      setAttempted(false);
      setPendingAction(null);
      toast.success('Đã lưu học phí và thời hạn truy cập.');
      after?.();
    } catch (error) {
      toast.error(builderErrorText(error, 'Không thể lưu học phí. Vui lòng thử lại.'));
    } finally {
      requestRef.current = false;
    }
  };

  const reload = async () => {
    if (!course) return;
    try {
      const result = await dispatch(getCourseBuilder(course.id)).unwrap();
      const next = initialPricing(result.course);
      setForm(next);
      setBaseline(next);
      setAttempted(false);
    } catch (error) {
      toast.error(builderErrorText(error, 'Không thể tải lại khóa học.'));
    }
  };

  return (
    <>
      <Form
        id="course-pricing-form"
        noValidate
        onSubmit={event => {
          event.preventDefault();
          void save();
        }}
      >
        {/* TIÊU ĐỀ & TRẠNG THÁI */}
        <div className="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-4">
          <div>
            <h4 className="fw-bold mb-1">Học phí & quyền truy cập</h4>
            <div className="d-flex flex-wrap align-items-center gap-2 mt-2">
              {valid ? (
                <span className="badge rounded-pill bg-success-subtle text-success border border-success-subtle py-1.5 px-3 fw-medium d-inline-flex align-items-center gap-1.5">
                  <FontAwesomeIcon icon={faCircleCheck} />
                  Học phí và thời hạn truy cập hợp lệ
                </span>
              ) : (
                <span className="badge rounded-pill bg-light text-secondary border py-1.5 px-3 fw-medium d-inline-flex align-items-center gap-1.5">
                  <FontAwesomeIcon icon={faCircleInfo} />
                  Hoàn thiện học phí và thời hạn truy cập để tiếp tục
                </span>
              )}

              {dirty && (
                <span className="badge rounded-pill bg-warning-subtle text-warning-emphasis border border-warning-subtle py-1.5 px-3 fw-medium d-inline-flex align-items-center gap-1.5">
                  <span className="spinner-grow spinner-grow-sm" style={{ width: '7px', height: '7px' }} />
                  Có thay đổi chưa lưu
                </span>
              )}
            </div>
          </div>
        </div>

        {errorMessage && (
          <Alert variant="danger" className="rounded-3 shadow-sm border-0 mb-4">
            {errorStatus === 409 ? 'Dữ liệu hoặc trạng thái khóa học đã thay đổi. Hãy tải lại trước khi lưu tiếp.' : errorMessage}
          </Alert>
        )}

        {locked && (
          <Alert variant="warning" className="rounded-3 shadow-sm border-0 mb-4 d-flex justify-content-between align-items-center">
            <span>Khóa học chưa sẵn sàng hoặc hiện không thể chỉnh sửa.</span>
            {course && (
              <Button
                variant="outline-warning"
                size="sm"
                className="rounded-pill px-3"
                disabled={saving || loading}
                onClick={() => {
                  if (dirty)
                    setPendingAction(() => () => {
                      void reload();
                    });
                  else void reload();
                }}
              >
                Tải lại dữ liệu
              </Button>
            )}
          </Alert>
        )}

        <Row className="g-4">
          {/* CỘT TRÁI: FORM CÀI ĐẶT */}
          <Col lg={8}>
            <fieldset disabled={disabled}>
              {/* NHÓM 1: HÌNH THỨC KHÓA HỌC (SELECTABLE CARDS) */}
              <Card className="p-4 rounded-4 border-0 shadow-sm mb-4">
                <h5 className="fw-bold mb-3">Hình thức khóa học</h5>
                <Row className="g-3">
                  <Col md={6}>
                    <div
                      className={`p-3 rounded-4 border cursor-pointer h-100 transition-all ${
                        form.isPaid
                          ? 'border-primary bg-primary-subtle bg-opacity-25 shadow-sm'
                          : 'bg-white border-light-subtle hover-bg-light'
                      }`}
                      style={{ cursor: disabled ? 'not-allowed' : 'pointer' }}
                      onClick={() => !disabled && setForm({ ...form, isPaid: true })}
                    >
                      <div className="d-flex align-items-start gap-2 mb-2">
                        <Form.Check
                          type="radio"
                          id="pricing-paid"
                          aria-label="Có học phí"
                          name="pricingModel"
                          checked={form.isPaid}
                          disabled={disabled}
                          onChange={() => setForm({ ...form, isPaid: true })}
                          className="d-none"
                        />
                        <div>
                          <div className="fw-bold text-dark d-flex align-items-center gap-1">
                            <FontAwesomeIcon icon={faCoins} className="text-primary small" />
                            <span>Có học phí</span>
                            {form.isPaid && <FontAwesomeIcon icon={faCircleCheck} className="text-primary" />}
                          </div>
                          <div className="text-muted small mt-1">Học viên thanh toán một lần để truy cập trọn vẹn khóa học.</div>
                        </div>
                      </div>
                    </div>
                  </Col>

                  <Col md={6}>
                    <div
                      className={`p-3 rounded-4 border cursor-pointer h-100 transition-all ${
                        !form.isPaid
                          ? 'border-primary bg-primary-subtle bg-opacity-25 shadow-sm'
                          : 'bg-white border-light-subtle hover-bg-light'
                      }`}
                      style={{ cursor: disabled ? 'not-allowed' : 'pointer' }}
                      onClick={() => !disabled && setForm({ ...form, isPaid: false })}
                    >
                      <div className="d-flex align-items-start gap-2 mb-2">
                        <Form.Check
                          type="radio"
                          id="pricing-free"
                          aria-label="Miễn phí"
                          name="pricingModel"
                          checked={!form.isPaid}
                          disabled={disabled}
                          onChange={() => setForm({ ...form, isPaid: false })}
                          className="d-none"
                        />
                        <div>
                          <div className="fw-bold text-dark d-flex align-items-center gap-1">
                            <FontAwesomeIcon icon={faGift} className="text-success small" />
                            <span>Miễn phí</span>
                            {!form.isPaid && <FontAwesomeIcon icon={faCircleCheck} className="text-primary" />}
                          </div>
                          <div className="text-muted small mt-1">Học viên không cần trả học phí khi đăng ký khóa học.</div>
                        </div>
                      </div>
                    </div>
                  </Col>
                </Row>
              </Card>

              {/* NHÓM 2: GIÁ & THỜI HẠN TRUY CẬP */}
              <Card className="p-4 rounded-4 border-0 shadow-sm mb-4">
                <h5 className="fw-bold mb-3">Giá & thời hạn truy cập</h5>

                {form.isPaid && (
                  <>
                    <Row className="g-3 mb-3">
                      <Col md={8}>
                        <Form.Group controlId="pricing-price">
                          <Form.Label className="fw-semibold small">
                            Học phí trọn khóa <span className="text-danger">*</span>
                          </Form.Label>
                          <InputGroup hasValidation>
                            <Form.Control
                              type="text"
                              inputMode="numeric"
                              value={form.price}
                              placeholder="Ví dụ: 499000"
                              isInvalid={(attempted || dirty) && !!priceError}
                              onChange={event => setForm({ ...form, price: event.target.value })}
                              className="py-2 rounded-start-3"
                            />
                            <InputGroup.Text className="bg-light text-secondary fw-semibold rounded-end-3">₫ (VND)</InputGroup.Text>
                            <Form.Control.Feedback type="invalid">{priceError}</Form.Control.Feedback>
                          </InputGroup>
                          {!priceError && Boolean(form.price) && (
                            <Form.Text className="text-success small d-flex align-items-center gap-1 mt-1">
                              <FontAwesomeIcon icon={faCircleCheck} />
                              Học phí: <strong>{Number(form.price).toLocaleString('vi-VN')} ₫</strong>
                            </Form.Text>
                          )}
                        </Form.Group>
                      </Col>

                      <Col md={4}>
                        <Form.Group controlId="pricing-payment">
                          <Form.Label className="fw-semibold small">Hình thức thanh toán</Form.Label>
                          <Form.Control
                            type="text"
                            value="Thanh toán một lần"
                            disabled
                            className="py-2 rounded-3 bg-light text-secondary"
                          />
                        </Form.Group>
                      </Col>
                    </Row>
                  </>
                )}

                <Row className="g-3">
                  <Col md={2}>
                    <Form.Group controlId="pricing-unit">
                      <Form.Label className="fw-semibold small">Đơn vị thời gian</Form.Label>
                      <Form.Select
                        value={form.unit}
                        onChange={event => setForm({ ...form, unit: event.target.value as AccessDurationUnit })}
                        className="py-2 rounded-3"
                      >
                        <option value="DAY">Ngày</option>
                        <option value="MONTH">Tháng</option>
                        <option value="YEAR">Năm</option>
                      </Form.Select>
                    </Form.Group>
                  </Col>

                  <Col md={2}>
                    <Form.Group controlId="pricing-duration">
                      <Form.Label className="fw-semibold small">
                        Thời gian truy cập <span className="text-danger">*</span>
                      </Form.Label>
                      <Form.Control
                        type="text"
                        inputMode="numeric"
                        placeholder="Ví dụ: 12"
                        value={form.duration}
                        isInvalid={(attempted || dirty) && !!durationError}
                        onChange={event => setForm({ ...form, duration: event.target.value })}
                        className="py-2 rounded-3"
                      />
                      <Form.Control.Feedback type="invalid">{durationError}</Form.Control.Feedback>
                    </Form.Group>
                  </Col>
                </Row>

                {!durationError && Boolean(form.duration) && (
                  <div className="p-3 rounded-3 bg-light border border-light-subtle d-flex align-items-center gap-2 mt-3 text-secondary small">
                    <FontAwesomeIcon icon={faClock} className="text-primary" />
                    <span>
                      Học viên có quyền truy cập khóa học trong{' '}
                      <strong className="text-dark">
                        {duration} {durationLabels[form.unit]}
                      </strong>{' '}
                      kể từ khi đăng ký thành công.
                    </span>
                  </div>
                )}
              </Card>
            </fieldset>

            {/* NHÓM 3: HƯỚNG DẪN & LƯU Ý */}
            <div
              className="p-3 rounded-4 d-flex align-items-start gap-3 mb-4"
              style={{ backgroundColor: '#f0f7ff', border: '1px solid #d0e7ff' }}
            >
              <FontAwesomeIcon icon={faCircleInfo} className="text-primary mt-1" />
              <div className="small">
                <div className="fw-bold text-dark mb-1">Kiểm tra cách học phí hiển thị</div>
                <div className="text-muted">
                  Bản tóm tắt bên cạnh phản ánh thiết lập đang nhập. Bạn có thể điều chỉnh giá và thời hạn ở đây trước khi gửi duyệt; lưu
                  nháp không xuất bản khóa học.
                </div>
              </div>
            </div>
          </Col>

          {/* CỘT PHẢI: TÓM TẮT HỌC VIÊN SẼ THẤY */}
          <Col lg={4}>
            <PricingSummaryCard
              {...pricingSummary(data)}
              isPaid={form.isPaid}
              price={priceError ? null : price}
              durationText={durationError ? null : `${duration} ${durationLabels[form.unit]}`}
              disabled={disabled}
              onPreviewClick={() => {
                void save(onNext);
              }}
            />
          </Col>
        </Row>

        {/* THANH ĐIỀU HƯỚNG CUỐI TRANG */}
        <div className="d-flex justify-content-between align-items-center mt-4 pt-3 border-top">
          <Button
            variant="outline-secondary"
            className="rounded-pill px-4 py-2 fw-semibold"
            disabled={saving || loading}
            onClick={() => requestLeave(onBack)}
          >
            <FontAwesomeIcon icon={faArrowLeft} className="me-2" /> Nội dung bài học
          </Button>

          <div className="d-flex gap-2">
            <Button variant="outline-primary" className="rounded-pill px-4 py-2 fw-semibold" type="submit" disabled={disabled}>
              <FontAwesomeIcon icon={faFloppyDisk} className="me-2" />
              {saving ? 'Đang lưu...' : 'Lưu nháp'}
            </Button>

            <Button
              variant="primary"
              className="rounded-pill px-4 py-2 fw-semibold"
              disabled={disabled}
              onClick={() => {
                void save(onNext);
              }}
            >
              Tiếp: Xem trước <FontAwesomeIcon icon={faArrowRight} className="ms-2" />
            </Button>
          </div>
        </div>
      </Form>

      {/* MODAL CẢNH BÁO KHI CÒN THAY ĐỔI CHƯA LƯU */}
      <Modal
        show={pendingAction !== null}
        onHide={() => {
          if (!saving) setPendingAction(null);
        }}
        backdrop="static"
        centered
      >
        <Modal.Header closeButton={!saving}>
          <Modal.Title className="fw-bold">Học phí chưa được lưu</Modal.Title>
        </Modal.Header>
        <Modal.Body className="text-secondary">
          Bạn có thay đổi học phí hoặc thời hạn truy cập chưa được lưu. Bạn có muốn lưu lại trước khi chuyển trang không?
        </Modal.Body>
        <Modal.Footer>
          <Button variant="outline-secondary" className="rounded-pill px-3" disabled={saving} onClick={() => setPendingAction(null)}>
            Ở lại
          </Button>
          <Button
            variant="outline-danger"
            className="rounded-pill px-3"
            disabled={saving}
            onClick={() => {
              const action = pendingAction;
              setPendingAction(null);
              action?.();
            }}
          >
            Bỏ thay đổi
          </Button>
          <Button
            variant="primary"
            className="rounded-pill px-3"
            disabled={disabled}
            onClick={() => {
              void save(pendingAction ?? undefined);
            }}
          >
            Lưu và tiếp tục
          </Button>
        </Modal.Footer>
      </Modal>
    </>
  );
};

export default CoursePricing;
