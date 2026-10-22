import './feedback-submit.scss';

import React, { useEffect, useRef, useState } from 'react';
import { Button, Form } from 'react-bootstrap';
import { Translate, translate } from 'react-jhipster';
import { Link, useNavigate } from 'react-router';
import { toast } from 'react-toastify';
import axios from 'axios';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { FeedbackType } from 'app/shared/model/feedback.model';
import { ICourse } from 'app/shared/model/course.model';
import { ILesson } from 'app/shared/model/lesson.model';
import { createFeedback, reset } from './feedback.reducer';

const FeedbackSectionHeading = ({ number, children }: { number: string; children: React.ReactNode }) => (
  <h2 className="feedback-section-heading">
    <span aria-hidden="true">{number}</span>
    <span className="feedback-section-heading__divider" aria-hidden="true" />
    {children}
  </h2>
);

export const FeedbackSubmit = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const account = useAppSelector(state => state.authentication.account);
  const updating = useAppSelector(state => state.feedback.updating);
  const updateSuccess = useAppSelector(state => state.feedback.updateSuccess);
  const requestError = useAppSelector(state => state.feedback.errorMessage);

  const submitLockRef = useRef(false);
  const [type, setType] = useState<FeedbackType>(FeedbackType.GENERAL);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [contactEmail, setContactEmail] = useState('');
  const [emailTouched, setEmailTouched] = useState(false);
  const [courseId, setCourseId] = useState('');
  const [lessonId, setLessonId] = useState('');
  const [confirmedEmail, setConfirmedEmail] = useState(false);

  const [courses, setCourses] = useState<ICourse[]>([]);
  const [lessons, setLessons] = useState<ILesson[]>([]);
  const [loadingCourses, setLoadingCourses] = useState(true);
  const [loadingLessons, setLoadingLessons] = useState(false);
  const [courseLoadError, setCourseLoadError] = useState(false);
  const [lessonLoadError, setLessonLoadError] = useState(false);
  const [validationErrors, setValidationErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    dispatch(reset());
    let isCurrent = true;
    setLoadingCourses(true);
    setCourseLoadError(false);

    axios
      .get<ICourse[]>('api/courses?status.equals=ACTIVE&isDeleted.equals=false&size=100&sort=title,asc')
      .then(response => {
        if (isCurrent) {
          setCourses(response.data || []);
        }
      })
      .catch(() => {
        if (isCurrent) {
          setCourses([]);
          setCourseLoadError(true);
        }
      })
      .finally(() => {
        if (isCurrent) {
          setLoadingCourses(false);
        }
      });

    return () => {
      isCurrent = false;
    };
  }, [dispatch]);

  useEffect(() => {
    if (account?.email && !emailTouched) {
      setContactEmail(account.email);
    }
  }, [account, emailTouched]);

  useEffect(() => {
    let isCurrent = true;

    if (!courseId) {
      setLessons([]);
      setLessonLoadError(false);
      setLoadingLessons(false);
      return () => {
        isCurrent = false;
      };
    }

    setLessons([]);
    setLessonLoadError(false);
    setLoadingLessons(true);
    axios
      .get<ILesson[]>(`api/courses/${courseId}/lessons`)
      .then(response => {
        if (isCurrent) {
          setLessons(response.data || []);
        }
      })
      .catch(() => {
        if (isCurrent) {
          setLessons([]);
          setLessonLoadError(true);
        }
      })
      .finally(() => {
        if (isCurrent) {
          setLoadingLessons(false);
        }
      });

    return () => {
      isCurrent = false;
    };
  }, [courseId]);

  useEffect(() => {
    if (!updating) {
      submitLockRef.current = false;
    }
  }, [updating]);

  useEffect(() => {
    if (updateSuccess) {
      toast.success(translate('lumiApp.feedback.submit.successMessage'), { autoClose: 1500 });
      const timer = setTimeout(() => {
        dispatch(reset());
        navigate('/');
      }, 1500);
      return () => clearTimeout(timer);
    }
  }, [updateSuccess, dispatch, navigate]);

  const clearValidationError = (field: string) => {
    if (validationErrors[field]) {
      setValidationErrors(previous => ({ ...previous, [field]: '' }));
    }
  };

  const handleCourseChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setCourseId(event.target.value);
    setLessonId('');
  };

  const validate = () => {
    const errors: Record<string, string> = {};
    const trimmedTitle = title.trim();
    if (!trimmedTitle) {
      errors.title = translate('lumiApp.feedback.validation.titleRequired');
    } else if (trimmedTitle.length > 255) {
      errors.title = translate('lumiApp.feedback.validation.titleMaxLength');
    }

    const trimmedContent = content.trim();
    if (!trimmedContent) {
      errors.content = translate('lumiApp.feedback.validation.contentRequired');
    } else if (trimmedContent.length < 10) {
      errors.content = translate('lumiApp.feedback.validation.contentMinLength');
    } else if (trimmedContent.length > 5000) {
      errors.content = translate('lumiApp.feedback.validation.contentMaxLength');
    }

    const emailRegex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
    const trimmedEmail = contactEmail.trim();
    if (!trimmedEmail) {
      errors.contactEmail = translate('lumiApp.feedback.validation.contactEmailRequired');
    } else if (trimmedEmail.length > 254 || !emailRegex.test(trimmedEmail)) {
      errors.contactEmail = translate('lumiApp.feedback.validation.contactEmailInvalid');
    }

    if (!confirmedEmail) {
      errors.confirmedEmail = translate('lumiApp.feedback.validation.confirmEmailRequired');
    }

    setValidationErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    if (submitLockRef.current || updating || !validate()) {
      return;
    }

    submitLockRef.current = true;
    dispatch(
      createFeedback({
        type,
        title: title.trim(),
        content: content.trim(),
        contactEmail: contactEmail.trim(),
        courseId: courseId ? Number(courseId) : null,
        lessonId: lessonId ? Number(lessonId) : null,
      }),
    );
  };

  const typeOptions: { value: FeedbackType; labelKey: string; fallback: string }[] = [
    { value: FeedbackType.GENERAL, labelKey: 'lumiApp.feedback.types.GENERAL', fallback: 'Góp ý chung' },
    { value: FeedbackType.FEATURE_REQUEST, labelKey: 'lumiApp.feedback.types.FEATURE_REQUEST', fallback: 'Đề xuất tính năng' },
    { value: FeedbackType.BUG_REPORT, labelKey: 'lumiApp.feedback.types.BUG_REPORT', fallback: 'Báo lỗi' },
    { value: FeedbackType.UI_UX, labelKey: 'lumiApp.feedback.types.UI_UX', fallback: 'Góp ý giao diện' },
  ];

  return (
    <main className="feedback-submit-page">
      <div className="feedback-submit-page__content">
        <header className="feedback-intro">
          <nav className="feedback-breadcrumb" aria-label={translate('lumiApp.feedback.submit.breadcrumbLabel')}>
            <Link to="/">
              <Translate contentKey="lumiApp.feedback.submit.home">Trang chủ</Translate>
            </Link>
            <img src="/content/images/feedback/chevron-right.svg" alt="" />
            <span>
              <Translate contentKey="lumiApp.feedback.submit.support">Hỗ trợ</Translate>
            </span>
            <img src="/content/images/feedback/chevron-right.svg" alt="" />
            <span aria-current="page">
              <Translate contentKey="lumiApp.feedback.submit.title">Gửi phản hồi</Translate>
            </span>
          </nav>

          <div className="feedback-intro__heading">
            <div>
              <h1 className="feedback-header__title">
                <Translate contentKey="lumiApp.feedback.submit.title">Gửi phản hồi</Translate>
              </h1>
              <p>
                <Translate contentKey="lumiApp.feedback.submit.subtitle">
                  Chia sẻ góp ý hoặc vấn đề bạn gặp khi học cùng LUMI để chúng tôi cải thiện trải nghiệm học tập.
                </Translate>
              </p>
            </div>
            <button type="button" className="feedback-back" onClick={() => navigate(-1)}>
              <img src="/content/images/feedback/arrow-left.svg" alt="" />
              <Translate contentKey="lumiApp.feedback.submit.back">Quay lại</Translate>
            </button>
          </div>
        </header>

        <Form onSubmit={handleSubmit} id="feedback-submit-form" className="feedback-form" noValidate>
          <section className="feedback-form__type" aria-labelledby="feedback-type-heading">
            <div className="feedback-section-caption">
              <FeedbackSectionHeading number="01">
                <span id="feedback-type-heading">
                  <Translate contentKey="lumiApp.feedback.type">Loại phản hồi</Translate>
                  <span aria-hidden="true"> *</span>
                </span>
              </FeedbackSectionHeading>
              <span className="feedback-required-note">
                <Translate contentKey="lumiApp.feedback.submit.requiredFields">Các trường có dấu * là bắt buộc</Translate>
              </span>
            </div>
            <fieldset className="feedback-type-options">
              <legend className="visually-hidden">
                <Translate contentKey="lumiApp.feedback.type">Loại phản hồi</Translate>
              </legend>
              {typeOptions.map(option => (
                <label
                  className={`feedback-type-option${type === option.value ? ' feedback-type-option--selected' : ''}`}
                  key={option.value}
                  data-cy={`feedback-type-${option.value}`}
                >
                  <input
                    type="radio"
                    name="feedback-type"
                    value={option.value}
                    checked={type === option.value}
                    onChange={() => setType(option.value)}
                  />
                  <span className="feedback-type-option__radio" aria-hidden="true" />
                  <span className="feedback-type-option__label">
                    <Translate contentKey={option.labelKey}>{option.fallback}</Translate>
                  </span>
                </label>
              ))}
            </fieldset>
          </section>

          <div className="feedback-divider" />

          <div className="feedback-form__body">
            <section className="feedback-details" aria-labelledby="feedback-details-heading">
              <FeedbackSectionHeading number="02">
                <span id="feedback-details-heading">
                  <Translate contentKey="lumiApp.feedback.submit.detailsHeading">Chi tiết phản hồi</Translate>
                </span>
              </FeedbackSectionHeading>

              <div className="feedback-course-row">
                <div className="feedback-field">
                  <Form.Label htmlFor="feedback-course">
                    <Translate contentKey="lumiApp.feedback.course">Khóa học</Translate>
                    <span className="feedback-optional">
                      {' '}
                      (<Translate contentKey="lumiApp.feedback.submit.optional">tùy chọn</Translate>)
                    </span>
                  </Form.Label>
                  <div className="feedback-select">
                    <Form.Select
                      id="feedback-course"
                      data-cy="feedback-course"
                      value={courseId}
                      onChange={handleCourseChange}
                      disabled={loadingCourses}
                    >
                      <option value="">
                        {loadingCourses
                          ? translate('lumiApp.feedback.submit.loadingCourses')
                          : translate('lumiApp.feedback.submit.courseSelectPlaceholder')}
                      </option>
                      {courses.map(course => (
                        <option key={course.id} value={course.id}>
                          {course.title}
                        </option>
                      ))}
                    </Form.Select>
                    <img src="/content/images/feedback/chevron-down.svg" alt="" />
                  </div>
                  {courseLoadError && (
                    <p className="feedback-field__error" role="alert">
                      <Translate contentKey="lumiApp.feedback.submit.coursesLoadError">
                        Không thể tải danh sách khóa học. Vui lòng tải lại trang để thử lại.
                      </Translate>
                    </p>
                  )}
                </div>
                <div className="feedback-field">
                  <Form.Label htmlFor="feedback-lesson">
                    <Translate contentKey="lumiApp.feedback.lesson">Bài học</Translate>
                    <span className="feedback-optional">
                      {' '}
                      (<Translate contentKey="lumiApp.feedback.submit.optional">tùy chọn</Translate>)
                    </span>
                  </Form.Label>
                  <div className="feedback-select">
                    <Form.Select
                      id="feedback-lesson"
                      data-cy="feedback-lesson"
                      value={lessonId}
                      onChange={event => setLessonId(event.target.value)}
                      disabled={!courseId || loadingLessons}
                    >
                      <option value="">
                        {!courseId
                          ? translate('lumiApp.feedback.submit.lessonSelectPrompt')
                          : loadingLessons
                            ? translate('lumiApp.feedback.submit.loadingLessons')
                            : translate('lumiApp.feedback.submit.lessonSelectPlaceholder')}
                      </option>
                      {lessons.map(lesson => (
                        <option key={lesson.id} value={lesson.id}>
                          {lesson.title}
                        </option>
                      ))}
                    </Form.Select>
                    <img src="/content/images/feedback/chevron-down.svg" alt="" />
                  </div>
                  {lessonLoadError && (
                    <p className="feedback-field__error" role="alert">
                      <Translate contentKey="lumiApp.feedback.submit.lessonsLoadError">
                        Không thể tải danh sách bài học. Vui lòng chọn lại khóa học để thử lại.
                      </Translate>
                    </p>
                  )}
                </div>
              </div>
              <p className="feedback-field__hint">
                <Translate contentKey="lumiApp.feedback.submit.courseLessonHint">
                  Có thể bỏ qua nếu phản hồi không liên quan đến một khóa học hoặc bài học.
                </Translate>
              </p>

              <div className="feedback-field">
                <Form.Label htmlFor="feedback-title">
                  <Translate contentKey="lumiApp.feedback.title">Tiêu đề</Translate>
                  <span aria-hidden="true"> *</span>
                </Form.Label>
                <Form.Control
                  id="feedback-title"
                  data-cy="feedback-title"
                  type="text"
                  maxLength={255}
                  value={title}
                  onChange={event => {
                    setTitle(event.target.value);
                    clearValidationError('title');
                  }}
                  placeholder={translate('lumiApp.feedback.submit.titlePlaceholder')}
                  isInvalid={!!validationErrors.title}
                  aria-required="true"
                  aria-invalid={!!validationErrors.title}
                  aria-describedby={validationErrors.title ? 'feedback-title-error' : undefined}
                />
                {validationErrors.title && (
                  <div className="feedback-field__error" id="feedback-title-error" role="alert">
                    {validationErrors.title}
                  </div>
                )}
              </div>

              <div className="feedback-field">
                <Form.Label htmlFor="feedback-content">
                  <Translate contentKey="lumiApp.feedback.content">Nội dung phản hồi</Translate>
                  <span aria-hidden="true"> *</span>
                </Form.Label>
                <Form.Control
                  id="feedback-content"
                  data-cy="feedback-content"
                  as="textarea"
                  rows={5}
                  maxLength={5000}
                  value={content}
                  onChange={event => {
                    setContent(event.target.value);
                    clearValidationError('content');
                  }}
                  placeholder={translate('lumiApp.feedback.submit.contentPlaceholder')}
                  isInvalid={!!validationErrors.content}
                  aria-required="true"
                  aria-invalid={!!validationErrors.content}
                  aria-describedby={['feedback-content-hint', validationErrors.content ? 'feedback-content-error' : '']
                    .filter(Boolean)
                    .join(' ')}
                />
                {validationErrors.content && (
                  <div className="feedback-field__error" id="feedback-content-error" role="alert">
                    {validationErrors.content}
                  </div>
                )}
                <div className="feedback-content-meta">
                  <p id="feedback-content-hint">
                    <Translate contentKey="lumiApp.feedback.submit.contentHint">Viết rõ và cụ thể để LUMI hiểu phản hồi của bạn.</Translate>
                  </p>
                  <span aria-live="polite">{content.length}/5000</span>
                </div>
              </div>
            </section>

            <aside className="feedback-contact" aria-labelledby="feedback-contact-heading">
              <FeedbackSectionHeading number="03">
                <span id="feedback-contact-heading">
                  <Translate contentKey="lumiApp.feedback.submit.contactHeading">Email liên hệ</Translate>
                </span>
              </FeedbackSectionHeading>

              <div className="feedback-field">
                <Form.Label htmlFor="feedback-contact-email">
                  <Translate contentKey="lumiApp.feedback.contactEmail">Email</Translate>
                  <span aria-hidden="true"> *</span>
                </Form.Label>
                <Form.Control
                  id="feedback-contact-email"
                  data-cy="feedback-contact-email"
                  type="email"
                  maxLength={254}
                  autoComplete="email"
                  value={contactEmail}
                  onChange={event => {
                    setEmailTouched(true);
                    setContactEmail(event.target.value);
                    clearValidationError('contactEmail');
                  }}
                  placeholder={translate('lumiApp.feedback.submit.emailPlaceholder')}
                  isInvalid={!!validationErrors.contactEmail}
                  aria-required="true"
                  aria-invalid={!!validationErrors.contactEmail}
                  aria-describedby={validationErrors.contactEmail ? 'feedback-email-error' : 'feedback-email-hint'}
                />
                {validationErrors.contactEmail && (
                  <div className="feedback-field__error" id="feedback-email-error" role="alert">
                    {validationErrors.contactEmail}
                  </div>
                )}
                <p className="feedback-field__hint" id="feedback-email-hint">
                  <Translate contentKey="lumiApp.feedback.submit.emailHint">
                    Bạn có thể đổi email để nhận trao đổi về phản hồi này.
                  </Translate>
                </p>
              </div>

              <div className="feedback-confirmation">
                <Form.Check
                  type="checkbox"
                  id="feedback-confirm-email"
                  data-cy="feedback-confirm-email"
                  checked={confirmedEmail}
                  onChange={event => {
                    setConfirmedEmail(event.target.checked);
                    clearValidationError('confirmedEmail');
                  }}
                  label={translate('lumiApp.feedback.submit.confirmEmailCheckbox')}
                  isInvalid={!!validationErrors.confirmedEmail}
                  aria-invalid={!!validationErrors.confirmedEmail}
                  aria-describedby={validationErrors.confirmedEmail ? 'feedback-confirm-email-error' : undefined}
                />
                {validationErrors.confirmedEmail && (
                  <div className="feedback-field__error" id="feedback-confirm-email-error" role="alert">
                    {validationErrors.confirmedEmail}
                  </div>
                )}
              </div>

              <div className="feedback-security-note">
                <img src="/content/images/feedback/info.svg" alt="" />
                <p>
                  <Translate contentKey="lumiApp.feedback.submit.securityNote">
                    Không chia sẻ mật khẩu, thông tin thanh toán hoặc thông tin nhạy cảm trong phản hồi.
                  </Translate>
                </p>
              </div>
            </aside>
          </div>

          {requestError && (
            <div className="feedback-submit-error" role="alert" data-cy="feedback-submit-error">
              <Translate contentKey="lumiApp.feedback.submit.requestFailed">
                Không thể gửi phản hồi. Vui lòng thử lại; nội dung của bạn vẫn được giữ nguyên.
              </Translate>
            </div>
          )}

          <div className="feedback-divider" />

          <div className="feedback-form__actions">
            <p>
              <Translate contentKey="lumiApp.feedback.submit.reviewBeforeSubmit">
                Kiểm tra nội dung và email liên hệ trước khi gửi.
              </Translate>
            </p>
            <div className="feedback-form__action-buttons">
              <Button type="button" className="feedback-cancel-button" onClick={() => navigate(-1)} disabled={updating}>
                <Translate contentKey="lumiApp.feedback.submit.cancel">Hủy</Translate>
              </Button>
              <Button type="submit" className="btn-submit-feedback" data-cy="feedback-submit-btn" disabled={updating}>
                {updating ? (
                  <span role="status" aria-live="polite">
                    <Translate contentKey="lumiApp.feedback.submit.submitting">Đang gửi...</Translate>
                  </span>
                ) : (
                  <Translate contentKey="lumiApp.feedback.submit.submitButton">Gửi phản hồi</Translate>
                )}
              </Button>
            </div>
          </div>
        </Form>
      </div>
    </main>
  );
};

export default FeedbackSubmit;
