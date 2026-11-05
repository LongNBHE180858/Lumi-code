import dayjs from 'dayjs';

import { ICourse } from 'app/shared/model/course.model';
import { ILearningDocument } from 'app/shared/model/learning-document.model';
import { ILesson } from 'app/shared/model/lesson.model';
import { IUser } from 'app/shared/model/user.model';

export interface IAiGeneratedContent {
  id?: number;
  contentType?: string;
  requestHash?: string | null;
  generationConfig?: string;
  sourceVersion?: string | null;
  modelName?: string | null;
  promptVersion?: string | null;
  content?: string;
  generationStatus?: string;
  visibility?: string;
  version?: number;
  reviewStatus?: string;
  reviewedAt?: dayjs.Dayjs | null;
  reviewerFeedback?: string | null;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs;
  sourceQuestionId?: number | null;
  contentHash?: string | null;
  createdBy?: IUser;
  document?: ILearningDocument | null;
  lesson?: ILesson | null;
  reviewedBy?: IUser | null;
  course?: ICourse | null;
}

export const defaultValue: Readonly<IAiGeneratedContent> = {};
