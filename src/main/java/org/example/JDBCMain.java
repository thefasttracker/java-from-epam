package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCMain {
    public static void main(String[] args) {

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
    }
}
