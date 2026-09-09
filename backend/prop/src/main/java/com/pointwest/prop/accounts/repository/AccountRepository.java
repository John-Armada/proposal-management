package com.pointwest.prop.accounts.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.pointwest.prop.accounts.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    Optional<Account> findByNameIgnoreCase(String name);
}