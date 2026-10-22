package com.lumi.service.storage;

import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityManager;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.*;

/** Defer cloud cleanup to a separate thread, after the outer transaction releases its connection. */
@Service
public class CourseStorageCleanup {

    private static final Logger LOG = LoggerFactory.getLogger(CourseStorageCleanup.class);
    private final CourseObjectStorage storage;
    private final EntityManager em;
    private final TransactionTemplate transaction;
    private final ExecutorService executor = Executors.newSingleThreadExecutor(
        Thread.ofPlatform().daemon(true).name("course-object-cleanup").factory()
    );
    private CompletableFuture<Void> pending = CompletableFuture.completedFuture(null);

    public CourseStorageCleanup(CourseObjectStorage storage, EntityManager em, PlatformTransactionManager manager) {
        this.storage = storage;
        this.em = em;
        this.transaction = new TransactionTemplate(manager);
        this.transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    public void afterCommit(String url) {
        schedule(url, false);
    }

    public void onRollback(String url) {
        schedule(url, true);
    }

    public synchronized CompletionStage<Void> completion() {
        return pending;
    }

    private void schedule(String url, boolean rollback) {
        if (url == null) return;
        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (rollback ? status != STATUS_ROLLED_BACK : status != STATUS_COMMITTED) return;
                    enqueue(url);
                }
            }
        );
    }

    private synchronized void enqueue(String url) {
        pending = pending
            .handle((result, failure) -> null)
            .thenRunAsync(() -> {
                try {
                    transaction.executeWithoutResult(tx -> {
                        long count = em
                            .createQuery("select count(c) from Course c where c.thumbnailUrl = :url or c.introVideoUrl = :url", Long.class)
                            .setParameter("url", url)
                            .getSingleResult();
                        count += em
                            .createQuery("select count(l) from Lesson l where l.videoUrl = :url", Long.class)
                            .setParameter("url", url)
                            .getSingleResult();
                        count += em
                            .createQuery("select count(m) from CourseMaterial m where m.fileUrl = :url", Long.class)
                            .setParameter("url", url)
                            .getSingleResult();
                        if (count == 0) storage.delete(url);
                    });
                } catch (RuntimeException failure) {
                    LOG.warn("Course object cleanup failed; unreferenced private object retained for operational cleanup");
                }
            }, executor);
    }

    @PreDestroy
    public void close() {
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
        }
    }
}
