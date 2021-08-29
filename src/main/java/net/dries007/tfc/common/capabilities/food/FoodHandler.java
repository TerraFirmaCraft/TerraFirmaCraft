/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.food;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

public class FoodHandler implements ICapabilitySerializable<CompoundTag>, IFood
{
    /**
     * Most TFC foods have decay modifiers in the range [1, 4] (high = faster decay)
     * That puts decay times at 25% - 100% of this value
     * So meat / fruit will decay in ~5 days, grains take ~20 days
     * Other modifiers are applied on top of that
     */
    public static final int DEFAULT_DECAY_TICKS = ICalendar.TICKS_IN_DAY * 22;

    public static final long ROTTEN_DATE = Long.MIN_VALUE;
    public static final long NEVER_DECAY_DATE = Long.MAX_VALUE;
    public static final long UNKNOWN_CREATION_DATE = -1;

    // Stacks created at certain times during loading, we infer to be non-decaying ones.
    private static final AtomicBoolean NON_DECAYING = new AtomicBoolean(true);

    public static void setNonDecaying(boolean value)
    {
        FoodHandler.NON_DECAYING.set(value);
    }

    protected final List<FoodTrait> foodTraits;
    private final LazyOptional<IFood> capability;
    protected FoodRecord data;
    protected long creationDate;
    protected boolean isNonDecaying; // This is intentionally not serialized, as we don't want it to preserve over `ItemStack.copy()` operations

    public FoodHandler(FoodRecord data)
    {
        this.foodTraits = new ArrayList<>(2);
        this.data = data;
        this.isNonDecaying = FoodHandler.NON_DECAYING.get();
        this.capability = LazyOptional.of(() -> this);
        this.creationDate = UNKNOWN_CREATION_DATE;
    }

    @Override
    public long getCreationDate(boolean isClientSide)
    {
        if (isNonDecaying)
        {
            return UNKNOWN_CREATION_DATE;
        }
        if (calculateRottenDate(creationDate) < Calendars.get(isClientSide).getTicks())
        {
            this.creationDate = ROTTEN_DATE;
        }
        return creationDate;
    }

    @Override
    public void setCreationDate(long creationDate)
    {
        this.creationDate = creationDate;
    }

    @Override
    public long getRottenDate(boolean isClientSide)
    {
        if (isNonDecaying)
        {
            return NEVER_DECAY_DATE;
        }
        if (creationDate == ROTTEN_DATE)
        {
            return ROTTEN_DATE;
        }
        long rottenDate = calculateRottenDate(creationDate);
        if (rottenDate < Calendars.get(isClientSide).getTicks())
        {
            return ROTTEN_DATE;
        }
        return rottenDate;
    }

    @Override
    public FoodRecord getData()
    {
        return data;
    }

    @Override
    public float getDecayDateModifier()
    {
        // Decay modifiers are higher = shorter
        float mod = data.getDecayModifier() * TFCConfig.SERVER.foodDecayModifier.get().floatValue();
        for (FoodTrait trait : foodTraits)
        {
            mod *= trait.getDecayModifier();
        }
        // The modifier returned is used to calculate time, so higher = longer
        return mod == 0 ? Float.POSITIVE_INFINITY : 1 / mod;
    }

    @Override
    public void setNonDecaying()
    {
        isNonDecaying = true;
    }

    @Override
    public List<FoodTrait> getTraits()
    {
        return foodTraits;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        return cap == FoodCapability.CAPABILITY ? capability.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong("creationDate", getCreationDate());
        if (isDynamic())
        {
            nbt.put("foodData", data.write());
        }
        // Traits are sorted so they match when trying to stack them
        ListTag traitList = new ListTag();
        for (FoodTrait trait : foodTraits)
        {
            traitList.add(StringTag.valueOf(trait.getName()));
        }
        nbt.put("traits", traitList);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        foodTraits.clear();
        if (isDynamic())
        {
            data = new FoodRecord(nbt.getCompound("foodData"));
        }
        ListTag traitList = nbt.getList("traits", Constants.NBT.TAG_STRING);
        for (int i = 0; i < traitList.size(); i++)
        {
            foodTraits.add(FoodTrait.TRAITS.get(traitList.getString(i)));
        }
        creationDate = nbt.contains("creationDate") ? nbt.getLong("creationDate") : FoodCapability.getRoundedCreationDate();
    }

    /**
     * This marks if the food data should be serialized. For normal food items, it isn't, because all values are provided on construction via CapabilityFood. Only mark this if food data will change per item stack
     */
    protected boolean isDynamic()
    {
        return false;
    }

    private long calculateRottenDate(long creationDateIn)
    {
        float decayMod = getDecayDateModifier();
        if (decayMod == Float.POSITIVE_INFINITY)
        {
            // Infinite decay modifier
            return Long.MAX_VALUE;
        }
        return creationDateIn + (long) (decayMod * DEFAULT_DECAY_TICKS);
    }
}
