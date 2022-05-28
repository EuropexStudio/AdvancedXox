package eu.mixeration.xox.core.util;

import eu.mixeration.xox.PluginMain;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class ActionBarUtil implements Listener {
    private final HashMap<UUID, String> activeMessages = new HashMap<>();
    public void sendActionBarMessage(Player player, String message) {
        if (player == null || !player.isOnline()) {
            return;
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));
    }

    public void sendStickyActionBarMessage(final Player player, final String message) {
        sendActionBarMessage(player, message);
        activeMessages.put(player.getUniqueId(), message);
    }

    public void runLoop() {
        PluginMain plugin = PluginMain.getInstance();

        new BukkitRunnable() {
            @Override
            public void run() {
                Iterator<Map.Entry<UUID, String>> iterator = activeMessages.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<UUID, String> entry = iterator.next();

                    Player player = Bukkit.getPlayer(entry.getKey());
                    if (player != null && player.isOnline()) {
                        sendActionBarMessage(player, entry.getValue());
                    } else {
                        iterator.remove();
                    }
                }
            }
        }.runTaskTimer(plugin, 40, 40);
    }

    public void clearActionBarMessage(Player player) {
        activeMessages.remove(player.getUniqueId());
        sendActionBarMessage(player, "");
    }
}