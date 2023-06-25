/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.coral;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import net.dries007.tfc.common.blocks.TFCBlocks;

public enum Coral
{
    TUBE(MapColor.COLOR_BLUE),
    BRAIN(MapColor.COLOR_PINK),
    BUBBLE(MapColor.COLOR_PURPLE),
    FIRE(MapColor.COLOR_RED),
    HORN(MapColor.COLOR_YELLOW);

    private final MapColor color;

    Coral(MapColor color)
    {
        this.color = color;
    }

    public enum BlockType
    {
        DEAD_CORAL((color, type) -> new TFCCoralPlantBlock(TFCCoralPlantBlock.BIG_SHAPE, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().noCollission().instabreak())),
        CORAL((color, type) -> new LivingCoralPlantBlock(TFCCoralPlantBlock.BIG_SHAPE, TFCBlocks.CORAL.get(color).get(DEAD_CORAL), BlockBehaviour.Properties.of().mapColor(color.color).noCollission().instabreak().sound(SoundType.WET_GRASS))),
        DEAD_CORAL_FAN((color, type) -> new TFCCoralPlantBlock(TFCCoralPlantBlock.SMALL_SHAPE, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().noCollission().instabreak())),
        CORAL_FAN((color, type) -> new LivingCoralPlantBlock(TFCCoralPlantBlock.SMALL_SHAPE, TFCBlocks.CORAL.get(color).get(DEAD_CORAL_FAN), BlockBehaviour.Properties.of().mapColor(color.color).noCollission().instabreak().sound(SoundType.WET_GRASS))),
        DEAD_CORAL_WALL_FAN((color, type) -> new CoralWallFanBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).requiresCorrectToolForDrops().noCollission().instabreak().lootFrom(TFCBlocks.CORAL.get(color).get(DEAD_CORAL_FAN)))),
        CORAL_WALL_FAN((color, type) -> new LivingCoralWallFanBlock(TFCBlocks.CORAL.get(color).get(DEAD_CORAL_WALL_FAN), BlockBehaviour.Properties.of().mapColor(color.color).noCollission().instabreak().sound(SoundType.WET_GRASS).lootFrom(TFCBlocks.CORAL.get(color).get(CORAL_FAN))));

        private final BiFunction<Coral, Coral.BlockType, ? extends Block> factory;
        private final BiFunction<Block, Item.Properties, ? extends BlockItem> blockItemFactory;

        BlockType(BiFunction<Coral, Coral.BlockType, ? extends Block> factory)
        {
            this(factory, BlockItem::new);
        }

        BlockType(BiFunction<Coral, Coral.BlockType, ? extends Block> factory, BiFunction<Block, Item.Properties, ? extends BlockItem> blockItemFactory)
        {
            this.factory = factory;
            this.blockItemFactory = blockItemFactory;
        }

        public boolean needsItem()
        {
            return this == DEAD_CORAL || this == CORAL;
        }

        public Supplier<Block> create(Coral color)
        {
            return () -> factory.apply(color, this);
        }

        @Nullable
        public Function<Block, BlockItem> createBlockItem(Item.Properties properties)
        {
            return needsItem() ? block -> blockItemFactory.apply(block, properties) : null;
        }
    }
}
