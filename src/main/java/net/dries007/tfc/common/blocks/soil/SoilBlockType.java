/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.DryingBricksBlock;
import net.dries007.tfc.common.items.TFCItems;

public enum SoilBlockType
{
    DIRT((self, variant) -> new DirtBlock(Block.Properties.of(Material.DIRT, MaterialColor.DIRT).strength(0.5F).sound(SoundType.GRAVEL), self.transform(), variant)),
    GRASS((self, variant) -> new ConnectedGrassBlock(Block.Properties.of(Material.GRASS).randomTicks().strength(0.6F).sound(SoundType.GRASS), self.transform(), variant)),
    GRASS_PATH((self, variant) -> new PathBlock(Block.Properties.of(Material.DIRT).strength(0.65F).sound(SoundType.GRASS), self.transform(), variant)),
    CLAY((self, variant) -> new DirtBlock(Block.Properties.of(Material.DIRT, MaterialColor.DIRT).strength(0.5F).sound(SoundType.GRAVEL), self.transform(), variant)),
    CLAY_GRASS((self, variant) -> new ConnectedGrassBlock(Block.Properties.of(Material.GRASS).randomTicks().strength(0.6F).sound(SoundType.GRASS), self.transform(), variant)),
    FARMLAND((self, variant) -> new FarmlandBlock(ExtendedProperties.of(BlockBehaviour.Properties.of(Material.DIRT).strength(0.6f).sound(SoundType.GRAVEL).isViewBlocking(TFCBlocks::always).isSuffocating(TFCBlocks::always)).blockEntity(TFCBlockEntities.FARMLAND), variant)),
    ROOTED_DIRT((self, variant) -> new TFCRootedDirtBlock(Block.Properties.of(Material.DIRT, MaterialColor.DIRT).strength(0.5F).sound(SoundType.ROOTED_DIRT), self.transform(), variant)),
    MUD((self, variant) -> new MudBlock(mudProperties())),
    MUD_BRICKS((self, variant) -> new Block(mudProperties())),
    DRYING_BRICKS((self, variant) -> new DryingBricksBlock(ExtendedProperties.of(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().noOcclusion().instabreak().sound(SoundType.STEM).randomTicks()).blockEntity(TFCBlockEntities.TICK_COUNTER), variant.dried()));

    public static final SoilBlockType[] VALUES = values();

    public static SoilBlockType valueOf(int i)
    {
        return i >= 0 && i < VALUES.length ? VALUES[i] : DIRT;
    }

    public static BlockBehaviour.Properties mudProperties()
    {
        // todo: 1.19. correct sound
        return BlockBehaviour.Properties.of(Material.DIRT, MaterialColor.DIRT).strength(1.0f).sound(SoundType.SHROOMLIGHT);
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
        return switch (this)
            {
                case DIRT -> GRASS;
                case GRASS, GRASS_PATH, FARMLAND, ROOTED_DIRT, MUD, MUD_BRICKS, DRYING_BRICKS -> DIRT;
                case CLAY -> CLAY_GRASS;
                case CLAY_GRASS -> CLAY;
            };
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

        public Supplier<? extends Item> dried()
        {
            return switch (this)
            {
                case SILT -> TFCItems.SILT_MUD_BRICK;
                case LOAM -> TFCItems.LOAM_MUD_BRICK;
                case SANDY_LOAM -> TFCItems.SANDY_LOAM_MUD_BRICK;
                case SILTY_LOAM -> TFCItems.SILTY_LOAM_MUD_BRICK;
            };
        }
    }
}