# Cấu trúc Withdrawal & Refund Management

## Phạm vi hiện tại

Giảng viên xem số dư ví, lịch sử giao dịch, gửi và xem yêu cầu rút tiền tại `/instructor/wallet`. Phụ huynh đã thanh toán xem và gửi yêu cầu hoàn tiền tại `/refund-requests`. Admin xem, duyệt hoặc từ chối yêu cầu và ghi mã giao dịch sau khi chuyển khoản thủ công tại `/admin/finance`.

## Backend

```text
lumi-api/src/main/java/com/lumi/
├─ web/rest/
│  └─ FinanceResource.java                       [CHÍNH] API /api/finance cho giảng viên, phụ huynh và admin
├─ service/
│  ├─ finance/
│  │  ├─ FinanceService.java                     [CHÍNH] Ghi nhận ví, giữ/giải phóng tiền, rút tiền và hoàn tiền
│  │  └─ FinanceDTOs.java                       Request/response riêng cho API tài chính
│  └─ payment/impl/
│     └─ SePayWebhookProcessorImpl.java         Gọi ghi nhận doanh thu ví khi thanh toán thành công
├─ repository/
│  ├─ InstructorWalletRepository.java            Tìm và khóa ví khi cập nhật số dư
│  ├─ WalletTransactionRepository.java           Lịch sử ví, kiểm tra giao dịch đã ghi và doanh thu đơn
│  ├─ PayoutRequestRepository.java               Danh sách, khóa đơn rút và kiểm tra giới hạn mỗi ngày
│  ├─ RefundRepository.java                      Danh sách, khóa đơn hoàn và kiểm tra yêu cầu trùng
│  ├─ PaymentRepository.java                     Tìm khoản thanh toán thành công, doanh thu chưa ghi ví
│  └─ CourseEnrollmentRepository.java            Tìm quyền học để thu hồi sau khi duyệt hoàn
├─ domain/
│  ├─ InstructorWallet.java                      [JHipster có sẵn] Tiền chờ và tiền có thể rút
│  ├─ WalletTransaction.java                     [JHipster có sẵn] Bút toán ví
│  ├─ PayoutRequest.java                         [JHipster có sẵn] Yêu cầu rút và thông tin chuyển khoản
│  └─ Refund.java                                [JHipster có sẵn] Yêu cầu hoàn, kết quả xử lý và quyền học
└─ config/
   └─ SecurityConfiguration.java                 Chặn API CRUD JHipster tài chính cũ

lumi-api/src/main/resources/config/liquibase/
├─ changelog/
│  ├─ 20261012230456_added_entity_Refund.xml           Tạo bảng refunds
│  ├─ 20261012230457_added_entity_InstructorWallet.xml Tạo bảng instructor_wallets
│  ├─ 20261012230458_added_entity_WalletTransaction.xml Tạo bảng wallet_transactions
│  └─ 20261012230459_added_entity_PayoutRequest.xml    Tạo bảng payout_requests
└─ master.xml                                           Include các changeset có sẵn

lumi-api/lumi.jdl                                      Mô tả bốn entity tài chính có sẵn

lumi-api/src/test/java/com/lumi/
├─ service/finance/FinanceServiceTest.java       Kiểm tra ghi ví, rút, hoàn và tương thích lý do cũ
└─ web/rest/FinanceResourceIT.java               Kiểm tra API và phân quyền trên DB thử nghiệm
```

## Frontend

```text
lumi-webapp/src/main/webapp/app/
├─ modules/finance/
│  ├─ instructor-finance.tsx                     [CHÍNH] Ví, giao dịch, form rút và danh sách yêu cầu
│  ├─ parent-refunds.tsx                         [CHÍNH] Form hoàn tiền và danh sách yêu cầu của phụ huynh
│  ├─ admin-finance.tsx                          [CHÍNH] Hai danh sách, duyệt/từ chối, xác nhận chuyển khoản
│  ├─ finance-api.ts                             Kiểu dữ liệu, lời gọi /api/finance và nhãn hiển thị
│  └─ finance.scss                              Kiểu dáng dùng chung
├─ modules/course-purchase/
│  └─ course-payment.tsx                        Liên kết từ đơn đã thanh toán tới form hoàn tiền
├─ modules/instructor/
│  └─ index.tsx                                 Route /instructor/wallet
├─ modules/administration/
│  ├─ index.tsx                                 Route /admin/finance
│  └─ layout/admin-sidebar.tsx                  Liên kết rút tiền và hoàn tiền cho admin
├─ shared/layout/menus/
│  └─ account.tsx                               Liên kết ví giảng viên và yêu cầu hoàn tiền
└─ routes.tsx                                   Route /refund-requests cho ROLE_PARENT
```

Luồng rút tiền: `instructor-finance.tsx` → `finance-api.ts` → `FinanceResource` → `FinanceService` → `InstructorWalletRepository`/`PayoutRequestRepository`/`WalletTransactionRepository` → các bảng ví và rút tiền. Luồng hoàn tiền: `course-payment.tsx` → `parent-refunds.tsx` → `finance-api.ts` → `FinanceResource` → `FinanceService` → `RefundRepository`/`PaymentRepository`/`CourseEnrollmentRepository` → yêu cầu hoàn, thanh toán và quyền học. Admin dùng cùng API để duyệt hoặc từ chối, rồi ghi mã giao dịch sau khi chuyển khoản.

## API và quy tắc

| Vai trò | Thao tác | Endpoint |
| --- | --- | --- |
| Giảng viên | Xem số dư và giao dịch ví | `GET /api/finance/wallet`, `GET /api/finance/wallet/transactions` |
| Giảng viên | Xem và gửi yêu cầu rút tiền | `GET /api/finance/payouts/my`, `POST /api/finance/payouts` |
| Phụ huynh | Xem và gửi yêu cầu hoàn tiền | `GET /api/finance/refunds/my`, `POST /api/finance/refunds` |
| Admin | Xem và xử lý yêu cầu rút | `GET /api/finance/admin/payouts`, `POST /api/finance/admin/payouts/{id}/decision`, `POST /api/finance/admin/payouts/{id}/complete` |
| Admin | Xem và xử lý yêu cầu hoàn | `GET /api/finance/admin/refunds`, `POST /api/finance/admin/refunds/{id}/decision`, `POST /api/finance/admin/refunds/{id}/complete` |

Doanh thu đơn đã thanh toán được chia 80% cho giảng viên và 20% cho LUMI. Tiền giảng viên được giữ 7 ngày trước khi có thể rút; yêu cầu hoàn đang chờ hoặc đã duyệt ngăn giải phóng phần tiền của đơn đó. Mỗi yêu cầu rút từ 50.000 đến 10.000.000 VND, không vượt số dư khả dụng; mỗi giảng viên gửi tối đa một yêu cầu mỗi ngày.

Chỉ phụ huynh đứng tên đơn được gửi yêu cầu hoàn trước mốc 7 ngày từ lúc thanh toán thành công; số tiền hoàn bằng 80% số tiền thực trả. Tạm chưa áp điều kiện đã học dưới 20% thời lượng. Học sinh giữ quyền học khi yêu cầu chờ duyệt; quyền học bị thu hồi lúc admin duyệt. Cả rút và hoàn tiền đều chuyển khoản thủ công, có mã giao dịch mới đánh dấu hoàn tất.

## Schema và mã JHipster còn lại

Các bảng `instructor_wallets`, `wallet_transactions`, `payout_requests` và `refunds` cùng Liquibase changeset tạo bảng đã có sẵn. Nhóm chức năng này không thêm bảng, cột hay migration mới. Thông tin ngân hàng nhận hoàn tiền được lưu bằng JSON có phiên bản trong cột `refunds.reason` hiện có; lý do dạng văn bản cũ vẫn đọc được. Hai `lumi.jdl` và metadata `.jhipster` vẫn mô tả các entity có sẵn.

Các resource, service, DTO và màn CRUD JHipster cho ví, giao dịch ví, yêu cầu rút và hoàn tiền vẫn tồn tại trong mã. `SecurityConfiguration` chặn các endpoint CRUD cũ; menu và route entity tương ứng đã được gỡ khỏi giao diện. Luồng nghiệp vụ hiện dùng `FinanceResource`, `FinanceService` và `modules/finance/`.
