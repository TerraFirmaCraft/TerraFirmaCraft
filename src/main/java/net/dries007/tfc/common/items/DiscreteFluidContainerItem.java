/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import java.util.function.Supplier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public class DiscreteFluidContainerItem extends FluidContainerItem
{
    public DiscreteFluidContainerItem(Properties properties, Supplier<Integer> capacity, TagKey<Fluid> whitelist, boolean canPlaceLiquidsInWorld, boolean canPlaceSourceBlocks)
    {
        super(properties, capacity, whitelist, canPlaceLiquidsInWorld, canPlaceSourceBlocks);
    }
}
