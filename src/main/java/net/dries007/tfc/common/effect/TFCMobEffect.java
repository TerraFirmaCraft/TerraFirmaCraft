/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * A plain effect with no per-tick behavior, used for effects that only need to exist as a marker
 * or apply static attribute modifiers. Effects with tick behavior have their own classes, e.g.
 * {@link ThirstEffect} and {@link ExhaustedEffect}.
 */
public class TFCMobEffect extends MobEffect
{
    public TFCMobEffect(MobEffectCategory category, int color)
    {
        super(category, color);
    }
}
