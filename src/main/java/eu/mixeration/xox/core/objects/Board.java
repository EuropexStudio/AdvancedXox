

package eu.mixeration.xox.core.objects;

import eu.mixeration.xox.PluginMain;
import eu.mixeration.xox.handler.BoardHandler;
import eu.mixeration.xox.handler.ItemHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Board {
    private UUID id;
    private WorldVector centerVector;

    public BoardItem getBoardItem(BoardPosition boardPosition) {
        // Validation checks
        if (centerVector == null || centerVector.getWorldName() == null || centerVector.getLocation() == null || centerVector.getLocation().getWorld() == null) {
            return null;
        }

        Location centerLocation = centerVector.getLocation();
        Collection<Entity> entities = centerLocation.getWorld().getNearbyEntities(centerLocation, 0, 0, 0, entity -> entity instanceof ItemFrame);

        BlockFace attachedFace = null;

        // Identify the center item's attached face
        if (entities.size() == 1) {
            for (Entity entity : entities) {
                attachedFace = ((ItemFrame) entity).getAttachedFace();
                break;
            }
        }

        // Sanity check
        if (attachedFace == null) {
            return null;
        }

        // Identify the correct offsets
        double xOffset = 0;
        double yOffset = 0;
        double zOffset = 0;

        switch (boardPosition) {
            case TOP_LEFT: {
                switch (Objects.requireNonNull(attachedFace)) {
                    case NORTH:
                        xOffset = -1;
                        yOffset = 1;
                        zOffset = 0;
                        break;
                    case WEST:
                        xOffset = 0;
                        yOffset = 1;
                        zOffset = 1;
                        break;
                    case SOUTH:
                        xOffset = 1;
                        yOffset = 1;
                        zOffset = 0;
                        break;
                    case EAST:
                        xOffset = 0;
                        yOffset = 1;
                        zOffset = -1;
                        break;
                }
                break;
            }
            case TOP_RIGHT: {
                switch (Objects.requireNonNull(attachedFace)) {
                    case NORTH:
                        xOffset = 1;
                        yOffset = 1;
                        zOffset = 0;
                        break;
                    case WEST:
                        xOffset = 0;
                        yOffset = 1;
                        zOffset = -1;
                        break;
                    case SOUTH:
                        xOffset = -1;
                        yOffset = 1;
                        zOffset = 0;
                        break;
                    case EAST:
                        xOffset = 0;
                        yOffset = 1;
                        zOffset = 1;
                        break;
                }
                break;
            }
            case MIDDLE_LEFT: {
                switch (Objects.requireNonNull(attachedFace)) {
                    case NORTH:
                        xOffset = 1;
                        yOffset = 0;
                        zOffset = 0;
                        break;
                    case WEST:
                        xOffset = 0;
                        yOffset = 0;
                        zOffset = 1;
                        break;
                    case SOUTH:
                        xOffset = -1;
                        yOffset = 0;
                        zOffset = 0;
                        break;
                    case EAST:
                        xOffset = 0;
                        yOffset = 0;
                        zOffset = -1;
                        break;
                }
                break;
            }
            case MIDDLE_RIGHT: {
                switch (Objects.requireNonNull(attachedFace)) {
                    case NORTH:
                        xOffset = -1;
                        yOffset = 0;
                        zOffset = 0;
                        break;
                    case WEST:
                        xOffset = 0;
                        yOffset = 0;
                        zOffset = -1;
                        break;
                    case SOUTH:
                        xOffset = 1;
                        yOffset = 0;
                        zOffset = 0;
                        break;
                    case EAST:
                        xOffset = 0;
                        yOffset = 0;
                        zOffset = 1;
                        break;
                }
                break;
            }
            case BOTTOM_LEFT: {
                switch (Objects.requireNonNull(attachedFace)) {
                    case NORTH:
                        xOffset = -1;
                        yOffset = -1;
                        zOffset = 0;
                        break;
                    case WEST:
                        xOffset = 0;
                        yOffset = -1;
                        zOffset = 1;
                        break;
                    case SOUTH:
                        xOffset = 1;
                        yOffset = -1;
                        zOffset = 0;
                        break;
                    case EAST:
                        xOffset = 0;
                        yOffset = -1;
                        zOffset = -1;
                        break;
                }
                break;
            }
            case BOTTOM_RIGHT: {
                switch (Objects.requireNonNull(attachedFace)) {
                    case NORTH:
                        xOffset = 1;
                        yOffset = -1;
                        zOffset = 0;
                        break;
                    case WEST:
                        xOffset = 0;
                        yOffset = -1;
                        zOffset = -1;
                        break;
                    case SOUTH:
                        xOffset = -1;
                        yOffset = -1;
                        zOffset = 0;
                        break;
                    case EAST:
                        xOffset = 0;
                        yOffset = -1;
                        zOffset = 1;
                        break;
                }
                break;
            }
            case TOP_MIDDLE: {
                yOffset = 1;
                break;
            }
            case BOTTOM_MIDDLE: {
                yOffset = -1;
                break;
            }
        }

        // Identify the new item
        Location newLocation = centerLocation.clone().add(xOffset, yOffset, zOffset);
        BoardItem boardItem = new BoardItem(new WorldVector(newLocation), this, boardPosition);
        ItemFrame itemFrame = boardItem.getItemFrame();

        // Final sanity check
        if (itemFrame == null) {
            return null;
        }

        return boardItem;
    }

    public BoardPosition getPositionOfItemFrame(ItemFrame itemFrame) {
        for (BoardPosition itemPosition : BoardPosition.values()) {
            BoardItem boardItem = getBoardItem(itemPosition);

            if (boardItem != null && boardItem.getItemFrame().equals(itemFrame)) {
                return itemPosition;
            }
        }

        return null;
    }

    public boolean isBoardValid() {
        // Loop through all board items
        for (BoardPosition itemPosition : BoardPosition.values()) {
            BoardItem boardItem = getBoardItem(itemPosition);

            if (boardItem == null) {
                return false;
            } else {
                ItemFrame itemFrame = boardItem.getItemFrame();

                if (itemFrame.getItem().getType() != Material.AIR && !ItemHandler.isTicTacToeItem(itemFrame.getItem())) {
                    return false;
                }
            }
        }

        // Check if any other boards exist at this location
        BoardHandler boardHandler = PluginMain.getInstance().getBoardHandler();
        Board otherBoard = boardHandler.getBoardAtBlockLocation(getCenterVector());

        if (otherBoard != null && otherBoard != this) {
            return false;
        }

        return true;
    }

    public void playSound(Sound sound, int pitch) {
        Location location = getCenterVector().getBlockLocation();
        Objects.requireNonNull(location.getWorld()).playSound(location, sound, SoundCategory.NEUTRAL, 1, pitch);
    }

    private void setBoardItem(BoardPosition position, boolean doDisplayName, ItemStack item) {
        ItemFrame itemFrame = getBoardItem(position).getItemFrame();

        // Remove Display Name
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            if (!doDisplayName) {
                itemMeta.setDisplayName(null);
                item.setItemMeta(itemMeta);
            }
        }

        // Clear any rotation
        itemFrame.setRotation(Rotation.NONE);

        // Apply Item
        itemFrame.setItem(item, false);
    }

    public void fillBoardItems(ItemStack item, boolean doDisplayName) {
        ItemStack fillItem = item.clone();
        PluginMain pluginMain = PluginMain.getInstance();

        for (BoardPosition boardPosition : BoardPosition.values()) {
            // Task required to fix issue with block updates not being delivered
            Bukkit.getScheduler().runTask(pluginMain, () -> setBoardItem(boardPosition, doDisplayName, fillItem));
        }
    }
}
