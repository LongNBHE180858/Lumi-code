import axios from 'axios';

export type CatalogKind = 'subjects' | 'grades';

export interface CatalogItem {
  id: number;
  name: string;
  description: string | null;
  level: number | null;
}

export type CatalogInput = Pick<CatalogItem, 'name' | 'description' | 'level'>;

const url = (kind: CatalogKind) => `api/admin/catalog/${kind}`;

export const catalogApi = {
  list: (kind: CatalogKind) => axios.get<CatalogItem[]>(url(kind)),
  create: (kind: CatalogKind, input: CatalogInput) => axios.post<CatalogItem>(url(kind), input),
  update: (kind: CatalogKind, id: number, input: CatalogInput) => axios.put<CatalogItem>(`${url(kind)}/${id}`, input),
};
