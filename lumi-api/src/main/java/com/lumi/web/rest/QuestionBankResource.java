package com.lumi.web.rest;

import com.lumi.service.QuestionBankService;
import com.lumi.service.dto.APIResponse;
import com.lumi.service.dto.studyClass.QuestionBankDTO;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/instructor/question-banks")
@PreAuthorize("hasAuthority('ROLE_INSTRUCTOR')")
public class QuestionBankResource {

    private final QuestionBankService questionBankService;

    public QuestionBankResource(QuestionBankService questionBankService) {
        this.questionBankService = questionBankService;
    }

    private <T> ResponseEntity<APIResponse<T>> result(HttpStatus status, T data) {
        return ResponseEntity.status(status).body(
            APIResponse.<T>builder().status(status.value()).code(1000).message("Thành công").data(data).build()
        );
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<QuestionBankDTO.BankSummary>>> listBanks(@RequestParam(required = false) Long classId) {
        return result(HttpStatus.OK, questionBankService.listBanks(classId));
    }

    @PostMapping
    public ResponseEntity<APIResponse<QuestionBankDTO.BankSummary>> createBank(@Valid @RequestBody QuestionBankDTO.CreateBank input) {
        return result(HttpStatus.CREATED, questionBankService.createBank(input));
    }

    @GetMapping("/{bankId}/questions")
    public ResponseEntity<APIResponse<List<QuestionBankDTO.QuestionItem>>> listQuestions(
        @PathVariable Long bankId,
        @RequestParam(required = false) String topic,
        @RequestParam(required = false) Integer difficulty
    ) {
        return result(HttpStatus.OK, questionBankService.listQuestions(bankId, topic, difficulty));
    }

    @PostMapping("/{bankId}/questions")
    public ResponseEntity<APIResponse<QuestionBankDTO.QuestionItem>> addQuestion(
        @PathVariable Long bankId,
        @Valid @RequestBody QuestionBankDTO.CreateQuestion input
    ) {
        return result(HttpStatus.CREATED, questionBankService.addQuestion(bankId, input));
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        byte[] excelBytes = questionBankService.generateExcelTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("Mau_Cau_Hoi_Lumi.xlsx").build());
        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }

    @PostMapping(value = "/{bankId}/import-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIResponse<QuestionBankDTO.ImportResult>> importExcel(
        @PathVariable Long bankId,
        @RequestParam("file") MultipartFile file
    ) {
        QuestionBankDTO.ImportResult importResult = questionBankService.importExcel(bankId, file);
        if (!importResult.errors().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                APIResponse.<QuestionBankDTO.ImportResult>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .code(1001)
                    .message("File Excel có " + importResult.errors().size() + " lỗi. Vui lòng sửa lại theo danh sách.")
                    .data(importResult)
                    .build()
            );
        }
        return result(HttpStatus.OK, importResult);
    }

    @GetMapping("/{bankId}/topics")
    public ResponseEntity<APIResponse<List<QuestionBankDTO.TopicSummary>>> getTopics(@PathVariable Long bankId) {
        return result(HttpStatus.OK, questionBankService.getTopicsSummary(bankId));
    }
}
