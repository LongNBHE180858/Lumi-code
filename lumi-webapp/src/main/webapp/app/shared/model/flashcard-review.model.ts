import dayjs from 'dayjs';

import { IFlashcard } from 'app/shared/model/flashcard.model';
import { IUser } from 'app/shared/model/user.model';

export interface IFlashcardReview {
  id?: number;
  nextReviewAt?: dayjs.Dayjs | null;
  intervalDays?: number | null;
  student?: IUser;
  flashcard?: IFlashcard;
}

export const defaultValue: Readonly<IFlashcardReview> = {};
