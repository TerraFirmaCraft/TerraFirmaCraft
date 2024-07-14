/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.advancements;

import java.util.Optional;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class EntityActionTrigger extends SimpleCriterionTrigger<EntityActionTrigger.TriggerInstance>
{
    public static final Codec<EntityActionTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(i -> i.group(
        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(c -> c.player),
        EntityPredicate.CODEC.fieldOf("entity").forGetter(c -> c.entity)
    ).apply(i, TriggerInstance::new));

    @Override
    public Codec<TriggerInstance> codec()
    {
        return CODEC;
    }

    public void trigger(ServerPlayer serverPlayer, Entity entity)
    {
        trigger(serverPlayer, instance -> instance.entity.matches(serverPlayer, entity));
    }

    record TriggerInstance(
        Optional<ContextAwarePredicate> player,
        EntityPredicate entity
    ) implements SimpleInstance
    {}
}
