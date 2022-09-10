package me.mical.starrytown.listener;

import me.mical.starrytown.inventory.holder.GlobalChestExecutor;
import me.mical.starrytown.inventory.holder.InventoryExecutor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.InventoryHolder;

/**
 * @author xiaomu
 * @since 2022/8/28 23:33
 */
public class InventoryListener implements Listener {

    @EventHandler
    public void e(final InventoryClickEvent e) {
        final InventoryHolder holder = e.getInventory().getHolder();
        if (holder instanceof InventoryExecutor) {
            e.setCancelled(true);
            ((InventoryExecutor) holder).e(e);
        }
    }

    @EventHandler
    public void e(final InventoryMoveItemEvent e) {
        final InventoryHolder holder = e.getDestination().getHolder();
        if (holder instanceof InventoryExecutor) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void e(final InventoryCloseEvent e) {
        final InventoryHolder holder = e.getInventory().getHolder();
        if (holder instanceof GlobalChestExecutor) {
            ((GlobalChestExecutor) holder).e(e);
        }
    }
}
