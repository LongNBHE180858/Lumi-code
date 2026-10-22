import { ICourseMaterial } from 'app/shared/model/course-material.model';

export interface IPdfChunk {
  id?: number;
  pageNumber?: number | null;
  chunkText?: string;
  embedding?: string | null;
  material?: ICourseMaterial;
}

export const defaultValue: Readonly<IPdfChunk> = {};
