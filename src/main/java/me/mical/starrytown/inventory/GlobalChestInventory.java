package me.mical.starrytown.inventory;

import me.mical.starrytown.data.Town;
import me.mical.starrytown.inventory.holder.GlobalChestExecutor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaomu
 * @since 2022/9/10 13:32
 */
public class GlobalChestInventory implements GlobalChestExecutor {

    private final Town town;
    private final Inventory inventory;

    public GlobalChestInventory(final Town town) {
        this.town = town;
        this.inventory = build();
    }

    @Override
    public void e(InventoryCloseEvent e) {
        final Map<Integer, ItemStack> items = new HashMap<>();
        for (int i = 0; i < e.getInventory().getSize(); i++) {
            final ItemStack item = e.getInventory().getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                items.put(i, item);
            }
        }
        town.setItems(items);
    }

    @Override
    public Inventory build() {
        final Inventory inv = Bukkit.createInventory(this, 6 * 9, "聚落 " + town.getName() + " 的公共箱子");
        town.getItems().forEach(inv::setItem);
        return inv;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
