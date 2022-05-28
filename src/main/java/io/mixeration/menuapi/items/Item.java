
package io.mixeration.menuapi.items;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public interface Item {

    public ItemStack stack();

    public default void act(Player player, ClickType clickType) {
    }
}
