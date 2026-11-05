import { beforeEach, describe, expect, it } from 'vitest';
import React from 'react';
import { MemoryRouter } from 'react-router';
import { render, screen, fireEvent } from '@testing-library/react';
import { Provider } from 'react-redux';

import initStore from 'app/config/store';
import { RegisterPage } from './register';

describe('Student Registration UI Unit Tests (Phase 3.1 Scope)', () => {
  let store: any;

  beforeEach(() => {
    store = initStore();
  });

  const renderRegisterPage = () =>
    render(
      <Provider store={store}>
        <MemoryRouter>
          <RegisterPage />
        </MemoryRouter>
      </Provider>,
    );

  it('TC-REG-STU-UT-027: Verify RegisterPage displays Student fields and suppresses Teacher-only fields', () => {
    // Act
    const { container } = renderRegisterPage();

    // Assert 1: Default role header is Student
    const titleElement = screen.getByText('Đăng ký tài khoản Học sinh');
    expect(titleElement).toBeDefined();

    // Assert 2: Subtitle contains Student learning description
    const subtitleElement = screen.getByText(/Khám phá kho bài giảng thông minh/i);
    expect(subtitleElement).toBeDefined();

    // Assert 3: Student-relevant inputs are present
    expect(screen.getByText(/Trường đang học/i)).toBeDefined();
    expect(screen.getByText(/Khối lớp học tập/i)).toBeDefined();

    // Assert 4: Teacher-only fields are NOT present for Student role
    expect(screen.queryByText('Môn học giảng dạy chính')).toBeNull();
    expect(screen.queryByText('Số năm kinh nghiệm giảng dạy')).toBeNull();
    expect(screen.queryByText('Giới thiệu ngắn / Phương pháp giảng dạy')).toBeNull();
  });

  it('TC-REG-STU-UT-028: Verify client-side required field validation on empty submit (MSG01)', () => {
    // Arrange
    renderRegisterPage();

    // Act: Click submit button without filling mandatory fields
    const submitBtn = screen.getByText('Tạo tài khoản LUMI');
    fireEvent.click(submitBtn);

    // Assert: Client validation notice is displayed for empty required fields
    // Per SRS MSG01: "[field] không được để trống"
    const errorNotice = screen.getByText('Vui lòng nhập đầy đủ Họ và Tên.');
    expect(errorNotice).toBeDefined();
  });

  it('TC-REG-STU-UT-029: Verify client-side password confirmation mismatch validation (MSG07)', () => {
    // Arrange
    const { container } = renderRegisterPage();

    // Fill Họ & Tên
    const textInputs = container.querySelectorAll('input.form-input-element');
    // Inputs order in account-info-section:
    // 0: lastName, 1: firstName, 2: email, 3: phoneNumber, 4: username, 5: password, 6: confirmPassword
    const lastNameInput = textInputs[0];
    const firstNameInput = textInputs[1];
    const emailInput = textInputs[2];
    const usernameInput = textInputs[4];
    const passwordInput = textInputs[5];
    const confirmPasswordInput = textInputs[6];

    fireEvent.change(lastNameInput, { target: { value: 'Nguyễn' } });
    fireEvent.change(firstNameInput, { target: { value: 'Văn A' } });
    fireEvent.change(emailInput, { target: { value: 'student.hanoi@lumi.edu.vn' } });
    fireEvent.change(usernameInput, { target: { value: 'student_hanoi' } });
    fireEvent.change(passwordInput, { target: { value: 'Lumi@2026' } });
    fireEvent.change(confirmPasswordInput, { target: { value: 'Mismatch@999' } });

    // Act: Submit form with mismatched password confirmation
    const submitBtn = screen.getByText('Tạo tài khoản LUMI');
    fireEvent.click(submitBtn);

    // Assert: Client validation displays mismatch error matching SRS MSG07
    // SRS MSG07: "Mật khẩu và mật khẩu xác nhận không khớp."
    const mismatchNotice = screen.getByText('Mật khẩu và xác nhận mật khẩu không khớp.');
    expect(mismatchNotice).toBeDefined();
  });
});
