package com.lumi.service.storage;

import static org.assertj.core.api.Assertions.*;

import com.lumi.config.CourseStorageProperties;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.*;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

class CourseStorageTest {

    private final CourseFileValidator validator = new CourseFileValidator();

    @Test
    void docxRequiresWordPackageAndDoesNotTrustClientMimeType() throws Exception {
        byte[] content = docx(false);
        try (
            var file = validator.validate(
                new MockMultipartFile("file", "notes.docx", "text/plain", content),
                CourseFileValidator.Kind.DOCUMENT
            )
        ) {
            assertThat(file.mimeType()).isEqualTo("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        }
        assertThatThrownBy(() ->
            validator.validate(
                new MockMultipartFile("file", "notes.docx", "application/zip", docx(true)),
                CourseFileValidator.Kind.DOCUMENT
            )
        ).isInstanceOf(ResponseStatusException.class);
        assertThatThrownBy(() ->
            validator.validate(
                new MockMultipartFile("file", "notes.docx", "application/zip", "fake zip".getBytes()),
                CourseFileValidator.Kind.DOCUMENT
            )
        ).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void pdfHeaderAndTailAreChecked() throws Exception {
        try (
            var file = validator.validate(
                new MockMultipartFile("file", "notes.pdf", "text/html", "%PDF-1.7\n%%EOF".getBytes()),
                CourseFileValidator.Kind.DOCUMENT
            )
        ) {
            assertThat(file.mimeType()).isEqualTo("application/pdf");
        }
        assertThatThrownBy(() ->
            validator.validate(
                new MockMultipartFile("file", "notes.pdf", "application/pdf", "<script>bad</script>".getBytes()),
                CourseFileValidator.Kind.DOCUMENT
            )
        ).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void rawVideoAndImageContentCannotBeSubstitutedByExtension() {
        assertThatThrownBy(() ->
            validator.validate(new MockMultipartFile("file", "movie.mp4", "video/mp4", "fake".getBytes()), CourseFileValidator.Kind.VIDEO)
        ).isInstanceOf(ResponseStatusException.class);
        assertThatThrownBy(() ->
            validator.validate(
                new MockMultipartFile("file", "cover.png", "image/png", "fake".getBytes()),
                CourseFileValidator.Kind.THUMBNAIL
            )
        ).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void privateDurableUrlCanBeSignedWithoutStoringTheSignature() {
        var properties = new CourseStorageProperties();
        properties.setEnabled(true);
        properties.setEndpoint("https://example.r2.cloudflarestorage.com");
        properties.setBucket("lumi-test");
        properties.setAccessKey("test-access-key");
        properties.setSecretKey("test-secret-key");
        try {
            var storage = new R2CourseObjectStorage(properties);
            try {
                String durable = "https://example.r2.cloudflarestorage.com/lumi-test/courses/1/document/0123abcd.pdf";
                var access = storage.access(durable, "Bài học.pdf", true);
                assertThat(access.url()).contains("X-Amz-Signature=").contains("response-content-disposition");
                assertThat(durable).doesNotContain("X-Amz");
                assertThatThrownBy(() -> storage.access("https://other.example/private.pdf", "file.pdf", true)).isInstanceOf(
                    ResponseStatusException.class
                );
            } finally {
                storage.close();
            }
        } catch (RuntimeException failure) {
            throw failure;
        }
    }

    @Test
    void unconfiguredStorageFailsExplicitly() {
        var storage = new R2CourseObjectStorage(new CourseStorageProperties());
        assertThatThrownBy(() -> storage.access("https://example/file", "file", false)).isInstanceOfSatisfying(
            ResponseStatusException.class,
            error -> assertThat(error.getStatusCode().value()).isEqualTo(503)
        );
    }

    private byte[] docx(boolean macro) throws IOException {
        var bytes = new ByteArrayOutputStream();
        try (var zip = new ZipOutputStream(bytes)) {
            entry(
                zip,
                "[Content_Types].xml",
                "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\"><Override PartName=\"/word/document.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml\"/></Types>"
            );
            entry(zip, "word/document.xml", "<document/>");
            entry(zip, "_rels/.rels", "<Relationships/>");
            if (macro) entry(zip, "word/vbaProject.bin", "macro");
        }
        return bytes.toByteArray();
    }

    private void entry(ZipOutputStream zip, String name, String value) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(value.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }
}
