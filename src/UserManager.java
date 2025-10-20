import java.sql.*;
import java.util.Scanner;
public class UserManager {
    Scanner sc = new Scanner(System.in);

    public void registerUser() {
        try (Connection conn = DbConnect.getConnection()) {
            System.out.print("Enter Your Name");
            String name = sc.nextLine();
            System.out.println("Enter Your Email :");
            String email = sc.nextLine();
            String sql = "insert into users(name,email,borrowed_count) values(?,?,0)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, name);
            stmt.setString(2, email);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int user_id = rs.getInt(1);
                    System.out.println("Register Successfully With User ID: " + user_id);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void searchUser() {
        try (Connection conn = DbConnect.getConnection()) {
            System.out.print("Enter Name To Find");
            String name = sc.nextLine();
            System.out.println("Enter User Id");
            int user_id = sc.nextInt();
            String sql = "select * from users where name like ? AND user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + name + "%");
            stmt.setInt(2, user_id);
            ResultSet rs = stmt.executeQuery();
            System.out.println("👤 User Results:");
            while (rs.next()) {
                System.out.println(rs.getInt("user_id") + " | " + rs.getString("name") + "|" + rs.getInt("borrowed_count") + "|" + rs.getString("borrowed_books"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
