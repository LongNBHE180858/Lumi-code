package com.lumi.service;

import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class LoginAuditListener {

    private static final Logger LOG = LoggerFactory.getLogger(LoginAuditListener.class);

    private final AuditLogService auditLogService;
    private final Executor auditExecutor;

    public LoginAuditListener(AuditLogService auditLogService, @Qualifier("loginAuditExecutor") Executor auditExecutor) {
        this.auditLogService = auditLogService;
        this.auditExecutor = auditExecutor;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void recordSuccessfulLogin(LoginSucceededEvent event) {
        try {
            auditExecutor.execute(() -> writeAudit(event));
        } catch (RuntimeException exception) {
            LOG.warn("Could not schedule successful login audit for user {}", event.userId(), exception);
        }
    }

    private void writeAudit(LoginSucceededEvent event) {
        try {
            auditLogService.recordSuccessfulLogin(event.userId(), event.ipAddress(), event.userAgent());
        } catch (RuntimeException exception) {
            LOG.warn("Could not record successful login audit for user {}", event.userId(), exception);
        }
    }
}
