/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic;

import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.layer.GenLayer;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.world.classic.biomes.BiomesTFC;
import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BiomeProviderTFC extends BiomeProvider
{
    public BiomeProviderTFC(World world)
    {
        super(world.getWorldInfo());

        if (!(world.getWorldType() instanceof WorldTypeTFC))
        {
            throw new RuntimeException("Terrible things have gone wrong here.");
        }
    }

    @Override
    public float getTemperatureAtHeight(float p_76939_1_, int p_76939_2_)
    {
        return super.getTemperatureAtHeight(p_76939_1_, p_76939_2_);
    }

    @Override
    public List<Biome> getBiomesToSpawnIn()
    {
        return BiomesTFC.getSpawnBiomes();
    }

    /**
     * This is where we do the actual override of the generation, we discard the original and insert our own.
     */
    @Override
    public GenLayer[] getModdedBiomeGenerators(WorldType worldType, long seed, GenLayer[] original)
    {
        original = GenLayerTFC.initialize2(seed);
        return super.getModdedBiomeGenerators(worldType, seed, original);
    }
}
