/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.plant.PipePlantBlock;
import net.dries007.tfc.common.blocks.soil.FarmlandBlock;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.ClimateRange;

import static net.dries007.tfc.common.blocks.plant.fruit.FruitTreeSaplingBlock.*;

public class FruitTreeBranchBlock extends PipePlantBlock implements HoeOverlayBlock
{
    public static final IntegerProperty STAGE = TFCBlockStateProperties.STAGE_3;
    private final Supplier<ClimateRange> climateRange;

    public FruitTreeBranchBlock(ExtendedProperties properties, Supplier<ClimateRange> climateRange)
    {
        super(0.25F, properties);
        this.climateRange = climateRange;
        registerDefaultState(stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, false));
    }

    @Override
    public void addHoeOverlayInfo(Level level, BlockPos pos, BlockState state, Consumer<Component> text, boolean isDebug)
    {
        final ClimateRange range = climateRange.get();

        text.accept(FarmlandBlock.getHydrationTooltip(level, pos, range, false, FruitTreeLeavesBlock.getHydration(level, pos)));
        text.accept(FarmlandBlock.getAverageTemperatureTooltip(level, pos, range, false));
        if (maySplice(level, pos.above(), level.getBlockState(pos.above())))
        {
            text.accept(Component.translatable("tfc.tooltip.fruit_tree.sapling_splice"));
        }
        addExtraInfo(text);
    }

    public void addExtraInfo(Consumer<Component> text)
    {
        text.accept(Component.translatable("tfc.tooltip.fruit_tree.done_growing"));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(STAGE));
    }

    @Override
    protected boolean testDown(BlockState state)
    {
        return Helpers.isBlock(state, TFCTags.Blocks.FRUIT_TREE_BRANCH) || Helpers.isBlock(state, TFCTags.Blocks.BUSH_PLANTABLE_ON);
    }

    @Override
    protected boolean testUp(BlockState state)
    {
        return Helpers.isBlock(state, TFCTags.Blocks.FRUIT_TREE_BRANCH) || Helpers.isBlock(state, TFCTags.Blocks.FRUIT_TREE_SAPLING);
    }

    @Override
    protected boolean testHorizontal(BlockState state)
    {
        return Helpers.isBlock(state, TFCTags.Blocks.FRUIT_TREE_BRANCH);
    }

    @Override
    protected boolean canGrowLongSideways()
    {
        return true;
    }
}
