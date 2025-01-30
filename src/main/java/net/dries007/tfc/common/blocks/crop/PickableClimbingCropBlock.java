/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.crop;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.CropBlockEntity;
import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.climate.ClimateRange;
import net.dries007.tfc.util.climate.ClimateRanges;

public abstract class PickableClimbingCropBlock extends ClimbingCropBlock implements IPickableCrop
{
    public static PickableClimbingCropBlock create(ExtendedProperties properties, int singleStages, int doubleStages, Crop crop, @Nullable Supplier<Supplier<? extends Item>> fruit, Supplier<Supplier<? extends Item>> matureFruit)
    {
        final IntegerProperty property = TFCBlockStateProperties.getAgeProperty(singleStages + doubleStages - 1);
        return new PickableClimbingCropBlock(properties, singleStages - 1, singleStages + doubleStages - 1, TFCBlocks.DEAD_CROPS.get(crop), TFCItems.CROP_SEEDS.get(crop), crop.getPrimaryNutrient(), ClimateRanges.CROPS.get(crop), fruit, matureFruit)
        {
            @Override
            public IntegerProperty getAgeProperty()
            {
                return property;
            }
        };
    }

    private final @Nullable  Supplier<Supplier<? extends Item>> fruit;
    private final Supplier<Supplier<? extends Item>> matureFruit;

    protected PickableClimbingCropBlock(ExtendedProperties properties, int maxSingleAge, int maxAge, Supplier<? extends Block> dead, Supplier<? extends Item> seeds, FarmlandBlockEntity.NutrientType primaryNutrient, Supplier<ClimateRange> climateRange, @Nullable Supplier<Supplier<? extends Item>> fruit, Supplier<Supplier<? extends Item>> matureFruit)
    {
        super(properties, maxSingleAge, maxAge, dead, seeds, primaryNutrient, climateRange);
        this.fruit = fruit;
        this.matureFruit = matureFruit;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        final ItemInteractionResult res = super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        if (res.consumesAction())
        {
            return res; // use fertilizer
        }
        final BlockState belowState = level.getBlockState(pos.below());
        if (state.getValue(PART) == Part.TOP && belowState.getBlock() instanceof PickableClimbingCropBlock)
        {
            return useItemOn(stack, belowState, level, pos.below(), player, hand, hitResult);
        }
        if (level.getBlockEntity(pos) instanceof CropBlockEntity crop)
        {
            final CropBlock cropBlock = (CropBlock) state.getBlock();
            final float yield = crop.getYield();
            final int age = state.getValue(cropBlock.getAgeProperty());
            final RandomSource random = level.getRandom();
            final int maxAge = cropBlock.getMaxAge();
            if (age == maxAge - 1 && getFirstFruit() != null)
            {
                crop.setGrowth(Mth.nextFloat(random, 0.4f, 0.5f));
                crop.setYield(0f);
                cropBlock.postGrowthTick(level, pos, state, crop);
                ItemHandlerHelper.giveItemToPlayer(player, yieldItemStack(getFirstFruit(), yield, random));
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            else if (age == maxAge)
            {
                crop.setGrowth(Mth.nextFloat(random, 0.5f, 0.6f));
                crop.setYield(0f);
                cropBlock.postGrowthTick(level, pos, state, crop);
                ItemHandlerHelper.giveItemToPlayer(player, yieldItemStack(getSecondFruit(), yield, random));
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void postGrowthTick(Level level, BlockPos pos, BlockState state, CropBlockEntity crop)
    {
        final BlockPos posAbove = pos.above();
        final BlockState stateAbove = level.getBlockState(posAbove);
        int age = Mth.clamp((int) (crop.getGrowth() * getMaxAge()), 0, getMaxAge());
        if (stateAbove.getBlock() instanceof PickableClimbingCropBlock && age <= maxSingleAge)
        {
            age = maxSingleAge + 1;
        }

        level.setBlock(pos, state.setValue(getAgeProperty(), age), Block.UPDATE_CLIENTS);
        if (age > maxSingleAge && (stateAbove.isAir() || stateAbove.getBlock() == this))
        {
            level.setBlock(posAbove, state.setValue(getAgeProperty(), age).setValue(PART, Part.TOP), Block.UPDATE_ALL);
        }
    }

    @Override
    @Nullable
    public Item getFirstFruit()
    {
        return fruit == null ? null : fruit.get().get();
    }

    @Override
    public Item getSecondFruit()
    {
        return matureFruit.get().get();
    }
}
