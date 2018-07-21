/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects;

import net.minecraft.util.IStringSerializable;

/**
 * Use api.types.ore instead
 */
@Deprecated
public enum OreEnum
{
    NATIVE_COPPER(Metal.COPPER),
    NATIVE_GOLD(Metal.GOLD),
    NATIVE_PLATINUM(Metal.PLATINUM),
    HEMATITE(Metal.PIG_IRON),
    NATIVE_SILVER(Metal.SILVER),
    CASSITERITE(Metal.TIN),
    GALENA(Metal.LEAD),
    BISMUTHINITE(Metal.BISMUTH),
    GARNIERITE(Metal.NICKEL),
    MALACHITE(Metal.COPPER),
    MAGNETITE(Metal.PIG_IRON),
    LIMONITE(Metal.PIG_IRON),
    SPHALERITE(Metal.ZINC),
    TETRAHEDRITE(Metal.COPPER),
    BITUMINOUS_COAL,
    LIGNITE,
    KAOLINITE,
    GYPSUM,
    SATINSPAR,
    SELENITE,
    GRAPHITE,
    KIMBERLITE,
    PETRIFIED_WOOD,
    SULFUR,
    JET,
    MICROCLINE,
    PITCHBLENDE,
    CINNABAR,
    CRYOLITE,
    SALTPETER,
    SERPENTINE,
    SYLVITE,
    BORAX,
    OLIVINE,
    LAPIS_LAZULI;

    public final boolean graded;
    public final Metal metal;

    OreEnum(Metal metal)
    {
        this.graded = true;
        this.metal = metal;
    }

    OreEnum()
    {
        this.graded = false;
        this.metal = null;
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
