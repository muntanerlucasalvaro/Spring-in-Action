package es.evolucionia.microloan;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/applicants")
public class ApplicantServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StringBuilder body = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            body.append(line);
        }

        String json = body.toString();
        String fullName = extractValue(json, "fullName");
        String email = extractValue(json, "email");
        String monthlyIncomeStr = extractValue(json, "monthlyIncome");
        BigDecimal monthlyIncome = new BigDecimal(monthlyIncomeStr);

        int newId = Main.applicants.size() + 1;
        Applicant applicant = new Applicant(newId, fullName, email, monthlyIncome);
        Main.applicants.add(applicant);

        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write("{\"id\":" + applicant.getId() + "}");
    }

    // manual JSON string-field extraction (Jackson replaces this soon)ñ
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