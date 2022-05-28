
package eu.mixeration.xox.event;

import eu.mixeration.xox.PluginMain;
import eu.mixeration.xox.core.objects.Board;
import eu.mixeration.xox.core.objects.Game;
import eu.mixeration.xox.core.plugin.config.MainConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
@Data
@AllArgsConstructor
public class PlayerMoveEvents implements Listener {
    private final PluginMain plugin;

    public PlayerMoveEvents() {
        this.plugin = PluginMain.getInstance();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Location newLocation = event.getTo();
        Player player = event.getPlayer();

        Game game = plugin.getGameHandler().getGameForPlayer(player);
        if (game != null) {
            Board closestBoard = plugin.getBoardHandler().getBoardClosestToLocation(newLocation, MainConfig.getConfig().getMaxPlayerBoardDistance());

            if (closestBoard == null || closestBoard != game.getBoard()) {
                plugin.getGameHandler().removeFromGame(player);
            }
        }
    }
}
