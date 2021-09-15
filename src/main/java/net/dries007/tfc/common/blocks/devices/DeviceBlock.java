/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;

/**
 * Base class for blocks which:
 * - Have a block entity, of the {@link InventoryBlockEntity} variety.
 * - Use both {@link ExtendedProperties} and {@link EntityBlockExtension}.
 *
 * In addition, this class integrates with vanilla's block entity tag system for saving block entities, if desired.
 *
 * @see net.minecraft.world.item.BlockItem#updateCustomBlockEntityTag(Level, Player, BlockPos, ItemStack)
 */
public class DeviceBlock extends Block implements IForgeBlockExtension, EntityBlockExtension
{
    private final ExtendedProperties properties;

    public DeviceBlock(ExtendedProperties properties)
    {
        super(properties.properties());
        this.properties = properties;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        final BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof InventoryBlockEntity<?> inv && !(state.is(newState.getBlock())))
        {
            beforeRemove(inv);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        final BlockEntity entity = level.getBlockEntity(pos);
        if (stack.hasCustomHoverName() && entity instanceof InventoryBlockEntity<?> inv)
        {
            inv.setCustomName(stack.getHoverName());
        }
    }

    @Override
    public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player)
    {
        final ItemStack stack = super.getPickBlock(state, target, world, pos, player);
        if (properties.getDeviceInventoryRemoveMode() == ExtendedProperties.Mode.SAVE)
        {
            final BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof InventoryBlockEntity<?> inv)
            {
                stack.addTagElement(BlockItem.BLOCK_ENTITY_TAG, inv.save(new CompoundTag()));
            }
        }
        return stack;
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    protected void beforeRemove(InventoryBlockEntity<?> entity)
    {
        if (properties.getDeviceInventoryRemoveMode() == ExtendedProperties.Mode.DUMP)
        {
            entity.ejectInventory();
        }
        entity.invalidateCapabilities();
    }
}
