import './home.scss';

import React, { useEffect, useState } from 'react';
import { Col, Row } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router';

import { useAppSelector } from 'app/config/store';

import heroIllustration from '/content/images/hero-illustration.png';
import subjectHistory from '/content/images/subject-history.png';
import subjectMath from '/content/images/subject-math.png';

/* --------------------------------------------------------------------------
   Inline SVG Icons (Pixel-matched with Figma Specs)
   -------------------------------------------------------------------------- */
const SearchIcon = () => (
  <svg
    width="18"
    height="18"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="2.2"
    strokeLinecap="round"
    strokeLinejoin="round"
  >
    <circle cx="11" cy="11" r="8" />
    <line x1="21" y1="21" x2="16.65" y2="16.65" />
  </svg>
);

const CheckIcon = () => (
  <svg
    width="20"
    height="20"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="2.5"
    strokeLinecap="round"
    strokeLinejoin="round"
  >
    <polyline points="20 6 9 17 4 12" />
  </svg>
);

const ArrowRightIcon = () => (
  <svg
    width="16"
    height="16"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="2.2"
    strokeLinecap="round"
    strokeLinejoin="round"
  >
    <line x1="5" y1="12" x2="19" y2="12" />
    <polyline points="12 5 19 12 12 19" />
  </svg>
);

const UsersIcon = () => (
  <svg
    width="22"
    height="22"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
  >
    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
    <circle cx="9" cy="7" r="4" />
    <path d="M23 21v-2a4 4 0 0 0-3-3.87" />
    <path d="M16 3.13a4 4 0 0 1 0 7.75" />
  </svg>
);

const StarIcon = () => (
  <svg width="22" height="22" viewBox="0 0 24 24" fill="#FFA800" stroke="#FFA800" strokeWidth="1">
    <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" />
  </svg>
);

const BookOpenIcon = () => (
  <svg
    width="22"
    height="22"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
  >
    <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z" />
    <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z" />
  </svg>
);

const RouteIcon = () => (
  <svg
    width="24"
    height="24"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
  >
    <circle cx="6" cy="19" r="3" />
    <path d="M9 19h8.5a3.5 3.5 0 0 0 0-7h-11a3.5 3.5 0 0 1 0-7H15" />
    <circle cx="18" cy="5" r="3" />
  </svg>
);

const SparklesIcon = () => (
  <svg
    width="24"
    height="24"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
  >
    <path d="m12 3-1.912 5.813a2 2 0 0 1-1.275 1.275L3 12l5.813 1.912a2 2 0 0 1 1.275 1.275L12 21l1.912-5.813a2 2 0 0 1 1.275-1.275L21 12l-5.813-1.912a2 2 0 0 1-1.275-1.275L12 3Z" />
    <path d="M5 3v4" />
    <path d="M19 17v4" />
    <path d="M3 5h4" />
    <path d="M17 19h4" />
  </svg>
);

const NotebookPenIcon = () => (
  <svg
    width="24"
    height="24"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
  >
    <path d="M2 6h4" />
    <path d="M2 10h4" />
    <path d="M2 14h4" />
    <path d="M2 18h4" />
    <rect width="16" height="20" x="4" y="2" rx="2" />
    <path d="M16 2v20" />
  </svg>
);

const ChartIcon = () => (
  <svg
    width="24"
    height="24"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
  >
    <path d="M3 3v18h18" />
    <path d="m19 9-5 5-4-4-3 3" />
  </svg>
);

const ScanLineIcon = () => (
  <svg
    width="24"
    height="24"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
  >
    <path d="M3 7V5a2 2 0 0 1 2-2h2" />
    <path d="M17 3h2a2 2 0 0 1 2 2v2" />
    <path d="M21 17v2a2 2 0 0 1-2 2h-2" />
    <path d="M7 21H5a2 2 0 0 1-2-2v-2" />
    <line x1="7" y1="12" x2="17" y2="12" />
  </svg>
);

const TrendingUpIcon = () => (
  <svg
    width="24"
    height="24"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
  >
    <polyline points="22 7 13.5 15.5 8.5 10.5 2 17" />
    <polyline points="16 7 22 7 22 13" />
  </svg>
);

export const Home = () => {
  const account = useAppSelector(state => state.authentication.account);
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);
  const [searchQuery, setSearchQuery] = useState('');
  const navigate = useNavigate();

  // Scroll Reveal Observer (vio.edu.vn style)
  useEffect(() => {
    const scrollContainer = document.getElementById('app-view-container');
    const elements = document.querySelectorAll('.reveal-on-scroll');

    const observer = new IntersectionObserver(
      (entries, obs) => {
        entries.forEach(entry => {
          if (entry.isIntersecting) {
            entry.target.classList.add('is-revealed');
            obs.unobserve(entry.target);
          }
        });
      },
      {
        root: scrollContainer || null,
        threshold: 0.1,
        rootMargin: '0px 0px -40px 0px',
      },
    );

    elements.forEach(el => observer.observe(el));

    return () => {
      observer.disconnect();
    };
  }, []);

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      navigate(`/courses?search=${encodeURIComponent(searchQuery.trim())}`);
    } else {
      navigate('/courses');
    }
  };

  return (
    <div className="lumi-home">
      {/* ====================================================================
          1. HERO SECTION (#267:26)
          ==================================================================== */}
      <section className="hero-section">
        <div className="hero-container">
          <Row className="align-items-center g-4">
            <Col lg={6}>
              <div className="hero-badge reveal-on-scroll">
                <span className="badge-dot" />
                <span>{isAuthenticated ? `Chào mừng trở lại, ${account.login}!` : 'Học mọi lúc - Tiến bộ mỗi ngày'}</span>
              </div>

              <h1 className="hero-heading reveal-on-scroll reveal-delay-1">
                Học thông minh hơn <br />
                cùng{' '}
                <span className="text-lumi">
                  LUMI!
                  <span className="heading-underline" />
                </span>
              </h1>

              <p className="hero-description reveal-on-scroll reveal-delay-2">
                Nền tảng học tập trực tuyến giúp học sinh ôn luyện, củng cố kiến thức và phát triển kỹ năng toàn diện.
              </p>

              {/* Search Box */}
              <form onSubmit={handleSearchSubmit} className="hero-search-box reveal-on-scroll reveal-delay-3">
                <div className="search-input-group">
                  <span className="search-icon">
                    <SearchIcon />
                  </span>
                  <input
                    type="text"
                    value={searchQuery}
                    onChange={e => setSearchQuery(e.target.value)}
                    placeholder="Em muốn khám phá môn gì hôm nay?"
                  />
                </div>
                <button type="submit" className="search-btn">
                  Tìm kiếm
                </button>
              </form>

              {/* Social Proof & Metrics */}
              <div className="hero-stats-row reveal-on-scroll reveal-delay-4">
                <div className="stat-item">
                  <span className="stat-icon">
                    <UsersIcon />
                  </span>
                  <div className="stat-content">
                    <span className="stat-number">50K+</span>
                    <span className="stat-label">Học sinh tin dùng</span>
                  </div>
                </div>

                <div className="stat-item">
                  <span className="stat-icon">
                    <StarIcon />
                  </span>
                  <div className="stat-content">
                    <span className="stat-number">4.9/5</span>
                    <span className="stat-label">Đánh giá từ học sinh</span>
                  </div>
                </div>

                <div className="stat-item">
                  <span className="stat-icon">
                    <BookOpenIcon />
                  </span>
                  <div className="stat-content">
                    <span className="stat-number">100+</span>
                    <span className="stat-label">Khóa học chất lượng</span>
                  </div>
                </div>
              </div>
            </Col>

            <Col lg={6}>
              <div className="hero-artwork-wrapper reveal-on-scroll reveal-fade-right reveal-delay-2">
                <img src={heroIllustration} alt="LUMI Học sinh và tri thức" className="hero-illustration" />

                {/* Floating Completion Card */}
                <div className="floating-completion-card reveal-on-scroll reveal-scale reveal-delay-4">
                  <div className="check-icon-circle">
                    <CheckIcon />
                  </div>
                  <div className="completion-text">
                    <span className="completion-sub">Hoàn thành</span>
                    <div className="completion-main">Bài học hôm nay</div>
                    <div className="progress-bar-custom">
                      <div className="progress-fill" />
                    </div>
                  </div>
                </div>
              </div>
            </Col>
          </Row>
        </div>
      </section>

      {/* ====================================================================
          2. BENEFITS SECTION (#267:80 - Lợi ích của LUMI)
          ==================================================================== */}
      <section className="benefits-section">
        <div className="benefits-container">
          <div className="section-header reveal-on-scroll">
            <span className="section-tagline tagline-blue">HIỂU BẠN, ĐỒNG HÀNH CÙNG BẠN</span>
            <h2 className="section-title">LUMI hỗ trợ việc học như thế nào?</h2>
            <p className="section-subtitle">Các công cụ cốt lõi đồng hành cùng học sinh trong từng buổi học.</p>
          </div>

          <Row className="g-4">
            <Col md={6} lg={3}>
              <div className="benefit-card reveal-on-scroll reveal-delay-1">
                <div className="benefit-icon-box">
                  <RouteIcon />
                </div>
                <div className="benefit-body">
                  <h4>Học tập cá nhân hóa</h4>
                  <p>Nội dung học được đề xuất phù hợp với năng lực và tiến độ của học sinh.</p>
                </div>
              </div>
            </Col>

            <Col md={6} lg={3}>
              <div className="benefit-card reveal-on-scroll reveal-delay-2">
                <div className="benefit-icon-box">
                  <SparklesIcon />
                </div>
                <div className="benefit-body">
                  <h4>AI hỗ trợ kiến thức</h4>
                  <p>Hỗ trợ giải thích kiến thức khi học sinh gặp khó khăn.</p>
                </div>
              </div>
            </Col>

            <Col md={6} lg={3}>
              <div className="benefit-card reveal-on-scroll reveal-delay-3">
                <div className="benefit-icon-box">
                  <NotebookPenIcon />
                </div>
                <div className="benefit-body">
                  <h4>Học và luyện tập</h4>
                  <p>Kết hợp nội dung bài học, bài tập và kiểm tra.</p>
                </div>
              </div>
            </Col>

            <Col md={6} lg={3}>
              <div className="benefit-card reveal-on-scroll reveal-delay-4">
                <div className="benefit-icon-box">
                  <ChartIcon />
                </div>
                <div className="benefit-body">
                  <h4>Theo dõi tiến độ</h4>
                  <p>Theo dõi quá trình học tập và kết quả theo thời gian.</p>
                </div>
              </div>
            </Col>
          </Row>
        </div>
      </section>

      {/* ====================================================================
          3. EXPLORE SUBJECTS SECTION (#267:114 - Khám phá môn học)
          ==================================================================== */}
      <section className="subjects-section">
        <div className="subjects-container">
          <div className="section-header reveal-on-scroll">
            <span className="section-tagline tagline-green">TỪ TÒ MÒ ĐẾN THẤU HIỂU</span>
            <h2 className="section-title">Khám phá môn học</h2>
            <p className="section-subtitle">Chọn môn học phù hợp để bắt đầu lộ trình từ lớp 1 đến lớp 9.</p>
          </div>

          <Row className="g-4">
            {/* Subject: Toán */}
            <Col lg={6}>
              <div className="subject-card reveal-on-scroll reveal-delay-1">
                <div className="subject-img-wrap">
                  <span className="grade-badge">Lớp 1–9</span>
                  <img src={subjectMath} alt="Môn Toán" />
                </div>
                <div className="subject-content">
                  <div>
                    <h3 className="subject-title">Toán</h3>
                    <p className="subject-desc">Từ những con số đến tư duy logic và cách giải quyết vấn đề.</p>
                  </div>
                  <Link to="/courses?subject=math" className="subject-action-btn">
                    <span>Khám phá Toán</span>
                    <ArrowRightIcon />
                  </Link>
                </div>
              </div>
            </Col>

            {/* Subject: Lịch sử */}
            <Col lg={6}>
              <div className="subject-card reveal-on-scroll reveal-delay-2">
                <div className="subject-img-wrap">
                  <span className="grade-badge">Lớp 1–9</span>
                  <img src={subjectHistory} alt="Môn Lịch sử" />
                </div>
                <div className="subject-content">
                  <div>
                    <h3 className="subject-title">Lịch sử</h3>
                    <p className="subject-desc">Khám phá dòng thời gian, hiểu sự kiện và kết nối tri thức.</p>
                  </div>
                  <Link to="/courses?subject=history" className="subject-action-btn">
                    <span>Khám phá Lịch sử</span>
                    <ArrowRightIcon />
                  </Link>
                </div>
              </div>
            </Col>
          </Row>
        </div>
      </section>

      {/* ====================================================================
          4. ROADMAP SECTION (#267:148 - Lộ trình học tập)
          ==================================================================== */}
      <section className="roadmap-section">
        <div className="roadmap-container">
          <div className="section-header reveal-on-scroll">
            <span className="section-tagline tagline-blue">MỖI BƯỚC MỘT TIẾN BỘ</span>
            <h2 className="section-title">Lộ trình học tập cùng LUMI</h2>
            <p className="section-subtitle">Một hành trình liên tục, được điều chỉnh dựa trên kết quả học tập.</p>
          </div>

          <div className="roadmap-steps-row">
            {/* Step 1 */}
            <div className="step-card reveal-on-scroll reveal-delay-1">
              <div className="step-top">
                <span className="step-number">1</span>
                <span className="step-icon">
                  <ScanLineIcon />
                </span>
              </div>
              <h4 className="step-title">Đánh giá năng lực</h4>
              <p className="step-desc">Xác định điểm bắt đầu phù hợp.</p>
              <span className="step-arrow-next">
                <ArrowRightIcon />
              </span>
            </div>

            {/* Step 2 */}
            <div className="step-card reveal-on-scroll reveal-delay-2">
              <div className="step-top">
                <span className="step-number">2</span>
                <span className="step-icon">
                  <BookOpenIcon />
                </span>
              </div>
              <h4 className="step-title">Học và luyện tập</h4>
              <p className="step-desc">Học bài mới và thực hành theo lộ trình.</p>
              <span className="step-arrow-next">
                <ArrowRightIcon />
              </span>
            </div>

            {/* Step 3 */}
            <div className="step-card reveal-on-scroll reveal-delay-3">
              <div className="step-top">
                <span className="step-number">3</span>
                <span className="step-icon">
                  <SparklesIcon />
                </span>
              </div>
              <h4 className="step-title">AI phân tích kết quả</h4>
              <p className="step-desc">Nhận diện nội dung đã vững và cần ôn.</p>
              <span className="step-arrow-next">
                <ArrowRightIcon />
              </span>
            </div>

            {/* Step 4 */}
            <div className="step-card reveal-on-scroll reveal-delay-4">
              <div className="step-top">
                <span className="step-number">4</span>
                <span className="step-icon">
                  <TrendingUpIcon />
                </span>
              </div>
              <h4 className="step-title">Theo dõi và cải thiện</h4>
              <p className="step-desc">Điều chỉnh lộ trình cho bước tiếp theo.</p>
            </div>
          </div>
        </div>
      </section>

      {/* ====================================================================
          5. CALL TO ACTION SECTION (#267:200 - Khởi đầu cùng LUMI)
          ==================================================================== */}
      <section className="cta-section">
        <div className="cta-container">
          <div className="cta-card reveal-on-scroll reveal-scale">
            {/* Decorative background circles */}
            <div className="cta-circle-1" />
            <div className="cta-circle-2" />

            <div className="cta-sparkle-box reveal-on-scroll reveal-delay-1">
              <SparklesIcon />
            </div>

            <h2 className="cta-heading reveal-on-scroll reveal-delay-2">Bắt đầu hành trình học tập cùng LUMI</h2>
            <p className="cta-subtitle reveal-on-scroll reveal-delay-3">Chọn môn học và khám phá lộ trình phù hợp với năng lực của bạn.</p>

            <div className="cta-actions reveal-on-scroll reveal-delay-4">
              {isAuthenticated ? (
                <Link to="/courses" className="cta-btn-primary">
                  <span>Tiếp tục học tập</span>
                  <ArrowRightIcon />
                </Link>
              ) : (
                <Link to="/account/register" className="cta-btn-primary">
                  <span>Đăng ký miễn phí</span>
                  <ArrowRightIcon />
                </Link>
              )}

              <Link to="/courses" className="cta-btn-secondary">
                <span>Khám phá khóa học</span>
              </Link>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default Home;
