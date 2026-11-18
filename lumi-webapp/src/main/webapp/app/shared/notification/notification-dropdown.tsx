import './notification-dropdown.scss';

import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router';
import { Bell, BookOpen, Users, Settings, Wallet, CheckCheck, Inbox } from 'lucide-react';
import { toast } from 'react-toastify';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import {
  fetchMyNotifications,
  fetchUnreadCount,
  markAllNotificationsAsRead,
  markNotificationAsRead,
  receiveRealtimeNotification,
  setActiveCategory,
} from './notification.reducer';
import { INotification, NotificationCategory } from 'app/shared/model/notification.model';
import { notificationWebSocketService } from './notification-websocket.service';

const CATEGORIES: { key: 'ALL' | NotificationCategory; label: string }[] = [
  { key: 'ALL', label: 'Tất cả' },
  { key: 'COURSE', label: 'Khóa học' },
  { key: 'CLASSROOM', label: 'Lớp học' },
  { key: 'SYSTEM', label: 'Hệ thống' },
  { key: 'FINANCE', label: 'Tài chính' },
];

const formatRelativeTime = (dateStr?: string | null): string => {
  if (!dateStr) return '';
  const now = new Date();
  const date = new Date(dateStr);
  const diffSec = Math.floor((now.getTime() - date.getTime()) / 1000);

  if (diffSec < 60) return 'Vừa xong';
  const diffMin = Math.floor(diffSec / 60);
  if (diffMin < 60) return `${diffMin} phút trước`;
  const diffHour = Math.floor(diffMin / 60);
  if (diffHour < 24) return `${diffHour} giờ trước`;
  const diffDay = Math.floor(diffHour / 24);
  if (diffDay < 7) return `${diffDay} ngày trước`;
  return date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit' });
};

const getCategoryIcon = (category?: NotificationCategory | null) => {
  switch (category) {
    case 'COURSE':
      return <BookOpen size={18} />;
    case 'CLASSROOM':
      return <Users size={18} />;
    case 'FINANCE':
      return <Wallet size={18} />;
    case 'SYSTEM':
    default:
      return <Settings size={18} />;
  }
};

const getCategoryClass = (category?: NotificationCategory | null) => {
  switch (category) {
    case 'COURSE':
      return 'course';
    case 'CLASSROOM':
      return 'classroom';
    case 'FINANCE':
      return 'finance';
    case 'SYSTEM':
    default:
      return 'system';
  }
};

const getCategoryLabel = (category?: NotificationCategory | null) => {
  switch (category) {
    case 'COURSE':
      return 'Khóa học';
    case 'CLASSROOM':
      return 'Lớp học';
    case 'FINANCE':
      return 'Tài chính';
    case 'SYSTEM':
      return 'Hệ thống';
    default:
      return 'Thông báo';
  }
};

export const NotificationDropdown: React.FC = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const [isOpen, setIsOpen] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);

  const { entities, unreadCount, loading, activeCategory } = useAppSelector(state => (state as any).userNotification);
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);

  // Initialize and WebSocket subscription
  useEffect(() => {
    if (!isAuthenticated) return;

    // Fetch initial count
    dispatch(fetchUnreadCount());

    // Connect WebSocket
    notificationWebSocketService.connect();

    // Listen to real-time events
    const unsubscribe = notificationWebSocketService.subscribe((notification: INotification) => {
      dispatch(receiveRealtimeNotification(notification));
      toast.info(
        <div style={{ cursor: 'pointer' }}>
          <strong>{notification.title}</strong>
          <div style={{ fontSize: '13px' }}>{notification.content}</div>
        </div>,
        {
          autoClose: 6000,
          onClick() {
            if (notification.actionUrl) {
              navigate(notification.actionUrl);
            }
          },
        },
      );
    });

    return () => {
      unsubscribe();
    };
  }, [dispatch, isAuthenticated, navigate]);

  // Fetch notifications when opened or category changed
  useEffect(() => {
    if (isOpen && isAuthenticated) {
      dispatch(fetchMyNotifications({ category: activeCategory, page: 0 }));
    }
  }, [isOpen, activeCategory, dispatch, isAuthenticated]);

  // Click outside listener
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (containerRef.current && !containerRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };
    if (isOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [isOpen]);

  const handleToggle = () => {
    setIsOpen(prev => !prev);
  };

  const handleTabChange = (key: 'ALL' | NotificationCategory) => {
    dispatch(setActiveCategory(key));
  };

  const handleMarkAllRead = async () => {
    await dispatch(markAllNotificationsAsRead());
  };

  const handleItemClick = (item: INotification) => {
    if (!item.isRead) {
      dispatch(markNotificationAsRead(item.id));
    }
    setIsOpen(false);

    // 1. Kiểm tra nếu là thông báo Admin duyệt khóa học
    if (item.targetType === 'COURSE_REVIEW_REQUEST' && item.targetId) {
      navigate(`/admin/course-review/${item.targetId}`);
    }
    // 2. ĐƯA LÊN TRƯỚC: Nếu là thông báo khóa học và có targetId -> vào thẳng builder preview
    else if ((item.category === 'COURSE' || item.targetType === 'COURSE') && item.targetId) {
      navigate(`/instructor/courses/${item.targetId}/builder?step=preview`);
    }
    // 3. Các trường hợp còn lại mới dùng actionUrl từ backend
    else if (item.actionUrl) {
      navigate(item.actionUrl);
    }
  };

  return (
    <div className="lumi-notification-wrapper" ref={containerRef}>
      <button
        type="button"
        className={`lumi-notification-btn ${isOpen ? 'active' : ''}`}
        aria-label="Thông báo"
        title="Thông báo"
        onClick={handleToggle}
      >
        <Bell className="lumi-bell-icon" />
        {unreadCount > 0 && <span className="lumi-notification-badge">{unreadCount > 99 ? '99+' : unreadCount}</span>}
      </button>

      {isOpen && (
        <div className="lumi-notification-popover" role="dialog" aria-label="Hộp thông báo">
          {/* Header */}
          <div className="lumi-popover-header">
            <div className="title-group">
              <h6>Thông báo</h6>
              {unreadCount > 0 && <span className="unread-tag">{unreadCount} mới</span>}
            </div>
            <button type="button" className="mark-all-btn" onClick={handleMarkAllRead} disabled={unreadCount === 0}>
              <CheckCheck size={15} />
              Đã đọc tất cả
            </button>
          </div>

          {/* Category Filter Tabs */}
          <div className="lumi-category-tabs">
            {CATEGORIES.map(cat => (
              <button
                key={cat.key}
                type="button"
                className={`category-tab-btn ${activeCategory === cat.key ? 'active' : ''}`}
                onClick={() => handleTabChange(cat.key)}
              >
                {cat.label}
              </button>
            ))}
          </div>

          {/* Notification List */}
          <div className="lumi-notification-list">
            {loading && entities.length === 0 ? (
              <div className="p-4 text-center text-muted small">Đang tải thông báo...</div>
            ) : entities.length === 0 ? (
              <div className="empty-state">
                <Inbox />
                <p>Không có thông báo nào trong mục này</p>
              </div>
            ) : (
              entities.map(item => (
                <div
                  key={item.id}
                  className={`lumi-notification-item ${!item.isRead ? 'unread' : ''}`}
                  onClick={() => handleItemClick(item)}
                >
                  <div className={`item-icon-box ${getCategoryClass(item.category)}`}>{getCategoryIcon(item.category)}</div>
                  <div className="item-content-box">
                    <div className="item-title">{item.title}</div>
                    <div className="item-desc">{item.content}</div>
                    <div className="item-footer">
                      <span className={`item-category-badge ${getCategoryClass(item.category)}`}>{getCategoryLabel(item.category)}</span>
                      <span className="item-time">{formatRelativeTime(item.createdAt)}</span>
                    </div>
                  </div>
                  {!item.isRead && <span className="unread-indicator" />}
                </div>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default NotificationDropdown;
