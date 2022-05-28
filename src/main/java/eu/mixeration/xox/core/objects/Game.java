
package eu.mixeration.xox.core.objects;

import eu.mixeration.xox.PluginMain;
import eu.mixeration.xox.core.plugin.Locale;
import eu.mixeration.xox.core.util.ArrayUtil;
import eu.mixeration.xox.handler.BoardHandler;
import eu.mixeration.xox.handler.ItemHandler;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.Sound;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Represents an active game (or lobby), asscoated with one particular Board
 */
@Data
@RequiredArgsConstructor
public class Game {
    private final UUID gameId;
    private final UUID boardId;
    private final HashMap<BoardPosition, UUID> playerTurns = new HashMap<>();
    private UUID player1Id;
    private UUID player2Id;
    private UUID winnerId;
    private ItemStack player1Item;
    private ItemStack player2Item;
    private int currentTurn; // 1 or 2
    private Gamestate gamestate = Gamestate.NONE;
    private int winTaskAnimationId = 0;
    public void changeGamestate(Gamestate gamestate) {
        this.gamestate = gamestate;

        switch (gamestate) {
            case NONE: {
                // Reset Board (only valid on game start or Board destroy)
                getBoard().fillBoardItems(new ItemStack(Material.AIR), false);

                player1Id = null;
                player2Id = null;
                winnerId = null;
                player1Item = null;
                player2Item = null;
                currentTurn = 0;
                playerTurns.clear();
                break;
            }
            case WAITING: {
                // Reset Board and Put up the join blocks
                getBoard().fillBoardItems(ItemHandler.ITEM_GAME_JOIN, true);

                winnerId = null;
                currentTurn = 0;
                playerTurns.clear();
                break;
            }
            case INGAME: {
                // Start the game
                getBoard().fillBoardItems(new ItemStack(Material.AIR), false);
                playerTurns.clear();

                sendPlayersMessage(Locale.EVENT_GAME_START);
                setTurn(new Random().nextBoolean() ? 1 : 2); // Pick either 1 or 2
                break;
            }
            case END: {
                break;
            }
        }

        PluginMain.getInstance().getGameHandler().checkGame(this);
    }

    private void setTurn(int playerNumber) {
        currentTurn = playerNumber;
        if (playerNumber != 1 && playerNumber != 2) {
            setTurn(1);
        }
    }

    public void playTurn(BoardPosition boardPosition) {
        // Check if the square already has been filled
        if (playerTurns.containsKey(boardPosition)) {
            // Silently cancel
            return;
        }

        // Update the item frame
        BoardItem boardItem = getBoard().getBoardItem(boardPosition);
        ItemFrame itemFrame = boardItem.getItemFrame();

        Player player = null;
        ItemStack playerItem = null;
        if (currentTurn == 1) {
            playerItem = getPlayer1Item();
            player = Bukkit.getPlayer(player1Id);
        } else if (currentTurn == 2) {
            playerItem = getPlayer2Item();
            player = Bukkit.getPlayer(player2Id);
        }

        ItemStack finalPlayerItem = playerItem;
        Bukkit.getScheduler().runTask(PluginMain.getInstance(), () -> {
            itemFrame.setItem(finalPlayerItem);
            itemFrame.setRotation(Rotation.NONE);
        });

        // Update the register
        playerTurns.put(boardPosition, player.getUniqueId());

        // Provide user feedback
        getBoard().playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 2);
        setTurn(currentTurn + 1);

        // Check the game's status
        PluginMain.getInstance().getGameHandler().checkGame(this);
    }

    public void sendPlayersMessage(String message, Object... format) {
        Player player1 = Bukkit.getPlayer(getPlayer1Id());
        Player player2 = Bukkit.getPlayer(getPlayer2Id());

        if (player1 != null) {
            Locale.sendMessage(player1, message, format);
        }

        if (player2 != null) {
            Locale.sendMessage(player2, message, format);
        }
    }

    public void sendPlayersActionBar(String message, Object... format) {
        Player player1 = Bukkit.getPlayer(getPlayer1Id());
        Player player2 = Bukkit.getPlayer(getPlayer2Id());

        if (player1 != null) {
            PluginMain.getInstance().getActionBarUtil().sendStickyActionBarMessage(player1, String.format(message, format));
        }

        if (player2 != null) {
            PluginMain.getInstance().getActionBarUtil().sendStickyActionBarMessage(player2, String.format(message, format));
        }
    }

    public Board getBoard() {
        BoardHandler boardHandler = PluginMain.getInstance().getBoardHandler();

        return boardHandler.getBoardById(getBoardId());
    }

    public boolean isGameATie() {
        return findThreeInARow() == null && playerTurns.size() == 9;
    }

    public List<BoardPosition> findThreeInARow() {
        // Test Vertical Columns
        if (ArrayUtil.testIfElementsIdentical(false, getPlayerTurn(BoardPosition.TOP_LEFT), getPlayerTurn(BoardPosition.TOP_MIDDLE), getPlayerTurn(BoardPosition.TOP_RIGHT))) {
            return Arrays.asList(BoardPosition.TOP_LEFT, BoardPosition.TOP_MIDDLE, BoardPosition.TOP_RIGHT);
        }

        if (ArrayUtil.testIfElementsIdentical(false, getPlayerTurn(BoardPosition.MIDDLE_LEFT), getPlayerTurn(BoardPosition.CENTER), getPlayerTurn(BoardPosition.MIDDLE_RIGHT))) {
            return Arrays.asList(BoardPosition.MIDDLE_LEFT, BoardPosition.CENTER, BoardPosition.MIDDLE_RIGHT);
        }

        if (ArrayUtil.testIfElementsIdentical(false, getPlayerTurn(BoardPosition.BOTTOM_LEFT), getPlayerTurn(BoardPosition.BOTTOM_MIDDLE), getPlayerTurn(BoardPosition.BOTTOM_RIGHT))) {
            return Arrays.asList(BoardPosition.BOTTOM_LEFT, BoardPosition.BOTTOM_MIDDLE, BoardPosition.BOTTOM_RIGHT);
        }

        // Test Horizontal Rows
        if (ArrayUtil.testIfElementsIdentical(false, getPlayerTurn(BoardPosition.TOP_LEFT), getPlayerTurn(BoardPosition.MIDDLE_LEFT), getPlayerTurn(BoardPosition.BOTTOM_LEFT))) {
            return Arrays.asList(BoardPosition.TOP_LEFT, BoardPosition.MIDDLE_LEFT, BoardPosition.BOTTOM_LEFT);
        }

        if (ArrayUtil.testIfElementsIdentical(false, getPlayerTurn(BoardPosition.TOP_MIDDLE), getPlayerTurn(BoardPosition.CENTER), getPlayerTurn(BoardPosition.BOTTOM_MIDDLE))) {
            return Arrays.asList(BoardPosition.TOP_MIDDLE, BoardPosition.CENTER, BoardPosition.BOTTOM_MIDDLE);
        }

        if (ArrayUtil.testIfElementsIdentical(false, getPlayerTurn(BoardPosition.TOP_RIGHT), getPlayerTurn(BoardPosition.MIDDLE_RIGHT), getPlayerTurn(BoardPosition.BOTTOM_RIGHT))) {
            return Arrays.asList(BoardPosition.TOP_RIGHT, BoardPosition.MIDDLE_RIGHT, BoardPosition.BOTTOM_RIGHT);
        }

        // Test Diagonals
        if (ArrayUtil.testIfElementsIdentical(false, getPlayerTurn(BoardPosition.TOP_LEFT), getPlayerTurn(BoardPosition.CENTER), getPlayerTurn(BoardPosition.BOTTOM_RIGHT))) {
            return Arrays.asList(BoardPosition.TOP_LEFT, BoardPosition.CENTER, BoardPosition.BOTTOM_RIGHT);
        }

        if (ArrayUtil.testIfElementsIdentical(false, getPlayerTurn(BoardPosition.TOP_RIGHT), getPlayerTurn(BoardPosition.CENTER), getPlayerTurn(BoardPosition.BOTTOM_LEFT))) {
            return Arrays.asList(BoardPosition.TOP_RIGHT, BoardPosition.CENTER, BoardPosition.BOTTOM_LEFT);
        }

        return null;
    }

    public UUID getPlayerTurn(BoardPosition boardPosition) {
        return playerTurns.getOrDefault(boardPosition, null);
    }

    public void cancelWinAnimationTask() {
        Bukkit.getScheduler().cancelTask(winTaskAnimationId);
    }
}
