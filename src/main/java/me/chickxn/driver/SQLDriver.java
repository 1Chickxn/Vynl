package me.chickxn.driver;

import java.sql.*;

public class SQLDriver {

    private final String hostname;
    private final String database;
    private final String username;
    private final String password;
    private final int port;
    private Connection connection;

    public SQLDriver(String hostname, String database, String username, String password, int port) {
        this.hostname = hostname;
        this.database = database;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public boolean connect() {
        if (!this.isConnected()) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + port + "/" + database, username, password);
                return true;
            } catch (SQLException sqlexception) {
                sqlexception.printStackTrace();
            } catch (ClassNotFoundException classNotFoundException) {
                classNotFoundException.printStackTrace();
            }
        }
        return false;
    }

    public boolean disconnect() {
        if (this.isConnected()) {
            try {
                connection.close();
                connection = null;
                return true;
            } catch (SQLException sqlexception) {
                sqlexception.printStackTrace();
            }
        }
        return false;
    }


    public ResultSet query(final String query) {
        if (!this.isConnected()) {
            this.connect();
        }
        try {
            return this.query(this.connection.prepareStatement(query));
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public ResultSet query(final PreparedStatement preparedStatement) {
        if (!this.isConnected()) {
            this.connect();
        }
        try {
            return preparedStatement.executeQuery();
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void update(final PreparedStatement preparedStatement) {
        if (!this.isConnected()) {
            this.connect();
        }
        try {
            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            try {
                preparedStatement.close();
            } catch (SQLException sqlException1) {
                sqlException1.printStackTrace();
            }
        } finally {
            try {
                preparedStatement.close();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
    }

    public boolean isConnected() {
        return connection != null;
    }

    public Connection getConnection() {
        return connection;
    }

    public static class ConnectionException extends Exception {
        private static final long serialVersionUID = 8348749992936357317L;

        public ConnectionException(String message) {
            super(message);
        }
    }

}