/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.animal;

import net.minecraft.world.biome.Biome;

public interface IAnimalTFC
{
    boolean isValidSpawnConditions(Biome biome, float temperature, float rainfall);
}
