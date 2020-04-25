/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.metal;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.advancements.TFCTriggers;
import net.dries007.tfc.objects.blocks.property.ILightableBlock;
import net.dries007.tfc.objects.te.TELamp;
import net.dries007.tfc.objects.te.TETickCounter;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.calendar.ICalendar;

@ParametersAreNonnullByDefault
public class BlockMetalLamp extends Block implements ILightableBlock
{

    private static final Map<Metal, BlockMetalLamp> MAP = new HashMap<>();
    private static final AxisAlignedBB AABB = new AxisAlignedBB(0.25, 0, 0.25, 0.75, 0.5, 0.75);

    private final Metal metal;

    public static final PropertyDirection FACING = PropertyDirection.create("facing", new Predicate<EnumFacing>()
    {
        public boolean apply(@Nullable EnumFacing p_apply_1_)
        {
            return p_apply_1_ == EnumFacing.DOWN || p_apply_1_ == EnumFacing.UP;
        }
    });

    /* 1.7 lamp render bounds
                    renderer.setRenderBounds(0.275, 0.0, 0.275, 0.725, 0.0625F, 0.725);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.25, 0.0625, 0.25, 0.75, 0.375F, 0.75);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.3125, 0.375, 0.3125, 0.6875, 0.4375, 0.6875);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.375, 0.4375, 0.375, 0.625, 0.5, 0.625);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.46875, 0.5, 0.46875, 0.53125, 0.5625F, 0.53125);
    */


    public BlockMetalLamp(Metal metal)
    {
        super(Material.IRON);
        this.metal = metal;
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.DOWN).withProperty(LIT, true));
        setHardness(1f);
        setLightLevel(0.9375F);
        setTickRandomly(true);
        setSoundType(SoundType.METAL);
        if (!MAP.containsKey(metal))
            MAP.put(metal, this);


        OreDictionaryHelper.register(this, "lamp");
    }

    public static BlockMetalLamp get(Metal metal)
    {
        return MAP.get(metal);
    }

    public static ItemStack get(Metal metal, int amount)
    {
        return new ItemStack(MAP.get(metal), amount);
    }

    public Metal getMetal()
    {
        return metal;
    }

    //Don't need to do any clientside display ticking
    //public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)

    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        IBlockState iblockstate = this.getDefaultState();

        switch (meta)
        {
            case 0:
                iblockstate = iblockstate.withProperty(FACING, EnumFacing.DOWN);
                break;
            case 1:
                iblockstate = iblockstate.withProperty(FACING, EnumFacing.EAST);
                break;
            case 2:
                iblockstate = iblockstate.withProperty(FACING, EnumFacing.WEST);
                break;
            case 3:
                iblockstate = iblockstate.withProperty(FACING, EnumFacing.SOUTH);
                break;
            case 4:
                iblockstate = iblockstate.withProperty(FACING, EnumFacing.NORTH);
                break;
            case 5:
            default:
                iblockstate = iblockstate.withProperty(FACING, EnumFacing.UP);
        }

        return iblockstate.withProperty(LIT, meta >= 6);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int i = 0; //DOWN

        switch ((EnumFacing)state.getValue(FACING))
        {
            case DOWN:
                break;
            case EAST:
                i = i | 1;
                break;
            case WEST:
                i = i | 2;
                break;
            case SOUTH:
                i = i | 3;
                break;
            case NORTH:
                i = i | 4;
                break;
            case UP:
            default:
                i = i | 5;
        }

        return i + (state.getValue(LIT) ? 6 : 0);
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, LIT);
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
        if (state.getValue(LIT) && ConfigTFC.GENERAL.oilLampBurnRate > 0)
        {
            TETickCounter tel = Helpers.getTE(worldIn, pos, TELamp.class);
            if (tel != null)
            {
                IFluidHandler fluidHandler = tel.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                if (!worldIn.isRemote && fluidHandler != null)
                {
                    long ticks = tel.getTicksSinceUpdate();
                    int usage = (int) (ConfigTFC.GENERAL.oilLampBurnRate * ticks / ICalendar.TICKS_IN_HOUR);
                    FluidStack remaining = fluidHandler.drain(usage, true); // use fuel
                    if (remaining == null || remaining.amount < usage)
                    {
                        worldIn.setBlockState(pos, state.withProperty(LIT, false));
                    }
                    tel.resetCounter();
                }
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TELamp tel = Helpers.getTE(worldIn, pos, TELamp.class);
        if (!worldIn.isRemote && tel != null)
        {
            if (state.getValue(LIT))
            {
                worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(LIT, false));
            }
            else if (playerIn.getHeldItem(hand).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
            { //refill only if not lit
                IFluidHandler fluidHandler = tel.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                if (fluidHandler != null)
                {
                    FluidUtil.interactWithFluidHandler(playerIn, hand, fluidHandler);
                    tel.markDirty();
                }
            }
            else
            { // light if has fuel
                IFluidHandler fluidHandler = tel.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                if (fluidHandler !=  null)
                {
                    FluidStack fuelStack = fluidHandler.drain(Fluid.BUCKET_VOLUME,false);
                    if (fuelStack != null && fuelStack.amount> 0)
                    {
                        TFCTriggers.LIT_TRIGGER.trigger((EntityPlayerMP) playerIn, state.getBlock()); // Trigger lit block
                        worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(LIT, true));
                        tel.resetCounter();
                        tel.markDirty();
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        if (!worldIn.isRemote)
        {
            // Set the initial counter value
            NBTTagCompound nbt = stack.getTagCompound();
            TELamp tile = Helpers.getTE(worldIn, pos, TELamp.class);
            if (tile != null)
            {
                tile.resetCounter();
                if (nbt != null)
                {
                    tile.readFromItemTag(nbt);
                }
            }
            super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (worldIn.isBlockPowered(pos))
        {
            worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(LIT, true));
            TELamp te = Helpers.getTE(worldIn, pos, TELamp.class);
            if (te != null)
            {
                te.resetCounter();
            }
        }
        else
        {
            worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(LIT, false));
            TELamp te = Helpers.getTE(worldIn, pos, TELamp.class);
            if (te != null)
            {
                te.resetCounter();
            }
        }
    }


    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return state.getValue(LIT) ? super.getLightValue(state, world, pos) : 0;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return AABB;
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @SideOnly(Side.CLIENT)
    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
    {
        return getBoundingBox(state, worldIn, pos).offset(pos);
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
        return new TELamp();
    }

    @Override
    @Nonnull
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world,
                                  BlockPos pos, EntityPlayer player)
    {
        ItemStack stack = new ItemStack(state.getBlock());
        TELamp tile = Helpers.getTE(world, pos, TELamp.class);
        if (tile != null)
        {
            stack.setTagCompound(tile.getItemTag());
        }
        return stack;
    }
}
