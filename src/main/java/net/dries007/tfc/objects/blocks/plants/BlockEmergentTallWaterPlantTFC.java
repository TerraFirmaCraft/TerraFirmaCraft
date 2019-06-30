/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.plants;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.property.ITallPlant;

import static net.dries007.tfc.world.classic.ChunkGenTFC.SALT_WATER;

@ParametersAreNonnullByDefault
public class BlockEmergentTallWaterPlantTFC extends BlockTallWaterPlantTFC implements ITallPlant
{
    private static final Map<Plant, BlockEmergentTallWaterPlantTFC> MAP = new HashMap<>();

    public static BlockEmergentTallWaterPlantTFC get(Plant plant)
    {
        return BlockEmergentTallWaterPlantTFC.MAP.get(plant);
    }

    public BlockEmergentTallWaterPlantTFC(Plant plant)
    {
        super(plant);
        if (MAP.put(plant, this) != null) throw new IllegalStateException("There can only be one.");
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        IBlockState water = plant.getWaterType();
        int i;
        //noinspection StatementWithEmptyBody
        for (i = 1; worldIn.getBlockState(pos.down(i)).getBlock() == this; ++i) ;
        if (water == SALT_WATER)
            return i < plant.getMaxHeight() && (worldIn.isAirBlock(pos.up()) || BlocksTFC.isSaltWater(worldIn.getBlockState(pos.up()))) && canBlockStay(worldIn, pos.up(), state);
        else
            return i < plant.getMaxHeight() && (worldIn.isAirBlock(pos.up()) || BlocksTFC.isFreshWater(worldIn.getBlockState(pos.up()))) && canBlockStay(worldIn, pos.up(), state);
    }

    public void shrink(World worldIn, BlockPos pos)
    {
        boolean flag = false;
        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
        {
            if (BlocksTFC.isWater(worldIn.getBlockState(pos.offset(enumfacing))))
            {
                flag = true;
            }
        }

        if (flag) worldIn.setBlockState(pos, plant.getWaterType());
        else worldIn.setBlockToAir(pos);
        worldIn.getBlockState(pos).neighborChanged(worldIn, pos.down(), this, pos);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        IBlockState soil = worldIn.getBlockState(pos.down());
        if (plant.getWaterType() == SALT_WATER)
            return (soil.getBlock() == this || BlocksTFC.isSaltWater(worldIn.getBlockState(pos))) && this.canSustainBush(soil);
        return (soil.getBlock() == this || BlocksTFC.isFreshWater(worldIn.getBlockState(pos))) && this.canSustainBush(soil);
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
        this.onBlockHarvested(world, pos, state, player);

        boolean flag = false;
        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
        {
            if (BlocksTFC.isWater(world.getBlockState(pos.offset(enumfacing))))
            {
                flag = true;
            }
        }

        if (flag) return world.setBlockState(pos, plant.getWaterType(), world.isRemote ? 11 : 3);
        else return world.setBlockState(pos, net.minecraft.init.Blocks.AIR.getDefaultState(), world.isRemote ? 11 : 3);
    }

    @Override
    protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!this.canBlockStay(worldIn, pos, state))
        {
            boolean flag = false;
            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
            {
                if (BlocksTFC.isWater(worldIn.getBlockState(pos.offset(enumfacing))))
                {
                    flag = true;
                }
            }

            this.dropBlockAsItem(worldIn, pos, state, 0);
            if (flag) worldIn.setBlockState(pos, plant.getWaterType());
            else worldIn.setBlockToAir(pos);
        }
    }
}
