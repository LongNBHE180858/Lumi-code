package com.lumi.web.rest.errors;

import com.lumi.security.UserNotActivatedException;
import com.lumi.service.dto.APIResponse;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice(basePackages = "com.lumi.web.rest")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AuthExceptionHandler.class);

    @ExceptionHandler(BadRequestAlertException.class)
    public ResponseEntity<APIResponse<Void>> handleBadRequestAlertException(BadRequestAlertException ex) {
        String cleanMessage =
            ex.getBody() != null && ex.getBody().getTitle() != null && !ex.getBody().getTitle().isBlank()
                ? ex.getBody().getTitle()
                : "Yêu cầu không hợp lệ.";

        LOG.warn("Auth business error: {} (key: {})", cleanMessage, ex.getErrorKey());

        APIResponse<Void> body = APIResponse.<Void>builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .code(HttpStatus.BAD_REQUEST.value())
            .message(cleanMessage)
            .build();
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(GraphCycleException.class)
    public ResponseEntity<Map<String, Object>> handleGraphCycleException(GraphCycleException ex) {
        String cleanMessage =
            ex.getBody() != null && ex.getBody().getTitle() != null && !ex.getBody().getTitle().isBlank()
                ? ex.getBody().getTitle()
                : ex.getMessage();

        LOG.warn("Graph cycle error: {} (path: {})", cleanMessage, ex.getCyclePath());

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("code", HttpStatus.BAD_REQUEST.value());
        body.put("message", cleanMessage);
        body.put("errorKey", ex.getErrorKey());
        body.put("cyclePath", ex.getCyclePath());
        body.put("cycleNodeIds", ex.getCycleNodeIds());

        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Bắt lỗi (401, 403, 404 từ Service)
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<APIResponse<Void>> handleResponseStatusException(ResponseStatusException ex) {
        int statusCode = ex.getStatusCode().value();
        String cleanMessage = ex.getReason() != null && !ex.getReason().isBlank() ? ex.getReason() : "Lỗi xử lý yêu cầu.";

        LOG.warn("Auth status error [{}]: {}", statusCode, cleanMessage);

        APIResponse<Void> body = APIResponse.<Void>builder().status(statusCode).code(statusCode).message(cleanMessage).build();
        return ResponseEntity.status(statusCode).body(body);
    }

    /**
     * Bắt lỗi sai thông tin đăng nhập từ Spring Security
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<APIResponse<Void>> handleBadCredentialsException(BadCredentialsException ex) {
        LOG.warn("Login failed: Bad credentials");
        APIResponse<Void> body = APIResponse.<Void>builder()
            .status(HttpStatus.UNAUTHORIZED.value())
            .code(HttpStatus.UNAUTHORIZED.value())
            .message("Tài khoản hoặc mật khẩu không chính xác.")
            .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    /**
     * Bắt lỗi tài khoản chưa kích hoạt
     */
    @ExceptionHandler(UserNotActivatedException.class)
    public ResponseEntity<APIResponse<Void>> handleUserNotActivatedException(UserNotActivatedException ex) {
        LOG.warn("Login failed: User not activated: {}", ex.getMessage());
        String msg =
            ex.getMessage() != null && !ex.getMessage().isBlank()
                ? ex.getMessage()
                : "Tài khoản chưa được kích hoạt. Vui lòng xác thực mã OTP gửi về email trước khi đăng nhập.";
        APIResponse<Void> body = APIResponse.<Void>builder()
            .status(HttpStatus.UNAUTHORIZED.value())
            .code(HttpStatus.UNAUTHORIZED.value())
            .message(msg)
            .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    /**
     * Bắt lỗi validation khi người dùng nhập thiếu hoặc sai trường dữ liệu trên DTO
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<Map<String, String>>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        String firstErrorMessage = ex.getBindingResult().getFieldErrors().isEmpty()
            ? "Dữ liệu nhập vào không hợp lệ."
            : ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        LOG.warn("Validation error: {}", fieldErrors);

        APIResponse<Map<String, String>> body = APIResponse.<Map<String, String>>builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .code(HttpStatus.BAD_REQUEST.value())
            .message(firstErrorMessage)
            .data(fieldErrors)
            .build();
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Tấm lưới bảo vệ cuối cùng: Bắt TẤT CẢ các lỗi không lường trước được (Lỗi 500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<Void>> handleGeneralException(Exception ex) {
        LOG.error("Lỗi hệ thống bất ngờ (500): ", ex);

        APIResponse<Void> body = APIResponse.<Void>builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .message("Hệ thống đang gặp sự cố gián đoạn. Vui lòng thử lại sau ít phút.")
            .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
