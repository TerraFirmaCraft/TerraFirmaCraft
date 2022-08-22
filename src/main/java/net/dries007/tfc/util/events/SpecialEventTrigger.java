/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.events;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import net.dries007.tfc.util.Helpers;

public class SpecialEventTrigger extends SimpleCriterionTrigger<SpecialEventTrigger.TriggerInstance>
{
    public static void registerSpecialEventTriggers()
    {
        CriteriaTriggers.register(FULL_POWDERKEG);
        CriteriaTriggers.register(FULL_FERTILIZER);
        CriteriaTriggers.register(LAVA_LAMP);
        CriteriaTriggers.register(ROTTEN_COMPOST_KILL);
        SpecialEntityTrigger.registerSpecialEntityTriggers();
        SpecialBlockTrigger.registerSpecialBlockTriggers();
    }

    public static final SpecialEventTrigger FULL_POWDERKEG = new SpecialEventTrigger(Helpers.identifier("full_powderkeg"));
    public static final SpecialEventTrigger FULL_FERTILIZER = new SpecialEventTrigger(Helpers.identifier("full_fertilizer"));
    public static final SpecialEventTrigger LAVA_LAMP = new SpecialEventTrigger(Helpers.identifier("lava_lamp"));
    public static final SpecialEventTrigger ROTTEN_COMPOST_KILL = new SpecialEventTrigger(Helpers.identifier("rotten_compost_kill"));

    private final ResourceLocation id;

    public SpecialEventTrigger(ResourceLocation id)
    {
        this.id = id;
    }

    public void trigger(ServerPlayer player)
    {
        this.trigger(player, instance -> true);
    }

    @Override
    protected SpecialEventTrigger.TriggerInstance createInstance(JsonObject json, EntityPredicate.Composite predicate, DeserializationContext context)
    {
        return new SpecialEventTrigger.TriggerInstance(predicate);
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
