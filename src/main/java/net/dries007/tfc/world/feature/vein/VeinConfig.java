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
import javax.annotation.Nullable;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraftforge.common.BiomeDictionary;

import com.mojang.datafixers.util.Either;
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
        Codec.either( // Filter can't accept biomes as it ends up resolving the biomes too early (circular reference)
            Biome.BiomeCategory.CODEC.fieldOf("category").codec(),
            Codecs.BIOME_DICTIONARY.fieldOf("biome_dictionary").codec()
        ).listOf().optionalFieldOf("biomes", new ArrayList<>()).forGetter(c -> c.biomeFilter)
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
    private final List<Either<Biome.BiomeCategory, BiomeDictionary.Type>> biomeFilter;

    private final Predicate<Supplier<Biome>> resolvedBiomeFilter;
    private final PositionalRandomFactory fork;

    public VeinConfig(VeinConfig other)
    {
        this(other.states, Optional.ofNullable(other.indicator), other.rarity, other.size, other.density, other.minY, other.maxY, other.randomName, other.biomeFilter);
    }

    public VeinConfig(Map<Block, IWeighted<BlockState>> states, Optional<Indicator> indicator, int rarity, int size, float density, VerticalAnchor minY, VerticalAnchor maxY, String randomName, List<Either<Biome.BiomeCategory, BiomeDictionary.Type>> biomeFilter)
    {
        this.states = states;
        this.indicator = indicator.orElse(null);
        this.rarity = rarity;
        this.size = size;
        this.density = density;
        this.minY = minY;
        this.maxY = maxY;
        this.randomName = randomName;
        this.biomeFilter = biomeFilter;

        this.resolvedBiomeFilter = resolveBiomeFilter();
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

    public boolean canSpawnInBiome(Supplier<Biome> biome)
    {
        return resolvedBiomeFilter.test(biome);
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

    private Predicate<Supplier<Biome>> resolveBiomeFilter()
    {
        // If there's no filter to be found, then importantly, we DO NOT RESOLVE the provided supplier
        // This is important, as it is an expensive operation that need not be applied if not necessary
        if (biomeFilter.isEmpty())
        {
            return supplier -> true;
        }
        else
        {
            final List<BiomeDictionary.Type> types = new ArrayList<>();
            final Set<Biome.BiomeCategory> categories = EnumSet.noneOf(Biome.BiomeCategory.class);
            for (Either<Biome.BiomeCategory, BiomeDictionary.Type> either : biomeFilter)
            {
                either.map(categories::add, types::add);
            }
            if (types.isEmpty() && categories.isEmpty())
            {
                return supplier -> true; // No types or categories matches all biomes
            }
            return supplier -> {
                final Biome biome = supplier.get();
                if (categories.contains(biome.getBiomeCategory()))
                {
                    return true;
                }
                final Set<BiomeDictionary.Type> biomeTypes = BiomeDictionary.getTypes(ResourceKey.create(Registry.BIOME_REGISTRY, Objects.requireNonNull(biome.getRegistryName())));
                for (BiomeDictionary.Type requiredType : types)
                {
                    if (biomeTypes.contains(requiredType))
                    {
                        return true;
                    }
                }
                return false;
            };
        }
    }
}
