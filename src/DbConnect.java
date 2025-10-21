import java.sql.*;
public class DbConnect {
    private static final String URL = "jdbc:mysql://interchange.proxy.rlwy.net:58187/railway";
    private static final String USER = "root";
    private static final String PASSWORD = "jlYtuOEUTeLYBFwVfxFknhHdGostKiMW";
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
