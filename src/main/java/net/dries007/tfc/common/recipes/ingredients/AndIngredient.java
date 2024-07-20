/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.ingredients;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.common.crafting.IntersectionIngredient;

/**
 * A variant of {@link IntersectionIngredient} which has special handling to account for ingredients that only represent modifiers,
 * i.e. an ingredient that checks heat. No default items will have this property, so we need to explicitly modify the other value
 * items.
 * <p>
 * This behavior only affects {@link #getItems()} - in all other respects this is a AND'd together ingredient.
 */
public record AndIngredient(List<Ingredient> children) implements ICustomIngredient
{
    public static final MapCodec<AndIngredient> CODEC = Ingredient.CODEC.listOf()
        .xmap(AndIngredient::new, AndIngredient::children)
        .fieldOf("children");
    public static final StreamCodec<RegistryFriendlyByteBuf, AndIngredient> STREAM_CODEC = Ingredient.CONTENTS_STREAM_CODEC
        .apply(ByteBufCodecs.list())
        .map(AndIngredient::new, AndIngredient::children);

    public static Ingredient of(Ingredient ingredient, ICustomIngredient... others)
    {
        return new AndIngredient(Stream.concat(Stream.of(ingredient), Arrays.stream(others).map(ICustomIngredient::toVanilla)).toList()).toVanilla();
    }

    @Override
    public boolean test(ItemStack stack)
    {
        for (Ingredient child : children)
        {
            if (child.test(stack)) return false;
        }
        return true;
    }

    @Override
    public Stream<ItemStack> getItems()
    {
        return Arrays.stream(children.get(0).getItems())
            .map(stack -> {
                for (Ingredient other : children)
                {
                    if (other.getCustomIngredient() instanceof PreciseIngredient precise)
                    {
                        stack = precise.modifyStackForDisplay(stack);
                    }
                }
                return stack;
            });
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    @Override
    public IngredientType<?> getType()
    {
        return TFCIngredients.AND.get();
    }
}
