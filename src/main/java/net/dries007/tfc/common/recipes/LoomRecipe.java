/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public class LoomRecipe implements INoopInputRecipe, IRecipePredicate<ItemStack>
{
    public static final IndirectHashCollection<Item, LoomRecipe> CACHE = IndirectHashCollection.createForRecipe(r -> RecipeHelpers.itemKeys(r.ingredient.ingredient()), TFCRecipeTypes.LOOM);

    public static final MapCodec<LoomRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        SizedIngredient.FLAT_CODEC.fieldOf("ingredient").forGetter(c -> c.ingredient),
        ItemStackProvider.CODEC.fieldOf("result").forGetter(c -> c.result),
        Codec.INT.fieldOf("steps").forGetter(c -> c.steps),
        ResourceLocation.CODEC.fieldOf("texture").forGetter(c -> c.inProgressTexture)
    ).apply(i, LoomRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LoomRecipe> STREAM_CODEC = StreamCodec.composite(
        SizedIngredient.STREAM_CODEC, c -> c.ingredient,
        ItemStackProvider.STREAM_CODEC, c -> c.result,
        ByteBufCodecs.VAR_INT, c -> c.steps,
        ResourceLocation.STREAM_CODEC, c -> c.inProgressTexture,
        LoomRecipe::new
    );

    @Nullable
    public static LoomRecipe getRecipe(ItemStack stack)
    {
        return RecipeHelpers.getRecipe(CACHE, stack, stack.getItem());
    }

    private final SizedIngredient ingredient;
    private final ItemStackProvider result;
    private final int steps;
    private final ResourceLocation inProgressTexture;

    public LoomRecipe(SizedIngredient ingredient, ItemStackProvider result, int steps, ResourceLocation inProgressTexture)
    {
        this.ingredient = ingredient;
        this.result = result;
        this.steps = steps;
        this.inProgressTexture = inProgressTexture;
    }

    /**
     * @return {@code true} if the recipe matches the {@code input}, without counting the amount
     */
    @Override
    public boolean matches(ItemStack input)
    {
        return ingredient.ingredient().test(input);
    }

    /**
     * @return The output of this recipe with the provided {@code input}
     */
    public ItemStack assemble(ItemStack input)
    {
        return result.getSingleStack(input);
    }

    public SizedIngredient getItemStackIngredient()
    {
        return ingredient;
    }

    public int getInputCount()
    {
        return ingredient.count();
    }

    public ResourceLocation getInProgressTexture()
    {
        return inProgressTexture;
    }

    public int getStepCount()
    {
        return steps;
    }

    @Override
    public ItemStack getResultItem(@Nullable HolderLookup.Provider registries)
    {
        return result.getEmptyStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.LOOM.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.LOOM.get();
    }

    @Override
    public boolean isSpecial()
    {
        return result.dependsOnInput();
    }
}
