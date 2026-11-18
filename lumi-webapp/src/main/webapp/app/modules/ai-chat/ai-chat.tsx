import './ai-chat.scss';
import 'katex/dist/katex.min.css';
import React, { useLayoutEffect, useRef } from 'react';
import { Link } from 'react-router';
import ReactMarkdown from 'react-markdown';
import remarkMath from 'remark-math';
import remarkGfm from 'remark-gfm';
import rehypeKatex from 'rehype-katex';
import useAiChat from './use-ai-chat';

const suggestions = [
  ['Toán', 'Phương trình bậc nhất hai ẩn là gì?'],
  ['Toán', 'Giải thích giúp mình cách giải hệ phương trình.'],
  ['Lịch sử', 'Giúp mình tìm hiểu nguyên nhân một sự kiện lịch sử.'],
];
export default function AiChat() {
  const {
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
    reloadHistory,
    loginSearch,
  } = useAiChat();
  const textareaRef = useRef<HTMLTextAreaElement>(null);
  const feedRef = useRef<HTMLDivElement>(null);
  const followBottomRef = useRef(true);

  useLayoutEffect(() => {
    followBottomRef.current = true;
  }, [scrollVersion]);

  useLayoutEffect(() => {
    if (textareaRef.current) {
      textareaRef.current.style.height = 'auto';
      textareaRef.current.style.height = `${Math.min(textareaRef.current.scrollHeight, 120)}px`;
    }
  }, [draft]);
  useLayoutEffect(() => {
    if (followBottomRef.current && feedRef.current) feedRef.current.scrollTop = feedRef.current.scrollHeight;
  }, [messages, pending, notice, scrollVersion]);

  return (
    <main className="ai-chat" aria-labelledby="ai-chat-title">
      <header className="ai-chat__heading">
        <div>
          <h1 id="ai-chat-title">Trợ lý học tập AI</h1>
          <p>Cùng bạn khám phá Toán và Lịch sử</p>
        </div>
        <button
          type="button"
          className="ai-chat__new"
          disabled={loading || sending}
          onClick={() => {
            newConversation();
            textareaRef.current?.focus();
          }}
        >
          <span aria-hidden="true">＋</span> Cuộc trò chuyện mới
        </button>
      </header>
      <div
        className="ai-chat__feed"
        ref={feedRef}
        aria-label="Nội dung cuộc trò chuyện"
        tabIndex={0}
        onScroll={() => {
          const el = feedRef.current;
          if (el) followBottomRef.current = el.scrollHeight - el.scrollTop - el.clientHeight < 80;
        }}
      >
        {loading && (
          <div role="status" className="ai-chat__answer">
            <strong>Đang tải lịch sử…</strong>
            <p>Mình đang lấy lại cuộc trò chuyện của bạn. Bạn chờ một chút nhé.</p>
          </div>
        )}
        {!loading && !messages.length && !pending && !notice && (
          <section className="ai-chat__welcome">
            <div className="ai-chat__symbol" aria-hidden="true">
              ✦
            </div>
            <h2>Bạn muốn tìm hiểu điều gì hôm nay?</h2>
            <p>Cùng tìm hiểu Toán và Lịch sử qua những lời giải thích dễ hiểu.</p>
            <div className="ai-chat__suggestions">
              {suggestions.map(([subject, question]) => (
                <button
                  type="button"
                  key={question}
                  onClick={() => {
                    remember(question);
                    textareaRef.current?.focus();
                  }}
                >
                  <span>
                    {subject}
                    <span aria-hidden="true">→</span>
                  </span>
                  <strong>{question}</strong>
                </button>
              ))}
            </div>
            <small>Tự tìm tài liệu học tập bạn được phép truy cập.</small>
          </section>
        )}
        {messages.map(message =>
          message.senderRole === 'USER' ? (
            <div className="ai-chat__question" key={message.id}>
              <small>Bạn</small>
              <p>{message.content}</p>
            </div>
          ) : (
            <article className="ai-chat__answer" key={message.id}>
              <div className="ai-chat__identity">
                <span aria-hidden="true">✦</span> LUMI AI
              </div>
              {message.generationStatus === 'CLARIFICATION' && <small>Cần làm rõ câu hỏi</small>}
              {message.generationStatus === 'INSUFFICIENT_CONTEXT' ? (
                <p>Chưa tìm thấy đủ thông tin trong tài liệu để trả lời. Bạn thử diễn đạt cụ thể hơn nhé.</p>
              ) : message.content ? (
                <ReactMarkdown
                  remarkPlugins={[remarkGfm, remarkMath]}
                  rehypePlugins={[rehypeKatex]}
                  skipHtml
                  components={{ a: ({ children }) => <span>{children}</span>, img: () => null }}
                >
                  {message.content}
                </ReactMarkdown>
              ) : (
                <p>Chưa có nội dung phản hồi cho tin nhắn này.</p>
              )}
              {message.citations?.length > 0 && (
                <aside className="ai-chat__sources" aria-label="Nguồn tham khảo">
                  <strong>Nguồn tham khảo</strong>
                  {message.citations.map(source => (
                    <div key={`${source.sourceNumber}-${source.chunkId}`}>
                      [{source.sourceNumber}] {source.documentTitle}
                      {source.pageNumber != null ? ` · Trang ${source.pageNumber}` : ''}
                    </div>
                  ))}
                </aside>
              )}
            </article>
          ),
        )}
        {pending && (
          <>
            <div className="ai-chat__question">
              <small>Bạn · Đang gửi</small>
              <p>{pending}</p>
            </div>
            <div className="ai-chat__answer" role="status">
              <div className="ai-chat__identity">
                <span aria-hidden="true">✦</span> LUMI AI
              </div>
              <p>Đang tìm hiểu câu hỏi của bạn…</p>
            </div>
          </>
        )}
        {notice && (
          <section className="ai-chat__notice" role="alert">
            <strong>{notice.fatal ? 'Chưa thể tiếp tục' : 'Cần kiểm tra cuộc trò chuyện'}</strong>
            <p>{notice.text}</p>
            {notice.action === 'reload' && (
              <button type="button" onClick={reloadHistory} disabled={loading}>
                Tải lại lịch sử
              </button>
            )}
            {notice.action === 'login' && (
              <Link to="/login" state={{ from: { pathname: '/ai-chat', search: loginSearch } }}>
                Đăng nhập lại
              </Link>
            )}
            {notice.action === 'home' && <Link to="/">Về trang chủ</Link>}
          </section>
        )}
      </div>
      <form
        className="ai-chat__composer"
        onSubmit={event => {
          event.preventDefault();
          void send();
        }}
      >
        <div className="ai-chat__input">
          <label className="visually-hidden" htmlFor="ai-question">
            Câu hỏi của bạn
          </label>
          <textarea
            id="ai-question"
            ref={textareaRef}
            rows={2}
            placeholder="Nhập câu hỏi của bạn…"
            value={draft}
            disabled={sending}
            aria-describedby="ai-chat-hint ai-chat-disclaimer"
            onChange={event => remember(event.target.value)}
            onKeyDown={event => {
              if (
                event.key === 'Enter' &&
                !event.shiftKey &&
                !event.nativeEvent.isComposing &&
                window.matchMedia('(pointer: fine)').matches
              ) {
                event.preventDefault();
                void send();
              }
            }}
          />
          <button type="submit" disabled={blocked || !draft.trim() || tooLong} aria-label="Gửi câu hỏi">
            ↑
          </button>
        </div>
        {tooLong && <p role="alert">Câu hỏi vượt giới hạn 3.000 từ. Bạn hãy rút gọn trước khi gửi.</p>}
        <small id="ai-chat-hint">Enter để gửi · Shift + Enter để xuống dòng</small>
        <small id="ai-chat-disclaimer">AI có thể mắc sai sót. Hãy đối chiếu với nguồn tham khảo.</small>
      </form>
    </main>
  );
}
