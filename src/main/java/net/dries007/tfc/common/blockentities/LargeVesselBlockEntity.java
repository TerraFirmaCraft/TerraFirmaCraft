/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandlerModifiable;

import net.dries007.tfc.common.blocks.LargeVesselBlock;
import net.dries007.tfc.common.capabilities.DelegateItemHandler;
import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.container.LargeVesselContainer;
import net.dries007.tfc.common.recipes.inventory.EmptyInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class LargeVesselBlockEntity extends InventoryBlockEntity<LargeVesselBlockEntity.VesselInventory>
{
    public static final int SLOTS = 9;
    private static final Component NAME = new TranslatableComponent(MOD_ID + ".tile_entity.large_vessel");

    public LargeVesselBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.LARGE_VESSEL.get(), pos, state, VesselInventory::new, NAME);
    }

    @Override
    public void saveAdditional(CompoundTag tag)
    {
        super.saveAdditional(tag);
    }

    @Override
    public void loadAdditional(CompoundTag tag)
    {
        super.loadAdditional(tag);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory inv, Player player)
    {
        return LargeVesselContainer.create(this, inv, windowID);
    }

    public static class VesselInventory implements DelegateItemHandler, INBTSerializable<CompoundTag>, EmptyInventory
    {
        private final LargeVesselBlockEntity barrel;
        private final InventoryItemHandler inventory;
        private boolean mutable;

        VesselInventory(InventoryBlockEntity<?> entity)
        {
            barrel = (LargeVesselBlockEntity) entity;
            inventory = new InventoryItemHandler(entity, SLOTS);
        }

        @Override
        public IItemHandlerModifiable getItemHandler()
        {
            return inventory;
        }

        @NotNull
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
        {
            return canModify() ? inventory.insertItem(slot, stack, simulate) : stack;
        }

        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            return canModify() ? inventory.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack)
        {
            return canModify() && DelegateItemHandler.super.isItemValid(slot, stack);
        }

        @Override
        public CompoundTag serializeNBT()
        {
            return inventory.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt)
        {
            inventory.deserializeNBT(nbt);
        }

        private boolean canModify()
        {
            return mutable || !barrel.getBlockState().getValue(LargeVesselBlock.SEALED);
        }
    }
}
