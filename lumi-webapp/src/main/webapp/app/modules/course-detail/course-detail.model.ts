import type { InstructorPublicProfile } from 'app/modules/instructor-profile/instructor-profile.model';

export interface CourseRatingSummary {
  averageRating: number | null;
  totalReviews: number;
  oneStarCount: number;
  twoStarCount: number;
  threeStarCount: number;
  fourStarCount: number;
  fiveStarCount: number;
}

export interface CourseLesson {
  id: number;
  title: string;
  orderIndex: number;
  contentType: string | null;
  durationMinutes: number | null;
}

export interface CourseChapter {
  id: number;
  title: string;
  orderIndex: number;
  lessonCount: number;
  lessons: CourseLesson[];
}

export interface CourseOverview {
  id: number;
  title: string;
  description: string | null;
  thumbnailUrl: string | null;
  subjectId: number | null;
  subjectName: string | null;
  gradeId: number | null;
  gradeLevel: number | null;
  gradeName: string | null;
  price: number | null;
  validDurationDays: number | null;
  author: InstructorPublicProfile | null;
  chapterCount: number;
  lessonCount: number;
  ratingSummary: CourseRatingSummary;
  chapters: CourseChapter[];
}

export interface CourseReviewItem {
  id: number;
  displayName: string | null;
  imageUrl: string | null;
  rating: number;
  content: string | null;
  createdAt: string | null;
}

export interface CourseReviewsPage {
  courseId: number;
  ratingSummary: CourseRatingSummary;
  content: CourseReviewItem[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export type CourseReviewSort = 'createdAt,desc' | 'rating,desc' | 'rating,asc';
