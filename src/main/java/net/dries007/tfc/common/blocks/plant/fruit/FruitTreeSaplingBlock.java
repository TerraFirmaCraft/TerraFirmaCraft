/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.Random;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.common.blocks.*;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateRange;

public class FruitTreeSaplingBlock extends BushBlock implements IForgeBlockExtension, EntityBlockExtension
{
    private static final IntegerProperty SAPLINGS = TFCBlockStateProperties.SAPLINGS;
    protected final Supplier<? extends Block> block;
    protected final int treeGrowthDays;
    private final ExtendedProperties properties;
    private final Supplier<ClimateRange> climateRange;

    public FruitTreeSaplingBlock(ExtendedProperties properties, Supplier<? extends Block> block, int treeGrowthDays, Supplier<ClimateRange> climateRange)
    {
        super(properties.properties());
        this.properties = properties;
        this.block = block;
        this.treeGrowthDays = treeGrowthDays;
        this.climateRange = climateRange;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit)
    {
        int saplings = state.getValue(SAPLINGS);
        if (!level.isClientSide() && handIn == InteractionHand.MAIN_HAND && saplings < 4)
        {
            ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStack off = player.getItemInHand(InteractionHand.OFF_HAND);
            if (defaultBlockState().getBlock().asItem() == held.getItem() && off.is(TFCTags.Items.KNIVES) && state.hasProperty(TFCBlockStateProperties.SAPLINGS))
            {
                if (saplings > 2 && level.getBlockState(pos.below()).is(TFCTags.Blocks.FRUIT_TREE_BRANCH))
                    return InteractionResult.FAIL;
                if (!player.isCreative())
                    held.shrink(1);
                level.setBlockAndUpdate(pos, state.setValue(SAPLINGS, saplings + 1));
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SeasonalPlantBlock.PLANT_SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random)
    {
        level.getBlockEntity(pos, TFCBlockEntities.TICK_COUNTER.get()).ifPresent(counter -> {
            if (counter.getTicksSinceUpdate() > (long) ICalendar.TICKS_IN_DAY * treeGrowthDays)
            {
                int hydration = (int) Climate.getRainfall(level, pos) / 5;
                float temp = Climate.getTemperature(level, pos);
                if (climateRange.get().checkBoth(hydration, temp, false))
                {
                    level.setBlockAndUpdate(pos, TFCBlocks.PLANTS.get(Plant.DEAD_BUSH).get().defaultBlockState());
                }
                else
                {
                    boolean onBranch = level.getBlockState(pos.below()).is(TFCTags.Blocks.FRUIT_TREE_BRANCH);
                    level.setBlockAndUpdate(pos, block.get().defaultBlockState().setValue(PipeBlock.DOWN, true).setValue(TFCBlockStateProperties.SAPLINGS, onBranch ? 3 : state.getValue(SAPLINGS)).setValue(TFCBlockStateProperties.STAGE_3, onBranch ? 1 : 0));
                    Helpers.resetCounter(level, pos);
                }
            }
        });
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockPos downPos = pos.below();
        BlockState downState = level.getBlockState(downPos);
        if (downState.is(TFCTags.Blocks.FRUIT_TREE_BRANCH))
        {
            if (downState.getValue(FruitTreeBranchBlock.STAGE) > 1)
            {
                return false;
            }
            for (Direction d : Direction.Plane.HORIZONTAL)
            {
                if (downState.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(d)))
                {
                    return true;
                }
            }
            return false;
        }
        return super.canSurvive(state, level, pos) || downState.is(TFCTags.Blocks.BUSH_PLANTABLE_ON);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        Helpers.resetCounter(level, pos);
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(SAPLINGS));
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }
}
