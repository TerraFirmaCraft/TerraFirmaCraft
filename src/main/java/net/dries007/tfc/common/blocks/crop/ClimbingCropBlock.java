/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.crop;


import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.IGhostBlockHandler;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.ClimateRange;
import net.dries007.tfc.util.climate.ClimateRanges;

public abstract class ClimbingCropBlock extends DoubleCropBlock implements IGhostBlockHandler
{
    public static final BooleanProperty STICK = TFCBlockStateProperties.STICK;

    public static ClimbingCropBlock create(ExtendedProperties properties, int singleStages, int doubleStages, Crop crop)
    {
        final IntegerProperty property = TFCBlockStateProperties.getAgeProperty(singleStages + doubleStages - 1);
        return new ClimbingCropBlock(properties, singleStages - 1, singleStages + doubleStages - 1, TFCBlocks.DEAD_CROPS.get(crop), TFCItems.CROP_SEEDS.get(crop), crop.getPrimaryNutrient(), ClimateRanges.CROPS.get(crop))
        {
            @Override
            public IntegerProperty getAgeProperty()
            {
                return property;
            }
        };
    }

    protected ClimbingCropBlock(ExtendedProperties properties, int maxSingleAge, int maxAge, Supplier<? extends Block> dead, Supplier<? extends Item> seeds, FarmlandBlockEntity.NutrientType primaryNutrient, Supplier<ClimateRange> climateRange)
    {
        super(properties, maxSingleAge, maxAge, dead, seeds, primaryNutrient, climateRange);
        registerDefaultState(getStateDefinition().any().setValue(STICK, false).setValue(PART, Part.BOTTOM));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        final ItemStack heldStack = player.getItemInHand(hand);
        final BlockPos posAbove = pos.above();
        if (Helpers.isItem(heldStack.getItem(), Tags.Items.RODS_WOODEN) && !state.getValue(STICK) && level.isEmptyBlock(posAbove) && posAbove.getY() <= level.getMaxBuildHeight())
        {
            if (!level.isClientSide())
            {
                level.setBlock(pos, state.setValue(STICK, true), Block.UPDATE_CLIENTS);
                level.setBlock(pos.above(), state.setValue(STICK, true).setValue(PART, Part.TOP), Block.UPDATE_ALL);
                if (!player.isCreative()) heldStack.shrink(1);
                Helpers.playSound(level, pos, TFCSounds.CROP_STICK_ADD.get());
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(STICK));
    }

    @Override
    public float getGrowthLimit(Level level, BlockPos pos, BlockState state)
    {
        if (!CropHelpers.lightValid(level, pos))
        {
            return 0f;
        }
        final BlockState stateAbove = level.getBlockState(pos.above());
        return stateAbove.getBlock() == this && stateAbove.getValue(STICK) && stateAbove.getValue(PART) == Part.TOP ? CropHelpers.GROWTH_LIMIT : maxSingleGrowth;
    }

    @Override
    public void die(Level level, BlockPos pos, BlockState state, boolean fullyGrown)
    {
        final BlockPos posAbove = pos.above();
        final BlockState stateAbove = level.getBlockState(posAbove);
        final boolean hasTop = stateAbove.getBlock() == this;
        final BlockState deadState = dead.get().defaultBlockState().setValue(DeadCropBlock.MATURE, fullyGrown).setValue(STICK, state.getValue(STICK));
        if (hasTop)
        {
            level.setBlock(posAbove, deadState.setValue(DeadDoubleCropBlock.PART, Part.TOP), Block.UPDATE_CLIENTS);
        }
        else
        {
            level.destroyBlock(posAbove, false);
        }
        level.setBlockAndUpdate(pos, deadState.setValue(DeadDoubleCropBlock.PART, Part.BOTTOM));
    }

    @Nullable
    @Override
    public BlockState getStateToDraw(Level level, Player player, BlockState state, Direction direction, BlockPos pos, double x, double y, double z, ItemStack item)
    {
        BlockPos abovePos = pos.above();
        if (Helpers.isItem(item.getItem(), Tags.Items.RODS_WOODEN) && !state.getValue(STICK) && level.isEmptyBlock(abovePos) && abovePos.getY() <= level.getMaxBuildHeight())
        {
            return state.setValue(STICK, true);
        }
        return null;
    }
}