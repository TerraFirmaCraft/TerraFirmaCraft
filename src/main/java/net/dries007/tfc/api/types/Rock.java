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
public class Rock extends IForgeRegistryEntry.Impl<Rock>
{
    private final RockCategory rockCategory;
    private final boolean layer1;
    private final boolean layer2;
    private final boolean layer3;

    public Rock(ResourceLocation name, RockCategory rockCategory, boolean layer1, boolean layer2, boolean layer3)
    {
        setRegistryName(name);
        this.rockCategory = rockCategory;
        this.layer1 = layer1;
        this.layer2 = layer2;
        this.layer3 = layer3;
    }

    public RockCategory getRockCategory()
    {
        return rockCategory;
    }
}
