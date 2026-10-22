import { IKnowledgeNode } from 'app/shared/model/knowledge-node.model';

export interface IKnowledgeEdge {
  id?: number;
  relationType?: string;
  fromNode?: IKnowledgeNode;
  toNode?: IKnowledgeNode;
}

export const defaultValue: Readonly<IKnowledgeEdge> = {};
