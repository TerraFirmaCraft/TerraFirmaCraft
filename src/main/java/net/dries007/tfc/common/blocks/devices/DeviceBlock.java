/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.util.Helpers;

/**
 * Base class for blocks which:
 * <ul>
 *     <li>Have a block entity, of the {@link InventoryBlockEntity} variety.</li>
 *     <li>Use both {@link ExtendedProperties} and {@link EntityBlockExtension}.</li>
 * </ul>
 * In addition, this class integrates with vanilla's block entity tag system for saving block entities, if desired.
 */
public class DeviceBlock extends ExtendedBlock implements IForgeBlockExtension, EntityBlockExtension
{
    private final InventoryRemoveBehavior removeBehavior;

    public DeviceBlock(ExtendedProperties properties, InventoryRemoveBehavior removeBehavior)
    {
        super(properties);

        this.removeBehavior = removeBehavior;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        final BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof InventoryBlockEntity<?> inv && !(Helpers.isBlock(state, newState.getBlock())))
        {
            beforeRemove(inv);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
    {
        final ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
        if (removeBehavior == InventoryRemoveBehavior.SAVE)
        {
            final BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof InventoryBlockEntity<?> inv)
            {
                inv.saveToItem(stack, level.registryAccess());
            }
        }
        return stack;
    }

    protected void beforeRemove(InventoryBlockEntity<?> entity)
    {
        if (removeBehavior == InventoryRemoveBehavior.DROP)
        {
            entity.ejectInventory();
        }
    }

    protected enum InventoryRemoveBehavior
    {
        NOOP, DROP, SAVE
    }
}
