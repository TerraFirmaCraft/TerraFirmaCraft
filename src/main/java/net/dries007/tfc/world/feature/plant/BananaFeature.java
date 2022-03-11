/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import java.util.Random;

import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.plant.fruit.Lifecycle;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.common.blocks.plant.fruit.SeasonalPlantBlock.LIFECYCLE;
import static net.dries007.tfc.common.blocks.plant.fruit.SeasonalPlantBlock.STAGE;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class BananaFeature extends Feature<BlockStateConfiguration>
{
    public BananaFeature(Codec<BlockStateConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> context)
    {
        final WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        final Random random = context.random();
        final BlockStateConfiguration config = context.config();

        BlockState banana = config.state;

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        mutablePos.set(pos);
        if (Helpers.isBlock(level.getBlockState(mutablePos.below()), TFCTags.Blocks.BUSH_PLANTABLE_ON))
        {
            if (level.canSeeSky(mutablePos))
            {
                for (int stage = 0; stage <= 2; stage++)
                {
                    final int height = Mth.nextInt(random, 2, 3);
                    for (int k = 1; k < height; k++)
                    {
                        setBlock(level, mutablePos, banana.setValue(STAGE, stage).setValue(LIFECYCLE, Lifecycle.HEALTHY));
                        mutablePos.move(Direction.UP);
                        if (stage == 2) return true;
                    }
                }
            }
        }
        return false;
    }
}
