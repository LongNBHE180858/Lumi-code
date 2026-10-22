import { ICourse } from 'app/shared/model/course.model';

export interface IChapter {
  id?: number;
  title?: string;
  orderIndex?: number;
  course?: ICourse;
}

export const defaultValue: Readonly<IChapter> = {};
