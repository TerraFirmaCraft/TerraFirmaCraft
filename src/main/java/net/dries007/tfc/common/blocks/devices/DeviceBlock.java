/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.IForgeBlockProperties;
import net.dries007.tfc.common.tileentity.InventoryTileEntity;
import net.dries007.tfc.util.Helpers;

/**
 * Helper class for blocks attached to InventoryTileEntity when they break
 */
public class DeviceBlock extends Block implements IForgeBlockProperties
{
    private final ForgeBlockProperties properties;

    public DeviceBlock(ForgeBlockProperties properties)
    {
        super(properties.properties());
        this.properties = properties;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving)
    {
        InventoryTileEntity<?> entity = Helpers.getTileEntity(world, pos, InventoryTileEntity.class);
        if (entity != null && !(state.is(newState.getBlock())))
        {
            beforeRemovingTileEntity(entity);
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }

    @Override
    public ForgeBlockProperties getForgeProperties()
    {
        return properties;
    }

    protected void beforeRemovingTileEntity(InventoryTileEntity<?> entity)
    {
        entity.ejectInventory();
        entity.invalidateCapabilities();
    }
}
