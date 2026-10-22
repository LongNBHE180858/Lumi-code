import { IExam } from 'app/shared/model/exam.model';
import { IQuestion } from 'app/shared/model/question.model';

export interface IExamQuestion {
  id?: number;
  orderIndex?: number;
  score?: number;
  exam?: IExam;
  question?: IQuestion;
}

export const defaultValue: Readonly<IExamQuestion> = {};
