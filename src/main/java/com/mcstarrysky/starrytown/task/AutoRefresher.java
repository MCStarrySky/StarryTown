package com.mcstarrysky.starrytown.task;

import com.mcstarrysky.starrytown.ConfigReader;
import com.mcstarrysky.starrytown.StarryTown;
import org.bukkit.Bukkit;

import java.util.concurrent.TimeUnit;

/**
 * @author xiaomu
 * @since 2022/8/29 11:58
 */
public class AutoRefresher {

    public AutoRefresher() {
        Bukkit.getAsyncScheduler().runAtFixedRate(StarryTown.getInstance(), task -> {
            ConfigReader.export();
        }, 0L, 5L, TimeUnit.MINUTES);
    }
}
