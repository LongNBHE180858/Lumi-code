import { IExamAttempt } from 'app/shared/model/exam-attempt.model';

export interface IProctorSession {
  id?: number;
  faceVerified?: boolean | null;
  warningCount?: number | null;
  status?: string | null;
  attempt?: IExamAttempt;
}

export const defaultValue: Readonly<IProctorSession> = {
  faceVerified: false,
};
