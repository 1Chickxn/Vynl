package me.chickxn.commands;

import me.chickxn.Vynl;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermissionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (player.hasPermission("module.permission.use")) {
                if (args.length == 0) {
                    player.sendMessage(Vynl.getInstance().getPrefix() + "Permissions");
                    player.sendMessage(Vynl.getInstance().getPrefix() + "/permission group");
                    player.sendMessage(Vynl.getInstance().getPrefix() + "/permission group §8(§aGROUP§8) §7create");
                    player.sendMessage(Vynl.getInstance().getPrefix() + "/permission group §8(§aGROUP§8) §7delete");
                    player.sendMessage(Vynl.getInstance().getPrefix() + "/permission group §8(§aGROUP§8) §7remove §8(§aPERMISSION§8)");
                    player.sendMessage(Vynl.getInstance().getPrefix() + "/permission group §8(§aGROUP§8) §7add §8(§aPERMISSION§8)");
                }else if (args.length == 1){
                    if (args[0].equalsIgnoreCase("group")) {
                        player.sendMessage(Vynl.getInstance().getPrefix() + "all groups");
                        String groups = String.valueOf(Vynl.getInstance().getPermissionHandler().listGroups());
                        player.sendMessage(Vynl.getInstance().getPrefix() + "§a" + groups.replace("[", "").replace("]", "").replace(",", "§8,§a"));
                    }
                }else if (args.length == 2) {
                    String groupName = args[1];
                    if (groupName != null) {
                        if (Vynl.getInstance().getPermissionHandler().existsGroup(groupName)) {
                            String groupPermissions = String.valueOf(Vynl.getInstance().getPermissionHandler().listGroupPermissions(groupName));
                            player.sendMessage(Vynl.getInstance().getPrefix() + "Info zur Gruppe §a" + groupName);
                            player.sendMessage(Vynl.getInstance().getPrefix() + "Permissions: §a" + groupPermissions.replace("[", "").replace("]", "").replace(",", "§8,§a"));
                        }else{
                            player.sendMessage(Vynl.getInstance().getPrefix() + "Die Gruppe §8(§a" + groupName + "§8) §7exestiert nicht§8!");
                        }
                    }
                }else if (args.length == 3) {
                    String groupName = args[1];
                    if (args[2].equalsIgnoreCase("create")) {
                        if (!Vynl.getInstance().getPermissionHandler().existsGroup(groupName)){
                            Vynl.getInstance().getPermissionHandler().createGroup(groupName);
                            player.sendMessage(Vynl.getInstance().getPrefix() + "Die Gruppe §8(§a" + groupName + "§8) §7wurde erstellt§8!");
                        }else{
                            player.sendMessage(Vynl.getInstance().getPrefix() + "Die Gruppe §8(§a" + groupName + "§8) §7exestiert bereits§8!");
                        }
                    }else if (args[2].equalsIgnoreCase("delete") || args[2].equalsIgnoreCase("remove") ) {
                        if (Vynl.getInstance().getPermissionHandler().existsGroup(groupName)) {
                            Vynl.getInstance().getPermissionHandler().removeGroup(groupName);
                            player.sendMessage(Vynl.getInstance().getPrefix() + "Die Gruppe §8(§a" + groupName + "§8) §7wurde entfernt§8!");
                        }else{
                            player.sendMessage(Vynl.getInstance().getPrefix() + "Die Gruppe §8(§a" + groupName + "§8) §7exestiert nicht§8!");
                        }
                    }else{
                        player.sendMessage(Vynl.getInstance().getPrefix() + "Permissions");
                        player.sendMessage(Vynl.getInstance().getPrefix() + "/permission group");
                        player.sendMessage(Vynl.getInstance().getPrefix() + "/permission group §8(§aGROUP§8) §7create");
                        player.sendMessage(Vynl.getInstance().getPrefix() + "/permission group §8(§aGROUP§8) §7delete");
                        player.sendMessage(Vynl.getInstance().getPrefix() + "/permission group §8(§aGROUP§8) §7remove §8(§aPERMISSION§8)");
                        player.sendMessage(Vynl.getInstance().getPrefix() + "/permission group §8(§aGROUP§8) §7add §8(§aPERMISSION§8)");
                    }
                }else if (args.length == 4) {
                    String groupName = args[1];
                    String permissions = args[3];
                    if (args[2].equalsIgnoreCase("add")) {
                        if (!Vynl.getInstance().getPermissionHandler().existsGroupPermission(groupName, permissions)) {
                            Vynl.getInstance().getPermissionHandler().addGroupPermission(groupName, permissions);
                            player.sendMessage(Vynl.getInstance().getPrefix() + "Die Permission §8(§a" + permissions + "§8) §7wurde in der Gruppe §8(§a" + groupName + "§8) §7hinzugefügt§8!");
                            for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                                Vynl.getInstance().getPermissionHandler().updatePermission(onlinePlayers);
                            }
                        }else{
                            player.sendMessage(Vynl.getInstance().getPrefix() + "Die Permission §8(§a" + permissions + "§8) §7ist bereits in der Gruppe §8(§a" + groupName + "§8)§8!");
                        }
                    }else if(args[2].equalsIgnoreCase("remove")) {
                        if (Vynl.getInstance().getPermissionHandler().existsGroupPermission(groupName, permissions)) {
                            Vynl.getInstance().getPermissionHandler().removeGroupPermission(groupName, permissions);
                            player.sendMessage(Vynl.getInstance().getPrefix() + "Die Permission §8(§a" + permissions + "§8) §7wurde in der Gruppe §8(§a" + groupName + "§8) §7entfernt§8!");
                            for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
                                Vynl.getInstance().getPermissionHandler().updatePermission(onlinePlayers);
                            }
                        }else{
                            player.sendMessage(Vynl.getInstance().getPrefix() + "Die Permission §8(§a" + permissions + "§8) §7exestiert in der Gruppe §8(§a" + groupName + "§8) §7nicht§8!");
                        }
                    }else{
                        player.sendMessage(Vynl.getInstance().getPrefix() + "Permissions");
                        player.sendMessage(Vynl.getInstance().getPrefix() + "/permission group");
                        player.sendMessage(Vynl.getInstance().getPrefix() + "/permission group §8(§aGROUP§8) §7create");
                        player.sendMessage(Vynl.getInstance().getPrefix() + "/permission group §8(§aGROUP§8) §7delete");
                        player.sendMessage(Vynl.getInstance().getPrefix() + "/permission group §8(§aGROUP§8) §7remove §8(§aPERMISSION§8)");
                        player.sendMessage(Vynl.getInstance().getPrefix() + "/permission group §8(§aGROUP§8) §7add §8(§aPERMISSION§8)");
                    }
                }
            }else{
                player.sendMessage(Vynl.getInstance().getPrefix() + "Dazu hast du keine §cRechte§8!");
            }
        }
        return false;
    }

}
