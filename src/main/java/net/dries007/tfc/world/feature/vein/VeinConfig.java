package net.dries007.tfc.world.feature.vein;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.FastRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.IFeatureConfig;

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
        Indicator.CODEC.optionalFieldOf("indicator").forGetter(c -> c.indicator),
        Codecs.POSITIVE_INT.fieldOf("rarity").forGetter(VeinConfig::getRarity),
        Codecs.POSITIVE_INT.optionalFieldOf("size", 8).forGetter(VeinConfig::getSize),
        Codecs.NONNEGATIVE_FLOAT.optionalFieldOf("density", 0.2f).forGetter(VeinConfig::getDensity),
        Codec.intRange(0, 256).optionalFieldOf("min_y", 16).forGetter(VeinConfig::getMinY),
        Codec.intRange(0, 256).optionalFieldOf("max_y", 128).forGetter(VeinConfig::getMaxY),
        Codec.LONG.optionalFieldOf("salt").forGetter(c -> Optional.of(c.salt))
    ).apply(instance, VeinConfig::new));
    public static final Codec<VeinConfig> CODEC = MAP_CODEC.codec();

    private final Map<Block, IWeighted<BlockState>> states;
    private final Optional<Indicator> indicator;
    private final int rarity;
    private final int size;
    private final float density;
    private final int minY;
    private final int maxY;
    private final long salt;

    protected VeinConfig(VeinConfig other)
    {
        this(other.states, other.indicator, other.rarity, other.size, other.density, other.minY, other.maxY, Optional.of(other.salt));
    }

    protected VeinConfig(Map<Block, IWeighted<BlockState>> states, Optional<Indicator> indicator, int rarity, int size, float density, int minY, int maxY, Optional<Long> salt)
    {
        this.states = states;
        this.indicator = indicator;
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

    /**
     * A unique, deterministic value for this vein configuration.
     * This is used to randomize the vein chunk seeding such that no two veins have the same seed.
     * If not provided in the codec, a value is computed from the other vein characteristics, but that is likely going to be worse than providing a custom salt.
     */
    public long getSalt()
    {
        return salt;
    }

    public Optional<Indicator> getIndicator()
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
