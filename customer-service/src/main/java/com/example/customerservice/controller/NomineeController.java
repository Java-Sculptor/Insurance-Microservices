package com.example.customerservice.controller;

import com.example.customerservice.dto.NomineeRequest;
import com.example.customerservice.dto.NomineeResponse;
import com.example.customerservice.service.NomineeService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class NomineeController {

    private final NomineeService nomineeService;

    public NomineeController(NomineeService nomineeService) {
        this.nomineeService = nomineeService;
    }

    // 1. Add nominee
    @PostMapping("/customers/{id}/nominees")
    public ResponseEntity<NomineeResponse> addNominee(
            @PathVariable("id") Long customerId,
            @Valid @RequestBody NomineeRequest request) {

        NomineeResponse response =
                nomineeService.addNominee(customerId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Get all nominees for a customer
    @GetMapping("/customers/{id}/nominees")
    public ResponseEntity<List<NomineeResponse>> getNominees(
            @PathVariable("id") Long customerId) {

        List<NomineeResponse> nominees =
                nomineeService.getNomineesByCustomerId(customerId);

        return ResponseEntity.ok(nominees);
    }

    // 3. Update nominee
    @PutMapping("/nominees/{nomineeId}")
    public ResponseEntity<NomineeResponse> updateNominee(
            @PathVariable Long nomineeId,
            @Valid @RequestBody NomineeRequest request) {

        NomineeResponse response =
                nomineeService.updateNominee(nomineeId, request);

        return ResponseEntity.ok(response);
    }

    // 4. Delete nominee
    @DeleteMapping("/nominees/{nomineeId}")
    public ResponseEntity<Void> deleteNominee(
            @PathVariable Long nomineeId) {

        nomineeService.deleteNominee(nomineeId);

        return ResponseEntity.noContent().build();
    }
}
