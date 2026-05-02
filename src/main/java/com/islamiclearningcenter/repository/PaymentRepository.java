package com.islamiclearningcenter.repository;

import com.islamiclearningcenter.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {}
