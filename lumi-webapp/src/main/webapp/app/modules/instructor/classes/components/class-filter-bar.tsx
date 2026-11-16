import React from 'react';

interface ClassFilterBarProps {
  searchTerm: string;
  onSearchChange: (value: string) => void;
  selectedCourse: string;
  onCourseChange: (value: string) => void;
  selectedGrade: string;
  onGradeChange: (value: string) => void;
  selectedStatus: string;
  onStatusChange: (value: string) => void;
  coursesList: string[];
  gradesList: string[];
  onResetFilters?: () => void;
}

export const ClassFilterBar: React.FC<ClassFilterBarProps> = ({
  searchTerm,
  onSearchChange,
  selectedCourse,
  onCourseChange,
  selectedGrade,
  onGradeChange,
  selectedStatus,
  onStatusChange,
  coursesList,
  gradesList,
  onResetFilters,
}) => {
  const hasActiveFilters = searchTerm.trim() !== '' || selectedCourse !== 'all' || selectedGrade !== 'all' || selectedStatus !== 'all';

  return (
    <div className="class-filter-bar">
      {/* Search Input */}
      <div className="filter-search-box">
        <svg
          className="filter-search-icon"
          width="18"
          height="18"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="2.2"
          strokeLinecap="round"
          strokeLinejoin="round"
        >
          <circle cx="11" cy="11" r="8" />
          <line x1="21" y1="21" x2="16.65" y2="16.65" />
        </svg>
        <input
          type="text"
          className="filter-search-input"
          placeholder="Tìm theo tên lớp hoặc mã lớp..."
          value={searchTerm}
          onChange={e => onSearchChange(e.target.value)}
        />
        {searchTerm && (
          <button type="button" className="filter-clear-btn" onClick={() => onSearchChange('')} title="Xóa tìm kiếm">
            ✕
          </button>
        )}
      </div>

      {/* Select Course */}
      <div className="filter-select-wrapper">
        <select className="filter-select-input" value={selectedCourse} onChange={e => onCourseChange(e.target.value)}>
          <option value="all">Khóa học: Tất cả</option>
          {coursesList.map(course => (
            <option key={course} value={course}>
              {course}
            </option>
          ))}
        </select>
        <span className="filter-select-arrow">▼</span>
      </div>

      {/* Select Grade */}
      <div className="filter-select-wrapper">
        <select className="filter-select-input" value={selectedGrade} onChange={e => onGradeChange(e.target.value)}>
          <option value="all">Khối lớp: Tất cả</option>
          {gradesList.map(grade => (
            <option key={grade} value={grade}>
              {grade}
            </option>
          ))}
        </select>
        <span className="filter-select-arrow">▼</span>
      </div>

      {/* Select Status */}
      <div className="filter-select-wrapper">
        <select className="filter-select-input" value={selectedStatus} onChange={e => onStatusChange(e.target.value)}>
          <option value="all">Trạng thái: Tất cả</option>
          <option value="active">Đang hoạt động</option>
          <option value="archived">Tạm dừng / Đã đóng</option>
        </select>
        <span className="filter-select-arrow">▼</span>
      </div>

      {/* Reset Filter Button */}
      {hasActiveFilters && onResetFilters && (
        <button type="button" className="btn-filter-reset" onClick={onResetFilters} title="Đặt lại bộ lọc">
          <svg
            width="14"
            height="14"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2.2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <path d="M3 12a9 9 0 1 0 9-9 9.75 9.75 0 0 0-6.74 2.74L3 8" />
            <path d="M3 3v5h5" />
          </svg>
          <span>Đặt lại</span>
        </button>
      )}
    </div>
  );
};

export default ClassFilterBar;
