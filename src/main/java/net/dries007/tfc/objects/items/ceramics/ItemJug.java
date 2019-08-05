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
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.food.FoodStatsTFC;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.fluids.capability.FluidWhitelistHandler;

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

    public ItemJug()
    {
        setHasSubtypes(true);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!stack.isEmpty())
        {
            IFluidHandler jugCap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
            if (jugCap != null)
            {
                RayTraceResult rayTrace = rayTrace(worldIn, playerIn, true);

                //noinspection ConstantConditions - ray trace can be null
                if (rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK)
                {
                    if (!worldIn.isRemote)
                    {
                        BlockPos pos = rayTrace.getBlockPos();
                        TileEntity te = worldIn.getTileEntity(pos);
                        if (te instanceof IFluidHandler)
                        {
                            IFluidHandler tank = (IFluidHandler) te;
                            FluidStack fluidStack = tank.drain(CAPACITY, false);

                            if (jugCap.fill(fluidStack, true) > 0)
                            {
                                tank.drain(CAPACITY, true);
                                return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
                            }
                            else
                            {
                                return ActionResult.newResult(EnumActionResult.FAIL, stack);
                            }
                        }

                        if (jugCap.drain(CAPACITY, false) == null) //Empty jug, try to fill it
                        {
                            IFluidHandler fluidHandlerBlock = FluidUtil.getFluidHandler(worldIn, pos, null);
                            if (fluidHandlerBlock != null)
                            {
                                FluidStack drainStack = fluidHandlerBlock.drain(Integer.MAX_VALUE, false);
                                if (drainStack != null && drainStack.amount >= CAPACITY && jugCap.fill(drainStack, true) > 0)
                                {
                                    worldIn.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.PLAYERS, 1.0F, 1.0F);
                                    return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
                                }
                            }
                        }
                    }
                }
                else if (jugCap.drain(CAPACITY, false) != null)
                {
                    playerIn.setActiveHand(handIn);
                    return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
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
