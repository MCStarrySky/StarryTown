package me.mical.starrytown.inventory;

import me.mical.starrytown.ConfigReader;
import me.mical.starrytown.data.Cache;
import me.mical.starrytown.data.Member;
import me.mical.starrytown.data.Town;
import me.mical.starrytown.inventory.holder.InventoryExecutor;
import me.mical.starrytown.util.ItemBuilder;
import me.mical.starrytown.util.LocaleUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaomu
 * @since 2022/8/29 08:01
 */
public class MemberInfoInventory implements InventoryExecutor {

    private final Inventory inventory;
    private final Town town;
    private final Player p;

    public MemberInfoInventory(final Town town, final Player p) {
        this.town = town;
        this.p = p;
        this.inventory = build();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void e(InventoryClickEvent e) {
        if (town.isOwner(e.getWhoClicked().getUniqueId()) && e.isLeftClick() && Arrays.stream(getSlots()).anyMatch(value -> value == e.getSlot())) {
            final ItemStack item = e.getCurrentItem();
            if (e.getCurrentItem() != null) {
                assert item != null;
                assert item.getItemMeta() != null;
                final String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                if (name.equalsIgnoreCase(e.getWhoClicked().getName())) {
                    LocaleUtil.send(e.getWhoClicked(), "你不可以将自己驱逐出聚落, 但你可以解散聚落!");
                } else {
                    final OfflinePlayer player = Bukkit.getOfflinePlayer(name);
                    final List<Member> members = town.getMember();
                    members.remove(members.stream().filter(member -> member.getPlayer().equals(player.getUniqueId())).collect(Collectors.toList()).get(0));
                    town.setMember(members);
                    ConfigReader.TOWNS.put(town.getUuid(), town);
                    LocaleUtil.send(e.getWhoClicked(), "你已将 <green>" + player.getName() + " <white>驱逐出聚落!");
                    if (player.isOnline()) {
                        LocaleUtil.send(player.getPlayer(), "你已被驱逐出聚落 <green>" + town.getName() + "<white>!");
                    } else {
                        Cache.send(player, "你已被驱逐出聚落 <green>" + town.getName() + "<white>!");
                    }
                }
                e.getWhoClicked().closeInventory();
            }
        }
        if (e.isLeftClick() && e.getSlot() == 49) {
            e.getWhoClicked().closeInventory();
            e.getWhoClicked().openInventory(new TownInfoInventory(town, p).getInventory());
        }
    }

    @Override
    public Inventory build() {
        final Inventory inv = Bukkit.createInventory(this, 6 * 9, "聚落 " + town.getName() + " 的成员");
        for (int i : getFrameSlots()) {
            inv.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1));
        }
        for (int i : getSlots()) {
            final int index = Arrays.stream(getSlots()).boxed().collect(Collectors.toList()).indexOf(i);
            if ((index + 1) <= town.getMember().size()) {
                final Member member = town.getMember().get(index);
                final OfflinePlayer player = Bukkit.getOfflinePlayer(member.getPlayer());
                final String[] loreFormat = {
                        "",
                        " &7玩家信息 ▶",
                        "   &7名称: &f" + player.getName(),
                        "   &7UUID: &f" + player.getUniqueId(),
                        "",
                        " &7加入于 ▶",
                        "   &f" + member.getTime(),
                        ""
                };
                final List<String> lore = Arrays.stream(loreFormat).map(LocaleUtil::color).collect(Collectors.toList());
                if (town.isOwner(p.getUniqueId())) {
                    lore.add("   &a▶ &f左键以驱逐该成员");
                    lore.add("");
                }
                final ItemStack info = ItemBuilder.builder(Material.PLAYER_HEAD)
                        .name("&f" + player.getName())
                        .lore(lore)
                        .build();
                inv.setItem(i, info);
            }
        }
        /*
        if (StarryTown.getInstance().getServer().getPluginManager().isPluginEnabled("ItemsAdder")) {
            final CustomStack close = CustomStack.getInstance("icon_cancel");
            if (close != null) {
                close.setDisplayName(LocaleUtil.color("&c关闭"));
                inv.setItem(49, close.getItemStack());
            }
        } else {
            inv.setItem(49, ItemBuilder.builder(Material.RED_STAINED_GLASS_PANE).name("&c关闭").build());
        }
         */
        inv.setItem(49, ItemBuilder.builder(Material.RED_STAINED_GLASS_PANE).name("&c关闭").build());
        return inv;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private static int[] getSlots() {
        return new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
    }
}
