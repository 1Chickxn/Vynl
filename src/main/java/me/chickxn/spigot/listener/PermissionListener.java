package me.chickxn.spigot.listener;

import me.chickxn.spigot.Vynl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PermissionListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        if (!Vynl.getInstance().getPermissionHandler().existsPlayer(player.getUniqueId().toString())) {
            Vynl.getInstance().getPermissionHandler().createPlayer(player.getUniqueId().toString(), "default");
        }
        if (!Vynl.getInstance().getPermissionHandler().existsGroup(Vynl.getInstance().getPermissionHandler().getPlayerGroup(player.getUniqueId().toString()))) {
            Vynl.getInstance().getPermissionHandler().setPlayerGroup(player.getUniqueId().toString(), "default");
        }
        Vynl.getInstance().getPermissionHandler().initGroupPermissions(player);
        Vynl.getInstance().getPermissionHandler().initPlayerPermissions(player);
        Vynl.getInstance().getPermissionHandler().setGroupPrefix(player);
        for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
            Vynl.getInstance().getPermissionHandler().setGroupPrefix(onlinePlayers);
        }
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent playerChatEvent) {
        Player player = playerChatEvent.getPlayer();
        String uuid = player.getUniqueId().toString();
        playerChatEvent.setFormat(Vynl.getInstance().getPermissionHandler().getYamlConfiguration().getString("prefix.chat").replace("{group}", Vynl.getInstance().getPermissionHandler().getGroupPrefix(Vynl.getInstance().getPermissionHandler().getPlayerGroup(uuid))).replace("{player}", player.getName()).replace("{message}", playerChatEvent.getMessage()).replace("{suffix}", Vynl.getInstance().getPermissionHandler().getGroupSuffix(Vynl.getInstance().getPermissionHandler().getPlayerGroup(uuid))).replace("&", "§").replace("%", "%%"));
    }
}