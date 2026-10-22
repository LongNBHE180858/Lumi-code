import { afterEach, describe, expect, it, vi } from 'vitest';
import { chapterDuration, formatDuration, normalizeVideoDuration, readVideoDuration } from './video-duration';

afterEach(() => {
  vi.restoreAllMocks();
  vi.unstubAllGlobals();
  vi.useRealTimers();
});

describe('Thời lượng video', () => {
  it('định dạng theo giây, không làm tròn cả chương thành 0 giờ', () => {
    expect(formatDuration(125)).toBe('2 phút 5 giây');
    expect(formatDuration(3601)).toBe('1 giờ 1 giây');
    expect(
      chapterDuration([
        { videoUrl: 'a', durationSeconds: 125 },
        { videoUrl: 'b', durationSeconds: 35 },
      ]),
    ).toBe('2 phút 40 giây');
  });
  it('phân biệt không có video, chưa xác định và chỉ biết một phần', () => {
    expect(chapterDuration([{}])).toBe('Chưa có video');
    expect(chapterDuration([{ videoUrl: 'a' }])).toBe('Chưa xác định thời lượng');
    expect(chapterDuration([{ videoUrl: 'a', durationSeconds: 60 }, { videoUrl: 'b' }])).toBe(
      'Đã xác định 1 phút • Còn 1 video chưa có thời lượng',
    );
  });
  it('hỗ trợ dữ liệu phút cũ và bỏ qua metadata không hợp lệ', () => {
    expect(chapterDuration([{ videoUrl: 'a', durationMinutes: 2 }])).toBe('2 phút');
    expect(normalizeVideoDuration(Infinity)).toBeNull();
    expect(normalizeVideoDuration(NaN)).toBeNull();
    expect(normalizeVideoDuration(-1)).toBeNull();
    expect(normalizeVideoDuration(2147483648)).toBeNull();
  });
  it('đọc metadata của file và giải phóng blob URL', async () => {
    const video = document.createElement('video');
    vi.spyOn(document, 'createElement').mockReturnValue(video);
    const revokeObjectURL = vi.fn();
    vi.stubGlobal('URL', { createObjectURL: () => 'blob:local-video', revokeObjectURL });
    const pending = readVideoDuration(new File(['video'], 'video.mp4'));
    Object.defineProperty(video, 'duration', { value: 125.2 });
    video.dispatchEvent(new Event('loadedmetadata'));
    expect(await pending).toBe(125);
    expect(revokeObjectURL).toHaveBeenCalledWith('blob:local-video');
  });
  it('metadata không đọc được vẫn cho upload với thời lượng chưa xác định', async () => {
    vi.useFakeTimers();
    vi.stubGlobal('URL', { createObjectURL: () => 'blob:video', revokeObjectURL: vi.fn() });
    const pending = readVideoDuration(new File(['video'], 'video.mp4'));
    await vi.advanceTimersByTimeAsync(10000);
    expect(await pending).toBeNull();
  });
});
