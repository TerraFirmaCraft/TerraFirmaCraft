/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import java.util.Random;

import net.minecraft.block.AbstractTopPlantBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;

public class TFCTwistingVinesFeature extends Feature<TallPlantConfig>
{
    public TFCTwistingVinesFeature(Codec<TallPlantConfig> codec)
    {
        super(codec);
    }

    public boolean generate(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, TallPlantConfig config)
    {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        boolean placedAny = false;
        int radius = config.getRadius();
        for (int i = 0; i < config.getTries(); i++)
        {
            mutablePos.setAndOffset(pos, rand.nextInt(radius) - rand.nextInt(radius), 0, rand.nextInt(radius) - rand.nextInt(radius));
            mutablePos.move(Direction.DOWN);
            if (!world.getBlockState(mutablePos).isIn(TFCTags.Blocks.BUSH_PLANTABLE_ON))
                return false;
            mutablePos.move(Direction.UP);
            if (world.isAirBlock(mutablePos))
            {
                placeColumn(world, rand, world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, mutablePos).toMutable(), rand.nextInt(config.getMaxHeight() - config.getMinHeight()) + config.getMinHeight(), 17, 25, config.getBodyState(), config.getHeadState());
                placedAny = true;
            }
        }
        return placedAny;
    }

    public static void placeColumn(IWorld world, Random rand, BlockPos.Mutable mutablePos, int height, int minAge, int maxAge, BlockState body, BlockState head)
    {
        for (int i = 1; i <= height; ++i)
        {
            if (world.isAirBlock(mutablePos))
            {
                if (i == height || !world.isAirBlock(mutablePos.up()))
                {
                    world.setBlockState(mutablePos, head.with(AbstractTopPlantBlock.AGE, MathHelper.nextInt(rand, minAge, maxAge)), 2);
                    break;
                }
                world.setBlockState(mutablePos, body, 2);
            }
            mutablePos.move(Direction.UP);
        }
    }
}
