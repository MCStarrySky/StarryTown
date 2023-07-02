package com.mcstarrysky.starrytown.util;

import com.mcstarrysky.starrytown.ConfigReader;
import com.mcstarrysky.starrytown.StarryTown;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;

/**
 * @author xiaomu
 * @since 2022/8/28 21:53
 */
public class LocaleUtil {

    public static final String PREFIX = "[ <gradient:#08AEEA:#2AF598>StarryTown</gradient> ] ";

    @NotNull
    public static String color(@Nullable final String msg) {
        if (msg == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    /**
     * 快速发送带有前缀的文本信息.
     * @param source 对象
     * @param msg 内容
     * @param args 变量
     */
    public static void send(@Nullable final CommandSender source, @Nullable final String msg, final Object... args) {
        sendWithoutPrefix(source, PREFIX + msg, args);
    }

    public static void sendToConsole(@Nullable final String msg, final Object... args) {
        send(Bukkit.getConsoleSender(), msg, args);
    }

    public static void sendWithoutPrefix(@Nullable final CommandSender source, @Nullable final String msg, final Object... args) {
        if (source == null) {
            return;
        }
        final Audience user = StarryTown.adventure.sender(source);
        user.sendMessage(MiniMessage.miniMessage().deserialize(MessageFormat.format(msg == null ? "" : msg, args)));
    }

    public static void debug(@Nullable final String msg, final Object... args) {
        if (ConfigReader.Config.DEBUG) {
            sendWithoutPrefix(Bukkit.getConsoleSender(), PREFIX + "(DEBUG) " + msg, args);
        }
    }
}
