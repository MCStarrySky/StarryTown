package com.mcstarrysky.starrytown.util.inventory;

import com.mcstarrysky.starrytown.util.inventory.element.BaseElement;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface InventoryElement {
    @NotNull BaseElement getBase();

    boolean isClickable();

    default int getPriority() {
        return getBase().getPriority();
    }

    default List<Integer> getPositions() {
        return getBase().getPositions();
    }

    default BaseElement preload(final PInventory<?> inv) {
        return getBase().preload(inv);
    }

    default ItemStack parseItem(final PInventory<?> inv, final int slot) {
        return getBase().parseItem(inv, slot);
    }

    default void click(final PInventory<?> holder, final InventoryClickEvent event) {
        getBase().click(holder, event);
    }
}
