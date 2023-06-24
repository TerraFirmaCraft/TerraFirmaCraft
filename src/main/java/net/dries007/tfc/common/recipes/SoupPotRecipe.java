/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.PotBlockEntity;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodData;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.common.capabilities.food.Nutrient;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.DynamicBowlFood;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.ingredients.FluidStackIngredient;
import net.dries007.tfc.util.Helpers;

public class SoupPotRecipe extends PotRecipe
{
    public static final OutputType OUTPUT_TYPE = nbt -> {
        ItemStack stack = ItemStack.of(nbt.getCompound("item"));
        return new SoupOutput(stack);
    };

    public static final int SOUP_HUNGER_VALUE = 4;
    public static final float SOUP_DECAY_MODIFIER = 3.5F;

    public SoupPotRecipe(ResourceLocation id, List<Ingredient> itemIngredients, FluidStackIngredient fluidIngredient, int duration, float minTemp)
    {
        super(id, itemIngredients, fluidIngredient, duration, minTemp);
    }

    @Override
    public Output getOutput(PotBlockEntity.PotInventory inventory)
    {
        int ingredientCount = 0;
        float water = 20, saturation = 2;
        float[] nutrition = new float[Nutrient.TOTAL];
        ItemStack soupStack = ItemStack.EMPTY;
        final List<ItemStack> itemIngredients = new ArrayList<>();
        for (int i = PotBlockEntity.SLOT_EXTRA_INPUT_START; i <= PotBlockEntity.SLOT_EXTRA_INPUT_END; i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            IFood food = stack.getCapability(FoodCapability.CAPABILITY).resolve().orElse(null);
            if (food != null)
            {
                itemIngredients.add(stack);
                if (food.isRotten()) // this should mostly not happen since the ingredients are not rotten to start, but worth checking
                {
                    ingredientCount = 0;
                    break;
                }
                final FoodData data = food.getData();
                water += data.water();
                saturation += data.saturation();
                for (Nutrient nutrient : Nutrient.VALUES)
                {
                    nutrition[nutrient.ordinal()] += data.nutrient(nutrient);
                }
                ingredientCount++;
            }
        }
        if (ingredientCount > 0)
        {
            float multiplier = 1 - (0.05f * ingredientCount); // per-serving multiplier of nutrition
            water *= multiplier; saturation *= multiplier;
            Nutrient maxNutrient = Nutrient.GRAIN; // determines what item you get. this is a default
            float maxNutrientValue = 0;
            for (Nutrient nutrient : Nutrient.VALUES)
            {
                final int idx = nutrient.ordinal();
                nutrition[idx] *= multiplier;
                if (nutrition[idx] > maxNutrientValue)
                {
                    maxNutrientValue = nutrition[idx];
                    maxNutrient = nutrient;
                }
            }
            FoodData data = FoodData.create(SOUP_HUNGER_VALUE, water, saturation, nutrition, SOUP_DECAY_MODIFIER);
            int servings = (int) (ingredientCount / 2f) + 1;
            long created = FoodCapability.getRoundedCreationDate();

            soupStack = new ItemStack(TFCItems.SOUPS.get(maxNutrient).get(), servings);
            final IFood food = soupStack.getCapability(FoodCapability.CAPABILITY).resolve().orElse(null);
            if (food instanceof DynamicBowlFood.DynamicBowlHandler handler)
            {
                handler.setCreationDate(created);
                handler.setIngredients(itemIngredients);
                handler.setFood(data);
            }
        }

        return new SoupOutput(soupStack);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.POT_SOUP.get();
    }

    public record SoupOutput(ItemStack stack) implements Output
    {
        @Override
        public boolean isEmpty()
        {
            return stack.isEmpty();
        }

        @Override
        public InteractionResult onInteract(PotBlockEntity entity, Player player, ItemStack clickedWith)
        {
            if (Helpers.isItem(clickedWith.getItem(), TFCTags.Items.SOUP_BOWLS) && !stack.isEmpty())
            {
                // set the internal bowl to the one we clicked with
                stack.getCapability(FoodCapability.CAPABILITY)
                    .filter(food -> food instanceof DynamicBowlFood.DynamicBowlHandler)
                    .ifPresent(food -> ((DynamicBowlFood.DynamicBowlHandler) food).setBowl(clickedWith));

                // take the player's bowl, give a soup
                clickedWith.shrink(1);
                ItemHandlerHelper.giveItemToPlayer(player, stack.split(1));
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        @Override
        public int getFluidColor()
        {
            return TFCFluids.ALPHA_MASK | 0xA64214;
        }

        @Override
        public void write(CompoundTag nbt)
        {
            nbt.put("item", stack.save(new CompoundTag()));
        }

        @Override
        public OutputType getType()
        {
            return SoupPotRecipe.OUTPUT_TYPE;
        }
    }

    public static class Serializer extends PotRecipe.Serializer<SoupPotRecipe>
    {
        @Override
        protected SoupPotRecipe fromJson(ResourceLocation recipeId, JsonObject json, List<Ingredient> ingredients, FluidStackIngredient fluidIngredient, int duration, float minTemp)
        {
            return new SoupPotRecipe(recipeId, ingredients, fluidIngredient, duration, minTemp);
        }

        @Override
        protected SoupPotRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer, List<Ingredient> ingredients, FluidStackIngredient fluidIngredient, int duration, float minTemp)
        {
            return new SoupPotRecipe(recipeId, ingredients, fluidIngredient, duration, minTemp);
        }
    }
}
