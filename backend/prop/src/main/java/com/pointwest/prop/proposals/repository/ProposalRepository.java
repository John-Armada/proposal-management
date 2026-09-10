package com.pointwest.prop.proposals.repository;

import com.pointwest.prop.common.entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Long>, JpaSpecificationExecutor<Proposal> {
    boolean existsByRequestId(Long requestId);
}
