/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import net.dries007.tfc.world.Codecs;

public record CreepingPlantConfig(Block block, int radius, int height, float integrity) implements FeatureConfiguration
{
    public static final Codec<CreepingPlantConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.BLOCK.fieldOf("block").forGetter(c -> c.block),
        Codec.INT.fieldOf("radius").forGetter(c -> c.radius),
        Codec.INT.fieldOf("height").forGetter(c -> c.height),
        Codec.FLOAT.optionalFieldOf("integrity", 1f).forGetter(c -> c.integrity)
    ).apply(instance, CreepingPlantConfig::new));
}
