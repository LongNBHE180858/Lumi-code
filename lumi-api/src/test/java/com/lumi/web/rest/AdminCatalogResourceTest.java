package com.lumi.web.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lumi.service.AdminService;
import com.lumi.service.MailService;
import com.lumi.service.dto.GradeDTO;
import com.lumi.service.dto.SubjectDTO;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AdminCatalogResourceTest {

    private final AdminService service = mock(AdminService.class);
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new AdminResource(service, mock(MailService.class))).build();
    }

    @Test
    void listsBothCatalogTypesWithTheirOwnFields() throws Exception {
        SubjectDTO subject = subject(1L);
        GradeDTO grade = grade(2L);
        when(service.getCatalogSubjects()).thenReturn(List.of(subject));
        when(service.getCatalogGrades()).thenReturn(List.of(grade));

        mvc.perform(get("/api/admin/catalog/subjects"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].description").value("Môn học"))
            .andExpect(jsonPath("$[0].level").doesNotExist());
        mvc.perform(get("/api/admin/catalog/grades"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].level").value(12))
            .andExpect(jsonPath("$[0].description").doesNotExist());
    }

    @Test
    void writesThroughAdminOnlyRoutes() throws Exception {
        when(service.saveCatalogSubject(eq(null), any(SubjectDTO.class))).thenReturn(subject(1L));
        when(service.saveCatalogGrade(eq(2L), any(GradeDTO.class))).thenReturn(grade(2L));

        mvc.perform(
            post("/api/admin/catalog/subjects").contentType("application/json").content("{\"name\":\"Toán\",\"description\":\"Môn học\"}")
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Toán"));
        mvc.perform(put("/api/admin/catalog/grades/2").contentType("application/json").content("{\"name\":\"Lớp 12\",\"level\":12}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.level").value(12));
    }

    private SubjectDTO subject(Long id) {
        SubjectDTO dto = new SubjectDTO();
        dto.setId(id);
        dto.setName("Toán");
        dto.setDescription("Môn học");
        return dto;
    }

    private GradeDTO grade(Long id) {
        GradeDTO dto = new GradeDTO();
        dto.setId(id);
        dto.setName("Lớp 12");
        dto.setLevel(12);
        return dto;
    }
}
