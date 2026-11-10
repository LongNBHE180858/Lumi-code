import React from 'react';
import { Navigate } from 'react-router';

import { useAppSelector } from 'app/config/store';
import { useInstructorApproval } from './use-instructor-approval';

const InstructorAccessRoute = ({ children }: { children: React.ReactNode }) => {
  const account = useAppSelector(state => state.authentication.account);
  const authorities = account.authorities ?? [];
  const admin = authorities.includes('ROLE_ADMIN');
  const instructor = authorities.includes('ROLE_INSTRUCTOR');
  const approval = useInstructorApproval(account.id ?? null, instructor && !admin);

  if (admin) return <>{children}</>;
  if (!instructor) return <Navigate to="/profile" replace />;
  if (approval.loading) return <p role="status">Đang kiểm tra trạng thái hồ sơ giảng viên...</p>;
  if (approval.error) {
    return (
      <div role="alert">
        Không tải được trạng thái hồ sơ giảng viên. <button onClick={approval.reload}>Thử lại</button>
      </div>
    );
  }
  if (!approval.approved) return <Navigate to="/profile" replace />;
  return <>{children}</>;
};

export default InstructorAccessRoute;
