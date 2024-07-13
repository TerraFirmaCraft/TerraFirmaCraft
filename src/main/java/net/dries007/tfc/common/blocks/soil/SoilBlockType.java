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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MudBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.DryingBricksBlock;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.registry.RegistrySoilVariant;

/**
 * @see RegistrySoilVariant
 */
public enum SoilBlockType
{
    DIRT((self, variant) -> new DirtBlock(Block.Properties.of().mapColor(MapColor.DIRT).strength(1.4f).sound(SoundType.GRAVEL), self.transform(), variant)),
    GRASS((self, variant) -> new ConnectedGrassBlock(Block.Properties.of().mapColor(MapColor.GRASS).randomTicks().strength(1.8f).sound(SoundType.GRASS), self.transform(), variant)),
    GRASS_PATH((self, variant) -> new PathBlock(Block.Properties.of().mapColor(MapColor.DIRT).strength(1.5f).sound(SoundType.GRASS), self.transform(), variant)),
    CLAY((self, variant) -> new DirtBlock(Block.Properties.of().mapColor(MapColor.DIRT).strength(1.5f).sound(SoundType.GRAVEL), self.transform(), variant)),
    CLAY_GRASS((self, variant) -> new ConnectedGrassBlock(Block.Properties.of().mapColor(MapColor.GRASS).randomTicks().strength(1.8f).sound(SoundType.GRASS), self.transform(), variant)),
    FARMLAND((self, variant) -> new FarmlandBlock(ExtendedProperties.of(MapColor.DIRT).strength(1.3f).sound(SoundType.GRAVEL).isViewBlocking(TFCBlocks::always).isSuffocating(TFCBlocks::always).blockEntity(TFCBlockEntities.FARMLAND), variant)),
    ROOTED_DIRT((self, variant) -> new TFCRootedDirtBlock(Block.Properties.of().mapColor(MapColor.DIRT).strength(2.0f).sound(SoundType.ROOTED_DIRT), self.transform(), variant)),
    MUD((self, variant) -> new MudBlock(BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).sound(SoundType.MUD).strength(2f).speedFactor(0.8f).isRedstoneConductor(TFCBlocks::always).isViewBlocking(TFCBlocks::always).isSuffocating(TFCBlocks::always).instrument(NoteBlockInstrument.BASEDRUM))),
    MUD_BRICKS((self, variant) -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).sound(SoundType.MUD_BRICKS).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.6f))),
    DRYING_BRICKS((self, variant) -> new DryingBricksBlock(ExtendedProperties.of(MapColor.DIRT).noCollission().noOcclusion().instabreak().sound(SoundType.STEM).randomTicks().blockEntity(TFCBlockEntities.TICK_COUNTER), variant.getDriedMudBrick())),
    MUDDY_ROOTS((self, variant) -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUDDY_MANGROVE_ROOTS).strength(4f)))
    ;

    public static final SoilBlockType[] VALUES = values();

    public static SoilBlockType valueOf(int i)
    {
        return i >= 0 && i < VALUES.length ? VALUES[i] : DIRT;
    }

    private final BiFunction<SoilBlockType, RegistrySoilVariant, Block> factory;

    SoilBlockType(BiFunction<SoilBlockType, RegistrySoilVariant, Block> factory)
    {
        this.factory = factory;
    }

    public Block create(RegistrySoilVariant variant)
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
                case GRASS, GRASS_PATH, FARMLAND, ROOTED_DIRT, MUD, MUD_BRICKS, DRYING_BRICKS, MUDDY_ROOTS -> DIRT;
                case CLAY -> CLAY_GRASS;
                case CLAY_GRASS -> CLAY;
            };
    }

    public enum Variant implements RegistrySoilVariant
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

        @Override
        public Supplier<? extends Block> getBlock(SoilBlockType type)
        {
            return TFCBlocks.SOIL.get(type).get(this);
        }

        @Override
        public Supplier<? extends Item> getDriedMudBrick()
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