package com.lumi.service;

import com.lumi.domain.ClassMaterial;
import com.lumi.domain.StudyClass;
import com.lumi.domain.User;
import com.lumi.domain.enumeration.ClassMemberStatus;
import com.lumi.repository.ClassMaterialRepository;
import com.lumi.repository.ClassMemberRepository;
import com.lumi.repository.StudyClassRepository;
import com.lumi.repository.UserRepository;
import com.lumi.security.SecurityUtils;
import com.lumi.service.dto.studyClass.ClassroomDTO;
import com.lumi.service.storage.ClassroomObjectStorage;
import com.lumi.service.storage.ClassroomStorageCleanup;
import com.lumi.service.storage.CourseFileValidator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class ClassroomMaterialService {

    private final StudyClassRepository classes;
    private final ClassMemberRepository members;
    private final ClassMaterialRepository materials;
    private final UserRepository users;
    private final CourseFileValidator validator;
    private final ClassroomObjectStorage storage;
    private final ClassroomStorageCleanup cleanup;
    private final Path uploadRoot;

    public ClassroomMaterialService(
        StudyClassRepository classes,
        ClassMemberRepository members,
        ClassMaterialRepository materials,
        UserRepository users,
        CourseFileValidator validator,
        ClassroomObjectStorage storage,
        ClassroomStorageCleanup cleanup,
        @Value("${lumi.classroom-upload-dir:uploads/classrooms}") String uploadRoot
    ) {
        this.classes = classes;
        this.members = members;
        this.materials = materials;
        this.users = users;
        this.validator = validator;
        this.storage = storage;
        this.cleanup = cleanup;
        this.uploadRoot = Path.of(uploadRoot).toAbsolutePath().normalize();
    }

    public ClassroomDTO.Material upload(Long classId, String title, MultipartFile file) {
        User instructor = users
            .findOneByLogin(SecurityUtils.getCurrentUserLogin().orElse(""))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        StudyClass studyClass = classes
            .findById(classId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lớp"));
        if (Boolean.TRUE.equals(studyClass.getIsDeleted()) || !Objects.equals(studyClass.getInstructor().getId(), instructor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không quản lý lớp này");
        }
        if (title == null || title.isBlank() || title.trim().length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên bài học phải có từ 1 đến 255 ký tự");
        }
        String name = file == null ? "" : file.getOriginalFilename();
        CourseFileValidator.Kind kind =
            name != null && name.toLowerCase(java.util.Locale.ROOT).endsWith(".pdf")
                ? CourseFileValidator.Kind.DOCUMENT
                : CourseFileValidator.Kind.VIDEO;
        if (kind == CourseFileValidator.Kind.VIDEO && (name == null || !name.toLowerCase(java.util.Locale.ROOT).endsWith(".mp4"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ hỗ trợ PDF hoặc MP4");
        }
        try (var validated = validator.validate(file, kind)) {
            if (
                (kind == CourseFileValidator.Kind.DOCUMENT && file.getSize() > 25 * 1024 * 1024L) ||
                (kind == CourseFileValidator.Kind.VIDEO && file.getSize() > 500 * 1024 * 1024L)
            ) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tệp vượt quá dung lượng cho phép");
            }
            var uploaded = storage.upload(classId, kind, validated.path(), validated.name(), validated.mimeType());
            cleanup.onRollback(classId, uploaded.url());
            int nextIndex =
                materials
                    .findByStudyClassIdOrderByOrderIndexAscIdAsc(classId)
                    .stream()
                    .map(ClassMaterial::getOrderIndex)
                    .filter(Objects::nonNull)
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(-1) + 1;
            Instant now = Instant.now();
            ClassMaterial material = new ClassMaterial()
                .studyClass(studyClass)
                .title(title.trim())
                .fileType(kind == CourseFileValidator.Kind.DOCUMENT ? "PDF" : "VIDEO")
                .fileSizeBytes(uploaded.size())
                .fileObjectKey(null)
                .orderIndex(nextIndex)
                .status("PUBLISHED")
                .uploadedAt(now)
                .updatedAt(now)
                .fileUrl(uploaded.url());
            material = materials.saveAndFlush(material);
            return new ClassroomDTO.Material(
                material.getId(),
                material.getTitle(),
                material.getFileType(),
                publicUrl(classId, material.getId()),
                material.getUploadedAt(),
                material.getOrderIndex()
            );
        } catch (IOException error) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không lưu được tệp bài học");
        }
    }

    public List<ClassroomDTO.Material> reorder(Long classId, List<Long> materialIds) {
        User instructor = users
            .findOneByLogin(SecurityUtils.getCurrentUserLogin().orElse(""))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        StudyClass studyClass = classes
            .findById(classId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lớp"));
        if (Boolean.TRUE.equals(studyClass.getIsDeleted()) || !Objects.equals(studyClass.getInstructor().getId(), instructor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không quản lý lớp này");
        }
        List<ClassMaterial> current = materials.findByStudyClassIdOrderByOrderIndexAscIdAsc(classId);
        if (
            materialIds == null ||
            materialIds.stream().anyMatch(Objects::isNull) ||
            materialIds.size() != current.size() ||
            Set.copyOf(materialIds).size() != current.size() ||
            !current.stream().map(ClassMaterial::getId).collect(java.util.stream.Collectors.toSet()).equals(Set.copyOf(materialIds))
        ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Danh sách sắp xếp bài học không hợp lệ");
        }
        for (int index = 0; index < materialIds.size(); index++) {
            Long materialId = materialIds.get(index);
            int order = index;
            current
                .stream()
                .filter(item -> item.getId().equals(materialId))
                .findFirst()
                .orElseThrow()
                .setOrderIndex(order);
        }
        materials.saveAll(current);
        return current
            .stream()
            .sorted(java.util.Comparator.comparing(ClassMaterial::getOrderIndex))
            .map(item ->
                new ClassroomDTO.Material(
                    item.getId(),
                    item.getTitle(),
                    item.getFileType(),
                    publicUrl(classId, item.getId()),
                    item.getUploadedAt(),
                    item.getOrderIndex()
                )
            )
            .toList();
    }

    @Transactional(readOnly = true)
    public FileAccess access(Long classId, Long materialId) {
        ClassMaterial material = authorizedMaterial(classId, materialId);
        String key = material.getFileObjectKey();
        if (key == null || !key.matches("[a-f0-9-]{36}\\.(pdf|mp4)")) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tệp cũ không tồn tại");
        }
        Path path = uploadRoot.resolve(key).normalize();
        if (!path.startsWith(uploadRoot) || !Files.isRegularFile(path)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tệp bài học không tồn tại");
        }
        try {
            Resource resource = new UrlResource(path.toUri());
            return new FileAccess(resource, "PDF".equals(material.getFileType()) ? "application/pdf" : "video/mp4");
        } catch (java.net.MalformedURLException error) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không đọc được tệp bài học");
        }
    }

    @Transactional(readOnly = true)
    public ClassroomObjectStorage.Access accessUrl(Long classId, Long materialId) {
        ClassMaterial material = authorizedMaterial(classId, materialId);
        if (material.getFileObjectKey() != null && material.getFileObjectKey().matches("[a-f0-9-]{36}\\.(pdf|mp4)")) {
            return new ClassroomObjectStorage.Access(publicUrl(classId, materialId), Instant.now().plusSeconds(300));
        }
        return storage.access(
            classId,
            material.getFileUrl(),
            material.getTitle() + ("PDF".equals(material.getFileType()) ? ".pdf" : ".mp4")
        );
    }

    private String publicUrl(Long classId, Long materialId) {
        return "/api/classes/" + classId + "/materials/" + materialId + "/file";
    }

    private ClassMaterial authorizedMaterial(Long classId, Long materialId) {
        User user = users
            .findOneByLogin(SecurityUtils.getCurrentUserLogin().orElse(""))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        StudyClass studyClass = classes
            .findById(classId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lớp"));
        ClassMaterial material = materials
            .findById(materialId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài học"));
        if (Boolean.TRUE.equals(studyClass.getIsDeleted()) || !Objects.equals(material.getStudyClass().getId(), classId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài học");
        }
        boolean owner = Objects.equals(studyClass.getInstructor().getId(), user.getId());
        boolean enrolled = members
            .findByStudyClassIdAndStudentId(classId, user.getId())
            .filter(member -> ClassMemberStatus.ACTIVE.name().equals(member.getStatus()))
            .isPresent();
        if (!owner && (!enrolled || !"PUBLISHED".equals(material.getStatus()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không được xem bài học này");
        }
        return material;
    }

    public record FileAccess(Resource resource, String mimeType) {}
}
