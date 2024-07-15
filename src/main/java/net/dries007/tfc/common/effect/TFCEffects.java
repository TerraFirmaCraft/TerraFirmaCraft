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
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistryHolder;

public class TFCEffects
{
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, TerraFirmaCraft.MOD_ID);

    // You can still engage swim mode and go directly upwards... for some reason
    // todo: what are the names that need to be used for the attributes here?
    public static final Id<MobEffect> PINNED = register("pinned", () -> new TFCMobEffect(MobEffectCategory.HARMFUL, 5926017)
        .addAttributeModifier(Attributes.MOVEMENT_SPEED, Helpers.identifier("effect.pinned"), -7.5D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
        .addAttributeModifier(NeoForgeMod.SWIM_SPEED, Helpers.identifier("effect.swim_speed"), -7.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final Id<MobEffect> INK = register("ink", () -> new TFCMobEffect(MobEffectCategory.HARMFUL, 0x483454));
    public static final Id<MobEffect> GLOW_INK = register("glow_ink", () -> new TFCMobEffect(MobEffectCategory.HARMFUL, 0x40EDE7));
    public static final Id<MobEffect> OVERBURDENED = register("overburdened", () -> new TFCMobEffect(MobEffectCategory.HARMFUL, 0x263659)
        .addAttributeModifier(Attributes.MOVEMENT_SPEED, Helpers.identifier("effect.move"), -7.5D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
        .addAttributeModifier(NeoForgeMod.SWIM_SPEED, Helpers.identifier("effect.swim_speed"), -7.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final Id<MobEffect> EXHAUSTED = register("exhausted", () -> new TFCMobEffect(MobEffectCategory.HARMFUL, 0x667A25));
    public static final Id<MobEffect> THIRST = register("thirst", () -> new TFCMobEffect(MobEffectCategory.HARMFUL, 0x9DADD1));

    public static <T extends MobEffect> Id<T> register(String name, Supplier<T> supplier)
    {
        return new Id<>(EFFECTS.register(name, supplier));
    }

    public record Id<T extends MobEffect>(DeferredHolder<MobEffect, T> holder)
        implements RegistryHolder<MobEffect, T> {}
}
