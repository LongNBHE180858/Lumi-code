package com.lumi.service;

import com.lumi.domain.*;
import com.lumi.repository.*;
import com.lumi.security.SecurityUtils;
import com.lumi.service.dto.studyClass.QuestionBankDTO;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class QuestionBankService {

    private final QuestionBankRepository questionBankRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final QuestionKnowledgeLinkRepository questionKnowledgeLinkRepository;
    private final KnowledgeNodeRepository knowledgeNodeRepository;
    private final StudyClassRepository studyClassRepository;
    private final UserRepository userRepository;

    public QuestionBankService(
        QuestionBankRepository questionBankRepository,
        QuestionRepository questionRepository,
        QuestionOptionRepository questionOptionRepository,
        QuestionKnowledgeLinkRepository questionKnowledgeLinkRepository,
        KnowledgeNodeRepository knowledgeNodeRepository,
        StudyClassRepository studyClassRepository,
        UserRepository userRepository
    ) {
        this.questionBankRepository = questionBankRepository;
        this.questionRepository = questionRepository;
        this.questionOptionRepository = questionOptionRepository;
        this.questionKnowledgeLinkRepository = questionKnowledgeLinkRepository;
        this.knowledgeNodeRepository = knowledgeNodeRepository;
        this.studyClassRepository = studyClassRepository;
        this.userRepository = userRepository;
    }

    private User currentInstructor() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        return userRepository
            .findOneByLogin(login)
            .filter(User::isActivated)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    private QuestionBank getOwnedBank(Long bankId, User instructor) {
        QuestionBank bank = questionBankRepository
            .findById(bankId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy ngân hàng câu hỏi"));
        if (!Objects.equals(bank.getInstructor().getId(), instructor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền quản lý ngân hàng này");
        }
        return bank;
    }

    @Transactional(readOnly = true)
    public List<QuestionBankDTO.BankSummary> listBanks(Long classId) {
        User instructor = currentInstructor();
        List<QuestionBank> banks;
        if (classId != null) {
            banks = questionBankRepository.findAccessibleBanks(instructor.getId(), classId);
        } else {
            banks = questionBankRepository.findByInstructorIdOrderByUpdatedAtDesc(instructor.getId());
        }
        List<QuestionBankDTO.BankSummary> result = new ArrayList<>();
        for (QuestionBank bank : banks) {
            long count = questionRepository.countByBankIdAndIsDeletedFalse(bank.getId());
            Long bankClassId = bank.getStudyClass() != null ? bank.getStudyClass().getId() : null;
            String bankClassName = bank.getStudyClass() != null ? bank.getStudyClass().getName() : null;
            result.add(
                new QuestionBankDTO.BankSummary(bank.getId(), bank.getName(), bankClassId, bankClassName, count, bank.getUpdatedAt())
            );
        }
        return result;
    }

    public QuestionBankDTO.BankSummary createBank(QuestionBankDTO.CreateBank input) {
        User instructor = currentInstructor();
        StudyClass studyClass = null;
        if (input.classId() != null) {
            studyClass = studyClassRepository
                .findById(input.classId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lớp học"));
            if (!Objects.equals(studyClass.getInstructor().getId(), instructor.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không quản lý lớp học này");
            }
        }
        Instant now = Instant.now();
        QuestionBank bank = new QuestionBank()
            .name(input.name().trim())
            .visibility("PRIVATE")
            .instructor(instructor)
            .studyClass(studyClass)
            .createdAt(now)
            .updatedAt(now);
        bank = questionBankRepository.save(bank);
        Long classId = studyClass != null ? studyClass.getId() : null;
        String className = studyClass != null ? studyClass.getName() : null;
        return new QuestionBankDTO.BankSummary(bank.getId(), bank.getName(), classId, className, 0, now);
    }

    @Transactional(readOnly = true)
    public List<QuestionBankDTO.QuestionItem> listQuestions(Long bankId, String topic, Integer difficulty) {
        User instructor = currentInstructor();
        getOwnedBank(bankId, instructor);

        List<Question> questions = questionRepository.findByBankIdAndIsDeletedFalseOrderByCreatedAtDesc(bankId);
        List<QuestionKnowledgeLink> links = questionKnowledgeLinkRepository.findByBankId(bankId);
        Map<Long, String> questionTopicMap = new HashMap<>();
        for (QuestionKnowledgeLink link : links) {
            if (link.getKnowledgeNode() != null) {
                questionTopicMap.put(link.getQuestion().getId(), link.getKnowledgeNode().getTitle());
            }
        }

        List<QuestionBankDTO.QuestionItem> result = new ArrayList<>();
        for (Question q : questions) {
            String questionTopic = questionTopicMap.getOrDefault(q.getId(), "Mặc định");
            if (difficulty != null && !Objects.equals(q.getDifficulty(), difficulty)) {
                continue;
            }
            if (topic != null && !topic.isBlank() && !topic.equalsIgnoreCase("ALL") && !questionTopic.equalsIgnoreCase(topic.trim())) {
                continue;
            }
            List<QuestionOption> options = questionOptionRepository.findByQuestionIdOrderByOrderIndexAsc(q.getId());
            List<QuestionBankDTO.OptionItem> optionItems = new ArrayList<>();
            for (QuestionOption opt : options) {
                optionItems.add(
                    new QuestionBankDTO.OptionItem(
                        opt.getId(),
                        opt.getContent(),
                        Boolean.TRUE.equals(opt.getIsCorrect()),
                        opt.getOrderIndex()
                    )
                );
            }
            result.add(
                new QuestionBankDTO.QuestionItem(
                    q.getId(),
                    q.getContent(),
                    q.getDifficulty(),
                    questionTopic,
                    q.getExplanation(),
                    optionItems
                )
            );
        }
        return result;
    }

    public QuestionBankDTO.QuestionItem addQuestion(Long bankId, QuestionBankDTO.CreateQuestion input) {
        User instructor = currentInstructor();
        QuestionBank bank = getOwnedBank(bankId, instructor);

        if (questionRepository.existsByBankIdAndContentIgnoreCaseAndIsDeletedFalse(bankId, input.content().trim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Câu hỏi này đã có trong ngân hàng");
        }
        if (input.correctIndex() < 0 || input.correctIndex() >= input.options().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Đáp án đúng không hợp lệ");
        }

        Instant now = Instant.now();
        Question question = new Question()
            .bank(bank)
            .content(input.content().trim())
            .type("SINGLE_CHOICE")
            .difficulty(input.difficulty() != null ? input.difficulty() : 1)
            .explanation(input.explanation() != null ? input.explanation().trim() : "")
            .sourceType("INSTRUCTOR")
            .visibility("PRIVATE")
            .version(1L)
            .isDeleted(false)
            .createdAt(now)
            .updatedAt(now)
            .createdBy(instructor);
        question = questionRepository.saveAndFlush(question);

        List<QuestionBankDTO.OptionItem> optionItems = new ArrayList<>();
        for (int i = 0; i < input.options().size(); i++) {
            boolean isCorrect = i == input.correctIndex();
            QuestionOption opt = new QuestionOption()
                .question(question)
                .content(input.options().get(i).trim())
                .orderIndex(i)
                .isCorrect(isCorrect);
            opt = questionOptionRepository.save(opt);
            optionItems.add(new QuestionBankDTO.OptionItem(opt.getId(), opt.getContent(), isCorrect, i));
        }

        String topicName = input.topic() != null && !input.topic().isBlank() ? input.topic().trim() : "Mặc định";
        KnowledgeNode node = knowledgeNodeRepository.findFirstByTitleIgnoreCase(topicName).orElseGet(() -> {
            KnowledgeNode newNode = new KnowledgeNode().title(topicName).type("TOPIC");
            return knowledgeNodeRepository.save(newNode);
        });
        QuestionKnowledgeLink link = new QuestionKnowledgeLink().question(question).knowledgeNode(node).weight(BigDecimal.ONE);
        questionKnowledgeLinkRepository.save(link);

        bank.setUpdatedAt(now);
        questionBankRepository.save(bank);

        return new QuestionBankDTO.QuestionItem(
            question.getId(),
            question.getContent(),
            question.getDifficulty(),
            topicName,
            question.getExplanation(),
            optionItems
        );
    }

    @Transactional(readOnly = true)
    public byte[] generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Mau_Cau_Hoi");

            // Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            Row header = sheet.createRow(0);
            String[] headers = {
                "Nội dung câu hỏi (*)",
                "Lựa chọn A (*)",
                "Lựa chọn B (*)",
                "Lựa chọn C",
                "Lựa chọn D",
                "Đáp án đúng (A/B/C/D) (*)",
                "Độ khó (1-5) (*)",
                "Chủ đề",
                "Giải thích",
            };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Dòng mẫu 1
            Row row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("Thủ đô của Việt Nam là thành phố nào?");
            row1.createCell(1).setCellValue("Hà Nội");
            row1.createCell(2).setCellValue("TP. Hồ Chí Minh");
            row1.createCell(3).setCellValue("Đà Nẵng");
            row1.createCell(4).setCellValue("Hải Phòng");
            row1.createCell(5).setCellValue("A");
            row1.createCell(6).setCellValue(1);
            row1.createCell(7).setCellValue("Địa lý");
            row1.createCell(8).setCellValue("Hà Nội là thủ đô của Việt Nam.");

            // Dòng mẫu 2
            Row row2 = sheet.createRow(2);
            row2.createCell(0).setCellValue("Kết quả của phép tính: 5 + 3 * 2 là bao nhiêu?");
            row2.createCell(1).setCellValue("16");
            row2.createCell(2).setCellValue("11");
            row2.createCell(3).setCellValue("13");
            row2.createCell(4).setCellValue("10");
            row2.createCell(5).setCellValue("B");
            row2.createCell(6).setCellValue(2);
            row2.createCell(7).setCellValue("Toán học");
            row2.createCell(8).setCellValue("Nhân chia trước cộng trừ sau: 3 * 2 = 6; 5 + 6 = 11.");

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể tạo file mẫu Excel", ex);
        }
    }

    private String getCellString(Row row, int colIndex) {
        Cell cell = row.getCell(colIndex);
        if (cell == null) return "";
        if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue().trim();
        if (cell.getCellType() == CellType.NUMERIC) {
            double val = cell.getNumericCellValue();
            if (val == (long) val) return String.valueOf((long) val);
            return String.valueOf(val);
        }
        if (cell.getCellType() == CellType.BOOLEAN) return String.valueOf(cell.getBooleanCellValue());
        return "";
    }

    public QuestionBankDTO.ImportResult importExcel(Long bankId, MultipartFile file) {
        User instructor = currentInstructor();
        QuestionBank bank = getOwnedBank(bankId, instructor);

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File Excel trống");
        }

        List<QuestionBankDTO.ImportError> errors = new ArrayList<>();
        record ParsedRow(
            int rowNum,
            String content,
            List<String> options,
            int correctIndex,
            int difficulty,
            String topic,
            String explanation
        ) {}
        List<ParsedRow> validRows = new ArrayList<>();
        Set<String> fileSeenContents = new HashSet<>();

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            int lastRow = sheet.getLastRowNum();

            for (int r = 1; r <= lastRow; r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                String content = getCellString(row, 0);
                String optA = getCellString(row, 1);
                String optB = getCellString(row, 2);
                String optC = getCellString(row, 3);
                String optD = getCellString(row, 4);
                String correctStr = getCellString(row, 5).toUpperCase();
                String diffStr = getCellString(row, 6);
                String topic = getCellString(row, 7);
                String explanation = getCellString(row, 8);

                // Nếu cả dòng đều trống thì bỏ qua
                if (content.isBlank() && optA.isBlank() && optB.isBlank()) continue;

                int rowDisplay = r + 1;
                if (content.isBlank()) {
                    errors.add(new QuestionBankDTO.ImportError(rowDisplay, "Nội dung câu hỏi không được để trống"));
                    continue;
                }
                if (optA.isBlank() || optB.isBlank()) {
                    errors.add(new QuestionBankDTO.ImportError(rowDisplay, "Cần ít nhất 2 lựa chọn A và B"));
                    continue;
                }

                List<String> options = new ArrayList<>();
                options.add(optA);
                options.add(optB);
                if (!optC.isBlank()) options.add(optC);
                if (!optD.isBlank()) options.add(optD);

                int correctIndex = -1;
                if ("A".equals(correctStr)) correctIndex = 0;
                else if ("B".equals(correctStr)) correctIndex = 1;
                else if ("C".equals(correctStr) && options.size() >= 3) correctIndex = 2;
                else if ("D".equals(correctStr) && options.size() >= 4) correctIndex = 3;

                if (correctIndex < 0) {
                    errors.add(
                        new QuestionBankDTO.ImportError(
                            rowDisplay,
                            "Đáp án đúng phải là chữ cái (A, B, C, D) tương ứng với lựa chọn có nội dung"
                        )
                    );
                    continue;
                }

                int difficulty = 1;
                try {
                    difficulty = Integer.parseInt(diffStr);
                    if (difficulty < 1 || difficulty > 5) {
                        errors.add(new QuestionBankDTO.ImportError(rowDisplay, "Độ khó phải từ 1 đến 5"));
                        continue;
                    }
                } catch (Exception e) {
                    errors.add(new QuestionBankDTO.ImportError(rowDisplay, "Độ khó phải là số từ 1 đến 5"));
                    continue;
                }

                String lowerContent = content.toLowerCase();
                if (fileSeenContents.contains(lowerContent)) {
                    errors.add(new QuestionBankDTO.ImportError(rowDisplay, "Câu hỏi bị trùng với một dòng khác trong file"));
                    continue;
                }
                fileSeenContents.add(lowerContent);

                if (questionRepository.existsByBankIdAndContentIgnoreCaseAndIsDeletedFalse(bankId, content)) {
                    errors.add(new QuestionBankDTO.ImportError(rowDisplay, "Câu hỏi đã có sẵn trong ngân hàng"));
                    continue;
                }

                validRows.add(
                    new ParsedRow(rowDisplay, content, options, correctIndex, difficulty, topic.isBlank() ? "Mặc định" : topic, explanation)
                );
            }
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không đọc được file Excel: " + ex.getMessage());
        }

        // Quy tắc: Nếu có lỗi bất kỳ thì KHÔNG LƯU MỘT PHẦN
        if (!errors.isEmpty()) {
            return new QuestionBankDTO.ImportResult(0, errors);
        }

        Instant now = Instant.now();
        for (ParsedRow item : validRows) {
            Question question = new Question()
                .bank(bank)
                .content(item.content())
                .type("SINGLE_CHOICE")
                .difficulty(item.difficulty())
                .explanation(item.explanation())
                .sourceType("INSTRUCTOR")
                .visibility("PRIVATE")
                .version(1L)
                .isDeleted(false)
                .createdAt(now)
                .updatedAt(now)
                .createdBy(instructor);
            question = questionRepository.saveAndFlush(question);

            for (int i = 0; i < item.options().size(); i++) {
                boolean isCorrect = i == item.correctIndex();
                QuestionOption opt = new QuestionOption()
                    .question(question)
                    .content(item.options().get(i))
                    .orderIndex(i)
                    .isCorrect(isCorrect);
                questionOptionRepository.save(opt);
            }

            String topicName = item.topic();
            KnowledgeNode node = knowledgeNodeRepository.findFirstByTitleIgnoreCase(topicName).orElseGet(() -> {
                KnowledgeNode newNode = new KnowledgeNode().title(topicName).type("TOPIC");
                return knowledgeNodeRepository.save(newNode);
            });
            QuestionKnowledgeLink link = new QuestionKnowledgeLink().question(question).knowledgeNode(node).weight(BigDecimal.ONE);
            questionKnowledgeLinkRepository.save(link);
        }

        bank.setUpdatedAt(now);
        questionBankRepository.save(bank);

        return new QuestionBankDTO.ImportResult(validRows.size(), List.of());
    }

    @Transactional(readOnly = true)
    public List<QuestionBankDTO.TopicSummary> getTopicsSummary(Long bankId) {
        User instructor = currentInstructor();
        getOwnedBank(bankId, instructor);

        List<Question> questions = questionRepository.findByBankIdAndIsDeletedFalseOrderByCreatedAtDesc(bankId);
        List<QuestionKnowledgeLink> links = questionKnowledgeLinkRepository.findByBankId(bankId);
        Map<Long, String> questionTopicMap = new HashMap<>();
        for (QuestionKnowledgeLink link : links) {
            if (link.getKnowledgeNode() != null) {
                questionTopicMap.put(link.getQuestion().getId(), link.getKnowledgeNode().getTitle());
            }
        }

        Map<String, int[]> stats = new LinkedHashMap<>();
        for (Question q : questions) {
            String topic = questionTopicMap.getOrDefault(q.getId(), "Mặc định");
            int diff = q.getDifficulty() != null && q.getDifficulty() >= 1 && q.getDifficulty() <= 5 ? q.getDifficulty() : 1;
            stats.computeIfAbsent(topic, k -> new int[6]);
            stats.get(topic)[0]++; // Tổng
            stats.get(topic)[diff]++; // Theo độ khó
        }

        List<QuestionBankDTO.TopicSummary> result = new ArrayList<>();
        for (Map.Entry<String, int[]> entry : stats.entrySet()) {
            int[] d = entry.getValue();
            result.add(new QuestionBankDTO.TopicSummary(entry.getKey(), d[0], d[1], d[2], d[3], d[4], d[5]));
        }
        return result;
    }
}
