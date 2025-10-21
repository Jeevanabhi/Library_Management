import java.sql.*;
import java.time.LocalDate;
import java.sql.Date;
import java.util.Scanner;
public class BorrowManager {
    Scanner sc = new Scanner(System.in);
    LocalDate today = LocalDate.now();
    Date sqlDate = Date.valueOf(today);
    public void borrowBook() {
        try (Connection conn = DbConnect.getConnection()) {
            System.out.print("Enter User ID: ");
            int userId = sc.nextInt();
            System.out.print("Enter Book ID: ");
            int bookId = sc.nextInt();

            // check book availability
            String checkBook = "SELECT available FROM books WHERE book_id = ?";
            PreparedStatement stmt1 = conn.prepareStatement(checkBook);
            stmt1.setInt(1, bookId);
            ResultSet rs1 = stmt1.executeQuery();
            if (rs1.next() && rs1.getBoolean("available")) {
                String borrowSQL = "INSERT INTO borrowed_books (user_id, book_id, borrow_date,return_date) VALUES (?, ?,?,?)";
                PreparedStatement stmt2 = conn.prepareStatement(borrowSQL);
                stmt2.setInt(1, userId);
                stmt2.setInt(2, bookId);
                stmt2.setDate(3, sqlDate);
                stmt2.setNull(4, java.sql.Types.DATE);

                stmt2.executeUpdate();

                String books_name = null;

                String getBookName = "SELECT book_name, available FROM books WHERE book_id = ?";
                PreparedStatement stmt4 = conn.prepareStatement(getBookName);
                stmt4.setInt(1, bookId);
                ResultSet rs4 = stmt4.executeQuery();

                if (rs4.next()) {
                    System.out.println("Book found in DB");
                    boolean available = rs4.getBoolean("available");
                    System.out.println("Available: " + available);
                    if (available) {
                        books_name = rs4.getString("book_name");
                        System.out.println("Book Name fetched: " + books_name);
                    } else {
                        System.out.println("Book exists but not available");
                    }
                } else {
                    System.out.println("No book found with book_id = " + bookId);
                }

                String updateUser = "UPDATE users SET borrowed_count = borrowed_count + 1,borrowed_books=? WHERE user_id = ?";
                PreparedStatement stmt5 = conn.prepareStatement(updateUser);
                stmt5.setString(1,books_name);
                stmt5.setInt(2, userId);
                System.out.println("Book name: " + books_name);
                stmt5.executeUpdate();

                System.out.println("📖 Book Borrowed Successfully!");
            } else {
                System.out.println("❌ Book not available!");
            }

            String updateBook = "UPDATE books SET available = false WHERE book_id = ?";
            PreparedStatement stmt3 = conn.prepareStatement(updateBook);
            stmt3.setInt(1, bookId);
            stmt3.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void returnBook() {
        try (Connection conn = DbConnect.getConnection()) {
            System.out.print("Enter User ID: ");
            int userId = sc.nextInt();
            System.out.print("Enter Book ID: ");
            int bookId = sc.nextInt();

            String returnSQL = "UPDATE borrowed_books SET return_date = ? WHERE user_id = ? AND book_id = ? AND return_date IS NULL";
            PreparedStatement stmt1 = conn.prepareStatement(returnSQL);
            stmt1.setDate(1, sqlDate);
            stmt1.setInt(2, userId);
            stmt1.setInt(3, bookId);
            int updated = stmt1.executeUpdate();

            if (updated > 0) {
                String updateBook = "UPDATE books SET available = true WHERE id = ?";
                PreparedStatement stmt2 = conn.prepareStatement(updateBook);
                stmt2.setInt(1, bookId);
                stmt2.executeUpdate();

                String updateUser = "UPDATE users SET borrowed_count = borrowed_count - 1 WHERE id = ?";
                PreparedStatement stmt3 = conn.prepareStatement(updateUser);
                stmt3.setInt(1, userId);
                stmt3.executeUpdate();

                System.out.println("✅ Book Returned Successfully!");
            } else {
                System.out.println("⚠️ No active borrow record found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

