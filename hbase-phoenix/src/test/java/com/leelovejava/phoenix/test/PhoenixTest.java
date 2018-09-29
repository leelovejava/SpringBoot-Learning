package com.leelovejava.phoenix.test;

import java.sql.*;

/**
 * Created by jixin on 18-2-25.
 */
public class PhoenixTest {
    // com.mysql.jdbc.Driver
    private static String driver = "org.apache.phoenix.jdbc.PhoenixDriver";

    /**
     * systemctl start mariadb.service
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        Class.forName(driver);

        Connection connection = DriverManager.getConnection("jdbc:phoenix:192.168.109.130:2181");

        PreparedStatement statement = connection.prepareStatement("select * from PERSON");

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            System.out.println(resultSet.getString("NAME"));
        }

        statement.close();
        connection.close();
    }
}
