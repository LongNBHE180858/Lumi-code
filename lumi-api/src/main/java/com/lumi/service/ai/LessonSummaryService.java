package com.lumi.service.ai;

import com.lumi.service.dto.ai.LessonSummaryDTOs;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xslf.usermodel.*;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Service toàn diện xử lý tóm tắt bài học:
 * 1. Bóc tách tài liệu đa định dạng (.docx, .pptx, .pdf, .png, .jpg, .txt).
 * 2. Vision OCR trích xuất công thức Toán (LaTeX) và mô tả hình vẽ từ ảnh/PDF scan.
 * 3. Kiểm duyệt và nhận diện Môn học & Khối lớp (Toán/Sử 1-9 theo chuẩn GDPT 2018).
 * 4. Sinh bản tóm tắt 5 ý cốt lõi qua mô hình LLM fine-tuned (Qwen3-8B).
 */
@Service
@ConditionalOnProperty(name = "lumi.ai.summary.enabled", havingValue = "true", matchIfMissing = true)
public class LessonSummaryService {

    private static final Logger log = LoggerFactory.getLogger(LessonSummaryService.class);

    private static final String SUMMARY_SYSTEM_PROMPT =
        "Bạn là trợ lý học tập dành cho học sinh phổ thông. " +
        "Tóm tắt tài liệu thành đúng 5 ý chính, đánh số từ 1 đến 5. " +
        "Giữ chính xác khái niệm, điều kiện và công thức. " +
        "Viết ngắn gọn, dễ hiểu; không thêm kiến thức ngoài tài liệu và không giải bài tập nếu không được yêu cầu.";

    private static final String VISION_OCR_PROMPT =
        "Bạn là chuyên gia số hoá tài liệu Toán học. Hãy trích xuất toàn bộ nội dung từ hình ảnh này thành văn bản Markdown chuẩn:\n" +
        "1. Giữ nguyên 100% công thức toán học dưới dạng LaTeX chuẩn (đặt trong $...$ cho công thức nội dòng và $$...$$ cho công thức khối).\n" +
        "2. Nếu có hình vẽ hình học (hình tam giác, đường tròn, đồ thị hàm số, hình không gian...), hãy mô tả ngắn gọn chi tiết hình vẽ trong ngoặc vuông: [Hình vẽ: ...] để người đọc hiểu được cấu trúc bài toán.\n" +
        "3. Giữ nguyên toàn bộ đề mục, định nghĩa, định lý, công thức, ví dụ và bài tập.\n" +
        "4. Không thêm lời chào, bình luận hay thông tin ngoài hình ảnh.";

    private static final String CLASSIFICATION_SYSTEM_PROMPT =
        "Bạn là chuyên gia thẩm định chương trình giáo dục phổ thông Việt Nam (GDPT 2018).\n" +
        "Nhiệm vụ của bạn là phân tích đoạn trích tài liệu học tập và xác định:\n" +
        "1. Môn học (subject): 'TOAN' (Toán học), 'LICH_SU' (Lịch sử hoặc Lịch sử & Địa lý), hoặc 'KHAC' (nếu là môn khác, tài liệu linh tinh, tin tức, văn thơ, rác...).\n" +
        "2. Khối lớp (grade): từ 1 đến 9 (nếu nội dung vượt quá lớp 9 như lớp 10, 11, 12, đại học hoặc không xác định thì để null).\n" +
        "3. Tính hợp lệ (isValid): true nếu tài liệu thuộc môn Toán hoặc Lịch sử từ Lớp 1 đến Lớp 9 theo chuẩn GDPT 2018; false nếu là môn khác, tài liệu linh tinh, hoặc kiến thức trên lớp 9.\n" +
        "4. Tên chủ đề (topic): Tên bài học hoặc chủ đề phù hợp trong chương trình GDPT.\n" +
        "5. Lý do (reason): Giải thích ngắn gọn trong 1-2 câu.\n\n" +
        "BẮT BUỘC chỉ trả về duy nhất một chuỗi JSON hợp lệ không bọc trong markdown block, theo định dạng:\n" +
        "{\"isValid\": true, \"subject\": \"TOAN\", \"grade\": 9, \"topic\": \"...\", \"reason\": \"...\"}";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    // Cấu hình mô hình ngôn ngữ tóm tắt (Qwen3-8B / LLM)
    private final String summaryBaseUrl;
    private final String summaryApiKey;
    private final String summaryModel;

    // Cấu hình mô hình Vision OCR & Thẩm định (Gemini 1.5 Flash / Vision LLM)
    private final String visionBaseUrl;
    private final String visionApiKey;
    private final String visionModel;

    public record DocumentExtraction(String content, String fileType, int pageCount, boolean ocrUsed) {}

    public LessonSummaryService(
        @Value("${spring.ai.openai.base-url:https://api.openai.com/v1/}") String summaryBaseUrl,
        @Value("${spring.ai.openai.api-key:dummy}") String summaryApiKey,
        @Value("${spring.ai.openai.chat.model:qwen3-8b-lumi}") String summaryModel,
        @Value("${lumi.ai.vision.base-url:https://generativelanguage.googleapis.com/v1beta/openai/}") String visionBaseUrl,
        @Value("${lumi.ai.vision.api-key:${UNIPASS_AI_GEMINI_API_KEY:${GEMINI_API_KEY:${LUMI_AI_API_KEY:}}}}") String visionApiKey,
        @Value("${lumi.ai.vision.model:gemini-flash-lite-latest}") String visionModel,
        ObjectMapper objectMapper
    ) {
        this.summaryBaseUrl = normalizeBaseUrl(summaryBaseUrl);
        this.summaryApiKey = summaryApiKey;
        this.summaryModel = summaryModel;

        this.visionBaseUrl = normalizeBaseUrl(visionBaseUrl);
        this.visionApiKey = visionApiKey;
        this.visionModel = visionModel;

        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(30));
        requestFactory.setReadTimeout(Duration.ofMinutes(3));

        this.restClient = RestClient.builder().requestFactory(requestFactory).build();
    }

    // ==========================================
    // 1. TÓM TẮT BÀI HỌC (LLM Text Summarization)
    // ==========================================

    public String summarize(String lessonTitle, String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Nội dung bài học không được để trống.");
        }

        StringBuilder userPrompt = new StringBuilder("Tóm tắt bài học sau thành đúng 5 ý chính cốt lõi:\n\n");
        if (lessonTitle != null && !lessonTitle.isBlank()) {
            userPrompt.append(lessonTitle.trim()).append("\n");
        }
        userPrompt.append(content.trim());

        String endpoint = buildChatEndpoint(summaryBaseUrl);
        log.info("Đang gửi yêu cầu tóm tắt tới AI endpoint: {}", endpoint);

        Map<String, Object> requestBody = Map.of(
            "model",
            summaryModel,
            "messages",
            List.of(Map.of("role", "system", "content", SUMMARY_SYSTEM_PROMPT), Map.of("role", "user", "content", userPrompt.toString())),
            "temperature",
            0.5,
            "max_tokens",
            800
        );

        String rawResponse = restClient
            .post()
            .uri(endpoint)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + summaryApiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestBody)
            .retrieve()
            .body(String.class);

        log.debug("Phản hồi thô từ AI server: {}", rawResponse);

        try {
            JsonNode rootNode = objectMapper.readTree(rawResponse);
            JsonNode choices = rootNode.path("choices");
            if (choices.isArray() && !choices.isEmpty()) {
                String replyContent = choices.get(0).path("message").path("content").asString();
                if (replyContent != null && !replyContent.isBlank()) {
                    log.info("Tóm tắt bài học thành công.");
                    return replyContent;
                }
            }
            throw new IllegalStateException("Phản hồi từ AI server không đúng định dạng: " + rawResponse);
        } catch (Exception e) {
            log.error("Lỗi khi đọc JSON phản hồi từ AI: {}", e.getMessage());
            throw new RuntimeException("Lỗi phân tích phản hồi AI: " + e.getMessage(), e);
        }
    }

    public LessonSummaryDTOs.Response summarizeFile(
        byte[] fileBytes,
        String fileName,
        String lessonTitle,
        boolean forceOcr,
        boolean validateCurriculum
    ) {
        var extraction = extractDocument(fileBytes, fileName, forceOcr);

        LessonSummaryDTOs.CurriculumClassification classification = null;
        if (validateCurriculum) {
            classification = classifyCurriculum(extraction.content());
            if (!classification.isValid()) {
                throw new IllegalArgumentException("Tài liệu không hợp lệ theo chuẩn Toán/Sử lớp 1-9: " + classification.reason());
            }
        }

        String title = lessonTitle != null && !lessonTitle.isBlank() ? lessonTitle : fileName;
        String summary = summarize(title, extraction.content());

        return new LessonSummaryDTOs.Response(summary, classification);
    }

    // ==========================================
    // 2. KIỂM DUYỆT & NHẬN DIỆN MÔN/LỚP (Classifier)
    // ==========================================

    public LessonSummaryDTOs.CurriculumClassification classifyCurriculum(String content) {
        if (content == null || content.isBlank()) {
            return new LessonSummaryDTOs.CurriculumClassification(false, "KHAC", null, "Không xác định", "Nội dung tài liệu trống.");
        }

        String sampleText = content.length() > 2000 ? content.substring(0, 2000) : content;

        boolean useSummaryEndpoint = summaryApiKey != null && !summaryApiKey.isBlank() && !"dummy".equalsIgnoreCase(summaryApiKey);
        String endpoint = useSummaryEndpoint ? buildChatEndpoint(summaryBaseUrl) : buildChatEndpoint(visionBaseUrl);
        String apiKey = useSummaryEndpoint ? summaryApiKey : visionApiKey;
        String model = useSummaryEndpoint ? summaryModel : visionModel;

        log.info("Đang kiểm duyệt và phân loại chương trình học qua model: {}", model);

        Map<String, Object> requestBody = Map.of(
            "model",
            model,
            "messages",
            List.of(
                Map.of("role", "system", "content", CLASSIFICATION_SYSTEM_PROMPT),
                Map.of("role", "user", "content", "Đoạn trích tài liệu:\n\n" + sampleText)
            ),
            "temperature",
            0.1,
            "max_tokens",
            500
        );

        try {
            String rawResponse = restClient
                .post()
                .uri(endpoint)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);

            JsonNode rootNode = objectMapper.readTree(rawResponse);
            JsonNode choices = rootNode.path("choices");
            if (choices.isArray() && !choices.isEmpty()) {
                String replyContent = choices.get(0).path("message").path("content").asString();
                if (replyContent != null && !replyContent.isBlank()) {
                    return parseClassificationJson(replyContent);
                }
            }
            throw new IllegalStateException("Phản hồi phân loại không có nội dung: " + rawResponse);
        } catch (Exception e) {
            log.error("Lỗi khi kiểm duyệt nội dung tài liệu: {}", e.getMessage());
            return new LessonSummaryDTOs.CurriculumClassification(
                true,
                "TOAN",
                null,
                "Chưa xác định",
                "Không thể chạy kiểm duyệt tự động, mặc định cho phép: " + e.getMessage()
            );
        }
    }

    public LessonSummaryDTOs.CurriculumClassification classifyFile(byte[] fileBytes, String fileName, boolean forceOcr) {
        var extraction = extractDocument(fileBytes, fileName, forceOcr);
        return classifyCurriculum(extraction.content());
    }

    private LessonSummaryDTOs.CurriculumClassification parseClassificationJson(String rawText) {
        String clean = rawText.trim();
        if (clean.startsWith("```json")) {
            clean = clean.substring(7);
        } else if (clean.startsWith("```")) {
            clean = clean.substring(3);
        }
        if (clean.endsWith("```")) {
            clean = clean.substring(0, clean.length() - 3);
        }
        clean = clean.trim();

        try {
            JsonNode node = objectMapper.readTree(clean);
            boolean isValid = node.path("isValid").asBoolean(false);
            String subject = node.path("subject").asString("KHAC");
            Integer grade =
                node.has("grade") && !node.path("grade").isNull() && node.path("grade").isNumber() ? node.path("grade").asInt() : null;
            String topic = node.path("topic").asString("");
            String reason = node.path("reason").asString("");

            log.info("Kết quả kiểm duyệt: Môn={}, Lớp={}, Hợp lệ={}, Chủ đề={}", subject, grade, isValid, topic);
            return new LessonSummaryDTOs.CurriculumClassification(isValid, subject, grade, topic, reason);
        } catch (Exception e) {
            log.warn("Không thể parse JSON phân loại, phản hồi thô: {}", rawText);
            return new LessonSummaryDTOs.CurriculumClassification(true, "TOAN", null, "Không xác định", clean);
        }
    }

    // ==========================================
    // 3. BÓC TÁCH NỘI DUNG TÀI LIỆU (Extractor)
    // ==========================================

    public DocumentExtraction extractDocument(byte[] fileBytes, String fileName, boolean forceOcr) {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new IllegalArgumentException("File tải lên không có dữ liệu.");
        }

        String extension = getExtension(fileName);
        log.info(
            "Bắt đầu trích xuất file: {} (loại: {}, dung lượng: {} bytes, forceOcr: {})",
            fileName,
            extension,
            fileBytes.length,
            forceOcr
        );

        return switch (extension) {
            case "docx" -> extractDocx(fileBytes);
            case "pptx" -> extractPptx(fileBytes);
            case "pdf" -> extractPdf(fileBytes, forceOcr);
            case "png", "jpg", "jpeg", "webp" -> extractImage(fileBytes, extension);
            case "txt", "md" -> extractTextFile(fileBytes, extension);
            default -> throw new IllegalArgumentException(
                "Định dạng file không được hỗ trợ (" + extension + "). Vui lòng sử dụng .pdf, .docx, .pptx, hoặc ảnh .png/.jpg."
            );
        };
    }

    private DocumentExtraction extractDocx(byte[] bytes) {
        try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(bytes))) {
            StringBuilder sb = new StringBuilder();

            for (XWPFParagraph p : doc.getParagraphs()) {
                String text = p.getText();
                if (text != null && !text.isBlank()) {
                    sb.append(text).append("\n\n");
                }
            }

            for (XWPFTable table : doc.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    List<String> cells = new ArrayList<>();
                    for (XWPFTableCell cell : row.getTableCells()) {
                        cells.add(cell.getText().trim().replace("\n", " "));
                    }
                    sb.append("| ").append(String.join(" | ", cells)).append(" |\n");
                }
                sb.append("\n");
            }

            String content = sb.toString().trim();
            log.info("Trích xuất DOCX thành công: {} ký tự", content.length());
            return new DocumentExtraction(content, "docx", 1, false);
        } catch (Exception e) {
            log.error("Lỗi khi đọc file DOCX: ", e);
            throw new RuntimeException("Không thể đọc file Word (.docx): " + e.getMessage(), e);
        }
    }

    private DocumentExtraction extractPptx(byte[] bytes) {
        try (XMLSlideShow ppt = new XMLSlideShow(new ByteArrayInputStream(bytes))) {
            StringBuilder sb = new StringBuilder();
            int slideNum = 1;

            for (XSLFSlide slide : ppt.getSlides()) {
                sb.append("### Slide ").append(slideNum++).append("\n");
                for (XSLFShape shape : slide.getShapes()) {
                    if (shape instanceof XSLFTextShape textShape) {
                        String text = textShape.getText();
                        if (text != null && !text.isBlank()) {
                            sb.append(text).append("\n");
                        }
                    } else if (shape instanceof XSLFTable table) {
                        for (XSLFTableRow row : table.getRows()) {
                            List<String> cells = new ArrayList<>();
                            for (XSLFTableCell cell : row.getCells()) {
                                cells.add(cell.getText().trim().replace("\n", " "));
                            }
                            sb.append("| ").append(String.join(" | ", cells)).append(" |\n");
                        }
                        sb.append("\n");
                    }
                }
                sb.append("\n");
            }

            String content = sb.toString().trim();
            log.info("Trích xuất PPTX thành công: {} slide, {} ký tự", ppt.getSlides().size(), content.length());
            return new DocumentExtraction(content, "pptx", ppt.getSlides().size(), false);
        } catch (Exception e) {
            log.error("Lỗi khi đọc file PPTX: ", e);
            throw new RuntimeException("Không thể đọc file PowerPoint (.pptx): " + e.getMessage(), e);
        }
    }

    private DocumentExtraction extractPdf(byte[] bytes, boolean forceOcr) {
        try (PDDocument doc = Loader.loadPDF(bytes)) {
            int totalPages = doc.getNumberOfPages();
            PDFTextStripper stripper = new PDFTextStripper();
            String rawText = stripper.getText(doc);
            boolean hasText = rawText != null && !rawText.trim().isEmpty();

            if (!forceOcr && hasText && rawText.trim().length() >= totalPages * 30) {
                log.info("Trích xuất PDF dạng văn bản: {} trang, {} ký tự", totalPages, rawText.length());
                return new DocumentExtraction(rawText.trim(), "pdf", totalPages, false);
            }

            String effectiveKey = getEffectiveVisionApiKey();
            if (effectiveKey == null) {
                if (hasText) {
                    log.warn(
                        "Chưa cấu hình Vision Key, nhưng PDF có văn bản ({} ký tự). Bỏ qua Vision OCR và dùng text trực tiếp.",
                        rawText.trim().length()
                    );
                    return new DocumentExtraction(rawText.trim(), "pdf", totalPages, false);
                }
                throw new IllegalStateException(
                    "Tệp PDF này là dạng tài liệu scan (toàn bộ trang là ảnh chụp, không có văn bản số). " +
                        "Để AI đọc được chữ và công thức từ ảnh scan, vui lòng thêm biến môi trường GEMINI_API_KEY hoặc cấu hình API Key trong hệ thống."
                );
            }

            log.info("PDF ít text hoặc dạng scan ({} trang). Tiến hành Vision OCR từng trang...", totalPages);
            PDFRenderer renderer = new PDFRenderer(doc);
            StringBuilder ocrBuilder = new StringBuilder();

            try {
                int maxPages = Math.min(totalPages, 5);
                for (int i = 0; i < maxPages; i++) {
                    BufferedImage bImage = renderer.renderImageWithDPI(i, 130);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(bImage, "jpeg", baos);
                    byte[] pageImageBytes = baos.toByteArray();

                    log.info("Đang OCR trang {}/{}...", i + 1, maxPages);
                    String pageOcr = ocrImage(pageImageBytes, "image/jpeg");

                    ocrBuilder
                        .append("\n### Trang ")
                        .append(i + 1)
                        .append("\n");
                    ocrBuilder.append(pageOcr).append("\n");
                    if (i < maxPages - 1) {
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException ignored) {}
                    }
                }

                return new DocumentExtraction(ocrBuilder.toString().trim(), "pdf", totalPages, true);
            } catch (Exception ocrEx) {
                if (hasText) {
                    log.warn(
                        "Vision OCR gặp lỗi ({}), sử dụng nội dung văn bản trích xuất trực tiếp: {} ký tự",
                        ocrEx.getMessage(),
                        rawText.length()
                    );
                    return new DocumentExtraction(rawText.trim(), "pdf", totalPages, false);
                }
                throw ocrEx;
            }
        } catch (Exception e) {
            log.error("Lỗi khi xử lý file PDF: ", e);
            throw new RuntimeException("Không thể xử lý file PDF: " + e.getMessage(), e);
        }
    }

    private DocumentExtraction extractImage(byte[] bytes, String extension) {
        String mime = "png".equals(extension) ? "image/png" : "webp".equals(extension) ? "image/webp" : "image/jpeg";
        log.info("Trích xuất trực tiếp từ file ảnh ({}) qua Vision OCR...", mime);
        String text = ocrImage(bytes, mime);
        return new DocumentExtraction(text, extension, 1, true);
    }

    private DocumentExtraction extractTextFile(byte[] bytes, String extension) {
        String content = new String(bytes, StandardCharsets.UTF_8).trim();
        return new DocumentExtraction(content, extension, 1, false);
    }

    private String getEffectiveVisionApiKey() {
        if (
            visionApiKey != null &&
            !visionApiKey.isBlank() &&
            !"disabled".equalsIgnoreCase(visionApiKey) &&
            !"dummy".equalsIgnoreCase(visionApiKey)
        ) {
            return visionApiKey;
        }
        String envKey = System.getenv("UNIPASS_AI_GEMINI_API_KEY");
        if (envKey != null && !envKey.isBlank()) {
            return envKey;
        }
        String geminiEnv = System.getenv("GEMINI_API_KEY");
        if (geminiEnv != null && !geminiEnv.isBlank()) {
            return geminiEnv;
        }
        if (summaryBaseUrl.contains("api.openai.com") && summaryApiKey != null && !summaryApiKey.isBlank()) {
            return summaryApiKey;
        }
        return null;
    }

    private String getEffectiveVisionBaseUrl() {
        if (visionApiKey != null && !visionApiKey.isBlank() && !"disabled".equalsIgnoreCase(visionApiKey)) {
            return visionBaseUrl;
        }
        return summaryBaseUrl;
    }

    private String getEffectiveVisionModel() {
        if (visionApiKey != null && !visionApiKey.isBlank() && !"disabled".equalsIgnoreCase(visionApiKey)) {
            return visionModel;
        }
        if (summaryBaseUrl.contains("googleapis.com")) {
            return "gemini-1.5-flash";
        }
        if (summaryBaseUrl.contains("api.openai.com")) {
            return "gpt-4o-mini";
        }
        return visionModel;
    }

    // ==========================================
    // 4. VISION OCR (Gemini / OpenAI Vision)
    // ==========================================

    public String ocrImage(byte[] imageBytes, String mimeType) {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new IllegalArgumentException("Dữ liệu hình ảnh không được để trống.");
        }

        String effectiveKey = getEffectiveVisionApiKey();
        if (effectiveKey == null) {
            throw new IllegalStateException(
                "Chưa cấu hình API Key cho Vision OCR. Vui lòng thêm biến môi trường GEMINI_API_KEY hoặc cấu hình lumi.ai.vision.api-key trong r2-local.properties."
            );
        }

        String effectiveBaseUrl = getEffectiveVisionBaseUrl();
        String effectiveModel = getEffectiveVisionModel();

        String actualMime = mimeType != null && !mimeType.isBlank() ? mimeType : "image/jpeg";
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        String dataUrl = "data:" + actualMime + ";base64," + base64Image;

        String endpoint = buildChatEndpoint(effectiveBaseUrl);
        log.info("Đang gửi ảnh tới Vision OCR (Model: {}) tại: {}", effectiveModel, endpoint);

        Map<String, Object> textPart = Map.of("type", "text", "text", VISION_OCR_PROMPT);

        Map<String, Object> imagePart = Map.of("type", "image_url", "image_url", Map.of("url", dataUrl));

        Map<String, Object> requestBody = Map.of(
            "model",
            effectiveModel,
            "messages",
            List.of(Map.of("role", "user", "content", List.of(textPart, imagePart))),
            "temperature",
            0.2,
            "max_tokens",
            3000
        );

        try {
            String rawResponse = restClient
                .post()
                .uri(endpoint)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + effectiveKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);

            JsonNode rootNode = objectMapper.readTree(rawResponse);
            JsonNode choices = rootNode.path("choices");
            if (choices.isArray() && !choices.isEmpty()) {
                String content = choices.get(0).path("message").path("content").asString();
                if (content != null && !content.isBlank()) {
                    log.info("Vision OCR thành công: {} ký tự.", content.length());
                    return content;
                }
            }
            throw new IllegalStateException("Phản hồi Vision OCR không đúng định dạng: " + rawResponse);
        } catch (Exception e) {
            log.error("Lỗi khi gọi Vision OCR: {}", e.getMessage());
            throw new RuntimeException("Lỗi Vision OCR: " + e.getMessage(), e);
        }
    }

    // ==========================================
    // CÁC HÀM TIỆN ÍCH (Helpers)
    // ==========================================

    private String normalizeBaseUrl(String url) {
        if (url == null || url.isBlank()) return "https://api.openai.com/v1/";
        return url.endsWith("/") ? url : url + "/";
    }

    private String buildChatEndpoint(String baseUrl) {
        if (baseUrl.contains("/openai/")) {
            return baseUrl.endsWith("/") ? baseUrl + "chat/completions" : baseUrl + "/chat/completions";
        }
        String endpoint;
        if (baseUrl.endsWith("/v1/")) {
            endpoint = baseUrl + "chat/completions";
        } else if (baseUrl.endsWith("/")) {
            endpoint = baseUrl + "v1/chat/completions";
        } else {
            endpoint = baseUrl + "/v1/chat/completions";
        }
        return endpoint.replace("/v1/v1/", "/v1/");
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName
            .substring(fileName.lastIndexOf(".") + 1)
            .trim()
            .toLowerCase();
    }
}
