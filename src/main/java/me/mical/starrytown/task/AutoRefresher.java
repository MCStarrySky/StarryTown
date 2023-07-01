package me.mical.starrytown.task;

import me.mical.starrytown.ConfigReader;
import me.mical.starrytown.StarryTown;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

/**
 * @author xiaomu
 * @since 2022/8/29 11:58
 */
public class AutoRefresher {

    public AutoRefresher() {
        Bukkit.getAsyncScheduler().runAtFixedRate(StarryTown.getInstance(), task -> {
            ConfigReader.Towns.export();
        }, 0L, 5L, TimeUnit.MINUTES);
    }
}
