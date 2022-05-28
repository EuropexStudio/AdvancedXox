package eu.mixeration.xox.core.util;

import eu.mixeration.xox.core.plugin.config.Lobby_Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LobbyUtil {

    public static void setSpawn(Player player, String name) {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        if(Lobby_Config.getConfig().getString("xox-lobby." + name) == null) {
            Location location = player.getLocation();
            Lobby_Config.getConfig().set("xox-lobby." + name + ".worldName", player.getWorld().getName());
            Lobby_Config.getConfig().set("xox-lobby." + name + ".x", location.getX());
            Lobby_Config.getConfig().set("xox-lobby." + name + ".y", location.getY());
            Lobby_Config.getConfig().set("xox-lobby." + name + ".z", location.getZ());
            Lobby_Config.getConfig().set("xox-lobby." + name + ".yaw", location.getYaw());
            Lobby_Config.getConfig().set("xox-lobby." + name + ".pitch", location.getPitch());
            Lobby_Config.getConfig().set("xox-lobby." + name + ".by", player.getName());
            Lobby_Config.getConfig().set("xox-lobby." + name + ".createdAt", now);
            Lobby_Config.getConfig().set("xox-lobby." + name + ".permission", "iospawn." + name);
            Lobby_Config.saveConfig();
        } else {
            Bukkit.getConsoleSender().sendMessage("[XOX] Lobby name (" + name + ") already in use.");
        }
    }

    public static void sendSpawn(Player player, String name) {
        if(!(Lobby_Config.getConfig().getString("xox-lobby." + name) == null)) {
            String worldName = Lobby_Config.getConfig().getString("xox-lobby." + name + ".worldName");
            World spawnWorld = Bukkit.getServer().getWorld(worldName);
            double x = Lobby_Config.getConfig().getDouble("xox-lobby." + name + ".x");
            double y = Lobby_Config.getConfig().getDouble("xox-lobby." + name + ".y");
            double z = Lobby_Config.getConfig().getDouble("xox-lobby." + name + ".z");
            float yaw = (float) Lobby_Config.getConfig().getDouble("xox-lobby." + name + ".double");
            float pitch = (float) Lobby_Config.getConfig().getDouble("xox-lobby." + name + ".pitch");
            Location loc = new Location(spawnWorld, x, y, z);
            loc.setYaw(yaw);
            player.getLocation().setPitch(pitch);
            player.teleport(loc);
        } else {
            Bukkit.getConsoleSender().sendMessage("[XOX] Lobby (" + name + ") not found.");

        }
    }

}
