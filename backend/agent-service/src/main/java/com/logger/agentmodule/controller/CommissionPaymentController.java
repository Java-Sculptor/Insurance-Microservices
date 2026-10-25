package com.logger.agentmodule.controller;
import com.logger.agentmodule.dto.PayoutCreateRequest;
import com.logger.agentmodule.entity.CommissionPayment;
import com.logger.agentmodule.service.CommissionPaymentService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CommissionPaymentController {

    private final CommissionPaymentService paymentService;

    public CommissionPaymentController(
            CommissionPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Initiate payouts for approved commissions
    @PostMapping("/payments/payouts")
    public ResponseEntity<List<CommissionPayment>> processPayouts(
            @Valid @RequestBody PayoutCreateRequest request) {

        List<CommissionPayment> payouts =
                paymentService.processPayouts(request.getPaymentMode());

        return ResponseEntity.status(HttpStatus.CREATED).body(payouts);
    }

    // Get all payout history
    @GetMapping("/payments/payouts")
    public ResponseEntity<List<CommissionPayment>> getAllPayouts() {
        return ResponseEntity.ok(paymentService.getAllPayouts());
    }

    // Get payout history for a specific agent
    @GetMapping("/agents/{agentId}/payments/payouts")
    public ResponseEntity<List<CommissionPayment>> getPayoutsByAgentId(
            @PathVariable Long agentId) {

        return ResponseEntity.ok(
                paymentService.getPayoutsByAgentId(agentId));
    }
}

