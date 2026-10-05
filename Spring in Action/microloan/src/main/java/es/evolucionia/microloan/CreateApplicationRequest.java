package es.evolucionia.microloan;

import java.math.BigDecimal;

public record CreateApplicationRequest(int applicantId, BigDecimal amount, int termMonths, String purpose) {
}