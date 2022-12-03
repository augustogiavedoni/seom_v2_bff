package com.agprogramming.seom_v2_bff.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agprogramming.seom_v2_bff.models.Receipt;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {}
