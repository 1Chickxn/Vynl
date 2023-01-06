package me.chickxn.handler;

import lombok.Getter;
import me.chickxn.Vynl;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                yamlConfiguration.set("permission.player." + uuid + ".group", groupName);
                this.saveConfig();
            }
        }
    }

    public void createPlayer(String uuid, String groupName) {
        if (existsGroup(groupName)) {
            if (!existsPlayer(uuid)) {
                ArrayList<String> playerPermissions = new ArrayList<>();
                yamlConfiguration.set("permission.player." + uuid + ".group", groupName);
                yamlConfiguration.set("permission.player." + uuid + ".permissions", playerPermissions);
                this.saveConfig();
            }
        }
    }

    public ArrayList<String> listPlayerPermission(String uuid) {
        ArrayList<String> playerPermissions = (ArrayList<String>) yamlConfiguration.get("permission.player." + uuid + ".permissions");
        return playerPermissions;
    }

    public String getPlayerGroup(String uuid) {
        return yamlConfiguration.getString("permission.player." + uuid + ".group");
    }

    public String getPlayerGroupWithID(String uuid) {
        String group = yamlConfiguration.getString("permission.player." + uuid + ".group");
        String groupID = getGroupID(group);
        return groupID + group;
    }

    public boolean existsPlayer(String uuid) {
        return !(yamlConfiguration.get("permission.player." + uuid) == null);
    }

    public void updatePermission(Player player) {
        String uuid = player.getUniqueId().toString();
        PermissionAttachment permissionAttachment = permissions.get(uuid);
        if (permissionAttachment != null) {
            player.removeAttachment(permissionAttachment);
        }
        permissions.clear();
        player.setOp(false);
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
            for (String initGroupPermissions : listGroupPermissions(groupName)) {
                if (initGroupPermissions.contains("*")) {
                    player.setOp(true);
                } else {
                    permissionAttachment.setPermission(initGroupPermissions, true);
                }
            }
        }
    }

    public void removePlayerPermission(String uuid, String permission) {
        if (existsPlayer(uuid)) {
            if (existsPlayerPermission(uuid, permission)) {
                ArrayList<String> playerPermissions = (ArrayList<String>) yamlConfiguration.get("permission.player." + uuid + ".permissions");
                playerPermissions.remove(permission);
                yamlConfiguration.set("permission.player." + uuid + ".permissions", playerPermissions);
                this.saveConfig();
            }
        }
    }

    public void addPlayerPermission(String uuid, String permission) {
        if (existsPlayer(uuid)) {
            if (!existsPlayerPermission(uuid, permission)) {
                ArrayList<String> playerPermissions = (ArrayList<String>) yamlConfiguration.get("permission.player." + uuid + ".permissions");
                playerPermissions.add(permission);
                yamlConfiguration.set("permission." + uuid + ".permissions", playerPermissions);
                this.saveConfig();
            }
        }
    }

    public boolean existsPlayerPermission(String uuid, String permission) {
        ArrayList<String> playerPermissions = (ArrayList<String>) yamlConfiguration.get("permission.player." + uuid + ".permissions");
        return playerPermissions.contains(permission);
    }

    public void setGroupPrefix(Player player) {
        String uuid = player.getUniqueId().toString();
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        for (String groupName : listGroups()) {
            Team team = scoreboard.getTeam(getGroupID(groupName) + groupName);
            if (team == null) {
                scoreboard.registerNewTeam(getGroupID(groupName) + groupName);
            }
            team.setPrefix(getGroupPrefix(groupName));

            if (getPlayerGroupWithID(uuid).contains(team.getName())) {
                team.addEntry(player.getName());
                team.setColor(ChatColor.getByChar(getGroupTablistColor(groupName).replace("§", "")));
            }
        }
    }

    public String getGroupID(String groupName) {
        return yamlConfiguration.getString("permission.groups." + groupName + ".id");
    }

    public String getGroupTablistColor(String groupName) {
        return yamlConfiguration.getString("permission.groups." + groupName + ".tablist.namecolor");
    }

    public String getGroupPrefix(String groupName) {
        return yamlConfiguration.getString("permission.groups." + groupName + ".prefix");
    }

    public String getGroupSuffix(String groupName) {
        return yamlConfiguration.getString("permission.groups." + groupName + ".suffix");
    }

    public void removeGroupPermission(String groupName, String permission) {
        if (existsGroup(groupName)) {
            if (existsGroupPermission(groupName, permission)) {
                ArrayList<String> groupPermission = (ArrayList<String>) yamlConfiguration.get("permission.groups." + groupName + ".permissions");
                groupPermission.remove(permission);
                yamlConfiguration.set("permission.groups." + groupName + ".permissions", groupPermission);
                this.saveConfig();
            }
        }
    }

    public void addGroupPermission(String groupName, String permission) {
        if (existsGroup(groupName)) {
            if (!existsGroupPermission(groupName, permission)) {
                ArrayList<String> groupPermission = (ArrayList<String>) yamlConfiguration.get("permission.groups." + groupName + ".permissions");
                groupPermission.add(permission);
                yamlConfiguration.set("permission.groups." + groupName + ".permissions", groupPermission);
                this.saveConfig();
            }
        }
    }

    public boolean existsGroupPermission(String groupName, String permission) {
        ArrayList<String> groupPermissions = (ArrayList<String>) yamlConfiguration.get("permission.groups." + groupName + ".permissions");
        return groupPermissions.contains(permission);
    }

    public void createGroup(String groupName) {
        if (!existsGroup(groupName)) {
            yamlConfiguration.set("permission.groups." + groupName + ".permissions", List.of("module.bank.use"));
            yamlConfiguration.set("permission.groups." + groupName + ".prefix", "§7");
            yamlConfiguration.set("permission.groups." + groupName + ".suffix", "§7");
            yamlConfiguration.set("permission.groups." + groupName + ".tablist.namecolor", "§7");
            yamlConfiguration.set("permission.groups." + groupName + ".id", 10);
            saveConfig();
            loadConfig();
        }
    }

    public void removeGroup(String groupName) {
        if (existsGroup(groupName)) {
            yamlConfiguration.set("permission.groups." + groupName, null);
            saveConfig();
            loadConfig();
        }
    }

    public void listPlayers() {

    }

    public ArrayList<String> listGroupPermissions(String groupName) {
        ArrayList<String> groupPermissions = (ArrayList<String>) yamlConfiguration.get("permission.groups." + groupName + ".permissions");
        return groupPermissions;
    }

    public ArrayList<String> listGroups() {
        ArrayList<String> groups = new ArrayList<>();
        for (String key : yamlConfiguration.getConfigurationSection("permission.groups").getKeys(false)) {
            groups.add(key);
        }
        return groups;
    }

    public boolean existsGroup(String groupName) {
        return !(yamlConfiguration.getString("permission.groups." + groupName) == null);
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
