package com.lumi.service.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class ClassroomStorageCleanup {

    private static final Logger LOG = LoggerFactory.getLogger(ClassroomStorageCleanup.class);
    private final ClassroomObjectStorage storage;

    public ClassroomStorageCleanup(ClassroomObjectStorage storage) {
        this.storage = storage;
    }

    public void onRollback(Long classId, String url) {
        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status != STATUS_ROLLED_BACK) return;
                    try {
                        storage.delete(classId, url);
                    } catch (RuntimeException failure) {
                        LOG.warn("Classroom upload rollback cleanup failed; object retained for operational cleanup");
                    }
                }
            }
        );
    }
}
