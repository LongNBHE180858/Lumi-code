import React, { useRef, useState } from 'react';
import { Button, Card, Form, Modal, ProgressBar } from 'react-bootstrap';
import { useStore } from 'react-redux';
import { toast } from 'react-toastify';
import { IRootState, useAppDispatch, useAppSelector } from 'app/config/store';
import { builderErrorStatus, builderErrorText } from '../course-builder-error';
import { formatDuration, normalizeVideoDuration } from './video-duration';
import {
  getLessonMediaAccess,
  ILessonMediaTarget,
  removeLessonMedia,
  renameLessonMaterial,
  uploadLessonMedia,
  reorderLessonMaterials,
  updateVideoDuration,
} from '../course-builder.reducer';

const errorText = (error: unknown) => {
  if (builderErrorStatus(error) === 400) return 'Tệp không hợp lệ. Video cần là MP4; tài liệu cần là PDF hoặc DOCX hợp lệ.';
  return builderErrorText(error);
};

const fileSize = (bytes?: number | null) => (bytes == null ? '' : `${(bytes / 1024 / 1024).toFixed(1)} MB`);

// Mỗi nút giữ lại tệp upload lỗi để thử lại, không thay đổi tệp đang dùng.
const MediaUpload = ({ target, disabled, label }: { target: ILessonMediaTarget; disabled: boolean; label: string }) => {
  const dispatch = useAppDispatch();
  const store = useStore<IRootState>();
  const inputRef = useRef<HTMLInputElement>(null);
  const busyRef = useRef(false);
  const [file, setFile] = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);
  const [percent, setPercent] = useState<number | null>(0);
  const [error, setError] = useState('');
  const isVideo = target.kind === 'video';

  const upload = async (selected: File) => {
    const state = store.getState().courseBuilder;
    if (disabled || state.saving || busyRef.current || !state.data) return;
    setFile(selected);
    setError('');
    if (!selected.size || selected.name.length > 255) {
      setError('Chọn tệp có nội dung và tên không vượt quá 255 ký tự.');
      return;
    }
    const valid = isVideo ? /\.mp4$/i.test(selected.name) : /\.(pdf|docx)$/i.test(selected.name);
    if (!valid) {
      setError(isVideo ? 'Vui lòng chọn video MP4.' : 'Vui lòng chọn tài liệu PDF hoặc DOCX.');
      return;
    }
    busyRef.current = true;
    setUploading(true);
    setPercent(0);
    try {
      await dispatch(uploadLessonMedia({ ...target, version: state.data.course.version, file: selected, onProgress: setPercent })).unwrap();
      setFile(null);
      toast.success('Đã lưu tệp.');
    } catch (failure) {
      const message = errorText(failure);
      setError(message);
      toast.error(message);
    } finally {
      busyRef.current = false;
      setUploading(false);
    }
  };

  return (
    <div>
      <input
        ref={inputRef}
        type="file"
        className="d-none"
        aria-label={label}
        accept={isVideo ? '.mp4' : '.pdf,.docx'}
        disabled={disabled || uploading}
        onChange={event => {
          const selected = event.target.files?.[0];
          event.target.value = '';
          if (selected) void upload(selected);
        }}
      />
      <Button size="sm" variant="outline-primary" disabled={disabled || uploading} onClick={() => inputRef.current?.click()}>
        {label}
      </Button>
      {file && (
        <div className="small text-muted mt-2">
          {file.name} • {fileSize(file.size)}
        </div>
      )}
      {uploading && (
        <div className="mt-2" role="status">
          <ProgressBar now={percent ?? 100} animated={percent === null} label={percent === null ? undefined : `${percent}%`} />
          <span className="small text-muted">{percent === 100 ? 'Đang lưu tệp…' : 'Đang tải tệp lên…'}</span>
        </div>
      )}
      {error && (
        <div className="mt-2">
          <div className="text-danger small" role="alert">
            {error}
          </div>
          <div className="d-flex gap-2 mt-1">
            <Button size="sm" disabled={disabled} onClick={() => file && void upload(file)}>
              Thử lại
            </Button>
            <Button
              size="sm"
              variant="link"
              disabled={disabled}
              onClick={() => {
                setFile(null);
                setError('');
              }}
            >
              Bỏ tệp
            </Button>
          </div>
        </div>
      )}
    </div>
  );
};

const VideoPreview = ({
  target,
  storedUrl,
  durationSeconds,
}: {
  target: ILessonMediaTarget;
  storedUrl: string;
  durationSeconds?: number | null;
}) => {
  const dispatch = useAppDispatch();
  const store = useStore<IRootState>();
  const durationBusyRef = useRef(false);
  const [measured, setMeasured] = useState<{ source: string; seconds: number } | null>(null);
  const [preview, setPreview] = useState<{ source: string; url: string } | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const saveDuration = async (seconds: number) => {
    const state = store.getState().courseBuilder;
    const lesson = state.data?.chapters.flatMap(chapter => chapter.lessons).find(item => item.lesson.id === target.lessonId)?.lesson;
    if (!lesson || lesson.videoUrl !== storedUrl || lesson.durationSeconds != null || state.saving || durationBusyRef.current) return;
    durationBusyRef.current = true;
    try {
      await dispatch(
        updateVideoDuration({
          courseId: target.courseId,
          lessonId: target.lessonId,
          version: state.data!.course.version,
          durationSeconds: seconds,
        }),
      ).unwrap();
    } catch (failure) {
      setError(errorText(failure));
    } finally {
      durationBusyRef.current = false;
    }
  };
  const load = async () => {
    if (loading) return;
    setLoading(true);
    setError('');
    try {
      const access = await getLessonMediaAccess(target, true);
      setPreview({ source: storedUrl, url: access.url });
    } catch (failure) {
      setError(errorText(failure));
    } finally {
      setLoading(false);
    }
  };
  const url = preview?.source === storedUrl ? preview.url : null;
  if (!storedUrl) return <div className="text-muted mb-3">Chưa có video cho bài học này.</div>;
  return (
    <div className="mb-3">
      {url && (
        <video
          key={url}
          src={url}
          controls
          preload="metadata"
          className="w-100 rounded-3"
          aria-label="Video bài học"
          style={{ maxHeight: 320 }}
          onLoadedMetadata={event => {
            const seconds = normalizeVideoDuration(event.currentTarget.duration);
            if (seconds !== null) {
              setMeasured({ source: storedUrl, seconds });
              void saveDuration(seconds);
            }
          }}
          onError={() => setError('Không phát được video hoặc URL đã hết hạn. Hãy lấy lại URL phát.')}
        />
      )}
      <Button size="sm" variant="outline-secondary" disabled={loading} onClick={load}>
        {loading ? 'Đang lấy URL…' : 'Xem / tải lại video'}
      </Button>
      {measured?.source === storedUrl && durationSeconds == null && (
        <Button size="sm" variant="link" onClick={() => void saveDuration(measured.seconds)}>
          Lưu thời lượng {formatDuration(measured.seconds)}
        </Button>
      )}
      {error && (
        <div className="text-danger small mt-2" role="alert">
          {error}
        </div>
      )}
    </div>
  );
};

const downloadMedia = async (target: ILessonMediaTarget) => {
  try {
    const { url } = await getLessonMediaAccess(target);
    const link = document.createElement('a');
    link.href = url;
    link.rel = 'noopener';
    link.download = '';
    document.body.appendChild(link);
    link.click();
    link.remove();
  } catch (failure) {
    toast.error(errorText(failure));
  }
};

export const LessonMedia = ({ lessonId, disabled }: { lessonId: number; disabled: boolean }) => {
  const dispatch = useAppDispatch();
  const store = useStore<IRootState>();
  const data = useAppSelector(state => state.courseBuilder.data);
  const node = data?.chapters.flatMap(chapter => chapter.lessons).find(item => item.lesson.id === lessonId);
  const busyRef = useRef(false);
  const [removeTarget, setRemoveTarget] = useState<ILessonMediaTarget | null>(null);
  const [rename, setRename] = useState<{ id: number; name: string } | null>(null);
  const [dialogError, setDialogError] = useState('');
  const [working, setWorking] = useState(false);

  const saveDialog = async () => {
    const state = store.getState().courseBuilder;
    if (!state.data || state.saving || busyRef.current) return;
    busyRef.current = true;
    setWorking(true);
    setDialogError('');
    try {
      const args = { courseId: state.data.course.id, lessonId, version: state.data.course.version };
      if (removeTarget) await dispatch(removeLessonMedia({ ...removeTarget, version: args.version })).unwrap();
      else if (rename) await dispatch(renameLessonMaterial({ ...args, materialId: rename.id, displayName: rename.name.trim() })).unwrap();
      setRemoveTarget(null);
      setRename(null);
      toast.success('Đã cập nhật tệp.');
    } catch (failure) {
      setDialogError(errorText(failure));
      toast.error(errorText(failure));
    } finally {
      busyRef.current = false;
      setWorking(false);
    }
  };

  const moveMaterial = async (index: number, offset: number) => {
    const latest = store.getState().courseBuilder;
    if (disabled || latest.saving || !latest.data || busyRef.current) return;
    const lesson = latest.data.chapters.flatMap(chapter => chapter.lessons).find(item => item.lesson.id === lessonId);
    if (!lesson || index + offset < 0 || index + offset >= lesson.materials.length) return;
    const materialIds = lesson.materials.map(material => material.id!);
    [materialIds[index], materialIds[index + offset]] = [materialIds[index + offset], materialIds[index]];
    busyRef.current = true;
    try {
      await dispatch(
        reorderLessonMaterials({ courseId: latest.data.course.id, lessonId, version: latest.data.course.version, materialIds }),
      ).unwrap();
    } catch (error) {
      toast.error(builderErrorText(error));
    } finally {
      busyRef.current = false;
    }
  };

  if (!data || !node) return null;
  const videoTarget: ILessonMediaTarget = { courseId: data.course.id, lessonId, kind: 'video' };
  const materialTarget: ILessonMediaTarget = { ...videoTarget, kind: 'material' };
  const closeDialog = () => {
    if (!working) {
      setRemoveTarget(null);
      setRename(null);
      setDialogError('');
    }
  };

  return (
    <>
      <Card className="p-4 rounded-4 border-0 shadow-sm">
        <h5 className="fw-bold mb-3">Tài liệu đính kèm</h5>
        <MediaUpload target={materialTarget} disabled={disabled} label="Tải tài liệu PDF / DOCX" />
        {!node.materials.length && <div className="text-muted small mt-3">Chưa có tài liệu đính kèm.</div>}
        {node.materials.map((material, index) => {
          const target = { ...materialTarget, materialId: material.id! };
          const name = material.displayName || material.fileName || 'Tài liệu';
          return (
            <div key={material.id} className="border rounded-3 p-3 mt-3">
              <div className="fw-semibold text-break">{name}</div>
              <div className="small text-muted mb-2">
                {fileSize(material.fileSizeBytes)} • {material.fileType}
              </div>
              <div className="d-flex flex-wrap gap-2 align-items-start">
                <Button
                  size="sm"
                  variant="outline-secondary"
                  aria-label={`Đưa ${name} lên`}
                  disabled={disabled || index === 0}
                  onClick={() => void moveMaterial(index, -1)}
                >
                  ↑
                </Button>
                <Button
                  size="sm"
                  variant="outline-secondary"
                  aria-label={`Đưa ${name} xuống`}
                  disabled={disabled || index === node.materials.length - 1}
                  onClick={() => void moveMaterial(index, 1)}
                >
                  ↓
                </Button>
                <MediaUpload target={target} disabled={disabled} label={`${name}`} />
                <Button
                  size="sm"
                  variant="outline-secondary"
                  disabled={disabled}
                  onClick={() => {
                    setDialogError('');
                    setRename({ id: material.id!, name });
                  }}
                >
                  Đổi tên
                </Button>
                <Button size="sm" variant="outline-secondary" disabled={disabled} onClick={() => void downloadMedia(target)}>
                  Tải xuống
                </Button>
                <Button
                  size="sm"
                  variant="outline-danger"
                  disabled={disabled}
                  onClick={() => {
                    setDialogError('');
                    setRemoveTarget(target);
                  }}
                >
                  Gỡ
                </Button>
              </div>
            </div>
          );
        })}
      </Card>

      <Card className="p-4 rounded-4 border-0 shadow-sm">
        <h5 className="fw-bold mb-3">Video bài học</h5>
        <VideoPreview target={videoTarget} storedUrl={node.lesson.videoUrl ?? ''} durationSeconds={node.lesson.durationSeconds} />
        {node.lesson.videoUrl && (
          <div className="small text-muted mb-3">
            {node.lesson.videoFileName} • {fileSize(node.lesson.videoSizeBytes)}
            {' • '}
            {node.lesson.durationSeconds == null ? 'Chưa xác định thời lượng' : formatDuration(node.lesson.durationSeconds)}
          </div>
        )}
        <div className="d-flex flex-wrap gap-2 align-items-start">
          <MediaUpload target={videoTarget} disabled={disabled} label={node.lesson.videoUrl ? 'Thay thế video MP4' : 'Tải video MP4'} />
          {node.lesson.videoUrl && (
            <>
              <Button size="sm" variant="outline-secondary" disabled={disabled} onClick={() => void downloadMedia(videoTarget)}>
                Tải video xuống
              </Button>
              <Button
                size="sm"
                variant="outline-danger"
                disabled={disabled}
                onClick={() => {
                  setDialogError('');
                  setRemoveTarget(videoTarget);
                }}
              >
                Gỡ video
              </Button>
            </>
          )}
        </div>
      </Card>

      <Modal show={Boolean(removeTarget || rename)} onHide={closeDialog} centered backdrop="static" keyboard={!working}>
        <Modal.Header closeButton={!working}>
          <Modal.Title>{rename ? 'Đổi tên tài liệu' : 'Gỡ tệp khỏi bài học'}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {rename ? (
            <Form.Group>
              <Form.Control
                autoFocus
                aria-label="Tên tài liệu"
                value={rename.name}
                maxLength={255}
                disabled={working}
                isInvalid={!rename.name.trim()}
                onChange={event => setRename({ ...rename, name: event.target.value })}
              />
              <Form.Control.Feedback type="invalid">Vui lòng nhập tên tài liệu.</Form.Control.Feedback>
            </Form.Group>
          ) : (
            'Bạn có chắc muốn gỡ tệp này?'
          )}
          {dialogError && (
            <div className="text-danger small mt-2" role="alert">
              {dialogError}
            </div>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="outline-secondary" disabled={working} onClick={closeDialog}>
            Hủy
          </Button>
          <Button variant={rename ? 'primary' : 'danger'} disabled={working || Boolean(rename && !rename.name.trim())} onClick={saveDialog}>
            {working ? 'Đang lưu…' : 'Xác nhận'}
          </Button>
        </Modal.Footer>
      </Modal>
    </>
  );
};

export default LessonMedia;
