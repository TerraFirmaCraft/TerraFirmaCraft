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
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredients;
import net.dries007.tfc.util.Helpers;

public class SpecialBlockTrigger extends SimpleCriterionTrigger<SpecialBlockTrigger.TriggerInstance>
{
    public static void registerSpecialBlockTriggers()
    {
        CriteriaTriggers.register(CHISELED);
        CriteriaTriggers.register(LIT);
        CriteriaTriggers.register(ROCK_ANVIL);
        CriteriaTriggers.register(FIREPIT_CREATED);
        CriteriaTriggers.register(PLANT_KILLED);
    }

    public static final SpecialBlockTrigger CHISELED = new SpecialBlockTrigger(Helpers.identifier("chiseled"));
    public static final SpecialBlockTrigger LIT = new SpecialBlockTrigger(Helpers.identifier("lit"));
    public static final SpecialBlockTrigger ROCK_ANVIL = new SpecialBlockTrigger(Helpers.identifier("rock_anvil"));
    public static final SpecialBlockTrigger FIREPIT_CREATED = new SpecialBlockTrigger(Helpers.identifier("firepit_created"));
    public static final SpecialBlockTrigger PLANT_KILLED = new SpecialBlockTrigger(Helpers.identifier("plant_killed"));

    private final ResourceLocation id;

    public SpecialBlockTrigger(ResourceLocation id)
    {
        this.id = id;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    protected TriggerInstance createInstance(JsonObject json, EntityPredicate.Composite predicate, DeserializationContext context)
    {
        BlockIngredient ingredient = BlockIngredients.fromJson(json);
        return new TriggerInstance(predicate, ingredient);
    }

    public void trigger(ServerPlayer serverPlayer, BlockState state)
    {
        this.trigger(serverPlayer, instance -> instance.ingredient.test(state));
    }

    public class TriggerInstance extends AbstractCriterionTriggerInstance
    {
        private final BlockIngredient ingredient;

        public TriggerInstance(EntityPredicate.Composite predicate, BlockIngredient ingredient)
        {
            super(id, predicate);
            this.ingredient = ingredient;
        }
    }
}
