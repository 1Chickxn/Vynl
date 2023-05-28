package me.chickxn.bungeecord;

import lombok.Getter;
import me.chickxn.bungeecord.listener.PermissonListener;
import me.chickxn.driver.SQLDriver;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.event.EventHandler;


public class Vynl extends Plugin{

    @Getter
    private static Vynl instance;

    @Getter
    private SQLDriver sqlDriver;

    @Override
    public void onEnable() {
        instance = this;
        getProxy().registerChannel("BungeeCord"); // Kanal registrieren
        PluginManager pluginManager = ProxyServer.getInstance().getPluginManager();
        pluginManager.registerListener(this, new PermissonListener());
        sqlDriver = new SQLDriver("plesk01.dashserv.io", "vynl", "vynl", "x993_Uld6", 3306);
        this.sqlDriver.connect();
        this.sqlDriver.createTables();
        if (!this.sqlDriver.isConnected()) return;
    }

    @Override
    public void onDisable() {

    }

}
