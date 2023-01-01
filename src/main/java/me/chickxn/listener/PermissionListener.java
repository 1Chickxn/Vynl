package me.chickxn.listener;

import me.chickxn.Vynl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PermissionListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        if (!Vynl.getInstance().getPermissionHandler().existsPlayer(player.getUniqueId().toString())) {
            Vynl.getInstance().getPermissionHandler().createPlayer(player.getUniqueId().toString(), "default");
        }
        Vynl.getInstance().getPermissionHandler().initGroupPermissions(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        Player player = playerQuitEvent.getPlayer();
        player.setOp(false);
    }
}
