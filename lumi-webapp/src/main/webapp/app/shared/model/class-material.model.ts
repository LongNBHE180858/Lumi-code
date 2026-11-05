import dayjs from 'dayjs';

import { IStudyClass } from 'app/shared/model/study-class.model';

export interface IClassMaterial {
  id?: number;
  title?: string;
  fileType?: string;
  fileUrl?: string;
  fileSizeBytes?: number;
  uploadedAt?: dayjs.Dayjs | null;
  fileObjectKey?: string | null;
  updatedAt?: dayjs.Dayjs;
  studyClass?: IStudyClass;
}

export const defaultValue: Readonly<IClassMaterial> = {};
