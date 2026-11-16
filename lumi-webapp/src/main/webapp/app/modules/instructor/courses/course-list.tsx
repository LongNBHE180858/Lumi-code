import React, { useState, useEffect, useRef } from 'react';
import { Link, useNavigate } from 'react-router';
import { Modal, Button } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faPlus,
  faRotateLeft,
  faSearch,
  faPencilAlt,
  faTrash,
  faEye,
  faBookOpen,
  faLayerGroup,
  faClock,
  faCheckCircle,
  faExclamationTriangle,
  faFileAlt,
  faChevronLeft,
  faChevronRight,
  faRotateRight,
  faTimes,
} from '@fortawesome/free-solid-svg-icons';
import axios from 'axios';
import {
  COURSES_API,
  ICourseListItem,
  accessDurationText,
  courseListError,
  courseStatusLabels,
  fetchCoursePage,
} from './course-list.reducer';
import './course-list.scss';

type StatusFilterTab = 'ALL' | 'DRAFT' | 'PENDING' | 'ACTIVE';
type SortBy = 'updated_desc' | 'updated_asc' | 'title_asc' | 'price_desc' | 'price_asc';
const sorts: Record<SortBy, string> = {
  updated_desc: 'updatedAt,desc',
  updated_asc: 'updatedAt,asc',
  title_asc: 'title,asc',
  price_desc: 'price,desc',
  price_asc: 'price,asc',
};

const CourseThumbnail = ({ course, gradient }: { course: ICourseListItem; gradient: string }) => {
  const [url, setUrl] = useState<string | null>(null);
  useEffect(() => {
    const controller = new AbortController();
    setUrl(null);
    if (course.thumbnailUrl) {
      axios
        .get<{ url: string }>(`${COURSES_API}/${course.id}/thumbnail/access`, { signal: controller.signal })
        .then(response => {
          if (!controller.signal.aborted) setUrl(response.data.url);
        })
        .catch(() => {
          /* Keep the placeholder if the cover cannot be loaded. */
        });
    }
    return () => controller.abort();
  }, [course.id, course.thumbnailUrl, course.updatedAt]);
  return url ? (
    <img src={url} alt={course.title} onError={() => setUrl(null)} />
  ) : (
    <div className="thumb-placeholder" style={{ background: gradient }}>
      <FontAwesomeIcon icon={faBookOpen} />
    </div>
  );
};

export const CourseList = () => {
  const navigate = useNavigate();
  const [courses, setCourses] = useState<ICourseListItem[]>([]);
  const [totalItems, setTotalItems] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);
  const [revision, setRevision] = useState(0);
  const [searchQuery, setSearchQuery] = useState('');
  const [search, setSearch] = useState('');
  const [statusTab, setStatusTab] = useState<StatusFilterTab>('ALL');
  const [selectedSubject, setSelectedSubject] = useState('ALL');
  const [selectedGrade, setSelectedGrade] = useState('ALL');
  const [sortBy, setSortBy] = useState<SortBy>('updated_desc');
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(5);
  const [courseToDelete, setCourseToDelete] = useState<ICourseListItem | null>(null);
  const [courseToWithdraw, setCourseToWithdraw] = useState<ICourseListItem | null>(null);
  const [withdrawing, setWithdrawing] = useState(false);
  const [withdrawError, setWithdrawError] = useState(false);
  const withdrawRef = useRef(false);
  const closeWithdraw = () => {
    if (!withdrawRef.current) setCourseToWithdraw(null);
  };
  const confirmWithdraw = async () => {
    if (!courseToWithdraw || withdrawRef.current) return;
    withdrawRef.current = true;
    setWithdrawing(true);
    setWithdrawError(false);
    try {
      await axios.post(`${COURSES_API}/${courseToWithdraw.id}/review-requests/withdraw`, { version: courseToWithdraw.version });
      setCourseToWithdraw(null);
      setActionError(null);
    } catch {
      setWithdrawError(true);
    } finally {
      withdrawRef.current = false;
      setWithdrawing(false);
      setRevision(value => value + 1);
    }
  };
  const [deleting, setDeleting] = useState(false);
  const [deleteError, setDeleteError] = useState<string | null>(null);
  const [availableSubjects, setAvailableSubjects] = useState<{ id: number; name: string }[]>([]);
  const [availableGrades, setAvailableGrades] = useState<{ id: number; name: string }[]>([]);
  const [catalogError, setCatalogError] = useState(false);
  const [tabCounts, setTabCounts] = useState({ all: 0, draft: 0, pending: 0, active: 0 });
  const [countsReady, setCountsReady] = useState(false);

  useEffect(() => {
    const timer = setTimeout(() => setSearch(searchQuery.trim()), 300);
    return () => clearTimeout(timer);
  }, [searchQuery]);

  useEffect(() => {
    const controller = new AbortController();
    setLoading(true);
    setError(null);
    const params: Record<string, string | number> = { page: currentPage - 1, size: pageSize, sort: sorts[sortBy] };
    if (search) params['title.contains'] = search;
    if (statusTab !== 'ALL') params['status.equals'] = statusTab;
    if (selectedSubject !== 'ALL') params['subjectId.equals'] = selectedSubject;
    if (selectedGrade !== 'ALL') params['gradeId.equals'] = selectedGrade;
    fetchCoursePage(params, controller.signal)
      .then(result => {
        if (controller.signal.aborted) return;
        const lastPage = Math.max(1, Math.ceil(result.total / pageSize));
        if (currentPage > lastPage) {
          setCurrentPage(lastPage);
          return;
        }
        setCourses(result.courses);
        setTotalItems(result.total);
      })
      .catch(err => {
        if (!controller.signal.aborted) {
          setCourses([]);
          setTotalItems(0);
          setError(courseListError(err, 'Không thể tải danh sách khóa học. Vui lòng thử lại.'));
        }
      })
      .finally(() => {
        if (!controller.signal.aborted) setLoading(false);
      });
    return () => controller.abort();
  }, [currentPage, pageSize, sortBy, search, statusTab, selectedSubject, selectedGrade, revision]);

  useEffect(() => {
    const controller = new AbortController();
    setCountsReady(false);
    Promise.all(
      ['ALL', 'DRAFT', 'PENDING', 'ACTIVE'].map(status =>
        fetchCoursePage(
          {
            page: 0,
            size: 1,
            ...(status === 'ALL' ? {} : { 'status.equals': status }),
          },
          controller.signal,
        ),
      ),
    )
      .then(([all, draft, pending, active]) => {
        if (!controller.signal.aborted) {
          setTabCounts({ all: all.total, draft: draft.total, pending: pending.total, active: active.total });
          setCountsReady(true);
        }
      })
      .catch(() => {
        /* Unavailable totals are displayed as a dash. */
      });
    return () => controller.abort();
  }, [revision]);

  useEffect(() => {
    const controller = new AbortController();
    setCatalogError(false);
    Promise.all([
      axios.get<{ id: number; name: string }[]>('/api/subjects', { params: { size: 100, sort: 'name,asc' }, signal: controller.signal }),
      axios.get<{ id: number; name: string }[]>('/api/grades', { params: { size: 100, sort: 'level,asc' }, signal: controller.signal }),
    ])
      .then(([subjects, grades]) => {
        if (!controller.signal.aborted) {
          setAvailableSubjects(subjects.data);
          setAvailableGrades(grades.data);
        }
      })
      .catch(() => {
        if (!controller.signal.aborted) setCatalogError(true);
      });
    return () => controller.abort();
  }, [revision]);

  const totalPages = Math.max(1, Math.ceil(totalItems / pageSize));
  const currentPageSafe = Math.min(currentPage, totalPages);
  const startIndex = (currentPageSafe - 1) * pageSize;
  const handlePageChange = (page: number) => {
    if (!loading && page >= 1 && page <= totalPages) setCurrentPage(page);
  };
  const handleResetFilters = () => {
    setSearchQuery('');
    setSearch('');
    setSelectedSubject('ALL');
    setSelectedGrade('ALL');
    setStatusTab('ALL');
    setCurrentPage(1);
  };
  const closeDelete = () => {
    if (!deleting) {
      setCourseToDelete(null);
      setDeleteError(null);
    }
  };
  const confirmDelete = async () => {
    if (!courseToDelete || deleting) return;
    setDeleting(true);
    setDeleteError(null);
    setActionError(null);
    try {
      await axios.delete(`${COURSES_API}/${courseToDelete.id}`, { params: { version: courseToDelete.version } });
      setCourseToDelete(null);
      setRevision(value => value + 1);
    } catch (err) {
      const message = courseListError(err, 'Không thể xóa khóa học. Vui lòng thử lại.');
      const status = (err as { response?: { status?: number } })?.response?.status;
      if (status === 409 || status === 404) {
        setCourseToDelete(null);
        setActionError(message);
        setRevision(value => value + 1);
      } else setDeleteError(message);
    } finally {
      setDeleting(false);
    }
  };

  const formatPrice = (price?: number | null) => {
    if (price == null) return 'Chưa đặt học phí';
    if (price === 0) return 'Miễn phí';
    return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
  };

  const getSubjectClass = (subject: string) => {
    if (subject.includes('Toán')) return 'tag-subject-math';
    if (subject.includes('Văn')) return 'tag-subject-lit';
    if (subject.includes('Anh')) return 'tag-subject-eng';
    return 'tag-subject-sci';
  };

  const getSubjectGradient = (subject: string) => {
    if (subject.includes('Toán')) return 'linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%)';
    if (subject.includes('Văn')) return 'linear-gradient(135deg, #f43f5e 0%, #be123c 100%)';
    if (subject.includes('Anh')) return 'linear-gradient(135deg, #8b5cf6 0%, #6d28d9 100%)';
    return 'linear-gradient(135deg, #10b981 0%, #047857 100%)';
  };

  return (
    <div className="manage-course-container container">
      {/* 1. Breadcrumbs */}
      <nav className="mc-breadcrumb">
        <Link to="/">Trang chủ</Link>
        <span className="separator">›</span>
        <span>Không gian giảng viên</span>
        <span className="separator">›</span>
        <span className="active-crumb">Khóa học của tôi</span>
      </nav>

      {/* 2. Page Header */}
      <div className="mc-header">
        <div className="mc-title-wrap">
          <h1>
            Khóa học của tôi
            <span className="count-pill">{countsReady ? tabCounts.all : '—'} khóa học</span>
          </h1>
          <p>Quản lý nội dung, biểu phí, trạng thái kiểm duyệt và các thông số khóa học bạn đã tạo.</p>
        </div>
        <button onClick={() => navigate('/instructor/courses/new')} className="mc-btn-create">
          <FontAwesomeIcon icon={faPlus} />
          <span>Tạo khóa học mới</span>
        </button>
      </div>

      {/* 3. Stat Cards */}
      <div className="mc-stat-grid">
        <div className="stat-card card-total">
          <div className="stat-info">
            <div className="stat-label">Tổng khóa học</div>
            <div className="stat-value">{countsReady ? tabCounts.all : '—'}</div>
            <div className="stat-desc">Toàn bộ khóa học</div>
          </div>
          <div className="stat-icon">
            <FontAwesomeIcon icon={faLayerGroup} />
          </div>
        </div>

        <div className="stat-card card-active">
          <div className="stat-info">
            <div className="stat-label">Đã xuất bản</div>
            <div className="stat-value">{countsReady ? tabCounts.active : '—'}</div>
            <div className="stat-desc">Đang hiển thị cho học viên</div>
          </div>
          <div className="stat-icon">
            <FontAwesomeIcon icon={faCheckCircle} />
          </div>
        </div>

        <div className="stat-card card-pending">
          <div className="stat-info">
            <div className="stat-label">Đang chờ duyệt</div>
            <div className="stat-value">{countsReady ? tabCounts.pending : '—'}</div>
            <div className="stat-desc">Đang chờ xét duyệt</div>
          </div>
          <div className="stat-icon">
            <FontAwesomeIcon icon={faClock} />
          </div>
        </div>

        <div className="stat-card card-draft">
          <div className="stat-info">
            <div className="stat-label">Bản nháp</div>
            <div className="stat-value">{countsReady ? tabCounts.draft : '—'}</div>
            <div className="stat-desc">Chưa gửi duyệt</div>
          </div>
          <div className="stat-icon">
            <FontAwesomeIcon icon={faFileAlt} />
          </div>
        </div>
      </div>

      {/* 4. Main Course Table Card */}
      <div className="mc-card">
        {/* Status Tabs */}
        <div className="mc-tabs">
          <button
            className={`mc-tab-btn ${statusTab === 'ALL' ? 'active' : ''}`}
            onClick={() => {
              setStatusTab('ALL');
              setCurrentPage(1);
            }}
          >
            <span>Tất cả</span>
            <span className="mc-tab-badge">{countsReady ? tabCounts.all : '—'}</span>
          </button>
          <button
            className={`mc-tab-btn ${statusTab === 'DRAFT' ? 'active' : ''}`}
            onClick={() => {
              setStatusTab('DRAFT');
              setCurrentPage(1);
            }}
          >
            <span>Bản nháp</span>
            <span className="mc-tab-badge">{countsReady ? tabCounts.draft : '—'}</span>
          </button>
          <button
            className={`mc-tab-btn ${statusTab === 'PENDING' ? 'active' : ''}`}
            onClick={() => {
              setStatusTab('PENDING');
              setCurrentPage(1);
            }}
          >
            <span>Chờ duyệt</span>
            <span className="mc-tab-badge">{countsReady ? tabCounts.pending : '—'}</span>
          </button>
          <button
            className={`mc-tab-btn ${statusTab === 'ACTIVE' ? 'active' : ''}`}
            onClick={() => {
              setStatusTab('ACTIVE');
              setCurrentPage(1);
            }}
          >
            <span>Đã xuất bản</span>
            <span className="mc-tab-badge">{countsReady ? tabCounts.active : '—'}</span>
          </button>
        </div>

        {/* Toolbar: Search, Filters & Sorting */}
        <div className="mc-toolbar">
          <div className="mc-toolbar-row">
            {/* Search Input */}
            <div className="mc-search-box">
              <FontAwesomeIcon icon={faSearch} className="search-icon" />
              <input
                type="text"
                placeholder="Tìm theo tên khóa học..."
                value={searchQuery}
                onChange={e => {
                  setSearchQuery(e.target.value);
                  setCurrentPage(1);
                }}
              />
              {searchQuery && (
                <button onClick={() => setSearchQuery('')} className="clear-btn" title="Xóa tìm kiếm">
                  <FontAwesomeIcon icon={faTimes} />
                </button>
              )}
            </div>

            {/* Subject Filter */}
            <select
              aria-label="Môn học"
              value={selectedSubject}
              onChange={e => {
                setSelectedSubject(e.target.value);
                setCurrentPage(1);
              }}
              className="mc-filter-select"
            >
              <option value="ALL">Môn học: Tất cả</option>
              {availableSubjects.map(sub => (
                <option key={sub.id} value={sub.id}>
                  {sub.name}
                </option>
              ))}
            </select>

            {/* Grade Filter */}
            <select
              aria-label="Lớp"
              value={selectedGrade}
              onChange={e => {
                setSelectedGrade(e.target.value);
                setCurrentPage(1);
              }}
              className="mc-filter-select"
            >
              <option value="ALL">Lớp: Tất cả</option>
              {availableGrades.map(gr => (
                <option key={gr.id} value={gr.id}>
                  {gr.name}
                </option>
              ))}
            </select>

            {/* Sort Dropdown */}
            <div className="mc-sort-group">
              <span className="sort-label">Sắp xếp:</span>
              <select
                value={sortBy}
                aria-label="Sắp xếp"
                onChange={e => {
                  setSortBy(e.target.value as SortBy);
                  setCurrentPage(1);
                }}
                className="mc-filter-select"
              >
                <option value="updated_desc">Cập nhật mới nhất</option>
                <option value="updated_asc">Cũ nhất trước</option>
                <option value="title_asc">Tên (A → Z)</option>
                <option value="price_desc">Học phí: Cao → Thấp</option>
                <option value="price_asc">Học phí: Thấp → Cao</option>
              </select>
            </div>
          </div>

          {/* Meta text & reset */}
          <div className="mc-toolbar-meta">
            <div>
              Đang hiển thị <strong>{totalItems}</strong> khóa học trong tài khoản của bạn
              {searchQuery && <span> • Khớp với &ldquo;{searchQuery}&rdquo;</span>}
              {selectedSubject !== 'ALL' && (
                <span> • {availableSubjects.find(subject => String(subject.id) === selectedSubject)?.name}</span>
              )}
              {selectedGrade !== 'ALL' && <span> • {availableGrades.find(grade => String(grade.id) === selectedGrade)?.name}</span>}
            </div>
            {(searchQuery || selectedSubject !== 'ALL' || selectedGrade !== 'ALL' || statusTab !== 'ALL') && (
              <button onClick={handleResetFilters} className="reset-link">
                <FontAwesomeIcon icon={faRotateRight} />
                Đặt lại bộ lọc
              </button>
            )}
          </div>
        </div>

        {catalogError && (
          <div className="alert alert-warning m-3" role="alert">
            Không thể tải bộ lọc môn/lớp.{' '}
            <button className="btn btn-link" onClick={() => setRevision(value => value + 1)}>
              Thử lại
            </button>
          </div>
        )}
        {actionError && (
          <div className="alert alert-warning m-3" role="alert">
            {actionError}
          </div>
        )}
        {/* Table Content */}
        <div className="mc-table">
          {/* Table Header */}
          <div className="mc-thead">
            <div className="mc-col-course">Khóa học</div>
            <div className="mc-col-status">Trạng thái</div>
            <div className="mc-col-price">Học phí</div>
            <div className="mc-col-updated">Cập nhật</div>
            <div className="mc-col-actions">Thao tác</div>
          </div>

          {/* Rows */}
          <div className="mc-tbody">
            {loading ? (
              <div className="mc-empty" role="status">
                Đang tải khóa học...
              </div>
            ) : error ? (
              <div className="mc-empty" role="alert">
                <p>{error}</p>
                <button className="btn-empty-reset" onClick={() => setRevision(value => value + 1)}>
                  Thử lại
                </button>
              </div>
            ) : courses.length === 0 ? (
              <div className="mc-empty">
                <div className="empty-icon">
                  <FontAwesomeIcon icon={faSearch} />
                </div>
                <h4>Không tìm thấy khóa học phù hợp</h4>
                <p>Thử tìm kiếm với từ khóa khác hoặc xóa bộ lọc để hiển thị danh sách đầy đủ.</p>
                <button onClick={handleResetFilters} className="btn-empty-reset">
                  Xóa tất cả bộ lọc
                </button>
              </div>
            ) : (
              courses.map(course => (
                <div key={course.id} className="mc-row">
                  {/* 1. Khóa học */}
                  <div className="mc-col-course">
                    <div className="course-identity">
                      <div className="course-thumb-box">
                        <CourseThumbnail course={course} gradient={getSubjectGradient(course.subject?.name ?? '')} />
                      </div>

                      <div className="course-text-wrap">
                        <Link to={`/instructor/courses/${course.id}/builder`} className="course-title" title={course.title}>
                          {course.title}
                        </Link>

                        <div className="course-tags">
                          <span className={`tag ${getSubjectClass(course.subject?.name ?? '')}`}>
                            {course.subject?.name ?? 'Chưa chọn môn'}
                          </span>
                          <span className="tag tag-grade">{course.grade?.name ?? 'Chưa chọn lớp'}</span>
                          {course.bookSeries && (
                            <span className="tag tag-book" title={course.bookSeries}>
                              {course.bookSeries}
                            </span>
                          )}
                        </div>

                        <div className="course-stats">
                          <span>{course.chapterCount} chương</span>
                          <span>•</span>
                          <span>{course.videoCount} video</span>
                          <span>•</span>
                          <span>{(course.totalDurationHours ?? 0).toLocaleString('vi-VN', { maximumFractionDigits: 1 })} giờ học</span>
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* 2. Trạng thái */}
                  <div className="mc-col-status">
                    <div className="status-badge-wrap">
                      <div className={`status-pill status-${course.status.toLowerCase()}`}>
                        <span className="dot" />
                        <span>{courseStatusLabels[course.status]}</span>
                      </div>
                      <div className="status-note">
                        {course.status === 'DRAFT'
                          ? 'Chưa gửi duyệt'
                          : course.status === 'ACTIVE'
                            ? 'Đang hiển thị cho học viên'
                            : courseStatusLabels[course.status]}
                      </div>
                    </div>
                  </div>

                  {/* 3. Học phí */}
                  <div className="mc-col-price">
                    <div className="price-wrap">
                      <div className={`price-main ${course.price === 0 ? 'price-free' : ''}`}>{formatPrice(course.price)}</div>
                      <div className="price-sub">{accessDurationText(course)}</div>
                    </div>
                  </div>

                  {/* 4. Cập nhật */}
                  <div className="mc-col-updated">
                    <div className="updated-wrap">
                      <div className="updated-date">{course.updatedAt ? new Date(course.updatedAt).toLocaleDateString('vi-VN') : '—'}</div>
                      <div className="updated-time">
                        {course.updatedAt
                          ? new Date(course.updatedAt).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })
                          : ''}
                      </div>
                    </div>
                  </div>

                  {/* 5. Thao tác */}
                  <div className="mc-col-actions">
                    <div className="actions-wrap">
                      {course.status === 'PENDING' && (
                        <button
                          className="btn-act btn-act-withdraw"
                          title="Rút lại gửi duyệt"
                          disabled={withdrawing}
                          onClick={() => {
                            setCourseToWithdraw(course);
                            setWithdrawError(false);
                          }}
                        >
                          <FontAwesomeIcon icon={faRotateLeft} />
                          <span>Rút lại gửi duyệt</span>
                        </button>
                      )}
                      {course.status === 'DRAFT' && (
                        <button
                          onClick={() => navigate(`/instructor/courses/${course.id}/builder`)}
                          className="btn-act btn-act-edit"
                          disabled={course.status !== 'DRAFT'}
                          title={course.status === 'DRAFT' ? 'Chỉnh sửa nội dung khóa học' : 'Chỉ có thể sửa khóa học nháp'}
                        >
                          <FontAwesomeIcon icon={faPencilAlt} />
                          <span>Sửa</span>
                        </button>
                      )}

                      <button
                        onClick={() => navigate(`/instructor/courses/${course.id}/builder?step=preview`)}
                        className="btn-act btn-act-preview"
                        title="Xem trước giao diện học viên"
                      >
                        <FontAwesomeIcon icon={faEye} />
                        <span>Xem</span>
                      </button>

                      {course.status === 'DRAFT' && (
                        <button
                          onClick={() => {
                            setCourseToDelete(course);
                            setDeleteError(null);
                          }}
                          className="btn-act btn-act-delete"
                          disabled={deleting || course.status !== 'DRAFT'}
                          title={course.status === 'DRAFT' ? 'Xóa khóa học này' : 'Chỉ có thể xóa khóa học nháp'}
                        >
                          <FontAwesomeIcon icon={faTrash} />
                          <span>Xóa</span>
                        </button>
                      )}
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>

        {/* Pagination Bar */}
        {!loading && !error && totalItems > 0 && (
          <div className="mc-pagination">
            <div className="page-summary">
              <span>
                Hiển thị <strong>{startIndex + 1}</strong>–<strong>{Math.min(startIndex + pageSize, totalItems)}</strong> trong{' '}
                <strong>{totalItems}</strong> khóa học
              </span>
              <select
                value={pageSize}
                onChange={e => {
                  setPageSize(Number(e.target.value));
                  setCurrentPage(1);
                }}
                className="page-size-select"
              >
                <option value={5}>5 / trang</option>
                <option value={10}>10 / trang</option>
                <option value={20}>20 / trang</option>
              </select>
            </div>

            <div className="page-controls">
              <button className="pg-btn" disabled={currentPageSafe <= 1} onClick={() => handlePageChange(currentPageSafe - 1)}>
                <FontAwesomeIcon icon={faChevronLeft} className="me-1" />
                Trước
              </button>

              {Array.from({ length: totalPages }, (_, i) => i + 1).map(page => (
                <button key={page} className={`pg-btn ${page === currentPageSafe ? 'active' : ''}`} onClick={() => handlePageChange(page)}>
                  {page}
                </button>
              ))}

              <button className="pg-btn" disabled={currentPageSafe >= totalPages} onClick={() => handlePageChange(currentPageSafe + 1)}>
                Sau
                <FontAwesomeIcon icon={faChevronRight} className="ms-1" />
              </button>
            </div>
          </div>
        )}
      </div>

      {/* 5. Bottom Notice Bar */}
      <div className="mc-notice-bar">
        <FontAwesomeIcon icon={faExclamationTriangle} className="notice-icon" />
        <div className="notice-content">
          <strong>Lưu ý về trạng thái khóa học: </strong>
          Khóa học ở trạng thái <strong>Bản nháp</strong> chưa được xuất bản và chưa hiển thị cho học viên. Giảng viên cần hoàn thiện đầy đủ
          nội dung bài giảng, đặt học phí và bấm gửi duyệt tại bước Review để ban quản trị LUMI xét duyệt trước khi chính thức mở bán.
        </div>
      </div>

      <Modal show={courseToWithdraw !== null} centered backdrop="static" onHide={closeWithdraw} keyboard={!withdrawing}>
        <Modal.Header closeButton={!withdrawing}>
          <Modal.Title>Rút lại yêu cầu duyệt?</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          Khóa học sẽ quay về bản nháp để bạn chỉnh sửa. Lần gửi duyệt này vẫn được giữ trong lịch sử.
          {withdrawError && (
            <div className="alert alert-danger mt-3 mb-0" role="alert">
              Không thể rút duyệt. Hãy đóng hộp thoại và tải lại dữ liệu; yêu cầu có thể đã được xử lý hoặc phiên bản đã thay đổi.
            </div>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" disabled={withdrawing} onClick={closeWithdraw}>
            Hủy
          </Button>
          <Button
            variant="warning"
            disabled={withdrawing || withdrawError}
            onClick={() => {
              void confirmWithdraw();
            }}
          >
            {withdrawing ? 'Đang rút duyệt...' : 'Xác nhận rút duyệt'}
          </Button>
        </Modal.Footer>
      </Modal>
      {/* 6. Delete Confirmation Modal */}
      <Modal show={courseToDelete !== null} onHide={closeDelete} backdrop={deleting ? 'static' : true} keyboard={!deleting} centered>
        <Modal.Header closeButton={!deleting}>
          <Modal.Title className="h6 fw-bold text-dark m-0 d-flex align-items-center gap-2">
            <FontAwesomeIcon icon={faTrash} className="text-danger" />
            Xác nhận xóa khóa học
          </Modal.Title>
        </Modal.Header>
        <Modal.Body className="py-3">
          <p className="text-muted small mb-2">Bạn có chắc chắn muốn xóa khóa học sau khỏi tài khoản không?</p>
          <div className="p-3 bg-light border rounded fw-bold text-dark small">{courseToDelete?.title}</div>
          <p className="text-danger small mt-2 mb-0">
            Khóa học sẽ được gỡ khỏi danh sách quản lý. Chỉ khóa học nháp chưa chờ duyệt mới có thể xóa.
          </p>
          {deleteError && (
            <div className="alert alert-danger mt-3 mb-0" role="alert">
              {deleteError}
            </div>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="outline-secondary" size="sm" disabled={deleting} onClick={closeDelete}>
            Hủy bỏ
          </Button>
          <Button variant="danger" size="sm" disabled={deleting} onClick={confirmDelete}>
            {deleting ? 'Đang xóa...' : 'Đồng ý xóa'}
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};
