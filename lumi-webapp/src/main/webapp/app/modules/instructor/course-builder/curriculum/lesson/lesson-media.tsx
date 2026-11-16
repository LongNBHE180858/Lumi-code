import React, { useRef, useState } from 'react';
import { Button, Card, Form, Modal, ProgressBar, Spinner } from 'react-bootstrap';
import { useStore } from 'react-redux';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faPause, faPlay, faEye, faTrash, faVideo, faPen, faCheck, faInfoCircle } from '@fortawesome/free-solid-svg-icons';
import { toast } from 'react-toastify';
import { IRootState, useAppDispatch, useAppSelector } from 'app/config/store';
import { builderErrorStatus, builderErrorText } from '../../course-builder-error';
import { formatDuration, normalizeVideoDuration } from './video-duration';
import {
  getLessonMediaAccess,
  ILessonMediaTarget,
  removeLessonMedia,
  renameLessonMaterial,
  uploadLessonMedia,
  reorderLessonMaterials,
  updateVideoDuration,
  getLessonCuePoints,
  createLessonCuePoint,
  addExamQuestion,
  updateExamQuestion,
  getExamDetail,
  deleteExam,
  IExamSummaryDTO,
  IQuestionDetailDTO,
  IExamDetailDTO,
} from '../../course-builder.reducer';
import { QuestionEditorModal } from './question-editor-modal';
import { QuestionDetailModal } from './question-detail-modal';

// =========================================================================
// Component Modal Xem Trước Tài Liệu (PDF / DOCX)
// =========================================================================
interface IMaterialPreviewState {
  isOpen: boolean;
  name: string;
  fileType: string;
  url: string;
  loading: boolean;
  error: string;
}

const MaterialPreviewModal = ({
  preview,
  onClose,
  onDownload,
}: {
  preview: IMaterialPreviewState;
  onClose: () => void;
  onDownload: () => void;
}) => {
  const isPdf = preview.fileType?.toUpperCase() === 'PDF' || preview.name?.toLowerCase().endsWith('.pdf');

  return (
    <Modal show={preview.isOpen} onHide={onClose} size="xl" centered backdrop="static">
      <Modal.Header closeButton>
        <Modal.Title className="text-truncate" style={{ maxWidth: '80%' }}>
          {preview.name || 'Xem trước tài liệu'}
        </Modal.Title>
      </Modal.Header>
      <Modal.Body style={{ minHeight: '500px', maxHeight: '80vh', overflowY: 'auto' }} className="p-2">
        {preview.loading && (
          <div className="d-flex flex-column align-items-center justify-content-center py-5">
            <Spinner animation="border" variant="primary" />
            <div className="mt-3 text-muted">Đang tải tài liệu xem trước...</div>
          </div>
        )}

        {preview.error && (
          <div className="alert alert-danger my-3" role="alert">
            {preview.error}
          </div>
        )}

        {!preview.loading &&
          !preview.error &&
          preview.url &&
          (isPdf ? (
            /* Hiển thị PDF trực tiếp bằng trình đọc của trình duyệt */
            <iframe src={preview.url} title={preview.name} width="100%" height="700px" style={{ border: 'none', borderRadius: '4px' }} />
          ) : (
            /* Hiển thị DOCX qua Office Viewer */
            <div className="w-100 h-100">
              <iframe
                src={`https://view.officeapps.live.com/op/embed.aspx?src=${encodeURIComponent(preview.url)}`}
                title={preview.name}
                width="100%"
                height="700px"
                style={{ border: 'none', borderRadius: '4px' }}
              />
            </div>
          ))}
      </Modal.Body>
      <Modal.Footer>
        <Button variant="outline-secondary" onClick={onDownload}>
          Tải xuống tệp gốc
        </Button>
        <Button variant="secondary" onClick={onClose}>
          Đóng
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

const errorText = (error: unknown) => {
  if (builderErrorStatus(error) === 400) return 'Tệp không hợp lệ. Video cần là MP4; tài liệu cần là PDF hoặc DOCX hợp lệ.';
  return builderErrorText(error);
};

const fileSize = (bytes?: number | null) => (bytes == null ? '' : `${(bytes / 1024 / 1024).toFixed(1)} MB`);

const formatTimeMMSS = (sec: number) => {
  const m = Math.floor(sec / 60);
  const s = Math.floor(sec % 60);
  return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
};

// Mỗi nút giữ lại tệp upload lỗi để thử lại, không thay đổi tệp đang dùng.
const MediaUpload = ({
  target,
  disabled,
  label,
  ariaLabel,
}: {
  target: ILessonMediaTarget;
  disabled: boolean;
  label: string;
  ariaLabel?: string;
}) => {
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
        aria-label={ariaLabel || label}
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
  disabled = false,
}: {
  target: ILessonMediaTarget;
  storedUrl: string;
  durationSeconds?: number | null;
  disabled?: boolean;
}) => {
  const dispatch = useAppDispatch();
  const store = useStore<IRootState>();
  const durationBusyRef = useRef(false);
  const videoRef = useRef<HTMLVideoElement | null>(null);

  const [measured, setMeasured] = useState<{ source: string; seconds: number } | null>(null);
  const [preview, setPreview] = useState<{ source: string; url: string } | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [currentSeconds, setCurrentSeconds] = useState(0);

  // Cue points
  const [cuePoints, setCuePoints] = useState<IExamSummaryDTO[]>([]);
  const [cueModalOpen, setCueModalOpen] = useState(false);
  const [targetCueSeconds, setTargetCueSeconds] = useState(0);
  const [viewQuestion, setViewQuestion] = useState<IQuestionDetailDTO | null>(null);
  const [editingCueTarget, setEditingCueTarget] = useState<{
    examId: number;
    question: IQuestionDetailDTO;
    triggerSeconds: number;
  } | null>(null);
  const [deleteCueTarget, setDeleteCueTarget] = useState<number | null>(null);

  // Video interactive playback popup
  const [triggeredCueIds, setTriggeredCueIds] = useState<Set<number>>(new Set());
  const [activeInteractiveExam, setActiveInteractiveExam] = useState<IExamDetailDTO | null>(null);
  const [selectedOptionId, setSelectedOptionId] = useState<number | null>(null);
  const [showAnswerFeedback, setShowAnswerFeedback] = useState(false);

  const loadCuePoints = async () => {
    try {
      const res = await dispatch(getLessonCuePoints({ courseId: target.courseId, lessonId: target.lessonId }));
      if (getLessonCuePoints.fulfilled.match(res)) {
        setCuePoints(res.payload);
      }
    } catch {
      /* ignore */
    }
  };

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
      void loadCuePoints();
    } catch (failure) {
      setError(errorText(failure));
    } finally {
      setLoading(false);
    }
  };

  const handlePauseAndAddCue = () => {
    if (videoRef.current) {
      videoRef.current.pause();
    }
    const sec = Math.floor(videoRef.current?.currentTime || currentSeconds || 0);
    const isDuplicate = cuePoints.some(cp => cp.videoTriggerSeconds != null && Math.floor(cp.videoTriggerSeconds) === sec);
    if (isDuplicate) {
      toast.warning(`Thời điểm ${formatTimeMMSS(sec)} đã có câu hỏi tương tác. Vui lòng chọn thời điểm khác.`);
      return;
    }
    setTargetCueSeconds(sec);
    setCueModalOpen(true);
  };

  const handleSaveCueQuestion = async (input: any) => {
    const isDuplicate = cuePoints.some(cp => cp.videoTriggerSeconds != null && Math.floor(cp.videoTriggerSeconds) === targetCueSeconds);
    if (isDuplicate) {
      toast.warning(`Thời điểm ${formatTimeMMSS(targetCueSeconds)} đã có câu hỏi tương tác.`);
      return;
    }

    try {
      const createdExam = await dispatch(
        createLessonCuePoint({
          courseId: target.courseId,
          lessonId: target.lessonId,
          input: {
            title: `Dừng video tại ${formatTimeMMSS(targetCueSeconds)}`,
            videoTriggerSeconds: targetCueSeconds,
          },
        }),
      ).unwrap();

      await dispatch(
        addExamQuestion({
          courseId: target.courseId,
          examId: createdExam.id,
          input: {
            ...input,
            difficulty: 'MEDIUM',
            score: 1,
          },
        }),
      ).unwrap();

      toast.success('Đã thêm câu hỏi tương tác vào video.');
      void loadCuePoints();
    } catch {
      toast.error('Không thể thêm câu hỏi tương tác.');
    }
  };

  const handleSaveEditCueQuestion = async (input: any) => {
    if (!editingCueTarget) return;
    try {
      await dispatch(
        updateExamQuestion({
          courseId: target.courseId,
          examId: editingCueTarget.examId,
          questionId: editingCueTarget.question.id,
          input: {
            ...input,
            difficulty: 'MEDIUM',
            score: 1,
          },
        }),
      ).unwrap();

      toast.success('Đã cập nhật câu hỏi tương tác.');
      setEditingCueTarget(null);
      void loadCuePoints();
    } catch {
      toast.error('Không thể cập nhật câu hỏi.');
    }
  };

  const handleDeleteCue = async () => {
    if (!deleteCueTarget) return;
    try {
      await dispatch(deleteExam({ courseId: target.courseId, examId: deleteCueTarget })).unwrap();
      toast.success('Đã xóa điểm dừng video.');
      void loadCuePoints();
    } catch {
      toast.error('Không thể xóa điểm dừng video.');
    } finally {
      setDeleteCueTarget(null);
    }
  };

  const url = preview?.source === storedUrl ? preview.url : null;
  if (!storedUrl) return <div className="text-muted mb-3">Chưa có video cho bài học này.</div>;

  return (
    <div className="mb-3">
      {url && (
        <video
          ref={videoRef}
          key={url}
          src={url}
          controls
          preload="metadata"
          className="w-100 rounded-3"
          aria-label="Video bài học"
          style={{ maxHeight: 320 }}
          onTimeUpdate={event => {
            const sec = Math.floor(event.currentTarget.currentTime || 0);
            setCurrentSeconds(sec);

            // Kiểm tra điểm dừng tương tác chưa hiển thị tại mốc giây hiện tại
            if (cuePoints.length > 0 && !activeInteractiveExam) {
              const matchingCue = cuePoints.find(
                cp => cp.videoTriggerSeconds != null && Math.floor(cp.videoTriggerSeconds) === sec && !triggeredCueIds.has(cp.id),
              );
              if (matchingCue) {
                videoRef.current?.pause();
                setTriggeredCueIds(prev => new Set(prev).add(matchingCue.id));
                void (async () => {
                  try {
                    const detail = await dispatch(getExamDetail({ courseId: target.courseId, examId: matchingCue.id })).unwrap();
                    if (detail.questions && detail.questions.length > 0) {
                      setActiveInteractiveExam(detail);
                      setSelectedOptionId(null);
                      setShowAnswerFeedback(false);
                    }
                  } catch {
                    /* ignore */
                  }
                })();
              }
            }
          }}
          onSeeked={event => {
            const seekSec = Math.floor(event.currentTarget.currentTime || 0);
            setCurrentSeconds(seekSec);
            // Cho phép kích hoạt lại các điểm dừng ở sau mốc được tua
            setTriggeredCueIds(prev => {
              const updated = new Set<number>();
              prev.forEach(id => {
                const cp = cuePoints.find(c => c.id === id);
                if (cp && cp.videoTriggerSeconds != null && Math.floor(cp.videoTriggerSeconds) < seekSec) {
                  updated.add(id);
                }
              });
              return updated;
            });
          }}
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

      <div className="d-flex flex-wrap gap-2 align-items-center mt-2">
        <Button size="sm" variant="outline-secondary" disabled={loading} onClick={load}>
          {loading ? 'Đang lấy URL…' : 'Xem / tải lại video'}
        </Button>
        {url && (
          <Button size="sm" variant="outline-danger" disabled={loading || disabled} onClick={handlePauseAndAddCue}>
            <FontAwesomeIcon icon={faPause} className="me-1" />
            Chèn câu hỏi tương tác tại {formatTimeMMSS(currentSeconds)}
          </Button>
        )}
        {measured?.source === storedUrl && durationSeconds == null && (
          <Button size="sm" variant="link" onClick={() => void saveDuration(measured.seconds)}>
            Lưu thời lượng {formatDuration(measured.seconds)}
          </Button>
        )}
      </div>

      {error && (
        <div className="text-danger small mt-2" role="alert">
          {error}
        </div>
      )}

      {/* Danh sách các câu hỏi Cue-point đã chèn (chỉ hiển thị khi đã mở xem video) */}
      {url && cuePoints.length > 0 && (
        <div className="mt-3 p-3 bg-light rounded-3 border">
          <div className="fw-semibold small text-dark mb-2">
            <FontAwesomeIcon icon={faVideo} className="text-danger me-1" />
            Điểm dừng tương tác trong video ({cuePoints.length}):
          </div>
          <div className="d-flex flex-column gap-2">
            {cuePoints.map(cp => (
              <div key={cp.id} className="p-2 px-3 bg-white rounded border d-flex justify-content-between align-items-center">
                <div className="d-flex align-items-center gap-2">
                  <Button
                    size="sm"
                    variant="danger"
                    className="py-0 px-2 font-monospace small"
                    title="Tua video đến thời điểm này"
                    onClick={() => {
                      if (videoRef.current && cp.videoTriggerSeconds != null) {
                        videoRef.current.currentTime = cp.videoTriggerSeconds;
                        void videoRef.current.play();
                      }
                    }}
                  >
                    <FontAwesomeIcon icon={faPlay} className="me-1" style={{ fontSize: 9 }} />
                    {formatTimeMMSS(cp.videoTriggerSeconds ?? 0)}
                  </Button>
                  <span className="small text-dark fw-medium">{cp.title}</span>
                </div>
                <div className="d-flex align-items-center gap-2">
                  <Button
                    size="sm"
                    variant="link"
                    className="text-info p-0"
                    title="Xem chi tiết câu hỏi"
                    onClick={async () => {
                      try {
                        const detail = await dispatch(getExamDetail({ courseId: target.courseId, examId: cp.id })).unwrap();
                        if (detail.questions?.length) {
                          setViewQuestion(detail.questions[0]);
                        } else {
                          toast.info('Điểm dừng này chưa có nội dung câu hỏi.');
                        }
                      } catch {
                        toast.error('Không thể tải chi tiết câu hỏi.');
                      }
                    }}
                  >
                    <FontAwesomeIcon icon={faEye} />
                  </Button>
                  <Button
                    size="sm"
                    variant="link"
                    className="text-primary p-0"
                    title="Chỉnh sửa câu hỏi"
                    disabled={disabled}
                    onClick={async () => {
                      try {
                        const detail = await dispatch(getExamDetail({ courseId: target.courseId, examId: cp.id })).unwrap();
                        if (detail.questions?.length) {
                          setEditingCueTarget({
                            examId: cp.id,
                            question: detail.questions[0],
                            triggerSeconds: cp.videoTriggerSeconds ?? 0,
                          });
                        } else {
                          toast.info('Điểm dừng này chưa có nội dung câu hỏi.');
                        }
                      } catch {
                        toast.error('Không thể tải chi tiết câu hỏi để sửa.');
                      }
                    }}
                  >
                    <FontAwesomeIcon icon={faPen} />
                  </Button>
                  <Button
                    size="sm"
                    variant="link"
                    className="text-danger p-0"
                    title="Xóa điểm dừng"
                    disabled={disabled}
                    onClick={() => setDeleteCueTarget(cp.id)}
                  >
                    <FontAwesomeIcon icon={faTrash} />
                  </Button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Modal soạn câu hỏi tương tác Cue-point mới */}
      <QuestionEditorModal
        show={cueModalOpen}
        title={`Chèn câu hỏi tương tác tại ${formatTimeMMSS(targetCueSeconds)}`}
        hideDifficulty
        hideScore
        onClose={() => setCueModalOpen(false)}
        onSave={handleSaveCueQuestion}
      />

      {/* Modal chỉnh sửa câu hỏi tương tác Cue-point */}
      <QuestionEditorModal
        show={Boolean(editingCueTarget)}
        title={`Chỉnh sửa câu hỏi tại ${formatTimeMMSS(editingCueTarget?.triggerSeconds ?? 0)}`}
        question={editingCueTarget?.question}
        hideDifficulty
        // hideExplanation
        hideScore
        onClose={() => setEditingCueTarget(null)}
        onSave={handleSaveEditCueQuestion}
      />

      {/* Modal xem chi tiết câu hỏi */}
      <QuestionDetailModal show={Boolean(viewQuestion)} question={viewQuestion} onClose={() => setViewQuestion(null)} />

      {/* Modal câu hỏi tương tác bật lên khi video phát đến cue-point */}
      <Modal
        show={Boolean(activeInteractiveExam)}
        onHide={() => {
          setActiveInteractiveExam(null);
          setSelectedOptionId(null);
          setShowAnswerFeedback(false);
          void videoRef.current?.play();
        }}
        centered
        backdrop="static"
      >
        <Modal.Header closeButton>
          <Modal.Title className="h6 fw-bold text-danger">
            <FontAwesomeIcon icon={faVideo} className="me-2" />
            {activeInteractiveExam?.title || 'Câu hỏi tương tác video'}
          </Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {activeInteractiveExam?.questions && activeInteractiveExam.questions.length > 0 ? (
            (() => {
              const q = activeInteractiveExam.questions[0];
              return (
                <div>
                  <div className="fw-semibold mb-3 text-dark">{q.content}</div>
                  <div className="d-flex flex-column gap-2">
                    {q.options?.map((opt, idx) => {
                      let optClass = 'p-2 px-3 border rounded text-start w-100 transition-all ';
                      if (showAnswerFeedback) {
                        if (opt.isCorrect) {
                          optClass += 'border-success bg-success-subtle text-success fw-bold';
                        } else if (selectedOptionId === (opt.id ?? idx)) {
                          optClass += 'border-danger bg-danger-subtle text-danger';
                        } else {
                          optClass += 'border-light-subtle text-muted opacity-75';
                        }
                      } else if (selectedOptionId === (opt.id ?? idx)) {
                        optClass += 'border-primary bg-primary-subtle text-primary fw-medium';
                      } else {
                        optClass += 'border-secondary-subtle bg-white text-dark';
                      }

                      return (
                        <button
                          key={opt.id ?? idx}
                          type="button"
                          className={optClass}
                          disabled={showAnswerFeedback}
                          onClick={() => setSelectedOptionId(opt.id ?? idx)}
                          style={{ cursor: showAnswerFeedback ? 'default' : 'pointer' }}
                        >
                          <div className="d-flex align-items-center justify-content-between">
                            <span>
                              <strong className="me-2">{String.fromCharCode(65 + idx)}.</strong>
                              {opt.content}
                            </span>
                            {showAnswerFeedback && opt.isCorrect && <FontAwesomeIcon icon={faCheck} className="text-success ms-2" />}
                          </div>
                        </button>
                      );
                    })}
                  </div>

                  {showAnswerFeedback && (
                    <>
                      <div
                        className={`alert ${
                          q.options?.find((o, idx) => (o.id ?? idx) === selectedOptionId)?.isCorrect ? 'alert-success' : 'alert-danger'
                        } py-2 px-3 mt-3 mb-0 small`}
                      >
                        {q.options?.find((o, idx) => (o.id ?? idx) === selectedOptionId)?.isCorrect ? (
                          <strong>Chính xác! Bạn đã chọn đáp án đúng.</strong>
                        ) : (
                          <strong>Chưa chính xác! Hãy quan sát đáp án đúng được tô màu xanh.</strong>
                        )}
                      </div>

                      {q.explanation && (
                        <div className="p-2 px-3 mt-2 bg-info-subtle text-info-emphasis rounded-3 border border-info-subtle small">
                          <div className="fw-bold mb-1">
                            <FontAwesomeIcon icon={faInfoCircle} className="me-1" />
                            Giải thích đáp án:
                          </div>
                          <div style={{ whiteSpace: 'pre-line' }}>{q.explanation}</div>
                        </div>
                      )}
                    </>
                  )}
                </div>
              );
            })()
          ) : (
            <p className="text-muted">Không có câu hỏi tương tác nào tại điểm dừng này.</p>
          )}
        </Modal.Body>
        <Modal.Footer>
          {!showAnswerFeedback ? (
            <Button variant="primary" size="sm" disabled={selectedOptionId === null} onClick={() => setShowAnswerFeedback(true)}>
              Kiểm tra đáp án
            </Button>
          ) : null}
          <Button
            variant={showAnswerFeedback ? 'success' : 'secondary'}
            size="sm"
            onClick={() => {
              setActiveInteractiveExam(null);
              setSelectedOptionId(null);
              setShowAnswerFeedback(false);
              void videoRef.current?.play();
            }}
          >
            Tiếp tục xem video
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Modal xác nhận xóa điểm dừng */}
      <Modal show={Boolean(deleteCueTarget)} onHide={() => setDeleteCueTarget(null)} centered>
        <Modal.Header closeButton>
          <Modal.Title className="h6 fw-bold">Xác nhận xóa điểm dừng video</Modal.Title>
        </Modal.Header>
        <Modal.Body>Bạn có chắc muốn xóa điểm dừng tương tác này khỏi video không?</Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" size="sm" onClick={() => setDeleteCueTarget(null)}>
            Hủy
          </Button>
          <Button variant="danger" size="sm" onClick={handleDeleteCue}>
            Xác nhận xóa
          </Button>
        </Modal.Footer>
      </Modal>
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

// Component LessonMedia
export const LessonMedia = ({ lessonId, disabled = false }: { lessonId: number; disabled?: boolean }) => {
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

  // State quản lý Modal xem trước tài liệu
  const [previewMaterial, setPreviewMaterial] = useState<IMaterialPreviewState>({
    isOpen: false,
    name: '',
    fileType: '',
    url: '',
    loading: false,
    error: '',
  });

  // Hàm xử lý khi bấm nút "Xem tài liệu"
  const handlePreviewMaterial = async (
    target: ILessonMediaTarget,
    material: { displayName?: string; fileName?: string; fileType?: string },
  ) => {
    const name = material.displayName || material.fileName || 'Tài liệu';
    const fileType = material.fileType || (name.toLowerCase().endsWith('.pdf') ? 'PDF' : 'DOCX');

    setPreviewMaterial({
      isOpen: true,
      name,
      fileType,
      url: '',
      loading: true,
      error: '',
    });

    try {
      const access = await getLessonMediaAccess(target, true);
      if (access && access.url) {
        setPreviewMaterial(prev => ({
          ...prev,
          url: access.url,
          loading: false,
        }));
      } else {
        throw new Error('Không nhận được URL xem trước');
      }
    } catch (failure) {
      console.error('Lỗi khi lấy liên kết xem trước:', failure);
      setPreviewMaterial(prev => ({
        ...prev,
        loading: false,
        error: 'Không thể lấy liên kết xem trước tài liệu. Vui lòng thử lại hoặc tải xuống.',
      }));
    }
  };

  return (
    <>
      {/* Danh sách tài liệu bổ sung */}
      <Card className="p-4 rounded-4 border-0 shadow-sm">
        <div className="d-flex justify-content-between align-items-center mb-3">
          <div>
            <h5 className="fw-bold mb-0">Tài liệu bài học</h5>
            <span className="small text-muted">Hỗ trợ PDF hoặc DOCX (tối đa 255 ký tự tên tệp)</span>
          </div>
          <MediaUpload target={materialTarget} disabled={disabled} label="Thêm tài liệu" ariaLabel="Tải tài liệu PDF / DOCX" />
        </div>
        <div className="d-flex flex-column gap-2">
          {node.materials.length === 0 && <div className="text-muted">Chưa có tài liệu đính kèm.</div>}
          {node.materials.map((material, index) => {
            const target: ILessonMediaTarget = { ...materialTarget, materialId: material.id };
            const name = material.displayName || material.fileName || 'Tài liệu';
            return (
              <div key={material.id ?? index} className="p-3 border rounded-3 d-flex flex-column gap-2 bg-light">
                <div className="d-flex justify-content-between align-items-start gap-2">
                  <div>
                    <div className="fw-semibold text-break">{name}</div>
                    <div className="small text-muted">
                      {material.fileName} • {fileSize(material.fileSizeBytes)}
                    </div>
                  </div>
                  <div className="d-flex gap-1">
                    <Button
                      size="sm"
                      variant="outline-secondary"
                      disabled={disabled || index === 0}
                      aria-label={`Đưa ${name} lên`}
                      onClick={() => void moveMaterial(index, -1)}
                    >
                      ↑
                    </Button>
                    <Button
                      size="sm"
                      variant="outline-secondary"
                      disabled={disabled || index === node.materials.length - 1}
                      aria-label={`Đưa ${name} xuống`}
                      onClick={() => void moveMaterial(index, 1)}
                    >
                      ↓
                    </Button>
                  </div>
                </div>
                <div className="d-flex flex-wrap gap-2">
                  {/* Nút Xem tài liệu */}
                  <Button size="sm" variant="outline-info" disabled={disabled} onClick={() => void handlePreviewMaterial(target, material)}>
                    Xem tài liệu
                  </Button>
                  {/* Nút Thay thế tài liệu */}
                  <MediaUpload target={target} disabled={disabled} label="Thay thế" ariaLabel={name} />
                  <Button size="sm" variant="outline-secondary" disabled={disabled} onClick={() => void downloadMedia(target)}>
                    Tải xuống
                  </Button>
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
        </div>
      </Card>

      {/* Card video bài học  */}
      <Card className="p-4 rounded-4 border-0 shadow-sm">
        <h5 className="fw-bold mb-3">Video bài học</h5>
        <VideoPreview
          target={videoTarget}
          storedUrl={node.lesson.videoUrl ?? ''}
          durationSeconds={node.lesson.durationSeconds}
          disabled={disabled}
        />
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

      {/* Modal đổi tên */}
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

      {/* Modal Xem trước tài liệu */}
      <MaterialPreviewModal
        preview={previewMaterial}
        onClose={() => setPreviewMaterial(prev => ({ ...prev, isOpen: false }))}
        onDownload={() => {
          if (previewMaterial.url) {
            window.open(previewMaterial.url, '_blank');
          }
        }}
      />
    </>
  );
};

export default LessonMedia;
