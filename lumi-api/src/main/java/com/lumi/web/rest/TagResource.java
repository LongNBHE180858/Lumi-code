package com.lumi.web.rest;

import com.lumi.repository.TagRepository;
import com.lumi.service.dto.CatalogItemDTO;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tags")
public class TagResource {

    private final TagRepository tags;

    public TagResource(TagRepository tags) {
        this.tags = tags;
    }

    @GetMapping
    public List<CatalogItemDTO> list() {
        return tags
            .findByActiveTrueOrderByNameAsc()
            .stream()
            .map(tag -> new CatalogItemDTO(tag.getId(), tag.getName(), null, null, null, tag.getActive()))
            .toList();
    }
}
