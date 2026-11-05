import { IKnowledgeNode } from 'app/shared/model/knowledge-node.model';
import { IQuestion } from 'app/shared/model/question.model';

export interface IQuestionKnowledgeLink {
  id?: number;
  weight?: number;
  question?: IQuestion;
  knowledgeNode?: IKnowledgeNode;
}

export const defaultValue: Readonly<IQuestionKnowledgeLink> = {};
