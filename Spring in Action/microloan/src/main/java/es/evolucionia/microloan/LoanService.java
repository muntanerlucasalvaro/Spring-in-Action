package es.evolucionia.microloan;

import java.math.BigDecimal;

public class LoanService {

    private final LoanRepository repository;

    public LoanService(LoanRepository repository) {
        this.repository = repository;
    }

    public LoanApplication createApplication(Applicant applicant, BigDecimal amount, int termMonths, String purpose) {
        // r1
        if (amount.compareTo(new BigDecimal("500")) < 0 || amount.compareTo(new BigDecimal("15000")) > 0) {
            throw new InvalidLoanException("Amount must be between 500 and 15000");
        }
        // r2
        if (termMonths < 3 || termMonths > 36) {
            throw new InvalidLoanException("Term months must be between 3 and 36");
        }

        int newId = repository.findAll().size() + 1;
        LoanApplication application = new LoanApplication(newId, applicant, amount, termMonths, purpose);
        repository.save(application);
        return application;
    }

    public void submitApplication(int applicationId) {
        LoanApplication application = repository.findById(applicationId)
                .orElseThrow(() -> new InvalidLoanException("Application not found"));
        changeStatus(application, LoanStatus.SUBMITTED);
        repository.updateStatus(application);
    }

    public void reviewApplication(int applicationId) {
        LoanApplication application = repository.findById(applicationId)
                .orElseThrow(() -> new InvalidLoanException("Application not found"));
        changeStatus(application, LoanStatus.UNDER_REVIEW);
        repository.updateStatus(application);
    }

    public void approveApplication(int applicationId, boolean simulateFailure) {
        LoanApplication application = repository.findById(applicationId)
                .orElseThrow(() -> new InvalidLoanException("Application not found"));

        LoanStatus oldStatus = application.getStatus();
        changeStatus(application, LoanStatus.APPROVED);

        ((JdbcLoanRepository) repository).approveWithHistory(application, oldStatus, simulateFailure);
    }

    public void rejectApplication(int applicationId) {
        LoanApplication application = repository.findById(applicationId)
                .orElseThrow(() -> new InvalidLoanException("Application not found"));
        changeStatus(application, LoanStatus.REJECTED);
        repository.updateStatus(application);
    }

    private void changeStatus(LoanApplication application, LoanStatus newStatus) {

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