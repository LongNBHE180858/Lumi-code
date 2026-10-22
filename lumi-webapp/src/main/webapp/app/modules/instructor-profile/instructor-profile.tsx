import React, { useState } from 'react';
import { Button } from 'react-bootstrap';
import { Link, useParams } from 'react-router';

import avatar from '/content/images/instructor-avatar.svg';
import separator from '/content/images/instructor-breadcrumb.svg';
import { useInstructorCourses, useInstructorProfile } from './use-instructor-profile';
import type { InstructorPublicCourse } from './instructor-profile.model';

import './instructor-profile.scss';

const currency = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' });

const InstructorAvatar = ({ imageUrl, name }: { imageUrl: string | null; name: string }) => {
  const [failed, setFailed] = useState(false);
  const hasImage = Boolean(imageUrl?.trim()) && !failed;
  return (
    <div className="instructor-avatar">
      {hasImage ? (
        <img className="instructor-avatar-photo" src={imageUrl!} alt={`Ảnh đại diện ${name}`} onError={() => setFailed(true)} />
      ) : (
        <img src={avatar} alt="Ảnh đại diện mặc định" width={44} height={44} />
      )}
    </div>
  );
};

const CourseCard = ({ course }: { course: InstructorPublicCourse }) => {
  const subject = course.subjectName?.trim();
  const grade = course.gradeLevel;
  const price = course.price;
  return (
    <article className="instructor-course">
      {(subject || grade != null) && (
        <div className="instructor-course-labels">
          {subject && <span>{subject}</span>}
          {subject && grade != null && <span aria-hidden="true">/</span>}
          {grade != null && <span>Khối {grade}</span>}
        </div>
      )}
      <div className="instructor-course-content">
        <h3>{course.title}</h3>
        {course.description?.trim() && <p className="instructor-course-description">{course.description}</p>}
        <div className="instructor-course-price">
          <strong>{price == null ? 'Chưa cập nhật giá' : price === 0 ? 'Miễn phí' : currency.format(price)}</strong>
          {course.validDurationDays != null && <span>Truy cập trong {course.validDurationDays} ngày</span>}
        </div>
        <Link className="btn btn-primary instructor-button" to={`/courses/${course.id}`}>
          Xem chi tiết
        </Link>
      </div>
    </article>
  );
};

const InstructorProfile = () => {
  const { id: routeId } = useParams<{ id: string }>();
  const parsedId = Number(routeId);
  const id = routeId && /^\d+$/.test(routeId) && Number.isSafeInteger(parsedId) && parsedId > 0 ? parsedId : null;
  const profileRequest = useInstructorProfile(id);
  const coursesRequest = useInstructorCourses(id);
  const profile = profileRequest.data;
  const scopes = (profile?.teachingScopes ?? []).filter(
    (scope, index, all) =>
      scope.subjectName?.trim() &&
      scope.gradeLevel != null &&
      scope.gradeLevel >= 1 &&
      scope.gradeLevel <= 9 &&
      all.findIndex(item => item.subjectId === scope.subjectId && item.gradeLevel === scope.gradeLevel) === index,
  );

  return (
    <main className="lumi-instructor-profile">
      <div className="instructor-content">
        <nav className="instructor-breadcrumb" aria-label="Đường dẫn">
          <Link to="/">Trang chủ</Link>
          <img src={separator} alt="" width={12} height={12} />
          <span aria-current="page">Hồ sơ giảng viên</span>
        </nav>
        <h1>Hồ sơ giảng viên</h1>

        {profileRequest.loading && <p role="status">Đang tải hồ sơ giảng viên...</p>}
        {(id === null || profileRequest.notFound) && <p role="alert">Không tìm thấy hồ sơ giảng viên.</p>}
        {profileRequest.error && !profileRequest.notFound && (
          <div className="instructor-feedback" role="alert">
            <p>{profileRequest.error}</p>
            <Button variant="outline-primary" onClick={profileRequest.reload}>
              Thử lại
            </Button>
          </div>
        )}

        {profile && (
          <>
            <section className="instructor-summary" aria-labelledby="instructor-name">
              <InstructorAvatar key={profile.imageUrl} imageUrl={profile.imageUrl} name={profile.fullName || 'giảng viên'} />
              <div className="instructor-introduction">
                <h2 id="instructor-name">{profile.fullName?.trim() || 'Giảng viên'}</h2>
                <h3>Giới thiệu giảng viên</h3>
                <p className="instructor-bio">{profile.bio?.trim() ? profile.bio : 'Giảng viên chưa cập nhật giới thiệu'}</p>
                {scopes.length > 0 && (
                  <div className="instructor-scopes">
                    <h3>Môn và khối có khóa học</h3>
                    <div className="instructor-scope-list">
                      {scopes.map(scope => (
                        <span key={`${scope.subjectId}-${scope.gradeLevel}`}>
                          {scope.subjectName} · Khối {scope.gradeLevel}
                        </span>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            </section>

            <section className="instructor-courses" aria-labelledby="instructor-courses-heading">
              <div className="instructor-courses-heading">
                <h2 id="instructor-courses-heading">Khóa học của giảng viên</h2>
                {coursesRequest.data && <span>{coursesRequest.data.length} khóa học</span>}
              </div>
              {coursesRequest.loading && <p role="status">Đang tải khóa học...</p>}
              {coursesRequest.error && (
                <div className="instructor-feedback" role="alert">
                  <p>{coursesRequest.notFound ? 'Danh sách khóa học hiện không khả dụng.' : coursesRequest.error}</p>
                  <Button variant="outline-primary" onClick={coursesRequest.reload}>
                    Thử lại
                  </Button>
                </div>
              )}
              {coursesRequest.data?.length === 0 && <p>Giảng viên chưa có khóa học công khai.</p>}
              {coursesRequest.data && coursesRequest.data.length > 0 && (
                <>
                  <div className="instructor-course-grid">
                    {coursesRequest.data.map(course => (
                      <CourseCard key={course.id} course={course} />
                    ))}
                  </div>
                </>
              )}
            </section>
          </>
        )}
      </div>
    </main>
  );
};

export default InstructorProfile;
