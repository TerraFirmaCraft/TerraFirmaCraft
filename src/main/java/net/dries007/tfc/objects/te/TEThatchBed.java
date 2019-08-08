/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nullable;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.EnumFacing;

import mcp.MethodsReturnNonnullByDefault;

@MethodsReturnNonnullByDefault
public class TEThatchBed extends TileEntityBed
{
    @Nullable
    public EnumFacing getBlockFacing()
    {
        if (this.hasWorld())
        {
            return getWorld().getBlockState(pos).getValue(BlockHorizontal.FACING);
        }
        return null;
    }
}
