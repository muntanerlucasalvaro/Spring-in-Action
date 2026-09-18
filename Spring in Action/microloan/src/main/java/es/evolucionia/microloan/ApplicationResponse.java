package es.evolucionia.microloan;

import java.math.BigDecimal;

public record ApplicationResponse(int id, String applicantName, BigDecimal amount, int termMonths, String purpose,
        String status) {

    public static ApplicationResponse from(LoanApplication application) {
        return new ApplicationResponse(
                application.getId(),
                application.getApplicant().getFullName(),
                application.getAmount(),
                application.getTermMonths(),
                application.getPurpose(),
                application.getStatus().toString());
    }
}