import { ISubject } from 'app/shared/model/subject.model';
import { IUser } from 'app/shared/model/user.model';

export interface IFlashcardDeck {
  id?: number;
  title?: string;
  isPublic?: boolean | null;
  owner?: IUser;
  subject?: ISubject | null;
}

export const defaultValue: Readonly<IFlashcardDeck> = {
  isPublic: false,
};
