package net.dries007.tfc.common.capabilities.food;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

public class FoodHandler implements ICapabilitySerializable<CompoundNBT>, IFood
{
    /**
     * Most TFC foods have decay modifiers in the range [1, 4] (high = faster decay)
     * That puts decay times at 25% - 100% of this value
     * So meat / fruit will decay in ~5 days, grains take ~20 days
     * Other modifiers are applied on top of that
     */
    public static final int DEFAULT_DECAY_TICKS = ICalendar.TICKS_IN_DAY * 22;
    private static final long ROTTEN_DATE = Long.MIN_VALUE;
    private static final long NEVER_DECAY_DATE = Long.MAX_VALUE;
    private static final long UNKNOWN_CREATION_DATE = 0;
    private static boolean markStacksNonDecaying = true;

    public static void setNonDecaying(boolean markStacksNonDecaying)
    {
        FoodHandler.markStacksNonDecaying = markStacksNonDecaying;
    }

    protected final List<FoodTrait> foodTraits;
    private final LazyOptional<IFood> capability;
    protected FoodData data;
    protected long creationDate;
    protected boolean isNonDecaying; // This is intentionally not serialized, as we don't want it to preserve over `ItemStack.copy()` operations

    public FoodHandler(FoodData data)
    {
        this.foodTraits = new ArrayList<>(2);
        this.data = data;
        this.isNonDecaying = FoodHandler.markStacksNonDecaying;
        this.capability = LazyOptional.of(() -> this);
    }

    @Override
    public long getCreationDate()
    {
        if (isNonDecaying)
        {
            return UNKNOWN_CREATION_DATE;
        }
        if (calculateRottenDate(creationDate) < Calendars.SERVER.getTicks())
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
    public long getRottenDate()
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
        if (rottenDate < Calendars.SERVER.getTicks())
        {
            return ROTTEN_DATE;
        }
        return rottenDate;
    }

    @Override
    public FoodData getData()
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

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        return cap == FoodCapability.CAPABILITY ? capability.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putLong("creationDate", getCreationDate());
        if (isDynamic())
        {
            nbt.put("foodData", data.serializeNBT());
        }
        // Traits are sorted so they match when trying to stack them
        ListNBT traitList = new ListNBT();
        for (FoodTrait trait : foodTraits)
        {
            traitList.add(StringNBT.valueOf(trait.getName()));
        }
        nbt.put("traits", traitList);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        foodTraits.clear();
        if (isDynamic())
        {
            data = new FoodData(nbt.getCompound("foodData"));
        }
        ListNBT traitList = nbt.getList("traits", Constants.NBT.TAG_STRING);
        for (int i = 0; i < traitList.size(); i++)
        {
            foodTraits.add(FoodTrait.getTraits().get(traitList.getString(i)));
        }
        creationDate = nbt.getLong("creationDate");
        if (creationDate == 0)
        {
            // Stop defaulting to zero, in cases where the item stack is cloned or copied from one that was initialized at load (and thus was before the calendar was initialized)
            creationDate = FoodCapability.getRoundedCreationDate();
        }
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
