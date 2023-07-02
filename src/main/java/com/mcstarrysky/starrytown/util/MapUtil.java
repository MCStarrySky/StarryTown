package com.mcstarrysky.starrytown.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MapUtil {

    @NotNull
    public static <T> Map<T, Object> filter(@Nullable final Map<?, ?> map, @NotNull final Class<T> clazz) {
        final Map<T, Object> result = new HashMap<>();
        if (Objects.isNull(map)) {
            return result;
        }
        map.forEach((key, value) -> {
            if (clazz.isInstance(key)) {
                result.put(clazz.cast(key), value);
            }
        });
        return result;
    }
}
