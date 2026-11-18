import React, { useCallback, useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router';
import { classroomError, getStudentClasses, joinStudentClass, type JoinedClass } from 'app/shared/util/classroom-api';
import '../classroom.scss';

export const StudentClasses: React.FC = () => {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const [classes, setClasses] = useState<JoinedClass[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [code, setCode] = useState(() => searchParams.get('code')?.trim().toUpperCase() ?? '');
  const [loading, setLoading] = useState(true);
  const [joining, setJoining] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);

  const load = useCallback(async (requestedPage: number) => {
    setLoading(true);
    try {
      const result = await getStudentClasses(requestedPage);
      if (requestedPage > 0 && result.items.length === 0) {
        const previous = await getStudentClasses(requestedPage - 1);
        setClasses(previous.items);
        setPage(previous.page);
        setTotalPages(previous.totalPages);
        setTotalElements(previous.totalElements);
      } else {
        setClasses(result.items);
        setPage(result.page);
        setTotalPages(result.totalPages);
        setTotalElements(result.totalElements);
      }
      setError(null);
    } catch (cause) {
      setError(classroomError(cause));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void load(0);
  }, [load]);

  useEffect(() => {
    const sharedCode = searchParams.get('code')?.trim().toUpperCase();
    if (sharedCode) setCode(sharedCode);
  }, [searchParams]);

  const join = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!code.trim()) return;
    setJoining(true);
    setError(null);
    try {
      const joined = await joinStudentClass(code.trim().toUpperCase());
      setCode('');
      if (searchParams.has('code')) setSearchParams({}, { replace: true });
      setNotice(
        joined.membershipStatus === 'ACTIVE' ? `Đã vào lớp ${joined.name}.` : `Đã gửi yêu cầu vào lớp ${joined.name}. Chờ giáo viên duyệt.`,
      );
      await load(0);
    } catch (cause) {
      setError(classroomError(cause));
    } finally {
      setJoining(false);
    }
  };

  return (
    <main className="classroom-page">
      <header className="classroom-header classroom-hero">
        <div>
          <span className="classroom-eyebrow">HỌC TẬP CÙNG LUMI</span>
          <h1>Lớp học của tôi</h1>
          <p>Nhập mã hoặc mở liên kết giáo viên chia sẻ để tham gia lớp.</p>
        </div>
        <div className="classroom-hero-progress">
          <strong>{totalElements}</strong>
          <span>Lớp đã tham gia</span>
        </div>
      </header>
      <section className="classroom-panel">
        {searchParams.has('code') && (
          <p className="classroom-link-prompt">
            Bạn được mời tham gia lớp bằng liên kết. Kiểm tra mã lớp bên dưới rồi chọn “Tham gia lớp”.
          </p>
        )}
        <form className="classroom-join-form" onSubmit={event => void join(event)}>
          <label htmlFor="join-class-code">Mã lớp</label>
          <input
            id="join-class-code"
            value={code}
            onChange={event => setCode(event.target.value)}
            maxLength={10}
            required
            placeholder="Nhập mã lớp"
          />
          <button type="submit" disabled={joining}>
            {joining ? 'Đang xử lý...' : 'Tham gia lớp'}
          </button>
        </form>
        {error && (
          <p className="classroom-error" role="alert">
            {error}
          </p>
        )}
        {notice && (
          <p className="classroom-notice" role="status">
            {notice}
          </p>
        )}
      </section>
      <div className="classroom-metric-grid">
        <div className="classroom-metric-card">
          <span>Lớp của tôi</span>
          <strong>{totalElements}</strong>
          <small>Không gian học tập đang theo dõi</small>
        </div>
        <div className="classroom-metric-card">
          <span>Trong trang này</span>
          <strong>{classes.filter(item => item.membershipStatus === 'ACTIVE').length}</strong>
          <small>Lớp có thể truy cập ngay</small>
        </div>
        <div className="classroom-metric-card">
          <span>Chờ duyệt</span>
          <strong>{classes.filter(item => item.membershipStatus === 'PENDING').length}</strong>
          <small>Yêu cầu tham gia trong trang này</small>
        </div>
      </div>
      <section className="classroom-panel">
        <div className="classroom-section-heading">
          <div>
            <h2>Danh sách lớp</h2>
            <p>Chọn một lớp để xem bài học, bài được giao và kết quả kiểm tra.</p>
          </div>
        </div>
        {loading && <p>Đang tải danh sách...</p>}
        {!loading && classes.length === 0 && <p>Bạn chưa tham gia lớp nào.</p>}
        <div className="classroom-class-grid">
          {classes.map(item => (
            <div className="classroom-class-card" key={item.id}>
              <span className="classroom-class-icon">{item.name.slice(0, 1).toLocaleUpperCase('vi-VN')}</span>
              <div>
                <strong>{item.name}</strong>
                <small>{item.description || 'Chưa có mô tả.'}</small>
              </div>
              {item.membershipStatus === 'ACTIVE' ? (
                <button type="button" onClick={() => navigate(`/student/classes/${item.id}`)}>
                  Vào lớp
                </button>
              ) : (
                <span className="classroom-status">Chờ duyệt</span>
              )}
            </div>
          ))}
        </div>
        {totalPages > 1 && (
          <nav className="classroom-pagination" aria-label="Phân trang lớp học">
            <button type="button" disabled={loading || page === 0} onClick={() => void load(page - 1)}>
              Trang trước
            </button>
            <span>
              Trang {page + 1} / {totalPages} · {totalElements} lớp
            </span>
            <button type="button" disabled={loading || page + 1 >= totalPages} onClick={() => void load(page + 1)}>
              Trang sau
            </button>
          </nav>
        )}
      </section>
    </main>
  );
};

export default StudentClasses;
