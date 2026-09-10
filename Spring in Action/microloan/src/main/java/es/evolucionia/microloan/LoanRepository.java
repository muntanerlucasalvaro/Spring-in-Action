package es.evolucionia.microloan;

import java.util.List;
import java.util.Optional;

public interface LoanRepository {

    void save(LoanApplication application);

    Optional<LoanApplication> findById(int id);

    List<LoanApplication> findAll();

    List<LoanApplication> findByStatus(LoanStatus status);

    void updateStatus(LoanApplication application);
}
