package com.lumi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lumi.domain.AuditLog;
import com.lumi.domain.User;
import com.lumi.repository.AuditLogRepository;
import com.lumi.repository.UserRepository;
import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository repository;

    @Mock
    private UserRepository userRepository;

    private AuditLogService service;

    @BeforeEach
    void setUp() {
        service = new AuditLogService(repository, userRepository, new ObjectMapper());
    }

    @Test
    void recordsLoginWithServerSuppliedDeviceAndIp() {
        User user = new User();
        user.setId(42L);
        user.setLogin("student");

        when(userRepository.getReferenceById(42L)).thenReturn(user);
        service.recordSuccessfulLogin(42L, "192.0.2.1", "Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 Chrome/154.0.0.0 Safari/537.36");

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(repository).save(captor.capture());
        AuditLog log = captor.getValue();
        assertThat(log.getActor()).isSameAs(user);
        assertThat(log.getAction()).isEqualTo("LOGIN_SUCCESS");
        assertThat(log.getTargetEntity()).isEqualTo("User");
        assertThat(log.getTargetId()).isEqualTo("42");
        assertThat(log.getIpAddress()).isEqualTo("192.0.2.1");
        assertThat(log.getDetails()).isEqualTo("Chrome · Windows");
        assertThat(log.getOccurredAt()).isNotNull();
    }

    @Test
    void exportsExcelAsTextAndEscapesSpreadsheetFormulas() throws Exception {
        User user = new User();
        user.setLogin("=danger");
        AuditLog log = new AuditLog()
            .actor(user)
            .action("LOGIN_SUCCESS")
            .targetEntity("User")
            .targetId("42")
            .ipAddress("192.0.2.1")
            .details("{\"userAgent\":\"Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 Chrome/154.0.0.0 Safari/537.36\"}")
            .occurredAt(Instant.parse("2026-11-16T00:00:00Z"));
        when(repository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(log)));

        byte[] bytes = service.export(null, null, null, null, null, null, 1);
        ArgumentCaptor<PageRequest> pageRequest = ArgumentCaptor.forClass(PageRequest.class);
        verify(repository).findAll(any(Specification.class), pageRequest.capture());
        assertThat(pageRequest.getValue().getPageSize()).isEqualTo(1);

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            var row = workbook.getSheetAt(0).getRow(1);
            assertThat(row.getCell(0).getStringCellValue()).isEqualTo("2026-11-16T00:00:00Z");
            assertThat(row.getCell(1).getStringCellValue()).isEqualTo("'=danger");
            assertThat(row.getCell(2).getStringCellValue()).isEqualTo("Đăng nhập thành công");
            assertThat(row.getCell(6).getStringCellValue()).isEqualTo("Chrome · Windows");
            assertThat(
                service.search(null, null, null, null, null, null, PageRequest.of(0, 10)).getContent().getFirst().details()
            ).isEqualTo("Chrome · Windows");
        }
    }

    @Test
    void rejectsExportWhenTooManyRowsMatch() {
        when(repository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(
            new PageImpl<>(List.of(), PageRequest.of(0, 10001), 10001)
        );

        assertThatThrownBy(() -> service.export(null, null, null, null, null, null, null)).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void rejectsInvalidExportRowCount() {
        assertThatThrownBy(() -> service.export(null, null, null, null, null, null, 0)).isInstanceOf(ResponseStatusException.class);
        assertThatThrownBy(() -> service.export(null, null, null, null, null, null, 10001)).isInstanceOf(ResponseStatusException.class);
    }
}
