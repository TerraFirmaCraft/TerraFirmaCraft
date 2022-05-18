/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;


import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodHandler;
import net.dries007.tfc.common.capabilities.food.FoodRecord;
import org.jetbrains.annotations.NotNull;
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
        return new SoupHandler(stack, SOUP_STATS);
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

    public static class SoupHandler extends FoodHandler.Dynamic
    {
        private final ItemStack stack;
        private ItemStack bowl;
        private boolean initialized;

        public SoupHandler(ItemStack stack, FoodRecord data)
        {
            super(data);
            this.stack = stack;
            this.bowl = ItemStack.EMPTY;
        }

        public void setBowl(ItemStack bowl)
        {
            ItemStack copy = bowl.copy();
            copy.setCount(1);
            this.bowl = copy;
            save();
        }

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
        {
            if (cap == FoodCapability.CAPABILITY)
            {
                load();
            }
            return super.getCapability(cap, side);
        }

        private void save()
        {
            final CompoundTag tag = stack.getOrCreateTag();
            if (!bowl.isEmpty())
            {
                tag.put("bowl", bowl.save(new CompoundTag()));
            }
        }

        private void load()
        {
            if (!initialized)
            {
                initialized = true;
                final CompoundTag tag = stack.getOrCreateTag();
                bowl = tag.contains("bowl") ? ItemStack.of(tag.getCompound("bowl")) : ItemStack.EMPTY;
            }
        }

        public ItemStack getBowl()
        {
            return bowl.copy();
        }
    }
}
