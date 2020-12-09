package net.dries007.tfc.common.blocks.plant.coral;

import java.util.function.Supplier;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.common.util.NonNullFunction;

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
        DEAD_CORAL(color -> new TFCDeadCoralPlantBlock(AbstractBlock.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY).requiresCorrectToolForDrops().noCollission().instabreak())),
        CORAL(color -> new TFCCoralPlantBlock(TFCBlocks.CORAL.get(color).get(DEAD_CORAL), AbstractBlock.Properties.of(Material.WATER_PLANT, color.material).noCollission().instabreak().sound(SoundType.WET_GRASS))),
        DEAD_CORAL_FAN(color -> new TFCCoralFanBlock(AbstractBlock.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY).requiresCorrectToolForDrops().noCollission().instabreak())),
        CORAL_FAN(color -> new TFCCoralFinBlock(TFCBlocks.CORAL.get(color).get(DEAD_CORAL_FAN), AbstractBlock.Properties.of(Material.WATER_PLANT, color.material).noCollission().instabreak().sound(SoundType.WET_GRASS))),
        DEAD_CORAL_WALL_FAN(color -> new TFCDeadCoralWallFanBlock(AbstractBlock.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY).requiresCorrectToolForDrops().noCollission().instabreak())),
        CORAL_WALL_FAN(color -> new TFCCoralWallFanBlock(TFCBlocks.CORAL.get(color).get(DEAD_CORAL_WALL_FAN), AbstractBlock.Properties.of(Material.WATER_PLANT, color.material).noCollission().instabreak().sound(SoundType.WET_GRASS)));

        private final NonNullFunction<Coral.Color, Block> blockFactory;

        BlockType(NonNullFunction<Coral.Color, Block> blockFactory)
        {
            this.blockFactory = blockFactory;
        }

        public Supplier<Block> create(Color color)
        {
            return () -> blockFactory.apply(color);
        }
    }
}
