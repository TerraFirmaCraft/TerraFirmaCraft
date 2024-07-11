/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.Optional;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;

/**
 * An advanced recipe that takes advantage of two additional mechanisms to provide a rich set of behaviors.
 * <ul>
 *     <li>It uses an {@link ItemStackProvider} as an output, identifying the input via the {@code primary_ingredient}</li>
 *     <li>It uses an {@link ItemStackProvider} to compute the remaining items, allowing damaging inputs or other functions</li>
 * </ul>
 */
public class AdvancedShapelessRecipe extends ShapelessRecipe
{
    public static final MapCodec<AdvancedShapelessRecipe> CODEC = RecordCodecBuilder.<AdvancedShapelessRecipe>mapCodec(i -> i.group(
        RecipeSerializer.SHAPELESS_RECIPE.codec().forGetter(c -> c),
        ItemStackProvider.CODEC.optionalFieldOf("result_provider").forGetter(c -> c.result),
        ItemStackProvider.CODEC.optionalFieldOf("remainder").forGetter(c -> c.remainder),
        Ingredient.CODEC.optionalFieldOf("primary_ingredient").forGetter(c -> c.primaryIngredient)
    ).apply(i, AdvancedShapelessRecipe::new)).validate(recipe -> recipe.result.isPresent() && recipe.primaryIngredient.isEmpty()
        ? DataResult.error(() -> "If result_provider is present, then primary_ingredient must also be present")
        : DataResult.success(recipe));

    public static final StreamCodec<RegistryFriendlyByteBuf, AdvancedShapelessRecipe> STREAM_CODEC = StreamCodec.composite(
        RecipeSerializer.SHAPELESS_RECIPE.streamCodec(), c -> c,
        ByteBufCodecs.optional(ItemStackProvider.STREAM_CODEC), c -> c.result,
        ByteBufCodecs.optional(ItemStackProvider.STREAM_CODEC), c -> c.remainder,
        ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC), c -> c.primaryIngredient,
        AdvancedShapelessRecipe::new
    );

    private final Optional<ItemStackProvider> result;
    private final Optional<ItemStackProvider> remainder;
    private final Optional<Ingredient> primaryIngredient;

    public AdvancedShapelessRecipe(ShapelessRecipe parent, Optional<ItemStackProvider> result, Optional<ItemStackProvider> remainder, Optional<Ingredient> primaryIngredient)
    {
        super(parent.getGroup(), parent.category(), RecipeHelpers.getResultUnsafe(parent), parent.getIngredients());

        this.result = result;
        this.remainder = remainder;
        this.primaryIngredient = primaryIngredient;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries)
    {
        return this.result.map(result -> {
            RecipeHelpers.setCraftingInput(input);
            final ItemStack output = result.getSingleStack(getPrimaryInput(input).copy());
            RecipeHelpers.clearCraftingInput();
            return output;
        }).orElseGet(() -> super.assemble(input, registries));
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries)
    {
        return result.map(ItemStackProvider::getEmptyStack).orElseGet(() -> super.getResultItem(registries));
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input)
    {
        return remainder.map(remainder -> RecipeHelpers.getRemainderItemsWithProvider(input, remainder))
            .orElseGet(() -> super.getRemainingItems(input));
    }

    private ItemStack getPrimaryInput(CraftingInput input)
    {
        final Ingredient primaryInput = primaryIngredient.orElseThrow();
        for (int i = 0; i < input.size(); i++)
        {
            final ItemStack item = input.getItem(i);
            if (primaryInput.test(item))
            {
                return item;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial()
    {
        return result.isPresent() && result.get().dependsOnInput();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.ADVANCED_SHAPELESS_CRAFTING.get();
    }
}
