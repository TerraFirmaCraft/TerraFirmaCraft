/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placement;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.biome.VolcanoNoise;

public class VolcanoPlacement extends CenterOrDistanceToPlacement<VolcanoNoise>
{
    public static final MapCodec<VolcanoPlacement> CODEC = codec(VolcanoPlacement::new);

    public VolcanoPlacement(boolean center, float distance)
    {
        super(center, distance);
    }

    @Override
    public PlacementModifierType<?> type()
    {
        return TFCPlacements.VOLCANO.get();
    }

    @Override
    protected VolcanoNoise createContext(Seed seed)
    {
        return new VolcanoNoise(seed);
    }
}
