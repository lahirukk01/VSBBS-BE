package com.lkksoftdev.accountservice.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByCustomerId(Long customerId);

    @Query("SELECT new com.lkksoftdev.accountservice.account.AccountDto(a.id, a.customerId, a.accountType, a.balance, a.createdAt, a.updatedAt) FROM Account a WHERE a.customerId = :customerId")
    List<AccountDto> findAccountsByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT new com.lkksoftdev.accountservice.account.AccountDto(a.id, a.customerId, a.accountType, a.balance, a.createdAt, a.updatedAt) FROM Account a WHERE a.customerId = :customerId AND a.id = :accountId")
    AccountDto findByCustomerIdAndId(@Param("customerId") Long customerId, @Param("accountId") Long accountId);
}
