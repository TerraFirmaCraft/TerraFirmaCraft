/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class GenericTrigger extends SimpleCriterionTrigger<GenericTrigger.TriggerInstance>
{
    private final ResourceLocation id;

    public GenericTrigger(ResourceLocation id)
    {
        this.id = id;
    }

    public void trigger(ServerPlayer player)
    {
        this.trigger(player, instance -> true);
    }

    @Override
    protected GenericTrigger.TriggerInstance createInstance(JsonObject json, EntityPredicate.Composite predicate, DeserializationContext context)
    {
        return new GenericTrigger.TriggerInstance(predicate);
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    public class TriggerInstance extends AbstractCriterionTriggerInstance
    {
        public TriggerInstance(EntityPredicate.Composite predicate)
        {
            super(id, predicate);
        }
    }
}
