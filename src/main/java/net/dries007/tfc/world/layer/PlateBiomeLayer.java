package net.dries007.tfc.world.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

import static net.dries007.tfc.world.layer.TFCLayerUtil.*;

public enum PlateBiomeLayer implements IC0Transformer
{
    INSTANCE;

    private static final int[] OCEAN_CONTINENT_CONVERGING_BIOMES = new int[] {FLOODED_MOUNTAINS, FLOODED_MOUNTAINS, OLD_MOUNTAINS, PLATEAU, CANYONS, CANYONS};
    private static final int[] CONTINENT_LOW_BIOMES = new int[] {PLAINS, HILLS, LOW_CANYONS, LOWLANDS, HILLS};
    private static final int[] CONTINENT_MID_BIOMES = new int[] {ROLLING_HILLS, CANYONS, BADLANDS, HILLS, PLAINS};
    private static final int[] CONTINENT_HIGH_BIOMES = new int[] {PLATEAU, BADLANDS, OLD_MOUNTAINS, ROLLING_HILLS};

    @Override
    public int apply(INoiseRandom context, int value)
    {
        switch (value)
        {
            case OCEANIC:
                return DEEP_OCEAN;
            case OCEAN_OCEAN_CONVERGING:
                // Volcanic Island chains or rifts
                // Sample sparse flooded mountains for now until a proper archipelago biome exists
                return OCEAN_OCEAN_CONVERGING_MARKER;
            case OCEAN_OCEAN_DIVERGING:
                // Mid-ocean rift
                // Return converging marker for now, will generate island chains
                return OCEAN_OCEAN_CONVERGING_MARKER;
            case OCEAN_CONTINENT_CONVERGING:
                // Mountains or high terrain
                return OCEAN_CONTINENT_CONVERGING_BIOMES[context.nextRandom(OCEAN_CONTINENT_CONVERGING_BIOMES.length)];
            case OCEAN_CONTINENT_DIVERGING:
            case CONTINENTAL_LOW:
                // Normal biomes
                return CONTINENT_LOW_BIOMES[context.nextRandom(CONTINENT_LOW_BIOMES.length)];
            case CONTINENT_CONTINENT_DIVERGING:
            case CONTINENTAL_MID:
                // Mid scale height biomes
                return CONTINENT_MID_BIOMES[context.nextRandom(CONTINENT_MID_BIOMES.length)];
            case CONTINENTAL_HIGH:
                // High height biomes
                return CONTINENT_HIGH_BIOMES[context.nextRandom(CONTINENT_HIGH_BIOMES.length)];
            case CONTINENT_CONTINENT_CONVERGING:
                // Ultra supreme mountain building
                return context.nextRandom(3) == 0 ? PLATEAU : MOUNTAINS;
        }
        throw new IllegalStateException("What is this: " + value);
    }
}
