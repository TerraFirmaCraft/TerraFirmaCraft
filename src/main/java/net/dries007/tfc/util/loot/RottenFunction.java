package net.dries007.tfc.util.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import net.dries007.tfc.common.capabilities.food.FoodCapability;

public class RottenFunction extends LootItemConditionalFunction
{
    protected RottenFunction(LootItemCondition[] conditions)
    {
        super(conditions);
    }

    @Override
    public LootItemFunctionType getType()
    {
        return TFCLoot.ROTTEN.get();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context)
    {
        return FoodCapability.setRotten(stack);
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<RottenFunction>
    {
        @Override
        public RottenFunction deserialize(JsonObject json, JsonDeserializationContext context, LootItemCondition[] conditions)
        {
            return new RottenFunction(conditions);
        }
    }
}
