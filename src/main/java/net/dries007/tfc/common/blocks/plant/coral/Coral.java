package net.dries007.tfc.common.blocks.plant.coral;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

import net.dries007.tfc.common.blocks.TFCBlocks;

public class Coral
{
    public enum Color
    {
        TUBE(MaterialColor.COLOR_BLUE),
        BRAIN(MaterialColor.COLOR_PINK),
        BUBBLE(MaterialColor.COLOR_PURPLE),
        FIRE(MaterialColor.COLOR_RED),
        HORN(MaterialColor.COLOR_YELLOW);

        private final MaterialColor material;

        Color(MaterialColor material)
        {
            this.material = material;
        }
    }

    public enum BlockType
    {
        DEAD_CORAL((color, type) -> new TFCDeadCoralPlantBlock(AbstractBlock.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY).requiresCorrectToolForDrops().noCollission().instabreak())),
        CORAL((color, type) -> new TFCCoralPlantBlock(TFCBlocks.CORAL.get(color).get(DEAD_CORAL), AbstractBlock.Properties.of(Material.WATER_PLANT, color.material).noCollission().instabreak().sound(SoundType.WET_GRASS))),
        DEAD_CORAL_FAN((color, type) -> new TFCCoralFanBlock(AbstractBlock.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY).requiresCorrectToolForDrops().noCollission().instabreak())),
        CORAL_FAN((color, type) -> new TFCCoralFinBlock(TFCBlocks.CORAL.get(color).get(DEAD_CORAL_FAN), AbstractBlock.Properties.of(Material.WATER_PLANT, color.material).noCollission().instabreak().sound(SoundType.WET_GRASS))),
        DEAD_CORAL_WALL_FAN((color, type) -> new TFCDeadCoralWallFanBlock(AbstractBlock.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY).requiresCorrectToolForDrops().noCollission().instabreak().dropsLike(TFCBlocks.CORAL.get(color).get(DEAD_CORAL_FAN).get()))),
        CORAL_WALL_FAN((color, type) -> new TFCCoralWallFanBlock(TFCBlocks.CORAL.get(color).get(DEAD_CORAL_WALL_FAN), AbstractBlock.Properties.of(Material.WATER_PLANT, color.material).noCollission().instabreak().sound(SoundType.WET_GRASS).dropsLike(TFCBlocks.CORAL.get(color).get(CORAL_FAN).get())));

        public boolean needsItem()
        {
            return this == DEAD_CORAL || this == CORAL;
        }

        private final BiFunction<Color, Coral.BlockType, ? extends Block> factory;
        private final BiFunction<Block, Item.Properties, ? extends BlockItem> blockItemFactory;

        BlockType(BiFunction<Color, Coral.BlockType, ? extends Block> factory)
        {
            this(factory, BlockItem::new);
        }

        BlockType(BiFunction<Color, Coral.BlockType, ? extends Block> factory, BiFunction<Block, Item.Properties, ? extends BlockItem> blockItemFactory)
        {
            this.factory = factory;
            this.blockItemFactory = blockItemFactory;
        }

        public Supplier<Block> create(Color color)
        {
            return () -> factory.apply(color, this);
        }

        public BlockItem createBlockItem(Block block, Item.Properties properties)
        {
            return this.blockItemFactory.apply(block, properties);
        }
    }
}
