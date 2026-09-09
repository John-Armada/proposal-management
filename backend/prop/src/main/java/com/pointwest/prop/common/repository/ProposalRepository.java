package com.pointwest.prop.common.repository;

import com.pointwest.prop.common.entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository 
public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    
    Proposal findByRequestId(Long requestId);
}
