/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.wood;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.te.TEChestTFC;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockChestTFC extends BlockContainer
{
    private static final Map<Tree, BlockChestTFC> MAP_BASIC = new HashMap<>();
    private static final Map<Tree, BlockChestTFC> MAP_TRAP = new HashMap<>();

    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    private static final AxisAlignedBB NORTH = new AxisAlignedBB(0.0625D, 0.0D, 0.0D, 0.9375D, 0.875D, 0.9375D);
    private static final AxisAlignedBB SOUTH = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.875D, 1.0D);
    private static final AxisAlignedBB WEST = new AxisAlignedBB(0.0D, 0.0D, 0.0625D, 0.9375D, 0.875D, 0.9375D);
    private static final AxisAlignedBB EAST = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 1.0D, 0.875D, 0.9375D);
    private static final AxisAlignedBB NOT_CONNECTED_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.875D, 0.9375D);
    private static final AxisAlignedBB[] CHEST_AABB = new AxisAlignedBB[] {NORTH, SOUTH, WEST, EAST};

    public static BlockChestTFC getBasic(Tree wood)
    {
        return MAP_BASIC.get(wood);
    }

    public static BlockChestTFC getTrap(Tree wood)
    {
        return MAP_TRAP.get(wood);
    }

    public final Tree wood;
    public final BlockChest.Type type;

    public BlockChestTFC(BlockChest.Type type, Tree wood)
    {
        super(Material.WOOD);
        this.wood = wood;
        setHarvestLevel("axe", 0);
        setHardness(2.5F);
        setSoundType(SoundType.WOOD);
        this.type = type;
        switch (type)
        {
            case BASIC:
                if (MAP_BASIC.put(wood, this) != null) throw new IllegalStateException("There can only be one.");
                OreDictionaryHelper.register(this, "chest");
                break;
            case TRAP:
                if (MAP_TRAP.put(wood, this) != null) throw new IllegalStateException("There can only be one.");
                OreDictionaryHelper.register(this, "chest", "chestTrapped");
                break;
            default:
                throw new IllegalStateException();
        }
        Blocks.FIRE.setFireInfo(this, 5, 20);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @SuppressWarnings("deprecation")
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TEChestTFC();
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta + 2));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).getIndex() - 2;
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
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
        TEChestTFC te = Helpers.getTE(source, pos, TEChestTFC.class);
        if (te != null)
        {
            EnumFacing connection = te.getConnection();
            if (connection != null)
            {
                return CHEST_AABB[connection.getIndex() - 2];
            }
        }
        return NOT_CONNECTED_AABB;
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            TEChestTFC te = Helpers.getTE(worldIn, pos, TEChestTFC.class);
            if (te != null)
            {
                playerIn.displayGUIChest(te.getPriorityTE());
            }
            if (this.type == BlockChest.Type.BASIC)
            {
                playerIn.addStat(StatList.CHEST_OPENED);
            }
            else if (this.type == BlockChest.Type.TRAP)
            {
                playerIn.addStat(StatList.TRAPPED_CHEST_TRIGGERED);
            }
        }

        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        //todo check if the facing player clicked has a chest. try to place this one at the same facing if positive
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        EnumFacing placedFacing = state.getValue(FACING);
        TEChestTFC placedTE = Helpers.getTE(worldIn, pos, TEChestTFC.class);
        EnumFacing connectFacing = placedFacing.rotateY();
        TEChestTFC neighborChestTE = null;
        for (int i = 0; i < 2; i++)
        {
            BlockPos neighborChestPos = pos.offset(connectFacing);
            IBlockState neighborChestState = worldIn.getBlockState(neighborChestPos);
            neighborChestTE = Helpers.getTE(worldIn, neighborChestPos, TEChestTFC.class);
            if (neighborChestTE == null || neighborChestTE.getConnection() != null || neighborChestState.getValue(FACING) != placedFacing)
            {
                neighborChestTE = null;
            }
            else
            {
                break;
            }
            connectFacing = connectFacing.getOpposite();
        }
        if (neighborChestTE != null && placedTE != null)
        {
            //found a connectable chest!
            placedTE.setConnectedTo(connectFacing);
            neighborChestTE.setConnectedTo(connectFacing.getOpposite());
        }
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING);
    }

}
