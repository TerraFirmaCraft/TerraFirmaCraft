/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.List;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.blockentities.PotBlockEntity;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import net.dries007.tfc.util.JsonHelpers;

public class JamPotRecipe extends PotRecipe
{
    public static final OutputType OUTPUT_TYPE = nbt -> {
        ItemStack stack = ItemStack.of(nbt.getCompound("item"));
        ResourceLocation texture = new ResourceLocation(nbt.getString("texture"));
        return new JamPotRecipe.JamOutput(stack, texture);
    };

    private final ItemStack jarredStack;
    private final ResourceLocation texture;

    public JamPotRecipe(ResourceLocation id, List<Ingredient> itemIngredients, FluidStackIngredient fluidIngredient, int duration, float minTemp, ItemStack jarredStack, ResourceLocation texture)
    {
        super(id, itemIngredients, fluidIngredient, duration, minTemp);
        this.jarredStack = jarredStack;
        this.texture = texture;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access)
    {
        return jarredStack;
    }

    public ResourceLocation getTexture()
    {
        return texture;
    }

    @Override
    public Output getOutput(PotBlockEntity.PotInventory inventory)
    {
        return new JamOutput(jarredStack.copy(), texture);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.POT_JAM.get();
    }

    public record JamOutput(ItemStack stack, ResourceLocation texture) implements Output
    {
        @Override
        public boolean isEmpty()
        {
            return stack.isEmpty();
        }

        @Override
        public InteractionResult onInteract(PotBlockEntity entity, Player player, ItemStack clickedWith)
        {
            if (clickedWith.getItem() == TFCItems.EMPTY_JAR_WITH_LID.get() && !stack.isEmpty())
            {
                // take the player's empty jar
                clickedWith.shrink(1);
                ItemHandlerHelper.giveItemToPlayer(player, stack.split(1));
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        @Override
        public ResourceLocation getRenderTexture()
        {
            return texture;
        }

        @Override
        public float getFluidYLevel()
        {
            return Mth.clampedMap(stack.getCount(), 0, 4, 7f / 16, 10f / 16);
        }

        @Override
        public void write(CompoundTag nbt)
        {
            nbt.put("item", stack.save(new CompoundTag()));
            nbt.putString("texture", texture.toString());
        }

        @Override
        public OutputType getType()
        {
            return JamPotRecipe.OUTPUT_TYPE;
        }
    }

    public static class Serializer extends PotRecipe.Serializer<JamPotRecipe>
    {
        @Override
        protected JamPotRecipe fromJson(ResourceLocation recipeId, JsonObject json, List<Ingredient> ingredients, FluidStackIngredient fluidIngredient, int duration, float minTemp)
        {
            return new JamPotRecipe(recipeId, ingredients, fluidIngredient, duration, minTemp, JsonHelpers.getItemStack(json, "result"), new ResourceLocation(JsonHelpers.getAsString(json, "texture")));
        }

        @Override
        protected JamPotRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer, List<Ingredient> ingredients, FluidStackIngredient fluidIngredient, int duration, float minTemp)
        {
            final ItemStack stack = buffer.readItem();
            final ResourceLocation texture = buffer.readResourceLocation();
            return new JamPotRecipe(recipeId, ingredients, fluidIngredient, duration, minTemp, stack, texture);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, JamPotRecipe recipe)
        {
            super.toNetwork(buffer, recipe);
            buffer.writeItem(recipe.jarredStack);
            buffer.writeResourceLocation(recipe.texture);
        }
    }
}
