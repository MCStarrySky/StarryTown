package com.mcstarrysky.starrytown.util;

import net.minecraft.nbt.*;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.lang.reflect.Method;

/**
 * @author Nipuru
 * @since 2021/9/14 18:07
 */
public final class NBTUtil {
    private static Method WRITE_NBT;
    private static Method READ_NBT;

    static {
        try {
            WRITE_NBT = NBTCompressedStreamTools.class.getDeclaredMethod("a", NBTBase.class, DataOutput.class);
            WRITE_NBT.setAccessible(true);
            READ_NBT = NBTCompressedStreamTools.class.getDeclaredMethod("a", DataInput.class, Integer.TYPE, NBTReadLimiter.class);
            READ_NBT.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private NBTUtil() {
    }


    public static String serializeItems(ItemStack[] itemStacks) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        NBTTagList nbtTagList = new NBTTagList();
        for (ItemStack itemStack : itemStacks) {
            CraftItemStack craftItemStack = asCraftCopy(itemStack);
            NBTTagCompound nbtTagCompound = new NBTTagCompound();

            if (craftItemStack != null) {
                try {
                    CraftItemStack.asNMSCopy(craftItemStack).b(nbtTagCompound);
                } catch (Exception ignored) {

                }
            }
            nbtTagList.add(nbtTagCompound);
        }
        try {
            WRITE_NBT.invoke(null, nbtTagList, dos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Base64Coder.encodeLines(baos.toByteArray());
    }

    private static CraftItemStack asCraftCopy(ItemStack itemStack) {
        if (itemStack instanceof CraftItemStack) {
            return ((CraftItemStack) itemStack);
        }
        return itemStack == null ? null : CraftItemStack.asCraftCopy(itemStack);
    }

    public static ItemStack[] deserializeItems(String content) {
        ByteArrayInputStream bais = new ByteArrayInputStream(Base64Coder.decodeLines(content));
        NBTTagList nbtTagList = (NBTTagList) readNbt(new DataInputStream(bais));
        org.bukkit.inventory.ItemStack[] itemStacks = new org.bukkit.inventory.ItemStack[nbtTagList.size()];
        for (int i = 0; i < nbtTagList.size(); i++) {
            NBTTagCompound nbtTagCompound = (NBTTagCompound) nbtTagList.get(i);
            if (!nbtTagCompound.g()) {
                itemStacks[i] = CraftItemStack.asCraftMirror(net.minecraft.world.item.ItemStack.a(nbtTagCompound));
            }
        }
        return itemStacks;
    }

    private static NBTBase readNbt(DataInput dataInput) {
        try {
            return (NBTBase) READ_NBT.invoke(null, dataInput, 0, new NBTReadLimiter(Long.MAX_VALUE));
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to read from " + dataInput, e);
        }
    }
}