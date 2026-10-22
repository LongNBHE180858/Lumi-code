import { RecordStatus } from 'app/shared/model/enumerations/record-status.model';
import { IStudyClass } from 'app/shared/model/study-class.model';

export interface IExam {
  id?: number;
  title?: string;
  durationMinutes?: number;
  passScore?: number;
  maxAttempts?: number | null;
  shuffleQuestions?: boolean | null;
  status?: keyof typeof RecordStatus | null;
  isDeleted?: boolean | null;
  studyClass?: IStudyClass | null;
}

export const defaultValue: Readonly<IExam> = {
  shuffleQuestions: false,
  isDeleted: false,
};
