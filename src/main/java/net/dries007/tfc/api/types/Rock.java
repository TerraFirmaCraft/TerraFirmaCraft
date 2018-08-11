/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.types;

import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * todo: document API
 */
public class Rock extends IForgeRegistryEntry.Impl<Rock>
{
    @Nonnull
    public static Collection<Rock> values()
    {
        return Collections.unmodifiableCollection(TFCRegistries.getRocks().getValuesCollection());
    }

    @Nullable
    public static Rock get(String name)
    {
        return values().stream().filter(x -> x.name().equals(name)).findFirst().orElse(null);
    }

    private final ResourceLocation name;

    private final RockCategory rockCategory;
    public Rock(ResourceLocation name, RockCategory rockCategory, boolean layer1, boolean layer2, boolean layer3)
    {
        setRegistryName(name);
        this.rockCategory = rockCategory;
        this.layer1 = layer1;
        this.layer2 = layer2;
        this.layer3 = layer3;
        this.name = name;
    }

    private final boolean layer1;
    private final boolean layer2;
    private final boolean layer3;

    @Nonnull
    public String name()
    {
        return name.getResourcePath();
    }

    public RockCategory getRockCategory()
    {
        return rockCategory;
    }
}
