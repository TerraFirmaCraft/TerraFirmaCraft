/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.capabilities.SimpleFluidHandler;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.items.TFCItems;

public final class CauldronInteractions
{
    private static final BiMap<Block, Fluid> CAULDRONS = HashBiMap.create();

    /**
     * {@link net.minecraft.core.cauldron.CauldronInteraction}'s are added to a non-thread-safe map underneath the hood.
     */
    public static void registerCauldronInteractions()
    {
        // Add generic interactions to vanilla cauldrons
        // These handlers will work with any combination of filled / not-filled, as they use a proper
        registerForVanillaCauldrons(TFCItems.WOODEN_BUCKET.get(), CauldronInteractions::interactWithBucket);
        TFCItems.FLUID_BUCKETS.values().forEach(reg -> registerForVanillaCauldrons(reg.get(), CauldronInteractions::interactWithBucket));
        registerForVanillaCauldrons(TFCItems.RED_STEEL_BUCKET.get(), CauldronInteractions::interactWithBucket);
        registerForVanillaCauldrons(TFCItems.BLUE_STEEL_BUCKET.get(), CauldronInteractions::interactWithBucket);

        TFCBlocks.CAULDRONS.forEach((type, reg) -> registerCauldronBlock(reg.get(), type.fluid().get()));

        registerCauldronBlock(Blocks.CAULDRON, Fluids.EMPTY);
        registerCauldronBlock(Blocks.WATER_CAULDRON, Fluids.WATER);
        registerCauldronBlock(Blocks.LAVA_CAULDRON, Fluids.LAVA);
    }

    public static void registerCauldronBlock(Block cauldron, Fluid fluid)
    {
        CAULDRONS.put(cauldron, fluid);
    }

    public static void registerForVanillaCauldrons(Item item, CauldronInteraction interaction)
    {
        CauldronInteraction.EMPTY.put(item, interaction);
        CauldronInteraction.WATER.put(item, interaction);
        CauldronInteraction.LAVA.put(item, interaction);
    }

    public static IFluidHandler createFluidHandler(Level level, BlockPos pos)
    {
        return new CauldronBlockHandler(level, pos);
    }

    public static InteractionResult interactWithBucket(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack)
    {
        if (FluidHelpers.transferBetweenBlockHandlerAndItem(stack, createFluidHandler(level, pos), level, pos, new FluidHelpers.AfterTransferWithPlayer(player, hand)))
        {
            player.awardStat(Stats.USE_CAULDRON);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    record CauldronBlockHandler(Level level, BlockPos pos) implements SimpleFluidHandler
    {
        @NotNull
        @Override
        public FluidStack getFluidInTank(int tank)
        {
            final Fluid fluid = CAULDRONS.get(level.getBlockState(pos).getBlock());
            return fluid != null ? new FluidStack(fluid, FluidHelpers.BUCKET_VOLUME) : FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(int tank)
        {
            return FluidHelpers.BUCKET_VOLUME;
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack)
        {
            return CAULDRONS.inverse().containsKey(stack.getFluid());
        }

        @Override
        public int fill(FluidStack stack, IFluidHandler.FluidAction action)
        {
            if (getFluidInTank(0).isEmpty() && isFluidValid(0, stack) && stack.getAmount() >= FluidHelpers.BUCKET_VOLUME)
            {
                // Attempt to fill an empty cauldron.
                final Block block = CAULDRONS.inverse().get(stack.getFluid());
                if (!action.simulate())
                {
                    BlockState state = block.defaultBlockState();
                    if (state.hasProperty(BlockStateProperties.LEVEL_CAULDRON))
                    {
                        state = state.setValue(BlockStateProperties.LEVEL_CAULDRON, 3);
                    }
                    level.setBlockAndUpdate(pos, state);
                }
                return FluidHelpers.BUCKET_VOLUME;
            }
            return 0;
        }

        @NotNull
        @Override
        public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action)
        {
            final FluidStack stack = getFluidInTank(0);
            if (!stack.isEmpty() && maxDrain >= FluidHelpers.BUCKET_VOLUME)
            {
                if (!action.simulate())
                {
                    level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
                }
                return stack;
            }
            return FluidStack.EMPTY;
        }
    }
}
