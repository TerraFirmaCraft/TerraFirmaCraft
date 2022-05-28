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
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DynamicBowlFood extends DecayingItem
{
    public DynamicBowlFood(Properties properties)
    {
        super(properties);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new DynamicBowlHandler(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity)
    {
        final ItemStack result = super.finishUsingItem(stack, level, entity);
        if (entity instanceof Player player && player.getAbilities().instabuild)
        {
            return result;
        }
        else
        {
            return stack.getCapability(FoodCapability.CAPABILITY)
                .map(cap -> cap instanceof DynamicBowlHandler handler ? handler.getBowl() : result)
                .orElse(result);
        }
    }

    public static class DynamicBowlHandler extends FoodHandler.Dynamic
    {
        private final ItemStack stack;
        private ItemStack bowl;

        private boolean initialized;

        protected DynamicBowlHandler(ItemStack stack)
        {
            this.stack = stack;
            this.bowl = ItemStack.EMPTY;
        }

        public ItemStack getBowl()
        {
            return bowl.copy();
        }

        public void setBowl(ItemStack bowl)
        {
            this.bowl = Helpers.copyWithSize(bowl, 1);
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
    }
}
