import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router';

import authorIcon from '/content/images/course-detail/author.svg';
import breadcrumbIcon from '/content/images/course-detail/breadcrumb.svg';
import calendarIcon from '/content/images/course-detail/calendar.svg';
import downIcon from '/content/images/course-detail/chevron-down.svg';
import rightIcon from '/content/images/course-detail/chevron-right.svg';
import fileIcon from '/content/images/course-detail/file-text.svg';
import heartIcon from '/content/images/course-detail/heart.svg';
import lockIcon from '/content/images/course-detail/lock.svg';
import monitorIcon from '/content/images/course-detail/monitor.svg';
import starIcon from '/content/images/course-detail/star.svg';
import { useAppSelector } from 'app/config/store';
import type { CourseChapter, CourseOverview, CourseRatingSummary, CourseReviewSort } from './course-detail.model';
import { useCourseOverview, useCourseReviews } from './use-course-detail';

import './course-detail.scss';

const currency = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' });
const decimal = new Intl.NumberFormat('vi-VN', { minimumFractionDigits: 1, maximumFractionDigits: 1 });
const dateFormat = new Intl.DateTimeFormat('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
const priceLabel = (price: number | null) => (price === null ? 'Chưa cập nhật giá' : price === 0 ? 'Miễn phí' : currency.format(price));

const Stars = ({ value }: { value: number }) => (
  <span className="course-detail-stars" role="img" aria-label={`${decimal.format(value)} trên 5 sao`}>
    {[0, 1, 2, 3, 4].map(index => (
      <span className="course-detail-star" key={index} aria-hidden="true">
        <img src={starIcon} alt="" width={14} height={14} />
        <span style={{ width: `${Math.max(0, Math.min(1, value - index)) * 100}%` }}>
          <img src={starIcon} alt="" width={14} height={14} />
        </span>
      </span>
    ))}
  </span>
);

const PersonImage = ({ imageUrl, name, instructor = false }: { imageUrl: string | null; name: string; instructor?: boolean }) => {
  const [failed, setFailed] = useState(false);
  const initials = name
    .trim()
    .split(/\s+/)
    .slice(-2)
    .map(part => part[0])
    .join('')
    .toUpperCase();
  return (
    <div className={`course-detail-avatar${instructor ? ' is-instructor' : ''}`}>
      {imageUrl?.trim() && !failed ? (
        <img src={imageUrl} alt={`Ảnh đại diện ${name}`} onError={() => setFailed(true)} />
      ) : instructor ? (
        <img src={authorIcon} alt="" width={88} height={88} />
      ) : (
        <span aria-hidden="true">{initials || 'HV'}</span>
      )}
    </div>
  );
};

const CourseCover = ({ course }: { course: CourseOverview }) => {
  const [failed, setFailed] = useState(false);
  return (
    <div className="course-detail-cover">
      {course.thumbnailUrl?.trim() && !failed ? (
        <img src={course.thumbnailUrl} alt={`Ảnh bìa khóa học ${course.title}`} onError={() => setFailed(true)} />
      ) : (
        <div className="course-detail-cover-fallback">
          <span>
            {course.subjectName || 'LUMI'}
            {course.gradeLevel != null ? ` · Lớp ${course.gradeLevel}` : ''}
          </span>
          <strong>{course.title}</strong>
          <span>Học theo nhịp của bạn</span>
        </div>
      )}
    </div>
  );
};

const EnrollAction = ({ course, isAuthenticated }: { course: CourseOverview; isAuthenticated: boolean }) => {
  const label = course.price === 0 ? 'Đăng ký học miễn phí' : 'Đăng ký học';
  return isAuthenticated ? (
    <button className="course-detail-button" type="button" disabled title="Tính năng đăng ký học sắp ra mắt">
      {label}
    </button>
  ) : (
    <Link to="/login" state={{ from: { pathname: `/courses/${course.id}` } }} className="course-detail-button">
      {label}
    </Link>
  );
};

const Chapter = ({
  chapter,
  expanded,
  showAll,
  onToggle,
}: {
  chapter: CourseChapter;
  expanded: boolean;
  showAll: boolean;
  onToggle: () => void;
}) => {
  const [allLessons, setAllLessons] = useState(false);
  const lessons = chapter.lessons ?? [];
  const visibleLessons = allLessons || showAll ? lessons : lessons.slice(0, 3);
  return (
    <div className={`course-detail-chapter${expanded ? ' is-expanded' : ''}`}>
      <button
        className="course-detail-chapter-toggle"
        type="button"
        aria-expanded={expanded}
        aria-controls={`course-chapter-${chapter.id}`}
        onClick={onToggle}
      >
        <img src={expanded ? downIcon : rightIcon} alt="" width={16} height={20} />
        <span>
          <strong>{chapter.title}</strong>
          {!expanded && lessons.length > 0 && (
            <small>
              {lessons
                .slice(0, 3)
                .map(lesson => lesson.title)
                .join(' · ')}
            </small>
          )}
        </span>
        <small>{chapter.lessonCount} bài</small>
      </button>
      <div id={`course-chapter-${chapter.id}`} hidden={!expanded}>
        {lessons.length === 0 && <p className="course-detail-no-lessons">Chương này chưa có bài học công khai.</p>}
        <ul className="course-detail-lessons">
          {visibleLessons.map(lesson => (
            <li key={lesson.id}>
              <strong>{lesson.title}</strong>
              <span>
                {lesson.contentType?.trim() || 'Bài học'}
                {lesson.durationMinutes != null && lesson.durationMinutes > 0 && ` · ${lesson.durationMinutes} phút`}
              </span>
            </li>
          ))}
        </ul>
        {visibleLessons.length < lessons.length && (
          <button className="course-detail-text-button" type="button" onClick={() => setAllLessons(true)}>
            Xem thêm {lessons.length - visibleLessons.length} bài trong chương
          </button>
        )}
      </div>
    </div>
  );
};

const RatingSummary = ({ summary }: { summary: CourseRatingSummary }) => {
  const counts = [summary.fiveStarCount, summary.fourStarCount, summary.threeStarCount, summary.twoStarCount, summary.oneStarCount];
  return (
    <div className="course-detail-rating-summary">
      <p className="course-detail-average">
        <strong>{summary.averageRating === null ? '—' : decimal.format(summary.averageRating)}</strong> / 5
      </p>
      {summary.averageRating !== null && <Stars value={summary.averageRating} />}
      <p className="course-detail-muted">Từ {summary.totalReviews} đánh giá</p>
      <div className="course-detail-distribution">
        {counts.map((count, index) => {
          const percent = summary.totalReviews > 0 ? (count / summary.totalReviews) * 100 : 0;
          return (
            <div key={5 - index} aria-label={`${5 - index} sao: ${count} đánh giá`}>
              <span>{5 - index} ★</span>
              <span className="course-detail-rating-track">
                <span style={{ width: `${percent}%` }} />
              </span>
              <span>
                {Math.round(percent)}% ({count})
              </span>
            </div>
          );
        })}
      </div>
    </div>
  );
};

const CourseReviews = ({ course, isAuthenticated }: { course: CourseOverview; isAuthenticated: boolean }) => {
  const [query, setQuery] = useState<{ page: number; sort: CourseReviewSort }>({ page: 0, sort: 'createdAt,desc' });
  const request = useCourseReviews(course.id, query.page, query.sort);
  const data = request.data;
  const summary = data?.ratingSummary ?? course.ratingSummary;
  const hasReviews = data != null && data.content.length > 0;
  const firstResult = hasReviews ? data.page * data.size + 1 : 0;
  const lastResult = hasReviews ? data.page * data.size + data.content.length : 0;
  return (
    <section className="course-detail-panel course-detail-reviews" id="course-reviews" aria-labelledby="course-reviews-title">
      <h2 id="course-reviews-title">Đánh giá học viên</h2>
      <div className="course-detail-review-layout">
        {summary.totalReviews > 0 && <RatingSummary summary={summary} />}
        <div className="course-detail-review-list" aria-busy={request.loading}>
          {summary.totalReviews > 0 && (
            <div className="course-detail-review-toolbar">
              <strong>Tất cả nhận xét ({summary.totalReviews})</strong>
              <label>
                <span className="visually-hidden">Sắp xếp nhận xét</span>
                <select value={query.sort} onChange={event => setQuery({ page: 0, sort: event.target.value as CourseReviewSort })}>
                  <option value="createdAt,desc">Mới nhất</option>
                  <option value="rating,desc">Sao cao nhất</option>
                  <option value="rating,asc">Sao thấp nhất</option>
                </select>
              </label>
            </div>
          )}
          {request.loading ? (
            <p className="course-detail-feedback" role="status">
              Đang tải đánh giá…
            </p>
          ) : request.error ? (
            <div className="course-detail-feedback" role="alert">
              <p>{request.notFound ? 'Đánh giá của khóa học này hiện không khả dụng.' : request.error}</p>
              <button className="course-detail-text-button" type="button" onClick={request.reload}>
                Thử lại
              </button>
            </div>
          ) : data && data.totalElements === 0 ? (
            <div className="course-detail-empty-reviews">
              <h3>Chưa có đánh giá</h3>
              <p>Đánh giá sẽ xuất hiện tại đây khi học viên chia sẻ trải nghiệm.</p>
            </div>
          ) : data ? (
            <>
              {data.content.map(review => {
                const name = review.displayName?.trim() || 'Học viên';
                const createdAt = review.createdAt ? new Date(review.createdAt) : null;
                return (
                  <article className="course-detail-review" key={review.id}>
                    <div className="course-detail-review-person">
                      <PersonImage key={review.imageUrl} imageUrl={review.imageUrl} name={name} />
                      <div>
                        <strong>{name}</strong>
                        <Stars value={review.rating} />
                      </div>
                      {createdAt && !Number.isNaN(createdAt.getTime()) && (
                        <time dateTime={review.createdAt!}>{dateFormat.format(createdAt)}</time>
                      )}
                    </div>
                    {review.content && <p>{review.content}</p>}
                  </article>
                );
              })}
              {data.content.length === 0 && <p className="course-detail-feedback">Không có nhận xét ở trang này.</p>}
              <div className="course-detail-review-pagination">
                <span>
                  Hiển thị {firstResult}–{lastResult} / {data.totalElements} nhận xét
                </span>
                {data.totalPages > 1 && (
                  <nav aria-label="Phân trang nhận xét">
                    <button
                      type="button"
                      disabled={query.page === 0}
                      onClick={() => setQuery(previous => ({ ...previous, page: previous.page - 1 }))}
                    >
                      Trang trước
                    </button>
                    <span>
                      {data.page + 1} / {data.totalPages}
                    </span>
                    <button
                      type="button"
                      disabled={query.page >= data.totalPages - 1}
                      onClick={() => setQuery(previous => ({ ...previous, page: previous.page + 1 }))}
                    >
                      Trang sau
                    </button>
                  </nav>
                )}
              </div>
            </>
          ) : null}
        </div>
      </div>
      <div className="course-detail-bottom-cta">
        <div>
          <h3>Bắt đầu hành trình học tập cùng LUMI</h3>
          <p>
            {priceLabel(course.price)} · {course.lessonCount} bài học
          </p>
        </div>
        <EnrollAction course={course} isAuthenticated={isAuthenticated} />
      </div>
    </section>
  );
};

const CourseContent = ({ course }: { course: CourseOverview }) => {
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);
  const [activeSection, setActiveSection] = useState('course-introduction');
  const [expanded, setExpanded] = useState<Set<number>>(() => new Set(course.chapters.slice(0, 1).map(chapter => chapter.id)));
  const [allChapters, setAllChapters] = useState(false);
  const [allLessons, setAllLessons] = useState(false);
  const chapters = allChapters ? course.chapters : course.chapters.slice(0, 3);
  const allExpanded = course.chapters.length > 0 && course.chapters.every(chapter => expanded.has(chapter.id));
  const free = course.price === 0;
  const summary = course.ratingSummary;
  const author = course.author;
  const authorName = author?.fullName?.trim() || 'Giảng viên';
  const hasVideo = course.chapters.some(chapter => chapter.lessons.some(lesson => lesson.contentType?.toUpperCase().includes('VIDEO')));
  const allPdf =
    course.lessonCount > 0 &&
    course.chapters.every(chapter => chapter.lessons.every(lesson => lesson.contentType?.toUpperCase() === 'PDF'));
  const materialsLabel = allPdf ? 'Tài liệu PDF theo từng bài' : hasVideo ? 'Bài học và tài liệu, kèm video' : 'Nội dung theo từng bài học';
  const sections = [
    { id: 'course-introduction', label: 'Tổng quan' },
    { id: 'course-curriculum', label: 'Đề cương' },
    ...(author ? [{ id: 'course-instructor', label: 'Giảng viên' }] : []),
    { id: 'course-reviews', label: 'Đánh giá' },
  ];

  useEffect(() => {
    document.title = `${course.title} | LUMI`;
    const observer = new IntersectionObserver(
      entries => {
        for (const entry of entries) if (entry.isIntersecting) setActiveSection(entry.target.id);
      },
      { rootMargin: '-100px 0px -55% 0px', threshold: 0 },
    );
    for (const section of sections) {
      const element = document.getElementById(section.id);
      if (element) observer.observe(element);
    }
    return () => observer.disconnect();
  }, [course.id]);

  const toggleChapter = (id: number) =>
    setExpanded(previous => {
      const next = new Set(previous);
      if (next.has(id)) next.delete(id);
      else next.add(id);
      return next;
    });
  const toggleAll = () => {
    setAllChapters(true);
    setAllLessons(!allExpanded);
    setExpanded(new Set(allExpanded ? [] : course.chapters.map(chapter => chapter.id)));
  };

  return (
    <>
      <div className="course-detail-heading">
        <div className="course-detail-tags">
          {course.subjectName && <span>{course.subjectName}</span>}
          {course.gradeLevel != null && <span>Lớp {course.gradeLevel}</span>}
          {course.price != null && <span>{free ? 'Miễn phí' : 'Có học phí'}</span>}
        </div>
        <h1>{course.title}</h1>
        <p>Nội dung được sắp xếp theo chương và bài học, giúp bạn chủ động học tập.</p>
        <div className="course-detail-heading-meta">
          {author && <Link to={`/instructors/${author.id}`}>Giảng viên: {authorName}</Link>}
          {summary.totalReviews > 0 && summary.averageRating !== null && (
            <a href="#course-reviews">
              <Stars value={summary.averageRating} />
              <strong>{decimal.format(summary.averageRating)} / 5</strong>
              <span>({summary.totalReviews} đánh giá)</span>
            </a>
          )}
        </div>
      </div>

      <div className="course-detail-columns course-detail-hero">
        <div>
          <CourseCover key={course.thumbnailUrl} course={course} />
          <p className="course-detail-cover-caption">
            {course.subjectName}
            {course.gradeLevel != null ? ` · Lớp ${course.gradeLevel}` : ''} · {course.lessonCount} bài học
          </p>
        </div>
        <aside className="course-detail-panel course-detail-purchase" aria-label="Thông tin học phí">
          <div>
            <p className="course-detail-eyebrow">{free ? 'KHÓA HỌC MIỄN PHÍ' : 'HỌC PHÍ KHÓA HỌC'}</p>
            <p className="course-detail-price">{priceLabel(course.price)}</p>
            <p className="course-detail-muted">{free ? 'Không cần thanh toán' : 'Thanh toán một lần'}</p>
          </div>
          <ul>
            <li>
              <img src={fileIcon} alt="" width={18} height={18} />
              {course.chapterCount} chương · {course.lessonCount} bài học
            </li>
            <li>
              <img src={fileIcon} alt="" width={18} height={18} />
              {materialsLabel}
            </li>
            {course.validDurationDays != null && (
              <li>
                <img src={calendarIcon} alt="" width={18} height={18} />
                Truy cập trong {course.validDurationDays} ngày
              </li>
            )}
            <li>
              <img src={monitorIcon} alt="" width={18} height={18} />
              Học trực tuyến, chủ động theo tiến độ
            </li>
          </ul>
          <EnrollAction course={course} isAuthenticated={isAuthenticated} />
          <p className="course-detail-purchase-note">
            {isAuthenticated ? 'Tính năng đăng ký học sắp ra mắt.' : 'Đăng nhập hoặc tạo tài khoản để tiếp tục đăng ký học.'}
          </p>
        </aside>
      </div>

      <nav className="course-detail-tabs" aria-label="Nội dung khóa học">
        {sections.map(section => (
          <a
            key={section.id}
            href={`#${section.id}`}
            aria-current={activeSection === section.id ? 'location' : undefined}
            onClick={() => setActiveSection(section.id)}
          >
            {section.label}
          </a>
        ))}
      </nav>

      <section
        className="course-detail-panel course-detail-introduction"
        id="course-introduction"
        aria-labelledby="course-introduction-title"
      >
        <h2 id="course-introduction-title">Giới thiệu khóa học</h2>
        <p>{course.description?.trim() || 'Thông tin giới thiệu khóa học đang được cập nhật.'}</p>
      </section>

      <div className="course-detail-columns">
        <section className="course-detail-panel course-detail-curriculum" id="course-curriculum" aria-labelledby="course-curriculum-title">
          <h2 id="course-curriculum-title">Đề cương khóa học</h2>
          <div className="course-detail-curriculum-toolbar">
            <span>
              {course.chapterCount} chương · {course.lessonCount} bài học
            </span>
            {course.chapters.length > 0 && (
              <button className="course-detail-text-button" type="button" onClick={toggleAll}>
                {allExpanded ? 'Thu gọn tất cả' : 'Mở rộng tất cả'}
              </button>
            )}
          </div>
          <div className="course-detail-chapters">
            {chapters.map(chapter => (
              <Chapter
                key={chapter.id}
                chapter={chapter}
                expanded={expanded.has(chapter.id)}
                showAll={allLessons}
                onToggle={() => toggleChapter(chapter.id)}
              />
            ))}
          </div>
          {course.chapters.length === 0 && <p className="course-detail-feedback">Đề cương khóa học đang được cập nhật.</p>}
          {!allChapters && course.chapters.length > chapters.length && (
            <button className="course-detail-more-chapters" type="button" onClick={() => setAllChapters(true)}>
              Xem thêm {course.chapters.length - chapters.length} chương
              <img src={downIcon} alt="" width={16} height={20} />
            </button>
          )}
          <p className="course-detail-access-note">
            <img src={lockIcon} alt="" width={17} height={20} />
            Đề cương giúp bạn tìm hiểu nội dung khóa học. Đăng ký học để truy cập các bài học.
          </p>
        </section>
        <aside className="course-detail-guidance">
          <section className="course-detail-panel">
            <h2>Học theo nhịp của bạn</h2>
            <ol>
              <li>
                <span>01</span>
                <div>
                  <strong>Chọn chủ đề</strong>
                  <p>Tìm chương và bài học bạn muốn tìm hiểu.</p>
                </div>
              </li>
              <li>
                <span>02</span>
                <div>
                  <strong>{allPdf ? 'Đọc tài liệu PDF' : 'Khám phá bài học'}</strong>
                  <p>Theo dõi nội dung và các ví dụ trong bài.</p>
                </div>
              </li>
              <li>
                <span>03</span>
                <div>
                  <strong>Ôn lại kiến thức</strong>
                  <p>Ghi chép và xem lại những phần chưa hiểu.</p>
                </div>
              </li>
            </ol>
          </section>
          <section className="course-detail-panel course-detail-save">
            <h3>Lưu để học sau</h3>
            <p>Thêm khóa học vào danh sách yêu thích để dễ tìm lại.</p>
            {isAuthenticated ? (
              <button className="course-detail-button is-outline" type="button" disabled>
                <img src={heartIcon} alt="" width={17} height={17} />
                Lưu yêu thích
              </button>
            ) : (
              <Link className="course-detail-button is-outline" to="/login" state={{ from: { pathname: `/courses/${course.id}` } }}>
                <img src={heartIcon} alt="" width={17} height={17} />
                Lưu yêu thích
              </Link>
            )}
            <small>{isAuthenticated ? 'Tính năng lưu yêu thích sắp ra mắt.' : 'Đăng nhập để lưu khóa học.'}</small>
            <div className="course-detail-save-help">
              <p>Bạn muốn tìm hiểu thêm về nội dung?</p>
              <a href="#course-curriculum">Khám phá đề cương →</a>
            </div>
          </section>
        </aside>
      </div>

      {author && (
        <section className="course-detail-panel" id="course-instructor" aria-labelledby="course-instructor-title">
          <div className="course-detail-section-heading">
            <h2 id="course-instructor-title">Giảng viên</h2>
            <Link to={`/instructors/${author.id}`}>Xem hồ sơ →</Link>
          </div>
          <div className="course-detail-instructor">
            <PersonImage key={author.imageUrl} name={authorName} imageUrl={author.imageUrl} instructor />
            <div>
              <Link to={`/instructors/${author.id}`}>
                <strong>{authorName}</strong>
              </Link>
              <p>{author.bio?.trim() || 'Giảng viên chưa cập nhật giới thiệu.'}</p>
            </div>
          </div>
        </section>
      )}

      <CourseReviews course={course} isAuthenticated={isAuthenticated} />
    </>
  );
};

const CourseDetail = () => {
  const { id: routeId } = useParams<{ id: string }>();
  const parsedId = Number(routeId);
  const id = routeId && /^\d+$/.test(routeId) && Number.isSafeInteger(parsedId) && parsedId > 0 ? parsedId : null;
  const request = useCourseOverview(id);
  const course = request.data;
  return (
    <main className="lumi-course-detail">
      <div className="course-detail-content">
        <nav className="course-detail-breadcrumb" aria-label="Đường dẫn">
          <Link to="/">Trang chủ</Link>
          <img src={breadcrumbIcon} alt="" width={12} height={12} />
          <Link to="/courses">Khóa học</Link>
          <img src={breadcrumbIcon} alt="" width={12} height={12} />
          {course?.subjectName && (
            <>
              <span>
                {course.subjectName}
                {course.gradeLevel != null ? ` lớp ${course.gradeLevel}` : ''}
              </span>
              <img src={breadcrumbIcon} alt="" width={12} height={12} />
            </>
          )}
          <span aria-current="page">Chi tiết khóa học</span>
        </nav>
        {request.loading && (
          <div className="course-detail-panel course-detail-feedback" role="status">
            Đang tải thông tin khóa học…
          </div>
        )}
        {(id === null || request.notFound) && (
          <div className="course-detail-panel course-detail-feedback" role="alert">
            <h1>Không tìm thấy khóa học</h1>
            <p>Khóa học không tồn tại hoặc không còn được công khai.</p>
            <Link className="course-detail-text-button" to="/courses">
              Quay lại danh sách khóa học
            </Link>
          </div>
        )}
        {request.error && !request.notFound && (
          <div className="course-detail-panel course-detail-feedback" role="alert">
            <p>{request.error}</p>
            <button className="course-detail-text-button" type="button" onClick={request.reload}>
              Thử lại
            </button>
          </div>
        )}
        {course && <CourseContent key={course.id} course={course} />}
      </div>
    </main>
  );
};

export default CourseDetail;
