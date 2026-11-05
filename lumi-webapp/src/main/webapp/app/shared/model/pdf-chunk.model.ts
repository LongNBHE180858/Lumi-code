import { ILearningDocument } from 'app/shared/model/learning-document.model';

export interface IPdfChunk {
  id?: number;
  pageNumber?: number | null;
  chunkText?: string;
  embedding?: string | null;
  chunkIndex?: number;
  indexVersion?: number;
  material?: ILearningDocument;
}

export const defaultValue: Readonly<IPdfChunk> = {};
