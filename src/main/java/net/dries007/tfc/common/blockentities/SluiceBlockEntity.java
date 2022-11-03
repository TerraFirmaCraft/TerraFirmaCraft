/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.devices.SluiceBlock;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.items.PanItem;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.loot.TFCLoot;

import org.jetbrains.annotations.Nullable;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class SluiceBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    public static final int MAX_SOIL = 32;

    private static final Component NAME = Helpers.translatable(MOD_ID + ".block_entity.sluice");

    public static void serverTick(Level level, BlockPos pos, BlockState state, SluiceBlockEntity sluice)
    {
        if (!state.getValue(SluiceBlock.UPPER))
        {
            return; // only tick the top block
        }

        final State sluiceState = sluice.getRepresentativeState();
        if (sluiceState == State.NONE)
        {
            return; // Skip all updates
        }

        // If the state is both, aka fully structured, then perform sluice operation
        if (sluiceState == State.BOTH)
        {
            // Consume inputs, once per second
            if (level.getGameTime() % 20 == 0)
            {
                for (ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(1D), entity -> !entity.isRemoved()))
                {
                    ItemStack stack = entity.getItem();
                    if (stack.getItem() instanceof BlockItem blockItem)
                    {
                        Block block = blockItem.getBlock();
                        if (Helpers.isBlock(block.defaultBlockState(), TFCTags.Blocks.CAN_BE_PANNED))
                        {
                            boolean itemUsed = false;
                            for (int slot = 0; slot < sluice.inventory.getSlots(); slot++)
                            {
                                if (sluice.inventory.getStackInSlot(slot).isEmpty())
                                {
                                    ItemStack setStack = stack.copy();
                                    setStack.setCount(1);
                                    sluice.inventory.setStackInSlot(slot, setStack);
                                    itemUsed = true;
                                    break;
                                }
                            }
                            if (itemUsed)
                            {
                                sluice.markForSync();
                                stack.shrink(1);
                                if (entity.getItem().getCount() <= 0)
                                {
                                    entity.setRemoved(Entity.RemovalReason.DISCARDED);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            // Produce ores, every time the sluice finishes
            if (--sluice.ticksRemaining <= 0)
            {
                boolean itemUsed = false;
                for (ItemStack stack : Helpers.iterate(sluice.inventory))
                {
                    final Item item = stack.getItem();
                    if (!stack.isEmpty() && item instanceof BlockItem blockItem)
                    {
                        Helpers.dropWithContext((ServerLevel) level, blockItem.getBlock().defaultBlockState(), sluice.getWaterOutputPos(), ctx -> ctx.withParameter(TFCLoot.SLUICED, true), false);
                        stack.setCount(0);
                        itemUsed = true;
                        break;
                    }
                }
                if (itemUsed) Helpers.playSound(level, sluice.getBlockPos(), SoundEvents.ITEM_PICKUP);
                sluice.ticksRemaining = TFCConfig.SERVER.sluiceTicks.get();
                sluice.markForSync();
            }
        }
        else if (level.getGameTime() % 20 == 0)
        {
            // If only input, attempt to let water flow through the sluice, and pick up entities
            if (sluiceState == State.INPUT_ONLY)
            {
                final Fluid fluid = level.getFluidState(sluice.getWaterInputPos()).getType();
                if (!fluid.isSame(Fluids.EMPTY) && Helpers.isFluid(fluid, TFCTags.Fluids.USABLE_IN_SLUICE))
                {
                    final BlockPos outputPos = sluice.getWaterOutputPos();
                    if (level.getBlockState(outputPos).getMaterial().isReplaceable())
                    {
                        FluidHelpers.setSourceBlock(level, outputPos, fluid);
                    }
                }
            }
            else
            {
                // Assume that we created the output here, and we want to remove it.
                assert sluiceState == State.OUTPUT_ONLY;

                final BlockPos fluidOutputPos = sluice.getWaterOutputPos();
                final Fluid fluid = level.getFluidState(fluidOutputPos).getType();
                if (Helpers.isFluid(fluid, TFCTags.Fluids.USABLE_IN_SLUICE))
                {
                    FluidHelpers.pickupFluid(level, fluidOutputPos, level.getBlockState(fluidOutputPos), IFluidHandler.FluidAction.EXECUTE, f -> {});
                }
            }
        }
    }

    private int ticksRemaining;

    public SluiceBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.SLUICE.get(), pos, state, defaultInventory(MAX_SOIL), NAME);
        ticksRemaining = 0;
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        ticksRemaining = nbt.getInt("ticksRemaining");
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putInt("ticksRemaining", ticksRemaining);
        super.saveAdditional(nbt);
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    public Direction getFacing()
    {
        return getBlockState().getValue(SluiceBlock.FACING);
    }

    public BlockPos getWaterOutputPos()
    {
        return SluiceBlock.getFluidOutputPos(getBlockState(), getBlockPos());
    }

    public BlockPos getWaterInputPos()
    {
        return getBlockPos().above().relative(getFacing().getOpposite());
    }

    @Nullable
    public Fluid getFlow()
    {
        assert level != null;

        final FluidState inputState = level.getFluidState(getWaterInputPos());
        final Fluid input = inputState.getType();
        final Fluid output = level.getFluidState(getWaterOutputPos()).getType();
        if (inputState.hasProperty(FlowingFluid.LEVEL) && inputState.getValue(FlowingFluid.LEVEL) == 1)
        {
            if (Helpers.isFluid(input, TFCTags.Fluids.USABLE_IN_SLUICE) && output.isSame(input))
            {
                return input;
            }
        }
        return null;
    }

    private State getRepresentativeState()
    {
        assert level != null;

        final FluidState inputState = level.getFluidState(getWaterInputPos());
        final Fluid input = inputState.getType();
        final Fluid output = level.getFluidState(getWaterOutputPos()).getType();

        State state = Helpers.isFluid(output, TFCTags.Fluids.USABLE_IN_SLUICE) ? State.OUTPUT_ONLY : State.NONE;
        if (inputState.hasProperty(FlowingFluid.LEVEL) && inputState.getValue(FlowingFluid.LEVEL) == 1)
        {
            if (state == State.OUTPUT_ONLY && output.isSame(input))
            {
                return State.BOTH;
            }
            return State.INPUT_ONLY;
        }
        return state;
    }

    enum State
    {
        NONE, INPUT_ONLY, OUTPUT_ONLY, BOTH;
    }
}
