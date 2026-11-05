import dayjs from 'dayjs';

import { ICourse } from 'app/shared/model/course.model';
import { IUser } from 'app/shared/model/user.model';

export interface ICourseReviewRequest {
  id?: number;
  courseVersion?: number;
  status?: string;
  submissionNote?: string | null;
  rightsConfirmedAt?: dayjs.Dayjs;
  submittedAt?: dayjs.Dayjs;
  reviewedAt?: dayjs.Dayjs | null;
  reviewerFeedback?: string | null;
  course?: ICourse;
  submittedBy?: IUser;
  reviewedBy?: IUser | null;
}

export const defaultValue: Readonly<ICourseReviewRequest> = {};
