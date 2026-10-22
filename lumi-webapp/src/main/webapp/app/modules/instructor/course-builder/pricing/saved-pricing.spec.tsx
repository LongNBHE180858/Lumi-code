import React from 'react';
import { configureStore } from '@reduxjs/toolkit';
import { Provider } from 'react-redux';
import { act, cleanup, render, screen, within, waitFor } from '@testing-library/react';
import { afterEach, beforeEach, expect, it, vi } from 'vitest';
import axios from 'axios';
import reducer, { createDraft, updatePricing, type IBuilderAggregate, type ICourseData } from '../course-builder.reducer';
import { savedPricing } from './saved-pricing';
import CoursePreview from '../preview/course-preview';
import CourseReviewSubmit from '../review/course-review-submit';
import CourseMediaIntro from '../basic-info/course-media-intro';

vi.mock('axios', () => ({ default: { get: vi.fn(), put: vi.fn() } }));
vi.mock('react-toastify', () => ({ toast: { success: vi.fn(), error: vi.fn() } }));
afterEach(cleanup);
beforeEach(() => {
  vi.clearAllMocks();
  sessionStorage.clear();
});

const course = (patch: Partial<ICourseData> = {}): ICourseData => ({
  id: 1,
  version: 1,
  title: 'Khóa học',
  status: 'DRAFT',
  price: 350000,
  accessDurationValue: 6,
  accessDurationUnit: 'MONTH',
  ...patch,
});
const aggregate = (value: ICourseData): IBuilderAggregate => ({
  course: value,
  chapters: [],
  totalLessons: 0,
  totalVideos: 0,
  totalMaterials: 0,
  review: {
    version: value.version,
    canSubmit: savedPricing(value).valid,
    checks: [
      { code: 'pricing', tab: 'pricing', label: 'Học phí và thời hạn truy cập', required: true, complete: savedPricing(value).valid },
    ],
  },
});
const setup = (value: ICourseData, view: React.ReactNode) => {
  const store = configureStore({ reducer: { courseBuilder: reducer, authentication: () => ({ account: { login: 'teacher' } }) } });
  store.dispatch(createDraft.fulfilled(aggregate(value), 'initial', undefined));
  vi.mocked(axios.get).mockResolvedValue({ data: aggregate(value) });
  render(<Provider store={store}>{view}</Provider>);
  return store;
};

it.each(['DAY', 'MONTH', 'YEAR'] as const)('formats saved %s without approximating calendar units', unit => {
  const result = savedPricing(course({ accessDurationUnit: unit }));
  expect(result.valid).toBe(true);
  expect(result.priceText).toBe('350.000 ₫');
  expect(result.durationText).toBe(`6 ${{ DAY: 'ngày', MONTH: 'tháng', YEAR: 'năm' }[unit]}`);
});

it.each([
  { price: null },
  { price: -1 },
  { price: 1.5 },
  { price: NaN },
  { accessDurationValue: null },
  { accessDurationValue: 0 },
  { accessDurationValue: -1 },
  { accessDurationValue: 1.5 },
  { accessDurationValue: 2147483648 },
  { accessDurationUnit: null, validDurationDays: 30 },
] as Partial<ICourseData>[])('does not complete pricing for invalid or partial data: %j', patch => {
  expect(savedPricing(course(patch)).valid).toBe(false);
});

it('accepts legacy days only when both new duration fields are absent', () => {
  expect(savedPricing(course({ accessDurationValue: null, accessDurationUnit: null, validDurationDays: 45 })).durationText).toBe('45 ngày');
  expect(savedPricing(course({ accessDurationValue: 2, accessDurationUnit: 'YEAR', validDurationDays: 45 })).durationText).toBe('2 năm');
  expect(savedPricing(undefined).valid).toBe(false);
});

it('updates Preview from the saved aggregate and omits payment wording for free courses', () => {
  const store = setup(course(), <CoursePreview onBack={vi.fn()} onNext={vi.fn()} />);
  expect(screen.getByText('350.000 ₫')).toBeTruthy();
  expect(screen.getByText('Thanh toán một lần • Truy cập 6 tháng')).toBeTruthy();
  const saved = aggregate(course({ price: 0, version: 2, accessDurationUnit: 'YEAR', accessDurationValue: 1 }));
  act(() =>
    store.dispatch(
      updatePricing.fulfilled(saved, 'save', {
        courseId: 1,
        data: { version: 1, price: 0, accessDurationValue: 1, accessDurationUnit: 'YEAR' },
      }),
    ),
  );
  expect(screen.getByText('Miễn phí')).toBeTruthy();
  expect(screen.getByText('Truy cập 1 năm')).toBeTruthy();
  expect(screen.queryByText(/Thanh toán một lần/)).toBeNull();
  expect(screen.queryByText('350.000 ₫')).toBeNull();
});

it('shows incomplete pricing and blocks the review action when duration is missing', async () => {
  const store = setup(course({ price: 0, accessDurationValue: null }), <CourseReviewSubmit onBack={vi.fn()} />);
  await waitFor(() => expect(store.getState().courseBuilder.loading).toBe(false));
  expect(screen.getByRole('button', { name: 'Bổ sung: Học phí và thời hạn truy cập' })).toBeTruthy();
  expect((screen.getByRole('button', { name: 'Gửi duyệt', exact: true }) as HTMLButtonElement).disabled).toBe(true);
  expect(screen.queryByText(/499.000/)).toBeNull();
});

it('uses saved free pricing and legacy days in review without pre-confirming content rights', async () => {
  const store = setup(
    course({ price: 0, accessDurationValue: null, accessDurationUnit: null, validDurationDays: 30 }),
    <CourseReviewSubmit onBack={vi.fn()} />,
  );
  await waitFor(() => expect(store.getState().courseBuilder.loading).toBe(false));
  expect(screen.getByText('Truy cập 30 ngày')).toBeTruthy();
  expect(screen.queryByText(/Thanh toán một lần/)).toBeNull();
  expect((screen.getByRole('button', { name: 'Gửi duyệt', exact: true }) as HTMLButtonElement).disabled).toBe(true);
});

it('marks the progress checklist complete only after valid pricing and duration are saved', () => {
  const store = setup(
    course({ accessDurationValue: null }),
    <CourseMediaIntro basicInfo={{ title: 'Khóa học', subjectId: 1, gradeId: 1 }} infoDirty={false} />,
  );
  const heading = screen.getByRole('heading', { name: 'Tiến độ bản nháp' });
  const card = within(heading.parentElement!);
  expect(card.getByText('Chưa hoàn tất học phí hoặc thời hạn truy cập').parentElement!.className).toContain('text-secondary');
  const saved = aggregate(course({ version: 2 }));
  act(() =>
    store.dispatch(
      updatePricing.fulfilled(saved, 'save', {
        courseId: 1,
        data: { version: 1, price: 350000, accessDurationValue: 6, accessDurationUnit: 'MONTH' },
      }),
    ),
  );
  expect(card.getByText('Học phí: 350.000 ₫ • Truy cập 6 tháng').parentElement!.className).toContain('text-success');
});
