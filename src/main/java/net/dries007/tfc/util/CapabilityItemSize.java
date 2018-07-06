/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.util;

import java.util.concurrent.Callable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.Constants;
import net.dries007.tfc.objects.Size;
import net.dries007.tfc.objects.Weight;

public class CapabilityItemSize
{
    private static final CapabilityItemSize INSTANCE = new CapabilityItemSize();
    private static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "item_size");
    @CapabilityInject(IItemSize.class)
    public static Capability<IItemSize> ITEM_SIZE_CAPABILITY = null;

    public static void preInit()
    {
        CapabilityManager.INSTANCE.register(IItemSize.class, new Capability.IStorage<IItemSize>()
        {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IItemSize> capability, IItemSize instance, EnumFacing side)
            {
                return null;
            }

            @Override
            public void readNBT(Capability<IItemSize> capability, IItemSize instance, EnumFacing side, NBTBase nbt)
            {

            }
        }, new CapabilityItemSize.Factory());

    }

    public static void add(AttachCapabilitiesEvent<ItemStack> event, Item item, Size size, Weight weight, boolean canStack)
    {
        event.addCapability(ID, INSTANCE.get(size, weight, canStack));
        item.setMaxStackSize(IItemSize.getStackSize(size, weight, canStack));
    }

    public ICapabilityProvider get(Size size, Weight weight, boolean canStack)
    {
        return new ItemSizeProvider(size, weight, canStack);
    }

    public static class Factory implements Callable<IItemSize>
    {
        @Nonnull
        @Override
        public IItemSize call()
        {
            return new ItemSize(Size.SMALL, Weight.LIGHT, true);
        }
    }

    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    public static class ItemSize implements IItemSize
    {
        private final Size size;
        private final Weight weight;
        private boolean canStack;

        public ItemSize(Size size, Weight weight, boolean canStack)
        {
            this.size = size;
            this.weight = weight;
            this.canStack = canStack;
        }

        @Override
        public Size getSize(ItemStack stack)
        {
            return this.size;
        }

        @Override
        public Weight getWeight(ItemStack stack)
        {
            return this.weight;
        }

        @Override
        public boolean canStack(ItemStack stack)
        {
            return canStack;
        }
    }

    public class ItemSizeProvider implements ICapabilityProvider
    {
        private final IItemSize capability;

        public ItemSizeProvider(ItemSize capability)
        {
            this.capability = capability;
        }

        public ItemSizeProvider(Size size, Weight weight, boolean canStack)
        {
            this.capability = new ItemSize(size, weight, canStack);
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
        {
            //noinspection ConstantConditions
            return capability == ITEM_SIZE_CAPABILITY;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
        {
            return capability == ITEM_SIZE_CAPABILITY ? (T) this.capability : null;
        }
    }
}
