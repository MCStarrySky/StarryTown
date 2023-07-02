package com.mcstarrysky.starrytown.util.inventory.element;

import com.mcstarrysky.starrytown.util.inventory.InventoryElement;
import com.mcstarrysky.starrytown.util.inventory.PInventory;
import lombok.Builder;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.function.BiConsumer;

@Data
@Builder
public class InventoryItemButton implements InventoryElement {

    private final BaseElement base;
    private final BiConsumer<InventoryClickEvent, ItemStack> onClick;

    @Override
    public boolean isClickable() {
        return true;
    }

    @Override
    public void click(PInventory<?> holder, InventoryClickEvent event) {
        event.setCancelled(true);

        final InventoryAction action = event.getAction();
        if (!getBase().getItem().get().getType().equals(Material.AIR)) {
            if (action != InventoryAction.SWAP_WITH_CURSOR) {
                return;
            }
        } else {
            if (action != InventoryAction.PLACE_ALL) {
                return;
            }
        }

        if (Objects.isNull(this.onClick)) {
            return;
        }

        final ItemStack cursor = event.getCursor();
        if (Objects.isNull(cursor)) {
            return;
        }

        final Player user = (Player) event.getWhoClicked();
        final ItemStack item = cursor.clone();
        Bukkit.getScheduler().runTask(holder.getPlugin(), () -> {
            event.getView().setCursor(null);
            user.getInventory().addItem(item);

            this.onClick.accept(event, item);
        });
    }
}
