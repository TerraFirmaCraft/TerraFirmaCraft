/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.forge;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;

public final class ForgeSteps
{
    @Nullable private ForgeStep first, second, third;
    private int total;

    public void addStep(@Nullable ForgeStep step)
    {
        third = second;
        second = first;
        first = step;
        total++;
    }

    public void write(CompoundTag tag)
    {
        tag.putByte("first", (byte) (first != null ? first.ordinal() : -1));
        tag.putByte("second", (byte) (second != null ? second.ordinal() : -1));
        tag.putByte("third", (byte) (third != null ? third.ordinal() : -1));
        tag.putInt("total", total);
    }

    public void read(CompoundTag nbt)
    {
        first = ForgeStep.valueOf(nbt.getByte("first"));
        second = ForgeStep.valueOf(nbt.getByte("second"));
        third = ForgeStep.valueOf(nbt.getByte("third"));
        total = nbt.getInt("total");
    }

    @Nullable
    public ForgeStep getStep(int step)
    {
        return switch (step)
            {
                case 0 -> first;
                case 1 -> second;
                case 2 -> third;
                default -> throw new IllegalArgumentException("Cannot get step for index: " + step);
            };
    }

    @Override
    public String toString()
    {
        return "[" + first + ", " + second + ", " + third + "]";
    }

    /**
     * Checks if this is fresh new (no forging has been done yet)
     *
     * @return {@code true} if has been worked at least once, {@code false} otherwise
     */
    public boolean any()
    {
        return total > 0;
    }
}