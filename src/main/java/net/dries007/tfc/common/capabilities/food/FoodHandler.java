/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.food;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
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
    public static final long NEVER_DECAY_CREATION_DATE = -2;
    public static final long UNKNOWN_CREATION_DATE = -1;

    // Stacks created at certain times during loading, we infer to be non-decaying ones.
    private static final AtomicBoolean NON_DECAYING = new AtomicBoolean(true);

    public static void setNonDecaying(boolean value)
    {
        FoodHandler.NON_DECAYING.set(value);
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
        this.isNonDecaying = FoodHandler.NON_DECAYING.get();
        this.capability = LazyOptional.of(() -> this);
        this.creationDate = UNKNOWN_CREATION_DATE;
    }

    @Override
    public long getCreationDate()
    {
        if (isNonDecaying)
        {
            return UNKNOWN_CREATION_DATE;
        }
        if (creationDate == UNKNOWN_CREATION_DATE)
        {
            this.creationDate = FoodCapability.getRoundedCreationDate();
        }
        final long rottenDate = calculateRottenDate(creationDate);
        if (rottenDate == NEVER_DECAY_DATE)
        {
            this.creationDate = NEVER_DECAY_CREATION_DATE;
        }
        if (rottenDate < Calendars.get().getTicks())
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
        final long creationDate = getCreationDate();
        if (creationDate == ROTTEN_DATE)
        {
            return ROTTEN_DATE;
        }
        final long rottenDate = calculateRottenDate(creationDate);
        if (rottenDate < Calendars.get().getTicks())
        {
            return ROTTEN_DATE;
        }
        return rottenDate;
    }

    @Override
    public boolean isTransientNonDecaying()
    {
        return isNonDecaying;
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
        float mod = data.decayModifier() * TFCConfig.SERVER.foodDecayModifier.get().floatValue();
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
        creationDate = UNKNOWN_CREATION_DATE;
    }

    @Override
    public List<FoodTrait> getTraits()
    {
        return foodTraits;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        if (cap == FoodCapability.CAPABILITY)
        {
            return capability.cast();
        }
        return LazyOptional.empty();
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
            traitList.add(StringTag.valueOf(FoodTrait.getId(trait).toString()));
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
            data = FoodData.read(nbt.getCompound("foodData"));
        }
        ListTag traitList = nbt.getList("traits", Tag.TAG_STRING);
        for (int i = 0; i < traitList.size(); i++)
        {
            final FoodTrait trait = FoodTrait.getTrait(new ResourceLocation(traitList.getString(i)));
            if (trait != null)
            {
                foodTraits.add(trait);
            }
        }
        creationDate = nbt.contains("creationDate") ? nbt.getLong("creationDate") : UNKNOWN_CREATION_DATE;
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
            return NEVER_DECAY_DATE;
        }
        return creationDateIn + (long) (decayMod * DEFAULT_DECAY_TICKS);
    }

    /**
     * Convenience class for dynamic food handlers
     */
    public static class Dynamic extends FoodHandler
    {
        protected List<ItemStack> ingredients = new ArrayList<>();

        public Dynamic()
        {
            super(FoodData.EMPTY);
        }

        public void setFood(FoodData data)
        {
            this.data = data;
        }

        public void setIngredients(List<ItemStack> ingredients)
        {
            this.ingredients = ingredients;
        }

        public List<ItemStack> getIngredients()
        {
            return ingredients;
        }

        @Override
        public CompoundTag serializeNBT()
        {
            var tag = super.serializeNBT();
            tag.put("ingredients", Helpers.writeItemStacksToNbt(ingredients));
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt)
        {
            super.deserializeNBT(nbt);
            Helpers.readItemStacksFromNbt(ingredients, nbt.getList("ingredients", Tag.TAG_COMPOUND));
        }

        @Override
        public void addTooltipInfo(ItemStack stack, List<Component> text)
        {
            super.addTooltipInfo(stack, text);
            final List<Component> tooltips = new ArrayList<>();
            for (ItemStack ingredient : ingredients)
            {
                if (!ingredient.isEmpty())
                {
                    tooltips.add(ingredient.getHoverName());
                }
            }
            // for each distinct item (as determined by Object#equals) (which is implemented by BaseComponent)
            tooltips.stream().distinct().forEach(item -> {
                // count the frequency of that tooltip and append it with a count
                text.add(Helpers.literal(Collections.frequency(tooltips, item) + "x ").append(item));
            });
        }

        @Override
        public boolean isDynamic()
        {
            return true;
        }
    }
}
