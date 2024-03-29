/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.coral;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.CoralMushroomFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class TFCCoralMushroomFeature extends CoralMushroomFeature
{
    public TFCCoralMushroomFeature(Codec<NoneFeatureConfiguration> codec)
    {
        super(codec);
    }

    @Override
    protected boolean placeCoralBlock(LevelAccessor level, RandomSource random, BlockPos blockPos, BlockState state)
    {
        return CoralHelpers.placeCoralBlock(level, random, blockPos, state);
    }
}
