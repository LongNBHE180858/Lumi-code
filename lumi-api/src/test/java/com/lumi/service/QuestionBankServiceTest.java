package com.lumi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lumi.domain.Authority;
import com.lumi.domain.KnowledgeNode;
import com.lumi.domain.Question;
import com.lumi.domain.QuestionBank;
import com.lumi.domain.QuestionOption;
import com.lumi.domain.User;
import com.lumi.repository.KnowledgeNodeRepository;
import com.lumi.repository.QuestionBankRepository;
import com.lumi.repository.QuestionKnowledgeLinkRepository;
import com.lumi.repository.QuestionOptionRepository;
import com.lumi.repository.QuestionRepository;
import com.lumi.repository.StudyClassRepository;
import com.lumi.repository.UserRepository;
import com.lumi.security.AuthoritiesConstants;
import com.lumi.service.dto.studyClass.QuestionBankDTO;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class QuestionBankServiceTest {

    @Mock
    QuestionBankRepository banks;

    @Mock
    QuestionRepository questions;

    @Mock
    QuestionOptionRepository options;

    @Mock
    QuestionKnowledgeLinkRepository links;

    @Mock
    KnowledgeNodeRepository knowledgeNodes;

    @Mock
    StudyClassRepository classes;

    @Mock
    UserRepository users;

    @InjectMocks
    QuestionBankService service;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void editingCreatesNewVersionAndPreservesQuestionUsedByExistingExams() {
        User instructor = new User();
        instructor.setId(7L);
        instructor.setLogin("instructor");
        instructor.setActivated(true);
        instructor.setAuthorities(Set.of(new Authority().name(AuthoritiesConstants.INSTRUCTOR)));
        QuestionBank bank = new QuestionBank().id(3L).instructor(instructor);
        Question previous = new Question().id(9L).bank(bank).content("Câu cũ").version(1L).isDeleted(false);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("instructor", "unused"));
        when(users.findOneByLogin("instructor")).thenReturn(Optional.of(instructor));
        when(banks.findById(3L)).thenReturn(Optional.of(bank));
        when(questions.findByIdForUpdate(9L)).thenReturn(Optional.of(previous));
        when(questions.saveAndFlush(any(Question.class))).thenAnswer(invocation -> {
            Question question = invocation.getArgument(0);
            question.setId(10L);
            return question;
        });
        when(options.save(any(QuestionOption.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(knowledgeNodes.findFirstByTitleIgnoreCase("Phân số")).thenReturn(Optional.of(new KnowledgeNode().title("Phân số")));

        var result = service.updateQuestion(
            3L,
            9L,
            new QuestionBankDTO.CreateQuestion("Câu mới", 2, "Phân số", "Giải thích", List.of("A", "B", "C"), 1)
        );

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.options()).hasSize(3);
        assertThat(result.options().get(1).isCorrect()).isTrue();
        assertThat(previous.getIsDeleted()).isTrue();
        org.mockito.ArgumentCaptor<Question> saved = org.mockito.ArgumentCaptor.forClass(Question.class);
        verify(questions).saveAndFlush(saved.capture());
        assertThat(saved.getValue().getPreviousQuestion()).isSameAs(previous);
        assertThat(saved.getValue().getVersion()).isEqualTo(2L);
    }

    @Test
    void refusesToEditAQuestionFromAnotherBank() {
        User instructor = new User();
        instructor.setId(7L);
        instructor.setLogin("instructor");
        instructor.setActivated(true);
        QuestionBank bank = new QuestionBank().id(3L).instructor(instructor);
        Question otherQuestion = new Question().id(9L).bank(new QuestionBank().id(4L)).version(1L).isDeleted(false);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("instructor", "unused"));
        when(users.findOneByLogin("instructor")).thenReturn(Optional.of(instructor));
        when(banks.findById(3L)).thenReturn(Optional.of(bank));
        when(questions.findByIdForUpdate(9L)).thenReturn(Optional.of(otherQuestion));

        assertThatThrownBy(() ->
            service.updateQuestion(3L, 9L, new QuestionBankDTO.CreateQuestion("Câu mới", 1, null, null, List.of("A", "B"), 0))
        ).isInstanceOfSatisfying(ResponseStatusException.class, error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
        verify(questions, never()).saveAndFlush(any(Question.class));
    }
}
