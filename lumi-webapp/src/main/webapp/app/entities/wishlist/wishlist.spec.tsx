import React from 'react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import { configureStore } from '@reduxjs/toolkit';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import axios from 'axios';
import { beforeAll, beforeEach, describe, expect, it, vi } from 'vitest';
import { TranslatorContext } from 'react-jhipster';

import { IWishlist } from 'app/shared/model/wishlist.model';

import wishlistReducer from './wishlist.reducer';
import Wishlist from './wishlist';

const createWishlists = (): IWishlist[] => [
  {
    id: 1,
    course: {
      id: 11,
      title: 'Toán lớp 6',
      description: 'Kiến thức nền tảng môn Toán.',
      price: 499000,
      validDurationDays: 365,
      subject: { name: 'Toán' },
      grade: { name: 'Lớp 6' },
      author: { id: 3, firstName: 'Minh', lastName: 'Anh', login: 'minhanh' },
    },
  },
  {
    id: 2,
    course: {
      id: 12,
      title: 'Ngữ văn lớp 7',
      price: null,
      subject: { name: 'Ngữ văn' },
      grade: { name: 'Lớp 7' },
    },
  },
];

const renderWishlist = () => {
  const store = configureStore({
    reducer: {
      wishlist: wishlistReducer,
    },
  });

  return render(
    <Provider store={store}>
      <MemoryRouter>
        <Wishlist />
      </MemoryRouter>
    </Provider>,
  );
};

describe('Wishlist screen', () => {
  beforeAll(() => {
    TranslatorContext.setDefaultLocale('vi');
    TranslatorContext.setLocale('vi');
    TranslatorContext.registerTranslations('vi', {
      'lumiApp.wishlist.home.title': 'Khóa học yêu thích',
      'lumiApp.wishlist.screen.home': 'Trang chủ',
      'lumiApp.wishlist.screen.breadcrumbLabel': 'Đường dẫn',
      'lumiApp.wishlist.screen.description': 'Mô tả',
      'lumiApp.wishlist.screen.exploreCourses': 'Khám phá khóa học',
      'lumiApp.wishlist.screen.addCourse': 'Thêm khóa học yêu thích',
      'lumiApp.wishlist.screen.accountNavigation': 'Điều hướng học tập',
      'lumiApp.wishlist.screen.myCourses': 'Khóa học của tôi',
      'lumiApp.wishlist.screen.favorites': 'Yêu thích',
      'lumiApp.wishlist.screen.enrollmentHistory': 'Lịch sử đăng ký',
      'lumiApp.wishlist.screen.searchLabel': 'Tìm trong danh sách yêu thích',
      'lumiApp.wishlist.screen.searchPlaceholder': 'Nhập tên khóa học hoặc giảng viên',
      'lumiApp.wishlist.screen.gradeLabel': 'Khối lớp',
      'lumiApp.wishlist.screen.allGrades': 'Tất cả lớp (1–9)',
      'lumiApp.wishlist.screen.subjectLabel': 'Môn học',
      'lumiApp.wishlist.screen.allSubjects': 'Tất cả môn học',
      'lumiApp.wishlist.screen.sortLabel': 'Sắp xếp',
      'lumiApp.wishlist.screen.sortRecent': 'Mới lưu gần đây',
      'lumiApp.wishlist.screen.sortOldest': 'Lưu từ lâu nhất',
      'lumiApp.wishlist.screen.sortPriceAsc': 'Giá thấp đến cao',
      'lumiApp.wishlist.screen.sortPriceDesc': 'Giá cao đến thấp',
      'lumiApp.wishlist.screen.sortTitle': 'Tên khóa học A–Z',
      'lumiApp.wishlist.screen.resultCount': '{{count}} khóa học đã lưu',
      'lumiApp.wishlist.screen.priceUnit': 'Học phí trọn khóa • Đơn vị: VNĐ',
      'lumiApp.wishlist.screen.requestFailed': 'Đã xảy ra lỗi khi tải dữ liệu.',
      'lumiApp.wishlist.screen.loadError': 'Không thể tải danh sách yêu thích: {{message}}',
      'lumiApp.wishlist.screen.retry': 'Thử lại',
      'lumiApp.wishlist.screen.loading': 'Đang tải danh sách yêu thích…',
      'lumiApp.wishlist.screen.emptyTitle': 'Danh sách yêu thích đang trống',
      'lumiApp.wishlist.screen.emptyDescription': 'Khám phá khóa học phù hợp và lưu lại để dễ dàng tìm thấy sau này.',
      'lumiApp.wishlist.screen.noMatchesTitle': 'Không tìm thấy khóa học phù hợp',
      'lumiApp.wishlist.screen.noMatchesDescription': 'Hãy thử thay đổi từ khóa hoặc bộ lọc.',
      'lumiApp.wishlist.screen.clearFilters': 'Xóa bộ lọc',
      'lumiApp.wishlist.screen.courseUnavailable': 'Khóa học không còn khả dụng',
      'lumiApp.wishlist.screen.unclassified': 'Chưa phân loại',
      'lumiApp.wishlist.screen.authorLabel': 'Người đăng',
      'lumiApp.wishlist.screen.accessDuration': 'Truy cập {{days}} ngày',
      'lumiApp.wishlist.screen.priceUnavailable': 'Chưa cập nhật học phí',
      'lumiApp.wishlist.screen.savedAt': 'Đã lưu',
      'lumiApp.wishlist.screen.viewDetails': 'Xem chi tiết',
      'lumiApp.wishlist.screen.viewFavoriteDetails': 'Xem chi tiết mục yêu thích: {{title}}',
      'lumiApp.wishlist.screen.editFavorite': 'Chỉnh sửa mục yêu thích: {{title}}',
      'lumiApp.wishlist.screen.enroll': 'Đăng ký học',
      'lumiApp.wishlist.screen.enrollmentUnavailable': 'Luồng đăng ký học viên chưa được tích hợp.',
      'lumiApp.wishlist.screen.removeFavorite': 'Bỏ {{title}} khỏi danh sách yêu thích',
      'lumiApp.wishlist.screen.removeFavoriteLabel': 'Bỏ khỏi yêu thích',
      'lumiApp.wishlist.screen.enrollmentNote':
        'Chức năng đăng ký học viên chưa được tích hợp. Lưu yêu thích chưa phải là đăng ký học; hãy xem chi tiết khóa học trước khi đăng ký.',
      'lumiApp.wishlist.home.refreshListLabel': 'Làm mới danh sách',
    });
  });

  beforeEach(() => {
    axios.get = vi.fn().mockResolvedValue({ data: createWishlists() });
  });

  it('renders API course data and filters by course title or author', async () => {
    const { container } = renderWishlist();

    await screen.findByText('Toán lớp 6');
    expect(container.querySelectorAll('[data-cy="entityTable"]')).toHaveLength(2);

    fireEvent.change(screen.getByLabelText('Khối lớp'), { target: { value: 'Lớp 6' } });
    expect(container.querySelectorAll('[data-cy="entityTable"]')).toHaveLength(1);

    fireEvent.change(screen.getByLabelText('Khối lớp'), { target: { value: '' } });
    fireEvent.change(screen.getByLabelText('Tìm trong danh sách yêu thích'), { target: { value: 'Minh Anh' } });
    await waitFor(() => expect(container.querySelectorAll('[data-cy="entityTable"]')).toHaveLength(1));
    expect(screen.getByText('Toán lớp 6')).toBeTruthy();
  });

  it('shows a no-matches state for a search with no results', async () => {
    const { container } = renderWishlist();

    await screen.findByText('Toán lớp 6');
    fireEvent.change(screen.getByLabelText('Tìm trong danh sách yêu thích'), { target: { value: 'Không có khóa học' } });

    expect(screen.getByText('Không tìm thấy khóa học phù hợp')).toBeTruthy();
    expect(container.querySelectorAll('[data-cy="entityTable"]')).toHaveLength(0);
  });

  it('surfaces a failed wishlist request as an alert', async () => {
    axios.get = vi.fn().mockRejectedValue(new Error('Network unavailable'));

    renderWishlist();

    const alert = await screen.findByRole('alert');
    expect(alert.textContent).toContain('Network unavailable');
  });
});
