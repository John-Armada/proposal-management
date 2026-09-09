package com.pointwest.prop.pricing.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pointwest.prop.common.entity.CatalogItem;
import com.pointwest.prop.common.entity.LineItem;
import com.pointwest.prop.common.entity.Proposal;
import com.pointwest.prop.common.exception.ResourceNotFoundException;
import com.pointwest.prop.pricing.dto.LineItemRequestDto;
import com.pointwest.prop.pricing.dto.LineItemResponseDto;
import com.pointwest.prop.pricing.repository.CatalogItemRepository;
import com.pointwest.prop.pricing.repository.LineItemRepository;
import com.pointwest.prop.pricing.repository.ProposalRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LineItemService {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final int MATH_SCALE = 10;
    private static final int MONEY_SCALE = 2;

    private final LineItemRepository lineItemRepository;
    private final ProposalRepository proposalRepository;
    private final CatalogItemRepository catalogItemRepository;

    public Page<LineItemResponseDto> getLineItems(Long proposalId, Pageable pageable) {
        ensureProposalExists(proposalId);
        return lineItemRepository.findByProposalId(proposalId, pageable)
                .map(this::toResponse);
    }

    @Transactional
    public LineItemResponseDto createLineItem(Long proposalId, LineItemRequestDto request) {
        Proposal proposal = getProposalOrThrow(proposalId);
        CatalogItem catalogItem = resolveCatalogItem(request.getCatalogItemId());

        LineItem lineItem = new LineItem();
        lineItem.setProposal(proposal);
        lineItem.setCatalogItem(catalogItem);
        applyRequest(lineItem, request);

        LineItem saved = lineItemRepository.save(lineItem);
        return toResponse(saved);
    }

    @Transactional
    public LineItemResponseDto updateLineItem(Long proposalId, Long lineItemId, LineItemRequestDto request) {
        LineItem lineItem = getLineItemOrThrow(proposalId, lineItemId);
        CatalogItem catalogItem = resolveCatalogItem(request.getCatalogItemId());

        lineItem.setCatalogItem(catalogItem);
        applyRequest(lineItem, request);

        LineItem saved = lineItemRepository.save(lineItem);
        return toResponse(saved);
    }

    @Transactional
    public void deleteLineItem(Long proposalId, Long lineItemId) {
        LineItem lineItem = getLineItemOrThrow(proposalId, lineItemId);
        lineItemRepository.delete(lineItem);
    }

    private void applyRequest(LineItem lineItem, LineItemRequestDto request) {
        lineItem.setDescription(request.getDescription());
        lineItem.setQuantity(request.getQuantity());
        lineItem.setUnitPrice(request.getUnitPrice());
        lineItem.setDiscountPct(request.getDiscountPct());
        lineItem.setTaxPct(request.getTaxPct());
    }

    private Proposal getProposalOrThrow(Long proposalId) {
        return proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ResourceNotFoundException("Proposal", "id", proposalId));
    }

    private void ensureProposalExists(Long proposalId) {
        if (!proposalRepository.existsById(proposalId)) {
            throw new ResourceNotFoundException("Proposal", "id", proposalId);
        }
    }

    private CatalogItem resolveCatalogItem(Long catalogItemId) {
        if (catalogItemId == null) {
            return null;
        }
        return catalogItemRepository.findById(catalogItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CatalogItem", "id", catalogItemId));
    }

    private LineItem getLineItemOrThrow(Long proposalId, Long lineItemId) {
        return lineItemRepository.findByIdAndProposalId(lineItemId, proposalId)
                .orElseThrow(() -> new ResourceNotFoundException("LineItem", "id", lineItemId));
    }

    private LineItemResponseDto toResponse(LineItem lineItem) {
        BigDecimal lineTotal = computeLineTotal(
                lineItem.getQuantity(),
                lineItem.getUnitPrice(),
                lineItem.getDiscountPct(),
                lineItem.getTaxPct());

        return LineItemResponseDto.builder()
                .id(lineItem.getId())
                .description(lineItem.getDescription())
                .catalogItemId(lineItem.getCatalogItem() != null ? lineItem.getCatalogItem().getId() : null)
                .quantity(lineItem.getQuantity())
                .unitPrice(lineItem.getUnitPrice())
                .discountPct(lineItem.getDiscountPct())
                .taxPct(lineItem.getTaxPct())
                .lineTotal(lineTotal)
                .build();
    }

    private BigDecimal computeLineTotal(BigDecimal quantity, BigDecimal unitPrice,
            BigDecimal discountPct, BigDecimal taxPct) {

        BigDecimal safeQuantity = quantity == null ? BigDecimal.ZERO : quantity;
        BigDecimal safeUnitPrice = unitPrice == null ? BigDecimal.ZERO : unitPrice;
        BigDecimal safeDiscount = discountPct == null ? BigDecimal.ZERO : discountPct;
        BigDecimal safeTax = taxPct == null ? BigDecimal.ZERO : taxPct;

        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                safeDiscount.divide(HUNDRED, MATH_SCALE, RoundingMode.HALF_UP));
        BigDecimal taxMultiplier = BigDecimal.ONE.add(
                safeTax.divide(HUNDRED, MATH_SCALE, RoundingMode.HALF_UP));

        return safeQuantity.multiply(safeUnitPrice)
                .multiply(discountMultiplier)
                .multiply(taxMultiplier)
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}