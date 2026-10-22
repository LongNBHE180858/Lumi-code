import dayjs from 'dayjs';

import { ClassJoinMode } from 'app/shared/model/enumerations/class-join-mode.model';
import { RecordStatus } from 'app/shared/model/enumerations/record-status.model';
import { IGrade } from 'app/shared/model/grade.model';
import { ISubject } from 'app/shared/model/subject.model';
import { IUser } from 'app/shared/model/user.model';

export interface IStudyClass {
  id?: number;
  name?: string;
  description?: string | null;
  joinCode?: string | null;
  joinMode?: keyof typeof ClassJoinMode | null;
  maxStudents?: number | null;
  currentStudents?: number | null;
  status?: keyof typeof RecordStatus | null;
  isDeleted?: boolean | null;
  createdAt?: dayjs.Dayjs | null;
  instructor?: IUser;
  subject?: ISubject | null;
  grade?: IGrade | null;
}

export const defaultValue: Readonly<IStudyClass> = {
  isDeleted: false,
};
