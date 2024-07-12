/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.Optional;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.container.KnappingContainer;
import net.dries007.tfc.util.data.DataManager;
import net.dries007.tfc.util.data.KnappingPattern;
import net.dries007.tfc.util.data.KnappingType;

public class KnappingRecipe implements INoopInputRecipe, IRecipePredicate<KnappingContainer>
{
    public static final MapCodec<KnappingRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        KnappingType.MANAGER.byIdReferenceCodec().fieldOf("knapping_type").forGetter(c -> c.knappingType),
        KnappingPattern.CODEC.fieldOf("pattern").forGetter(c -> c.pattern),
        Ingredient.CODEC.optionalFieldOf("ingredient").forGetter(c -> c.ingredient),
        ItemStack.CODEC.fieldOf("result").forGetter(c -> c.result)
    ).apply(i, KnappingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, KnappingRecipe> STREAM_CODEC = StreamCodec.composite(
        KnappingType.MANAGER.byIdStreamCodec(), c -> c.knappingType,
        KnappingPattern.STREAM_CODEC, c -> c.pattern,
        ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC), c -> c.ingredient,
        ItemStack.STREAM_CODEC, c -> c.result,
        KnappingRecipe::new
    );

    @Nullable
    public static KnappingRecipe get(Level level, KnappingContainer input)
    {
        return RecipeHelpers.unbox(RecipeHelpers.getHolder(level, TFCRecipeTypes.KNAPPING, input));
    }

    private final DataManager.Reference<KnappingType> knappingType;
    private final KnappingPattern pattern;
    private final Optional<Ingredient> ingredient;
    private final ItemStack result;

    public KnappingRecipe(DataManager.Reference<KnappingType> knappingType, KnappingPattern pattern, Optional<Ingredient> ingredient, ItemStack result)
    {
        this.knappingType = knappingType;
        this.pattern = pattern;
        this.ingredient = ingredient;
        this.result = result;
    }

    @Override
    public boolean matches(KnappingContainer input)
    {
        return input.getKnappingType() == knappingType.get()
            && input.getPattern().matches(getPattern())
            && matchesItem(input.getOriginalStack());
    }

    public boolean matchesItem(ItemStack stack)
    {
        return ingredient.isEmpty() || ingredient.get().test(stack);
    }

    public ItemStack assemble()
    {
        return result.copy();
    }

    public KnappingType getKnappingType()
    {
        return knappingType.get();
    }

    public KnappingPattern getPattern()
    {
        return pattern;
    }

    @Nullable
    public Ingredient getIngredient()
    {
        return ingredient.orElse(null);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries)
    {
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.KNAPPING.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.KNAPPING.get();
    }
}
