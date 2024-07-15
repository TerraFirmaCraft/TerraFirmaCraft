/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.effect;

import java.util.function.Supplier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

import net.dries007.tfc.common.player.IPlayerInfo;
import net.dries007.tfc.common.player.PlayerInfo;
import net.dries007.tfc.config.TFCConfig;

// todo: this should really be split out into individual classes for specific effects, this is really ugly
public class TFCMobEffect extends MobEffect
{
    public TFCMobEffect(MobEffectCategory category, int color)
    {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplitude)
    {
        if (entity instanceof Player player)
        {
            if (this == TFCEffects.PINNED.get())
            {
                player.setForcedPose(Pose.SLEEPING);
            }
            else if (this == TFCEffects.THIRST.get())
            {
                final IPlayerInfo info = IPlayerInfo.get(player);
                if (info.getThirst() > 0.05f)
                {
                    info.addThirst(-0.02f * (amplitude + 1));
                }
            }
            else if (this == TFCEffects.EXHAUSTED.get())
            {
                player.causeFoodExhaustion(PlayerInfo.PASSIVE_EXHAUSTION_PER_TICK * 20 * TFCConfig.SERVER.passiveExhaustionModifier.get().floatValue() * 0.25f);
            }
        }
        return true; // todo 1.21: what to return here?
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplitude)
    {
        // todo: was replaced from isDurationEffectTick
        return this == TFCEffects.PINNED.get() || tickForAmplitude(TFCEffects.THIRST, 50, amplitude) || tick(TFCEffects.EXHAUSTED, duration % 20 == 0);
    }

    private boolean tick(Supplier<MobEffect> check, boolean accepted)
    {
        return this == check.get() && accepted;
    }

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
