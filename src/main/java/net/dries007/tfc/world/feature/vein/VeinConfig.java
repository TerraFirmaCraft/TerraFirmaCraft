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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.FastRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraftforge.common.util.Lazy;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.world.Codecs;

public class VeinConfig implements IFeatureConfig
{
    @SuppressWarnings("deprecation")
    public static final MapCodec<VeinConfig> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codecs.mapKeyListCodec(Codec.mapPair(
            Registry.BLOCK.listOf().fieldOf("stone"),
            Codecs.weightedCodec(Codecs.LENIENT_BLOCKSTATE, "block").fieldOf("ore")
        ).codec()).fieldOf("blocks").forGetter(c -> c.states),
        Indicator.CODEC.optionalFieldOf("indicator").forGetter(c -> Optional.ofNullable(c.indicator)),
        Codecs.POSITIVE_INT.optionalFieldOf("rarity", 60).forGetter(VeinConfig::getRarity),
        Codecs.POSITIVE_INT.optionalFieldOf("size", 8).forGetter(VeinConfig::getSize),
        Codecs.NONNEGATIVE_FLOAT.optionalFieldOf("density", 0.2f).forGetter(VeinConfig::getDensity),
        Codec.intRange(0, 256).optionalFieldOf("min_y", 16).forGetter(VeinConfig::getMinY),
        Codec.intRange(0, 256).optionalFieldOf("max_y", 128).forGetter(VeinConfig::getMaxY),
        Codec.LONG.optionalFieldOf("salt").forGetter(c -> Optional.of(c.salt)),
        Codec.either(
            Biome.Category.CODEC.fieldOf("category").codec(),
            Biome.CODEC
        ).listOf().optionalFieldOf("biomes", new ArrayList<>()).forGetter(c -> c.biomeFilter)
    ).apply(instance, VeinConfig::new));
    public static final Codec<VeinConfig> CODEC = MAP_CODEC.codec();

    private final Map<Block, IWeighted<BlockState>> states;
    @Nullable
    private final Indicator indicator;
    private final int rarity;
    private final int size;
    private final float density;
    private final int minY;
    private final int maxY;
    private final long salt;
    private final List<Either<Biome.Category, Supplier<Biome>>> biomeFilter;
    private final Lazy<Predicate<Supplier<Biome>>> resolvedBiomeFilter;

    public VeinConfig(VeinConfig other)
    {
        this(other.states, Optional.ofNullable(other.indicator), other.rarity, other.size, other.density, other.minY, other.maxY, Optional.of(other.salt), other.biomeFilter);
    }

    public VeinConfig(Map<Block, IWeighted<BlockState>> states, Optional<Indicator> indicator, int rarity, int size, float density, int minY, int maxY, Optional<Long> salt, List<Either<Biome.Category, Supplier<Biome>>> biomeFilter)
    {
        this.states = states;
        this.indicator = indicator.orElse(null);
        this.rarity = rarity;
        this.size = size;
        this.density = density;
        this.minY = minY;
        this.maxY = maxY;
        this.salt = salt.orElseGet(() -> {
            long seed = FastRandom.next(size, Float.floatToIntBits(density));
            seed = FastRandom.next(seed, minY);
            seed = FastRandom.next(seed, maxY);
            seed = FastRandom.next(seed, rarity);
            seed = FastRandom.next(seed, rarity);
            return seed;
        });
        this.biomeFilter = biomeFilter;
        this.resolvedBiomeFilter = Lazy.of(() -> {
            // If there's no filter to be found, then importantly, we DO NOT RESOLVE the provided supplier
            // This is important, as it is an expensive operation that need not be applied if not necessary
            if (biomeFilter.isEmpty())
            {
                return supplier -> true;
            }
            else
            {
                final Set<Biome> biomes = new HashSet<>();
                final Set<Biome.Category> categories = EnumSet.noneOf(Biome.Category.class);
                for (Either<Biome.Category, Supplier<Biome>> either : biomeFilter)
                {
                    either.map(categories::add, b -> biomes.add(b.get()));
                }
                return supplier -> {
                    final Biome biome = supplier.get();
                    return categories.contains(biome.getBiomeCategory()) || biomes.contains(biome);
                };
            }
        });
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
        return resolvedBiomeFilter.get().test(biome);
    }

    /**
     * A unique, deterministic value for this vein configuration.
     * This is used to randomize the vein chunk seeding such that no two veins have the same seed.
     * If not provided in the codec, a value is computed from the other vein characteristics, but that is likely going to be worse than providing a custom salt.
     */
    public long getSalt()
    {
        return salt;
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

    public int getMinY()
    {
        return minY;
    }

    public int getMaxY()
    {
        return maxY;
    }
}
