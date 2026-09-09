package com.pointwest.prop.common.repository;

import com.pointwest.prop.common.entity.ProposalRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProposalRequestRepository extends JpaRepository<ProposalRequest, Long> {
}