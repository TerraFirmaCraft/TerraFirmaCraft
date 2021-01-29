/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.decorator;

import net.minecraft.world.gen.placement.IPlacementConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

public class NearWaterConfig implements IPlacementConfig
{
    public static final Codec<NearWaterConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.NONNEGATIVE_INT.optionalFieldOf("radius", 2).forGetter(NearWaterConfig::getRadius)
    ).apply(instance, NearWaterConfig::new));

    private final int radius;

    public NearWaterConfig(int radius)
    {
        this.radius = radius;
    }

    public int getRadius()
    {
        return radius;
    }
}
