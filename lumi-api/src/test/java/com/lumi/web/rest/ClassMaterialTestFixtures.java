package com.lumi.web.rest;

import com.lumi.domain.ClassMaterial;
import com.lumi.domain.StudyClass;
import com.lumi.domain.User;
import com.lumi.domain.enumeration.ClassJoinMode;
import com.lumi.domain.enumeration.RecordStatus;
import jakarta.persistence.EntityManager;
import java.time.Instant;

/** Test data fixture for ClassMaterial when its resource tests are removed. */
final class ClassMaterialTestFixtures {

    private ClassMaterialTestFixtures() {}

    public static ClassMaterial createEntity(EntityManager em) {
        ClassMaterial classMaterial = new ClassMaterial()
            .title("DEFAULT_TITLE")
            .fileType("DEFAULT_FILE_TYPE")
            .fileUrl("DEFAULT_FILE_URL")
            .fileSizeBytes(1L)
            .uploadedAt(Instant.ofEpochMilli(0L))
            .fileObjectKey("DEFAULT_FILE_OBJECT_KEY")
            .updatedAt(Instant.ofEpochMilli(0L));

        StudyClass studyClass;
        if (TestUtil.findAll(em, StudyClass.class).isEmpty()) {
            User user = UserResourceIT.createEntity();
            em.persist(user);
            em.flush();
            studyClass = new StudyClass()
                .name("DEFAULT_NAME")
                .joinCode("DEFAULT_JOIN_CODE")
                .joinMode(ClassJoinMode.CODE_AUTO)
                .status(RecordStatus.ACTIVE)
                .isDeleted(false)
                .instructor(user)
                .createdAt(Instant.ofEpochMilli(0L))
                .updatedAt(Instant.ofEpochMilli(0L));
            em.persist(studyClass);
            em.flush();
        } else {
            studyClass = TestUtil.findAll(em, StudyClass.class).getFirst();
        }
        classMaterial.setStudyClass(studyClass);
        return classMaterial;
    }
}
