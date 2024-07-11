/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.List;
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

import net.dries007.tfc.common.capabilities.glass.GlassOperation;
import net.dries007.tfc.common.capabilities.glass.GlassWorkData;
import net.dries007.tfc.util.Helpers;

public record GlassworkingRecipe(
    List<GlassOperation> operations,
    Ingredient batchItem,
    ItemStack resultItem
) implements INoopInputRecipe
{
    public static final MapCodec<GlassworkingRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        GlassOperation.CODEC.listOf().fieldOf("operations").forGetter(c -> c.operations),
        Ingredient.CODEC.fieldOf("batch").forGetter(c -> c.batchItem),
        ItemStack.CODEC.fieldOf("result").forGetter(c -> c.resultItem)
    ).apply(i, GlassworkingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GlassworkingRecipe> STREAM_CODEC = StreamCodec.composite(
        GlassOperation.STREAM_CODEC.apply(ByteBufCodecs.list()), c -> c.operations,
        Ingredient.CONTENTS_STREAM_CODEC, c -> c.batchItem,
        ItemStack.STREAM_CODEC, c -> c.resultItem,
        GlassworkingRecipe::new
    );

    @Nullable
    public static GlassworkingRecipe get(Level level, ItemStack stack)
    {
        for (GlassworkingRecipe recipe : Helpers.getRecipes(level, TFCRecipeTypes.GLASSWORKING).values())
        {
            if (recipe.matches(stack))
            {
                return recipe;
            }
        }
        return null;
    }

    /**
     * @return {@code true} if the recipe matches the input stack
     */
    public boolean matches(ItemStack input)
    {
        final GlassWorkData data = GlassWorkData.get(input);
        if (data != null)
        {
            final List<GlassOperation> steps = data.getOperations().getSteps();
            return steps.equals(operations) && batchItem.test(data.getBatch());
        }
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries)
    {
        return resultItem;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.GLASSWORKING.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return TFCRecipeTypes.GLASSWORKING.get();
    }
}
