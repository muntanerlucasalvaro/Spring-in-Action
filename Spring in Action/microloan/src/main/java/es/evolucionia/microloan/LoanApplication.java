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

}