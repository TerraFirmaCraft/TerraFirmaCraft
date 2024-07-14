/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.advancements;

import java.util.function.BiConsumer;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.TerraFirmaCraft;

public class TFCAdvancements
{
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, TerraFirmaCraft.MOD_ID);

    public static final Id FULL_POWDERKEG = registerGeneric("full_powderkeg");
    public static final Id FULL_FERTILIZER = registerGeneric("full_fertilizer");
    public static final Id LAVA_LAMP = registerGeneric("lava_lamp");
    public static final Id ROTTEN_COMPOST_KILL = registerGeneric("rotten_compost_kill");
    public static final Id PRESENT_DAY = registerGeneric("present_day");
    public static final Id EAT_ROTTEN_FOOD = registerGeneric("eat_rotten_food");
    public static final Id PERFECTLY_FORGED = registerGeneric("perfectly_forged");
    public static final Id FULL_NUTRITION = registerGeneric("full_nutrition");
    public static final Id MAX_WINDMILL = registerGeneric("max_windmill");
    public static final Id BASIN_POUR = registerGeneric("basin_pour");
    public static final Id TABLE_POUR = registerGeneric("table_pour");

    public static final Id1<BlockState> CHISELED = registerBlock("chiseled");
    public static final Id1<BlockState> LIT = registerBlock("lit");
    public static final Id1<BlockState> ROCK_ANVIL = registerBlock("rock_anvil");
    public static final Id1<BlockState> FIREPIT_CREATED = registerBlock("firepit_created");

    public static final Id1<Entity> HOOKED_ENTITY = registerEntity("hooked_entity");
    public static final Id1<Entity> FED_ANIMAL = registerEntity("fed_animal");
    public static final Id1<Entity> STAB_ENTITY = registerEntity("stab_entity");

    public static Id1<BlockState> registerBlock(String name)
    {
        return Id1.of(TRIGGERS.register(name, BlockActionTrigger::new), BlockActionTrigger::trigger);
    }

    public static Id registerGeneric(String name)
    {
        return new Id(TRIGGERS.register(name, GenericTrigger::new));
    }

    public static Id1<Entity> registerEntity(String name)
    {
        return Id1.of(TRIGGERS.register(name, EntityActionTrigger::new), EntityActionTrigger::trigger);
    }

    public record Id(DeferredHolder<CriterionTrigger<?>, GenericTrigger> holder)
    {
        public void trigger(ServerPlayer player)
        {
            holder.value().trigger(player);
        }
    }

    /**
     * This is so indirect because we want to have a top-level {@code trigger(T1)}, but we also don't want to have an ugly double-generic
     * on the {@code Id1<T extends CriterionTrigger<?>, T1>}
     */
    public record Id1<T1>(DeferredHolder<CriterionTrigger<?>, ? extends CriterionTrigger<?>> holder, BiConsumer<ServerPlayer, T1> triggerFunction)
    {
        static <E extends CriterionTrigger<?>, T1> Id1<T1> of(DeferredHolder<CriterionTrigger<?>, E> holder, Function3<E, ServerPlayer, T1> triggerFunction)
        {
            return new Id1<>(holder, (player, t1) -> triggerFunction.apply(holder.get(), player, t1));
        }

        public void trigger(ServerPlayer player, T1 t1)
        {
            triggerFunction.accept(player, t1);
        }
    }

    interface Function3<T1, T2, T3>
    {
        void apply(T1 t1, T2 t2, T3 t3);
    }
}
