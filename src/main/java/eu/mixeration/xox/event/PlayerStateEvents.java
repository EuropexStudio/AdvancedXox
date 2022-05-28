

package eu.mixeration.xox.event;

import eu.mixeration.xox.PluginMain;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
@Data
@AllArgsConstructor
public class PlayerStateEvents implements Listener {
    private final PluginMain plugin;

    public PlayerStateEvents() {
        this.plugin = PluginMain.getInstance();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLeave(PlayerQuitEvent event) {
        plugin.getGameHandler().removeFromGame(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        plugin.getGameHandler().removeFromGame(event.getEntity());
    }
}
