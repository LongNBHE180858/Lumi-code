import './admin-catalog.scss';

import React, { useEffect, useMemo, useState } from 'react';
import axios from 'axios';
import { Plus, RefreshCw } from 'lucide-react';

import AdminLayout from '../layout/admin-layout';
import { catalogApi, type CatalogInput, type CatalogItem, type CatalogKind } from './catalog-api';
import CatalogTable from './components/catalog-table';
import CatalogFormDialog from './components/catalog-form-dialog';

const tabs: { kind: CatalogKind; label: string; singular: string }[] = [
  { kind: 'categories', label: 'Danh mục khóa học', singular: 'danh mục' },
  { kind: 'subjects', label: 'Môn học', singular: 'môn học' },
  { kind: 'grades', label: 'Khối lớp', singular: 'khối lớp' },
  { kind: 'tags', label: 'Thẻ (tag)', singular: 'thẻ' },
];
const pageSize = 10;
const emptyInput: CatalogInput = { name: '', description: '', level: null, parentId: null };

const getError = (error: unknown) => {
  if (axios.isAxiosError(error)) {
    if (error.response?.status === 409) return 'Mục này đã tồn tại hoặc còn danh mục con đang sử dụng.';
    if (error.response?.status === 400) return 'Dữ liệu chưa hợp lệ. Hãy kiểm tra tên, số lớp hoặc danh mục cha.';
    if (error.response?.status === 404) return 'Không tìm thấy mục này. Hãy tải lại danh sách.';
  }
  return 'Không thể hoàn tất yêu cầu. Vui lòng thử lại.';
};

const AdminCatalog = () => {
  const [kind, setKind] = useState<CatalogKind>('categories');
  const [items, setItems] = useState<CatalogItem[]>([]);
  const [query, setQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState<'all' | 'active' | 'inactive'>('all');
  const [page, setPage] = useState(0);
  const [refreshKey, setRefreshKey] = useState(0);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState('');
  const [notice, setNotice] = useState('');
  const [editing, setEditing] = useState<CatalogItem | null>(null);
  const [formOpen, setFormOpen] = useState(false);
  const [form, setForm] = useState<CatalogInput>(emptyInput);
  const [formError, setFormError] = useState('');
  const [saving, setSaving] = useState(false);
  const [busyId, setBusyId] = useState<number | null>(null);

  const currentTab = tabs.find(tab => tab.kind === kind)!;

  useEffect(() => {
    let active = true;
    setLoading(true);
    setLoadError('');
    catalogApi
      .list(kind)
      .then(response => {
        if (active) setItems(response.data);
      })
      .catch(() => {
        if (!active) return;
        setItems([]);
        setLoadError('Không tải được danh sách. Vui lòng thử lại.');
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, [kind, refreshKey]);

  const filtered = useMemo(() => {
    const search = query.trim().toLocaleLowerCase('vi');
    return items.filter(item => {
      if (statusFilter === 'active' && !item.active) return false;
      if (statusFilter === 'inactive' && item.active) return false;
      return (
        !search || [item.name, item.description, item.level?.toString()].some(value => value?.toLocaleLowerCase('vi').includes(search))
      );
    });
  }, [items, query, statusFilter]);
  const pageCount = Math.ceil(filtered.length / pageSize);
  const currentPage = Math.min(page, Math.max(0, pageCount - 1));
  const pageItems = filtered.slice(currentPage * pageSize, (currentPage + 1) * pageSize);
  const visiblePageCount = Math.min(5, pageCount);
  const firstVisiblePage = Math.max(0, Math.min(currentPage - 2, pageCount - visiblePageCount));
  const visiblePages = Array.from({ length: visiblePageCount }, (_, index) => firstVisiblePage + index);
  const columnCount = kind === 'categories' ? 5 : kind === 'tags' ? 3 : 4;

  const changeKind = (next: CatalogKind) => {
    setKind(next);
    setQuery('');
    setStatusFilter('all');
    setPage(0);
    setNotice('');
  };

  const openForm = (item: CatalogItem | null) => {
    setEditing(item);
    setForm(item ? { name: item.name, description: item.description, level: item.level, parentId: item.parentId } : { ...emptyInput });
    setFormError('');
    setFormOpen(true);
  };

  const closeForm = () => {
    if (!saving) setFormOpen(false);
  };

  const save = async (event: React.FormEvent) => {
    event.preventDefault();
    const name = form.name.trim();
    if (!name) return setFormError('Vui lòng nhập tên.');
    if (kind === 'grades' && (form.level == null || form.level < 1 || form.level > 12)) return setFormError('Số lớp phải từ 1 đến 12.');
    setSaving(true);
    setFormError('');
    try {
      const input = { ...form, name };
      if (editing) await catalogApi.update(kind, editing.id, input);
      else await catalogApi.create(kind, input);
      setFormOpen(false);
      setNotice(editing ? 'Đã cập nhật thành công.' : 'Đã thêm thành công.');
      setRefreshKey(value => value + 1);
    } catch (error) {
      setFormError(getError(error));
    } finally {
      setSaving(false);
    }
  };

  const setActive = async (item: CatalogItem) => {
    setBusyId(item.id);
    setNotice('');
    setLoadError('');
    try {
      const response = await catalogApi.setActive(kind, item.id, !item.active);
      setItems(current => current.map(value => (value.id === item.id ? response.data : value)));
      setNotice(item.active ? 'Đã tạm ngưng. Dữ liệu cũ vẫn được giữ lại.' : 'Đã kích hoạt lại.');
    } catch (error) {
      setLoadError(getError(error));
    } finally {
      setBusyId(null);
    }
  };

  return (
    <AdminLayout
      searchValue={query}
      onSearchChange={value => {
        setQuery(value);
        setPage(0);
      }}
      searchLabel={`Tìm ${currentTab.singular}`}
    >
      <main className="admin-page-content admin-catalog">
        <div className="admin-catalog-card">
          <div className="admin-catalog-heading">
            <div>
              <h1>Danh mục</h1>
              <p>Quản lý các lựa chọn dùng trong khóa học.</p>
            </div>
            <div className="admin-catalog-heading-actions">
              <button type="button" className="admin-catalog-button" disabled={loading} onClick={() => setRefreshKey(value => value + 1)}>
                <RefreshCw size={16} aria-hidden="true" /> Làm mới
              </button>
              <button type="button" className="admin-catalog-button primary" onClick={() => openForm(null)}>
                <Plus size={16} aria-hidden="true" /> Thêm {currentTab.singular}
              </button>
            </div>
          </div>

          <div className="admin-catalog-tabs" role="group" aria-label="Loại danh mục">
            {tabs.map(tab => (
              <button
                key={tab.kind}
                type="button"
                className={kind === tab.kind ? 'active' : ''}
                aria-pressed={kind === tab.kind}
                onClick={() => changeKind(tab.kind)}
              >
                {tab.label}
              </button>
            ))}
          </div>

          <div className="admin-catalog-filters">
            <label htmlFor="admin-catalog-status">Trạng thái</label>
            <select
              id="admin-catalog-status"
              value={statusFilter}
              onChange={event => {
                setStatusFilter(event.target.value as typeof statusFilter);
                setPage(0);
              }}
            >
              <option value="all">Tất cả</option>
              <option value="active">Hoạt động</option>
              <option value="inactive">Tạm ngưng</option>
            </select>
          </div>

          {loadError && (
            <div className="admin-catalog-error" role="alert">
              {loadError}
            </div>
          )}
          {notice && (
            <div className="admin-catalog-notice" role="status">
              {notice}
            </div>
          )}

          <CatalogTable
            kind={kind}
            items={items}
            pageItems={pageItems}
            filtered={filtered}
            loading={loading}
            busyId={busyId}
            columnCount={columnCount}
            currentPage={currentPage}
            pageCount={pageCount}
            pageSize={pageSize}
            visiblePages={visiblePages}
            openForm={openForm}
            setActive={setActive}
            setPage={setPage}
          />
        </div>
      </main>

      {formOpen && (
        <CatalogFormDialog
          kind={kind}
          singular={currentTab.singular}
          items={items}
          editing={editing}
          form={form}
          setForm={setForm}
          formError={formError}
          saving={saving}
          closeForm={closeForm}
          save={save}
        />
      )}
    </AdminLayout>
  );
};

export default AdminCatalog;
