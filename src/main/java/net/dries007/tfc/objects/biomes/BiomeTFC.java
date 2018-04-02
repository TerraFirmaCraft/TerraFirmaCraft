package net.dries007.tfc.objects.biomes;

import net.minecraft.world.biome.Biome;

public class BiomeTFC extends Biome
{
    public BiomeTFC(BiomeProperties properties)
    {
        super(properties);
    }

    @Override
    public String toString()
    {
        return getBiomeName();
    }
}
