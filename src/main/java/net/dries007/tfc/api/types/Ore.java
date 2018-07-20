/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.types;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * todo: document API
 */
public class Ore extends IForgeRegistryEntry.Impl<Ore>
{
    public Ore(ResourceLocation name)
    {
        setRegistryName(name);
    }

    // todo: add required fields (metal, graded, nuggets?)
}
