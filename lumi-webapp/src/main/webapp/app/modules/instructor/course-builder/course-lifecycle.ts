import type { IBuilderAggregate } from './course-builder.reducer';

export const isBuilderReadOnly = (data: IBuilderAggregate | null) =>
  Boolean(data && (data.course.status !== 'DRAFT' || data.review?.request?.status === 'PENDING'));

export const courseLifecycle = (data: IBuilderAggregate | null) => {
  const status = data?.course.status;
  if (data?.review?.request?.status === 'PENDING' || status === 'PENDING')
    return { label: 'Đang chờ duyệt', description: 'Đã gửi duyệt • Chưa xuất bản' };
  if (status === 'ACTIVE') return { label: 'Đã xuất bản', description: 'Khóa học đang hoạt động' };
  if (status && status !== 'DRAFT') return { label: status, description: 'Khóa học hiện không thể chỉnh sửa' };
  return {
    label: 'Bản nháp',
    description: data?.review?.request?.status === 'REJECTED' ? 'Cần bổ sung theo phản hồi duyệt' : 'Chưa xuất bản • Chưa gửi duyệt',
  };
};
