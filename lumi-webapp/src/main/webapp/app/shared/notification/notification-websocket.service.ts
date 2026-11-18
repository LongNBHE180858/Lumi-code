import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Storage } from 'react-jhipster';
import { AUTHENTICATION_TOKEN_KEY } from 'app/shared/jhipster/constants';
import { INotification } from 'app/shared/model/notification.model';

class NotificationWebSocketService {
  private client: Client | null = null;
  private isConnected = false;
  private subscribers: ((notification: INotification) => void)[] = [];
  private topicSubscribers = new Map<string, ((data: any) => void)[]>();
  private activeStompSubscriptions = new Map<string, any>();

  connect() {
    if (this.client?.active) {
      return;
    }

    const token = Storage.local.get(AUTHENTICATION_TOKEN_KEY) || Storage.session.get(AUTHENTICATION_TOKEN_KEY);
    if (!token) {
      return;
    }

    const loc = window.location;
    const protocol = loc.protocol === 'https:' ? 'wss:' : 'ws:';
    const brokerURL = `${protocol}//${loc.host}/websocket/tracker?access_token=${encodeURIComponent(token)}`;

    this.client = new Client({
      brokerURL,
      webSocketFactory() {
        if (typeof WebSocket !== 'undefined') {
          return new WebSocket(brokerURL);
        }
        return new SockJS(`/websocket/tracker?access_token=${encodeURIComponent(token)}`) as any;
      },
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      onConnect: () => {
        this.isConnected = true;
        // User personal notifications
        this.client?.subscribe('/user/topic/notifications', (message: IMessage) => {
          this.handleIncoming(message);
        });

        // Resubscribe any pending topic subscribers
        this.topicSubscribers.forEach((callbacks, topic) => {
          this.ensureStompTopicSubscription(topic);
        });
      },
      onDisconnect: () => {
        this.isConnected = false;
        this.activeStompSubscriptions.clear();
      },
      onStompError(frame) {
        console.warn('STOMP notification connection notice:', frame.headers?.message);
      },
    });

    this.client.activate();
  }

  private ensureStompTopicSubscription(topic: string) {
    if (!this.client?.connected || this.activeStompSubscriptions.has(topic)) {
      return;
    }
    const sub = this.client.subscribe(topic, (message: IMessage) => {
      try {
        const payload = JSON.parse(message.body);
        const callbacks = this.topicSubscribers.get(topic) || [];
        callbacks.forEach(cb => cb(payload));
      } catch (e) {
        console.error('Error parsing topic message from ' + topic, e);
      }
    });
    this.activeStompSubscriptions.set(topic, sub);
  }

  private handleIncoming(message: IMessage) {
    try {
      const payload: INotification = JSON.parse(message.body);
      this.subscribers.forEach(cb => cb(payload));
    } catch (e) {
      console.error('Error parsing notification message:', e);
    }
  }

  subscribe(callback: (notification: INotification) => void) {
    this.subscribers.push(callback);
    return () => {
      this.subscribers = this.subscribers.filter(cb => cb !== callback);
    };
  }

  /**
   * Subscribe to a custom STOMP topic, e.g. /topic/course-review/{id}
   */
  subscribeTopic(topic: string, callback: (data: any) => void) {
    if (!this.topicSubscribers.has(topic)) {
      this.topicSubscribers.set(topic, []);
    }
    this.topicSubscribers.get(topic)!.push(callback);

    if (!this.client?.active) {
      this.connect();
    } else if (this.isConnected) {
      this.ensureStompTopicSubscription(topic);
    }

    return () => {
      const callbacks = this.topicSubscribers.get(topic) || [];
      const updated = callbacks.filter(cb => cb !== callback);
      if (updated.length > 0) {
        this.topicSubscribers.set(topic, updated);
      } else {
        this.topicSubscribers.delete(topic);
        const sub = this.activeStompSubscriptions.get(topic);
        if (sub) {
          sub.unsubscribe();
          this.activeStompSubscriptions.delete(topic);
        }
      }
    };
  }

  disconnect() {
    if (this.client?.active) {
      this.client.deactivate();
    }
    this.client = null;
    this.isConnected = false;
    this.activeStompSubscriptions.clear();
  }
}

export const notificationWebSocketService = new NotificationWebSocketService();
