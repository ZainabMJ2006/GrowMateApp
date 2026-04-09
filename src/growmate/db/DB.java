package growmate.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    private static final String URL  =
        "jdbc:mysql://localhost:3306/growmate?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";       // <-- your MySQL username
    private static final String PASS = "200118118";  // <-- your MySQL password

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
