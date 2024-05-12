package me.chickxn.spigot.listener;

import me.chickxn.spigot.Vynl;
import me.chickxn.spigot.checker.UpdateChecker;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
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
        new UpdateChecker(Vynl.getInstance(), 107221).getVersion(version -> {
            if (Vynl.getInstance().getDescription().getVersion().equals(version)) {
            } else {
                if (player.isOp()) {
                    player.sendMessage(Vynl.getInstance().getPrefix() + "There is a new update §aavailable§8!");
                    TextComponent textComponent = new TextComponent(Vynl.getInstance().getPrefix() + "hover above this message to §aupdate§8!");
                    textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click me to open the §aurl")));
                    textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/vynl-permissionsystem.107221/"));
                    player.spigot().sendMessage(textComponent);
                }
            }
        });
        if (!Vynl.getInstance().getPermissionHandler().existsPlayer(player.getUniqueId().toString())) {
            Vynl.getInstance().getPermissionHandler().createPlayer(player.getUniqueId().toString(), "default");
            Vynl.getInstance().sendBungeeMessage("BungeeCord", "permission update");
        }
        if (!Vynl.getInstance().getPermissionHandler().existsGroup(Vynl.getInstance().getPermissionHandler().getPlayerGroup(player.getUniqueId().toString()))) {
            Vynl.getInstance().getPermissionHandler().setPlayerGroup(player.getUniqueId().toString(), "default");
            Vynl.getInstance().sendBungeeMessage("BungeeCord", "permission update");
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
        playerChatEvent.setFormat(Vynl.getInstance().getPermissionHandler().getYamlConfiguration().getString("prefix.chat").replace("{group}", Vynl.getInstance().getPermissionHandler().getGroupPrefix(Vynl.getInstance().getPermissionHandler().getPlayerGroup(uuid))).replace("{player}", Vynl.getInstance().getPermissionHandler().getGroupTablistColor(Vynl.getInstance().getPermissionHandler().getPlayerGroup(uuid)) + player.getName()).replace("{message}", playerChatEvent.getMessage()).replace("{suffix}", Vynl.getInstance().getPermissionHandler().getGroupSuffix(Vynl.getInstance().getPermissionHandler().getPlayerGroup(uuid))).replace("&", "§").replace("%", "%%"));
    }
}