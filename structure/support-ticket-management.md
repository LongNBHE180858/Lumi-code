# Cấu trúc Support Ticket Management

## Backend

```text
lumi-api/src/main/java/com/lumi/
├─ web/rest/
│  ├─ SupportTicketResource.java                 [CHÍNH] API tạo, xem, nhắn tin, mở lại ticket
│  └─ AdminSupportTicketResource.java            [CHÍNH] API admin xem, lọc và đổi trạng thái ticket
├─ service/
│  ├─ SupportTicketManagementService.java        [CHÍNH] Quyền truy cập và nghiệp vụ ticket
│  └─ dto/
│     ├─ SupportTicketCreateRequest.java         Dữ liệu tạo ticket
│     ├─ TicketMessageCreateRequest.java         Nội dung tin nhắn gửi lên
│     ├─ SupportTicketStatusRequest.java         Trạng thái admin cập nhật
│     ├─ SupportTicketViewDTO.java               Dữ liệu ticket trả về FE
│     └─ TicketMessageViewDTO.java               Dữ liệu tin nhắn trả về FE
├─ domain/
│  ├─ SupportTicket.java                         [JH có sẵn] Ticket, người tạo, ngày tạo, trạng thái
│  ├─ TicketMessage.java                         [JH có sẵn] Tin nhắn, người gửi, ticket liên quan
│  └─ enumeration/
│     └─ SupportTicketStatus.java                OPEN, IN_PROGRESS, RESOLVED
└─ repository/
   ├─ SupportTicketRepository.java               Lấy ticket của người tạo; lọc/tìm kiếm cho admin
   └─ TicketMessageRepository.java               Lấy tin nhắn theo ticket và phân trang

lumi-api/src/test/java/com/lumi/
├─ service/
│  └─ SupportTicketManagementServiceTest.java    Kiểm tra quyền và quy tắc nghiệp vụ
└─ web/rest/
   └─ SupportTicketResourceIT.java               Kiểm tra API và chuyển trạng thái
```

## Frontend

```text
lumi-webapp/src/main/webapp/app/
├─ modules/
│  ├─ support-tickets/
│  │  ├─ ticket-list.tsx                         [CHÍNH] Danh sách, tạo ticket; admin lọc/tìm kiếm
│  │  ├─ ticket-detail.tsx                       [CHÍNH] Hội thoại, nhắn tin, mở lại, đổi trạng thái
│  │  ├─ ticket-api.ts                           Gọi API ticket và khai báo kiểu dữ liệu FE
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

```text
lumi-api/src/main/java/com/lumi/service/
├─ SupportTicketService.java                    [JH cũ] CRUD sinh sẵn, không dùng cho API ticket mới
├─ TicketMessageService.java                    [JH cũ] CRUD sinh sẵn, không dùng cho API ticket mới
├─ SupportTicketQueryService.java               [JH cũ] Bộ lọc CRUD sinh sẵn
└─ TicketMessageQueryService.java               [JH cũ] Bộ lọc CRUD sinh sẵn

lumi-webapp/src/main/webapp/app/entities/
├─ support-ticket/                             [ĐÃ GỠ] Màn hình CRUD JHipster cũ
└─ ticket-message/                             [ĐÃ GỠ] Màn hình CRUD JHipster cũ
```

Luồng hiện dùng: `ticket-list.tsx` / `ticket-detail.tsx` → `ticket-api.ts` → hai `Resource` → `SupportTicketManagementService` → `Repository` → `Entity`.
