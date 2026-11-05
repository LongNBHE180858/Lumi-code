import './admin-premium-packages.scss';

import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Pencil, Plus, RefreshCw } from 'lucide-react';

import AdminLayout from '../layout/admin-layout';
import PremiumPackageForm, { aiPeriodLabels, packageTypeLabels } from './premium-package-form';
import { premiumPackagesApi, type PremiumPackage, type PremiumPackageInput } from './premium-packages-api';

const pageSize = 10;
const currency = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 });

const errorMessage = (error: unknown) => {
  if (axios.isAxiosError(error)) {
    if (error.response?.status === 400) return 'Dữ liệu chưa hợp lệ. Hãy kiểm tra loại gói, giá và các hạn mức.';
    if (error.response?.status === 404) return 'Không tìm thấy gói. Hãy tải lại danh sách.';
    if (error.response?.status === 409) return 'Gói đã có phiên bản mới. Hãy tải lại danh sách trước khi sửa.';
  }
  return 'Không thể hoàn tất yêu cầu. Vui lòng thử lại.';
};

const AdminPremiumPackages = () => {
  const [items, setItems] = useState<PremiumPackage[]>([]);
  const [page, setPage] = useState(0);
  const [total, setTotal] = useState(0);
  const [refreshKey, setRefreshKey] = useState(0);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState('');
  const [notice, setNotice] = useState('');
  const [editing, setEditing] = useState<PremiumPackage | null>(null);
  const [formOpen, setFormOpen] = useState(false);
  const [formError, setFormError] = useState('');
  const [saving, setSaving] = useState(false);
  const [busyId, setBusyId] = useState<number | null>(null);

  useEffect(() => {
    let active = true;
    setLoading(true);
    setLoadError('');
    premiumPackagesApi
      .list(page, pageSize)
      .then(response => {
        if (!active) return;
        setItems(response.data);
        const count = Number(response.headers['x-total-count']);
        setTotal(Number.isFinite(count) ? count : response.data.length);
      })
      .catch(() => {
        if (!active) return;
        setItems([]);
        setLoadError('Không tải được danh sách gói. Vui lòng thử lại.');
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, [page, refreshKey]);

  const openForm = (item: PremiumPackage | null) => {
    setEditing(item);
    setFormError('');
    setFormOpen(true);
  };

  const save = async (input: PremiumPackageInput) => {
    setSaving(true);
    setFormError('');
    try {
      if (editing) await premiumPackagesApi.update(editing.id, input);
      else await premiumPackagesApi.create(input);
      setFormOpen(false);
      setNotice(editing ? 'Đã lưu phiên bản mới của gói.' : 'Đã thêm gói. Gói đang ở trạng thái ngừng bán.');
      setPage(0);
      setRefreshKey(value => value + 1);
    } catch (error) {
      setFormError(errorMessage(error));
    } finally {
      setSaving(false);
    }
  };

  const toggleAvailability = async (item: PremiumPackage) => {
    setBusyId(item.id);
    setLoadError('');
    setNotice('');
    try {
      const response = await premiumPackagesApi.setAvailability(item.id, !item.available);
      setItems(current => current.map(value => (value.id === item.id ? response.data : value)));
      setNotice(item.available ? 'Đã ngừng bán. Quyền lợi đã mua vẫn được giữ.' : 'Đã bật bán gói.');
    } catch (error) {
      setLoadError(errorMessage(error));
    } finally {
      setBusyId(null);
    }
  };

  return (
    <AdminLayout>
      <main className="admin-page-content admin-premium">
        <section className="admin-premium-card">
          <div className="admin-premium-heading">
            <div>
              <h1>Gói Premium</h1>
              <p>Quản lý giá, thời hạn và quyền lợi của từng loại gói.</p>
            </div>
            <div className="admin-premium-actions">
              <button type="button" className="admin-premium-button" onClick={() => setRefreshKey(value => value + 1)} disabled={loading}>
                <RefreshCw size={16} aria-hidden="true" /> Làm mới
              </button>
              <button type="button" className="admin-premium-button primary" onClick={() => openForm(null)}>
                <Plus size={16} aria-hidden="true" /> Thêm gói
              </button>
            </div>
          </div>

          {loadError && (
            <p className="admin-premium-error" role="alert">
              {loadError}
            </p>
          )}
          {notice && (
            <p className="admin-premium-notice" role="status">
              {notice}
            </p>
          )}

          <div className="admin-premium-table-wrap">
            <table>
              <thead>
                <tr>
                  <th scope="col">Tên gói</th>
                  <th scope="col">Loại</th>
                  <th scope="col">Giá</th>
                  <th scope="col">Thời hạn</th>
                  <th scope="col">Hạn mức mỗi người</th>
                  <th scope="col">Số lớp</th>
                  <th scope="col">Phiên bản</th>
                  <th scope="col">Trạng thái</th>
                  <th scope="col">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {items.map(item => (
                  <tr key={item.id}>
                    <td>
                      <strong>{item.name}</strong>
                    </td>
                    <td>{item.packageType ? packageTypeLabels[item.packageType] : 'Chưa phân loại'}</td>
                    <td>{currency.format(item.price)}</td>
                    <td>{item.durationDays} ngày</td>
                    <td>
                      {item.storageQuotaMb.toLocaleString('vi-VN')} MB · {item.aiGenerationLimit.toLocaleString('vi-VN')} lượt AI /{' '}
                      {aiPeriodLabels[item.aiLimitPeriod as keyof typeof aiPeriodLabels] ?? 'chưa xác định'}
                    </td>
                    <td>{item.maxClasses ?? '—'}</td>
                    <td>{item.revision}</td>
                    <td>
                      <span className={`admin-premium-status${item.available ? ' active' : ''}`}>
                        {item.needsClassification ? 'Cần phân loại' : item.available ? 'Đang bán' : 'Ngừng bán'}
                      </span>
                    </td>
                    <td>
                      <div className="admin-premium-row-actions">
                        <button type="button" onClick={() => openForm(item)} aria-label={`Sửa ${item.name}`}>
                          <Pencil size={14} aria-hidden="true" /> Sửa
                        </button>
                        <button
                          type="button"
                          disabled={busyId === item.id || item.needsClassification}
                          onClick={() => toggleAvailability(item)}
                        >
                          {item.available ? 'Ngừng bán' : 'Bật bán'}
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
                {!loading && items.length === 0 && (
                  <tr>
                    <td colSpan={9} className="admin-premium-empty">
                      Chưa có gói Premium nào.
                    </td>
                  </tr>
                )}
                {loading && (
                  <tr>
                    <td colSpan={9} className="admin-premium-empty">
                      Đang tải danh sách...
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
          <div className="admin-premium-pagination">
            <span>{total === 0 ? '0 gói' : `${page * pageSize + 1}–${Math.min((page + 1) * pageSize, total)} / ${total} gói`}</span>
            <div>
              <button type="button" disabled={loading || page === 0} onClick={() => setPage(value => value - 1)}>
                Trang trước
              </button>
              <button type="button" disabled={loading || (page + 1) * pageSize >= total} onClick={() => setPage(value => value + 1)}>
                Trang sau
              </button>
            </div>
          </div>
        </section>
      </main>
      {formOpen && (
        <PremiumPackageForm
          key={editing?.id ?? 'new'}
          item={editing}
          saving={saving}
          error={formError}
          onClose={() => !saving && setFormOpen(false)}
          onSave={save}
        />
      )}
    </AdminLayout>
  );
};

export default AdminPremiumPackages;
