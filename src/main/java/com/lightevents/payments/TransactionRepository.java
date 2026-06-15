package com.lightevents.payments;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface TransactionRepository extends JpaRepository<Transaction, Long> { Optional<Transaction> findByReference(String reference); }
