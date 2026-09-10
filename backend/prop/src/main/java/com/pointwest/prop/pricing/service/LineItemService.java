package com.pointwest.prop.pricing.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pointwest.prop.common.entity.CatalogItem;
import com.pointwest.prop.common.entity.LineItem;
import com.pointwest.prop.common.entity.Proposal;
import com.pointwest.prop.common.exception.BadRequestException;
import com.pointwest.prop.common.exception.ResourceNotFoundException;
import com.pointwest.prop.pricing.dto.LineItemRequestDto;
import com.pointwest.prop.pricing.dto.LineItemResponseDto;
import com.pointwest.prop.pricing.mapper.LineItemMapper;
import com.pointwest.prop.pricing.repository.LineItemRepository;
import com.pointwest.prop.pricing.util.PricingCalculator;
import com.pointwest.prop.proposals.service.ProposalService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LineItemService {

    private final LineItemRepository lineItemRepository;
    private final ProposalService proposalService;
    private final CatalogItemService catalogItemService;
    private final LineItemMapper lineItemMapper;

    public Page<LineItemResponseDto> getLineItems(Long proposalId, Pageable pageable) {
        proposalService.getProposalEntity(proposalId);
        return lineItemRepository.findByProposalId(proposalId, pageable)
                .map(lineItemMapper::toResponse);
    }

    @Transactional
    public LineItemResponseDto createLineItem(Long proposalId, LineItemRequestDto request) {
        Proposal proposal = proposalService.getProposalEntity(proposalId);
        CatalogItem catalogItem = resolveCatalogItem(request.getCatalogItemId());

        LineItem lineItem = new LineItem();
        lineItem.setProposal(proposal);
        lineItem.setCatalogItem(catalogItem);
        lineItemMapper.updateEntityFromRequest(request, lineItem);
        applyCatalogFallback(lineItem, catalogItem);

        LineItem saved = lineItemRepository.save(lineItem);
        recalculateProposalTotal(proposal);
        return lineItemMapper.toResponse(saved);
    }

    @Transactional
    public LineItemResponseDto updateLineItem(Long proposalId, Long lineItemId, LineItemRequestDto request) {
        LineItem lineItem = getLineItemOrThrow(proposalId, lineItemId);
        CatalogItem catalogItem = resolveCatalogItem(request.getCatalogItemId());

        lineItem.setCatalogItem(catalogItem);
        lineItemMapper.updateEntityFromRequest(request, lineItem);
        applyCatalogFallback(lineItem, catalogItem);

        LineItem saved = lineItemRepository.save(lineItem);
        recalculateProposalTotal(lineItem.getProposal());
        return lineItemMapper.toResponse(saved);
    }

    @Transactional
    public void deleteLineItem(Long proposalId, Long lineItemId) {
        LineItem lineItem = getLineItemOrThrow(proposalId, lineItemId);
        Proposal proposal = lineItem.getProposal();
        lineItemRepository.delete(lineItem);
        recalculateProposalTotal(proposal);
    }

    private void applyCatalogFallback(LineItem lineItem, CatalogItem catalogItem) {
        if (isBlank(lineItem.getDescription()) && catalogItem != null) {
            lineItem.setDescription(catalogItem.getName());
        }
        if (lineItem.getUnitPrice() == null && catalogItem != null) {
            lineItem.setUnitPrice(catalogItem.getDefaultUnitPrice());
        }

        if (isBlank(lineItem.getDescription())) {
            throw new BadRequestException(
                    "description is required (the selected catalog item has no name to fall back on)");
        }
        if (lineItem.getUnitPrice() == null) {
            throw new BadRequestException(
                    "unitPrice is required (the selected catalog item has no defaultUnitPrice to fall back on)");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void recalculateProposalTotal(Proposal proposal) {
        List<LineItem> items = lineItemRepository.findAllByProposalId(proposal.getId());

        BigDecimal total = items.stream()
                .map(item -> PricingCalculator.computeLineTotal(
                        item.getQuantity(), item.getUnitPrice(), item.getDiscountPct(), item.getTaxPct()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        proposal.setContractValue(total);
        proposalService.saveProposalEntity(proposal);
    }

    private CatalogItem resolveCatalogItem(Long catalogItemId) {
        if (catalogItemId == null) {
            return null;
        }
        return catalogItemService.getCatalogItemEntity(catalogItemId);
    }

    private LineItem getLineItemOrThrow(Long proposalId, Long lineItemId) {
        return lineItemRepository.findByIdAndProposalId(lineItemId, proposalId)
                .orElseThrow(() -> new ResourceNotFoundException("LineItem", "id", lineItemId));
    }
}