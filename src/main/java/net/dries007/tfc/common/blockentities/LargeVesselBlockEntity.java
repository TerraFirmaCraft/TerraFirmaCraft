/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.blocks.LargeVesselBlock;
import net.dries007.tfc.common.blocks.devices.SealableDeviceBlock;
import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodTraits;
import net.dries007.tfc.common.component.item.ItemListComponent;
import net.dries007.tfc.common.component.size.ItemSizeManager;
import net.dries007.tfc.common.component.size.Size;
import net.dries007.tfc.common.container.LargeVesselContainer;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class LargeVesselBlockEntity extends InventoryBlockEntity<LargeVesselBlockEntity.VesselInventory>
{
    public static final int SLOTS = 9;

    public LargeVesselBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.LARGE_VESSEL.get(), pos, state);
    }

    public LargeVesselBlockEntity(BlockEntityType<? extends LargeVesselBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, VesselInventory::new);
        if (TFCConfig.SERVER.largeVesselEnableAutomation.get())
        {
            sidedInventory.on(new PartialItemHandler(inventory).insert(0, 1, 2, 3, 4, 5, 6, 7, 8), d -> d != Direction.DOWN);
            sidedInventory.on(new PartialItemHandler(inventory).extract(0, 1, 2, 3, 4, 5, 6, 7, 8), Direction.DOWN);
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory inv, Player player)
    {
        return LargeVesselContainer.create(this, inv, windowID);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput components)
    {
        final List<ItemStack> content = components.getOrDefault(TFCComponents.CONTENTS, ItemListComponent.EMPTY).contents();
        Helpers.copyFrom(content, inventory);
        super.applyImplicitComponents(components);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder)
    {
        if (getBlockState().getValue(SealableDeviceBlock.SEALED))
        {
            builder.set(TFCComponents.CONTENTS, ItemListComponent.of(inventory));
        }
        super.collectImplicitComponents(builder);
    }

    public void onUnseal()
    {
        assert level != null;
        for (int i = 0; i < SLOTS; i++)
        {
            inventory.setStackInSlot(i, FoodCapability.removeTrait(inventory.getStackInSlot(i).copy(), FoodTraits.PRESERVED));
        }
        Helpers.playSound(level, worldPosition, TFCSounds.OPEN_VESSEL.get());
    }

    public void onSeal()
    {
        assert level != null;
        for (int i = 0; i < SLOTS; i++)
        {
            inventory.setStackInSlot(i, FoodCapability.applyTrait(inventory.getStackInSlot(i).copy(), FoodTraits.PRESERVED));
        }
        Helpers.playSound(level, worldPosition, TFCSounds.CLOSE_VESSEL.get());
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
