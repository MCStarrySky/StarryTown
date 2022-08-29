package me.mical.starrytown.data;

import org.bukkit.OfflinePlayer;

import java.util.*;

/**
 * @author xiaomu
 * @since 2022/8/29 00:44
 */
public class Cache {

    public static final Map<UUID, UUID> REMOVE_CACHE = new HashMap<>();
    public static final Map<UUID, List<String>> SEND_CACHE = new HashMap<>();

    public static void send(final OfflinePlayer player, final String msg) {
        List<String> msgs = new ArrayList<>();
        if (SEND_CACHE.containsKey(player.getUniqueId())) {
            msgs = SEND_CACHE.get(player.getUniqueId());
        }
        msgs.add(msg);
        SEND_CACHE.put(player.getUniqueId(), msgs);
    }
}
