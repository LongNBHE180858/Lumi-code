import './tickets.scss';

import React, { useEffect, useState, type FormEvent } from 'react';
import { ArrowLeft, RefreshCw, Send } from 'lucide-react';
import { Link, useParams } from 'react-router';

import { useAppSelector } from 'app/config/store';
import AdminLayout from 'app/modules/administration/layout/admin-layout';
import { ticketApi, type Ticket, type TicketMessage } from './ticket-api';
import TicketImagePicker from './ticket-image-picker';
import { statusLabel, ticketDate, ticketError, typeLabel } from './ticket-format';

const messagePageSize = 50;

const MessageImage = ({
  ticketId,
  messageId,
  index,
  fileName,
}: {
  ticketId: number;
  messageId: number;
  index: number;
  fileName: string;
}) => {
  const [url, setUrl] = useState('');
  const [failed, setFailed] = useState(false);
  useEffect(() => {
    let active = true;
    let refreshTimer: number | undefined;
    const load = async () => {
      try {
        const response = await ticketApi.imageAccess(ticketId, messageId, index);
        if (!active) return;
        setUrl(response.data.url);
        setFailed(false);
        const delay = Math.max(1000, new Date(response.data.expiresAt).getTime() - Date.now() - 30_000);
        if (Number.isFinite(delay)) refreshTimer = window.setTimeout(load, delay);
      } catch {
        if (active) {
          setUrl('');
          setFailed(true);
        }
      }
    };
    void load();
    return () => {
      active = false;
      window.clearTimeout(refreshTimer);
    };
  }, [ticketId, messageId, index]);
  return url ? (
    <a href={url} target="_blank" rel="noopener noreferrer" aria-label={`Xem ảnh ${fileName}`}>
      <img src={url} alt={fileName} loading="lazy" />
    </a>
  ) : failed ? (
    <span>Không tải được ảnh {fileName}</span>
  ) : null;
};

const TicketDetail = ({ admin = false }: { admin?: boolean }) => {
  const { id } = useParams();
  const ticketId = Number(id);
  const login = useAppSelector(state => state.authentication.account.login);
  const [ticket, setTicket] = useState<Ticket | null>(null);
  const [messages, setMessages] = useState<TicketMessage[]>([]);
  const [page, setPage] = useState(0);
  const [total, setTotal] = useState(0);
  const [refreshKey, setRefreshKey] = useState(0);
  const [loading, setLoading] = useState(true);
  const [messagesLoading, setMessagesLoading] = useState(true);
  const [loadError, setLoadError] = useState('');
  const [messageError, setMessageError] = useState('');
  const [actionError, setActionError] = useState('');
  const [reply, setReply] = useState('');
  const [images, setImages] = useState<File[]>([]);
  const [sending, setSending] = useState(false);
  const [acting, setActing] = useState(false);

  useEffect(() => {
    let active = true;
    if (!Number.isSafeInteger(ticketId) || ticketId < 1) {
      setLoadError('Mã yêu cầu không hợp lệ.');
      setLoading(false);
      return;
    }
    setLoading(true);
    setLoadError('');
    ticketApi
      .get(ticketId)
      .then(response => {
        if (active) setTicket(response.data);
      })
      .catch(error => {
        if (active) {
          setTicket(null);
          setLoadError(ticketError(error));
        }
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, [ticketId, refreshKey]);

  useEffect(() => {
    let active = true;
    if (!Number.isSafeInteger(ticketId) || ticketId < 1) {
      setMessagesLoading(false);
      return;
    }
    setMessagesLoading(true);
    setMessageError('');
    ticketApi
      .messages(ticketId, page, messagePageSize)
      .then(response => {
        if (!active) return;
        setMessages(response.data);
        setTotal(Number(response.headers['x-total-count'] ?? response.data.length));
      })
      .catch(error => {
        if (!active) return;
        setMessages([]);
        setMessageError(ticketError(error));
      })
      .finally(() => {
        if (active) setMessagesLoading(false);
      });
    return () => {
      active = false;
    };
  }, [ticketId, page, refreshKey]);

  const sendMessage = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const content = reply.trim();
    if (!content && images.length === 0) {
      setActionError('Vui lòng nhập nội dung hoặc chọn ảnh.');
      return;
    }
    setSending(true);
    setActionError('');
    try {
      if (images.length) await ticketApi.sendMessage(ticketId, content, images);
      else await ticketApi.sendMessage(ticketId, content);
      setReply('');
      setImages([]);
      setPage(Math.floor(total / messagePageSize));
      setRefreshKey(value => value + 1);
    } catch (error) {
      setActionError(ticketError(error));
    } finally {
      setSending(false);
    }
  };

  const changeStatus = async (status: 'IN_PROGRESS' | 'RESOLVED') => {
    setActing(true);
    setActionError('');
    try {
      const response = await ticketApi.changeStatus(ticketId, status);
      setTicket(response.data);
    } catch (error) {
      setActionError(ticketError(error));
    } finally {
      setActing(false);
    }
  };

  const reopen = async () => {
    setActing(true);
    setActionError('');
    try {
      const response = await ticketApi.reopen(ticketId);
      setTicket(response.data);
    } catch (error) {
      setActionError(ticketError(error));
    } finally {
      setActing(false);
    }
  };

  const backUrl = admin ? '/admin/support-tickets' : '/support-tickets';
  const pageCount = Math.max(1, Math.ceil(total / messagePageSize));
  const main = (
    <main className={`ticket-page${admin ? ' ticket-page-admin admin-page-content' : ' ticket-page-user'}`}>
      <div className="ticket-panel">
        <div className="ticket-detail-back">
          <Link to={backUrl}>
            <ArrowLeft size={17} aria-hidden="true" /> Danh sách yêu cầu
          </Link>
        </div>
        {loadError && (
          <p className="ticket-error" role="alert">
            {loadError}
          </p>
        )}
        {loading ? (
          <p className="ticket-muted" role="status">
            Đang tải yêu cầu...
          </p>
        ) : ticket ? (
          <>
            <div className="ticket-heading ticket-detail-heading">
              <div>
                <span className="ticket-eyebrow">YÊU CẦU #{ticket.id}</span>
                <h1>{ticket.subject}</h1>
                <p>
                  {typeLabel(ticket.type)} · Tạo lúc {ticketDate(ticket.createdAt)}
                  {admin ? ` · ${ticket.userLogin}` : ''}
                </p>
              </div>
              <div className="ticket-heading-actions">
                <span className={`ticket-status ticket-status-${ticket.status.toLowerCase()}`}>{statusLabel(ticket.status)}</span>
                <button
                  type="button"
                  className="ticket-button ticket-button-outline"
                  onClick={() => setRefreshKey(value => value + 1)}
                  disabled={loading || messagesLoading}
                >
                  <RefreshCw size={16} aria-hidden="true" /> Làm mới
                </button>
              </div>
            </div>

            <div className="ticket-detail-actions">
              {admin && ticket.status === 'OPEN' && (
                <button
                  type="button"
                  className="ticket-button ticket-button-primary"
                  onClick={() => changeStatus('IN_PROGRESS')}
                  disabled={acting}
                >
                  Bắt đầu xử lý
                </button>
              )}
              {admin && ticket.status !== 'RESOLVED' && (
                <button
                  type="button"
                  className="ticket-button ticket-button-outline"
                  onClick={() => changeStatus('RESOLVED')}
                  disabled={acting}
                >
                  Đánh dấu đã giải quyết
                </button>
              )}
              {ticket.status === 'RESOLVED' && (
                <button type="button" className="ticket-button ticket-button-outline" onClick={reopen} disabled={acting}>
                  Mở lại yêu cầu
                </button>
              )}
            </div>

            <section className="ticket-conversation" aria-label="Hội thoại hỗ trợ">
              <h2>Hội thoại</h2>
              {messageError && (
                <p className="ticket-error" role="alert">
                  {messageError}
                </p>
              )}
              {messagesLoading ? (
                <p className="ticket-muted" role="status">
                  Đang tải tin nhắn...
                </p>
              ) : messages.length === 0 ? (
                <p className="ticket-muted">Chưa có tin nhắn.</p>
              ) : (
                <div className="ticket-message-list">
                  {messages.map(message => (
                    <article key={message.id} className={`ticket-message${message.senderLogin === login ? ' ticket-message-own' : ''}`}>
                      <div className="ticket-message-meta">
                        <strong>{message.senderLogin === login ? 'Bạn' : message.senderLogin}</strong>
                        <time>{ticketDate(message.createdAt)}</time>
                      </div>
                      {message.content && <p>{message.content}</p>}
                      {message.images?.length > 0 && (
                        <div className="ticket-message-images">
                          {message.images.map(image => (
                            <MessageImage
                              key={image.index}
                              ticketId={ticketId}
                              messageId={message.id}
                              index={image.index}
                              fileName={image.fileName}
                            />
                          ))}
                        </div>
                      )}
                    </article>
                  ))}
                </div>
              )}
              {!messagesLoading && total > messagePageSize && (
                <div className="ticket-pagination">
                  <span>
                    Trang {page + 1}/{pageCount} · {total} tin nhắn
                  </span>
                  <div>
                    <button
                      type="button"
                      className="ticket-button ticket-button-outline"
                      disabled={page === 0}
                      onClick={() => setPage(value => value - 1)}
                    >
                      Trước
                    </button>
                    <button
                      type="button"
                      className="ticket-button ticket-button-outline"
                      disabled={page + 1 >= pageCount}
                      onClick={() => setPage(value => value + 1)}
                    >
                      Sau
                    </button>
                  </div>
                </div>
              )}
            </section>

            <form className="ticket-reply-form" onSubmit={sendMessage}>
              <label htmlFor="ticket-reply">Trả lời</label>
              <textarea
                id="ticket-reply"
                value={reply}
                onChange={event => setReply(event.target.value)}
                maxLength={5000}
                rows={4}
                placeholder="Nhập tin nhắn của bạn..."
              />
              <TicketImagePicker images={images} onChange={setImages} onError={setActionError} disabled={sending} />
              {ticket.status === 'RESOLVED' && <p className="ticket-muted">Tin nhắn mới sẽ tự mở lại yêu cầu và chuyển sang đang xử lý.</p>}
              {actionError && (
                <p className="ticket-error" role="alert">
                  {actionError}
                </p>
              )}
              <div className="ticket-form-actions">
                <button type="submit" className="ticket-button ticket-button-primary" disabled={sending}>
                  <Send size={16} aria-hidden="true" /> {sending ? 'Đang gửi...' : 'Gửi tin nhắn'}
                </button>
              </div>
            </form>
          </>
        ) : null}
      </div>
    </main>
  );

  return admin ? <AdminLayout>{main}</AdminLayout> : main;
};

export default TicketDetail;
