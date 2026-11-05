package com.lumi.service.dto;

import jakarta.validation.constraints.Size;

/** Shared admin payload for the small course catalog dictionaries. */
public record CatalogItemDTO(Long id, @Size(max = 100) String name, @Size(max = 5000) String description, Integer level) {}
