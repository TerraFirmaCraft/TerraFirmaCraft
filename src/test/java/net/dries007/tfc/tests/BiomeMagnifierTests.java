package net.dries007.tfc.tests;

import java.awt.*;

import net.minecraft.world.biome.*;

import net.dries007.tfc.ImageUtil;
import net.dries007.tfc.world.biome.SmoothColumnBiomeMagnifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class BiomeMagnifierTests
{
    static final BiomeManager.IBiomeReader READER = (x, y, z) -> ((x >> 2) & 1) == 0 ? Biomes.PLAINS : Biomes.OCEAN;
    static final ImageUtil<IBiomeMagnifier> IMAGES = ImageUtil.colored(target -> (x, y) -> {
        Biome biome = target.getBiome(1234, (int) x, 0, (int) y, READER);
        return biome == Biomes.PLAINS ? Color.GREEN : Color.BLUE;
    }, builder -> builder.size(400).dimensions(400));

    @Test
    void testColumnFuzzedBiomeMagnifier()
    {
        IMAGES.draw("fuzzed_biome_magnifier", FuzzedBiomeMagnifier.INSTANCE);
    }

    @Test
    void testSmoothColumnBiomeMagnifierVanilla()
    {
        IMAGES.draw("smooth_biome_magnifier_vanilla", SmoothColumnBiomeMagnifier.VANILLA);
    }

    @Test
    void testSmoothColumnBiomeMagnifierSmall()
    {
        IMAGES.draw("smooth_biome_magnifier_small", SmoothColumnBiomeMagnifier.SMOOTH);
    }

    @Test
    void testSmoothColumnBiomeMagnifierLarge()
    {
        IMAGES.draw("smooth_biome_magnifier_large", new SmoothColumnBiomeMagnifier(1.3));
    }
}
