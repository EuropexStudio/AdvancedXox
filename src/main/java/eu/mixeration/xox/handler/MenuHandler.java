package eu.mixeration.xox.handler;

import eu.mixeration.xox.PluginMain;
import eu.mixeration.xox.core.objects.Game;
import eu.mixeration.xox.core.plugin.Locale;
import io.mixeration.menuapi.MenuFactory;
import io.mixeration.menuapi.items.Item;
import io.mixeration.menuapi.menu.Menu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class MenuHandler {
    private final PluginMain plugin;

    public MenuHandler() {
        this.plugin = PluginMain.getInstance();
    }

    public void createColourSelectionMenu(Player player) {
        Menu menu = MenuFactory.createMenu(Locale.MENU_COLOURSELECTION_TITLE, 9, true);

        int i = 0;
        for (ItemStack playerItem : ItemHandler.getAllPlayerItems()) {
            menu.setItem(i, new Item() {
                @Override
                public ItemStack stack() {
                    return playerItem;
                }

                @Override
                public void act(Player player, ClickType clickType) {
                    Game game = plugin.getGameHandler().getGameForPlayer(player);

                    if (game != null) {
                        ItemStack otherPlayer;
                        if (game.getPlayer1Id() == player.getUniqueId()) {
                            otherPlayer = game.getPlayer2Item();
                        } else {
                            otherPlayer = game.getPlayer1Item();
                        }

                        if (otherPlayer != null) {
                            if (otherPlayer.getType() == playerItem.getType()) {
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);

                                Locale.sendMessage(player, Locale.ERROR_COLOUR_SELECT_IN_USE);
                                return;
                            }
                        }

                        if (game.getPlayer1Id() == player.getUniqueId()) {
                            game.setPlayer1Item(playerItem);
                        } else {
                            game.setPlayer2Item(playerItem);
                        }

                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                        Locale.sendMessage(player, Locale.SUCCESS_COLOUR_SELECTED);
                    }

                    player.closeInventory();
                }
            });

            i++;
        }

        menu.showTo(player);
    }
}
