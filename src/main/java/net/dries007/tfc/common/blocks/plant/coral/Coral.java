/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

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
        TUBE(MaterialColor.BLUE),
        BRAIN(MaterialColor.PINK),
        BUBBLE(MaterialColor.PURPLE),
        FIRE(MaterialColor.RED),
        HORN(MaterialColor.YELLOW);

        private final MaterialColor material;

        Color(MaterialColor material)
        {
            this.material = material;
        }
    }

    public enum BlockType
    {
        DEAD_CORAL((color, type) -> new TFCDeadCoralPlantBlock(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.GRAY).setRequiresTool().doesNotBlockMovement().zeroHardnessAndResistance())),
        CORAL((color, type) -> new TFCCoralPlantBlock(TFCBlocks.CORAL.get(color).get(DEAD_CORAL), AbstractBlock.Properties.create(Material.OCEAN_PLANT, color.material).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.WET_GRASS))),
        DEAD_CORAL_FAN((color, type) -> new TFCCoralFanBlock(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.GRAY).setRequiresTool().doesNotBlockMovement().zeroHardnessAndResistance())),
        CORAL_FAN((color, type) -> new TFCCoralFinBlock(TFCBlocks.CORAL.get(color).get(DEAD_CORAL_FAN), AbstractBlock.Properties.create(Material.OCEAN_PLANT, color.material).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.WET_GRASS))),
        DEAD_CORAL_WALL_FAN((color, type) -> new TFCDeadCoralWallFanBlock(AbstractBlock.Properties.create(Material.ROCK, MaterialColor.GRAY).setRequiresTool().doesNotBlockMovement().zeroHardnessAndResistance().lootFrom(TFCBlocks.CORAL.get(color).get(DEAD_CORAL_FAN)))),
        CORAL_WALL_FAN((color, type) -> new TFCCoralWallFanBlock(TFCBlocks.CORAL.get(color).get(DEAD_CORAL_WALL_FAN), AbstractBlock.Properties.create(Material.OCEAN_PLANT, color.material).doesNotBlockMovement().zeroHardnessAndResistance().sound(SoundType.WET_GRASS).lootFrom(TFCBlocks.CORAL.get(color).get(CORAL_FAN))));

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
