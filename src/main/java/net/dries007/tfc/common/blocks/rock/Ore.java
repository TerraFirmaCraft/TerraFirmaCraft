/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import net.dries007.tfc.common.blocks.ExtendedBlock;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.registry.RegistryRock;

/**
 * Default ores used for block registration calls
 */
public enum Ore
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
    GYPSUM(false),
    GRAPHITE(false),
    SULFUR(false),
    CINNABAR(false),
    CRYOLITE(false),
    SALTPETER(false),
    SYLVITE(false),
    BORAX(false),
    HALITE(false),
    AMETHYST(false),
    DIAMOND(false),
    EMERALD(false),
    LAPIS_LAZULI(false),
    OPAL(false),
    PYRITE(false),
    RUBY(false),
    SAPPHIRE(false),
    TOPAZ(false);

    private final boolean graded;

    Ore(boolean graded)
    {
        this.graded = graded;
    }

    public boolean isGraded()
    {
        return graded;
    }

    public Metal metal()
    {
        return switch(this)
        {
            case NATIVE_COPPER, MALACHITE, TETRAHEDRITE -> Metal.COPPER;
            case NATIVE_GOLD -> Metal.GOLD;
            case HEMATITE, MAGNETITE, LIMONITE -> Metal.CAST_IRON;
            case NATIVE_SILVER -> Metal.SILVER;
            case CASSITERITE -> Metal.TIN;
            case BISMUTHINITE -> Metal.BISMUTH;
            case GARNIERITE -> Metal.NICKEL;
            case SPHALERITE -> Metal.ZINC;
            default -> throw new IllegalStateException("No metal for ore " + this);
        };
    }

    public Block create(RegistryRock rock)
    {
        // Same hardness as raw rock
        final BlockBehaviour.Properties properties = Block.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).strength(rock.category().hardness(6.5f), 10).requiresCorrectToolForDrops();
        if (this == LIGNITE || this == BITUMINOUS_COAL)
        {
            return new ExtendedBlock(ExtendedProperties.of(properties).flammable(5, 120));
        }
        return new Block(properties);
    }

    public enum Grade
    {
        POOR, NORMAL, RICH;

        private static final Grade[] VALUES = values();

        public static Grade valueOf(int i)
        {
            return i < 0 || i >= VALUES.length ? NORMAL : VALUES[i];
        }
    }
}
