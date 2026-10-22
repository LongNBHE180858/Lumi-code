import { IExamAttempt } from 'app/shared/model/exam-attempt.model';
import { IQuestionOption } from 'app/shared/model/question-option.model';
import { IQuestion } from 'app/shared/model/question.model';

export interface IExamAnswer {
  id?: number;
  textAnswer?: string | null;
  isCorrect?: boolean | null;
  score?: number | null;
  attempt?: IExamAttempt;
  question?: IQuestion;
  option?: IQuestionOption | null;
}

export const defaultValue: Readonly<IExamAnswer> = {
  isCorrect: false,
};
