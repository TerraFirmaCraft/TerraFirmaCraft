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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.world.classic.CalenderTFC;
import net.dries007.tfc.world.classic.ClimateTFC;

@ParametersAreNonnullByDefault
public class BlockShortGrassTFC extends BlockPlantTFC implements IShearable
{
    public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);
    protected static final AxisAlignedBB GRASS_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);
    protected static final AxisAlignedBB SHORT_GRASS_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.75D, 0.875D);
    protected static final AxisAlignedBB SHORTER_GRASS_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.5D, 0.875D);
    protected static final AxisAlignedBB SHORTEST_GRASS_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.25D, 0.875D);
    private static final Map<Plant, BlockShortGrassTFC> MAP = new HashMap<>();

    public static BlockShortGrassTFC get(Plant plant)
    {
        return BlockShortGrassTFC.MAP.get(plant);
    }

    public final Plant plant;

    public BlockShortGrassTFC(Plant plant)
    {
        super(plant);
        if (MAP.put(plant, this) != null) throw new IllegalStateException("There can only be one.");

        this.plant = plant;
        this.setDefaultState(this.blockState.getBaseState().withProperty(GROWTH_STAGE, CalenderTFC.Month.MARCH.id()));
    }

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(AGE, meta).withProperty(GROWTH_STAGE, CalenderTFC.Month.MARCH.id());
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(AGE);
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, AGE, GROWTH_STAGE, TIME_OF_DAY);
    }

    @Override
    @Nonnull
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
                    worldIn.setBlockState(pos, state.withProperty(AGE, j - 1).withProperty(GROWTH_STAGE, state.getValue(GROWTH_STAGE)));
                }
                else
                {
                    worldIn.setBlockToAir(pos);
                }
                net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
            }
        }
        else if (ClimateTFC.getHeightAdjustedBiomeTemp(worldIn, pos) > 20 && plant.isValidSunlight(worldIn.getLightFromNeighbors(pos.up())))
        {
            int j = state.getValue(AGE);

            if (rand.nextFloat() < getGrowthRate(worldIn, pos) && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos.up(), state, true))
            {
                if (j < 15)
                {
                    worldIn.setBlockState(pos, state.withProperty(AGE, j + 1).withProperty(GROWTH_STAGE, state.getValue(GROWTH_STAGE)));
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
    @Nonnull
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
    @Nonnull
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.AIR;
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        if (!worldIn.isRemote && stack.getItem() == Items.SHEARS)
        {
            spawnAsEntity(worldIn, pos, new ItemStack(this, 1));
        }
        else if (!worldIn.isRemote && stack.getItem().getHarvestLevel(stack, "knife", player, state) != -1)
        {
            spawnAsEntity(worldIn, pos, new ItemStack(ItemsTFC.HAY, 1));
        }
        else
        {
            super.harvestBlock(worldIn, player, pos, state, te, stack);
        }
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random)
    {
        return 1 + random.nextInt(fortune * 2 + 1);
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(this, 1);
    }

    @Override
    @Nonnull
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(this, 1);
    }

    @Override
    public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) { return true; }

    @Override
    @Nonnull
    public NonNullList<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune)
    {
        return NonNullList.withSize(1, new ItemStack(this, 1));
    }
}