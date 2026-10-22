package com.lumi.service.storage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Locale;
import java.util.zip.*;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CourseFileValidator {

    public enum Kind {
        DOCUMENT,
        VIDEO,
        THUMBNAIL,
    }

    public record ValidatedFile(Path path, String name, String mimeType) implements AutoCloseable {
        @Override
        public void close() throws IOException {
            Files.deleteIfExists(path);
        }
    }

    public ValidatedFile validate(MultipartFile upload, Kind kind) {
        if (upload == null || upload.isEmpty()) throw invalid("File is empty");
        String original = upload.getOriginalFilename();
        if (original == null) throw invalid("File name is required");
        String name = original.replace('\\', '/');
        name = name.substring(name.lastIndexOf('/') + 1);
        if (name.isBlank() || name.length() > 255 || name.chars().anyMatch(c -> Character.isISOControl(c))) throw invalid(
            "Invalid file name"
        );
        String extension = name.substring(name.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        Path temp = null;
        try {
            temp = Files.createTempFile("lumi-course-", ".upload");
            upload.transferTo(temp);
            String mime = switch (kind) {
                case DOCUMENT -> document(temp, extension);
                case VIDEO -> video(temp, extension);
                case THUMBNAIL -> image(temp, extension);
            };
            return new ValidatedFile(temp, name, mime);
        } catch (Exception failure) {
            if (temp != null) try {
                Files.deleteIfExists(temp);
            } catch (IOException ignored) {}
            if (failure instanceof ResponseStatusException response) throw response;
            throw invalid("File content is invalid or cannot be read");
        }
    }

    private String document(Path file, String extension) throws Exception {
        if (extension.equals("pdf")) {
            try (var input = Files.newInputStream(file)) {
                if (!new String(input.readNBytes(5), StandardCharsets.US_ASCII).equals("%PDF-")) throw invalid("Invalid PDF");
            }
            try (var input = new RandomAccessFile(file.toFile(), "r")) {
                input.seek(Math.max(0, input.length() - 1024));
                byte[] tail = new byte[(int) Math.min(1024, input.length())];
                input.readFully(tail);
                if (!new String(tail, StandardCharsets.US_ASCII).contains("%%EOF")) throw invalid("Incomplete PDF");
            }
            return "application/pdf";
        }
        if (!extension.equals("docx")) throw invalid("Lesson documents must be PDF or DOCX");
        try (ZipFile zip = new ZipFile(file.toFile())) {
            if (
                zip.getEntry("[Content_Types].xml") == null ||
                zip.getEntry("word/document.xml") == null ||
                zip.getEntry("_rels/.rels") == null
            ) throw invalid("Invalid DOCX package");
            if (zip.getEntry("word/vbaProject.bin") != null) throw invalid("Macro-enabled documents are not supported");
            var factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setAttribute(javax.xml.XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(javax.xml.XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            byte[] content;
            try (var input = zip.getInputStream(zip.getEntry("[Content_Types].xml"))) {
                content = input.readNBytes(1024 * 1024 + 1);
            }
            if (content.length > 1024 * 1024) throw invalid("DOCX manifest is too large");
            var document = factory.newDocumentBuilder().parse(new ByteArrayInputStream(content));
            var entries = document.getElementsByTagNameNS("http://schemas.openxmlformats.org/package/2006/content-types", "Override");
            boolean word = false;
            for (int i = 0; i < entries.getLength(); i++) {
                var entry = (org.w3c.dom.Element) entries.item(i);
                if (
                    "/word/document.xml".equals(entry.getAttribute("PartName")) &&
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml".equals(
                        entry.getAttribute("ContentType")
                    )
                ) word = true;
            }
            if (!word) throw invalid("Invalid DOCX content type");
        }
        return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    }

    private String video(Path file, String extension) throws IOException {
        try (var input = Files.newInputStream(file)) {
            byte[] header = input.readNBytes(32);
            if (
                !extension.equals("mp4") || header.length < 12 || !new String(header, 4, 4, StandardCharsets.US_ASCII).equals("ftyp")
            ) throw invalid("Video must be an MP4 file");
        }
        return "video/mp4";
    }

    private String image(Path file, String extension) throws IOException {
        if (!java.util.Set.of("png", "jpg", "jpeg").contains(extension)) throw invalid("Thumbnail must be PNG or JPEG");
        try (var input = ImageIO.createImageInputStream(file.toFile())) {
            var readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) throw invalid("Invalid thumbnail image");
            var reader = readers.next();
            try {
                reader.setInput(input);
                String format = reader.getFormatName().toLowerCase(Locale.ROOT);
                boolean jpeg = format.equals("jpeg") && (extension.equals("jpg") || extension.equals("jpeg"));
                if (!jpeg && !(format.equals("png") && extension.equals("png"))) throw invalid(
                    "Thumbnail must match PNG or JPEG extension"
                );
                long pixels = (long) reader.getWidth(0) * reader.getHeight(0);
                if (pixels < 1 || pixels > 40_000_000L) throw invalid("Thumbnail dimensions are too large");
                if (reader.read(0) == null) throw invalid("Invalid thumbnail image");
                return jpeg ? "image/jpeg" : "image/png";
            } finally {
                reader.dispose();
            }
        }
    }

    private ResponseStatusException invalid(String reason) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, reason);
    }
}
