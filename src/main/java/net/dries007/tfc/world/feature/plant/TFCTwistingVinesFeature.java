/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import java.util.Random;

import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class TFCTwistingVinesFeature extends Feature<ColumnPlantConfig>
{
    public static void placeColumn(LevelAccessor world, Random rand, BlockPos.MutableBlockPos mutablePos, int height, int minAge, int maxAge, BlockState body, BlockState head)
    {
        for (int i = 1; i <= height; ++i)
        {
            if (world.isEmptyBlock(mutablePos))
            {
                if (i == height || !world.isEmptyBlock(mutablePos.above()))
                {
                    world.setBlock(mutablePos, head.setValue(GrowingPlantHeadBlock.AGE, Mth.nextInt(rand, minAge, maxAge)), 2);
                    break;
                }
                world.setBlock(mutablePos, body, 2);
            }
            mutablePos.move(Direction.UP);
        }
    }

    public TFCTwistingVinesFeature(Codec<ColumnPlantConfig> codec)
    {
        super(codec);
    }

    public boolean place(FeaturePlaceContext<ColumnPlantConfig> context)
    {
        final WorldGenLevel world = context.level();
        final BlockPos pos = context.origin();
        final Random rand = context.random();
        final ColumnPlantConfig config = context.config();

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        boolean placedAny = false;
        int radius = config.radius();
        for (int i = 0; i < config.tries(); i++)
        {
            mutablePos.setWithOffset(pos, rand.nextInt(radius) - rand.nextInt(radius), 0, rand.nextInt(radius) - rand.nextInt(radius));
            mutablePos.move(Direction.DOWN);
            if (!world.getBlockState(mutablePos).is(TFCTags.Blocks.BUSH_PLANTABLE_ON))
                return false;
            mutablePos.move(Direction.UP);
            if (world.isEmptyBlock(mutablePos))
            {
                placeColumn(world, rand, world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, mutablePos).mutable(), rand.nextInt(config.maxHeight() - config.minHeight()) + config.minHeight(), 17, 25, config.bodyState(), config.headState());
                placedAny = true;
            }
        }
        return placedAny;
    }
}
