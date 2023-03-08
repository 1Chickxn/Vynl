package me.chickxn.spigot.handler;

import lombok.Getter;
import me.chickxn.spigot.Vynl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public final class PermissionHandler {
    private final File config = new File("plugins/Vynl/permissions.yml");

    @Getter
    private final YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(config);

    private final Map<String, PermissionAttachment> permissions = new HashMap<>();

    public PermissionHandler() {
        try {
            if (!config.exists()) {
                config.createNewFile();
                yamlConfiguration.options().header("Permissions");
                yamlConfiguration.options().copyDefaults(true);
                yamlConfiguration.addDefault("mysql.use", false);
                yamlConfiguration.addDefault("mysql.hostname", "hostname");
                yamlConfiguration.addDefault("mysql.database", "database");
                yamlConfiguration.addDefault("mysql.username", "username");
                yamlConfiguration.addDefault("mysql.password", "passowrd");
                yamlConfiguration.addDefault("mysql.port", 3306);

                yamlConfiguration.addDefault("prefix.chat", "{group} {player} §8| §7{message} {suffix}");
                yamlConfiguration.addDefault("permission.groups.default.permissions", List.of("module.bank.use"));
                yamlConfiguration.addDefault("permission.groups.admin.permissions", List.of("module.bank.use"));
                yamlConfiguration.addDefault("permission.groups.admin.prefix", "§cAdmin §7");
                yamlConfiguration.addDefault("permission.groups.default.prefix", "§7");
                yamlConfiguration.addDefault("permission.groups.admin.tablist.namecolor", "§c");
                yamlConfiguration.addDefault("permission.groups.default.tablist.namecolor", "§7");
                yamlConfiguration.addDefault("permission.groups.admin.suffix", "");
                yamlConfiguration.addDefault("permission.groups.default.suffix", "");
                yamlConfiguration.addDefault("permission.groups.admin.id", 001);
                yamlConfiguration.addDefault("permission.groups.default.id", 002);
                yamlConfiguration.save(config);
            }
            yamlConfiguration.load(config);
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }
    }

    public void setPlayerGroup(String uuid, String groupName) {
        if (existsGroup(groupName)) {
            if (existsPlayer(uuid)) {
                if (!yamlConfiguration.getBoolean("mysql.use")) {
                    yamlConfiguration.set("permission.player." + uuid + ".group", groupName.toLowerCase());
                    this.saveConfig();
                }else{
                    try {
                        Statement statement = Vynl.getInstance().getSqlDriver().getConnection().createStatement();
                        statement.executeUpdate("UPDATE permission_player SET currentGroup='" + groupName.toLowerCase() + "' WHERE uuid='"+ uuid +"'");
                    } catch (SQLException sqlException) {
                        throw new RuntimeException(sqlException);
                    }
                }
            }
        }
    }

    public void createPlayer(String uuid, String groupName) {
        if (existsGroup(groupName)) {
            if (!existsPlayer(uuid)) {
                if (!yamlConfiguration.getBoolean("mysql.use")) {
                    ArrayList<String> playerPermissions = new ArrayList<>();
                    yamlConfiguration.set("permission.player." + uuid + ".group", groupName.toLowerCase());
                    yamlConfiguration.set("permission.player." + uuid + ".permissions", playerPermissions);
                    this.saveConfig();
                }else{
                    try {
                        Statement statement = Vynl.getInstance().getSqlDriver().getConnection().createStatement();
                        statement.executeUpdate("INSERT INTO permission_player (uuid, currentGroup, playerPermissions) VALUES ('" + uuid + "', '" + groupName.toLowerCase() + "', '" +  List.of("module.use") + "')");
                    } catch (SQLException sqlException) {
                        throw new RuntimeException(sqlException);
                    }
                }
            }
        }
    }

    public ArrayList<String> listPlayerPermission(String uuid) {
        if (!yamlConfiguration.getBoolean("mysql.use")) {
            ArrayList<String> playerPermissions = (ArrayList<String>) yamlConfiguration.get("permission.player." + uuid + ".permissions");
            return playerPermissions;
        }else{
            try {
                PreparedStatement preparedStatement = Vynl.getInstance().getSqlDriver().getConnection().prepareStatement("SELECT * FROM permission_player WHERE uuid='" + uuid + "'");
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                String permissionsInGroup = resultSet.getString("playerPermissions");
                permissionsInGroup = permissionsInGroup.replace("[", "").replace("]", "").replace(" ", "");
                ArrayList<String> permissionsList = new ArrayList<String>(Arrays.asList(permissionsInGroup.split(",")));
                permissionsList.add(permissionsInGroup);
                permissionsList.remove(permissionsInGroup);
                resultSet.close();
                preparedStatement.close();
                return permissionsList;
            } catch (SQLException sqlException) {
                throw new RuntimeException(sqlException);
            }
        }
    }

    public String getPlayerGroup(String uuid) {
        if (!yamlConfiguration.getBoolean("mysql.use")) {
            return yamlConfiguration.getString("permission.player." + uuid + ".group");
        }else{
            try {
                PreparedStatement preparedStatement = Vynl.getInstance().getSqlDriver().getConnection().prepareStatement("SELECT * FROM permission_player WHERE uuid='" + uuid + "'");
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                String groupID = resultSet.getString("currentGroup");
                resultSet.close();
                preparedStatement.close();
                return groupID;
            } catch (SQLException sqlException) {
                throw new RuntimeException(sqlException);
            }
        }
    }

    public String getPlayerGroupWithID(String uuid) {
        if (!yamlConfiguration.getBoolean("mysql.use")) {
            String group = yamlConfiguration.getString("permission.player." + uuid + ".group");
            String groupID = getGroupID(group);
            return groupID + group;
        }else{
            try {
                PreparedStatement preparedStatement = Vynl.getInstance().getSqlDriver().getConnection().prepareStatement("SELECT * FROM permission_groups WHERE groupName='" + getPlayerGroup(uuid) + "'");
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                String groupID = resultSet.getString("groupID");
                resultSet.close();
                preparedStatement.close();
                return groupID + getPlayerGroup(uuid);
            } catch (SQLException sqlException) {
                throw new RuntimeException(sqlException);
            }
        }
    }

    public boolean existsPlayer(String uuid) {
        if (!yamlConfiguration.getBoolean("mysql.use")) {
            return !(yamlConfiguration.get("permission.player." + uuid) == null);
        }else{
            try {
                PreparedStatement preparedStatement = Vynl.getInstance().getSqlDriver().getConnection().prepareStatement("SELECT * FROM permission_player WHERE uuid='" + uuid + "'");
                ResultSet resultSet = preparedStatement.executeQuery();
                boolean existsPlayer = resultSet.next();
                preparedStatement.close();
                return existsPlayer;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void removePlayerPermission(String uuid, String permission) {
        if (existsPlayer(uuid)) {
            if (existsPlayerPermission(uuid, permission)) {
                if (!yamlConfiguration.getBoolean("mysql.use")) {
                    ArrayList<String> playerPermissions = (ArrayList<String>) yamlConfiguration.get("permission.player." + uuid + ".permissions");
                    playerPermissions.remove(permission);
                    yamlConfiguration.set("permission.player." + uuid + ".permissions", playerPermissions);
                    this.saveConfig();
                }else{
                    try {
                        ArrayList<String> playerPermissions = listPlayerPermission(uuid);
                        playerPermissions.remove(permission);
                        Vynl.getInstance().getSqlDriver().update(Vynl.getInstance().getSqlDriver().getConnection().prepareStatement("UPDATE permission_player SET playerPermissions='" + playerPermissions + "' WHERE uuid='" + uuid + "'"));
                    } catch (Exception sqlException) {
                        throw new RuntimeException(sqlException);
                    }
                }
            }
        }
    }

    public void addPlayerPermission(String uuid, String permission) {
        if (existsPlayer(uuid)) {
            if (!existsPlayerPermission(uuid, permission)) {
                if (!yamlConfiguration.getBoolean("mysql.use")) {
                    ArrayList<String> playerPermissions = (ArrayList<String>) yamlConfiguration.get("permission.player." + uuid + ".permissions");
                    playerPermissions.add(permission);
                    yamlConfiguration.set("permission." + uuid + ".permissions", playerPermissions);
                    this.saveConfig();
                }else{
                    try {
                        ArrayList<String> playerPermissions = listPlayerPermission(uuid);
                        playerPermissions.add(permission);
                        Vynl.getInstance().getSqlDriver().update(Vynl.getInstance().getSqlDriver().getConnection().prepareStatement("UPDATE permission_player SET playerPermissions='" + playerPermissions + "' WHERE uuid='" + uuid + "'"));
                    } catch (Exception sqlException) {
                        throw new RuntimeException(sqlException);
                    }
                }
            }
        }
    }

    public boolean existsPlayerPermission(String uuid, String permission) {
        if (!yamlConfiguration.getBoolean("mysql.use")) {
            ArrayList<String> playerPermissions = (ArrayList<String>) yamlConfiguration.get("permission.player." + uuid + ".permissions");
            return playerPermissions.contains(permission);
        }else {
            try {
                PreparedStatement preparedStatement = Vynl.getInstance().getSqlDriver().getConnection().prepareStatement("SELECT * FROM permission_player WHERE uuid='" + uuid + "'");
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                String playerPermissions = String.valueOf(listPlayerPermission(uuid));
                resultSet.close();
                preparedStatement.close();
                return playerPermissions.contains(permission);
            } catch (SQLException sqlException) {
                throw new RuntimeException(sqlException);
            }
        }
    }

    public void updatePermission(Player player) {
        String uuid = player.getUniqueId().toString();
        PermissionAttachment permissionAttachment = permissions.get(uuid);
        if (permissionAttachment != null) {
            player.removeAttachment(permissionAttachment);
        }
        permissions.clear();
        if (!Vynl.getInstance().getPermissionHandler().existsGroup(Vynl.getInstance().getPermissionHandler().getPlayerGroup(player.getUniqueId().toString()))) {
            Vynl.getInstance().getPermissionHandler().setPlayerGroup(player.getUniqueId().toString(), "default");
        }
        initGroupPermissions(player);
        initPlayerPermissions(player);
    }

    public void initPlayerPermissions(Player player) {
        String uuid = player.getUniqueId().toString();
        if (existsPlayer(uuid)) {
            PermissionAttachment permissionAttachment = permissions.get(uuid);
            permissions.put(uuid, permissionAttachment);
            for (String initPlayerPermissions : listPlayerPermission(uuid)) {
                if (initPlayerPermissions.contains("*")) {
                    player.setOp(true);
                } else {
                    permissionAttachment.setPermission(initPlayerPermissions, true);
                }
            }
        }
    }

    public void initGroupPermissions(Player player) {
        String uuid = player.getUniqueId().toString();
        if (existsPlayer(uuid)) {
            String groupName = getPlayerGroup(uuid);
            PermissionAttachment permissionAttachment = player.addAttachment(Vynl.getInstance());
            permissions.put(uuid, permissionAttachment);
            for (String initGroupPermissions : listGroupPermissions(groupName.toLowerCase())) {
                if (initGroupPermissions.contains("*")) {
                    player.setOp(true);
                } else {
                    permissionAttachment.setPermission(initGroupPermissions, true);
                }
            }
        }
    }


    public void setGroupPrefix(Player player) {
        String uuid = player.getUniqueId().toString();
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        for (String groupName : listGroups()) {
            Team team = scoreboard.getTeam(getGroupID(groupName.toLowerCase()) + groupName.toLowerCase());
            if (team == null) {
                team = scoreboard.registerNewTeam(getGroupID(groupName.toLowerCase()) + groupName.toLowerCase());
            }
            team.setPrefix(getGroupPrefix(groupName.toLowerCase()));

            if (getPlayerGroupWithID(uuid).startsWith(team.getName())) {
                team.addEntry(player.getName());
                team.setColor(ChatColor.getByChar(getGroupTablistColor(groupName.toLowerCase()).replace("§", "")));
            }
        }
    }

    public String getGroupID(String groupName) {
        if (!yamlConfiguration.getBoolean("mysql.use")) {
            return yamlConfiguration.getString("permission.groups." + groupName.toLowerCase() + ".id");
        } else {
            try {
                PreparedStatement preparedStatement = Vynl.getInstance().getSqlDriver().getConnection().prepareStatement("SELECT * FROM permission_groups WHERE groupName='" + groupName.toLowerCase() + "'");
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                String groupID = resultSet.getString("groupID");
                resultSet.close();
                preparedStatement.close();
                return groupID;
            } catch (SQLException sqlException) {
                throw new RuntimeException(sqlException);
            }
        }
    }

    public String getGroupTablistColor(String groupName) {
        if (!yamlConfiguration.getBoolean("mysql.use")) {
            return yamlConfiguration.getString("permission.groups." + groupName.toLowerCase() + ".tablist.namecolor");
        } else {
            try {
                PreparedStatement preparedStatement = Vynl.getInstance().getSqlDriver().getConnection().prepareStatement("SELECT * FROM permission_groups WHERE groupName='" + groupName.toLowerCase() + "'");
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                String groupTablistColor = resultSet.getString("groupTablistColor");
                resultSet.close();
                preparedStatement.close();
                return groupTablistColor;
            } catch (SQLException sqlException) {
                throw new RuntimeException(sqlException);
            }
        }
    }

    public String getGroupPrefix(String groupName) {
        if (!yamlConfiguration.getBoolean("mysql.use")) {
            return yamlConfiguration.getString("permission.groups." + groupName.toLowerCase() + ".prefix");
        } else {
            try {
                PreparedStatement preparedStatement = Vynl.getInstance().getSqlDriver().getConnection().prepareStatement("SELECT * FROM permission_groups WHERE groupName='" + groupName.toLowerCase() + "'");
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                String groupPrefix = resultSet.getString("groupPrefix");
                resultSet.close();
                preparedStatement.close();
                return groupPrefix;
            } catch (SQLException sqlException) {
                throw new RuntimeException(sqlException);
            }
        }
    }

    public String getGroupSuffix(String groupName) {
        if (!yamlConfiguration.getBoolean("mysql.use")) {
            return yamlConfiguration.getString("permission.groups." + groupName.toLowerCase() + ".suffix");
        } else {
            try {
                PreparedStatement preparedStatement = Vynl.getInstance().getSqlDriver().getConnection().prepareStatement("SELECT * FROM permission_groups WHERE groupName='" + groupName.toLowerCase() + "'");
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                String groupSuffix = resultSet.getString("groupSuffix");
                resultSet.close();
                preparedStatement.close();
                return groupSuffix;
            } catch (SQLException sqlException) {
                throw new RuntimeException(sqlException);
            }
        }
    }

    public void setGroupSuffix(String groupName, String suffix) {
        if (existsGroup(groupName.toLowerCase())) {
            if (!yamlConfiguration.getBoolean("mysql.use")) {
                yamlConfiguration.set("permission.groups." + groupName.toLowerCase() + ".suffix", suffix);
                saveConfig();
            }else{
                try {
                    Vynl.getInstance().getSqlDriver().update(Vynl.getInstance().getSqlDriver().getConnection().prepareStatement("UPDATE permission_groups SET groupSuffix='" + suffix + "' WHERE groupName='" + groupName.toLowerCase() + "'"));
                } catch (Exception sqlException) {
                    throw new RuntimeException(sqlException);
                }
            }
        }
    }

    public void setGroupPrefix(String groupName, String prefix) {
        if (existsGroup(groupName.toLowerCase())) {
            if (!yamlConfiguration.getBoolean("mysql.use")) {
                yamlConfiguration.set("permission.groups." + groupName.toLowerCase() + ".prefix", prefix);
                saveConfig();
            }else{
                try {
                    Vynl.getInstance().getSqlDriver().update(Vynl.getInstance().getSqlDriver().getConnection().prepareStatement("UPDATE permission_groups SET groupPrefix='" + prefix + "' WHERE groupName='" + groupName.toLowerCase() + "'"));
                } catch (Exception sqlException) {
                    throw new RuntimeException(sqlException);
                }
            }
        }
    }

    public void setGroupNameColor(String groupName, String nameColor) {
        if (existsGroup(groupName.toLowerCase())) {
            if (!yamlConfiguration.getBoolean("mysql.use")) {
                yamlConfiguration.set("permission.groups." + groupName.toLowerCase() + ".tablist.namecolor", nameColor);
                saveConfig();
            }else{
                try {
                    Vynl.getInstance().getSqlDriver().update(Vynl.getInstance().getSqlDriver().getConnection().prepareStatement("UPDATE permission_groups SET groupTablistColor='" + nameColor + "' WHERE groupName='" + groupName.toLowerCase() + "'"));
                } catch (Exception sqlException) {
                    throw new RuntimeException(sqlException);
                }
            }
        }
    }

    public void setGroupID(String groupName, String groupID) {
        if (existsGroup(groupName.toLowerCase())) {
            if (!yamlConfiguration.getBoolean("mysql.use")) {
                yamlConfiguration.set("permission.groups." + groupName.toLowerCase() + ".id", groupID);
                saveConfig();
            }else{
                try {
                    Vynl.getInstance().getSqlDriver().update(Vynl.getInstance().getSqlDriver().getConnection().prepareStatement("UPDATE permission_groups SET groupID='" + groupID + "' WHERE groupName='" + groupName.toLowerCase() + "'"));
                } catch (Exception sqlException) {
                    throw new RuntimeException(sqlException);
                }
            }
        }
    }

    public void removeGroupPermission(String groupName, String permission) {
        if (existsGroup(groupName.toLowerCase())) {
            if (existsGroupPermission(groupName.toLowerCase(), permission)) {
                if (!yamlConfiguration.getBoolean("mysql.use")) {
                    ArrayList<String> groupPermission = (ArrayList<String>) yamlConfiguration.get("permission.groups." + groupName.toLowerCase() + ".permissions");
                    groupPermission.remove(permission);
                    yamlConfiguration.set("permission.groups." + groupName.toLowerCase() + ".permissions", groupPermission);
                    this.saveConfig();
                } else {
                    try {
                        ArrayList<String> groupPermission = listGroupPermissions(groupName.toLowerCase());
                        groupPermission.remove(permission);
                        Vynl.getInstance().getSqlDriver().update(Vynl.getInstance().getSqlDriver().getConnection().prepareStatement("UPDATE permission_groups SET groupPermissions='" + groupPermission + "' WHERE groupName='" + groupName.toLowerCase() + "'"));
                    } catch (SQLException sqlException) {
                        throw new RuntimeException(sqlException);
                    }
                }
            }
        }
    }

    public void addGroupPermission(String groupName, String permission) {
        if (existsGroup(groupName)) {
            if (!existsGroupPermission(groupName, permission)) {
                if (!yamlConfiguration.getBoolean("mysql.use")) {
                    ArrayList<String> groupPermission = (ArrayList<String>) yamlConfiguration.get("permission.groups." + groupName.toLowerCase() + ".permissions");
                    groupPermission.add(permission);
                    yamlConfiguration.set("permission.groups." + groupName.toLowerCase() + ".permissions", groupPermission);
                    this.saveConfig();
                } else {
                    try {
                        ArrayList<String> groupPermission = listGroupPermissions(groupName.toLowerCase());
                        groupPermission.add(permission);
                        Vynl.getInstance().getSqlDriver().update(Vynl.getInstance().getSqlDriver().getConnection().prepareStatement("UPDATE permission_groups SET groupPermissions='" + groupPermission + "' WHERE groupName='" + groupName.toLowerCase() + "'"));
                    } catch (Exception sqlException) {
                        throw new RuntimeException(sqlException);
                    }
                }
            }
        }
    }

    public boolean existsGroupPermission(String groupName, String permission) {
        if (!yamlConfiguration.getBoolean("mysql.use")) {
            ArrayList<String> groupPermissions = (ArrayList<String>) yamlConfiguration.get("permission.groups." + groupName.toLowerCase() + ".permissions");
            return groupPermissions.contains(permission);
        } else {
            try {
                PreparedStatement preparedStatement = Vynl.getInstance().getSqlDriver().getConnection().prepareStatement("SELECT * FROM permission_groups WHERE groupName='" + groupName.toLowerCase() + "'");
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                String groupPermissions = String.valueOf(listGroupPermissions(groupName.toLowerCase()));
                resultSet.close();
                preparedStatement.close();
                return groupPermissions.contains(permission);
            } catch (SQLException sqlException) {
                throw new RuntimeException(sqlException);
            }
        }
    }

    public void createGroup(String groupName) {
        if (!existsGroup(groupName)) {
            if (!yamlConfiguration.getBoolean("mysql.use")) {
                yamlConfiguration.set("permission.groups." + groupName + ".permissions", List.of("module.bank.use"));
                yamlConfiguration.set("permission.groups." + groupName + ".prefix", "§7");
                yamlConfiguration.set("permission.groups." + groupName + ".suffix", "§7");
                yamlConfiguration.set("permission.groups." + groupName + ".tablist.namecolor", "§7");
                yamlConfiguration.set("permission.groups." + groupName + ".id", 10);
                saveConfig();
                loadConfig();
            } else {
                try {
                    Statement statement = Vynl.getInstance().getSqlDriver().getConnection().createStatement();
                    statement.executeUpdate("INSERT INTO permission_groups (groupName, groupPermissions, groupID, groupPrefix, groupTablistColor, groupSuffix) VALUES ('" + groupName.toLowerCase() + "', '" + List.of("module.use") + "', '003', '§7', '§7', '§7')");
                } catch (SQLException sqlException) {
                    throw new RuntimeException(sqlException);
                }
            }
        }
    }

    public void removeGroup(String groupName) {
        if (existsGroup(groupName)) {
            if (!yamlConfiguration.getBoolean("mysql.use")) {
                yamlConfiguration.set("permission.groups." + groupName, null);
                saveConfig();
                loadConfig();
            } else {
                try {
                    Vynl.getInstance().getSqlDriver().update(Vynl.getInstance().getSqlDriver().getConnection().prepareStatement("DELETE FROM permission_groups WHERE grouoName='" + groupName.toLowerCase() + "'"));
                } catch (SQLException sqlException) {
                    throw new RuntimeException(sqlException);
                }
            }
        }
    }

    public ArrayList<String> listGroupPermissions(String groupName) {
        if (!yamlConfiguration.getBoolean("mysql.use")) {
            ArrayList<String> groupPermissions = (ArrayList<String>) yamlConfiguration.get("permission.groups." + groupName.toLowerCase() + ".permissions");
            return groupPermissions;
        } else {
            try {
                PreparedStatement preparedStatement = Vynl.getInstance().getSqlDriver().getConnection().prepareStatement("SELECT * FROM permission_groups WHERE groupName='" + groupName.toLowerCase() + "'");
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                String permissionsInGroup = resultSet.getString("groupPermissions");
                permissionsInGroup = permissionsInGroup.replace("[", "").replace("]", "").replace(" ", "");
                ArrayList<String> permissionsList = new ArrayList<String>(Arrays.asList(permissionsInGroup.split(",")));
                permissionsList.add(permissionsInGroup);
                permissionsList.remove(permissionsInGroup);
                resultSet.close();
                preparedStatement.close();
                return permissionsList;
            } catch (SQLException sqlException) {
                throw new RuntimeException(sqlException);
            }
        }
    }


    public ArrayList<String> listGroups() {
        ArrayList<String> groups = new ArrayList<>();
        if (!yamlConfiguration.getBoolean("mysql.use")) {
            for (String key : yamlConfiguration.getConfigurationSection("permission.groups").getKeys(false)) {
                groups.add(key);
            }
            return groups;
        } else {
            try {
                ResultSet resultSet = Vynl.getInstance().getSqlDriver().query("SELECT * FROM permission_groups");
                while (resultSet.next()) {
                    groups.add(resultSet.getString("groupName"));
                }
            } catch (SQLException sqlException) {
                throw new RuntimeException(sqlException);
            }
            return groups;
        }
    }

    public boolean existsGroup(String groupName) {
        if (!yamlConfiguration.getBoolean("mysql.use")) {
            return !(yamlConfiguration.getString("permission.groups." + groupName.toLowerCase()) == null);
        } else {
            try {
                PreparedStatement preparedStatement = Vynl.getInstance().getSqlDriver().getConnection().prepareStatement("SELECT * FROM permission_groups WHERE groupName='" + groupName.toLowerCase() + "'");
                ResultSet resultSet = preparedStatement.executeQuery();
                boolean existsGroup = resultSet.next();
                preparedStatement.close();
                return existsGroup;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void saveConfig() {
        try {
            yamlConfiguration.save(config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadConfig() {
        try {
            yamlConfiguration.load(config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}