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

package eu.mixeration.xox.handler;

import eu.mixeration.xox.PluginMain;
import eu.mixeration.xox.core.objects.*;
import eu.mixeration.xox.core.plugin.config.BoardsConfig;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.*;
import java.util.logging.Level;

public class BoardHandler {
    private final PluginMain plugin;
    private final ArrayList<UUID> boardCreators = new ArrayList<>();
    @Getter
    private HashMap<UUID, Board> boards = new HashMap<>();

    public BoardHandler() {
        this.plugin = PluginMain.getInstance();
    }

    public Board getBoardById(UUID id) {
        return boards.get(id);
    }

    public Board getBoardAtBlockLocation(WorldVector locationVector) {
        for (Board board : boards.values()) {
            for (BoardPosition itemPosition : BoardPosition.values()) {
                BoardItem boardItem = board.getBoardItem(itemPosition);

                if (boardItem != null) {
                    WorldVector boardItemBlockVector = boardItem.getLocation().getBlockLocationVector();

                    if (boardItemBlockVector.equals(locationVector)) {
                        return board;
                    }
                }
            }
        }

        return null;
    }

    public Board getBoardClosestToLocation(Location searchLocation, double maxDistance) {
        HashMap<Board, Double> nearbyBoards = new HashMap<>();
        for (Board board : boards.values()) {
            for (BoardPosition itemPosition : BoardPosition.values()) {
                BoardItem boardItem = board.getBoardItem(itemPosition);

                if (boardItem != null) {
                    try {
                        double distance = boardItem.getLocation().getLocation().distance(searchLocation);

                        if (distance <= maxDistance) {
                            nearbyBoards.put(board, distance);
                        }
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
        }
        if (!nearbyBoards.isEmpty()) {
            Board closestBoard = null;
            double closestDistance = Double.MAX_VALUE;

            for (Map.Entry<Board, Double> entrySet : nearbyBoards.entrySet()) {
                Board board = entrySet.getKey();
                double distance = entrySet.getValue();

                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestBoard = board;
                }
            }

            return closestBoard;
        }

        return null;
    }
    public void addBoard(Board board) {
        boards.put(board.getId(), board);
        saveBoards();

        // Generate Game
        plugin.getGameHandler().generateGame(board);
    }
    public void destroyBoard(Board board) {
        // Stop any active games
        Game game = plugin.getGameHandler().getGameForBoard(board);
        game.changeGamestate(Gamestate.NONE);
        plugin.getGameHandler().destroyGame(game);

        // Destroy the board
        Bukkit.getLogger().log(Level.INFO, String.format("[XOX] Board %s was removed due to an admin command.", board.getId()));
        boards.remove(board.getId());
        saveBoards();
    }
    public void saveBoards() {
        // Validate boards
        validateBoards();

        // Save to disk
        BoardsConfig boardsConfig = BoardsConfig.getConfig();
        boardsConfig.setSavedBoards(boards);
        boardsConfig.saveConfig();

        // Log
        Bukkit.getLogger().log(Level.INFO, String.format("[XOX] Saved and validated %s board%s to boards.json", boards.size(), (boards.size() > 1 ? "s" : "")));
    }
    public void loadBoards() {
        // Load from disk
        BoardsConfig boardsConfig = BoardsConfig.getConfig();
        boards = boardsConfig.getSavedBoards();

        // Validate & Save boards
        saveBoards();

        // Log
        Bukkit.getLogger().log(Level.INFO, String.format("[XOX] Loaded and validated %s board%s from boards.json", boards.size(), (boards.size() > 1 ? "s" : "")));
    }
    private void validateBoards() {
        // Validate boards
        Iterator<Board> iterator = boards.values().iterator();
        while (iterator.hasNext()) {
            Board board = iterator.next();

            if (!board.isBoardValid()) {
                // Invalid board found
                iterator.remove();

                Bukkit.getLogger().log(Level.WARNING, String.format("[XOX] Board %s was removed due to a validation error.", board.getId()));
            }
        }
    }
    public void addBoardCreator(UUID id) {
        boardCreators.add(id);
    }
    public boolean isBoardCreator(UUID id) {
        return boardCreators.contains(id);
    }
    public void removeBoardCreator(UUID id) {
        boardCreators.remove(id);
    }
}
