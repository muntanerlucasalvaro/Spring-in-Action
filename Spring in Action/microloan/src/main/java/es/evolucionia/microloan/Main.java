package es.evolucionia.microloan;

import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Optional;
import java.util.Set;
import java.math.BigDecimal;
import java.util.Scanner;

public class Main {

    private static Set<Applicant> applicants = new HashSet<>();
    private static LoanRepository loanRepository = new JdbcLoanRepository(
            "jdbc:postgresql://localhost:5432/microloan", "postgres", "microloan");
    private static LoanService loanService = new LoanService(loanRepository);

    public static void main(String[] args) {
        System.out.println("MicroLoan starting...");

        Scanner sc = new Scanner(System.in);
        boolean on = true;

        while (on) {
            System.out.println("1 add applicant");
            System.out.println("2 create application");
            System.out.println("3 submit application");
            System.out.println("4 review application");
            System.out.println("5 approve");
            System.out.println("6 reject");
            System.out.println("7 list all");
            System.out.println("8 reports");
            System.out.println("0 quit");
            System.out.print("Choose an option: ");

            int option;

            try {
                option = sc.nextInt();
                sc.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number");
                sc.nextLine();
                continue;
            }

            switch (option) {

                case 1:
                    System.out.print("Enter applicant ID: ");
                    int applicantId = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Enter full name: ");
                    String fullName = sc.nextLine();

                    System.out.print("Enter email: ");
                    String email = sc.nextLine();

                    System.out.print("Enter monthly income: ");
                    BigDecimal monthlyIncome = sc.nextBigDecimal();
                    sc.nextLine();

                    Applicant applicant = new Applicant(applicantId, fullName, email, monthlyIncome);
                    applicants.add(applicant);

                    System.out.println("Applicant added: " + applicant.getFullName());
                    break;

                case 2:
                    System.out.print("Enter applicant ID: ");
                    applicantId = sc.nextInt();
                    sc.nextLine();

                    Applicant found = null;
                    for (Applicant a : applicants) {
                        if (a.getId() == applicantId) {
                            found = a;
                            break;
                        }
                    }

                    if (found != null) {
                        System.out.print("Enter loan amount: ");
                        BigDecimal amount = sc.nextBigDecimal();
                        sc.nextLine();

                        System.out.print("Enter term in months: ");
                        int termMonths = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Enter purpose: ");
                        String purpose = sc.nextLine();

                        LoanApplication application = new LoanApplication(loanRepository.findAll().size() + 1, found,
                                amount, termMonths, purpose);
                        loanRepository.save(application);

                        System.out.println("Loan application created with ID: " + application.getId());
                    } else {
                        System.out.println("Applicant not found");
                    }
                    break;

                case 3:
                    System.out.print("Enter application ID to submit: ");
                    int applicationId = sc.nextInt();
                    sc.nextLine();

                    Optional<LoanApplication> resultSubmit = loanRepository.findById(applicationId);
                    if (resultSubmit.isPresent()) {
                        LoanApplication appToSubmit = resultSubmit.get();
                        try {
                            loanService.changeStatus(appToSubmit, LoanStatus.SUBMITTED);
                            loanRepository.updateStatus(appToSubmit);
                            System.out.println("Loan application submitted: " + appToSubmit.getId());
                        } catch (InvalidLoanException e) {
                            System.out.println(e.getMessage());
                        }
                    } else {
                        System.out.println("Application not found");
                    }
                    break;

                case 4:
                    System.out.print("Enter application ID to review: ");
                    int applicationIdToReview = sc.nextInt();
                    sc.nextLine();

                    Optional<LoanApplication> resultReview = loanRepository.findById(applicationIdToReview);
                    if (resultReview.isPresent()) {
                        LoanApplication appToReview = resultReview.get();
                        try {
                            loanService.changeStatus(appToReview, LoanStatus.UNDER_REVIEW);
                            loanRepository.updateStatus(appToReview);
                            System.out.println("Loan application under review: " + appToReview.getId());
                        } catch (InvalidLoanException e) {
                            System.out.println(e.getMessage());
                        }
                    } else {
                        System.out.println("Application not found");
                    }
                    break;

                case 5:
                    System.out.print("Enter application ID to approve: ");
                    int applicationIdToApprove = sc.nextInt();
                    sc.nextLine();

                    Optional<LoanApplication> resultApprove = loanRepository.findById(applicationIdToApprove);
                    if (resultApprove.isPresent()) {
                        LoanApplication appToApprove = resultApprove.get();
                        LoanStatus oldStatus = appToApprove.getStatus();
                        try {
                            loanService.changeStatus(appToApprove, LoanStatus.APPROVED);

                            System.out.print("Simulate a failure to test rollback? (yes/no): ");
                            boolean simulateFailure = sc.nextLine().equalsIgnoreCase("yes");

                            ((JdbcLoanRepository) loanRepository).approveWithHistory(appToApprove, oldStatus,
                                    simulateFailure);
                            System.out.println("Loan application approved: " + appToApprove.getId());
                        } catch (InvalidLoanException e) {
                            System.out.println(e.getMessage());
                        } catch (RuntimeException e) {
                            System.out.println("Transaction failed, nothing was saved: " + e.getMessage());
                        }
                    } else {
                        System.out.println("Application not found");
                    }
                    break;

                case 6:
                    System.out.print("Enter application ID to reject: ");
                    int applicationIdToReject = sc.nextInt();
                    sc.nextLine();

                    Optional<LoanApplication> resultReject = loanRepository.findById(applicationIdToReject);
                    if (resultReject.isPresent()) {
                        LoanApplication appToReject = resultReject.get();
                        try {
                            loanService.changeStatus(appToReject, LoanStatus.REJECTED);
                            loanRepository.updateStatus(appToReject);
                            System.out.println("Loan application rejected: " + appToReject.getId());
                        } catch (InvalidLoanException e) {
                            System.out.println(e.getMessage());
                        }
                    } else {
                        System.out.println("Application not found");
                    }
                    break;

                case 7:
                    for (LoanApplication app : loanRepository.findAll()) {
                        System.out.println("ID: " + app.getId() + " | Applicant: " + app.getApplicant().getFullName()
                                + " | Amount: " + app.getAmount() + " | Status: " + app.getStatus());
                    }
                    break;

                case 8:
                    long pending = loanRepository.findAll().stream()
                            .filter(app -> app.getStatus() == LoanStatus.SUBMITTED
                                    || app.getStatus() == LoanStatus.UNDER_REVIEW)
                            .count();
                    System.out.println("Pending review: " + pending);

                    BigDecimal totalApproved = loanRepository.findAll().stream()
                            .filter(app -> app.getStatus() == LoanStatus.APPROVED)
                            .map(app -> app.getAmount())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    System.out.println("Total approved: " + totalApproved);

                    for (Applicant a : applicants) {
                        boolean hasApplication = loanRepository.findAll().stream()
                                .anyMatch(app -> app.getApplicant().equals(a));
                        if (!hasApplication) {
                            System.out.println("Never applied: " + a.getFullName());
                        }
                    }
                    break;

                case 0:
                    on = false;
                    break;
                default:
                    System.out.println("Invalid option");
            }
        }

        sc.close();
    }
}