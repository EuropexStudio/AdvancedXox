
package io.mixeration.menuapi.menu;

import io.mixeration.menuapi.items.Item;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class Menu implements Listener {
    private static final JavaPlugin OWNER = JavaPlugin.getProvidingPlugin(Menu.class);
    protected Map<Integer, Item> items = new HashMap<>(); // map for quick lookup
    private String name;
    private int size;
    private Inventory inventory;
    private Menu parent;
    private boolean stickyMenu = false;

    protected Menu(String name, int size, boolean sticky) { // allow for sub classes
        this.name = ChatColor.translateAlternateColorCodes('&', name);
        this.size = size;
        this.stickyMenu = sticky;

        this.inventory = Bukkit.createInventory(null, size, this.name);
        Bukkit.getPluginManager().registerEvents(this, OWNER);
    }

    public static Menu createMenu(String name, int size, boolean sticky) {
        return new Menu(name, size, sticky);
    }

    public String name() {
        return name;
    }

    public int size() {
        return size;
    }

    public Inventory inventory() {
        return inventory;
    }


    public Item itemAt(int index) {
        return items.get(index);
    }

    public Item itemAt(int x, int z) {
        return items.get(z * 9 + x);
    }

    public Menu setItem(int index, Item item) {
        if (item == null) {
            inventory.setItem(index, null);
        } else {
            inventory.setItem(index, item.stack());
        }

        items.put(index, item);
        return this;
    }

    public Menu setItem(int x, int z, Item item) {
        return setItem(z * 9 + x, item);
    }

    /**
     * Sets the parent of the menu, used when the player exits the menu
     */
    public Menu setParent(Menu parent) {
        this.parent = parent;
        return this;
    }

    public void showTo(Player... players) {
        for (Player p : players) {
            p.openInventory(inventory);
        }
    }

    @EventHandler
    public void onExit(InventoryCloseEvent event) {
        if (!event.getInventory().equals(inventory))
            return;

        if (parent != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getPlayer().openInventory(parent.inventory);
                }
            }.runTaskLater(OWNER, 2L);
        } else if (stickyMenu) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getPlayer().openInventory(inventory);
                }
            }.runTaskLater(OWNER, 2L);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inventory))
            return;

        if (event.getRawSlot() >= size && !event.getClick().isShiftClick())
            return;

        event.setCancelled(true);

        if (!items.containsKey(event.getSlot())) {
            return;
        }

        stickyMenu = false;
        items.get(event.getSlot()).act((Player) event.getWhoClicked(), event.getClick());
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!event.getInventory().equals(inventory))
            return;

        event.setCancelled(true);
    }

    public Menu setSticky(Boolean sticky) {
        this.stickyMenu = sticky;
        return this;
    }
}
