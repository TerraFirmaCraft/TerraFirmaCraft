package net.dries007.tfc.common.recipes.ingredients;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.dries007.tfc.util.JsonHelpers;

public class HasTraitIngredient extends DelegateIngredient
{
    private final FoodTrait trait;

    private HasTraitIngredient(Ingredient delegate, FoodTrait trait)
    {
        super(delegate);
        this.trait = trait;
    }

    @Override
    public boolean test(@Nullable ItemStack stack)
    {
        return super.test(stack) && stack != null && stack.getCapability(FoodCapability.CAPABILITY).map(f -> f.getTraits().contains(trait)).orElse(false);
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer()
    {
        return Serializer.INSTANCE;
    }

    public enum Serializer implements IIngredientSerializer<HasTraitIngredient>
    {
        INSTANCE;

        @Override
        public HasTraitIngredient parse(JsonObject json)
        {
            final Ingredient internal = Ingredient.fromJson(JsonHelpers.get(json, "ingredient"));
            final FoodTrait trait = FoodTrait.getTraitOrThrow(new ResourceLocation(JsonHelpers.getAsString(json, "trait")));
            return new HasTraitIngredient(internal, trait);
        }

        @Override
        public HasTraitIngredient parse(FriendlyByteBuf buffer)
        {
            final Ingredient internal = Ingredient.fromNetwork(buffer);
            final FoodTrait trait = FoodTrait.getTraitOrThrow(new ResourceLocation(buffer.readUtf()));
            return new HasTraitIngredient(internal, trait);
        }

        @Override
        public void write(FriendlyByteBuf buffer, HasTraitIngredient ingredient)
        {
            ingredient.delegate.toNetwork(buffer);
            buffer.writeResourceLocation(FoodTrait.getId(ingredient.trait));
        }
    }
}
