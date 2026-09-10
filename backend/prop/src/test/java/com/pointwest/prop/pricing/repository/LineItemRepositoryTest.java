package com.pointwest.prop.pricing.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import com.pointwest.prop.common.entity.LineItem;
import com.pointwest.prop.common.entity.Proposal;
import com.pointwest.prop.proposals.enums.ProposalStatus;
import com.pointwest.prop.proposals.repository.ProposalRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=none"
})
class LineItemRepositoryTest {

    @Autowired
    private LineItemRepository repository;

    @Autowired
    private ProposalRepository proposalRepository;

    @Test
    void findsItemsByProposalAndScopesLookupToProposal() {
        var proposal = new Proposal();
        proposal.setTitle("Pricing proposal");
        proposal.setStatus(ProposalStatus.DRAFT);
        proposal = proposalRepository.save(proposal);

        var item = new LineItem(null, "Support", BigDecimal.ONE, BigDecimal.TEN,
                BigDecimal.ZERO, BigDecimal.ZERO, proposal, null);
        item = repository.save(item);

        assertEquals(1, repository.findByProposalId(proposal.getId(), PageRequest.of(0, 20)).getTotalElements());
        assertTrue(repository.findByIdAndProposalId(item.getId(), proposal.getId()).isPresent());
        assertTrue(repository.findByIdAndProposalId(item.getId(), proposal.getId() + 1).isEmpty());
        assertEquals(1, repository.findAllByProposalId(proposal.getId()).size());
    }
}
