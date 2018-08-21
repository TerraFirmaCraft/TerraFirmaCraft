/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import java.util.EnumMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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

import net.dries007.tfc.api.capability.IMoldHandler;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.MetalType;
import net.dries007.tfc.objects.fluids.FluidMetal;

public class ItemMold extends ItemFiredPottery
{
    private static final EnumMap<MetalType, ItemMold> MAP = new EnumMap<>(MetalType.class);

    public static ItemMold get(MetalType category)
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
                            ModelResourceLocation loc = new ModelResourceLocation(stack.getItem().getRegistryName().toString() + "/" + metal.name());
                            return loc;
                        }
                    }
                    return FALLBACK;
                }
            });
            ModelBakery.registerItemVariants(item, Metal.values()
                .stream()
                .map(x -> new ModelResourceLocation(item.getRegistryName().toString() + "/" + x.name()))
                .toArray(ModelResourceLocation[]::new));
            ModelBakery.registerItemVariants(item, new ModelResourceLocation(item.getRegistryName().toString() + "/empty"));
        }
    }

    public final MetalType type;

    public ItemMold(MetalType type)
    {
        this.type = type;
        if (MAP.put(type, this) != null) throw new IllegalStateException("There can only be one.");
    }

    @Override
    public String getTranslationKey(ItemStack stack)
    {
        IFluidHandler capFluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
        if (capFluidHandler instanceof IMoldHandler)
        {
            IMoldHandler cap = (IMoldHandler) capFluidHandler;
            FluidStack fs = cap.drain(1, false);
            if (fs != null)
            {
                Metal metal = ((FluidMetal) fs.getFluid()).metal;
                return super.getTranslationKey(stack) + "." + metal.name();
            }
        }
        return super.getTranslationKey(stack);
    }


    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {

        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote)
        {
            if (!player.isSneaking())
            {
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new FilledMoldCapability(nbt, 1, Float.MAX_VALUE);
    }

    public static class FilledMoldCapability implements ICapabilityProvider, IMoldHandler
    {
        private final FluidTank tank;
        private final float heatCapacity;
        private final float meltingPoint;

        private IFluidTankProperties fluidTankProperties[];
        private float temperature;

        public FilledMoldCapability(@Nullable NBTTagCompound nbt, float heatCapacity, float meltingPoint)
        {
            tank = new FluidTank(100);

            this.heatCapacity = heatCapacity;
            this.meltingPoint = meltingPoint;

            if (nbt != null)
                deserializeNBT(nbt);
        }

        @Nullable
        @Override
        public Metal getMetal()
        {
            if (tank.getFluidAmount() == 0)
                return null;
            return ((FluidMetal) tank.getFluid().getFluid()).metal;
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setFloat("item_heat", temperature);
            return tank.writeToNBT(nbt);
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt)
        {
            temperature = nbt.getFloat("item_heat");
            tank.readFromNBT(nbt);
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
        {
            return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
        {
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this);
            return null;
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
            if (resource.getFluid() instanceof FluidMetal)
                return tank.fill(resource, doFill);
            return 0;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain)
        {
            if (resource.getFluid() instanceof FluidMetal)
                return tank.drain(resource, doDrain);
            return null;
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain)
        {
            return tank.drain(maxDrain, doDrain);
        }

        @Override
        public float getTemperature()
        {
            return temperature;
        }

        @Override
        public void setTemperature(float temperature)
        {
            this.temperature = temperature;
        }

        @Override
        public float getMeltingPoint()
        {
            return meltingPoint;
        }

        @Override
        public float getHeatCapacity()
        {
            return heatCapacity;
        }

        @Override
        public void addTemperature(float temperature)
        {
            this.temperature += temperature;
        }
    }
}
