import { IAssignment } from 'app/shared/model/assignment.model';
import { IUser } from 'app/shared/model/user.model';

export interface IAssignmentStudent {
  id?: number;
  student?: IUser;
  assignment?: IAssignment;
}

export const defaultValue: Readonly<IAssignmentStudent> = {};
