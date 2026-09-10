package es.evolucionia.microloan;

import java.math.BigDecimal;

public class LoanController {

    private final LoanRepository repository;
    private final LoanService service;

    public LoanController(LoanRepository repository, LoanService service) {
        this.repository = repository;
        this.service = service;
    }

    public LoanApplication createApplication(Applicant applicant, BigDecimal amount, int termMonths, String purpose) {
        int newId = repository.findAll().size() + 1;
        LoanApplication application = service.createApplication(newId, applicant, amount, termMonths, purpose);
        repository.save(application);
        return application;
    }

    public void submitApplication(int applicationId) {
        LoanApplication application = repository.findById(applicationId)
                .orElseThrow(() -> new InvalidLoanException("Application not found"));
        service.changeStatus(application, LoanStatus.SUBMITTED);
        repository.updateStatus(application);
    }

    public void reviewApplication(int applicationId) {
        LoanApplication application = repository.findById(applicationId)
                .orElseThrow(() -> new InvalidLoanException("Application not found"));
        service.changeStatus(application, LoanStatus.UNDER_REVIEW);
        repository.updateStatus(application);
    }

    public void approveApplication(int applicationId, boolean simulateFailure) {
        LoanApplication application = repository.findById(applicationId)
                .orElseThrow(() -> new InvalidLoanException("Application not found"));

        LoanStatus oldStatus = application.getStatus();
        service.changeStatus(application, LoanStatus.APPROVED);

        ((JdbcLoanRepository) repository).approveWithHistory(application, oldStatus, simulateFailure);
    }

    public void rejectApplication(int applicationId) {
        LoanApplication application = repository.findById(applicationId)
                .orElseThrow(() -> new InvalidLoanException("Application not found"));
        service.changeStatus(application, LoanStatus.REJECTED);
        repository.updateStatus(application);
    }
}