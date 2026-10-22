import { IChapter } from 'app/shared/model/chapter.model';
import { RecordStatus } from 'app/shared/model/enumerations/record-status.model';

export interface ILesson {
  description?: string | null;
  videoUrl?: string | null;
  videoFileName?: string | null;
  videoSizeBytes?: number | null;

  id?: number;
  title?: string;
  contentType?: string;
  orderIndex?: number;
  isPreview?: boolean | null;
  durationMinutes?: number | null;
  durationSeconds?: number | null;
  status?: keyof typeof RecordStatus | null;
  chapter?: IChapter;
}

export const defaultValue: Readonly<ILesson> = {
  isPreview: false,
};
