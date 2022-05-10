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

import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.NotNull;

public class ResetDecayModifier extends LootModifier
{
    protected ResetDecayModifier(LootItemCondition[] conditionsIn)
    {
        super(conditionsIn);
    }

    @NotNull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> loot, LootContext context)
    {
        loot.forEach(Helpers::resetCreationDate);
        return loot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<ResetDecayModifier>
    {
        @Override
        public ResetDecayModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] conditions)
        {
            return new ResetDecayModifier(conditions);
        }

        @Override
        public JsonObject write(ResetDecayModifier instance)
        {
            return makeConditions(instance.conditions);
        }
    }

}
