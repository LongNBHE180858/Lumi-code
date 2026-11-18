# Cấu trúc Quản lý tài khoản

## Phạm vi hiện tại

Admin quản lý người dùng tại `/admin/user-management`: xem danh sách và chi tiết, tìm theo họ tên hoặc email, lọc vai trò, sắp xếp, phân trang, tạo tài khoản và kích hoạt/tạm khóa tài khoản thường, gồm cả Reviewer. Màn này không có chức năng sửa thông tin, đổi vai trò hoặc xóa tài khoản. Tài khoản có `ROLE_ADMIN` chỉ được xem, không có nút đổi trạng thái; API cũng chặn thao tác kích hoạt/tạm khóa admin.

## Backend

```text
lumi-api/src/main/java/com/lumi/
├─ web/rest/
│  └─ AdminResource.java                         [CHÍNH] API /api/admin/users và /api/admin/roles; yêu cầu ROLE_ADMIN
├─ service/
│  ├─ AdminService.java                          [CHÍNH] Tạo, tìm kiếm/lọc, xem, đổi trạng thái và kiểm tra vai trò
│  ├─ UserService.java                           Xóa cache người dùng sau khi lưu/đổi trạng thái
│  ├─ MailService.java                           Gửi email tạo tài khoản
│  └─ dto/
│     ├─ AdminUserDTO.java                       Thông tin tài khoản và danh sách authorities
│     └─ UserActivationRequest.java              Body PATCH: activated bắt buộc
├─ repository/
│  ├─ UserRepository.java                        Truy vấn phân trang theo tên/email và vai trò; tìm theo login
│  └─ AuthorityRepository.java                   Lấy quyền để gán lúc tạo tài khoản
└─ domain/
   ├─ User.java                                 Ánh xạ jhi_user và quan hệ authorities
   ├─ Authority.java                            Ánh xạ jhi_authority
   └─ enumeration/Role.java                     ROLE_ADMIN, ROLE_INSTRUCTOR, ROLE_STUDENT, ROLE_PARENT, ROLE_REVIEWER

lumi-api/src/main/resources/config/liquibase/
├─ changelog/00000000000000_initial_schema.xml  Tạo jhi_user, jhi_authority, jhi_user_authority; nạp dữ liệu nền
├─ data/
│  ├─ authority.csv                             Quyền khởi tạo
│  ├─ user.csv                                  Tài khoản khởi tạo
│  └─ user_authority.csv                        Quan hệ tài khoản và quyền khởi tạo
└─ master.xml                                   Include initial schema

lumi-api/src/test/java/com/lumi/service/
└─ AdminServiceUserTest.java                    Kiểm tra tạo tài khoản, quyền admin, tìm kiếm và kích hoạt
```

## Frontend

```text
lumi-webapp/src/main/webapp/app/
├─ routes.tsx                                   Cho ROLE_ADMIN và ROLE_REVIEWER vào nhánh /admin/*
└─ modules/administration/
   ├─ index.tsx                                Bọc trang trong AdminLayout; route /admin/user-management/*
   ├─ layout/admin-sidebar.tsx                 Liên kết "Người dùng" cho admin; Reviewer chỉ thấy duyệt khóa học
   └─ user-management/
      ├─ index.tsx                             Các route danh sách, /new và /:login
      ├─ user-management.tsx                   [CHÍNH] Bảng, tìm kiếm, lọc role, sắp xếp, phân trang, đổi trạng thái
      ├─ user-management-detail.tsx            Xem chi tiết tài khoản
      ├─ user-management-create.tsx            Form tạo tài khoản và chọn vai trò nghiệp vụ
      ├─ user-api.ts                           Kiểu tham số và lời gọi API
      ├─ user-management.reducer.ts            Thunk và state danh sách/chi tiết/tạo/kích hoạt
      ├─ user-role.ts                          Nhãn và màu theo vai trò; ưu tiên hiển thị admin
      ├─ user-management.scss                  Kiểu dáng màn quản lý tài khoản
      └─ user-management.reducer.spec.ts       Kiểm tra reducer

lumi-webapp/src/main/webapp/app/shared/model/
└─ user.model.ts                               Kiểu IUser dùng lại ở giao diện

lumi-webapp/src/main/webapp/app/shared/layout/menus/
├─ account.tsx                                Nhãn Reviewer và liên kết duyệt khóa học
└─ account.spec.tsx                           Kiểm tra nhãn và liên kết Reviewer

lumi-webapp/src/test/javascript/playwright/e2e/entity/
└─ user-management.pw.ts                       E2E danh sách, chi tiết, mở form và tạo với API mô phỏng
```

Luồng đang dùng: `user-management.tsx` / `user-management-create.tsx` / `user-management-detail.tsx` → `user-management.reducer.ts` → `user-api.ts` → `AdminResource` → `AdminService` → `UserRepository`/`AuthorityRepository` → `User`/`Authority` → `jhi_user`/`jhi_authority`/`jhi_user_authority`.

## API và quy tắc

| Thao tác | Endpoint | Hành vi |
| --- | --- | --- |
| Liệt kê | `GET /api/admin/users` | Phân trang, sắp xếp; nhận `role` và `search`; tổng số qua header `X-Total-Count` |
| Xem | `GET /api/admin/users/{login}` | Trả thông tin tài khoản theo login |
| Tạo | `POST /api/admin/users` | Tạo tài khoản đang kích hoạt, sinh mật khẩu/reset key, gửi email tạo tài khoản |
| Đổi trạng thái | `PATCH /api/admin/users/{login}/activation` | Nhận `{ "activated": true/false }`; từ chối tài khoản admin |
| Vai trò có thể tạo | `GET /api/admin/roles` | Trả `ROLE_INSTRUCTOR`, `ROLE_STUDENT`, `ROLE_PARENT`, `ROLE_REVIEWER`; không trả `ROLE_ADMIN` |

Tất cả endpoint trên yêu cầu `ROLE_ADMIN`. FE gửi `ROLE_USER` cùng một vai trò nghiệp vụ khi tạo; BE chỉ cho tối đa một vai trò nghiệp vụ và tự gán vai trò học sinh nếu chỉ gửi `ROLE_USER`. BE từ chối tạo thêm admin. Tìm kiếm ở BE không phân biệt hoa thường, gồm tên, họ, họ tên ghép và email; danh sách FE tìm kiếm sau 300 ms và hiển thị 10 tài khoản mỗi trang. Danh sách cho sắp xếp các cột ID, login, email, ngôn ngữ và ngày tạo; bộ lọc vai trò nằm ở tiêu đề cột vai trò. Bộ lọc Reviewer chỉ trả tài khoản `ROLE_REVIEWER` không mang `ROLE_ADMIN`; các bộ lọc Instructor, Parent và Student loại Reviewer theo thứ tự ưu tiên hiển thị vai trò. Reviewer có liên kết đến `/admin/course-review` trong menu tài khoản và chỉ thấy liên kết duyệt khóa học ở sidebar admin.

## Phần mã còn tồn tại nhưng không nối với màn hiện tại

`AdminResource` không có endpoint PUT hoặc DELETE cho tài khoản; `AdminService` cũng không còn hàm sửa/xóa tài khoản. FE không có trang sửa hoặc hộp thoại xóa. `user-api.ts` và reducer vẫn giữ lời gọi `GET /api/users` tới `PublicUserResource`, nhưng trang danh sách admin dùng `GET /api/admin/users`; API public chỉ trả thông tin người dùng công khai.

`User` là built-in entity của JHipster, được các quan hệ trong `lumi.jdl` tham chiếu; màn quản lý này không dùng entity JDL riêng hay migration tài khoản riêng. `ROLE_REVIEWER` đã có trong `authority.csv`, và ảnh dữ liệu `jhi_authority` do người dùng cung cấp cũng cho thấy quyền này đã tồn tại. Không cần thêm SQL hoặc changeset cho quyền Reviewer trong DB được hiển thị.
