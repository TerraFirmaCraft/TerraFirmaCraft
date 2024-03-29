/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;

/**
 * Blocks that can be turned into their mud forms
 */
public interface IMudBlock
{
    /**
     * Gets the mud block this block will transform into when interacted with
     */
    BlockState getMud();

    /**
     * Transforms this block into mud if the player has the required amount of water in their container
     */
    default InteractionResult transformToMud(BlockState mud, Level level, BlockPos pos, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        final int waterRequired = 100;
        final FluidStack water = new FluidStack(Fluids.WATER, waterRequired);

        if (stack.getItem().equals(Items.WATER_BUCKET))
        {
            // Unable to deplete fluid in bucket, ignore interaction
            return InteractionResult.PASS;
        }

        if (PotionUtils.getPotion(stack) == Potions.WATER)
        {
            if (!player.isCreative())
            {
                player.setItemInHand(hand, new ItemStack(Items.GLASS_BOTTLE));
            }

            setMud(mud, level, pos, water);
            return InteractionResult.SUCCESS;
        }

        IFluidHandler fluidHandler = Helpers.getCapability(stack, Capabilities.FLUID_ITEM);

        if (fluidHandler != null && fluidHandler.getFluidInTank(0).containsFluid(water))
        {
            fluidHandler.drain(waterRequired, player.isCreative() ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);

            setMud(mud, level, pos, water);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    /**
     * Sets the block to mud and plays the appropriate particles and sound
     * Particles like {@link net.minecraft.world.item.PotionItem}
     */
    default void setMud(BlockState mud, Level level, BlockPos pos, FluidStack water) {
        FluidHelpers.playTransferSound(level, pos, water, FluidHelpers.Transfer.DRAIN);
        level.setBlockAndUpdate(pos, mud);

        // Particles
        if (!level.isClientSide)
        {
            for (int i = 0; i < 5; ++i)
            {
                ((ServerLevel) level).sendParticles(
                    ParticleTypes.SPLASH,
                    (double) pos.getX() + level.random.nextDouble(),
                    (double) pos.getY() + 1,
                    (double) pos.getZ() + level.random.nextDouble(),
                    1, 0.0, 0.0, 0.0, 1.0);
            }
        }
    }
}
