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

import net.dries007.tfc.api.registries.TFCRegistries;

/**
 * todo: document API
 */
public class Ore extends IForgeRegistryEntry.Impl<Ore>
{
    private final boolean graded;
    private final Metal metal;

    public Ore(ResourceLocation name, @Nullable Metal metal)
    {
        this.graded = (metal != null);
        this.metal = metal;
        setRegistryName(name);
    }

    public Ore(ResourceLocation name, @Nonnull ResourceLocation metal)
    {
        this(name, TFCRegistries.METALS.getValue(metal));
    }

    public Ore(ResourceLocation name)
    {
        this(name, (Metal) null);
    }

    public boolean isGraded()
    {
        return graded;
    }

    public Metal getMetal()
    {
        return metal;
    }

    @Override
    public String toString()
    {
        return getRegistryName().getPath();
    }

    public enum Grade implements IStringSerializable
    {
        NORMAL(25), POOR(15), RICH(35);

        public static Grade byMetadata(int meta)
        {
            return Grade.values()[meta];
        }

        public final int smeltAmount;

        Grade(int smeltAmount)
        {
            this.smeltAmount = smeltAmount;
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