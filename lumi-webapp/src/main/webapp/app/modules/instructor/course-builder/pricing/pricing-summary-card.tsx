import React from 'react';
import { Card, Button } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCircleInfo, faClock, faEye, faLayerGroup, faPaperclip } from '@fortawesome/free-solid-svg-icons';

interface PricingSummaryCardProps {
  title: string;
  instructorName: string;
  isPaid: boolean;
  price: number | null;
  durationText: string | null;
  totalChapters: number;
  totalLessons: number;
  totalVideos: number;
  totalMaterials: number;
  durationSummary: string;
  disabled?: boolean;
  onPreviewClick?: () => void;
}

export const PricingSummaryCard = (props: PricingSummaryCardProps) => (
  <Card className="p-4 rounded-4 border-0 shadow-sm position-sticky" style={{ top: '24px' }}>
    <div className="d-flex align-items-center justify-content-between mb-3">
      <h5 className="fw-bold mb-0">Học viên sẽ thấy</h5>
      <span className="badge rounded-pill bg-light text-secondary border fw-normal py-1 px-2.5">Xem trước</span>
    </div>

    <div className="fw-bold text-dark fs-6 mb-1 text-break">{props.title}</div>
    <div className="text-muted small mb-3">{props.instructorName}</div>

    <div className="p-3 rounded-3 bg-light border border-light-subtle my-2">
      <div className="text-secondary small fw-bold text-uppercase" style={{ fontSize: '11px', letterSpacing: '0.5px' }}>
        Học phí trọn khóa
      </div>
      <div className="fw-bold fs-3 text-primary my-1">
        {!props.isPaid ? (
          <span className="text-success">Miễn phí</span>
        ) : props.price == null ? (
          <span className="text-muted fs-5 fw-normal">Chưa có giá hợp lệ</span>
        ) : (
          `${props.price.toLocaleString('vi-VN')} ₫`
        )}
      </div>
      <div className="text-muted small d-flex align-items-center gap-1">
        <FontAwesomeIcon icon={faClock} style={{ fontSize: '11px' }} />
        <span>
          {props.isPaid && 'Thanh toán một lần • '}
          {props.durationText ? `Truy cập ${props.durationText}` : 'Chưa có thời hạn hợp lệ'}
        </span>
      </div>
    </div>

    <div className="small my-3 d-flex flex-column gap-2 text-secondary">
      <div className="d-flex align-items-center gap-2">
        <FontAwesomeIcon icon={faLayerGroup} className="text-primary opacity-75" style={{ width: '14px' }} />
        <span>
          <strong>{props.totalChapters}</strong> chương • <strong>{props.totalLessons}</strong> bài học •{' '}
          <strong>{props.totalVideos}</strong> video
        </span>
      </div>
      {Boolean(props.durationSummary) && (
        <div className="d-flex align-items-center gap-2 ps-4 text-muted">
          <span>{props.durationSummary}</span>
        </div>
      )}
      <div className="d-flex align-items-center gap-2">
        <FontAwesomeIcon icon={faPaperclip} className="text-info opacity-75" style={{ width: '14px' }} />
        <span>
          <strong>{props.totalMaterials}</strong> tài liệu đính kèm
        </span>
      </div>
      {props.durationText && (
        <div className="d-flex align-items-center gap-2">
          <FontAwesomeIcon icon={faClock} className="text-warning opacity-75" style={{ width: '14px' }} />
          <span>{props.durationText} kể từ khi đăng ký thành công</span>
        </div>
      )}
    </div>

    <div
      className="text-muted small mb-3 d-flex align-items-center gap-1.5 p-2 rounded-2 bg-light border border-light-subtle"
      style={{ fontSize: '11.5px' }}
    >
      <FontAwesomeIcon icon={faCircleInfo} className="text-muted" />
      <span>Bản nháp — chưa mở đăng ký hoặc thanh toán công khai.</span>
    </div>

    <Button
      variant="outline-primary"
      size="sm"
      disabled={props.disabled}
      className="w-100 rounded-pill py-2 fw-semibold d-inline-flex align-items-center justify-content-center gap-2"
      onClick={props.onPreviewClick}
    >
      <FontAwesomeIcon icon={faEye} />
      <span>Lưu và xem trước khóa học</span>
    </Button>
  </Card>
);

export default PricingSummaryCard;
