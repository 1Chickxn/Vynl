package me.chickxn.bungeecord;

import me.chickxn.bungeecord.listener.PermissonListener;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class Vynl extends Plugin {

    @Override
    public void onEnable() {
        PluginManager pluginManager = ProxyServer.getInstance().getPluginManager();
        pluginManager.registerListener(this, new PermissonListener());
    }

    @Override
    public void onDisable() {

    }
}
