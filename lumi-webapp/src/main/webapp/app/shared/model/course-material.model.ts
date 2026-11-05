import dayjs from 'dayjs';

import { ILesson } from 'app/shared/model/lesson.model';

export interface ICourseMaterial {
  id?: number;
  fileName?: string | null;
  displayName?: string | null;
  mimeType?: string | null;
  orderIndex?: number | null;
  fileType?: string;
  fileUrl?: string;
  fileSizeBytes?: number | null;
  fileObjectKey?: string | null;
  updatedAt?: dayjs.Dayjs;
  lesson?: ILesson;
}

export const defaultValue: Readonly<ICourseMaterial> = {};
