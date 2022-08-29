package me.mical.starrytown.command;

import me.mical.starrytown.ConfigReader;
import me.mical.starrytown.StarryTown;
import me.mical.starrytown.data.Cache;
import me.mical.starrytown.data.Invitation;
import me.mical.starrytown.data.Member;
import me.mical.starrytown.data.Town;
import me.mical.starrytown.inventory.TownInfoInventory;
import me.mical.starrytown.item.Items;
import me.mical.starrytown.util.LocaleUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xiaomu
 * @since 2022/8/28 22:37
 */
public class CommandHandler implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        final Player p;
        if (args.length == 0) {
            sendHelp(sender);
        } else {
            switch (args[0].toLowerCase()) {
                case "reload":
                    ConfigReader.reloadConfig();
                    LocaleUtil.send(sender, "已成功重载插件.");
                    break;
                case "join":
                    if (sender instanceof Player) {
                        p = (Player) sender;
                        if (ConfigReader.Towns.has(p)) {
                            LocaleUtil.send(p, "你已经存在于一个聚落或为一个聚落的领导人, 无法申请加入其他聚落");
                        } else {
                            if (args.length <= 2) {
                                LocaleUtil.send(p, "参数长度有误!");
                            } else {
                                final String name = args[1];
                                final List<String> argument = Arrays.stream(args).collect(Collectors.toList());
                                argument.remove(0);
                                argument.remove(0);
                                final String reason = String.join("", argument);
                                if (ConfigReader.Towns.exists(name)) {
                                    final Town town = ConfigReader.Towns.get(name);
                                    if (town.isOwner(p.getUniqueId())) {
                                        LocaleUtil.send(p, "你是这个聚落的首领, 不需要申请加入你的聚落!");
                                    } else {
                                        final Invitation invitation = Invitation.builder().player(p.getUniqueId()).reason(reason).timestamp(System.currentTimeMillis()).build();
                                        final List<Invitation> invitations = town.getInvitation();
                                        invitations.add(invitation);
                                        town.setInvitation(invitations);
                                        ConfigReader.TOWNS.put(town.getUuid(), town);
                                        LocaleUtil.send(p, "已向聚落 <green>" + name + " <white>发送加入请求!");
                                        final OfflinePlayer player = Bukkit.getOfflinePlayer(town.getOwner());
                                        if (player.isOnline()) {
                                            LocaleUtil.send(player.getPlayer(), "玩家 <green>" + p.getName() + " <white>向你发送了加入聚落申请! 理由是: <green>" + reason);
                                        } else {
                                            Cache.send(player, "玩家 <green>" + p.getName() + " <white>向你发送了加入聚落申请! 理由是: <green>" + reason);
                                        }
                                    }
                                } else {
                                    LocaleUtil.send(p, "聚落不存在!");
                                }
                            }
                        }
                    }
                    break;
                case "confirm":
                    if (sender instanceof Player) {
                        p = (Player) sender;
                        if (Cache.REMOVE_CACHE.containsKey(p.getUniqueId())) {
                            final Town town = ConfigReader.TOWNS.get(Cache.REMOVE_CACHE.get(p.getUniqueId()));
                            if (town.isOwner(p.getUniqueId())) {
                                ConfigReader.TOWNS.remove(Cache.REMOVE_CACHE.get(p.getUniqueId()));
                                LocaleUtil.send(p, "已经删除聚落 <green>" + town.getName() + "<white>!");
                            } else {
                                final List<Member> members = town.getMember();
                                members.remove(members.stream().filter(member -> member.getPlayer().equals(p.getUniqueId())).collect(Collectors.toList()).get(0));
                                town.setMember(members);
                                ConfigReader.TOWNS.put(town.getUuid(), town);
                                LocaleUtil.send(p, "已经退出聚落 <green>" + town.getName() + "<white>!");
                            }
                            Cache.REMOVE_CACHE.remove(p.getUniqueId());
                        } else {
                            LocaleUtil.send(p, "没有待处理的请求!");
                        }
                    } else {
                        LocaleUtil.send(sender, "该命令只能由玩家发出!");
                    }
                    break;
                case "create":
                    if (sender instanceof Player) {
                        p = (Player) sender;
                        if (ConfigReader.Towns.has(p)) {
                            LocaleUtil.send(p, "你已经存在于聚落中或为一个聚落的首领, 不能建立聚落!");
                        } else {
                            if (args.length != 2) {
                                LocaleUtil.send(p, "参数长度有误!");
                            } else {
                                final String name = args[1];
                                if (ConfigReader.Towns.exists(name)) {
                                    LocaleUtil.send(p, "已存在聚落 <green>" + name + "<white>!");
                                } else {
                                    assert p.getEquipment() != null;
                                    final ItemStack itemInHand = p.getEquipment().getItemInMainHand();
                                    if (itemInHand.isSimilar(Items.TOWN_CREATE_PAPER)) {
                                        itemInHand.setAmount(itemInHand.getAmount() - 1);
                                        final long time = System.currentTimeMillis();
                                        final List<Member> member = new ArrayList<>();
                                        member.add(Member.builder().player(p.getUniqueId()).timestamp(time).build());
                                        final Town town = Town.builder()
                                                .name(name)
                                                .uuid(UUID.randomUUID())
                                                .owner(p.getUniqueId())
                                                .timestamp(time)
                                                .economy(0.0D)
                                                .member(member)
                                                .invitation(new ArrayList<>()).build();
                                        ConfigReader.TOWNS.put(town.getUuid(), town);
                                        LocaleUtil.send(p, "已创建聚落 <green>" + town.getName() + " <white>(<red>" + town.getUuid().toString() + "<white>).");
                                        p.openInventory(new TownInfoInventory(town, p).getInventory());
                                        ConfigReader.Towns.export();
                                    } else {
                                        LocaleUtil.send(sender, "若想创建聚落, 你的手里必须拿着创建聚落所需的聚落创建凭证.");
                                    }
                                }
                            }
                        }
                    } else {
                        LocaleUtil.send(sender, "该命令只能由玩家发出!");
                    }
                    break;
                case "my":
                    if (sender instanceof Player) {
                        p = (Player) sender;
                        if (ConfigReader.Towns.has(p)) {
                            p.openInventory(new TownInfoInventory(ConfigReader.Towns.get(p), p).getInventory());
                        } else {
                            LocaleUtil.send(p, "你不存在于任何一个聚落中!");
                        }
                    } else {
                        LocaleUtil.send(sender, "该命令只能由玩家发出!");
                    }
                    break;
                case "help":
                    sendHelp(sender);
                    break;
                case "economy":
                    if (sender instanceof Player) {
                        p = (Player) sender;
                        if (args.length != 3) {
                            LocaleUtil.send(p, "参数长度有误!");
                        } else {
                            if (ConfigReader.Towns.has(p)) {
                                final double amount;
                                try {
                                    amount = Double.parseDouble(args[2]);
                                } catch (final NumberFormatException e) {
                                    LocaleUtil.send(p, "你输入的金钱不是一个数字!");
                                    break;
                                }
                                if (amount <= 0) {
                                    LocaleUtil.send(p, "你输入的金钱必须大于0!");
                                } else {
                                    final Town town = ConfigReader.Towns.get(p);
                                    final OfflinePlayer owner = Bukkit.getOfflinePlayer(town.getOwner());
                                    switch (args[1].toLowerCase()) {
                                        case "add":
                                            if (StarryTown.getEconomy().getBalance(p) >= amount) {
                                                StarryTown.getEconomy().withdrawPlayer(p, amount);
                                                town.setEconomy(town.getEconomy() + amount);
                                                ConfigReader.TOWNS.put(town.getUuid(), town);
                                                LocaleUtil.send(p, "你向所在聚落的资产账户中捐了 <green>$" + amount + "<white>, 你当前剩余 <green>$" + StarryTown.getEconomy().getBalance(p) + "<white>.");

                                                if (owner.isOnline()) {
                                                    LocaleUtil.send((Player) owner, "<green>" + p.getName() + " <white>向聚落账户中捐了 <green>$" + amount + "<white>, 当前账户中剩余 <green>$" + town.getEconomy() + "<white>.");
                                                } else {
                                                    Cache.send(owner, "<green>" + p.getName() + " <white>向聚落账户中捐了 <green>$" + amount + "<white>, 当前账户中剩余 <green>$" + town.getEconomy() + "<white>.");
                                                }
                                            } else {
                                                LocaleUtil.send(p, "你的金钱不足!");
                                            }
                                            break;
                                        case "take":
                                            if (town.getEconomy() >= amount) {
                                                StarryTown.getEconomy().depositPlayer(p, amount);
                                                town.setEconomy(town.getEconomy() - amount);
                                                ConfigReader.TOWNS.put(town.getUuid(), town);
                                                LocaleUtil.send(p, "你已从聚落账户中取走了 <green>$" + amount + "<white>, 你当前剩余 <green>$" + StarryTown.getEconomy().getBalance(p) + "<white>, 聚落账户中剩余 <green>$" + town.getEconomy() + "<white>.");
                                                if (!town.isOwner(p.getUniqueId())) {
                                                    if (owner.isOnline()) {
                                                        LocaleUtil.send((Player) owner, "<green>" + p.getName() + " <white>从聚落账户中取走了 <green>$" + amount + "<white>, 当前账户中剩余 <green>$" + town.getEconomy() + "<white>.");
                                                    } else {
                                                        Cache.send(owner, "<green>" + p.getName() + " <white>从聚落账户中取走了 <green>$" + amount + "<white>, 当前账户中剩余 <green>$" + town.getEconomy() + "<white>.");
                                                    }
                                                }
                                            } else {
                                                LocaleUtil.send(p, "聚落中的资产不足!");
                                            }
                                            break;
                                    }
                                }
                            } else {
                                LocaleUtil.send(p, "你还不是任何一个聚落的首领或成员!");
                            }
                        }
                    } else {
                        LocaleUtil.send(sender, "该命令只能由玩家发出!");
                    }
                    break;
                default:
                    LocaleUtil.send(sender, "未知命令, 请检查你输入的命令是否正确.");
                    break;
            }
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0) {
            if (args.length == 1) {
                final List<String> arrayList = new ArrayList<>();
                for (final String cmd : Arrays.asList("reload", "create", "confirm", "my", "help", "join", "economy")) {
                    if (cmd.startsWith(args[0])) {
                        if (cmd.equalsIgnoreCase("reload")) {
                            if (sender.isOp()) {
                                arrayList.add(cmd);
                            }
                        } else {
                            arrayList.add(cmd);
                        }
                    }
                }
                return arrayList;
            }
            if (args[0].equalsIgnoreCase("create") && args.length == 2) {
                return Collections.singletonList("<名字>");
            }
            if (args[0].equalsIgnoreCase("join")) {
                switch (args.length) {
                    case 2:
                        return Collections.singletonList("<名字>");
                    case 3:
                        return Collections.singletonList("<理由>");
                }
            }
            if (args[0].equalsIgnoreCase("economy")) {
                switch (args.length) {
                    case 2:
                        return Arrays.asList("add", "take");
                    case 3:
                        return Collections.singletonList("<金额>");
                }
            }
        }
        return null;
    }

    private static void sendHelp(final CommandSender sender) {
        LocaleUtil.send(sender, "Starry Town 插件命令帮助");
        LocaleUtil.send(sender, "/starrytown help <yellow>--- <green>查看此帮助页面.");
        LocaleUtil.send(sender, "/starrytown create <名字> <yellow>--- <green>创建一个聚落.");
        LocaleUtil.send(sender, "/starrytown join <名字> <理由><yellow>--- <green>发送聚落加入申请.");
        LocaleUtil.send(sender, "/starrytown my <yellow>--- <green>查看你所在聚落的详情信息.");
        LocaleUtil.send(sender, "/starrytown economy add <amount> <yellow>--- <green>向你所在聚落捐献金钱.");
        LocaleUtil.send(sender, "/starrytown economy take <amount> <yellow>--- <green>申请从你所在聚落账户中划款.");
        if (sender.isOp()) {
            LocaleUtil.send(sender, "/starrytown reload <yellow>--- <green>重新载入插件.");
        }
    }
}
