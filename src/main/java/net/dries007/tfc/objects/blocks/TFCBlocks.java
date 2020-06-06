/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks;

import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.api.Metal;
import net.dries007.tfc.api.Ore;
import net.dries007.tfc.api.Rock;
import net.dries007.tfc.objects.blocks.rock.TFCOreBlock;
import net.dries007.tfc.objects.blocks.soil.SandBlockType;
import net.dries007.tfc.objects.blocks.soil.SoilBlockType;
import net.dries007.tfc.objects.blocks.soil.TFCSandBlock;
import net.dries007.tfc.objects.items.TFCItems;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.dries007.tfc.objects.TFCItemGroup.METAL;
import static net.dries007.tfc.objects.TFCItemGroup.ROCK_BLOCKS;


public final class TFCBlocks
{
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MOD_ID);

    public static final Map<Rock.Default, Map<Rock.BlockType, RegistryObject<Block>>> ROCKS = Helpers.mapOfKeys(Rock.Default.class, rock ->
        Helpers.mapOfKeys(Rock.BlockType.class, type ->
            register(("rock/" + type.name() + "/" + rock.name()).toLowerCase(), () -> type.create(rock), ROCK_BLOCKS)
        )
    );

    public static final Map<Rock.Default, Map<Ore.Default, Map<Ore.Grade, RegistryObject<Block>>>> GRADED_ORES = Helpers.mapOfKeys(Rock.Default.class, rock ->
        Helpers.mapOfKeys(Ore.Default.class, Ore.Default::isGraded, ore ->
            Helpers.mapOfKeys(Ore.Grade.class, grade ->
                register(("ore/" + grade.name() + "_" + ore.name() + "/" + rock.name()).toLowerCase(), TFCOreBlock::new, ROCK_BLOCKS)
            )
        )
    );
    public static final Map<Rock.Default, Map<Ore.Default, RegistryObject<Block>>> ORES = Helpers.mapOfKeys(Rock.Default.class, rock ->
        Helpers.mapOfKeys(Ore.Default.class, ore -> !ore.isGraded(), ore ->
            register(("ore/" + ore.name() + "/" + rock.name()).toLowerCase(), TFCOreBlock::new, ROCK_BLOCKS)
        )
    );

    public static final Map<SandBlockType, RegistryObject<Block>> SAND = Helpers.mapOfKeys(SandBlockType.class, type ->
        register(("sand/" + type.name()).toLowerCase(), () -> new TFCSandBlock(type.getDustColor(), Block.Properties.create(Material.SAND, MaterialColor.ADOBE).hardnessAndResistance(0.5F).sound(SoundType.SAND)), ROCK_BLOCKS)
    );

    public static final RegistryObject<Block> PEAT = register("peat", () -> new Block(Block.Properties.create(Material.EARTH)), ROCK_BLOCKS);
    public static final RegistryObject<Block> PEAT_GRASS = register("peat_grass", () -> new Block(Block.Properties.create(Material.EARTH)), ROCK_BLOCKS);
    public static final Map<SoilBlockType, Map<SoilBlockType.Variant, RegistryObject<Block>>> SOIL = Helpers.mapOfKeys(SoilBlockType.class, type ->
        Helpers.mapOfKeys(SoilBlockType.Variant.class, variant ->
            register((type.name() + "/" + variant.name()).toLowerCase(), type::create, ROCK_BLOCKS)
        )
    );

    public static final Map<Metal.Default, Map<Metal.BlockType, RegistryObject<Block>>> METALS = Util.make(new EnumMap<>(Metal.Default.class), map -> {
        for (Metal.Default metal : Metal.Default.values())
        {
            Map<Metal.BlockType, RegistryObject<Block>> inner = new EnumMap<>(Metal.BlockType.class);
            for (Metal.BlockType type : Metal.BlockType.values())
            {
                if (type.hasType(metal))
                {
                    String name = ("metal/" + type.name() + "/" + metal.name()).toLowerCase();
                    RegistryObject<Block> block = BLOCKS.register(name, () -> type.create(metal));
                    TFCItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().group(METAL)));
                    inner.put(type, block);
                }
            }
            map.put(metal, inner);
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
