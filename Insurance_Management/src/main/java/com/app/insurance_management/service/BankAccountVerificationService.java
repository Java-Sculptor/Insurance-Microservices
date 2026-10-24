
package com.app.insurance_management.service;

public interface BankAccountVerificationService {

    boolean verifyBankAccount(
            Long claimId,
            String bankAccountNumber,
            String bankIfscCode
    );
}
