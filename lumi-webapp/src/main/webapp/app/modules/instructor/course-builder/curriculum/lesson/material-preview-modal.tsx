import React, { useEffect, useState } from 'react';
import { Button, Form, Modal, Spinner } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCheck, faRobot, faSave, faTimes } from '@fortawesome/free-solid-svg-icons';
import { toast } from 'react-toastify';
import { useAppDispatch } from 'app/config/store';
import { summarizeLessonMaterial, saveMaterialSummary, ILessonMaterialSummaryDTO } from '../../course-builder.reducer';

export interface IMaterialPreviewState {
  isOpen: boolean;
  name: string;
  fileType: string;
  url: string;
  loading: boolean;
  error: string;
  materialId?: number;
  initialSummary?: string;
  showSummaryPanel?: boolean;
}

export const MaterialPreviewModal = ({
  preview,
  courseId,
  lessonId,
  courseVersion,
  onClose,
  onDownload,
}: {
  preview: IMaterialPreviewState;
  courseId?: number;
  lessonId?: number;
  courseVersion?: number;
  onClose: () => void;
  onDownload: () => void;
}) => {
  const dispatch = useAppDispatch();
  const [showSummary, setShowSummary] = useState(false);
  const [summaryText, setSummaryText] = useState('');
  const [aiSuggestion, setAiSuggestion] = useState<string | null>(null);
  const [aiClassification, setAiClassification] = useState<ILessonMaterialSummaryDTO['classification']>(null);
  const [isSummarizing, setIsSummarizing] = useState(false);
  const [isSaving, setIsSaving] = useState(false);

  useEffect(() => {
    if (preview.isOpen) {
      setSummaryText(preview.initialSummary || '');
      setShowSummary(Boolean(preview.showSummaryPanel || preview.initialSummary));
      setAiSuggestion(null);
      setAiClassification(null);
    }
  }, [preview.isOpen, preview.materialId, preview.initialSummary, preview.showSummaryPanel]);

  const isPdf = preview.fileType?.toUpperCase() === 'PDF' || preview.name?.toLowerCase().endsWith('.pdf');

  const handleAiSummarize = async () => {
    if (!preview.materialId) return;
    setIsSummarizing(true);
    try {
      const data = await summarizeLessonMaterial(preview.materialId, true);
      setAiClassification(data.classification || null);
      if (!summaryText.trim()) {
        setSummaryText(data.summary);
        setAiSuggestion(null);
        toast.success('Đã tạo bản tóm tắt AI thành công!');
      } else {
        setAiSuggestion(data.summary);
        toast.info('AI đã hoàn thành tóm tắt. Bạn có thể đối chiếu và chọn Áp dụng bên dưới.');
      }
    } catch (err: any) {
      console.error('Lỗi tóm tắt AI:', err);
      const msg = err.response?.data?.summary || err.response?.data?.message || err.message || 'Không thể tạo bản tóm tắt AI.';
      toast.error(msg);
    } finally {
      setIsSummarizing(false);
    }
  };

  const handleApplyAi = () => {
    if (aiSuggestion) {
      setSummaryText(aiSuggestion);
      setAiSuggestion(null);
      toast.info('Đã áp dụng kết quả tóm tắt từ AI vào ô soạn thảo.');
    }
  };

  const handleDiscardAi = () => {
    setAiSuggestion(null);
    toast.info('Đã xóa bỏ kết quả gợi ý.');
  };

  const handleSaveSummary = async () => {
    if (!courseId || !lessonId || !preview.materialId || courseVersion === undefined) {
      toast.error('Thiếu thông tin khóa học hoặc tài liệu để lưu.');
      return;
    }
    setIsSaving(true);
    try {
      await dispatch(
        saveMaterialSummary({
          courseId,
          lessonId,
          materialId: preview.materialId,
          version: courseVersion,
          summary: summaryText,
        }),
      ).unwrap();
      toast.success('Đã lưu nội dung tóm tắt thành công!');
    } catch (err: any) {
      console.error('Lỗi khi lưu tóm tắt:', err);
      toast.error('Lưu tóm tắt thất bại. Vui lòng thử lại.');
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <Modal show={preview.isOpen} onHide={onClose} size="xl" centered backdrop="static" style={{ zIndex: 1060 }}>
      <Modal.Header closeButton>
        <div className="d-flex align-items-center justify-content-between w-100 me-3">
          <Modal.Title className="text-truncate" style={{ maxWidth: '65%' }}>
            {preview.name || 'Xem trước tài liệu'}
          </Modal.Title>
          <Button
            variant={showSummary ? 'outline-primary' : summaryText ? 'outline-success' : 'primary'}
            size="sm"
            onClick={() => setShowSummary(prev => !prev)}
          >
            <FontAwesomeIcon icon={faRobot} className="me-1" />
            {showSummary ? 'Ẩn khung tóm tắt' : summaryText ? 'Xem tóm tắt' : 'Tóm tắt'}
          </Button>
        </div>
      </Modal.Header>
      <Modal.Body style={{ minHeight: '520px', maxHeight: '85vh', overflowY: 'auto' }} className="p-3">
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

        {!preview.loading && !preview.error && preview.url && (
          <div className="row g-3">
            {/* Cột tài liệu */}
            <div className={showSummary ? 'col-12 col-lg-7' : 'col-12'}>
              <div className="border rounded bg-light p-1 h-100" style={{ minHeight: '650px' }}>
                {isPdf ? (
                  <iframe
                    src={preview.url}
                    title={preview.name}
                    width="100%"
                    height="100%"
                    style={{ minHeight: '650px', border: 'none', borderRadius: '4px' }}
                  />
                ) : (
                  <iframe
                    src={`https://view.officeapps.live.com/op/embed.aspx?src=${encodeURIComponent(preview.url)}`}
                    title={preview.name}
                    width="100%"
                    height="100%"
                    style={{ minHeight: '650px', border: 'none', borderRadius: '4px' }}
                  />
                )}
              </div>
            </div>

            {/* Cột tóm tắt tài liệu (Hiển thị song song) */}
            {showSummary && (
              <div className="col-12 col-lg-5">
                <div className="border rounded p-3 bg-white h-100 d-flex flex-column" style={{ minHeight: '650px' }}>
                  <div className="d-flex justify-content-between align-items-center mb-2 pb-2 border-bottom">
                    <span className="fw-bold text-dark">
                      <FontAwesomeIcon icon={faRobot} className="text-primary me-2" />
                      Tóm tắt tài liệu
                    </span>
                    <div className="d-flex gap-2">
                      <Button size="sm" variant="outline-primary" onClick={handleAiSummarize} disabled={isSummarizing || isSaving}>
                        {isSummarizing ? (
                          <>
                            <Spinner animation="border" size="sm" className="me-1" />
                            Đang tóm tắt...
                          </>
                        ) : (
                          <>
                            <FontAwesomeIcon icon={faRobot} className="me-1" />
                            Tóm tắt bằng AI
                          </>
                        )}
                      </Button>
                      <Button size="sm" variant="success" onClick={handleSaveSummary} disabled={isSaving || isSummarizing}>
                        {isSaving ? (
                          <>
                            <Spinner animation="border" size="sm" className="me-1" />
                            Đang lưu...
                          </>
                        ) : (
                          <>
                            <FontAwesomeIcon icon={faSave} className="me-1" />
                            Lưu
                          </>
                        )}
                      </Button>
                    </div>
                  </div>

                  {aiClassification && (
                    <div className="mb-2 p-2 bg-light rounded border d-flex flex-wrap gap-2 align-items-center small">
                      <span className="badge bg-primary">
                        {aiClassification.subject === 'TOAN'
                          ? 'Toán'
                          : aiClassification.subject === 'LICH_SU'
                            ? 'Lịch sử'
                            : aiClassification.subject}
                      </span>
                      {Boolean(aiClassification.grade) && <span className="badge bg-success">Lớp {aiClassification.grade}</span>}
                      {aiClassification.topic && <span className="text-secondary fw-semibold">Chủ đề: {aiClassification.topic}</span>}
                    </div>
                  )}

                  <Form.Group className="mb-2 flex-grow-1 d-flex flex-column">
                    <Form.Label className="small text-muted mb-1">Nội dung tóm tắt bài học (giáo viên có thể chỉnh sửa tự do):</Form.Label>
                    <Form.Control
                      as="textarea"
                      rows={12}
                      className="flex-grow-1"
                      placeholder="Nhập nội dung tóm tắt hoặc bấm 'Tóm tắt bằng AI' để tự động tạo..."
                      value={summaryText}
                      onChange={e => setSummaryText(e.target.value)}
                      style={{ fontSize: '14px', lineHeight: '1.6', resize: 'vertical' }}
                    />
                  </Form.Group>

                  {/* Khung đối chiếu khi có gợi ý từ AI */}
                  {aiSuggestion && (
                    <div className="p-3 bg-light border border-info rounded-3 mt-2 shadow-sm">
                      <div className="d-flex justify-content-between align-items-center mb-2">
                        <span className="fw-bold text-primary small">✨ Kết quả gợi ý từ AI (đối chiếu):</span>
                        <div className="d-flex gap-1">
                          <Button size="sm" variant="success" onClick={handleApplyAi} className="py-0 px-2" style={{ fontSize: '12px' }}>
                            <FontAwesomeIcon icon={faCheck} className="me-1" />
                            Áp dụng
                          </Button>
                          <Button
                            size="sm"
                            variant="outline-danger"
                            onClick={handleDiscardAi}
                            className="py-0 px-2"
                            style={{ fontSize: '12px' }}
                          >
                            <FontAwesomeIcon icon={faTimes} className="me-1" />
                            Xóa bỏ
                          </Button>
                        </div>
                      </div>
                      <div
                        className="bg-white p-2 rounded border small text-dark"
                        style={{ maxHeight: '180px', overflowY: 'auto', whiteSpace: 'pre-line', lineHeight: '1.5' }}
                      >
                        {aiSuggestion}
                      </div>
                      <div className="text-muted small mt-1 fst-italic" style={{ fontSize: '11px' }}>
                        * Bấm &ldquo;Áp dụng&rdquo; để ghi đè vào ô tóm tắt của bạn hoặc &ldquo;Xóa bỏ&rdquo; để giữ nguyên tóm tắt hiện
                        tại.
                      </div>
                    </div>
                  )}
                </div>
              </div>
            )}
          </div>
        )}
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

export default MaterialPreviewModal;
