package com.mcstarrysky.starrytown.util.inventory;

import com.mcstarrysky.starrytown.util.LocaleUtil;
import lombok.Getter;
import com.mcstarrysky.starrytown.util.inventory.element.BaseElement;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public abstract class PInventory<T> extends AutoRefreshInventory {

    @Getter
    protected final T data;
    @Getter
    private final Map<String, InventoryElement> elements = new HashMap<>();
    @Getter
    private final Map<Integer, InventoryElement> slotMap = new HashMap<>();
    protected Inventory inventory;

    public PInventory(JavaPlugin plugin, T data, Player user, File file) {
        super(new FileDefinedInventory(plugin, user, file));
        this.data = data;
    }

    public PInventory(JavaPlugin plugin, T data, Player user, String title, int row) {
        super(new BaseInventory(plugin, user, title, row));
        this.data = data;
    }

    @Override
    public @NotNull Inventory getInventory() {
        if (Objects.isNull(this.inventory)) {
            this.inventory = construct(this);
        }
        return this.inventory;
    }

    @Override
    public Inventory construct(final InventoryHolder executor) {
        final Inventory result = base.construct(this);

        final List<InventoryElement> elements = new ArrayList<>(this.elements.values());
        elements.sort(Comparator.comparingInt(InventoryElement::getPriority));

        for (InventoryElement element : elements) {
            final BaseElement base = element.preload(this);

            if (!base.condition(super.base.viewer)) {
                continue;
            }

            for (int slot : element.getPositions()) {
                this.slotMap.put(slot, element);
                result.setItem(slot, element.parseItem(this, slot));
            }
        }

        return result;
    }

    @Override
    public void open(InventoryOpenEvent event) {
        startRefresh(event.getInventory());
        final Sound sound = base.getSetting("Sound.Open", Sound.class);
        if (Objects.nonNull(sound)) {
            final Player user = base.getViewer();
            user.playSound(user.getLocation(), sound, 1, 1);
        }
    }

    @Override
    public void close(InventoryCloseEvent event) {
        endRefresh();
        final Sound sound = base.getSetting("Sound.Close", Sound.class);
        if (Objects.nonNull(sound)) {
            final Player user = base.getViewer();
            user.playSound(user.getLocation(), sound, 1, 1);
        }
    }

    public void onShiftMove(final InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void execute(InventoryClickEvent event) {
        final InventoryAction action = event.getAction();
        if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            LocaleUtil.debug("尝试发射物品到 Inventory 中.");
            onShiftMove(event);
            return;
        }

        if (!check(this, event)) {
            LocaleUtil.debug("被点击位置非该 Gui.");
            return;
        }

        final InventoryElement element = getElement(event.getSlot());
        if (Objects.isNull(element) || !element.isClickable()) {
            LocaleUtil.debug("被点击槽位没有没有元素或者元素不可点击: {0}.", element);
            event.setCancelled(true);
            return;
        }

        LocaleUtil.debug("被点击槽位内的元素名为 {0}.", element.getBase().getName());
        element.click(this, event);
    }

    protected void addElement(InventoryElement element) {
        this.elements.put(element.getBase().getName(), element);
    }

    public InventoryElement getElement(String name) {
        return this.elements.get(name);
    }

    public InventoryElement getElement(int slot) {
        return this.slotMap.get(slot);
    }
}
