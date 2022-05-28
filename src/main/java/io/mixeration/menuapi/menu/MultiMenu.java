
package io.mixeration.menuapi.menu;

import io.mixeration.menuapi.MenuFactory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class MultiMenu extends Menu {
    private static final Menu EMPTY_MENU = MenuFactory.createMenu("Empty Menu", 18, false);
    protected Menu[] menus;
    private ItemStack next = new ItemStack(Material.ARROW);
    private ItemStack back = new ItemStack(Material.RED_BED);

    protected MultiMenu(String name, int size) {
        this(name, size, 5);
    }

    protected MultiMenu(String name, int size, int pages) {
        super(name, size, false);

        menus = new Menu[pages];

        menus[0] = this; // this menu is the front page
        setName(next, "&3Next");
        setName(back, "&3Back");

        for (int i = 1; i < pages; i++) {
            menus[i] = EMPTY_MENU;
        }

        updateMenus();
    }

    public static MultiMenu create(String name, int size) {
        return new MultiMenu(name, size);
    }

    public static MultiMenu create(String name, int size, int pages) {
        return new MultiMenu(name, size, pages);
    }

    static void setName(ItemStack stack, String name) {
        ItemMeta meta = stack.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        stack.setItemMeta(meta);
    }

    public void setMenu(int index, Menu menu) {
        if (menus.length <= index && menus.length != (index - 1)) {
            resizeMenus(index - (menus.length - 1));
        }

        menus[index] = menu;
        updateMenus();

        if (menu != this) { // compare references
            menu.setParent(this);
        }
    }

    private void updateMenus() {
        for (int index = 0; index < menus.length; ++index) {
            Menu menu = menuAt(index);
            int indexSize = menu.size() - 1;

            Menu next = (menus.length == (index + 1)) ? menu : menus[index + 1];
            Menu back = (index == 0) ? menu : menus[index - 1];

            menu.setItem(indexSize, MenuFactory.createItem(this.next,
                    (p, c) -> next.showTo(p)));
            menu.setItem(indexSize - 8, MenuFactory.createItem(this.back,
                    (p, c) -> back.showTo(p)));
        }
    }

    public Menu menuAt(int index) {
        return menus[index];
    }

    public ItemStack nextItem() {
        return next;
    }

    public ItemStack backItem() {
        return back;
    }

    public void setNext(ItemStack next) {
        this.next = next;
    }

    public void setBack(ItemStack back) {
        this.back = back;
    }

    private void resizeMenus(int amount) {
        Menu[] menus = new Menu[(this.menus.length + amount)];

        System.arraycopy(this.menus, 0, menus, 0, this.menus.length);

        for (int i = this.menus.length; i < menus.length; i++) {
            menus[i] = EMPTY_MENU;
        }

        this.menus = menus;
    }
}
