/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.capabilities.ICapabilityProvider;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodHandler;
import net.dries007.tfc.common.capabilities.food.FoodRecord;
import org.jetbrains.annotations.Nullable;

public class SoupItem extends DecayingItem
{
    public static final FoodRecord SOUP_STATS = new FoodRecord(4, 0f, 3f, new float[5], 3.5f);

    public SoupItem(Properties properties)
    {
        super(properties);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new SoupHandler(SOUP_STATS);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity)
    {
        ItemStack itemstack = super.finishUsingItem(stack, level, entity);
        if (entity instanceof Player player && player.getAbilities().instabuild)
        {
            return itemstack;
        }
        else
        {
            return stack.getCapability(FoodCapability.CAPABILITY)
                .filter(cap -> cap instanceof SoupHandler)
                .map(food -> ((SoupHandler) food).getBowl())
                .orElse(itemstack);
        }
    }

    public static class SoupHandler extends FoodHandler
    {
        private ItemStack bowl;

        public SoupHandler(FoodRecord data)
        {
            super(data);
            this.bowl = ItemStack.EMPTY;
        }

        public void setFood(FoodRecord data)
        {
            this.data = data;
        }

        public void setBowl(ItemStack bowl)
        {
            ItemStack copy = bowl.copy();
            copy.setCount(1);
            this.bowl = copy;
        }

        @Override
        public CompoundTag serializeNBT()
        {
            CompoundTag tag = super.serializeNBT();
            tag.put("bowl", bowl.save(new CompoundTag()));
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt)
        {
            super.deserializeNBT(nbt);
            bowl = ItemStack.of(nbt.getCompound("bowl"));
        }

        @Override
        protected boolean isDynamic()
        {
            return true;
        }

        public ItemStack getBowl()
        {
            return bowl.copy();
        }
    }
}
