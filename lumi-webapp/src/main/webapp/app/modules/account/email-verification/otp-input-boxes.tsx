import React, { useRef } from 'react';

interface OtpInputBoxesProps {
  value: string;
  onChange: (val: string) => void;
  disabled?: boolean;
}

export const OtpInputBoxes: React.FC<OtpInputBoxesProps> = ({ value, onChange, disabled = false }) => {
  const inputsRef = useRef<(HTMLInputElement | null)[]>([]);

  const handleDigitChange = (index: number, digit: string) => {
    const clean = digit.replace(/\D/g, '');
    const chars = value.split('');
    chars[index] = clean.slice(-1);
    const updated = chars.join('').slice(0, 6);
    onChange(updated);

    if (clean && index < 5) {
      inputsRef.current[index + 1]?.focus();
    }
  };

  const handleKeyDown = (index: number, e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Backspace' && !value[index] && index > 0) {
      inputsRef.current[index - 1]?.focus();
    }
  };

  const handlePaste = (e: React.ClipboardEvent<HTMLInputElement>) => {
    e.preventDefault();
    const paste = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, 6);
    if (paste) {
      onChange(paste);
      const nextFocus = Math.min(paste.length, 5);
      inputsRef.current[nextFocus]?.focus();
    }
  };

  return (
    <div className="otp-digit-inputs-group">
      {[0, 1, 2, 3, 4, 5].map(idx => (
        <input
          key={idx}
          ref={el => {
            inputsRef.current[idx] = el;
          }}
          type="text"
          inputMode="numeric"
          maxLength={1}
          className="otp-digit-input"
          value={value[idx] || ''}
          onChange={e => handleDigitChange(idx, e.target.value)}
          onKeyDown={e => handleKeyDown(idx, e)}
          onPaste={handlePaste}
          disabled={disabled}
          autoFocus={idx === 0}
        />
      ))}
    </div>
  );
};

export default OtpInputBoxes;
