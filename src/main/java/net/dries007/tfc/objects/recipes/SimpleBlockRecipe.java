package net.dries007.tfc.objects.recipes;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
    protected final boolean copyInputState;

    public SimpleBlockRecipe(ResourceLocation id, IBlockIngredient ingredient, BlockState outputState, boolean copyInputState)
    {
        this.id = id;
        this.ingredient = ingredient;
        this.outputState = outputState;
        this.copyInputState = copyInputState;
    }

    @Override
    public boolean matches(World worldIn, BlockPos pos, BlockState state)
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

    public static abstract class Serializer<R extends SimpleBlockRecipe> extends RecipeSerializer<R>
    {
        @Override
        public R read(ResourceLocation recipeId, JsonObject json)
        {
            IBlockIngredient ingredient = IBlockIngredient.Serializer.INSTANCE.read(json.get("ingredient"));
            boolean copyInputState = JSONUtils.getBoolean(json, "copy_input", false);
            BlockState state;
            if (!copyInputState)
            {
                state = Helpers.readBlockState(JSONUtils.getString(json, "result"));
            }
            else
            {
                state = Blocks.AIR.getDefaultState();
            }
            return create(recipeId, ingredient, state, copyInputState);
        }

        @Nullable
        @Override
        public R read(ResourceLocation recipeId, PacketBuffer buffer)
        {
            IBlockIngredient ingredient = IBlockIngredient.Serializer.INSTANCE.read(buffer);
            boolean copyInputState = buffer.readBoolean();
            BlockState state;
            if (!copyInputState)
            {
                state = buffer.readRegistryIdSafe(Block.class).getDefaultState();
            }
            else
            {
                state = Blocks.AIR.getDefaultState();
            }
            return create(recipeId, ingredient, state, copyInputState);
        }

        @Override
        public void write(PacketBuffer buffer, R recipe)
        {
            IBlockIngredient.Serializer.INSTANCE.write(buffer, recipe.ingredient);
            buffer.writeBoolean(recipe.copyInputState);
            buffer.writeRegistryId(recipe.outputState.getBlock());
        }

        protected abstract R create(ResourceLocation id, IBlockIngredient ingredient, BlockState state, boolean copyInputState);
    }
}
