package library_system;
import java.util.Scanner;

public class LIBRARY_APP {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ADMIN admin = new ADMIN("THAER", "777");
        LIBRARY library = new LIBRARY();

        while (true) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║           L I B R A R Y   A P P        ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║  1  ADMIN LOGIN                        ║");
            System.out.println("║  2  ADMIN LOGOUT                       ║");
            System.out.println("║  3  ADD BOOK                           ║");
            System.out.println("║  4  SEARCH BOOK                        ║");
            System.out.println("║  5  EXIT                               ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.print(" Enter option [1-5]: ");
            int choice = 0;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch(Exception e) {
                System.out.println(" Invalid input! Enter a number between 1 and 5.");
                continue;
            }

            System.out.println("\n----------------------------------------");

            switch (choice) {
                case 1:
                    if(admin.isLoggedIn()) {
                        System.out.println(" Already logged in. You are the master of the library.");
                        break;
                    }
                    String username, password;
                    while(true) {
                        System.out.print(" Username: ");
                        username = scanner.nextLine();
                        if(username.trim().isEmpty()) {
                            System.out.println(" Invalid username! Try again.");
                            continue;
                        }
                        break;
                    }
                    while(true) {
                        System.out.print(" Password: ");
                        password = scanner.nextLine();
                        if(password.trim().isEmpty()) {
                            System.out.println(" Invalid password! Try again.");
                            continue;
                        }
                        break;
                    }
                    if(admin.login(username, password)) {
                        System.out.println(" Login successful. Welcome, " + username.trim() + "!");
                    } else {
                        System.out.println(" Invalid credentials.");
                    }
                    break;

                case 2:
                    if(admin.isLoggedIn()) {
                        admin.logout();
                        System.out.println(" You have successfully logged out.");
                    } else {
                        System.out.println(" You are not logged in.");
                    }
                    break;

                case 3:
                    if(!admin.isLoggedIn()) {
                        System.out.println(" Please login first to add books.");
                        break;
                    }
                    String title, author, isbn;
                    while(true) {
                        System.out.print(" Book Title : ");
                        title = scanner.nextLine();
                        if(title.trim().isEmpty()) {
                            System.out.println(" Invalid title! Try again.");
                            continue;
                        }
                        break;
                    }
                    while(true) {
                        System.out.print(" Book Author: ");
                        author = scanner.nextLine();
                        if(author.trim().isEmpty()) {
                            System.out.println(" Invalid author! Try again.");
                            continue;
                        }
                        break;
                    }
                    while(true) {
                        System.out.print(" Book ISBN  : ");
                        isbn = scanner.nextLine();
                        if(isbn.trim().isEmpty()) {
                            System.out.println(" Invalid ISBN! Try again.");
                            continue;
                        }
                        break;
                    }
                    library.addBook(new BOOK(title, author, isbn));
                    System.out.println(" Book added successfully to the library!");
                    break;

                case 4:
                    System.out.print(" Enter title, author, or ISBN to search: ");
                    String keyword = scanner.nextLine();
                    library.searchBook(keyword);
                    break;

                case 5:
                    System.out.println(" Exiting Library App. Goodbye!");
                    scanner.close();
                    return;

                default:
                    System.out.println(" Invalid option! Please choose a number between 1 and 5.");
            }

            System.out.println("----------------------------------------");
        }
    }
}
