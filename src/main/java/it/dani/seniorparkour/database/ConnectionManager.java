package it.dani.seniorparkour.database;

import lombok.Builder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Builder
public class ConnectionManager {
    private final String hostname, user, password, database;
    private final int port;

    public Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://%s:%d/%s?";
        url = String.format(url,hostname,port,database);

        return DriverManager.getConnection(url,user,password);
    }
}
