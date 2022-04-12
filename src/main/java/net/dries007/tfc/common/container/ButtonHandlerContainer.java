/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;

public interface ButtonHandlerContainer
{
    /**
     * An interface for containers that need to receive button presses from a client-side GUI
     * If you implement this interface you should also use {@link net.dries007.tfc.network.ScreenButtonPacket} to send update packets from the GUI
     *
     * @param buttonID the button ID that was pressed
     * @param extraNBT any extra NBT stored data from the individual button, null if empty
     */
    void onButtonPress(int buttonID, @Nullable CompoundTag extraNBT);
}
