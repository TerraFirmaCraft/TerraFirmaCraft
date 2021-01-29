/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import java.util.Random;

import net.minecraft.block.AbstractTopPlantBlock;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
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
import net.dries007.tfc.common.blocks.plant.TFCKelpTopBlock;
import net.dries007.tfc.common.fluids.IFluidLoggable;

public class TFCKelpFeature extends Feature<TallPlantConfig>
{
    public TFCKelpFeature(Codec<TallPlantConfig> codec)
    {
        super(codec);
    }

    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, TallPlantConfig config)
    {
        if (!((TFCKelpTopBlock) (config.getHeadState().getBlock())).getFluidProperty().canContain(world.getBlockState(pos).getFluidState().getType()))
            return false;
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        boolean placedAny = false;
        int radius = config.getRadius();
        for (int i = 0; i < config.getTries(); i++)
        {
            mutablePos.setWithOffset(pos, rand.nextInt(radius) - rand.nextInt(radius), 0, rand.nextInt(radius) - rand.nextInt(radius));
            mutablePos.set(world.getHeightmapPos(Heightmap.Type.OCEAN_FLOOR, mutablePos));
            mutablePos.move(Direction.DOWN);
            if (!world.getBlockState(mutablePos).is(TFCTags.Blocks.SEA_BUSH_PLANTABLE_ON))
                return false;
            mutablePos.move(Direction.UP);
            if (world.isWaterAt(mutablePos) && !(world.getBlockState(mutablePos).getBlock() instanceof IFluidLoggable))
            {
                placeColumn(world, rand, mutablePos, rand.nextInt(config.getMaxHeight() - config.getMinHeight()) + config.getMinHeight(), 17, 25, getWetBlock(world, mutablePos, config.getBodyState()), getWetBlock(world, mutablePos, config.getHeadState()));
                placedAny = true;
            }
        }
        return placedAny;
    }

    public static void placeColumn(IWorld world, Random rand, BlockPos.Mutable mutablePos, int height, int minAge, int maxAge, BlockState body, BlockState head)
    {
        for (int i = 1; i <= height; ++i)
        {
            if (world.isWaterAt(mutablePos) && body.canSurvive(world, mutablePos))
            {
                if (i == height || !world.isWaterAt(mutablePos.above()))
                {
                    if (!world.getBlockState(mutablePos.below()).is(head.getBlock()))
                        world.setBlock(mutablePos, head.setValue(AbstractTopPlantBlock.AGE, MathHelper.nextInt(rand, minAge, maxAge)), 16);
                    return;
                }
                world.setBlock(mutablePos, body, 16);
            }
            mutablePos.move(Direction.UP);
        }
    }

    private static BlockState getWetBlock(IWorld world, BlockPos pos, BlockState state)
    {
        if (state.getBlock() instanceof IFluidLoggable)
        {
            IFluidLoggable block = (IFluidLoggable) state.getBlock();
            BlockState setState = block.getStateWithFluid(state, world.getFluidState(pos).getType());
            if (setState.getValue(block.getFluidProperty()).getFluid() != Fluids.EMPTY)
                return setState;
        }
        return state;
    }
}
