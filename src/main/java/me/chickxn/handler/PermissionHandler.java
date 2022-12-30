package me.chickxn.handler;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class PermissionHandler {

    private File config = new File("plugins/Vynl/permissions.yml");
    private YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(config);

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
        if (!groupPermissions.contains(permission)) {
            return false;
        }else{
            return true;
        }
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

    public ArrayList<String> listGroupPermissions(String groupName) {
        ArrayList<String> groupPermissions = (ArrayList<String>) yamlConfiguration.get("permission.groups." + groupName);
        return groupPermissions;
    }

    public void listGroups() {
        for(String key : yamlConfiguration.getConfigurationSection("permission.groups").getKeys(false)){
            System.out.println(yamlConfiguration.getString("permission.groups."+key));
        }
    }

    public boolean existsGroup(String groupName) {
        if (!(yamlConfiguration.getString("permission.groups." + groupName) == null)) {
            return true;
        }else{
            return false;
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
