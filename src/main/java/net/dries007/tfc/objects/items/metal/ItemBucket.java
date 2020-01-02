/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.metal;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.fluids.capability.FluidWhitelistHandler;
import net.dries007.tfc.objects.fluids.properties.DrinkableProperty;
import net.dries007.tfc.objects.fluids.properties.FluidWrapper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static net.dries007.tfc.api.types.Metal.BLUE_STEEL;
import static net.dries007.tfc.api.types.Metal.RED_STEEL;
import static net.minecraftforge.fluids.FluidRegistry.LAVA;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemBucket extends ItemMetal
{
    private static final int CAPACITY = Fluid.BUCKET_VOLUME;

    public ItemBucket(Metal metal, Metal.ItemType itemType) {
        super(metal, itemType);
        setHasSubtypes(true);
    }

    @SuppressWarnings("ConstantConditions")

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
                if (bucketCap.drain(CAPACITY, false) == null) //Empty bucket, try to fill it
                {
                    ItemStack single = stack.copy();
                    single.setCount(1);
                    FluidActionResult result = FluidUtil.tryPickUpFluid(single, playerIn, worldIn, pos, rayTrace.sideHit);
                    if (result.isSuccess())
                    {
                        stack.shrink(1);
                        if (stack.isEmpty())
                        {
                            return ActionResult.newResult(EnumActionResult.SUCCESS, result.getResult());
                        }
                        if (!playerIn.isCreative())
                        {
                            // In creative, buckets function but don't give new items
                            ItemHandlerHelper.giveItemToPlayer(playerIn, result.getResult());
                        }
                        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
                    }
                }
                else
                {
                    //Filled bucket, try to empty it.
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
                                worldIn.setBlockState(pos, fluid.getBlock().getDefaultState());
                            }
                            else if (fluid.getBlock() instanceof BlockLiquid)
                            {
                                // Vanilla water and lava, and hopefully nothing else
                                try
                                {
                                    BlockLiquid flowingBlock = BlockStaticLiquid.getFlowingBlock(fluid.getBlock().getDefaultState().getMaterial());
                                    worldIn.setBlockState(pos, flowingBlock.getDefaultState());
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
                String fluidname = fluidStack.getLocalizedName();
                return new TextComponentTranslation("item.tfc.wooden_bucket.filled", fluidname).getFormattedText();
            }
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (isInCreativeTab(tab))
        {
            items.add(new ItemStack(this));
            for (Fluid fluid : getValidFluids())
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

    public Set<Fluid> getValidFluids () {
        if (metal.equals(BLUE_STEEL)) {
            Set<Fluid> blueFluids = FluidsTFC.getAllMetalFluids().stream().map(FluidWrapper::get).collect(Collectors.toSet());
            blueFluids.add(LAVA);
            return blueFluids;
        } else if (metal.equals(RED_STEEL)) {
            return FluidsTFC.getAllWrappers().stream().filter(x -> x.get(DrinkableProperty.DRINKABLE) != null).map(FluidWrapper::get).collect(Collectors.toSet());
        }
        else return (Set<Fluid>)Collections.EMPTY_SET;
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new FluidWhitelistHandler(stack, CAPACITY, getValidFluids());
    }
}
