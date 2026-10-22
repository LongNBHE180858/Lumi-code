package com.lumi.service.storage;

import java.nio.file.Path;
import java.time.Instant;

public interface CourseObjectStorage {
    record Uploaded(String url, String fileName, String mimeType, long size) {}

    record Access(String url, Instant expiresAt) {}

    Uploaded upload(Long courseId, CourseFileValidator.Kind kind, Path file, String name, String mimeType);
    Access access(String url, String fileName, boolean download);
    void delete(String url);
}
