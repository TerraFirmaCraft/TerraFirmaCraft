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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.FluidHelpers.Transfer;
import net.dries007.tfc.config.TFCConfig;
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
     * Particles like {@link net.minecraft.world.item.PotionItem}
     */
    default InteractionResult transformToMud(BlockState mud, Level level, BlockPos pos, Player player, InteractionHand hand)
    {
        if (!TFCConfig.SERVER.enableDirtToMudCreation.get())
        {
            return InteractionResult.PASS;
        }

        final ItemStack held = player.getItemInHand(hand);
        final int waterRequired = 100;
        final FluidStack water = new FluidStack(Fluids.WATER, waterRequired);

        final IFluidHandler fluidHandler = Helpers.getCapability(held, Capabilities.FLUID_ITEM);

        if (fluidHandler != null)
        {
            final FluidStack simulatedDrained = fluidHandler.drain(waterRequired, IFluidHandler.FluidAction.SIMULATE);

            if (simulatedDrained.containsFluid(water))
            {
                fluidHandler.drain(waterRequired, IFluidHandler.FluidAction.EXECUTE);
                level.setBlockAndUpdate(pos, mud);
                FluidHelpers.playTransferSound(level, pos, water, Transfer.DRAIN);

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

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }
}
