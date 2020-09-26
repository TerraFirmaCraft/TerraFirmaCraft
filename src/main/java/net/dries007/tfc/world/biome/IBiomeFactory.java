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

public interface IBiomeFactory
{
    static TFCBiome getBiome(int id)
    {
        return (TFCBiome) ((ForgeRegistry<Biome>) ForgeRegistries.BIOMES).getValue(id);
    }

    static IBiomeFactory create(IAreaFactory<LazyArea> areaFactory)
    {
        LazyArea area = areaFactory.make();
        return (x, z) -> getBiome(area.getValue(x, z));
    }

    TFCBiome getBiome(int x, int z);
}
