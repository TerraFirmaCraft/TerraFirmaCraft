/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import java.util.EnumMap;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankPropertiesWrapper;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.IMoldHandler;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.api.capability.heat.ItemHeatHandler;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.objects.fluids.FluidMetal;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.classic.CalenderTFC;

public class ItemMold extends ItemFiredPottery
{
    private static final EnumMap<Metal.ItemType, ItemMold> MAP = new EnumMap<>(Metal.ItemType.class);

    public static ItemMold get(Metal.ItemType category)
    {
        return MAP.get(category);
    }

    @SideOnly(Side.CLIENT)
    public static void registerModels()
    {
        for (ItemMold item : MAP.values())
        {
            ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition()
            {
                private ModelResourceLocation FALLBACK = new ModelResourceLocation(item.getRegistryName().toString() + "/empty");

                @Override
                public ModelResourceLocation getModelLocation(ItemStack stack)
                {
                    IFluidHandler cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                    if (cap instanceof IMoldHandler)
                    {
                        Metal metal = ((IMoldHandler) cap).getMetal();
                        if (metal != null)
                        {
                            ModelResourceLocation loc = new ModelResourceLocation(stack.getItem().getRegistryName().toString() + "/" + metal.getRegistryName().getPath());
                            return loc;
                        }
                    }
                    return FALLBACK;
                }
            });

            ModelBakery.registerItemVariants(item, new ModelResourceLocation(item.getRegistryName().toString() + "/empty"));
            ModelBakery.registerItemVariants(item, new ModelResourceLocation(item.getRegistryName().toString() + "/unknown"));
            ModelBakery.registerItemVariants(item, TFCRegistries.METALS.getValuesCollection()
                .stream()
                .filter(x -> item.type.hasMold && x.isToolMetal() && (x.tier == Metal.Tier.TIER_I || x.tier == Metal.Tier.TIER_II))
                .map(x -> new ModelResourceLocation(item.getRegistryName().toString() + "/" + x.getRegistryName().getPath()))
                .toArray(ModelResourceLocation[]::new));
        }
    }

    public final Metal.ItemType type;

    public ItemMold(Metal.ItemType type)
    {
        this.type = type;
        if (MAP.put(type, this) != null) throw new IllegalStateException("There can only be one.");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {

        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote && !player.isSneaking())
        {
            IItemHeat cap = stack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
            if (cap.isMolten())
            {
                player.openGui(TerraFirmaCraft.getInstance(), TFCGuiHandler.MOLD, world, 0, 0, 0);
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public String getTranslationKey(ItemStack stack)
    {
        IFluidHandler capFluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
        if (capFluidHandler instanceof IMoldHandler)
        {
            Metal metal = ((IMoldHandler) capFluidHandler).getMetal();
            if (metal != null)
            {
                return super.getTranslationKey(stack) + "." + metal.getRegistryName().getPath();
            }
        }
        return super.getTranslationKey(stack);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new FilledMoldCapability(nbt);
    }

    // Extends ItemHeatHandler for ease of use
    private class FilledMoldCapability extends ItemHeatHandler implements ICapabilityProvider, IMoldHandler
    {
        private final FluidTank tank;
        private IFluidTankProperties fluidTankProperties[];

        public FilledMoldCapability(@Nullable NBTTagCompound nbt)
        {
            super();
            tank = new FluidTank(100);

            if (nbt != null)
                deserializeNBT(nbt);

            updateFluidData(tank.getFluid());
        }

        @Nullable
        @Override
        public Metal getMetal()
        {
            return tank.getFluid() != null ? ((FluidMetal) tank.getFluid().getFluid()).getMetal() : null;
        }

        @Override
        public int getAmount()
        {
            return tank.getFluidAmount();
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
        {
            return hasCapability(capability, facing) ? (T) this : null;
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
        {
            return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                || capability == CapabilityItemHeat.ITEM_HEAT_CAPABILITY;
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setFloat("heat", getTemperature());
            nbt.setLong("ticks", CalenderTFC.getTotalTime());
            return tank.writeToNBT(nbt);
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt)
        {
            temperature = nbt.getFloat("heat");
            lastUpdateTick = nbt.getLong("ticks");
            tank.readFromNBT(nbt);
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
            if (resource.getFluid() instanceof FluidMetal && ((FluidMetal) resource.getFluid()).doesFluidHaveMold(type))
            {
                updateFluidData(resource);
                return tank.fill(resource, doFill);
            }
            return 0;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain)
        {
            return getTemperature() >= meltingPoint ? tank.drain(resource, doDrain) : null;
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain)
        {
            return getTemperature() >= meltingPoint ? tank.drain(maxDrain, doDrain) : null;
        }

        @SideOnly(Side.CLIENT)
        @Override
        public void addHeatInfo(ItemStack stack, List<String> text)
        {
            Metal metal = getMetal();
            if (metal != null)
            {
                String desc = TextFormatting.DARK_GREEN + I18n.format(Helpers.getTypeName(metal)) + ": " + I18n.format("tfc.tooltip.units", getAmount());
                if (isMolten())
                    desc += " - " + I18n.format("tfc.tooltip.liquid");
                text.add(desc);
            }
            IMoldHandler.super.addHeatInfo(stack, text);
        }

        private void updateFluidData(FluidStack fluid)
        {
            if (fluid != null && fluid.getFluid() instanceof FluidMetal)
            {
                Metal metal = ((FluidMetal) fluid.getFluid()).getMetal();
                this.meltingPoint = metal.meltTemp;
                this.heatCapacity = metal.specificHeat;
            }
            else
            {
                this.meltingPoint = 1000;
                this.heatCapacity = 1;
            }
        }
    }
}
