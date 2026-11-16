package com.lumi.service.util;

import com.lumi.domain.*;
import com.lumi.domain.enumeration.RecordStatus;
import com.lumi.repository.QuestionOptionRepository;
import com.lumi.service.dto.InstructorExamDTOs.CreateExamInput;
import com.lumi.service.dto.InstructorExamDTOs.OptionInput;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class InstructorCourseHelper {

    public static Exam initBaseExam(CreateExamInput input, String assessmentType, User author) {
        Exam exam = new Exam();
        exam.setTitle(input.title().trim());
        exam.setAssessmentType(assessmentType);
        exam.setDurationMinutes(input.durationMinutes() != null ? input.durationMinutes() : 0);
        exam.setPassScore(input.passScore() != null ? input.passScore() : BigDecimal.valueOf(60.00));
        exam.setMaxAttempts(input.maxAttempts() != null ? input.maxAttempts() : 1);
        exam.setShuffleQuestions(Boolean.TRUE.equals(input.shuffleQuestions()));
        exam.setResultReleaseMode("ON_SUBMIT");
        exam.setAnswerReleaseMode("ON_SUBMIT");
        exam.setAssessmentConfig("{}");
        exam.setSecurityConfig("{}");
        exam.setStatus(RecordStatus.ACTIVE);
        exam.setIsDeleted(false);
        exam.setVersion(0L);
        exam.setCreatedBy(author);
        exam.setCreatedAt(Instant.now());
        return exam;
    }

    public static void validateOptions(List<OptionInput> options) {
        if (options == null || options.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one option is required");
        }
        boolean hasCorrect = options.stream().anyMatch(o -> Boolean.TRUE.equals(o.isCorrect()));
        if (!hasCorrect) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one option must be marked as correct");
        }
    }

    public static void saveOptions(Question question, List<OptionInput> options, QuestionOptionRepository questionOptions) {
        for (int i = 0; i < options.size(); i++) {
            OptionInput opt = options.get(i);
            QuestionOption option = new QuestionOption();
            option.setContent(opt.content().trim());
            option.setIsCorrect(Boolean.TRUE.equals(opt.isCorrect()));
            option.setOrderIndex(opt.orderIndex() != null ? opt.orderIndex() : i);
            option.setQuestion(question);
            questionOptions.saveAndFlush(option);
        }
    }

    public static ResponseStatusException missing(String entity) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, entity + " not found");
    }

    public static ResponseStatusException conflict(String message) {
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }

    public static ResponseStatusException invalid(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
