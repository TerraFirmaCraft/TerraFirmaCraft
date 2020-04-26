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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.advancements.TFCTriggers;
import net.dries007.tfc.objects.blocks.BlockTorchTFC;
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
    private static final AxisAlignedBB AABB_UP = new AxisAlignedBB(0.25, 0, 0.25, 0.75, 0.5, 0.75);
    private static final AxisAlignedBB AABB_DOWN = new AxisAlignedBB(0.25, .5, 0.25, 0.75, 1, 0.75);

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
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.DOWN).withProperty(LIT, false));
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

    @SuppressWarnings("deprecation")
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

        switch (state.getValue(FACING))
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
    @Override
    @Nonnull
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
                    double usage = ConfigTFC.GENERAL.oilLampBurnRate * ticks / ICalendar.TICKS_IN_HOUR;
                    if (usage >= 10) // minimize rounding issues
                    {
                        FluidStack remaining = fluidHandler.drain((int) usage, true); // use fuel
                        if (remaining == null || remaining.amount < usage)
                        {
                            worldIn.setBlockState(pos, state.withProperty(LIT, false));
                        }
                        tel.resetCounter();
                    }
                }
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TELamp tel = Helpers.getTE(worldIn, pos, TELamp.class);
        ItemStack stack = playerIn.getHeldItem(hand);
        if (!worldIn.isRemote && tel != null)
        {
            if (state.getValue(LIT))
            {
                long ticks = tel.getTicksSinceUpdate();
                double usage = ConfigTFC.GENERAL.oilLampBurnRate * ticks / ICalendar.TICKS_IN_HOUR;
                if (usage >= 1) // minimize rounding issues
                {
                    IFluidHandler fluidHandler = tel.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                    if (fluidHandler != null)
                    {
                        fluidHandler.drain((int) usage, true); // use fuel
                    }
                }
                worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(LIT, false));
                tel.resetCounter();
                tel.markDirty();
            }
            else if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
            { //refill only if not lit
                IFluidHandler fluidHandler = tel.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                if (fluidHandler != null)
                {
                    FluidUtil.interactWithFluidHandler(playerIn, hand, fluidHandler);
                    tel.markDirty();
                }
            }
            else if (BlockTorchTFC.canLight(stack))
            { // light if has fuel
                IFluidHandler fluidHandler = tel.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                if (fluidHandler !=  null)
                {
                    FluidStack fuelStack = fluidHandler.drain(Fluid.BUCKET_VOLUME,false);
                    if (fuelStack != null && fuelStack.amount > 0)
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
            // Set the initial counter value and fill from item
            TELamp tile = Helpers.getTE(worldIn, pos, TELamp.class);
            if (tile != null)
            {
                tile.resetCounter();
                IFluidHandlerItem itemCap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                IFluidHandler teCap = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                if (itemCap != null && teCap != null)
                {
                    teCap.fill(itemCap.drain(TELamp.CAPACITY,false), true); //don't drain creative item
                }
            }
            super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        }
    }

    @SuppressWarnings("deprecation")
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
        else //may need to add powered boolean blockstate to avoid turning off when non power events occur?
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

    @SuppressWarnings("deprecation")
    @Override
    public boolean isBlockNormalCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isNormalCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
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
    @Nonnull
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        switch (state.getValue(FACING))
        {
            case UP:
                return AABB_UP;
            case DOWN:
            default:
                return AABB_DOWN;
        }
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
    @Nonnull
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
    {
        return getBoundingBox(state, worldIn, pos).offset(pos);
    }

    private boolean canPlaceOn(World worldIn, BlockPos pos)
    {
        IBlockState state = worldIn.getBlockState(pos);
        return state.getBlock().canPlaceTorchOnTop(state, worldIn, pos);
    }


    //Lifted directly from BlockTorch
    /**
     * Checks if this block can be placed exactly at the given position.
     */
    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        for (EnumFacing enumfacing : FACING.getAllowedValues())
        {
            if (this.canPlaceAt(worldIn, pos, enumfacing))
            {
                return true;
            }
        }

        return false;
    }

    private boolean canPlaceAt(World worldIn, BlockPos pos, EnumFacing facing)
    {
        if (!FACING.getAllowedValues().contains(facing))
        {
            return false;
        }

        BlockPos blockpos = pos.offset(facing.getOpposite());
        IBlockState iblockstate = worldIn.getBlockState(blockpos);
        Block block = iblockstate.getBlock();
        BlockFaceShape blockfaceshape = iblockstate.getBlockFaceShape(worldIn, blockpos, facing);

        if (facing.equals(EnumFacing.UP) && this.canPlaceOn(worldIn, blockpos))
        {
            return true;
        }
        else if (facing != EnumFacing.UP && facing != EnumFacing.DOWN)
        {
            return !isExceptBlockForAttachWithPiston(block) && blockfaceshape == BlockFaceShape.SOLID;
        }
        else if (facing == EnumFacing.DOWN && blockfaceshape == BlockFaceShape.SOLID)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        if (this.canPlaceAt(worldIn, pos, facing))
        {
            return this.getDefaultState().withProperty(FACING, facing);
        }
        else
        {
            return this.getDefaultState();
        }
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

/*    @Override
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
    }*/
}
