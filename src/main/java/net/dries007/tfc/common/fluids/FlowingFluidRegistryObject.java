/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.fluids;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.registries.RegistryObject;

/**
 * A pair of a source and getFlowing fluid, for {@link FlowingFluid}s.
 */
public record FlowingFluidRegistryObject<F extends FlowingFluid>(RegistryObject<F> flowing, RegistryObject<F> source)
{
    public F getFlowing()
    {
        return flowing.get();
    }

    public F getSource()
    {
        return source.get();
    }

    public BlockState createSourceBlock()
    {
        return source.get().defaultFluidState().createLegacyBlock();
    }
}
