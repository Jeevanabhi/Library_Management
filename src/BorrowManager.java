import java.sql.*;
import java.util.Scanner;
public class BorrowManager {
    Scanner sc = new Scanner(System.in);
    public void borrowBook() {
        try (Connection conn = DbConnect.getConnection()) {
            System.out.print("Enter User ID: ");
            int userId = sc.nextInt();
            System.out.print("Enter Book ID: ");
            int bookId = sc.nextInt();

            // check book availability
            String checkBook = "SELECT available FROM books WHERE id = ?";
            PreparedStatement stmt1 = conn.prepareStatement(checkBook);
            stmt1.setInt(1, bookId);
            ResultSet rs1 = stmt1.executeQuery();
            if (rs1.next() && rs1.getBoolean("available")) {
                String borrowSQL = "INSERT INTO borrow_records (user_id, book_id, borrow_date) VALUES (?, ?, CURDATE())";
                PreparedStatement stmt2 = conn.prepareStatement(borrowSQL);
                stmt2.setInt(1, userId);
                stmt2.setInt(2, bookId);
                stmt2.executeUpdate();

                String updateBook = "UPDATE books SET available = false WHERE id = ?";
                PreparedStatement stmt3 = conn.prepareStatement(updateBook);
                stmt3.setInt(1, bookId);
                stmt3.executeUpdate();

                String updateUser = "UPDATE users SET borrowed_count = borrowed_count + 1 WHERE id = ?";
                PreparedStatement stmt4 = conn.prepareStatement(updateUser);
                stmt4.setInt(1, userId);
                stmt4.executeUpdate();

                System.out.println("📖 Book Borrowed Successfully!");
            } else {
                System.out.println("❌ Book not available!");
            }
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

            String returnSQL = "UPDATE borrow_records SET return_date = CURDATE() WHERE user_id = ? AND book_id = ? AND return_date IS NULL";
            PreparedStatement stmt1 = conn.prepareStatement(returnSQL);
            stmt1.setInt(1, userId);
            stmt1.setInt(2, bookId);
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

