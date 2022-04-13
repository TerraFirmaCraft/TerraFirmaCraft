/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredients;
import net.dries007.tfc.util.JsonHelpers;

/**
 * Generic class for single block -> block based in-world crafting recipes.
 */
public abstract class SimpleBlockRecipe implements IBlockRecipe
{
    protected final ResourceLocation id;
    protected final BlockIngredient ingredient;
    protected final BlockState outputState;
    protected final boolean copyInputState;

    public SimpleBlockRecipe(ResourceLocation id, BlockIngredient ingredient, BlockState outputState, boolean copyInputState)
    {
        this.id = id;
        this.ingredient = ingredient;
        this.outputState = outputState;
        this.copyInputState = copyInputState;
    }

    @Override
    public boolean matches(BlockState state)
    {
        return ingredient.test(state);
    }

    @Override
    public BlockState getBlockCraftingResult(BlockState state)
    {
        return copyInputState ? state : outputState;
    }

    @Override
    public Block getBlockRecipeOutput()
    {
        return outputState.getBlock();
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    public BlockIngredient getBlockIngredient()
    {
        return ingredient;
    }

    public static class Serializer<R extends SimpleBlockRecipe> extends RecipeSerializerImpl<R>
    {
        private final Factory<R> factory;

        public Serializer(Factory<R> factory)
        {
            this.factory = factory;
        }

        @Override
        public R fromJson(ResourceLocation recipeId, JsonObject json)
        {
            BlockIngredient ingredient = BlockIngredients.fromJson(JsonHelpers.get(json, "ingredient"));
            boolean copyInputState = GsonHelper.getAsBoolean(json, "copy_input", false);
            BlockState state;
            if (!copyInputState)
            {
                state = JsonHelpers.getBlockState(GsonHelper.getAsString(json, "result"));
            }
            else
            {
                state = Blocks.AIR.defaultBlockState();
            }
            return factory.create(recipeId, ingredient, state, copyInputState);
        }

        @Nullable
        @Override
        public R fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            final BlockIngredient ingredient = BlockIngredients.fromNetwork(buffer);
            final boolean copyInputState = buffer.readBoolean();
            final BlockState state = copyInputState ?
                Blocks.AIR.defaultBlockState() :
                buffer.readRegistryIdUnsafe(ForgeRegistries.BLOCKS).defaultBlockState();
            return factory.create(recipeId, ingredient, state, copyInputState);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, R recipe)
        {
            recipe.ingredient.toNetwork(buffer);
            buffer.writeBoolean(recipe.copyInputState);
            if (!recipe.copyInputState)
            {
                buffer.writeRegistryIdUnsafe(ForgeRegistries.BLOCKS, recipe.outputState.getBlock());
            }
        }

        public interface Factory<R extends SimpleBlockRecipe>
        {
            R create(ResourceLocation id, BlockIngredient ingredient, BlockState state, boolean copyInputState);
        }
    }
}