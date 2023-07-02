package com.mcstarrysky.starrytown.util;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * StarryTown
 * util.com.mcstarrysky.starrytown.ItemUtil
 *
 * @author mical
 * @since 2023/7/2 11:29 AM
 */
public class ItemUtil {

    @NotNull
    public static ItemStack build(@NotNull final Map<?, ?> map,
                                  @NotNull final Function<String, ItemStack> constructor) {
        final MappedData data = MappedData.of(MappedData.filter(map));
        ItemStack result = new ItemStack(Material.AIR);

        try {
            result = constructor.apply(data.getString("Material"));

            if (Objects.isNull(result)) {
                result = new ItemStack(Material.AIR);
                return result;
            }

            if (data.containsKey("NBT")) {
                final String nbtString = data.getString("NBT");
                if (StringUtils.isNotEmpty(nbtString)) {
                    final NBTItem nbt = new NBTItem(result);
                    final NBTContainer tag = new NBTContainer(nbtString);
                    nbt.mergeCompound(tag);
                }
            }

            final int amount = data.getInt("Amount", 1);
            result.setAmount(amount);

            ItemMeta meta = result.getItemMeta();
            if (Objects.isNull(meta)) {
                meta = Bukkit.getItemFactory().getItemMeta(result.getType());
            }
            if (Objects.isNull(meta)) {
                return result;
            }

            final int damage = data.getInt("Durability", -1);
            if (damage != -1) {
                if (XMaterial.supports(13)) {
                    if (meta instanceof Damageable) {
                        final Damageable damageable = (Damageable) meta;
                        damageable.setDamage(damage);
                    }
                } else {
                    //noinspection deprecation
                    result.setDurability((short) damage);
                }
            }

            final String display = data.getString("Display");
            if (Objects.nonNull(display)) {
                meta.setDisplayName(LocaleUtil.color(display));
            }

            final int customModelData = data.getInt("CustomModelData",  -1);
            if (customModelData != -1) {
                meta.setCustomModelData(customModelData);
            }

            final List<String> lore = data.getList("Lore", String.class);
            if (!lore.isEmpty()) {
                lore.replaceAll(LocaleUtil::color);
                meta.setLore(lore);
            }

            if (data.containsKey("Enchants")) {
                final MappedData enchants = data.getMappedData("Enchants");
                for (final String name : enchants.keySet()) {
                    final Optional<XEnchantment> xEnchantment = XEnchantment.matchXEnchantment(name.toLowerCase());
                    if (!xEnchantment.isPresent()) {
                        LocaleUtil.sendToConsole("构建 ItemStack 时读取到未知附魔: {0}.", name);
                        continue;
                    }

                    final Enchantment enchantment = xEnchantment.get().getEnchant();
                    if (Objects.isNull(enchantment)) {
                        LocaleUtil.sendToConsole("构建 ItemStack 时读取到未知附魔: {0}.", name);
                        continue;
                    }
                    meta.addEnchant(enchantment, enchants.getInt(name), true);
                }
            }

            if (XMaterial.supports(8) && data.containsKey("ItemFlags")) {
                final List<String> flags = data.getList("ItemFlags", String.class);
                for (final String name : flags) {
                    final ItemFlag flag = EnumUtil.valueOf(ItemFlag.class, name.toUpperCase());
                    if (flag == null) {
                        LocaleUtil.sendToConsole("构建 ItemStack 时读取到未知 ItemFlag: {0}.", name);
                        continue;
                    }
                    meta.addItemFlags(flag);
                }
            }

            result.setItemMeta(meta);
        } catch (final Exception exception) {
            LocaleUtil.sendToConsole("构建 ItemStack 时遇到错误: {0}.", exception.getMessage());
            exception.printStackTrace();
        }
        return result;
    }

    @NotNull
    public static ItemStack build(@NotNull final ConfigurationSection section) {
        final ConfigurationSection itemSection = section.getConfigurationSection("ItemStack");
        if (itemSection == null) {
            return new ItemStack(Material.AIR);
        }
        return build(itemSection.getValues(false), ItemUtil::compatibleGet);
    }

    @NotNull
    public static ItemStack build(@NotNull final ConfigurationSection section,
                                  @NotNull final Function<String, ItemStack> constructor) {
        final ConfigurationSection itemSection = section.getConfigurationSection("ItemStack");
        if (itemSection == null) {
            return new ItemStack(Material.AIR);
        }
        return build(itemSection.getValues(false), constructor);
    }

    @SuppressWarnings("JavaReflectionMemberAccess")
    @NotNull
    public static ItemStack compatibleGet(final String material) {
        if (StringUtils.isNumeric(material)) {
            try {
                final int id = Integer.parseInt(material);
                final Constructor<ItemStack> constructor = ItemStack.class.getConstructor(int.class);
                return constructor.newInstance(id);
            } catch (NoSuchMethodException exception) {
                LocaleUtil.sendToConsole("尝试构建数字 Material 的物品, 但是获取数字 ID 构造器失败: &c{0}&r.", material);
            } catch (Throwable error) {
                LocaleUtil.sendToConsole("尝试兼容性获取物品失败: &c{0}&r.", material);
                error.printStackTrace();
            }
        }

        final Material vanilla = EnumUtil.getMaterial(material.toUpperCase());
        if (Objects.nonNull(vanilla)) {
            return new ItemStack(vanilla);
        }

        final Optional<XMaterial> xMaterial = XMaterial.matchXMaterial(material);
        if (!xMaterial.isPresent()) {
            return new ItemStack(Material.AIR);
        }

        final ItemStack item = xMaterial.get().parseItem();
        if (Objects.isNull(item)) {
            return new ItemStack(Material.AIR);
        }
        return item;
    }

    @Contract("null -> true")
    public static boolean invalid(@Nullable final ItemStack item) {
        return Objects.isNull(item) || item.getType() == Material.AIR;
    }
}
