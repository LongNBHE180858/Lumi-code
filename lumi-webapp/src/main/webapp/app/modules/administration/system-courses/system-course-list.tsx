import './system-course-list.scss';

import React, { useEffect, useState, useMemo } from 'react';
import { Link, useNavigate } from 'react-router';
import { Modal, Button, Spinner } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faBookOpen,
  faLayerGroup,
  faPlus,
  faSearch,
  faPencilAlt,
  faTrash,
  faEye,
  faRotateRight,
  faCheckCircle,
  faEyeSlash,
} from '@fortawesome/free-solid-svg-icons';
import { toast } from 'react-toastify';
import axios from 'axios';

import AdminLayout from '../layout/admin-layout';
import { fetchSubjects, fetchGrades } from 'app/modules/instructor/course-builder/course-builder.reducer';

export interface ISystemCourseItem {
  id: number;
  version: number;
  title: string;
  subject?: { id: number; name: string };
  grade?: { id: number; name: string };
  bookSeries?: string;
  status: 'DRAFT' | 'PENDING' | 'ACTIVE' | 'HIDDEN' | 'ARCHIVED' | 'BLOCKED';
  updatedAt?: string;
  thumbnailUrl?: string;
  chapterCount: number;
  lessonCount: number;
  videoCount: number;
  totalDurationSeconds: number;
  totalDurationHours: number;
  isSystemCourse?: boolean;
}

const statusLabels: Record<string, { label: string; className: string }> = {
  ACTIVE: { label: 'Đang hoạt động', className: 'status-active' },
  DRAFT: { label: 'Bản nháp', className: 'status-draft' },
  HIDDEN: { label: 'Đã tạm ẩn', className: 'status-hidden' },
  PENDING: { label: 'Chờ duyệt', className: 'status-draft' },
};

export const AdminSystemCourseList = () => {
  const navigate = useNavigate();
  const [courses, setCourses] = useState<ISystemCourseItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [statusTab, setStatusTab] = useState<'ALL' | 'DRAFT' | 'ACTIVE' | 'HIDDEN'>('ALL');
  const [query, setQuery] = useState('');
  const [subjectId, setSubjectId] = useState<string>('ALL');
  const [gradeId, setGradeId] = useState<string>('ALL');
  const [subjects, setSubjects] = useState<any[]>([]);
  const [grades, setGrades] = useState<any[]>([]);

  // Delete modal state
  const [deleteCourse, setDeleteCourse] = useState<ISystemCourseItem | null>(null);
  const [deleting, setDeleting] = useState(false);

  // Quick toggle status state
  const [togglingId, setTogglingId] = useState<number | null>(null);

  const loadData = () => {
    setLoading(true);
    axios
      .get<ISystemCourseItem[]>('/api/instructor/courses/system', {
        params: { size: 100, sort: 'updatedAt,desc' },
      })
      .then(res => setCourses(res.data || []))
      .catch(() => toast.error('Không tải được danh sách khóa học hệ thống.'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadData();
    fetchSubjects()
      .then(setSubjects)
      .catch(() => {});
    fetchGrades()
      .then(setGrades)
      .catch(() => {});
  }, []);

  const filteredCourses = useMemo(() => {
    return courses.filter(item => {
      if (statusTab !== 'ALL' && item.status !== statusTab) return false;
      if (subjectId !== 'ALL' && String(item.subject?.id) !== subjectId) return false;
      if (gradeId !== 'ALL' && String(item.grade?.id) !== gradeId) return false;
      if (query.trim()) {
        const q = query.trim().toLowerCase();
        const matchesTitle = item.title.toLowerCase().includes(q);
        const matchesSubject = item.subject?.name?.toLowerCase().includes(q);
        if (!matchesTitle && !matchesSubject) return false;
      }
      return true;
    });
  }, [courses, statusTab, query, subjectId, gradeId]);

  const handleDelete = async () => {
    if (!deleteCourse) return;
    setDeleting(true);
    try {
      await axios.delete(`/api/instructor/courses/${deleteCourse.id}`, {
        params: { version: deleteCourse.version },
      });
      toast.success('Đã xóa khóa học thành công.');
      setDeleteCourse(null);
      loadData();
    } catch {
      toast.error('Không thể xóa khóa học. Vui lòng thử lại.');
    } finally {
      setDeleting(false);
    }
  };

  const handleTogglePublish = async (course: ISystemCourseItem) => {
    setTogglingId(course.id);
    try {
      if (course.status === 'ACTIVE') {
        await axios.post(`/api/instructor/courses/${course.id}/unpublish-system`, { version: course.version });
        toast.success(`Đã chuyển khóa học "${course.title}" sang trạng thái tạm ẩn.`);
      } else {
        await axios.post(`/api/instructor/courses/${course.id}/publish-system`, { version: course.version });
        toast.success(`Đã xuất bản khóa học "${course.title}" thành công!`);
      }
      loadData();
    } catch (err: any) {
      toast.error(err?.response?.data?.detail || err?.response?.data?.message || 'Thao tác không thành công.');
    } finally {
      setTogglingId(null);
    }
  };

  return (
    <AdminLayout>
      <div className="system-course-page">
        {/* Header */}
        <div className="page-header">
          <div className="header-title">
            <h1>Nội dung học tập (Khóa học chuẩn SGK)</h1>
            <p>Quản lý các khóa học hệ thống theo sách giáo khoa phục vụ các gói học tập theo học kỳ của học sinh.</p>
          </div>
          <div className="d-flex gap-2">
            <Button variant="outline-secondary" className="rounded-pill px-3" onClick={loadData} disabled={loading}>
              <FontAwesomeIcon icon={faRotateRight} className={`me-1 ${loading ? 'fa-spin' : ''}`} /> Tải lại
            </Button>
            <Button variant="primary" className="rounded-pill px-4 fw-semibold" onClick={() => navigate('/admin/system-courses/new')}>
              <FontAwesomeIcon icon={faPlus} className="me-2" /> Thêm khóa học SGK
            </Button>
          </div>
        </div>

        {/* Status Tabs */}
        <div className="status-tabs">
          <button type="button" className={`tab-btn ${statusTab === 'ALL' ? 'active' : ''}`} onClick={() => setStatusTab('ALL')}>
            Tất cả ({courses.length})
          </button>
          <button type="button" className={`tab-btn ${statusTab === 'ACTIVE' ? 'active' : ''}`} onClick={() => setStatusTab('ACTIVE')}>
            Đang hoạt động ({courses.filter(c => c.status === 'ACTIVE').length})
          </button>
          <button type="button" className={`tab-btn ${statusTab === 'DRAFT' ? 'active' : ''}`} onClick={() => setStatusTab('DRAFT')}>
            Bản nháp ({courses.filter(c => c.status === 'DRAFT').length})
          </button>
          <button type="button" className={`tab-btn ${statusTab === 'HIDDEN' ? 'active' : ''}`} onClick={() => setStatusTab('HIDDEN')}>
            Đã tạm ẩn ({courses.filter(c => c.status === 'HIDDEN').length})
          </button>
        </div>

        {/* Filters */}
        <div className="filter-panel">
          <div className="search-input-group">
            <FontAwesomeIcon icon={faSearch} className="search-icon" />
            <input type="text" placeholder="Tìm theo tên khóa học hoặc môn học..." value={query} onChange={e => setQuery(e.target.value)} />
          </div>
          <div className="filter-selects">
            <select value={subjectId} onChange={e => setSubjectId(e.target.value)}>
              <option value="ALL">Tất cả môn học</option>
              {subjects.map(s => (
                <option key={s.id} value={String(s.id)}>
                  {s.name}
                </option>
              ))}
            </select>
            <select value={gradeId} onChange={e => setGradeId(e.target.value)}>
              <option value="ALL">Tất cả khối lớp</option>
              {grades.map(g => (
                <option key={g.id} value={String(g.id)}>
                  {g.name}
                </option>
              ))}
            </select>
          </div>
        </div>

        {/* Loading state */}
        {loading ? (
          <div className="text-center p-5">
            <Spinner animation="border" variant="primary" />
            <div className="mt-2 text-muted">Đang tải danh sách khóa học hệ thống...</div>
          </div>
        ) : filteredCourses.length === 0 ? (
          <div className="empty-state">
            <FontAwesomeIcon icon={faBookOpen} size="3x" className="text-muted mb-3 opacity-50" />
            <h3>Chưa có khóa học hệ thống nào</h3>
            <p>Bắt đầu tạo khóa học chuẩn sách giáo khoa đầu tiên để bổ sung vào kho học liệu của hệ thống.</p>
            <Button variant="primary" className="rounded-pill px-4" onClick={() => navigate('/admin/system-courses/new')}>
              <FontAwesomeIcon icon={faPlus} className="me-2" /> Tạo khóa học SGK ngay
            </Button>
          </div>
        ) : (
          <div className="course-grid">
            {filteredCourses.map(item => {
              const statusCfg = statusLabels[item.status] || { label: item.status, className: 'status-draft' };
              return (
                <div key={item.id} className="course-card">
                  <div className="card-thumb">
                    {item.thumbnailUrl ? (
                      <img src={item.thumbnailUrl} alt={item.title} />
                    ) : (
                      <div className="thumb-fallback">
                        <FontAwesomeIcon icon={faBookOpen} size="2x" />
                        <span>Kho tài liệu SGK</span>
                      </div>
                    )}
                    <span className="badge-system">SGK Hệ thống</span>
                    <span className={`badge-status ${statusCfg.className}`}>{statusCfg.label}</span>
                  </div>

                  <div className="card-body">
                    <div className="tags">
                      {item.subject && <span className="tag subject">{item.subject.name}</span>}
                      {item.grade && <span className="tag grade">{item.grade.name}</span>}
                      <span className="tag">{item.bookSeries || 'Kết nối tri thức'}</span>
                    </div>

                    <h3 className="course-title" title={item.title}>
                      {item.title}
                    </h3>

                    <div className="curriculum-stats">
                      <span>
                        <FontAwesomeIcon icon={faLayerGroup} className="me-1 opacity-75" />
                        {item.chapterCount || 0} chương
                      </span>
                      <span>•</span>
                      <span>
                        <FontAwesomeIcon icon={faBookOpen} className="me-1 opacity-75" />
                        {item.lessonCount || 0} bài học
                      </span>
                    </div>

                    <div className="footer-info">
                      <span>Gói học kỳ LUMI</span>
                      <span>Cập nhật {item.updatedAt ? new Date(item.updatedAt).toLocaleDateString('vi-VN') : '—'}</span>
                    </div>
                  </div>

                  <div className="card-actions">
                    <Link to={`/admin/system-courses/${item.id}/builder`} className="btn-edit">
                      <FontAwesomeIcon icon={faPencilAlt} /> Biên soạn bài học
                    </Link>

                    <button
                      type="button"
                      className="btn-icon"
                      title={item.status === 'ACTIVE' ? 'Tạm ẩn khóa học' : 'Xuất bản khóa học'}
                      disabled={togglingId === item.id}
                      onClick={() => handleTogglePublish(item)}
                    >
                      {togglingId === item.id ? (
                        <Spinner animation="border" size="sm" />
                      ) : (
                        <FontAwesomeIcon
                          icon={item.status === 'ACTIVE' ? faEyeSlash : faCheckCircle}
                          className={item.status === 'ACTIVE' ? 'text-warning' : 'text-success'}
                        />
                      )}
                    </button>

                    <Link to={`/courses/${item.id}`} target="_blank" className="btn-icon" title="Xem trước khóa học">
                      <FontAwesomeIcon icon={faEye} />
                    </Link>

                    <button type="button" className="btn-icon btn-delete" title="Xóa khóa học" onClick={() => setDeleteCourse(item)}>
                      <FontAwesomeIcon icon={faTrash} />
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        )}

        {/* Delete Confirmation Modal */}
        <Modal show={Boolean(deleteCourse)} onHide={() => !deleting && setDeleteCourse(null)} centered>
          <Modal.Header closeButton={!deleting}>
            <Modal.Title className="h5 fw-bold text-danger">Xác nhận xóa khóa học hệ thống</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            Bạn có chắc chắn muốn xóa khóa học <strong>{deleteCourse?.title}</strong> không? Các bài học và tài liệu liên quan sẽ bị ẩn khỏi
            hệ thống.
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setDeleteCourse(null)} disabled={deleting}>
              Hủy
            </Button>
            <Button variant="danger" onClick={handleDelete} disabled={deleting}>
              {deleting ? 'Đang xóa...' : 'Xác nhận xóa'}
            </Button>
          </Modal.Footer>
        </Modal>
      </div>
    </AdminLayout>
  );
};

export default AdminSystemCourseList;
