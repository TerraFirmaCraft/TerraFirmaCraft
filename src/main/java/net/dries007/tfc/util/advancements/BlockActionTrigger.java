/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;

public class BlockActionTrigger extends SimpleCriterionTrigger<BlockActionTrigger.TriggerInstance>
{
    private final ResourceLocation id;

    public BlockActionTrigger(ResourceLocation id)
    {
        this.id = id;
    }

    @Override
    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    protected TriggerInstance createInstance(JsonObject json, ContextAwarePredicate predicate, DeserializationContext context)
    {
        BlockIngredient ingredient = BlockIngredient.fromJson(json);
        return new TriggerInstance(predicate, ingredient);
    }

    public void trigger(ServerPlayer serverPlayer, BlockState state)
    {
        this.trigger(serverPlayer, instance -> instance.ingredient.test(state));
    }

    public class TriggerInstance extends AbstractCriterionTriggerInstance
    {
        private final BlockIngredient ingredient;

        public TriggerInstance(ContextAwarePredicate predicate, BlockIngredient ingredient)
        {
            super(id, predicate);
            this.ingredient = ingredient;
        }
    }
}
