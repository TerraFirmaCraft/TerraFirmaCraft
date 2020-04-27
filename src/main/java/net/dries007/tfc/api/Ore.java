package net.dries007.tfc.api;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.util.IStringSerializable;

import net.dries007.tfc.objects.blocks.rock.GradedOreBlock;
import net.dries007.tfc.objects.blocks.rock.TFCOreBlock;


public class Ore
{
    /**
     * Default ores used for block registration calls
     * Not extensible
     *
     * @see Ore instead and register via json
     */
    public enum Default
    {
        NATIVE_COPPER(true),
        NATIVE_GOLD(true),
        HEMATITE(true),
        NATIVE_SILVER(true),
        CASSITERITE(true),
        BISMUTHINITE(true),
        GARNIERITE(true),
        MALACHITE(true),
        MAGNETITE(true),
        LIMONITE(true),
        SPHALERITE(true),
        TETRAHEDRITE(true),
        BITUMINOUS_COAL(false),
        LIGNITE(false),
        KAOLINITE(false),
        GYPSUM(false),
        GRAPHITE(false),
        SULFUR(false),
        CINNABAR(false),
        CRYOLITE(false),
        SALTPETER(false),
        SYLVITE(false),
        BORAX(false),
        LAPIS_LAZULI(false),
        HALITE(false);

        private final boolean graded;

        Default(boolean graded)
        {
            this.graded = graded;
        }

        public Block create(Rock.Default rock)
        {
            return graded ? new GradedOreBlock() : new TFCOreBlock();
        }
    }

    public enum Grade implements IStringSerializable
    {
        NORMAL, POOR, RICH;

        private static final Grade[] VALUES = values();

        @Nonnull
        public static Grade valueOf(int i)
        {
            return i < 0 || i >= VALUES.length ? NORMAL : VALUES[i];
        }

        @Override
        public String getName()
        {
            return this.name().toLowerCase();
        }
    }
}
