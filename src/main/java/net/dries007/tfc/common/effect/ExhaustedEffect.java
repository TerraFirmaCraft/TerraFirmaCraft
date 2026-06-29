/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import net.dries007.tfc.common.player.PlayerInfo;
import net.dries007.tfc.config.TFCConfig;

public class ExhaustedEffect extends MobEffect
{
    public ExhaustedEffect(MobEffectCategory category, int color)
    {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplitude)
    {
        if (entity instanceof Player player)
        {
            player.causeFoodExhaustion(PlayerInfo.PASSIVE_EXHAUSTION_PER_TICK * 20 * TFCConfig.SERVER.passiveExhaustionModifier.get().floatValue() * 0.25f);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplitude)
    {
        return duration % 20 == 0;
    }
}
