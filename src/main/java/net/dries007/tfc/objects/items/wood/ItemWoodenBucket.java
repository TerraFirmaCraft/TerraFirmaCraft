/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.wood;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.fluids.capability.FluidWhitelistHandler;
import net.dries007.tfc.objects.items.ItemTFC;

import static net.minecraftforge.fluids.BlockFluidBase.LEVEL;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemWoodenBucket extends ItemTFC
{
    private static final int CAPACITY = Fluid.BUCKET_VOLUME;

    public ItemWoodenBucket()
    {
        setHasSubtypes(true);
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.LARGE;
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.LIGHT;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote && !stack.isEmpty())
        {
            IFluidHandler bucketCap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
            RayTraceResult rayTrace = rayTrace(worldIn, playerIn, true);

            //noinspection ConstantConditions - ray trace can be null
            if (rayTrace != null && bucketCap != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                BlockPos pos = rayTrace.getBlockPos();
                TileEntity te = worldIn.getTileEntity(pos);
                if (te instanceof IFluidHandler)
                {
                    IFluidHandler tank = (IFluidHandler) te;
                    FluidStack fluidStack = tank.drain(CAPACITY, false);

                    if (bucketCap.fill(fluidStack, true) > 0)
                    {
                        tank.drain(CAPACITY, true);
                        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
                    }
                    else
                    {
                        return ActionResult.newResult(EnumActionResult.FAIL, stack);
                    }
                }

                if (bucketCap.drain(CAPACITY, false) == null) //Empty bucket, try to fill it
                {
                    IFluidHandler fluidHandlerBlock = FluidUtil.getFluidHandler(worldIn, pos, null);
                    if (fluidHandlerBlock != null)
                    {
                        FluidStack drainStack = fluidHandlerBlock.drain(Integer.MAX_VALUE, false);
                        if (drainStack != null && drainStack.amount >= CAPACITY && bucketCap.fill(drainStack, true) > 0)
                        {
                            worldIn.setBlockToAir(pos);
                            worldIn.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.PLAYERS, 1.0F, 1.0F);
                            return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
                        }
                    }
                }
                else
                {
                    //Filled bucket, try to empty it. Place a flowing water block (not a source block!)
                    IBlockState stateAt = worldIn.getBlockState(pos);
                    if (!(stateAt.getBlock().isReplaceable(worldIn, pos) || !stateAt.getMaterial().isSolid()))
                    {
                        //if cant place in the block we found, try the side of it
                        pos = pos.offset(rayTrace.sideHit);
                    }
                    stateAt = worldIn.getBlockState(pos);
                    if (stateAt.getBlock().isReplaceable(worldIn, pos) || !stateAt.getMaterial().isSolid())
                    {
                        if (!stateAt.getMaterial().isLiquid())
                        {
                            worldIn.destroyBlock(pos, true); //drop the replaceable block (ie: torches, flowers...)
                        }

                        FluidStack fluidStack = bucketCap.drain(CAPACITY, true);
                        if (fluidStack != null)
                        {
                            Fluid fluid = fluidStack.getFluid();
                            // Place a flowing water block, not a source
                            if (fluid.getBlock() instanceof BlockFluidBase)
                            {
                                worldIn.setBlockState(pos, fluid.getBlock().getDefaultState().withProperty(LEVEL, 1));
                            }
                            else if (fluid.getBlock() instanceof BlockLiquid)
                            {
                                // Vanilla water and lava, and hopefully nothing else
                                try
                                {
                                    BlockLiquid flowingBlock = BlockStaticLiquid.getFlowingBlock(fluid.getBlock().getDefaultState().getMaterial());
                                    worldIn.setBlockState(pos, flowingBlock.getDefaultState().withProperty(BlockLiquid.LEVEL, 1));
                                }
                                catch (IllegalArgumentException e) { /* Just skip */ }
                            }
                        }
                        worldIn.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
                    }
                }
            }
        }
        return ActionResult.newResult(EnumActionResult.PASS, stack);
    }

    @Override
    @Nonnull
    public String getItemStackDisplayName(@Nonnull ItemStack stack)
    {
        IFluidHandler bucketCap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (bucketCap != null)
        {
            FluidStack fluidStack = bucketCap.drain(CAPACITY, false);
            if (fluidStack != null)
            {
                return fluidStack.getLocalizedName() + " " + super.getItemStackDisplayName(stack);
            }
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (isInCreativeTab(tab))
        {
            for (String fluidName : ConfigTFC.GENERAL.woodenBucketWhitelist)
            {
                Fluid fluid = FluidRegistry.getFluid(fluidName);
                if (fluid != null)
                {
                    ItemStack stack = new ItemStack(this);
                    IFluidHandlerItem cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                    if (cap != null)
                    {
                        cap.fill(new FluidStack(fluid, CAPACITY), true);
                    }
                    items.add(stack);
                }
            }
        }
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new FluidWhitelistHandler(stack, CAPACITY, ConfigTFC.GENERAL.woodenBucketWhitelist);
    }
}
