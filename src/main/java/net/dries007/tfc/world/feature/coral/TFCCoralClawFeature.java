/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.coral;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.feature.CoralClawFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import com.mojang.serialization.Codec;

public class TFCCoralClawFeature extends CoralClawFeature
{
    public TFCCoralClawFeature(Codec<NoneFeatureConfiguration> codec)
    {
        super(codec);
    }

    @Override
    protected boolean placeCoralBlock(LevelAccessor level, Random random, BlockPos pos, BlockState state)
    {
        return CoralHelpers.placeCoralBlock(level, random, pos, state);
    }
}
