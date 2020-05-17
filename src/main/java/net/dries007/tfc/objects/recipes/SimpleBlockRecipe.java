package net.dries007.tfc.objects.recipes;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.util.Helpers;

/**
 * Generic class for single block -> block based in-world crafting recipes.
 */
public abstract class SimpleBlockRecipe implements IBlockRecipe
{
    protected final ResourceLocation id;
    protected final IBlockIngredient ingredient;
    protected final BlockState outputState;

    public SimpleBlockRecipe(ResourceLocation id, IBlockIngredient ingredient, BlockState outputState)
    {
        this.id = id;
        this.ingredient = ingredient;
        this.outputState = outputState;
    }

    @Override
    public boolean matches(World worldIn, BlockPos pos, BlockState state)
    {
        return ingredient.test(state);
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

    public static abstract class Serializer<R extends SimpleBlockRecipe> extends RecipeSerializer<R>
    {
        @Override
        public R read(ResourceLocation recipeId, JsonObject json)
        {
            IBlockIngredient ingredient = IBlockIngredient.Serializer.INSTANCE.read(json.get("ingredient"));
            BlockState state = Helpers.readBlockState(JSONUtils.getString(json, "result"));
            return create(recipeId, ingredient, state);
        }

        @Nullable
        @Override
        public R read(ResourceLocation recipeId, PacketBuffer buffer)
        {
            BlockState state = buffer.readRegistryIdSafe(Block.class).getDefaultState();
            IBlockIngredient ingredient = IBlockIngredient.Serializer.INSTANCE.read(buffer);
            return create(recipeId, ingredient, state);
        }

        @Override
        public void write(PacketBuffer buffer, R recipe)
        {
            buffer.writeRegistryId(recipe.outputState.getBlock());
            IBlockIngredient.Serializer.INSTANCE.write(buffer, recipe.ingredient);
        }

        protected abstract R create(ResourceLocation id, IBlockIngredient ingredient, BlockState state);
    }
}
