package me.mical.starrytown;

import lombok.Getter;
import me.mical.starrytown.command.CommandHandler;
import me.mical.starrytown.listener.InventoryListener;
import me.mical.starrytown.listener.PlayerListener;
import me.mical.starrytown.task.DataTask;
import me.mical.starrytown.util.LocaleUtil;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class StarryTown extends JavaPlugin {

    @Getter
    private static StarryTown instance;
    public static BukkitAudiences adventure;
    @Getter
    private static Economy economy;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        adventure = BukkitAudiences.create(this);
        if (setupEconomy()) {
            LocaleUtil.send(getServer().getConsoleSender(), "已挂钩 <green>Vault<gray>.");
        } else {
            LocaleUtil.send(getServer().getConsoleSender(), "未找到 <green>Vault<gray>, 插件即将禁用.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        ConfigReader.loadConfig();
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        final PluginCommand cmd = getServer().getPluginCommand("starrytown");
        if (cmd != null) {
            cmd.setExecutor(new CommandHandler());
            cmd.setTabCompleter(new CommandHandler());
        }
        new DataTask();
        LocaleUtil.send(getServer().getConsoleSender(), "插件已经启动, 正在运行在 <green>" + getServer().getVersion() + " <gray>上, 插件版本 <green>" + getDescription().getVersion() + "<gray>.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        ConfigReader.Towns.export();
        LocaleUtil.send(getServer().getConsoleSender(), "插件已经停止.");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            final RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                return false;
            }
            economy = rsp.getProvider();
            return true;
        }
        return false;
    }
}
