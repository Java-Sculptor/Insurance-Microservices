
package com.app.insurance_management.serviceImpl;

import com.app.insurance_management.service.BankAccountVerificationService;
import org.springframework.stereotype.Service;

@Service
public class BankAccountVerificationServiceImpl
        implements BankAccountVerificationService {

    @Override
    public boolean verifyBankAccount(
            Long claimId,
            String bankAccountNumber,
            String bankIfscCode) {

        // TODO: Add your actual bank verification logic here.
        // Do not treat format checks alone as real bank verification.

        return true;
    }
}
