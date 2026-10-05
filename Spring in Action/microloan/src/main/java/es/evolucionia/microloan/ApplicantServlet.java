package es.evolucionia.microloan;

import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/applicants")
public class ApplicantServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CreateApplicantRequest request = mapper.readValue(req.getReader(), CreateApplicantRequest.class);

        int newId = Main.applicants.size() + 1;
        Applicant applicant = new Applicant(newId, request.fullName(), request.email(), request.monthlyIncome());
        Main.applicants.add(applicant);

        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_CREATED);
        mapper.writeValue(resp.getWriter(), java.util.Map.of("id", applicant.getId()));
    }
}