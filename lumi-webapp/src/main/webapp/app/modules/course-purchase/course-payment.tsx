import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router';

import { useAppSelector } from 'app/config/store';
import { Authority } from 'app/shared/jhipster/constants';
import { useCourseOverview } from 'app/modules/course-detail/use-course-detail';
import { useLinkedStudents } from 'app/modules/profile/use-profile';
import { useCoursePayment } from './use-course-payment';
import type { BankTransferInfo, CourseOrderStatus } from './course-purchase.model';
import { amountLabel, durationLabel, PurchaseSteps } from './purchase-shared';

import './course-purchase.scss';

const CopyValue = ({ label, value, copyValue }: { label: string; value: string; copyValue?: string }) => {
  const [message, setMessage] = useState('');
  useEffect(() => setMessage(''), [value]);
  const copy = async () => {
    try {
      await navigator.clipboard.writeText(copyValue ?? value);
      setMessage('Đã sao chép');
    } catch {
      setMessage('Không sao chép được. Bạn có thể chọn và sao chép nội dung bên trên.');
    }
  };
  return (
    <div className="payment-transfer-field">
      <dt>{label}</dt>
      <dd>
        <strong>{value}</strong>
        {copyValue !== undefined && (
          <button type="button" onClick={() => void copy()}>
            Sao chép<span className="visually-hidden"> {label}</span>
          </button>
        )}
      </dd>
      {message && (
        <p className="purchase-help" role="status">
          {message}
        </p>
      )}
    </div>
  );
};

const PaymentQr = ({ bank, orderId, seconds }: { bank: BankTransferInfo; orderId: number; seconds: number }) => {
  const [imageFailed, setImageFailed] = useState(false);
  const [imageRevision, setImageRevision] = useState(0);
  const [downloadError, setDownloadError] = useState('');
  const [downloading, setDownloading] = useState(false);
  const download = async () => {
    if (downloading) return;
    setDownloading(true);
    setDownloadError('');
    try {
      const response = await fetch(bank.qrImageUrl, { credentials: 'omit', signal: AbortSignal.timeout(10000) });
      if (!response.ok) throw new Error('Không tải được ảnh');
      const blob = await response.blob();
      if (!blob.type.startsWith('image/')) throw new Error('Không phải ảnh');
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `LUMI${orderId}.png`;
      link.click();
      setTimeout(() => URL.revokeObjectURL(url), 1000);
    } catch {
      setDownloadError('Không tải trực tiếp được ảnh. Mở ảnh QR để lưu từ trình duyệt.');
    } finally {
      setDownloading(false);
    }
  };
  return (
    <section className="purchase-panel payment-qr-panel">
      <div className="payment-panel-heading">
        <h2>Quét mã để thanh toán</h2>
        <span>VietQR / SePay</span>
      </div>
      <div className="payment-deadline">
        <div>
          <strong>Hoàn tất thanh toán trong</strong>
          <p>Tải lại trang không gia hạn thời gian của đơn.</p>
        </div>
        <strong className="payment-countdown" aria-label="Thời gian còn lại">
          {String(Math.floor(seconds / 60)).padStart(2, '0')}:{String(seconds % 60).padStart(2, '0')}
        </strong>
      </div>
      <div className="payment-transfer">
        <div className="payment-qr-column">
          <div className="payment-qr-image">
            {imageFailed ? (
              <div role="alert">
                <p>Không tải được ảnh QR.</p>
                <button
                  className="purchase-text-button"
                  type="button"
                  onClick={() => {
                    setImageFailed(false);
                    setImageRevision(value => value + 1);
                  }}
                >
                  Tải lại ảnh
                </button>
              </div>
            ) : (
              <img
                key={imageRevision}
                src={bank.qrImageUrl}
                alt={`Mã QR thanh toán đơn LUMI${orderId}`}
                onError={() => setImageFailed(true)}
              />
            )}
          </div>
          <button className="payment-secondary-button" type="button" disabled={downloading} onClick={() => void download()}>
            {downloading ? 'Đang tải…' : 'Tải mã QR'}
          </button>
          {downloadError && (
            <p role="status" className="purchase-help">
              {downloadError}{' '}
              <a href={bank.qrImageUrl} target="_blank" rel="noopener noreferrer">
                Mở ảnh QR
              </a>
            </p>
          )}
        </div>
        <dl className="payment-bank-details">
          <CopyValue label="Ngân hàng" value={bank.bankName} />
          <CopyValue label="Chủ tài khoản" value={bank.accountName} />
          <CopyValue label="Số tài khoản" value={bank.accountNumber} copyValue={bank.accountNumber} />
          <CopyValue label="Số tiền chuyển khoản" value={amountLabel(bank.amount)} copyValue={String(bank.amount)} />
          <CopyValue label="Nội dung chuyển khoản" value={bank.transferContent} copyValue={bank.transferContent} />
        </dl>
      </div>
      <div className="payment-notice">
        <strong>Giữ nguyên số tiền và nội dung chuyển khoản</strong>
        <p>Khi quét QR, kiểm tra thông tin trên ứng dụng ngân hàng trước khi xác nhận.</p>
      </div>
    </section>
  );
};

const statusTitles: Record<CourseOrderStatus, string> = {
  PENDING: 'Đang chờ thanh toán',
  PAID: 'Thanh toán thành công',
  EXPIRED: 'Đơn hàng đã hết hạn',
  CANCELLED: 'Đơn hàng đã bị hủy',
  REFUNDED: 'Đơn hàng đã được hoàn tiền',
};

const paymentResultMessage = (status: CourseOrderStatus, studentName: string) => {
  if (status === 'PAID') return `Thanh toán đã được hệ thống xác nhận. Quyền học của đơn được cấp cho ${studentName}.`;
  if (status === 'EXPIRED') return 'QR không còn hiển thị. Nếu đã chuyển tiền, hãy kiểm tra lại hoặc liên hệ hỗ trợ trước khi tạo đơn mới.';
  if (status === 'PENDING') return 'Chưa có thông tin QR để tiếp tục. Vui lòng kiểm tra trạng thái hoặc thử lại.';
  return 'Đơn này không còn yêu cầu thanh toán. Không chuyển tiền theo mã QR cũ.';
};

const CoursePayment = () => {
  const { orderId: routeId } = useParams<{ orderId: string }>();
  const parsed = Number(routeId);
  const orderId = routeId && /^\d+$/.test(routeId) && Number.isSafeInteger(parsed) && parsed > 0 ? parsed : null;
  const isParent = useAppSelector(
    state => state.authentication.isAuthenticated && (state.authentication.account.authorities ?? []).includes(Authority.PARENT),
  );
  const request = useCoursePayment(orderId, isParent);
  const { order, bank } = request;
  const overview = useCourseOverview(order?.courseId ?? null);
  const children = useLinkedStudents(isParent && Boolean(order));
  const navigate = useNavigate();
  const [restartError, setRestartError] = useState('');
  const title = overview.data?.title || (order ? `Khóa học #${order.courseId}` : '');
  const studentName =
    children.data?.find(child => child.studentId === order?.studentId)?.fullName?.trim() || (order ? `Học sinh #${order.studentId}` : '');
  useEffect(() => {
    window.scrollTo(0, 0);
    setRestartError('');
  }, [orderId]);
  const restart = () => {
    if (order?.status !== 'EXPIRED') return;
    try {
      sessionStorage.removeItem(`lumi.course-order-attempt:${order.buyerId}:${order.courseId}:${order.studentId}`);
      void navigate(`/courses/${order.courseId}/checkout`);
    } catch {
      setRestartError('Chưa thể bắt đầu yêu cầu mới. Vui lòng thử lại.');
    }
  };

  if (!isParent)
    return (
      <main className="lumi-course-purchase">
        <p className="purchase-feedback">Chỉ phụ huynh được xem thanh toán.</p>
      </main>
    );
  const waitingForDeadline = order?.status === 'PENDING' && request.remainingSeconds === 0;
  return (
    <main className="lumi-course-purchase lumi-course-payment">
      <div className="purchase-content">
        <Link className="purchase-back" to={order ? `/courses/${order.courseId}/checkout` : '/courses'}>
          ‹ Quay lại {order ? 'thông tin đăng ký' : 'danh sách khóa học'}
        </Link>
        <header className="purchase-heading">
          <h1>{order?.status === 'PAID' ? 'Thanh toán thành công' : 'Thanh toán khóa học'}</h1>
          <p>{order ? `Thanh toán cho con: ${studentName} · ${title}` : 'Kiểm tra thông tin đơn hàng và thanh toán bằng QR.'}</p>
          <PurchaseSteps active={order?.status === 'PAID' ? 2 : 1} />
        </header>
        {orderId === null && (
          <p className="purchase-feedback" role="alert">
            Mã đơn hàng không hợp lệ.
          </p>
        )}
        {request.loading && (
          <p className="purchase-feedback" role="status">
            Đang tải thông tin thanh toán…
          </p>
        )}
        {request.error && (
          <div className="purchase-feedback" role="alert">
            <p>{request.error}</p>
            <button className="purchase-text-button" type="button" disabled={request.checking} onClick={request.refresh}>
              Thử lại
            </button>
          </div>
        )}
        {order && (
          <div className="purchase-columns payment-columns">
            <div className="payment-main">
              {bank ? (
                <PaymentQr
                  key={`${order.orderId}:${bank.qrImageUrl}`}
                  bank={bank}
                  orderId={order.orderId}
                  seconds={request.remainingSeconds}
                />
              ) : (
                <section className="purchase-panel payment-result" role="status">
                  <h2>{waitingForDeadline ? 'Đang xác nhận trạng thái đơn' : statusTitles[order.status]}</h2>
                  <p>{paymentResultMessage(order.status, studentName)}</p>
                  {order.status === 'PAID' && (
                    <>
                      <Link to="/courses">Quay lại danh sách khóa học →</Link>{' '}
                      <Link to={`/refund-requests?orderId=${order.orderId}`}>Yêu cầu hoàn tiền</Link>
                    </>
                  )}
                  {order.status === 'EXPIRED' && (
                    <button className="payment-secondary-button" type="button" onClick={restart}>
                      Chưa chuyển tiền? Quay lại xác nhận mua
                    </button>
                  )}
                  {restartError && <p role="alert">{restartError}</p>}
                </section>
              )}
              {order.status === 'PENDING' && (
                <section className="purchase-panel payment-guide">
                  <h2>Thanh toán trong 3 bước</h2>
                  <ol>
                    {[
                      ['Mở ứng dụng ngân hàng', 'Chọn quét QR hoặc tải ảnh QR lên.'],
                      ['Kiểm tra và chuyển tiền', 'Đối chiếu người nhận, số tiền và nội dung chuyển khoản.'],
                      ['Chờ xác nhận trên LUMI', 'Trang tự cập nhật khi giao dịch được xác nhận.'],
                    ].map(([heading, description], index) => (
                      <li key={heading}>
                        <span>{String(index + 1).padStart(2, '0')}</span>
                        <div>
                          <strong>{heading}</strong>
                          <p>{description}</p>
                        </div>
                      </li>
                    ))}
                  </ol>
                </section>
              )}
            </div>
            <aside className="payment-sidebar">
              <section className="purchase-panel purchase-summary payment-order-summary">
                <h2>Thông tin đơn hàng</h2>
                <dl>
                  <div>
                    <dt>Mã đơn</dt>
                    <dd>LUMI{order.orderId}</dd>
                  </div>
                </dl>
                <strong>{title}</strong>
                <dl>
                  <div>
                    <dt>Con nhận quyền học</dt>
                    <dd>{studentName}</dd>
                  </div>
                  <div>
                    <dt>Thời hạn truy cập</dt>
                    <dd>{durationLabel(order.accessDurationValue, order.accessDurationUnit)}</dd>
                  </div>
                  <div>
                    <dt>Học phí</dt>
                    <dd>{amountLabel(order.originalPrice)}</dd>
                  </div>
                  <div>
                    <dt>Thuế</dt>
                    <dd>{amountLabel(order.taxAmount)}</dd>
                  </div>
                </dl>
                <div className="purchase-total">
                  <span>Tổng thanh toán</span>
                  <strong>{amountLabel(order.finalPrice)}</strong>
                </div>
              </section>
              <section className="purchase-panel payment-status">
                <h2>{statusTitles[order.status]}</h2>
                <p>
                  {order.status === 'PENDING'
                    ? 'Trang tự kiểm tra trạng thái đơn. Chỉ xác nhận thành công khi nhận kết quả từ hệ thống.'
                    : 'Trạng thái được lấy từ hệ thống.'}
                </p>
                <button className="payment-secondary-button" type="button" disabled={request.checking} onClick={request.refresh}>
                  {request.checking ? 'Đang kiểm tra…' : 'Kiểm tra trạng thái'}
                </button>
                {order.status !== 'PAID' && (
                  <p className="purchase-help">
                    Đã chuyển tiền nhưng chưa cập nhật? Không chuyển lại; hãy kiểm tra trạng thái hoặc liên hệ hỗ trợ.
                  </p>
                )}
                <Link to="/support-tickets">Liên hệ hỗ trợ LUMI →</Link>
              </section>
            </aside>
          </div>
        )}
      </div>
    </main>
  );
};

export default CoursePayment;
