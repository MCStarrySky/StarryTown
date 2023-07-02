package com.mcstarrysky.starrytown.inventory.holder;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * @author xiaomu
 * @since 2022/9/10 13:27
 */
public interface GlobalChestExecutor extends InventoryHolder {

    void o(final InventoryOpenEvent e);

    void c(final InventoryClickEvent e);

    void e(final InventoryCloseEvent e);

    Inventory build();
}
