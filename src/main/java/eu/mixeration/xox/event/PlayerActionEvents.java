/*
 * MIT License
 *
 * Copyright (c) 2020 Luke Anderson (stuntguy3000)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package eu.mixeration.xox.event;

import eu.mixeration.xox.PluginMain;
import eu.mixeration.xox.core.objects.*;
import eu.mixeration.xox.core.plugin.Locale;
import eu.mixeration.xox.handler.BoardHandler;
import eu.mixeration.xox.handler.GameHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PlayerActionEvents implements Listener {
    private final PluginMain plugin;

    public PlayerActionEvents() {
        this.plugin = PluginMain.getInstance();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemRemove(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();

        if (entity instanceof ItemFrame && damager instanceof Player) {
            Block block = entity.getLocation().getBlock();
            Board board = plugin.getBoardHandler().getBoardAtBlockLocation(new WorldVector(block.getLocation()));
            if (board != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDestroy(HangingBreakByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity remover = event.getRemover();

        if (entity instanceof ItemFrame && remover instanceof Player) {
            Block block = entity.getLocation().getBlock();
            Board board = plugin.getBoardHandler().getBoardAtBlockLocation(new WorldVector(block.getLocation()));
            if (board != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onRightClick(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame) || event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        ItemFrame itemFrame = (ItemFrame) event.getRightClicked();
        Block block = itemFrame.getLocation().getBlock();

        BoardHandler boardHandler = plugin.getBoardHandler();

        if (boardHandler.isBoardCreator(id)) {
            event.setCancelled(true);

            player.sendMessage("");

            Board newBoard = new Board(UUID.randomUUID(), new WorldVector(itemFrame.getLocation()));
            if (!newBoard.isBoardValid()) {
                Locale.sendMessage(player, Locale.ERROR_BOARD_CREATE);
                Locale.sendMessage(player, Locale.COMMAND_BOARD_CREATE_START_L2, "xox");
            } else {
                boardHandler.addBoard(newBoard);
                boardHandler.removeBoardCreator(id);
                Locale.sendMessage(player, Locale.SUCCESS_BOARD_CREATE);
            }

            player.sendMessage("");
            return;
        }

        Board board = boardHandler.getBoardAtBlockLocation(new WorldVector(block.getLocation()));
        if (board != null) {
            event.setCancelled(true);
            GameHandler gameHandler = plugin.getGameHandler();
            Game playerGame = gameHandler.getGameForPlayer(player);
            Game boardGame = gameHandler.getGameForBoard(board);
            if (boardGame == null) {
                Locale.sendMessage(player, Locale.ERROR_GAME_JOIN_FAIL);
            } else if (playerGame == null) {
                if (boardGame.getGamestate() == Gamestate.WAITING) {
                    gameHandler.tryAddToGame(player, boardGame);
                } else {
                    return;
                }
            } else {
                if (boardGame.getBoard() != board) {
                    return;
                }
                if (boardGame.getGamestate() != Gamestate.INGAME) {
                    return;
                }
                if (!(boardGame.getCurrentTurn() == 1 && boardGame.getPlayer1Id() == player.getUniqueId()) && !(boardGame.getCurrentTurn() == 2 && boardGame.getPlayer2Id() == player.getUniqueId())) {
                    Locale.sendMessage(player, Locale.ERROR_NOT_YOUR_TURN);
                    return;
                }
                BoardPosition itemPosition = board.getPositionOfItemFrame(itemFrame);
                boardGame.playTurn(itemPosition);
            }
        }
    }
}
