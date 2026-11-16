package com.lumi.service;

import com.lumi.domain.AuditLog;
import com.lumi.repository.AuditLogRepository;
import com.lumi.repository.UserRepository;
import com.lumi.service.dto.AdminAuditLogDTO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

@Service
public class AuditLogService {

    private static final int EXPORT_LIMIT = 10000;
    private static final Sort ORDER = Sort.by(Sort.Order.desc("occurredAt"), Sort.Order.desc("id"));

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public AuditLogService(AuditLogRepository auditLogRepository, UserRepository userRepository, ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordSuccessfulLogin(Long userId, String ipAddress, String userAgent) {
        String safeIp = ipAddress == null ? null : ipAddress.trim();
        if (safeIp != null && safeIp.length() > 50) {
            safeIp = safeIp.substring(0, 50);
        }
        AuditLog auditLog = new AuditLog()
            .actor(userRepository.getReferenceById(userId))
            .action("LOGIN_SUCCESS")
            .targetEntity("User")
            .targetId(userId.toString())
            .ipAddress(safeIp)
            .details(describeDevice(userAgent))
            .occurredAt(Instant.now());
        auditLogRepository.save(auditLog);
    }

    @Transactional(readOnly = true)
    public Page<AdminAuditLogDTO> search(
        String actor,
        String action,
        String targetEntity,
        String targetId,
        Instant from,
        Instant to,
        Pageable pageable
    ) {
        validateRange(from, to);
        if (pageable.getPageSize() > 100 || pageable.getPageNumber() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kích thước trang không hợp lệ.");
        }
        return auditLogRepository
            .findAll(
                filter(actor, action, targetEntity, targetId, from, to),
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), ORDER)
            )
            .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public byte[] export(String actor, String action, String targetEntity, String targetId, Instant from, Instant to, Integer limit) {
        validateRange(from, to);
        if (limit != null && (limit < 1 || limit > EXPORT_LIMIT)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số dòng xuất phải từ 1 đến 10.000.");
        }
        Page<AuditLog> page = auditLogRepository.findAll(
            filter(actor, action, targetEntity, targetId, from, to),
            PageRequest.of(0, limit == null ? EXPORT_LIMIT + 1 : limit, ORDER)
        );
        if (limit == null && page.getTotalElements() > EXPORT_LIMIT) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Có hơn 10.000 bản ghi. Hãy thu hẹp bộ lọc.");
        }
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Audit logs");
            writeRow(sheet.createRow(0), "Thời điểm (UTC)", "Tài khoản", "Hành động", "Loại đối tượng", "ID đối tượng", "IP", "Chi tiết");
            int rowIndex = 1;
            for (AuditLog log : page.getContent()) {
                writeRow(
                    sheet.createRow(rowIndex++),
                    text(log.getOccurredAt()),
                    log.getActor().getLogin(),
                    "LOGIN_SUCCESS".equals(log.getAction()) ? "Đăng nhập thành công" : log.getAction(),
                    log.getTargetEntity(),
                    log.getTargetId(),
                    log.getIpAddress(),
                    displayDetails(log)
                );
            }
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Không thể tạo tệp Excel audit log.", exception);
        }
    }

    private void writeRow(Row row, String... values) {
        for (int index = 0; index < values.length; index++) {
            String value = values[index] == null ? "" : values[index];
            if (!value.isEmpty() && "=+-@".indexOf(value.charAt(0)) >= 0) {
                value = "'" + value;
            }
            row.createCell(index).setCellValue(value);
        }
    }

    private String text(Instant value) {
        return value == null ? "" : value.toString();
    }

    private AdminAuditLogDTO toDto(AuditLog log) {
        return new AdminAuditLogDTO(
            log.getId(),
            log.getActor().getLogin(),
            log.getAction(),
            log.getTargetEntity(),
            log.getTargetId(),
            log.getIpAddress(),
            displayDetails(log),
            log.getOccurredAt()
        );
    }

    private void validateRange(Instant from, Instant to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Khoảng thời gian không hợp lệ.");
        }
    }

    private String displayDetails(AuditLog log) {
        String details = log.getDetails();
        if (details == null || !"LOGIN_SUCCESS".equals(log.getAction())) {
            return details;
        }
        try {
            var parsed = objectMapper.readTree(details);
            if (parsed.hasNonNull("userAgent")) {
                return describeDevice(parsed.get("userAgent").asText());
            }
        } catch (RuntimeException ignored) {}
        if (details.contains("Mozilla/") || details.contains("Chrome/") || details.contains("Firefox/") || details.contains("Safari/")) {
            return describeDevice(details);
        }
        return details;
    }

    private String describeDevice(String userAgent) {
        if (userAgent == null || userAgent.isBlank() || "Unknown Device".equals(userAgent)) {
            return "Thiết bị không xác định";
        }
        String browser = userAgent.contains("Edg/")
            ? "Microsoft Edge"
            : userAgent.contains("Firefox/")
              ? "Firefox"
              : userAgent.contains("Chrome/")
                ? "Chrome"
                : userAgent.contains("Safari/")
                  ? "Safari"
                  : "Trình duyệt không xác định";
        String system = userAgent.contains("iPhone")
            ? "iPhone"
            : userAgent.contains("iPad")
              ? "iPad"
              : userAgent.contains("Android")
                ? "Android"
                : userAgent.contains("Macintosh") || userAgent.contains("Mac OS")
                  ? "macOS"
                  : userAgent.contains("Windows")
                    ? "Windows"
                    : userAgent.contains("Linux")
                      ? "Linux"
                      : "Hệ điều hành không xác định";
        return browser + " · " + system;
    }

    private Specification<AuditLog> filter(String actor, String action, String targetEntity, String targetId, Instant from, Instant to) {
        return (root, query, builder) -> {
            var conditions = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
            if (actor != null && !actor.isBlank()) {
                conditions.add(
                    builder.like(
                        builder.lower(root.join("actor").get("login")),
                        "%" + actor.trim().toLowerCase(java.util.Locale.ROOT) + "%"
                    )
                );
            }
            if (action != null && !action.isBlank()) {
                conditions.add(builder.equal(root.get("action"), action.trim()));
            }
            if (targetEntity != null && !targetEntity.isBlank()) {
                conditions.add(builder.equal(root.get("targetEntity"), targetEntity.trim()));
            }
            if (targetId != null && !targetId.isBlank()) {
                conditions.add(builder.equal(root.get("targetId"), targetId.trim()));
            }
            if (from != null) {
                conditions.add(builder.greaterThanOrEqualTo(root.get("occurredAt"), from));
            }
            if (to != null) {
                conditions.add(builder.lessThanOrEqualTo(root.get("occurredAt"), to));
            }
            return builder.and(conditions.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
