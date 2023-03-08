package me.chickxn.bungeecord.listener;

import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PermissonListener implements Listener {

    @EventHandler
    public void onPlayerJoin(LoginEvent loginEvent) {
        System.out.println(loginEvent.getConnection().getUniqueId());
    }
}
