package me.mical.starrytown.data;

import lombok.Builder;
import lombok.Data;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author xiaomu
 * @since 2022/8/28 22:07
 */
@Data
@Builder
public class Town {

    private String name;
    private UUID uuid;
    private UUID owner;
    private long timestamp;
    private double economy;
    private List<Member> member;
    private List<Invitation> invitation;
    private Map<Integer, ItemStack> items;

    @Nullable
    private String residence;

    public String getTime() {
        return new SimpleDateFormat().format(new Date(timestamp));
    }

    public boolean isOwner(final UUID player) {
        return getOwner().equals(player);
    }
}
