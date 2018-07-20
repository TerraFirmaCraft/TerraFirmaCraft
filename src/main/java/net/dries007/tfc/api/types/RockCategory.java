/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.types;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * todo: document API
 */
public class RockCategory extends IForgeRegistryEntry.Impl<RockCategory>
{
    @Nullable
    private final Item.ToolMaterial toolMaterial;

    public RockCategory(ResourceLocation name, @Nullable Item.ToolMaterial toolMaterial)
    {
        setRegistryName(name);
        this.toolMaterial = toolMaterial;
    }

    @Nullable
    public Item.ToolMaterial getToolMaterial()
    {
        return toolMaterial;
    }
}
