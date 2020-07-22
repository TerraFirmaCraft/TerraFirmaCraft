/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

public interface IBiomeFactory
{
    TFCBiome getBiome(int x, int z);
}
