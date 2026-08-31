package es.evolucionia.microloan;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcLoanRepository implements LoanRepository {

    private final String url;
    private final String user;
    private final String password;

    public JdbcLoanRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public void save(LoanApplication application) {
        String sql = "INSERT INTO loan_applications (applicant_id, amount, term_months, purpose, status, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, application.getApplicant().getId());
            stmt.setBigDecimal(2, application.getAmount());
            stmt.setInt(3, application.getTermMonths());
            stmt.setString(4, application.getPurpose());
            stmt.setString(5, application.getStatus().toString());
            stmt.setObject(6, application.getCreatedAt());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving loan application", e);
        }
    }

    @Override
    public Optional<LoanApplication> findById(int id) {
        String sql = "SELECT la.*, a.full_name, a.email, a.monthly_income "
                + "FROM loan_applications la JOIN applicants a ON la.applicant_id = a.id "
                + "WHERE la.id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding loan application", e);
        }
    }

    @Override
    public List<LoanApplication> findAll() {
        String sql = "SELECT la.*, a.full_name, a.email, a.monthly_income "
                + "FROM loan_applications la JOIN applicants a ON la.applicant_id = a.id";
        List<LoanApplication> result = new ArrayList<>();
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding loan applications", e);
        }
        return result;
    }

    @Override
    public List<LoanApplication> findByStatus(LoanStatus status) {
        String sql = "SELECT la.*, a.full_name, a.email, a.monthly_income "
                + "FROM loan_applications la JOIN applicants a ON la.applicant_id = a.id "
                + "WHERE la.status = ?";
        List<LoanApplication> result = new ArrayList<>();
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding loan applications by status", e);
        }
        return result;
    }

    private LoanApplication mapRow(ResultSet rs) throws SQLException {
        Applicant applicant = new Applicant(
                rs.getInt("applicant_id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getBigDecimal("monthly_income"));

        return new LoanApplication(
                rs.getInt("id"),
                applicant,
                rs.getBigDecimal("amount"),
                rs.getInt("term_months"),
                rs.getString("purpose"),
                LoanStatus.valueOf(rs.getString("status")),
                rs.getObject("created_at", LocalDate.class));
    }
}