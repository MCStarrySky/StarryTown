package com.mcstarrysky.starrytown.util.inventory.element;

import com.mcstarrysky.starrytown.util.LocaleUtil;
import com.mcstarrysky.starrytown.util.inventory.InventoryElement;
import com.mcstarrysky.starrytown.util.inventory.PInventory;
import lombok.Builder;
import lombok.Data;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Objects;
import java.util.function.Consumer;

@Data
@Builder
public class InventoryButton implements InventoryElement {
    private final BaseElement base;
    private final Consumer<InventoryClickEvent> onClick;

    @Override
    public void click(final PInventory<?> holder, final InventoryClickEvent event) {
        LocaleUtil.debug("InventoryButton {0} 被点击了.", base.getName());
        event.setCancelled(true);
        if (Objects.isNull(onClick)) {
            LocaleUtil.debug("点击处理函数为 null.");
            return;
        }
        onClick.accept(event);
    }

    @Override
    public boolean isClickable() {
        return true;
    }
}
