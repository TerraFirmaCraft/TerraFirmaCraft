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
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
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
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.devices.SluiceBlock;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.items.PanItem;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class SluiceBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    public static final int MAX_SOIL = 32;

    private static final Component NAME = new TranslatableComponent(MOD_ID + ".tile_entity.sluice");

    public static void serverTick(Level level, BlockPos pos, BlockState state, SluiceBlockEntity sluice)
    {
        if (!state.getValue(SluiceBlock.UPPER)) return; // only tick the top block
        if (sluice.hasFlow() && --sluice.ticksRemaining <= 0) // consume a single ore block
        {
            boolean itemUsed = false;
            for (ItemStack stack : Helpers.iterate(sluice.inventory))
            {
                final Item item = stack.getItem();
                if (!stack.isEmpty() && item instanceof BlockItem blockItem)
                {
                    PanItem.dropItems((ServerLevel) level, blockItem.getBlock().defaultBlockState(), sluice.getWaterOutputPos());
                    stack.setCount(0);
                    itemUsed = true;
                    break;
                }
            }
            if (itemUsed) Helpers.playSound(level, sluice.getBlockPos(), SoundEvents.ITEM_PICKUP);
            sluice.ticksRemaining = TFCConfig.SERVER.sluiceTicks.get();
            sluice.markForSync();
        }
        else if (level.getGameTime() % 20 == 0)
        {
            Fluid fluid = level.getFluidState(sluice.getWaterInputPos()).getType();
            if (!fluid.isSame(Fluids.EMPTY) && fluid.is(FluidTags.WATER)) // attempt to let water flow through the sluice
            {
                final BlockPos outputPos = sluice.getWaterOutputPos();
                if (level.getBlockState(outputPos).getMaterial().isReplaceable())
                {
                    FluidHelpers.setSourceBlock(level, outputPos, fluid);
                }
            }
            // Consume inputs
            for (ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(1D), entity -> !entity.isRemoved()))
            {
                ItemStack stack = entity.getItem();
                if (stack.getItem() instanceof BlockItem blockItem)
                {
                    Block block = blockItem.getBlock();
                    if (block.defaultBlockState().is(TFCTags.Blocks.CAN_BE_PANNED))
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
        return getBlockPos().below().relative(getFacing(), 2);
    }

    public BlockPos getWaterInputPos()
    {
        return getBlockPos().above().relative(getFacing().getOpposite());
    }

    private boolean hasFlow()
    {
        assert level != null;
        FluidState inputState = level.getFluidState(getWaterInputPos());
        Fluid input = inputState.getType();
        Fluid output = level.getFluidState(getWaterOutputPos()).getType();
        if (inputState.hasProperty(FlowingFluid.LEVEL) && inputState.getValue(FlowingFluid.LEVEL) == 1)
        {
            return input.is(FluidTags.WATER) && output.isSame(input);
        }
        return false;
    }
}
