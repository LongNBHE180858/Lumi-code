import React from 'react';
import { Link } from 'react-router';

interface TermsAndActionsProps {
  commitAccuracy: boolean;
  agreeTerms: boolean;
  onToggleCommit: () => void;
  onToggleAgreeTerms: () => void;
  onSubmit: (e: React.FormEvent) => void;
  isLoading?: boolean;
}

export const TermsAndActions: React.FC<TermsAndActionsProps> = ({
  commitAccuracy,
  agreeTerms,
  onToggleCommit,
  onToggleAgreeTerms,
  onSubmit,
  isLoading = false,
}) => {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
      <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
        {/* Checkbox 1 */}
        <div
          className="agreement-checkbox-row"
          onClick={onToggleCommit}
          role="button"
          tabIndex={0}
          onKeyDown={e => {
            if (e.key === 'Enter' || e.key === ' ') {
              onToggleCommit();
            }
          }}
        >
          <div className={`checkbox-custom-box ${commitAccuracy ? 'checked' : ''}`}>
            {commitAccuracy && (
              <svg
                width="14"
                height="14"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="3"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <polyline points="20 6 9 17 4 12" />
              </svg>
            )}
          </div>
          <span className="agreement-label-text">
            Tôi cam kết các thông tin họ tên, liên hệ và trường lớp cung cấp là hoàn toàn chính xác.
          </span>
        </div>

        {/* Checkbox 2 */}
        <div
          className="agreement-checkbox-row"
          onClick={onToggleAgreeTerms}
          role="button"
          tabIndex={0}
          onKeyDown={e => {
            if (e.key === 'Enter' || e.key === ' ') {
              onToggleAgreeTerms();
            }
          }}
        >
          <div className={`checkbox-custom-box ${agreeTerms ? 'checked' : ''}`}>
            {agreeTerms && (
              <svg
                width="14"
                height="14"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="3"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <polyline points="20 6 9 17 4 12" />
              </svg>
            )}
          </div>
          <span className="agreement-label-text">
            Tôi đồng ý với <strong style={{ color: 'var(--color-primary, #24a8e8)' }}>Điều khoản dịch vụ</strong> và{' '}
            <strong style={{ color: 'var(--color-primary, #24a8e8)' }}>Chính sách bảo mật</strong> của nền tảng LUMI.
          </span>
        </div>
      </div>

      {/* Submit Button */}
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '14px', marginTop: '8px' }}>
        <button type="button" className="btn-large-submit" onClick={onSubmit} disabled={isLoading}>
          {isLoading ? (
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: '8px' }}>
              <span className="spinner-border spinner-border-sm" role="status" aria-hidden="true" />
              Đang đăng ký tài khoản...
            </span>
          ) : (
            'Tạo tài khoản LUMI'
          )}
        </button>

        <p style={{ fontSize: '14px', color: '#6884a4', margin: 0 }}>
          Đã có tài khoản LUMI?{' '}
          <Link to="/login" style={{ fontWeight: 800, color: 'var(--color-primary, #24a8e8)', textDecoration: 'none' }}>
            Đăng nhập ngay
          </Link>
        </p>
      </div>
    </div>
  );
};

export default TermsAndActions;
