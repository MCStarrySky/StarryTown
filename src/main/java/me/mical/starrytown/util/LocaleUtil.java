package me.mical.starrytown.util;

import me.mical.starrytown.StarryTown;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author xiaomu
 * @since 2022/8/28 21:53
 */
public class LocaleUtil {

    public static final String PREFIX = "[ <gradient:#08AEEA:#2AF598>StarryTown</gradient> ] ";

    public static String color(final String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    /**
     * 快速发送带有前缀的文本信息.
     * @param source 对象
     * @param msg 内容
     */
    public static void send(final CommandSender source, final String msg) {
        final Audience user = StarryTown.adventure.sender(source);
        user.sendMessage(MiniMessage.miniMessage().deserialize(PREFIX + msg));
    }

    public static void debug(final String msg) {
        send(Bukkit.getConsoleSender(), "[DEBUG] " + msg);
    }
}
