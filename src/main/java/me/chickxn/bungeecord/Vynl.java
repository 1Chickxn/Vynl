package me.chickxn.bungeecord;

import lombok.Getter;
import me.chickxn.bungeecord.listener.PermissonListener;
import me.chickxn.driver.SQLDriver;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class Vynl extends Plugin{

    @Getter
    private static Vynl instance;

    @Getter
    private SQLDriver sqlDriver;

    @Override
    public void onEnable() {
        instance = this;
        getProxy().registerChannel("BungeeCord");
        PluginManager pluginManager = ProxyServer.getInstance().getPluginManager();
        pluginManager.registerListener(this, new PermissonListener());
        //sqlDriver = new SQLDriver("", "", "", "", 3306);
        this.sqlDriver.connect();
        this.sqlDriver.createTables();
        if (!this.sqlDriver.isConnected()) return;
    }
}
