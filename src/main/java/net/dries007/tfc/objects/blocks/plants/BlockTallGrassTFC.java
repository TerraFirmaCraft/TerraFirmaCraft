/*
 *
 *  * Work under Copyright. Licensed under the EUPL.
 *  * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.blocks.plants;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.world.classic.CalenderTFC;
import net.dries007.tfc.world.classic.ClimateTFC;

public class BlockTallGrassTFC extends BlockShortGrassTFC implements IGrowable
{
    // todo: in 1.13 we will be able to save blockstates instead of relying on metadata, so will probably rewrite this as 2 blocks with part=upper and part=lower
    protected static final AxisAlignedBB GRASS_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 2.0D, 0.875D);
    protected static final AxisAlignedBB SHORT_GRASS_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.5D, 0.875D);
    protected static final AxisAlignedBB SHORTER_GRASS_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);
    protected static final AxisAlignedBB SHORTEST_GRASS_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.5D, 0.875D);
    private static final Map<Plant, BlockTallGrassTFC> MAP = new HashMap<>();

    public static BlockTallGrassTFC get(Plant plant)
    {
        return BlockTallGrassTFC.MAP.get(plant);
    }

    public final Plant plant;

    public BlockTallGrassTFC(Plant plant)
    {
        super(plant);
        if (MAP.put(plant, this) != null) throw new IllegalStateException("There can only be one.");

        this.plant = plant;
        this.setDefaultState(this.blockState.getBaseState().withProperty(GROWTHSTAGE, CalenderTFC.getMonthOfYear().id()));
    }

    @Override
    public Block.EnumOffsetType getOffsetType()
    {
        return Block.EnumOffsetType.XZ;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isAreaLoaded(pos, 1)) return;

        if ((ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, pos) < 15 || !plant.isValidSunlight(worldIn.getLightFromNeighbors(pos.up()))))
        {
            int j = state.getValue(AGE);

            if (rand.nextFloat() < getGrowthRate(worldIn, pos) && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, true))
            {
                if (j > 0)
                {
                    worldIn.setBlockState(pos, state.withProperty(AGE, j - 1));
                }
                net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
            }

        }
        else if (ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, pos) > 20 && plant.isValidSunlight(worldIn.getLightFromNeighbors(pos.up())))
        {
            int j = state.getValue(AGE);

            if (rand.nextFloat() < getGrowthRate(worldIn, pos) && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos.up(), state, true))
            {
                if ((j >= 8 && j < 15) || (j < 8 && worldIn.isAirBlock(pos.up())))
                {
                    worldIn.setBlockState(pos, state.withProperty(AGE, j + 1));
                }
                net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
            }
        }

        if (!canBlockStay(worldIn, pos, state))
        {
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        switch (state.getValue(AGE))
        {
            case 0:
            case 1:
            case 2:
            case 3:
                return SHORTEST_GRASS_AABB.offset(state.getOffset(source, pos));
            case 4:
            case 5:
            case 6:
            case 7:
                return SHORTER_GRASS_AABB.offset(state.getOffset(source, pos));
            case 8:
            case 9:
            case 10:
            case 11:
                return SHORT_GRASS_AABB.offset(state.getOffset(source, pos));
            case 12:
            case 13:
            case 14:
            default:
                return GRASS_AABB.offset(state.getOffset(source, pos));
        }
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        if (!worldIn.isRemote && state.getValue(AGE) > 7 && stack.getItem().getHarvestLevel(stack, "knife", player, state) != -1)
        {
            spawnAsEntity(worldIn, pos, new ItemStack(ItemsTFC.HAY, 2));
        }
        else
        {
            super.harvestBlock(worldIn, player, pos, state, te, stack);
        }
    }

    @Override
    public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) { return true; }

    @Override
    public NonNullList<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune)
    {
        return NonNullList.withSize(1, new ItemStack(this, 1));
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        return state.getValue(AGE) < 8 && worldIn.isAirBlock(pos.up());
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        return false;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        worldIn.setBlockState(pos, this.getDefaultState().withProperty(AGE, 15).withProperty(GROWTHSTAGE, CalenderTFC.getMonthOfYear().id()));
    }
}