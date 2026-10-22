import { ICourse } from 'app/shared/model/course.model';
import { ILesson } from 'app/shared/model/lesson.model';

export enum FeedbackType {
  BUG_REPORT = 'BUG_REPORT',
  FEATURE_REQUEST = 'FEATURE_REQUEST',
  UI_UX = 'UI_UX',
  GENERAL = 'GENERAL',
}

export enum FeedbackStatus {
  NEW = 'NEW',
  IN_REVIEW = 'IN_REVIEW',
  RESOLVED = 'RESOLVED',
  DISMISSED = 'DISMISSED',
}

export interface IFeedback {
  id?: number;
  title?: string;
  type?: FeedbackType;
  content?: string;
  contactEmail?: string;
  status?: FeedbackStatus;
  adminNotes?: string | null;
  isDeleted?: boolean;
  createdAt?: string;
  updatedAt?: string;
  course?: ICourse | null;
  lesson?: ILesson | null;
  senderIdentity?: string;
}

export interface IFeedbackCreate {
  title: string;
  type: FeedbackType;
  content: string;
  contactEmail: string;
  courseId?: number | null;
  lessonId?: number | null;
}
