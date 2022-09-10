package me.mical.starrytown;

import me.mical.starrytown.data.Invitation;
import me.mical.starrytown.data.Member;
import me.mical.starrytown.data.Town;
import me.mical.starrytown.util.ItemUtil;
import me.mical.starrytown.util.LocaleUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xiaomu
 * @since 2022/8/28 22:09
 */
public class ConfigReader {

    public static final Map<UUID, Town> TOWNS = new HashMap<>();

    public static YamlConfiguration getConfig(final String name) {
        final File file = new File(StarryTown.getInstance().getDataFolder(), name);
        if (!file.exists()) {
            StarryTown.getInstance().saveResource(name, true);
            LocaleUtil.send(Bukkit.getConsoleSender(), "已自动生成并准备加载配置文件 " + name);
            return YamlConfiguration.loadConfiguration(file);
        }
        LocaleUtil.send(Bukkit.getConsoleSender(), "加载配置文件 " + name);
        return YamlConfiguration.loadConfiguration(file);
    }

    public static List<File> getData(final String name) {
        final File folder = new File(StarryTown.getInstance().getDataFolder(), name);
        final List<File> result = new ArrayList<>();
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                LocaleUtil.send(Bukkit.getConsoleSender(), "已自动生成数据文件夹 " + name);
            } else {
                LocaleUtil.send(Bukkit.getConsoleSender(), "自动生成数据文件夹 " + name + "失败.");
            }
            return result;
        }
        final File[] files = folder.listFiles(file -> file.getName().endsWith(".yml"));
        if (files != null) {
            Collections.addAll(result, files);
        }
        return result;
    }

    public static void reloadConfig() {
        Config.load();
    }

    public static void loadConfig() {
        Config.load();
        Towns.load();
    }

    public static class Config {

        public static boolean DEBUG = false;

        public static void load() {
            final YamlConfiguration config = getConfig("config.yml");
            DEBUG = config.getBoolean("Debug", false);
        }
    }

    public static class Towns {

        public static void load() {
            TOWNS.clear();
            final List<File> files = getData("towns");
            for (final File file : files) {
                final YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
                if (!(data.contains("Name") && data.contains("Owner") && data.contains("Timestamp") && data.contains("Economy") && data.contains("Member"))) {
                    continue;
                }
                final String name = data.getString("Name");
                final UUID uuid = UUID.fromString(file.getName().replace(".yml", ""));
                final UUID owner = UUID.fromString(Objects.requireNonNull(data.getString("Owner")));
                final long timestamp = data.getLong("Timestamp");
                final double economy = data.getDouble("Economy");
                final String residence = data.getString("Residence", null);
                final List<Member> member = new ArrayList<>();
                final List<Invitation> invitation = new ArrayList<>();
                for (final String key : Objects.requireNonNull(data.getConfigurationSection("Member")).getKeys(false)) {
                    final ConfigurationSection section = data.getConfigurationSection("Member." + key);
                    assert section != null;
                    if (!section.contains("Timestamp")) {
                        continue;
                    }
                    final UUID player;
                    try {
                        player = UUID.fromString(key);
                    } catch (final IllegalArgumentException ignored) {
                        continue;
                    }
                    if (Bukkit.getOnlinePlayers().stream().noneMatch(offlinePlayer -> offlinePlayer.getUniqueId().equals(player))) {
                        continue;
                    }
                    final long joinTimestamp = section.getLong("Timestamp");
                    member.add(Member.builder().player(player).timestamp(joinTimestamp).build());

                }
                if (data.getConfigurationSection("Invitation") != null) {
                    for (final String key : Objects.requireNonNull(data.getConfigurationSection("Invitation")).getKeys(false)) {
                        final ConfigurationSection section = data.getConfigurationSection("Invitation." + key);
                        assert section != null;
                        if (!section.contains("Timestamp")) {
                            continue;
                        }
                        final UUID player;
                        try {
                            player = UUID.fromString(key);
                        } catch (final IllegalArgumentException ignored) {
                            continue;
                        }
                        if (Bukkit.getOnlinePlayers().stream().noneMatch(offlinePlayer -> offlinePlayer.getUniqueId().equals(player))) {
                            continue;
                        }
                        final String reason = section.getString("Reason", "这个人没有说明原因.");
                        final long time = section.getLong("Timestamp");
                        invitation.add(Invitation.builder().player(player).reason(reason).timestamp(time).build());
                    }
                }
                final Map<Integer, ItemStack> items = new HashMap<>();
                if (data.getConfigurationSection("Items") != null) {
                    for (final String key : Objects.requireNonNull(data.getConfigurationSection("Items")).getKeys(false)) {
                        try {
                            final String content = data.getString("Items." + key);
                            final int slot = Integer.parseInt(key);
                            final ItemStack stack = ItemUtil.deserializeItems(content)[0];
                            items.put(slot, stack);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                }
                TOWNS.put(uuid, Town.builder()
                        .name(name)
                        .uuid(uuid)
                        .owner(owner)
                        .timestamp(timestamp)
                        .economy(economy)
                        .residence(residence)
                        .member(member)
                        .invitation(invitation)
                        .items(items)
                        .build());
            }
            if (TOWNS.size() > 0) {
                LocaleUtil.send(Bukkit.getConsoleSender(), "加载了 " + TOWNS.size() + " 个聚落.");
            }
        }

        public static void export() {
            getData("towns").stream().filter(file -> !TOWNS.containsKey(UUID.fromString(file.getName().replace(".yml", "")))).forEach(file -> {
                if (file.delete()) {
                    LocaleUtil.debug("已删除小组 " + file.getName() + ".");
                } else {
                    LocaleUtil.debug("删除小组 " + file.getName() + " 失败.");
                }
            });
            TOWNS.values().forEach(town -> {
                final File file;
                if (getData("towns").stream().map(File::getName).anyMatch(name -> name.equalsIgnoreCase(town.getUuid().toString()))) {
                    file = getData("towns").stream().filter(file0 -> file0.getName().replace(".yml", "").equalsIgnoreCase(town.getUuid().toString())).collect(Collectors.toList()).get(0);
                } else {
                    file = new File(new File(StarryTown.getInstance().getDataFolder(), "towns"), town.getUuid().toString() + ".yml");
                }
                final YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
                data.getKeys(false).forEach(key -> data.set(key, null));
                data.set("Name", town.getName());
                data.set("Owner", town.getOwner().toString());
                data.set("Timestamp", town.getTimestamp());
                data.set("Economy", town.getEconomy());
                data.set("Residence", town.getResidence());
                town.getMember().forEach(member -> data.set("Member." + member.getPlayer().toString() + ".Timestamp", member.getTimestamp()));
                town.getInvitation().forEach(invitation -> {
                    data.set("Invitation." + invitation.getPlayer() + ".Reason", invitation.getReason());
                    data.set("Invitation." + invitation.getTimestamp() + ".Timestamp", invitation.getTimestamp());
                });
                town.getItems().forEach((slot, itemStack) -> data.set("Items." + slot, ItemUtil.serializeItems(new ItemStack[]{itemStack})));
                try {
                    data.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        public static boolean exists(final String name) {
            return TOWNS.values().stream().anyMatch(town -> town.getName().equalsIgnoreCase(name));
        }

        public static Town get(final String name) {
            return TOWNS.values().stream().filter(town -> town.getName().equalsIgnoreCase(name)).collect(Collectors.toList()).get(0);
        }

        public static Town get(final Player user) {
            return TOWNS.values().stream().filter(town -> town.getMember().stream().anyMatch(member -> member.getPlayer().equals(user.getUniqueId()))).collect(Collectors.toList()).get(0);
        }

        public static boolean has(final Player user) {
            return TOWNS.values().stream().anyMatch(town -> town.getMember().stream().anyMatch(member -> member.getPlayer().equals(user.getUniqueId())));
        }
    }
}
