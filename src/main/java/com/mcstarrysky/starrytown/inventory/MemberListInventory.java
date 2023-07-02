package com.mcstarrysky.starrytown.inventory;

import com.mcstarrysky.starrytown.ConfigReader;
import com.mcstarrysky.starrytown.StarryTown;
import com.mcstarrysky.starrytown.data.Cache;
import com.mcstarrysky.starrytown.data.Member;
import com.mcstarrysky.starrytown.data.Town;
import com.mcstarrysky.starrytown.util.ItemBuilder;
import com.mcstarrysky.starrytown.util.LocaleUtil;
import com.mcstarrysky.starrytown.util.inventory.PInventory;
import com.mcstarrysky.starrytown.util.inventory.element.InventoryTemplate;
import com.mcstarrysky.starrytown.util.inventory.element.BaseElement;
import com.mcstarrysky.starrytown.util.inventory.element.InventoryButton;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StarryTown
 * inventory.com.mcstarrysky.starrytown.MemberListInventory
 *
 * @author mical
 * @since 2023/7/2 12:15 PM
 */
public class MemberListInventory extends PInventory<List<Member>> {

    public MemberListInventory(Player user, Town town) {
        super(StarryTown.getInstance(), town.getMember(), user, "聚落 " + town.getName() + " 的成员", 6);

        this.addElement(new InventoryTemplate.InventoryTemplateBuilder<Member>()
                .base(InventoryButton.builder()
                        .onClick(e -> {
                            final Member member = (Member) InventoryTemplate.get(this, "template").getContent(e.getSlot());
                            if (town.isOwner(user.getUniqueId())) {
                                if (town.isOwner(member.getPlayer())) {
                                    LocaleUtil.send(e.getWhoClicked(), "你不可以将自己驱逐出聚落, 但你可以解散聚落!");
                                } else {
                                    final OfflinePlayer player = Bukkit.getOfflinePlayer(member.getPlayer());
                                    final List<Member> members = town.getMember();
                                    members.remove(members.stream().filter(member0 -> member0.getPlayer().equals(player.getUniqueId())).toList().get(0));
                                    town.setMember(members);
                                    ConfigReader.TOWNS.put(town.getUuid(), town);
                                    LocaleUtil.send(user, "你已将 <green>" + player.getName() + " <white>驱逐出聚落!");
                                    if (player.isOnline()) {
                                        LocaleUtil.send(player.getPlayer(), "你已被驱逐出聚落 <green>" + town.getName() + "<white>!");
                                    } else {
                                        Cache.send(player, "你已被驱逐出聚落 <green>" + town.getName() + "<white>!");
                                    }
                                }
                            }
                        })
                        .base(BaseElement.builder()
                                .name("template")
                                .item(() -> new ItemStack(Material.PLAYER_HEAD, 1))
                                .xPos("2-8")
                                .yPos("3-5")
                                .build())
                        .build())
                        .contents(town.getMember())
                        .applyTemple((itemStack, member) -> {
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
                            if (town.isOwner(user.getUniqueId())) {
                                lore.add("   &a▶ &f左键以驱逐该成员");
                                lore.add("");
                            }
                            final ItemStack head = ItemBuilder.builder(Material.PLAYER_HEAD)
                                    .name("&f" + user.getName())
                                    .lore(lore)
                                    .build();
                            final SkullMeta meta = (SkullMeta) head.getItemMeta();
                            meta.setPlayerProfile(player.getPlayerProfile());
                            head.setItemMeta(meta);
                            return head;
                        })
                .build()
        );
    }
}
