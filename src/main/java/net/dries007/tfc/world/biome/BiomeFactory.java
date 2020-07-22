/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

public class BiomeFactory implements IBiomeFactory
{
    public static TFCBiome getBiome(int id)
    {
        return (TFCBiome) ((ForgeRegistry<Biome>) ForgeRegistries.BIOMES).getValue(id);
    }

    private final LazyArea lazyArea;

    public BiomeFactory(IAreaFactory<LazyArea> lazyAreaFactoryIn)
    {
        this.lazyArea = lazyAreaFactoryIn.make();
    }

    @Override
    public TFCBiome getBiome(int x, int z)
    {
        return getBiome(lazyArea.getValue(x, z));
    }
}
