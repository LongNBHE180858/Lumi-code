import React, { useEffect, useState, type DragEvent } from 'react';

const maxImageSize = 5 * 1024 * 1024;

const SelectedImage = ({ file, remove }: { file: File; remove: () => void }) => {
  const [url, setUrl] = useState('');
  useEffect(() => {
    const preview = URL.createObjectURL(file);
    setUrl(preview);
    return () => URL.revokeObjectURL(preview);
  }, [file]);
  return (
    <div className="ticket-selected-image">
      {url && <img src={url} alt={`Xem trước ${file.name}`} />}
      <span>{file.name}</span>
      <button type="button" onClick={remove} aria-label={`Bỏ ảnh ${file.name}`}>
        Bỏ
      </button>
    </div>
  );
};

const TicketImagePicker = ({
  images,
  onChange,
  onError,
  disabled,
}: {
  images: File[];
  onChange: (images: File[]) => void;
  onError: (message: string) => void;
  disabled?: boolean;
}) => {
  const [dragging, setDragging] = useState(false);

  const addImages = (selected: File[]) => {
    if (selected.length === 0 || disabled) return;
    if (images.length + selected.length > 3) {
      onError('Mỗi tin nhắn được đính kèm tối đa 3 ảnh.');
      return;
    }
    if (selected.some(file => !['image/png', 'image/jpeg'].includes(file.type) || file.size === 0 || file.size > maxImageSize)) {
      onError('Chỉ chọn ảnh PNG/JPEG, tối đa 5 MB mỗi ảnh.');
      return;
    }
    onError('');
    onChange([...images, ...selected]);
  };

  const handleDrop = (event: DragEvent<HTMLDivElement>) => {
    event.preventDefault();
    setDragging(false);
    addImages(Array.from(event.dataTransfer.files));
  };

  return (
    <div className="ticket-image-picker">
      <div
        className={`ticket-image-dropzone${dragging && !disabled ? ' ticket-image-dropzone-active' : ''}`}
        onDragEnter={event => {
          event.preventDefault();
          if (!disabled) setDragging(true);
        }}
        onDragOver={event => {
          event.preventDefault();
          event.dataTransfer.dropEffect = disabled ? 'none' : 'copy';
        }}
        onDragLeave={event => {
          if (!event.currentTarget.contains(event.relatedTarget as Node | null)) setDragging(false);
        }}
        onDrop={handleDrop}
      >
        <label>
          <span>Ảnh đính kèm (tối đa 3 ảnh, 5 MB/ảnh)</span>
          <span className="ticket-image-dropzone-hint">Kéo thả ảnh vào đây hoặc bấm để chọn</span>
          <input
            type="file"
            accept="image/png,image/jpeg"
            multiple
            disabled={disabled}
            onChange={event => {
              addImages(Array.from(event.currentTarget.files || []));
              event.currentTarget.value = '';
            }}
          />
        </label>
      </div>
      {images.length > 0 && (
        <div className="ticket-selected-images">
          {images.map((file, index) => (
            <SelectedImage
              key={`${file.name}-${file.lastModified}-${index}`}
              file={file}
              remove={() => onChange(images.filter((_, i) => i !== index))}
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default TicketImagePicker;
