/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.ItemHandlerHelper;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.Constants;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.fluids.capability.FluidWhitelistHandler;
import net.dries007.tfc.objects.fluids.properties.DrinkableProperty;
import net.dries007.tfc.objects.fluids.properties.FluidWrapper;
import net.dries007.tfc.util.FluidTransferHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemJug extends ItemPottery
{
    private static final int CAPACITY = 100;

    public ItemJug()
    {
        setHasSubtypes(true);
        setContainerItem(this);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (!stack.isEmpty())
        {
            IFluidHandler jugCap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
            if (jugCap != null)
            {
                RayTraceResult rayTrace = rayTrace(world, player, true);

                if (jugCap.drain(CAPACITY, false) != null)
                {
                    player.setActiveHand(hand);
                    return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                }
                else if (!world.isRemote && jugCap.drain(CAPACITY, false) == null && rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK)
                {
                    ItemStack single = stack.copy();
                    single.setCount(1);
                    FluidActionResult result = FluidTransferHelper.tryPickUpFluidGreedy(single, player, world, rayTrace.getBlockPos(), rayTrace.sideHit, Fluid.BUCKET_VOLUME, false);
                    if (result.isSuccess())
                    {
                        stack.shrink(1);
                        if (stack.isEmpty())
                        {
                            return new ActionResult<>(EnumActionResult.SUCCESS, result.getResult());
                        }
                        ItemHandlerHelper.giveItemToPlayer(player, result.getResult());
                        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                    }
                }
                else
                {
                    player.world.playSound(null, player.getPosition(), TFCSounds.JUG_BLOW, SoundCategory.PLAYERS, 1.0f, 0.8f + (float) (player.getLookVec().y / 2));
                }
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    @Nonnull
    public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
        IFluidHandler jugCap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (jugCap != null)
        {
            FluidStack fluidConsumed = jugCap.drain(CAPACITY, true);
            if (fluidConsumed != null && entityLiving instanceof EntityPlayer)
            {
                DrinkableProperty drinkable = FluidsTFC.getWrapper(fluidConsumed.getFluid()).get(DrinkableProperty.DRINKABLE);
                if (drinkable != null)
                {
                    drinkable.onDrink((EntityPlayer) entityLiving);
                }
            }
            if (Constants.RNG.nextFloat() < 0.02) // 1/50 chance, same as 1.7.10
            {
                stack.shrink(1);
                worldIn.playSound(null, entityLiving.getPosition(), TFCSounds.CERAMIC_BREAK, SoundCategory.PLAYERS, 1.0f, 1.0f);
            }
        }
        return stack;
    }

    @Override
    @Nonnull
    public EnumAction getItemUseAction(@Nonnull ItemStack stack)
    {
        return EnumAction.DRINK;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 32;
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
                return new TextComponentTranslation("item.tfc.ceramics.fired.jug.filled", fluidname).getFormattedText();
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
            for (FluidWrapper wrapper : FluidsTFC.getAllWrappers())
            {
                if (wrapper.get(DrinkableProperty.DRINKABLE) != null)
                {
                    ItemStack stack = new ItemStack(this);
                    IFluidHandlerItem cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
                    if (cap != null)
                    {
                        cap.fill(new FluidStack(wrapper.get(), CAPACITY), true);
                    }
                    items.add(stack);
                }
            }
        }
    }

    @Override
    public boolean canStack(ItemStack stack)
    {
        return false;
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new FluidWhitelistHandler(stack, CAPACITY, FluidsTFC.getAllWrappers().stream().filter(x -> x.get(DrinkableProperty.DRINKABLE) != null).map(FluidWrapper::get).collect(Collectors.toSet()));
    }
}
