package com.example.customerservice.service;

import com.example.customerservice.dto.NomineeRequest;
import com.example.customerservice.dto.NomineeResponse;

import java.util.List;

public interface NomineeService {

    NomineeResponse addNominee(Long customerId, NomineeRequest request);

    List<NomineeResponse> getNomineesByCustomerId(Long customerId);

    NomineeResponse updateNominee(Long nomineeId, NomineeRequest request);

    void deleteNominee(Long nomineeId);
}
