/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common;

import java.util.function.Supplier;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.TerraFirmaCraft;

public class TFCEffects
{
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, TerraFirmaCraft.MOD_ID);

    public static final RegistryObject<MobEffect> PINNED = register("pinned", () -> new TFCMobEffect(MobEffectCategory.HARMFUL, 5926017).addAttributeModifier(Attributes.MOVEMENT_SPEED, "0e31b409-5bbe-44a8-a0df-f596c00897f3", -7.5D, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryObject<MobEffect> INK = register("ink", () -> new TFCMobEffect(MobEffectCategory.HARMFUL, 0x483454));
    public static final RegistryObject<MobEffect> GLOW_INK = register("glow_ink", () -> new TFCMobEffect(MobEffectCategory.HARMFUL, 0x40EDE7));

    public static <T extends MobEffect> RegistryObject<T> register(String name, Supplier<T> supplier)
    {
        return EFFECTS.register(name, supplier);
    }

    public static class TFCMobEffect extends MobEffect
    {
        public TFCMobEffect(MobEffectCategory category, int color)
        {
            super(category, color);
        }

        @Override
        public void applyEffectTick(LivingEntity entity, int amplitude)
        {
            if (this == PINNED.get() && entity instanceof Player player)
            {
                player.setForcedPose(Pose.SLEEPING);
            }
        }

        @Override
        public boolean isDurationEffectTick(int duration, int amplitude)
        {
            return this == PINNED.get();
        }
    }
}
