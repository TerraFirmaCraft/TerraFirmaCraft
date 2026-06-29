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

import net.dries007.tfc.common.player.IPlayerInfo;

public class ThirstEffect extends MobEffect
{
    public ThirstEffect(MobEffectCategory category, int color)
    {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplitude)
    {
        if (entity instanceof Player player)
        {
            final IPlayerInfo info = IPlayerInfo.get(player);
            if (info.getThirst() > 0.05f)
            {
                info.addThirst(-0.02f * (amplitude + 1));
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplitude)
    {
        final int ticker = 50 >> amplitude;
        return ticker == 0 || duration % ticker == 0;
    }
}
