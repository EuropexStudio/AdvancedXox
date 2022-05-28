
package io.mixeration.menuapi.items;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class BasicItem implements Item {
    private ItemListener listener;
    private ItemStack stack;

    private BasicItem(ItemStack stack, ItemListener listener) {
        this.stack = stack;
        this.listener = listener;
    }

    public static BasicItem create(ItemStack stack, ItemListener listener) {
        return new BasicItem(stack, listener);
    }

    @Override
    public ItemStack stack() {
        return stack;
    }

    @Override
    public void act(Player player, ClickType clickType) {
        if (listener == null)
            return;

        listener.act(player, clickType);
    }
}
