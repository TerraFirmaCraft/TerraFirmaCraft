/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.plants;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

@ParametersAreNonnullByDefault
public class BlockHangingPlantTFC extends BlockCreepingPlantTFC implements IGrowable
{
    private static final PropertyBool BOTTOM = PropertyBool.create("bottom");
    private static final Map<Plant, BlockHangingPlantTFC> MAP = new HashMap<>();

    public static BlockHangingPlantTFC get(Plant plant)
    {
        return BlockHangingPlantTFC.MAP.get(plant);
    }

    public BlockHangingPlantTFC(Plant plant)
    {
        super(plant);
        if (MAP.put(plant, this) != null) throw new IllegalStateException("There can only be one.");
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos.down(2));
        Material material = iblockstate.getMaterial();

        int i;
        //noinspection StatementWithEmptyBody
        for (i = 1; worldIn.getBlockState(pos.up(i)).getBlock() == this; ++i) ;
        return i < plant.getMaxHeight() && worldIn.isAirBlock(pos.down()) && ((!material.isSolid() || material == Material.LEAVES)) && canBlockStay(worldIn, pos.down(), state);
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        return false;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        worldIn.setBlockState(pos.down(), this.getDefaultState());
        IBlockState iblockstate = state.withProperty(AGE, 0).withProperty(growthStageProperty, plant.getStageForMonth()).withProperty(BOTTOM, false);
        worldIn.setBlockState(pos, iblockstate);
        iblockstate.neighborChanged(worldIn, pos.down(), this, pos);
    }

    public void shrink(World worldIn, BlockPos pos)
    {
        worldIn.setBlockToAir(pos);
        worldIn.getBlockState(pos).neighborChanged(worldIn, pos.up(), this, pos);
    }

    @Override
    @Nonnull
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        IBlockState actualState = super.getActualState(state, worldIn, pos);
        if (worldIn.getBlockState(pos.down()).getBlock() == this && actualState.getValue(UP) && !actualState.getValue(DOWN) && !actualState.getValue(NORTH) && !actualState.getValue(SOUTH) && !actualState.getValue(EAST) && !actualState.getValue(WEST))
        {
            actualState = actualState.withProperty(NORTH, true).withProperty(SOUTH, true).withProperty(EAST, true).withProperty(WEST, true);
        }
        if (worldIn.getBlockState(pos.up()).getBlock() == this && !actualState.getValue(UP) && !actualState.getValue(NORTH) && !actualState.getValue(SOUTH) && !actualState.getValue(EAST) && !actualState.getValue(WEST))
        {
            if (!actualState.getValue(DOWN))
            {
                actualState = actualState.getActualState(worldIn, pos.up()).withProperty(UP, false);
            }
            else
            {
                actualState = actualState.getActualState(worldIn, pos.up()).withProperty(DOWN, true).withProperty(UP, false);
            }
        }
        return actualState.withProperty(BOTTOM, getIsBottom(worldIn, pos));
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        for (EnumFacing face : EnumFacing.values())
        {
            IBlockState blockState = worldIn.getBlockState(pos.offset(face));
            Material material = blockState.getMaterial();

            if (material == Material.LEAVES || worldIn.getBlockState(pos.up()).getBlock() == this)
            {
                return plant.isValidTemp(ClimateTFC.getActualTemp(worldIn, pos)) && plant.isValidRain(ChunkDataTFC.getRainfall(worldIn, pos));
            }
        }
        return false;
    }

    @Override
    @Nonnull
    protected BlockStateContainer createPlantBlockState()
    {
        return new BlockStateContainer(this, DOWN, UP, NORTH, EAST, WEST, SOUTH, growthStageProperty, DAYPERIOD, AGE, BOTTOM);
    }

    @Override
    protected boolean canConnectTo(IBlockAccess worldIn, BlockPos pos, EnumFacing facing)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Material material = iblockstate.getMaterial();

        return material == Material.LEAVES;
    }

    @Override
    protected boolean canPlantConnectTo(IBlockAccess world, BlockPos pos, EnumFacing facing)
    {
        if (!super.canPlantConnectTo(world, pos, facing) && world.getBlockState(pos.up()).getBlock() == this && facing != EnumFacing.DOWN && facing != EnumFacing.UP)
        {
            return canPlantConnectTo(world, pos.up(), facing);
        }

        return super.canPlantConnectTo(world, pos, facing);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isAreaLoaded(pos, 1)) return;

        if (plant.isValidGrowthTemp(ClimateTFC.getActualTemp(worldIn, pos)) && plant.isValidSunlight(Math.subtractExact(worldIn.getLightFor(EnumSkyBlock.SKY, pos), worldIn.getSkylightSubtracted())))
        {
            int j = state.getValue(AGE);

            if (rand.nextDouble() < getGrowthRate(worldIn, pos) && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos.down(), state, true))
            {
                if (j == 3)
                {
                    if (canGrow(worldIn, pos, state, worldIn.isRemote)) grow(worldIn, rand, pos, state);
                    else if (canGrowHorizontally(worldIn, pos, state)) growHorizontally(worldIn, rand, pos, state);
                    else if (canGrowDiagonally(worldIn, pos, state)) growDiagonally(worldIn, rand, pos, state);
                }
                else if (j < 3)
                {
                    worldIn.setBlockState(pos, state.withProperty(AGE, j + 1).withProperty(BOTTOM, getIsBottom(worldIn, pos)));
                }
                net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
            }
        }
        else if (!plant.isValidGrowthTemp(ClimateTFC.getActualTemp(worldIn, pos)) || !plant.isValidSunlight(worldIn.getLightFor(EnumSkyBlock.SKY, pos)))
        {
            int j = state.getValue(AGE);

            if (rand.nextDouble() < getGrowthRate(worldIn, pos) && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, true))
            {
                if (j == 0)
                {
                    if (canShrink(worldIn, pos)) shrink(worldIn, pos);
                    else if (canShrinkHorizontally(worldIn, pos)) shrinkHorizontally(worldIn, pos);
                }
                else if (j > 0)
                {
                    worldIn.setBlockState(pos, state.withProperty(AGE, j - 1).withProperty(BOTTOM, getIsBottom(worldIn, pos)));
                }
                net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
            }
        }

        checkAndDropBlock(worldIn, pos, state);
    }

    private boolean canGrowDiagonally(World worldIn, BlockPos pos, IBlockState state)
    {
        boolean flag = false;
        if (!state.getValue(BOTTOM))
        {
            for (EnumFacing face : EnumFacing.Plane.HORIZONTAL.facings())
            {
                BlockPos sidePos = pos.offset(face);
                IBlockState sideState = worldIn.getBlockState(sidePos.down(2));
                Material sideMaterial = sideState.getMaterial();

                if (worldIn.isAirBlock(sidePos) && worldIn.isAirBlock(sidePos.down()) && (!sideMaterial.isSolid() || sideMaterial == Material.LEAVES) && canBlockStay(worldIn, sidePos.down(), state))
                {
                    flag = true;
                }
            }
        }
        return flag;
    }

    private void growDiagonally(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        if (!state.getValue(BOTTOM))
        {
            for (EnumFacing face : EnumFacing.Plane.HORIZONTAL.facings())
            {
                BlockPos sidePos = pos.offset(face);

                if (rand.nextDouble() < 0.5D && worldIn.isAirBlock(sidePos) && worldIn.isAirBlock(sidePos.down()))
                {
                    worldIn.setBlockState(sidePos.down(), this.getDefaultState());
                    IBlockState iblockstate = state.withProperty(AGE, 0).withProperty(growthStageProperty, plant.getStageForMonth());
                    worldIn.setBlockState(pos, iblockstate);
                    iblockstate.neighborChanged(worldIn, sidePos.down(), this, pos);
                    break;
                }
            }
        }
    }

    private boolean canGrowHorizontally(World worldIn, BlockPos pos, IBlockState state)
    {
        boolean flag = false;
        for (EnumFacing face : EnumFacing.Plane.HORIZONTAL.facings())
        {
            BlockPos sidePos = pos.offset(face);
            IBlockState sideState = worldIn.getBlockState(sidePos.down());
            Material sideMaterial = sideState.getMaterial();

            if (worldIn.isAirBlock(sidePos) && (!sideMaterial.isSolid() || sideMaterial == Material.LEAVES) && canBlockStay(worldIn, sidePos, state))
            {
                flag = true;
            }
        }
        return flag;
    }

    private void growHorizontally(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        for (EnumFacing face : EnumFacing.Plane.HORIZONTAL.facings())
        {
            BlockPos sidePos = pos.offset(face);

            if (rand.nextDouble() < 0.01D && worldIn.isAirBlock(sidePos))
            {
                worldIn.setBlockState(sidePos, this.getDefaultState());
                IBlockState iblockstate = state.withProperty(AGE, 0).withProperty(growthStageProperty, plant.getStageForMonth());
                worldIn.setBlockState(pos, iblockstate);
                iblockstate.neighborChanged(worldIn, sidePos, this, pos);
                break;
            }
        }
    }

    private void shrinkHorizontally(World worldIn, BlockPos pos)
    {
        worldIn.setBlockToAir(pos);
        IBlockState state = worldIn.getBlockState(pos);
        state.neighborChanged(worldIn, pos.east(), this, pos);
        state.neighborChanged(worldIn, pos.west(), this, pos);
        state.neighborChanged(worldIn, pos.north(), this, pos);
        state.neighborChanged(worldIn, pos.south(), this, pos);
    }

    private boolean canShrink(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.up()).getBlock() == this && worldIn.getBlockState(pos.down()).getBlock() != this;
    }

    private boolean canShrinkHorizontally(World worldIn, BlockPos pos)
    {
        boolean flag = false;
        for (EnumFacing face : EnumFacing.Plane.HORIZONTAL.facings())
        {
            if (worldIn.getBlockState(pos.offset(face)).getBlock() == this)
            {
                flag = true;
            }
        }
        return flag;
    }

    private boolean getIsBottom(IBlockAccess world, BlockPos pos)
    {
        IBlockState iblockstate = world.getBlockState(pos.down());
        Material material = iblockstate.getMaterial();

        return world.getBlockState(pos.down()).getBlock() != this && !material.isSolid();
    }
}
