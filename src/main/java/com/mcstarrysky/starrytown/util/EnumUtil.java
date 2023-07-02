package com.mcstarrysky.starrytown.util;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"unchecked", "rawtypes"})
public class EnumUtil {
    public static <T extends Enum> T valueOf(Class<T> enumClass, String... names) {
        for (String name : names) {
            try {
                Field enumField = enumClass.getDeclaredField(name);
                if (enumField.isEnumConstant())
                    return (T) enumField.get(null);
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
        }
        return null;
    }

    public static Material getMaterial(String... names) {
        return valueOf(Material.class, names);
    }
}
