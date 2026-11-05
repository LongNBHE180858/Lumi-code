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
import com.lumi.service.dto.CatalogItemDTO;
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
    void createSubject() {
        when(subjects.findByNameIgnoreCase("Vật lý")).thenReturn(Optional.empty());
        when(subjects.save(any(Subject.class))).thenAnswer(call -> call.getArgument(0));

        CatalogItemDTO result = service.saveCatalog("subjects", null, new CatalogItemDTO(null, "Vật lý", "Môn học", null));

        assertThat(result.description()).isEqualTo("Môn học");
    }

    @Test
    void updatingSubjectKeepsItsOwnName() {
        Subject subject = new Subject().id(12L).name("Toán");
        when(subjects.findById(12L)).thenReturn(Optional.of(subject));
        when(subjects.findByNameIgnoreCase("Toán")).thenReturn(Optional.of(subject));
        when(subjects.save(subject)).thenReturn(subject);

        CatalogItemDTO result = service.saveCatalog("subjects", 12L, new CatalogItemDTO(null, "Toán", "Mô tả mới", null));

        assertThat(result.description()).isEqualTo("Mô tả mới");
    }

    @Test
    void rejectsGradeOutsideSupportedRange() {
        CatalogItemDTO input = new CatalogItemDTO(null, "Lớp 13", null, 13);

        assertThatThrownBy(() -> service.saveCatalog("grades", null, input))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
        verify(grades, never()).save(any(Grade.class));
    }

    @Test
    void rejectsDuplicateGradeLevel() {
        when(grades.findByLevel(2)).thenReturn(Optional.of(new Grade().id(4L).level(2).name("Lớp 2")));
        CatalogItemDTO input = new CatalogItemDTO(null, "Khối 2", null, 2);

        assertThatThrownBy(() -> service.saveCatalog("grades", null, input))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void rejectsUnknownCatalogKind() {
        assertThatThrownBy(() -> service.getCatalog("unknown"))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void rejectsRemovedTagCatalogKind() {
        assertThatThrownBy(() -> service.getCatalog("tags"))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }
}
