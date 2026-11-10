import React, { useEffect, useState } from 'react';

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
}) => (
  <div className="ticket-image-picker">
    <label>
      <span>Ảnh đính kèm (tối đa 3 ảnh, 5 MB/ảnh)</span>
      <input
        type="file"
        accept="image/png,image/jpeg"
        multiple
        disabled={disabled}
        onChange={event => {
          const selected = Array.from(event.currentTarget.files || []);
          event.currentTarget.value = '';
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
        }}
      />
    </label>
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

export default TicketImagePicker;
