import React, { useState } from 'react';
import { X } from 'lucide-react';

import type { PremiumPackage, PremiumPackageInput, PremiumPackageType } from './premium-packages-api';

export const packageTypeLabels: Record<PremiumPackageType, string> = {
  PARENT_PERSONAL: 'Phụ huynh cá nhân',
  PARENT_FAMILY: 'Gia đình',
  INSTRUCTOR_PERSONAL: 'Giảng viên cá nhân',
  INSTRUCTOR_CLASS: 'Giảng viên và một lớp',
};

const typeOptions = Object.entries(packageTypeLabels) as [PremiumPackageType, string][];

interface FormValues {
  name: string;
  packageType: PremiumPackageType | '';
  price: string;
  durationDays: string;
  storageQuotaMb: string;
  aiGenerationLimit: string;
  maxClasses: string;
}

const valuesFrom = (item: PremiumPackage | null): FormValues => ({
  name: item?.name ?? '',
  packageType: item?.packageType ?? '',
  price: item?.price?.toString() ?? '',
  durationDays: item?.durationDays?.toString() ?? '',
  storageQuotaMb: item?.storageQuotaMb?.toString() ?? '',
  aiGenerationLimit: item?.aiGenerationLimit?.toString() ?? '',
  maxClasses: item?.maxClasses?.toString() ?? '',
});

const parseInteger = (value: string, min: number): number | null => {
  if (!/^\d+$/.test(value)) return null;
  const parsed = Number(value);
  return Number.isSafeInteger(parsed) && parsed >= min ? parsed : null;
};

interface PremiumPackageFormProps {
  item: PremiumPackage | null;
  saving: boolean;
  error: string;
  onClose: () => void;
  onSave: (input: PremiumPackageInput) => void;
}

const PremiumPackageForm = ({ item, saving, error, onClose, onSave }: PremiumPackageFormProps) => {
  const [values, setValues] = useState<FormValues>(() => valuesFrom(item));
  const [validationError, setValidationError] = useState('');
  const instructor = values.packageType === 'INSTRUCTOR_PERSONAL' || values.packageType === 'INSTRUCTOR_CLASS';

  const update = (field: keyof FormValues, value: string) => {
    setValues(current => ({ ...current, [field]: value }));
    setValidationError('');
  };

  const submit = (event: React.FormEvent) => {
    event.preventDefault();
    const name = values.name.trim();
    const price = parseInteger(values.price, 0);
    const durationDays = parseInteger(values.durationDays, 1);
    const storageQuotaMb = parseInteger(values.storageQuotaMb, 0);
    const aiGenerationLimit = parseInteger(values.aiGenerationLimit, 0);
    const maxClasses = instructor ? parseInteger(values.maxClasses, 1) : null;
    if (
      !name ||
      name.length > 100 ||
      !values.packageType ||
      price === null ||
      durationDays === null ||
      storageQuotaMb === null ||
      aiGenerationLimit === null ||
      (instructor && maxClasses === null)
    ) {
      setValidationError('Vui lòng nhập đủ thông tin hợp lệ. Giá và các hạn mức phải là số nguyên trong phạm vi cho phép.');
      return;
    }
    onSave({ name, packageType: values.packageType, price, durationDays, storageQuotaMb, aiGenerationLimit, maxClasses });
  };

  return (
    <div
      className="admin-premium-overlay"
      role="presentation"
      onMouseDown={event => event.target === event.currentTarget && !saving && onClose()}
    >
      <section className="admin-premium-dialog" role="dialog" aria-modal="true" aria-labelledby="admin-premium-dialog-title">
        <button type="button" className="admin-premium-close" aria-label="Đóng" disabled={saving} onClick={onClose}>
          <X size={20} aria-hidden="true" />
        </button>
        <h2 id="admin-premium-dialog-title">{item ? 'Sửa gói Premium' : 'Thêm gói Premium'}</h2>
        {item && <p className="admin-premium-help">Khi lưu, hệ thống tạo phiên bản mới. Phiên bản cũ vẫn phục vụ các lượt mua trước.</p>}
        <form onSubmit={submit}>
          <label>
            Tên gói
            <input autoFocus value={values.name} maxLength={100} onChange={event => update('name', event.target.value)} required />
          </label>
          <label>
            Loại gói
            <select
              value={values.packageType}
              disabled={!!item?.packageType}
              onChange={event => update('packageType', event.target.value)}
              required
            >
              <option value="">Chọn loại gói</option>
              {typeOptions.map(([value, label]) => (
                <option key={value} value={value}>
                  {label}
                </option>
              ))}
            </select>
          </label>
          {values.packageType === 'PARENT_FAMILY' && (
            <p className="admin-premium-help">Tối đa 4 người: phụ huynh mua gói và tối đa 3 học sinh đã liên kết.</p>
          )}
          {values.packageType === 'INSTRUCTOR_CLASS' && (
            <p className="admin-premium-help">
              Áp dụng cho giảng viên và một lớp được chọn. Học sinh vào lớp sau lúc mua không được hưởng quyền lợi của lượt mua đó.
            </p>
          )}
          <label>
            Giá (VNĐ)
            <input type="number" min="0" step="1" value={values.price} onChange={event => update('price', event.target.value)} required />
          </label>
          <label>
            Thời hạn (ngày)
            <input
              type="number"
              min="1"
              step="1"
              value={values.durationDays}
              onChange={event => update('durationDays', event.target.value)}
              required
            />
          </label>
          <label>
            Dung lượng mỗi người (MB)
            <input
              type="number"
              min="0"
              step="1"
              value={values.storageQuotaMb}
              onChange={event => update('storageQuotaMb', event.target.value)}
              required
            />
          </label>
          <label>
            Lượt tạo AI mỗi người
            <input
              type="number"
              min="0"
              step="1"
              value={values.aiGenerationLimit}
              onChange={event => update('aiGenerationLimit', event.target.value)}
              required
            />
          </label>
          {instructor && (
            <label>
              Số lớp tối đa của giảng viên
              <input
                type="number"
                min="1"
                step="1"
                value={values.maxClasses}
                onChange={event => update('maxClasses', event.target.value)}
                required
              />
            </label>
          )}
          {(validationError || error) && (
            <p className="admin-premium-error" role="alert">
              {validationError || error}
            </p>
          )}
          <div className="admin-premium-dialog-actions">
            <button type="button" className="admin-premium-button" disabled={saving} onClick={onClose}>
              Hủy
            </button>
            <button type="submit" className="admin-premium-button primary" disabled={saving}>
              {saving ? 'Đang lưu...' : 'Lưu gói'}
            </button>
          </div>
        </form>
      </section>
    </div>
  );
};

export default PremiumPackageForm;
