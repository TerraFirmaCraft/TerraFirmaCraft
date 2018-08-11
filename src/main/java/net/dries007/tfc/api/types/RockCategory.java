/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.types;

import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * todo: document API
 */
public class RockCategory extends IForgeRegistryEntry.Impl<RockCategory>
{
    @Nonnull
    public static Collection<RockCategory> values()
    {
        return Collections.unmodifiableCollection(TFCRegistries.getRockCategories().getValuesCollection());
    }

    @Nullable
    public static RockCategory get(String name)
    {
        return values().stream().filter(x -> x.name().equals(name)).findFirst().orElse(null);
    }

    private final ResourceLocation name;

    @Nullable
    private final Item.ToolMaterial toolMaterial;

    public RockCategory(@Nonnull ResourceLocation name, @Nullable Item.ToolMaterial toolMaterial)
    {
        setRegistryName(name);
        this.name = name;
        this.toolMaterial = toolMaterial;
    }

    public String name()
    {
        return name.getResourcePath();
    }

    @Nullable
    public Item.ToolMaterial getToolMaterial()
    {
        return toolMaterial;
    }
}
