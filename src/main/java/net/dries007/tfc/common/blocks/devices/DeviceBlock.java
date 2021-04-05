package net.dries007.tfc.common.blocks.devices;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
    {
        InventoryTileEntity te = Helpers.getTileEntity(world, pos, InventoryTileEntity.class);
        if (te != null && !(state.is(newState.getBlock())))
        {
            te.onRemove();
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }

    @Override
    public ForgeBlockProperties getForgeProperties()
    {
        return properties;
    }
}
