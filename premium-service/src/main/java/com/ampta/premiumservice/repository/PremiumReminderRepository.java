package com.ampta.premiumservice.repository;

import com.ampta.premiumservice.entity.PremiumReminder;
import com.ampta.premiumservice.entity.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PremiumReminderRepository extends JpaRepository<PremiumReminder, Long> {
    List<PremiumReminder> findByScheduleId(Long scheduleId);
    List<PremiumReminder> findByPolicyId(Long policyId);
    List<PremiumReminder> findByDeliveryStatus(DeliveryStatus status);
    boolean existsByScheduleIdAndReminderStageAndCreatedAtAfter(Long scheduleId, String reminderStage, LocalDateTime after);
}
