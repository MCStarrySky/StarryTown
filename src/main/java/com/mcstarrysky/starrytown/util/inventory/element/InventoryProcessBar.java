package com.mcstarrysky.starrytown.util.inventory.element;

import com.mcstarrysky.starrytown.util.BasicUtil;
import com.mcstarrysky.starrytown.util.inventory.InventoryElement;
import com.mcstarrysky.starrytown.util.inventory.PInventory;
import lombok.Builder;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@Data
public class InventoryProcessBar implements InventoryElement {
    private final BaseElement base;
    private final Supplier<ItemStack> processItem;
    private final Supplier<Double> rate;
    private final Map<Integer, ItemStack> barMap = new HashMap<>();

    @Builder
    public InventoryProcessBar(BaseElement base, Supplier<ItemStack> processItem, Supplier<Double> rate) {
        this.base = base;
        this.processItem = processItem;
        this.rate = rate;
    }

    @Override
    public boolean isClickable() {
        return false;
    }

    @Override
    public BaseElement preload(PInventory<?> inv) {
        if (Objects.nonNull(this.rate) && Objects.nonNull(this.processItem)) {
            final double rate = this.rate.get();
            this.barMap.clear();
            final List<Integer> slots = getPositions();
            for (int amount = 0; amount < BasicUtil.roundToInt(slots.size() * rate); amount++) {
                this.barMap.put(slots.get(amount), this.processItem.get());
            }
        }
        return getBase();
    }

    @Override
    public ItemStack parseItem(PInventory<?> inv, int slot) {
        return this.barMap.getOrDefault(slot, getBase().getItem().get());
    }
}
