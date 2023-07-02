package com.mcstarrysky.starrytown.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * CustomCooking
 * me.mical.customcooking.util.BasicUtil
 *
 * @author xiaomu
 * @since 2023/1/28 1:26 PM
 */
public class BasicUtil {

    public static int roundToInt(final double number) {
        return BigDecimal.valueOf(number).setScale(2, RoundingMode.HALF_DOWN).intValue();
    }
}
