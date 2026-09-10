package com.pointwest.prop.pricing.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pointwest.prop.common.entity.CatalogItem;
import com.pointwest.prop.common.entity.LineItem;
import com.pointwest.prop.common.entity.Proposal;
import com.pointwest.prop.common.exception.BadRequestException;
import com.pointwest.prop.common.exception.ResourceNotFoundException;
import com.pointwest.prop.pricing.dto.LineItemRequestDto;
import com.pointwest.prop.pricing.dto.LineItemResponseDto;
import com.pointwest.prop.pricing.mapper.LineItemMapper;
import com.pointwest.prop.pricing.repository.LineItemRepository;
import com.pointwest.prop.proposals.service.ProposalService;

@ExtendWith(MockitoExtension.class)
class LineItemServiceTest {

    @Mock private LineItemRepository repository;
    @Mock private ProposalService proposalService;
    @Mock private CatalogItemService catalogItemService;
    @Mock private LineItemMapper mapper;
    @InjectMocks private LineItemService service;

    @Test
    void createUsesCatalogDefaultsAndRecalculatesProposalTotal() {
        var proposal = new Proposal();
        proposal.setId(7L);
        var catalog = new CatalogItem(3L, "Consulting", "Services", new BigDecimal("100.00"));
        var request = new LineItemRequestDto(3L, null, new BigDecimal("2"), null, null, null);
        var response = LineItemResponseDto.builder().id(1L).build();
        var savedItem = new AtomicReference<LineItem>();
        when(proposalService.getProposalEntity(7L)).thenReturn(proposal);
        when(catalogItemService.getCatalogItemEntity(3L)).thenReturn(catalog);
        when(repository.save(any(LineItem.class))).thenAnswer(invocation -> {
            LineItem item = invocation.getArgument(0);
            savedItem.set(item);
            return item;
        });
        when(repository.findAllByProposalId(7L)).thenAnswer(invocation -> List.of(savedItem.get()));
        doAnswer(invocation -> {
            var target = invocation.getArgument(1, LineItem.class);
            target.setQuantity(request.getQuantity());
            return null;
        }).when(mapper).updateEntityFromRequest(eq(request), any(LineItem.class));
        when(mapper.toResponse(any(LineItem.class))).thenReturn(response);

        assertEquals(response, service.createLineItem(7L, request));
        assertEquals("Consulting", savedItem.get().getDescription());
        assertEquals(new BigDecimal("100.00"), savedItem.get().getUnitPrice());
        assertEquals(new BigDecimal("200.00"), proposal.getContractValue());
        verify(proposalService).saveProposalEntity(proposal);
    }

    @Test
    void createWithoutDescriptionOrCatalogPriceFails() {
        var proposal = new Proposal();
        proposal.setId(7L);
        var request = new LineItemRequestDto(null, null, BigDecimal.ONE, null, null, null);
        when(proposalService.getProposalEntity(7L)).thenReturn(proposal);
        doAnswer(invocation -> null).when(mapper).updateEntityFromRequest(eq(request), any(LineItem.class));

        var exception = assertThrows(BadRequestException.class, () -> service.createLineItem(7L, request));
        assertEquals("description is required (the selected catalog item has no name to fall back on)",
                exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void updateRequiresLineItemToBelongToProposal() {
        when(repository.findByIdAndProposalId(2L, 7L)).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class,
                () -> service.updateLineItem(7L, 2L,
                        new LineItemRequestDto(null, "Support", BigDecimal.ONE, BigDecimal.TEN, null, null)));
        assertEquals("LineItem not found with id: '2'", exception.getMessage());
    }
}
