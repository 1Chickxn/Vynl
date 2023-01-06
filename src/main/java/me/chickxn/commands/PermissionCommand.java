package me.chickxn.commands;

import me.chickxn.Vynl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class PermissionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player player) {
            if (player.hasPermission("module.permission.use")) {
                if (args.length == 0) {
                    sendHelp(player);
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("group")) {
                        player.sendMessage(Vynl.getInstance().getPrefix() + "all groups");
                        String groups = String.valueOf(Vynl.getInstance().getPermissionHandler().listGroups());
                        player.sendMessage(Vynl.getInstance().getPrefix() + "§a" + groups.replace("[", "").replace("]", "").replace(",", "§8,§a"));
                    }
                } else if (args.length == 2) {
                    String groupName = args[1];
                    String playerName = args[1];
                    if (args[0].equalsIgnoreCase("group")) {
                        if (groupName != null) {
                            if (Vynl.getInstance().getPermissionHandler().existsGroup(groupName)) {
                                String groupPermissions = String.valueOf(Vynl.getInstance().getPermissionHandler().listGroupPermissions(groupName));
                                player.sendMessage(Vynl.getInstance().getPrefix() + "Info zur Gruppe: §a" + groupName);
                                player.sendMessage(Vynl.getInstance().getPrefix() + "Permissions: §a" + groupPermissions.replace("[", "").replace("]", "").replace(",", "§8,§a"));
                            } else {
                                player.sendMessage(Vynl.getInstance().getPrefix() + "Die Gruppe §8(§a" + groupName + "§8) §7exestiert nicht§8!");
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("player")) {
                        String uuid = Vynl.getInstance().getUuidFetcher().getUUID(playerName);
                        if (Vynl.getInstance().getPermissionHandler().existsPlayer(uuid)) {
                            String playerPermissions = String.valueOf(Vynl.getInstance().getPermissionHandler().listPlayerPermission(uuid));
                            player.sendMessage(Vynl.getInstance().getPrefix() + "Info zum Spieler: §a" + playerName);
                            player.sendMessage(Vynl.getInstance().getPrefix() + "Gruppe: §a" + Vynl.getInstance().getPermissionHandler().getPlayerGroup(uuid));
                            player.sendMessage(Vynl.getInstance().getPrefix() + "Permissions: §a" + playerPermissions.replace("[", "").replace("]", "").replace(",", "§8,§a"));
                        } else {
                            player.sendMessage(Vynl.getInstance().getPrefix() + "Der Spieler §8(§a" + playerName + "§8) §7exestiert nicht§8!");
                        }
                    }
                } else if (args.length == 3) {
                    String groupName = args[1];
                    if (args[2].equalsIgnoreCase("create")) {
                        if (!Vynl.getInstance().getPermissionHandler().existsGroup(groupName)) {
                            Vynl.getInstance().getPermissionHandler().createGroup(groupName);
                            player.sendMessage(Vynl.getInstance().getPrefix() + "Die Gruppe §8(§a" + groupName + "§8) §7wurde erstellt§8!");
                        } else {
                            player.sendMessage(Vynl.getInstance().getPrefix() + "Die Gruppe §8(§a" + groupName + "§8) §7exestiert bereits§8!");
                        }
                    } else if (args[2].equalsIgnoreCase("delete")) {
                        if (Vynl.getInstance().getPermissionHandler().existsGroup(groupName)) {
                            if (!groupName.equalsIgnoreCase("default")) {
                                Vynl.getInstance().getPermissionHandler().removeGroup(groupName);
                                player.sendMessage(Vynl.getInstance().getPrefix() + "Die Gruppe §8(§a" + groupName + "§8) §7wurde entfernt§8!");
                            } else {
                                player.sendMessage(Vynl.getInstance().getPrefix() + "Die Gruppe §8(§a" + groupName + "§8) §7kann nicht gelöscht werden§8!");
                            }
                        } else {
                            player.sendMessage(Vynl.getInstance().getPrefix() + "Die Gruppe §8(§a" + groupName + "§8) §7exestiert nicht§8!");
                        }
                    } else {
                        sendHelp(player);
                    }
                } else if (args.length == 4) {
                    String groupName = args[1];
                    String playerName = args[1];
                    String permissions = args[3];
                    String newGroupName = args[3];
                    String uuid = Vynl.getInstance().getUuidFetcher().getUUID(playerName);
                    if (args[0].equalsIgnoreCase("group")) {
                        if (args[2].equalsIgnoreCase("add")) {
                            if (!Vynl.getInstance().getPermissionHandler().existsGroupPermission(groupName, permissions)) {
                                Vynl.getInstance().getPermissionHandler().addGroupPermission(groupName, permissions);
                                player.sendMessage(Vynl.getInstance().getPrefix() + "Die Permission §8(§a" + permissions + "§8) §7wurde in der Gruppe §8(§a" + groupName + "§8) §7hinzugefügt§8!");
                                for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                                    Vynl.getInstance().getPermissionHandler().updatePermission(onlinePlayers);
                                }
                            } else {
                                player.sendMessage(Vynl.getInstance().getPrefix() + "Die Permission §8(§a" + permissions + "§8) §7ist bereits in der Gruppe §8(§a" + groupName + "§8)§8!");
                            }
                        } else if (args[2].equalsIgnoreCase("remove")) {
                            if (Vynl.getInstance().getPermissionHandler().existsGroupPermission(groupName, permissions)) {
                                Vynl.getInstance().getPermissionHandler().removeGroupPermission(groupName, permissions);
                                player.sendMessage(Vynl.getInstance().getPrefix() + "Die Permission §8(§a" + permissions + "§8) §7wurde in der Gruppe §8(§a" + groupName + "§8) §7entfernt§8!");
                                for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                                    Vynl.getInstance().getPermissionHandler().updatePermission(onlinePlayers);
                                }
                            } else {
                                player.sendMessage(Vynl.getInstance().getPrefix() + "Die Permission §8(§a" + permissions + "§8) §7exestiert in der Gruppe §8(§a" + groupName + "§8) §7nicht§8!");
                            }
                        } else {
                            sendHelp(player);
                        }
                    } else if (args[0].equalsIgnoreCase("player")) {
                        if (args[2].equalsIgnoreCase("add")) {
                            if (Vynl.getInstance().getPermissionHandler().existsPlayer(uuid)) {
                                if (!Vynl.getInstance().getPermissionHandler().existsPlayerPermission(uuid, permissions)) {
                                    Vynl.getInstance().getPermissionHandler().addPlayerPermission(uuid, permissions);
                                    player.sendMessage(Vynl.getInstance().getPrefix() + "Der Spieler §8(§a" + playerName + "§8) §7hat nun die Permission §8(§a" + permissions + "§8)");
                                    for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                                        Vynl.getInstance().getPermissionHandler().updatePermission(onlinePlayers);
                                    }
                                } else {
                                    player.sendMessage(Vynl.getInstance().getPrefix() + "Der Spieler §8(§a" + playerName + "§8) §7hat die Permission §8(§a" + permissions + "§8) §7bereits§8!");
                                }
                            } else {
                                player.sendMessage(Vynl.getInstance().getPrefix() + "Der Spieler §8(§a" + playerName + "§8) §7exestiert nicht§8!");
                            }
                        } else if (args[2].equalsIgnoreCase("remove")) {
                            if (Vynl.getInstance().getPermissionHandler().existsPlayer(uuid)) {
                                if (Vynl.getInstance().getPermissionHandler().existsPlayerPermission(uuid, permissions)) {
                                    Vynl.getInstance().getPermissionHandler().removePlayerPermission(uuid, permissions);
                                    player.sendMessage(Vynl.getInstance().getPrefix() + "Der Spieler §8(§a" + playerName + "§8) §7hat nun nicht mehr die Permission §8(§a" + permissions + "§8)");
                                    for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                                        Vynl.getInstance().getPermissionHandler().updatePermission(onlinePlayers);
                                    }
                                } else {
                                    player.sendMessage(Vynl.getInstance().getPrefix() + "Der Spieler §8(§a" + playerName + "§8) §7hat die Permission §8(§a" + permissions + "§8) §7nicht§8!");
                                }
                            } else {
                                player.sendMessage(Vynl.getInstance().getPrefix() + "Der Spieler §8(§a" + playerName + "§8) §7exestiert nicht§8!");
                            }
                        } else if (args[2].equalsIgnoreCase("set")) {
                            if (Vynl.getInstance().getPermissionHandler().existsPlayer(uuid)) {
                                if (Vynl.getInstance().getPermissionHandler().existsGroup(newGroupName)) {
                                    if (!Vynl.getInstance().getPermissionHandler().getPlayerGroup(uuid).contains(newGroupName)) {
                                        Vynl.getInstance().getPermissionHandler().setPlayerGroup(uuid, newGroupName);
                                        player.sendMessage(Vynl.getInstance().getPrefix() + "Der Spieler §8(§a" + playerName + "§8) §7ist nun in der Gruppe §8(§a" + newGroupName + "§8) §8!");
                                        for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                                            Vynl.getInstance().getPermissionHandler().updatePermission(onlinePlayers);
                                            Vynl.getInstance().getPermissionHandler().setGroupPrefix(onlinePlayers);
                                        }
                                    } else {
                                        player.sendMessage(Vynl.getInstance().getPrefix() + "Der Spieler §8(§a" + playerName + "§8) §7ist bereits in der Gruppe §8(§a" + newGroupName + "§8) §8!");
                                    }
                                } else {
                                    player.sendMessage(Vynl.getInstance().getPrefix() + "Die Gruppe §8(§a" + newGroupName + "§8) §7exestiert nicht!");
                                }
                            } else {
                                player.sendMessage(Vynl.getInstance().getPrefix() + "Der Spieler §8(§a" + playerName + "§8) §7exestiert nicht§8!");
                            }
                        } else {
                            sendHelp(player);
                        }
                    }
                }
            } else {
                player.sendMessage(Vynl.getInstance().getPrefix() + "Dazu hast du keine §cRechte§8!");
            }
        }
        return false;
    }

    public void sendHelp(Player player) {
        player.sendMessage(Vynl.getInstance().getPrefix() + "Permissions");
        player.sendMessage(Vynl.getInstance().getPrefix() + "/permission group");
        player.sendMessage(Vynl.getInstance().getPrefix() + "/permission group §8(§aGROUP§8) §7create");
        player.sendMessage(Vynl.getInstance().getPrefix() + "/permission group §8(§aGROUP§8) §7delete");
        player.sendMessage(Vynl.getInstance().getPrefix() + "/permission group §8(§aGROUP§8) §7remove §8(§aPERMISSION§8)");
        player.sendMessage(Vynl.getInstance().getPrefix() + "/permission group §8(§aGROUP§8) §7add §8(§aPERMISSION§8)");
        player.sendMessage(Vynl.getInstance().getPrefix() + "/permission player §8(§aPLAYER§8)");
        player.sendMessage(Vynl.getInstance().getPrefix() + "/permission player §8(§aPLAYER§8) §7set §8(§aGROUP§8)");
        player.sendMessage(Vynl.getInstance().getPrefix() + "/permission player §8(§aPLAYER§8) §7add §8(§aPERMISSION§8)");
        player.sendMessage(Vynl.getInstance().getPrefix() + "/permission player §8(§aPLAYER§8) §7remove §8(§aPERMISSION§8)");
    }
}
