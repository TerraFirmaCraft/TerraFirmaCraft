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
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.types.IBerryBush;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.te.TETickCounter;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockBerryBush extends Block
{
    public static final PropertyBool FRUITING = PropertyBool.create("fruiting");

    private static final AxisAlignedBB SMALL_SIZE_AABB = new AxisAlignedBB(0D, 0.0D, 0, 1D, 0.25D, 1D);
    private static final AxisAlignedBB MEDIUM_SIZE_AABB = new AxisAlignedBB(0D, 0.0D, 0, 1D, 0.5D, 1D);

    private static final Map<IBerryBush, BlockBerryBush> MAP = new HashMap<>();

    public static BlockBerryBush get(IBerryBush bush)
    {
        return MAP.get(bush);
    }

    private final IBerryBush bush;

    public BlockBerryBush(IBerryBush bush)
    {
        super(Material.PLANTS);
        this.bush = bush;
        if (MAP.put(bush, this) != null) throw new IllegalStateException("There can only be one.");
        Blocks.FIRE.setFireInfo(this, 30, 60);
        setHardness(1.0F);
        setTickRandomly(true);
        setSoundType(SoundType.PLANT);
        setDefaultState(blockState.getBaseState().withProperty(FRUITING, false));
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(FRUITING, meta == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FRUITING) ? 1 : 0;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean causesSuffocation(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return bush.getSize() == IBerryBush.Size.LARGE;
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
    {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        switch (bush.getSize())
        {
            case SMALL:
                return SMALL_SIZE_AABB;
            case MEDIUM:
                return MEDIUM_SIZE_AABB;
            default:
                return FULL_BLOCK_AABB;
        }
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        if (bush.getSize() == IBerryBush.Size.LARGE && face == EnumFacing.UP)
            return BlockFaceShape.SOLID;
        else
            return BlockFaceShape.UNDEFINED;
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
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
                float temp = ClimateTFC.getActualTemp(world, pos);
                float rainfall = ChunkDataTFC.getRainfall(world, pos);
                long hours = te.getTicksSinceUpdate() / ICalendar.TICKS_IN_HOUR;
                if (hours > (bush.getGrowthTime() * ConfigTFC.General.FOOD.berryBushGrowthTimeModifier) && bush.isValidForGrowth(temp, rainfall))
                {
                    if (bush.isHarvestMonth(CalendarTFC.CALENDAR_TIME.getMonthOfYear()))
                    {
                        //Fruiting
                        world.setBlockState(pos, world.getBlockState(pos).withProperty(FRUITING, true));
                    }
                    te.resetCounter();
                }
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        if (!canStay(worldIn, pos))
        {
            worldIn.destroyBlock(pos, true);
        }
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

    @SideOnly(Side.CLIENT)
    @Override
    @Nonnull
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, @Nonnull BlockPos pos)
    {
        if (super.canPlaceBlockAt(worldIn, pos))
        {
            return canStay(worldIn, pos);
        }
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.getBlockState(pos).getValue(FRUITING))
        {
            if (!worldIn.isRemote)
            {
                ItemHandlerHelper.giveItemToPlayer(playerIn, bush.getFoodDrop());
                worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(FRUITING, false));
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

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        if (!(entityIn instanceof EntityPlayer && ((EntityPlayer) entityIn).isCreative()))
        {
            // Entity motion is reduced (like leaves).
            entityIn.motionX *= ConfigTFC.General.MISC.berryBushMovementModifier;
            if (entityIn.motionY < 0)
            {
                entityIn.motionY *= ConfigTFC.General.MISC.berryBushMovementModifier;
            }
            entityIn.motionZ *= ConfigTFC.General.MISC.berryBushMovementModifier;
            if (bush.isSpiky() && entityIn instanceof EntityLivingBase)
            {
                entityIn.attackEntityFrom(DamageSource.CACTUS, 1.0F);
            }
        }
    }

    @Override
    @Nonnull
    public BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FRUITING);
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

    @Nonnull
    public IBerryBush getBush()
    {
        return bush;
    }

    private boolean canStay(IBlockAccess world, BlockPos pos)
    {
        IBlockState below = world.getBlockState(pos.down());
        if (bush.getSize() == IBerryBush.Size.LARGE && below.getBlock() instanceof BlockBerryBush && ((BlockBerryBush) below.getBlock()).bush == this.bush)
        {
            return BlocksTFC.isGrowableSoil(world.getBlockState(pos.down(2))); // Only stack once
        }
        return BlocksTFC.isGrowableSoil(below);
    }
}
