# Cấu trúc Premium Package Management

## Phạm vi hiện tại

Admin có thể xem danh sách, tạo, sửa và bật/ngừng bán gói Premium. Chưa triển khai luồng mua, gia hạn, chọn thành viên hoặc lớp, và cấp quyền sử dụng. Không có thao tác xóa gói trong API quản trị.

## Backend

```text
lumi-api/src/main/java/com/lumi/
├─ web/rest/
│  └─ AdminPremiumPackageResource.java           [CHÍNH] API quản trị, chỉ ROLE_ADMIN
├─ service/
│  ├─ AdminPremiumPackageService.java            [CHÍNH] Kiểm tra loại gói, quản lý trạng thái và phiên bản
│  └─ dto/
│     ├─ PremiumPackageRequest.java              Dữ liệu tạo/sửa gói
│     ├─ PremiumPackageAvailabilityRequest.java  Dữ liệu bật/ngừng bán
│     └─ PremiumPackageDTO.java                  Dữ liệu trả về cho FE
├─ domain/
│  ├─ SystemPlan.java                            [JHipster có sẵn, đã mở rộng] Ánh xạ bảng system_plans
│  └─ enumeration/
│     ├─ PremiumPackageType.java                 Bốn loại gói; ánh xạ plan_type và target_role
│     ├─ PremiumPackageStatus.java               AVAILABLE, UNAVAILABLE, SUPERSEDED
│     └─ PremiumAiLimitPeriod.java               DAY, MONTH, PACKAGE_TERM
└─ repository/
   └─ SystemPlanRepository.java                  Danh sách gói hiện hành, đếm phiên bản, khóa khi sửa

lumi-api/src/main/resources/config/liquibase/
├─ changelog/
│  └─ 20261012230451_added_entity_SystemPlan.xml  Tạo bảng system_plans, gồm cột package_key
└─ master.xml                                     Đã include changeset tạo bảng trên

lumi-api/
├─ lumi.jdl                                       Mô tả SystemPlan và packageKey cho JHipster
└─ .jhipster/SystemPlan.json                      Metadata sinh mã của SystemPlan

lumi-api/src/test/java/com/lumi/
├─ domain/SystemPlanTestFixtures.java            Dữ liệu gói dùng trong test
├─ service/AdminPremiumPackageServiceTest.java   Unit test nghiệp vụ Premium
└─ web/rest/AdminPremiumPackageResourceIT.java   Integration test API và phân quyền
```

## Frontend

```text
lumi-webapp/src/main/webapp/app/
├─ modules/administration/
│  ├─ premium-packages/
│  │  ├─ admin-premium-packages.tsx               [CHÍNH] Danh sách, tạo/sửa, bật/ngừng bán
│  │  ├─ premium-package-form.tsx                 Biểu mẫu loại gói, giá, hạn mức và kỳ AI
│  │  ├─ premium-packages-api.ts                  Kiểu dữ liệu FE và lời gọi API admin
│  │  ├─ admin-premium-packages.scss              Kiểu dáng trang quản trị
│  │  └─ admin-premium-packages.spec.tsx          Unit test component
│  ├─ index.tsx                                   Route /admin/premium-packages
│  └─ layout/admin-sidebar.tsx                    Liên kết trong menu admin

lumi-webapp/src/test/javascript/playwright/e2e/
└─ premium-packages.pw.ts                        Kiểm thử giao diện với API mô phỏng

lumi-webapp/
├─ lumi.jdl                                       Đồng bộ mô tả SystemPlan với BE
└─ .jhipster/SystemPlan.json                      Đồng bộ metadata SystemPlan với BE
```

Luồng đang dùng: `admin-premium-packages.tsx` → `premium-packages-api.ts` → `AdminPremiumPackageResource` → `AdminPremiumPackageService` → `SystemPlanRepository` → `SystemPlan` → bảng `system_plans`.

## API và quy tắc

| Thao tác | Endpoint | Hành vi |
| --- | --- | --- |
| Liệt kê | `GET /api/admin/premium-packages` | Phân trang; không trả về phiên bản `SUPERSEDED` |
| Xem | `GET /api/admin/premium-packages/{id}` | Chỉ xem phiên bản hiện hành |
| Tạo | `POST /api/admin/premium-packages` | Sinh `package_key`; gói mới ở trạng thái `UNAVAILABLE` |
| Sửa | `PUT /api/admin/premium-packages/{id}` | Đánh dấu bản trước `SUPERSEDED`, tạo bản mới cùng `package_key` |
| Bật/ngừng bán | `PATCH /api/admin/premium-packages/{id}/availability` | Đổi giữa `AVAILABLE` và `UNAVAILABLE` |

Tất cả endpoint yêu cầu `ROLE_ADMIN`. FE hiển thị trạng thái bằng tiếng Việt: đang bán và ngừng bán. Phiên bản cũ có mã `SUPERSEDED` nhưng không xuất hiện trong danh sách quản trị.

- `PARENT_PERSONAL`: `plan_type=PERSONAL`, `target_role=PARENT`.
- `PARENT_FAMILY`: `plan_type=FAMILY`, `target_role=PARENT`; tối đa 4 người tính cả phụ huynh mua gói.
- `INSTRUCTOR_PERSONAL`: `plan_type=PERSONAL`, `target_role=INSTRUCTOR`.
- `INSTRUCTOR_CLASS`: `plan_type=CLASS`, `target_role=INSTRUCTOR`; quyền lợi dự kiến áp dụng cho giảng viên và một lớp được chọn.
- Giá nhập bằng số nguyên VNĐ. `ai_generation_limit` là tổng lượt AI trong mỗi kỳ, không phải lượt cộng thêm. `ai_limit_period` nhận `DAY`, `MONTH` hoặc `PACKAGE_TERM`.
- Dung lượng và lượt AI được định nghĩa theo từng người hưởng quyền lợi; số lớp tối đa chỉ nhập cho gói giảng viên. Do `max_classes` trong DB không cho rỗng, gói phụ huynh lưu `0` và API trả `null` cho trường này.
- `features` hiện được service ghi `["AI_REQUESTS"]` khi lượt AI lớn hơn 0, hoặc `[]` khi bằng 0; form admin không nhập riêng trường này.
- Khi sửa giá hoặc hạn mức, service giữ bản cũ và tạo bản mới. Việc áp quyền theo phiên bản đã mua thuộc giai đoạn triển khai luồng mua sau này. Học sinh gia nhập lớp sau thời điểm mua sẽ không thuộc nhóm hưởng quyền lợi của lần mua đó; quy tắc này cũng chưa có mã cấp quyền.

## Schema và mã JHipster còn lại

Chỉ bổ sung cột `package_key` kiểu `varchar(36)` ngay trong changeset **tạo bảng gốc** `20261012230451_added_entity_SystemPlan.xml`; không có migration Premium riêng và không thêm include mới vào `master.xml`. Cách này dựa trên quyết định mọi database cần chạy Premium sẽ được tạo lại từ đầu. Hai `lumi.jdl` và hai `.jhipster/SystemPlan.json` mô tả cùng trường nhưng không tự thay đổi DB khi ứng dụng chạy.

API CRUD JHipster `SystemPlanResource` và service CRUD `SystemPlanService` không còn. `SystemPlanCriteria.java` vẫn tồn tại nhưng không được API Premium sử dụng. `SystemPlanDTO.java` vẫn được `CourseOrderDTO` tham chiếu nên không thể xóa riêng mà không rà quan hệ đó.

Ở FE, màn CRUD `entities/system-plan/` không còn, nhưng menu entity vẫn có liên kết `/system-plan` và bài E2E `entity/system-plan.pw.ts` vẫn trỏ đến route/API cũ. Màn quản trị Premium dùng route `/admin/premium-packages` và bài E2E riêng của nó.
