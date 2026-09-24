package es.evolucionia.microloan;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/applications")
public class ApplicationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StringBuilder body = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }

        String json = body.toString();
        int applicantId = Integer.parseInt(extractValue(json, "applicantId"));
        BigDecimal amount = new BigDecimal(extractValue(json, "amount"));
        int termMonths = Integer.parseInt(extractValue(json, "termMonths"));
        String purpose = extractValue(json, "purpose");

        Applicant found = null;
        for (Applicant a : Main.applicants) {
            if (a.getId() == applicantId) {
                found = a;
                break;
            }
        }

        if (found == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\":\"Applicant not found\"}");
            return;
        }

        try {
            LoanApplication application = Main.loanController.createApplication(found, amount, termMonths, purpose);
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"id\":" + application.getId() + "}");
        } catch (InvalidLoanException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // manual JSON string-field extraction, Jackson replaces this soon
    private String extractValue(String json, String field) {
        String key = "\"" + field + "\":";
        int start = json.indexOf(key) + key.length();
        int end;
        if (json.charAt(start) == '"') {
            start++;
            end = json.indexOf('"', start);
        } else {
            end = json.indexOf(',', start);
            if (end == -1) {
                end = json.indexOf('}', start);
            }
        }
        return json.substring(start, end).trim();
    }
}