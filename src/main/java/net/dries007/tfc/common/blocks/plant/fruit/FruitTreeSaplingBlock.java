/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.List;
import java.util.function.Supplier;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
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
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.soil.FarmlandBlock;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateRange;

public class FruitTreeSaplingBlock extends BushBlock implements IForgeBlockExtension, EntityBlockExtension, HoeOverlayBlock
{
    /**
     * Checks if splicing, the action with clicking with an offhand knife and a sapling, works.
     * @param pos   The position the sapling would be or is
     * @param state The state at that position currently
     */
    public static boolean maySplice(Level level, BlockPos pos, BlockState state)
    {
        final BlockState below = level.getBlockState(pos.below());
        // if there's currently a sapling there
        if (state.hasProperty(SAPLINGS))
        {
            final int saplings = state.getValue(SAPLINGS);
            return Helpers.isBlock(below, TFCTags.Blocks.FRUIT_TREE_BRANCH) ? saplings < 3 : saplings < 4;
        }
        // to splice a fresh sapling, we need a branch below
        return Helpers.isBlock(below, TFCTags.Blocks.FRUIT_TREE_BRANCH) && state.isAir();
    }

    private static final IntegerProperty SAPLINGS = TFCBlockStateProperties.SAPLINGS;
    protected final Supplier<? extends Block> block;
    protected final Supplier<Integer> treeGrowthDays;
    private final ExtendedProperties properties;
    private final Supplier<ClimateRange> climateRange;
    private final Lifecycle[] stages;

    public FruitTreeSaplingBlock(ExtendedProperties properties, Supplier<? extends Block> block, int treeGrowthDays, Supplier<ClimateRange> climateRange, Lifecycle[] stages)
    {
        this(properties, block, () -> treeGrowthDays, climateRange, stages);
    }

    public FruitTreeSaplingBlock(ExtendedProperties properties, Supplier<? extends Block> block, Supplier<Integer> treeGrowthDays, Supplier<ClimateRange> climateRange, Lifecycle[] stages)
    {
        super(properties.properties());
        this.properties = properties;
        this.block = block;
        this.treeGrowthDays = treeGrowthDays;
        this.climateRange = climateRange;
        this.stages = stages;
    }

    @Override
    public void addHoeOverlayInfo(Level level, BlockPos pos, BlockState state, List<Component> text, boolean isDebug)
    {
        final ClimateRange range = climateRange.get();

        text.add(FarmlandBlock.getHydrationTooltip(level, pos, range, false, FruitTreeLeavesBlock.getHydration(level, pos)));
        text.add(FarmlandBlock.getAverageTemperatureTooltip(level, pos, range, false));

        if (!stages[Calendars.SERVER.getCalendarMonthOfYear().ordinal()].active())
        {
            text.add(Component.translatable("tfc.tooltip.fruit_tree.sapling_wrong_month"));
        }
        else
        {
            text.add(Component.translatable("tfc.tooltip.fruit_tree.growing"));
        }
        if (maySplice(level, pos, state))
        {
            text.add(Component.translatable("tfc.tooltip.fruit_tree.sapling_splice"));
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        final int saplings = state.getValue(SAPLINGS);
        final ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
        final ItemStack off = player.getItemInHand(InteractionHand.OFF_HAND);
        if (defaultBlockState().getBlock().asItem() == held.getItem() && Helpers.isItem(off, TFCTags.Items.TOOLS_KNIVES) && maySplice(level, pos, state))
        {
            held.shrink(1);
            level.setBlockAndUpdate(pos, state.setValue(SAPLINGS, saplings + 1));
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SeasonalPlantBlock.PLANT_SHAPE;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        // only go through this check if we are reasonably sure the plant would actually live
        if (stages[Calendars.SERVER.getCalendarMonthOfYear().ordinal()].active())
        {
            if (level.getBlockEntity(pos) instanceof TickCounterBlockEntity counter)
            {
                if (counter.getTicksSinceUpdate() > ICalendar.TICKS_IN_DAY * getTreeGrowthDays() * TFCConfig.SERVER.globalFruitSaplingGrowthModifier.get())
                {
                    final int hydration = FruitTreeLeavesBlock.getHydration(level, pos);
                    final float temp = Climate.getAverageTemperature(level, pos);
                    if (!climateRange.get().checkBoth(hydration, temp, false))
                    {
                        level.setBlockAndUpdate(pos, TFCBlocks.PLANTS.get(Plant.DEAD_BUSH).get().defaultBlockState());
                    }
                    else
                    {
                        createTree(level, pos, state, random);
                    }
                }
            }
        }
    }

    public void createTree(Level level, BlockPos pos, BlockState state, RandomSource random)
    {
        final boolean onBranch = Helpers.isBlock(level.getBlockState(pos.below()), TFCTags.Blocks.FRUIT_TREE_BRANCH);
        int internalSapling = onBranch ? 3 : state.getValue(SAPLINGS);
        if (internalSapling == 1 && random.nextBoolean()) internalSapling += 1;
        level.setBlockAndUpdate(pos, block.get().defaultBlockState().setValue(PipeBlock.DOWN, true).setValue(TFCBlockStateProperties.SAPLINGS, internalSapling).setValue(TFCBlockStateProperties.STAGE_3, onBranch ? 1 : 0));
        TickCounterBlockEntity.reset(level, pos);
    }


    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockPos downPos = pos.below();
        BlockState downState = level.getBlockState(downPos);
        if (Helpers.isBlock(downState, TFCTags.Blocks.FRUIT_TREE_BRANCH))
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
        return super.canSurvive(state, level, pos) || Helpers.isBlock(downState, TFCTags.Blocks.BUSH_PLANTABLE_ON);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        TickCounterBlockEntity.reset(level, pos);
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(SAPLINGS));
    }

    public int getTreeGrowthDays()
    {
        return treeGrowthDays.get();
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    @Override
    protected MapCodec<? extends BushBlock> codec()
    {
        return fakeBlockCodec();
    }
}
