package com.lumi.service.mapper;

import com.lumi.domain.*;
import com.lumi.repository.ExamQuestionRepository;
import com.lumi.repository.QuestionOptionRepository;
import com.lumi.service.dto.InstructorExamDTOs.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ExamMapper {

    private final ExamQuestionRepository examQuestions;
    private final QuestionOptionRepository questionOptions;

    public ExamMapper(ExamQuestionRepository examQuestions, QuestionOptionRepository questionOptions) {
        this.examQuestions = examQuestions;
        this.questionOptions = questionOptions;
    }

    public ExamSummaryDTO toSummaryDto(Exam exam) {
        List<ExamQuestion> eqList = examQuestions.findByExamIdOrderByOrderIndexAsc(exam.getId());
        return toSummaryDto(exam, eqList);
    }

    public ExamSummaryDTO toSummaryDto(Exam exam, List<ExamQuestion> eqList) {
        BigDecimal totalScore =
            eqList != null ? eqList.stream().map(ExamQuestion::getScore).reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO;
        int questionCount = eqList != null ? eqList.size() : 0;

        return new ExamSummaryDTO(
            exam.getId(),
            exam.getTitle(),
            exam.getAssessmentType(),
            exam.getDurationMinutes(),
            exam.getPassScore(),
            exam.getMaxAttempts(),
            exam.getShuffleQuestions(),
            exam.getVideoTriggerSeconds(),
            exam.getCourse() != null ? exam.getCourse().getId() : null,
            exam.getChapter() != null ? exam.getChapter().getId() : null,
            exam.getLesson() != null ? exam.getLesson().getId() : null,
            questionCount,
            totalScore,
            exam.getCreatedAt()
        );
    }

    public ExamDetailDTO toDetailDto(Exam exam) {
        List<ExamQuestion> eqList = examQuestions.findByExamIdOrderByOrderIndexAsc(exam.getId());
        List<QuestionDetailDTO> questionDTOs = new ArrayList<>();

        for (ExamQuestion eq : eqList) {
            Question q = eq.getQuestion();
            List<QuestionOption> options = questionOptions.findByQuestionIdOrderByOrderIndexAsc(q.getId());
            List<OptionDTO> optDTOs = options
                .stream()
                .map(o -> new OptionDTO(o.getId(), o.getContent(), o.getIsCorrect(), o.getOrderIndex()))
                .toList();

            questionDTOs.add(
                new QuestionDetailDTO(
                    q.getId(),
                    q.getContent(),
                    q.getType(),
                    q.getDifficulty(),
                    q.getExplanation(),
                    eq.getScore(),
                    eq.getOrderIndex(),
                    optDTOs
                )
            );
        }

        return toDetailDto(exam, questionDTOs);
    }

    public ExamDetailDTO toDetailDto(Exam exam, List<QuestionDetailDTO> questions) {
        return new ExamDetailDTO(
            exam.getId(),
            exam.getTitle(),
            exam.getAssessmentType(),
            exam.getDurationMinutes(),
            exam.getPassScore(),
            exam.getMaxAttempts(),
            exam.getShuffleQuestions(),
            exam.getVideoTriggerSeconds(),
            exam.getCourse() != null ? exam.getCourse().getId() : null,
            exam.getChapter() != null ? exam.getChapter().getId() : null,
            exam.getLesson() != null ? exam.getLesson().getId() : null,
            questions,
            exam.getCreatedAt()
        );
    }
}
