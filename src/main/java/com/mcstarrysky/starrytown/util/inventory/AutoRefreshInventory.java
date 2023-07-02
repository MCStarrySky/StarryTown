package com.mcstarrysky.starrytown.util.inventory;

import com.mcstarrysky.starrytown.util.LocaleUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;

public abstract class AutoRefreshInventory implements InventoryExecutor {

    protected final JavaPlugin plugin;
    @Getter
    protected final BaseInventory base;
    @Getter
    @Setter
    private int refreshInterval = -1;
    @Getter
    @Setter
    private BukkitTask refreshTask;

    public AutoRefreshInventory(final BaseInventory base) {
        this.base = base;
        this.plugin = this.base.plugin;
    }

    public void startRefresh(final Inventory inv) {
        if (this.refreshInterval > 0) {
            LocaleUtil.debug("{0} 刷新间隔: {1}s", name(), this.refreshInterval);
            this.refreshTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> refresh(inv), 1L, refreshInterval * 20L);
        }
    }

    public void endRefresh() {
        if (Objects.nonNull(this.refreshTask) && !this.refreshTask.isCancelled()) {
            LocaleUtil.debug("{0} 取消自动刷新任务", name());
            this.refreshTask.cancel();
        }
    }

    @Override
    public String name() {
        return base.name();
    }

    @Override
    public JavaPlugin getPlugin() {
        return this.plugin;
    }
}
