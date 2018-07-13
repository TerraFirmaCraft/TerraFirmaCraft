/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.blocks.metal;

import java.util.EnumMap;
import java.util.Random;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.Metal;
import net.dries007.tfc.objects.items.metal.ItemSheet;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockSheet extends Block
{
    public static final PropertyDirection FACE = PropertyDirection.create("face");
    private static final EnumMap<Metal, BlockSheet> MAP = new EnumMap<>(Metal.class);

    public static BlockSheet get(Metal metal)
    {
        return MAP.get(metal);
    }

    public static ItemStack get(Metal metal, int amount)
    {
        return new ItemStack(MAP.get(metal), amount);
    }

    public final Metal metal;
    private static final AxisAlignedBB[] SHEET_AABB = new AxisAlignedBB[] {
        new AxisAlignedBB(0d, 0.9375d, 0d, 1d, 1d, 1d),
        new AxisAlignedBB(0d, 0d, 0d, 1d, 0.0625d, 1d),
        new AxisAlignedBB(0d, 0d, 0.9375d, 1d, 1d, 1d),
        new AxisAlignedBB(0d, 0d, 0d, 1d, 1d, 0.0625d),
        new AxisAlignedBB(0.9375d, 0d, 0d, 1d, 1d, 1d),
        new AxisAlignedBB(0d, 0d, 0d, 0.0625d, 1d, 1d)
    };

    public BlockSheet(Metal metal)
    {
        super(Material.IRON);

        this.metal = metal;
        if (MAP.put(metal, this) != null) throw new IllegalStateException("There can only be one.");

        setHardness(3.5F);
        setResistance(10F);
        setHarvestLevel("pickaxe", 0);
        this.setDefaultState(blockState.getBaseState().withProperty(FACE, EnumFacing.NORTH));
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACE, EnumFacing.getFront(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACE).getIndex();
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return SHEET_AABB[state.getValue(FACE).getIndex()];
    }

    @Nullable
    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return SHEET_AABB[state.getValue(FACE).getIndex()];
    }

    @SideOnly(Side.CLIENT)
    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
    {
        return SHEET_AABB[state.getValue(FACE).getIndex()];
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACE);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return ItemSheet.get(metal, Metal.ItemType.SHEET);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(ItemSheet.get(metal, Metal.ItemType.SHEET));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        EnumFacing face = state.getValue(FACE).getOpposite();
        if (!worldIn.isSideSolid(pos.offset(face), face.getOpposite()))
        {
            InventoryHelper.spawnItemStack(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, getItem(worldIn, pos, state));
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(ItemSheet.get(metal, Metal.ItemType.SHEET));
    }
}
