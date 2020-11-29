package net.dries007.tfc.unit;

import java.awt.*;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.area.LazyArea;

import net.dries007.tfc.Artist;
import net.dries007.tfc.util.IArtist;
import net.dries007.tfc.world.biome.BiomeVariants;
import net.dries007.tfc.world.biome.TFCBiomeProvider;
import net.dries007.tfc.world.biome.VolcanoNoise;
import net.dries007.tfc.world.layer.TFCLayerUtil;
import net.dries007.tfc.world.noise.Cellular2D;
import net.dries007.tfc.world.noise.CellularNoiseType;
import net.dries007.tfc.world.noise.INoise2D;
import org.junit.jupiter.api.Test;

public class VolcanoNoiseTests
{
    static final Artist.Raw RAW = Artist.raw().size(1000);

    @Test
    public void testVolcanoNoise()
    {
        long seed = System.currentTimeMillis();

        Cellular2D volcanoNoise = VolcanoNoise.cellNoise(seed);
        INoise2D volcanoJitterNoise = VolcanoNoise.distanceVariationNoise(seed);

        LazyArea biomeArea = TFCLayerUtil.createOverworldBiomeLayer(seed, new TFCBiomeProvider.LayerSettings(), IArtist.nope(), IArtist.nope()).make();

        Artist.Pixel<Color> volcanoBiomeMap = Artist.Pixel.coerceFloat((x, z) -> {
            int value = biomeArea.get(((int) x) >> 2, ((int) z) >> 2);
            BiomeVariants biome = TFCLayerUtil.getFromLayerId(value);
            if (biome.isVolcanic())
            {
                float distance = volcanoNoise.noise(x, z) + volcanoJitterNoise.noise(x, z);
                float volcano = VolcanoNoise.calculateEasing(distance);
                float chance = volcanoNoise.get(CellularNoiseType.VALUE);
                if (volcano > 0 && chance < biome.getVolcanoChance())
                {
                    return new Color(MathHelper.clamp((int) (155 + 100 * volcano), 0, 255), 30, 30); // Near volcano
                }
            }
            return TFCLayerUtilTests.biomeColor(value);
        });

        RAW.center(20_000).size(1_000); // 40 km image, at 1 pixel = 20 blocks
        //RAW.draw("volcano_locations", volcanoMap);
        RAW.draw("volcano_biome_map", volcanoBiomeMap);
    }
}
