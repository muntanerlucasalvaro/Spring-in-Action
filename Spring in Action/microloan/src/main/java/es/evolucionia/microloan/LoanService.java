package es.evolucionia.microloan;

import java.math.BigDecimal;

public class LoanService {

    public void changeStatus(LoanApplication application, LoanStatus newStatus) {
        LoanStatus current = application.getStatus();

        // r5
        if (current == LoanStatus.APPROVED || current == LoanStatus.REJECTED) {
            throw new InvalidLoanException("Cannot change status from " + current);
        }
        // r4
        boolean isValidTransition = (current == LoanStatus.DRAFT && newStatus == LoanStatus.SUBMITTED) ||
                (current == LoanStatus.SUBMITTED && newStatus == LoanStatus.UNDER_REVIEW) ||
                (current == LoanStatus.UNDER_REVIEW && newStatus == LoanStatus.APPROVED) ||
                (current == LoanStatus.UNDER_REVIEW && newStatus == LoanStatus.REJECTED);
        if (!isValidTransition) {
            throw new InvalidLoanException("Invalid status transition from " + current + " to " + newStatus);
        }

        // r3
        if (newStatus == LoanStatus.APPROVED) {
            BigDecimal amount = application.getAmount();
            BigDecimal monthlyIncome = application.getApplicant().getMonthlyIncome();
            BigDecimal maxAllowedAmount = monthlyIncome.multiply(new BigDecimal("4"));
            if (amount.compareTo(maxAllowedAmount) > 0) {
                throw new InvalidLoanException("Loan amount exceeds 4 times the applicant's monthly income");
            }

        }
        application.setStatus(newStatus);
    }

}