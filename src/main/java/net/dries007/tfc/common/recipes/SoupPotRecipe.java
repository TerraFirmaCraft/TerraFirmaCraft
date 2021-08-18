/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.List;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.recipes.ingredients.FluidIngredient;
import net.dries007.tfc.common.tileentity.PotTileEntity;

public class SoupPotRecipe extends PotRecipe
{
    protected SoupPotRecipe(ResourceLocation id, List<Ingredient> itemIngredients, FluidIngredient fluidIngredient, int duration, float minTemp)
    {
        super(id, itemIngredients, fluidIngredient, duration, minTemp);
    }

    @Override
    public Output getOutput(PotTileEntity.PotInventory inventory)
    {
        return new SoupOutput(3); // todo: calculate soup output
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.POT_SOUP.get();
    }

    static class SoupOutput implements Output
    {
        private int servings;

        SoupOutput(int servings)
        {
            this.servings = servings;
        }

        @Override
        public boolean isEmpty()
        {
            return servings == 0;
        }

        @Override
        public boolean renderDefaultFluid()
        {
            return true;
        }

        @Override
        public InteractionResult onInteract(PotTileEntity entity, Player player, ItemStack clickedWith)
        {
            if (clickedWith.getItem() == Items.BOWL) // todo: proper soup item and output
            {
                servings--;
                clickedWith.shrink(1);
                ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.BEETROOT_SOUP));
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        @Override
        public CompoundTag serializeNBT()
        {
            CompoundTag nbt = new CompoundTag();
            nbt.putInt("servings", servings);
            return nbt;
        }

        @Override
        public void deserializeNBT(@Nullable CompoundTag nbt)
        {
            if (nbt != null)
            {
                servings = nbt.getInt("servings");
            }
        }
    }

    public static class Serializer extends PotRecipe.Serializer<SoupPotRecipe>
    {
        @Override
        protected SoupPotRecipe fromJson(ResourceLocation recipeId, JsonObject json, List<Ingredient> ingredients, FluidIngredient fluidIngredient, int duration, float minTemp)
        {
            return new SoupPotRecipe(recipeId, ingredients, fluidIngredient, duration, minTemp);
        }

        @Override
        protected SoupPotRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer, List<Ingredient> ingredients, FluidIngredient fluidIngredient, int duration, float minTemp)
        {
            return new SoupPotRecipe(recipeId, ingredients, fluidIngredient, duration, minTemp);
        }
    }
}
