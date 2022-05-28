
package io.mixeration.menuapi;

import io.mixeration.menuapi.items.BasicItem;
import io.mixeration.menuapi.items.Item;
import io.mixeration.menuapi.items.ItemListener;
import io.mixeration.menuapi.menu.Menu;
import io.mixeration.menuapi.menu.MultiMenu;
import io.mixeration.menuapi.menu.ScrollingMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public final class MenuFactory {
    public static final ItemListener EMPTY_LISTENER = (ItemListener) MenuFactory::doNothing;

    private MenuFactory() {
    }

    private static void doNothing(Player p, ClickType type) {
    }

    public static Menu createMenu(String name, int size, boolean sticky) {
        return Menu.createMenu(name, size, sticky);
    }


    public static Menu createMenu(String name, int size, Menu parent, boolean sticky) {
        return createMenu(name, size, sticky).setParent(parent).setSticky(sticky);
    }

    public static MultiMenu createMultiMenu(String name, int size) {
        return MultiMenu.create(name, size);
    }

    public static MultiMenu createMultiMenu(String name, int size, Menu parent) {
        return (MultiMenu) createMultiMenu(name, size).setParent(parent);
    }

    public static MultiMenu createMultiMenu(String name, int size, int pages) {
        return MultiMenu.create(name, size, pages);
    }

    public static MultiMenu createMultiMenu(String name, int size, int pages, Menu parent) {
        return (MultiMenu) createMultiMenu(name, size, pages).setParent(parent);
    }

    public static ScrollingMenu createScrollingMenu(String name) {
        return ScrollingMenu.create(name);
    }

    public static Item createItem(ItemStack stack, ItemListener listener) {
        return BasicItem.create(stack, listener);
    }

    public static Item createItem(ItemListener listener, Material material, String name, String... lore) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setLore(Arrays.asList(lore));

        stack.setItemMeta(meta);
        return createItem(stack, listener);
    }

    public static void dispose(Menu menu) {
        HandlerList.unregisterAll(menu);
    }
}
