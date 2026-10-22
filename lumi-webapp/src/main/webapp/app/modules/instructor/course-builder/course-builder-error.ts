export const builderErrorStatus = (error: unknown): number | null => {
  const value = error as { status?: number; response?: { status?: number }; code?: string; message?: string } | null;
  const status = value?.response?.status ?? value?.status;
  if (status) return status;
  const match = `${value?.code ?? ''} ${value?.message ?? ''}`.match(/\b(400|401|403|404|409|413|503)\b/);
  return match ? Number(match[1]) : null;
};

export const builderErrorText = (error: unknown, fallback = 'Thao tác không thành công. Vui lòng thử lại.') => {
  switch (builderErrorStatus(error)) {
    case 401:
      return 'Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.';
    case 403:
      return 'Bạn không có quyền chỉnh sửa khóa học này.';
    case 404:
      return 'Khóa học, bài học hoặc tệp không còn tồn tại hoặc bạn không có quyền truy cập.';
    case 409:
      return 'Dữ liệu hoặc trạng thái khóa học đã thay đổi. Hãy tải lại dữ liệu trước khi tiếp tục; bản nháp bài học được giữ lại.';
    case 413:
      return 'Tệp vượt quá giới hạn dung lượng của máy chủ.';
    case 503:
      return 'Dịch vụ lưu trữ chưa sẵn sàng. Vui lòng thử lại sau.';
    default:
      return fallback;
  }
};
