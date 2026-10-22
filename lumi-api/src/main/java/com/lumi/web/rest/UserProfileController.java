package com.lumi.web.rest;

import com.lumi.service.UserProfileService;
import com.lumi.service.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/profile")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileController {

    UserProfileService service;

    @Operation(summary = "Get my profile ", description = "get my profile information")
    @GetMapping
    public ResponseEntity<APIResponse<AccountProfileDTO>> getProfile() {
        APIResponse<AccountProfileDTO> response = APIResponse.<AccountProfileDTO>builder()
            .code(1000)
            .status(HttpStatus.OK.value())
            .message("Get user profile successfully")
            .data(service.getProfile())
            .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update My Profile", description = "Update My Profile")
    @PutMapping
    public ResponseEntity<APIResponse<AccountProfileDTO>> update(@Valid @RequestBody AccountProfileDTO req) {
        APIResponse<AccountProfileDTO> response = APIResponse.<AccountProfileDTO>builder()
            .code(1000)
            .status(HttpStatus.OK.value())
            .message("Update Profile successfully")
            .data(service.updateProfile(req))
            .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get student profile statistics", description = "Get statistics in my profile if i'm student ")
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<APIResponse<StudentProfileStatisticsDTO>> getStudentStatistics() {
        APIResponse<StudentProfileStatisticsDTO> response = APIResponse.<StudentProfileStatisticsDTO>builder()
            .code(1000)
            .status(HttpStatus.OK.value())
            .message("Get my statistic successfully")
            .data(service.getProfileStatistic())
            .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get student linked with parents account", description = "Get my student in my profile if i'm parents ")
    @GetMapping("/linked-students")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<APIResponse<List<LinkedStudentDTO>>> getLinkedStudent() {
        APIResponse<List<LinkedStudentDTO>> response = APIResponse.<List<LinkedStudentDTO>>builder()
            .code(1000)
            .status(HttpStatus.OK.value())
            .message("Get linked students successfully")
            .data(service.getLinkedStudents())
            .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get public instructor profile", description = "Get instructor profile ìnormation for instructor profile page ")
    @GetMapping("instructor/{id}")
    public ResponseEntity<APIResponse<InstructorPublicProfileDTO>> getPublicInstructorProfileById(@PathVariable Long id) {
        APIResponse<InstructorPublicProfileDTO> response = APIResponse.<InstructorPublicProfileDTO>builder()
            .code(1000)
            .status(HttpStatus.OK.value())
            .message("Get instructor public profile by instructorId successfully")
            .data(service.getPublicInstructorProfile(id))
            .build();
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get instructor public course by instructorId",
        description = "Get instructor public course by their instructorId for instructor profile page  "
    )
    @GetMapping("/instructor/course/{id}")
    public ResponseEntity<APIResponse<List<InstructorPublicCourseDTO>>> getInstructorPublicCourseById(@PathVariable Long id) {
        APIResponse<List<InstructorPublicCourseDTO>> response = APIResponse.<List<InstructorPublicCourseDTO>>builder()
            .code(1000)
            .status(HttpStatus.OK.value())
            .message("Get instructor public course by instructorId successfully")
            .data(service.getInstructorPublicCourses(id))
            .build();
        return ResponseEntity.ok(response);
    }
}
