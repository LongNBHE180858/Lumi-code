package com.lumi.web.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lumi.service.GradeService;
import com.lumi.service.SubjectService;
import com.lumi.service.criteria.GradeCriteria;
import com.lumi.service.criteria.SubjectCriteria;
import com.lumi.service.dto.GradeDTO;
import com.lumi.service.dto.SubjectDTO;
import com.lumi.service.query.GradeQueryService;
import com.lumi.service.query.SubjectQueryService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class CatalogReadResourceTest {

    private final SubjectService subjectService = mock(SubjectService.class);
    private final GradeService gradeService = mock(GradeService.class);
    private final SubjectQueryService subjectQueryService = mock(SubjectQueryService.class);
    private final GradeQueryService gradeQueryService = mock(GradeQueryService.class);
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(
            new SubjectResource(subjectService, subjectQueryService),
            new GradeResource(gradeService, gradeQueryService)
        )
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void readsSubjectsAndGrades() throws Exception {
        SubjectDTO subject = new SubjectDTO();
        subject.setId(1L);
        subject.setName("Toán");
        GradeDTO grade = new GradeDTO();
        grade.setId(2L);
        grade.setName("Lớp 12");
        grade.setLevel(12);
        when(subjectService.findOne(1L)).thenReturn(Optional.of(subject));
        when(gradeService.findOne(2L)).thenReturn(Optional.of(grade));

        mvc.perform(get("/api/subjects/1")).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Toán"));
        mvc.perform(get("/api/grades/2")).andExpect(status().isOk()).andExpect(jsonPath("$.level").value(12));
    }

    @Test
    void listsSubjectsAndGradesForExistingConsumers() throws Exception {
        SubjectDTO subject = new SubjectDTO();
        subject.setId(1L);
        subject.setName("Toán");
        GradeDTO grade = new GradeDTO();
        grade.setId(2L);
        grade.setName("Lớp 12");
        grade.setLevel(12);
        when(subjectQueryService.findByCriteria(any(SubjectCriteria.class), any(Pageable.class))).thenReturn(
            new PageImpl<>(List.of(subject))
        );
        when(gradeQueryService.findByCriteria(any(GradeCriteria.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(grade)));

        mvc.perform(get("/api/subjects")).andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("Toán"));
        mvc.perform(get("/api/grades")).andExpect(status().isOk()).andExpect(jsonPath("$[0].level").value(12));
    }

    @Test
    void rejectsLegacyWrites() throws Exception {
        for (String path : new String[] { "/api/subjects", "/api/grades" }) {
            mvc.perform(post(path).contentType("application/json").content("{}")).andExpect(status().isMethodNotAllowed());
            mvc.perform(
                put(path + "/1")
                    .contentType("application/json")
                    .content("{}")
            ).andExpect(status().isMethodNotAllowed());
            mvc.perform(
                patch(path + "/1")
                    .contentType("application/json")
                    .content("{}")
            ).andExpect(status().isMethodNotAllowed());
            mvc.perform(delete(path + "/1")).andExpect(status().isMethodNotAllowed());
        }
    }
}
