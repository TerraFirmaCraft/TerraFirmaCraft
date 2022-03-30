/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.vein;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.world.Codecs;

public class VeinConfig implements FeatureConfiguration
{
    public static final MapCodec<VeinConfig> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codecs.BLOCK_TO_WEIGHTED_BLOCKSTATE.fieldOf("blocks").forGetter(c -> c.states),
        Indicator.CODEC.optionalFieldOf("indicator").forGetter(c -> Optional.ofNullable(c.indicator)),
        Codecs.POSITIVE_INT.optionalFieldOf("rarity", 60).forGetter(VeinConfig::getRarity),
        Codecs.POSITIVE_INT.optionalFieldOf("size", 8).forGetter(VeinConfig::getSize),
        Codecs.UNIT_FLOAT.optionalFieldOf("density", 0.2f).forGetter(VeinConfig::getDensity),
        VerticalAnchor.CODEC.fieldOf("min_y").forGetter(c -> c.minY),
        VerticalAnchor.CODEC.fieldOf("max_y").forGetter(c -> c.maxY),
        Codec.STRING.fieldOf("random_name").forGetter(c -> c.randomName),
        TagKey.hashedCodec(Registry.BIOME_REGISTRY).optionalFieldOf("biomes").forGetter(c -> Optional.ofNullable(c.biomes))
    ).apply(instance, VeinConfig::new));

    public static final Codec<VeinConfig> CODEC = MAP_CODEC.codec();

    private final Map<Block, IWeighted<BlockState>> states;
    @Nullable private final Indicator indicator;
    private final int rarity;
    private final int size;
    private final float density;
    private final VerticalAnchor minY;
    private final VerticalAnchor maxY;
    private final String randomName;
    @Nullable private final TagKey<Biome> biomes;

    private final PositionalRandomFactory fork;

    public VeinConfig(VeinConfig other)
    {
        this(other.states, Optional.ofNullable(other.indicator), other.rarity, other.size, other.density, other.minY, other.maxY, other.randomName, Optional.ofNullable(other.biomes));
    }

    public VeinConfig(Map<Block, IWeighted<BlockState>> states, Optional<Indicator> indicator, int rarity, int size, float density, VerticalAnchor minY, VerticalAnchor maxY, String randomName, Optional<TagKey<Biome>> biomes)
    {
        this.states = states;
        this.indicator = indicator.orElse(null);
        this.rarity = rarity;
        this.size = size;
        this.density = density;
        this.minY = minY;
        this.maxY = maxY;
        this.randomName = randomName;
        this.biomes = biomes.orElse(null);

        this.fork = new XoroshiroRandomSource(18729341234L, 9182639418231L)
            .forkPositional()
            .fromHashOf(randomName)
            .forkPositional();
    }

    public Set<BlockState> getOreStates()
    {
        return states.values().stream().flatMap(weighted -> weighted.values().stream()).collect(Collectors.toSet());
    }

    @Nullable
    public BlockState getStateToGenerate(BlockState stoneState, Random random)
    {
        final IWeighted<BlockState> weighted = states.get(stoneState.getBlock());
        if (weighted != null)
        {
            return weighted.get(random);
        }
        return null;
    }

    public boolean canSpawnInBiome(Holder<Biome> biome)
    {
        return biomes == null || biome.is(biomes);
    }

    public RandomSource random(long levelSeed, int chunkX, int chunkZ)
    {
        return fork.at((int) levelSeed, chunkX, chunkZ);
    }

    @Nullable
    public Indicator getIndicator()
    {
        return indicator;
    }

    public int getSize()
    {
        return size;
    }

    public int getRarity()
    {
        return rarity;
    }

    public float getDensity()
    {
        return density;
    }

    public int getChunkRadius()
    {
        return 1 + (size >> 4);
    }

    public int getMinY(WorldGenerationContext context)
    {
        return minY.resolveY(context);
    }

    public int getMaxY(WorldGenerationContext context)
    {
        return maxY.resolveY(context);
    }
}
