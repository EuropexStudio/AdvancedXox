
package io.mixeration.menuapi.items;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public interface ItemListener {
    public void act(Player player, ClickType type);
}
