# Dự án
LUMI là nền tảng học Toán và Lịch sử trực tuyến, có khóa học, hồ sơ giảng viên và khu vực quản trị.
- Dùng Java 21, Spring Boot, JPA, Liquibase, PostgreSQL cho BE; React, TypeScript, Vite và mã nền JHipster cho FE.
- Làm việc trong hai repo Git con `lumi-api/` và `lumi-webapp/`; repo Git gốc lưu bản chụp đầy đủ của cả hai, không phải submodule.
- Chạy PostgreSQL từ `lumi-api/`: `docker compose -f src/main/docker/postgresql.yml up -d` (DB dev `lumig46`, cổng máy 5436).
- Chạy BE từ `lumi-api/`: `./mvnw` hoặc `.\mvnw.cmd` trên Windows (profile dev, cổng 8080).
- Chạy FE từ `lumi-webapp/`: `npm ci`, rồi `npm start` (Vite cổng 9000, proxy `/api` sang 8080).
- Kiểm tra BE từ `lumi-api/`: `./mvnw test` cho unit test; `./mvnw verify` cho integration test cần Docker.
- Kiểm tra FE từ `lumi-webapp/`: `npm run lint`, `npm test`, `npm run webapp:build:dev` theo `package.json`.
- Tìm API của ba nhóm admin tại `lumi-api/src/main/java/com/lumi/web/rest/AdminResource.java` và nghiệp vụ tại `service/AdminService.java`.
- Tìm UI admin tại `lumi-webapp/src/main/webapp/app/modules/administration/`; route ở `lumi-webapp/src/main/webapp/app/routes.tsx` và `modules/administration/index.tsx`.
- Xem `health/`, `metrics/`, `logs/`, `docs/` trong FE là mã nền JHipster cho tới khi chức năng dự án chủ động dùng chúng.
