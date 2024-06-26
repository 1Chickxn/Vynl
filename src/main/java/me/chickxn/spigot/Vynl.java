package me.chickxn.spigot;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import me.chickxn.driver.SQLDriver;
import me.chickxn.spigot.checker.UpdateChecker;
import me.chickxn.spigot.commands.PermissionCommand;
import me.chickxn.spigot.fetcher.UUIDFetcher;
import me.chickxn.spigot.handler.PermissionHandler;
import me.chickxn.spigot.listener.PermissionListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.File;


@Getter
public class Vynl extends JavaPlugin implements PluginMessageListener {

    @Getter
    private static Vynl instance;

    private final String prefix = "§8(§aVynl§8) §7";

    @Getter
    private final UUIDFetcher uuidFetcher = new UUIDFetcher();

    private File file;

    @Getter
    private PermissionHandler permissionHandler;

    @Getter
    private SQLDriver sqlDriver;

    @Override
    public void onEnable() {
        instance = this;

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
        new UpdateChecker(this, 107221).getVersion(version -> {
            if (!this.getDescription().getVersion().equals(version)) {
                Bukkit.getConsoleSender().sendMessage(getPrefix() + "There is a new update §aavailable§8!");
                Bukkit.getConsoleSender().sendMessage(getPrefix() + "https://www.spigotmc.org/resources/vynl-permissionsystem.107221/");
            }
        });
        this.file = new File("plugins/Vynl/");
        if (!file.exists()) file.mkdir();
        this.permissionHandler = new PermissionHandler();
        if (permissionHandler.getYamlConfiguration().getBoolean("mysql.use")) {
            sqlDriver = new SQLDriver(permissionHandler.getYamlConfiguration().getString("mysql.hostname"), permissionHandler.getYamlConfiguration().getString("mysql.database"), permissionHandler.getYamlConfiguration().getString("mysql.username"), permissionHandler.getYamlConfiguration().getString("mysql.password"), permissionHandler.getYamlConfiguration().getInt("mysql.port"));
            this.sqlDriver.connect();
            this.sqlDriver.createTables();
            if (!this.sqlDriver.isConnected()) return;
        }
        getCommand("permission").setExecutor(new PermissionCommand());
        getCommand("permission").setTabCompleter(new PermissionCommand());
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PermissionListener(), this);
        Bukkit.getConsoleSender().sendMessage(getPrefix() + "Vynl - Permissionsystem §asuccessfully §7loaded§8!");
        Bukkit.getConsoleSender().sendMessage(getPrefix() + "Author: §a1Chickxn §8| §7Version: §a" + this.getDescription().getVersion());
        for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
            Vynl.getInstance().getPermissionHandler().updatePermission(onlinePlayers);
            Vynl.getInstance().getPermissionHandler().setGroupPrefix(onlinePlayers);
            sendBungeeMessage("BungeeCord", "daqrkIPvMxYz7O7triETahoKj1mD6BAn");
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(getPrefix() + "Vynl - Permissionsystem §asuccessfully §7disabled§8!");
        Bukkit.getConsoleSender().sendMessage(getPrefix() + "Author: §a1Chickxn §8| §7Version: §a" + this.getDescription().getVersion());
    }

    @Override
    public void onPluginMessageReceived(String string, Player player, byte[] bytes) {

    }

    public void sendBungeeMessage(String channel, String message) {
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
        byteArrayDataOutput.writeUTF(message);
        Bukkit.getServer().sendPluginMessage(this, channel, byteArrayDataOutput.toByteArray());
    }
}
