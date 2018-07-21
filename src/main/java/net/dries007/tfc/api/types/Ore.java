/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.types;

import javax.annotation.Nullable;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import net.dries007.tfc.objects.Metal;

/**
 * todo: document API
 */
public class Ore extends IForgeRegistryEntry.Impl<Ore>
{

    public final boolean graded;
    public final Metal metal;
    public final String name;

    public Ore(ResourceLocation name, @Nullable Metal metal)
    {
        this.graded = (metal != null);
        this.metal = metal;

        this.name = name.getResourcePath();
        setRegistryName(name);
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
