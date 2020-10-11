/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.blocks.soil;

import java.util.function.BiFunction;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public enum SoilBlockType
{
    DIRT((self, variant) -> new DirtBlock(Block.Properties.of(Material.DIRT, MaterialColor.DIRT).strength(0.5F).sound(SoundType.GRAVEL), self.transform(), variant)),
    GRASS((self, variant) -> new ConnectedGrassBlock(Block.Properties.of(Material.GRASS).randomTicks().strength(0.6F).sound(SoundType.GRASS), self.transform(), variant)),
    GRASS_PATH((self, variant) -> new TFCGrassPathBlock(Block.Properties.of(Material.DIRT).strength(0.65F).sound(SoundType.GRASS), self.transform(), variant)),
    CLAY((self, variant) -> new DirtBlock(Block.Properties.of(Material.DIRT, MaterialColor.DIRT).strength(0.5F).sound(SoundType.GRAVEL), self.transform(), variant)),
    CLAY_GRASS((self, variant) -> new ConnectedGrassBlock(Block.Properties.of(Material.GRASS).randomTicks().strength(0.6F).sound(SoundType.GRASS), self.transform(), variant)),
    CLAY_GRASS_PATH((self, variant) -> new TFCGrassPathBlock(Block.Properties.of(Material.DIRT).strength(0.65F).sound(SoundType.GRASS), self.transform(), variant));

    public static final SoilBlockType[] VALUES = values();

    public static SoilBlockType valueOf(int i)
    {
        return i >= 0 && i < VALUES.length ? VALUES[i] : DIRT;
    }

    private final BiFunction<SoilBlockType, Variant, Block> factory;

    SoilBlockType(BiFunction<SoilBlockType, Variant, Block> factory)
    {
        this.factory = factory;
    }

    public Block create(Variant variant)
    {
        return factory.apply(this, variant);
    }

    /**
     * Gets the transformed state between grass and dirt variants. Used to subvert shitty compiler illegal forward reference errors.
     */
    private SoilBlockType transform()
    {
        switch (this)
        {
            case DIRT:
                return GRASS;
            case GRASS:
            case GRASS_PATH:
                return DIRT;
            case CLAY:
                return CLAY_GRASS;
            case CLAY_GRASS:
            case CLAY_GRASS_PATH:
                return CLAY;
        }
        throw new IllegalStateException("SoilBlockType." + name() + " missing from switch in SoilBlockType#transform");
    }

    public enum Variant
    {
        SILT,
        LOAM,
        SANDY_LOAM,
        SILTY_LOAM;

        private static final Variant[] VALUES = values();

        public static Variant valueOf(int i)
        {
            return i >= 0 && i < VALUES.length ? VALUES[i] : SILT;
        }
    }
}