package me.chickxn.spigot.commands;

import me.chickxn.spigot.Vynl;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermissionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
            if (commandSender.hasPermission("module.permission.use")) {
                if (args.length == 0) {
                    sendHelp(commandSender);
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("group")) {
                        commandSender.sendMessage(Vynl.getInstance().getPrefix() + "all groups");
                        String groups = String.valueOf(Vynl.getInstance().getPermissionHandler().listGroups());
                        commandSender.sendMessage(Vynl.getInstance().getPrefix() + "§a" + groups.replace("[", "").replace("]", "").replace(",", "§8,§a"));
                    }
                } else if (args.length == 2) {
                    String groupName = args[1];
                    String playerName = args[1];
                    if (args[0].equalsIgnoreCase("group")) {
                        if (groupName != null) {
                            if (Vynl.getInstance().getPermissionHandler().existsGroup(groupName)) {
                                String groupPermissions = String.valueOf(Vynl.getInstance().getPermissionHandler().listGroupPermissions(groupName));
                                commandSender.sendMessage(Vynl.getInstance().getPrefix() + "Group: §a" + groupName);
                                commandSender.sendMessage(Vynl.getInstance().getPrefix() + "Permissions: §a" + groupPermissions.replace("[", "").replace("]", "").replace(",", "§8,§a"));
                            } else {
                                commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The group §8(§a" + groupName + "§8) §7does not exist§8!");
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("player")) {
                        String uuid = Vynl.getInstance().getUuidFetcher().getUUID(playerName);
                        if (Vynl.getInstance().getPermissionHandler().existsPlayer(uuid)) {
                            String playerPermissions = String.valueOf(Vynl.getInstance().getPermissionHandler().listPlayerPermission(uuid));
                            commandSender.sendMessage(Vynl.getInstance().getPrefix() + "Player: §a" + playerName);
                            commandSender.sendMessage(Vynl.getInstance().getPrefix() + "Group: §a" + Vynl.getInstance().getPermissionHandler().getPlayerGroup(uuid));
                            commandSender.sendMessage(Vynl.getInstance().getPrefix() + "Permissions: §a" + playerPermissions.replace("[", "").replace("]", "").replace(",", "§8,§a"));
                        } else {
                            commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The player §8(§a" + playerName + "§8) §7does not exist§8!");
                        }
                    }
                } else if (args.length == 3) {
                    String groupName = args[1];
                    if (args[2].equalsIgnoreCase("create")) {
                        if (!Vynl.getInstance().getPermissionHandler().existsGroup(groupName)) {
                            Vynl.getInstance().getPermissionHandler().createGroup(groupName);
                            commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The group §8(§a" + groupName + "§8) §7has been created§8!");
                        } else {
                            commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The group §8(§a" + groupName + "§8) §7already exists§8!");
                        }
                    } else if (args[2].equalsIgnoreCase("delete")) {
                        if (Vynl.getInstance().getPermissionHandler().existsGroup(groupName)) {
                            if (!groupName.equalsIgnoreCase("default")) {
                                Vynl.getInstance().getPermissionHandler().removeGroup(groupName);
                                commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The group §8(§a" + groupName + "§8) §7has been removed §8!");
                            } else {
                                commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The group §8(§a" + groupName + "§8) §7cannot be deleted§8!");
                            }
                        } else {
                            commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The group §8(§a" + groupName + "§8) §7does not exist§8!");
                        }
                    } else {
                        sendHelp(commandSender);
                    }
                } else if (args.length == 4) {
                    String groupName = args[1];
                    String playerName = args[1];
                    String permissions = args[3];
                    String newGroupName = args[3];
                    String uuid = Vynl.getInstance().getUuidFetcher().getUUID(playerName);
                    if (args[0].equalsIgnoreCase("group")) {
                        if (args[2].equalsIgnoreCase("add")) {
                            if (Vynl.getInstance().getPermissionHandler().existsGroup(groupName)) {
                                if (!Vynl.getInstance().getPermissionHandler().existsGroupPermission(groupName, permissions)) {
                                    Vynl.getInstance().getPermissionHandler().addGroupPermission(groupName, permissions);
                                    commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The permission §8(§a" + permissions + "§8) §7has been added to the group §8(§a" + groupName + "§8)§8!");
                                    for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                                        Vynl.getInstance().getPermissionHandler().updatePermission(onlinePlayers);
                                    }
                                } else {
                                    commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The permission §8(§a" + permissions + "§8) §7is already in the group §8(§a" + groupName + "§8)§8!");
                                }
                            }else{
                                commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The group §8(§a" + groupName + "§8) §7does not exist§8!");
                            }
                        }else if (args[2].equalsIgnoreCase("setnamecolor")) {
                            if (Vynl.getInstance().getPermissionHandler().existsGroup(groupName)) {
                                String nameColor = args[3];
                                Vynl.getInstance().getPermissionHandler().setGroupNameColor(groupName, nameColor.replace("&", "§"));
                                for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                                    Vynl.getInstance().getPermissionHandler().setGroupPrefix(onlinePlayers);
                                }
                                commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The group §8(§a" + groupName + "§8) §7has a new suffix §8(§a" + nameColor.replace("&", "§") + "§8)§8!");

                            } else {
                                commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The group §8(§a" + groupName + "§8) §7does not exist§8!");
                            }
                        }else if (args[2].equalsIgnoreCase("setid")) {
                            if (Vynl.getInstance().getPermissionHandler().existsGroup(groupName)) {
                                String groupID = args[3];
                                Vynl.getInstance().getPermissionHandler().setGroupID(groupName, groupID);
                                for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                                    Vynl.getInstance().getPermissionHandler().setGroupPrefix(onlinePlayers);
                                }
                                commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The group §8(§a" + groupName + "§8) §7has a new id §8(§a" + groupID + "§8)§8!");

                            } else {
                                commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The group §8(§a" + groupName + "§8) §7does not exist§8!");
                            }
                        }else if (args[2].equalsIgnoreCase("remove")) {
                            if (Vynl.getInstance().getPermissionHandler().existsGroup(groupName)) {
                                if (Vynl.getInstance().getPermissionHandler().existsGroupPermission(groupName, permissions)) {
                                    Vynl.getInstance().getPermissionHandler().removeGroupPermission(groupName, permissions);
                                    commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The permission §8(§a" + permissions + "§8) §7has been removed from the group §8(§a" + groupName + "§8)§8!");
                                    for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                                        Vynl.getInstance().getPermissionHandler().updatePermission(onlinePlayers);
                                    }
                                } else {
                                    commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The permission §8(§a" + permissions + "§8) §7does not exist in the group §8(§a" + groupName + "§8)§8!");
                                }
                            }else{
                                commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The group §8(§a" + groupName + "§8) §7does not exist§8!");
                            }
                        }else{
                            sendHelp(commandSender);
                        }
                    } else if (args[0].equalsIgnoreCase("player") || args[0].equalsIgnoreCase("user")) {
                        if (args[2].equalsIgnoreCase("add")) {
                            if (Vynl.getInstance().getPermissionHandler().existsPlayer(uuid)) {
                                if (!Vynl.getInstance().getPermissionHandler().existsPlayerPermission(uuid, permissions)) {
                                    Vynl.getInstance().getPermissionHandler().addPlayerPermission(uuid, permissions);
                                    commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The player §8(§a" + playerName + "§8) §7has now the permission §8(§a" + permissions + "§8)");
                                    for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                                        Vynl.getInstance().getPermissionHandler().updatePermission(onlinePlayers);
                                    }
                                } else {
                                    commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The player §8(§a" + playerName + "§8) §7already has the permission §8(§a" + permissions + "§8)§8!");
                                }
                            } else {
                                commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The player §8(§a" + playerName + "§8) §7does not exist§8!");
                            }
                        } else if (args[2].equalsIgnoreCase("remove")) {
                            if (Vynl.getInstance().getPermissionHandler().existsPlayer(uuid)) {
                                if (Vynl.getInstance().getPermissionHandler().existsPlayerPermission(uuid, permissions)) {
                                    Vynl.getInstance().getPermissionHandler().removePlayerPermission(uuid, permissions);
                                    commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The player §8(§a" + playerName + "§8) §7has no longer the permission §8(§a" + permissions + "§8)");
                                    for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                                        Vynl.getInstance().getPermissionHandler().updatePermission(onlinePlayers);
                                    }
                                } else {
                                    commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The player §8(§a" + playerName + "§8) §7does not have the permission §8(§a" + permissions + "§8)§8!");
                                }
                            } else {
                                commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The player §8(§a" + playerName + "§8) §7does not exist§8!");
                            }
                        } else if (args[2].equalsIgnoreCase("set")) {
                            if (Vynl.getInstance().getPermissionHandler().existsPlayer(uuid)) {
                                if (Vynl.getInstance().getPermissionHandler().existsGroup(newGroupName)) {
                                    if (!Vynl.getInstance().getPermissionHandler().getPlayerGroup(uuid).contains(newGroupName)) {
                                        Vynl.getInstance().getPermissionHandler().setPlayerGroup(uuid, newGroupName);
                                        commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The player §8(§a" + playerName + "§8) §7is now in the group §8(§a" + newGroupName + "§8)§8!");
                                        for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                                            Vynl.getInstance().getPermissionHandler().updatePermission(onlinePlayers);
                                            Vynl.getInstance().getPermissionHandler().setGroupPrefix(onlinePlayers);
                                        }
                                    } else {
                                        commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The player §8(§a" + playerName + "§8) §7is already in the group §8(§a" + newGroupName + "§8)§8!");
                                    }
                                } else {
                                    commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The group §8(§a" + newGroupName + "§8) §7does not exist§8!");
                                }
                            } else {
                                commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The player §8(§a" + playerName + "§8) §7does not exist§8!");
                            }
                        } else {
                            sendHelp(commandSender);
                        }
                    }
                }else if (args.length >= 5) {
                    String groupName = args[1];
                    String playerName = args[1];
                    String permissions = args[3];
                    String newGroupName = args[3];
                    if (args[2].equalsIgnoreCase("setprefix")) {
                        if (Vynl.getInstance().getPermissionHandler().existsGroup(groupName)) {
                            String prefix = "";
                            for (int i = 3; i < args.length; ++i) {
                                prefix = prefix + args[i] + " ";
                            }
                            Vynl.getInstance().getPermissionHandler().setGroupPrefix(groupName, prefix.replace("&", "§"));
                            for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                                Vynl.getInstance().getPermissionHandler().setGroupPrefix(onlinePlayers);
                            }
                            commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The group §8(§a" + groupName + "§8) §7has a new prefix §8(§a" + prefix.replace("&", "§") + "§8)§8!");

                        } else {
                            commandSender.sendMessage(Vynl.getInstance().getPrefix() + "The group §8(§a" + groupName + "§8) §7does not exist§8!");
                        }
                    }
                }else{
                    sendHelp(commandSender);
                }
            } else {
                commandSender.sendMessage(Vynl.getInstance().getPrefix() + "You do not have §cpermission§7 to do that§8!");
            }

        return false;
    }

    public void sendHelp(CommandSender commandSender) {
        commandSender.sendMessage(Vynl.getInstance().getPrefix() + "Permissions");
        commandSender.sendMessage(Vynl.getInstance().getPrefix() + "/permission group");
        commandSender.sendMessage(Vynl.getInstance().getPrefix() + "/permission group §8(§aGROUP§8) §7create");
        commandSender.sendMessage(Vynl.getInstance().getPrefix() + "/permission group §8(§aGROUP§8) §7delete");
        commandSender.sendMessage(Vynl.getInstance().getPrefix() + "/permission group §8(§aGROUP§8) §7remove §8(§aPERMISSION§8)");
        commandSender.sendMessage(Vynl.getInstance().getPrefix() + "/permission group §8(§aGROUP§8) §7add §8(§aPERMISSION§8)");
        commandSender.sendMessage(Vynl.getInstance().getPrefix() + "/permission group §8(§aGROUP§8) §7setprefix §8(§aPREFIX§8)");
        commandSender.sendMessage(Vynl.getInstance().getPrefix() + "/permission group §8(§aGROUP§8) §7setid §8(§aID§8)");
        commandSender.sendMessage(Vynl.getInstance().getPrefix() + "/permission group §8(§aGROUP§8) §7setnamecolor §8(§aNAMECOLOR§8)");
        commandSender.sendMessage(Vynl.getInstance().getPrefix() + "/permission player §8(§aPLAYER§8)");
        commandSender.sendMessage(Vynl.getInstance().getPrefix() + "/permission player §8(§aPLAYER§8) §7set §8(§aGROUP§8)");
        commandSender.sendMessage(Vynl.getInstance().getPrefix() + "/permission player §8(§aPLAYER§8) §7add §8(§aPERMISSION§8)");
        commandSender.sendMessage(Vynl.getInstance().getPrefix() + "/permission player §8(§aPLAYER§8) §7remove §8(§aPERMISSION§8)");
    }
}
