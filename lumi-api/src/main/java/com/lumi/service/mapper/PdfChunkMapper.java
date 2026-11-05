package com.lumi.service.mapper;

import com.lumi.domain.LearningDocument;
import com.lumi.domain.PdfChunk;
import com.lumi.service.dto.LearningDocumentDTO;
import com.lumi.service.dto.PdfChunkDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PdfChunk} and its DTO {@link PdfChunkDTO}.
 */
@Mapper(componentModel = "spring")
public interface PdfChunkMapper extends EntityMapper<PdfChunkDTO, PdfChunk> {
    @Mapping(target = "material", source = "material", qualifiedByName = "learningDocumentId")
    PdfChunkDTO toDto(PdfChunk s);

    @Named("learningDocumentId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    LearningDocumentDTO toDtoLearningDocumentId(LearningDocument learningDocument);
}
