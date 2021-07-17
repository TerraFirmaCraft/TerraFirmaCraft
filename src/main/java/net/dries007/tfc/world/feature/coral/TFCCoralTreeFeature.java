/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.coral;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.CoralTreeFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import com.mojang.serialization.Codec;

public class TFCCoralTreeFeature extends CoralTreeFeature
{
    public TFCCoralTreeFeature(Codec<NoFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    protected boolean placeCoralBlock(IWorld world, Random random, BlockPos blockPos, BlockState state)
    {
        return CoralHelpers.placeCoralBlock(world, random, blockPos, state);
    }
}
