package com.mcstarrysky.starrytown.util.inventory.element;

import com.mcstarrysky.starrytown.util.inventory.InventoryElement;
import com.mcstarrysky.starrytown.util.inventory.PInventory;
import lombok.Builder;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@Data
public class InventoryAmountBar implements InventoryElement {
    private final BaseElement base;
    private final Supplier<ItemStack> processItem;
    private final Supplier<Integer> amount;
    private final boolean ignoreLimit;
    private final Map<Integer, ItemStack> barMap = new HashMap<>();

    @Builder
    public InventoryAmountBar(BaseElement base, Supplier<ItemStack> processItem, Supplier<Integer> amount,
                              boolean ignoreLimit) {
        this.base = base;
        this.processItem = processItem;
        this.amount = amount;
        this.ignoreLimit = ignoreLimit;
    }

    @Override
    public boolean isClickable() {
        return false;
    }

    @Override
    public BaseElement preload(PInventory<?> inv) {
        if (Objects.nonNull(this.amount) && Objects.nonNull(this.processItem)) {
            this.barMap.clear();
            int left = this.amount.get();

            final ItemStack item = this.processItem.get();
            final Material material = item.getType();
            final int max = ignoreLimit ? 99 : material.getMaxStackSize();

            for (final int position : getPositions()) {
                if (left <= 0) {
                    break;
                }

                final ItemStack result = item.clone();
                result.setAmount(Math.min(left, max));
                this.barMap.put(position, result);

                left -= max;
            }
        }

        return getBase();
    }

    @Override
    public ItemStack parseItem(PInventory<?> inv, int slot) {
        return this.barMap.getOrDefault(slot, getBase().getItem().get());
    }
}
