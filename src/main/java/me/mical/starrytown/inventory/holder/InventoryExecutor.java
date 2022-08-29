package me.mical.starrytown.inventory.holder;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * @author xiaomu
 * @since 2022/8/28 22:59
 */
public interface InventoryExecutor extends InventoryHolder {

    default int[] getFrameSlots() {
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 50, 51, 52, 53};
    }

    void e(final InventoryClickEvent e);

    Inventory build();
}
