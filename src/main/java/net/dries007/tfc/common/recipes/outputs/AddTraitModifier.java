package net.dries007.tfc.common.recipes.outputs;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.dries007.tfc.util.JsonHelpers;

public record AddTraitModifier(FoodTrait trait) implements ItemStackModifier
{
    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        return FoodCapability.applyTrait(stack, trait);
    }

    @Override
    public Serializer serializer()
    {
        return Serializer.INSTANCE;
    }

    public enum Serializer implements ItemStackModifier.Serializer<AddTraitModifier>
    {
        INSTANCE;

        @Override
        public AddTraitModifier fromJson(JsonObject json)
        {
            final FoodTrait trait = FoodTrait.getTrait(JsonHelpers.getAsString(json, "trait"));
            return new AddTraitModifier(trait);
        }

        @Override
        public AddTraitModifier fromNetwork(FriendlyByteBuf buffer)
        {
            final FoodTrait trait = FoodTrait.getTrait(buffer.readUtf());
            return new AddTraitModifier(trait);
        }

        @Override
        public void toNetwork(AddTraitModifier modifier, FriendlyByteBuf buffer)
        {
            buffer.writeUtf(modifier.trait.getName());
        }
    }
}
