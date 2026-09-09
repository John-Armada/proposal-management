package com.pointwest.prop.common.repository;

import com.pointwest.prop.common.entity.ProposalVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProposalVersionRepository extends JpaRepository<ProposalVersion, Long> {
}