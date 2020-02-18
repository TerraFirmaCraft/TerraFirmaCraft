package net.dries007.tfc.objects.blocks.soil;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public enum SoilBlockType
{
    DIRT,
    GRASS,
    DRY_GRASS,
    GRASS_PATH;

    public Block create()
    {
        switch (this)
        {
            case DIRT:
                return new TFCDirtBlock(Block.Properties.create(Material.EARTH, MaterialColor.DIRT).hardnessAndResistance(0.5F).sound(SoundType.GROUND));
            case GRASS:
            case DRY_GRASS:
                return new TFCGrassBlock(Block.Properties.create(Material.ORGANIC).tickRandomly().hardnessAndResistance(0.6F).sound(SoundType.PLANT));
            case GRASS_PATH:
                return new TFCGrassPathBlock(Block.Properties.create(Material.EARTH).hardnessAndResistance(0.65F).sound(SoundType.PLANT));
        }
        throw new IllegalArgumentException("Unknown block type");
    }

    public enum Variant
    {
        SILTY,
        SANDY,
        LOAMY,
        CLAY, // These two are special
        PEAT;

        public static final int TOTAL = values().length;

        private static final Variant[] VALUES = values();

        @Nonnull
        public static Variant valueOf(int i)
        {
            return i >= 0 && i < TOTAL ? VALUES[i] : SILTY;
        }
    }
}
