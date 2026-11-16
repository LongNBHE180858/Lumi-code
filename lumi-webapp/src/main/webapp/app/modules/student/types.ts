export type ClassStatus = 'active' | 'archived' | 'pending';
export type StudentStatus = 'active' | 'pending';
export type AssignmentStatus =
  | 'not_started' // Chưa làm
  | 'in_progress' // Đang làm
  | 'overdue' // Quá hạn · Chưa nộp
  | 'submitted' // Đã nộp
  | 'graded'; // Đã chấm

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

export interface AddStudentFormValues {
  name: string;
  email: string;
  username?: string;
}

export interface AttachmentFile {
  name: string;
  size: string;
  type: string;
}

export interface AssignmentItem {
  id: string;
  title: string;
  classId: string;
  className: string;
  courseName: string;
  teacherName: string;
  category: string;
  questionCountInfo: string;
  assignedDate: string;
  dueDate: string;
  status: AssignmentStatus;
  grade?: string;
  instructions: string;
  attachments: AttachmentFile[];
  submissionType: string;
  allowLateSubmission: boolean;
  notifyStudents: boolean;
  studentSubmission?: {
    fileName?: string;
    submittedDate?: string;
    comment?: string;
  };
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

export interface StudentClassEnrollment {
  classId: string;
  className: string;
  classCode: string;
  courseName: string;
  teacherName: string;
  grade: string;
  subject: string;
  studentProgress: number;
  pendingTasksCount: number;
  status: 'active' | 'pending';
  requestDate?: string;
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

export const INITIAL_ASSIGNMENTS: AssignmentItem[] = [
  {
    id: 'task-1',
    title: 'Luyện tập: Ước chung và bội chung',
    classId: 'toan-6a',
    className: 'Toán 6A',
    courseName: 'Toán lớp 6',
    teacherName: 'Lan Nguyễn',
    category: 'Phiếu bài tập',
    questionCountInfo: 'Toán 6A · Phiếu bài tập · 1 tài liệu',
    assignedDate: '04/11/2026',
    dueDate: '11/11/2026 23:59',
    status: 'not_started',
    instructions:
      'Hoàn thành bài 1–5 trong phiếu bài tập đính kèm. Trình bày đầy đủ các bước giải, sau đó tải ảnh hoặc tệp PDF bài làm lên lớp.',
    attachments: [
      {
        name: 'Phieu-bai-tap_UCLN-BCNN.pdf',
        size: '248 KB',
        type: 'PDF · 5 bài tập',
      },
    ],
    submissionType: 'Tải tệp hoặc ảnh bài làm lên lớp',
    allowLateSubmission: false,
    notifyStudents: true,
  },
  {
    id: 'task-2',
    title: 'Luyện tập phép tính với số tự nhiên',
    classId: 'toan-6a',
    className: 'Toán 6A',
    courseName: 'Toán lớp 6',
    teacherName: 'Lan Nguyễn',
    category: 'Bài tập',
    questionCountInfo: 'Toán 6A · Bài tập · 6 câu hỏi',
    assignedDate: '31/10/2026',
    dueDate: '05/11/2026 23:59',
    status: 'in_progress',
    instructions: 'Luyện tập các quy tắc nhân chia lũy thừa cùng cơ số và thứ tự thực hiện các phép tính trong ngoặc.',
    attachments: [],
    submissionType: 'Làm bài trực tuyến',
    allowLateSubmission: true,
    notifyStudents: true,
  },
  {
    id: 'task-3',
    title: 'Ôn tập dấu hiệu chia hết',
    classId: 'toan-6a',
    className: 'Toán 6A',
    courseName: 'Toán lớp 6',
    teacherName: 'Lan Nguyễn',
    category: 'Trắc nghiệm',
    questionCountInfo: 'Toán 6A · Trắc nghiệm · 10 câu hỏi',
    assignedDate: '30/10/2026',
    dueDate: '06/11/2026 20:00',
    status: 'not_started',
    instructions: 'Làm 10 câu trắc nghiệm về dấu hiệu chia hết cho 2, 3, 5, 9. Mỗi câu làm trong 1.5 phút.',
    attachments: [],
    submissionType: 'Trắc nghiệm trực tuyến',
    allowLateSubmission: false,
    notifyStudents: true,
  },
  {
    id: 'task-4',
    title: 'Bài tập về lũy thừa',
    classId: 'toan-6a',
    className: 'Toán 6A',
    courseName: 'Toán lớp 6',
    teacherName: 'Lan Nguyễn',
    category: 'Bài tập',
    questionCountInfo: 'Toán 6A · Bài tập · 4 câu hỏi',
    assignedDate: '26/10/2026',
    dueDate: '03/11/2026 23:59',
    status: 'overdue',
    instructions: 'Bài tập tự luyện củng cố định nghĩa lũy thừa với số mũ tự nhiên.',
    attachments: [],
    submissionType: 'Tải tệp bài làm',
    allowLateSubmission: true,
    notifyStudents: true,
  },
  {
    id: 'task-5',
    title: 'Tập hợp và phần tử',
    classId: 'toan-6a',
    className: 'Toán 6A',
    courseName: 'Toán lớp 6',
    teacherName: 'Lan Nguyễn',
    category: 'Bài tập',
    questionCountInfo: 'Toán 6A · Bài tập · 5 câu hỏi',
    assignedDate: '24/10/2026',
    dueDate: '02/11/2026 23:59',
    status: 'submitted',
    instructions: 'Viết các tập hợp số tự nhiên thỏa mãn điều kiện cho trước bằng hai cách.',
    attachments: [],
    submissionType: 'Tải tệp bài làm',
    allowLateSubmission: false,
    notifyStudents: true,
    studentSubmission: {
      fileName: 'NguyenMinhAnh_TapHop_BaiTap.pdf',
      submittedDate: '01/11/2026 18:30',
      comment: 'Em đã nộp đủ 5 câu ạ.',
    },
  },
  {
    id: 'task-6',
    title: 'Ôn tập thứ tự thực hiện phép tính',
    classId: 'toan-6a',
    className: 'Toán 6A',
    courseName: 'Toán lớp 6',
    teacherName: 'Lan Nguyễn',
    category: 'Phiếu bài tập',
    questionCountInfo: 'Toán 6A · Phiếu bài tập · 1 tài liệu',
    assignedDate: '22/10/2026',
    dueDate: '01/11/2026 23:59',
    status: 'submitted',
    instructions: 'Hoàn thành phiếu bài tập rèn luyện kỹ năng tính giá trị biểu thức chứa ngoặc tròn, ngoặc vuông và ngoặc nhọn.',
    attachments: [
      {
        name: 'Phieu-ThuTuPhepTinh.pdf',
        size: '180 KB',
        type: 'PDF',
      },
    ],
    submissionType: 'Tải tệp bài làm',
    allowLateSubmission: false,
    notifyStudents: true,
    studentSubmission: {
      fileName: 'BaiLam_ThuTuPhepTinh_MinhAnh.pdf',
      submittedDate: '31/10/2026 21:10',
    },
  },
  {
    id: 'task-7',
    title: 'Kiểm tra kiến thức chương I',
    classId: 'toan-6a',
    className: 'Toán 6A',
    courseName: 'Toán lớp 6',
    teacherName: 'Lan Nguyễn',
    category: 'Trắc nghiệm',
    questionCountInfo: 'Toán 6A · Trắc nghiệm · 10 câu hỏi',
    assignedDate: '20/10/2026',
    dueDate: '30/10/2026 20:00',
    status: 'graded',
    grade: '8/10',
    instructions: 'Đề kiểm tra 45 phút ôn tập toàn bộ chương I: Số tự nhiên.',
    attachments: [],
    submissionType: 'Trắc nghiệm trực tuyến',
    allowLateSubmission: false,
    notifyStudents: true,
    studentSubmission: {
      submittedDate: '30/10/2026 19:42',
      comment: 'Đạt 8/10 điểm. Sai 2 câu lý thuyết tập hợp con.',
    },
  },
];

export const INITIAL_STUDENT_ENROLLMENTS: StudentClassEnrollment[] = [
  {
    classId: 'toan-6a',
    className: 'Toán 6A',
    classCode: 'LUMI-MATH6A',
    courseName: 'Toán lớp 6',
    teacherName: 'Cô Lan Nguyễn',
    grade: 'Lớp 6',
    subject: 'Toán',
    studentProgress: 78,
    pendingTasksCount: 3,
    status: 'active',
  },
  {
    classId: 'anh-6b',
    className: 'Tiếng Anh 6B (Global Success)',
    classCode: 'LUMI-ENG6B',
    courseName: 'Tiếng Anh 6',
    teacherName: 'Thầy Hoàng Nam',
    grade: 'Lớp 6',
    subject: 'Tiếng Anh',
    studentProgress: 60,
    pendingTasksCount: 1,
    status: 'active',
  },
  {
    classId: 'khtn-6c',
    className: 'Khoa học tự nhiên 6C',
    classCode: 'LUMI-KHTN6C',
    courseName: 'Khoa học tự nhiên 6',
    teacherName: 'Cô Thảo Vy',
    grade: 'Lớp 6',
    subject: 'Khoa học tự nhiên',
    studentProgress: 45,
    pendingTasksCount: 2,
    status: 'active',
  },
];
