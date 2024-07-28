/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.food;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.util.Helpers;


/**
 * A component attached to items that stores the food, nutrition, and expiry related information.
 */
public final class FoodComponent implements IFood
{
    public static final Codec<FoodComponent> CODEC = RecordCodecBuilder.<FoodComponent>create(i -> i.group(
        FoodTrait.CODEC.listOf().optionalFieldOf("traits", List.of()).forGetter(c -> c.traits),
        FoodData.CODEC.optionalFieldOf("food").forGetter(c -> c.food),
        Codec.LONG.fieldOf("creation_date").forGetter(c -> c.creationDate)
    ).apply(i, FoodComponent::new)).xmap(Function.identity(), FoodComponent::sanitize);

    public static final StreamCodec<RegistryFriendlyByteBuf, FoodComponent> STREAM_CODEC = StreamCodec.composite(
        FoodTrait.STREAM_CODEC.apply(ByteBufCodecs.list()), c -> c.traits,
        ByteBufCodecs.optional(FoodData.STREAM_CODEC), c -> c.food,
        ByteBufCodecs.VAR_LONG, c -> c.creationDate,
        FoodComponent::new
    );

    private @Nullable FoodDefinition parent;
    private final List<FoodTrait> traits;
    private final Optional<FoodData> food;
    private long creationDate;

    public FoodComponent(FoodDefinition parent)
    {
        this(parent, List.of(), Optional.empty(), FoodCapability.getRoundedCreationDate());
    }

    private FoodComponent(List<FoodTrait> traits, Optional<FoodData> food, long creationDate)
    {
        this(null, traits, food, creationDate);
    }

    /**
     * @param parent       The parent food definition, which is attached based on the stack
     * @param traits       The list of food traits
     * @param food         Custom food data for the food, if present. This will take priority over {@code parent}
     * @param creationDate The creation date for the food. This has multiple special values.
     */
    private FoodComponent(@Nullable FoodDefinition parent, List<FoodTrait> traits, Optional<FoodData> food, long creationDate)
    {
        this.parent = parent;
        this.traits = traits;
        this.food = food;
        this.creationDate = creationDate;
    }

    @Override
    public long getCreationDate()
    {
        return sanitize().creationDate;
    }

    @Override
    public boolean isRotten()
    {
        return FoodCapability.isRotten(getCreationDate(), getDecayDateModifier());
    }

    @Override
    public FoodData getData()
    {
        return food.isPresent() ? food.get()
            : parent != null ? parent.food()
            : FoodData.EMPTY;
    }

    @Override
    public List<FoodTrait> getTraits()
    {
        return traits;
    }

    /**
     * Sanitizes this food component, overwriting interior-mutable values with the most up-to-date values. This should be called upon any access
     * to the interior values of {@link #creationDate} as it may have reset since being updated, or is equal despite being separate values.
     *
     * @return A sanitized component value.
     */
    FoodComponent sanitize()
    {
        if (creationDate >= 0 && parent != null && FoodCapability.isRotten(creationDate, getDecayDateModifier()))
        {
            creationDate = ROTTEN_FLAG;
        }
        return this;
    }

    FoodComponent with(long creationDate)
    {
        return new FoodComponent(parent, traits, food, creationDate);
    }

    FoodComponent with(FoodData food, long creationDate)
    {
        return new FoodComponent(parent, traits, Optional.of(food), creationDate);
    }

    FoodComponent withTraitApplied(FoodTrait trait, long creationDate)
    {
        return new FoodComponent(parent, Helpers.immutableAdd(traits, trait), food, creationDate);
    }

    FoodComponent withTraitsApplied(List<FoodTrait> others)
    {
        return new FoodComponent(parent, Helpers.immutableAddAll(traits, others), food, creationDate);
    }

    FoodComponent withTraitRemoved(FoodTrait trait, long creationDate)
    {
        return new FoodComponent(parent, Helpers.immutableRemove(traits, trait), food, creationDate);
    }

    public void capture(ItemStack stack)
    {
        if (parent == null)
        {
            parent = FoodCapability.getDefinition(stack);
            if (parent == null)
            {
                parent = FoodDefinition.DEFAULT;
            }
        }
        if (creationDate == TRANSIENT_NEVER_DECAY_FLAG)
        {
            stack.set(TFCComponents.FOOD, new FoodComponent(parent, traits, food, FoodCapability.getRoundedCreationDate()));
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (obj instanceof FoodComponent that)
        {
            // Sanitize before comparing directly
            this.sanitize();
            that.sanitize();
            return creationDate == that.creationDate
                && traits.equals(that.traits)
                && food.equals(that.food);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        // Don't hash creation date, as that is interior mutable and may change
        return Objects.hash(traits, food);
    }

    @Override
    public String toString()
    {
        return "Food[creationDate=%s,rotten=%s,traits=%s%s]".formatted(creationDate, isRotten(), traits, food.isPresent() ? ",food=" + food.get() : "");
    }
}
