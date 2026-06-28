/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placement;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.volcano.CenteredFeatureNoise;
import net.dries007.tfc.world.volcano.CenteredFeatureNoiseSampler;

public class TuyaPlacement extends CenterOrDistanceToPlacement<CenteredFeatureNoiseSampler>
{
    public static final MapCodec<TuyaPlacement> CODEC = codec(TuyaPlacement::new);

    public TuyaPlacement(boolean center, float minEasing, float maxEasing)
    {
        super(center, minEasing, maxEasing);
    }

    @Override
    public PlacementModifierType<?> type()
    {
        return TFCPlacements.TUYA.get();
    }

    @Override
    protected CenteredFeatureNoiseSampler createContext(Seed seed)
    {
        return CenteredFeatureNoise.tuya(seed);
    }
}
