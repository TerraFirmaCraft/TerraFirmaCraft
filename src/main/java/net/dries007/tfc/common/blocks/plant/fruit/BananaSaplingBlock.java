/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.ClimateRanges;

public class BananaSaplingBlock extends FruitTreeSaplingBlock
{
    public BananaSaplingBlock(ExtendedProperties properties, Lifecycle[] stages, Supplier<? extends Block> block, Supplier<Integer> treeGrowthDays)
    {
        super(properties, block, treeGrowthDays, ClimateRanges.BANANA_PLANT, stages);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void createTree(Level level, BlockPos pos, BlockState state, RandomSource random)
    {
        level.setBlockAndUpdate(pos, block.get().defaultBlockState().setValue(SeasonalPlantBlock.LIFECYCLE, Lifecycle.HEALTHY));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        return Helpers.isBlock(level.getBlockState(pos.below()), TFCTags.Blocks.BUSH_PLANTABLE_ON);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {}
}
