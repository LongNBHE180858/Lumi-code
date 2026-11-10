package com.lumi.service.dto;

import java.util.List;

public record TicketMessageContent(String format, String text, List<Image> images) {
    public record Image(String key, String fileName) {}
}
