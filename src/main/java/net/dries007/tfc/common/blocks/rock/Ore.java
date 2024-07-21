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
    // Graded Ores (Rich / Normal / Poor)
    NATIVE_COPPER(Type.GRADED),
    NATIVE_GOLD(Type.GRADED),
    HEMATITE(Type.GRADED),
    NATIVE_SILVER(Type.GRADED),
    CASSITERITE(Type.GRADED),
    BISMUTHINITE(Type.GRADED),
    GARNIERITE(Type.GRADED),
    MALACHITE(Type.GRADED),
    MAGNETITE(Type.GRADED),
    LIMONITE(Type.GRADED),
    SPHALERITE(Type.GRADED),
    TETRAHEDRITE(Type.GRADED),

    // Normal
    BITUMINOUS_COAL(Type.NORMAL),
    LIGNITE(Type.NORMAL),
    GYPSUM(Type.NORMAL),
    CINNABAR(Type.NORMAL),
    CRYOLITE(Type.NORMAL),
    BORAX(Type.NORMAL),
    HALITE(Type.NORMAL),

    // Normal + Powder
    GRAPHITE(Type.NORMAL_WITH_POWDER),
    SALTPETER(Type.NORMAL_WITH_POWDER),
    SULFUR(Type.NORMAL_WITH_POWDER),
    SYLVITE(Type.NORMAL_WITH_POWDER),

    // Gems
    AMETHYST(Type.GEM),
    DIAMOND(Type.GEM),
    EMERALD(Type.GEM),
    LAPIS_LAZULI(Type.GEM),
    OPAL(Type.GEM),
    PYRITE(Type.GEM),
    RUBY(Type.GEM),
    SAPPHIRE(Type.GEM),
    TOPAZ(Type.GEM);

    private final Type type;

    Ore(Type type)
    {
        this.type = type;
    }

    public boolean isGraded()
    {
        return type == Type.GRADED;
    }

    public boolean isGem()
    {
        return type == Type.GEM;
    }

    public boolean hasPowder()
    {
        return type != Type.NORMAL;
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
        POOR, NORMAL, RICH
    }

    enum Type
    {
        GRADED, NORMAL, NORMAL_WITH_POWDER, GEM
    }
}
