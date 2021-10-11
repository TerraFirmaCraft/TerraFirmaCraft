/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.BerryBushBlockEntity;
import net.dries007.tfc.common.blocks.plant.fruit.Lifecycle;
import net.dries007.tfc.common.blocks.plant.fruit.SeasonalPlantBlock;
import net.dries007.tfc.common.blocks.plant.fruit.WaterloggedBerryBushBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;

public class BerryBushFeature extends Feature<BlockStateConfiguration>
{
    private static final int REDUCTION_AMOUNT = -60 * ICalendar.TICKS_IN_DAY;

    public BerryBushFeature(Codec<BlockStateConfiguration> codec)
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

        BlockState bushState = config.state;
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 15; i++)
        {
            mutablePos.setWithOffset(world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, pos), rand.nextInt(10) - rand.nextInt(10), -1, rand.nextInt(10) - rand.nextInt(10));
            if (!world.isEmptyBlock(mutablePos)) continue;
            mutablePos.move(Direction.DOWN);
            if (!world.getBlockState(mutablePos).is(TFCTags.Blocks.BUSH_PLANTABLE_ON)) continue;
            mutablePos.move(Direction.UP);

            world.setBlock(mutablePos, bushState.setValue(SeasonalPlantBlock.LIFECYCLE, Lifecycle.HEALTHY), 3);

            BerryBushBlockEntity te = Helpers.getBlockEntity(world, pos, BerryBushBlockEntity.class);
            if (te != null)
            {
                te.reduceCounter(REDUCTION_AMOUNT);
            }
            return true;
        }
        return false;
    }
}
