/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.util.Helpers;

/**
 * Generic class for single block -> block based in-world crafting recipes.
 */
public abstract class SimpleBlockRecipe implements IBlockRecipe
{
    protected final ResourceLocation id;
    protected final IBlockIngredient ingredient;
    protected final BlockState outputState;
    protected final boolean copyInputState;

    public SimpleBlockRecipe(ResourceLocation id, IBlockIngredient ingredient, BlockState outputState, boolean copyInputState)
    {
        this.id = id;
        this.ingredient = ingredient;
        this.outputState = outputState;
        this.copyInputState = copyInputState;
    }

    @Override
    public boolean matches(Level worldIn, BlockPos pos, BlockState state)
    {
        return ingredient.test(state);
    }

    @Override
    public BlockState getBlockCraftingResult(BlockRecipeWrapper wrapper)
    {
        return copyInputState ? wrapper.getState() : outputState;
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

    public IBlockIngredient getBlockIngredient()
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
            IBlockIngredient ingredient = IBlockIngredient.Serializer.INSTANCE.read(json.get("ingredient"));
            boolean copyInputState = GsonHelper.getAsBoolean(json, "copy_input", false);
            BlockState state;
            if (!copyInputState)
            {
                state = Helpers.readBlockState(GsonHelper.getAsString(json, "result"));
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
            IBlockIngredient ingredient = IBlockIngredient.Serializer.INSTANCE.read(buffer);
            boolean copyInputState = buffer.readBoolean();
            BlockState state;
            if (!copyInputState)
            {
                state = buffer.readRegistryIdUnsafe(ForgeRegistries.BLOCKS).defaultBlockState();
            }
            else
            {
                state = Blocks.AIR.defaultBlockState();
            }
            return factory.create(recipeId, ingredient, state, copyInputState);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, R recipe)
        {
            IBlockIngredient.Serializer.INSTANCE.write(buffer, recipe.ingredient);
            buffer.writeBoolean(recipe.copyInputState);
            if (!recipe.copyInputState)
            {
                buffer.writeRegistryIdUnsafe(ForgeRegistries.BLOCKS, recipe.outputState.getBlock());
            }
        }

        protected interface Factory<R extends SimpleBlockRecipe>
        {
            R create(ResourceLocation id, IBlockIngredient ingredient, BlockState state, boolean copyInputState);
        }
    }
}