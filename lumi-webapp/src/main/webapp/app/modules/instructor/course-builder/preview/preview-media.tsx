import React, { useEffect, useState } from 'react';
import { Alert, Button, Spinner } from 'react-bootstrap';
import { builderErrorText } from '../course-builder-error';
import { getThumbnailAccessUrl, getIntroVideoPlaybackUrl, getLessonMediaAccess, type IMediaAccess } from '../course-builder.reducer';

interface MediaTarget {
  courseId: number;
  kind: 'thumbnail' | 'intro' | 'video' | 'material';
  source: string;
  lessonId?: number;
  materialId?: number;
}

const requestAccess = (target: MediaTarget) => {
  if (target.kind === 'thumbnail') return getThumbnailAccessUrl(target.courseId);
  if (target.kind === 'intro') return getIntroVideoPlaybackUrl(target.courseId);
  return getLessonMediaAccess({ ...target, lessonId: target.lessonId!, kind: target.kind }, target.kind === 'video');
};

// Access belongs to one resource only. Ignore responses after changing resource or unmounting.
const useMediaAccess = ({ courseId, kind, source, lessonId, materialId }: MediaTarget) => {
  const key = JSON.stringify([courseId, kind, source, lessonId, materialId]);
  const [attempt, setAttempt] = useState(0);
  const [state, setState] = useState<{ key: string; access?: IMediaAccess; error?: string }>({ key });
  useEffect(() => {
    let cancelled = false;
    let expiryTimer: ReturnType<typeof setTimeout> | undefined;
    setState({ key });
    requestAccess({ courseId, kind, source, lessonId, materialId })
      .then(access => {
        if (cancelled) return;
        const parsedExpiry = access?.expiresAt ? Date.parse(access.expiresAt) : NaN;
        const remaining = Number.isFinite(parsedExpiry) ? parsedExpiry - Date.now() : NaN;
        if (!access?.url || !Number.isFinite(remaining) || remaining <= 0) {
          setState({ key, error: 'Liên kết đã hết hạn. Hãy lấy lại liên kết để tiếp tục.' });
          return;
        }
        setState({ key, access });
        expiryTimer = setTimeout(
          () => {
            if (!cancelled) setState({ key, error: 'Liên kết đã hết hạn. Hãy lấy lại liên kết để tiếp tục.' });
          },
          Math.min(remaining, 2147483647),
        );
      })
      .catch(error => {
        if (!cancelled) setState({ key, error: builderErrorText(error, 'Không thể tải nội dung. Hãy thử lấy lại liên kết.') });
      });
    return () => {
      cancelled = true;
      if (expiryTimer) clearTimeout(expiryTimer);
    };
  }, [key, courseId, kind, source, lessonId, materialId, attempt]);
  const current = state.key === key ? state : { key };
  return {
    ...current,
    retry: () => setAttempt(value => value + 1),
    failed: () => setState({ key, error: 'Không thể mở tệp hoặc liên kết đã hết hạn. Hãy lấy lại liên kết và thử lại.' }),
  };
};

export const PreviewMedia = ({ target, label }: { target: MediaTarget; label: string }) => {
  const media = useMediaAccess(target);
  if (media.error)
    return (
      <Alert variant="warning" className="mb-0">
        <div role="alert">{media.error}</div>
        <Button variant="outline-primary" size="sm" className="mt-2" onClick={media.retry}>
          Lấy lại liên kết: {label}
        </Button>
      </Alert>
    );
  if (!media.access)
    return (
      <div
        role="status"
        className={`preview-media-skeleton preview-media-skeleton-${target.kind} d-flex align-items-center justify-content-center text-muted rounded-4`}
      >
        <Spinner size="sm" className="me-2" />
        <span>Đang tải {label}...</span>
      </div>
    );
  if (target.kind === 'thumbnail')
    return <img src={media.access.url} alt={label} className="preview-media-thumbnail w-100 rounded-4" onError={media.failed} />;
  if (target.kind === 'material')
    return (
      <a href={media.access.url} target="_blank" rel="noopener noreferrer" className="btn btn-outline-primary btn-sm">
        Mở/tải tài liệu: {label}
      </a>
    );
  return (
    <div className="preview-video-wrapper rounded-4 overflow-hidden position-relative">
      <video
        key={media.access.url}
        src={media.access.url}
        aria-label={label}
        controls
        preload="metadata"
        className="w-100 rounded-4 bg-dark d-block"
        onError={media.failed}
      >
        Trình duyệt không hỗ trợ phát video.
      </video>
    </div>
  );
};
