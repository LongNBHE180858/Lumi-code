export interface IKnowledgeGraphNode {
  id: number;
  title: string;
  type?: string | null;
  subjectId?: number;
  // Local coordinate for visual canvas
  x?: number;
  y?: number;
}

export interface IKnowledgeGraphEdge {
  id?: number | null;
  fromNodeId: number;
  toNodeId: number;
  relationType?: string;
}

export interface IKnowledgeGraph {
  subjectId: number;
  subjectName?: string;
  subjectDescription?: string;
  nodes: IKnowledgeGraphNode[];
  edges: IKnowledgeGraphEdge[];
}

export interface IKnowledgeGraphSubject {
  id: number;
  name: string;
  description?: string;
}

export interface IGraphCycleError {
  message?: string;
  cyclePath?: string;
  cycleNodeIds?: number[];
}
