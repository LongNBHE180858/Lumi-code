import { IQuestionBank } from 'app/shared/model/question-bank.model';

export interface IQuestion {
  id?: number;
  content?: string;
  type?: string;
  difficulty?: number | null;
  explanation?: string | null;
  isDeleted?: boolean | null;
  bank?: IQuestionBank | null;
}

export const defaultValue: Readonly<IQuestion> = {
  isDeleted: false,
};
