import { IQuestion } from 'app/shared/model/question.model';

export interface IQuestionOption {
  id?: number;
  content?: string;
  isCorrect?: boolean | null;
  question?: IQuestion;
}

export const defaultValue: Readonly<IQuestionOption> = {
  isCorrect: false,
};
