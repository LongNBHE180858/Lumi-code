import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router';

import bookIcon from '/content/images/course-purchase/book.svg';
import { useAppSelector } from 'app/config/store';
import { Authority } from 'app/shared/jhipster/constants';
import { useCourseOverview } from 'app/modules/course-detail/use-course-detail';
import type { CreatedCourseOrder } from './course-purchase.model';
import { useCourseCheckout } from './use-course-checkout';
import { amountLabel, durationLabel, PurchaseSteps } from './purchase-shared';

import './course-purchase.scss';

const Feedback = ({ message, retry }: { message: string; retry?: () => void }) => (
  <div className="purchase-feedback" role="alert">
    <p>{message}</p>
    {retry && (
      <button type="button" className="purchase-text-button" onClick={retry}>
        Thử lại
      </button>
    )}
  </div>
);

const CourseImage = ({ url, title, subject }: { url: string | null; title: string; subject: string }) => {
  const [failed, setFailed] = useState(false);
  return (
    <div className="purchase-course-image">
      {url?.trim() && !failed ? (
        <img src={url} alt={`Ảnh bìa ${title}`} onError={() => setFailed(true)} />
      ) : (
        <>
          <img className="purchase-book-icon" src={bookIcon} alt="" width={40} height={40} />
          <span>{subject}</span>
        </>
      )}
    </div>
  );
};

const CreatedOrder = ({ order, retryExpired }: { order: CreatedCourseOrder; retryExpired: () => void }) => {
  const messages: Record<CreatedCourseOrder['status'], string> = {
    PENDING: 'Đã tạo đơn, chưa thanh toán',
    PAID: 'Đơn hàng đã được thanh toán',
    EXPIRED: 'Đơn hàng đã hết hạn thanh toán',
    CANCELLED: 'Đơn hàng đã bị hủy',
    REFUNDED: 'Đơn hàng đã được hoàn tiền',
  };
  return (
    <section className="purchase-order-result" aria-label="Kết quả tạo đơn" role="status">
      <h3>{messages[order.status]}</h3>
      <p>
        Mã đơn: <strong>LUMI{order.orderId}</strong>
      </p>
      <p>
        Tổng tiền trên đơn: <strong>{amountLabel(order.finalPrice)}</strong>
      </p>
      {order.status === 'PENDING' && (
        <p>Việc tạo đơn chưa cấp quyền học cho con. Bạn có thể dùng mã đơn này để kiểm tra thông tin thanh toán.</p>
      )}
      {order.status === 'EXPIRED' && (
        <button type="button" className="purchase-text-button" onClick={retryExpired}>
          Kiểm tra lại để tạo đơn mới
        </button>
      )}
    </section>
  );
};

type CheckoutRequest = ReturnType<typeof useCourseCheckout>;

const NameAvatar = ({ name }: { name: string }) => {
  const initials = name
    .trim()
    .split(/\s+/)
    .slice(-2)
    .map(word => Array.from(word)[0])
    .join('')
    .toLocaleUpperCase('vi-VN');
  return (
    <span className="purchase-avatar" aria-hidden="true">
      {initials}
    </span>
  );
};

const BuyerInformation = ({ request }: { request: CheckoutRequest }) => {
  const { data: buyer, loading, error, reload } = request.buyerRequest;
  const fullName =
    buyer?.fullName?.trim() || [buyer?.firstName, buyer?.lastName].filter(Boolean).join(' ').trim() || buyer?.login || 'Phụ huynh';
  return (
    <section className="purchase-panel purchase-buyer" aria-labelledby="purchase-buyer-title">
      <h2 id="purchase-buyer-title" className="purchase-section-title">
        <span className="purchase-section-number">01</span>Thông tin người mua
      </h2>
      {loading ? (
        <p role="status">Đang tải thông tin người mua…</p>
      ) : error ? (
        <Feedback message={error} retry={reload} />
      ) : buyer ? (
        <dl className="purchase-buyer-fields">
          <div>
            <dt>Người mua</dt>
            <dd className="purchase-buyer-identity">
              <NameAvatar name={fullName} />
              <strong>{fullName}</strong>
            </dd>
          </div>
          <div>
            <dt>Email</dt>
            <dd>{buyer.email?.trim() || 'Chưa cập nhật'}</dd>
          </div>
          {buyer.phoneNumber?.trim() && (
            <div>
              <dt>Số điện thoại</dt>
              <dd>{buyer.phoneNumber}</dd>
            </div>
          )}
        </dl>
      ) : null}
    </section>
  );
};

const ChildSelector = ({ request }: { request: CheckoutRequest }) => {
  const { checkout, student, order } = request;
  const freeCourse = checkout?.finalPrice === 0;
  const singleChild = request.children.length === 1 && student !== null;
  const studentName = student?.fullName?.trim() || `Học sinh ${student?.studentId}`;
  return (
    <section className="purchase-panel purchase-child" aria-labelledby="purchase-child-title">
      <div>
        <h2 id="purchase-child-title" className="purchase-section-title">
          <span className="purchase-section-number">02</span>Chọn con nhận quyền học
        </h2>
        <p>Khóa học sẽ được cấp cho tài khoản của con bên dưới.</p>
      </div>
      {request.childrenRequest.loading ? (
        <p role="status">Đang tải danh sách con…</p>
      ) : request.childrenRequest.error ? (
        <Feedback message={request.childrenRequest.error} retry={request.childrenRequest.reload} />
      ) : (
        <>
          {singleChild ? (
            <>
              <p className="purchase-field-label" id="purchase-student-label">
                Tài khoản của con
              </p>
              <div className="purchase-selected-child" aria-labelledby="purchase-student-label">
                <NameAvatar name={studentName} />
                <strong>{studentName}</strong>
              </div>
            </>
          ) : (
            <>
              <label htmlFor="purchase-student">Tài khoản của con</label>
              <div className="purchase-select-wrapper">
                <select
                  id="purchase-student"
                  value={student?.studentId ?? ''}
                  onChange={event => request.selectStudent(Number(event.target.value))}
                  disabled={request.children.length <= 1 || request.saving || Boolean(order)}
                  aria-describedby="purchase-child-help"
                >
                  <option value="" disabled>
                    {request.children.length ? 'Chọn con nhận quyền học' : 'Chưa có con liên kết hợp lệ'}
                  </option>
                  {request.children.map(child => (
                    <option key={child.studentId} value={child.studentId}>
                      {child.fullName?.trim() || `Học sinh ${child.studentId}`}
                    </option>
                  ))}
                </select>
                <span className="purchase-select-arrow" aria-hidden="true">
                  ▾
                </span>
              </div>
              <p id="purchase-child-help" className="purchase-help">
                {request.children.length === 0
                  ? 'Chưa thể tiếp tục. Vui lòng kiểm tra liên kết với con trước khi mua.'
                  : 'Chỉ hiển thị các tài khoản đã liên kết hợp lệ với bạn.'}
              </p>
            </>
          )}
        </>
      )}
      {request.checkoutLoading && <p role="status">Đang kiểm tra thông tin mua cho con…</p>}
      {request.checkoutError && <Feedback message={request.checkoutError} retry={request.reloadCheckout} />}
      {checkout && !checkout.canPurchase && (
        <Feedback message={checkout.purchaseBlockReason || 'Con chưa đủ điều kiện mua khóa học này.'} />
      )}
      {freeCourse && <Feedback message="Khóa học này miễn phí. Đăng ký miễn phí chưa được hỗ trợ trong luồng thanh toán này." />}
    </section>
  );
};

const PurchaseSummary = ({ request, title }: { request: CheckoutRequest; title: string }) => {
  const { checkout, student, order } = request;
  const prices = order ?? checkout;
  const studentName = student?.fullName?.trim() || (student ? `Học sinh ${student.studentId}` : 'Chưa chọn con');
  return (
    <aside className="purchase-summary-column" aria-label="Tóm tắt thanh toán">
      <section className="purchase-panel purchase-summary">
        <h2>Tóm tắt thanh toán</h2>
        <strong>{title}</strong>
        <dl>
          <div>
            <dt>Người nhận quyền học</dt>
            <dd>{studentName}</dd>
          </div>
          <div>
            <dt>Học phí</dt>
            <dd>{amountLabel(prices?.originalPrice)}</dd>
          </div>
          <div>
            <dt>Thuế</dt>
            <dd>{amountLabel(prices?.taxAmount)}</dd>
          </div>
        </dl>
        <div className="purchase-total">
          <span>Tổng thanh toán</span>
          <strong>{amountLabel(prices?.finalPrice)}</strong>
          <small>{student ? 'Thanh toán một lần' : 'Chọn con để xem thông tin thanh toán'}</small>
        </div>
        {order ? (
          <CreatedOrder order={order} retryExpired={request.retryExpiredOrder} />
        ) : (
          <>
            <p className="purchase-payment-hint">Bạn sẽ nhận mã QR để chuyển khoản sau khi tiếp tục.</p>
            <button className="purchase-submit" type="button" disabled={!request.canSubmit} onClick={() => void request.submit()}>
              {request.saving ? 'Đang tạo đơn…' : 'Tiếp tục thanh toán →'}
            </button>
          </>
        )}
        {request.orderError && <Feedback message={request.orderError} />}
        <p className="purchase-entitlement">Quyền học được cấp cho con sau khi thanh toán được xác nhận thành công.</p>
      </section>
      <div className="purchase-support">
        <strong>Cần hỗ trợ?</strong>
        <Link to="/support-tickets">Liên hệ hỗ trợ LUMI</Link>
      </div>
    </aside>
  );
};

const CourseCheckout = () => {
  const { courseId: routeId } = useParams<{ courseId: string }>();
  const parsedId = Number(routeId);
  const courseId = routeId && /^\d+$/.test(routeId) && Number.isSafeInteger(parsedId) && parsedId > 0 ? parsedId : null;
  const isParent = useAppSelector(
    state => state.authentication.isAuthenticated && (state.authentication.account.authorities ?? []).includes(Authority.PARENT),
  );
  const overviewRequest = useCourseOverview(isParent ? courseId : null);
  const request = useCourseCheckout(courseId, isParent);
  const navigate = useNavigate();
  const { checkout, order } = request;
  const overview = overviewRequest.data;
  const title = checkout?.title ?? overview?.title;
  const subject = checkout?.subjectName ?? overview?.subjectName ?? 'Khóa học';
  const grade = checkout?.gradeName ?? overview?.gradeName;
  const prices = order ?? checkout;

  useEffect(() => {
    if (order) void navigate(`/course-orders/${order.orderId}/payment`, { replace: true });
  }, [order, navigate]);

  useEffect(() => {
    window.scrollTo(0, 0);
  }, [courseId]);

  if (!isParent) {
    return (
      <main className="lumi-course-purchase">
        <Feedback message="Chỉ phụ huynh được mua khóa học cho con." />
      </main>
    );
  }

  return (
    <main className="lumi-course-purchase">
      <div className="purchase-content">
        <Link className="purchase-back" to={courseId ? `/courses/${courseId}` : '/courses'}>
          ‹ Quay lại {courseId ? 'chi tiết khóa học' : 'danh sách khóa học'}
        </Link>
        <header className="purchase-heading">
          <h1>Xác nhận mua khóa học cho con</h1>
          <p>Chọn con nhận quyền học và kiểm tra thông tin trước khi thanh toán.</p>
          <PurchaseSteps active={0} />
        </header>

        {(courseId === null || overviewRequest.notFound) && <Feedback message="Khóa học không tồn tại hoặc không còn được công khai." />}
        {overviewRequest.loading && (
          <p className="purchase-feedback" role="status">
            Đang tải thông tin khóa học…
          </p>
        )}
        {overviewRequest.error && !overviewRequest.notFound && <Feedback message={overviewRequest.error} retry={overviewRequest.reload} />}

        {overview && courseId !== null && (
          <div className="purchase-columns">
            <div className="purchase-details">
              <section className="purchase-panel" aria-label="Khóa học đã chọn">
                <div className="purchase-course">
                  <CourseImage
                    key={checkout?.thumbnailUrl ?? overview.thumbnailUrl}
                    url={checkout?.thumbnailUrl ?? overview.thumbnailUrl}
                    title={title!}
                    subject={subject}
                  />
                  <div>
                    <div className="purchase-tags">
                      <span>{subject}</span>
                      {grade && <span>{grade}</span>}
                    </div>
                    <h2>{title}</h2>
                  </div>
                </div>
                <div className="purchase-course-facts">
                  <div>
                    <span>Học phí khóa học</span>
                    <strong>{amountLabel(prices?.originalPrice)}</strong>
                  </div>
                  <div>
                    <span>Thời hạn truy cập</span>
                    <strong>{durationLabel(prices?.accessDurationValue, prices?.accessDurationUnit)}</strong>
                    <small>Tính từ thời điểm thanh toán thành công</small>
                  </div>
                </div>
              </section>

              <BuyerInformation request={request} />
              <ChildSelector request={request} />

              <section className="purchase-panel">
                <h2 className="purchase-section-title">
                  <span className="purchase-section-number">03</span>Phương thức thanh toán
                </h2>
                <div className="purchase-method">
                  <span aria-hidden="true">◉</span>
                  <div>
                    <strong>Chuyển khoản ngân hàng bằng QR</strong>
                    <p>Quét mã QR hoặc chuyển khoản đúng số tiền và nội dung của đơn.</p>
                  </div>
                </div>
              </section>
            </div>

            <PurchaseSummary request={request} title={title!} />
          </div>
        )}
      </div>
    </main>
  );
};

export default CourseCheckout;
