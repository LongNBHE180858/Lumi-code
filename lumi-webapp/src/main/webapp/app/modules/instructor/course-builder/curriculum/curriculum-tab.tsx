import React, { useImperativeHandle, useRef, useState } from 'react';
import { useStore } from 'react-redux';
import type { IRootState } from 'app/config/store';
import { Row, Col, Card, Button, Form, Modal } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faGripVertical,
  faPlus,
  faChevronRight,
  faChevronDown,
  faArrowLeft,
  faArrowRight,
  faPen,
  faTrash,
  faRightLeft,
} from '@fortawesome/free-solid-svg-icons';

import LessonEditor, { ILessonData } from './lesson-editor';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { toast } from 'react-toastify';
import { builderErrorText } from '../course-builder-error';
import { isBuilderReadOnly } from '../course-lifecycle';
import { chapterDuration, formatDuration, lessonSeconds } from './video-duration';
import {
  createChapter,
  updateChapter,
  deleteChapter,
  reorderCurriculum,
  createLesson,
  updateLesson,
  deleteLesson,
  getCourseBuilder,
  moveLesson,
} from '../course-builder.reducer';

interface CurriculumTabProps {
  onBack: () => void;
  onNext: () => void;
  navigationRef?: React.Ref<CurriculumNavigation>;
}

export interface CurriculumNavigation {
  requestLeave: (action: () => void) => void;
}

interface IChapterItem {
  id: number;
  title: string;
  totalLessons: number;
  durationText: string;
  lessons: ILessonData[];
}

// Tự động sinh tên không trùng lặp (ví dụ: 'Chương mới' -> 'Chương mới (1)' -> 'Chương mới (2)')
const generateUniqueTitle = (baseTitle: string, existingTitles: string[]): string => {
  const trimmedBase = baseTitle.trim();
  const normalizedExisting = new Set(existingTitles.map(t => t.trim().toLowerCase()));

  if (!normalizedExisting.has(trimmedBase.toLowerCase())) {
    return trimmedBase;
  }

  const match = trimmedBase.match(/^(.*?)\s*\((\d+)\)$/);
  const root = match ? match[1].trim() : trimmedBase;
  let index = match ? parseInt(match[2], 10) + 1 : 1;

  while (true) {
    const suffix = ` (${index})`;
    const candidate = `${root.slice(0, 255 - suffix.length).trimEnd()}${suffix}`;
    if (!normalizedExisting.has(candidate.toLowerCase())) {
      return candidate;
    }
    index++;
  }
};

// Xác định bài đang chọn và trạng thái bản nháp của editor.
const getLessonEditorState = (chapter: IChapterItem | undefined, activeLessonId: number | null, draft: ILessonData | null) => {
  const currentLesson = chapter?.lessons.find(lesson => lesson.id === activeLessonId) ?? chapter?.lessons[0];
  const currentLessonIndex = chapter ? chapter.lessons.findIndex(lesson => lesson.id === currentLesson?.id) + 1 : 0;
  // Chỉ giữ các trường đang soạn; metadata media luôn lấy mới nhất từ BE.
  const editorLesson =
    draft && currentLesson && draft.id === currentLesson.id
      ? { ...currentLesson, title: draft.title, description: draft.description, isPreview: draft.isPreview }
      : currentLesson;
  const hasLessonChanges = Boolean(
    currentLesson &&
    editorLesson &&
    (editorLesson.title !== currentLesson.title ||
      editorLesson.description !== currentLesson.description ||
      editorLesson.isPreview !== currentLesson.isPreview),
  );
  const lessonTitleError = editorLesson && !editorLesson.title.trim() ? 'Vui lòng nhập tên bài học.' : '';

  return { currentLesson, currentLessonIndex, editorLesson, hasLessonChanges, lessonTitleError };
};

export const CurriculumTab = ({ onBack, onNext, navigationRef }: CurriculumTabProps) => {
  const dispatch = useAppDispatch();
  const store = useStore<IRootState>();

  const { data, loading, saving, errorMessage, errorStatus } = useAppSelector(state => state.courseBuilder);

  const [activeChapterId, setActiveChapterId] = useState<number | null>(null);
  const [activeLessonId, setActiveLessonId] = useState<number | null>(null);

  // null: không nhập, 'new': thêm chương, number: đổi tên chương
  const [editingChapter, setEditingChapter] = useState<number | 'new' | null>(null);
  const [chapterTitle, setChapterTitle] = useState('');

  const [chapterToDelete, setChapterToDelete] = useState<IChapterItem | null>(null);
  const [chapterBusy, setChapterBusy] = useState(false);
  const chapterRequestRef = useRef(false);

  const [lessonDraft, setLessonDraft] = useState<ILessonData | null>(null);
  const [lessonToDelete, setLessonToDelete] = useState<ILessonData | null>(null);
  const [lessonToMove, setLessonToMove] = useState<ILessonData | null>(null);
  const [moveChapterId, setMoveChapterId] = useState<number | null>(null);
  const [pendingAction, setPendingAction] = useState<(() => void) | null>(null);

  const lessonRequestRef = useRef(false);
  const controlsDisabled =
    saving || loading || isBuilderReadOnly(data) || chapterBusy || editingChapter !== null || [403, 404, 409].includes(errorStatus!);

  const formatFileSize = (bytes?: number | null) => {
    if (bytes == null) return 'Chưa có thông tin dung lượng';
    return `${(bytes / 1024 / 1024).toFixed(1)} MB`;
  };

  // Lưu vị trí (index) của chương đang được nhấc lên và vị trí đang rê chuột qua
  const [draggedIndex, setDraggedIndex] = useState<number | null>(null);
  const [dropPosition, setDropPosition] = useState<number | null>(null);
  const [draggedLesson, setDraggedLesson] = useState<{ chapterId: number; index: number } | null>(null);
  const [lessonDropPosition, setLessonDropPosition] = useState<number | null>(null);
  const [showAllChapters, setShowAllChapters] = useState(false);

  // Chuyển dữ liệu BE thành cấu trúc mà giao diện hiện tại đang dùng.
  const chapters: IChapterItem[] = (data?.chapters ?? []).map(node => ({
    id: node.chapter.id,
    title: node.chapter.title ?? '',
    totalLessons: node.lessons.length,
    durationText: chapterDuration(node.lessons.map(item => item.lesson)),
    lessons: node.lessons.map(({ lesson, materials }) => ({
      id: lesson.id,
      title: lesson.title ?? '',
      duration: lessonSeconds(lesson) != null ? formatDuration(lessonSeconds(lesson)!) : 'Chưa có thời lượng',
      description: lesson.description ?? '',
      videoFileName: lesson.videoFileName ?? '',
      videoSize: formatFileSize(lesson.videoSizeBytes),
      videoStatus: lesson.videoUrl ? 'Đã có video' : 'Chưa có video',
      isPreview: lesson.isPreview ?? false,
      attachments: materials.map(material => ({
        id: material.id!,
        name: material.displayName || material.fileName || 'Tài liệu',
        size: formatFileSize(material.fileSizeBytes),
        type: material.fileType || material.mimeType || 'Tài liệu',
        status: 'Đã tải lên',
      })),
    })),
  }));

  const currentChapter = chapters.find(chapter => chapter.id === activeChapterId) ?? chapters[0];
  const visibleChapters = showAllChapters ? chapters : chapters.slice(0, 6);

  const { currentLesson, currentLessonIndex, editorLesson, hasLessonChanges, lessonTitleError } = getLessonEditorState(
    currentChapter,
    activeLessonId,
    lessonDraft,
  );

  const requestAction = (action: () => void) => {
    // Lỗi quyền hoặc xung đột chỉ khóa ghi, vẫn cho phép rời trang và bỏ bản nháp.
    if (saving || loading || chapterBusy || editingChapter !== null || chapterRequestRef.current || lessonRequestRef.current) return;

    if (hasLessonChanges) {
      // Dùng hàm bọc vì React coi function truyền vào setter là updater.
      setPendingAction(() => action);
      return;
    }
    action();
  };

  useImperativeHandle(navigationRef, () => ({ requestLeave: requestAction }));

  const selectLesson = (chapterId: number, lessonId: number | null) => {
    setLessonDraft(null);
    setActiveChapterId(chapterId);
    setActiveLessonId(lessonId);
  };
  // Kiểm tra tên chương hiện tại có bị trùng lặp với các chương khác không
  const effectiveChapterTitle =
    chapterTitle.trim() || (editingChapter === 'new' ? 'Chương mới' : (chapters.find(c => c.id === editingChapter)?.title ?? 'Chương mới'));

  const otherChapterTitles = chapters
    .filter(chapter => editingChapter === 'new' || chapter.id !== editingChapter)
    .map(chapter => chapter.title);

  const isDuplicateTitle = Boolean(
    editingChapter !== null &&
    effectiveChapterTitle &&
    otherChapterTitles.some(t => t.trim().toLowerCase() === effectiveChapterTitle.toLowerCase()),
  );

  const suggestedNextTitle = isDuplicateTitle ? generateUniqueTitle(effectiveChapterTitle, otherChapterTitles) : '';

  const startAddChapter = () => {
    const nextTitle = generateUniqueTitle(
      'Chương mới',
      chapters.map(c => c.title),
    );
    setEditingChapter('new');
    setChapterTitle(nextTitle);
  };

  const startRenameChapter = (chapter: IChapterItem) => {
    setEditingChapter(chapter.id);
    setChapterTitle(chapter.title);
  };

  const cancelEditChapter = () => {
    if (chapterBusy) return;

    setEditingChapter(null);
    setChapterTitle('');
  };

  const saveChapterTitle = async (resolvedTitle?: string) => {
    if (!data || saving || chapterRequestRef.current || editingChapter === null) return;

    const isNew = editingChapter === 'new';
    const originalTitle = chapters.find(chapter => chapter.id === editingChapter)?.title ?? 'Chương mới';
    const otherTitles = chapters.filter(chapter => isNew || chapter.id !== editingChapter).map(chapter => chapter.title);

    let title = (resolvedTitle ?? chapterTitle).trim() || (isNew ? 'Chương mới' : originalTitle);
    title = generateUniqueTitle(title, otherTitles);
    setChapterTitle(title);

    if (!isNew && title === originalTitle) {
      cancelEditChapter();
      return;
    }

    chapterRequestRef.current = true;
    setChapterBusy(true);
    try {
      const payload = { courseId: data.course.id, version: data.course.version, title };
      const result = isNew
        ? await dispatch(createChapter(payload)).unwrap()
        : await dispatch(updateChapter({ ...payload, chapterId: editingChapter as number })).unwrap();

      if (isNew) {
        const newChapter = result.chapters.find(node => !chapters.some(chapter => chapter.id === node.chapter.id));
        if (newChapter) {
          setActiveChapterId(newChapter.chapter.id);
          setActiveLessonId(null);
          if (result.chapters.length > 6) setShowAllChapters(true);
        }
      }
      setEditingChapter(null);
      setChapterTitle('');
      toast.success(isNew ? 'Đã thêm chương.' : 'Đã đổi tên chương.');
    } catch (error) {
      toast.error(builderErrorText(error, 'Không thể lưu chương. Vui lòng thử lại.'));
    } finally {
      chapterRequestRef.current = false;
      setChapterBusy(false);
    }
  };

  const submitWithAutoRename = (targetTitle?: string) => {
    const isNew = editingChapter === 'new';
    const originalTitle = chapters.find(chapter => chapter.id === editingChapter)?.title ?? 'Chương mới';
    const raw = (targetTitle ?? chapterTitle).trim() || (isNew ? 'Chương mới' : originalTitle);

    const otherTitles = chapters.filter(chapter => isNew || chapter.id !== editingChapter).map(chapter => chapter.title);

    const finalTitle = generateUniqueTitle(raw, otherTitles);
    setChapterTitle(finalTitle);
    void saveChapterTitle(finalTitle);
  };

  const renderChapterInput = () => (
    <div className="flex-grow-1" style={{ minWidth: 0 }}>
      <Form.Control
        autoFocus
        size="sm"
        aria-label="Tên chương"
        value={chapterTitle}
        maxLength={255}
        isInvalid={isDuplicateTitle}
        readOnly={chapterBusy}
        onFocus={event => event.target.select()}
        onClick={event => event.stopPropagation()}
        onChange={event => setChapterTitle(event.target.value)}
        onBlur={() => {
          if (!chapterRequestRef.current && editingChapter !== null) {
            submitWithAutoRename();
          }
        }}
        onKeyDown={event => {
          event.stopPropagation();
          if (event.nativeEvent.isComposing) return;
          if (event.key === 'Enter') {
            event.preventDefault();
            submitWithAutoRename();
          } else if (event.key === 'Escape') {
            event.preventDefault();
            cancelEditChapter();
          }
        }}
      />
      {isDuplicateTitle && (
        <div className="text-danger small mt-1" style={{ fontSize: '11px', lineHeight: '1.2' }}>
          Tên chương đã tồn tại. Nhấn Enter để tự động đổi thành{' '}
          <button
            type="button"
            className="btn btn-link p-0 text-danger fw-bold text-decoration-underline"
            style={{ fontSize: '11px' }}
            onMouseDown={event => {
              event.preventDefault();
              setChapterTitle(suggestedNextTitle);
            }}
          >
            &ldquo;{suggestedNextTitle}&rdquo;
          </button>
        </div>
      )}
    </div>
  );

  const handleDeleteChapter = async () => {
    if (!data || saving || chapterRequestRef.current || !chapterToDelete) return;
    const chapterId = chapterToDelete.id;
    chapterRequestRef.current = true;
    setChapterBusy(true);

    try {
      await dispatch(
        deleteChapter({
          courseId: data.course.id,
          chapterId,
          version: data.course.version,
        }),
      ).unwrap();

      if (activeChapterId === chapterId) {
        setActiveChapterId(null);
        setActiveLessonId(null);
      }

      toast.success('Đã xóa chương.');
      setChapterToDelete(null);
    } catch (error) {
      toast.error(builderErrorText(error, 'Không thể xóa chương. Vui lòng kiểm tra thông báo lỗi.'));
    } finally {
      chapterRequestRef.current = false;
      setChapterBusy(false);
    }
  };

  if (loading && !data) {
    return <div className="p-4 text-center text-muted">Đang tải nội dung bài học...</div>;
  }

  if (!data) {
    return <div className="alert alert-info">Bạn cần lưu thông tin khóa học trước khi tạo nội dung bài học.</div>;
  }

  const handleAddLesson = async (chapterId: number) => {
    const latest = store.getState().courseBuilder;
    if (!latest.data || latest.saving || chapterBusy || chapterRequestRef.current || lessonRequestRef.current) return;

    lessonRequestRef.current = true;
    const chapter = latest.data.chapters.find(node => node.chapter.id === chapterId);
    const title = generateUniqueTitle('Bài học mới', chapter?.lessons.map(item => item.lesson.title ?? '') ?? []);

    try {
      const result = await dispatch(
        createLesson({
          courseId: data.course.id,
          chapterId,
          version: latest.data.course.version,
          lesson: {
            title,
            description: '',
            isPreview: false,
          },
        }),
      ).unwrap();

      const previousIds = new Set(data.chapters.flatMap(node => node.lessons.map(item => item.lesson.id)));
      const newLesson = result.chapters.find(node => node.chapter.id === chapterId)?.lessons.find(item => !previousIds.has(item.lesson.id));

      if (newLesson) {
        selectLesson(chapterId, newLesson.lesson.id);
      }

      toast.success('Đã thêm bài học.');
    } catch (error) {
      toast.error(builderErrorText(error, 'Không thể thêm bài học. Vui lòng thử lại.'));
    } finally {
      lessonRequestRef.current = false;
    }
  };

  const handleSaveLesson = async () => {
    if (
      !data ||
      !editorLesson ||
      !hasLessonChanges ||
      lessonTitleError ||
      saving ||
      chapterBusy ||
      chapterRequestRef.current ||
      lessonRequestRef.current
    )
      return false;

    lessonRequestRef.current = true;

    const otherTitles = currentChapter.lessons.filter(lesson => lesson.id !== editorLesson.id).map(lesson => lesson.title);
    const title = generateUniqueTitle(editorLesson.title, otherTitles);

    try {
      await dispatch(
        updateLesson({
          courseId: data.course.id,
          lessonId: editorLesson.id,
          version: data.course.version,
          lesson: {
            title,
            description: editorLesson.description,
            isPreview: editorLesson.isPreview,
          },
        }),
      ).unwrap();

      setLessonDraft(null);
      toast.success('Đã lưu bài học.');
      return true;
    } catch (error) {
      // Không xóa bản nháp khi lưu lỗi.
      toast.error(builderErrorText(error, 'Không thể lưu bài học. Nội dung đang nhập vẫn được giữ lại.'));
      return false;
    } finally {
      lessonRequestRef.current = false;
    }
  };

  const handleDeleteLesson = async () => {
    if (!data || !lessonToDelete || saving || chapterBusy || chapterRequestRef.current || lessonRequestRef.current) return;

    lessonRequestRef.current = true;

    try {
      await dispatch(
        deleteLesson({
          courseId: data.course.id,
          lessonId: lessonToDelete.id,
          version: data.course.version,
        }),
      ).unwrap();

      if (currentLesson?.id === lessonToDelete.id) {
        setActiveChapterId(currentChapter?.id ?? null);
        setActiveLessonId(null);
        setLessonDraft(null);
      }

      setLessonToDelete(null);
      toast.success('Đã xóa bài học.');
    } catch (error) {
      toast.error(builderErrorText(error, 'Không thể xóa bài học. Vui lòng thử lại.'));
    } finally {
      lessonRequestRef.current = false;
    }
  };

  const handleDrop = async (position: number) => {
    const sourceIndex = draggedIndex;
    setDraggedIndex(null);
    setDropPosition(null);
    if (sourceIndex === null || saving || chapterRequestRef.current || editingChapter !== null) return;

    // Vị trí chèn được tính trước khi bỏ chương khỏi mảng.
    const targetIndex = position > sourceIndex ? position - 1 : position;
    if (sourceIndex === targetIndex) return;

    const updated = [...data.chapters];
    const [moved] = updated.splice(sourceIndex, 1);
    updated.splice(targetIndex, 0, moved);

    chapterRequestRef.current = true;
    setChapterBusy(true);
    try {
      await dispatch(
        reorderCurriculum({
          courseId: data.course.id,
          version: data.course.version,
          chapters: updated.map(node => ({
            id: node.chapter.id,
            lessonIds: node.lessons.map(item => item.lesson.id),
          })),
        }),
      ).unwrap();
      // Giữ bài đang chọn ngay cả khi trước đó đang dùng chương mặc định.
      setActiveChapterId(currentChapter?.id ?? null);
      setActiveLessonId(currentLesson?.id ?? null);
    } catch (error) {
      toast.error(builderErrorText(error, 'Không thể lưu thứ tự chương. Vui lòng thử lại.'));
    } finally {
      chapterRequestRef.current = false;
      setChapterBusy(false);
    }
  };

  const handleLessonDrop = async (chapterId: number, position: number) => {
    const source = draggedLesson;
    setDraggedLesson(null);
    setLessonDropPosition(null);
    if (!source || source.chapterId !== chapterId || controlsDisabled || chapterRequestRef.current || lessonRequestRef.current) return;

    const targetIndex = position > source.index ? position - 1 : position;
    if (source.index === targetIndex) return;

    const order = data.chapters.map(node => {
      const lessonIds = node.lessons.map(item => item.lesson.id);
      if (node.chapter.id === chapterId) {
        const [moved] = lessonIds.splice(source.index, 1);
        lessonIds.splice(targetIndex, 0, moved);
      }
      return { id: node.chapter.id, lessonIds };
    });

    lessonRequestRef.current = true;
    try {
      await dispatch(reorderCurriculum({ courseId: data.course.id, version: data.course.version, chapters: order })).unwrap();
      setActiveChapterId(currentChapter.id);
      setActiveLessonId(currentLesson?.id ?? null);
    } catch (error) {
      toast.error(builderErrorText(error, 'Không thể lưu thứ tự bài học. Nội dung đang nhập vẫn được giữ lại.'));
    } finally {
      lessonRequestRef.current = false;
    }
  };

  const handleMoveLesson = async () => {
    const latest = store.getState().courseBuilder;
    if (!lessonToMove || moveChapterId === null || !latest.data || controlsDisabled || lessonRequestRef.current) return;
    const movedId = lessonToMove.id;
    lessonRequestRef.current = true;
    try {
      await dispatch(
        moveLesson({
          courseId: latest.data.course.id,
          lessonId: movedId,
          chapterId: moveChapterId,
          title: proposedMoveTitle(),
          version: latest.data.course.version,
        }),
      ).unwrap();
      selectLesson(moveChapterId, movedId);
      setShowAllChapters(true);
      setLessonToMove(null);
      toast.success('Đã chuyển bài học.');
    } catch (error) {
      toast.error(builderErrorText(error));
    } finally {
      lessonRequestRef.current = false;
    }
  };

  const proposedMoveTitle = () => {
    if (!lessonToMove) return '';
    const target = chapters.find(chapter => chapter.id === moveChapterId);
    return generateUniqueTitle(lessonToMove.title, target?.lessons.map(lesson => lesson.title) ?? []);
  };

  const renderMoveDialog = () => (
    <Modal show={lessonToMove !== null} onHide={() => !saving && setLessonToMove(null)} centered backdrop="static" keyboard={!saving}>
      <Modal.Header closeButton={!saving}>
        <Modal.Title>Chuyển bài học sang chương khác</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <div className="mb-2">{lessonToMove?.title}</div>
        <Form.Select
          aria-label="Chương đích"
          value={moveChapterId ?? ''}
          disabled={saving}
          onChange={event => setMoveChapterId(Number(event.target.value))}
        >
          <option value="" disabled>
            Chọn chương đích
          </option>
          {chapters
            .filter(chapter => !chapter.lessons.some(lesson => lesson.id === lessonToMove?.id))
            .map(chapter => (
              <option key={chapter.id} value={chapter.id}>
                {chapter.title}
              </option>
            ))}
        </Form.Select>
        <div className="small text-muted mt-2">Bài học sẽ được đặt ở cuối chương đích.</div>
        {lessonToMove && proposedMoveTitle() !== lessonToMove.title && (
          <div className="text-warning mt-2" role="status">
            Chương đích đã có bài cùng tên. Bài được chuyển sẽ đổi tên thành <strong>{proposedMoveTitle()}</strong>.
          </div>
        )}
      </Modal.Body>
      <Modal.Footer>
        <Button variant="outline-secondary" disabled={saving} onClick={() => setLessonToMove(null)}>
          Hủy
        </Button>
        <Button disabled={controlsDisabled || moveChapterId === null} onClick={handleMoveLesson}>
          {proposedMoveTitle() !== lessonToMove?.title ? 'Chuyển và đổi tên' : 'Chuyển bài học'}
        </Button>
      </Modal.Footer>
    </Modal>
  );

  const renderLessonEditor = () =>
    currentChapter && currentLesson ? (
      <LessonEditor
        key={currentLesson.id}
        lesson={editorLesson!}
        chapterTitle={currentChapter.title}
        lessonIndex={currentLessonIndex}
        totalLessonsInChapter={currentChapter.totalLessons}
        saving={saving || chapterBusy}
        disabled={controlsDisabled}
        hasChanges={hasLessonChanges}
        titleError={lessonTitleError}
        onUpdateLesson={setLessonDraft}
        onSave={() => void handleSaveLesson()}
      />
    ) : (
      <Card className="p-4 rounded-4 border-0 shadow-sm text-center text-muted">
        {currentChapter ? 'Chương này chưa có bài học.' : 'Chưa có nội dung bài học để hiển thị.'}
      </Card>
    );

  return (
    <div>
      {errorMessage && (
        <div className="alert alert-danger" role="alert">
          {errorMessage}
          <Button size="sm" className="ms-2" disabled={saving || loading} onClick={() => void dispatch(getCourseBuilder(data.course.id))}>
            {loading ? 'Đang tải lại…' : 'Tải lại dữ liệu'}
          </Button>
        </div>
      )}
      <Row className="g-4">
        {/* CỘT TRÁI: DANH SÁCH ĐỀ CƯƠNG (CHƯƠNG & BÀI HỌC) */}
        <Col lg={5}>
          <Card className="p-4 rounded-4 border-0 shadow-sm mb-3">
            <div className="d-flex justify-content-between align-items-center mb-3">
              <h5 className="fw-bold mb-0">Đề cương khóa học</h5>
            </div>

            <div className="d-flex flex-column gap-3">
              {chapters.length === 0 && <div className="text-muted text-center py-4">Khóa học chưa có chương nào.</div>}
              {visibleChapters.map((ch, index) => {
                const isExpanded = ch.id === currentChapter?.id;
                const isDragging = draggedIndex === index;

                return (
                  <div
                    key={ch.id}
                    onDragOver={e => {
                      if (draggedIndex === null || controlsDisabled) return;
                      e.preventDefault();
                      e.dataTransfer.dropEffect = 'move';
                      const bounds = e.currentTarget.getBoundingClientRect();
                      setDropPosition(e.clientY < bounds.top + bounds.height / 2 ? index : index + 1);
                    }}
                    onDragLeave={e => {
                      if (!e.currentTarget.contains(e.relatedTarget as Node | null)) setDropPosition(null);
                    }}
                    onDrop={e => {
                      e.preventDefault();
                      e.stopPropagation();
                      const bounds = e.currentTarget.getBoundingClientRect();
                      void handleDrop(e.clientY < bounds.top + bounds.height / 2 ? index : index + 1);
                    }}
                    className={`border rounded-3 bg-white position-relative ${isDragging ? 'opacity-50' : ''}`}
                  >
                    {draggedIndex !== null &&
                      (dropPosition === index || (index === visibleChapters.length - 1 && dropPosition === index + 1)) && (
                        <div
                          className="position-absolute start-0 w-100 bg-primary rounded"
                          style={{
                            height: 3,
                            top: dropPosition === index ? -7 : undefined,
                            bottom: dropPosition === index + 1 ? -7 : undefined,
                            pointerEvents: 'none',
                          }}
                        />
                      )}
                    {/* Chapter Header */}
                    <div
                      className={`p-3 d-flex justify-content-between align-items-center cursor-pointer ${
                        isExpanded ? 'bg-light border-bottom' : ''
                      }`}
                      onClick={() => {
                        if (editingChapter !== null || draggedIndex !== null || ch.id === currentChapter?.id) return;
                        requestAction(() => selectLesson(ch.id, null));
                      }}
                      style={{ cursor: 'pointer' }}
                    >
                      <div className="d-flex align-items-center gap-2 w-100" style={{ minWidth: 0 }}>
                        {/* thẻ kéo vị trí chương */}
                        <span
                          className="text-muted"
                          title="Kéo để đổi thứ tự chương"
                          style={{ cursor: controlsDisabled ? 'default' : 'grab' }}
                          draggable={!saving && !chapterBusy && editingChapter === null && chapterToDelete === null}
                          onClick={event => event.stopPropagation()}
                          onDragStart={event => {
                            event.stopPropagation();
                            event.dataTransfer.effectAllowed = 'move';
                            event.dataTransfer.setData('text/plain', String(ch.id));
                            setDraggedIndex(index);
                          }}
                          onDragEnd={() => {
                            setDraggedIndex(null);
                            setDropPosition(null);
                          }}
                        >
                          <FontAwesomeIcon icon={faGripVertical} />
                        </span>
                        <FontAwesomeIcon icon={isExpanded ? faChevronDown : faChevronRight} className="text-muted small" />
                        {editingChapter === ch.id ? renderChapterInput() : <span className="fw-bold small text-dark">{ch.title}</span>}
                      </div>
                    </div>

                    {/* Chapter Metadata & Sub-lessons */}
                    <div className="px-3 py-2 bg-light d-flex justify-content-between align-items-center text-muted small border-bottom">
                      <span className="d-flex align-items-center gap-1.5">
                        <span className="badge bg-white text-secondary border px-2 py-1 fw-normal" style={{ fontSize: '11.5px' }}>
                          {ch.totalLessons} bài
                        </span>
                        {ch.durationText && (
                          <span className="text-secondary small" style={{ fontSize: '12px' }}>
                            • {ch.durationText}
                          </span>
                        )}
                      </span>
                      <div className="d-flex align-items-center gap-1">
                        <Button
                          variant="outline-secondary"
                          size="sm"
                          className="py-1 px-2 border-0 rounded-pill d-inline-flex align-items-center gap-1 shadow-none"
                          style={{
                            fontSize: '11.5px',
                            backgroundColor: 'rgba(108, 117, 125, 0.08)',
                            color: '#495057',
                          }}
                          disabled={controlsDisabled}
                          onClick={() => startRenameChapter(ch)}
                          title="Đổi tên chương"
                        >
                          <FontAwesomeIcon icon={faPen} style={{ fontSize: '10px' }} />
                          <span className="fw-medium">Đổi tên</span>
                        </Button>

                        <Button
                          variant="outline-danger"
                          size="sm"
                          className="py-1 px-2 border-0 rounded-pill d-inline-flex align-items-center gap-1 shadow-none"
                          style={{
                            fontSize: '11.5px',
                            backgroundColor: 'rgba(220, 53, 69, 0.08)',
                            color: '#dc3545',
                          }}
                          disabled={controlsDisabled}
                          onClick={() => setChapterToDelete(ch)}
                          title="Xóa chương"
                        >
                          <FontAwesomeIcon icon={faTrash} style={{ fontSize: '10px' }} />
                          <span className="fw-medium">Xóa</span>
                        </Button>
                      </div>
                    </div>

                    {/* Lessons list if expanded */}
                    {isExpanded && (
                      <div className="p-2 d-flex flex-column gap-1 bg-white">
                        {ch.lessons.map((ls, idx) => {
                          const isSelected = ls.id === currentLesson?.id;
                          return (
                            <div
                              key={ls.id}
                              onDragOver={event => {
                                if (!draggedLesson || draggedLesson.chapterId !== ch.id || controlsDisabled) return;
                                event.preventDefault();
                                event.stopPropagation();
                                event.dataTransfer.dropEffect = 'move';
                                const bounds = event.currentTarget.getBoundingClientRect();
                                setLessonDropPosition(event.clientY < bounds.top + bounds.height / 2 ? idx : idx + 1);
                              }}
                              onDragLeave={event => {
                                if (!event.currentTarget.contains(event.relatedTarget as Node | null)) setLessonDropPosition(null);
                              }}
                              onDrop={event => {
                                if (!draggedLesson) return;
                                event.preventDefault();
                                event.stopPropagation();
                                const bounds = event.currentTarget.getBoundingClientRect();
                                void handleLessonDrop(ch.id, event.clientY < bounds.top + bounds.height / 2 ? idx : idx + 1);
                              }}
                              onClick={() => {
                                if (ls.id === currentLesson?.id) return;
                                requestAction(() => selectLesson(ch.id, ls.id));
                              }}
                              className={`p-2 rounded-2 d-flex justify-content-between align-items-center cursor-pointer ${
                                isSelected ? 'bg-primary-subtle text-primary border border-primary-subtle' : 'hover-bg-light'
                              }`}
                              style={{
                                cursor: 'pointer',
                                opacity: draggedLesson?.index === idx ? 0.5 : 1,
                                boxShadow: lessonDropPosition === idx ? '0 -3px 0 var(--bs-primary)' : undefined,
                                borderBottom:
                                  lessonDropPosition === idx + 1 && idx === ch.lessons.length - 1
                                    ? '3px solid var(--bs-primary)'
                                    : undefined,
                              }}
                            >
                              <div className="d-flex align-items-center gap-2">
                                <span
                                  className="text-muted cursor-grab"
                                  title="Kéo để đổi thứ tự bài học"
                                  style={{ cursor: controlsDisabled ? 'default' : 'grab' }}
                                  draggable={!controlsDisabled}
                                  onClick={event => event.stopPropagation()}
                                  onDragStart={event => {
                                    event.stopPropagation();
                                    event.dataTransfer.effectAllowed = 'move';
                                    event.dataTransfer.setData('text/plain', String(ls.id));
                                    setDraggedLesson({ chapterId: ch.id, index: idx });
                                  }}
                                  onDragEnd={() => {
                                    setDraggedLesson(null);
                                    setLessonDropPosition(null);
                                  }}
                                >
                                  <FontAwesomeIcon icon={faGripVertical} />
                                </span>
                                <span className="small fw-semibold">
                                  {idx + 1}. {ls.title}
                                </span>
                              </div>
                              <div className="d-flex align-items-center gap-1.5 ms-auto">
                                <span
                                  className="badge bg-light text-secondary border fw-normal py-1 px-2 me-1"
                                  style={{ fontSize: '11px' }}
                                >
                                  {ls.duration}
                                </span>
                                <Button
                                  size="sm"
                                  variant="outline-primary"
                                  className="py-1 px-2 border-0 rounded-pill d-inline-flex align-items-center gap-1 shadow-none"
                                  style={{
                                    fontSize: '11.5px',
                                    backgroundColor: 'rgba(13, 110, 253, 0.08)',
                                    color: '#0d6efd',
                                  }}
                                  disabled={controlsDisabled || chapters.length < 2}
                                  title="Chuyển sang chương khác"
                                  onClick={event => {
                                    event.stopPropagation();
                                    requestAction(() => {
                                      setLessonDraft(null);
                                      setMoveChapterId(null);
                                      setLessonToMove(ls);
                                    });
                                  }}
                                >
                                  <FontAwesomeIcon icon={faRightLeft} style={{ fontSize: '10px' }} />
                                  <span className="fw-medium">Chuyển</span>
                                </Button>
                                <Button
                                  size="sm"
                                  variant="outline-danger"
                                  className="py-1 px-2 border-0 rounded-pill d-inline-flex align-items-center gap-1 shadow-none"
                                  style={{
                                    fontSize: '11.5px',
                                    backgroundColor: 'rgba(220, 53, 69, 0.08)',
                                    color: '#dc3545',
                                  }}
                                  disabled={controlsDisabled}
                                  title="Xóa bài học"
                                  onClick={event => {
                                    event.stopPropagation();
                                    setLessonToDelete(ls);
                                  }}
                                >
                                  <FontAwesomeIcon icon={faTrash} style={{ fontSize: '10px' }} />
                                  <span className="fw-medium">Xóa</span>
                                </Button>
                              </div>
                            </div>
                          );
                        })}

                        <Button
                          variant="outline-primary"
                          size="sm"
                          className="w-100 rounded-3 mt-2 border-dashed"
                          disabled={controlsDisabled}
                          onClick={() =>
                            requestAction(() => {
                              setLessonDraft(null);
                              void handleAddLesson(ch.id);
                            })
                          }
                        >
                          <FontAwesomeIcon icon={faPlus} className="me-1" />
                          Thêm bài học
                        </Button>
                      </div>
                    )}
                  </div>
                );
              })}
              {editingChapter === 'new' && (
                <div className="border rounded-3 overflow-hidden bg-white">
                  {/* Chapter Header */}
                  <div className="p-3 d-flex align-items-center gap-2 bg-light">
                    <FontAwesomeIcon icon={faGripVertical} className="text-muted" />
                    <FontAwesomeIcon icon={faChevronRight} className="text-muted small" />
                    {renderChapterInput()}
                  </div>
                </div>
              )}
            </div>

            {chapters.length > 6 && (
              <Button
                variant="link"
                className="w-100 mt-2 text-decoration-none"
                disabled={controlsDisabled || draggedIndex !== null}
                onClick={() => {
                  requestAction(() => {
                    if (showAllChapters && !chapters.slice(0, 6).some(chapter => chapter.id === currentChapter?.id)) {
                      selectLesson(chapters[0].id, null);
                    }
                    setShowAllChapters(!showAllChapters);
                  });
                }}
              >
                {showAllChapters ? 'Thu gọn' : `Hiển thị tất cả (${chapters.length} chương)`}
              </Button>
            )}

            <Button
              variant="outline-secondary"
              className="w-100 rounded-pill mt-3 py-2"
              onClick={() => requestAction(startAddChapter)}
              disabled={controlsDisabled}
            >
              <FontAwesomeIcon icon={faPlus} className="me-1" />
              Thêm chương mới
            </Button>

            <div className="text-muted small mt-3 text-center">
              Thời lượng đề cương được tính từ các bài học trong khóa học.
              {/* Mỗi video: 30 phút. Video giới thiệu không nằm trong tổng thời lượng đề cương. */}
            </div>
          </Card>
        </Col>

        {/* CỘT PHẢI: CHI TIẾT BÀI HỌC ĐANG CHỌN */}
        <Col lg={7}>{renderLessonEditor()}</Col>
      </Row>
      {renderMoveDialog()}

      <Modal show={pendingAction !== null} onHide={() => !saving && setPendingAction(null)} centered backdrop="static" keyboard={!saving}>
        <Modal.Header closeButton={!saving}>
          <Modal.Title>Bài học có thay đổi chưa lưu</Modal.Title>
        </Modal.Header>
        <Modal.Body>Bạn muốn lưu nội dung đang chỉnh sửa trước khi tiếp tục?</Modal.Body>
        <Modal.Footer>
          <Button variant="outline-secondary" disabled={saving} onClick={() => setPendingAction(null)}>
            Ở lại
          </Button>
          <Button
            variant="outline-danger"
            disabled={saving}
            onClick={() => {
              const action = pendingAction;
              setPendingAction(null);
              setLessonDraft(null);
              action?.();
            }}
          >
            Bỏ thay đổi
          </Button>
          <Button
            variant="primary"
            disabled={controlsDisabled || Boolean(lessonTitleError)}
            onClick={async () => {
              if (await handleSaveLesson()) {
                const action = pendingAction;
                setPendingAction(null);
                action?.();
              }
            }}
          >
            {saving ? 'Đang lưu...' : 'Lưu và tiếp tục'}
          </Button>
        </Modal.Footer>
      </Modal>

      <Modal show={lessonToDelete !== null} onHide={() => !saving && setLessonToDelete(null)} centered backdrop="static" keyboard={!saving}>
        <Modal.Header closeButton={!saving}>
          <Modal.Title>Xóa bài học</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          Bạn có chắc muốn xóa bài học <strong>{lessonToDelete?.title}</strong>?
          <p className="text-muted mt-2 mb-0">Video, tài liệu và nội dung chưa lưu của bài này cũng sẽ bị xóa.</p>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="outline-secondary" disabled={saving} onClick={() => setLessonToDelete(null)}>
            Hủy
          </Button>
          <Button variant="danger" disabled={saving || chapterBusy} onClick={handleDeleteLesson}>
            {saving ? 'Đang xóa...' : 'Xóa bài học'}
          </Button>
        </Modal.Footer>
      </Modal>

      <Modal
        show={chapterToDelete !== null}
        onHide={() => !chapterBusy && setChapterToDelete(null)}
        centered
        backdrop="static"
        keyboard={!chapterBusy}
      >
        <Modal.Header closeButton={!chapterBusy}>
          <Modal.Title>Xóa chương</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          Bạn có chắc muốn xóa chương <strong>{chapterToDelete?.title}</strong>?
          <p className="text-muted mt-2 mb-0">Các bài học và tài liệu trong chương cũng sẽ bị xóa.</p>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="outline-secondary" disabled={chapterBusy} onClick={() => setChapterToDelete(null)}>
            Hủy
          </Button>
          <Button variant="danger" disabled={saving || chapterBusy} onClick={handleDeleteChapter}>
            {chapterBusy ? 'Đang xóa...' : 'Xóa chương'}
          </Button>
        </Modal.Footer>
      </Modal>

      {/* THANH ĐIỀU HƯỚNG CUỐI TRANG */}
      <div className="d-flex justify-content-between align-items-center mt-4 pt-3 border-top">
        <Button
          variant="outline-secondary"
          className="rounded-pill px-4 py-2"
          disabled={saving || chapterBusy}
          onClick={() => requestAction(onBack)}
        >
          <FontAwesomeIcon icon={faArrowLeft} className="me-2" /> Thông tin khóa học
        </Button>
        <Button variant="primary" className="rounded-pill px-4 py-2" disabled={saving || chapterBusy} onClick={() => requestAction(onNext)}>
          Tiếp: Học phí <FontAwesomeIcon icon={faArrowRight} className="ms-2" />
        </Button>
      </div>
    </div>
  );
};

export default CurriculumTab;
