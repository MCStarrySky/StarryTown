package com.mcstarrysky.starrytown.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaomu
 * @since 2022/8/29 11:13
 */
public class ItemBuilder {

    private final ItemStack item;

    private ItemBuilder(final Material material) {
        this.item = new ItemStack(material, 1);
    }

    public ItemBuilder name(final String name) {
        final ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(LocaleUtil.color(name));
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(final List<String> lore) {
        final ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setLore(lore.stream().map(LocaleUtil::color).collect(Collectors.toList()));
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder amount(final int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemStack build() {
        return this.item;
    }

    public static ItemBuilder builder(final Material material) {
        return new ItemBuilder(material);
    }
}
