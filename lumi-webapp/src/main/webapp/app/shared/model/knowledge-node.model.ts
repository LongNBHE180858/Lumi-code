import { ISubject } from 'app/shared/model/subject.model';

export interface IKnowledgeNode {
  id?: number;
  title?: string;
  type?: string | null;
  subject?: ISubject | null;
}

export const defaultValue: Readonly<IKnowledgeNode> = {};
