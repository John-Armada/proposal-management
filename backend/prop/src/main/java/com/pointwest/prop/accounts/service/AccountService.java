package com.pointwest.prop.accounts.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pointwest.prop.accounts.dto.AccountRequestDto;
import com.pointwest.prop.accounts.dto.AccountResponseDto;
import com.pointwest.prop.accounts.entity.Account;
import com.pointwest.prop.accounts.repository.AccountRepository;
import com.pointwest.prop.auth.model.Permission;
import com.pointwest.prop.common.exception.ConflictException;
import com.pointwest.prop.common.exception.ResourceNotFoundException;
import com.pointwest.prop.intake.service.ProposalRequestService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final ProposalRequestService proposalRequestService;

    @PreAuthorize("hasAuthority('" + Permission.ACCOUNT_VIEW + "')")
    @Transactional(readOnly = true)
    public List<AccountResponseDto> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    @PreAuthorize("hasAuthority('" + Permission.ACCOUNT_VIEW + "')")
    @Transactional(readOnly = true)
    public AccountResponseDto getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));
        return mapToDto(account);
    }

    @PreAuthorize("hasAuthority('" + Permission.ACCOUNT_CREATE + "')")
    @Transactional
    public AccountResponseDto createAccount(AccountRequestDto request) {
        if (accountRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new ConflictException("An account with this name already exists.");
        }
        Account account = new Account(null, request.getName().trim(), request.getIndustry(), request.getPrimaryContact());
        return mapToDto(accountRepository.save(account));
    }

    @PreAuthorize("hasAuthority('" + Permission.ACCOUNT_EDIT + "')")
    @Transactional
    public AccountResponseDto updateAccount(Long id, AccountRequestDto request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));
        if (accountRepository.existsByNameIgnoreCaseAndIdNot(request.getName().trim(), id)) {
            throw new ConflictException("Another account with this name already exists.");
        }
        account.setName(request.getName().trim());
        account.setIndustry(request.getIndustry());
        account.setPrimaryContact(request.getPrimaryContact());
        return mapToDto(accountRepository.save(account));
    }

    @PreAuthorize("hasAuthority('" + Permission.ACCOUNT_DELETE + "')")
    @Transactional
    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));

        // Enforces Service-to-Service boundary instead of calling ProposalRequestRepository directly
        if (proposalRequestService.hasRequestsForAccount(id)) {
            throw new ConflictException("Cannot delete an account because linked Proposal Requests exist.");
        }
        accountRepository.delete(account);
    }

    public Account findById(Long id){
        return accountRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Cannot find Account Entity with ID: " + id));
    }

    private AccountResponseDto mapToDto(Account a) {
        return new AccountResponseDto(a.getId(), a.getName(), a.getIndustry(), a.getPrimaryContact());
    }
}