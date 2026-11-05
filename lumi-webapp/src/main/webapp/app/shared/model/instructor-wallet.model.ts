import { IUser } from 'app/shared/model/user.model';

export interface IInstructorWallet {
  id?: number;
  pendingAmount?: number | null;
  availableAmount?: number | null;
  rowVersion?: number | null;
  instructor?: IUser;
}

export const defaultValue: Readonly<IInstructorWallet> = {};
