package com.ampta.premiumservice.repository;

import com.ampta.premiumservice.entity.PremiumSchedule;
import com.ampta.premiumservice.entity.enums.InstallmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PremiumScheduleRepository extends JpaRepository<PremiumSchedule, Long> {

    List<PremiumSchedule> findByPolicyIdOrderByInstallmentNoAsc(Long policyId);
    List<PremiumSchedule> findByStatusAndDueDateBefore(InstallmentStatus status, LocalDate date);
    List<PremiumSchedule> findByStatusInAndDueDateBefore(List<InstallmentStatus> statuses, LocalDate date);
    List<PremiumSchedule> findByStatusInAndDueDate(List<InstallmentStatus> statuses, LocalDate date);
    List<PremiumSchedule> findByStatusIn(List<InstallmentStatus> statuses);
    Optional<PremiumSchedule> findByPolicyIdAndInstallmentNo(Long policyId, Integer installmentNo);
}
