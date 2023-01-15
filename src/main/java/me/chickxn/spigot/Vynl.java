package me.chickxn.spigot;

import lombok.Getter;
import me.chickxn.spigot.commands.PermissionCommand;
import me.chickxn.spigot.fetcher.UUIDFetcher;
import me.chickxn.spigot.handler.PermissionHandler;
import me.chickxn.spigot.listener.PermissionListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
public class Vynl extends JavaPlugin {

    @Getter
    private static Vynl instance;
    private final String prefix = "§8(§aVynl§8) §7";
    @Getter
    private final UUIDFetcher uuidFetcher = new UUIDFetcher();
    private File file;
    @Getter
    private PermissionHandler permissionHandler;

    @Override
    public void onEnable() {
        instance = this;

        this.file = new File("plugins/Vynl/");
        if (!file.exists()) file.mkdir();
        this.permissionHandler = new PermissionHandler();


        getCommand("permission").setExecutor(new PermissionCommand());

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PermissionListener(), this);

        for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
            Vynl.getInstance().getPermissionHandler().updatePermission(onlinePlayers);
            Vynl.getInstance().getPermissionHandler().setGroupPrefix(onlinePlayers);
        }
    }

    @Override
    public void onDisable() {

    }
}