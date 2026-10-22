import React from 'react';
import { X } from 'lucide-react';
import type { CatalogInput, CatalogItem, CatalogKind } from '../catalog-api';

interface CatalogFormDialogProps {
  kind: CatalogKind;
  singular: string;
  items: CatalogItem[];
  editing: CatalogItem | null;
  form: CatalogInput;
  setForm: React.Dispatch<React.SetStateAction<CatalogInput>>;
  formError: string;
  saving: boolean;
  closeForm: () => void;
  save: (event: React.FormEvent) => void;
}

const CatalogFormDialog = ({
  kind,
  singular,
  items,
  editing,
  form,
  setForm,
  formError,
  saving,
  closeForm,
  save,
}: CatalogFormDialogProps) => (
  <div
    className="admin-catalog-overlay"
    role="presentation"
    onMouseDown={event => {
      if (event.target === event.currentTarget) closeForm();
    }}
  >
    <section className="admin-catalog-dialog" role="dialog" aria-modal="true" aria-labelledby="admin-catalog-dialog-title">
      <button type="button" className="admin-catalog-dialog-close" aria-label="Đóng" onClick={closeForm}>
        <X size={20} />
      </button>
      <h2 id="admin-catalog-dialog-title">
        {editing ? 'Sửa' : 'Thêm'} {singular}
      </h2>
      <form onSubmit={save}>
        <label>
          <span className="admin-catalog-field-label">
            Tên <span aria-hidden="true">*</span>
          </span>
          <input
            autoFocus
            value={form.name}
            maxLength={kind === 'grades' ? 50 : 100}
            onChange={event => setForm(current => ({ ...current, name: event.target.value }))}
            required
          />
        </label>
        {kind === 'grades' && (
          <label>
            <span className="admin-catalog-field-label">
              Số lớp <span aria-hidden="true">*</span>
            </span>
            <input
              type="number"
              min={1}
              max={12}
              value={form.level ?? ''}
              onChange={event => setForm(current => ({ ...current, level: event.target.value ? Number(event.target.value) : null }))}
              required
            />
          </label>
        )}
        {kind === 'categories' && (
          <label>
            Danh mục cha
            <select
              value={form.parentId ?? ''}
              onChange={event => setForm(current => ({ ...current, parentId: event.target.value ? Number(event.target.value) : null }))}
            >
              <option value="">Không có</option>
              {items
                .filter(item => item.active && item.id !== editing?.id)
                .map(item => (
                  <option key={item.id} value={item.id}>
                    {item.name}
                  </option>
                ))}
            </select>
          </label>
        )}
        {(kind === 'subjects' || kind === 'categories') && (
          <label>
            Mô tả
            <textarea
              rows={4}
              maxLength={5000}
              value={form.description ?? ''}
              onChange={event => setForm(current => ({ ...current, description: event.target.value }))}
            />
          </label>
        )}
        {formError && (
          <div className="admin-catalog-error" role="alert">
            {formError}
          </div>
        )}
        <div className="admin-catalog-dialog-actions">
          <button type="button" className="admin-catalog-button" disabled={saving} onClick={closeForm}>
            Hủy
          </button>
          <button type="submit" className="admin-catalog-button primary" disabled={saving}>
            {saving ? 'Đang lưu...' : 'Lưu'}
          </button>
        </div>
      </form>
    </section>
  </div>
);

export default CatalogFormDialog;
