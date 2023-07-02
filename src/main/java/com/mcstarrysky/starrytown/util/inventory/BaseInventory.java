package com.mcstarrysky.starrytown.util.inventory;

import com.mcstarrysky.starrytown.util.LocaleUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class BaseInventory implements InventoryExecutor {

    @Getter
    protected final Player viewer;
    protected final JavaPlugin plugin;
    @Getter
    protected final Map<String, Object> settings = new HashMap<>();
    protected Inventory inventory;


    public BaseInventory(JavaPlugin plugin, Player user, String title, int row) {
        this.plugin = plugin;
        this.viewer = user;
        this.settings.put("title", title);
        this.settings.put("row", row);
    }

    @Override
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public Inventory construct(final InventoryHolder executor) {
        return Bukkit.createInventory(executor, getRow() * 9, LocaleUtil.color(getTitle()));
    }

    @Override
    public void execute(InventoryClickEvent event) {
    }

    @Override
    public String name() {
        return "Gui/" + getTitle();
    }

    @Override
    public @NotNull Inventory getInventory() {
        if (Objects.isNull(this.inventory)) {
            this.inventory = construct(this);
        }
        return this.inventory;
    }

    public void addSetting(final String key, final Object value) {
        this.settings.put(key, value);
    }

    public <T> T getSetting(final String key, final Class<?> clazz) {
        return this.settings.containsKey(key) ? (T) clazz.cast(this.settings.get(key)) : null;
    }

    protected String getTitle() {
        return (String) this.settings.getOrDefault("title", "ParrotX 未初始化 Gui");
    }

    protected int getRow() {
        return (int) this.settings.getOrDefault("row", 1);
    }
}
