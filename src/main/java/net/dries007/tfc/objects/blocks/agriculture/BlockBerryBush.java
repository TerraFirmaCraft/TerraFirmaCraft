/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.agriculture;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.types.IBerryBush;
import net.dries007.tfc.objects.te.TETickCounter;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.world.classic.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

@ParametersAreNonnullByDefault
public class BlockBerryBush extends Block
{
    public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 4); //last one is for fruits

    private static final AxisAlignedBB STAGE_1_AABB = new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D, 0.375D, 0.6875D);
    private static final AxisAlignedBB STAGE_2_AABB = new AxisAlignedBB(0.21875D, 0.0D, 0.21875D, 0.78125D, 0.5625D, 0.78125D);
    private static final AxisAlignedBB STAGE_3_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.75D, 0.875D);
    private static final AxisAlignedBB STAGE_4_AABB = FULL_BLOCK_AABB;

    private static final Map<IBerryBush, BlockBerryBush> MAP = new HashMap<>();

    public static BlockBerryBush get(IBerryBush bush)
    {
        return MAP.get(bush);
    }

    public final IBerryBush bush;

    public BlockBerryBush(IBerryBush bush)
    {
        super(Material.LEAVES, Material.LEAVES.getMaterialMapColor());
        this.bush = bush;
        if (MAP.put(bush, this) != null) throw new IllegalStateException("There can only be one.");
        Blocks.FIRE.setFireInfo(this, 30, 60);
        setHardness(1.0F);
        setTickRandomly(true);
        setDefaultState(blockState.getBaseState().withProperty(STAGE, 0));
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        TETickCounter tile = Helpers.getTE(worldIn, pos, TETickCounter.class);
        if (tile != null)
        {
            tile.resetCounter();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(STAGE, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(STAGE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return state.getValue(STAGE) > 2;
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        switch (state.getValue(STAGE))
        {
            case 0:
                return STAGE_1_AABB;
            case 1:
                return STAGE_2_AABB;
            case 2:
                return STAGE_3_AABB;
            default:
                return STAGE_4_AABB;
        }
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, @Nonnull BlockPos pos)
    {
        if (super.canPlaceBlockAt(worldIn, pos))
        {
            IBlockState state = worldIn.getBlockState(pos.down());
            return state.getBlock().canPlaceTorchOnTop(state, worldIn, pos);
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public void randomTick(World world, BlockPos pos, IBlockState state, Random random)
    {
        if (!world.isRemote)
        {
            TETickCounter te = Helpers.getTE(world, pos, TETickCounter.class);
            if (te != null)
            {
                float temp = ClimateTFC.getTemp(world, pos);
                float rainfall = ChunkDataTFC.getRainfall(world, pos);
                long hours = te.getTicksSinceUpdate() / ICalendar.TICKS_IN_HOUR;
                if (hours > bush.getGrowthTime() && bush.isValidForGrowth(temp, rainfall))
                {
                    int stage = world.getBlockState(pos).getValue(STAGE);
                    if(stage < 3)
                    {
                        world.setBlockState(pos, world.getBlockState(pos).withProperty(STAGE, ++stage));
                    }
                    if(stage == 3 && bush.isHarvestMonth(CalendarTFC.CALENDAR_TIME.getMonthOfYear()))
                    {
                        //Fruiting
                        world.setBlockState(pos, world.getBlockState(pos).withProperty(STAGE, 4));
                    }
                    te.resetCounter();
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    @Nonnull
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    @Nonnull
    public BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, STAGE);
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
        return new TETickCounter();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.getBlockState(pos).getValue(STAGE) == 4)
        {
            if (!worldIn.isRemote)
            {
                Helpers.spawnItemStack(worldIn, pos, bush.getFoodDrop());
                worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(STAGE, 3));
                TETickCounter te = Helpers.getTE(worldIn, pos, TETickCounter.class);
                if (te != null)
                {
                    te.resetCounter();
                }
            }
            return true;
        }
        return false;
    }
}
