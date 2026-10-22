import React from 'react';
import { Badge, Button, Card } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCheck, faCircleInfo } from '@fortawesome/free-solid-svg-icons';
import type { IBuilderAggregate, IReviewState } from '../course-builder.reducer';
import { savedPricing } from '../pricing/saved-pricing';
import { chapterDuration } from '../curriculum/video-duration';
import { PreviewMedia } from '../preview/preview-media';
import { courseLifecycle } from '../course-lifecycle';

export type ReviewEditTab = 'basic-info' | 'curriculum' | 'pricing' | 'preview';

export const ReviewChecklist = ({
  review,
  edit,
  disabled,
}: {
  review: IReviewState;
  edit: (tab: ReviewEditTab) => void;
  disabled: boolean;
}) => (
  <Card className="review-card p-4">
    <h5 className="fw-bold mb-3">Danh sách kiểm tra</h5>
    <div className="text-muted small mb-3">Các mục bắt buộc được kiểm tra từ bản nháp đã lưu. Mục khuyến nghị không chặn gửi duyệt.</div>
    {review.checks.map(item => (
      <div key={item.code} className="review-check">
        <FontAwesomeIcon icon={item.complete ? faCheck : faCircleInfo} className={item.complete ? 'text-success' : 'text-warning'} />
        <div className="flex-grow-1">
          <div className="fw-semibold">{item.label}</div>
          <div className="small text-muted">
            {item.complete ? 'Đã hoàn tất' : 'Cần bổ sung'} • {item.required ? 'Bắt buộc' : 'Khuyến nghị'}
          </div>
        </div>
        {!item.complete && (
          <Button size="sm" variant="outline-primary" disabled={disabled} onClick={() => edit(item.tab)}>
            Bổ sung: {item.label}
          </Button>
        )}
      </div>
    ))}
  </Card>
);

export const ReviewCourseSummary = ({ data, preview }: { data: IBuilderAggregate; preview: () => void }) => {
  const course = data.course;
  const pricing = savedPricing(course);
  const lessons = data.chapters.flatMap(chapter => chapter.lessons.map(node => node.lesson));
  return (
    <Card className="review-card p-4">
      <h5 className="fw-bold mb-3">Khóa học gửi duyệt</h5>
      {course.thumbnailUrl ? (
        <PreviewMedia target={{ courseId: course.id, kind: 'thumbnail', source: course.thumbnailUrl }} label={`Ảnh bìa ${course.title}`} />
      ) : (
        <div className="p-4 bg-light rounded-4 text-muted">Chưa có ảnh bìa.</div>
      )}
      <h6 className="fw-bold mt-3">{course.title || 'Khóa học chưa đặt tên'}</h6>
      <div className="small text-muted">{course.authorDisplayName || course.author?.login || 'Chưa có thông tin giảng viên'}</div>
      <div className="small text-muted mt-1">
        {[course.subject?.name, course.grade?.name, course.bookSeries].filter(Boolean).join(' • ') || 'Chưa có phân loại khóa học'}
      </div>
      <hr />
      <div className="small mb-2">
        {data.chapters.length} chương • {data.totalLessons} bài học • {data.totalMaterials} tài liệu
      </div>
      <div className="small text-muted mb-3">{chapterDuration(lessons)}</div>
      <div className="fw-bold fs-4 text-primary">{pricing.priceText}</div>
      <div className="small text-muted mb-3">
        {[pricing.paymentText, pricing.durationValid ? `Truy cập ${pricing.durationText}` : pricing.durationText]
          .filter(Boolean)
          .join(' • ')}
      </div>
      <Badge bg="info" className="text-primary bg-info-subtle py-2 mb-3">
        {courseLifecycle(data).label}
      </Badge>
      <Button variant="outline-primary" onClick={preview}>
        Kiểm tra lại trang xem trước →
      </Button>
    </Card>
  );
};

const dateText = (date: string) => new Date(date).toLocaleString('vi-VN');

export const ReviewRequestStatus = ({ request }: { request: NonNullable<IReviewState['request']> }) => (
  <Card className="review-card p-4 border-start border-4 border-primary">
    <h5 className="fw-bold">
      {request.status === 'PENDING'
        ? 'Yêu cầu đang chờ duyệt'
        : request.status === 'APPROVED'
          ? 'Yêu cầu đã được phê duyệt'
          : 'Yêu cầu cần bổ sung'}
    </h5>
    <div className="small text-muted">
      Yêu cầu #{request.id} • Gửi lúc {dateText(request.submittedAt)}
    </div>
    <p className="mt-3 mb-2">
      {request.status === 'PENDING'
        ? 'Bản nháp đang được khóa chỉnh sửa trong lúc chờ duyệt. Khóa học chưa được xuất bản và chưa mở đăng ký.'
        : 'Kiểm tra trạng thái khóa học và phản hồi trước khi tiếp tục.'}
    </p>
    {request.submissionNote && (
      <div style={{ whiteSpace: 'pre-wrap' }}>
        <strong>Ghi chú đã gửi: </strong>
        {request.submissionNote}
      </div>
    )}
    {request.reviewerFeedback && (
      <div className="mt-2" style={{ whiteSpace: 'pre-wrap' }}>
        <strong>Phản hồi: </strong>
        {request.reviewerFeedback}
      </div>
    )}
    <div className="small text-muted mt-2">Đã xác nhận quyền nội dung lúc {dateText(request.rightsConfirmedAt)}</div>
  </Card>
);
