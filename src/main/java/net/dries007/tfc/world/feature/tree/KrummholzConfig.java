/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import net.dries007.tfc.world.Codecs;

public record KrummholzConfig(Block block, IntProvider height, boolean spawnsOnStone, boolean spawnsOnGravel) implements FeatureConfiguration
{
    public static final Codec<KrummholzConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.BLOCK.fieldOf("block").forGetter(c -> c.block),
        IntProvider.CODEC.fieldOf("height").forGetter(c -> c.height),
        Codec.BOOL.optionalFieldOf("spawns_on_stone", false).forGetter(c -> c.spawnsOnStone),
        Codec.BOOL.optionalFieldOf("spawns_on_gravel", false).forGetter(c -> c.spawnsOnGravel)
    ).apply(instance, KrummholzConfig::new));
}
