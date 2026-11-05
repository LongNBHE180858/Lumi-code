import dayjs from 'dayjs';

import { IChapter } from 'app/shared/model/chapter.model';
import { RecordStatus } from 'app/shared/model/enumerations/record-status.model';

export interface ILesson {
  id?: number;
  title?: string;
  description?: string | null;
  contentType?: string;
  videoUrl?: string | null;
  videoFileName?: string | null;
  videoSizeBytes?: number | null;
  orderIndex?: number;
  isPreview?: boolean | null;
  durationMinutes?: number | null;
  status?: keyof typeof RecordStatus | null;
  durationSeconds?: number | null;
  videoObjectKey?: string | null;
  updatedAt?: dayjs.Dayjs;
  chapter?: IChapter;
}

export const defaultValue: Readonly<ILesson> = {
  isPreview: false,
};
