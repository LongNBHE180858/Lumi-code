import { IFlashcardDeck } from 'app/shared/model/flashcard-deck.model';

export interface IFlashcard {
  id?: number;
  frontText?: string;
  backText?: string;
  deck?: IFlashcardDeck;
}

export const defaultValue: Readonly<IFlashcard> = {};
