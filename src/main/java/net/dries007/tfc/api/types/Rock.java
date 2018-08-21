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
import net.minecraftforge.registries.ForgeRegistry;
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

    @Nullable
    public static Rock get(int id)
    {
        return ((ForgeRegistry<Rock>) TFCRegistries.getRocks()).getValue(id);
    }

    private final ResourceLocation name;
    private final RockCategory rockCategory;

    public Rock(@Nonnull ResourceLocation name, @Nonnull RockCategory rockCategory)
    {
        setRegistryName(name);
        this.rockCategory = rockCategory;
        this.name = name;
    }

    public Rock(@Nonnull ResourceLocation name, @Nonnull ResourceLocation categoryName)
    {
        setRegistryName(name);
        this.name = name;
        this.rockCategory = TFCRegistries.getRockCategories().getValue(categoryName);
        if (rockCategory == null)
            throw new IllegalStateException("Rock category '" + categoryName.toString() + "' is not allowed to be null");
    }

    @Nonnull
    public String name()
    {
        return name.getPath();
    }

    public int getId()
    {
        return ((ForgeRegistry<Rock>) TFCRegistries.getRocks()).getID(this);
    }

    public RockCategory getRockCategory()
    {
        return rockCategory;
    }
}
