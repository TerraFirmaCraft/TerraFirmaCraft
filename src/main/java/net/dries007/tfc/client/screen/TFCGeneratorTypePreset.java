package net.dries007.tfc.client.screen;

import net.minecraft.client.gui.screen.BiomeGeneratorTypeScreens;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraftforge.common.util.Lazy;

import net.dries007.tfc.world.TFCChunkGenerator;

public class TFCGeneratorTypePreset extends BiomeGeneratorTypeScreens
{
    public static final Lazy<TFCGeneratorTypePreset> PRESET = Lazy.of(TFCGeneratorTypePreset::new);

    public static void setup()
    {
        PRESET.get(); // "Register" at the appropriate time
    }

    protected TFCGeneratorTypePreset()
    {
        super("tfc");

        PRESETS.add(this); // Add to the list of presets
    }

    @Override
    protected ChunkGenerator generator(Registry<Biome> biomeRegistry, Registry<DimensionSettings> dimensionSettings, long seed)
    {
        return TFCChunkGenerator.createDefaultPreset(() -> dimensionSettings.getOrThrow(DimensionSettings.OVERWORLD), biomeRegistry, seed);
    }
}
