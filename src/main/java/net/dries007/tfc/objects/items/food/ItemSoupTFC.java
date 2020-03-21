/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.food;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.FoodData;
import net.dries007.tfc.api.capability.food.FoodHandler;
import net.dries007.tfc.api.capability.food.IFood;
import net.dries007.tfc.util.agriculture.Food;

@ParametersAreNonnullByDefault
public class ItemSoupTFC extends ItemFoodTFC
{
    public ItemSoupTFC(Food food)
    {
        super(food);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new SoupHandler(nbt, food.getData());
    }

    @Nonnull
    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
        IFood food = stack.getCapability(CapabilityFood.CAPABILITY, null);
        if (food instanceof SoupHandler)
        {
            ItemStack bowlStack = ((SoupHandler) food).getBowlStack().copy();
            bowlStack.setCount(1);
            super.onItemUseFinish(stack, worldIn, entityLiving);
            return bowlStack;
        }
        return super.onItemUseFinish(stack, worldIn, entityLiving);
    }

    public static class SoupHandler extends FoodHandler
    {
        private ItemStack bowlStack;

        public SoupHandler(@Nullable NBTTagCompound nbt, FoodData data)
        {
            super(nbt, data);
            this.bowlStack = ItemStack.EMPTY;
        }

        public void initCreationDataAndBowl(ItemStack bowlStack, FoodData data)
        {
            this.bowlStack = bowlStack;
            this.data = data;
        }

        @Override
        public NBTTagCompound serializeNBT()
        {
            NBTTagCompound nbt = super.serializeNBT();
            nbt.setTag("bowl", bowlStack.serializeNBT());
            return nbt;
        }

        @Override
        public void deserializeNBT(@Nullable NBTTagCompound nbt)
        {
            super.deserializeNBT(nbt);
            if (nbt != null)
            {
                bowlStack = new ItemStack(nbt.getCompoundTag("bowl"));
            }
        }

        @Override
        protected boolean isDynamic()
        {
            return true;
        }

        @Nonnull
        ItemStack getBowlStack()
        {
            return bowlStack;
        }
    }
}
