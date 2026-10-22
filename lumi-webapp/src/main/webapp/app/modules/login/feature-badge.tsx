import React from 'react';

export interface IFeatureBadgeProps {
  icon: React.ReactNode;
  title: string;
}

export const FeatureBadge: React.FC<IFeatureBadgeProps> = ({ icon, title }) => {
  return (
    <div className="login-benefit-badge">
      <div className="badge-icon-wrap" aria-hidden="true">
        {icon}
      </div>
      <span className="badge-text">{title}</span>
    </div>
  );
};

export default FeatureBadge;
