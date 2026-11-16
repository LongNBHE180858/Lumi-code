package com.lumi.web.rest;

import com.lumi.service.GradeService;
import com.lumi.service.criteria.GradeCriteria;
import com.lumi.service.dto.GradeDTO;
import com.lumi.service.query.GradeQueryService;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api/grades")
public class GradeResource {

    private final GradeService gradeService;
    private final GradeQueryService gradeQueryService;

    public GradeResource(GradeService gradeService, GradeQueryService gradeQueryService) {
        this.gradeService = gradeService;
        this.gradeQueryService = gradeQueryService;
    }

    @GetMapping("")
    public ResponseEntity<List<GradeDTO>> getAllGrades(
        GradeCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        Page<GradeDTO> page = gradeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countGrades(GradeCriteria criteria) {
        return ResponseEntity.ok().body(gradeQueryService.countByCriteria(criteria));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GradeDTO> getGrade(@PathVariable("id") Long id) {
        Optional<GradeDTO> gradeDTO = gradeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(gradeDTO);
    }
}
