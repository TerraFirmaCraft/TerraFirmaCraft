/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.RayTraceResult;
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
import net.dries007.tfc.api.capability.food.FoodStatsTFC;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.fluids.capability.FluidWhitelistHandler;
import net.dries007.tfc.util.FluidTransferHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemJug extends ItemPottery
{
    private static final int CAPACITY = 100;

    private static final Map<Fluid, Consumer<EntityLivingBase>> DRINKABLES;

    static
    {
        DRINKABLES = new HashMap<>();
        DRINKABLES.put(FluidsTFC.FRESH_WATER, player -> {
            if (player instanceof EntityPlayer && ((EntityPlayer) player).foodStats instanceof FoodStatsTFC)
            {
                FoodStatsTFC foodStatsTFC = (FoodStatsTFC) ((EntityPlayer) player).foodStats;
                foodStatsTFC.addThirst(40);
            }
        });
        DRINKABLES.put(FluidsTFC.SALT_WATER, player -> {
            if (player instanceof EntityPlayer && ((EntityPlayer) player).foodStats instanceof FoodStatsTFC)
            {
                FoodStatsTFC foodStatsTFC = (FoodStatsTFC) ((EntityPlayer) player).foodStats;
                foodStatsTFC.addThirst(-40);
            }
        });
        for (Fluid alcohol : FluidsTFC.getAllAlcoholsFluids())
        {
            DRINKABLES.put(alcohol, player -> {
                if (player instanceof EntityPlayer && ((EntityPlayer) player).foodStats instanceof FoodStatsTFC)
                {
                    FoodStatsTFC foodStatsTFC = (FoodStatsTFC) ((EntityPlayer) player).foodStats;
                    foodStatsTFC.addThirst(10);
                    if (Constants.RNG.nextFloat() < 0.25f)
                    {
                        player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 1200, 1));
                    }
                }
            });
        }
    }

    @Override
    public boolean canStack(@Nonnull ItemStack stack)
    {
        IFluidHandler jugCap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (jugCap != null)
        {
            return jugCap.drain(CAPACITY, false) == null;
        }
        return true;
    }

    public ItemJug()
    {
        setHasSubtypes(true);
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
                    return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
                }
                else if (!world.isRemote && jugCap.drain(CAPACITY, false) == null && rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK)
                {
                    ItemStack single = stack.copy();
                    single.setCount(1);
                    FluidActionResult result = FluidTransferHelper.tryPickUpFluidGreedy(single, player, world, rayTrace.getBlockPos(), rayTrace.sideHit, Fluid.BUCKET_VOLUME);
                    if (result.isSuccess())
                    {
                        stack.shrink(1);
                        if (stack.isEmpty())
                        {
                            return ActionResult.newResult(EnumActionResult.SUCCESS, result.getResult());
                        }
                        ItemHandlerHelper.giveItemToPlayer(player, result.getResult());
                        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
                    }
                }
            }
        }
        return ActionResult.newResult(EnumActionResult.PASS, stack);
    }

    @Override
    @Nonnull
    public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
        IFluidHandler jugCap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (jugCap != null)
        {
            FluidStack fluidConsumed = jugCap.drain(CAPACITY, true);
            if (fluidConsumed != null)
            {
                DRINKABLES.get(fluidConsumed.getFluid()).accept(entityLiving);
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
            items.add(new ItemStack(this));
            for (Fluid fluid : DRINKABLES.keySet())
            {
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
        return new FluidWhitelistHandler(stack, CAPACITY, DRINKABLES.keySet());
    }
}
