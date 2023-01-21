package me.chickxn.driver;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public void createTables() {
        if (isConnected()) {
            try {
                Statement statement = this.getConnection().createStatement();
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS permission_groups (groupName VARCHAR(255), groupPermissions VARCHAR(255), groupID VARCHAR(255), groupPrefix VARCHAR(255), groupTablistColor VARCHAR(255), groupSuffix VARCHAR(255))");
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS permission_player (uuid VARCHAR(255),currentGroup VARCHAR(255), playerPermissions VARCHAR(255))");
                statement.executeUpdate("ALTER TABLE permission_groups ADD UNIQUE (groupName)");
                statement.executeUpdate("INSERT INTO permission_groups (groupName, groupPermissions, groupID, groupPrefix, groupTablistColor, groupSuffix) VALUES ('admin', '" + List.of("module.use") + "', '001', '§cAdmin', '§c', '§7')");
                statement.executeUpdate("INSERT INTO permission_groups (groupName, groupPermissions, groupID, groupPrefix, groupTablistColor, groupSuffix) VALUES ('default', '" + List.of("module.use") + "', '002', '§7default', '§7', '§7')");
            } catch (SQLException e) {

            }
        }else{
            connect();
        }
    }
}