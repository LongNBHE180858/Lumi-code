import React, { useState } from 'react';
import { Card, Button, Form, InputGroup } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faGripVertical, faPlus, faXmark } from '@fortawesome/free-solid-svg-icons';

interface LearningObjectivesProps {
  objectives: string[];
  onChange: (updatedObjectives: string[]) => void;
}

export const LearningObjectives = ({ objectives, onChange }: LearningObjectivesProps) => {
  const [draggedIndex, setDraggedIndex] = useState<number | null>(null); // 1. Lưu index của mục tiêu đang được nhấc lên
  const [dragOverIndex, setDragOverIndex] = useState<number | null>(null); // 2. Lưu index của mục tiêu đang được rê qua (để tạo hiệu ứng viền/highlight)

  const handleAdd = () => {
    onChange([...objectives, '']);
  };

  const handleUpdate = (index: number, value: string) => {
    const updated = [...objectives];
    updated[index] = value;
    onChange(updated);
  };

  const handleRemove = (index: number) => {
    onChange(objectives.filter((_, i) => i !== index));
  };

  const handleDrop = (targetIndex: number) => {
    if (draggedIndex !== null && draggedIndex !== targetIndex) {
      const newOrder = [...objectives];
      const [moved] = newOrder.splice(draggedIndex, 1);
      newOrder.splice(targetIndex, 0, moved);
      onChange(newOrder);
    }
    setDraggedIndex(null);
    setDragOverIndex(null);
  };

  return (
    <Card className="p-4 rounded-4 border-0 shadow-sm">
      <div className="d-flex justify-content-between align-items-center mb-2">
        <h5 className="fw-bold mb-0">Mục tiêu học tập</h5>
        <Button type="button" variant="link" className="text-decoration-none fw-semibold p-0" onClick={handleAdd}>
          <FontAwesomeIcon icon={faPlus} className="me-1" /> Thêm mục tiêu
        </Button>
      </div>
      <p className="text-muted small mb-3">Học sinh sẽ đạt được các kết quả sau khi hoàn thành khóa học.</p>

      <div className="d-flex flex-column gap-2">
        {objectives.map((obj, index) => {
          const isDragging = draggedIndex === index;
          const isOver = dragOverIndex === index;

          return (
            <InputGroup
              key={index}
              onDragOver={e => {
                e.preventDefault();
                e.dataTransfer.dropEffect = 'move'; // đổi icon con trỏ chuột
                if (dragOverIndex !== index) setDragOverIndex(index); // ghi nhận vtri con trỏ chuột
              }}
              onDragLeave={e => {
                if (!e.currentTarget.contains(e.relatedTarget as Node | null)) {
                  setDragOverIndex(null);
                }
              }}
              onDrop={e => {
                e.preventDefault();
                handleDrop(index); // Truyền đúng 1 tham số index
              }}
              className={`align-items-center bg-white rounded-3 border px-2 py-1 ${
                isDragging ? 'opacity-50' : ''
              } ${isOver ? 'border-primary shadow-sm' : ''}`}
            >
              {/* Nút cầm kéo */}
              <span
                className="text-muted px-2"
                style={{ cursor: 'grab' }}
                draggable
                onDragStart={e => {
                  e.stopPropagation();
                  e.dataTransfer.effectAllowed = 'move';
                  e.dataTransfer.setData('text/plain', String(index));
                  setDraggedIndex(index); // lưu lại index ban đầu đang kéo
                }}
                onDragEnd={() => {
                  setDraggedIndex(null);
                  setDragOverIndex(null);
                }}
              >
                <FontAwesomeIcon icon={faGripVertical} />
              </span>
              <Form.Control
                type="text"
                value={obj}
                onChange={e => handleUpdate(index, e.target.value)}
                placeholder="Nhập mục tiêu học tập..."
                className="border-0 shadow-none py-2"
              />
              <Button type="button" variant="link" className="text-muted p-2" onClick={() => handleRemove(index)}>
                <FontAwesomeIcon icon={faXmark} />
              </Button>
            </InputGroup>
          );
        })}
      </div>
    </Card>
  );
};

export default LearningObjectives;
