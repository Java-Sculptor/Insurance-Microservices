package com.ampta.premiumservice.scheduler;

import com.ampta.premiumservice.dto.ScheduleResponseDto;
import com.ampta.premiumservice.entity.PremiumReminder;
import com.ampta.premiumservice.entity.enums.DeliveryStatus;
import com.ampta.premiumservice.entity.enums.ReminderType;
import com.ampta.premiumservice.repository.PremiumReminderRepository;
import com.ampta.premiumservice.service.PremiumScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PremiumReminderScheduler {

    private final PremiumScheduleService premiumScheduleService;
    private final PremiumReminderRepository reminderRepository;

    /**
     * Daily Premium Reminder Dispatcher Job.
     * Runs at 08:00 AM every day.
     * Complies with BRD FR-PRM-008:
     * - 15 Days Before Due Date
     * - 7 Days Before Due Date
     * - Due Date
     * - 3 Days After Due Date
     * - 7 Days After Due Date
     */
    @Scheduled(cron = "${premium.scheduler.reminder.cron:0 0 8 * * ?}")
    public void runDailyReminderDispatcher() {
        log.info("Starting Daily Premium Reminder Dispatcher Job...");
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();

        int sentCount = 0;
        sentCount += processRemindersForStage(today.plusDays(15), "T_MINUS_15", "Reminder: Premium installment is due in 15 days", todayStart);
        sentCount += processRemindersForStage(today.plusDays(7), "T_MINUS_7", "Reminder: Premium installment is due in 7 days", todayStart);
        sentCount += processRemindersForStage(today, "DUE_DATE", "Urgent: Premium installment is due today", todayStart);
        sentCount += processRemindersForStage(today.minusDays(3), "T_PLUS_3", "Notice: Premium installment is 3 days overdue (Grace period active)", todayStart);
        sentCount += processRemindersForStage(today.minusDays(7), "T_PLUS_7", "Critical Notice: Premium installment is 7 days overdue", todayStart);

        log.info("Daily Premium Reminder Job completed. Total reminders generated: {}", sentCount);
    }

    private int processRemindersForStage(LocalDate targetDate, String stage, String messageTemplate, LocalDateTime todayStart) {
        List<ScheduleResponseDto> schedules = premiumScheduleService.getPendingSchedulesByDueDate(targetDate);
        int count = 0;

        for (ScheduleResponseDto s : schedules) {
            boolean alreadySent = reminderRepository.existsByScheduleIdAndReminderStageAndCreatedAtAfter(
                    s.getScheduleId(), stage, todayStart
            );

            if (!alreadySent) {
                PremiumReminder reminder = PremiumReminder.builder()
                        .scheduleId(s.getScheduleId())
                        .policyId(s.getPolicyId())
                        .reminderType(ReminderType.EMAIL)
                        .reminderDate(LocalDateTime.now())
                        .reminderStage(stage)
                        .deliveryStatus(DeliveryStatus.SENT)
                        .remarks(messageTemplate + " for Policy ID: " + s.getPolicyId() + " (Installment #" + s.getInstallmentNo() + ", Amount: ₹" + s.getTotalPayable() + ")")
                        .build();

                reminderRepository.save(reminder);
                count++;
            }
        }
        return count;
    }
}
