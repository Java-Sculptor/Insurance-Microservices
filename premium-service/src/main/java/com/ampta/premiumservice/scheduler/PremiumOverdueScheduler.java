package com.ampta.premiumservice.scheduler;

import com.ampta.premiumservice.service.PremiumScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PremiumOverdueScheduler {

    private final PremiumScheduleService premiumScheduleService;

    /**
     * Daily Overdue Assessment & Late Fee Calculation Job.
     * Runs at 00:00 (Midnight) every day.
     * Evaluates all unpaid installments past their grace period, marks them OVERDUE,
     * and calculates the dynamic late fee per configured penalty rate.
     */
    @Scheduled(cron = "${premium.scheduler.overdue.cron:0 0 0 * * ?}")
    public void runDailyOverdueAssessment() {
        log.info("Starting Daily Overdue Assessment & Late Fee Calculation Job...");
        try {
            int updated = premiumScheduleService.assessOverdueAndCalculateLateFees();
            log.info("Daily Overdue Assessment Job completed. {} schedules updated.", updated);
        } catch (Exception e) {
            log.error("Error occurred during Daily Overdue Assessment: {}", e.getMessage(), e);
        }
    }
}
