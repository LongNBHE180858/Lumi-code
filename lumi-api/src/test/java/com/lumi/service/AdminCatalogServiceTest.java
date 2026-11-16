package com.lumi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lumi.domain.Grade;
import com.lumi.domain.Subject;
import com.lumi.repository.GradeRepository;
import com.lumi.repository.SubjectRepository;
import com.lumi.service.dto.GradeDTO;
import com.lumi.service.dto.SubjectDTO;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AdminCatalogServiceTest {

    @Mock
    private SubjectRepository subjects;

    @Mock
    private GradeRepository grades;

    @InjectMocks
    private AdminService service;

    @Test
    void listsSubjectsAndGradesInTheirOwnOrder() {
        when(subjects.findAll()).thenReturn(List.of(new Subject().id(2L).name("Vật lý"), new Subject().id(1L).name("Toán")));
        when(grades.findAll()).thenReturn(List.of(new Grade().id(2L).name("Lớp 12").level(12), new Grade().id(1L).name("Lớp 1").level(1)));

        assertThat(service.getCatalogSubjects()).extracting(SubjectDTO::getName).containsExactly("Toán", "Vật lý");
        assertThat(service.getCatalogGrades()).extracting(GradeDTO::getLevel).containsExactly(1, 12);
    }

    @Test
    void createsSubjectWithTrimmedName() {
        when(subjects.save(any(Subject.class))).thenAnswer(call -> call.getArgument(0));
        SubjectDTO input = subject(" Vật lý ", "Môn học");

        SubjectDTO result = service.saveCatalogSubject(null, input);

        assertThat(result.getName()).isEqualTo("Vật lý");
        assertThat(result.getDescription()).isEqualTo("Môn học");
    }

    @Test
    void rejectsDuplicateSubjectNameIgnoringCase() {
        when(subjects.findByNameIgnoreCase("toán")).thenReturn(Optional.of(new Subject().id(1L).name("Toán")));

        assertStatus(HttpStatus.CONFLICT, () -> service.saveCatalogSubject(null, subject("toán", null)));
        verify(subjects, never()).save(any(Subject.class));
    }

    @Test
    void updatesSubjectWithoutRejectingItsOwnName() {
        Subject existing = new Subject().id(12L).name("Toán");
        when(subjects.findById(12L)).thenReturn(Optional.of(existing));
        when(subjects.findByNameIgnoreCase("Toán")).thenReturn(Optional.of(existing));
        when(subjects.save(existing)).thenReturn(existing);

        SubjectDTO result = service.saveCatalogSubject(12L, subject("Toán", "Mô tả mới"));

        assertThat(result.getDescription()).isEqualTo("Mô tả mới");
    }

    @Test
    void acceptsGradeTwelve() {
        when(grades.save(any(Grade.class))).thenAnswer(call -> call.getArgument(0));

        GradeDTO result = service.saveCatalogGrade(null, grade("Lớp 12", 12));

        assertThat(result.getLevel()).isEqualTo(12);
    }

    @Test
    void rejectsGradeOutsideSupportedRange() {
        assertStatus(HttpStatus.BAD_REQUEST, () -> service.saveCatalogGrade(null, grade("Lớp 13", 13)));
        verify(grades, never()).save(any(Grade.class));
    }

    @Test
    void rejectsDuplicateGradeLevel() {
        when(grades.findByLevel(2)).thenReturn(Optional.of(new Grade().id(4L).level(2).name("Lớp 2")));

        assertStatus(HttpStatus.CONFLICT, () -> service.saveCatalogGrade(null, grade("Khối 2", 2)));
    }

    private SubjectDTO subject(String name, String description) {
        SubjectDTO dto = new SubjectDTO();
        dto.setName(name);
        dto.setDescription(description);
        return dto;
    }

    private GradeDTO grade(String name, int level) {
        GradeDTO dto = new GradeDTO();
        dto.setName(name);
        dto.setLevel(level);
        return dto;
    }

    private void assertStatus(HttpStatus status, Runnable action) {
        assertThatThrownBy(action::run)
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(status));
    }
}
