import React, { useEffect, useRef, useState } from 'react';
import { X } from 'lucide-react';

import MonitoringCard, { type MonitoringItem, type MonitoringKind } from '../../components/monitoring-card';
import { getDashboardActivities, getDashboardRequests } from '../dashboard-api';
import { getActivityItems, getPendingRequestItems } from '../dashboard-data';

interface MonitoringDialogProps {
  kind: MonitoringKind;
  search: string;
  onClose: () => void;
}

const MonitoringDialog = ({ kind, search, onClose }: MonitoringDialogProps) => {
  const title = kind === 'activity' ? 'Hoạt động gần đây' : 'Yêu cầu cần xử lý';
  const [items, setItems] = useState<MonitoringItem[]>([]);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [retry, setRetry] = useState(0);
  const loadingRef = useRef(true);

  useEffect(() => {
    const previousBodyOverflow = document.body.style.overflow;
    const previousRootOverflow = document.documentElement.style.overflow;
    document.body.style.overflow = 'hidden';
    document.documentElement.style.overflow = 'hidden';

    return () => {
      document.body.style.overflow = previousBodyOverflow;
      document.documentElement.style.overflow = previousRootOverflow;
    };
  }, []);

  const loadMore = () => {
    if (!hasMore || loadingRef.current || error) return;
    loadingRef.current = true;
    setPage(value => value + 1);
  };

  const handleScroll = (event: React.UIEvent<HTMLElement>) => {
    const element = event.currentTarget;
    if (element.scrollHeight - element.scrollTop - element.clientHeight <= 120) loadMore();
  };

  useEffect(() => {
    const controller = new AbortController();
    loadingRef.current = true;
    setLoading(true);
    setError(false);
    const load =
      kind === 'activity'
        ? getDashboardActivities(page, search, controller.signal).then(response => ({
            items: getActivityItems(response.data.items, page * 20),
            hasMore: response.data.hasMore,
          }))
        : getDashboardRequests(page, search, controller.signal).then(response => ({
            items: getPendingRequestItems(response.data.items),
            hasMore: response.data.hasMore,
          }));
    load
      .then(result => {
        if (controller.signal.aborted) return;
        setItems(previous => (page === 0 ? result.items : [...previous, ...result.items]));
        setHasMore(result.hasMore);
      })
      .catch(() => {
        if (!controller.signal.aborted) setError(true);
      })
      .finally(() => {
        if (!controller.signal.aborted) {
          loadingRef.current = false;
          setLoading(false);
        }
      });
    return () => controller.abort();
  }, [kind, page, retry, search]);

  return (
    <div className="admin-dialog-backdrop" onClick={onClose}>
      <section
        className="admin-dialog admin-panel"
        role="dialog"
        aria-modal="true"
        aria-label={title}
        onClick={event => event.stopPropagation()}
        onScroll={handleScroll}
      >
        <button className="admin-dialog-close" type="button" onClick={onClose} aria-label="Đóng">
          <X size={20} aria-hidden="true" />
        </button>
        {(items.length > 0 || (!loading && !error)) && (
          <MonitoringCard title={title} kind={kind} items={items} search={search} onViewAll={onClose} />
        )}
        {loading && (
          <p className="admin-dialog-status" role="status">
            Đang tải danh sách...
          </p>
        )}
        {error && (
          <div className="admin-dialog-status" role="alert">
            Không tải được danh sách.
            <button type="button" onClick={() => setRetry(value => value + 1)}>
              Thử lại
            </button>
          </div>
        )}
        {hasMore && !error && (
          <button className="admin-dialog-load-more" type="button" disabled={loading} onClick={loadMore}>
            Tải thêm
          </button>
        )}
      </section>
    </div>
  );
};

export default MonitoringDialog;
