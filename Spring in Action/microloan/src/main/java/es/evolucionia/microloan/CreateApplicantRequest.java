package es.evolucionia.microloan;

import java.math.BigDecimal;

public record CreateApplicantRequest(String fullName, String email, BigDecimal monthlyIncome) {
}