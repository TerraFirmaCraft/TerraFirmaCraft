/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.blockentities.BarrelInventoryCallback;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.capabilities.DelegateFluidHandler;
import net.dries007.tfc.common.capabilities.FluidTankCallback;
import net.dries007.tfc.common.container.ISlotCallback;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BarrelBlockItem extends BlockItem
{
    public BarrelBlockItem(Block block, Properties properties)
    {
        super(block, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        final InteractionResult result = tryInteractWithFluid(level, player, hand);
        if (result != InteractionResult.PASS)
        {
            return new InteractionResultHolder<>(result, player.getItemInHand(hand));
        }
        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        // We override both `use` and `useOn`, as we want fluid filling to take priority, if we can place a block too.
        // The `super` call here is what eventually does block placement, and will call `use` if not.
        // `use` is called directly in the event we don't target a block at all.
        final Player player = context.getPlayer();
        if (player != null)
        {
            final InteractionResult result = tryInteractWithFluid(context.getLevel(), player, context.getHand());
            if (result != InteractionResult.PASS)
            {
                return result;
            }
        }
        return super.useOn(context);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new BarrelItemStackInventory(stack);
    }

    private InteractionResult tryInteractWithFluid(Level level, Player player, InteractionHand hand)
    {
        final ItemStack stack = player.getItemInHand(hand);
        final IFluidHandler handler = Helpers.getCapability(stack, Capabilities.FLUID_ITEM);
        if (handler == null)
        {
            return InteractionResult.PASS;
        }

        final BlockHitResult hit = Helpers.rayTracePlayer(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (FluidHelpers.transferBetweenWorldAndItem(stack, level, hit, player, hand, false, false, true))
        {
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private static class BarrelItemStackInventory implements ICapabilityProvider, DelegateFluidHandler, IFluidHandlerItem, ISlotCallback, FluidTankCallback, BarrelInventoryCallback
    {
        private final LazyOptional<BarrelItemStackInventory> capability;
        private final ItemStack stack;
        private final BarrelBlockEntity.BarrelInventory inventory;
        private boolean hasActiveRecipe;

        BarrelItemStackInventory(ItemStack stack)
        {
            this.capability = LazyOptional.of(() -> this);
            this.stack = stack;
            this.inventory = new BarrelBlockEntity.BarrelInventory(this);
            this.hasActiveRecipe = false;

            load();
        }

        @Override
        public boolean canModify()
        {
            return stack.getTag() == null || !hasActiveRecipe; // As long as not sealed, or sealed but with no active recipe.
        }

        @Override
        public void fluidTankChanged()
        {
            save();
        }

        @Override
        public IFluidHandler getFluidHandler()
        {
            return inventory;
        }

        @NotNull
        @Override
        public ItemStack getContainer()
        {
            return stack.copy();
        }

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
        {
            if (cap == Capabilities.FLUID_ITEM || cap == Capabilities.FLUID)
            {
                return capability.cast();
            }
            return LazyOptional.empty();
        }

        private void load()
        {
            final CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains(Helpers.BLOCK_ENTITY_TAG, Tag.TAG_COMPOUND))
            {
                final CompoundTag blockEntityTag = tag.getCompound(Helpers.BLOCK_ENTITY_TAG);

                hasActiveRecipe = blockEntityTag.contains("recipe", Tag.TAG_STRING);
                inventory.deserializeNBT(blockEntityTag.getCompound("inventory"));
            }
        }

        private void save()
        {
            if (inventory.isInventoryEmpty())
            {
                stack.removeTagKey(Helpers.BLOCK_ENTITY_TAG);
            }
            else
            {
                stack.getOrCreateTagElement(Helpers.BLOCK_ENTITY_TAG).put("inventory", inventory.serializeNBT());
            }
        }
    }
}
