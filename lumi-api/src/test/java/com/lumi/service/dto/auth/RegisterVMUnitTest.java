package com.lumi.service.dto.auth;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit Test for {@link RegisterRequestDTO} validation boundaries.
 * Covers Phase 3.1 Unit Testing scope for Student Registration:
 * TC-REG-STU-UT-001, UT-002, UT-003, UT-004, UT-005, UT-007.
 */
class RegisterRequestDTOUnitTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("TC-REG-STU-UT-001: Verify valid email address accepted by email validator")
    @Tag("UT-001")
    void UT001_validEmail_shouldPassValidation() {
        // Arrange
        RegisterRequestDTO vm = new RegisterRequestDTO();
        vm.setEmail("student.hanoi@lumi.edu.vn");

        // Act
        Set<ConstraintViolation<RegisterRequestDTO>> violations = validator.validateProperty(vm, "email");

        // Assert
        assertThat(violations).as("Valid email address per SRS GB-02 must produce zero validation violations").isEmpty();
    }

    @Test
    @DisplayName("TC-REG-STU-UT-002: Verify email format rejected when missing @ symbol")
    @Tag("UT-002")
    void UT002_missingAtSymbolEmail_shouldFailValidation() {
        // Arrange
        RegisterRequestDTO vm = new RegisterRequestDTO();
        vm.setEmail("student.lumiedu.vn");

        // Act
        Set<ConstraintViolation<RegisterRequestDTO>> violations = validator.validateProperty(vm, "email");

        // Assert
        assertThat(violations).as("Email missing @ symbol per SRS GB-02 must be rejected by validator").isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName().equals("Email"));
    }

    @Test
    @DisplayName("TC-REG-STU-UT-003: Verify email format rejected when missing domain name")
    @Tag("UT-003")
    void UT003_missingDomainEmail_shouldFailValidation() {
        // Arrange
        RegisterRequestDTO vm = new RegisterRequestDTO();
        vm.setEmail("student@");

        // Act
        Set<ConstraintViolation<RegisterRequestDTO>> violations = validator.validateProperty(vm, "email");

        // Assert
        assertThat(violations).as("Email missing domain per SRS GB-02 must be rejected by validator").isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName().equals("Email"));
    }

    @Test
    @DisplayName("TC-REG-STU-UT-004: Verify email format rejected when containing embedded spaces")
    @Tag("UT-004")
    void UT004_embeddedSpacesEmail_shouldFailValidation() {
        // Arrange
        RegisterRequestDTO vm = new RegisterRequestDTO();
        vm.setEmail("student hanoi@lumi.edu.vn");

        // Act
        Set<ConstraintViolation<RegisterRequestDTO>> violations = validator.validateProperty(vm, "email");

        // Assert
        assertThat(violations).as("Email containing embedded spaces per SRS GB-02 must be rejected by validator").isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName().equals("Email"));
    }

    @Test
    @DisplayName("TC-REG-STU-UT-005: Verify email length accepted at 254 characters boundary")
    @Tag("UT-005")
    void UT005_boundary254CharactersEmail_shouldPassValidation() {
        // Arrange
        // RFC 5321 compliant construction: local-part 64 chars, domain 189 chars (labels <= 60 chars)
        String localPart = "a".repeat(64);
        String domain = "b".repeat(60) + "." + "c".repeat(60) + "." + "d".repeat(60) + ".edu.vn";
        String email254 = localPart + "@" + domain;

        assertThat(email254.length()).as("Verified email length must be exactly 254 characters").isEqualTo(254);

        RegisterRequestDTO vm = new RegisterRequestDTO();
        vm.setEmail(email254);

        // Act
        Set<ConstraintViolation<RegisterRequestDTO>> violations = validator.validateProperty(vm, "email");

        // Assert
        assertThat(violations).as("Email at 254 characters boundary per SRS GB-01 and RFC 5321 must pass validation").isEmpty();
    }

    @Test
    @DisplayName("TC-REG-STU-UT-007: Verify email length rejected when exceeding 255 characters (256 chars)")
    @Tag("UT-007")
    void UT007_exceeding255CharactersEmail_shouldFailValidation() {
        // Arrange
        String localPart = "a".repeat(64);
        String domain = "b".repeat(60) + "." + "c".repeat(60) + "." + "d".repeat(62) + ".edu.vn";
        String email256 = localPart + "@" + domain;

        assertThat(email256.length()).as("Verified email length must be exactly 256 characters").isEqualTo(256);

        RegisterRequestDTO vm = new RegisterRequestDTO();
        vm.setEmail(email256);

        // Act
        Set<ConstraintViolation<RegisterRequestDTO>> violations = validator.validateProperty(vm, "email");

        // Assert
        assertThat(violations).as("Email exceeding 255 characters per SRS GB-01 must be rejected by validator").isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName().equals("Size"));
    }
}
