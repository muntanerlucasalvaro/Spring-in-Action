package es.evolucionia.microloan;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryLoanRepository implements LoanRepository {

    private final List<LoanApplication> applications = new ArrayList<>();

    @Override
    public void save(LoanApplication application) {
        applications.add(application);
    }

    @Override
    public Optional<LoanApplication> findById(int id) {
        for (LoanApplication app : applications) {
            if (app.getId() == id) {
                return Optional.of(app);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<LoanApplication> findAll() {
        return new ArrayList<>(applications);
    }

    @Override
    public List<LoanApplication> findByStatus(LoanStatus status) {
        List<LoanApplication> result = new ArrayList<>();
        for (LoanApplication app : applications) {
            if (app.getStatus() == status) {
                result.add(app);
            }
        }
        return result;
    }
}