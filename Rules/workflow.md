# Quy trình làm việc trong workspace
- Khi người dùng chốt hoặc thay đổi quy tắc làm việc, cập nhật file phù hợp trong `Rules/`; sửa mục lục nếu thêm chủ đề mới.
- Kiểm tra `git status` ở repo gốc và repo con liên quan trước khi sửa; cùng một file xuất hiện ở cả hai lịch sử Git.
- Đối chiếu route, kiểu request/response, file gọi API, service và test trước khi đổi một trong ba nhóm admin; FE và BE phải khớp.
- Hỏi lại khi quy tắc vai trò, trạng thái đơn, số lượng admin hoặc dữ liệu bị xóa chưa được chốt; các quyết định này đổi nghiệp vụ và DB.
- Chỉ xóa/tạo lại DB và seed khi người dùng yêu cầu rõ. Sau khi reset DB, để người dùng tự khởi động BE/FE, trừ khi họ yêu cầu chạy ứng dụng.
- Kiểm tra đúng phạm vi bằng test liên quan, build/lint và `git diff --check`; báo riêng bước không chạy được do Docker hoặc dịch vụ ngoài.
- Khi giải thích thay đổi admin, đi theo luồng UI → file API FE → `AdminResource` → `AdminService` → repository/entity → Liquibase để dễ kiểm chứng.
- Trước khi báo xong, nêu file đã đổi, lý do, kết quả kiểm tra và migration nào còn chờ chạy trên DB thật.
