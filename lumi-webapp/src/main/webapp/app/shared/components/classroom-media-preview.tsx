import React, { useEffect, useState } from 'react';
import axios from 'axios';
import {
  classroomError,
  getClassAssignmentFileAccess,
  getClassAssignmentSubmissionFileAccess,
  getClassMaterialAccess,
} from 'app/shared/util/classroom-api';

interface Props {
  classId: number;
  itemId: number;
  kind: 'lesson' | 'assignment' | 'submission';
  assignmentId?: number;
  fileType: string;
  title: string;
}

export const ClassroomMediaPreview: React.FC<Props> = ({ classId, itemId, kind, assignmentId, fileType, title }) => {
  const [source, setSource] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setSource(null);
    setError(null);
  }, [classId, itemId, kind, assignmentId]);

  useEffect(() => {
    return () => {
      if (source?.startsWith('blob:')) URL.revokeObjectURL(source);
    };
  }, [source]);

  useEffect(() => {
    if (!source) return;
    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape') setSource(null);
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [source]);

  const load = async () => {
    setLoading(true);
    setError(null);
    try {
      const access =
        kind === 'lesson'
          ? await getClassMaterialAccess(classId, itemId)
          : kind === 'assignment'
            ? await getClassAssignmentFileAccess(classId, itemId)
            : assignmentId != null
              ? await getClassAssignmentSubmissionFileAccess(classId, assignmentId, itemId)
              : null;
      if (!access) throw new Error('Thiếu thông tin tệp bài nộp.');
      const url = access.url.startsWith('/api/')
        ? URL.createObjectURL((await axios.get<Blob>(access.url, { responseType: 'blob' })).data)
        : access.url;
      setSource(url);
    } catch (cause) {
      setError(classroomError(cause));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="classroom-media-preview">
      <button type="button" className="classroom-secondary-action" disabled={loading} onClick={() => void load()}>
        {loading
          ? 'Đang mở...'
          : fileType === 'DOCX'
            ? 'Tải DOCX'
            : `Xem ${fileType === 'VIDEO' ? 'video' : fileType === 'IMAGE' ? 'ảnh' : 'PDF'}`}
      </button>
      {error && !source && (
        <p className="classroom-error" role="alert">
          {error}
        </p>
      )}
      {source && (
        <div
          className="classroom-media-modal-backdrop"
          role="dialog"
          aria-modal="true"
          aria-label={`Xem tài liệu: ${title}`}
          onClick={() => setSource(null)}
        >
          <div className="classroom-media-modal" onClick={event => event.stopPropagation()}>
            <div className="classroom-media-modal-header">
              <div className="classroom-media-modal-title">
                <span className={`classroom-media-type-pill${fileType === 'VIDEO' ? ' is-video' : ' is-pdf'}`}>
                  {fileType === 'VIDEO' ? '🎬 VIDEO' : fileType === 'IMAGE' ? '🖼️ ẢNH' : `📄 ${fileType}`}
                </span>
                <strong>{title}</strong>
              </div>
              <div className="classroom-media-preview-actions">
                <button type="button" className="classroom-secondary-action" disabled={loading} onClick={() => void load()}>
                  {loading ? 'Đang tải...' : 'Tải lại nội dung'}
                </button>
                <button type="button" className="classroom-outline-button" onClick={() => setSource(null)}>
                  ✕ Đóng
                </button>
              </div>
            </div>
            <div className={`classroom-media-modal-body${fileType === 'VIDEO' ? ' is-video' : ' is-pdf'}`}>
              {fileType === 'DOCX' ? (
                <a href={source} target="_blank" rel="noopener noreferrer">
                  Mở hoặc tải tệp DOCX
                </a>
              ) : fileType === 'IMAGE' ? (
                <img src={source} alt={title} />
              ) : fileType === 'VIDEO' ? (
                <video
                  key={source}
                  src={source}
                  controls
                  autoPlay
                  preload="metadata"
                  aria-label={title}
                  onError={() => setError('Không phát được video. Hãy tải lại nội dung.')}
                />
              ) : (
                <iframe key={source} src={source} title={title} onError={() => setError('Không mở được PDF. Hãy tải lại nội dung.')} />
              )}
            </div>
            {error && (
              <p className="classroom-error classroom-media-modal-error" role="alert">
                {error}
              </p>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default ClassroomMediaPreview;
