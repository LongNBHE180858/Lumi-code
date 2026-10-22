export interface ISubject {
  id?: number;
  name?: string;
  description?: string | null;
}

export const defaultValue: Readonly<ISubject> = {};
