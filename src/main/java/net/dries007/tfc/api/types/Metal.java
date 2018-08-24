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
    public final float specificHeat;
    public final int meltTemp;
    public final boolean usable;
    public final int color;

    private final Item.ToolMaterial toolMetal;
    private final ResourceLocation name;

    /**
     * This is a registry object that will create a number of things.
     *
     * Use the provided Builder to create your own metals
     *
     * @param name        the registry name of the object. The path must also be unique
     * @param tier        the tier of the metal
     * @param usable      is the metal usable to create basic metal items? (not tools)
     * @param sh          specific heat capacity. Higher = harder to heat up / cool down. Most IRL metals are between 0.3 - 0.7
     * @param melt        melting point. See @link Heat for temperature scale. Similar to IRL melting point in celcius.
     * @param color       color of the metal when in fluid form. Used to autogenerate a fluid texture
     * @param toolMetal   The tool material. Null if metal is not able to create tools
     * @param alloyRecipe The alloy recipe. Null if the metal is not an alloy
     */
    public Metal(@Nonnull ResourceLocation name, Tier tier, boolean usable, float sh, int melt, int color, @Nullable Item.ToolMaterial toolMetal)
    {
        this.usable = usable;
        this.tier = tier;
        this.specificHeat = sh;
        this.meltTemp = melt;
        this.color = color;

        this.toolMetal = toolMetal;

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

    public boolean isToolMetal()
    {
        return toolMetal != null;
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
