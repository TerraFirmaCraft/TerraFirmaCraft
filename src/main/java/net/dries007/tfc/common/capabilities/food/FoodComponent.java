/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.food;

import java.util.List;
import java.util.Optional;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;


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
)
{
    public static final Codec<FoodComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
        FoodTrait.CODEC.listOf().optionalFieldOf("traits", List.of()).forGetter(c -> c.traits),
        FoodData.CODEC.optionalFieldOf("food").forGetter(c -> c.food),
        Codec.LONG.optionalFieldOf("creation_date", IFood.UNKNOWN_CREATION_DATE).forGetter(c -> c.creationDate)
    ).apply(i, FoodComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FoodComponent> STREAM_CODEC = StreamCodec.composite(
        FoodTrait.STREAM_CODEC.apply(ByteBufCodecs.list()), c -> c.traits,
        ByteBufCodecs.optional(FoodData.STREAM_CODEC), c -> c.food,
        ByteBufCodecs.VAR_LONG, c -> c.creationDate,
        FoodComponent::new
    );

    public static FoodComponent with(FoodDefinition parent)
    {
        return new FoodComponent(new ParentHolder(parent));
    }

    FoodComponent(ParentHolder parent)
    {
        this(parent, List.of(), Optional.empty(), IFood.UNKNOWN_CREATION_DATE);
    }

    FoodComponent(List<FoodTrait> traits, Optional<FoodData> food, long creationDate)
    {
        this(new ParentHolder(null), traits, food, creationDate);
    }

    FoodComponent with(long creationDate)
    {
        return new FoodComponent(holder, traits, food, creationDate);
    }

    FoodComponent with(FoodData food)
    {
        return new FoodComponent(holder, traits, Optional.of(food), creationDate);
    }

    FoodData getFood()
    {
        return food.isPresent() ? food.get()
            : holder.value != null ? holder.value.food()
            : FoodData.EMPTY;
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
