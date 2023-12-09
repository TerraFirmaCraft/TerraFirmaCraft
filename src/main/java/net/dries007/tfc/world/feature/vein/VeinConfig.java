/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.vein;

import java.util.Map;
import java.util.Optional;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.RandomSupport;

import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.world.Codecs;

public record VeinConfig(
    Map<Block, IWeighted<BlockState>> states,
    Optional<Indicator> indicator,
    int rarity,
    float density,
    int minY,
    int maxY,
    boolean projectToSurface,
    boolean projectOffset,
    long seed,
    Optional<TagKey<Biome>> biomes,
    boolean nearLava
) {
    public static final MapCodec<VeinConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codecs.BLOCK_TO_WEIGHTED_BLOCKSTATE.fieldOf("blocks").forGetter(c -> c.states),
        Codecs.optionalFieldOf(Indicator.CODEC, "indicator").forGetter(c -> c.indicator),
        Codecs.POSITIVE_INT.fieldOf("rarity").forGetter(c -> c.rarity),
        Codecs.UNIT_FLOAT.fieldOf("density").forGetter(c -> c.density),
        Codec.INT.fieldOf("min_y").forGetter(c -> c.minY),
        Codec.INT.fieldOf("max_y").forGetter(c -> c.maxY),
        Codec.BOOL.optionalFieldOf("project", false).forGetter(c -> c.projectToSurface),
        Codec.BOOL.optionalFieldOf("project_offset", false).forGetter(c -> c.projectOffset),
        Codec.either(
            Codec.STRING,
            Codec.LONG
        ).xmap(e -> e.map(
            VeinConfig::hash,
            l -> l
        ), Either::right).fieldOf("random_name").forGetter(c -> c.seed),
        Codecs.optionalFieldOf(TagKey.hashedCodec(Registries.BIOME), "biomes").forGetter(c -> c.biomes),
        Codecs.optionalFieldOf(Codec.BOOL, "near_lava", false).forGetter(c -> c.nearLava)
    ).apply(instance, VeinConfig::new));

    private static long hash(String name)
    {
        final RandomSupport.Seed128bit seed128 = RandomSupport.seedFromHashOf(name);
        return seed128.seedLo() ^ seed128.seedHi();
    }
}
