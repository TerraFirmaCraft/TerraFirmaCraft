/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import java.util.EnumMap;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankPropertiesWrapper;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.api.capability.IMoldHandler;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.Heat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.api.capability.heat.ItemHeatHandler;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.objects.container.CapabilityContainerListener;
import net.dries007.tfc.objects.container.ContainerEmpty;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.recipes.UnmoldRecipe;
import net.dries007.tfc.util.Helpers;

import static net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;

@ParametersAreNonnullByDefault
public class ItemMold extends ItemPottery
{
    private static final EnumMap<Metal.ItemType, ItemMold> MAP = new EnumMap<>(Metal.ItemType.class);

    public static ItemMold get(Metal.ItemType category)
    {
        return MAP.get(category);
    }

    private final Metal.ItemType type;

    public ItemMold(Metal.ItemType type)
    {
        this.type = type;
        if (MAP.put(type, this) != null)
        {
            throw new IllegalStateException("There can only be one.");
        }
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote)
        {
            IItemHeat cap = stack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
            if (!player.isSneaking() && cap != null && cap.isMolten())
            {
                TFCGuiHandler.openGui(world, player, TFCGuiHandler.Type.MOLD);
            }

            if (player.isSneaking())
            {
                // Unmold on right click, if possible
                InventoryCrafting craftMatrix = new InventoryCrafting(new ContainerEmpty(), 3, 3);
                craftMatrix.setInventorySlotContents(0, stack);
                for (IRecipe recipe : ForgeRegistries.RECIPES.getValuesCollection())
                {
                    if (recipe instanceof UnmoldRecipe && recipe.matches(craftMatrix, world))
                    {
                        ItemStack result = recipe.getCraftingResult(craftMatrix);
                        if (!result.isEmpty())
                        {
                            ItemStack moldResult = ((UnmoldRecipe) recipe).getMoldResult(stack);
                            player.setHeldItem(hand, result);
                            if (!moldResult.isEmpty())
                            {
                                ItemHandlerHelper.giveItemToPlayer(player, moldResult);
                            }
                            else
                            {
                                player.world.playSound(null, player.getPosition(), TFCSounds.CERAMIC_BREAK, SoundCategory.PLAYERS, 1.0f, 1.0f);
                            }
                        }

                    }
                }
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    @Nonnull
    public String getTranslationKey(ItemStack stack)
    {
        IFluidHandler capFluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
        if (capFluidHandler instanceof IMoldHandler)
        {
            Metal metal = ((IMoldHandler) capFluidHandler).getMetal();
            if (metal != null)
            {
                //noinspection ConstantConditions
                return super.getTranslationKey(stack) + "." + metal.getRegistryName().getPath();
            }
        }
        return super.getTranslationKey(stack);
    }

    @Nullable
    @Override
    public NBTTagCompound getNBTShareTag(ItemStack stack)
    {
        return CapabilityContainerListener.readShareTag(stack);
    }

    @Override
    public void readNBTShareTag(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        CapabilityContainerListener.applyShareTag(stack, nbt);
    }

    public Metal.ItemType getType()
    {
        return type;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new FilledMoldCapability(nbt);
    }

    @Override
    public boolean canStack(ItemStack stack)
    {
        IMoldHandler moldHandler = (IMoldHandler) stack.getCapability(FLUID_HANDLER_CAPABILITY, null);
        return moldHandler == null || moldHandler.getMetal() == null;
    }

    // Extends ItemHeatHandler for ease of use
    private class FilledMoldCapability extends ItemHeatHandler implements ICapabilityProvider, IMoldHandler
    {
        private final FluidTank tank;
        private IFluidTankProperties[] fluidTankProperties;

        FilledMoldCapability(@Nullable NBTTagCompound nbt)
        {
            tank = new FluidTank(100);

            if (nbt != null)
            {
                deserializeNBT(nbt);
            }
        }

        @Nullable
        @Override
        public Metal getMetal()
        {
            return tank.getFluid() != null ? FluidsTFC.getMetalFromFluid(tank.getFluid().getFluid()) : null;
        }

        @Override
        public int getAmount()
        {
            return tank.getFluidAmount();
        }

        @Override
        public IFluidTankProperties[] getTankProperties()
        {
            if (fluidTankProperties == null)
            {
                fluidTankProperties = new IFluidTankProperties[] {new FluidTankPropertiesWrapper(tank)};
            }
            return fluidTankProperties;
        }

        @Override
        public int fill(FluidStack resource, boolean doFill)
        {
            if (resource != null)
            {
                Metal metal = FluidsTFC.getMetalFromFluid(resource.getFluid());
                //noinspection ConstantConditions
                if (metal != null && type.hasMold(metal))
                {
                    int fillAmount = tank.fill(resource, doFill);
                    if (fillAmount == tank.getFluidAmount())
                    {
                        updateFluidData();
                    }
                    return fillAmount;
                }
            }
            return 0;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain)
        {
            return getTemperature() >= meltTemp ? tank.drain(resource, doDrain) : null;
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain)
        {
            if (getTemperature() > meltTemp)
            {
                FluidStack stack = tank.drain(maxDrain, doDrain);
                if (tank.getFluidAmount() == 0)
                {
                    updateFluidData();
                }
                return stack;
            }
            return null;
        }

        @SideOnly(Side.CLIENT)
        @Override
        public void addHeatInfo(@Nonnull ItemStack stack, @Nonnull List<String> text)
        {
            Metal metal = getMetal();
            if (metal != null)
            {
                String desc = TextFormatting.DARK_GREEN + I18n.format(Helpers.getTypeName(metal)) + ": " + I18n.format("tfc.tooltip.units", getAmount());
                if (isMolten())
                {
                    desc += I18n.format("tfc.tooltip.liquid");
                }
                else
                {
                    desc += I18n.format("tfc.tooltip.solid");
                }
                text.add(desc);
            }
            IMoldHandler.super.addHeatInfo(stack, text);
        }

        @Override
        public float getHeatCapacity()
        {
            return heatCapacity;
        }

        @Override
        public float getMeltTemp()
        {
            return meltTemp;
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
        {
            return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                || capability == CapabilityItemHeat.ITEM_HEAT_CAPABILITY;
        }

        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
        {
            return hasCapability(capability, facing) ? (T) this : null;
        }

        @Override
        @Nonnull
        public NBTTagCompound serializeNBT()
        {
            NBTTagCompound nbt = new NBTTagCompound();

            // Duplicated from ItemHeatHandler
            if (getTemperature() <= 0)
            {
                nbt.setLong("ticks", -1);
                nbt.setFloat("heat", 0);
            }
            else
            {
                nbt.setLong("ticks", lastUpdateTick);
                nbt.setFloat("heat", temperature);
            }
            return tank.writeToNBT(nbt);
        }

        @Override
        public void deserializeNBT(@Nullable NBTTagCompound nbt)
        {
            if (nbt != null)
            {
                temperature = nbt.getFloat("heat");
                lastUpdateTick = nbt.getLong("ticks");
                tank.readFromNBT(nbt);
            }
            updateFluidData();
        }

        private void updateFluidData()
        {
            updateFluidData(tank.getFluid());
        }

        private void updateFluidData(@Nullable FluidStack fluid)
        {
            meltTemp = Heat.maxVisibleTemperature();
            heatCapacity = 1;
            if (fluid != null)
            {
                Metal metal = FluidsTFC.getMetalFromFluid(fluid.getFluid());
                //noinspection ConstantConditions
                if (metal != null)
                {
                    meltTemp = metal.getMeltTemp();
                    heatCapacity = metal.getSpecificHeat();
                }
            }
        }
    }
}
