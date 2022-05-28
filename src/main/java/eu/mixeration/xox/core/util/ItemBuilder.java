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

package eu.mixeration.xox.core.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.List;

/**
 * Easy construction of ItemStacks inline.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemBuilder {

    private HashMap<ItemData, Object> metadata = new HashMap<>();

    public ItemBuilder material(Material material) {
        metadata.put(ItemData.MATERIAL, material);
        return this;
    }

    public ItemBuilder amount(int amount) {
        metadata.put(ItemData.AMOUNT, amount);
        return this;
    }

    public ItemBuilder data(short data) {
        metadata.put(ItemData.DATA, data);
        return this;
    }

    public ItemBuilder displayName(String displayName) {
        metadata.put(ItemData.DISPLAY_NAME, displayName);
        return this;
    }

    public ItemBuilder displayLore(List<String> displayLore) {
        metadata.put(ItemData.DISPLAY_LORE, displayLore);
        return this;
    }

    public ItemBuilder skullOwner(String skullOwner) {
        metadata.put(ItemData.SKULL_OWNER, skullOwner);
        return this;
    }

    public ItemBuilder glowing(boolean glowing) {
        metadata.put(ItemData.GLOWING, glowing);
        return this;
    }

    public ItemBuilder armorColour(Color color) {
        metadata.put(ItemData.ARMORCOLOR, color);
        return this;
    }

    public Material getMaterial() {
        if (!metadata.containsKey(ItemData.MATERIAL)) {
            return null;
        }

        return (Material) metadata.get(ItemData.MATERIAL);
    }

    public Integer getAmount() {
        if (!metadata.containsKey(ItemData.AMOUNT)) {
            return null;
        }

        return (int) metadata.get(ItemData.AMOUNT);
    }

    public Short getData() {
        if (!metadata.containsKey(ItemData.DATA)) {
            return null;
        }

        return (short) metadata.get(ItemData.DATA);
    }

    public String getDisplayName() {
        if (!metadata.containsKey(ItemData.DISPLAY_NAME)) {
            return null;
        }

        return (String) metadata.get(ItemData.DISPLAY_NAME);
    }

    public List<String> getDisplayLore() {
        if (!metadata.containsKey(ItemData.DISPLAY_LORE)) {
            return null;
        }

        return (List<String>) metadata.get(ItemData.DISPLAY_LORE);
    }

    public String getSkullOwner() {
        if (!metadata.containsKey(ItemData.SKULL_OWNER)) {
            return null;
        }

        return (String) metadata.get(ItemData.SKULL_OWNER);
    }

    public boolean isGlowing() {
        return metadata.containsKey(ItemData.GLOWING) && (boolean) metadata.get(ItemData.GLOWING);
    }

    public ItemStack getItem() {
        ItemStack itemStack = new ItemStack(getMaterial(), getAmount() != null ? getAmount() : 1, getData() != null ? getData() : 0);
        ItemMeta itemMeta = itemStack.getItemMeta();

        String displayName = getDisplayName();
        List<String> displayLore = getDisplayLore();
        String skullOwner = getSkullOwner();
        boolean glowing = isGlowing();

        if (displayName != null) {
            assert itemMeta != null;
            itemMeta.setDisplayName(displayName);
        }

        if (displayLore != null) {
            assert itemMeta != null;
            itemMeta.setLore(displayLore);
        }

        if (glowing) {
            assert itemMeta != null;
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }

        itemStack.setItemMeta(itemMeta);

        if (glowing) {
            itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        }

        if (skullOwner != null && itemMeta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            skullMeta.setOwner(skullOwner);
            itemStack.setItemMeta(skullMeta);
        }

        if (metadata.containsKey(ItemData.ARMORCOLOR) && itemMeta instanceof LeatherArmorMeta) {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
            assert leatherArmorMeta != null;
            leatherArmorMeta.setColor((Color) metadata.get(ItemData.ARMORCOLOR));
            itemStack.setItemMeta(leatherArmorMeta);
        }

        return itemStack;
    }

    private enum ItemData {
        MATERIAL, AMOUNT, DATA, DISPLAY_NAME, DISPLAY_LORE, SKULL_OWNER, GLOWING, ARMORCOLOR
    }
}
