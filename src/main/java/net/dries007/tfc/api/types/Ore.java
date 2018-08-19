/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.types;

import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * todo: document API
 */
public class Ore extends IForgeRegistryEntry.Impl<Ore>
{
    @Nonnull
    public static Collection<Ore> values()
    {
        return Collections.unmodifiableCollection(TFCRegistries.getOres().getValuesCollection());
    }

    @Nullable
    public static Ore get(String name)
    {
        return values().stream().filter(x -> x.name().equals(name)).findFirst().orElse(null);
    }

    private final ResourceLocation name;

    public final boolean graded;
    public final Metal metal;

    public Ore(ResourceLocation name, @Nonnull ResourceLocation metalLoc)
    {
        this.name = name;
        setRegistryName(name);

        this.metal = TFCRegistries.getMetals().getValue(metalLoc);
        this.graded = metal != null;
    }

    public Ore(ResourceLocation name)
    {
        this.name = name;
        setRegistryName(name);

        this.metal = null;
        this.graded = false;
    }

    @Nonnull
    public String name()
    {
        return name.getPath();
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
