package com.lumi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lumi.domain.Category;
import com.lumi.domain.Grade;
import com.lumi.domain.Subject;
import com.lumi.repository.CategoryRepository;
import com.lumi.repository.GradeRepository;
import com.lumi.repository.SubjectRepository;
import com.lumi.service.dto.CatalogItemDTO;
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

    @Mock
    private CategoryRepository categories;

    @InjectMocks
    private AdminService service;

    @Test
    void deactivateSubjectWithoutDeletingIt() {
        Subject subject = new Subject().id(12L).name("Toán");
        when(subjects.findById(12L)).thenReturn(Optional.of(subject));

        CatalogItemDTO result = service.setCatalogActive("subjects", 12L, false);

        assertThat(result.active()).isFalse();
        assertThat(subject.getActive()).isFalse();
    }

    @Test
    void createSubjectActiveByDefault() {
        when(subjects.findByNameIgnoreCase("Vật lý")).thenReturn(Optional.empty());
        when(subjects.save(any(Subject.class))).thenAnswer(call -> call.getArgument(0));

        CatalogItemDTO result = service.saveCatalog("subjects", null, new CatalogItemDTO(null, "Vật lý", "Môn học", null, null, null));

        assertThat(result.active()).isTrue();
        assertThat(result.description()).isEqualTo("Môn học");
    }

    @Test
    void updatingSubjectKeepsItsOwnName() {
        Subject subject = new Subject().id(12L).name("Toán");
        when(subjects.findById(12L)).thenReturn(Optional.of(subject));
        when(subjects.findByNameIgnoreCase("Toán")).thenReturn(Optional.of(subject));
        when(subjects.save(subject)).thenReturn(subject);

        CatalogItemDTO result = service.saveCatalog("subjects", 12L, new CatalogItemDTO(null, "Toán", "Mô tả mới", null, null, null));

        assertThat(result.description()).isEqualTo("Mô tả mới");
    }

    @Test
    void rejectsGradeOutsideSupportedRange() {
        CatalogItemDTO input = new CatalogItemDTO(null, "Lớp 13", null, 13, null, null);

        assertThatThrownBy(() -> service.saveCatalog("grades", null, input))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
        verify(grades, never()).save(any(Grade.class));
    }

    @Test
    void rejectsDuplicateGradeLevel() {
        when(grades.findByLevel(2)).thenReturn(Optional.of(new Grade().id(4L).level(2).name("Lớp 2")));
        CatalogItemDTO input = new CatalogItemDTO(null, "Khối 2", null, 2, null, null);

        assertThatThrownBy(() -> service.saveCatalog("grades", null, input))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void rejectsInactiveParentCategory() {
        Category parent = new Category().id(7L).name("Parent");
        parent.setActive(false);
        when(categories.findById(7L)).thenReturn(Optional.of(parent));
        CatalogItemDTO input = new CatalogItemDTO(null, "Child", null, null, 7L, null);

        assertThatThrownBy(() -> service.saveCatalog("categories", null, input))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
        verify(categories, never()).save(any(Category.class));
    }

    @Test
    void rejectsCategoryParentCycle() {
        Category category = new Category().id(1L).name("Current");
        Category child = new Category().id(2L).name("Child").parent(category);
        when(categories.findById(1L)).thenReturn(Optional.of(category));
        when(categories.findById(2L)).thenReturn(Optional.of(child));
        CatalogItemDTO input = new CatalogItemDTO(null, "Current", null, null, 2L, null);

        assertThatThrownBy(() -> service.saveCatalog("categories", 1L, input))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
        verify(categories, never()).save(any(Category.class));
    }

    @Test
    void cannotSuspendCategoryWithActiveChildren() {
        Category parent = new Category().id(1L).name("Parent");
        when(categories.findById(1L)).thenReturn(Optional.of(parent));
        when(categories.existsByParentIdAndActiveTrue(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.setCatalogActive("categories", 1L, false))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
        assertThat(parent.getActive()).isTrue();
    }

    @Test
    void listsCategoriesByName() {
        Category history = new Category().id(2L).name("Lịch sử");
        Category math = new Category().id(1L).name("Toán");
        when(categories.findAll()).thenReturn(List.of(math, history));

        assertThat(service.getCatalog("categories")).extracting(CatalogItemDTO::name).containsExactly("Lịch sử", "Toán");
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
