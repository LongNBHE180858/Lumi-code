# Cấu trúc Audit Log Management

## Phạm vi hiện tại

Admin xem, tìm kiếm, phân trang và xuất Excel nhật ký tại `/admin/audit-logs`. Bộ lọc gồm tài khoản, hành động, loại đối tượng, ID đối tượng và khoảng thời gian. Mã ghi audit hiện tại chỉ tạo sự kiện `LOGIN_SUCCESS` sau khi đăng nhập thành công; cấu trúc bảng và bộ lọc có thể chứa các hành động khác nhưng chưa có luồng ghi mới cho chúng.

## Backend

```text
lumi-api/src/main/java/com/lumi/
├─ web/rest/
│  ├─ AuthController.java                       Lấy IP và User-Agent từ yêu cầu đăng nhập
│  └─ AdminAuditLogResource.java               [CHÍNH] API tìm kiếm và xuất Excel; yêu cầu ROLE_ADMIN
├─ service/
│  ├─ AuthService.java                          Phát sự kiện khi hoàn tất đăng nhập
│  ├─ LoginSucceededEvent.java                  Dữ liệu sự kiện: userId, IP, User-Agent
│  ├─ LoginAuditListener.java                   Nhận sự kiện sau commit, gửi việc ghi log sang executor
│  ├─ LoginAuditExecutorConfiguration.java      Executor riêng cho audit đăng nhập
│  ├─ AuditLogService.java                      [CHÍNH] Ghi log, lọc/phân trang và tạo tệp Excel
│  └─ dto/AdminAuditLogDTO.java                 Dữ liệu một bản ghi trả về trang admin
├─ repository/
│  └─ AuditLogRepository.java                  Truy vấn audit_logs bằng JPA Specification
└─ domain/
   └─ AuditLog.java                            [JHipster có sẵn] Ánh xạ bảng audit_logs

lumi-api/src/main/resources/config/liquibase/
├─ changelog/
│  ├─ 20261012230527_added_entity_AuditLog.xml              Tạo bảng audit_logs
│  └─ 20261012230527_added_entity_constraints_AuditLog.xml  Khóa ngoại actor_id đến User
└─ master.xml                                               Include hai changeset trên

lumi-api/
├─ lumi.jdl                                     Khai báo AuditLog và quan hệ với User
└─ .jhipster/AuditLog.json                      Metadata JHipster của AuditLog

lumi-api/src/test/java/com/lumi/
├─ service/
│  ├─ AuditLogServiceTest.java                  Kiểm tra ghi, tìm và xuất log
│  ├─ LoginAuditListenerTest.java               Kiểm tra việc ghi audit sau commit
│  └─ AuthServiceUnitTest.java                  Kiểm tra luồng đăng nhập
└─ web/rest/
   └─ LoginProfileAuditIT.java                 Kiểm tra đăng nhập, profile và lỗi ghi audit
```

## Frontend

```text
lumi-webapp/src/main/webapp/app/modules/administration/
├─ audit-logs/
│  ├─ admin-audit-logs.tsx                      [CHÍNH] Bộ lọc, bảng log, phân trang và popup xuất Excel
│  ├─ audit-logs-api.ts                         Kiểu dữ liệu và lời gọi API danh sách/xuất tệp
│  └─ admin-audit-logs.scss                     Kiểu dáng trang audit log
├─ index.tsx                                    Route /admin/audit-logs
└─ layout/admin-sidebar.tsx                    Liên kết "Nhật ký hoạt động" trong menu admin

lumi-webapp/
├─ lumi.jdl                                     Đồng bộ khai báo AuditLog với backend
└─ .jhipster/AuditLog.json                      Metadata JHipster của AuditLog
```

Luồng xem và xuất: `admin-audit-logs.tsx` → `audit-logs-api.ts` → `AdminAuditLogResource` → `AuditLogService` → `AuditLogRepository` → `audit_logs`.

Luồng ghi đăng nhập: `AuthController` → `AuthService` → `LoginSucceededEvent` → `LoginAuditListener` sau commit → executor riêng → `AuditLogService.recordSuccessfulLogin` trong giao dịch mới → `audit_logs`. Lỗi lên lịch hoặc ghi audit được ghi cảnh báo trong listener, không làm thất bại lần đăng nhập đã hoàn tất. Vì xử lý bất đồng bộ, bản ghi có thể xuất hiện sau phản hồi đăng nhập; nếu việc ghi audit lỗi, bản ghi đó có thể không được lưu.

## API và quy tắc

| Thao tác | Endpoint | Hành vi |
| --- | --- | --- |
| Xem và tìm kiếm | `GET /api/admin/audit-logs` | Lọc theo `actor`, `action`, `targetEntity`, `targetId`, `from`, `to`; sắp xếp thời điểm và ID giảm dần; FE hiển thị 10 dòng mỗi trang |
| Xuất Excel | `GET /api/admin/audit-logs/export` | Giữ bộ lọc tài khoản/hành động/đối tượng; popup chọn số dòng (`limit`) hoặc khoảng thời gian (`from`, `to`); tối đa 10.000 dòng |

Hai endpoint yêu cầu `ROLE_ADMIN`. Thời điểm trong tệp Excel ghi theo UTC. Chi tiết thiết bị của `LOGIN_SUCCESS` được trình bày thành tên trình duyệt và hệ điều hành; dữ liệu User-Agent cũ cũng được chuyển thành dạng dễ đọc khi trả về hoặc xuất tệp. IP là địa chỉ mà backend nhận từ kết nối HTTP (`request.getRemoteAddr()`).

## Schema và mã JHipster

Bảng `audit_logs`, entity, repository, metadata JHipster và hai changeset Liquibase đã có sẵn; chức năng này không cần bảng, cột hay migration mới. Changelog có dữ liệu mẫu trong ngữ cảnh `faker`, nên môi trường bật ngữ cảnh này có thể có các bản ghi mẫu ngoài `LOGIN_SUCCESS`.

Trong cây mã hiện tại không có màn CRUD JHipster `entities/audit-log` hoặc resource CRUD riêng cho AuditLog. Trang admin sử dụng API tùy biến `/api/admin/audit-logs`; không phụ thuộc vào màn CRUD sinh sẵn.
