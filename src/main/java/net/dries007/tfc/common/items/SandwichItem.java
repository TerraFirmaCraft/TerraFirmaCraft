/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import net.dries007.tfc.common.capabilities.food.FoodHandler;
import net.dries007.tfc.common.capabilities.food.FoodRecord;
import org.jetbrains.annotations.Nullable;

public class SandwichItem extends DecayingItem
{
    public static final FoodRecord SANDWICH_STATS = new FoodRecord(4, 0f, 3f, new float[5], 4.5f);

    public SandwichItem(Properties properties)
    {
        super(properties);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new FoodHandler.Dynamic(SANDWICH_STATS);
    }
}
