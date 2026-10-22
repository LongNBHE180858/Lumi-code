# LUMI — Course Builder: yêu cầu và hướng dẫn triển khai

Tài liệu dành cho Codex/Antigravity làm việc trực tiếp trong repository. Đọc toàn bộ trước khi sửa code. Mục tiêu: biến UI Course Builder hiện có thành chức năng chạy thật từ FE → BE → PostgreSQL → cloud, có kiểm thử.

## 1. Độ tin cậy và cách dùng

- Người dùng cho biết đã tạo các file FE. Tài liệu này được soạn sau khi xem Figma, **chưa kiểm tra repository, API, DB hoặc cloud thực tế**. Không được coi các phần này đã hoàn thành.
- Nguồn ưu tiên: yêu cầu người dùng mới nhất → yêu cầu nghiệp vụ đã xác nhận trong tài liệu này → hợp đồng API/schema hiện có phù hợp nghiệp vụ → Figma cho bố cục. Khi mâu thuẫn, chỉ ra cụ thể thay vì âm thầm thay nghiệp vụ.
- Tên entity, field, enum và endpoint dưới đây là khái niệm hoặc đề xuất, không phải xác nhận về code đang tồn tại. Tìm và tái sử dụng trước khi tạo mới.
- Không xây lại FE chỉ vì chưa thấy API. Giữ bố cục/component hiện có, bổ sung hành vi và trạng thái cần thiết.
- Tiến hành các bước có thể làm độc lập. Chỉ hỏi khi thiếu một quyết định nghiệp vụ thực sự chặn triển khai; ghi rõ phần nào chưa thể hoàn thành.

## 2. Context dự án và phạm vi

- LUMI phục vụ học sinh lớp 1–9. Stack mục tiêu: React + TypeScript + Vite, Spring Boot + PostgreSQL, sinh nền bằng JHipster. Kiểm tra phiên bản và cấu trúc thật trong repository.
- Cấu trúc từng dùng: `lumi-platform/lumi-api` và `lumi-webapp`; không giả định đường dẫn này nếu repo khác.
- Course là khóa học tự học do giảng viên tạo và bán/chia sẻ nội dung. Class là lớp riêng, độc lập với Course. Không gắn bắt buộc Course với Class.
- Không bắt giảng viên mua Teacher Premium để tạo/bán khóa học.
- Thời hạn truy cập do giảng viên đặt; không mặc định vĩnh viễn.
- Hệ thống có Reviewer kiểm duyệt nội dung. Gửi duyệt không đồng nghĩa xuất bản hoặc mở bán.
- Cloud mục tiêu thuộc Cloudflare, nhưng chưa xác định trong tài liệu là R2, Stream hay tổ hợp dịch vụ. Đọc cấu hình/code để xác định. Không coi R2 là dịch vụ tự chuyển mã video.
- Nhóm 5 người, tổng kế hoạch 7 tuần gồm cả kiểm thử. Phạm vi này ưu tiên luồng tạo bản nháp → soạn nội dung → upload → đặt giá → xem trước → gửi duyệt.
- Ngoài phạm vi triển khai mặc định: thanh toán SePay, enrollment thật, ví/rút tiền, refund, Student Player hoàn chỉnh, AI/RAG, Class, giao diện Reviewer đầy đủ. Chỉ tạo ranh giới tích hợp cần thiết.
- Quy tắc chia doanh thu 20% nền tảng/80% giảng viên thuộc luồng thanh toán; không trừ 20% vào giá hiển thị hoặc tự xây ví trong task này.

## 3. Các màn hình Figma

File: `PdP8MVTielUVkPtxsl3IUx` — Figma_G46.

| Bước               | Node     | URL                                                                            |
| ------------------ | -------- | ------------------------------------------------------------------------------ |
| Thông tin khóa học | 355:1789 | https://www.figma.com/design/PdP8MVTielUVkPtxsl3IUx/Figma_G46?node-id=355-1789 |
| Nội dung bài học   | 362:2029 | https://www.figma.com/design/PdP8MVTielUVkPtxsl3IUx/Figma_G46?node-id=362-2029 |
| Học phí            | 362:2820 | https://www.figma.com/design/PdP8MVTielUVkPtxsl3IUx/Figma_G46?node-id=362-2820 |
| Xem trước          | 362:2587 | https://www.figma.com/design/PdP8MVTielUVkPtxsl3IUx/Figma_G46?node-id=362-2587 |
| Gửi duyệt          | 362:2406 | https://www.figma.com/design/PdP8MVTielUVkPtxsl3IUx/Figma_G46?node-id=362-2406 |

Figma minh họa khóa Toán 6, 10 chương, 64 video, 32 giờ, mỗi video 30 phút, giá 499.000đ và truy cập 12 tháng. **Tất cả là dữ liệu mẫu, không phải giới hạn/quy tắc bắt buộc.** Tên giảng viên, giờ lưu 14:32, 72% tiến độ cũng là dữ liệu mẫu. Không hardcode trong UI thật.

## 4. Bắt đầu bằng kiểm tra repository

1. Đọc `AGENTS.md` áp dụng và hướng dẫn repo. Kiểm tra thay đổi chưa commit, không ghi đè công việc người khác.
2. Tìm route và component của 5 bước; xác định dữ liệu đang là mock, props, state, localStorage hay API thật.
3. Tìm auth/role, HTTP client/interceptor, cách xử lý lỗi và form hiện có. Tái sử dụng quy ước repo.
4. Tìm JDL, entity, DTO, mapper, repository, service, resource/controller, Liquibase changelog liên quan Course/Chapter/Lesson/asset/review. Kiểm tra mapping, khóa ngoại và enum thật.
5. Tìm dịch vụ upload, cấu hình cloud, callback/job và cơ chế cấp quyền xem file. Không in secret ra log/chat.
6. Kiểm tra migrations có chạy đủ không. Entity Java tồn tại không có nghĩa bảng DB đã được tạo. Nếu lỗi, chẩn đoán changelog/log trước; không xóa DB để chữa lỗi.
7. Lập bảng ngắn `Hạng mục | file hiện có | mức hoàn thành | việc cần làm`, rồi thực hiện từng lát cắt FE–BE.

Không thay đổi các bảng JHipster `jhi_user`, `jhi_authority`, `jhi_user_authority` chỉ để phục vụ Course Builder. Không regenerate toàn bộ JDL hoặc tạo entity trùng nếu đã có cấu trúc dùng được. Thay đổi schema bằng migration mới, không sửa changelog đã áp dụng.

## 5. Hành vi chung của builder

- Một Course ID bền vững dùng xuyên suốt 5 bước. Tạo bản nháp trước khi gắn chapter/lesson/asset. Tránh tạo nhiều course khi người dùng bấm lưu lại.
- Có điểm vào từ danh sách khóa học của giảng viên hoặc route tạo/sửa hiện có. Không cần chờ xây Course Discovery công khai mới triển khai builder.
- Lưu nháp cho phép dữ liệu chưa đủ điều kiện gửi duyệt, nhưng vẫn phải đúng kiểu, độ dài và quan hệ. Chỉ báo “Đã lưu” khi BE xác nhận thành công.
- Giữ dữ liệu đang nhập khi API lỗi. Hiển thị lỗi theo trường và thông báo thao tác thất bại rõ ràng.
- Theo dõi dirty state. Khi đổi tab hoặc rời trang, tránh mất thay đổi bằng cơ chế đang có; nếu cần, thêm cảnh báo rời khi chưa lưu. Không tự gọi là autosave nếu chưa triển khai.
- Lưu trước khi tạo bản xem trước/checklist gửi duyệt; nếu lưu thất bại thì dừng thao tác phụ thuộc. Preview phải nói rõ đang xem bản nào.
- Nút Tiếp/Quay lại chuyển đúng bước và giữ Course ID. Refresh route phải tải lại dữ liệu từ BE.
- Có loading, empty, error/retry, saving, disabled khi đang gửi request; tránh request trùng do double click.
- Tổng số chương/bài/video/thời lượng và checklist lấy từ dữ liệu thật, không từ chuỗi mẫu. Thời lượng video lấy từ metadata đáng tin cậy; video giới thiệu không tính vào tổng thời lượng đề cương.
- Không dùng localStorage làm nguồn dữ liệu chính của Course. Có thể dùng cho lựa chọn UI nếu repo đã có quy ước.

## 6. Yêu cầu từng bước

### 6.1 Thông tin khóa học

- Tên, môn học, lớp, bộ SGK; mô tả ngắn/đầy đủ; đối tượng; điều kiện học trước; danh sách mục tiêu có thêm/xóa/sắp xếp.
- Danh mục lấy từ nguồn hiện có. Kiểm tra quan hệ môn/lớp/bộ sách nếu DB hỗ trợ; không tự thêm danh mục khác nghiệp vụ.
- Ảnh bìa: upload, xem, thay; ảnh mẫu chỉ là fallback có chủ đích, không coi hình placeholder là file đã upload.
- Video giới thiệu: upload, phát xem, thay/gỡ và trạng thái sẵn sàng.
- Giảng viên lấy từ tài khoản/profile; BE xác định chủ sở hữu từ authentication, không tin owner ID FE gửi lên.
- Xử lý rich text theo cách repo dùng; sanitize khi render để tránh script injection.

### 6.2 Nội dung bài học

- Thêm/đổi tên/xóa chương; thêm/sửa/xóa bài; chọn bài để sửa tên và mô tả.
- Sắp xếp chương, bài và chuyển bài giữa các chương trong cùng khóa học. FE cập nhật theo phản hồi BE; lỗi phải hoàn nguyên hoặc refetch.
- Đổi thứ tự phải lưu bền vững, không trùng/mất bài. BE kiểm tra mọi ID thuộc đúng course, thực hiện thay đổi thứ tự trong transaction.
- Xác nhận trước khi xóa chương có bài hoặc gỡ tài nguyên đang dùng. Không xóa cloud object còn đang được tham chiếu.
- Mỗi bài trong màn hình hiện tại có một video đang dùng, nhiều tài liệu đính kèm và cờ xem thử miễn phí. Không tự kết luận mọi loại bài trong toàn hệ thống đều bắt buộc video; kiểm tra loại bài hiện có.
- Video: upload, xem, thay, gỡ; tải xuống theo quyền được phép. Tài liệu: upload nhiều file, thay, tải xuống, gỡ.
- Tệp đang dùng và tệp thay thế chưa áp dụng là hai trạng thái riêng. Upload thất bại hoặc đang xử lý **không làm mất file đang dùng**.
- Chỉ áp dụng bản thay khi file mới đã sẵn sàng và BE liên kết thành công. Nếu áp dụng tự động khi xử lý xong, phải thể hiện rõ hành vi; nếu UI ghi “chưa áp dụng”, cần thao tác áp dụng rõ ràng. Ưu tiên thao tác áp dụng tường minh cho đúng bản mẫu.
- Hủy/thử lại/bỏ tệp chờ không được tác động tài nguyên đang dùng. Retry sau refresh phải dựa vào upload session/job thật nếu tính năng hỗ trợ.
- Tệp thay thế chưa áp dụng không tính vào tổng nội dung hay bản gửi duyệt. File bắt buộc đang dùng chưa sẵn sàng thì chặn gửi duyệt.
- Không hiển thị % chuyển mã nếu provider chỉ cung cấp trạng thái. Tách % truyền file với trạng thái xử lý; không dựng timer giả.

### 6.3 Học phí và truy cập

- UI có Có học phí/Miễn phí; giá VND; thanh toán một lần; số lượng và đơn vị thời gian truy cập.
- Khóa có học phí: giá > 0. Miễn phí: giá lưu/hiển thị nhất quán là 0. Không để giá cũ gây hiểu nhầm khi chuyển chế độ.
- Giá dùng kiểu tiền chính xác: VND nguyên hoặc BigDecimal theo repo. Chuỗi định dạng `499.000` chỉ ở UI, API gửi giá trị số theo contract.
- Thời hạn là số nguyên dương với đơn vị được hệ thống hỗ trợ; không tự thêm trả góp, subscription hay trọn đời chỉ vì có dropdown.
- Nếu hiện chỉ có một phương thức/đơn vị hợp lệ, UI thể hiện đúng thay vì giả nhiều lựa chọn.
- Hiển thị thời hạn tính từ đăng ký thành công theo Figma; chưa triển khai tính ngày hết hạn/enrollment thật trong builder. Khi luồng đó được làm phải dùng cùng quy tắc.
- Không tự đặt min/max giá, storage quota, số bài hoặc số tháng nếu chưa có nguồn xác nhận. Nếu schema đã có ràng buộc, đối chiếu và báo mâu thuẫn.

### 6.4 Xem trước

- Dùng dữ liệu bản nháp thật gồm thông tin, mục tiêu, đề cương, giá, thời hạn và giảng viên.
- Phát video giới thiệu và bài xem thử đúng quyền; mở/đóng chương; link chỉnh sửa quay về đúng tab.
- Có nhãn “chưa xuất bản”. Nút đăng ký/thanh toán bị vô hiệu trong preview.
- Preview bản nháp chỉ chủ sở hữu hoặc vai trò được cấp quyền mới xem. Không mở toàn bộ tài nguyên draft qua API công khai chỉ để render preview.
- Dùng chung component hiển thị với Course Details nếu đã có, nhưng tách quyền truy cập dữ liệu private/public.

### 6.5 Kiểm tra và gửi duyệt

- Checklist do BE đánh giá trên bản đã lưu; FE trình bày lỗi thiếu và link quay về đúng bước/trường.
- Kiểm tra thông tin bắt buộc, đề cương hợp lệ, tài nguyên đang dùng sẵn sàng, giá/thời hạn hợp lệ và xác nhận quyền sử dụng nội dung.
- Chính sách bắt buộc ảnh bìa, video giới thiệu, số mục tiêu tối thiểu, số chương/bài tối thiểu cần lấy từ BR/repo. Figma minh họa có đầy đủ các mục này nhưng không đủ để khẳng định tất cả là bắt buộc. Chưa có chính sách thì ghi rõ cần xác nhận, không tự biến dữ liệu mẫu thành luật.
- Ghi chú gửi duyệt là tùy chọn, chỉ dành cho reviewer, không trả về public course DTO.
- Checkbox quyền sử dụng phải được gửi và BE kiểm tra; lưu dấu xác nhận cùng yêu cầu theo schema hiện có.
- Lúc submit, BE kiểm tra lại validation, quyền, phiên bản và trạng thái trong một thao tác nhất quán; không tin kết quả checklist cũ từ FE.
- Không submit nếu thay đổi chưa lưu hoặc phiên bản đã lỗi thời. Xử lý conflict bằng thông báo/refetch thay vì ghi đè âm thầm.
- Thành công: hiển thị Chờ duyệt, thời điểm gửi, mã yêu cầu nếu có. Retry/double click không được tạo nhiều yêu cầu đang chờ cho cùng bản.
- Gửi duyệt không công khai, không tạo enrollment hay giao dịch tiền.

## 7. Lifecycle và điểm cần xác nhận

Ánh xạ vào enum và workflow hiện có, không tạo enum song song tùy tiện.

| Trạng thái khái niệm     | Hành vi                                                  |
| ------------------------ | -------------------------------------------------------- |
| Draft                    | Chủ sở hữu chỉnh sửa và lưu, chưa công khai              |
| Pending review           | Có yêu cầu gửi duyệt, chưa công khai                     |
| Needs changes / Rejected | Xem phản hồi, sửa và gửi lại theo workflow               |
| Approved                 | Đã duyệt; hành động xuất bản theo quy tắc riêng          |
| Published                | Cho phép luồng công khai theo chính sách, ngoài task này |

Quy tắc chưa được người dùng chốt: sửa trong khi chờ duyệt; duyệt xong tự xuất bản hay xuất bản riêng; thu hồi yêu cầu; sửa khóa đã xuất bản; có bắt buộc ít nhất một bài xem thử hay không.

Nếu repo chưa có quy tắc, đề xuất MVP **khóa nội dung đã gửi trong lúc Pending review** để reviewer xem đúng bản. Đây là đề xuất triển khai, không phải yêu cầu đã xác nhận. Nếu cho phép sửa, cần revision/snapshot bất biến cho bản gửi duyệt; không để sửa live draft làm thay đổi âm thầm nội dung reviewer đang duyệt. Không tự xây hệ thống versioning phức tạp khi chưa cần.

## 8. Dữ liệu và API

Tái sử dụng model hiện có cho Course, Chapter, Lesson, Asset/File và Review Request. Không tạo thêm bảng chỉ vì tài liệu liệt kê khái niệm. Quan hệ cần bảo đảm: chapter thuộc course; lesson thuộc chapter/course; asset có trạng thái và quyền sở hữu; thứ tự ổn định; yêu cầu review gắn bản nội dung xác định.

DB lưu metadata và định danh object/provider, không lưu video/PDF binary. Không lưu signed URL hết hạn làm định danh bền vững. Với file private, cấp URL xem/tải ngắn hạn sau kiểm tra quyền.

### Contract cần có (đường dẫn là đề xuất)

| Năng lực           | Ví dụ nếu chưa có API tương đương                                     |
| ------------------ | --------------------------------------------------------------------- |
| Tạo/tải/lưu draft  | POST /api/instructor/courses; GET/PATCH /api/instructor/courses/{id}  |
| Danh sách của tôi  | GET /api/instructor/courses với phân trang/lọc nếu UI cần             |
| Quản lý chương     | POST /api/instructor/courses/{id}/chapters; PATCH/DELETE chapter      |
| Quản lý bài        | POST chapter/{id}/lessons; PATCH/DELETE lesson                        |
| Sắp xếp/chuyển bài | API cập nhật cấu trúc thứ tự theo transaction                         |
| Upload             | Tạo upload session → truyền file → xác minh hoàn tất → đọc trạng thái |
| Gắn/thay/gỡ asset  | API kiểm tra asset thuộc owner, ready, đúng loại và đúng course       |
| Học phí            | PATCH phần pricing trong course hoặc API hiện có                      |
| Preview            | GET bản preview private hoặc aggregate draft đã phân quyền            |
| Kiểm tra gửi duyệt | GET /api/instructor/courses/{id}/submission-check                     |
| Gửi duyệt          | POST /api/instructor/courses/{id}/submit-review                       |
| Theo dõi yêu cầu   | GET trạng thái/feedback từ model review hiện có                       |

Không gọi trực tiếp CRUD sinh sẵn nếu endpoint đó cho phép cập nhật owner/status/quan hệ tùy ý hoặc lách workflow. Bổ sung service nghiệp vụ, DTO giới hạn field và kiểm tra quyền phía server.

- Ghi contract thật sau khi kiểm tra: method/path, request DTO, response DTO, status code và lỗi.
- Lỗi validation trả mã và field path có thể ánh xạ tới form. Dùng quy ước lỗi JHipster hiện có.
- Phân biệt 401, 403/404 theo chính sách repo, 409 conflict và lỗi validation. Không trả stack trace hoặc secret ra FE.
- BE lấy current user; mọi mutation kiểm tra ownership và quan hệ lồng nhau. Role Instructor đơn thuần không đủ để sửa mọi course.
- Checklist/summary có thể trả `canSubmit`, danh sách lỗi gồm `code`, `field`, `step`, `message`, và `version` nếu repo hỗ trợ; đây là contract đề xuất.
- FE tái sử dụng client auth, interceptor, refresh/token và cơ chế cache của repo. Không dựng HTTP client hay auth thứ hai.

## 9. Upload cloud là một luồng riêng

1. FE chọn file; kiểm tra loại/kích thước theo chính sách cấu hình.
2. BE xác thực owner, loại asset, quota nếu đã có, tạo upload session hoặc dùng flow hiện có.
3. Truyền file theo kiến trúc hiện có (direct upload có quyền tạm thời hoặc BE streaming); không nhúng cloud secret vào FE.
4. BE/provider xác minh upload hoàn tất; nếu có chuyển mã, chờ trạng thái ready. Không cho FE tự khai file ready bằng một boolean.
5. Gắn asset vào bài/course hoặc để trong khu vực thay thế chờ áp dụng. Việc áp dụng phải nguyên tử, không làm mất liên kết cũ khi thất bại.
6. Cấp quyền phát/tải sau kiểm tra quyền; xử lý CORS và URL hết hạn.
7. Dọn upload bỏ dở/object mồ côi theo cơ chế phù hợp; chỉ xóa khi không còn tham chiếu, kể cả snapshot đã gửi duyệt.

Callback phải kiểm tra chữ ký/cơ chế xác thực provider và xử lý trùng idempotently. Với upload trực tiếp, xác minh object/provider metadata trước khi finalize. Nếu thiếu credential, vẫn triển khai adapter/config mẫu không có secret và test bằng fake; báo rõ **chưa kiểm thử cloud thật**, không báo upload production đã chạy.

## 10. Thứ tự thực hiện

1. Audit repo, mapping requirement → code/API/DB; xử lý migration/auth nền nếu đang chặn.
2. Làm lát cắt tạo draft → lưu thông tin → reload hiện đúng.
3. CRUD chương/bài → reorder/move → reload đúng cấu trúc.
4. Upload cloud cho ảnh/video/tài liệu → phát/tải đúng quyền → thay thế không phá file cũ.
5. Lưu giá/thời hạn → đồng bộ summary.
6. Preview private từ dữ liệu đã lưu.
7. Checklist server → submit → trạng thái chờ duyệt → chống gửi trùng.
8. Kiểm thử luồng, lỗi, quyền và build/lint phù hợp; sửa các lỗi do thay đổi gây ra.

Có thể phân thành công việc FE, BE nghiệp vụ, cloud và kiểm thử nhưng thống nhất DTO trước. Không để FE hoàn thành bằng mock trong khi BE định nghĩa contract khác.

## 11. Tiêu chí nghiệm thu và kiểm thử có ý nghĩa

- [ ] Giảng viên tạo draft, sửa, lưu rồi refresh vẫn thấy dữ liệu thật.
- [ ] Người khác không tải/sửa/xóa course, chapter, lesson hoặc asset bằng cách đổi ID; test cả API, không chỉ ẩn nút.
- [ ] Thêm/sửa/xóa/reorder/move bài lưu đúng, không thể chuyển sang course của người khác.
- [ ] Upload thật hoặc integration test phù hợp; failed/processing/ready hiển thị đúng. Mất kết nối không báo lưu thành công.
- [ ] Thay video/PDF thất bại giữ file cũ; chưa áp dụng không xuất hiện trong preview/bản gửi duyệt; áp dụng thành công mới đổi liên kết.
- [ ] Giá paid/free và thời hạn lưu/hiển thị đúng, FE và BE đều validate.
- [ ] Preview phản ánh bản đã lưu; không tạo thanh toán; draft/asset private không lộ qua API public.
- [ ] BE từ chối submit thiếu nội dung bắt buộc, asset đang dùng chưa ready, chưa xác nhận quyền sử dụng hoặc sai trạng thái.
- [ ] Tệp thay thế tùy chọn chưa áp dụng không chặn một bản hiện tại vốn đã hợp lệ.
- [ ] Submit hợp lệ tạo đúng một yêu cầu Pending review, không tự publish; double click/retry không tạo trùng.
- [ ] Kiểm tra conflict phiên bản nếu hỗ trợ nhiều tab/người sửa; phản hồi reviewer được hiển thị nếu API hiện có.
- [ ] Responsive cơ bản, keyboard/form label, trạng thái loading/error/empty không làm mất dữ liệu.
- [ ] Build/typecheck/lint và test liên quan chạy theo scripts thật của repo. Ghi lệnh và kết quả; không tuyên bố pass khi chưa chạy.

Ưu tiên service/integration test cho ownership, submit, thứ tự và thay asset; một luồng E2E với dữ liệu nhỏ cho tạo → upload → preview → submit. Không viết test snapshot dàn trải chỉ để kiểm tra các chuỗi Figma.

## 12. Kết quả agent phải báo cáo khi xong

1. Các chức năng đã hoàn thành và file chính đã thay đổi.
2. Contract API thực tế, schema/migration thêm nếu có và hướng dẫn chạy.
3. Biến môi trường cần cấu hình bằng tên biến, không kèm secret.
4. Các lệnh kiểm thử đã chạy, kết quả và giới hạn kiểm thử cloud.
5. Điểm chưa hoàn thành/đang chờ quyết định, không che bằng mock hoặc dữ liệu hardcode.

Không deploy, mua dịch vụ, xóa DB hoặc thay đổi dữ liệu thật chỉ dựa trên tài liệu này. Hoàn thành triển khai cục bộ trong phạm vi được giao và giữ nguyên công việc ngoài phạm vi.

## 13. Prompt khởi động

> Đọc COURSE_BUILDER_IMPLEMENTATION.md và AGENTS.md áp dụng. Kiểm tra các file FE Course Builder đang có, BE/JDL/Liquibase, auth và upload cloud. Trước tiên báo bảng khoảng trống có đường dẫn file thật, phân biệt phần đã chạy với mock. Sau đó triển khai tuần tự từng lát cắt theo tài liệu, tái sử dụng code hiện có và kiểm thử. Không chỉ nối API vào UI nếu BE thiếu ownership, lưu bền vững, upload lifecycle hoặc workflow gửi duyệt. Không tự thêm nghiệp vụ chưa xác nhận; tiếp tục những phần không bị chặn và ghi rõ quyết định còn thiếu.
