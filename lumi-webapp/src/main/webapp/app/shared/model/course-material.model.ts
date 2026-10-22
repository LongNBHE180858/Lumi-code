import { ILesson } from 'app/shared/model/lesson.model';

export interface ICourseMaterial {
  fileName?: string | null;
  displayName?: string | null;
  mimeType?: string | null;
  orderIndex?: number | null;

  id?: number;
  fileType?: string;
  fileUrl?: string;
  fileSizeBytes?: number | null;
  lesson?: ILesson;
}

export const defaultValue: Readonly<ICourseMaterial> = {};
