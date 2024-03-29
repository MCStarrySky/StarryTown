package com.mcstarrysky.starrytown.inventory;

import com.mcstarrysky.languageutils.LanguageUtils;
import com.mcstarrysky.starrytown.data.Cache;
import com.mcstarrysky.starrytown.data.Town;
import com.mcstarrysky.starrytown.inventory.holder.InventoryExecutor;
import com.mcstarrysky.starrytown.util.ItemBuilder;
import com.mcstarrysky.starrytown.util.LocaleUtil;
import com.mcstarrysky.starrytown.StarryTown;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author xiaomu
 * @since 2022/8/28 22:50
 */
public class TownInfoInventory implements InventoryExecutor {

    private final Inventory inventory;
    private final Town town;
    private final Player p;

    public TownInfoInventory(final Town town, final Player p) {
        this.town = town;
        this.p = p;
        this.inventory = build();
    }

    @Override
    public void e(InventoryClickEvent e) {
        final Player user = (Player) e.getWhoClicked();
        switch (e.getSlot()) {
            case 0:
                if (town.getInvitation().size() > 0) {
                    user.closeInventory();
                    user.openInventory(new InvitationInfoInventory(town).getInventory());
                }
                break;
            case 8:
                // TODO
                break;
            case 12:
                if (e.isRightClick()) {
                    user.closeInventory();
                    final String action = town.isOwner(p.getUniqueId()) ? "解散" : "退出";
                    LocaleUtil.send(user, "你是否要" + action + "聚落 <green>" + town.getName() + "<white>? 这将永远无法恢复!");
                    LocaleUtil.send(user, "请在20秒内输入 <green>/starrytown confirm<white> 来确认!");
                    Cache.REMOVE_CACHE.put(user.getUniqueId(), town.getUuid());
                    Bukkit.getAsyncScheduler().runDelayed(StarryTown.getInstance(), task -> {
                        Cache.REMOVE_CACHE.remove(user.getUniqueId());
                    }, 20L, TimeUnit.SECONDS);
                } else if (e.isLeftClick()) {
                    user.closeInventory();
                    user.openInventory(new MemberListInventory(p, town).getInventory());
                }
                break;
            case 14:
                if (e.isLeftClick()) {
                    user.closeInventory();
                    user.openInventory(new GlobalChestInventory(town).getInventory());
                }
                break;
                /*
            case 15:
                user.closeInventory();
                if (town.isOwner(user.getUniqueId())) {
                    if (e.isLeftClick()) {
                        final Location block = user.getLocation().clone().add(0, -1, 0);
                        final ClaimedResidence res = ResidenceApi.getResidenceManager().getByLoc(block);
                        if (res == null) {
                            LocaleUtil.send(user, "你脚下没有领地!");
                            return;
                        }
                        if (!res.isOwner(user)) {
                            LocaleUtil.send(user, "你不是你脚下领地的所有者!");
                            return;
                        }
                        town.getMember().stream().filter(member -> member.getPlayer() == user.getUniqueId()).forEach(member -> user.performCommand("res padd " + res.getName() + " " + Bukkit.getOfflinePlayer(member.getPlayer()).getName()));
                        town.setResidence(res.getName());
                        ConfigReader.TOWNS.put(town.getUuid(), town);
                        LocaleUtil.send(user, "已设置!");
                    } else {
                        final ClaimedResidence res = ResidenceApi.getResidenceManager().getByName(town.getResidence());
                        if (res == null) {
                            LocaleUtil.send(user, "暂未设置!");
                            return;
                        }
                        res.tpToResidence(user, user, false);
                        LocaleUtil.send(user, "已传送!");
                    }
                } else {
                    if (e.isLeftClick()) {
                        final ClaimedResidence res = ResidenceApi.getResidenceManager().getByName(town.getResidence());
                        if (res == null) {
                            LocaleUtil.send(user, "你的聚落暂时没有设置领地.");
                            return;
                        }
                        res.tpToResidence(user, user, false);
                        LocaleUtil.send(user, "已传送!");
                    }
                }
                break;
                 */
            case 22:
                if (e.isLeftClick()) {
                    user.closeInventory();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public Inventory build() {
        final Inventory inv = Bukkit.createInventory(this, 3 * 9, "聚落 " + town.getName());
        for (int i : getFrame()) {
            inv.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1));
        }
        final String[] loreFormat = {
                "",
                " &7聚落信息 ▶",
                "   &7首领: &f" + Bukkit.getOfflinePlayer(town.getOwner()).getName(),
                "   &7UUID: &f" + town.getUuid(),
                "   &7资产: &f$" + town.getEconomy(),
                "",
                " &7创建于 ▶",
                "   &f" + town.getTime(),
                "",
                "   &a▶ &f左键以查看聚落成员",
                "   &b▶ &f右键以{action}这个聚落",
                ""
        };
        // frame 0 1 0 1 0 1 0 frame
        // frame 0 0 1 0 1 0 0 frame
        final String action = town.isOwner(p.getUniqueId()) ? "解散" : "退出";
        final List<String> lore = Arrays.stream(loreFormat).map(s -> LocaleUtil.color(s).replace("{action}", action)).collect(Collectors.toList());
        final ItemStack info = ItemBuilder.builder(Material.BLACK_BED)
                .name("&f" + town.getName())
                .lore(lore)
                .build();
        inv.setItem(12, info);
        if (town.getInvitation().size() > 0) {
            final ItemStack invitation = ItemBuilder.builder(Material.OAK_SIGN)
                    .name("&f查看加入申请")
                    .amount(town.getInvitation().size())
                    .build();
            inv.setItem(0, invitation);
        } else {
            inv.setItem(0, ItemBuilder.builder(Material.BLACK_STAINED_GLASS_PANE).build());
        }
        final ItemStack members = ItemBuilder.builder(Material.PLAYER_WALL_HEAD)
                .name("&f查看成员列表")
                .amount(town.getMember().size())
                .build();
        inv.setItem(8, members);
        final ItemBuilder chest = ItemBuilder.builder(Material.CHEST)
                .name("&f查看公共箱子")
                .amount(1);
        final String[] chestLoreFormat = {
                "",
                " &7箱子内容 ▶",
        };
        final List<ItemStack> chestContent = new ArrayList<>(town.getItems().values());
        final List<String> chestLore = Arrays.stream(chestLoreFormat).map(LocaleUtil::color).collect(Collectors.toList());
        int j = 0;
        while (j < 5) {
            if ((j + 1) > chestContent.size()) {
                break;
            }
            // 石头 * 1
            final ItemStack item = chestContent.get(j);
            final String i18n = LanguageUtils.getName(p, item);
            final String name = item.getItemMeta() != null ? item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : i18n : i18n;
            chestLore.add("   &7" + name + " &a* " + item.getAmount());
            j++;
        }
        chestLore.add("");
        chestLore.add("   &a▶ &f左键以打开公共箱子");
        chestLore.add("   &b▶ &f右键以查看操作日志");
        chestLore.add("");
        chest.lore(chestLore);
        inv.setItem(14, chest.build());
        /*
        final String[] res = {
                "",
                " &7领地信息 ▶",
                "   &7名称: &f" + (town.getResidence() == null ? "&c暂未设置" : town.getResidence()),
                ""
        };
        final List<String> resLore = Arrays.stream(res).map(LocaleUtil::color).collect(Collectors.toList());
        if (town.isOwner(p.getUniqueId())) {
            resLore.add("   &a▶ &f左键以设置聚落领地为脚下领地");
            resLore.add("   &b▶ &f右键以传送到这个领地");
        } else {
            resLore.add("   &a▶ &f左键以传送到这个领地");
        }
        resLore.add(" ");
        inv.setItem(15, ItemBuilder.builder(Material.WOODEN_HOE)
                .name("&f领地")
                .lore(resLore).build());
         */
        /*
        if (StarryTown.getInstance().getServer().getPluginManager().isPluginEnabled("ItemsAdder")) {
            final CustomStack close = CustomStack.getInstance("icon_cancel");
            if (close != null) {
                close.setDisplayName(LocaleUtil.color("&c关闭"));
                inv.setItem(22, close.getItemStack());
            }
        } else {
            inv.setItem(22, ItemBuilder.builder(Material.RED_STAINED_GLASS_PANE).name("&c关闭").build());
        }
         */
        inv.setItem(22, ItemBuilder.builder(Material.RED_STAINED_GLASS_PANE).name("&c关闭").build());
        return inv;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private int[] getFrame() {
        return new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 23, 24, 25, 26};
    }
}
