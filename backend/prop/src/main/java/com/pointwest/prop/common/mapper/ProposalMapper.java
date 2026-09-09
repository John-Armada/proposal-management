package com.pointwest.prop.common.mapper;

import com.pointwest.prop.common.entity.Proposal;
import com.pointwest.prop.proposals.dto.CreateProposalRequestDto;
import com.pointwest.prop.proposals.dto.ProposalResponseDto;
import com.pointwest.prop.proposals.dto.UpdateProposalRequestDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ProposalMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "DRAFT")
    @Mapping(target = "currentVersion", constant = "1")
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "request", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "template", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "offering", ignore = true)
    Proposal toEntity(CreateProposalRequestDto dto);

    @Mapping(target = "requestId", source = "request.id")
    @Mapping(target = "requirementsSummary", source = "request.requirementsSummary") 
    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "accountName", source = "account.name")
    @Mapping(target = "templateId", source = "template.id")
    @Mapping(target = "templateName", source = "template.name")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "offeringId", source = "offering.id")
    @Mapping(target = "offeringName", source = "offering.name")
    ProposalResponseDto toDto(Proposal entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "currentVersion", ignore = true)
    @Mapping(target = "request", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "template", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "offering", ignore = true)
    void updateEntityFromDto(UpdateProposalRequestDto dto, @MappingTarget Proposal entity);
}
