/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.LogBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Util;

import net.dries007.tfc.api.Tree;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.api.Ore;
import net.dries007.tfc.api.Rock;
import net.dries007.tfc.objects.blocks.soil.SandBlockType;
import net.dries007.tfc.objects.blocks.soil.SoilBlockType;
import net.dries007.tfc.objects.blocks.soil.TFCSandBlock;
import net.dries007.tfc.objects.items.TFCItems;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.dries007.tfc.objects.TFCItemGroup.ROCK_BLOCKS;
import static net.dries007.tfc.objects.TFCItemGroup.WOOD;


public final class TFCBlocks
{
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MOD_ID);

    public static final Map<Rock.Default, Map<Rock.BlockType, RegistryObject<Block>>> ROCKS = Util.make(new EnumMap<>(Rock.Default.class), map -> {
        for (Rock.Default rock : Rock.Default.values())
        {
            Map<Rock.BlockType, RegistryObject<Block>> inner = new EnumMap<>(Rock.BlockType.class);
            for (Rock.BlockType type : Rock.BlockType.values())
            {
                String name = ("rock/" + type.name() + "/" + rock.name()).toLowerCase();
                RegistryObject<Block> block = BLOCKS.register(name, () -> type.create(rock));
                TFCItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().group(ROCK_BLOCKS)));
                inner.put(type, block);
            }
            map.put(rock, inner);
        }
    });

    public static final Map<Rock.Default, Map<Ore.Default, RegistryObject<Block>>> ORES = Util.make(new EnumMap<>(Rock.Default.class), map -> {
        for (Rock.Default rock : Rock.Default.values())
        {
            Map<Ore.Default, RegistryObject<Block>> inner = new EnumMap<>(Ore.Default.class);
            for (Ore.Default type : Ore.Default.values())
            {
                String name = ("ore/" + type.name() + "/" + rock.name()).toLowerCase();
                RegistryObject<Block> block = BLOCKS.register(name, () -> type.create(rock));
                TFCItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().group(ROCK_BLOCKS)));
                inner.put(type, block);
            }
            map.put(rock, inner);
        }
    });

    public static final Map<SandBlockType, RegistryObject<Block>> SAND = Util.make(new EnumMap<>(SandBlockType.class), map -> {
        for (SandBlockType type : SandBlockType.values())
        {
            String name = ("sand/" + type.name()).toLowerCase();
            RegistryObject<Block> block = BLOCKS.register(name, () -> new TFCSandBlock(type.getDustColor(), Block.Properties.create(Material.SAND, MaterialColor.ADOBE).hardnessAndResistance(0.5F).sound(SoundType.SAND)));
            TFCItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().group(ROCK_BLOCKS)));
            map.put(type, block);
        }
    });

    public static final Map<SoilBlockType, Map<SoilBlockType.Variant, RegistryObject<Block>>> SOIL = Util.make(new EnumMap<>(SoilBlockType.class), map -> {
        for (SoilBlockType type : SoilBlockType.values())
        {
            Map<SoilBlockType.Variant, RegistryObject<Block>> inner = new EnumMap<>(SoilBlockType.Variant.class);
            for (SoilBlockType.Variant variant : SoilBlockType.Variant.values())
            {
                String name = (type.name() + "/" + variant.name()).toLowerCase();
                RegistryObject<Block> block = BLOCKS.register(name, type::create);
                TFCItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().group(ROCK_BLOCKS)));
                inner.put(variant, block);
            }
            map.put(type, inner);
        }
    });

    public static final Map<Tree.Default, RegistryObject<Block>> LOGS = Util.make(new EnumMap<>(Tree.Default.class), map -> {
        for (Tree.Default type : Tree.Default.values()) {
            String name = ("wood/log/" + type.name()).toLowerCase();
            RegistryObject<Block> block = BLOCKS.register(name, () -> new LogBlock(MaterialColor.SAND, Block.Properties.create(Material.WOOD, MaterialColor.ADOBE).hardnessAndResistance(0.5F).sound(SoundType.WOOD)));
            TFCItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().group(WOOD)));
            map.put(type, block);
        }
    });

    public static final Map<Tree.Default, RegistryObject<Block>> LEAVES = Util.make(new EnumMap<>(Tree.Default.class), map -> {
        for (Tree.Default type : Tree.Default.values()) {
            String name = ("wood/leaves/" + type.name()).toLowerCase();
            RegistryObject<Block> block = BLOCKS.register(name, () -> new LeavesBlock(Block.Properties.create(Material.LEAVES, MaterialColor.ADOBE).hardnessAndResistance(0.5F).sound(SoundType.PLANT).tickRandomly().notSolid()));
            TFCItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().group(WOOD)));
            map.put(type, block);
        }
    });
}
