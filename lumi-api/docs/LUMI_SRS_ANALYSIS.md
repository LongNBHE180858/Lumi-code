# BẢN PHÂN TÍCH TỔNG QUAN TÀI LIỆU SRS DỰ ÁN LUMI PLATFORM
> **Dự án:** LUMI – Nền tảng học trực tuyến tích hợp AI cho học sinh Lớp 1 đến Lớp 9 (Toán học & Lịch sử)  
> **Tài liệu tham chiếu:** Capstone Project Report 3 – Software Requirement Specification (SRS)  
> **Phiên bản CSDL áp dụng:** LUMI V1.4 (59 Bảng chuẩn)

---

## I. MỤC TIÊU VÀ PHẠM VI DỰ ÁN (SCOPE & OBJECTIVES)
LUMI là hệ thống giáo dục trực tuyến thông minh (EdTech) bám sát chương trình Giáo dục Phổ thông của Bộ GD&ĐT (MOET), tập trung chuyên sâu vào:
1. **Toán học (Grades 1–9):** Luyện tập, vẽ hình học tương tác (Geometry Canvas), sinh đề thi AI, tự động kiểm định đáp án qua Python SymPy Sandbox.
2. **Lịch sử (Grades 1–9):** Dòng thời gian tương tác (Interactive Timeline), sơ đồ nguyên nhân - kết quả (Cause-Effect Graph), nhập vai đàm thoại nhân vật lịch sử qua AI Roleplay, đối soát sự kiện với Sách giáo khoa (độ khớp >= 80%).

---

## II. HỆ THỐNG 6 NHÓM TÁC NHÂN (ACTORS & ROLES)
1. **Guest (Khách vãng lai):** Xem trang chủ, tìm kiếm khóa học theo Môn/Khối lớp, xem hồ sơ giảng viên đã xác minh, đăng ký tài khoản.
2. **Student (Học sinh 6–15 tuổi):**
   - Học tập qua Video, tài liệu đọc, ghi chú cá nhân (theo timestamp video).
   - Tương tác với AI Tutor (RAG từ SGK), luyện tập bài tương tự, làm Flashcard, làm bài kiểm tra thích ứng (Adaptive AI Quiz).
   - Tham gia phòng thi trực tuyến có cơ chế Auto-save vào IndexedDB (<100ms) và giám sát trình duyệt (Anti-cheating).
   - Tích lũy Streak (chuỗi ngày học liên tục) và Gem đổi quà.
3. **Instructor (Giáo viên / Giảng viên):**
   - Soạn khóa học (Course Builder: Basic info, Curriculum, Pricing, Preview, Review).
   - Quản lý lớp học (Class Management: 3 chế độ tuyển sinh Auto, Manual, Entry Test).
   - Soạn đề thi (AI Exam Studio hoặc Ngân hàng câu hỏi), cấu hình bảo mật phòng thi, theo dõi giám sát trực tiếp.
   - Xem phân tích năng lực học sinh, giao bài tập bổ trợ (Remedial work).
   - Quản lý doanh thu (90/10 với mã giới thiệu, 80/20 với Marketplace), tạo coupon (tối đa 50%), yêu cầu rút tiền (Payout: 50k - 10tr, giữ 7 ngày).
4. **Parent (Phụ huynh):**
   - Liên kết tài khoản con qua mã 6 ký tự (hết hạn 24h).
   - Cấp quyền đồng thuận dữ liệu trẻ em (Nghị định 13/2023/NĐ-CP).
   - Xem báo cáo tiến độ, bảng điểm, biểu đồ lỗ hổng kiến thức (Skill Gap).
   - Nhận thông báo vi phạm thi khẩn cấp (qua App, Email, Zalo).
   - Giao nhiệm vụ học tập cho con (Parent Practice) và nhắn tin trực tiếp với giáo viên.
5. **Content Reviewer (Chuyên gia kiểm duyệt):**
   - Kiểm định nội dung do AI sinh, ngân hàng câu hỏi, duyệt khóa học trước khi mở bán để đảm bảo tính chuẩn xác sư phạm.
6. **Administrator (Quản trị viên):**
   - Xác minh bằng cấp giáo viên (trong vòng 72h).
   - Phê duyệt khóa học, quản lý danh mục/banner, cấu hình khóa API AI (mã hóa chuẩn cao).
   - Duyệt hoàn tiền (Refund), duyệt rút tiền (Payout), bảo trì Cây tri thức (Knowledge Graph DAG không chu trình), kiểm tra Audit Logs (lưu 365 ngày).

---

## III. KIẾN TRÚC DỮ LIỆU & QUY TẮC CƠ SỞ DỮ LIỆU (DATABASE SCHEMA V1.4)
CSDL gồm 59 bảng chính, phân tách theo các domain nghiệp vụ rõ ràng:

1. **User & Auth (5 bảng):** `jhi_user`, `user_profiles`, `teacher_verifications`, `parent_student_links`, `auth_sessions`.
2. **Khóa học & Bài học (8 bảng):** `subjects`, `grades`, `courses`, `chapters`, `lessons`, `course_materials`, `course_enrollments`, `wishlists`, `course_reviews`, `course_review_requests`.
3. **Lớp học (3 bảng):** `classes`, `class_members`, `class_materials`.
4. **Thương mại & Doanh thu (8 bảng):** `system_plans`, `teacher_quotas`, `coupons`, `orders`, `payments`, `refunds`, `instructor_wallets`, `wallet_transactions`, `payout_requests`.
5. **Khảo thí & Câu hỏi (8 bảng):** `question_banks`, `questions`, `question_options`, `exams`, `exam_questions`, `assignments`, `assignment_students`, `assignment_submissions`.
6. **Làm bài & Giám sát (4 bảng):** `exam_attempts`, `exam_answers`, `proctor_events`, `lesson_progress`.
7. **Động lực & Tương tác (5 bảng):** `student_notes`, `student_streaks`, `flashcard_decks`, `flashcards`, `flashcard_reviews`, `discussions`, `discussion_replies`.
8. **AI & Tri thức (9 bảng):** `knowledge_nodes`, `knowledge_edges`, `student_knowledge_progress`, `learning_documents`, `pdf_chunks`, `ai_generated_contents`, `ai_conversations`, `ai_messages`, `question_knowledge_links`.
9. **Hệ thống & Vận hành (5 bảng):** `support_tickets`, `ticket_messages`, `content_reports`, `notifications`, `audit_logs`.

---

## IV. CÁC QUY TẮC NGHIỆP VỤ ĐẶC THÙ (BUSINESS RULES & GUARDRAILS)

### 1. Bảo vệ Dữ liệu Trẻ em (Nghị định 13/2023/NĐ-CP)
- Học sinh tuổi từ 6 đến 15 bắt buộc phải có Parental Consent trước khi kích hoạt tài khoản.
- Phụ huynh có quyền thu hồi consent, yêu cầu xóa tài khoản hoặc xuất toàn bộ dữ liệu học tập.
- Dữ liệu giám sát thi (ảnh bằng chứng) chỉ được lưu trữ tối đa 15 ngày, log tạm thời 72 giờ, sau đó Background Job tự động xóa vĩnh viễn.

### 2. Guardrails Cho AI & Cơ chế RAG
- **Nguồn tài liệu:** Chỉ được trích xuất từ `learning_documents` đã duyệt, cắt thành các chunk 500 từ. Mỗi lần truy vấn chỉ lấy tối đa 5 chunk.
- **Phản hồi không bịa đặt:** Nếu độ khớp thấp hoặc không có trong sách, bắt buộc trả về câu: *"Không tìm thấy thông tin phù hợp trong tài liệu học tập đã được phê duyệt"* (MSG65).
- **Kiểm định Toán:** AI sinh lời giải toán -> gửi sang Python SymPy Sandbox (128MB RAM, timeout 2s) giải độc lập -> Khớp đáp án mới gắn mác Verified.
- **Kiểm định Sử:** Đối sánh văn bản với SGK đạt >= 80% và trích dẫn rõ tên sách, trang số thực tế.
- **Hạn mức AI:** Tài khoản Free: 10 câu/ngày; Premium: 100 câu/ngày. Bộ nhớ phiên chat tối đa 3.000 từ. Thời gian chờ tối đa 10s (timeout không trừ lượt).

### 3. Phòng thi Trực tuyến & Chống gian lận (Anti-Cheating)
- **Auto-save cục bộ:** Mọi thay đổi đáp án phải lưu ngay vào `IndexedDB` dưới 100ms.
- **Heartbeat:** Kiểm tra kết nối mỗi 10 giây. Khi mất mạng, bật Offline Mode, đồng hồ server vẫn đếm, học sinh vẫn tiếp tục làm bài. Khi có mạng lại, hệ thống đồng bộ 100% dữ liệu về server.
- **Giám sát vi phạm:** Bắt sự kiện rời toàn màn hình, chuyển tab, mở DevTools, chặn copy/paste.
  - Vi phạm lần 1: Ghi log.
  - Vi phạm lần 2: Cảnh báo đỏ trên màn hình + Gửi email/Zalo khẩn cho phụ huynh trong 5s.
  - Vi phạm lần 3: Khóa bài thi và tự động nộp bài ngay lập tức (Forced Submission).

### 4. Tài chính, Hoa hồng & Hoàn tiền
- Thanh toán SePay: Mã QR có hiệu lực 15 phút. Xử lý webhook phải đảm bảo Idempotent.
- Tỷ lệ chia doanh thu:
  - Giảng viên giới thiệu (`INSTRUCTOR_REFERRAL`): 90% GV - 10% Sàn.
  - Mua trên sàn (`LUMI_MARKETPLACE`): 80% GV - 20% Sàn.
- Điều kiện hoàn tiền: Trong vòng 7 ngày kể từ lúc mua và thời lượng học dưới 20%. Duyệt hoàn tiền sẽ thu hồi quyền truy cập khóa học trong vòng 3 giây.
- Tiền bán khóa học của GV bị giữ 7 ngày (hết hạn hoàn tiền) mới chuyển sang số dư khả dụng (`available_amount`).

---

## V. CÁC HỆ THỐNG PHỤ TRỢ (BACKGROUND JOBS & EXTERNAL APIS)

### Background Jobs:
1. `JOB-01: Unlock Expired Locked Accounts` (mỗi 15 phút): Mở khóa tài khoản bị khóa do đăng nhập sai sau 30 phút.
2. `JOB-02: Expire Pending Payments` (mỗi 5 phút): Hủy đơn hàng và QR quá 15 phút chưa thanh toán.
3. `JOB-03: Premium Expiration Reminder` (hằng ngày): Bắn thông báo nhắc gia hạn trước 3 ngày.
4. `JOB-04: Process AI Learning Materials` (event-based): Bóc tách text PDF, chia chunk 500 từ, sinh vector embedding 768 chiều.
5. `JOB-05: Retry Failed Notifications` (định kỳ): Thử lại email/thông báo lỗi.
6. `JOB-06: Clean Up Expired Temporary Data` (hằng ngày): Xóa OTP hết hạn, dữ liệu ảnh webcam giám sát quá 15 ngày.

### External APIs:
- `API-01`: AI Generation API (OpenAI / Gemini)
- `API-02`: Embedding API (Text-embedding 768 chiều)
- `API-03`: Cloud File Storage (Cloudflare R2 / AWS S3)
- `API-04`: SePay Payment API (Tạo giao dịch VietQR)
- `API-05`: SePay Payment Webhook (`/api/v1/payment/webhook/sepay`)
- `API-06`: SMTP Email Notification Service
- `API-07`: Zalo Notification API (Cảnh báo vi phạm thi và kết quả cho phụ huynh)
