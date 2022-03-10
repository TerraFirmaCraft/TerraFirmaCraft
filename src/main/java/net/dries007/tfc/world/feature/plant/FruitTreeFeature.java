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
import net.dries007.tfc.TerraFirmaCraft;
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
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();
        final Random rand = context.random();
        final BlockStateConfiguration config = context.config();

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        mutablePos.set(pos);

        if (level.getBlockState(mutablePos.below()).is(TFCTags.Blocks.BUSH_PLANTABLE_ON))
        {
            boolean blocked = false;
            for (int j = 1; j <= 10; j++)
            {
                mutablePos.move(Direction.UP);
                if (!level.isEmptyBlock(mutablePos))
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
                setBlock(level, mutablePos, branch);
                level.getBlockEntity(mutablePos, TFCBlockEntities.TICK_COUNTER.get()).ifPresent(entity -> entity.reduceCounter(-1 * ICalendar.TICKS_IN_DAY * 300));
                level.scheduleTick(mutablePos, branch.getBlock(), 1);
                TerraFirmaCraft.LOGGER.info("Fruit Tree: /tp @s " + pos.getX() + " ~ " + pos.getZ());
                return true;
            }
        }
        return false;
    }
}
