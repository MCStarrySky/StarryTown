package me.mical.starrytown.inventory;

import dev.lone.itemsadder.api.CustomStack;
import me.mical.starrytown.StarryTown;
import me.mical.starrytown.data.Cache;
import me.mical.starrytown.data.Town;
import me.mical.starrytown.inventory.holder.InventoryExecutor;
import me.mical.starrytown.util.ItemBuilder;
import me.mical.starrytown.util.LocaleUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
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
            case 22:
                if (e.isRightClick()) {
                    user.closeInventory();
                    final String action = town.isOwner(p.getUniqueId()) ? "解散" : "退出";
                    LocaleUtil.send(user, "你是否要" + action + "聚落 <green>" + town.getName() + "<white>? 这将永远无法恢复!");
                    LocaleUtil.send(user, "请在20秒内输入 <green>/starrytown confirm<white> 来确认!");
                    Cache.REMOVE_CACHE.put(user.getUniqueId(), town.getUuid());
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            Cache.REMOVE_CACHE.remove(user.getUniqueId());
                        }
                    }.runTaskLaterAsynchronously(StarryTown.getInstance(), 20L * 20L);
                } else if (e.isLeftClick()) {
                    user.closeInventory();
                    user.openInventory(new MemberInfoInventory(town, p).getInventory());
                }
                break;
            case 31:
                user.closeInventory();
                user.openInventory(new InvitationInfoInventory(town).getInventory());
                break;
            case 49:
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
        final String action = town.isOwner(p.getUniqueId()) ? "解散" : "退出";
        final List<String> lore = Arrays.stream(loreFormat).map(s -> LocaleUtil.color(s).replace("{action}", action)).collect(Collectors.toList());
        final ItemStack info = ItemBuilder.builder(Material.BLACK_BED)
                .name("&f" + town.getName())
                .lore(lore)
                .build();
        inv.setItem(11, info);
        if (town.getInvitation().size() > 0) {
            final ItemStack invitation = ItemBuilder.builder(Material.OAK_SIGN)
                    .name("&f查看加入申请")
                    .amount(town.getInvitation().size())
                    .build();
            inv.setItem(0, invitation);
        }
        if (StarryTown.getInstance().getServer().getPluginManager().isPluginEnabled("ItemsAdder")) {
            final CustomStack close = CustomStack.getInstance("icon_cancel");
            if (close != null) {
                close.setDisplayName(LocaleUtil.color("&c关闭"));
                inv.setItem(22, close.getItemStack());
            }
        } else {
            inv.setItem(22, ItemBuilder.builder(Material.RED_STAINED_GLASS_PANE).name("&c关闭").build());
        }
        return inv;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private int[] getFrame() {
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 23, 24, 25, 26};
    }
}
