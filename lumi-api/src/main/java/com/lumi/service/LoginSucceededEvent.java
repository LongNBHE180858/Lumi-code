package com.lumi.service;

public record LoginSucceededEvent(Long userId, String ipAddress, String userAgent) {}
