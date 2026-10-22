import React from 'react';
import { X } from 'lucide-react';

import MonitoringCard, { type MonitoringItem, type MonitoringKind } from '../../components/monitoring-card';

interface MonitoringDialogProps {
  kind: MonitoringKind;
  items: MonitoringItem[];
  search: string;
  onClose: () => void;
}

const MonitoringDialog = ({ kind, items, search, onClose }: MonitoringDialogProps) => {
  const title = kind === 'activity' ? 'Hoạt động gần đây' : 'Yêu cầu cần xử lý';

  return (
    <div className="admin-dialog-backdrop" onClick={onClose}>
      <section
        className="admin-dialog admin-panel"
        role="dialog"
        aria-modal="true"
        aria-label={title}
        onClick={event => event.stopPropagation()}
      >
        <button className="admin-dialog-close" type="button" onClick={onClose} aria-label="Đóng">
          <X size={20} aria-hidden="true" />
        </button>
        <MonitoringCard title={title} kind={kind} items={items} search={search} onViewAll={onClose} />
      </section>
    </div>
  );
};

export default MonitoringDialog;
