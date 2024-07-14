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
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;

public class BlockActionTrigger extends SimpleCriterionTrigger<BlockActionTrigger.TriggerInstance>
{
    public static final Codec<BlockActionTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(i -> i.group(
        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(c -> c.player),
        BlockIngredient.CODEC.fieldOf("ingredient").forGetter(c -> c.ingredient)
    ).apply(i, TriggerInstance::new));

    @Override
    public Codec<TriggerInstance> codec()
    {
        return CODEC;
    }

    public void trigger(ServerPlayer serverPlayer, BlockState state)
    {
        this.trigger(serverPlayer, instance -> instance.ingredient.test(state));
    }

    public record TriggerInstance(
        Optional<ContextAwarePredicate> player,
        BlockIngredient ingredient
    ) implements SimpleInstance
    {}
}
