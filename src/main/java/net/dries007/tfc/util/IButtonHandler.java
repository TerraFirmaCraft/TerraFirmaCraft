package net.dries007.tfc.util;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;

public interface IButtonHandler
{
    void onButtonPress(int buttonID, @Nullable NBTTagCompound extraNBT);
}
