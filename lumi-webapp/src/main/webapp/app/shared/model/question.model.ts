import dayjs from 'dayjs';

import { IAiGeneratedContent } from 'app/shared/model/ai-generated-content.model';
import { IGrade } from 'app/shared/model/grade.model';
import { IQuestionBank } from 'app/shared/model/question-bank.model';
import { ISubject } from 'app/shared/model/subject.model';
import { IUser } from 'app/shared/model/user.model';

export interface IQuestion {
  id?: number;
  content?: string;
  type?: string;
  difficulty?: number | null;
  explanation?: string | null;
  isDeleted?: boolean | null;
  sourceType?: string;
  contentHash?: string | null;
  visibility?: string;
  version?: number;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs;
  bank?: IQuestionBank | null;
  createdBy?: IUser;
  aiContent?: IAiGeneratedContent | null;
  subject?: ISubject | null;
  grade?: IGrade | null;
  previousQuestion?: IQuestion | null;
}

export const defaultValue: Readonly<IQuestion> = {
  isDeleted: false,
};
