package es.evolucionia.microloan;

import java.math.BigDecimal;

public class LoanController {

    private final LoanService service;

    public LoanController(LoanService service) {
        this.service = service;
    }

    public LoanApplication createApplication(Applicant applicant, BigDecimal amount, int termMonths, String purpose) {
        return service.createApplication(applicant, amount, termMonths, purpose);
    }

    public void submitApplication(int applicationId) {
        service.submitApplication(applicationId);
    }

    public void reviewApplication(int applicationId) {
        service.reviewApplication(applicationId);
    }

    public void approveApplication(int applicationId, boolean simulateFailure) {
        service.approveApplication(applicationId, simulateFailure);
    }

    public void rejectApplication(int applicationId) {
        service.rejectApplication(applicationId);
    }
}