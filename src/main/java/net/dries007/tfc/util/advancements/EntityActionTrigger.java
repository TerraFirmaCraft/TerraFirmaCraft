/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.advancements;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class EntityActionTrigger extends SimpleCriterionTrigger<EntityActionTrigger.TriggerInstance>
{
    private final ResourceLocation id;

    public EntityActionTrigger(ResourceLocation id)
    {
        this.id = id;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    protected EntityActionTrigger.TriggerInstance createInstance(JsonObject json, EntityPredicate.Composite predicate, DeserializationContext context)
    {
        EntityPredicate ingredient = EntityPredicate.fromJson(json.get("entity"));
        if (ingredient == EntityPredicate.ANY)
        {
            throw new JsonSyntaxException("Entity predicate " + id + " matches every entity. This probably means it failed to load.");
        }
        return new EntityActionTrigger.TriggerInstance(predicate, ingredient);
    }

    public void trigger(ServerPlayer serverPlayer, Entity entity)
    {
        this.trigger(serverPlayer, instance -> instance.predicate.matches(serverPlayer, entity));
    }

    public class TriggerInstance extends AbstractCriterionTriggerInstance
    {
        private final EntityPredicate predicate;

        public TriggerInstance(EntityPredicate.Composite predicate, EntityPredicate ingredient)
        {
            super(id, predicate);
            this.predicate = ingredient;
        }
    }
}
