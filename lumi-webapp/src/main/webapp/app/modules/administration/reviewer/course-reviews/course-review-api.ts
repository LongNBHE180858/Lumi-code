import axios from 'axios';

export type CourseReviewRequestStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'WITHDRAWN';

export interface ICourseReviewUser {
  id: number;
  login: string;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  imageUrl?: string | null;
}

export interface ICourseReviewSubject {
  id: number;
  name: string;
}

export interface ICourseReviewGrade {
  id: number;
  name: string;
  level?: number;
}

export interface ICourseReviewCourse {
  id: number;
  title: string;
  description?: string | null;
  shortDescription?: string | null;
  thumbnailUrl?: string | null;
  price?: number | null;
  version: number;
  status: 'DRAFT' | 'PENDING' | 'ACTIVE' | 'HIDDEN' | 'ARCHIVED' | 'BLOCKED';
  bookSeries?: string | null;
  targetAudience?: string | null;
  prerequisites?: string | null;
  learningObjectives?: string[] | null;
  introVideoUrl?: string | null;
  validDurationDays?: number | null;
  accessDurationValue?: number | null;
  accessDurationUnit?: string | null;
  authorDisplayName?: string | null;
  createdAt?: string;
  updatedAt?: string;
  author?: ICourseReviewUser | null;
  subject?: ICourseReviewSubject | null;
  grade?: ICourseReviewGrade | null;
  chapterCount?: number;
  lessonCount?: number;
  videoCount?: number;
  totalDurationHours?: number;
}

export interface ICourseReviewRequest {
  id: number;
  courseVersion: number;
  status: CourseReviewRequestStatus;
  submissionNote?: string | null;
  rightsConfirmedAt: string;
  submittedAt: string;
  reviewedAt?: string | null;
  reviewerFeedback?: string | null;
  course: ICourseReviewCourse;
  submittedBy: ICourseReviewUser;
  reviewedBy?: ICourseReviewUser | null;
}

export interface ICourseReviewMaterial {
  id: number;
  title?: string | null;
  displayName?: string | null;
  fileName?: string | null;
  fileUrl?: string | null;
  fileSizeBytes?: number | null;
  fileType?: string | null;
  materialType?: string | null;
  orderIndex?: number | null;
}

export interface IReviewQuestionOption {
  id?: number;
  content: string;
  isCorrect: boolean;
  orderIndex?: number;
}

export interface IReviewQuestionDetail {
  id: number;
  content: string;
  type?: string;
  difficulty?: string;
  explanation?: string;
  score?: number;
  orderIndex?: number;
  options: IReviewQuestionOption[];
}

export interface IReviewExamSummary {
  id: number;
  title: string;
  assessmentType: string;
  durationMinutes?: number | null;
  passScore?: number | null;
  maxAttempts?: number | null;
  shuffleQuestions?: boolean;
  videoTriggerSeconds?: number | null;
  courseId?: number | null;
  chapterId?: number | null;
  lessonId?: number | null;
  questionCount: number;
  totalScore?: number | null;
  createdAt?: string;
}

export interface IReviewExamDetail extends IReviewExamSummary {
  questions: IReviewQuestionDetail[];
}

export interface ICourseReviewLesson {
  id: number;
  title: string;
  description?: string | null;
  contentType: string;
  videoUrl?: string | null;
  videoFileName?: string | null;
  videoSizeBytes?: number | null;
  orderIndex: number;
  isPreview?: boolean;
  durationMinutes?: number | null;
  durationSeconds?: number | null;
  status?: string;
  materials?: ICourseReviewMaterial[];
  exams?: IReviewExamSummary[];
  cuePoints?: IReviewExamSummary[];
  chapter?: {
    id: number;
    title: string;
    description?: string | null;
    orderIndex?: number;
  };
}

export interface ICourseReviewChapter {
  id: number;
  title: string;
  description?: string | null;
  orderIndex: number;
  lessons: ICourseReviewLesson[];
  exams?: IReviewExamSummary[];
}

export const reviewStatusLabels: Record<CourseReviewRequestStatus, string> = {
  PENDING: 'Chờ duyệt',
  APPROVED: 'Đã duyệt',
  REJECTED: 'Đã từ chối',
  WITHDRAWN: 'Đã rút lại',
};

export const courseStatusLabels: Record<string, string> = {
  DRAFT: 'Bản nháp',
  PENDING: 'Chờ duyệt',
  ACTIVE: 'Đã xuất bản',
  HIDDEN: 'Đã tạm ẩn',
  ARCHIVED: 'Lưu trữ',
  BLOCKED: 'Bị khóa',
};

export const getSubjectGradient = (subjectName?: string): string => {
  const s = (subjectName || '').toLowerCase();
  if (s.includes('toán')) return 'linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%)';
  if (s.includes('văn') || s.includes('ngữ')) return 'linear-gradient(135deg, #881337 0%, #f43f5e 100%)';
  if (s.includes('anh') || s.includes('tiếng')) return 'linear-gradient(135deg, #4c1d95 0%, #8b5cf6 100%)';
  if (s.includes('lý') || s.includes('hóa') || s.includes('sinh') || s.includes('khoa'))
    return 'linear-gradient(135deg, #064e3b 0%, #10b981 100%)';
  if (s.includes('sử') || s.includes('địa')) return 'linear-gradient(135deg, #78350f 0%, #f59e0b 100%)';
  return 'linear-gradient(135deg, #0f172a 0%, #475569 100%)';
};

export const getSubjectClass = (subjectName?: string): string => {
  const s = (subjectName || '').toLowerCase();
  if (s.includes('toán')) return 'tag-subject-math';
  if (s.includes('văn') || s.includes('ngữ')) return 'tag-subject-lit';
  if (s.includes('anh') || s.includes('tiếng')) return 'tag-subject-eng';
  if (s.includes('lý') || s.includes('hóa') || s.includes('sinh') || s.includes('khoa')) return 'tag-subject-sci';
  return 'tag-subject-math';
};

export const formatPrice = (price?: number | null): string => {
  if (price == null) return 'Chưa đặt học phí';
  if (price === 0) return 'Miễn phí';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);
};

export const accessDurationText = (course?: { accessDurationValue?: number | null; accessDurationUnit?: string | null }): string => {
  if (!course?.accessDurationValue || !course?.accessDurationUnit) return 'Chưa cài đặt thời hạn';
  const unitMap: Record<string, string> = {
    DAY: 'ngày',
    MONTH: 'tháng',
    YEAR: 'năm',
  };
  return `Truy cập ${course.accessDurationValue} ${unitMap[course.accessDurationUnit] || course.accessDurationUnit}`;
};

export const formatDateTime = (dateString?: string | null): string => {
  if (!dateString) return '—';
  try {
    return new Date(dateString).toLocaleString('vi-VN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
    });
  } catch {
    return dateString;
  }
};

export const getUserDisplayName = (user?: ICourseReviewUser | null, fallback?: string | null): string => {
  if (!user) return fallback || 'Chưa có thông tin';
  const name = [user.firstName, user.lastName].filter(Boolean).join(' ').trim();
  return name || user.login || fallback || 'Chưa có thông tin';
};

export const courseReviewApi = {
  list(params: { page: number; size: number; status?: CourseReviewRequestStatus | 'ALL'; sort?: string }) {
    const queryParams: Record<string, unknown> = {
      page: params.page,
      size: params.size,
      sort: params.sort || 'submittedAt,desc',
    };
    if (params.status && params.status !== 'ALL') {
      queryParams['status.equals'] = params.status;
    }
    return axios.get<ICourseReviewRequest[]>('/api/course-review-requests', { params: queryParams });
  },

  count(status?: CourseReviewRequestStatus) {
    const params: Record<string, unknown> = {};
    if (status) {
      params['status.equals'] = status;
    }
    return axios.get<number>('/api/course-review-requests/count', { params });
  },

  get: (id: number | string) => axios.get<ICourseReviewRequest>(`/api/course-review-requests/${id}`),

  getCourse: (courseId: number | string) => axios.get<ICourseReviewCourse>(`/api/courses/${courseId}`),

  getLessons: (courseId: number | string) => axios.get<ICourseReviewLesson[]>(`/api/courses/${courseId}/lessons`),

  async getBuilderData(courseId: number | string) {
    try {
      const res = await axios.get(`/api/instructor/courses/${courseId}/builder`);
      return res.data;
    } catch {
      return null;
    }
  },

  async getThumbnailUrl(courseId: number | string): Promise<string | null> {
    try {
      const res = await axios.get<{ url: string }>(`/api/instructor/courses/${courseId}/thumbnail/access`);
      return res.data.url;
    } catch {
      return null;
    }
  },

  async getCourseExams(courseId: number | string): Promise<IReviewExamSummary[]> {
    try {
      const res = await axios.get<IReviewExamSummary[]>(`/api/instructor/courses/${courseId}/exams`);
      return res.data || [];
    } catch {
      return [];
    }
  },

  async getChapterExams(courseId: number | string, chapterId: number | string): Promise<IReviewExamSummary[]> {
    try {
      const res = await axios.get<IReviewExamSummary[]>(`/api/instructor/courses/${courseId}/chapters/${chapterId}/exams`);
      return res.data || [];
    } catch {
      return [];
    }
  },

  async getLessonExams(courseId: number | string, lessonId: number | string): Promise<IReviewExamSummary[]> {
    try {
      const res = await axios.get<IReviewExamSummary[]>(`/api/instructor/courses/${courseId}/lessons/${lessonId}/exams`);
      return res.data || [];
    } catch {
      return [];
    }
  },

  async getLessonCuePoints(courseId: number | string, lessonId: number | string): Promise<IReviewExamSummary[]> {
    try {
      const res = await axios.get<IReviewExamSummary[]>(`/api/instructor/courses/${courseId}/lessons/${lessonId}/cue-points`);
      return res.data || [];
    } catch {
      return [];
    }
  },

  async getExamDetail(courseId: number | string, examId: number | string): Promise<IReviewExamDetail | null> {
    try {
      const res = await axios.get<IReviewExamDetail>(`/api/instructor/courses/${courseId}/exams/${examId}`);
      return res.data;
    } catch {
      return null;
    }
  },

  async getVideoPlayback(courseId: number | string, lessonId: number | string): Promise<string | null> {
    try {
      const res = await axios.get<{ url: string }>(`/api/instructor/courses/${courseId}/lessons/${lessonId}/video/playback`);
      return res.data.url;
    } catch {
      return null;
    }
  },

  async getMaterialPreview(courseId: number | string, lessonId: number | string, materialId: number | string): Promise<string | null> {
    try {
      const res = await axios.get<{ url: string }>(
        `/api/instructor/courses/${courseId}/lessons/${lessonId}/materials/${materialId}/preview`,
      );
      return res.data.url;
    } catch {
      return null;
    }
  },

  async getMaterialDownload(courseId: number | string, lessonId: number | string, materialId: number | string): Promise<string | null> {
    try {
      const res = await axios.get<{ url: string }>(
        `/api/instructor/courses/${courseId}/lessons/${lessonId}/materials/${materialId}/download`,
      );
      return res.data.url;
    } catch {
      return null;
    }
  },

  async approve(request: ICourseReviewRequest, feedback?: string) {
    try {
      const res = await axios.post<ICourseReviewRequest>(`/api/course-review-requests/${request.id}/approve`, {
        feedback: feedback || 'Đã kiểm tra và phê duyệt khóa học.',
      });
      return res.data;
    } catch {
      const reviewResponse = await axios.patch<ICourseReviewRequest>(`/api/course-review-requests/${request.id}`, {
        id: request.id,
        status: 'APPROVED',
        reviewedAt: new Date().toISOString(),
        reviewerFeedback: feedback || 'Đã kiểm tra và phê duyệt khóa học.',
      });
      return reviewResponse.data;
    }
  },

  async reject(request: ICourseReviewRequest, reason: string) {
    try {
      const res = await axios.post<ICourseReviewRequest>(`/api/course-review-requests/${request.id}/reject`, {
        reason,
      });
      return res.data;
    } catch {
      const reviewResponse = await axios.patch<ICourseReviewRequest>(`/api/course-review-requests/${request.id}`, {
        id: request.id,
        status: 'REJECTED',
        reviewedAt: new Date().toISOString(),
        reviewerFeedback: reason,
      });
      return reviewResponse.data;
    }
  },

  async hideCourse(courseId: number | string, reason: string): Promise<void> {
    await axios.post(`/api/course-review-requests/courses/${courseId}/hide`, { reason });
  },

  async unhideCourse(courseId: number | string): Promise<void> {
    await axios.post(`/api/course-review-requests/courses/${courseId}/unhide`);
  },

  async requestRevision(courseId: number | string, reason: string): Promise<void> {
    await axios.post(`/api/course-review-requests/courses/${courseId}/request-revision`, { reason });
  },
};
