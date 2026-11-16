import React from 'react';
import type { AccessDurationUnit } from './course-purchase.model';

const currency = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' });
export const amountLabel = (value: number | null | undefined) => (value == null ? '—' : currency.format(value));
export const durationLabel = (value: number | null | undefined, unit: AccessDurationUnit | null | undefined) => {
  const label = { DAY: 'ngày', MONTH: 'tháng', YEAR: 'năm' }[unit ?? ''];
  return value && label ? `${value} ${label}` : 'Chưa có thông tin';
};

export const PurchaseSteps = ({ active }: { active: number }) => (
  <ol className="purchase-steps" aria-label="Tiến trình mua khóa học">
    {['Xác nhận', 'Thanh toán', 'Hoàn tất'].map((step, index) => (
      <li key={step} aria-current={index === active ? 'step' : undefined}>
        <div className="purchase-step">
          <span className="purchase-step-number" aria-hidden="true">
            {index + 1}
          </span>
          <span>{step}</span>
        </div>
      </li>
    ))}
  </ol>
);
