package com.lumi.web.rest;

import com.lumi.service.ClassroomMaterialService;
import com.lumi.service.ClassroomService;
import com.lumi.service.dto.APIResponse;
import com.lumi.service.dto.studyClass.ClassroomDTO;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ClassroomResource {

    private final ClassroomService service;
    private final ClassroomMaterialService materialService;

    public ClassroomResource(ClassroomService service, ClassroomMaterialService materialService) {
        this.service = service;
        this.materialService = materialService;
    }

    private <T> ResponseEntity<APIResponse<T>> result(HttpStatus status, String message, T data) {
        return ResponseEntity.status(status).body(
            APIResponse.<T>builder().status(status.value()).code(1000).message(message).data(data).build()
        );
    }

    @GetMapping("/instructor/classes/{id}/workspace")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<APIResponse<ClassroomDTO>> instructorClass(@PathVariable Long id) {
        return result(HttpStatus.OK, "Lấy thông tin lớp thành công", service.instructorClass(id));
    }

    @GetMapping("/instructor/classes/{id}/members")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<APIResponse<ClassroomDTO.MemberPage>> instructorMembers(
        @PathVariable Long id,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return result(HttpStatus.OK, "Lấy danh sách học sinh thành công", service.instructorMembers(id, page, size));
    }

    @PostMapping(value = "/instructor/classes/{id}/materials", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<APIResponse<ClassroomDTO.Material>> uploadMaterial(
        @PathVariable Long id,
        @RequestParam String title,
        @RequestPart("file") MultipartFile file
    ) {
        return result(HttpStatus.CREATED, "Đã tải bài học lên", materialService.upload(id, title, file));
    }

    @PutMapping("/instructor/classes/{id}/materials/order")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<APIResponse<List<ClassroomDTO.Material>>> reorderMaterials(
        @PathVariable Long id,
        @RequestBody List<Long> materialIds
    ) {
        return result(HttpStatus.OK, "Đã sắp xếp bài học", materialService.reorder(id, materialIds));
    }

    @GetMapping("/classes/{id}/materials/{materialId}/file")
    @PreAuthorize("hasAnyAuthority('ROLE_INSTRUCTOR', 'ROLE_STUDENT')")
    public ResponseEntity<Resource> materialFile(@PathVariable Long id, @PathVariable Long materialId) {
        var file = materialService.access(id, materialId);
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(file.mimeType()))
            .header("Content-Disposition", "inline")
            .header("Cache-Control", "private, no-store")
            .body(file.resource());
    }

    @PostMapping("/instructor/classes/{id}/members/{memberId}/approve")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<APIResponse<ClassroomDTO.Member>> approveMember(@PathVariable Long id, @PathVariable Long memberId) {
        return result(HttpStatus.OK, "Đã duyệt học sinh", service.approveMember(id, memberId));
    }

    @PostMapping("/instructor/classes/{id}/members/{memberId}/reject")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<Void> rejectMember(@PathVariable Long id, @PathVariable Long memberId) {
        service.removeMember(id, memberId, true);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/instructor/classes/{id}/members/{memberId}")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<Void> removeMember(@PathVariable Long id, @PathVariable Long memberId) {
        service.removeMember(id, memberId, false);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/instructor/classes/{id}/assignments")
    @PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
    public ResponseEntity<APIResponse<ClassroomDTO.Task>> createTask(
        @PathVariable Long id,
        @Valid @RequestBody ClassroomDTO.CreateTaskRequest request
    ) {
        return result(HttpStatus.CREATED, "Đã giao bài tập", service.createTask(id, request));
    }

    @GetMapping("/student/classes")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<APIResponse<ClassroomDTO.JoinedClassPage>> studentClasses(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return result(HttpStatus.OK, "Lấy danh sách lớp thành công", service.studentClasses(page, size));
    }

    @PostMapping("/student/classes/join")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<APIResponse<ClassroomDTO.JoinedClass>> joinClass(@Valid @RequestBody ClassroomDTO.JoinRequest request) {
        return result(HttpStatus.OK, "Đã xử lý yêu cầu tham gia lớp", service.joinClass(request.code()));
    }

    @GetMapping("/student/classes/{id}")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public ResponseEntity<APIResponse<ClassroomDTO>> studentClass(@PathVariable Long id) {
        return result(HttpStatus.OK, "Lấy nội dung lớp thành công", service.studentClass(id));
    }
}
