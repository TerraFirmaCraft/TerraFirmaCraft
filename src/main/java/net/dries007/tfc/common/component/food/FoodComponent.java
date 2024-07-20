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
 *
 * @param holder The parent food definition, which is attached based on the stack
 * @param traits The list of food traits
 * @param food Custom food data for the food, if present. This will take priority over {@code parent}
 * @param creationDate The creation date for the food. This has multiple special values.
 */
public record FoodComponent(
    ParentHolder holder,
    List<FoodTrait> traits,
    Optional<FoodData> food,
    long creationDate
) implements IFood
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

    public FoodComponent(FoodDefinition parent)
    {
        this(new ParentHolder(parent), List.of(), Optional.empty(), FoodCapability.getRoundedCreationDate());
    }

    FoodComponent(List<FoodTrait> traits, Optional<FoodData> food, long creationDate)
    {
        this(new ParentHolder(null), traits, food, creationDate);
    }

    @Override
    public long getCreationDate()
    {
        return creationDate < 0 || !FoodCapability.isRotten(creationDate, getDecayDateModifier())
            ? creationDate
            : ROTTEN_FLAG;
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
            : holder.value != null ? holder.value.food()
            : FoodData.EMPTY;
    }

    @Override
    public List<FoodTrait> getTraits()
    {
        return traits;
    }

    /**
     * Sanitizes this component before saving, so the reported creation date is equal to the current component value
     * @return A sanitized component value.
     */
    FoodComponent sanitize()
    {
        final long creationDate = getCreationDate();
        return this.creationDate != creationDate
            ? new FoodComponent(holder, traits, food, creationDate)
            : this;
    }

    FoodComponent with(long creationDate)
    {
        return new FoodComponent(holder, traits, food, creationDate);
    }

    FoodComponent with(FoodData food, long creationDate)
    {
        return new FoodComponent(holder, traits, Optional.of(food), creationDate);
    }

    FoodComponent withTraitApplied(FoodTrait trait, long creationDate)
    {
        return new FoodComponent(holder, Helpers.immutableAdd(traits, trait), food, creationDate);
    }

    FoodComponent withTraitsApplied(List<FoodTrait> others)
    {
        return new FoodComponent(holder, Helpers.immutableAddAll(traits, others), food, creationDate);
    }

    FoodComponent withTraitRemoved(FoodTrait trait, long creationDate)
    {
        return new FoodComponent(holder, Helpers.immutableRemove(traits, trait), food, creationDate);
    }

    public void capture(ItemStack stack)
    {
        if (holder.value == null)
        {
            holder.value = FoodCapability.getDefinition(stack);
            if (holder.value == null)
            {
                holder.value = FoodDefinition.DEFAULT;
            }
        }
        if (creationDate == TRANSIENT_NEVER_DECAY_FLAG)
        {
            stack.set(TFCComponents.FOOD, new FoodComponent(holder, traits, food, FoodCapability.getRoundedCreationDate()));
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        return this == obj || (obj instanceof FoodComponent that
            && traits.equals(that.traits)
            && food.equals(that.food)
            && creationDate == that.creationDate);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(traits, food, creationDate);
    }

    @Override
    public String toString()
    {
        return "Food[creationDate=%s,rotten=%s,traits=%s%s]".formatted(creationDate, isRotten(), traits, food.isPresent() ? ",food=" + food.get() : "");
    }

    static class ParentHolder
    {
        @Nullable FoodDefinition value;

        ParentHolder(@Nullable FoodDefinition value)
        {
            this.value = value;
        }
    }
}
