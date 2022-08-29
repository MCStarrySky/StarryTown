package me.mical.starrytown.task;

import me.mical.starrytown.ConfigReader;
import me.mical.starrytown.StarryTown;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author xiaomu
 * @since 2022/8/29 11:58
 */
public class DataTask {

    public DataTask() {
        new BukkitRunnable() {

            @Override
            public void run() {
                ConfigReader.Towns.export();
            }
        }.runTaskTimerAsynchronously(StarryTown.getInstance(), 0L, 20L * 60L * 30L);
    }
}
