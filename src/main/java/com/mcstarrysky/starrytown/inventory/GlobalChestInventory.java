package com.mcstarrysky.starrytown.inventory;

import com.mcstarrysky.languageutils.LanguageUtils;
import com.mcstarrysky.starrytown.data.Town;
import com.mcstarrysky.starrytown.inventory.holder.GlobalChestExecutor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author xiaomu
 * @since 2022/9/10 13:32
 */
public class GlobalChestInventory implements GlobalChestExecutor {

    private final Town town;
    private final Inventory inventory;

    private final List<ItemStack> origin = new ArrayList<>();

    public GlobalChestInventory(final Town town) {
        this.town = town;
        this.inventory = build();
    }

    @Override
    public void o(InventoryOpenEvent e) {
        origin.addAll(Arrays.stream(inventory.getContents())
                .filter(Objects::nonNull)
                .toList());
    }

    @Override
    public void c(InventoryClickEvent e) {
        // 获取玩家点击的 Inventory
        Inventory clickedInventory = e.getClickedInventory();
        if (clickedInventory == null) {
            return;
        }

        final Player p = (Player) e.getWhoClicked();

        // 判断是否为对比的 Inventory
        if (!clickedInventory.equals(inventory)) {
            return;
        }

        // event.setCancelled(true); // 取消点击事件

        // 获取玩家背包的 Inventory
        Inventory playerInventory = p.getInventory();

        // 比对两个 Inventory 并获取差异
        List<InventoryDifference> differences = compareInventories(clickedInventory, playerInventory);

        // 打印添加、减少和替换的物品
        for (InventoryDifference difference : differences) {
            int slot = difference.getSlot();
            ItemStack item = difference.getItem();
            ChangeType changeType = difference.getChangeType();
            int count = difference.getCount();

            final String i18n = LanguageUtils.getName(p, item);
            final String name = item.getItemMeta() != null ? item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : i18n : i18n;

            switch (changeType) {
                case ADDED -> town.log(p, p.getName() + " 放入了 " + name + " * " + count);
                case REMOVED -> town.log(p, p.getName() + " 取出了 " + name + " * " + count);
                case REPLACED -> town.log(p, p.getName() + " 替换了 " + name + " * " + count);
            }
        }
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

        // 记录日志
        final List<ItemStack> now = Arrays.stream(inventory.getContents())
                .filter(Objects::nonNull)
                .toList();
        // 原来有, 现在没有, 即为取出
        final List<ItemStack> take = origin.stream()
                .filter(i -> !now.contains(i))
                .toList();
        // 原来没有, 现在有, 即为
        final List<ItemStack> add = now.stream()
                .filter(i -> !origin.contains(i))
                .toList();
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

    private List<InventoryDifference> compareInventories(Inventory inventory1, Inventory inventory2) {
        List<InventoryDifference> differences = new ArrayList<>();

        for (int slot = 0; slot < inventory1.getSize(); slot++) {
            ItemStack item1 = inventory1.getItem(slot);
            ItemStack item2 = inventory2.getItem(slot);

            // 判断物品是否相同
            if (item1 == null && item2 != null) {
                differences.add(new InventoryDifference(slot, item2, ChangeType.ADDED, item2.getAmount()));
            } else if (item1 != null && item2 == null && item1.getType() != org.bukkit.Material.AIR) {
                differences.add(new InventoryDifference(slot, item1, ChangeType.REMOVED, item1.getAmount()));
            } else if (item1 != null && item2 != null && item1.getType() != item2.getType()) {
                differences.add(new InventoryDifference(slot, item2, ChangeType.REPLACED));
            }
        }

        return differences;
    }

    class InventoryDifference {
        private int slot;
        private ItemStack item;
        private ChangeType changeType;
        private int count;

        public InventoryDifference(int slot, ItemStack item, ChangeType changeType) {
            this.slot = slot;
            this.item = item;
            this.changeType = changeType;
            this.count = 0;
        }

        public InventoryDifference(int slot, ItemStack item, ChangeType changeType, int count) {
            this.slot = slot;
            this.item = item;
            this.changeType = changeType;
            this.count = count;
        }

        public int getSlot() {
            return slot;
        }

        public ItemStack getItem() {
            return item;
        }

        public ChangeType getChangeType() {
            return changeType;
        }

        public int getCount() {
            return count;
        }
    }

    enum ChangeType {
        ADDED,
        REMOVED,
        REPLACED
    }
}
