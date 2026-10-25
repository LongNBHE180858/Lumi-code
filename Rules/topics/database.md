# Database và Liquibase
- Lấy schema đang áp dụng từ `lumi-api/src/main/resources/config/liquibase/master.xml`; JDL mô tả mô hình nhưng không tự chạy migration.
- Giữ nguyên changeset đã có thể chạy trên DB của người khác; sửa nội dung cũ gây lỗi checksum, nên thêm changeset mới.
- Giữ lịch sử Tag trong `20261022160000_catalog_management.xml` và bước xóa trong `20261022170000_remove_course_tags.xml`; DB cuối không còn `tag`/`course_tag`.
- Giữ lịch sử `instructor_reviewed_by` trong `20261022120000_instructor_review_metadata.xml` và bước xóa trong `20261022180000_remove_instructor_reviewer.xml`.
- Giữ `active` ở `category`/`subject`/`grade` và `instructor_rejection_reason`/`instructor_reviewed_at` ở `user_profile`.
- Kiểm tra đúng DB dev `lumig46` trên cổng 5436 trước khi hướng dẫn SQL; cấu hình nằm trong `application-dev.yml` và `postgresql.yml`.
- Đừng nhầm nhãn `.tag(...)` của metrics, `@Tag` của Swagger hoặc tag của Grafana với bảng Tag khóa học đã bỏ.
- Chạy integration test có `DatabaseTestcontainer` khi Docker sẵn sàng; không coi unit test là bằng chứng migration đã chạy trên DB thật.
