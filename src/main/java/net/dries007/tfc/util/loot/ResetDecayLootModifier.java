/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.loot;

import java.util.List;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import org.jetbrains.annotations.NotNull;

public class ResetDecayLootModifier extends LootModifier
{
    protected ResetDecayLootModifier(LootItemCondition[] conditions)
    {
        super(conditions);
    }

    @NotNull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> loot, LootContext context)
    {
        if (!context.hasParam(TFCLoot.DECAY_HANDLED))
        {
            loot.forEach(FoodCapability::updateFoodDecayOnCreate);
        }
        return loot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<ResetDecayLootModifier>
    {
        @Override
        public ResetDecayLootModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] conditions)
        {
            return new ResetDecayLootModifier(conditions);
        }

        @Override
        public JsonObject write(ResetDecayLootModifier instance)
        {
            return makeConditions(instance.conditions);
        }
    }

}
