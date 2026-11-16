import axios from 'axios';

export type CatalogKind = 'subjects' | 'grades';

export interface SubjectCatalogItem {
  id: number;
  name: string;
  description: string | null;
}

export interface GradeCatalogItem {
  id: number;
  name: string;
  level: number;
}

export interface CatalogItem {
  id: number;
  name: string;
  description: string | null;
  level: number | null;
}

export type CatalogInput = Pick<CatalogItem, 'name' | 'description' | 'level'>;

const url = (kind: CatalogKind) => `api/admin/catalog/${kind}`;

export const catalogApi = {
  async list(kind: CatalogKind): Promise<CatalogItem[]> {
    if (kind === 'subjects') {
      const { data } = await axios.get<SubjectCatalogItem[]>(url(kind));
      return data.map(item => ({ ...item, level: null }));
    }
    const { data } = await axios.get<GradeCatalogItem[]>(url(kind));
    return data.map(item => ({ ...item, description: null }));
  },
  create: (kind: CatalogKind, input: CatalogInput) =>
    kind === 'subjects'
      ? axios.post<SubjectCatalogItem>(url(kind), { name: input.name, description: input.description })
      : axios.post<GradeCatalogItem>(url(kind), { name: input.name, level: input.level }),
  update: (kind: CatalogKind, id: number, input: CatalogInput) =>
    kind === 'subjects'
      ? axios.put<SubjectCatalogItem>(`${url(kind)}/${id}`, { name: input.name, description: input.description })
      : axios.put<GradeCatalogItem>(`${url(kind)}/${id}`, { name: input.name, level: input.level }),
};
