import dayjs from 'dayjs';

import { IClassMaterial } from 'app/shared/model/class-material.model';
import { ICourseMaterial } from 'app/shared/model/course-material.model';
import { IGrade } from 'app/shared/model/grade.model';
import { ISubject } from 'app/shared/model/subject.model';
import { IUser } from 'app/shared/model/user.model';

export interface ILearningDocument {
  id?: number;
  sourceType?: string;
  title?: string;
  fileObjectKey?: string;
  contentHash?: string | null;
  sourceVersion?: number;
  processingStatus?: string;
  indexStatus?: string;
  extractedTextObjectKey?: string | null;
  embeddingModel?: string | null;
  errorMessage?: string | null;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs;
  bookSeries?: string | null;
  sourceMetadata?: string;
  courseMaterial?: ICourseMaterial | null;
  classMaterial?: IClassMaterial | null;
  uploadedBy?: IUser;
  subject?: ISubject | null;
  grade?: IGrade | null;
}

export const defaultValue: Readonly<ILearningDocument> = {};
