/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.events;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import net.dries007.tfc.util.Helpers;

public class SpecialEntityTrigger extends SimpleCriterionTrigger<SpecialEntityTrigger.TriggerInstance>
{
    public static void registerSpecialEntityTriggers()
    {
        CriteriaTriggers.register(HOOKED_ENTITY);
        CriteriaTriggers.register(FED_ANIMAL);
        CriteriaTriggers.register(STAB_ENTITY);
    }

    public static final SpecialEntityTrigger HOOKED_ENTITY = new SpecialEntityTrigger(Helpers.identifier("hooked_entity"));
    public static final SpecialEntityTrigger FED_ANIMAL = new SpecialEntityTrigger(Helpers.identifier("fed_animal"));
    public static final SpecialEntityTrigger STAB_ENTITY = new SpecialEntityTrigger(Helpers.identifier("stab_entity"));

    private final ResourceLocation id;

    public SpecialEntityTrigger(ResourceLocation id)
    {
        this.id = id;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    protected SpecialEntityTrigger.TriggerInstance createInstance(JsonObject json, EntityPredicate.Composite predicate, DeserializationContext context)
    {
        EntityPredicate ingredient = EntityPredicate.fromJson(json.get("entity"));
        return new SpecialEntityTrigger.TriggerInstance(predicate, ingredient);
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
