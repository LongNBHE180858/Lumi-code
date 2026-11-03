# Cấu trúc Premium Package Management

## Backend

```text
lumi-api/src/main/java/com/lumi/
├─ web/rest/
│  └─ AdminPremiumPackageResource.java           [CHÍNH] API admin liệt kê, xem, tạo, sửa và bật/tắt bán gói
├─ service/
│  ├─ AdminPremiumPackageService.java            [CHÍNH] Kiểm tra loại gói, tạo phiên bản mới, giữ phiên bản cũ
│  └─ dto/
│     ├─ PremiumPackageRequest.java              Dữ liệu tạo/sửa; giá VNĐ nguyên, thời hạn và hạn mức
│     ├─ PremiumPackageAvailabilityRequest.java  Trạng thái đang bán/ngừng bán
│     └─ PremiumPackageDTO.java                  Dữ liệu trả về, gồm số người hưởng và cờ cần phân loại
├─ domain/
│  ├─ SystemPlan.java                            [JH có sẵn, đã mở rộng] Gói, loại, giá, hạn mức, phiên bản
│  └─ enumeration/
│     └─ PremiumPackageType.java                 Bốn loại gói Parent/Instructor
└─ repository/
   └─ SystemPlanRepository.java                  Lấy phiên bản hiện hành và khóa bản ghi khi cập nhật

lumi-api/src/main/resources/config/liquibase/
├─ changelog/
│  └─ 20261103000000_premium_package_management.xml  Bổ sung loại gói, khóa gói, phiên bản, trạng thái bán;
│                                                      cho phép max_classes rỗng với gói Parent
└─ master.xml                                     Khai báo migration Premium

lumi-api/src/test/java/com/lumi/
├─ domain/
│  └─ SystemPlanTestFixtures.java                Dữ liệu gói dùng trong test
├─ service/
│  └─ AdminPremiumPackageServiceTest.java        Unit test quy tắc loại gói và phiên bản
└─ web/rest/
   └─ AdminPremiumPackageResourceIT.java        Integration test API, phân quyền và PostgreSQL tạm
```

## Frontend

```text
lumi-webapp/src/main/webapp/app/
├─ modules/administration/
│  ├─ premium-packages/
│  │  ├─ admin-premium-packages.tsx               [CHÍNH] Danh sách phân trang, tạo/sửa, bật/tắt bán
│  │  ├─ premium-package-form.tsx                 Biểu mẫu loại gói, giá VNĐ và hạn mức
│  │  ├─ premium-packages-api.ts                  Kiểu dữ liệu FE và lời gọi API admin
│  │  ├─ admin-premium-packages.scss              Kiểu dáng trang quản trị
│  │  └─ admin-premium-packages.spec.tsx          Unit test component
│  ├─ index.tsx                                   Route /admin/premium-packages
│  └─ layout/
│     └─ admin-sidebar.tsx                       Liên kết Gói Premium trong menu admin
└─ entities/
   ├─ course-order/                             Form lấy gói qua API mới; danh sách bỏ liên kết CRUD cũ
   └─ teacher-quota/                             Form lấy gói qua API mới; danh sách bỏ liên kết CRUD cũ

lumi-webapp/src/test/javascript/playwright/e2e/
└─ premium-packages.pw.ts                        Kiểm thử giao diện Chromium với API mô phỏng
```

## API và quy tắc hiện có

| Thao tác | Endpoint | Ghi chú |
| --- | --- | --- |
| Liệt kê | `GET /api/admin/premium-packages` | Chỉ phiên bản hiện hành, có phân trang |
| Xem | `GET /api/admin/premium-packages/{id}` | Chỉ phiên bản hiện hành |
| Tạo | `POST /api/admin/premium-packages` | Tạo bản đầu tiên ở trạng thái ngừng bán |
| Sửa | `PUT /api/admin/premium-packages/{id}` | Tạo phiên bản mới, giữ dữ liệu bản cũ |
| Bật/tắt bán | `PATCH /api/admin/premium-packages/{id}/availability` | Gói cũ phải được phân loại trước khi bật bán |

Tất cả endpoint trên yêu cầu `ROLE_ADMIN`. Bốn loại gói là `PARENT_PERSONAL`, `PARENT_FAMILY`, `INSTRUCTOR_PERSONAL` và `INSTRUCTOR_CLASS`. Gói gia đình hiển thị tối đa 4 người hưởng quyền lợi, tính cả phụ huynh. `maxClasses` chỉ nhập cho gói Instructor; giá nhập bằng số nguyên VNĐ. Không có thao tác xóa gói trong API quản trị.

## Ghi chú về phạm vi và mã cũ

```text
lumi-api/src/main/java/com/lumi/
├─ web/rest/SystemPlanResource.java             [ĐÃ GỠ] API CRUD JHipster cũ
└─ service/SystemPlanService.java                [ĐÃ GỠ] Service CRUD JHipster cũ

lumi-webapp/src/main/webapp/app/entities/
└─ system-plan/                                 [ĐÃ GỠ] Màn hình CRUD JHipster cũ
```

Luồng hiện dùng: `admin-premium-packages.tsx` → `premium-packages-api.ts` → `AdminPremiumPackageResource` → `AdminPremiumPackageService` → `SystemPlanRepository` → `SystemPlan`. Hai file `lumi.jdl` đã đồng bộ mô hình; thay đổi schema thực tế do migration Premium trong `master.xml` thực hiện.

Phần mua gói, gia hạn, chọn thành viên/lớp và cấp quyền sử dụng chưa được triển khai. Các phiên bản gói cũ được giữ để phục vụ dữ liệu đã tham chiếu; quy tắc áp quyền khi mua hoặc gia hạn sẽ thuộc giai đoạn sau.
