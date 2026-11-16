import React, { useState } from 'react';
import { fireEvent, render, screen } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import TicketImagePicker from './ticket-image-picker';

const image = (name: string, type = 'image/png') => new File([new Uint8Array([1])], name, { type });

const Picker = () => {
  const [images, setImages] = useState<File[]>([]);
  const [error, setError] = useState('');
  return (
    <>
      <TicketImagePicker images={images} onChange={setImages} onError={setError} />
      {error && <p role="alert">{error}</p>}
    </>
  );
};

describe('TicketImagePicker', () => {
  beforeEach(() => {
    Object.defineProperty(URL, 'createObjectURL', { configurable: true, value: vi.fn(() => 'blob:ticket-preview') });
    Object.defineProperty(URL, 'revokeObjectURL', { configurable: true, value: vi.fn() });
  });

  it('adds dropped images alongside images selected with the file input', () => {
    render(<Picker />);
    const first = image('first.png');
    const second = image('second.jpg', 'image/jpeg');

    fireEvent.change(screen.getByLabelText(/Ảnh đính kèm/), { target: { files: [first] } });
    fireEvent.drop(screen.getByText(/Kéo thả ảnh vào đây/), { dataTransfer: { files: [second] } });

    expect(screen.getByText('first.png')).toBeTruthy();
    expect(screen.getByText('second.jpg')).toBeTruthy();
  });

  it('rejects invalid or excessive dropped files without discarding selected images', () => {
    render(<Picker />);
    const first = image('first.png');
    const dropzone = screen.getByText(/Kéo thả ảnh vào đây/);

    fireEvent.change(screen.getByLabelText(/Ảnh đính kèm/), { target: { files: [first] } });
    fireEvent.drop(dropzone, { dataTransfer: { files: [image('notes.txt', 'text/plain')] } });
    expect(screen.getByRole('alert').textContent).toContain('PNG/JPEG');

    fireEvent.drop(dropzone, { dataTransfer: { files: [image('second.png'), image('third.png'), image('fourth.png')] } });
    expect(screen.getByRole('alert').textContent).toContain('tối đa 3 ảnh');
    expect(screen.getByText('first.png')).toBeTruthy();
    expect(screen.queryByText('second.png')).toBeNull();
  });
});
