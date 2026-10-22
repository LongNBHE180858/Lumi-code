import React, { useEffect, useRef, useState } from 'react';
import { Button, Form } from 'react-bootstrap';
import { useForm } from 'react-hook-form';
import { Link, useNavigate } from 'react-router';
import { toast } from 'react-toastify';
import axios from 'axios';

import avatar from '/content/images/profile-avatar.svg';
import { useAppSelector } from 'app/config/store';
import { Authority } from 'app/shared/jhipster/constants';
import SettingsLayout from 'app/modules/account/settings-layout/settings-layout';
import { updateMyProfile } from './profile.api';
import type { AccountProfile, ProfileUpdate } from './profile.model';
import { useProfile } from './use-profile';

import './profile.scss';
import './profile-edit.scss';

interface ProfileFormValues {
  firstName: string;
  lastName: string;
  phoneNumber: string;
  schoolName: string;
  gradeLevel: string;
  bio: string;
}

const ProfileEditForm = ({ profile, isStudent, roleLabel }: { profile: AccountProfile; isStudent: boolean; roleLabel: string }) => {
  const navigate = useNavigate();
  const requestRef = useRef<AbortController | null>(null);
  const [saving, setSaving] = useState(false);
  const [saveError, setSaveError] = useState<string | null>(null);
  const {
    register,
    handleSubmit,
    setError,
    clearErrors,
    formState: { errors },
  } = useForm<ProfileFormValues>({
    mode: 'onTouched',
    defaultValues: {
      firstName: profile.firstName ?? '',
      lastName: profile.lastName ?? '',
      phoneNumber: profile.phoneNumber ?? '',
      schoolName: profile.schoolName ?? '',
      gradeLevel: profile.gradeLevel == null ? '' : String(profile.gradeLevel),
      bio: profile.bio ?? '',
    },
  });

  useEffect(() => () => requestRef.current?.abort(), []);

  const submit = async (values: ProfileFormValues) => {
    if (requestRef.current) return;
    const controller = new AbortController();
    requestRef.current = controller;
    setSaving(true);
    setSaveError(null);
    clearErrors('phoneNumber');

    // PUT ghi đè các trường hồ sơ: luôn gửi đủ dữ liệu và giữ URL ảnh hiện tại.
    const payload: ProfileUpdate = {
      firstName: values.firstName.trim(),
      lastName: values.lastName.trim(),
      phoneNumber: values.phoneNumber.replace(/[\s-]/g, '') || null,
      schoolName: isStudent ? values.schoolName.trim() || null : profile.schoolName,
      gradeLevel: isStudent ? (values.gradeLevel === '' ? null : Number(values.gradeLevel)) : profile.gradeLevel,
      bio: values.bio.trim() || null,
      imageUrl: profile.imageUrl,
    };

    try {
      const updated = await updateMyProfile(payload, controller.signal);
      if (controller.signal.aborted) return;
      if (updated.id !== profile.id) throw new Error('Hồ sơ không khớp tài khoản.');
      toast.success('Cập nhật hồ sơ thành công.');
      navigate('/account/settings', { replace: true });
    } catch (error: unknown) {
      const responseData = axios.isAxiosError<{ message?: string; errorKey?: string }>(error) ? error.response?.data : undefined;
      const msg = responseData?.message || '';
      const key = responseData?.errorKey || msg;
      if (key === 'error.phonealreadyused' || key === 'phonealreadyused' || msg.includes('đã được sử dụng')) {
        setError('phoneNumber', {
          type: 'server',
          message: 'Số điện thoại đã được sử dụng',
        });
      } else if (key === 'error.phoneinvalid' || key === 'phoneinvalid' || msg.includes('10 chữ số')) {
        setError('phoneNumber', {
          type: 'server',
          message: 'Số điện thoại phải có 10 chữ số, bắt đầu bằng 03, 05, 07, 08 hoặc 09.',
        });
      } else {
        setSaveError(msg || 'Không lưu được hồ sơ. Vui lòng kiểm tra thông tin và thử lại.');
      }
    } finally {
      if (!controller.signal.aborted) {
        requestRef.current = null;
        setSaving(false);
      }
    }
  };

  const fullName = profile.fullName?.trim() || [profile.firstName, profile.lastName].filter(Boolean).join(' ') || profile.login;
  const invalidGrade =
    profile.gradeLevel != null && (!Number.isInteger(profile.gradeLevel) || profile.gradeLevel < 1 || profile.gradeLevel > 9);

  const field = (name: Exclude<keyof ProfileFormValues, 'gradeLevel'>, label: string, maxLength: number, required = false) => (
    <Form.Group controlId={`profile-${name}`} className={name === 'bio' ? 'profile-edit-field profile-edit-wide' : 'profile-edit-field'}>
      <Form.Label>
        {label}
        {required && (
          <span className="profile-required" aria-hidden="true">
            {' '}
            *
          </span>
        )}
      </Form.Label>
      <Form.Control
        as={name === 'bio' ? 'textarea' : 'input'}
        type={name === 'phoneNumber' ? 'tel' : 'text'}
        autoComplete={{ firstName: 'family-name', lastName: 'given-name', phoneNumber: 'tel', schoolName: 'off', bio: 'off' }[name]}
        maxLength={maxLength}
        required={required}
        aria-required={required}
        isInvalid={Boolean(errors[name])}
        aria-invalid={Boolean(errors[name])}
        aria-describedby={`profile-${name}-feedback`}
        {...register(name, {
          maxLength: { value: maxLength, message: `Tối đa ${maxLength} ký tự.` },
          validate(value) {
            if (required && !value.trim()) return `Vui lòng nhập ${label.toLowerCase()}.`;
            if (name === 'phoneNumber' && value.replace(/[\s-]/g, '') !== '' && !/^0[35789]\d{8}$/.test(value.replace(/[\s-]/g, ''))) {
              return 'Số điện thoại phải có 10 chữ số, bắt đầu bằng 03, 05, 07, 08 hoặc 09.';
            }
            return true;
          },
        })}
      />
      <div id={`profile-${name}-feedback`}>
        {errors[name] ? (
          <p className="profile-field-error" role="alert">
            {errors[name].message}
          </p>
        ) : null}
      </div>
    </Form.Group>
  );

  return (
    <Form className="profile-panel profile-edit-panel" onSubmit={handleSubmit(submit)} noValidate aria-busy={saving}>
      <div className="profile-edit-identity">
        <img
          className="profile-avatar-image"
          src={profile.imageUrl?.trim() || avatar}
          alt="Ảnh đại diện"
          width={80}
          height={80}
          onError={event => {
            if (event.currentTarget.getAttribute('src') !== avatar) event.currentTarget.setAttribute('src', avatar);
          }}
        />
        <div>
          <h2>{fullName}</h2>
          <p>{roleLabel}</p>
        </div>
        {/* Bật lại chức năng đổi ảnh sau khi nhóm tích hợp dịch vụ lưu ảnh cloud. */}
      </div>
      <h2 className="profile-edit-section-title">Thông tin tài khoản</h2>
      {saveError && (
        <p className="profile-field-error" role="alert">
          {saveError}
        </p>
      )}
      <fieldset disabled={saving}>
        <legend className="visually-hidden">Chỉnh sửa thông tin cá nhân</legend>
        <div className="profile-edit-grid">
          {field('firstName', 'Họ', 50, true)}
          {field('lastName', 'Tên đệm và tên', 50, true)}
          <Form.Group controlId="profile-email" className="profile-edit-field">
            <Form.Label>Email đăng nhập</Form.Label>
            <Form.Control value={profile.email ?? ''} readOnly aria-describedby="profile-email-help" />
            <Form.Text id="profile-email-help">Email đăng nhập không thể chỉnh sửa tại đây.</Form.Text>
          </Form.Group>
          {field('phoneNumber', 'Số điện thoại', 20)}
          {isStudent && (
            <>
              {field('schoolName', 'Trường học', 255)}
              <Form.Group controlId="profile-gradeLevel" className="profile-edit-field">
                <Form.Label>Khối lớp</Form.Label>
                <Form.Select
                  {...register('gradeLevel', {
                    validate: value => value === '' || /^[1-9]$/.test(value) || 'Vui lòng chọn khối lớp từ 1 đến 9.',
                  })}
                  isInvalid={Boolean(errors.gradeLevel)}
                  aria-invalid={Boolean(errors.gradeLevel)}
                  aria-describedby="profile-grade-feedback"
                >
                  <option value="">Chưa chọn khối lớp</option>
                  {invalidGrade && (
                    <option value={String(profile.gradeLevel)} disabled>
                      Khối hiện tại không thuộc phạm vi 1–9
                    </option>
                  )}
                  {Array.from({ length: 9 }, (_, index) => index + 1).map(grade => (
                    <option value={grade} key={grade}>
                      Khối {grade}
                    </option>
                  ))}
                </Form.Select>
                <div id="profile-grade-feedback">
                  {errors.gradeLevel ? (
                    <p className="profile-field-error" role="alert">
                      {errors.gradeLevel.message}
                    </p>
                  ) : null}
                </div>
              </Form.Group>
            </>
          )}
          {field('bio', 'Giới thiệu bản thân', 5000)}
        </div>
        <div className="profile-actions">
          <Button type="submit" className="profile-button" disabled={saving}>
            {saving ? 'Đang lưu...' : 'Lưu thay đổi'}
          </Button>
          <Button
            type="button"
            onClick={() => navigate('/account/settings')}
            variant="outline-primary"
            className="profile-button profile-button-secondary"
            disabled={saving}
          >
            Hủy
          </Button>
        </div>
      </fieldset>
    </Form>
  );
};

const ProfileEdit = () => {
  const authorities: string[] = useAppSelector(state => state.authentication.account.authorities ?? []);
  const isInstructor = authorities.includes(Authority.INSTRUCTOR) || authorities.includes('ROLE_TEACHER');
  const isParent = authorities.includes(Authority.PARENT);
  const isStudent = !isInstructor && !isParent;
  const allowed = true;
  const { data: profile, loading, error, reload } = useProfile(allowed);

  return (
    <SettingsLayout
      activeTab="profile"
      pageTitle="Cài đặt tài khoản"
      pageSubtitle="Quản lý thông tin cá nhân và bảo mật tài khoản học tập của bạn trên LUMI."
      currentBreadcrumb="Chỉnh sửa hồ sơ"
    >
      <div className="profile-layout-container">
        <div className="profile-header-card">
          <div className="profile-header-info">
            <h2 className="profile-header-title">Cập nhật hồ sơ</h2>
            <p className="profile-header-desc">Cập nhật thông tin cá nhân để hồ sơ học tập của bạn luôn chính xác.</p>
          </div>
          <div className="profile-actions">
            <Link to="/account/settings" className="profile-back-btn">
              <svg
                width="16"
                height="16"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2.2"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <polyline points="15 18 9 12 15 6" />
              </svg>
              <span>Quay lại hồ sơ</span>
            </Link>
          </div>
        </div>
        {!allowed && (
          <p role="alert" className="profile-status-message">
            Tài khoản chưa có giao diện hồ sơ phù hợp.
          </p>
        )}
        {loading && (
          <p role="status" className="profile-status-message">
            Đang tải dữ liệu...
          </p>
        )}
        {error && (
          <div className="profile-request-error" role="alert">
            <span>{error}</span>
            <Button onClick={reload} variant="outline-primary" size="sm">
              Thử lại
            </Button>
          </div>
        )}
        {profile && (
          <div className="profile-edit-card">
            <ProfileEditForm
              key={profile.id}
              profile={profile}
              isStudent={isStudent}
              roleLabel={isStudent ? 'Học viên LUMI' : isInstructor ? 'Giáo viên LUMI' : 'Phụ huynh LUMI'}
            />
          </div>
        )}
      </div>
    </SettingsLayout>
  );
};

export default ProfileEdit;
