/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Optional;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class SupportManager extends DataManager<Support>
{

    private int maxSupportUp, maxSupportDown, maxSupportHorizontal;

    SupportManager()
    {
        super("supports", "support");
    }

    public Optional<Support> get(BlockState state)
    {
        return getValues().stream().filter(support -> support.matches(state)).findFirst();
    }

    public Iterable<BlockPos> getMaximumSupportedAreaAround(BlockPos minPoint, BlockPos maxPoint)
    {
        return BlockPos.betweenClosed(minPoint.offset(-maxSupportHorizontal, -maxSupportDown, -maxSupportHorizontal), maxPoint.offset(maxSupportHorizontal, maxSupportUp, maxSupportHorizontal));
    }

    @Override
    protected Support read(ResourceLocation id, JsonObject obj)
    {
        return new Support(id, obj);
    }

    @Override
    protected void postProcess()
    {
        // Calculate the maximum support radius, used for searching supported areas
        maxSupportUp = 0;
        maxSupportDown = 0;
        maxSupportHorizontal = 0;
        for (Support support : getValues())
        {
            maxSupportUp = Math.max(support.getSupportUp(), maxSupportUp);
            maxSupportDown = Math.max(support.getSupportDown(), maxSupportDown);
            maxSupportHorizontal = Math.max(support.getSupportHorizontal(), maxSupportHorizontal);
        }
        super.postProcess();
    }
}