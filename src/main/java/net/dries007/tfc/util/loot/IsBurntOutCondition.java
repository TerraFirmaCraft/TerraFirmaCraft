package net.dries007.tfc.util.loot;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public enum IsBurntOutCondition implements LootItemCondition
{
    INSTANCE;

    @Override
    public LootItemConditionType getType()
    {
        return TFCLoot.IS_BURNT_OUT.get();
    }

    @Override
    public boolean test(LootContext context)
    {
        return context.hasParam(TFCLoot.BURNT_OUT);
    }
}
