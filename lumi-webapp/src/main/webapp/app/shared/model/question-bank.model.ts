import { IUser } from 'app/shared/model/user.model';

export interface IQuestionBank {
  id?: number;
  name?: string;
  instructor?: IUser;
}

export const defaultValue: Readonly<IQuestionBank> = {};
