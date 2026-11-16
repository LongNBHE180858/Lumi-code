package com.lumi.web.rest;

import com.lumi.service.payment.CourseOrderService;
import com.lumi.service.dto.APIResponse;
import com.lumi.service.dto.CreateCourseOrderRequestDTO;
import com.lumi.service.dto.CreateCourseOrderResponseDTO;
import com.lumi.service.dto.CourseOrderDetailDTO;
import com.lumi.service.dto.CourseOrderPaymentInfoDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.CacheControl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestController
@RequestMapping("api/course-orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseOrderController {

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<APIResponse<Void>> handleInvalidIdempotencyKey(MissingRequestHeaderException exception) {
        return ResponseEntity.badRequest().body(APIResponse.<Void>builder()
            .status(400).code(400).message("Idempotency-Key bắt buộc phải là UUID hợp lệ.").build());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<APIResponse<Void>> handleInvalidParameter(MethodArgumentTypeMismatchException exception) {
        return ResponseEntity.badRequest().body(APIResponse.<Void>builder()
            .status(400).code(400).message("Tham số yêu cầu không đúng kiểu dữ liệu.").build());
    }

    CourseOrderService service;

    @Operation(summary = "Get owned course order", description = "Read the saved order and expire an overdue pending order")
    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<APIResponse<CourseOrderDetailDTO>> getOrderDetail(@PathVariable Long orderId) {
        APIResponse<CourseOrderDetailDTO> response = APIResponse.<CourseOrderDetailDTO>builder()
            .code(1000)
            .status(HttpStatus.OK.value())
            .message("Lấy thông tin đơn hàng thành công")
            .data(service.getOrderDetail(orderId))
            .build();
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(response);
    }

    @Operation(summary = "Get course order QR information", description = "Build transfer instructions without confirming payment")
    @GetMapping("/{orderId}/payment-info")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<APIResponse<CourseOrderPaymentInfoDTO>> getPaymentInfo(@PathVariable Long orderId) {
        APIResponse<CourseOrderPaymentInfoDTO> response = APIResponse.<CourseOrderPaymentInfoDTO>builder()
            .code(1000)
            .status(HttpStatus.OK.value())
            .message("Lấy thông tin thanh toán thành công")
            .data(service.getPaymentInfo(orderId))
            .build();
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(response);
    }

    @Operation(summary = "Create paid course order", description = "Create an unpaid order purchased by the current parent for a linked child without calling a payment provider")
    @PostMapping
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<APIResponse<CreateCourseOrderResponseDTO>> createOrder(
        @Valid @RequestBody CreateCourseOrderRequestDTO req,
        @RequestHeader("Idempotency-Key") UUID idempotencyKey
    ) {
        APIResponse<CreateCourseOrderResponseDTO> response = APIResponse.<CreateCourseOrderResponseDTO>builder()
            .code(1000)
            .status(HttpStatus.OK.value())
            .message("Xử lý đơn đăng ký khóa học thành công")
            .data(service.createOrder(req, idempotencyKey))
            .build();

        return ResponseEntity.ok(response);
    }
}
