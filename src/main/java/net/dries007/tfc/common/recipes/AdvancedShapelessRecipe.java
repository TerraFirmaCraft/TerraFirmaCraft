/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.Optional;
import java.util.function.Function;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
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
        // This part of the codec is identical to the shapeless recipe codec
        Codec.STRING.optionalFieldOf("group", "").forGetter(ShapelessRecipe::getGroup),
        CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(ShapelessRecipe::category),
        Ingredient.CODEC_NONEMPTY
            .listOf()
            .fieldOf("ingredients")
            .flatXmap(list -> {
                final Ingredient[] values = list.toArray(Ingredient[]::new); // Neo skip the empty check and immediately create the array.
                final int length = ShapedRecipePattern.getMaxHeight() * ShapedRecipePattern.getMaxWidth();
                return values.length == 0
                    ? DataResult.error(() -> "No ingredients for shapeless recipe")
                    : values.length > length
                        ? DataResult.error(() -> "Too many ingredients for shapeless recipe. The maximum is: %s".formatted(length))
                        : DataResult.success(NonNullList.of(Ingredient.EMPTY, values));
            }, DataResult::success)
            .forGetter(ShapelessRecipe::getIngredients),
        ItemStackProvider.CODEC.fieldOf("result").forGetter(c -> c.result),
        ItemStackProvider.CODEC.optionalFieldOf("remainder").forGetter(c -> c.remainder),
        Ingredient.CODEC.optionalFieldOf("primary_ingredient").forGetter(c -> c.primaryIngredient)
    ).apply(i, AdvancedShapelessRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AdvancedShapelessRecipe> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, ShapelessRecipe::getGroup,
        CraftingBookCategory.STREAM_CODEC, ShapelessRecipe::category,
        Ingredient.CONTENTS_STREAM_CODEC
            .apply(ByteBufCodecs.list())
            .map(NonNullList::copyOf, Function.identity()), ShapelessRecipe::getIngredients,
        ItemStackProvider.STREAM_CODEC, c -> c.result,
        ByteBufCodecs.optional(ItemStackProvider.STREAM_CODEC), c -> c.remainder,
        ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC), c -> c.primaryIngredient,
        AdvancedShapelessRecipe::new
    );

    private final ItemStackProvider result;
    private final Optional<ItemStackProvider> remainder;
    private final Optional<Ingredient> primaryIngredient;

    public AdvancedShapelessRecipe(String group, CraftingBookCategory category, NonNullList<Ingredient> ingredients, ItemStackProvider result, Optional<ItemStackProvider> remainder, Optional<Ingredient> primaryIngredient)
    {
        super(group, category, ItemStack.EMPTY, ingredients);

        this.result = result;
        this.remainder = remainder;
        this.primaryIngredient = primaryIngredient;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries)
    {
        RecipeHelpers.setCraftingInput(input);
        final ItemStack output = result.getSingleStack(getPrimaryInput(input).copy());
        RecipeHelpers.clearCraftingInput();
        return output;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries)
    {
        return result.getEmptyStack();
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
        return result.dependsOnInput();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.ADVANCED_SHAPELESS_CRAFTING.get();
    }
}
