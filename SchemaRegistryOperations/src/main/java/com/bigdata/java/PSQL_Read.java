package com.bigdata.java;

import java.sql.*;

public class PSQL_Read {
    public static void main(String[] args) {
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://192.168.0.144:5432/bigdatadb", "bigdata", "bigdata");
            System.out.println("Connected Successfully!");

            Statement stmt = connection.createStatement();
            String query = "select * from employees";

            ResultSet resultSet = stmt.executeQuery(query);

            while (resultSet.next()) {
                int empId = resultSet.getInt("eid");
                System.out.println("Employee ID :: " + empId);
            }

            resultSet.close();
            stmt.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
