/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.common.blocks.crop.DecayingBlock;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodHandler;

public class CopyDecayLootFunction extends LootItemConditionalFunction
{
    protected CopyDecayLootFunction(LootItemCondition[] conditions)
    {
        super(conditions);
    }

    @Override
    public LootItemFunctionType getType()
    {
        return TFCLoot.COPY_DECAY.get();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context)
    {
        if (context.getParamOrNull(LootContextParams.BLOCK_ENTITY) instanceof TickCounterBlockEntity counter)
        {
            BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
            if (state != null && state.getBlock() instanceof DecayingBlock)
            {
                // ignores modifiers
                stack.getCapability(FoodCapability.CAPABILITY).ifPresent(food -> food.setCreationDate(counter.getLastUpdateTick() - FoodHandler.DEFAULT_DECAY_TICKS));
            }
        }
        return stack;
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<CopyDecayLootFunction>
    {
        @Override
        public CopyDecayLootFunction deserialize(JsonObject json, JsonDeserializationContext context, LootItemCondition[] conditions)
        {
            return new CopyDecayLootFunction(conditions);
        }
    }
}
