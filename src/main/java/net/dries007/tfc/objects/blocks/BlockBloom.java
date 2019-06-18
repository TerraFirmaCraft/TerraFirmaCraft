/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.objects.te.TEBloom;
import net.dries007.tfc.util.Helpers;

@ParametersAreNonnullByDefault
public class BlockBloom extends Block
{
    public BlockBloom()
    {
        super(Material.IRON);
        setHardness(3.0f);
        setHarvestLevel("pickaxe", 0);
        setSoundType(SoundType.STONE);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TEBloom te = Helpers.getTE(worldIn, pos, TEBloom.class);
        if (te != null)
        {
            te.onBreakBlock(worldIn, pos);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TEBloom();
    }

}
