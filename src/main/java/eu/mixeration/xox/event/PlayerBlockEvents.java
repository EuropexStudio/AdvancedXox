

package eu.mixeration.xox.event;

import eu.mixeration.xox.PluginMain;
import eu.mixeration.xox.core.objects.Board;
import eu.mixeration.xox.core.objects.WorldVector;
import eu.mixeration.xox.core.plugin.Locale;
import eu.mixeration.xox.handler.BoardHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

@Data
@AllArgsConstructor
public class PlayerBlockEvents implements Listener {
    private final PluginMain plugin;

    public PlayerBlockEvents() {
        this.plugin = PluginMain.getInstance();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        BoardHandler boardHandler = plugin.getBoardHandler();
        Board board = boardHandler.getBoardAtBlockLocation(new WorldVector(block.getLocation()));

        if (board != null) {
            event.setCancelled(true);

            Locale.sendMessage(player, Locale.ERROR_BLOCK_PLACE_EVENT_DENY);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        BoardHandler boardHandler = plugin.getBoardHandler();
        Block northBlock = block.getRelative(BlockFace.NORTH);
        Board northBoard = boardHandler.getBoardAtBlockLocation(new WorldVector(northBlock.getLocation()));
        Block southBlock = block.getRelative(BlockFace.SOUTH);
        Board southBoard = boardHandler.getBoardAtBlockLocation(new WorldVector(southBlock.getLocation()));
        Block westBlock = block.getRelative(BlockFace.WEST);
        Board westBoard = boardHandler.getBoardAtBlockLocation(new WorldVector(westBlock.getLocation()));
        Block eastBlock = block.getRelative(BlockFace.EAST);
        Board eastBoard = boardHandler.getBoardAtBlockLocation(new WorldVector(eastBlock.getLocation()));
        if (northBoard != null || southBoard != null || westBoard != null || eastBoard != null) {
            event.setCancelled(true);
            Locale.sendMessage(player, Locale.ERROR_BLOCK_BREAK_EVENT_DENY);
        }
    }
}
