/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

public class FloodFillLakeConfig implements FeatureConfiguration
{
    public static final Codec<FloodFillLakeConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.BLOCK_STATE.fieldOf("state").forGetter(FloodFillLakeConfig::getState),
        Codecs.FLUID.listOf().fieldOf("replace_fluids").forGetter(c -> new ArrayList<>(c.replaceFluids)),
        Codec.BOOL.optionalFieldOf("overfill", false).forGetter(FloodFillLakeConfig::shouldOverfill)
    ).apply(instance, FloodFillLakeConfig::new));

    private final BlockState state;
    private final Set<Fluid> replaceFluids;
    private final boolean overfill;

    public FloodFillLakeConfig(BlockState state, List<Fluid> replaceFluids, boolean overfill)
    {
        this.state = state;
        this.replaceFluids = new HashSet<>(replaceFluids);
        this.overfill = overfill;
    }

    public boolean shouldOverfill()
    {
        return overfill;
    }

    public boolean shouldReplace(Fluid fluid)
    {
        return fluid == Fluids.EMPTY || replaceFluids.contains(fluid);
    }

    public BlockState getState()
    {
        return state;
    }
}
