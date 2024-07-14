/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.advancements;

import java.util.Optional;
import com.mojang.serialization.Codec;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

public class GenericTrigger extends SimpleCriterionTrigger<GenericTrigger.TriggerInstance>
{
    public static final Codec<GenericTrigger.TriggerInstance> CODEC = EntityPredicate.ADVANCEMENT_CODEC
        .optionalFieldOf("player")
        .xmap(TriggerInstance::new, TriggerInstance::player)
        .codec();

    @Override
    public Codec<TriggerInstance> codec()
    {
        return CODEC;
    }

    public void trigger(ServerPlayer player)
    {
        trigger(player, instance -> true);
    }

    record TriggerInstance(Optional<ContextAwarePredicate> player) implements SimpleInstance {}
}
