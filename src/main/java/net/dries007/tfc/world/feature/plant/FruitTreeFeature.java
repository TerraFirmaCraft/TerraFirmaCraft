/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.plant.fruit.GrowingFruitTreeBranchBlock;
import net.dries007.tfc.util.calendar.ICalendar;

public class FruitTreeFeature extends Feature<BlockStateConfiguration>
{
    public FruitTreeFeature(Codec<BlockStateConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> context)
    {
        final WorldGenLevel world = context.level();
        final BlockPos pos = context.origin();
        final Random rand = context.random();
        final BlockStateConfiguration config = context.config();

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int i = 0; i < 12; i++)
        {
            mutablePos.setWithOffset(world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, pos), rand.nextInt(10) - rand.nextInt(10), -1, rand.nextInt(10) - rand.nextInt(10));
            if (world.getBlockState(mutablePos).is(TFCTags.Blocks.BUSH_PLANTABLE_ON))
            {
                boolean blocked = false;
                for (int j = 1; j <= 10; j++)
                {
                    mutablePos.move(Direction.UP);
                    if (!world.isEmptyBlock(mutablePos))
                    {
                        blocked = true;
                        break;
                    }
                }
                if (!blocked)
                {
                    mutablePos.move(Direction.DOWN, 9);
                    int saplings = Mth.clamp(rand.nextInt(5) + 1, 2, 4);
                    BlockState branch = config.state.getBlock().defaultBlockState().setValue(GrowingFruitTreeBranchBlock.SAPLINGS, saplings);
                    setBlock(world, mutablePos, branch);
                    world.getBlockEntity(mutablePos, TFCBlockEntities.TICK_COUNTER.get()).ifPresent(entity -> entity.reduceCounter(-1 * ICalendar.TICKS_IN_DAY * 300));
                    world.scheduleTick(mutablePos, branch.getBlock(), 1);
                    return true;
                }
            }
        }
        return false;
    }
}
