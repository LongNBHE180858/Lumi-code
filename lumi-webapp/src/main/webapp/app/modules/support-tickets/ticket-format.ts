import axios from 'axios';

export const ticketTypes = [
  { value: 'GENERAL', label: 'Yêu cầu chung' },
  { value: 'ACCOUNT', label: 'Tài khoản' },
  { value: 'COURSE', label: 'Khóa học' },
  { value: 'PAYMENT', label: 'Thanh toán' },
  { value: 'TECHNICAL', label: 'Vấn đề kỹ thuật' },
] as const;

export const typeLabel = (type: string) => ticketTypes.find(item => item.value === type)?.label ?? type;

export const statusLabel = (status: string) => {
  switch (status) {
    case 'OPEN':
      return 'Mới';
    case 'IN_PROGRESS':
      return 'Đang xử lý';
    case 'RESOLVED':
      return 'Đã giải quyết';
    default:
      return status || 'Chưa xác định';
  }
};

export const ticketDate = (value?: string) => {
  if (!value) return '—';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return '—';
  return new Intl.DateTimeFormat('vi-VN', { dateStyle: 'short', timeStyle: 'short' }).format(date);
};

export const ticketError = (error: unknown) => {
  if (axios.isAxiosError(error)) {
    if (error.response?.status === 400) return 'Dữ liệu chưa hợp lệ. Vui lòng kiểm tra lại nội dung.';
    if (error.response?.status === 403) return 'Bạn không có quyền thao tác với yêu cầu này.';
    if (error.response?.status === 404) return 'Không tìm thấy yêu cầu hỗ trợ này.';
    if (error.response?.status === 409) return 'Trạng thái yêu cầu đã thay đổi. Vui lòng tải lại trang.';
  }
  return 'Không thể hoàn tất yêu cầu. Vui lòng thử lại.';
};
