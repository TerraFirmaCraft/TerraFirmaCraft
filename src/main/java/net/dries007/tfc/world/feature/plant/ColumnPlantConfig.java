/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

public record ColumnPlantConfig(BlockState bodyState, BlockState headState, int tries, int radius, int minHeight, int maxHeight) implements FeatureConfiguration
{
    public static final Codec<ColumnPlantConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.BLOCK_STATE.fieldOf("body").forGetter(c -> c.bodyState),
        Codecs.BLOCK_STATE.fieldOf("head").forGetter(c -> c.bodyState),
        Codec.intRange(1, 128).fieldOf("tries").forGetter(c -> c.tries),
        Codec.intRange(1, 16).fieldOf("radius").forGetter(c -> c.radius),
        Codec.intRange(1, 100).fieldOf("min_height").forGetter(c -> c.minHeight),
        Codec.intRange(1, 100).fieldOf("max_height").forGetter(c -> c.maxHeight)
    ).apply(instance, ColumnPlantConfig::new));

}
