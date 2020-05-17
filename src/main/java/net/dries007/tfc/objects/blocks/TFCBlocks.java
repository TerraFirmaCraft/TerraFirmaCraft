/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Util;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.api.Ore;
import net.dries007.tfc.api.Rock;
import net.dries007.tfc.objects.blocks.rock.TFCOreBlock;
import net.dries007.tfc.objects.blocks.soil.SandBlockType;
import net.dries007.tfc.objects.blocks.soil.SoilBlockType;
import net.dries007.tfc.objects.blocks.soil.TFCSandBlock;
import net.dries007.tfc.objects.items.TFCItems;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.dries007.tfc.objects.TFCItemGroup.ROCK_BLOCKS;


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

    public static final Map<Rock.Default, Map<Ore.Default, Map<Ore.Grade, RegistryObject<Block>>>> GRADED_ORES = new EnumMap<>(Rock.Default.class);
    public static final Map<Rock.Default, Map<Ore.Default, RegistryObject<Block>>> ORES = Util.make(new EnumMap<>(Rock.Default.class), map -> {
        for (Rock.Default rock : Rock.Default.values())
        {
            Map<Ore.Default, RegistryObject<Block>> inner = new EnumMap<>(Ore.Default.class);
            Map<Ore.Default, Map<Ore.Grade, RegistryObject<Block>>> innerGraded = new EnumMap<>(Ore.Default.class);
            for (Ore.Default type : Ore.Default.values())
            {
                if (type.isGraded())
                {
                    Map<Ore.Grade, RegistryObject<Block>> innerInnerGraded = new EnumMap<>(Ore.Grade.class);
                    for (Ore.Grade grade : Ore.Grade.values())
                    {
                        String name = ("ore/" + grade.name() + "_" + type.name() + "/" + rock.name()).toLowerCase();
                        RegistryObject<Block> block = BLOCKS.register(name, TFCOreBlock::new);
                        TFCItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().group(ROCK_BLOCKS)));
                        innerInnerGraded.put(grade, block);
                    }
                    innerGraded.put(type, innerInnerGraded);
                }
                else
                {
                    String name = ("ore/" + type.name() + "/" + rock.name()).toLowerCase();
                    RegistryObject<Block> block = BLOCKS.register(name, TFCOreBlock::new);
                    TFCItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().group(ROCK_BLOCKS)));
                    inner.put(type, block);
                }
            }
            map.put(rock, inner);
            GRADED_ORES.put(rock, innerGraded);
        }
    });

    public static final Map<SandBlockType, RegistryObject<Block>> SAND = Util.make(new EnumMap<>(SandBlockType.class), map -> {
        for (SandBlockType type : SandBlockType.values())
        {
            map.put(type, register(("sand/" + type.name()).toLowerCase(), () -> new TFCSandBlock(type.getDustColor(), Block.Properties.create(Material.SAND, MaterialColor.ADOBE).hardnessAndResistance(0.5F).sound(SoundType.SAND)), ROCK_BLOCKS));
        }
    });

    public static final RegistryObject<Block> PEAT = register("peat", () -> new Block(Block.Properties.create(Material.EARTH)), ROCK_BLOCKS);
    public static final RegistryObject<Block> PEAT_GRASS = register("peat_grass", () -> new Block(Block.Properties.create(Material.EARTH)), ROCK_BLOCKS);
    public static final Map<SoilBlockType, Map<SoilBlockType.Variant, RegistryObject<Block>>> SOIL = Util.make(new EnumMap<>(SoilBlockType.class), map -> {
        for (SoilBlockType type : SoilBlockType.values())
        {
            Map<SoilBlockType.Variant, RegistryObject<Block>> inner = new EnumMap<>(SoilBlockType.Variant.class);
            for (SoilBlockType.Variant variant : SoilBlockType.Variant.values())
            {
                inner.put(variant, register((type.name() + "/" + variant.name()).toLowerCase(), type::create, ROCK_BLOCKS));
            }
            map.put(type, inner);
        }
    });

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockSupplier, ItemGroup group)
    {
        return register(name, blockSupplier, new Item.Properties().group(group));
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockSupplier, Item.Properties blockItemProperties)
    {
        RegistryObject<T> block = BLOCKS.register(name, blockSupplier);
        TFCItems.ITEMS.register(name, () -> new BlockItem(block.get(), blockItemProperties));
        return block;
    }
}
