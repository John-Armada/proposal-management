package com.pointwest.prop.intake.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;   

import com.pointwest.prop.intake.entity.ProposalRequest;

@Repository
public interface ProposalRequestRepository extends JpaRepository<ProposalRequest, Long> {

    boolean existsByAccountId(Long accountId);

    // Eagerly joins Account, Assigned Author, Department, and Offering avoiding N+1
    @Query("SELECT pr FROM ProposalRequest pr " +
           "JOIN FETCH pr.account " +
           "JOIN FETCH pr.assignedAuthor " +
           "JOIN FETCH pr.department " +
           "JOIN FETCH pr.offering")
    List<ProposalRequest> findAllWithDetails();

    @Query("SELECT pr FROM ProposalRequest pr " +
           "JOIN FETCH pr.account " +
           "JOIN FETCH pr.assignedAuthor " +
           "JOIN FETCH pr.department " +
           "JOIN FETCH pr.offering " +
           "WHERE pr.id = :id")
    Optional<ProposalRequest> findByIdWithDetails(@Param("id") Long id);
}