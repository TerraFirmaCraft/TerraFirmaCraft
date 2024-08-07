/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.blockentities.BarrelInventoryCallback;
import net.dries007.tfc.common.blocks.devices.BarrelBlock;
import net.dries007.tfc.common.blocks.devices.SealableDeviceBlock;
import net.dries007.tfc.common.capabilities.DelegateFluidHandler;
import net.dries007.tfc.common.capabilities.FluidTankCallback;
import net.dries007.tfc.common.container.ISlotCallback;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.BlockItemPlacement;
import net.dries007.tfc.util.Helpers;

public class BarrelBlockItem extends TooltipBlockItem implements Rackable
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

    @Override
    public boolean useOnRack(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        final ItemStack item = player.getItemInHand(hand);
        if (item.getItem() instanceof BarrelBlockItem blockItem)
        {
            BlockState barrelState = blockItem.getStateForPlacement(level, pos)
                .setValue(BarrelBlock.FACING, player.getDirection().getOpposite())
                .setValue(BarrelBlock.RACK, true)
                .setValue(BarrelBlock.SEALED, true);
            barrelState = BlockItemPlacement.updateBlockStateFromTag(pos, level, item, barrelState);
            level.setBlockAndUpdate(pos, barrelState);
            BlockItem.updateCustomBlockEntityTag(level, player, pos, item);
            if (!player.isCreative()) item.shrink(1);
            Helpers.playPlaceSound(level, pos, state);
            return true;
        }
        return false;
    }

    private InteractionResult tryInteractWithFluid(Level level, Player player, InteractionHand hand)
    {
        final ItemStack stack = player.getItemInHand(hand);
        final @Nullable IFluidHandler handler = stack.getCapability(Capabilities.FluidHandler.ITEM);
        if (handler == null)
        {
            return InteractionResult.PASS;
        }

        final BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (FluidHelpers.transferBetweenWorldAndItem(stack, level, hit, player, hand, false, false, true))
        {
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack)
    {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack)
    {
        return getDefaultInstance();
    }

    public BlockState getStateForPlacement(LevelAccessor level, BlockPos pos)
    {
        return ((SealableDeviceBlock) getBlock()).getStateForPlacement(level, pos);
    }

    private static class BarrelItemStackInventory implements DelegateFluidHandler, IFluidHandlerItem, ISlotCallback, FluidTankCallback, BarrelInventoryCallback
    {
        private final ItemStack stack;
        private final BarrelBlockEntity.BarrelInventory inventory;
        private boolean hasActiveRecipe;

        BarrelItemStackInventory(ItemStack stack)
        {
            this.stack = stack;
            this.inventory = new BarrelBlockEntity.BarrelInventory(this);
            this.hasActiveRecipe = false;
        }

        @Override
        public boolean canModify()
        {
            return true; // todo 1.21, sealed barrel block entity components
            //return stack.getTag() == null || !hasActiveRecipe; // As long as not sealed, or sealed but with no active recipe.
        }

        @Override
        public void fluidTankChanged()
        {

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
    }
}
