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
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

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
        for (int i = 0; i < 5; i++)
        {
            mutablePos.setWithOffset(pos, Helpers.triangle(random, 10), -1, Helpers.triangle(random, 10));
            mutablePos.setY(level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, mutablePos).getY());
            if (level.getBlockState(mutablePos).is(TFCTags.Blocks.BUSH_PLANTABLE_ON))
            {
                mutablePos.move(Direction.UP);
                if (level.canSeeSky(mutablePos))
                {
                    for (int stage = 0; stage <= 2; stage++)
                    {
                        for (int k = 1; k < Mth.nextInt(random, 1, 3); k++)
                        {
                            setBlock(level, mutablePos, banana.setValue(STAGE, stage));
                            mutablePos.move(Direction.UP);
                            if (stage == 2) return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
