import { createAsyncThunk, createSlice, isPending, isRejected } from '@reduxjs/toolkit';
import axios from 'axios';
import { readVideoDuration } from './curriculum/lesson/video-duration';
import { builderErrorStatus, builderErrorText } from './course-builder-error';
import type { IChapter } from 'app/shared/model/chapter.model';
import type { ILesson } from 'app/shared/model/lesson.model';
import type { ICourseMaterial } from 'app/shared/model/course-material.model';

const API_BASE = '/api/instructor/courses';

// 1. ĐỊNH NGHĨA CÁC KIỂU DỮ LIỆU (TYPES / DTO)
export interface IBasicInfoPayload {
  version: number;
  title: string;
  subjectId?: number | null;
  gradeId?: number | null;
  bookSeries?: string | null;
  shortDescription?: string | null;
  description?: string | null;
  targetAudience?: string | null;
  prerequisites?: string | null;
  learningObjectives?: string[] | null;
}

export interface IMediaAccess {
  url: string;
  expiresAt: string;
}

export type AccessDurationUnit = 'DAY' | 'MONTH' | 'YEAR';

export interface IPricingPayload {
  version: number;
  price: number;
  accessDurationValue: number;
  accessDurationUnit: AccessDurationUnit;
}

export interface ICourseData {
  id: number;
  version: number;
  title: string;
  status: string;
  bookSeries?: string;
  shortDescription?: string;
  description?: string;
  targetAudience?: string;
  prerequisites?: string;
  learningObjectives?: string[];
  thumbnailUrl?: string;
  introVideoUrl?: string;
  price?: number | null;
  accessDurationValue?: number | null;
  accessDurationUnit?: AccessDurationUnit | null;
  author?: { id: number; login?: string; firstName?: string; lastName?: string };
  authorDisplayName?: string;
  validDurationDays?: number;
  subject?: { id: number; name: string };
  grade?: { id: number; name: string };
}

export interface ILessonNode {
  lesson: ILesson & { id: number };
  materials: ICourseMaterial[];
}

export interface IChapterNode {
  chapter: IChapter & { id: number };
  lessons: ILessonNode[];
}

export interface ILessonInput {
  title: string;
  description: string;
  isPreview: boolean;
}

export interface IBuilderAggregate {
  course: ICourseData;
  chapters: IChapterNode[];
  totalLessons: number;
  totalMaterials: number;
  totalVideos: number;
  review?: IReviewState;
}

export interface IReviewSummary {
  id: number;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'WITHDRAWN';
  courseVersion: number;
  submissionNote?: string | null;
  rightsConfirmedAt: string;
  submittedAt: string;
  reviewedAt?: string | null;
  reviewerFeedback?: string | null;
}

export interface IReviewState {
  version: number;
  canSubmit: boolean;
  checks: { code: string; tab: 'basic-info' | 'curriculum' | 'pricing'; label: string; required: boolean; complete: boolean }[];
  request?: IReviewSummary | null;
}

export interface ISubmitReviewPayload {
  version: number;
  note: string;
  confirmedRights: boolean;
}

// =========================================================================
// INTERFACES CHO EXAMS & QUESTIONS
// =========================================================================
export interface IOptionDTO {
  id?: number;
  content: string;
  isCorrect: boolean;
  orderIndex?: number;
}

export interface IQuestionDetailDTO {
  id: number;
  content: string;
  type: string;
  difficulty: string | number;
  explanation?: string;
  score: number;
  orderIndex: number;
  options: IOptionDTO[];
}

export interface IExamSummaryDTO {
  id: number;
  title: string;
  assessmentType: 'PRACTICE' | 'CHECKPOINT' | 'LESSON_QUIZ' | 'CHAPTER_QUIZ' | 'MIDTERM_EXAM' | 'FINAL_EXAM';
  durationMinutes: number;
  passScore: number;
  maxAttempts: number;
  shuffleQuestions: boolean;
  videoTriggerSeconds?: number | null;
  courseId?: number;
  chapterId?: number;
  lessonId?: number;
  totalQuestions: number;
  totalScore: number;
  createdAt: string;
}

export interface IExamDetailDTO extends IExamSummaryDTO {
  questions: IQuestionDetailDTO[];
}

export interface ICreateExamInput {
  title: string;
  durationMinutes?: number;
  passScore?: number;
  maxAttempts?: number;
  shuffleQuestions?: boolean;
  videoTriggerSeconds?: number;
}

export interface ICreateQuestionInput {
  content: string;
  type?: string;
  difficulty?: string;
  explanation?: string;
  score?: number;
  orderIndex?: number;
  options: { content: string; isCorrect: boolean; orderIndex?: number }[];
}

// 1. ĐỊNH NGHĨA STATE CỦA COURSE BUILDER TRONG REDUX
export interface CourseBuilderState {
  errorStatus: number | null;
  loadingRequestId: string | null;
  loading: boolean; // Đang tải dữ liệu ban đầu
  saving: boolean; // Đang thực hiện lưu/upload
  data: IBuilderAggregate | null; // Cây dữ liệu Aggregate (course, chapters, lessons)
  errorMessage: string | null;
  lastSavedTime: string | null;
}

const initialState: CourseBuilderState = {
  errorStatus: null,
  loadingRequestId: null,
  loading: false,
  saving: false,
  data: null,
  errorMessage: null,
  lastSavedTime: null,
};

export interface ILessonMediaTarget {
  courseId: number;
  lessonId: number;
  kind: 'video' | 'material';
  materialId?: number;
}

const getCurrentTime = () => {
  const now = new Date();
  return `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`;
};

// ==========================================
// 2. CÁC ASYNC THUNKS (GỌI API QUA AXIOS)
// ==========================================

// 0. Tạo bản nháp mới ban đầu khi vào /new
// condition chạy trước pending, nên khóa được cả hai dispatch trong cùng một lượt click.
const writeOptions = {
  condition(_args: unknown, { getState }: { getState: () => unknown }) {
    const state = (getState() as { courseBuilder: CourseBuilderState }).courseBuilder;
    if (state.data && (state.data.course.status !== 'DRAFT' || state.data.review?.request?.status === 'PENDING')) return false;
    return !state.saving && !state.loading && state.errorStatus !== 409 && state.errorStatus !== 403 && state.errorStatus !== 404;
  },
  serializeError: (error: unknown) => ({
    message: (error as { message?: string })?.message ?? 'Có lỗi xảy ra',
    code: String(builderErrorStatus(error) ?? ''),
  }),
};

export const createDraft = createAsyncThunk(
  'courseBuilder/create_draft',
  async (title?: string) => {
    const response = await axios.post<IBuilderAggregate>(API_BASE, { title });
    return response.data;
  },
  writeOptions,
);

// 1. Lấy toàn bộ cây dữ liệu khóa học
export const getCourseBuilder = createAsyncThunk(
  'courseBuilder/fetch_builder',
  async (courseId: number) => {
    const response = await axios.get<IBuilderAggregate>(`${API_BASE}/${courseId}/builder`);
    return response.data;
  },
  { serializeError: writeOptions.serializeError },
);

// 2. Lưu thông tin cơ bản Tab 1
export const submitCourseReview = createAsyncThunk(
  'courseBuilder/submit_review',
  async ({ courseId, data }: { courseId: number; data: ISubmitReviewPayload }) => {
    const response = await axios.post<IBuilderAggregate>(`${API_BASE}/${courseId}/review-requests`, data);
    return response.data;
  },
  writeOptions,
);

export const withdrawCourseReview = createAsyncThunk(
  'courseBuilder/withdraw_review',
  async ({ courseId, version }: { courseId: number; version: number }) => {
    const response = await axios.post<IBuilderAggregate>(`${API_BASE}/${courseId}/review-requests/withdraw`, { version });
    return response.data;
  },
  {
    serializeError: writeOptions.serializeError,
    condition(_args, { getState }) {
      const state = (getState() as { courseBuilder: CourseBuilderState }).courseBuilder;
      return (
        !state.saving &&
        !state.loading &&
        state.data?.course.status === 'PENDING' &&
        state.data.review?.request?.status === 'PENDING' &&
        ![403, 404, 409].includes(state.errorStatus!)
      );
    },
  },
);

export const updatePricing = createAsyncThunk(
  'courseBuilder/update_pricing',
  async ({ courseId, data }: { courseId: number; data: IPricingPayload }) => {
    const response = await axios.put<IBuilderAggregate>(`${API_BASE}/${courseId}/pricing`, data);
    return response.data;
  },
  writeOptions,
);

export const updateBasicInfo = createAsyncThunk(
  'courseBuilder/update_basic_info',
  async ({ courseId, data }: { courseId: number; data: IBasicInfoPayload }) => {
    const response = await axios.put<IBuilderAggregate>(`${API_BASE}/${courseId}/basic-info`, data);
    return response.data;
  },
  writeOptions,
);

// 3. Upload ảnh bìa
export const uploadThumbnail = createAsyncThunk(
  'courseBuilder/upload_thumbnail',
  async ({ courseId, version, file }: { courseId: number; version: number; file: File }) => {
    const formData = new FormData();
    formData.append('file', file);
    const response = await axios.post<IBuilderAggregate>(`${API_BASE}/${courseId}/thumbnail?version=${version}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return response.data;
  },
  writeOptions,
);

// 4. Gỡ ảnh bìa
export const deleteThumbnail = createAsyncThunk(
  'courseBuilder/delete_thumbnail',
  async ({ courseId, version }: { courseId: number; version: number }) => {
    const response = await axios.delete<IBuilderAggregate>(`${API_BASE}/${courseId}/thumbnail?version=${version}`);
    return response.data;
  },
  writeOptions,
);

// 5. Upload video intro
export const uploadIntroVideo = createAsyncThunk(
  'courseBuilder/upload_intro_video',
  async ({ courseId, version, file }: { courseId: number; version: number; file: File }) => {
    const formData = new FormData();
    formData.append('file', file);
    const response = await axios.post<IBuilderAggregate>(`${API_BASE}/${courseId}/intro-video?version=${version}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return response.data;
  },
  writeOptions,
);

// 6. Gỡ video intro
export const deleteIntroVideo = createAsyncThunk(
  'courseBuilder/delete_intro_video',
  async ({ courseId, version }: { courseId: number; version: number }) => {
    const response = await axios.delete<IBuilderAggregate>(`${API_BASE}/${courseId}/intro-video?version=${version}`);
    return response.data;
  },
  writeOptions,
);

// ==========================================
// 3. CÁC HÀM TIỆN ÍCH LẤY DANH MỤC & ACCESS URL
// ==========================================
export const fetchSubjects = async () => (await axios.get<any[]>('/api/subjects?size=100')).data;
export const fetchGrades = async () => (await axios.get<any[]>('/api/grades?size=100')).data;
export const getThumbnailAccessUrl = async (courseId: number) =>
  (await axios.get<IMediaAccess>(`${API_BASE}/${courseId}/thumbnail/access`)).data;
export const getIntroVideoPlaybackUrl = async (courseId: number) =>
  (await axios.get<IMediaAccess>(`${API_BASE}/${courseId}/intro-video/playback`)).data;

// ==========================================
// CÁC HÀM CHO CHAPTER
// ==========================================
// Thêm chương
export const createChapter = createAsyncThunk(
  'courseBuilder/create_chapter',
  async ({ courseId, version, title }: { courseId: number; version: number; title: string }) => {
    const response = await axios.post<IBuilderAggregate>(`${API_BASE}/${courseId}/chapters`, { version, title });
    return response.data;
  },
  writeOptions,
);

// Đổi tên chương
export const updateChapter = createAsyncThunk(
  'courseBuilder/update_chapter',
  async ({ courseId, chapterId, version, title }: { courseId: number; chapterId: number; version: number; title: string }) => {
    const response = await axios.put<IBuilderAggregate>(`${API_BASE}/${courseId}/chapters/${chapterId}`, { version, title });
    return response.data;
  },
  writeOptions,
);

// Xóa chương
export const deleteChapter = createAsyncThunk(
  'courseBuilder/delete_chapter',
  async ({ courseId, chapterId, version }: { courseId: number; chapterId: number; version: number }) => {
    const response = await axios.delete<IBuilderAggregate>(`${API_BASE}/${courseId}/chapters/${chapterId}`, { params: { version } });
    return response.data;
  },
  writeOptions,
);

// Sắp xếp lại thứ tự các chương
export const reorderCurriculum = createAsyncThunk(
  'courseBuilder/reorder_curriculum',
  async ({ courseId, version, chapters }: { courseId: number; version: number; chapters: { id: number; lessonIds: number[] }[] }) => {
    const response = await axios.put<IBuilderAggregate>(`${API_BASE}/${courseId}/curriculum/order`, { version, chapters });
    return response.data;
  },
  writeOptions,
);

// ==========================================
// CÁC HÀM CHO LESSON
// ==========================================
// 1. Thêm bài học
export const createLesson = createAsyncThunk(
  'courseBuilder/create_lesson',
  async ({ courseId, chapterId, version, lesson }: { courseId: number; chapterId: number; version: number; lesson: ILessonInput }) => {
    const response = await axios.post<IBuilderAggregate>(`${API_BASE}/${courseId}/chapters/${chapterId}/lessons`, { version, ...lesson });
    return response.data;
  },
  writeOptions,
);

// 2. Cập nhật bài học
export const updateLesson = createAsyncThunk(
  'courseBuilder/update_lesson',
  async ({ courseId, lessonId, version, lesson }: { courseId: number; lessonId: number; version: number; lesson: ILessonInput }) => {
    const response = await axios.put<IBuilderAggregate>(`${API_BASE}/${courseId}/lessons/${lessonId}`, { version, ...lesson });
    return response.data;
  },
  writeOptions,
);

// 3. Xóa bài học
export const deleteLesson = createAsyncThunk(
  'courseBuilder/delete_lesson',
  async ({ courseId, lessonId, version }: { courseId: number; lessonId: number; version: number }) => {
    const response = await axios.delete<IBuilderAggregate>(`${API_BASE}/${courseId}/lessons/${lessonId}`, { params: { version } });
    return response.data;
  },
  writeOptions,
);

// Video và tài liệu dùng chung luồng upload, thay thế và cập nhật Builder.
const lessonMediaPath = ({ courseId, lessonId, kind, materialId }: ILessonMediaTarget) => {
  const base = `${API_BASE}/${courseId}/lessons/${lessonId}`;
  if (kind === 'video') return `${base}/video`;
  return materialId == null ? `${base}/materials` : `${base}/materials/${materialId}`;
};

export const uploadLessonMedia = createAsyncThunk(
  'courseBuilder/upload_lesson_media',
  async (args: ILessonMediaTarget & { version: number; file: File; onProgress?: (percent: number | null) => void }) => {
    const form = new FormData();
    form.append('file', args.file);
    const durationSeconds = args.kind === 'video' ? await readVideoDuration(args.file) : null;
    const replacing = args.kind === 'material' && args.materialId != null;
    const response = await axios.request<IBuilderAggregate>({
      method: replacing ? 'PUT' : 'POST',
      url: lessonMediaPath(args) + (replacing ? '/file' : ''),
      params: { version: args.version, ...(durationSeconds == null ? {} : { durationSeconds }) },
      data: form,
      // Video lớn có thể cần lâu hơn timeout mặc định 60 giây của ứng dụng.
      timeout: 0,
      onUploadProgress: event => args.onProgress?.(event.total ? Math.min(100, Math.round((event.loaded / event.total) * 100)) : null),
    });
    return response.data;
  },
  writeOptions,
);

export const removeLessonMedia = createAsyncThunk(
  'courseBuilder/remove_lesson_media',
  async (args: ILessonMediaTarget & { version: number }) => {
    const response = await axios.delete<IBuilderAggregate>(lessonMediaPath(args), { params: { version: args.version } });
    return response.data;
  },
  writeOptions,
);

export const renameLessonMaterial = createAsyncThunk(
  'courseBuilder/rename_lesson_material',
  async (args: { courseId: number; lessonId: number; materialId: number; version: number; displayName: string }) => {
    const response = await axios.put<IBuilderAggregate>(lessonMediaPath({ ...args, kind: 'material' }), {
      version: args.version,
      displayName: args.displayName,
    });
    return response.data;
  },
  writeOptions,
);

// xem trước video mỗi lesson
// export const getLessonMediaAccess = async (args: ILessonMediaTarget, playback = false) =>
//   (await axios.get<IMediaAccess>(`${lessonMediaPath(args)}/${playback ? 'playback' : 'download'}`)).data;
export const getLessonMediaAccess = async (args: ILessonMediaTarget, preview = false) => {
  const action = preview ? (args.kind === 'video' ? 'playback' : 'preview') : 'download';
  return (await axios.get<IMediaAccess>(`${lessonMediaPath(args)}/${action}`)).data;
};

export const reorderLessonMaterials = createAsyncThunk(
  'courseBuilder/reorder_lesson_materials',
  async (args: { courseId: number; lessonId: number; version: number; materialIds: number[] }) => {
    const response = await axios.put<IBuilderAggregate>(`${API_BASE}/${args.courseId}/lessons/${args.lessonId}/materials/order`, {
      version: args.version,
      materialIds: args.materialIds,
    });
    return response.data;
  },
  writeOptions,
);

export const moveLesson = createAsyncThunk(
  'courseBuilder/move_lesson',
  async (args: { courseId: number; lessonId: number; chapterId: number; title: string; version: number }) => {
    const response = await axios.put<IBuilderAggregate>(`${API_BASE}/${args.courseId}/lessons/${args.lessonId}/move`, {
      version: args.version,
      chapterId: args.chapterId,
      title: args.title,
    });
    return response.data;
  },
  writeOptions,
);

export const updateVideoDuration = createAsyncThunk(
  'courseBuilder/update_video_duration',
  async (args: { courseId: number; lessonId: number; version: number; durationSeconds: number }) => {
    const response = await axios.put<IBuilderAggregate>(`${API_BASE}/${args.courseId}/lessons/${args.lessonId}/video/duration`, {
      version: args.version,
      durationSeconds: args.durationSeconds,
    });
    return response.data;
  },
  writeOptions,
);

// =========================================================================
// API THUNKS CHO EXAMS & QUESTIONS
// =========================================================================
export const getCourseExams = createAsyncThunk(
  'courseBuilder/get_course_exams',
  async (courseId: number) => (await axios.get<IExamSummaryDTO[]>(`/api/instructor/courses/${courseId}/exams`)).data,
);

export const createCourseExam = createAsyncThunk(
  'courseBuilder/create_course_exam',
  async ({ courseId, input }: { courseId: number; input: ICreateExamInput }) =>
    (await axios.post<IExamDetailDTO>(`/api/instructor/courses/${courseId}/exams`, input)).data,
);

export const getChapterExams = createAsyncThunk(
  'courseBuilder/get_chapter_exams',
  async ({ courseId, chapterId }: { courseId: number; chapterId: number }) =>
    (await axios.get<IExamSummaryDTO[]>(`/api/instructor/courses/${courseId}/chapters/${chapterId}/exams`)).data,
);

export const createChapterExam = createAsyncThunk(
  'courseBuilder/create_chapter_exam',
  async ({ courseId, chapterId, input }: { courseId: number; chapterId: number; input: ICreateExamInput }) =>
    (await axios.post<IExamDetailDTO>(`/api/instructor/courses/${courseId}/chapters/${chapterId}/exams`, input)).data,
);

export const getLessonExams = createAsyncThunk(
  'courseBuilder/get_lesson_exams',
  async ({ courseId, lessonId }: { courseId: number; lessonId: number }) =>
    (await axios.get<IExamSummaryDTO[]>(`/api/instructor/courses/${courseId}/lessons/${lessonId}/exams`)).data,
);

export const createLessonExam = createAsyncThunk(
  'courseBuilder/create_lesson_exam',
  async ({
    courseId,
    lessonId,
    input,
    isPractice = false,
  }: {
    courseId: number;
    lessonId: number;
    input: ICreateExamInput;
    isPractice?: boolean;
  }) =>
    (await axios.post<IExamDetailDTO>(`/api/instructor/courses/${courseId}/lessons/${lessonId}/exams?practice=${isPractice}`, input)).data,
);

export const getLessonCuePoints = createAsyncThunk(
  'courseBuilder/get_lesson_cue_points',
  async ({ courseId, lessonId }: { courseId: number; lessonId: number }) =>
    (await axios.get<IExamSummaryDTO[]>(`/api/instructor/courses/${courseId}/lessons/${lessonId}/cue-points`)).data,
);

export const createLessonCuePoint = createAsyncThunk(
  'courseBuilder/create_lesson_cue_point',
  async ({ courseId, lessonId, input }: { courseId: number; lessonId: number; input: ICreateExamInput }) =>
    (await axios.post<IExamDetailDTO>(`/api/instructor/courses/${courseId}/lessons/${lessonId}/cue-points`, input)).data,
);

export const getExamDetail = createAsyncThunk(
  'courseBuilder/get_exam_detail',
  async ({ courseId, examId }: { courseId: number; examId: number }) =>
    (await axios.get<IExamDetailDTO>(`/api/instructor/courses/${courseId}/exams/${examId}`)).data,
);

export const updateExam = createAsyncThunk(
  'courseBuilder/update_exam',
  async ({ courseId, examId, input }: { courseId: number; examId: number; input: ICreateExamInput }) =>
    (await axios.put<IExamDetailDTO>(`/api/instructor/courses/${courseId}/exams/${examId}`, input)).data,
);

export const deleteExam = createAsyncThunk(
  'courseBuilder/delete_exam',
  async ({ courseId, examId }: { courseId: number; examId: number }) =>
    await axios.delete(`/api/instructor/courses/${courseId}/exams/${examId}`),
);

export const addExamQuestion = createAsyncThunk(
  'courseBuilder/add_exam_question',
  async ({ courseId, examId, input }: { courseId: number; examId: number; input: ICreateQuestionInput }) =>
    (await axios.post<IExamDetailDTO>(`/api/instructor/courses/${courseId}/exams/${examId}/questions`, input)).data,
);

export const updateExamQuestion = createAsyncThunk(
  'courseBuilder/update_exam_question',
  async ({ courseId, examId, questionId, input }: { courseId: number; examId: number; questionId: number; input: ICreateQuestionInput }) =>
    (await axios.put<IExamDetailDTO>(`/api/instructor/courses/${courseId}/exams/${examId}/questions/${questionId}`, input)).data,
);

export const deleteExamQuestion = createAsyncThunk(
  'courseBuilder/delete_exam_question',
  async ({ courseId, examId, questionId }: { courseId: number; examId: number; questionId: number }) =>
    (await axios.delete<IExamDetailDTO>(`/api/instructor/courses/${courseId}/exams/${examId}/questions/${questionId}`)).data,
);

// 4. SLICE DEFINITION (LƯU VÀO REDUX STORE)
// ==========================================
export const CourseBuilderSlice = createSlice({
  name: 'courseBuilder',
  initialState,
  reducers: {
    resetCourseBuilder() {
      return initialState;
    },
  },
  extraReducers(builder) {
    builder
      .addCase(createDraft.fulfilled, (state, action) => {
        state.loading = false;
        state.saving = false;
        state.data = action.payload;
        state.errorMessage = null;
        state.errorStatus = null;
      })
      .addCase(getCourseBuilder.fulfilled, (state, action) => {
        if (state.loadingRequestId !== action.meta.requestId) return;
        state.loadingRequestId = null;
        state.loading = false;
        state.data = action.payload;
        state.errorMessage = null;
        state.errorStatus = null;
      })
      // Khi bất kỳ API nào sửa đổi thành công -> cập nhật cây data mới nhất từ BE
      .addMatcher(
        action =>
          [
            updateBasicInfo.fulfilled.type,
            updatePricing.fulfilled.type,
            submitCourseReview.fulfilled.type,
            withdrawCourseReview.fulfilled.type,
            uploadThumbnail.fulfilled.type,
            deleteThumbnail.fulfilled.type,
            uploadIntroVideo.fulfilled.type,
            deleteIntroVideo.fulfilled.type,
            createChapter.fulfilled.type,
            updateChapter.fulfilled.type,
            deleteChapter.fulfilled.type,
            reorderCurriculum.fulfilled.type,
            createLesson.fulfilled.type,
            updateLesson.fulfilled.type,
            deleteLesson.fulfilled.type,
            uploadLessonMedia.fulfilled.type,
            removeLessonMedia.fulfilled.type,
            renameLessonMaterial.fulfilled.type,
            reorderLessonMaterials.fulfilled.type,
            moveLesson.fulfilled.type,
            updateVideoDuration.fulfilled.type,
          ].includes(action.type),
        (state, action: any) => {
          state.saving = false;
          state.data = action.payload;
          state.lastSavedTime = getCurrentTime();
          state.errorMessage = null;
        },
      )
      .addMatcher(isPending(getCourseBuilder), (state, action) => {
        state.loadingRequestId = action.meta.requestId;
        state.loading = true;
        state.errorMessage = null;
      })
      .addMatcher(
        isPending(
          createDraft,
          updateBasicInfo,
          updatePricing,
          submitCourseReview,
          withdrawCourseReview,
          uploadThumbnail,
          deleteThumbnail,
          uploadIntroVideo,
          deleteIntroVideo,
          createChapter,
          updateChapter,
          deleteChapter,
          reorderCurriculum,
          createLesson,
          updateLesson,
          deleteLesson,
          uploadLessonMedia,
          removeLessonMedia,
          renameLessonMaterial,
          reorderLessonMaterials,
          moveLesson,
          updateVideoDuration,
        ),
        state => {
          state.saving = true;
          state.errorMessage = null;
        },
      )
      .addMatcher(
        isRejected(
          createDraft,
          getCourseBuilder,
          updateBasicInfo,
          updatePricing,
          submitCourseReview,
          withdrawCourseReview,
          uploadThumbnail,
          deleteThumbnail,
          uploadIntroVideo,
          deleteIntroVideo,
          createChapter,
          updateChapter,
          deleteChapter,
          reorderCurriculum,
          createLesson,
          updateLesson,
          deleteLesson,
          uploadLessonMedia,
          removeLessonMedia,
          renameLessonMaterial,
          reorderLessonMaterials,
          moveLesson,
          updateVideoDuration,
        ),
        (state, action: any) => {
          if (getCourseBuilder.rejected.match(action)) {
            if (state.loadingRequestId !== action.meta.requestId) return;
            state.loadingRequestId = null;
            state.loading = false;
            if (action.meta.aborted) return;
          }
          state.loading = false;
          state.saving = false;
          state.errorStatus = builderErrorStatus(action.error);
          state.errorMessage = builderErrorText(action.error, action.error?.message);
        },
      );
  },
});

export const { resetCourseBuilder } = CourseBuilderSlice.actions;
export default CourseBuilderSlice.reducer;
