package com.lumi.repository;

import com.lumi.domain.Tag;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByActiveTrueOrderByNameAsc();
    Optional<Tag> findByNameIgnoreCase(String name);
}
