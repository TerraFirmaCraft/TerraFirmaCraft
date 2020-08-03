package net.dries007.tfc.tests;

import java.awt.*;

import org.apache.commons.lang3.tuple.Pair;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.ColumnFuzzedBiomeMagnifier;
import net.minecraft.world.biome.IBiomeMagnifier;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

import net.dries007.tfc.ImageUtil;
import net.dries007.tfc.world.biome.SmoothColumnBiomeMagnifier;
import org.junit.jupiter.api.Test;

/**
 * Tests the vanilla and our biome magnifiers, in terms of how they line up with chunk edges.
 */
class BiomeOffsetTests
{
    static final BiomeManager.IBiomeReader READER = (x, y, z) -> ((ForgeRegistry<Biome>) ForgeRegistries.BIOMES).getValue((x + z * 4) & 15);
    static final ImageUtil<Pair<IBiomeMagnifier, BiomeManager.IBiomeReader>> IMAGES = ImageUtil.colored(pair -> (x, y) -> {
            if (x == 0 && y % 2 == 0)
            {
                return Color.BLACK;
            }
            else if (y == 0 && x % 2 == 0)
            {
                return Color.WHITE;
            }
            return ImageUtil.Colors.COLORS[MathHelper.clamp(((ForgeRegistry<Biome>) ForgeRegistries.BIOMES).getID(pair.getLeft().getBiome(0, (int) x, 0, (int) y, pair.getRight())), 0, ImageUtil.Colors.COLORS.length)];
        },
        builder -> builder.size(32).dimensions(32, 32)
    );

    @Test
    void testBiomeOffsets()
    {
        IMAGES.draw("biome_offsets_2x2_chunks", Pair.of(new SmoothColumnBiomeMagnifier(0), READER));
    }

    @Test
    void testNoisyBiomeOffsets()
    {
        IMAGES.draw("biome_offsets_noisy_2x2_chunks", Pair.of(ColumnFuzzedBiomeMagnifier.INSTANCE, READER));
    }
}
