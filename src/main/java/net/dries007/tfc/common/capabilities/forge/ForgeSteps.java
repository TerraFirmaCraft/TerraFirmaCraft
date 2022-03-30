/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.forge;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;

public class ForgeSteps
{
    @Nullable private ForgeStep first, second, third;

    public void addStep(@Nullable ForgeStep step)
    {
        third = second;
        second = first;
        first = step;
    }

    public void write(CompoundTag tag)
    {
        // Serialize to ordinal + 1, so that a zero entry (which is the default when reading from a nbt tag where the value doesn't exist) turns into null.
        tag.putInt("first", first != null ? first.ordinal() + 1 : 0);
        tag.putInt("second", second != null ? second.ordinal() + 1 : 0);
        tag.putInt("third", third != null ? third.ordinal() + 1 : 0);
    }

    public void read(CompoundTag nbt)
    {
        first = ForgeStep.valueOf(nbt.getInt("first") - 1);
        second = ForgeStep.valueOf(nbt.getInt("second") - 1);
        third = ForgeStep.valueOf(nbt.getInt("third") - 1);
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
        return "[" + getStep(0) + ", " + getStep(1) + ", " + getStep(2) + "]";
    }

    /**
     * Checks if this is fresh new (no forging has been done yet)
     *
     * @return {@code true} if has been worked at least once, {@code false} otherwise
     */
    public boolean any()
    {
        return first != null || second != null || third != null;
    }
}