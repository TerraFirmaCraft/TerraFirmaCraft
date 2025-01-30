/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.crop;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.CropBlockEntity;

public interface IPickableCrop
{
    default ItemStack yieldItemStack(Item item, float yield, RandomSource random)
    {
        return new ItemStack(item, Mth.floor(Mth.lerp(yield, 1f, 5f) + random.nextInt(2)));
    }

    default @Nullable ItemInteractionResult getItemInteractionResult(BlockState state, Level level, BlockPos pos, Player player, CropBlockEntity crop)
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
        return null;
    }

    @Nullable
    Item getFirstFruit();

    Item getSecondFruit();
}
