package es.evolucionia.microloan;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/applications/*")
public class ApplicationServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing application id");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid application id");
            return;
        }

        Optional<LoanApplication> result = Main.loanRepository.findById(id);
        if (result.isEmpty()) {
            writeError(resp, HttpServletResponse.SC_NOT_FOUND, "Application not found");
            return;
        }

        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        mapper.writeValue(resp.getWriter(), ApplicationResponse.from(result.get()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        // "/28/approve" -> action on an existing application
        if (pathInfo != null && pathInfo.lastIndexOf('/') > 0) {
            handleAction(resp, pathInfo);
            return;
        }

        handleCreate(req, resp);
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CreateApplicationRequest request;
        try {
            request = mapper.readValue(req.getReader(), CreateApplicationRequest.class);
        } catch (JsonProcessingException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON body");
            return;
        }

        Applicant applicant = null;
        for (Applicant a : Main.applicants) {
            if (a.getId() == request.applicantId()) {
                applicant = a;
                break;
            }
        }

        if (applicant == null) {
            writeError(resp, HttpServletResponse.SC_NOT_FOUND, "Applicant not found");
            return;
        }

        try {
            LoanApplication application = Main.loanController.createApplication(
                    applicant, request.amount(), request.termMonths(), request.purpose());
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            mapper.writeValue(resp.getWriter(), Map.of("id", application.getId()));
        } catch (InvalidLoanException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private void handleAction(HttpServletResponse resp, String pathInfo) throws IOException {
        // pathInfo looks like "/28/approve": id in the middle, action at the end
        int slash = pathInfo.lastIndexOf('/');
        String idPart = pathInfo.substring(1, slash);
        String action = pathInfo.substring(slash + 1);

        int id;
        try {
            id = Integer.parseInt(idPart);
        } catch (NumberFormatException e) {
            writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid application id");
            return;
        }

        String newStatus;
        try {
            switch (action) {
                case "submit" -> {
                    Main.loanController.submitApplication(id);
                    newStatus = "SUBMITTED";
                }
                case "review" -> {
                    Main.loanController.reviewApplication(id);
                    newStatus = "UNDER_REVIEW";
                }
                case "approve" -> {
                    Main.loanController.approveApplication(id, false);
                    newStatus = "APPROVED";
                }
                case "reject" -> {
                    Main.loanController.rejectApplication(id);
                    newStatus = "REJECTED";
                }
                default -> {
                    writeError(resp, HttpServletResponse.SC_NOT_FOUND, "Unknown action");
                    return;
                }
            }
        } catch (InvalidLoanException e) {
            // a missing application is a 404, a blocked rule (R3/R4/R5) is a 409
            int status = e.getMessage().equals("Application not found")
                    ? HttpServletResponse.SC_NOT_FOUND
                    : HttpServletResponse.SC_CONFLICT;
            writeError(resp, status, e.getMessage());
            return;
        }

        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        mapper.writeValue(resp.getWriter(), Map.of("id", id, "status", newStatus));
    }

    private void writeError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(status);
        mapper.writeValue(resp.getWriter(), Map.of("error", message));
    }
}