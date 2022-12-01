/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.INBTSerializable;

import net.dries007.tfc.common.blocks.LargeVesselBlock;
import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTraits;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.capabilities.size.Size;
import net.dries007.tfc.common.container.LargeVesselContainer;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class LargeVesselBlockEntity extends InventoryBlockEntity<LargeVesselBlockEntity.VesselInventory>
{
    public static final int SLOTS = 9;
    private static final Component NAME = Helpers.translatable(MOD_ID + ".block_entity.large_vessel");

    public LargeVesselBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.LARGE_VESSEL.get(), pos, state);
    }

    public LargeVesselBlockEntity(BlockEntityType<? extends LargeVesselBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, VesselInventory::new, NAME);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory inv, Player player)
    {
        return LargeVesselContainer.create(this, inv, windowID);
    }

    public void onUnseal()
    {
        for (int i = 0; i < SLOTS; i++)
        {
            inventory.setStackInSlot(i, FoodCapability.removeTrait(inventory.getStackInSlot(i).copy(), FoodTraits.PRESERVED));
        }
    }

    public void onSeal()
    {
        for (int i = 0; i < SLOTS; i++)
        {
            inventory.setStackInSlot(i, FoodCapability.applyTrait(inventory.getStackInSlot(i).copy(), FoodTraits.PRESERVED));
        }
    }

    public static class VesselInventory extends InventoryItemHandler implements INBTSerializable<CompoundTag>
    {
        private final LargeVesselBlockEntity vessel;

        VesselInventory(InventoryBlockEntity<?> entity)
        {
            super(entity, SLOTS);
            vessel = (LargeVesselBlockEntity) entity;
        }

        @NotNull
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
        {
            return canModify() ? super.insertItem(slot, stack, simulate) : stack;
        }

        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            return canModify() ? super.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack)
        {
            return canModify() && ItemSizeManager.get(stack).getSize(stack).isSmallerThan(Size.LARGE) && super.isItemValid(slot, stack);
        }

        private boolean canModify()
        {
            return !vessel.getBlockState().getValue(LargeVesselBlock.SEALED);
        }
    }
}
