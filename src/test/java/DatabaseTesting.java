import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseTesting {

    public static void main(String[] args) {
        try {
            // The newInstance() call is a work around for some
            // broken Java implementations

            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception ex) {
            // handle the error
        }
        Connection conn;

        try {
            conn = DriverManager.getConnection("rds-mysql-10mintutorial.cexszyenhnyx.us-east-2.rds.amazonaws.com", "masterUsername", "Bloodrose123");
            // Do something with the Connection
            System.out.println(conn.getClientInfo());
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }
}
