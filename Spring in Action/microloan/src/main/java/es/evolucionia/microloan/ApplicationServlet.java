package es.evolucionia.microloan;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/applications/*")
public class ApplicationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Missing application id\"}");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid application id\"}");
            return;
        }

        Optional<LoanApplication> result = Main.loanRepository.findById(id);
        if (result.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\":\"Application not found\"}");
            return;
        }

        ApplicationResponse response = ApplicationResponse.from(result.get());

        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("{"
                + "\"id\":" + response.id() + ","
                + "\"applicantName\":\"" + response.applicantName() + "\","
                + "\"amount\":" + response.amount() + ","
                + "\"termMonths\":" + response.termMonths() + ","
                + "\"purpose\":\"" + response.purpose() + "\","
                + "\"status\":\"" + response.status() + "\""
                + "}");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo != null && pathInfo.endsWith("/approve")) {
            handleApprove(req, resp, pathInfo);
            return;
        }

        handleCreate(req, resp);
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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

        Applicant applicant = null;
        for (Applicant a : Main.applicants) {
            if (a.getId() == applicantId) {
                applicant = a;
                break;
            }
        }

        if (applicant == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\":\"Applicant not found\"}");
            return;
        }

        try {
            LoanApplication application = Main.loanController.createApplication(applicant, amount, termMonths, purpose);
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"id\":" + application.getId() + "}");
        } catch (InvalidLoanException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void handleApprove(HttpServletRequest req, HttpServletResponse resp, String pathInfo) throws IOException {
        // pathInfo looks like "/28/approve", we only need the number in the midel
        String idPart = pathInfo.substring(1, pathInfo.indexOf("/approve"));

        int id;
        try {
            id = Integer.parseInt(idPart);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid application id\"}");
            return;
        }

        try {
            Main.loanController.approveApplication(id, false);
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"id\":" + id + ",\"status\":\"APPROVED\"}");
        } catch (InvalidLoanException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
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