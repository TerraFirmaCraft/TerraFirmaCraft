/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public class ScrapingRecipe extends ItemRecipe
{
    public static final IndirectHashCollection<Item, ScrapingRecipe> CACHE = IndirectHashCollection.createForRecipe(ScrapingRecipe::getValidItems, TFCRecipeTypes.SCRAPING);

    public static final MapCodec<ScrapingRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(c -> c.ingredient),
        ItemStackProvider.CODEC.fieldOf("result").forGetter(c -> c.result),
        ResourceLocation.CODEC.fieldOf("input_texture").forGetter(c -> c.inputTexture),
        ResourceLocation.CODEC.fieldOf("output_texture").forGetter(c -> c.outputTexture),
        ItemStackProvider.CODEC.optionalFieldOf("result_item", ItemStackProvider.empty()).forGetter(c -> c.extraDrop)
    ).apply(i, ScrapingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ScrapingRecipe> STREAM_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, c -> c.ingredient,
        ItemStackProvider.STREAM_CODEC, c -> c.result,
        ResourceLocation.STREAM_CODEC, c -> c.inputTexture,
        ResourceLocation.STREAM_CODEC, c -> c.outputTexture,
        ItemStackProvider.STREAM_CODEC, c -> c.extraDrop,
        ScrapingRecipe::new
    );

    @Nullable
    public static ScrapingRecipe getRecipe(ItemStack stack)
    {
        return RecipeHelpers.getRecipe(CACHE, stack, stack.getItem());
    }

    private final ResourceLocation inputTexture;
    private final ResourceLocation outputTexture;
    private final ItemStackProvider extraDrop;

    public ScrapingRecipe(Ingredient ingredient, ItemStackProvider result, ResourceLocation inputTexture, ResourceLocation outputTexture, ItemStackProvider extraDrop)
    {
        super(ingredient, result);

        this.inputTexture = inputTexture;
        this.outputTexture = outputTexture;
        this.extraDrop = extraDrop;
    }

    public ResourceLocation getInputTexture()
    {
        return inputTexture;
    }

    public ResourceLocation getOutputTexture()
    {
        return outputTexture;
    }

    public ItemStackProvider getExtraDrop()
    {
        return extraDrop;
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.SCRAPING.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.SCRAPING.get();
    }
}
