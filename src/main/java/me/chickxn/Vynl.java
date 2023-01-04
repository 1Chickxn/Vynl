package me.chickxn;

import lombok.Getter;
import me.chickxn.commands.PermissionCommand;
import me.chickxn.fetcher.UUIDFetcher;
import me.chickxn.handler.PermissionHandler;
import me.chickxn.listener.PermissionListener;
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
    private File file;
    @Getter
    private PermissionHandler permissionHandler;

    @Getter
    private UUIDFetcher uuidFetcher = new UUIDFetcher();

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
        }
    }

    @Override
    public void onDisable() {

    }
}
