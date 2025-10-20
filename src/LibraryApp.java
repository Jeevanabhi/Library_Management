import java.util.Scanner;

public class LibraryApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        BookManager bm = new BookManager();
        UserManager um = new UserManager();
        BorrowManager br = new BorrowManager();

        while (true) {
            System.out.println("\n===== 📚 LIBRARY MANAGEMENT =====");
            System.out.println("1. Add Book");
            System.out.println("2. Search Book");
            System.out.println("3. Register User");
            System.out.println("4. Search User");
            System.out.println("5. Borrow Book");
            System.out.println("6. Return Book");
            System.out.println("7. Update Book");
            System.out.println("8. Delete Book");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1: bm.addBook(); break;
                case 2: bm.searchBook(); break;
                case 3: um.registerUser(); break;
                case 4: um.searchUser(); break;
                case 5: br.borrowBook(); break;
                case 6: br.returnBook(); break;
                case 7: bm.updateBook(); break;
                case 8: bm.deleteBook(); break;
                case 0:
                    System.out.println("👋 Exiting... Bye!");
                    System.exit(0);
                default: System.out.println("❌ Invalid choice!");
            }
        }
    }
}

