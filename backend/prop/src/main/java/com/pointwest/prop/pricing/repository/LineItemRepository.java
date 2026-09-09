package com.pointwest.prop.pricing.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pointwest.prop.common.entity.LineItem;

public interface LineItemRepository extends JpaRepository<LineItem, Long> {

    Page<LineItem> findByProposalId(Long proposalId, Pageable pageable);

    Optional<LineItem> findByIdAndProposalId(Long id, Long proposalId);

    List<LineItem> findAllByProposalId(Long proposalId);
}