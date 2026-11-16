import type { ILesson } from 'app/shared/model/lesson.model';

export const formatDuration = (seconds: number) => {
  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  const rest = seconds % 60;
  return [hours ? `${hours} giờ` : '', minutes ? `${minutes} phút` : '', rest || !seconds ? `${rest} giây` : ''].filter(Boolean).join(' ');
};

export const lessonSeconds = (lesson: ILesson) =>
  lesson.durationSeconds ?? (lesson.durationMinutes == null ? null : lesson.durationMinutes * 60);

export const chapterDuration = (lessons: ILesson[]) => {
  const videos = lessons.filter(lesson => lesson.videoUrl);
  if (!videos.length) return 'Chưa có video';
  const known = videos.filter(lesson => lessonSeconds(lesson) !== null);
  if (!known.length) return 'Chưa xác định thời lượng';
  const total = known.reduce((sum, lesson) => sum + lessonSeconds(lesson)!, 0);
  const missing = videos.length - known.length;
  return missing ? `Đã xác định ${formatDuration(total)} • Còn ${missing} video chưa có thời lượng` : formatDuration(total);
};

export const normalizeVideoDuration = (duration: number): number | null =>
  Number.isFinite(duration) && duration > 0 && duration <= 2147483647 ? Math.max(1, Math.round(duration)) : null;

// Không đọc được metadata vẫn cho upload; không giả định video có thời lượng bằng 0.
export const readVideoDuration = (file: File): Promise<number | null> => {
  if (typeof URL.createObjectURL !== 'function') return Promise.resolve(null);
  return new Promise(resolve => {
    const video = document.createElement('video');
    const url = URL.createObjectURL(file);
    const finish = (seconds: number | null) => {
      clearTimeout(timer);
      video.onloadedmetadata = null;
      video.onerror = null;
      video.removeAttribute('src');
      URL.revokeObjectURL(url);
      resolve(seconds);
    };
    const timer = setTimeout(() => finish(null), 10000);
    video.preload = 'metadata';
    video.onloadedmetadata = () => finish(normalizeVideoDuration(video.duration));
    video.onerror = () => finish(null);
    video.src = url;
  });
};
