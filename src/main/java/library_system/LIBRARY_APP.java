package library_system;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class LIBRARY_APP {
    private static boolean isDigits(String s) {
        if (s == null || s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) if (!Character.isDigit(s.charAt(i))) return false;
        return true;
    }

    private static void printBooksTable(List<BOOK> list, String title) {
        List<String[]> rows = new ArrayList<>();
        for (BOOK b : list) {
            String st = b.isBorrowed() ? "BORROWED" : "FREE";
            String due = b.getDueDate() == null ? "-" : b.getDueDate().toString();
            rows.add(new String[]{b.getTitle(), b.getAuthor(), b.getIsbn(), st, due});
        }
        TABLE.print(title, new String[]{"TITLE","AUTHOR","ISBN","STATUS","DUE"}, rows);
    }

    private static void printUsersTable(List<String> users) {
        List<String[]> rows = new ArrayList<>();
        for (String line : users) {
            String[] p = line.split("\\s*\\|\\s*", -1);
            String id = p.length>0?p[0]:"-";
            String nm = p.length>1?p[1]:"-";
            String ph = p.length>2?p[2]:"-";
            String em = p.length>3?p[3]:"-";
            rows.add(new String[]{id,nm,ph,em});
        }
        TABLE.print("USERS", new String[]{"USER_ID","NAME","PHONE","EMAIL"}, rows);
    }

    private static String nowStr() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private static void exportSingleReport(LIBRARY library) {
        List<String> lines = new ArrayList<>();
        lines.add("FORMAT: SECTIONS");
        lines.add("TITLE: REPORTS");
        lines.add("DATE: " + nowStr());
        lines.add("-----");
        lines.add("[BOOKS]");
        lines.add("HEADER: ISBN | TITLE | AUTHOR | STATUS | USER_ID | DUE_DATE");
        for (BOOK x : library.getBooks()) {
            String st = x.isBorrowed() ? "BORROWED" : "FREE";
            String uid = x.getBorrowerId() == null ? "-" : x.getBorrowerId();
            String due = x.getDueDate() == null ? "-" : x.getDueDate().toString();
            lines.add(x.getIsbn()+" | "+x.getTitle()+" | "+x.getAuthor()+" | "+st+" | "+uid+" | "+due);
        }
        lines.add("");
        lines.add("[USERS]");
        lines.add("USER_ID | NAME | PHONE | EMAIL");
        for (String line : STORAGE.readDataLines(STORAGE.USERS_FILE)) lines.add(line);
        lines.add("");
        lines.add("[BORROWS]");
        lines.add("ISBN | USER_ID | DUE_DATE");
        for (String line : STORAGE.readDataLines(STORAGE.BORROWS_FILE)) lines.add(line);
        lines.add("");
        lines.add("[PAYMENTS]");
        lines.add("USER_ID | AMOUNT | DATE");
        for (String line : STORAGE.readDataLines(STORAGE.PAYMENTS_FILE)) lines.add(line);
        lines.add("");
        lines.add("[SETTINGS]");
        lines.add("KEY = VALUE");
        for (String line : STORAGE.readDataLines(STORAGE.SETTINGS_FILE)) lines.add(line);

        STORAGE.writeDataLines(STORAGE.REPORTS_FILE, lines);
        java.nio.file.Path rp = java.nio.file.Paths.get(STORAGE.REPORTS_FILE).toAbsolutePath();
        System.out.println("Reports written to: " + rp.toString());
    }

    public static void main(String[] args) {
        STORAGE.ensureFiles();
        if (SETTINGS.getLoanDays() <= 0) SETTINGS.setLoanDays(28);
        Scanner scanner = new Scanner(System.in);
        ADMIN admin = new ADMIN("THAER", "777");
        LIBRARY library = new LIBRARY();
        BORROW_SERVICE borrowService = new BORROW_SERVICE();
        PAYMENT_SERVICE paymentService = new PAYMENT_SERVICE();

        while (true) {
            System.out.println();
            TABLE.print("\n      ~ LIBRARY APP ~", new String[]{"OPT","ACTION"}, java.util.Arrays.asList(
                new String[][]{
                    {"1","ADMIN LOGIN"},
                    {"2","ADMIN LOGOUT"},
                    {"3","ADD BOOK"},
                    {"4","SEARCH BOOK"},
                    {"5","BORROW BOOK"},
                    {"6","RETURN BOOK"},
                    {"7","LIST OVERDUE"},
                    {"8","SHOW OUTSTANDING"},
                    {"9","PAY FINE"},
                    {"10","SET LOAN DAYS"},
                    {"11","CREATE USER"},
                    {"12","LIST USERS"},
                    {"13","EXPORT REPORTS.TXT"},
                    {"14","RESET DATA FILES"},
                    {"15","LIST ALL BOOKS"},
                    {"16","EXIT"}
                }
            ));
            System.out.print(" Enter option [1-16]: ");
            int choice = 0;
            try { choice = Integer.parseInt(scanner.nextLine().trim()); } catch(Exception e) { System.out.println("Invalid input."); continue; }

            switch (choice) {
                case 1:
                    System.out.print("\n Username: ");
                    String u = scanner.nextLine();
                    System.out.print(" Password: ");
                    String p = scanner.nextLine();
                    boolean ok = admin.login(u, p);
                    System.out.println(ok ? " Admin login successful." : "Admin login failed.");
                    break;
                case 2:
                    admin.logout();
                    System.out.println(" Admin logged out.");
                    break;
                case 3:
                    if (!admin.isLoggedIn()) { System.out.println(" Admin must be logged in to add books."); break; }
                    System.out.print(" Title: ");
                    String t = scanner.nextLine();
                    System.out.print(" Author: ");
                    String a = scanner.nextLine();
                    System.out.print(" ISBN: ");
                    String i = scanner.nextLine();
                    if (!isDigits(i)) { System.out.println(" Invalid ISBN. Digits only."); break; }
                    boolean added = library.addBook(new BOOK(t,a,i));
                    library.refresh();
                    System.out.println(added ? " Book added." : " Invalid or duplicate book.");
                    break;
                case 4:
                    System.out.print(" Search keyword: ");
                    String k = scanner.nextLine();
                    List<BOOK> res = library.search(k);
                    if (res.isEmpty()) System.out.println(" No matching books found.");
                    else printBooksTable(res, "\n          SEARCH RESULTS");
                    break;
                case 5:
                    System.out.print(" ISBN: ");
                    String bi = scanner.nextLine().trim();
                    if (!isDigits(bi)) { System.out.println(" Invalid ISBN."); break; }
                    System.out.print(" User ID (digits only, empty=create): ");
                    String uid = scanner.nextLine().trim();
                    if (uid.isEmpty()) {
                        System.out.print(" New User ID (digits only): ");
                        uid = scanner.nextLine().trim();
                        if (!isDigits(uid)) { System.out.println(" Invalid user id."); break; }
                        System.out.print(" Name: ");
                        String nm = scanner.nextLine().trim();
                        System.out.print(" Phone: ");
                        String ph = scanner.nextLine().trim();
                        System.out.print(" Email: ");
                        String em = scanner.nextLine().trim();
                        if (nm.isEmpty()) nm = uid;
                        if (ph.isEmpty()) ph = "-";
                        if (em.isEmpty()) em = "-";
                        SETTINGS.ensureUserExists(uid, nm, ph, em);
                    } else {
                        if (!isDigits(uid)) { System.out.println(" Invalid user id."); break; }
                        SETTINGS.ensureUserExists(uid, uid, "-", "-");
                    }
                    BOOK bb = library.findByIsbn(bi);
                    if (bb == null) { System.out.println(" Book not found."); break; }
                    System.out.println(" Confirm borrow:");
                    System.out.println(" Title: " + bb.getTitle());
                    System.out.println(" Author: " + bb.getAuthor());
                    System.out.println(" ISBN: " + bb.getIsbn());
                    System.out.print(" Confirm [ Y / N ]: ");
                    String cf = scanner.nextLine().trim();
                    if (!cf.equalsIgnoreCase("Y")) { System.out.println(" Cancelled."); break; }
                    boolean b = borrowService.borrow(library, bi, uid, LocalDate.now());
                    library.syncBorrowFlagsFromFile();
                    System.out.println(b ? " Borrowed successfully." : " Cannot borrow.");
                    break;
                case 6:
                    System.out.print(" ISBN to return: ");
                    String ri = scanner.nextLine();
                    if (!isDigits(ri)) { System.out.println(" Invalid ISBN."); break; }
                    boolean rb = borrowService.returnBook(library, ri);
                    library.syncBorrowFlagsFromFile();
                    System.out.println(rb ? " Returned successfully." : " Cannot return.");
                    break;
                case 7:
                    List<BOOK> over = OVERDUE.list(library, LocalDate.now());
                    if (over.isEmpty()) System.out.println(" No overdue books.");
                    else {
                        List<String[]> rows = new ArrayList<>();
                        for (BOOK ob : over) rows.add(new String[]{ob.getTitle(), ob.getAuthor(), ob.getIsbn(), ob.getDueDate()==null?"-":ob.getDueDate().toString()});
                        TABLE.print(" OVERDUE", new String[]{"TITLE","AUTHOR","ISBN","DUE_DATE"}, rows);
                    }
                    break;
                case 8:
                    System.out.print(" User ID: ");
                    String su = scanner.nextLine().trim();
                    if (!isDigits(su)) { System.out.println(" Invalid user id."); break; }
                    double out = FINE.computeOutstanding(su, library, LocalDate.now());
                    TABLE.print(" OUTSTANDING", new String[]{"USER_ID","AMOUNT"}, java.util.Arrays.asList(new String[][]{{su, String.valueOf(out)}}));
                    break;
                case 9:
                    System.out.print(" User ID: ");
                    String pu = scanner.nextLine().trim();
                    if (!isDigits(pu)) { System.out.println(" Invalid user id."); break; }
                    System.out.print(" Amount to pay: ");
                    double amt = 0.0;
                    try { amt = Double.parseDouble(scanner.nextLine().trim()); } catch(Exception e) { amt = 0.0; }
                    double remain = new PAYMENT_SERVICE().pay(pu, library, amt, LocalDate.now());
                    TABLE.print(" PAYMENT RESULT", new String[]{"USER_ID","REMAINING"}, java.util.Arrays.asList(new String[][]{{pu, String.valueOf(remain)}}));
                    break;
                case 10:
                    System.out.print(" New loan days: ");
                    int d = 28;
                    try { d = Integer.parseInt(scanner.nextLine().trim()); } catch(Exception e) { d = 28; }
                    if (d <= 0) d = 28;
                    SETTINGS.setLoanDays(d);
                    System.out.println(" Loan days set to " + d + ".");
                    break;
                case 11:
                    System.out.print(" User ID: ");
                    String nid = scanner.nextLine().trim();
                    if (!isDigits(nid)) { System.out.println(" Invalid user id."); break; }
                    System.out.print(" Name: ");
                    String nn = scanner.nextLine().trim();
                    System.out.print(" Phone: ");
                    String np = scanner.nextLine().trim();
                    System.out.print(" Email: ");
                    String ne = scanner.nextLine().trim();
                    if (nn.isEmpty()) nn = nid;
                    if (np.isEmpty()) np = "-";
                    if (ne.isEmpty()) ne = "-";
                    SETTINGS.ensureUserExists(nid, nn, np, ne);
                    System.out.println(" User saved.");
                    break;
                case 12:
                    List<String> users = STORAGE.readDataLines(STORAGE.USERS_FILE);
                    if (users.isEmpty()) System.out.println(" No users.");
                    else printUsersTable(users);
                    break;
                case 13:
                    exportSingleReport(library);
                    break;
                case 14:
                    STORAGE.writeDataLines(STORAGE.BOOKS_FILE, new ArrayList<String>());
                    STORAGE.writeDataLines(STORAGE.BORROWS_FILE, new ArrayList<String>());
                    STORAGE.writeDataLines(STORAGE.PAYMENTS_FILE, new ArrayList<String>());
                    STORAGE.writeDataLines(STORAGE.USERS_FILE, new ArrayList<String>());
                    STORAGE.writeDataLines(STORAGE.SETTINGS_FILE, new ArrayList<String>());
                    STORAGE.ensureFiles();
                    library.refresh();
                    System.out.println(" All Data files reset.");
                    break;
                case 15:
                    printBooksTable(library.getBooks(), " ALL BOOKS");
                    break;
                case 16:
                    System.out.println(" Exiting Library App. Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println(" Invalid option!");
            }
        }
    }
}






