package me.mical.starrytown.inventory;

import dev.lone.itemsadder.api.CustomStack;
import me.mical.starrytown.ConfigReader;
import me.mical.starrytown.StarryTown;
import me.mical.starrytown.data.Cache;
import me.mical.starrytown.data.Invitation;
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
 * @since 2022/8/29 12:40
 */
public class InvitationInfoInventory implements InventoryExecutor {

    private final Town town;
    private final Inventory inventory;

    public InvitationInfoInventory(final Town town) {
        this.town = town;
        this.inventory = build();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void e(InventoryClickEvent e) {
        if (town.isOwner(e.getWhoClicked().getUniqueId()) && Arrays.stream(getSlots()).anyMatch(value -> value == e.getSlot())) {
            final ItemStack item = e.getCurrentItem();
            if (e.getCurrentItem() != null) {
                assert item != null;
                assert item.getItemMeta() != null;
                final String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                final OfflinePlayer player = Bukkit.getOfflinePlayer(name);
                final List<Member> members = town.getMember();
                if (e.isLeftClick()) {
                    members.add(Member.builder().player(player.getUniqueId()).timestamp(System.currentTimeMillis()).build());
                    town.setMember(members);
                    ConfigReader.TOWNS.put(town.getUuid(), town);
                    LocaleUtil.send(e.getWhoClicked(), "???????????? <green>" + player.getName() + " <white>?????????!");
                    if (player.isOnline()) {
                        LocaleUtil.send(player.getPlayer(), "?????? <green>" + player.getName() + " <white>????????? <green>" + e.getWhoClicked().getName() + " <white>?????????????????????! ?????? <green>/starrytown my <white>????????????????????????.");
                    } else {
                        Cache.send(player, "?????? <green>" + player.getName() + " <white>????????? <green>" + e.getWhoClicked().getName() + " <white>?????????????????????! ?????? <green>/starrytown my <white>????????????????????????.");
                    }
                } else if (e.isRightClick()) {
                    LocaleUtil.send(e.getWhoClicked(), "???????????? <green>" + player.getName() + " <white>?????????!");
                    if (player.isOnline()) {
                        LocaleUtil.send(player.getPlayer(), "?????? <green>" + player.getName() + " <white>????????? <green>" + e.getWhoClicked().getName() + " <white>?????????????????????!");
                    } else {
                        Cache.send(player, "?????? <green>" + player.getName() + " <white>????????? <green>" + e.getWhoClicked().getName() + " <white>?????????????????????!");
                    }
                }
                e.getWhoClicked().closeInventory();
            }
        }
        if (e.isLeftClick() && e.getSlot() == 49) {
            e.getWhoClicked().closeInventory();
            e.getWhoClicked().openInventory(new TownInfoInventory(town, (Player) e.getWhoClicked()).getInventory());
        }
    }

    @Override
    public Inventory build() {
        final Inventory inv = Bukkit.createInventory(this, 6 * 9, "?????? " + town.getName() + " ???????????????");
        for (int i : getFrameSlots()) {
            inv.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1));
        }
        for (int i : getSlots()) {
            final int index = Arrays.stream(getSlots()).boxed().collect(Collectors.toList()).indexOf(i);
            if (index + 1 <= town.getInvitation().size()) {
                final Invitation invitation = town.getInvitation().get(index);
                final OfflinePlayer player = Bukkit.getOfflinePlayer(invitation.getPlayer());
                final String[] loreFormat = {
                        "",
                        " &7???????????? ???",
                        "   &7??????: &f" + player.getName(),
                        "   &7UUID: &f" + player.getUniqueId(),
                        "",
                        " &7?????? ???",
                        "   &f" + invitation.getReason(),
                        "",
                        " &7????????? ???",
                        "   &f" + invitation.getTime(),
                        "",
                        "   &a??? &f????????????????????????",
                        "   &b??? &f????????????????????????",
                        ""
                };
                final ItemStack info = ItemBuilder.builder(Material.PLAYER_HEAD).name("&f" + player.getName()).lore(Arrays.stream(loreFormat).map(LocaleUtil::color).collect(Collectors.toList())).build();
                inv.setItem(i, info);
            }
        }
        if (StarryTown.getInstance().getServer().getPluginManager().isPluginEnabled("ItemsAdder")) {
            final CustomStack close = CustomStack.getInstance("icon_cancel");
            if (close != null) {
                close.setDisplayName(LocaleUtil.color("&c??????"));
                inv.setItem(49, close.getItemStack());
            }
        } else {
            inv.setItem(49, ItemBuilder.builder(Material.RED_STAINED_GLASS_PANE).name("&c??????").build());
        }
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
