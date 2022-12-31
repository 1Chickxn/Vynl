package me.chickxn;

import lombok.Getter;
import me.chickxn.commands.PermissionCommand;
import me.chickxn.handler.PermissionHandler;
import me.chickxn.listener.PlayerJoinListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
public class Vynl extends JavaPlugin {

    @Getter
    private static Vynl instance;


    private File file;

    @Getter
    private PermissionHandler permissionHandler;

    private String prefix = "§8(§aVynl§8) §7";

    @Override
    public void onEnable() {
        instance = this;


        this.file = new File("plugins/Vynl/");
        if(!file.exists()) file.mkdir();

        this.permissionHandler = new PermissionHandler();
        permissionHandler.listGroups();
        getCommand("permission").setExecutor(new PermissionCommand());
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(), this);
    }

    @Override
    public void onDisable() {

    }
}
