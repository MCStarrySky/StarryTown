package com.mcstarrysky.starrytown.util.inventory;

import lombok.NonNull;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ListIterator;
import java.util.Objects;

public interface InventoryExecutor extends InventoryHolder {

    JavaPlugin getPlugin();

    Inventory construct(final InventoryHolder executor);

    void execute(InventoryClickEvent event);

    default void open(InventoryOpenEvent event) {
    }

    default void close(InventoryCloseEvent event) {
    }

    default void refresh(@NonNull Inventory inventory) {
        Inventory inv = construct(inventory.getHolder());
        ListIterator<ItemStack> iterator = inv.iterator();
        while (iterator.hasNext()) inventory.setItem(iterator.nextIndex(), iterator.next());
    }

    default boolean check(final @NonNull InventoryExecutor executor, final @NonNull InventoryClickEvent event) {
        final Inventory clickedInv = event.getClickedInventory();
        return Objects.nonNull(clickedInv) && Objects.nonNull(clickedInv.getHolder()) && executor.equals(clickedInv.getHolder());
    }

    String name();
}
