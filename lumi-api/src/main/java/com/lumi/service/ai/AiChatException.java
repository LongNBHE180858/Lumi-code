package com.lumi.service.ai;

import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;
import java.util.concurrent.TimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** Safe API errors: never expose a provider payload or credentials to the caller. */
public class AiChatException extends ResponseStatusException {

    private AiChatException(HttpStatus status, String reason) {
        super(status, reason);
    }

    static AiChatException invalidResponse() {
        return new AiChatException(HttpStatus.BAD_GATEWAY, "Phản hồi AI không hợp lệ. Vui lòng thử lại.");
    }

    static AiChatException providerFailure(RuntimeException failure) {
        Throwable cause = failure;
        for (int depth = 0; cause != null && depth < 20; depth++, cause = cause.getCause()) {
            if (cause instanceof SocketTimeoutException || cause instanceof HttpTimeoutException || cause instanceof TimeoutException) {
                return new AiChatException(HttpStatus.GATEWAY_TIMEOUT, "AI chưa phản hồi kịp. Vui lòng thử lại.");
            }
        }
        return new AiChatException(HttpStatus.BAD_GATEWAY, "Không thể nhận phản hồi từ AI. Vui lòng thử lại.");
    }
}
