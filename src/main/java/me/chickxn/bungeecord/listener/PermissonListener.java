package me.chickxn.bungeecord.listener;

import me.chickxn.bungeecord.Vynl;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.lang.reflect.Proxy;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class PermissonListener implements Listener {

    private ArrayList<String> currentPermissions = new ArrayList<>();
    @EventHandler
    public void onPlayerJoin(final PostLoginEvent postLoginEvent) {
        ProxiedPlayer proxiedPlayer = postLoginEvent.getPlayer();
        if (existsPlayer(proxiedPlayer.getUniqueId().toString())) {
            for (String playerPermissions : listPlayerPermission(proxiedPlayer.getUniqueId().toString())) {
                proxiedPlayer.setPermission(playerPermissions, true);
            }
            for (String groupPermissions : listGroupPermissions(getPlayerGroup(proxiedPlayer.getUniqueId().toString()))) {
                proxiedPlayer.setPermission(groupPermissions, true);
            }
        }
        System.out.println(proxiedPlayer.getPermissions());
    }

    @EventHandler
    public void onPluginMessageReceived(PluginMessageEvent event) {
        if (event.getTag().equals("BungeeCord")) {
            byte[] data = event.getData();
            String message = new String(data);
            ProxyServer.getInstance().getLogger().info("Nachricht erhalten: " + message);
            for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
                ProxyServer.getInstance().getLogger().info(String.valueOf(proxiedPlayer) + "-" + proxiedPlayer.getPermissions());
                for (String curPermissions : proxiedPlayer.getPermissions()) {
                    currentPermissions.add(curPermissions);
                }
                for (String permission : currentPermissions) {
                    proxiedPlayer.setPermission(permission, false);
                }
                currentPermissions.clear();
                ProxyServer.getInstance().getLogger().info(String.valueOf(proxiedPlayer) + "-" + proxiedPlayer.getPermissions());
                for (String playerPermissions : listPlayerPermission(proxiedPlayer.getUniqueId().toString())) {
                    proxiedPlayer.setPermission(playerPermissions, true);
                }
                for (String groupPermissions : listGroupPermissions(getPlayerGroup(proxiedPlayer.getUniqueId().toString()))) {
                    proxiedPlayer.setPermission(groupPermissions, true);
                }
                ProxyServer.getInstance().getLogger().info(String.valueOf(proxiedPlayer) + "-" + proxiedPlayer.getPermissions());
            }
        }
    }

    public ArrayList<String> listPlayerPermission(String uuid) {
        try {
            PreparedStatement preparedStatement = Vynl.getInstance().getSqlDriver().getConnection().prepareStatement("SELECT * FROM permission_player WHERE uuid='" + uuid + "'");
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            String permissionsInGroup = resultSet.getString("playerPermissions");
            permissionsInGroup = permissionsInGroup.replace("[", "").replace("]", "").replace(" ", "");
            ArrayList<String> permissionsList = new ArrayList<String>(Arrays.asList(permissionsInGroup.split(",")));
            permissionsList.add(permissionsInGroup);
            permissionsList.remove(permissionsInGroup);
            resultSet.close();
            preparedStatement.close();
            return permissionsList;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public ArrayList<String> listGroupPermissions(String groupName) {
        try {
            PreparedStatement preparedStatement = Vynl.getInstance().getSqlDriver().getConnection().prepareStatement("SELECT * FROM permission_groups WHERE groupName='" + groupName.toLowerCase() + "'");
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            String permissionsInGroup = resultSet.getString("groupPermissions");
            permissionsInGroup = permissionsInGroup.replace("[", "").replace("]", "").replace(" ", "");
            ArrayList<String> permissionsList = new ArrayList<String>(Arrays.asList(permissionsInGroup.split(",")));
            permissionsList.add(permissionsInGroup);
            permissionsList.remove(permissionsInGroup);
            resultSet.close();
            preparedStatement.close();
            return permissionsList;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public String getPlayerGroup(String uuid) {
        try {
            PreparedStatement preparedStatement = Vynl.getInstance().getSqlDriver().getConnection().prepareStatement("SELECT * FROM permission_player WHERE uuid='" + uuid + "'");
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            String groupID = resultSet.getString("currentGroup");
            resultSet.close();
            preparedStatement.close();
            return groupID;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public boolean existsPlayer(String uuid) {
        try {
            PreparedStatement preparedStatement = Vynl.getInstance().getSqlDriver().getConnection().prepareStatement("SELECT * FROM permission_player WHERE uuid='" + uuid + "'");
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean existsPlayer = resultSet.next();
            preparedStatement.close();
            return existsPlayer;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
