import { describe, it, expect } from 'vitest';
import reducer, {
  receiveRealtimeNotification,
  setActiveCategory,
  resetNotifications,
  NotificationState,
  fetchUnreadCount,
  markAllNotificationsAsRead,
  markNotificationAsRead,
} from './notification.reducer';
import { INotification } from 'app/shared/model/notification.model';

describe('Notification reducer', () => {
  const initialState: NotificationState = {
    entities: [],
    unreadCount: 0,
    loading: false,
    activeCategory: 'ALL',
    page: 0,
    hasMore: true,
  };

  it('should return initial state', () => {
    expect(reducer(undefined, { type: 'unknown' })).toEqual(initialState);
  });

  it('should receive real-time notification and increment unread count', () => {
    const item: INotification = {
      id: 1,
      title: 'Khóa học đã gửi duyệt',
      content: 'Chờ xét duyệt',
      type: 'COURSE_SUBMITTED',
      category: 'COURSE',
      isRead: false,
    };

    const state = reducer(initialState, receiveRealtimeNotification(item));
    expect(state.entities).toHaveLength(1);
    expect(state.entities[0].id).toBe(1);
    expect(state.unreadCount).toBe(1);
  });

  it('should not duplicate notification if already exists', () => {
    const item: INotification = {
      id: 1,
      title: 'Khóa học',
      content: 'Nội dung',
      type: 'COURSE_SUBMITTED',
      category: 'COURSE',
      isRead: false,
    };

    let state = reducer(initialState, receiveRealtimeNotification(item));
    state = reducer(state, receiveRealtimeNotification(item));
    expect(state.entities).toHaveLength(1);
    expect(state.unreadCount).toBe(1);
  });

  it('should switch active category and reset page', () => {
    const stateWithData: NotificationState = {
      ...initialState,
      entities: [{ id: 1, title: 'Test', content: '', type: 'COURSE_SUBMITTED', category: 'COURSE', isRead: false }],
      page: 2,
    };

    const state = reducer(stateWithData, setActiveCategory('COURSE'));
    expect(state.activeCategory).toBe('COURSE');
    expect(state.page).toBe(0);
    expect(state.entities).toHaveLength(0);
    expect(state.hasMore).toBe(true);
  });

  it('should reset notifications', () => {
    const state = reducer({ ...initialState, page: 3 }, resetNotifications());
    expect(state.page).toBe(0);
    expect(state.entities).toHaveLength(0);
  });

  it('should update unreadCount on fetchUnreadCount.fulfilled', () => {
    const state = reducer(initialState, { type: fetchUnreadCount.fulfilled.type, payload: 5 });
    expect(state.unreadCount).toBe(5);
  });

  it('should mark all notifications as read', () => {
    const stateWithItems: NotificationState = {
      ...initialState,
      unreadCount: 2,
      entities: [
        { id: 1, title: 'A', content: '', type: '', isRead: false },
        { id: 2, title: 'B', content: '', type: '', isRead: false },
      ],
    };

    const state = reducer(stateWithItems, { type: markAllNotificationsAsRead.fulfilled.type });
    expect(state.unreadCount).toBe(0);
    expect(state.entities.every(n => n.isRead)).toBe(true);
  });

  it('should mark single notification as read and decrement unread count', () => {
    const stateWithItems: NotificationState = {
      ...initialState,
      unreadCount: 2,
      entities: [
        { id: 1, title: 'A', content: '', type: '', isRead: false },
        { id: 2, title: 'B', content: '', type: '', isRead: false },
      ],
    };

    const state = reducer(stateWithItems, {
      type: markNotificationAsRead.fulfilled.type,
      payload: { id: 1, isRead: true },
    });
    expect(state.unreadCount).toBe(1);
    expect(state.entities.find(n => n.id === 1)?.isRead).toBe(true);
    expect(state.entities.find(n => n.id === 2)?.isRead).toBe(false);
  });
});
