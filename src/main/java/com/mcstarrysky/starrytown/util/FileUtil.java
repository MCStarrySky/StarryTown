package com.mcstarrysky.starrytown.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * CustomCooking
 * me.mical.customcooking.util.FileUtil
 *
 * @author xiaomu
 * @since 2022/12/26 9:41 AM
 */
public class FileUtil {

    @NotNull
    public static String getName(@Nullable final File file, boolean removeSuffix) {
        if (file == null) {
            return "";
        }
        return removeSuffix ? file.getName().replace(".yml", "") : file.getName();
    }
}
