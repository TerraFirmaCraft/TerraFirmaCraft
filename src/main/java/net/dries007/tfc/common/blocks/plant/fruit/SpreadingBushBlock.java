/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.BerryBushBlockEntity;
import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;

public class SpreadingBushBlock extends SeasonalPlantBlock implements IForgeBlockExtension
{
    protected final Supplier<? extends Block> companion;
    protected final int maxHeight;
    protected final int deathChance;

    public SpreadingBushBlock(ExtendedProperties properties, Supplier<? extends Item> productItem, Lifecycle[] stages, Supplier<? extends Block> companion, int maxHeight, int deathChance)
    {
        super(properties, productItem, stages);
        this.companion = companion;
        this.maxHeight = maxHeight;
        this.deathChance = deathChance;
        registerDefaultState(getStateDefinition().any().setValue(STAGE, 0));
    }

    @Override
    public void cycle(BerryBushBlockEntity te, Level world, BlockPos pos, BlockState state, int stage, Lifecycle lifecycle, Random random)
    {
        if (lifecycle == Lifecycle.HEALTHY)
        {
            if (!te.isGrowing() || te.isRemoved()) return;

            if (distanceToGround(world, pos, maxHeight) >= maxHeight)
            {
                te.setGrowing(false);
            }
            else if (stage == 0)
            {
                world.setBlockAndUpdate(pos, state.setValue(STAGE, 1));
            }
            else if (stage == 1 && random.nextInt(7) == 0)
            {
                world.setBlockAndUpdate(pos, state.setValue(STAGE, 2));
                if (world.isEmptyBlock(pos.above()))
                    world.setBlockAndUpdate(pos.above(), state.setValue(STAGE, 1));
            }
            else if (stage == 2)
            {
                Direction d = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                BlockPos offsetPos = pos.relative(d);
                if (world.isEmptyBlock(offsetPos))
                {
                    world.setBlockAndUpdate(offsetPos, companion.get().defaultBlockState().setValue(SpreadingCaneBlock.FACING, d));
                    TickCounterBlockEntity cane = Helpers.getBlockEntity(world, offsetPos, TickCounterBlockEntity.class);
                    if (cane != null)
                    {
                        cane.reduceCounter(-1 * ICalendar.TICKS_IN_DAY * te.getTicksSinceUpdate());
                    }
                }
                if (random.nextInt(deathChance) == 0)
                {
                    te.setGrowing(false);
                }
            }
        }
        else if (lifecycle == Lifecycle.DORMANT && !te.isGrowing())
        {
            te.addDeath();
            if (te.willDie() && random.nextInt(3) == 0)
            {
                if (!world.getBlockState(pos.above()).is(TFCTags.Blocks.SPREADING_BUSH))
                    world.setBlockAndUpdate(pos, TFCBlocks.DEAD_BERRY_BUSH.get().defaultBlockState().setValue(STAGE, stage));
            }
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        return belowState.is(TFCTags.Blocks.BUSH_PLANTABLE_ON) || belowState.is(TFCTags.Blocks.ANY_SPREADING_BUSH) || this.mayPlaceOn(level.getBlockState(belowPos), level, belowPos);
    }
}
