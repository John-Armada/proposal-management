package com.pointwest.prop.intake.entity;

import java.time.LocalDate;

import com.pointwest.prop.accounts.entity.Account;
import com.pointwest.prop.common.entity.Department;
import com.pointwest.prop.common.entity.Offering;
import com.pointwest.prop.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
// !IMPORTANT: Need to Document
@Table(name = "proposal_requests", indexes = {
    @jakarta.persistence.Index(name = "idx_pr_deadline_status", columnList = "deadline, status"),
    @jakarta.persistence.Index(name = "idx_pr_dept_offering", columnList = "department_id, offering_id")
})
public class ProposalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "requirements_summary", columnDefinition = "TEXT", nullable = false)
    private String requirementsSummary;

    @Column(nullable = false)
    private LocalDate deadline;

    @Column(nullable = false)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_author_id", nullable = false)
    private User assignedAuthor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offering_id", nullable = false)
    private Offering offering;
}