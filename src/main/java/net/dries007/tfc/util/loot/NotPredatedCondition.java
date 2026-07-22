/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.loot;

import java.util.Set;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import org.jetbrains.annotations.Contract;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

public enum NotPredatedCondition implements LootItemCondition
{
    INSTANCE;

    @Override
    public LootItemConditionType getType()
    {
        return TFCLoot.NOT_PREDATED.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams()
    {
        return Set.of(LootContextParams.ATTACKING_ENTITY);
    }

    @Override
    public boolean test(LootContext context)
    {
        if (!context.hasParam(LootContextParams.ATTACKING_ENTITY))
        {
            return true;
        }
        final Entity killer = context.getParam(LootContextParams.ATTACKING_ENTITY);
        return killer instanceof Player || (!Helpers.isEntity(killer, TFCTags.Entities.LAND_PREDATORS) && !Helpers.isEntity(killer, TFCTags.Entities.OCEAN_PREDATORS));
    }

    @Contract(pure = true)
    public static LootItemCondition.Builder notPredated()
    {
        return () -> INSTANCE;
    }
}
