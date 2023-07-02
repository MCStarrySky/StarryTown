package com.mcstarrysky.starrytown.listener;

import com.mcstarrysky.starrytown.data.Cache;
import com.mcstarrysky.starrytown.util.LocaleUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author xiaomu
 * @since 2022/8/29 13:28
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void e(final PlayerJoinEvent e) {
        if (Cache.SEND_CACHE.containsKey(e.getPlayer().getUniqueId())) {
            Cache.SEND_CACHE.get(e.getPlayer().getUniqueId()).forEach(msg -> LocaleUtil.send(e.getPlayer(), msg));
            Cache.SEND_CACHE.remove(e.getPlayer().getUniqueId());
        }
    }
}
