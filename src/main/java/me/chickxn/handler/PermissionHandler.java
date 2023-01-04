package me.chickxn.handler;

import me.chickxn.Vynl;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PermissionHandler {
    private final File config = new File("plugins/Vynl/permissions.yml");
    private final YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(config);

    private final Map<String, PermissionAttachment> permissions = new HashMap<>();

    public PermissionHandler() {
        try {
            if (!config.exists()) {
                config.createNewFile();
                yamlConfiguration.options().header("Permissions");
                yamlConfiguration.options().copyDefaults(true);
                yamlConfiguration.addDefault("permission.groups.default", List.of("module.bank.use"));
                yamlConfiguration.addDefault("permission.groups.admin", List.of("module.bank.use"));
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
        } else {
            createPlayer(uuid, "default");
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
        } else {
            createPlayer(uuid, "default");
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


    public void removeGroupPermission(String groupName, String permission) {
        if (existsGroup(groupName)) {
            if (existsGroupPermission(groupName, permission)) {
                ArrayList<String> groupPermission = (ArrayList<String>) yamlConfiguration.get("permission.groups." + groupName);
                groupPermission.remove(permission);
                yamlConfiguration.set("permission.groups." + groupName, groupPermission);
                this.saveConfig();
            }
        }
    }

    public void addGroupPermission(String groupName, String permission) {
        if (existsGroup(groupName)) {
            if (!existsGroupPermission(groupName, permission)) {
                ArrayList<String> groupPermission = (ArrayList<String>) yamlConfiguration.get("permission.groups." + groupName);
                groupPermission.add(permission);
                yamlConfiguration.set("permission.groups." + groupName, groupPermission);
                this.saveConfig();
            }
        }
    }

    public boolean existsGroupPermission(String groupName, String permission) {
        ArrayList<String> groupPermissions = (ArrayList<String>) yamlConfiguration.get("permission.groups." + groupName);
        return groupPermissions.contains(permission);
    }

    public void createGroup(String groupName) {
        if (!existsGroup(groupName)) {
            yamlConfiguration.set("permission.groups." + groupName, List.of("module.bank.use"));
            saveConfig();
        }
    }

    public void removeGroup(String groupName) {
        if (existsGroup(groupName)) {
            yamlConfiguration.set("permission.groups." + groupName, null);
            saveConfig();
        }
    }

    public void listPlayers() {

    }

    public ArrayList<String> listGroupPermissions(String groupName) {
        ArrayList<String> groupPermissions = (ArrayList<String>) yamlConfiguration.get("permission.groups." + groupName);
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
