/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * A collection of a {@link FluidType}, along with a source and flowing {@link FlowingFluid}.
 */
public record FluidHolder<F extends FlowingFluid>(
    DeferredHolder<FluidType, FluidType> type,
    DeferredHolder<Fluid, F> flowing,
    DeferredHolder<Fluid, F> source
) {
    public F getFlowing()
    {
        return flowing.get();
    }

    public F getSource()
    {
        return source.get();
    }

    public FluidType getType()
    {
        return type.value();
    }

    public BlockState createSourceBlock()
    {
        return source.get().defaultFluidState().createLegacyBlock();
    }
}
