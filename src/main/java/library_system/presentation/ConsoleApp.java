package library_system.presentation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.nio.file.*;
import library_system.domain.*;
import library_system.application.*;
import library_system.infrastructure.persistence.*;
import library_system.infrastructure.email.InMemoryEmailClient;

public class ConsoleApp {
    private static Scanner scanner = new Scanner(System.in);

    private static FileBookRepository repo = new FileBookRepository();
    private static FileBorrowLedger borrowLedger = new FileBorrowLedger();
    private static FilePaymentLedger paymentLedger = new FilePaymentLedger();
    private static FileSettingsGateway settings = new FileSettingsGateway();
    private static FileUserDirectory users = new FileUserDirectory();
    private static InMemoryEmailClient emailClient = new InMemoryEmailClient();

    private static BorrowBook borrowBook = new BorrowBook(repo, borrowLedger, settings);
    private static ReturnBook returnBook = new ReturnBook(repo, borrowLedger);
    private static OverdueBooks overdueBooks = new OverdueBooks(repo, borrowLedger);
    private static ComputeFine computeFine = new ComputeFine(repo);
    private static PayFine payFine = new PayFine(paymentLedger, repo);
    private static SendReminders sendReminders = new SendReminders();
    private static Admin admin = new Admin("THAER", "777");

    private static String nowStr() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private static boolean isDigits(String s) {
        return s != null && s.matches("\\d+");
    }

    private static void printBooksTable(List<Book> books, String title) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════════════╗");
        System.out.printf("║                              %-45s ║%n", title);
        System.out.println("╠════════════════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ %-20s │ %-20s │ %-12s │ %-13s ║%n", "TITLE", "AUTHOR", "ISBN", "STATUS");
        System.out.println("╠════════════════════════════════════════════════════════════════════════════╣");
        for (Book b : books) {
            String st = b.isBorrowed() ? "BORROWED" : "FREE";
            System.out.printf("║ %-20s │ %-20s │ %-12s │ %-13s ║%n", b.getTitle(), b.getAuthor(), b.getIsbn(), st);
        }
        System.out.println("╚════════════════════════════════════════════════════════════════════════════╝");
    }

    private static void printUsersTable(List<String> usersList, String title) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════════════╗");
        System.out.printf("║                              %-45s ║%n", title);
        System.out.println("╠════════════════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ %-10s │ %-20s │ %-15s │ %-20s ║%n", "USER ID", "NAME", "PHONE", "EMAIL");
        System.out.println("╠════════════════════════════════════════════════════════════════════════════╣");
        for (String line : usersList) {
            if (line.trim().isEmpty() || line.toUpperCase().contains("ID") && line.toUpperCase().contains("NAME")) continue;
            String cleaned = line.replace("|", "").trim();
            String[] parts = cleaned.split("\\s{2,}");
            String id = parts.length > 0 ? parts[0].trim() : "";
            String name = parts.length > 1 ? parts[1].trim() : "";
            String phone = parts.length > 2 ? parts[2].trim() : "";
            String email = parts.length > 3 ? parts[3].trim() : "";
            System.out.printf("║ %-10s │ %-20s │ %-15s │ %-20s ║%n", id, name, phone, email);
        }
        System.out.println("╚════════════════════════════════════════════════════════════════════════════╝");
    }

    public static void main(String[] args) {
        java.io.File dataDir = new java.io.File("DATA");
        if (!dataDir.exists()) dataDir.mkdirs();

        while (true) {
            System.out.println();
            System.out.println("╔════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                    ~ LIBRARY MANAGEMENT APP ~                      ║");
            System.out.println("╠════════════════════════════════════════════════════════════════════╣");
            System.out.println("║ 1. Admin Login                                                     ║");
            System.out.println("║ 2. Admin Logout                                                    ║");
            System.out.println("║ 3. Add Book                                                        ║");
            System.out.println("║ 4. Search Book                                                     ║");
            System.out.println("║ 5. Borrow Book                                                     ║");
            System.out.println("║ 6. Return Book                                                     ║");
            System.out.println("║ 7. List Overdue Books                                              ║");
            System.out.println("║ 8. Show Outstanding Fine                                           ║");
            System.out.println("║ 9. Pay Fine                                                        ║");
            System.out.println("║ 10. Set Loan Days                                                  ║");
            System.out.println("║ 11. Create User                                                    ║");
            System.out.println("║ 12. List Users                                                     ║");
            System.out.println("║ 13. Export Reports                                                 ║");
            System.out.println("║ 14. Reset Data Files                                               ║");
            System.out.println("║ 15. List All Books                                                 ║");
            System.out.println("║ 16. Send Reminders                                                 ║");
            System.out.println("║ 17. Exit                                                           ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════╝");
            System.out.print(" Enter option [1-17]: ");

            int opt = 0;
            try { opt = Integer.parseInt(scanner.nextLine().trim()); } catch (Exception e) { System.out.println("\nInvalid input."); continue; }

            switch (opt) {
                case 1:
                    System.out.print("\nUsername: ");
                    String u = scanner.nextLine();
                    System.out.print("Password: ");
                    String p = scanner.nextLine();
                    if (!admin.login(u, p)) {
                        System.out.println("\nWrong credentials. Press T to try again or E to exit.");
                        String resp = scanner.nextLine().trim().toUpperCase();
                        if (resp.equals("T")) continue;
                        else if (!continueOrExit()) return;
                        break;
                    } else {
                        System.out.println("\nAdmin login successful.");
                    }
                    if (!continueOrExit()) return;
                    break;
                case 2:
                    admin.logout();
                    System.out.println("\nAdmin logged out.");
                    if (!continueOrExit()) return;
                    break;
                case 3:
                    if (!admin.isLoggedIn()) { System.out.println("\nAdmin must login first."); if (!continueOrExit()) return; break; }
                    System.out.print("\nTitle: ");
                    String t = scanner.nextLine();
                    System.out.print("Author: ");
                    String a = scanner.nextLine();
                    System.out.print("ISBN: ");
                    String i = scanner.nextLine();
                    if (!isDigits(i)) { System.out.println("\nInvalid ISBN."); if (!continueOrExit()) return; break; }
                    List<Book> books = repo.getAll();
                    for (Book b : books) if (b.getIsbn().equals(i)) { System.out.println("\nDuplicate ISBN."); i = null; break; }
                    if (i == null) { if (!continueOrExit()) return; break; }
                    books.add(new Book(t, a, i));
                    repo.saveAll(books);
                    System.out.println("\nBook added successfully.");
                    if (!continueOrExit()) return;
                    break;
                case 4:
                    System.out.print("\nSearch keyword: ");
                    String k = scanner.nextLine().trim().toLowerCase();
                    List<Book> all = repo.getAll();
                    List<Book> result = new ArrayList<Book>();
                    for (Book b : all) {
                        if (b.getTitle().toLowerCase().contains(k) || b.getAuthor().toLowerCase().contains(k) || b.getIsbn().contains(k))
                            result.add(b);
                    }
                    if (result.isEmpty()) System.out.println("\nNo matching books found.");
                    else printBooksTable(result, "SEARCH RESULTS");
                    if (!continueOrExit()) return;
                    break;
                case 5:
                    System.out.println("\nBorrow Book ");
                    System.out.println(" 1. Select User");
                    System.out.println(" 2. Create New User");
                    System.out.print(" Choose option [1-2]: ");
                    int userOpt = 0;
                    try { userOpt = Integer.parseInt(scanner.nextLine().trim()); } catch (Exception e) {}
                    String uid = null;
                    if (userOpt == 1) {
                        List<String> allUsersList = users.getAllUsers();
                        if (allUsersList.isEmpty()) {
                            System.out.println("\nNo users found. Please create a new user first.");
                            if (!continueOrExit()) return;
                            break;
                        }
                        printUsersTable(allUsersList, "EXISTING USERS");
                        System.out.print("\nEnter existing User ID: ");
                        uid = scanner.nextLine().trim();
                    } else if (userOpt == 2) {
                        System.out.print("\nNew User ID: ");
                        uid = scanner.nextLine().trim();
                        System.out.print("Name: ");
                        String nn = scanner.nextLine().trim();
                        System.out.print("Phone: ");
                        String np = scanner.nextLine().trim();
                        System.out.print("Email: ");
                        String ne = scanner.nextLine().trim();
                        users.addUser(uid, nn, np, ne);
                        System.out.println("\nUser created successfully.");
                    } else {
                        System.out.println("\nInvalid option.");
                        if (!continueOrExit()) return;
                        break;
                    }
                    System.out.print("\nEnter Book ISBN: ");
                    String bi = scanner.nextLine().trim();
                    if (!isDigits(bi)) { System.out.println("\nInvalid ISBN."); if (!continueOrExit()) return; break; }
                    List<Book> bookList = repo.getAll();
                    Book targetBook = null;
                    for (Book b : bookList) {
                        if (b.getIsbn().equals(bi)) { targetBook = b; break; }
                    }
                    if (targetBook == null) { System.out.println("\nBook not found."); if (!continueOrExit()) return; break; }
                    System.out.println("\nBook Details:");
                    System.out.println(" Title: " + targetBook.getTitle());
                    System.out.println(" Author: " + targetBook.getAuthor());
                    System.out.println(" ISBN: " + targetBook.getIsbn());
                    System.out.print(" Confirm borrow (Y/N): ");
                    String conf = scanner.nextLine().trim().toUpperCase();
                    if (!conf.equals("Y")) { System.out.println("\nBorrow cancelled."); if (!continueOrExit()) return; break; }
                    boolean borrowed = borrowBook.borrow(bi, uid, LocalDate.now());
                    System.out.println(borrowed ? "\nBorrowed successfully." : "\nCannot borrow this book.");
                    if (!continueOrExit()) return;
                    break;
                case 6:
                    System.out.print("\nISBN to return: ");
                    String ri = scanner.nextLine();
                    if (!isDigits(ri)) { System.out.println("\nInvalid ISBN."); if (!continueOrExit()) return; break; }
                    boolean rb = returnBook.returnBook(ri);
                    System.out.println(rb ? "\nReturned successfully." : "\nCannot return.");
                    if (!continueOrExit()) return;
                    break;
                case 7:
                    refresh();
                    List<Book> over = overdueBooks.getOverdueBooks(LocalDate.now());
                    if (over.isEmpty()) {
                        System.out.println("\nNo overdue books.");
                    } else {
                        printBooksTable(over, "OVERDUE BOOKS");
                        Path path = Paths.get("DATA/OVERDUE.TXT");
                        List<String> lines = new ArrayList<>();
                        lines.add("| TYPE     | ISBN         | USER_ID      | DUE_DATE     |");
                        for (Book b : over) {
                            lines.add(String.format("| %-8s | %-12s | %-12s | %-12s |",
                                    "BORROW", b.getIsbn(), b.getBorrowerId(), b.getDueDate()));
                        }
                        try {
                            Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                            System.out.println("\nOverdue file updated: " + path.toAbsolutePath());
                        } catch (Exception e) {
                            System.out.println("\nError writing overdue file: " + e.getMessage());
                        }
                    }
                    if (!continueOrExit()) return;
                    break;
                case 8:
                    System.out.print("\nUser ID: ");
                    String su = scanner.nextLine().trim();
                    if (!isDigits(su)) { System.out.println("\nInvalid user id."); if (!continueOrExit()) return; break; }
                    double fine = computeFine.computeOutstanding(su, LocalDate.now());
                    System.out.println("\nOutstanding fine: " + fine);
                    if (!continueOrExit()) return;
                    break;
                case 9:
                    System.out.print("\nUser ID: ");
                    String pu = scanner.nextLine().trim();
                    System.out.print("Amount: ");
                    double amt = 0;
                    try { amt = Double.parseDouble(scanner.nextLine().trim()); } catch (Exception e) {}
                    double remain = payFine.pay(pu, amt, LocalDate.now());
                    System.out.println("\nRemaining fine: " + remain);
                    if (!continueOrExit()) return;
                    break;
                case 10:
                    System.out.print("\nNew loan days: ");
                    int d = 28;
                    try { d = Integer.parseInt(scanner.nextLine().trim()); } catch (Exception e) {}
                    settings.setLoanDays(d);
                    System.out.println("\nLoan days set to " + d + ".");
                    if (!continueOrExit()) return;
                    break;
                case 11:
                    System.out.print("\nUser ID: ");
                    String nid = scanner.nextLine().trim();
                    System.out.print("Name: ");
                    String nn = scanner.nextLine().trim();
                    System.out.print("Phone: ");
                    String np = scanner.nextLine().trim();
                    System.out.print("Email: ");
                    String ne = scanner.nextLine().trim();
                    users.addUser(nid, nn, np, ne);
                    System.out.println("\nUser created.");
                    if (!continueOrExit()) return;
                    break;
                case 12:
                    List<String> allUsers = users.getAllUsers();
                    if (allUsers.isEmpty()) System.out.println("\nNo users found.");
                    else printUsersTable(allUsers, "ALL USERS");
                    if (!continueOrExit()) return;
                    break;
                case 13:
                    List<String> report = new ArrayList<String>();
                    report.add("LIBRARY REPORT " + nowStr());
                    report.add("Books: " + repo.getAll().size());
                    report.add("Users: " + users.getAllUsers().size());
                    Path rp = Paths.get("DATA/REPORTS.TXT");
                    try { Files.write(rp, report); } catch (Exception e) {}
                    System.out.println("\nReports written to: " + rp.toAbsolutePath());
                    if (!continueOrExit()) return;
                    break;
                case 14:
                    try {
                        Files.deleteIfExists(Paths.get("DATA/BOOKS.TXT"));
                        Files.deleteIfExists(Paths.get("DATA/BORROWS.TXT"));
                        Files.deleteIfExists(Paths.get("DATA/PAYMENTS.TXT"));
                        Files.deleteIfExists(Paths.get("DATA/USERS.TXT"));
                        Files.deleteIfExists(Paths.get("DATA/SETTINGS.TXT"));
                        Files.deleteIfExists(Paths.get("DATA/REPORTS.TXT"));
                        Files.deleteIfExists(Paths.get("DATA/OVERDUE.TXT"));
                    } catch (Exception e) {}
                    System.out.println("\nAll data files reset.");
                    if (!continueOrExit()) return;
                    break;
                case 15:
                    printBooksTable(repo.getAll(), "ALL BOOKS");
                    if (!continueOrExit()) return;
                    break;
                case 16:
                    int sent = sendReminders.sendAll(LocalDate.now());
                    if (sent == 0) System.out.println("\nNo overdue users. No reminders sent.");
                    else {
                        for (String msg : sendReminders.getSentLogs()) System.out.println(msg);
                        System.out.println("\n" + sent + " reminders sent successfully.");
                    }
                    if (!continueOrExit()) return;
                    break;
                case 17:
                    System.out.println("\nExiting Library App. Goodbye!");
                    return;
                default:
                    System.out.println("\nInvalid option!");
                    if (!continueOrExit()) return;
            }
        }
    }

    private static void refresh() {
        repo.getAll();
        users.getAllUsers();
        settings.getLoanDays();
        System.out.println("\nData refreshed successfully.");
    }

    private static boolean continueOrExit() {
        System.out.println("\nPress Enter to return to menu or type E to exit:");
        String input = scanner.nextLine().trim().toUpperCase();
        return !input.equals("E");
    }
}
