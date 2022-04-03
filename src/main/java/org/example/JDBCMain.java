package org.example;

import java.sql.*;

public class JDBCMain {

     public static void main(String[] args) throws SQLException, ClassNotFoundException {

        //Class.forName("org.postgresql.Driver");
        final String url = "jdbc:derby:zoo";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("select a from b")) {

            System.out.println(conn);
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }

            boolean isResultSet = stmt.execute("select a from b");
            if (isResultSet) {
                stmt.getResultSet();
            } else {
                stmt.getUpdateCount();
            }
        }
    }

   /* public static void main(String[] args) {

        Connection connection = null;
        Statement statement = null;

        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/testphones","root", "pass"
            );
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        if (connection != null) {
            try {
                statement = connection.createStatement();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        };
    }*/
}
