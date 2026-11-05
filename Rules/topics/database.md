# Database và Liquibase
- Lấy schema đang áp dụng từ `lumi-api/src/main/resources/config/liquibase/master.xml`; JDL mô tả mô hình nhưng không tự chạy migration.
- Giữ nguyên changeset đã có thể chạy trên DB của người khác; sửa nội dung cũ gây lỗi checksum, nên thêm changeset mới.
- Mô hình hiện hành chỉ có `subjects` và `grades` cho hai danh mục Môn học và Khối lớp; cả hai không có cột `active`. Kiểm tra `master.xml` để xác định changeset được áp dụng khi tạo DB mới.
- Giữ lịch sử `instructor_reviewed_by` trong `20261022120000_instructor_review_metadata.xml` và bước xóa trong `20261022180000_remove_instructor_reviewer.xml`.
- Giữ `instructor_rejection_reason`/`instructor_reviewed_at` ở `user_profile`.
- Kiểm tra đúng DB dev `lumig46` trên cổng 5436 trước khi hướng dẫn SQL; cấu hình nằm trong `application-dev.yml` và `postgresql.yml`.
- Đừng nhầm nhãn `.tag(...)` của metrics, `@Tag` của Swagger hoặc tag của Grafana với bảng Tag khóa học đã bỏ.
- Chạy integration test có `DatabaseTestcontainer` khi Docker sẵn sàng; không coi unit test là bằng chứng migration đã chạy trên DB thật.
