/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.soil.FarmlandBlock;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;
import net.dries007.tfc.util.climate.ClimateRange;

/**
 * Spreading bushes have two parts: a bush block, which is a full block which can grow up to three blocks tall, and a cane block, which is a horizontal protrusion that can output from the sides of a bush block.
 * The cane can then turn into more bush blocks, spreading the plant and allowing it to climb up hills.
 * Both the cane and the bush block use the "stage" property from {@link SeasonalPlantBlock} to determine and limit their growth.
 * <p>
 * Spreading:
 * <ul>
 *   <li>Cane blocks always convert to bush blocks, if they can.</li>
 *   <li>Bush blocks can grow up to three blocks upwards, but can only spread canes to adjacent blocks, meaning a single berry bush has four directions to spread in.</li>
 *   <li>Stage 0 is for newly planted bushes. Stage 1 is for all bush blocks that are bushes and grown naturally. Advancing to stage 2 means the bush is mature, and won't spread anymore.</li>
 *   <li>This means an individual horizontal position can spread up to three blocks adjacent, *but* unless the bush is climbing a hill, most of those canes won't be able to grow into bushes, because they're on solid ground. Meaning natural bush spreading will eventually stop, as the bush will reach all stage 2, where it is unable to spread.</li>
 * </ul>
 * The player can harvest bush blocks, stage 2 for a guaranteed drop, all other stages for 1/2 chance.
 */
public class SpreadingBushBlock extends StationaryBerryBushBlock implements IForgeBlockExtension, IBushBlock, HoeOverlayBlock
{
    protected final Supplier<? extends Block> companion;
    protected final int maxHeight;

    public SpreadingBushBlock(ExtendedProperties properties, Supplier<? extends Item> productItem, Lifecycle[] stages, Supplier<? extends Block> companion, int maxHeight, Supplier<ClimateRange> climateRange)
    {
        super(properties, productItem, stages, climateRange);
        this.companion = companion;
        this.maxHeight = maxHeight;
        registerDefaultState(getStateDefinition().any().setValue(STAGE, 0));
    }

    public Block getCane()
    {
        return companion.get();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return state.getValue(STAGE) == 2 ? Shapes.block() : PLANT_SHAPE;
    }

    @Override
    protected BlockState growAndPropagate(Level level, BlockPos pos, RandomSource random, BlockState state)
    {
        if (!state.getValue(LIFECYCLE).active())
        {
            // Only grow when active
            return state;
        }

        // Increment stage by one
        final int originalStage = state.getValue(STAGE);

        if (originalStage == 0)
        {
            // Stage 0 -> grow into stage 1
            return state.setValue(STAGE, 1);
        }
        if (originalStage == 1)
        {
            // Stage 1: either grow upwards, or attempt to grow a cane and move to stage 2
            // Grow a bush upwards
            final BlockPos abovePos = pos.above();
            if (level.isEmptyBlock(abovePos) && distanceToGround(level, pos, maxHeight) < maxHeight)
            {
                // Growing upwards grows at stage = 1, because stage = 0 is just newly planted bushes.
                level.setBlockAndUpdate(abovePos, state.setValue(STAGE, 1).setValue(LIFECYCLE, state.getValue(LIFECYCLE)));
                return state; // Stay in stage 1, if we only grew upwards.
            }

            if (random.nextBoolean())
            {
                // Optionally cause a cane to grow on an adjacent block
                final Direction offset = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                final BlockPos offsetPos = pos.relative(offset);
                if (level.isEmptyBlock(offsetPos))
                {
                    level.setBlockAndUpdate(offsetPos, companion.get().defaultBlockState().setValue(SpreadingCaneBlock.FACING, offset).setValue(LIFECYCLE, state.getValue(LIFECYCLE)));
                }
            }

            return state.setValue(STAGE, 2);
        }
        return state; // Stay at stage 2, and don't grow
    }

    @Override
    public void addHoeOverlayInfo(Level level, BlockPos pos, BlockState state, List<Component> text, boolean isDebug)
    {
        final BlockPos sourcePos = pos.below();
        final ClimateRange range = climateRange.get();

        text.add(FarmlandBlock.getHydrationTooltip(level, sourcePos, range, false));
        text.add(FarmlandBlock.getTemperatureTooltip(level, sourcePos, range, false));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        final BlockPos belowPos = pos.below();
        final BlockState belowState = level.getBlockState(belowPos);
        return mayPlaceOn(belowState, level, belowPos) || (belowState.getBlock() == this && belowState.getValue(STAGE) != 0);
    }
}
