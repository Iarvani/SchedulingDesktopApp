package Utilitys;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** This class is used to connect to the database. */
public class DBConnection {
    //JDBC URL Parts
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String ipAddress = "//wgudb.ucertify.com/WJ05sem";

    //JDBC URL Connection string
    private static final String jdbcURL =  protocol + vendor + ipAddress;

    //JDBC driver interface reference
    private static final String mysqlJDBCdriver = "com.mysql.cj.jdbc.Driver";
    private static Connection conn = null;

    //Database credentials
    private static final String dbUsername = "U05sem";
    private static final String dbPassword = "53688595683";
    public final Connection connect = startConnection();

    public String getUN() {
        return dbUsername;
    }

    public String getPass() {
        return dbPassword;
    }

    public String getURL() {
        return jdbcURL;
    }
    /**
     * Method used to upload the JBDC driver, username, password for the Database connection
     * Creates the JDBC string from the protocl, vendor, ipaddress strings
     * @return Returns connection string for the DB.
     */
    //start the database connection with a try / catch error report
    public static Connection startConnection() {
        try {
            Class.forName(mysqlJDBCdriver);
            conn = DriverManager.getConnection(jdbcURL,dbUsername,dbPassword);
        }
        catch(ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        catch(SQLException e) {
            System.out.println("SQL Exception" + e.getMessage());
        }
        return conn;
    }
    /**
     * Method to close the connection to the database
     */
    //Close the database connection
    public static void closeConnection() {
        try {
            conn.close();
            System.out.println("Database connection closed.");
        }
        catch(SQLException e) {
            System.out.println("SQLException" + e.getMessage());
        }
    }
}
