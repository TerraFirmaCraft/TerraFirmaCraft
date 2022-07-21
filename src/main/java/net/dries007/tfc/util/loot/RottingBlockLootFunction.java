/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import net.dries007.tfc.common.blockentities.RottingBlockEntity;
import net.dries007.tfc.common.capabilities.food.FoodCapability;

public class RottingBlockLootFunction extends LootItemConditionalFunction
{
    protected RottingBlockLootFunction(LootItemCondition[] conditions)
    {
        super(conditions);
    }

    @Override
    public LootItemFunctionType getType()
    {
        return TFCLoot.ROTTING_BLOCK.get();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context)
    {
        if (context.getParamOrNull(LootContextParams.BLOCK_ENTITY) instanceof RottingBlockEntity rotting)
        {
            stack.getCapability(FoodCapability.CAPABILITY).ifPresent(food -> food.setCreationDate(rotting.getRottenTick()));
        }
        return stack;
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<RottingBlockLootFunction>
    {
        @Override
        public RottingBlockLootFunction deserialize(JsonObject json, JsonDeserializationContext context, LootItemCondition[] conditions)
        {
            return new RottingBlockLootFunction(conditions);
        }
    }
}
