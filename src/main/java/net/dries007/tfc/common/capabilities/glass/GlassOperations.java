/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.glass;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;

public final class GlassOperations
{
    public static final int LIMIT = 24;

    private final List<GlassOperation> steps = new ArrayList<>();

    public void apply(GlassOperation operation)
    {
        if (steps.size() < LIMIT)
        {
            steps.add(operation);
        }
    }

    public CompoundTag write(CompoundTag tag)
    {
        tag.putIntArray("steps", steps.stream().map(Enum::ordinal).toList());
        return tag;
    }

    public GlassOperations read(CompoundTag tag)
    {
        steps.clear();
        final int[] array = tag.getIntArray("steps");
        for (int id : array)
        {
            final GlassOperation step = GlassOperation.byIndex(id);
            if (step != null)
            {
                steps.add(step);
            }
        }
        return this;
    }

    public boolean any()
    {
        return steps.size() > 0;
    }

    public List<GlassOperation> getSteps()
    {
        return steps;
    }
}
