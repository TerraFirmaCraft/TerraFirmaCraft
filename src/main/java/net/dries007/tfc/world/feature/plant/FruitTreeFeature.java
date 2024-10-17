/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.plant.fruit.GrowingFruitTreeBranchBlock;
import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.util.Helpers;
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
        final var random = context.random();
        final BlockStateConfiguration config = context.config();

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        mutablePos.set(pos).move(0, -1, 0);

        if (Helpers.isBlock(level.getBlockState(mutablePos), TFCTags.Blocks.BUSH_PLANTABLE_ON))
        {
            mutablePos.move(0, 1, 0);
            for (int j = 1; j <= 10; j++)
            {
                if (!EnvironmentHelpers.isWorldgenReplaceable(level, mutablePos))
                {
                    return false;
                }
                mutablePos.move(0, 1, 0);
            }
            mutablePos.set(pos);
            BlockState branch = config.state.getBlock().defaultBlockState();
            branch = Helpers.setProperty(branch, GrowingFruitTreeBranchBlock.NATURAL, true);
            branch = Helpers.setProperty(branch, GrowingFruitTreeBranchBlock.SAPLINGS, Mth.nextInt(random, 2, 4));
            setBlock(level, mutablePos, branch);
            level.getBlockEntity(mutablePos, TFCBlockEntities.TICK_COUNTER.get()).ifPresent(entity -> entity.reduceCounter(-1 * ICalendar.CALENDAR_TICKS_IN_DAY * 300));
            level.scheduleTick(mutablePos, branch.getBlock(), 1);
            return true;
        }
        return false;
    }
}
