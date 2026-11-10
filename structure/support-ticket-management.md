# Cấu trúc Support Ticket Management

## Backend

```text
lumi-api/src/main/java/com/lumi/
├─ web/rest/
│  ├─ SupportTicketResource.java                 [CHÍNH] API ticket, tin nhắn multipart và quyền xem ảnh
│  └─ AdminSupportTicketResource.java            [CHÍNH] API admin xem, lọc và đổi trạng thái ticket
├─ service/
│  ├─ SupportTicketManagementService.java        [CHÍNH] Quyền truy cập, tin nhắn, ảnh, trạng thái và updated_at
│  └─ dto/
│     ├─ SupportTicketCreateRequest.java         Dữ liệu tạo ticket
│     ├─ TicketMessageCreateRequest.java         Nội dung tin nhắn gửi lên
│     ├─ SupportTicketStatusRequest.java         Trạng thái admin cập nhật
│     ├─ SupportTicketViewDTO.java               Dữ liệu ticket trả về FE
│     ├─ TicketMessageViewDTO.java               Nội dung chữ và danh sách ảnh trả về FE
│     ├─ TicketImageDTO.java                     Tên và vị trí ảnh trong tin nhắn
│     └─ TicketMessageContent.java               Định dạng JSON lưu chữ và tham chiếu ảnh
├─ service/storage/
│  └─ R2CourseObjectStorage.java                 Lưu ảnh ticket trong R2, cấp URL xem có thời hạn
├─ domain/
│  ├─ SupportTicket.java                         [JH mở rộng] Ticket, người tạo, trạng thái, created_at và updated_at
│  ├─ TicketMessage.java                         [JH có sẵn] Tin nhắn, người gửi, ticket liên quan
│  └─ enumeration/
│     └─ SupportTicketStatus.java                OPEN, IN_PROGRESS, RESOLVED
└─ repository/
   ├─ SupportTicketRepository.java               Lấy ticket của người tạo; lọc/tìm kiếm cho admin
   └─ TicketMessageRepository.java               Lấy tin nhắn theo ticket và phân trang

lumi-api/src/test/java/com/lumi/
├─ service/
│  └─ SupportTicketManagementServiceTest.java    Kiểm tra quyền, ảnh, trạng thái và thời điểm hoạt động
└─ web/rest/
   └─ SupportTicketResourceIT.java               Kiểm tra API, ảnh, chuyển trạng thái và dữ liệu thời gian
```

## Frontend

```text
lumi-webapp/src/main/webapp/app/
├─ modules/
│  ├─ support-tickets/
│  │  ├─ ticket-list.tsx                         [CHÍNH] Danh sách, tạo ticket; admin lọc/tìm kiếm
│  │  ├─ ticket-detail.tsx                       [CHÍNH] Hội thoại, nhắn tin, mở lại, đổi trạng thái
│  │  ├─ ticket-api.ts                           Gọi API ticket và khai báo kiểu dữ liệu FE
│  │  ├─ ticket-image-picker.tsx                  Chọn, xem trước và giới hạn ảnh đính kèm
│  │  ├─ ticket-format.ts                        Nhãn trạng thái/loại, ngày giờ, thông báo lỗi
│  │  ├─ tickets.scss                            Kiểu dáng các trang ticket
│  │  ├─ ticket-list.spec.tsx                    Kiểm tra component danh sách
│  │  └─ ticket-detail.spec.tsx                  Kiểm tra component chi tiết
│  └─ administration/
│     ├─ index.tsx                               Route /admin/support-tickets và trang chi tiết
│     └─ layout/
│        └─ admin-sidebar.tsx                    Liên kết ticket trong menu admin
├─ shared/layout/menus/
│  └─ account.tsx                                Liên kết ticket trong menu tài khoản
└─ routes.tsx                                    Route /support-tickets và trang chi tiết

lumi-webapp/src/test/javascript/playwright/e2e/
└─ support-tickets.pw.ts                         Kiểm thử FE bằng Chromium với API mô phỏng
```

## Ghi chú về mã cũ

Các đường dẫn dưới đây là mã JHipster cũ đã gỡ; chúng không còn là file hoặc thư mục trong cây hiện tại.

```text
lumi-api/src/main/java/com/lumi/service/
├─ SupportTicketService.java                    [ĐÃ GỠ] CRUD JHipster cũ
├─ TicketMessageService.java                    [ĐÃ GỠ] CRUD JHipster cũ
├─ SupportTicketQueryService.java               [ĐÃ GỠ] Bộ lọc CRUD JHipster cũ
└─ TicketMessageQueryService.java               [ĐÃ GỠ] Bộ lọc CRUD JHipster cũ

lumi-webapp/src/main/webapp/app/entities/
├─ support-ticket/                             [ĐÃ GỠ] Màn hình CRUD JHipster cũ
└─ ticket-message/                             [ĐÃ GỠ] Màn hình CRUD JHipster cũ
```

Luồng hiện dùng: `ticket-list.tsx` / `ticket-detail.tsx` → `ticket-api.ts` → hai `Resource` → `SupportTicketManagementService` → `Repository` → `Entity`.

`updated_at` bằng `created_at` khi tạo ticket; sau đó ghi nhận lần có tin nhắn mới hoặc lần trạng thái thực sự thay đổi gần nhất, kể cả tin nhắn mở lại ticket từ `RESOLVED`.

Một tin nhắn có thể gồm chữ và tối đa 3 ảnh PNG/JPEG, 5 MB mỗi ảnh. Ảnh lưu trong R2; `ticket_messages.content` lưu JSON tham chiếu ảnh. Tin nhắn chữ cũ vẫn được đọc như văn bản thường. Không thêm bảng hoặc cột DB.
