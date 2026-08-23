package es.evolucionia.microloan;

import java.math.BigDecimal;

public class Applicant {

    private final int id;
    private final String fullName;
    private final String email;
    private final BigDecimal monthlyIncome;

    public Applicant(int id, String fullName, String email, BigDecimal monthlyIncome) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.monthlyIncome = monthlyIncome;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Applicant other = (Applicant) obj;
        if (id != other.id)
            return false;
        return true;
    }

}