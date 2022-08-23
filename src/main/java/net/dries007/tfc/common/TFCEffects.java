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
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.capabilities.food.TFCFoodData;
import net.dries007.tfc.config.TFCConfig;

public class TFCEffects
{
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, TerraFirmaCraft.MOD_ID);

    // You can still engage swim mode and go directly upwards... for some reason
    public static final RegistryObject<MobEffect> PINNED = register("pinned", () -> new TFCMobEffect(MobEffectCategory.HARMFUL, 5926017).addAttributeModifier(Attributes.MOVEMENT_SPEED, "0e31b409-5bbe-44a8-a0df-f596c00897f3", -7.5D, AttributeModifier.Operation.MULTIPLY_TOTAL).addAttributeModifier(ForgeMod.SWIM_SPEED.get(), "99875c8b-c0eb-4ce3-ac4b-fd36b7823e32", -7.5, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryObject<MobEffect> INK = register("ink", () -> new TFCMobEffect(MobEffectCategory.HARMFUL, 0x483454));
    public static final RegistryObject<MobEffect> GLOW_INK = register("glow_ink", () -> new TFCMobEffect(MobEffectCategory.HARMFUL, 0x40EDE7));
    public static final RegistryObject<MobEffect> OVERBURDENED = register("overburdened", () -> new TFCMobEffect(MobEffectCategory.HARMFUL, 0x263659).addAttributeModifier(Attributes.MOVEMENT_SPEED, "81b630a5-3b60-438f-9c73-728a3427205b", -7.5D, AttributeModifier.Operation.MULTIPLY_TOTAL).addAttributeModifier(ForgeMod.SWIM_SPEED.get(), "459dacad-9943-4583-9bba-9206886e3974", -7.5, AttributeModifier.Operation.MULTIPLY_TOTAL));
    public static final RegistryObject<MobEffect> EXHAUSTED = register("exhausted", () -> new TFCMobEffect(MobEffectCategory.HARMFUL, 0x667A25));
    public static final RegistryObject<MobEffect> THIRST = register("thirst", () -> new TFCMobEffect(MobEffectCategory.HARMFUL, 0x9DADD1));

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
            if (entity instanceof Player player)
            {
                if (this == PINNED.get())
                {
                    player.setForcedPose(Pose.SLEEPING);
                }
                else if (this == THIRST.get() && player.getFoodData() instanceof TFCFoodData foodData)
                {
                    if (foodData.getThirst() > 0.05f)
                    {
                        foodData.addThirst(-0.02f * (amplitude + 1));
                    }
                }
                else if (this == EXHAUSTED.get())
                {
                    player.causeFoodExhaustion(TFCFoodData.PASSIVE_EXHAUSTION_PER_SECOND * 20 * TFCConfig.SERVER.passiveExhaustionModifier.get().floatValue() * 0.25f);
                }
            }
        }

        @Override
        public boolean isDurationEffectTick(int duration, int amplitude)
        {
            return this == PINNED.get() || tickForAmplitude(THIRST, 50, amplitude) || tick(EXHAUSTED, duration % 20 == 0);
        }

        private boolean tick(Supplier<MobEffect> check, boolean accepted)
        {
            return this == check.get() && accepted;
        }

        /**
         * {@link MobEffect#isDurationEffectTick(int, int)}
         */
        private boolean tickForAmplitude(Supplier<MobEffect> check, int base, int amplitude)
        {
            if (this == check.get())
            {
                final int ticker = base >> amplitude;
                return ticker <= 0 || amplitude % ticker == 0;
            }
            return false;
        }
    }
}
