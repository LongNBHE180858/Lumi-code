import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import { toast } from 'react-toastify';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { RoleSelector, UserRole } from './role-selector';
import { AccountInfoSection, AccountInfoFields } from './account-info-section';
import { RoleDetailsSection, RoleDetailFields } from './role-details-section';
import { TermsAndActions } from './terms-and-actions';
import { handleRegister, reset } from './register.reducer';

import './register.scss';

export const RegisterPage = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const loading = useAppSelector(state => state.register.loading);
  const registrationSuccess = useAppSelector(state => state.register.registrationSuccess);
  const registrationFailure = useAppSelector(state => state.register.registrationFailure);
  const errorMessage = useAppSelector(state => state.register.errorMessage);
  const registeredEmail = useAppSelector(state => state.register.registeredEmail);
  const currentLocale = useAppSelector(state => state.locale.currentLocale);

  // Form State - No mock data
  const [selectedRole, setSelectedRole] = useState<UserRole>('ROLE_STUDENT');
  const [accountInfo, setAccountInfo] = useState<AccountInfoFields>({
    username: '',
    email: '',
    phoneNumber: '',
    firstName: '',
    lastName: '',
    password: '',
    confirmPassword: '',
  });

  const [roleDetails, setRoleDetails] = useState<RoleDetailFields>({
    schoolName: '',
    gradeLevel: '',
    subjects: ['Toán học'],
    yearsOfExperience: '',
    notes: '',
  });

  const [commitAccuracy, setCommitAccuracy] = useState(false);
  const [agreeTerms, setAgreeTerms] = useState(false);
  const [validationError, setValidationError] = useState<string | null>(null);

  useEffect(() => {
    dispatch(reset());
  }, [dispatch]);

  useEffect(() => {
    if (registrationSuccess) {
      const email = registeredEmail || accountInfo.email;
      toast.success('Đăng ký tài khoản thành công! Vui lòng xác thực email để hoàn tất.');
      navigate(`/account/verify-email?email=${encodeURIComponent(email)}`);
    }
  }, [registrationSuccess, registeredEmail, accountInfo.email, navigate]);

  useEffect(() => {
    if (registrationFailure && errorMessage) {
      toast.error(errorMessage);
    }
  }, [registrationFailure, errorMessage]);

  const handleAccountChange = (field: keyof AccountInfoFields, value: string) => {
    setAccountInfo(prev => ({ ...prev, [field]: value }));
    if (validationError) setValidationError(null);
  };

  const handleRoleDetailChange = (field: keyof RoleDetailFields, value: any) => {
    setRoleDetails(prev => ({ ...prev, [field]: value }));
    if (validationError) setValidationError(null);
  };

  const handleToggleSubject = (subject: string) => {
    setRoleDetails(prev => {
      const curr = prev.subjects || [];
      const exists = curr.includes(subject);
      const updated = exists ? curr.filter(s => s !== subject) : [...curr, subject];
      return { ...prev, subjects: updated };
    });
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    // Validations
    if (!accountInfo.lastName.trim() || !accountInfo.firstName.trim()) {
      setValidationError('Vui lòng nhập đầy đủ Họ và Tên.');
      return;
    }
    if (!accountInfo.email.trim()) {
      setValidationError('Vui lòng nhập địa chỉ Email hợp lệ.');
      return;
    }
    if (!accountInfo.username.trim() || accountInfo.username.length < 3) {
      setValidationError('Tên đăng nhập cần có tối thiểu 3 ký tự.');
      return;
    }
    if (!accountInfo.password || accountInfo.password.length < 8) {
      setValidationError('Mật khẩu cần có tối thiểu 8 ký tự.');
      return;
    }
    if (accountInfo.password !== accountInfo.confirmPassword) {
      setValidationError('Mật khẩu và xác nhận mật khẩu không khớp.');
      return;
    }
    if (!roleDetails.gradeLevel) {
      const msg =
        selectedRole === 'ROLE_INSTRUCTOR'
          ? 'Vui lòng chọn khối lớp phụ trách chính.'
          : selectedRole === 'ROLE_PARENT'
            ? 'Vui lòng chọn khối lớp của con.'
            : 'Vui lòng chọn khối lớp học tập.';
      setValidationError(msg);
      return;
    }
    if (!commitAccuracy || !agreeTerms) {
      setValidationError('Vui lòng đồng ý với cam kết tính chính xác và điều khoản sử dụng của LUMI.');
      return;
    }

    setValidationError(null);

    dispatch(
      handleRegister({
        username: accountInfo.username.trim(),
        email: accountInfo.email.trim(),
        password: accountInfo.password,
        firstName: accountInfo.firstName.trim(),
        lastName: accountInfo.lastName.trim(),
        phoneNumber: accountInfo.phoneNumber.trim() || undefined,
        role: selectedRole,
        schoolName: roleDetails.schoolName.trim() || undefined,
        gradeLevel: roleDetails.gradeLevel ? Number(roleDetails.gradeLevel) : undefined,
        langKey: currentLocale || 'vi',
      }),
    );
  };

  const getHeroRoleTitle = () => {
    switch (selectedRole) {
      case 'ROLE_INSTRUCTOR':
        return 'Đăng ký tài khoản Giảng viên';
      case 'ROLE_PARENT':
        return 'Đăng ký tài khoản Phụ huynh';
      case 'ROLE_STUDENT':
      default:
        return 'Đăng ký tài khoản Học sinh';
    }
  };

  const getHeroRoleSubtitle = () => {
    switch (selectedRole) {
      case 'ROLE_INSTRUCTOR':
        return 'Tham gia giảng dạy, xây dựng ngân hàng khóa học và đồng hành cùng học sinh trên toàn quốc.';
      case 'ROLE_PARENT':
        return 'Theo dõi sát sao tiến độ học tập, kết quả kiểm tra và lộ trình phát triển của con bạn.';
      case 'ROLE_STUDENT':
      default:
        return 'Khám phá kho bài giảng thông minh, luyện thi trực tuyến và cá nhân hóa lộ trình học tập.';
    }
  };

  return (
    <div className="register-page-wrapper">
      <div className="register-page-inner">
        {/* Hero Header */}
        <div className="register-hero-header">
          <h1 className="register-title">{getHeroRoleTitle()}</h1>
          <p className="register-subtitle">{getHeroRoleSubtitle()}</p>
        </div>

        {/* Validation or API Error Notice */}
        {(validationError || errorMessage) && (
          <div
            style={{
              padding: '12px 18px',
              backgroundColor: '#fef2f2',
              color: '#b91c1c',
              border: '1px solid #fecaca',
              borderRadius: '12px',
              fontSize: '14px',
              fontWeight: 600,
              display: 'flex',
              alignItems: 'center',
              gap: '8px',
            }}
          >
            <span>⚠️</span>
            <span>{validationError || errorMessage}</span>
          </div>
        )}

        {/* Form Container */}
        <div className="register-form-container">
          {/* Part 1: Role Selector */}
          <RoleSelector selectedRole={selectedRole} onSelectRole={setSelectedRole} />

          <div className="lumi-divider" />

          {/* Part 2 & Part 3: Account info and Role-based details */}
          <div className="register-columns-grid">
            <div className="register-column">
              <AccountInfoSection fields={accountInfo} onChange={handleAccountChange} />
            </div>

            <div className="register-column">
              <RoleDetailsSection
                role={selectedRole}
                fields={roleDetails}
                onChange={handleRoleDetailChange}
                onToggleSubject={handleToggleSubject}
              />
            </div>
          </div>

          <div className="lumi-divider" />

          {/* Terms & Actions */}
          <TermsAndActions
            commitAccuracy={commitAccuracy}
            agreeTerms={agreeTerms}
            onToggleCommit={() => setCommitAccuracy(prev => !prev)}
            onToggleAgreeTerms={() => setAgreeTerms(prev => !prev)}
            onSubmit={handleSubmit}
            isLoading={loading}
          />
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;
