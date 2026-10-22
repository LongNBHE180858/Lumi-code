package com.lumi.web.rest;

import com.lumi.service.CourseService;
import com.lumi.service.dto.APIResponse;
import com.lumi.service.dto.CourseBrowseRequest;
import com.lumi.service.dto.CourseBrowseResponse;
import com.lumi.service.dto.CourseOverviewResponse;
import com.lumi.service.dto.CourseReviewsResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseController {

    CourseService service;

    @Operation(summary = "Browse courses", description = "Search, filter and paginate public courses")
    @GetMapping("/browse")
    public ResponseEntity<APIResponse<CourseBrowseResponse>> browse(
        @Valid @ModelAttribute CourseBrowseRequest request,
        @ParameterObject @PageableDefault(size = 6) Pageable pageable
    ) {
        APIResponse<CourseBrowseResponse> response = APIResponse.<CourseBrowseResponse>builder()
            .code(1000)
            .status(HttpStatus.OK.value())
            .message("Lấy danh sách khóa học thành công")
            .data(service.browse(request, pageable))
            .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get course overview", description = "Get public course information and full curriculum")
    @GetMapping("/{id}/overview")
    public ResponseEntity<APIResponse<CourseOverviewResponse>> overview(@PathVariable("id") Long id) {
        APIResponse<CourseOverviewResponse> response = APIResponse.<CourseOverviewResponse>builder()
            .code(1000)
            .status(HttpStatus.OK.value())
            .message("Lấy thông tin chi tiết khóa học thành công")
            .data(service.overview(id))
            .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get course reviews", description = "Get paginated public course reviews and rating statistics")
    @GetMapping("/{id}/reviews")
    public ResponseEntity<APIResponse<CourseReviewsResponse>> reviews(
        @PathVariable("id") Long id,
        @ParameterObject @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        APIResponse<CourseReviewsResponse> response = APIResponse.<CourseReviewsResponse>builder()
            .code(1000)
            .status(HttpStatus.OK.value())
            .message("Lấy đánh giá khóa học thành công")
            .data(service.reviews(id, pageable))
            .build();

        return ResponseEntity.ok(response);
    }
}
