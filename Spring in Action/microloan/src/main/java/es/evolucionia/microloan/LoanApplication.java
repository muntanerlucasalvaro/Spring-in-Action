package es.evolucionia.microloan;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LoanApplication {

    private final int id;
    private final Applicant applicant;
    private final BigDecimal amount;
    private final int termMonths;
    private final String purpose;
    private LoanStatus status;
    private final LocalDate createdAt;

    public LoanApplication(int id, Applicant applicant, BigDecimal amount, int termMonths, String purpose) {
        // r1
        if (amount.compareTo(new BigDecimal("500")) < 0 || amount.compareTo(new BigDecimal("15000")) > 0) {
            throw new InvalidLoanException("Amount must be between 500 and 15000");
        }
        // r2
        if (termMonths < 3 || termMonths > 36) {
            throw new InvalidLoanException("Term months must be between 3 and 36");
        }

        this.id = id;
        this.applicant = applicant;
        this.amount = amount;
        this.termMonths = termMonths;
        this.purpose = purpose;
        this.status = LoanStatus.DRAFT;
        this.createdAt = LocalDate.now();
    }

    public int getId() {
        return id;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public int getTermMonths() {
        return termMonths;
    }

    public String getPurpose() {
        return purpose;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    // LoanService
    public void setStatus(LoanStatus status) {
        this.status = status;
    }

}