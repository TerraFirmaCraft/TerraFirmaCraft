package net.dries007.tfc.world.biome;

import java.util.function.LongFunction;

import com.mojang.datafixers.util.Pair;
import net.dries007.tfc.world.noise.INoise2D;

/**
 * An extension of {@link BiomeVariants} for variants that carve sea level caverns
 */
public class CarvingBiomeVariants extends BiomeVariants
{
    private final BiomeVariants parent;
    private final LongFunction<Pair<INoise2D, INoise2D>> carvingNoiseFactory;

    public CarvingBiomeVariants(BiomeVariants parent, LongFunction<Pair<INoise2D, INoise2D>> carvingNoiseFactory)
    {
        super(parent::createNoiseLayer, parent.getSmallGroup(), parent.getLargeGroup());

        this.parent = parent;
        this.carvingNoiseFactory = carvingNoiseFactory;
    }

    public BiomeVariants getParent()
    {
        return parent;
    }

    /**
     * Create a pair of noise functions describing this variants carving features
     *
     * @param seed A seed to use in noise generation
     * @return A pair of the center noise (first), and the height noise (second)
     */
    public Pair<INoise2D, INoise2D> createCarvingNoiseLayer(long seed)
    {
        return carvingNoiseFactory.apply(seed);
    }
}
