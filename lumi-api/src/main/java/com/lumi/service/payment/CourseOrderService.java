package com.lumi.service.payment;

import com.lumi.service.dto.CreateCourseOrderRequestDTO;
import com.lumi.service.dto.CreateCourseOrderResponseDTO;
import com.lumi.service.dto.CourseOrderDetailDTO;
import com.lumi.service.dto.CourseOrderPaymentInfoDTO;
import java.util.UUID;

public interface CourseOrderService {

    CreateCourseOrderResponseDTO createOrder(CreateCourseOrderRequestDTO req, UUID idempotencyKey);

    CourseOrderDetailDTO getOrderDetail(Long orderId);

    CourseOrderPaymentInfoDTO getPaymentInfo(Long orderId);
}
