/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.loot;

import java.util.List;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import net.dries007.tfc.common.component.food.FoodCapability;

public class RottenFunction extends LootItemConditionalFunction
{
    public static final MapCodec<RottenFunction> CODEC = RecordCodecBuilder.mapCodec(i -> commonFields(i).apply(i, RottenFunction::new));

    public RottenFunction(List<LootItemCondition> conditions)
    {
        super(conditions);
    }

    @Override
    public LootItemFunctionType<RottenFunction> getType()
    {
        return TFCLoot.ROTTEN.get();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context)
    {
        return FoodCapability.setRotten(stack);
    }
}
