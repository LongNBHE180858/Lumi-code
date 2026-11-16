import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import {
  ClassItem,
  StudentItem,
  CreateClassFormValues,
  EditClassFormValues,
  INITIAL_CLASSES,
  INITIAL_STUDENTS_TOAN6A,
} from './class-management.model';

export interface ClassManagementState {
  classes: ClassItem[];
  selectedClassId: string;
  studentsByClass: Record<string, StudentItem[]>;
  searchTerm: string;
  selectedCourse: string;
  selectedGrade: string;
  selectedStatus: string;
  isCreateModalOpen: boolean;
  editingClass: ClassItem | null;
  toastMessage: string | null;
}

const initialState: ClassManagementState = {
  classes: INITIAL_CLASSES,
  selectedClassId: INITIAL_CLASSES[0]?.id || '',
  studentsByClass: {
    'toan-6a': INITIAL_STUDENTS_TOAN6A,
    'toan-7b': [
      {
        id: 's-7b-1',
        name: 'Hoàng Quốc Bảo',
        email: 'quocbao@email.com',
        username: 'quocbao',
        joinedDate: '20/08/2026',
        progress: 82,
        status: 'active',
      },
      {
        id: 's-7b-2',
        name: 'Vũ Thảo Vy',
        email: 'thaovy@email.com',
        username: 'thaovy',
        joinedDate: '22/08/2026',
        progress: 70,
        status: 'active',
      },
    ],
    'su-8a': [
      {
        id: 's-8a-1',
        name: 'Đặng Mai Phương',
        email: 'maiphuong@email.com',
        username: 'maiphuong',
        joinedDate: '25/08/2026',
        progress: 90,
        status: 'active',
      },
    ],
  },
  searchTerm: '',
  selectedCourse: 'all',
  selectedGrade: 'all',
  selectedStatus: 'all',
  isCreateModalOpen: false,
  editingClass: null,
  toastMessage: null,
};

export const ClassManagementSlice = createSlice({
  name: 'instructorClasses',
  initialState,
  reducers: {
    createClass(state, action: PayloadAction<CreateClassFormValues>) {
      const values = action.payload;
      const cleanName = values.name
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .toUpperCase()
        .replace(/[^A-Z0-9]/g, '');
      const code = `LUMI-${cleanName || 'CLASS' + (state.classes.length + 1)}`;
      const id = `class-${Date.now()}`;

      const newCls: ClassItem = {
        id,
        name: values.name,
        code,
        course: values.course,
        grade: values.grade,
        subject: values.subject,
        studentCount: 0,
        status: 'active',
        password: values.password,
        description: values.description || `Lớp ${values.name} dành cho học viên khối ${values.grade}.`,
        createdDate: new Date().toLocaleDateString('vi-VN'),
        teacherName: 'Giảng viên',
      };

      state.classes.unshift(newCls);
      state.studentsByClass[id] = [];
      state.selectedClassId = id;
      state.toastMessage = `Tạo lớp học "${values.name}" thành công! Mã lớp: ${code}`;
    },

    updateClass(state, action: PayloadAction<{ id: string; values: EditClassFormValues }>) {
      const { id, values } = action.payload;
      const index = state.classes.findIndex(c => c.id === id);
      if (index !== -1) {
        state.classes[index] = {
          ...state.classes[index],
          name: values.name,
          password: values.password,
          status: values.status,
          description: values.description || state.classes[index].description,
        };
        state.toastMessage = `Cập nhật thông tin lớp "${values.name}" thành công!`;
      }
    },

    selectClass(state, action: PayloadAction<string>) {
      state.selectedClassId = action.payload;
    },

    setSearchTerm(state, action: PayloadAction<string>) {
      state.searchTerm = action.payload;
    },

    setSelectedCourse(state, action: PayloadAction<string>) {
      state.selectedCourse = action.payload;
    },

    setSelectedGrade(state, action: PayloadAction<string>) {
      state.selectedGrade = action.payload;
    },

    setSelectedStatus(state, action: PayloadAction<string>) {
      state.selectedStatus = action.payload;
    },

    resetFilters(state) {
      state.searchTerm = '';
      state.selectedCourse = 'all';
      state.selectedGrade = 'all';
      state.selectedStatus = 'all';
    },

    setCreateModalOpen(state, action: PayloadAction<boolean>) {
      state.isCreateModalOpen = action.payload;
    },

    setEditingClass(state, action: PayloadAction<ClassItem | null>) {
      state.editingClass = action.payload;
    },

    approveStudent(state, action: PayloadAction<{ classId: string; studentId: string }>) {
      const { classId, studentId } = action.payload;
      const list = state.studentsByClass[classId];
      if (list) {
        const student = list.find(s => s.id === studentId);
        if (student) {
          student.status = 'active';
          student.joinedDate = new Date().toLocaleDateString('vi-VN');
          const cls = state.classes.find(c => c.id === classId);
          if (cls) cls.studentCount += 1;
          state.toastMessage = `Đã duyệt học viên ${student.name} vào lớp!`;
        }
      }
    },

    removeStudent(state, action: PayloadAction<{ classId: string; studentId: string }>) {
      const { classId, studentId } = action.payload;
      const list = state.studentsByClass[classId];
      if (list) {
        const studentIndex = list.findIndex(s => s.id === studentId);
        if (studentIndex !== -1) {
          const wasActive = list[studentIndex].status === 'active';
          list.splice(studentIndex, 1);
          const cls = state.classes.find(c => c.id === classId);
          if (cls && wasActive && cls.studentCount > 0) {
            cls.studentCount -= 1;
          }
          state.toastMessage = 'Đã xóa học viên khỏi danh sách lớp!';
        }
      }
    },

    clearToastMessage(state) {
      state.toastMessage = null;
    },
  },
});

export const {
  createClass,
  updateClass,
  selectClass,
  setSearchTerm,
  setSelectedCourse,
  setSelectedGrade,
  setSelectedStatus,
  resetFilters,
  setCreateModalOpen,
  setEditingClass,
  approveStudent,
  removeStudent,
  clearToastMessage,
} = ClassManagementSlice.actions;

export default ClassManagementSlice.reducer;
