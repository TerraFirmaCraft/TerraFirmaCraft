/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.egg;

import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.food.DelegateFoodHandler;
import net.dries007.tfc.common.capabilities.food.FoodData;
import net.dries007.tfc.common.capabilities.food.FoodHandler;
import net.dries007.tfc.common.capabilities.food.IFood;

public class EggHandler implements IEgg, DelegateFoodHandler
{
    private final ItemStack stack;
    private final FoodHandler foodHandler;

    private boolean fertilized;
    private long hatchDay;
    @Nullable
    private CompoundTag entityTag;

    private boolean initialized; // If the internal capability objects have loaded their data.

    public EggHandler(ItemStack itemStack)
    {
        stack = itemStack;
        fertilized = false;
        hatchDay = 0;
        entityTag = null;
        foodHandler = new FoodHandler(FoodData.decayOnly(2f));
    }

    @Override
    public long getHatchDay()
    {
        return hatchDay;
    }

    @Override
    public Optional<Entity> getEntity(Level level)
    {
        return entityTag != null ? EntityType.create(entityTag, level) : Optional.empty();
    }

    @Override
    public boolean isFertilized()
    {
        return fertilized;
    }

    @Override
    public void setFertilized(@NotNull Entity entity, long hatchDay)
    {
        fertilized = true;
        entityTag = entity.serializeNBT();
        this.hatchDay = hatchDay;
        save();
    }

    @Override
    public void removeFertilization()
    {
        entityTag = null;
        fertilized = false;
        hatchDay = 0;
        stack.removeTagKey("entity");
        stack.removeTagKey("fertilized");
        stack.removeTagKey("hatch");
    }

    @Override
    public IFood getFoodHandler()
    {
        return foodHandler;
    }

    @Override
    public void addTooltipInfo(ItemStack stack, List<Component> text)
    {
        if (!isFertilized())
        {
            DelegateFoodHandler.super.addTooltipInfo(stack, text);
        }
    }

    @Override
    public long getRottenDate()
    {
        if (isFertilized())
        {
            return FoodHandler.NEVER_DECAY_DATE;
        }
        return DelegateFoodHandler.super.getRottenDate();
    }

    private void load()
    {
        if (!initialized)
        {
            initialized = true;

            final CompoundTag tag = stack.getOrCreateTag();
            if (tag.contains("entity", Tag.TAG_COMPOUND))
            {
                entityTag = tag.getCompound("entity");
                fertilized = tag.getBoolean("fertilized");
                hatchDay = tag.getLong("hatch");
            }
            else
            {
                fertilized = false;
                entityTag = null;
                hatchDay = 0;
            }
        }
    }

    private void save()
    {
        final CompoundTag tag = stack.getOrCreateTag();
        if (entityTag != null)
        {
            tag.put("entity", entityTag);
            tag.putBoolean("fertilized", fertilized);
            tag.putLong("hatch", hatchDay);
        }
    }
}
