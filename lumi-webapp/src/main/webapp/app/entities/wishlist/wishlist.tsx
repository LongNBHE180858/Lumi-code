import './wishlist.scss';

import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { Translate, TextFormat, translate } from 'react-jhipster';
import { Link } from 'react-router';

import { faPencilAlt, faSync } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import dayjs from 'dayjs';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { IWishlist } from 'app/shared/model/wishlist.model';

import arrowRight from '/content/images/wishlist/arrow-right.svg';
import bookOpen from '/content/images/wishlist/book-open.svg';
import chevronDown from '/content/images/wishlist/chevron-down-filter.svg';
import chevronRight from '/content/images/wishlist/chevron-right.svg';
import heart from '/content/images/wishlist/heart.svg';
import info from '/content/images/wishlist/info.svg';
import search from '/content/images/wishlist/search.svg';
import trash from '/content/images/wishlist/trash.svg';

import { getEntities } from './wishlist.reducer';

type WishlistSort = 'recent' | 'oldest' | 'price-asc' | 'price-desc' | 'title';

const getRequestErrorMessage = (error: unknown) => {
  if (error instanceof Error) {
    return error.message;
  }
  if (typeof error === 'string') {
    return error;
  }
  if (typeof error === 'object' && error !== null && 'message' in error && typeof error.message === 'string') {
    return error.message;
  }
  return translate('lumiApp.wishlist.screen.requestFailed');
};

const displayAuthor = (wishlist: IWishlist) => {
  const author = wishlist.course?.author;
  if (!author) {
    return '';
  }

  const fullName = [author.firstName, author.lastName].filter(Boolean).join(' ');
  return fullName || author.login || '';
};

const savedAt = (wishlist: IWishlist) => (wishlist.createdAt ? dayjs(wishlist.createdAt).valueOf() : 0);

export const Wishlist = () => {
  const dispatch = useAppDispatch();
  const wishlistList = useAppSelector(state => state.wishlist.entities);
  const loading = useAppSelector(state => state.wishlist.loading);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedGrade, setSelectedGrade] = useState('');
  const [selectedSubject, setSelectedSubject] = useState('');
  const [sortBy, setSortBy] = useState<WishlistSort>('recent');
  const [loadError, setLoadError] = useState<string | null>(null);

  const loadWishlists = useCallback(async () => {
    setLoadError(null);
    try {
      await dispatch(getEntities({ sort: 'createdAt,desc' })).unwrap();
    } catch (error) {
      const message = getRequestErrorMessage(error);
      setLoadError(translate('lumiApp.wishlist.screen.loadError', { message }));
    }
  }, [dispatch]);

  useEffect(() => {
    void loadWishlists();
  }, [loadWishlists]);

  const grades = useMemo(
    () =>
      [...new Set(wishlistList.map(wishlist => wishlist.course?.grade?.name).filter((name): name is string => Boolean(name)))].sort(
        (a, b) => a.localeCompare(b, 'vi'),
      ),
    [wishlistList],
  );

  const subjects = useMemo(
    () =>
      [...new Set(wishlistList.map(wishlist => wishlist.course?.subject?.name).filter((name): name is string => Boolean(name)))].sort(
        (a, b) => a.localeCompare(b, 'vi'),
      ),
    [wishlistList],
  );

  const filteredWishlists = useMemo(() => {
    const term = searchTerm.trim().toLocaleLowerCase('vi');
    const filtered = wishlistList.filter(wishlist => {
      const course = wishlist.course;
      const searchableText = [course?.title, displayAuthor(wishlist)].filter(Boolean).join(' ').toLocaleLowerCase('vi');
      return (
        (!term || searchableText.includes(term)) &&
        (!selectedGrade || course?.grade?.name === selectedGrade) &&
        (!selectedSubject || course?.subject?.name === selectedSubject)
      );
    });

    return filtered.sort((first, second) => {
      switch (sortBy) {
        case 'oldest':
          return savedAt(first) - savedAt(second);
        case 'price-asc': {
          const firstPrice = first.course?.price;
          const secondPrice = second.course?.price;
          if (firstPrice == null) return secondPrice == null ? 0 : 1;
          if (secondPrice == null) return -1;
          return firstPrice - secondPrice;
        }
        case 'price-desc': {
          const firstPrice = first.course?.price;
          const secondPrice = second.course?.price;
          if (firstPrice == null) return secondPrice == null ? 0 : 1;
          if (secondPrice == null) return -1;
          return secondPrice - firstPrice;
        }
        case 'title':
          return (first.course?.title ?? '').localeCompare(second.course?.title ?? '', 'vi');
        default:
          return savedAt(second) - savedAt(first);
      }
    });
  }, [wishlistList, searchTerm, selectedGrade, selectedSubject, sortBy]);

  return (
    <main className="wishlist-page">
      <div className="wishlist-page__content">
        <header className="wishlist-intro">
          <nav className="wishlist-breadcrumb" aria-label={translate('lumiApp.wishlist.screen.breadcrumbLabel')}>
            <Link to="/">
              <Translate contentKey="lumiApp.wishlist.screen.home">Trang chủ</Translate>
            </Link>
            <img src={chevronRight} alt="" />
            <span aria-current="page">
              <Translate contentKey="lumiApp.wishlist.home.title">Khóa học yêu thích</Translate>
            </span>
          </nav>

          <div className="wishlist-intro__heading">
            <div>
              <h1 id="wishlist-heading" data-cy="WishlistHeading">
                <Translate contentKey="lumiApp.wishlist.home.title">Khóa học yêu thích</Translate>
              </h1>
              <p>
                <Translate contentKey="lumiApp.wishlist.screen.description">
                  Lưu lại khóa học bạn quan tâm, chọn đúng lớp và bộ sách để bắt đầu học cùng LUMI.
                </Translate>
              </p>
            </div>
            <div className="wishlist-intro__actions">
              <Link to="/course" className="wishlist-button wishlist-button--outline">
                <img src={arrowRight} alt="" />
                <Translate contentKey="lumiApp.wishlist.screen.exploreCourses">Khám phá khóa học</Translate>
              </Link>
              <Link to="/wishlist/new" className="wishlist-create-link" data-cy="entityCreateButton">
                <Translate contentKey="lumiApp.wishlist.screen.addCourse">Thêm khóa học yêu thích</Translate>
              </Link>
            </div>
          </div>

          <nav className="wishlist-tabs" aria-label={translate('lumiApp.wishlist.screen.accountNavigation')}>
            <Link to="/course-enrollment" className="wishlist-tab">
              <Translate contentKey="lumiApp.wishlist.screen.myCourses">Khóa học của tôi</Translate>
            </Link>
            <Link to="/wishlist" className="wishlist-tab wishlist-tab--active" aria-current="page">
              <Translate contentKey="lumiApp.wishlist.screen.favorites">Yêu thích</Translate>
              <span className="wishlist-tab__count">{wishlistList.length}</span>
            </Link>
            <Link to="/course-order" className="wishlist-tab">
              <Translate contentKey="lumiApp.wishlist.screen.enrollmentHistory">Lịch sử đăng ký</Translate>
            </Link>
          </nav>
        </header>

        <section className="wishlist-panel" aria-labelledby="wishlist-heading">
          <div className="wishlist-filters">
            <div className="wishlist-field wishlist-field--search">
              <label htmlFor="wishlist-search">
                <Translate contentKey="lumiApp.wishlist.screen.searchLabel">Tìm trong danh sách yêu thích</Translate>
              </label>
              <div className="wishlist-search">
                <img src={search} alt="" />
                <input
                  id="wishlist-search"
                  type="search"
                  value={searchTerm}
                  onChange={event => setSearchTerm(event.target.value)}
                  placeholder={translate('lumiApp.wishlist.screen.searchPlaceholder')}
                />
              </div>
            </div>

            <div className="wishlist-field">
              <label htmlFor="wishlist-grade">
                <Translate contentKey="lumiApp.wishlist.screen.gradeLabel">Khối lớp</Translate>
              </label>
              <div className="wishlist-select">
                <select id="wishlist-grade" value={selectedGrade} onChange={event => setSelectedGrade(event.target.value)}>
                  <option value="">
                    <Translate contentKey="lumiApp.wishlist.screen.allGrades">Tất cả lớp (1–9)</Translate>
                  </option>
                  {grades.map(grade => (
                    <option key={grade} value={grade}>
                      {grade}
                    </option>
                  ))}
                </select>
                <img src={chevronDown} alt="" />
              </div>
            </div>

            <div className="wishlist-field">
              <label htmlFor="wishlist-subject">
                <Translate contentKey="lumiApp.wishlist.screen.subjectLabel">Môn học</Translate>
              </label>
              <div className="wishlist-select">
                <select id="wishlist-subject" value={selectedSubject} onChange={event => setSelectedSubject(event.target.value)}>
                  <option value="">
                    <Translate contentKey="lumiApp.wishlist.screen.allSubjects">Tất cả môn học</Translate>
                  </option>
                  {subjects.map(subject => (
                    <option key={subject} value={subject}>
                      {subject}
                    </option>
                  ))}
                </select>
                <img src={chevronDown} alt="" />
              </div>
            </div>

            <div className="wishlist-field wishlist-field--sort">
              <label htmlFor="wishlist-sort">
                <Translate contentKey="lumiApp.wishlist.screen.sortLabel">Sắp xếp</Translate>
              </label>
              <div className="wishlist-select">
                <select id="wishlist-sort" value={sortBy} onChange={event => setSortBy(event.target.value as WishlistSort)}>
                  <option value="recent">
                    <Translate contentKey="lumiApp.wishlist.screen.sortRecent">Mới lưu gần đây</Translate>
                  </option>
                  <option value="oldest">
                    <Translate contentKey="lumiApp.wishlist.screen.sortOldest">Lưu từ lâu nhất</Translate>
                  </option>
                  <option value="price-asc">
                    <Translate contentKey="lumiApp.wishlist.screen.sortPriceAsc">Giá thấp đến cao</Translate>
                  </option>
                  <option value="price-desc">
                    <Translate contentKey="lumiApp.wishlist.screen.sortPriceDesc">Giá cao đến thấp</Translate>
                  </option>
                  <option value="title">
                    <Translate contentKey="lumiApp.wishlist.screen.sortTitle">Tên khóa học A–Z</Translate>
                  </option>
                </select>
                <img src={chevronDown} alt="" />
              </div>
            </div>
          </div>

          <div className="wishlist-panel__divider" />
          <div className="wishlist-results" aria-live="polite">
            <p>
              <Translate contentKey="lumiApp.wishlist.screen.resultCount" interpolate={{ count: filteredWishlists.length }}>
                {filteredWishlists.length} khóa học đã lưu
              </Translate>
            </p>
            <div className="wishlist-results__meta">
              <span>
                <Translate contentKey="lumiApp.wishlist.screen.priceUnit">Học phí trọn khóa • Đơn vị: VNĐ</Translate>
              </span>
              <button
                type="button"
                className="wishlist-refresh"
                onClick={() => void loadWishlists()}
                disabled={loading}
                aria-label={translate('lumiApp.wishlist.home.refreshListLabel')}
                title={translate('lumiApp.wishlist.home.refreshListLabel')}
              >
                <FontAwesomeIcon icon={faSync} spin={loading} />
              </button>
            </div>
          </div>

          {loadError && (
            <div className="wishlist-message wishlist-message--error" role="alert">
              <span>{loadError}</span>
              <button type="button" onClick={() => void loadWishlists()} disabled={loading}>
                <Translate contentKey="lumiApp.wishlist.screen.retry">Thử lại</Translate>
              </button>
            </div>
          )}

          {loading && wishlistList.length === 0 && (
            <div className="wishlist-message wishlist-message--loading" role="status" aria-live="polite">
              <span className="wishlist-spinner" aria-hidden="true" />
              <Translate contentKey="lumiApp.wishlist.screen.loading">Đang tải danh sách yêu thích…</Translate>
            </div>
          )}

          {!loading && !loadError && wishlistList.length === 0 && (
            <div className="wishlist-empty-state">
              <img src={heart} alt="" />
              <h2>
                <Translate contentKey="lumiApp.wishlist.screen.emptyTitle">Danh sách yêu thích đang trống</Translate>
              </h2>
              <p>
                <Translate contentKey="lumiApp.wishlist.screen.emptyDescription">
                  Khám phá khóa học phù hợp và lưu lại để dễ dàng tìm thấy sau này.
                </Translate>
              </p>
              <Link to="/course" className="wishlist-button wishlist-button--outline">
                <img src={arrowRight} alt="" />
                <Translate contentKey="lumiApp.wishlist.screen.exploreCourses">Khám phá khóa học</Translate>
              </Link>
            </div>
          )}

          {!loading && !loadError && wishlistList.length > 0 && filteredWishlists.length === 0 && (
            <div className="wishlist-empty-state wishlist-empty-state--filtered">
              <h2>
                <Translate contentKey="lumiApp.wishlist.screen.noMatchesTitle">Không tìm thấy khóa học phù hợp</Translate>
              </h2>
              <p>
                <Translate contentKey="lumiApp.wishlist.screen.noMatchesDescription">Hãy thử thay đổi từ khóa hoặc bộ lọc.</Translate>
              </p>
              <button
                type="button"
                className="wishlist-button wishlist-button--outline"
                onClick={() => {
                  setSearchTerm('');
                  setSelectedGrade('');
                  setSelectedSubject('');
                }}
              >
                <Translate contentKey="lumiApp.wishlist.screen.clearFilters">Xóa bộ lọc</Translate>
              </button>
            </div>
          )}

          {filteredWishlists.length > 0 && (
            <div className="wishlist-grid">
              {filteredWishlists.map(wishlist => {
                const course = wishlist.course;
                const courseTitle = course?.title || translate('lumiApp.wishlist.screen.courseUnavailable');
                const author = displayAuthor(wishlist);

                return (
                  <article className="wishlist-course-card" key={wishlist.id} data-cy="entityTable">
                    <div className="wishlist-course-card__header">
                      <div className="wishlist-course-card__classification">
                        <img src={bookOpen} alt="" />
                        <span className="wishlist-course-card__subject">
                          {course?.subject?.name || translate('lumiApp.wishlist.screen.unclassified')}
                        </span>
                        <span className="wishlist-course-card__separator">/</span>
                        <span>{course?.grade?.name || translate('lumiApp.wishlist.screen.unclassified')}</span>
                      </div>
                      <img className="wishlist-course-card__heart" src={heart} alt="" />
                      {wishlist.id !== undefined && (
                        <Link
                          to={`/wishlist/${wishlist.id}/edit`}
                          className="wishlist-course-card__edit"
                          data-cy="entityEditButton"
                          aria-label={translate('lumiApp.wishlist.screen.editFavorite', { title: courseTitle })}
                          title={translate('lumiApp.wishlist.screen.editFavorite', { title: courseTitle })}
                        >
                          <FontAwesomeIcon icon={faPencilAlt} aria-hidden="true" />
                        </Link>
                      )}
                    </div>

                    <div className="wishlist-course-card__body">
                      <h2 className="wishlist-course-card__title">
                        {course?.id !== undefined ? (
                          <Link to={`/course/${course.id}`} title={courseTitle}>
                            {courseTitle}
                          </Link>
                        ) : (
                          courseTitle
                        )}
                      </h2>

                      {course?.description && <p className="wishlist-course-card__description">{course.description}</p>}

                      {(author || course?.validDurationDays != null) && (
                        <div className="wishlist-course-card__metadata">
                          {author && (
                            <span>
                              <Translate contentKey="lumiApp.wishlist.screen.authorLabel">Người đăng</Translate>: {author}
                            </span>
                          )}
                          {course?.validDurationDays != null && (
                            <span>
                              <Translate
                                contentKey="lumiApp.wishlist.screen.accessDuration"
                                interpolate={{ days: course.validDurationDays }}
                              >
                                Truy cập {course.validDurationDays} ngày
                              </Translate>
                            </span>
                          )}
                        </div>
                      )}

                      <div className="wishlist-course-card__price-row">
                        <strong className="wishlist-course-card__price">
                          {course?.price != null
                            ? `${new Intl.NumberFormat('vi-VN').format(course.price)} ₫`
                            : translate('lumiApp.wishlist.screen.priceUnavailable')}
                        </strong>
                        {wishlist.createdAt && (
                          <span className="wishlist-course-card__saved-date">
                            <Translate contentKey="lumiApp.wishlist.screen.savedAt">Đã lưu</Translate>{' '}
                            <TextFormat type="date" value={wishlist.createdAt} format="DD/MM/YYYY" />
                          </span>
                        )}
                      </div>

                      <div className="wishlist-course-card__actions">
                        {wishlist.id !== undefined ? (
                          <Link
                            to={`/wishlist/${wishlist.id}`}
                            className="wishlist-button wishlist-button--outline"
                            data-cy="entityDetailsButton"
                            aria-label={translate('lumiApp.wishlist.screen.viewFavoriteDetails', { title: courseTitle })}
                          >
                            <Translate contentKey="lumiApp.wishlist.screen.viewDetails">Xem chi tiết</Translate>
                          </Link>
                        ) : (
                          <Link to={course?.id ? `/course/${course.id}` : '/course'} className="wishlist-button wishlist-button--outline">
                            <Translate contentKey="lumiApp.wishlist.screen.viewDetails">Xem chi tiết</Translate>
                          </Link>
                        )}
                        <button
                          type="button"
                          className="wishlist-button wishlist-button--dark"
                          disabled
                          aria-describedby="wishlist-enrollment-note"
                          title={translate('lumiApp.wishlist.screen.enrollmentUnavailable')}
                        >
                          <Translate contentKey="lumiApp.wishlist.screen.enroll">Đăng ký học</Translate>
                        </button>
                      </div>

                      {wishlist.id !== undefined && (
                        <Link
                          to={`/wishlist/${wishlist.id}/delete`}
                          className="wishlist-course-card__remove"
                          data-cy="entityDeleteButton"
                          aria-label={translate('lumiApp.wishlist.screen.removeFavorite', { title: courseTitle })}
                        >
                          <img src={trash} alt="" />
                          <Translate contentKey="lumiApp.wishlist.screen.removeFavoriteLabel">Bỏ khỏi yêu thích</Translate>
                        </Link>
                      )}
                    </div>
                  </article>
                );
              })}
            </div>
          )}

          {wishlistList.length > 0 && (
            <div className="wishlist-note" id="wishlist-enrollment-note">
              <img src={info} alt="" />
              <p>
                <Translate contentKey="lumiApp.wishlist.screen.enrollmentNote">
                  Lưu yêu thích chưa phải là đăng ký học. Xem chi tiết để kiểm tra nội dung trước khi đăng ký.
                </Translate>
              </p>
            </div>
          )}
        </section>
      </div>
    </main>
  );
};

export default Wishlist;
