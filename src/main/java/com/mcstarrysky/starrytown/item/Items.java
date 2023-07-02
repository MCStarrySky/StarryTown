package com.mcstarrysky.starrytown.item;

import com.mcstarrysky.starrytown.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

/**
 * @author xiaomu
 * @since 2022/8/29 11:10
 */
public class Items {

    public static ItemStack TOWN_CREATE_PAPER;

    static {
        TOWN_CREATE_PAPER = ItemBuilder.builder(Material.PAPER)
                .name("&b&l聚落创建凭证")
                .lore(Collections.singletonList("&b凭此凭证可创建聚落."))
                .build();
    }
}
