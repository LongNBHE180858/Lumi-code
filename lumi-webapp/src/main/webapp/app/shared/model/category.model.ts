export interface ICategory {
  id?: number;
  name?: string;
  description?: string | null;
  parent?: ICategory | null;
}

export const defaultValue: Readonly<ICategory> = {};
