/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.forge;

import java.util.Objects;

import net.minecraft.nbt.CompoundTag;

import org.jetbrains.annotations.Nullable;

/**
 * Records the last three steps taken, and also the total number of steps taken since starting working.
 */
public final class ForgeSteps
{
    @Nullable private ForgeStep first, second, third;
    private int total;

    /**
     * Adds a step to the head of the queue (the new "last") shifting all others down.
     */
    public void addStep(@Nullable ForgeStep step)
    {
        third = second;
        second = first;
        first = step;
        total++;
    }

    @Nullable
    public ForgeStep last()
    {
        return first;
    }

    @Nullable
    public ForgeStep secondLast()
    {
        return second;
    }

    @Nullable
    public ForgeStep thirdLast()
    {
        return third;
    }

    public int total()
    {
        return total;
    }

    public CompoundTag write(CompoundTag tag)
    {
        tag.putByte("first", (byte) (first != null ? first.ordinal() : -1));
        tag.putByte("second", (byte) (second != null ? second.ordinal() : -1));
        tag.putByte("third", (byte) (third != null ? third.ordinal() : -1));
        tag.putInt("total", total);
        return tag;
    }

    public ForgeSteps read(CompoundTag nbt)
    {
        first = ForgeStep.valueOf(nbt.getByte("first"));
        second = ForgeStep.valueOf(nbt.getByte("second"));
        third = ForgeStep.valueOf(nbt.getByte("third"));
        total = nbt.getInt("total");
        return this;
    }

    @Override
    public String toString()
    {
        return "[" + first + ", " + second + ", " + third + ", ...]";
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

    @Override
    public int hashCode()
    {
        return Objects.hash(first, second, third, total);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ForgeSteps that = (ForgeSteps) o;
        return total == that.total && first == that.first && second == that.second && third == that.third;
    }
}