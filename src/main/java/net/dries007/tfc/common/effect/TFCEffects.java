/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.effect;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.util.registry.RegistryHolder;

public class TFCEffects
{
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, TerraFirmaCraft.MOD_ID);

    // You can still engage swim mode and go directly upwards... for some reason
    public static final Id<MobEffect> PINNED = register("pinned", () -> new TFCMobEffect(MobEffectCategory.HARMFUL, 5926017).addAttributeModifier(Attributes.MOVEMENT_SPEED, "0e31b409-5bbe-44a8-a0df-f596c00897f3", -7.5D, AttributeModifier.Operation.MULTIPLY_TOTAL).addAttributeModifier(ForgeMod.SWIM_SPEED.get(), "99875c8b-c0eb-4ce3-ac4b-fd36b7823e32", -7.5, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final Id<MobEffect> INK = register("ink", () -> new TFCMobEffect(MobEffectCategory.HARMFUL, 0x483454));
    public static final Id<MobEffect> GLOW_INK = register("glow_ink", () -> new TFCMobEffect(MobEffectCategory.HARMFUL, 0x40EDE7));
    public static final Id<MobEffect> OVERBURDENED = register("overburdened", () -> new TFCMobEffect(MobEffectCategory.HARMFUL, 0x263659).addAttributeModifier(Attributes.MOVEMENT_SPEED, "81b630a5-3b60-438f-9c73-728a3427205b", -7.5D, AttributeModifier.Operation.MULTIPLY_TOTAL).addAttributeModifier(ForgeMod.SWIM_SPEED.get(), "459dacad-9943-4583-9bba-9206886e3974", -7.5, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final Id<MobEffect> EXHAUSTED = register("exhausted", () -> new TFCMobEffect(MobEffectCategory.HARMFUL, 0x667A25));
    public static final Id<MobEffect> THIRST = register("thirst", () -> new TFCMobEffect(MobEffectCategory.HARMFUL, 0x9DADD1));

    public static <T extends MobEffect> Id<T> register(String name, Supplier<T> supplier)
    {
        return new Id<>(EFFECTS.register(name, supplier));
    }

    public record Id<T extends MobEffect>(DeferredHolder<MobEffect, T> holder)
        implements RegistryHolder<MobEffect, T> {}
}
