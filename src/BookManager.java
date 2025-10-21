import java.sql.*;
import java.util.Scanner;
public class BookManager {
    Scanner sc = new Scanner(System.in);
    public void addBook(){
        try(Connection conn = DbConnect.getConnection()){
            System.out.println("Enter Book Name: ");
            String bookName = sc.nextLine();
            System.out.print("Enter Author: ");
            String author = sc.nextLine();
            System.out.print("Enter ISBN: ");
            String isbn = sc.nextLine();
            System.out.print("Enter Genre: ");
            String genre = sc.nextLine();
            String sql = "insert into books(book_name,genre,author,isbn) values(?,?,?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1,bookName);
            stmt.setString(2,genre);
            stmt.setString(3,author);
            stmt.setString(4,isbn);
            int rows = stmt.executeUpdate();
            if(rows > 0){
                ResultSet rs = stmt.getGeneratedKeys();
                if(rs.next()){
                    int book_id = rs.getInt(1);
                    System.out.println("Book Registered Successfully With ID: " + book_id);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public void searchBook() {
        try (Connection conn = DbConnect.getConnection()) {
            System.out.print("Enter book title or author to search: ");
            String keyword = sc.nextLine();

            String sql = "SELECT * FROM books WHERE book_name LIKE ? OR author LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");

            ResultSet rs = stmt.executeQuery();
            System.out.println("🔎 Search Results:");
            while (rs.next()) {
                System.out.println(rs.getInt("book_id") + " | " + rs.getString("book_name") + " | " + rs.getString("author") + " | Available: " + rs.getBoolean("available"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void deleteBook() {
        try (Connection conn = DbConnect.getConnection()){
            System.out.print("Enter book Id ");
            int book_id = sc.nextInt();
            String sql = "DELETE FROM books WHERE book_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1,book_id);
            int rows = stmt.executeUpdate();
            if(rows > 0){
                System.out.println("Book Deleted Successfully");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void updateBook() {
        try (Connection conn = DbConnect.getConnection()){
            System.out.print("Enter book Id ");
            int book_id = sc.nextInt();
            sc.nextLine();
            System.out.println("Enter Book Name: ");
            String bookName = sc.nextLine();
            System.out.print("Enter Author: ");
            String author = sc.nextLine();
            System.out.print("Enter ISBN: ");
            String isbn = sc.nextLine();
            System.out.print("Enter Genre: ");
            String genre = sc.nextLine();
            String sql = "update books set book_name = ?,genre = ?, author = ?, isbn = ? where book_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,bookName);
            stmt.setString(2,genre);
            stmt.setString(3,author);
            stmt.setString(4,isbn);
            stmt.setInt(5,book_id);
            int rows = stmt.executeUpdate();
            if(rows > 0){
                System.out.println("Book Updated Successfully");}
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
