package es.evolucionia.microloan;

import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Set;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static Set<Applicant> applicants = new HashSet<>();
    private static List<LoanApplication> applications = new ArrayList<>();
    private static LoanService loanService = new LoanService();

    public static void main(String[] args) {
        System.out.println("MicroLoan starting...");

        Scanner sc = new Scanner(System.in);
        boolean on = true;

        // Main loop
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

                    Applicant applicant = new Applicant(applicantId, fullName, email, monthlyIncome);
                    applicants.add(applicant);
                    sc.nextLine();

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

                        LoanApplication application = new LoanApplication(applications.size() + 1, found, amount,
                                termMonths, purpose);
                        applications.add(application);

                        System.out.println("Loan application created with ID: " + application.getId());
                    } else {
                        System.out.println("Applicant not found");

                    }
                    break;

                case 3:
                    System.out.print("Enter application ID to submit: ");
                    int applicationId = sc.nextInt();
                    sc.nextLine();

                    LoanApplication appToSubmit = null;
                    for (LoanApplication app : applications) {
                        if (app.getId() == applicationId) {
                            appToSubmit = app;
                            break;
                        }
                    }

                    if (appToSubmit != null) {
                        try {
                            loanService.changeStatus(appToSubmit, LoanStatus.SUBMITTED);
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

                    LoanApplication appToReview = null;
                    for (LoanApplication app : applications) {
                        if (app.getId() == applicationIdToReview) {
                            appToReview = app;
                            break;
                        }
                    }

                    if (appToReview != null) {
                        try {
                            loanService.changeStatus(appToReview, LoanStatus.UNDER_REVIEW);
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

                    LoanApplication appToApprove = null;
                    for (LoanApplication app : applications) {
                        if (app.getId() == applicationIdToApprove) {
                            appToApprove = app;
                            break;
                        }
                    }

                    if (appToApprove != null) {
                        try {
                            loanService.changeStatus(appToApprove, LoanStatus.APPROVED);
                            System.out.println("Loan application approved: " + appToApprove.getId());
                        } catch (InvalidLoanException e) {
                            System.out.println(e.getMessage());
                        }
                    } else {
                        System.out.println("Application not found");
                    }
                    break;
                case 6:
                    System.out.print("Enter application ID to reject: ");
                    int applicationIdToReject = sc.nextInt();
                    sc.nextLine();

                    LoanApplication appToReject = null;
                    for (LoanApplication app : applications) {
                        if (app.getId() == applicationIdToReject) {
                            appToReject = app;
                            break;
                        }
                    }

                    if (appToReject != null) {
                        try {
                            loanService.changeStatus(appToReject, LoanStatus.REJECTED);
                            System.out.println("Loan application rejected: " + appToReject.getId());
                        } catch (InvalidLoanException e) {
                            System.out.println(e.getMessage());
                        }
                    } else {
                        System.out.println("Application not found");
                    }
                    break;
                case 7:
                    for (LoanApplication app : applications) {
                        System.out.println("ID: " + app.getId() + " | Applicant: " + app.getApplicant().getFullName()
                                + " | Amount: " + app.getAmount() + " | Status: " + app.getStatus());
                    }
                    break;
                case 8:
                    long pending = applications.stream()
                            .filter(app -> app.getStatus() == LoanStatus.SUBMITTED
                                    || app.getStatus() == LoanStatus.UNDER_REVIEW)
                            .count();
                    System.out.println("Pending review: " + pending);

                    BigDecimal totalApproved = applications.stream()
                            .filter(app -> app.getStatus() == LoanStatus.APPROVED)
                            .map(app -> app.getAmount())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    System.out.println("Total approved: " + totalApproved);

                    for (Applicant a : applicants) {
                        boolean hasApplication = applications.stream()
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