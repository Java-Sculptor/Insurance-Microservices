package com.ampta.premiumservice.entity;

import com.ampta.premiumservice.entity.enums.DeliveryStatus;
import com.ampta.premiumservice.entity.enums.ReminderType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "premium_reminders", indexes = {
        @Index(name = "idx_reminder_schedule", columnList = "schedule_id")
})
public class PremiumReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reminderId;

    @Column(nullable = false)
    private Long scheduleId;

    private Long policyId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReminderType reminderType;

    private LocalDateTime reminderDate;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private DeliveryStatus deliveryStatus = DeliveryStatus.PENDING;

    @Column(length = 50)
    private String reminderStage; // e.g., T_MINUS_15, T_MINUS_7, DUE_DATE, T_PLUS_3, T_PLUS_7

    @Column(length = 500)
    private String remarks;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
