export type CourseFeeType = 'ALL' | 'FREE' | 'PAID';

export interface CourseBrowseRequest {
  keyword: string;
  subjectId: number | null;
  gradeIds: number[];
  feeType: CourseFeeType;
  page: number;
}

export interface CourseItem {
  id: number;
  title: string;
  subjectId: number | null;
  subjectName: string | null;
  gradeId: number | null;
  gradeLevel: number | null;
  gradeName: string | null;
  authorDisplayName: string | null;
  price: number | null;
  lessonCount: number | null;
}

export interface CourseBrowseResponse {
  content: CourseItem[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface CourseFilterOptions {
  subjects: { id: number; name: string }[];
  grades: { id: number; level: number; name: string }[];
}

export const defaultCourseFilters: CourseBrowseRequest = {
  keyword: '',
  subjectId: null,
  gradeIds: [],
  feeType: 'ALL',
  page: 0,
};
