export type ClassStatus = 'active' | 'archived' | 'pending';
export type StudentStatus = 'active' | 'pending';

export interface ClassItem {
  id: string;
  name: string;
  code: string;
  course: string;
  grade: string;
  subject: string;
  studentCount: number;
  status: ClassStatus;
  password?: string;
  description?: string;
  createdDate?: string;
  teacherName?: string;
}

export interface StudentItem {
  id: string;
  name: string;
  email: string;
  username: string;
  joinedDate: string;
  progress: number;
  status: StudentStatus;
}

export interface ClassStatistics {
  totalClasses: number;
  totalStudents: number;
  pendingRequests: number;
}

export interface CreateClassFormValues {
  name: string;
  course: string;
  grade: string;
  subject: string;
  password?: string;
  description?: string;
}

export interface EditClassFormValues {
  name: string;
  password?: string;
  status: ClassStatus;
  description?: string;
}

export interface AttachmentFile {
  name: string;
  size: string;
  type: string;
}

export interface AssignTaskFormValues {
  title: string;
  classId: string;
  instructions: string;
  attachments: AttachmentFile[];
  submissionType: string;
  assignTime: string;
  dueDate: string;
  allowLateSubmission: boolean;
  notifyStudents: boolean;
}

export const INITIAL_CLASSES: ClassItem[] = [
  {
    id: 'toan-6a',
    name: 'Toán 6A',
    code: 'LUMI-MATH6A',
    course: 'Toán lớp 6',
    grade: 'Lớp 6',
    subject: 'Toán',
    studentCount: 32,
    status: 'active',
    password: '••••••••',
    description: 'Lớp bồi dưỡng và củng cố kiến thức môn Toán chương trình lớp 6 theo sách giáo khoa mới.',
    createdDate: '15/08/2026',
    teacherName: 'Lan Nguyễn',
  },
  {
    id: 'toan-7b',
    name: 'Toán 7B',
    code: 'LUMI-MATH7B',
    course: 'Đại số cơ bản',
    grade: 'Lớp 7',
    subject: 'Toán',
    studentCount: 27,
    status: 'active',
    password: '••••••••',
    description: 'Lớp học củng cố nền tảng Đại số lớp 7 với các chuyên đề số thực, biểu thức đại số.',
    createdDate: '18/08/2026',
    teacherName: 'Lan Nguyễn',
  },
  {
    id: 'su-8a',
    name: 'Lịch sử 8A',
    code: 'LUMI-HIS8A',
    course: 'Lịch sử Việt Nam',
    grade: 'Lớp 8',
    subject: 'Lịch sử',
    studentCount: 29,
    status: 'active',
    password: '••••••••',
    description: 'Lớp tìm hiểu và phân tích các giai đoạn hào hùng trong lịch sử Việt Nam thế kỷ XIX – XX.',
    createdDate: '20/08/2026',
    teacherName: 'Lan Nguyễn',
  },
];

export const INITIAL_STUDENTS_TOAN6A: StudentItem[] = [
  {
    id: 's-1',
    name: 'Nguyễn Minh Anh',
    email: 'minhanh@email.com',
    username: 'minhanh',
    joinedDate: '01/09/2026',
    progress: 78,
    status: 'active',
  },
  {
    id: 's-2',
    name: 'Trần Gia Bảo',
    email: 'giabao@email.com',
    username: 'giabao',
    joinedDate: '01/09/2026',
    progress: 65,
    status: 'active',
  },
  {
    id: 's-3',
    name: 'Lê Khánh Linh',
    email: 'khanhlinh@email.com',
    username: 'khanhlinh',
    joinedDate: '—',
    progress: 0,
    status: 'pending',
  },
  {
    id: 's-4',
    name: 'Phạm Đức Huy',
    email: 'duchuy@email.com',
    username: 'duchuy',
    joinedDate: '03/09/2026',
    progress: 60,
    status: 'active',
  },
];
