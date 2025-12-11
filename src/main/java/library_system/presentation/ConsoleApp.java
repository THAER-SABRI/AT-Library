package library_system.presentation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.nio.file.*;

import library_system.domain.Admin;
import library_system.domain.Book;
import library_system.domain.CD;
import library_system.domain.Media;
import library_system.application.*;
import library_system.domain.strategy.BorrowDurationStrategy;
import library_system.domain.strategy.CdBorrowDurationStrategy;
import library_system.domain.strategy.CdFineStrategy;
import library_system.domain.strategy.FineStrategy;
import library_system.domain.strategy.BookBorrowDurationStrategy;
import library_system.domain.strategy.BorrowDurationStrategy;
import library_system.domain.strategy.BookFineStrategy;
import library_system.domain.strategy.FineStrategy;
import library_system.infrastructure.persistence.*;
import library_system.infrastructure.email.EnvConfigLoader;
import library_system.infrastructure.email.InMemoryEmailClient;
import library_system.infrastructure.email.SMTPEmailService;

public class ConsoleApp {

    private static Scanner scanner = new Scanner(System.in);

    // Settings file
    private static FileSettingsGateway settings = new FileSettingsGateway("DATA/SETTINGS.TXT");

    // Strategies
    private static BorrowDurationStrategy bookBorrowStrategy =
            new BookBorrowDurationStrategy(settings);
    private static FineStrategy bookFineStrategy = new BookFineStrategy();

    private static BorrowDurationStrategy cdBorrowStrategy =
            new CdBorrowDurationStrategy(settings);
    private static FineStrategy cdFineStrategy = new CdFineStrategy();

    // Repositories
    private static FileBookRepository bookRepo =
            new FileBookRepository("DATA/BOOKS.TXT");

    private static FileCdRepository cdRepo =
            new FileCdRepository("DATA/CDS.TXT", cdBorrowStrategy, cdFineStrategy);

    private static FileBorrowLedger borrowLedger = new FileBorrowLedger();
    private static FilePaymentLedger paymentLedger = new FilePaymentLedger();
    private static FileUserDirectory users = new FileUserDirectory();

    // Application services
    private static OverdueMedia overdueMedia =
            new OverdueMedia(bookRepo, cdRepo);

    private static BorrowMedia borrowMedia =
            new BorrowMedia(bookRepo, cdRepo, borrowLedger, overdueMedia);

    private static ComputeFine computeFine =
            new ComputeFine(bookRepo, cdRepo, paymentLedger);

    private static ReturnBook returnBook =
            new ReturnBook(bookRepo, borrowLedger, computeFine);

    private static PayFine payFine =
            new PayFine(paymentLedger, computeFine);
    
    private static SendReminders sendReminders;


    Properties env = EnvConfigLoader.loadEnv("DATA/email.env");

    EmailService emailService = new SMTPEmailService(
            env.getProperty("EMAIL_USERNAME"),
            env.getProperty("EMAIL_PASSWORD"),
            env.getProperty("EMAIL_HOST"),
            Integer.parseInt(env.getProperty("EMAIL_PORT")),
            Boolean.parseBoolean(env.getProperty("EMAIL_TLS")));
   
    SendReminders sendRemainders= new SendReminders(emailService, computeFine);



    private static Admin admin = new Admin("THAER", "777");

    private static UnregisterUser unregisterUser =
            new UnregisterUser(users, bookRepo, computeFine, admin);

    private static String nowStr() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private static boolean isDigits(String s) {
        return s != null && s.matches("\\d+");
    }

    private static void printBooksTable(List<Book> books, String title) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════════════╗");
        System.out.printf ("║                              %-45s ║%n", title);
        System.out.println("╠════════════════════════════════════════════════════════════════════════════╣");
        System.out.printf ("║ %-20s │ %-20s │ %-12s │ %-13s ║%n", "TITLE", "AUTHOR", "ISBN", "STATUS");
        System.out.println("╠════════════════════════════════════════════════════════════════════════════╣");
        for (Book b : books) {
        	
        	if (b.getTitle().trim().equals("TITLE")) continue;
        	
            String st = b.isBorrowed() ? "BORROWED" : "FREE";
            System.out.printf("║ %-20s │ %-20s │ %-12s │ %-13s ║%n",
                    b.getTitle(), b.getAuthor(), b.getIsbn(), st);
        }
        System.out.println("╚════════════════════════════════════════════════════════════════════════════╝");
    }

    private static void printCdsTable(List<CD> cds, String title) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════════════╗");
        System.out.printf ("║                              %-45s ║%n", title);
        System.out.println("╠════════════════════════════════════════════════════════════════════════════╣");
        System.out.printf ("║ %-20s │ %-12s │ %-13s ║%n", "TITLE", "ID", "STATUS");
        System.out.println("╠════════════════════════════════════════════════════════════════════════════╣");
        for (CD cd : cds) {
            String st = cd.isBorrowed() ? "BORROWED" : "FREE";
            System.out.printf("║ %-20s │ %-12s │ %-13s ║%n",
                    cd.getTitle(), cd.getId(), st);
        }
        System.out.println("╚════════════════════════════════════════════════════════════════════════════╝");
    }

    private static void printUsersTable(List<String> usersList, String title) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════════════╗");
        System.out.printf ("║                              %-45s ║%n", title);
        System.out.println("╠════════════════════════════════════════════════════════════════════════════╣");
        System.out.printf ("║ %-10s │ %-20s │ %-15s │ %-20s ║%n", "USER ID", "NAME", "PHONE", "EMAIL");
        System.out.println("╠════════════════════════════════════════════════════════════════════════════╣");
        for (String line : usersList) {
            if (line.trim().isEmpty() || line.toUpperCase().contains("ID") && line.toUpperCase().contains("NAME"))
                continue;
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
        if (!dataDir.exists())
            dataDir.mkdirs();

        Properties env = EnvConfigLoader.loadEnv("DATA/email.env");

        EmailService emailService = new SMTPEmailService(
                env.getProperty("EMAIL_USERNAME"),
                env.getProperty("EMAIL_PASSWORD"),
                env.getProperty("EMAIL_HOST"),
                Integer.parseInt(env.getProperty("EMAIL_PORT")),
                Boolean.parseBoolean(env.getProperty("EMAIL_TLS")));

        sendReminders = new SendReminders(emailService, computeFine);

        while (true) {
            System.out.println();
            System.out.println("╔════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                    ~ LIBRARY MANAGEMENT APP ~                      ║");
            System.out.println("╠════════════════════════════════════════════════════════════════════╣");
            System.out.println("║  1. Admin Login                                                    ║");
            System.out.println("║  2. Admin Logout                                                   ║");
            System.out.println("║  3. Add Book                                                       ║");
            System.out.println("║  4. Add CD                                                         ║");
            System.out.println("║  5. Search Media                                                   ║");
            System.out.println("║  6. Borrow Media                                                   ║");
            System.out.println("║  7. Return Book                                                    ║");
            System.out.println("║  8. List Overdue Media                                             ║");
            System.out.println("║  9. Show Outstanding Fine                                          ║");
            System.out.println("║ 10. Pay Fine                                                       ║");
            System.out.println("║ 11. Set Loan Days (Books)                                          ║");
            System.out.println("║ 12. Register User                                                  ║");
            System.out.println("║ 13. Unregister User                                                ║");
            System.out.println("║ 14. List Users                                                     ║");
            System.out.println("║ 15. Export Reports                                                 ║");
            System.out.println("║ 16. Reset Data Files                                               ║");
            System.out.println("║ 17. List All Books                                                 ║");
            System.out.println("║ 18. List All CDs                                                   ║");
            System.out.println("║ 19. Send Reminders                                                 ║");
            System.out.println("║ 20. Exit                                                           ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════╝");
            System.out.print(" Enter option [1-20]: ");

            int opt = 0;
            try {
                opt = Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("\nInvalid input.");
                continue;
            }

            switch (opt) {
                case 1:
                    System.out.print("\nUsername: ");
                    String u = scanner.nextLine();
                    System.out.print("Password: ");
                    String p = scanner.nextLine();
                    if (!admin.login(u, p)) {
                        System.out.println("\nWrong credentials. Press T to try again or E to exit.");
                        String resp = scanner.nextLine().trim().toUpperCase();
                        if (resp.equals("T"))
                            continue;
                        else if (!continueOrExit())
                            return;
                        break;
                    } else {
                        System.out.println("\nAdmin login successful.");
                    }
                    if (!continueOrExit())
                        return;
                    break;

                case 2:
                    admin.logout();
                    System.out.println("\nAdmin logged out.");
                    if (!continueOrExit())
                        return;
                    break;

                case 3:
                    if (!admin.isLoggedIn()) {
                        System.out.println("\nAdmin must login first.");
                        if (!continueOrExit())
                            return;
                        break;
                    }
                    System.out.print("\nTitle: ");
                    String bt = scanner.nextLine();
                    System.out.print("Author: ");
                    String ba = scanner.nextLine();
                    System.out.print("ISBN (digits only): ");
                    String biAdd = scanner.nextLine();
                    if (!isDigits(biAdd)) {
                        System.out.println("\nInvalid ISBN.");
                        if (!continueOrExit())
                            return;
                        break;
                    }
                    List<Book> books = bookRepo.getAll();
                    boolean dup = false;
                    for (Book b : books)
                        if (b.getIsbn().equals(biAdd)) {
                            System.out.println("\nDuplicate ISBN.");
                            dup = true;
                            break;
                        }
                    if (dup) {
                        if (!continueOrExit())
                            return;
                        break;
                    }
                    books.add(new Book(bt, ba, biAdd, bookBorrowStrategy, bookFineStrategy));
                    bookRepo.saveAll(books);
                    System.out.println("\nBook added successfully.");
                    if (!continueOrExit())
                        return;
                    break;

                case 4:
                    if (!admin.isLoggedIn()) {
                        System.out.println("\nAdmin must login first.");
                        if (!continueOrExit())
                            return;
                        break;
                    }
                    System.out.print("\nCD ID: ");
                    String cid = scanner.nextLine().trim();
                    System.out.print("Title: ");
                    String ct = scanner.nextLine().trim();

                    List<CD> cdsAll = cdRepo.getAll();
                    boolean cdDup = false;
                    for (CD cd : cdsAll) {
                        if (cd.getId().equalsIgnoreCase(cid)) {
                            System.out.println("\nDuplicate CD ID.");
                            cdDup = true;
                            break;
                        }
                    }
                    if (cdDup) {
                        if (!continueOrExit())
                            return;
                        break;
                    }

                    cdsAll.add(new CD(cid, ct, cdBorrowStrategy, cdFineStrategy));
                    cdRepo.saveAll(cdsAll);
                    System.out.println("\nCD added successfully.");
                    if (!continueOrExit())
                        return;
                    break;

                case 5:
                    System.out.print("\nSearch keyword: ");
                    String k = scanner.nextLine().trim().toLowerCase();

                    List<Book> allBooks = bookRepo.getAll();
                    List<Book> bookResult = new ArrayList<>();
                    for (Book b : allBooks) {
                        if (b.getTitle().toLowerCase().contains(k)
                                || b.getAuthor().toLowerCase().contains(k)
                                || b.getIsbn().contains(k))
                            bookResult.add(b);
                    }

                    List<CD> allCds = cdRepo.getAll();
                    List<CD> cdResultSearch = new ArrayList<>();
                    for (CD cd : allCds) {
                        if (cd.getTitle().toLowerCase().contains(k)
                                || cd.getId().toLowerCase().contains(k)) {
                            cdResultSearch.add(cd);
                        }
                    }

                    if (bookResult.isEmpty() && cdResultSearch.isEmpty()) {
                        System.out.println("\nNo matching media found.");
                    } else {
                        if (!bookResult.isEmpty())
                            printBooksTable(bookResult, "BOOK SEARCH RESULTS");
                        if (!cdResultSearch.isEmpty())
                            printCdsTable(cdResultSearch, "CD SEARCH RESULTS");
                    }

                    if (!continueOrExit())
                        return;
                    break;

                case 6:
                    System.out.println("\nBorrow Media ");
                    System.out.println(" 1. Select User");
                    System.out.println(" 2. Create New User");
                    System.out.print(" Choose option [1-2]: ");
                    int userOpt = 0;
                    try {
                        userOpt = Integer.parseInt(scanner.nextLine().trim());
                    } catch (Exception e) {
                    }
                    String uid = null;
                    if (userOpt == 1) {
                        List<String> allUsersList = users.getAllUsers();
                        if (allUsersList.isEmpty()) {
                            System.out.println("\nNo users found. Please create a new user first.");
                            if (!continueOrExit())
                                return;
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
                        if (!continueOrExit())
                            return;
                        break;
                    }

                    System.out.println("\nBorrow:");
                    System.out.println(" 1. Book (by ISBN)");
                    System.out.println(" 2. CD (by ID)");
                    System.out.print(" Choose [1-2]: ");
                    int mediaOpt = 0;
                    try {
                        mediaOpt = Integer.parseInt(scanner.nextLine().trim());
                    } catch (Exception e) {
                    }

                    String idInput;
                    if (mediaOpt == 1) {
                        System.out.print("\nEnter Book ISBN: ");
                        idInput = scanner.nextLine().trim();
                        if (!isDigits(idInput)) {
                            System.out.println("\nInvalid ISBN.");
                            if (!continueOrExit())
                                return;
                            break;
                        }
                    } else if (mediaOpt == 2) {
                        System.out.print("\nEnter CD ID: ");
                        idInput = scanner.nextLine().trim();
                    } else {
                        System.out.println("\nInvalid media option.");
                        if (!continueOrExit())
                            return;
                        break;
                    }

                    boolean borrowed = borrowMedia.borrow(idInput, uid, LocalDate.now());
                    System.out.println(borrowed ? "\nBorrowed successfully." : "\nCannot borrow this media item.");
                    if (!continueOrExit())
                        return;
                    break;

                case 7:
                    if (!admin.isLoggedIn()) {
                        System.out.println("\nAdmin must login first.");
                        if (!continueOrExit()) return;
                        break;
                    }

                    System.out.print("\nUser ID: ");
                    String userid = scanner.nextLine().trim();

                    System.out.print("\nISBN to return: ");
                    String ri = scanner.nextLine().trim();

                    double outstanding = computeFine.computeOutstanding(userid, LocalDate.now());
                    boolean rb = returnBook.returnBook(ri, userid, LocalDate.now());

                    if (rb) {
                        System.out.println("\nReturned successfully.");
                    } else {
                        if (outstanding > 0) {
                            System.out.println("\nCannot return this book before paying outstanding fines. Current fine: " + outstanding);
                        } else {
                            System.out.println("\nCannot return this book. Check that:");
                            System.out.println(" - The ISBN is correct");
                            System.out.println(" - The book is currently borrowed");
                            System.out.println(" - The book is borrowed by this user ID");
                        }
                    }

                    if (!continueOrExit()) return;
                    break;

                case 8:
                    List<Media> overMedia = overdueMedia.getAllOverdues(LocalDate.now());
                    if (overMedia.isEmpty()) {
                        System.out.println("\nNo overdue media.");
                    } else {
                        List<Book> overBooks = new ArrayList<>();
                        List<CD> overCds = new ArrayList<>();
                        for (Media m : overMedia) {
                            if (m instanceof Book) overBooks.add((Book) m);
                            else if (m instanceof CD) overCds.add((CD) m);
                        }
                        if (!overBooks.isEmpty())
                            printBooksTable(overBooks, "OVERDUE BOOKS");
                        if (!overCds.isEmpty())
                            printCdsTable(overCds, "OVERDUE CDS");

                        Path path = Paths.get("DATA/OVERDUE.TXT");
                        List<String> lines = new ArrayList<>();
                        lines.add("| TYPE  | ID           | USER_ID      | DUE_DATE     |");
                        for (Media m : overMedia) {
                            String type = (m instanceof Book) ? "BOOK" : "CD";
                            lines.add(String.format("| %-5s | %-11s | %-12s | %-12s |",
                                    type,
                                    (m instanceof Book) ? ((Book) m).getIsbn() : ((CD) m).getId(),
                                    m.getBorrowerId(),
                                    m.getDueDate()));
                        }
                        try {
                            Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                            System.out.println("\nOverdue file updated: " + path.toAbsolutePath());
                        } catch (Exception e) {
                            System.out.println("\nError writing overdue file: " + e.getMessage());
                        }
                    }
                    if (!continueOrExit())
                        return;
                    break;

                case 9:
                    System.out.print("\nUser ID: ");
                    String su = scanner.nextLine().trim();
                    if (!isDigits(su)) {
                        System.out.println("\nInvalid user id.");
                        if (!continueOrExit())
                            return;
                        break;
                    }
                    double fine = computeFine.computeOutstanding(su, LocalDate.now());
                    System.out.println("\nOutstanding fine: " + fine);
                    if (!continueOrExit())
                        return;
                    break;

                case 10:
                    System.out.print("\nUser ID: ");
                    String pu = scanner.nextLine().trim();
                    System.out.print("Amount: ");
                    double amt = 0;
                    try {
                        amt = Double.parseDouble(scanner.nextLine().trim());
                    } catch (Exception e) {
                    }
                    double remain = payFine.pay(pu, amt, LocalDate.now());
                    System.out.println("\nRemaining fine: " + remain);
                    if (!continueOrExit())
                        return;
                    break;

                case 11:
                    System.out.print("\nNew loan days for books: ");
                    int d = 28;
                    try {
                        d = Integer.parseInt(scanner.nextLine().trim());
                    } catch (Exception e) {
                    }
                    settings.setLoanDays(d);
                    System.out.println("\nLoan days set to " + d + ".");
                    if (!continueOrExit())
                        return;
                    break;

                case 12:
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
                    if (!continueOrExit())
                        return;
                    break;

                case 13:
                    if (!admin.isLoggedIn()) {
                        System.out.println("\nAdmin must login first.");
                        if (!continueOrExit()) return;
                        break;
                    }
                    System.out.print("\nUser ID: ");
                    String userid2 = scanner.nextLine().trim();
                    boolean ok2 = unregisterUser.execute(userid2, LocalDate.now());
                    if (ok2) System.out.println("\nUser unregistered.");
                    else System.out.println("\nCannot unregister this user.");
                    if (!continueOrExit()) return;
                    break;

                case 14:
                    List<String> allUsers = users.getAllUsers();
                    if (allUsers.isEmpty())
                        System.out.println("\nNo users found.");
                    else
                        printUsersTable(allUsers, "ALL USERS");
                    if (!continueOrExit())
                        return;
                    break;

                case 15:
                    List<String> report = new ArrayList<>();
                    report.add("LIBRARY REPORT " + nowStr());
                    report.add("Books: " + bookRepo.getAll().size());
                    report.add("CDs: " + cdRepo.getAll().size());
                    report.add("Users: " + users.getAllUsers().size());
                    Path rp = Paths.get("DATA/REPORTS.TXT");
                    try {
                        Files.write(rp, report);
                    } catch (Exception e) {
                    }
                    System.out.println("\nReports written to: " + rp.toAbsolutePath());
                    if (!continueOrExit())
                        return;
                    break;

                case 16:
                    try {
                        Files.deleteIfExists(Paths.get("DATA/BOOKS.TXT"));
                        Files.deleteIfExists(Paths.get("DATA/CDS.TXT"));
                        Files.deleteIfExists(Paths.get("DATA/BORROWS.TXT"));
                        Files.deleteIfExists(Paths.get("DATA/PAYMENTS.TXT"));
                        Files.deleteIfExists(Paths.get("DATA/USERS.TXT"));
                        Files.deleteIfExists(Paths.get("DATA/SETTINGS.TXT"));
                        Files.deleteIfExists(Paths.get("DATA/REPORTS.TXT"));
                        Files.deleteIfExists(Paths.get("DATA/OVERDUE.TXT"));
                    } catch (Exception e) {
                    }
                    System.out.println("\nAll data files reset.");
                    if (!continueOrExit())
                        return;
                    break;

                case 17:
                    printBooksTable(bookRepo.getAll(), "ALL BOOKS");
                    if (!continueOrExit())
                        return;
                    break;

                case 18:
                    printCdsTable(cdRepo.getAll(), "ALL CDS");
                    if (!continueOrExit())
                        return;
                    break;

                case 19:
                    int sent = sendReminders.sendAll(LocalDate.now());
                    if (sent == 0)
                        System.out.println("\nNo overdue users. No reminders sent.");
                    else {
                        for (String msg : sendReminders.getSentLogs())
                            System.out.println(msg);
                        System.out.println("\n" + sent + " reminders sent successfully.");
                    }
                    if (!continueOrExit())
                        return;
                    break;

                case 20:
                    System.out.println("\nExiting Library App. Goodbye!");
                    return;

                default:
                    System.out.println("\nInvalid option!");
                    if (!continueOrExit())
                        return;
            }
        }
    }

    private static boolean continueOrExit() {
        System.out.println("\nPress Enter to return to menu or type E to exit:");
        String input = scanner.nextLine().trim().toUpperCase();
        return !input.equals("E");
    }
}
