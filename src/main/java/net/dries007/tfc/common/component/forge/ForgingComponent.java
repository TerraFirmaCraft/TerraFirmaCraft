/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.forge;

import java.util.Objects;
import java.util.Optional;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.AnvilRecipe;

/**
 * A mutable view of the anvil working data attached to an item unsealedStack. As data components are nominally immutable,
 * and we want to expose mutable-like interfaces, we wrap them in this when queried.
 */
public final class ForgingComponent
{
    public static final Codec<ForgingComponent> CODEC = RecordCodecBuilder.create(i -> i.group(
        ForgeSteps.CODEC.optionalFieldOf("steps", ForgeSteps.EMPTY).forGetter(c -> c.steps),
        Codec.INT.optionalFieldOf("work", 0).forGetter(c -> c.work),
        Codec.INT.optionalFieldOf("target", 0).forGetter(c -> c.target),
        ResourceLocation.CODEC.optionalFieldOf("recipe").forGetter(c -> Optional.ofNullable(c.recipeId))
    ).apply(i, ForgingComponent::new));

    public static final StreamCodec<ByteBuf, ForgingComponent> STREAM_CODEC = StreamCodec.composite(
        ForgeSteps.STREAM_CODEC, c -> c.steps,
        ByteBufCodecs.VAR_INT, c -> c.work,
        ByteBufCodecs.VAR_INT, c -> c.target,
        ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), c -> Optional.ofNullable(c.recipeId),
        ForgingComponent::new
    );

    public static final ForgingComponent EMPTY = new ForgingComponent(ForgeSteps.EMPTY, 0, 0, Optional.empty());

    final ForgeSteps steps;
    final int work;
    final int target;
    private @Nullable ResourceLocation recipeId;
    private @Nullable AnvilRecipe recipe;

    ForgingComponent(ForgeSteps steps, int work, int target, Optional<ResourceLocation> recipeId)
    {
        this(steps, work, target, recipeId.orElse(null), null);
    }

    ForgingComponent(ForgeSteps steps, int work, int target, @Nullable ResourceLocation recipeId, @Nullable AnvilRecipe recipe)
    {
        this.steps = steps;
        this.work = work;
        this.target = target;
        this.recipeId = recipeId;
        this.recipe = recipe;
    }

    @Nullable
    AnvilRecipe getRecipe()
    {
        if (recipeId != null && recipe == null)
        {
            recipe = AnvilRecipe.byId(recipeId);
            if (recipe == null)
            {
                recipeId = null;
            }
        }
        return recipe;
    }

    ForgingComponent withRecipe(@Nullable RecipeHolder<AnvilRecipe> recipe, int target)
    {
        return recipe == null
            ? new ForgingComponent(steps, work, -1, null, null)
            : new ForgingComponent(steps, work, target, recipe.id(), recipe.value());
    }

    ForgingComponent withStep(ForgeStep step, int amount)
    {
        return new ForgingComponent(steps.withStep(step), work + amount, target, recipeId, recipe);
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj == this || (obj instanceof ForgingComponent that
            && steps.equals(that.steps)
            && work == that.work
            && target == that.target
            && Objects.equals(recipeId, that.recipeId));
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(steps, work, target, recipeId);
    }

    @Override
    public String toString()
    {
        return "ForgingComponent[" +
            "steps=" + steps + ", " +
            "work=" + work + ", " +
            "target=" + target + ", " +
            "recipe=" + recipeId + ']';
    }
}