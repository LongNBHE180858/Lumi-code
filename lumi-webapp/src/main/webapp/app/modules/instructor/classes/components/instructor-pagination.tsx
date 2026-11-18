import React from 'react';
import './instructor-pagination.scss';

interface Props {
  page: number;
  totalPages: number;
  onPageChange: (page: number) => void;
  label: string;
  totalItems?: number;
  itemLabel?: string;
  disabled?: boolean;
}

export const InstructorPagination: React.FC<Props> = ({
  page,
  totalPages,
  onPageChange,
  label,
  totalItems,
  itemLabel,
  disabled = false,
}) => {
  if (totalPages <= 1) return null;
  const first = Math.max(0, Math.min(page - 2, totalPages - 5));
  const pages = Array.from({ length: Math.min(5, totalPages - first) }, (_, index) => first + index);
  return (
    <nav className="instructor-pagination" aria-label={label}>
      <span className="instructor-pagination-summary">
        Trang {page + 1}/{totalPages}
        {totalItems != null ? ` · ${totalItems} ${itemLabel ?? 'mục'}` : ''}
      </span>
      <div className="instructor-pagination-actions">
        <button type="button" disabled={disabled || page === 0} onClick={() => onPageChange(page - 1)}>
          Trước
        </button>
        {pages.map(number => (
          <button
            type="button"
            key={number}
            className={number === page ? 'is-current' : ''}
            aria-current={number === page ? 'page' : undefined}
            aria-label={`Trang ${number + 1}`}
            disabled={disabled}
            onClick={() => onPageChange(number)}
          >
            {number + 1}
          </button>
        ))}
        <button type="button" disabled={disabled || page + 1 >= totalPages} onClick={() => onPageChange(page + 1)}>
          Sau
        </button>
      </div>
    </nav>
  );
};

export default InstructorPagination;
