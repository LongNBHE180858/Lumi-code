import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit';
import axios from 'axios';
import { INotification, NotificationCategory } from 'app/shared/model/notification.model';

export interface NotificationState {
  entities: INotification[];
  unreadCount: number;
  loading: boolean;
  activeCategory: 'ALL' | NotificationCategory;
  page: number;
  hasMore: boolean;
}

const initialState: NotificationState = {
  entities: [],
  unreadCount: 0,
  loading: false,
  activeCategory: 'ALL',
  page: 0,
  hasMore: true,
};

export const fetchUnreadCount = createAsyncThunk('notification/fetch_unread_count', async () => {
  const response = await axios.get<number>('/api/notifications/unread-count');
  return response.data;
});

export const fetchMyNotifications = createAsyncThunk(
  'notification/fetch_my_notifications',
  async ({ category, page = 0, size = 15 }: { category?: 'ALL' | NotificationCategory; page?: number; size?: number }) => {
    const params: Record<string, unknown> = {
      page,
      size,
      sort: 'createdAt,desc',
    };
    if (category && category !== 'ALL') {
      params.category = category;
    }
    const response = await axios.get<INotification[]>('/api/notifications/my', { params });
    return { data: response.data, page };
  },
);

export const markNotificationAsRead = createAsyncThunk('notification/mark_as_read', async (id: number) => {
  const response = await axios.patch<INotification>(`/api/notifications/${id}/read`);
  return response.data;
});

export const markAllNotificationsAsRead = createAsyncThunk('notification/mark_all_as_read', async () => {
  await axios.post('/api/notifications/mark-all-read');
  return true;
});

export const userNotificationSlice = createSlice({
  name: 'userNotification',
  initialState,
  reducers: {
    receiveRealtimeNotification(state, action: PayloadAction<INotification>) {
      const exists = state.entities.some(n => n.id === action.payload.id);
      if (!exists) {
        state.entities = [action.payload, ...state.entities];
        state.unreadCount += 1;
      }
    },
    setActiveCategory(state, action: PayloadAction<'ALL' | NotificationCategory>) {
      state.activeCategory = action.payload;
      state.page = 0;
      state.entities = [];
      state.hasMore = true;
    },
    resetNotifications(state) {
      state.entities = [];
      state.page = 0;
      state.hasMore = true;
    },
  },
  extraReducers(builder) {
    builder
      .addCase(fetchUnreadCount.fulfilled, (state, action) => {
        state.unreadCount = action.payload;
      })
      .addCase(fetchMyNotifications.pending, state => {
        state.loading = true;
      })
      .addCase(fetchMyNotifications.fulfilled, (state, action) => {
        state.loading = false;
        const { data, page } = action.payload;
        if (page === 0) {
          state.entities = data;
        } else {
          // Filter duplicates when appending
          const existingIds = new Set(state.entities.map(n => n.id));
          const newItems = data.filter(n => !existingIds.has(n.id));
          state.entities = [...state.entities, ...newItems];
        }
        state.page = page;
        state.hasMore = data.length >= 15;
      })
      .addCase(fetchMyNotifications.rejected, state => {
        state.loading = false;
      })
      .addCase(markNotificationAsRead.fulfilled, (state, action) => {
        const updated = action.payload;
        const index = state.entities.findIndex(n => n.id === updated.id);
        if (index !== -1) {
          if (!state.entities[index].isRead) {
            state.unreadCount = Math.max(0, state.unreadCount - 1);
          }
          state.entities[index] = { ...state.entities[index], ...updated, isRead: true };
        }
      })
      .addCase(markAllNotificationsAsRead.fulfilled, state => {
        state.unreadCount = 0;
        state.entities = state.entities.map(n => ({ ...n, isRead: true }));
      });
  },
});

export const { receiveRealtimeNotification, setActiveCategory, resetNotifications } = userNotificationSlice.actions;
export default userNotificationSlice.reducer;
