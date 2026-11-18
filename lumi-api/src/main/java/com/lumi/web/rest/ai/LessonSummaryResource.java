package com.lumi.web.rest.ai;

import com.lumi.domain.CourseMaterial;
import com.lumi.repository.CourseMaterialRepository;
import com.lumi.service.ai.LessonSummaryService;
import com.lumi.service.dto.ai.LessonSummaryDTOs;
import com.lumi.service.storage.CourseObjectStorage;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
@ConditionalOnProperty(name = "lumi.ai.summary.enabled", havingValue = "true", matchIfMissing = true)
public class LessonSummaryResource {

    private static final Logger log = LoggerFactory.getLogger(LessonSummaryResource.class);

    private final LessonSummaryService lessonSummaryService;
    private final CourseMaterialRepository courseMaterialRepository;
    private final Optional<CourseObjectStorage> courseObjectStorage;

    /**
     * Tóm tắt bài học từ đoạn văn bản thuần (Text/JSON).
     */
    @PostMapping("/summarize")
    public ResponseEntity<LessonSummaryDTOs.Response> summarize(@Valid @RequestBody LessonSummaryDTOs.Request request) {
        try {
            String summary = lessonSummaryService.summarize(request.lessonTitle(), request.content());
            return ResponseEntity.ok(new LessonSummaryDTOs.Response(summary));
        } catch (Throwable e) {
            log.error("Lỗi khi tóm tắt bài học: ", e);
            String errorMsg = e.getClass().getSimpleName() + ": " + e.getMessage();
            if (e.getCause() != null) {
                errorMsg += " -> Cause: " + e.getCause().getClass().getSimpleName() + ": " + e.getCause().getMessage();
            }
            return ResponseEntity.status(500).body(new LessonSummaryDTOs.Response("ERROR_DEBUG: " + errorMsg));
        }
    }

    /**
     * Tóm tắt bài học trực tiếp từ file tải lên (.pdf, .docx, .pptx, hoặc ảnh .png/.jpg).
     * Hỗ trợ tham số validateCurriculum=true để tự động kiểm duyệt chuẩn GDPT Toán/Sử 1-9.
     */
    @PostMapping(value = "/summarize-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LessonSummaryDTOs.Response> summarizeFile(
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "lessonTitle", required = false) String lessonTitle,
        @RequestParam(value = "forceOcr", defaultValue = "false") boolean forceOcr,
        @RequestParam(value = "validateCurriculum", defaultValue = "false") boolean validateCurriculum
    ) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(new LessonSummaryDTOs.Response("File tải lên không được để trống."));
            }

            String filename = file.getOriginalFilename();
            log.info("Nhận file tải lên để tóm tắt: {}, dung lượng: {} bytes", filename, file.getSize());

            var response = lessonSummaryService.summarizeFile(file.getBytes(), filename, lessonTitle, forceOcr, validateCurriculum);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("Tài liệu không hợp lệ: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new LessonSummaryDTOs.Response(e.getMessage()));
        } catch (Throwable e) {
            log.error("Lỗi khi tóm tắt từ file: ", e);
            String errorMsg = e.getClass().getSimpleName() + ": " + e.getMessage();
            if (e.getCause() != null) {
                errorMsg += " -> Cause: " + e.getCause().getClass().getSimpleName() + ": " + e.getCause().getMessage();
            }
            return ResponseEntity.status(500).body(new LessonSummaryDTOs.Response("ERROR_DEBUG: " + errorMsg));
        }
    }

    /**
     * Tóm tắt tài liệu đã lưu trên hệ thống theo materialId.
     * Tự động lấy file từ Cloudflare R2 và tóm tắt.
     */
    @PostMapping("/materials/{materialId}/summarize")
    public ResponseEntity<LessonSummaryDTOs.Response> summarizeMaterial(
        @PathVariable Long materialId,
        @RequestParam(value = "forceOcr", defaultValue = "false") boolean forceOcr,
        @RequestParam(value = "validateCurriculum", defaultValue = "false") boolean validateCurriculum
    ) {
        try {
            CourseMaterial material = courseMaterialRepository
                .findById(materialId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài liệu: " + materialId));

            if (courseObjectStorage.isEmpty()) {
                throw new IllegalStateException("Hệ thống lưu trữ R2 chưa được bật trên máy chủ.");
            }

            var access = courseObjectStorage.get().access(material.getFileUrl(), material.getFileName(), true);
            if (access == null || access.url() == null || access.url().isBlank()) {
                throw new IllegalStateException("Không tạo được liên kết tải tài liệu từ kho lưu trữ.");
            }

            log.info("Đang tải dữ liệu tài liệu {} ({}) từ storage...", materialId, material.getFileName());
            RestClient downloader = RestClient.create();
            byte[] fileBytes = downloader.get().uri(URI.create(access.url())).retrieve().body(byte[].class);

            if (fileBytes == null || fileBytes.length == 0) {
                throw new IllegalStateException("Không đọc được dữ liệu của tài liệu từ kho lưu trữ.");
            }

            String fileName = material.getFileName() != null ? material.getFileName() : "";
            if (!fileName.contains(".") && material.getFileType() != null) {
                fileName = fileName + "." + material.getFileType().toLowerCase();
            }
            String title = material.getDisplayName() != null ? material.getDisplayName() : fileName;
            var response = lessonSummaryService.summarizeFile(fileBytes, fileName, title, forceOcr, validateCurriculum);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("Tài liệu không hợp lệ: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new LessonSummaryDTOs.Response(e.getMessage()));
        } catch (Throwable e) {
            log.error("Lỗi khi tóm tắt material {}: ", materialId, e);
            return ResponseEntity.status(500).body(new LessonSummaryDTOs.Response("ERROR_DEBUG: " + e.getMessage()));
        }
    }

    /**
     * Endpoint kiểm duyệt và nhận diện Môn học & Khối lớp từ file tải lên.
     */
    @PostMapping(value = "/classify-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> classifyFile(
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "forceOcr", defaultValue = "false") boolean forceOcr
    ) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("File tải lên không được để trống.");
            }

            var result = lessonSummaryService.classifyFile(file.getBytes(), file.getOriginalFilename(), forceOcr);
            return ResponseEntity.ok(result);
        } catch (Throwable e) {
            log.error("Lỗi khi phân loại file: ", e);
            return ResponseEntity.status(500).body("ERROR_DEBUG: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    /**
     * Endpoint kiểm tra nội dung bóc tách (Preview Extracted Text + LaTeX) trước khi tóm tắt.
     */
    @PostMapping(value = "/extract-text", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> extractText(
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "forceOcr", defaultValue = "false") boolean forceOcr
    ) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("File tải lên không được để trống.");
            }

            String filename = file.getOriginalFilename();
            var result = lessonSummaryService.extractDocument(file.getBytes(), filename, forceOcr);

            return ResponseEntity.ok(
                new LessonSummaryDTOs.ExtractionPreview(
                    filename != null ? filename : "",
                    result.fileType(),
                    result.pageCount(),
                    result.ocrUsed(),
                    result.content().length(),
                    result.content()
                )
            );
        } catch (Throwable e) {
            log.error("Lỗi khi trích xuất file: ", e);
            return ResponseEntity.status(500).body("ERROR_DEBUG: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
