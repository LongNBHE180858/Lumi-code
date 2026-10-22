import React, { useEffect, useState } from 'react';
import { Button } from 'react-bootstrap';
import { Link } from 'react-router';

import curriculumBook from '/content/images/courses/curriculum-book.svg';
import filterIcon from '/content/images/courses/filter.svg';
import lessonBook from '/content/images/courses/lesson-book.svg';
import searchIcon from '/content/images/courses/search.svg';
import { useAppSelector } from 'app/config/store';
import { defaultCourseFilters } from './courses.model';
import type { CourseBrowseRequest, CourseFeeType, CourseItem } from './courses.model';
import { useCourseBrowse, useCourseFilterOptions } from './use-courses';

import './courses.scss';

const currency = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' });
const feeOptions: { value: CourseFeeType; label: string }[] = [
  { value: 'ALL', label: 'Tất cả học phí' },
  { value: 'FREE', label: 'Miễn phí' },
  { value: 'PAID', label: 'Có học phí' },
];

const CourseCard = ({ course }: { course: CourseItem }) => {
  const subjectName = course.subjectName?.trim();
  const isHistory =
    subjectName
      ?.normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toLowerCase() === 'lich su';
  return (
    <article className={`course-browse-card${isHistory ? ' course-browse-card-history' : ''}`}>
      <div className="course-browse-card-heading">
        {subjectName && <span>{subjectName}</span>}
        {course.gradeLevel != null ? <span>{course.gradeLevel}</span> : course.gradeName && <span>{course.gradeName}</span>}
      </div>
      <div className="course-browse-card-body">
        <h2>{course.title}</h2>
        {course.authorDisplayName?.trim() && <p className="course-browse-author">Người biên soạn: {course.authorDisplayName}</p>}
        {course.lessonCount != null && (
          <p className="course-browse-lessons">
            <img src={lessonBook} alt="" width={15} height={15} />
            {course.lessonCount} bài học
          </p>
        )}
        <div className="course-browse-card-bottom">
          <p className={`course-browse-price${course.price === 0 ? ' course-browse-price-free' : ''}`}>
            {course.price == null ? 'Chưa cập nhật giá' : course.price === 0 ? 'Miễn phí' : currency.format(course.price)}
          </p>
          <Link className="btn btn-primary course-browse-button" to={`/courses/${course.id}`}>
            Xem chi tiết
          </Link>
        </div>
      </div>
    </article>
  );
};

const Courses = () => {
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);
  const sessionHasBeenFetched = useAppSelector(state => state.authentication.sessionHasBeenFetched);
  const [filters, setFilters] = useState<CourseBrowseRequest>(defaultCourseFilters);
  const [keyword, setKeyword] = useState('');
  const [filtersOpen, setFiltersOpen] = useState(false);
  const courses = useCourseBrowse(filters);
  const options = useCourseFilterOptions();

  useEffect(() => {
    const timer = window.setTimeout(() => {
      setFilters(previous => (previous.keyword === keyword.trim() ? previous : { ...previous, keyword: keyword.trim(), page: 0 }));
    }, 350);
    return () => window.clearTimeout(timer);
  }, [keyword]);

  const updateFilters = (changes: Partial<CourseBrowseRequest>) => setFilters(previous => ({ ...previous, ...changes, page: 0 }));
  const resetFilters = () => {
    setKeyword('');
    setFilters(defaultCourseFilters);
  };
  const toggleGrade = (id: number) =>
    updateFilters({
      gradeIds: filters.gradeIds.includes(id) ? filters.gradeIds.filter(gradeId => gradeId !== id) : [...filters.gradeIds, id],
    });

  const data = courses.data;
  const totalPages = data?.totalPages ?? 0;
  const pages: number[] = [];
  for (let page = Math.max(0, filters.page - 2); page <= Math.min(totalPages - 1, filters.page + 2); page++) pages.push(page);
  const firstResult = data && data.content.length > 0 ? data.page * data.size + 1 : 0;
  const lastResult = data && data.content.length > 0 ? data.page * data.size + data.content.length : 0;

  return (
    <main className="lumi-courses">
      <div className="course-browse-content">
        <div className="course-browse-heading">
          <div>
            <h1>Khóa học</h1>
            <p>Khám phá Toán và Lịch sử, từng bước học vững kiến thức.</p>
          </div>
          <span className="course-browse-curriculum">
            <img src={curriculumBook} alt="" width={18} height={18} />
            Theo chương trình MOET
          </span>
        </div>

        {!sessionHasBeenFetched && !isAuthenticated ? (
          <div className="course-browse-feedback" role="status">
            Đang tải thông tin tài khoản…
          </div>
        ) : !isAuthenticated ? (
          <div className="course-browse-feedback">
            <h2>Đăng nhập để khám phá khóa học</h2>
            <p>Bạn cần đăng nhập để xem danh sách khóa học hiện tại.</p>
            <Link to="/login" state={{ from: { pathname: '/courses' } }} className="btn btn-primary course-browse-button">
              Đăng nhập
            </Link>
          </div>
        ) : (
          <>
            <Button
              className="course-browse-mobile-filter"
              variant="outline-primary"
              onClick={() => setFiltersOpen(open => !open)}
              aria-expanded={filtersOpen}
              aria-controls="course-browse-filters"
            >
              <img src={filterIcon} alt="" width={18} height={18} />
              Bộ lọc
            </Button>
            <div className="course-browse-layout">
              <aside
                id="course-browse-filters"
                className={`course-browse-filters${filtersOpen ? ' is-open' : ''}`}
                aria-label="Bộ lọc khóa học"
              >
                <div className="course-browse-filter-heading">
                  <h2>
                    <img src={filterIcon} alt="" width={18} height={18} />
                    Bộ lọc
                  </h2>
                  <button type="button" onClick={resetFilters}>
                    Đặt lại
                  </button>
                </div>
                {options.loading && <p role="status">Đang tải bộ lọc…</p>}
                {options.error && (
                  <div className="course-browse-filter-error" role="alert">
                    <p>{options.error}</p>
                    <button type="button" onClick={options.reload}>
                      Thử lại
                    </button>
                  </div>
                )}
                <fieldset>
                  <legend>Môn học</legend>
                  <label className="course-browse-radio">
                    <input
                      type="radio"
                      name="course-subject"
                      checked={filters.subjectId === null}
                      onChange={() => updateFilters({ subjectId: null })}
                    />
                    Tất cả môn học
                  </label>
                  {options.data?.subjects.map(subject => (
                    <label className="course-browse-radio" key={subject.id}>
                      <input
                        type="radio"
                        name="course-subject"
                        checked={filters.subjectId === subject.id}
                        onChange={() => updateFilters({ subjectId: subject.id })}
                      />
                      {subject.name}
                    </label>
                  ))}
                </fieldset>
                <fieldset>
                  <legend>Khối lớp</legend>
                  <div className="course-browse-grade-grid">
                    {options.data?.grades.map(grade => (
                      <button
                        key={grade.id}
                        type="button"
                        className={filters.gradeIds.includes(grade.id) ? 'is-selected' : ''}
                        aria-pressed={filters.gradeIds.includes(grade.id)}
                        onClick={() => toggleGrade(grade.id)}
                      >
                        Lớp {grade.level}
                      </button>
                    ))}
                  </div>
                </fieldset>
                <fieldset>
                  <legend>Học phí</legend>
                  {feeOptions.map(option => (
                    <label className="course-browse-radio" key={option.value}>
                      <input
                        type="radio"
                        name="course-fee"
                        checked={filters.feeType === option.value}
                        onChange={() => updateFilters({ feeType: option.value })}
                      />
                      {option.label}
                    </label>
                  ))}
                </fieldset>
                <div className="course-browse-tip">
                  <strong>Học đúng lớp, chọn đúng môn</strong>
                  <p>Chọn môn và khối lớp để tìm nội dung phù hợp với bạn.</p>
                </div>
              </aside>

              <section className="course-browse-results" aria-label="Danh sách khóa học" aria-busy={courses.loading}>
                <div className="course-browse-search">
                  <img src={searchIcon} alt="" width={20} height={20} />
                  <input
                    type="search"
                    aria-label="Tìm kiếm khóa học"
                    placeholder="Tìm theo tên khóa học…"
                    value={keyword}
                    onChange={event => setKeyword(event.target.value)}
                  />
                </div>
                {courses.loading ? (
                  <div className="course-browse-feedback" role="status">
                    Đang tải danh sách khóa học…
                  </div>
                ) : courses.error ? (
                  <div className="course-browse-feedback" role="alert">
                    <p>{courses.error}</p>
                    <Button variant="outline-primary" onClick={courses.reload}>
                      Thử lại
                    </Button>
                  </div>
                ) : data ? (
                  <>
                    <div className="course-browse-result-heading" aria-live="polite">
                      <p>
                        <strong>{data.totalElements} khóa học</strong>
                      </p>
                      <span>Nội dung phù hợp từng khối lớp</span>
                    </div>
                    {data.content.length > 0 ? (
                      <div className="course-browse-grid">
                        {data.content.map(course => (
                          <CourseCard key={course.id} course={course} />
                        ))}
                      </div>
                    ) : (
                      <div className="course-browse-feedback">
                        <h2>Chưa tìm thấy khóa học phù hợp</h2>
                        <p>Thử thay đổi từ khóa hoặc chọn lại bộ lọc.</p>
                        <Button variant="outline-primary" onClick={resetFilters}>
                          Xóa bộ lọc
                        </Button>
                      </div>
                    )}
                    <div className="course-browse-pagination">
                      <p>
                        Hiển thị {firstResult}–{lastResult} trên {data.totalElements} khóa học
                      </p>
                      {totalPages > 0 && (
                        <nav aria-label="Phân trang khóa học">
                          <button
                            type="button"
                            aria-label="Trang trước"
                            disabled={filters.page === 0}
                            onClick={() => setFilters(previous => ({ ...previous, page: previous.page - 1 }))}
                          >
                            ‹
                          </button>
                          {pages.map(page => (
                            <button
                              key={page}
                              type="button"
                              aria-label={`Trang ${page + 1}`}
                              aria-current={page === filters.page ? 'page' : undefined}
                              onClick={() => setFilters(previous => ({ ...previous, page }))}
                            >
                              {page + 1}
                            </button>
                          ))}
                          <button
                            type="button"
                            aria-label="Trang sau"
                            disabled={filters.page >= totalPages - 1}
                            onClick={() => setFilters(previous => ({ ...previous, page: previous.page + 1 }))}
                          >
                            ›
                          </button>
                        </nav>
                      )}
                    </div>
                  </>
                ) : null}
              </section>
            </div>
          </>
        )}
      </div>
    </main>
  );
};

export default Courses;
