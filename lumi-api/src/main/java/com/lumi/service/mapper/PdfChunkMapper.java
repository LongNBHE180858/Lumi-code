package com.lumi.service.mapper;

import com.lumi.domain.CourseMaterial;
import com.lumi.domain.PdfChunk;
import com.lumi.service.dto.CourseMaterialDTO;
import com.lumi.service.dto.PdfChunkDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PdfChunk} and its DTO {@link PdfChunkDTO}.
 */
@Mapper(componentModel = "spring")
public interface PdfChunkMapper extends EntityMapper<PdfChunkDTO, PdfChunk> {
    @Mapping(target = "material", source = "material", qualifiedByName = "courseMaterialId")
    PdfChunkDTO toDto(PdfChunk s);

    @Named("courseMaterialId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CourseMaterialDTO toDtoCourseMaterialId(CourseMaterial courseMaterial);
}
