import type { AccessDurationUnit, ICourseData } from '../course-builder.reducer';

export const durationLabels: Record<AccessDurationUnit, string> = { DAY: 'ngày', MONTH: 'tháng', YEAR: 'năm' };

export const savedPricing = (course?: ICourseData) => {
  const price = course?.price;
  const priceValid = price != null && Number.isSafeInteger(price) && price >= 0;
  const legacy = course?.accessDurationValue == null && course?.accessDurationUnit == null;
  const duration = legacy ? course?.validDurationDays : course?.accessDurationValue;
  const unit = legacy ? 'DAY' : course?.accessDurationUnit;
  const durationValid =
    duration != null &&
    Number.isInteger(duration) &&
    duration > 0 &&
    duration <= 2147483647 &&
    (unit === 'DAY' || unit === 'MONTH' || unit === 'YEAR');
  const durationText = durationValid ? `${duration} ${durationLabels[unit]}` : 'Chưa thiết lập thời hạn truy cập hợp lệ';
  const priceText = priceValid ? (price === 0 ? 'Miễn phí' : `${price.toLocaleString('vi-VN')} ₫`) : 'Chưa thiết lập học phí hợp lệ';
  return {
    valid: priceValid && durationValid,
    priceValid,
    durationValid,
    priceText,
    durationText,
    paymentText: priceValid && price > 0 ? 'Thanh toán một lần' : '',
    checklistText:
      priceValid && durationValid ? `Học phí: ${priceText} • Truy cập ${durationText}` : 'Chưa hoàn tất học phí hoặc thời hạn truy cập',
  };
};
