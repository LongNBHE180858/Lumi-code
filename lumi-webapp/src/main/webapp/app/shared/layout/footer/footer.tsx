import './footer.scss';

import React from 'react';
import { Translate } from 'react-jhipster';
import { Link } from 'react-router';

import logo from '/content/images/logo-lumi.png';

const Footer = () => (
  <footer className="lumi-footer" id="app-footer">
    <div className="footer-container">
      {/* Top Row: Brand & Navigation Links */}
      <div className="footer-top-row">
        <div className="footer-brand">
          <Link to="/" className="brand-logo-wrap">
            <img src={logo} alt="LUMI Logo" className="lumi-logo-footer" />
          </Link>
          <p className="footer-slogan">Khơi mở tri thức, theo cách của bạn.</p>
        </div>

        <nav className="footer-links" aria-label="Footer Navigation">
          <Link to="/about" className="footer-nav-link">
            Giới thiệu
          </Link>
          <Link to="/courses" className="footer-nav-link">
            Khóa học
          </Link>
          <Link to="/terms" className="footer-nav-link">
            Điều khoản sử dụng
          </Link>
          <Link to="/privacy" className="footer-nav-link">
            Chính sách bảo mật
          </Link>
          <Link to="/contact" className="footer-nav-link">
            Liên hệ
          </Link>
          <Link to="/feedback" className="footer-nav-link" data-cy="footerFeedback">
            <Translate contentKey="global.menu.feedback">Góp ý & Báo lỗi</Translate>
          </Link>
        </nav>
      </div>

      {/* Divider */}
      <div className="footer-divider" />

      {/* Bottom Row: Copyright Notice */}
      <div className="footer-bottom">
        <p className="copyright-text">© LUMI — Nền tảng học tập trực tuyến tích hợp AI</p>
      </div>
    </div>
  </footer>
);

export default Footer;
