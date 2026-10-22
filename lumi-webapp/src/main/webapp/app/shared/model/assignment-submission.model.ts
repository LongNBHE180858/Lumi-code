import dayjs from 'dayjs';

import { IAssignment } from 'app/shared/model/assignment.model';
import { IUser } from 'app/shared/model/user.model';

export interface IAssignmentSubmission {
  id?: number;
  content?: string | null;
  score?: number | null;
  teacherFeedback?: string | null;
  status?: string | null;
  submittedAt?: dayjs.Dayjs | null;
  student?: IUser;
  assignment?: IAssignment;
}

export const defaultValue: Readonly<IAssignmentSubmission> = {};
