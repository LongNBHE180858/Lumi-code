import './tickets.scss';

import React, { useEffect, useState, type FormEvent } from 'react';
import { ArrowRight, Plus, RefreshCw } from 'lucide-react';
import { Link, useNavigate } from 'react-router';

import AdminLayout from 'app/modules/administration/layout/admin-layout';
import { ticketApi, type Ticket, type TicketStatus } from './ticket-api';
import { statusLabel, ticketDate, ticketError, ticketTypes, typeLabel } from './ticket-format';

const pageSize = 10;

const TicketList = ({ admin = false }: { admin?: boolean }) => {
  const navigate = useNavigate();
  const [tickets, setTickets] = useState<Ticket[]>([]);
  const [page, setPage] = useState(0);
  const [total, setTotal] = useState(0);
  const [statusFilter, setStatusFilter] = useState<TicketStatus | ''>('');
  const [typeFilter, setTypeFilter] = useState('');
  const [query, setQuery] = useState('');
  const [search, setSearch] = useState('');
  const [refreshKey, setRefreshKey] = useState(0);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState('');
  const [showCreate, setShowCreate] = useState(false);
  const [type, setType] = useState<string>(ticketTypes[0].value);
  const [subject, setSubject] = useState('');
  const [content, setContent] = useState('');
  const [creating, setCreating] = useState(false);
  const [createError, setCreateError] = useState('');

  useEffect(() => {
    const timer = window.setTimeout(() => {
      setSearch(query.trim());
      setPage(0);
    }, 300);
    return () => window.clearTimeout(timer);
  }, [query]);

  useEffect(() => {
    let active = true;
    setLoading(true);
    setLoadError('');
    const request = admin
      ? ticketApi.listAdmin(page, pageSize, statusFilter || undefined, typeFilter || undefined, search || undefined)
      : ticketApi.listMine(page, pageSize);
    request
      .then(response => {
        if (!active) return;
        setTickets(response.data);
        setTotal(Number(response.headers['x-total-count'] ?? response.data.length));
      })
      .catch(error => {
        if (!active) return;
        setTickets([]);
        setTotal(0);
        setLoadError(ticketError(error));
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, [admin, page, statusFilter, typeFilter, search, refreshKey]);

  const createTicket = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const trimmedSubject = subject.trim();
    const trimmedContent = content.trim();
    if (!trimmedSubject || !trimmedContent) {
      setCreateError('Vui lòng nhập tiêu đề và nội dung yêu cầu.');
      return;
    }
    setCreating(true);
    setCreateError('');
    try {
      const response = await ticketApi.create({ type, subject: trimmedSubject, content: trimmedContent });
      navigate(`/support-tickets/${response.data.id}`);
    } catch (error) {
      setCreateError(ticketError(error));
    } finally {
      setCreating(false);
    }
  };

  const detailBase = admin ? '/admin/support-tickets' : '/support-tickets';
  const pageCount = Math.max(1, Math.ceil(total / pageSize));
  const main = (
    <main className={`ticket-page${admin ? ' ticket-page-admin admin-page-content' : ' ticket-page-user'}`}>
      <div className="ticket-panel">
        <div className="ticket-heading">
          <div>
            <span className="ticket-eyebrow">TRUNG TÂM HỖ TRỢ</span>
            <h1>{admin ? 'Quản lý yêu cầu hỗ trợ' : 'Yêu cầu hỗ trợ của tôi'}</h1>
            <p>{admin ? 'Theo dõi và trả lời các yêu cầu từ người dùng.' : 'Gửi câu hỏi và trao đổi trực tiếp với đội ngũ LUMI.'}</p>
          </div>
          <div className="ticket-heading-actions">
            <button
              type="button"
              className="ticket-button ticket-button-outline"
              onClick={() => setRefreshKey(value => value + 1)}
              disabled={loading}
            >
              <RefreshCw size={16} aria-hidden="true" /> Làm mới
            </button>
            {!admin && (
              <button type="button" className="ticket-button ticket-button-primary" onClick={() => setShowCreate(value => !value)}>
                <Plus size={16} aria-hidden="true" /> Tạo yêu cầu
              </button>
            )}
          </div>
        </div>

        {!admin && showCreate && (
          <form className="ticket-create-form" onSubmit={createTicket}>
            <h2>Tạo yêu cầu mới</h2>
            <div className="ticket-form-grid">
              <label>
                <span>Loại yêu cầu</span>
                <select value={type} onChange={event => setType(event.target.value)}>
                  {ticketTypes.map(item => (
                    <option key={item.value} value={item.value}>
                      {item.label}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                <span>Tiêu đề</span>
                <input
                  value={subject}
                  onChange={event => setSubject(event.target.value)}
                  maxLength={255}
                  required
                  placeholder="Bạn cần hỗ trợ điều gì?"
                />
              </label>
            </div>
            <label>
              <span>Nội dung</span>
              <textarea
                value={content}
                onChange={event => setContent(event.target.value)}
                maxLength={5000}
                rows={5}
                required
                placeholder="Mô tả vấn đề hoặc câu hỏi của bạn..."
              />
            </label>
            {createError && (
              <p className="ticket-error" role="alert">
                {createError}
              </p>
            )}
            <div className="ticket-form-actions">
              <button
                type="button"
                className="ticket-button ticket-button-outline"
                onClick={() => setShowCreate(false)}
                disabled={creating}
              >
                Hủy
              </button>
              <button type="submit" className="ticket-button ticket-button-primary" disabled={creating}>
                {creating ? 'Đang gửi...' : 'Gửi yêu cầu'}
              </button>
            </div>
          </form>
        )}

        {admin && (
          <div className="ticket-filters">
            <label>
              <span>Trạng thái</span>
              <select
                value={statusFilter}
                onChange={event => {
                  setStatusFilter(event.target.value as TicketStatus | '');
                  setPage(0);
                }}
              >
                <option value="">Tất cả</option>
                <option value="OPEN">Mới</option>
                <option value="IN_PROGRESS">Đang xử lý</option>
                <option value="RESOLVED">Đã giải quyết</option>
              </select>
            </label>
            <label>
              <span>Loại yêu cầu</span>
              <select
                value={typeFilter}
                onChange={event => {
                  setTypeFilter(event.target.value);
                  setPage(0);
                }}
              >
                <option value="">Tất cả</option>
                {ticketTypes.map(item => (
                  <option key={item.value} value={item.value}>
                    {item.label}
                  </option>
                ))}
              </select>
            </label>
          </div>
        )}

        {loadError && (
          <p className="ticket-error" role="alert">
            {loadError}
          </p>
        )}
        {loading ? (
          <p className="ticket-muted" role="status">
            Đang tải danh sách yêu cầu...
          </p>
        ) : tickets.length === 0 ? (
          <div className="ticket-empty">
            <h2>Chưa có yêu cầu nào</h2>
            <p>{admin ? 'Thử thay đổi bộ lọc hoặc tìm kiếm.' : 'Khi cần hỗ trợ, bạn có thể tạo yêu cầu mới tại đây.'}</p>
          </div>
        ) : (
          <div className="ticket-table-wrap">
            <table className="ticket-table">
              <thead>
                <tr>
                  <th>Mã</th>
                  <th>Tiêu đề</th>
                  <th>Loại</th>
                  {admin && <th>Người tạo</th>}
                  <th>Trạng thái</th>
                  <th>Ngày tạo</th>
                  <th>
                    <span className="visually-hidden">Xem</span>
                  </th>
                </tr>
              </thead>
              <tbody>
                {tickets.map(ticket => (
                  <tr key={ticket.id}>
                    <td>#{ticket.id}</td>
                    <td>
                      <Link className="ticket-subject-link" to={`${detailBase}/${ticket.id}`}>
                        {ticket.subject}
                      </Link>
                    </td>
                    <td>{typeLabel(ticket.type)}</td>
                    {admin && <td>{ticket.userLogin}</td>}
                    <td>
                      <span className={`ticket-status ticket-status-${ticket.status.toLowerCase()}`}>{statusLabel(ticket.status)}</span>
                    </td>
                    <td>{ticketDate(ticket.createdAt)}</td>
                    <td>
                      <Link className="ticket-row-link" to={`${detailBase}/${ticket.id}`} aria-label={`Xem yêu cầu #${ticket.id}`}>
                        <ArrowRight size={17} />
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {!loading && total > 0 && (
          <div className="ticket-pagination">
            <span>
              Trang {page + 1}/{pageCount} · {total} yêu cầu
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
      </div>
    </main>
  );

  return admin ? (
    <AdminLayout searchValue={query} onSearchChange={setQuery} searchLabel="Tìm theo tiêu đề hoặc người tạo">
      {main}
    </AdminLayout>
  ) : (
    main
  );
};

export default TicketList;
