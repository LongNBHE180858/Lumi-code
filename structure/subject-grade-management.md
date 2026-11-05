# Cấu trúc Môn học & Khối lớp

## Phạm vi hiện tại

Admin quản lý Môn học (`subjects`) và Khối lớp (`grades`) tại `/admin/catalog`. Trang mở ở tab Môn học, có danh sách, tìm kiếm, phân trang phía giao diện, tạo và sửa. Không có tab danh mục khóa học, trường danh mục cha, trạng thái kích hoạt/tạm ngưng hoặc thao tác xóa trong màn quản trị này.

## Backend

```text
lumi-api/src/main/java/com/lumi/
├─ web/rest/
│  └─ AdminResource.java                         [CHÍNH] GET, POST, PUT /api/admin/catalog/{kind}; yêu cầu ROLE_ADMIN
├─ service/
│  ├─ AdminService.java                          [CHÍNH] Liệt kê, kiểm tra và lưu Môn học/Khối lớp
│  └─ dto/CatalogItemDTO.java                    Dữ liệu chung: id, name, description, level
├─ repository/
│  ├─ SubjectRepository.java                    Tìm Môn học theo tên không phân biệt hoa thường
│  └─ GradeRepository.java                      Tìm Khối lớp theo level
└─ domain/
   ├─ Subject.java                              Ánh xạ bảng subjects
   └─ Grade.java                                Ánh xạ bảng grades

lumi-api/src/main/resources/config/liquibase/
├─ changelog/
│  ├─ 20261012230439_added_entity_Subject.xml    Tạo subjects; seed Toán và Lịch sử khi không dùng faker
│  └─ 20261012230440_added_entity_Grade.xml      Tạo grades; seed lớp 1 đến lớp 9 khi không dùng faker
└─ master.xml                                   Include hai changeset trên

lumi-api/
├─ lumi.jdl                                     Mô tả Subject và Grade
├─ .jhipster/Subject.json                       Metadata của Subject
└─ .jhipster/Grade.json                         Metadata của Grade

lumi-api/src/test/java/com/lumi/service/
└─ AdminCatalogServiceTest.java                 Unit test lưu, kiểm tra trùng và giới hạn level
```

## Frontend

```text
lumi-webapp/src/main/webapp/app/modules/administration/
├─ catalog/
│  ├─ admin-catalog.tsx                         [CHÍNH] Hai tab, tải dữ liệu, tìm kiếm, phân trang, tạo/sửa
│  ├─ catalog-api.ts                            Kiểu dữ liệu FE và lời gọi GET, POST, PUT
│  ├─ admin-catalog.scss                        Kiểu dáng trang quản trị
│  ├─ admin-catalog.spec.tsx                    Kiểm tra tab mặc định và hai loại dữ liệu
│  └─ components/
│     ├─ catalog-table.tsx                      Bảng Môn học hoặc Khối lớp
│     └─ catalog-form-dialog.tsx                Biểu mẫu tạo/sửa
├─ index.tsx                                   Route /admin/catalog
└─ layout/admin-sidebar.tsx                    Liên kết "Môn học & Khối lớp"

lumi-webapp/
├─ lumi.jdl                                     Đồng bộ mô tả Subject và Grade với BE
├─ .jhipster/Subject.json                       Metadata của Subject
└─ .jhipster/Grade.json                         Metadata của Grade
```

Luồng đang dùng: `admin-catalog.tsx` → `catalog-api.ts` → `AdminResource` → `AdminService` → `SubjectRepository`/`GradeRepository` → `Subject`/`Grade` → `subjects`/`grades`.

## API và quy tắc

| Thao tác | Endpoint | Hành vi |
| --- | --- | --- |
| Liệt kê | `GET /api/admin/catalog/subjects` hoặc `/grades` | Trả toàn bộ mục; BE sắp theo tên môn học hoặc level khối lớp |
| Tạo | `POST /api/admin/catalog/subjects` hoặc `/grades` | Tạo mục; trả `201` và URL mục mới |
| Sửa | `PUT /api/admin/catalog/subjects/{id}` hoặc `/grades/{id}` | Sửa mục hiện có; trả `404` nếu không có ID |

Tất cả endpoint trên yêu cầu `ROLE_ADMIN`. Tên được cắt khoảng trắng đầu/cuối và không được rỗng. Môn học giới hạn tên 100 ký tự, mô tả 5000 ký tự và kiểm tra trùng tên không phân biệt hoa thường. Khối lớp giới hạn tên 50 ký tự, `level` từ 1 đến 12 và không cho trùng `level`. FE gửi `name`, `description`, `level`; API không trả trường trạng thái hoặc danh mục cha.

## Schema và mã JHipster còn lại

Schema hiện hành tạo hai bảng `subjects` và `grades`, không có cột `active`. `master.xml` không include changeset của danh mục khóa học hoặc bổ sung trạng thái. Không có migration mới cho lần dọn chức năng này; DB cũ cần được người dùng tạo lại theo quy trình của dự án.

API và màn CRUD JHipster riêng cho Subject/Grade vẫn tồn tại ở `/api/subjects`, `/api/grades`, `/subject` và `/grade`. Chúng không được trang admin `/admin/catalog` sử dụng. Các endpoint DELETE JHipster hiện gọi service nhưng service không xóa bản ghi; màn admin không cung cấp thao tác xóa.
