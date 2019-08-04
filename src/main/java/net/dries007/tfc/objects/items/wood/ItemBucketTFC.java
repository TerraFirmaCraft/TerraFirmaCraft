/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.wood;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.capability.FluidWhitelistCapability;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.items.ItemTFC;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemBucketTFC extends ItemTFC
{
    public static final Set<Fluid> ALLOWED_BUCKET_FLUIDS;
    private static final int CAPACITY = 1000;

    static
    {
        ALLOWED_BUCKET_FLUIDS = new HashSet<>();
        for (String fluidName : ConfigTFC.GENERAL.woodenBucketWhitelist)
        {
            ALLOWED_BUCKET_FLUIDS.add(FluidRegistry.getFluid(fluidName));
        }
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
            RayTraceResult rtr = this.rayTrace(worldIn, playerIn, true);

            //noinspection ConstantConditions
            if (rtr != null && bucketCap != null && rtr.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                BlockPos pos = rtr.getBlockPos();
                TileEntity te = worldIn.getTileEntity(pos);
                if (te instanceof IFluidHandler)
                {
                    IFluidHandler tank = (IFluidHandler) te;
                    FluidStack fluidStack = tank.drain(CAPACITY, false);
                    //noinspection ConstantConditions
                    if (stack != null && bucketCap.fill(fluidStack, true) > 0)
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
                else //Filled bucket, try to empty it
                {
                    IBlockState blockState = worldIn.getBlockState(pos);
                    if (!(blockState.getBlock().isReplaceable(worldIn, pos) || !blockState.getMaterial().isSolid()))
                    {
                        //if cant place in the block we found, try the side of it
                        pos = pos.offset(rtr.sideHit);
                    }
                    blockState = worldIn.getBlockState(pos);
                    if (blockState.getBlock().isReplaceable(worldIn, pos) || !blockState.getMaterial().isSolid())
                    {
                        if (!blockState.getMaterial().isLiquid())
                            worldIn.destroyBlock(pos, true); //drop the replaceable block (ie: torches, flowers...)
                        //noinspection ConstantConditions
                        worldIn.setBlockState(pos, bucketCap.drain(CAPACITY, true).getFluid().getBlock().getDefaultState());
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
        if (bucketCap != null && bucketCap.drain(CAPACITY, false) != null)
        {
            FluidStack fluidStack = bucketCap.drain(CAPACITY, false);
            //noinspection ConstantConditions
            return fluidStack.getLocalizedName() + " " + super.getItemStackDisplayName(stack);
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new FluidWhitelistCapability(stack, CAPACITY, ALLOWED_BUCKET_FLUIDS);
    }
}
