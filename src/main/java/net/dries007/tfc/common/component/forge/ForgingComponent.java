/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.forge;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.AnvilRecipe;

/**
 * A mutable view of the anvil working data attached to an item stack. As data components are nominally immutable,
 * and we want to expose mutable-like interfaces, we wrap them in this when queried.
 *
 * @param work the current work value, in the range [0, {@link ForgeStep#LIMIT}]
 * @param target the work target for the current selected recipe, if present, or -1 if there is no recipe.
 */
public record ForgingComponent(
    ForgeSteps steps,
    int work,
    int target,
    AnvilRecipeHolder holder
)
{
    public static final Codec<ForgingComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
        ForgeSteps.CODEC.optionalFieldOf("steps", ForgeSteps.EMPTY).forGetter(c -> c.steps),
        Codec.INT.optionalFieldOf("work", 0).forGetter(c -> c.work),
        Codec.INT.optionalFieldOf("target", 0).forGetter(c -> c.target),
        ResourceLocation.CODEC.optionalFieldOf("recipe")
            .xmap(
                opt -> new AnvilRecipeHolder(opt.orElse(null), null),
                holder -> Optional.ofNullable(holder.recipeId))
            .forGetter(c -> c.holder)
    ).apply(i, ForgingComponent::new));

    public static final StreamCodec<ByteBuf, ForgingComponent> STREAM_CODEC = StreamCodec.composite(
        ForgeSteps.STREAM_CODEC, c -> c.steps,
        ByteBufCodecs.VAR_INT, c -> c.work,
        ByteBufCodecs.VAR_INT, c -> c.target,
        ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC)
            .map(
                opt -> new AnvilRecipeHolder(opt.orElse(null), null),
                holder -> Optional.ofNullable(holder.recipeId)
            ), c -> c.holder,
        ForgingComponent::new
    );

    public static final ForgingComponent DEFAULT = new ForgingComponent(
        new ForgeSteps(Optional.empty(), Optional.empty(), Optional.empty(), 0),
        0, 0,
        new AnvilRecipeHolder(null, null));

    /**
     * @return The current anvil recipe, or {@code null} if none is selected, possibly looking up by ID.
     */
    @Nullable
    public AnvilRecipe recipe()
    {
        return holder.recipe();
    }

    public boolean matches(List<ForgeRule> rules)
    {
        for (ForgeRule rule : rules)
        {
            if (!matches(rule))
            {
                return false;
            }
        }
        return true;
    }

    public boolean matches(ForgeRule rule)
    {
        return rule.matches(steps);
    }

    ForgingComponent withRecipe(@Nullable AnvilRecipe recipe, int target)
    {
        return new ForgingComponent(steps, work, target, new AnvilRecipeHolder(null, recipe));
    }

    ForgingComponent withStep(@Nullable ForgeStep step, int amount)
    {
        return new ForgingComponent(steps.withStep(step), work + amount, target, holder);
    }

    ForgingComponent withNoRecipeIfNotWorked()
    {
        return steps.any() ? this : new ForgingComponent(steps, work, target, new AnvilRecipeHolder(null, null));
    }

    /**
     * A small helper to provide interior mutability for the recipe ID and recipe, which need to query the recipe
     * ID cache on access, and store the cached recipe for future queries.
     */
    static class AnvilRecipeHolder
    {
        @Nullable ResourceLocation recipeId;
        @Nullable AnvilRecipe recipe;

        AnvilRecipeHolder(@Nullable ResourceLocation recipeId, @Nullable AnvilRecipe recipe)
        {
            this.recipe = recipe;
            this.recipeId = recipeId;
        }

        @Nullable
        AnvilRecipe recipe()
        {
            if (recipe != null)
            {
                return recipe;
            }
            if (recipeId != null)
            {
                recipe = AnvilRecipe.byId(recipeId);
                if (recipe == null)
                {
                    recipeId = null;
                }
            }
            return recipe;
        }

        @Override
        public boolean equals(Object obj)
        {
            return obj instanceof AnvilRecipeHolder holder
                && recipe == holder.recipe
                && Objects.equals(recipeId, holder.recipeId);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(recipe, recipeId);
        }

        @Override
        public String toString()
        {
            return recipeId != null ? recipeId.toString() : (recipe != null ? "???" : "null");
        }
    }
}