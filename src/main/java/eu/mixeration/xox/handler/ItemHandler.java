package eu.mixeration.xox.handler;

import eu.mixeration.xox.core.plugin.config.Message_Config;
import eu.mixeration.xox.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
public class ItemHandler {
    private static final String ITEM_TAG_STRING = Message_Config.getConfig().getString("messages.item-tag-string");
    private static final List<String> ITEM_TAG_LIST = Collections.singletonList(ITEM_TAG_STRING);
    public static final ItemStack ITEM_GAME_JOIN = new ItemBuilder().material(Material.SLIME_BALL).displayName(Message_Config.getConfig().getString("messages.right-click-to-join")).displayLore(ITEM_TAG_LIST).getItem();
    private static final ItemStack ITEM_PLAYER_WHITE = new ItemBuilder().material(Material.WHITE_WOOL).displayLore(ITEM_TAG_LIST).getItem();
    private static final ItemStack ITEM_PLAYER_ORANGE = new ItemBuilder().material(Material.ORANGE_WOOL).displayLore(ITEM_TAG_LIST).getItem();
    private static final ItemStack ITEM_PLAYER_MAGENTA = new ItemBuilder().material(Material.MAGENTA_WOOL).displayLore(ITEM_TAG_LIST).getItem();
    private static final ItemStack ITEM_PLAYER_CYAN = new ItemBuilder().material(Material.CYAN_WOOL).displayLore(ITEM_TAG_LIST).getItem();
    private static final ItemStack ITEM_PLAYER_YELLOW = new ItemBuilder().material(Material.YELLOW_WOOL).displayLore(ITEM_TAG_LIST).getItem();
    private static final ItemStack ITEM_PLAYER_LIME = new ItemBuilder().material(Material.LIME_WOOL).displayLore(ITEM_TAG_LIST).getItem();
    private static final ItemStack ITEM_PLAYER_PINK = new ItemBuilder().material(Material.PINK_WOOL).displayLore(ITEM_TAG_LIST).getItem();
    private static final ItemStack ITEM_PLAYER_RED = new ItemBuilder().material(Material.RED_WOOL).displayLore(ITEM_TAG_LIST).getItem();
    private static final ItemStack ITEM_PLAYER_BLUE = new ItemBuilder().material(Material.BLUE_WOOL).displayLore(ITEM_TAG_LIST).getItem();

    public static boolean isTicTacToeItem(ItemStack itemStack) {
        if (itemStack != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();

            if (itemMeta != null) {
                List<String> lore = itemMeta.getLore();

                return lore != null && !lore.isEmpty() && lore.get(0).equals(ITEM_TAG_STRING);
            }
        }

        return false;
    }

    public static List<ItemStack> getAllPlayerItems() {
        return Arrays.asList(ITEM_PLAYER_WHITE.clone(), ITEM_PLAYER_ORANGE.clone(), ITEM_PLAYER_MAGENTA.clone(), ITEM_PLAYER_CYAN.clone(), ITEM_PLAYER_YELLOW.clone(), ITEM_PLAYER_LIME.clone(), ITEM_PLAYER_PINK.clone(), ITEM_PLAYER_RED.clone(), ITEM_PLAYER_BLUE.clone());
    }
}
