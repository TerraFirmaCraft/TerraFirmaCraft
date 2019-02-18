/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.IBellowsHandler;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.objects.items.ItemFireStarter;
import net.dries007.tfc.objects.te.TEBellows;
import net.dries007.tfc.objects.te.TECharcoalForge;
import net.dries007.tfc.objects.te.TEInventory;
import net.dries007.tfc.util.Helpers;

@ParametersAreNonnullByDefault
public class BlockCharcoalForge extends Block implements ITileEntityProvider, IBellowsHandler
{
    public static final PropertyBool LIT = PropertyBool.create("lit");
    private static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D);
    private static final Vec3i BELLOWS_OFFSET = new Vec3i(1, -1, 0);

    static
    {
        TEBellows.addBellowsOffset(BELLOWS_OFFSET);
    }

    public static boolean hasValidSideBlocks(World world, BlockPos pos)
    {
        for (EnumFacing face : EnumFacing.values())
        {
            IBlockState state = world.getBlockState(pos.offset(face));
            if (face == EnumFacing.UP)
            {
                // The block on top must be non-solid
                if (state.isNormalCube())
                {
                    return false;
                }
            }
            else
            {
                // Side blocks must be rock, opaque, and full blocks
                if (state.getMaterial() != Material.ROCK || !state.isOpaqueCube() || !state.isFullBlock())
                {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean canIntakeFrom(TEBellows te, Vec3i offset, EnumFacing facing)
    {
        return offset.equals(BELLOWS_OFFSET);
    }

    @Override
    public float onAirIntake(TEBellows te, World world, BlockPos pos, float airAmount)
    {
        TerraFirmaCraft.getLog().info("Making bellows stuff!");
        TECharcoalForge teForge = Helpers.getTE(world, pos, TECharcoalForge.class);
        if (teForge != null)
        {
            teForge.onAirIntake(airAmount);
        }
        return airAmount;
    }

    public static boolean hasValidChimney(World world, BlockPos pos)
    {
        if (world.canBlockSeeSky(pos))
        {
            // Trivial case, chimney above the forge
            return true;
        }
        for (EnumFacing face : EnumFacing.HORIZONTALS)
        {
            // Chimney is offset by one
            BlockPos pos1 = pos.offset(face);
            if (world.getBlockState(pos1.up()).getBlock() != Blocks.AIR)
            {
                // If the block one up, one to the side is not air, no valid chimneys appear in this direction
                continue;
            }

            if (world.canBlockSeeSky(pos1))
            {
                // Chimney one block away
                return true;
            }

            if (world.canBlockSeeSky(pos1.offset(face)) && world.getBlockState(pos.offset(face).up()).getBlock() == Blocks.AIR)
            {
                // Chimney two blocks away
                return true;
            }
        }
        return false;
    }

    BlockCharcoalForge()
    {
        super(Material.GROUND);

        setSoundType(SoundType.GROUND);
        setHarvestLevel("shovel", 0);
        setHardness(1.0F);
        setTickRandomly(true); // Used for chimney checks -> extinguish
        this.setDefaultState(this.blockState.getBaseState().withProperty(LIT, false));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TECharcoalForge();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isTopSolid(IBlockState state)
    {
        return false;
    }

    @Nonnull
    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(LIT, meta == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(LIT) ? 1 : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return AABB;
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return face.getAxis() == EnumFacing.Axis.Y ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @Override
    @Nullable
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return AABB;
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        if (!worldIn.isRemote && te instanceof TEInventory)
        {
            ((TEInventory) te).onBreakBlock(worldIn, pos);
        }
        super.harvestBlock(worldIn, player, pos, state, te, stack);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!hasValidChimney(worldIn, pos))
        {
            worldIn.setBlockState(pos, state.withProperty(LIT, false));
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!worldIn.isRemote)
        {
            if (state.getValue(LIT) && (!hasValidChimney(worldIn, pos) || !hasValidSideBlocks(worldIn, pos)))
            {
                // This is not a valid pit, therefor extinguish it
                worldIn.setBlockState(pos, state.withProperty(LIT, false));
            }
        }
    }

    @Override
    public int quantityDropped(Random random)
    {
        return 7;
    }

    @Override
    @Nonnull
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.COAL;
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return 1;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            if (!state.getValue(LIT))
            {
                ItemStack held = player.getHeldItem(hand);
                if (ItemFireStarter.canIgnite(held))
                {
                    world.setBlockState(pos, state.withProperty(LIT, true));
                    return true;
                }
            }
            if (!player.isSneaking())
            {
                TFCGuiHandler.openGui(world, pos, player, TFCGuiHandler.Type.CHARCOAL_FORGE);
            }
        }
        return true;
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, LIT);
    }

    @Override
    @Nonnull
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(Items.COAL, 1, 1);
    }

}
