import { useEffect, useRef, useState } from 'react';
import { useSearchParams } from 'react-router';
import axios from 'axios';
import { useAppSelector } from 'app/config/store';
import { ChatMessage, createConversation, getHistory, sendQuestion } from './ai-chat.api';

type Notice = { text: string; action?: 'reload' | 'login' | 'home'; fatal?: boolean };
const words = (value: string) => value.trim().split(/\s+/u).filter(Boolean).length;

export default function useAiChat() {
  const [params, setParams] = useSearchParams();
  const rawId = params.get('conversation');
  const account = useAppSelector(state => state.authentication.account);
  const storageKey = `lumi-ai-draft:${account.id ?? account.login}`;
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [draft, setDraft] = useState('');
  const [pending, setPending] = useState('');
  const [loading, setLoading] = useState(false);
  const [sending, setSending] = useState(false);
  const [notice, setNotice] = useState<Notice | null>(null);
  const [reload, setReload] = useState(0);
  const [scrollVersion, setScrollVersion] = useState(0);
  const currentIdRef = useRef<number | null>(null);
  const initializedRef = useRef(false);
  const epochRef = useRef(0);
  const lockedRef = useRef(false);
  const validId = rawId !== null && /^[1-9]\d*$/.test(rawId) && Number.isSafeInteger(Number(rawId));

  const remember = (value: string, id = currentIdRef.current) => {
    setDraft(value);
    try {
      if (value) sessionStorage.setItem(storageKey, JSON.stringify({ id, value }));
      else sessionStorage.removeItem(storageKey);
    } catch {
      /* The draft remains usable in memory when storage is unavailable. */
    }
  };

  useEffect(() => {
    initializedRef.current = false;
    currentIdRef.current = null;
    try {
      for (const key of Object.keys(sessionStorage)) {
        if (key.startsWith('lumi-ai-draft:') && key !== storageKey) sessionStorage.removeItem(key);
      }
    } catch {
      /* Storage can be disabled by the browser. */
    }
    let stored: { id: number | null; value: string } | null = null;
    try {
      stored = JSON.parse(sessionStorage.getItem(storageKey) || 'null');
    } catch {
      /* Ignore invalid local draft. */
    }
    setDraft(stored?.id === (validId ? Number(rawId) : null) ? stored.value : '');
    return () => {
      epochRef.current++;
      if (window.location.pathname.endsWith('/logout')) {
        try {
          sessionStorage.removeItem(storageKey);
        } catch {
          /* Ignore unavailable storage. */
        }
      }
    };
  }, [storageKey]);

  useEffect(() => {
    const id = validId ? Number(rawId) : null;
    if (initializedRef.current && id === currentIdRef.current && reload === 0 && (rawId === null || validId)) return;
    initializedRef.current = true;
    try {
      const saved = JSON.parse(sessionStorage.getItem(storageKey) || 'null');
      setDraft(saved?.id === id ? saved.value : '');
    } catch {
      setDraft('');
    }
    currentIdRef.current = id;
    const requestEpoch = ++epochRef.current;
    const controller = new AbortController();
    lockedRef.current = false;
    setSending(false);
    setPending('');
    setMessages([]);
    setNotice(null);
    setScrollVersion(value => value + 1);
    if (rawId !== null && !validId) {
      setNotice({ text: 'Đường dẫn cuộc trò chuyện không hợp lệ.', action: 'home', fatal: true });
      setLoading(false);
      return;
    }
    if (!id) {
      setLoading(false);
      return;
    }
    setLoading(true);
    getHistory(id, controller.signal)
      .then(history => {
        if (epochRef.current === requestEpoch) setMessages(history);
      })
      .catch(error => {
        if (!axios.isCancel(error) && epochRef.current === requestEpoch) handleError(error, true);
      })
      .finally(() => {
        if (epochRef.current === requestEpoch) setLoading(false);
      });
    return () => controller.abort();
  }, [rawId, reload, storageKey]);

  function handleError(error: unknown, historyRequest = false) {
    const status = axios.isAxiosError(error) ? error.response?.status : undefined;
    if (status === 401) setNotice({ text: 'Phiên đăng nhập đã hết hạn. Bạn cần đăng nhập lại để tiếp tục.', action: 'login', fatal: true });
    else if (status === 403 || status === 404)
      setNotice({
        text: 'Chưa thể mở cuộc trò chuyện này. Tài khoản có thể không có quyền truy cập hoặc cuộc trò chuyện không còn tồn tại.',
        action: 'home',
        fatal: true,
      });
    else if (status === 409)
      setNotice({
        text: 'Hội thoại đã thay đổi ở nơi khác. Hãy tải lại lịch sử trước khi gửi tiếp. Câu hỏi của bạn vẫn được giữ lại.',
        action: 'reload',
      });
    else if (historyRequest || !status || status >= 500)
      setNotice({
        text: historyRequest
          ? 'Chưa tải được lịch sử. Hãy kiểm tra kết nối và thử lại.'
          : 'Chưa nhận được phản hồi. Câu hỏi có thể đã được lưu; hãy kiểm tra lịch sử trước khi gửi lại.',
        action: 'reload',
      });
    else setNotice({ text: 'Chưa gửi được câu hỏi. Hãy kiểm tra nội dung và thử lại.' });
  }

  async function send() {
    const question = draft.trim();
    if (lockedRef.current || loading || notice?.action || !question || words(question) > 3000) return;
    lockedRef.current = true;
    setSending(true);
    setPending(question);
    setNotice(null);
    setScrollVersion(value => value + 1);
    const requestEpoch = epochRef.current;
    try {
      let id = currentIdRef.current;
      if (!id) {
        const created = await createConversation();
        if (epochRef.current !== requestEpoch) return;
        id = created.conversationId;
        currentIdRef.current = id;
        remember(question, id);
        setParams({ conversation: String(id) }, { replace: true });
      }
      const sent = await sendQuestion(id, question);
      if (epochRef.current !== requestEpoch) return;
      setMessages(previous => [
        ...previous,
        { id: sent.userMessageId, senderRole: 'USER', content: question, citations: [], generationStatus: 'NOT_APPLICABLE' },
        {
          id: sent.assistantMessageId,
          senderRole: 'ASSISTANT',
          content: sent.result.answer,
          citations: sent.result.citations,
          generationStatus: sent.result.status,
        },
      ]);
      remember('');
    } catch (error) {
      if (epochRef.current === requestEpoch) handleError(error);
    } finally {
      if (epochRef.current === requestEpoch) {
        lockedRef.current = false;
        setSending(false);
        setPending('');
      }
    }
  }

  function newConversation() {
    if (lockedRef.current || loading) return;
    remember('');
    currentIdRef.current = null;
    initializedRef.current = false;
    setMessages([]);
    setNotice(null);
    setReload(0);
    setParams({});
  }

  const blocked = loading || sending || Boolean(notice?.action);
  const tooLong = words(draft) > 3000;
  return {
    messages,
    draft,
    pending,
    loading,
    sending,
    notice,
    blocked,
    tooLong,
    scrollVersion,
    remember,
    send,
    newConversation,
    reloadHistory: () => setReload(value => value + 1),
    loginSearch: params.toString() ? `?${params}` : '',
  };
}
