/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.types;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.registries.TFCRegistries;

/**
 * todo: document API
 */
public class Ore extends IForgeRegistryEntry.Impl<Ore>
{
    private final boolean graded;
    private final Metal metal;
    private final boolean canMelt;

    /**
     * Creates a registry object for an ore type
     *
     * @param name    The registry name of the ore
     * @param metal   The metal, or null if it's a non-metal ore
     * @param canMelt If the metal can be melted directly from the ore
     */
    public Ore(ResourceLocation name, @Nullable Metal metal, boolean canMelt)
    {
        this.graded = (metal != null);
        this.metal = metal;
        this.canMelt = canMelt;

        setRegistryName(name);
    }

    public Ore(ResourceLocation name, @Nonnull ResourceLocation metal, boolean canMelt)
    {
        this(name, TFCRegistries.METALS.getValue(metal), canMelt);
    }

    public Ore(ResourceLocation name, @Nonnull ResourceLocation metal)
    {
        this(name, TFCRegistries.METALS.getValue(metal), true);
    }

    public Ore(ResourceLocation name)
    {
        this(name, (Metal) null, false);
    }

    public boolean isGraded()
    {
        return graded;
    }

    @Nullable
    public Metal getMetal()
    {
        return metal;
    }

    public boolean canMelt()
    {
        return canMelt;
    }

    @Override
    public String toString()
    {
        //noinspection ConstantConditions
        return getRegistryName().getPath();
    }

    public enum Grade implements IStringSerializable
    {
        NORMAL, POOR, RICH;

        public static Grade byMetadata(int meta)
        {
            return Grade.values()[meta];
        }

        public int getSmeltAmount()
        {
            switch (this)
            {
                case POOR:
                    return ConfigTFC.GENERAL.poorOreMetalAmount;
                case RICH:
                    return ConfigTFC.GENERAL.richOreMetalAmount;
                case NORMAL:
                default:
                    return ConfigTFC.GENERAL.normalOreMetalAmount;
            }
        }

        @Override
        public String getName()
        {
            return this.name().toLowerCase();
        }

        public int getMeta()
        {
            return this.ordinal();
        }
    }
}