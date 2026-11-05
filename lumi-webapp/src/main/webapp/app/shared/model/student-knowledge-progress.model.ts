import dayjs from 'dayjs';

import { IKnowledgeNode } from 'app/shared/model/knowledge-node.model';
import { IUser } from 'app/shared/model/user.model';

export interface IStudentKnowledgeProgress {
  id?: number;
  masteryScore?: number | null;
  evidenceCount?: number;
  calculationVersion?: string | null;
  updatedAt?: dayjs.Dayjs;
  student?: IUser;
  knowledgeNode?: IKnowledgeNode;
}

export const defaultValue: Readonly<IStudentKnowledgeProgress> = {};
