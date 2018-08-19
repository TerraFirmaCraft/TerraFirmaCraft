/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
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
public class Metal extends IForgeRegistryEntry.Impl<Metal>
{
    @Nonnull
    public static Collection<Metal> values()
    {
        return Collections.unmodifiableCollection(TFCRegistries.getMetals().getValuesCollection());
    }

    @Nullable
    public static Metal get(String name)
    {
        return values().stream().filter(x -> x.name().equals(name)).findFirst().orElse(null);
    }

    public final Tier tier;
    public final double specificHeat;
    public final int meltTemp;
    public final Item.ToolMaterial toolMetal;
    public final boolean usable;
    public final int color;

    private final ResourceLocation name;

    public Metal(ResourceLocation name, Tier tier, double sh, int melt, int color)
    {
        this(name, tier, true, sh, melt, color, null);
    }

    public Metal(ResourceLocation name, Tier tier, double sh, int melt, int color, Item.ToolMaterial toolMetal)
    {
        this(name, tier, true, sh, melt, color, toolMetal);
    }

    public Metal(ResourceLocation name, Tier tier, boolean usable, double sh, int melt, int color)
    {
        this(name, tier, usable, sh, melt, color, null);
    }

    public Metal(ResourceLocation name, Tier tier, boolean usable, double sh, int melt, int color, Item.ToolMaterial toolMetal)
    {
        this.usable = usable;
        this.tier = tier;
        this.specificHeat = sh;
        this.meltTemp = melt;
        this.toolMetal = toolMetal;
        this.color = color;

        this.name = name;
        setRegistryName(name);
    }

    public String name()
    {
        return name.getPath();
    }

    @Nullable
    public Item.ToolMaterial getToolMetal()
    {
        return toolMetal;
    }

    public enum Tier
    {
        TIER_I,
        TIER_II, // Not implemented, but presumed to be a more advanced, more capable version of the pit kiln.
        TIER_III,
        TIER_IV,
        TIER_V
    }

}
