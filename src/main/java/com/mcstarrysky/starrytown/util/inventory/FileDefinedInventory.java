package com.mcstarrysky.starrytown.util.inventory;

import com.mcstarrysky.starrytown.util.LocaleUtil;
import lombok.Getter;
import lombok.NonNull;
import com.mcstarrysky.starrytown.util.FileUtil;
import com.mcstarrysky.starrytown.util.inventory.element.BaseElement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public class FileDefinedInventory extends BaseInventory implements FileSaved {

    protected File file;
    @Getter
    private final Map<String, ConfigurationSection> itemMap = new HashMap<>();
    @Getter
    protected FileConfiguration data;
    @Getter
    protected ConfigurationSection items;

    public FileDefinedInventory(JavaPlugin plugin, Player user, File file) {
        super(plugin, user, null, 6);
        setFile(file);
        load(file);
    }

    @Override
    public File getFile() {
        return this.file;
    }

    @Override
    public void setFile(@NonNull File file) {
        this.file = file;
    }

    @Override
    public void load(@NonNull File file) {
        this.data = YamlConfiguration.loadConfiguration(this.file);
        this.items = this.data.getConfigurationSection("Items");

        final ConfigurationSection settings = this.data.getConfigurationSection("Settings");
        if (Objects.isNull(settings)) {
            LocaleUtil.sendToConsole("构建 {0} 时遇到错误(Gui 设置配置节为 null).", name());
            addSetting("title", "未初始化 Gui - " + file.getName());
            addSetting("row", 6);
        } else for (final String key : settings.getKeys(true)) {
            final Object value = settings.get(key);
            LocaleUtil.debug("加载 Gui {0} 设置: {1} -> {2}", name(), key, value);
            addSetting(key.toLowerCase(), value);
        }

        this.items.getKeys(false).forEach(key -> this.itemMap.put(key, items.getConfigurationSection(key)));
    }

    @Override
    public String getFilename() {
        return FileUtil.getName(file, true);
    }

    @Override
    public String name() {
        return "Gui/" + getFilename();
    }

    public BaseElement get(final String key, final int priority, final Predicate<Player> condition) {
        return BaseElement.of(plugin, items.getConfigurationSection(key), priority, condition);
    }

    public BaseElement get(final String key, final int priority) {
        return get(key, priority, user -> true);
    }
}
