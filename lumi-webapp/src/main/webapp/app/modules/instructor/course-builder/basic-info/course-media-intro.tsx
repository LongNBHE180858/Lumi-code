import { toast } from 'react-toastify';
import { builderErrorText } from '../course-builder-error';
import { savedPricing } from '../pricing/saved-pricing';
import React, { useRef, useState, useEffect } from 'react';
import { Card, Button } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faImage, faPlay, faTrash, faUpload, faUserTie, faCheck, faCircleExclamation } from '@fortawesome/free-solid-svg-icons';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import {
  uploadThumbnail,
  deleteThumbnail,
  uploadIntroVideo,
  deleteIntroVideo,
  getThumbnailAccessUrl,
  getIntroVideoPlaybackUrl,
} from '../course-builder.reducer';

interface BasicInfoProgress {
  title: string;
  subjectId?: number;
  gradeId?: number;
}
interface CourseMediaIntroProps {
  basicInfo: BasicInfoProgress;
  infoDirty: boolean;
}

// A blob URL previews the local file only; it does not mean the file is saved.
const useLocalPreview = (file: File | null) => {
  const [preview, setPreview] = useState<{ file: File; url: string } | null>(null);
  useEffect(() => {
    if (!file) {
      setPreview(null);
      return;
    }
    const url = URL.createObjectURL(file);
    setPreview({ file, url });
    return () => URL.revokeObjectURL(url);
  }, [file]);
  return preview?.file === file ? preview?.url : null;
};

const mediaErrorMessage = (error: unknown, fallback: string) => {
  return builderErrorText(error, fallback);
};

const mediaPlaceholder = (isThumbnail: boolean, loading: boolean, storedUrl?: string) => {
  if (loading) return 'Đang tải file đã lưu...';
  if (storedUrl) return 'Có file đã lưu — chưa xem được';
  return isThumbnail ? 'Chưa có ảnh bìa' : 'Chưa có video giới thiệu';
};

// Both media cards follow the same steps: choose -> preview -> explicitly upload.
const CourseMediaCard = ({ kind }: { kind: 'thumbnail' | 'intro' }) => {
  const isThumbnail = kind === 'thumbnail';
  const dispatch = useAppDispatch();
  const { data: builderData, saving } = useAppSelector(state => state.courseBuilder);
  const course = builderData?.course;
  const storedUrl = isThumbnail ? course?.thumbnailUrl : course?.introVideoUrl;
  const getAccess = isThumbnail ? getThumbnailAccessUrl : getIntroVideoPlaybackUrl;
  const inputRef = useRef<HTMLInputElement>(null);
  const operationInProgressRef = useRef(false); // chống click đúp nhiều lần
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const localUrl = useLocalPreview(selectedFile);
  const [savedPreview, setSavedPreview] = useState<{ storedUrl: string; url: string } | null>(null);
  const [accessLoading, setAccessLoading] = useState(false);
  const [accessError, setAccessError] = useState<string | null>(null);
  const [sourceError, setSourceError] = useState<string | null>(null);
  const [retry, setRetry] = useState(0);

  useEffect(() => {
    let ignore = false;
    setSavedPreview(null);
    setAccessError(null);
    setSourceError(null);
    if (!course?.id || !storedUrl) {
      setAccessLoading(false);
      return;
    }
    setAccessLoading(true);
    getAccess(course.id)
      .then(result => {
        if (!ignore) setSavedPreview({ storedUrl, url: result.url });
      })
      .catch(error => {
        if (!ignore) {
          const message = mediaErrorMessage(error, 'File đã lưu nhưng chưa lấy được URL xem. Bạn có thể thử tải lại.');
          setAccessError(message);
          toast.error(message);
        }
      })
      .finally(() => {
        if (!ignore) setAccessLoading(false);
      });
    return () => {
      ignore = true;
    };
  }, [course?.id, storedUrl, getAccess, retry]);

  const savedUrl = savedPreview?.storedUrl === storedUrl ? savedPreview?.url : null;
  const displayedUrl = localUrl || savedUrl;

  const reportSourceError = (message: string) => {
    setSourceError(message);
    toast.error(message, { toastId: `course-media-${kind}-${displayedUrl}` });
  };

  const handleChoose = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    event.target.value = '';
    if (!file) return;
    const valid = isThumbnail ? /\.(png|jpe?g)$/i.test(file.name) : /\.mp4$/i.test(file.name);
    if (!valid) {
      toast.error(isThumbnail ? 'Chọn ảnh PNG hoặc JPG.' : 'Chọn video MP4.');
      return;
    }
    setSelectedFile(file);
    setSourceError(null);
  };

  const handleUpload = async () => {
    if (saving || operationInProgressRef.current || !selectedFile) return;
    if (!course?.id) {
      toast.error('Hãy lưu nháp thông tin để tạo khóa học trước khi upload.');
      return;
    }
    operationInProgressRef.current = true;
    try {
      const args = { courseId: course.id, version: course.version, file: selectedFile };
      if (isThumbnail) await dispatch(uploadThumbnail(args)).unwrap();
      else await dispatch(uploadIntroVideo(args)).unwrap();
      setSelectedFile(null);
      toast.success(isThumbnail ? 'Upload ảnh bìa thành công.' : 'Upload video giới thiệu thành công.');
    } catch (error) {
      toast.error(
        mediaErrorMessage(error, 'Upload không thành công. File đã lưu trước đó được giữ nguyên; file vừa chọn vẫn có thể xem trước.'),
      );
    } finally {
      operationInProgressRef.current = false;
    }
  };

  const handleRemoveSaved = async () => {
    if (saving || operationInProgressRef.current || !course?.id) return;
    operationInProgressRef.current = true;
    try {
      const args = { courseId: course.id, version: course.version };
      if (isThumbnail) await dispatch(deleteThumbnail(args)).unwrap();
      else await dispatch(deleteIntroVideo(args)).unwrap();
      toast.success('Đã gỡ file đã lưu.');
    } catch (error) {
      toast.error(mediaErrorMessage(error, 'Không gỡ được file đã lưu. Vui lòng thử lại.'));
    } finally {
      operationInProgressRef.current = false;
    }
  };

  return (
    <Card className="p-4 rounded-4 border-0 shadow-sm">
      <h5 className="fw-bold mb-3">{isThumbnail ? 'Ảnh bìa khóa học' : 'Video giới thiệu'}</h5>
      <input
        type="file"
        ref={inputRef}
        accept={isThumbnail ? 'image/png,image/jpeg' : 'video/mp4'}
        className="d-none"
        onChange={handleChoose}
        disabled={saving}
        aria-label={isThumbnail ? 'Chọn ảnh bìa' : 'Chọn video giới thiệu'}
      />
      <div
        className="rounded-4 d-flex flex-column justify-content-center align-items-center mb-3 text-center overflow-hidden"
        style={{ height: '180px', backgroundColor: '#E6F5FF', border: '1px dashed #24A8E8' }}
      >
        {displayedUrl ? (
          isThumbnail ? (
            <img
              src={displayedUrl}
              alt="Ảnh bìa khóa học"
              style={{ width: '100%', height: '100%', objectFit: 'cover' }}
              onError={() => reportSourceError('Không hiển thị được ảnh. Nếu là ảnh đã lưu, hãy thử lấy lại URL xem.')}
            />
          ) : (
            <video
              key={displayedUrl}
              src={displayedUrl}
              controls
              preload="metadata"
              aria-label="Video giới thiệu"
              style={{ width: '100%', height: '100%', objectFit: 'contain' }}
              onError={() =>
                reportSourceError('Không phát được video. Kiểm tra định dạng MP4 tương thích trình duyệt hoặc thử lấy lại URL phát.')
              }
            />
          )
        ) : (
          <>
            <div className="bg-white p-3 rounded-circle shadow-sm mb-2 text-primary">
              <FontAwesomeIcon icon={isThumbnail ? faImage : faPlay} />
            </div>
            <span className="fw-bold text-primary small">{mediaPlaceholder(isThumbnail, accessLoading, storedUrl)}</span>
          </>
        )}
      </div>
      {selectedFile && (
        <div className="small text-warning mb-2">{selectedFile.name} • Xem trước từ máy, chưa upload. Lưu nháp không lưu file này.</div>
      )}
      {!selectedFile && storedUrl && !accessError && !sourceError && (
        <div className="small text-success mb-2">File đã được lưu trên cloud.</div>
      )}
      <div className="d-flex flex-wrap gap-2">
        <Button
          type="button"
          variant="outline-primary"
          size="sm"
          className="rounded-pill px-3"
          onClick={() => inputRef.current?.click()}
          disabled={saving}
        >
          <FontAwesomeIcon icon={faUpload} className="me-1" /> {isThumbnail ? 'Chọn ảnh từ máy' : 'Chọn video MP4'}
        </Button>
        {selectedFile && (
          <>
            <Button type="button" variant="primary" size="sm" className="rounded-pill px-3" onClick={handleUpload} disabled={saving}>
              {saving ? 'Đang xử lý...' : 'Upload file đã chọn'}
            </Button>
            <Button
              type="button"
              variant="outline-secondary"
              size="sm"
              className="rounded-pill px-3"
              disabled={saving}
              onClick={() => {
                setSelectedFile(null);
                setSourceError(null);
              }}
            >
              Bỏ file vừa chọn
            </Button>
          </>
        )}
        {storedUrl && (
          <Button
            type="button"
            variant="outline-danger"
            size="sm"
            className="rounded-pill px-3"
            onClick={handleRemoveSaved}
            disabled={saving}
          >
            <FontAwesomeIcon icon={faTrash} className="me-1" /> Gỡ file đã lưu
          </Button>
        )}
        {!selectedFile && storedUrl && (accessError || sourceError) && (
          <Button
            type="button"
            variant="outline-secondary"
            size="sm"
            disabled={saving || accessLoading}
            onClick={() => setRetry(value => value + 1)}
          >
            Tải lại file đã lưu
          </Button>
        )}
      </div>
      {!course?.id && (
        <p className="small text-muted mt-2 mb-0">Bạn có thể xem trước ngay. Hãy lưu nháp thông tin trước khi upload lên cloud.</p>
      )}
      <p className="small text-muted mt-2 mb-0">
        {isThumbnail
          ? 'Ảnh bìa hỗ trợ PNG/JPG.'
          : 'Video tùy chọn, không tính vào tổng video bài học. MP4 cần codec tương thích trình duyệt.'}
      </p>
    </Card>
  );
};

// 3. Thông tin giảng viên phụ trách
const InstructorInfoCard = () => {
  const account = useAppSelector(state => state.authentication.account);
  const instructorName = [account?.firstName, account?.lastName].filter(Boolean).join(' ') || account?.login || 'Giảng viên';
  const instructorEmail = account?.email || account?.login || '';

  return (
    <Card className="p-4 rounded-4 border-0 shadow-sm">
      <h5 className="fw-bold mb-3">Giảng viên phụ trách</h5>
      <div className="d-flex align-items-center gap-3 mb-2">
        <div className="rounded-circle bg-light p-3 border text-secondary">
          <FontAwesomeIcon icon={faUserTie} size="lg" />
        </div>
        <div>
          <div className="fw-bold small">{instructorName}</div>
          <div className="text-muted" style={{ fontSize: '12px' }}>
            {instructorEmail}
          </div>
        </div>
      </div>
      <p className="text-muted small mb-0">Khóa học được tạo và chịu trách nhiệm bởi tài khoản của bạn.</p>
    </Card>
  );
};

// 4. Checklist tiến độ bản nháp
interface ChecklistItemProps {
  isValid: boolean;
  text: string;
  inactiveClass?: string;
}

const ChecklistItem = ({ isValid, text, inactiveClass = 'text-secondary' }: ChecklistItemProps) => (
  <div className={`d-flex align-items-center gap-2 fw-semibold ${isValid ? 'text-success' : inactiveClass}`}>
    <FontAwesomeIcon icon={isValid ? faCheck : faCircleExclamation} />
    <span>{text}</span>
  </div>
);

const formatCurriculumText = (isValid: boolean, chapters: number, lessons: number, materials: number): string => {
  if (isValid) {
    return `${chapters} chương • ${lessons} bài học (${materials} tài liệu)`;
  }
  return 'Chưa có chương hoặc bài học';
};

const CourseDraftProgressCard = ({ basicInfo, infoDirty }: CourseMediaIntroProps) => {
  const { data: builderData } = useAppSelector(state => state.courseBuilder);
  const course = builderData?.course;

  const isBasicInfoValid = Boolean(basicInfo.title.trim() && basicInfo.subjectId && basicInfo.gradeId);

  const totalChapters = builderData?.chapters?.length || 0;
  const totalLessons = builderData?.totalLessons || 0;
  const totalMaterials = builderData?.totalMaterials || 0;
  const isCurriculumValid = totalChapters > 0 && totalLessons > 0;

  const pricing = savedPricing(course);
  const isThumbnailReady = Boolean(course?.thumbnailUrl);

  return (
    <Card className="p-4 rounded-4 border-0 shadow-sm" style={{ backgroundColor: '#F8FAFC' }}>
      <h5 className="fw-bold mb-3">Tiến độ bản nháp</h5>
      <div className="d-flex flex-column gap-3 small">
        <ChecklistItem isValid={isBasicInfoValid} text={isBasicInfoValid ? 'Đã điền tên, môn và lớp' : 'Chưa hoàn tất tên, môn hoặc lớp'} />
        <ChecklistItem
          isValid={isCurriculumValid}
          text={formatCurriculumText(isCurriculumValid, totalChapters, totalLessons, totalMaterials)}
        />
        <ChecklistItem isValid={pricing.valid} text={pricing.checklistText} />
        <ChecklistItem
          isValid={isThumbnailReady}
          text={isThumbnailReady ? 'Ảnh bìa đã sẵn sàng' : 'Chưa có ảnh bìa'}
          inactiveClass="text-muted"
        />
      </div>
      {infoDirty && <div className="text-warning small mt-3">Thông tin vừa nhập chưa được lưu.</div>}
      <hr className="my-3" />
      <span className="text-muted" style={{ fontSize: '11px' }}>
        Tiếp tục chỉnh sửa ở các mục bên cạnh. Lưu nháp không tự động gửi duyệt và không xuất bản khóa học.
      </span>
    </Card>
  );
};

export const CourseMediaIntro = ({ basicInfo, infoDirty }: CourseMediaIntroProps) => {
  return (
    <div className="d-flex flex-column gap-4">
      <CourseMediaCard kind="thumbnail" />
      <CourseMediaCard kind="intro" />
      <InstructorInfoCard />
      <CourseDraftProgressCard basicInfo={basicInfo} infoDirty={infoDirty} />
    </div>
  );
};

export default CourseMediaIntro;
