package com.lumi.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.concurrent.RejectedExecutionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.transaction.event.TransactionalEventListenerFactory;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronizationUtils;

@ExtendWith(MockitoExtension.class)
class LoginAuditListenerTest {

    @Mock
    private AuditLogService auditLogService;

    private LoginAuditListener listener;

    @BeforeEach
    void setUp() {
        listener = new LoginAuditListener(auditLogService, Runnable::run);
    }

    @AfterEach
    void clearTransactionState() {
        TransactionSynchronizationManager.clear();
    }

    @Test
    void recordsOnlyAfterCommit() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(LoginAuditListener.class, () -> listener);
            context.registerBean(TransactionalEventListenerFactory.class);
            context.refresh();
            TransactionSynchronizationManager.initSynchronization();
            TransactionSynchronizationManager.setActualTransactionActive(true);

            context.publishEvent(new LoginSucceededEvent(42L, "192.0.2.1", "browser"));
            verifyNoInteractions(auditLogService);

            TransactionSynchronizationUtils.triggerAfterCompletion(TransactionSynchronization.STATUS_COMMITTED);
            verify(auditLogService).recordSuccessfulLogin(42L, "192.0.2.1", "browser");
        }
    }

    @Test
    void skipsAuditWhenLoginRollsBack() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(LoginAuditListener.class, () -> listener);
            context.registerBean(TransactionalEventListenerFactory.class);
            context.refresh();
            TransactionSynchronizationManager.initSynchronization();
            TransactionSynchronizationManager.setActualTransactionActive(true);

            context.publishEvent(new LoginSucceededEvent(42L, "192.0.2.1", "browser"));
            TransactionSynchronizationUtils.triggerAfterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);

            verifyNoInteractions(auditLogService);
        }
    }

    @Test
    void auditWriteFailureDoesNotFailCommittedLogin() {
        LoginSucceededEvent event = new LoginSucceededEvent(42L, "192.0.2.1", "browser");
        doThrow(new IllegalStateException("audit storage unavailable"))
            .when(auditLogService)
            .recordSuccessfulLogin(42L, "192.0.2.1", "browser");

        assertThatCode(() -> listener.recordSuccessfulLogin(event)).doesNotThrowAnyException();
        verify(auditLogService).recordSuccessfulLogin(42L, "192.0.2.1", "browser");
    }

    @Test
    void auditQueueRejectionDoesNotFailCommittedLogin() {
        LoginAuditListener rejectingListener = new LoginAuditListener(auditLogService, command -> {
            throw new RejectedExecutionException("queue full");
        });

        assertThatCode(() ->
            rejectingListener.recordSuccessfulLogin(new LoginSucceededEvent(42L, "192.0.2.1", "browser"))
        ).doesNotThrowAnyException();
        verifyNoInteractions(auditLogService);
    }
}
