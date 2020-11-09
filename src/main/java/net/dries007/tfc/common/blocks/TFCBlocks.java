/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.blocks;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.common.TFCItemGroup;
import net.dries007.tfc.common.blocks.rock.TFCOreBlock;
import net.dries007.tfc.common.blocks.soil.ConnectedGrassBlock;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.blocks.soil.TFCSandBlock;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.types.Metal;
import net.dries007.tfc.common.types.Ore;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.Wood;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.mixin.block.AbstractBlockAccessor;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.dries007.tfc.common.TFCItemGroup.*;


/**
 * Collection of all TFC blocks.
 * Unused is as the registry object fields themselves may be unused but they are required to register each item.
 * Whenever possible, avoid using hardcoded references to these, prefer tags or recipes.
 */
@SuppressWarnings("unused")
public final class TFCBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);

    // Earth

    public static final Map<SoilBlockType, Map<SoilBlockType.Variant, RegistryObject<Block>>> SOIL = Helpers.mapOfKeys(SoilBlockType.class, type ->
        Helpers.mapOfKeys(SoilBlockType.Variant.class, variant ->
            register((type.name() + "/" + variant.name()).toLowerCase(), () -> type.create(variant), EARTH)
        )
    );

    public static final RegistryObject<Block> PEAT = register("peat", () -> new Block(Properties.of(Material.DIRT, MaterialColor.TERRACOTTA_BLACK).harvestTool(ToolType.SHOVEL).sound(SoundType.GRAVEL).harvestLevel(0)), EARTH);
    public static final RegistryObject<Block> PEAT_GRASS = register("peat_grass", () -> new ConnectedGrassBlock(Properties.of(Material.GRASS).randomTicks().strength(0.6F).sound(SoundType.GRASS).harvestTool(ToolType.SHOVEL).harvestLevel(0), PEAT, null, null), EARTH);

    public static final Map<SandBlockType, RegistryObject<Block>> SAND = Helpers.mapOfKeys(SandBlockType.class, type ->
        register(("sand/" + type.name()).toLowerCase(), () -> new TFCSandBlock(type.getDustColor(), Properties.of(Material.SAND, MaterialColor.COLOR_ORANGE).strength(0.5F).sound(SoundType.SAND).harvestTool(ToolType.SHOVEL).harvestLevel(0)), EARTH)
    );

    public static final Map<GroundcoverBlockType, RegistryObject<Block>> GROUNDCOVER = Helpers.mapOfKeys(GroundcoverBlockType.class, type ->
        register(("groundcover/" + type.name()).toLowerCase(), () -> new GroundcoverBlock(type), EARTH)
    );

    // Ores

    public static final Map<Rock.Default, Map<Ore.Default, RegistryObject<Block>>> ORES = Helpers.mapOfKeys(Rock.Default.class, rock ->
        Helpers.mapOfKeys(Ore.Default.class, ore -> !ore.isGraded(), ore ->
            register(("ore/" + ore.name() + "/" + rock.name()).toLowerCase(), TFCOreBlock::new, TFCItemGroup.ORES)
        )
    );
    public static final Map<Rock.Default, Map<Ore.Default, Map<Ore.Grade, RegistryObject<Block>>>> GRADED_ORES = Helpers.mapOfKeys(Rock.Default.class, rock ->
        Helpers.mapOfKeys(Ore.Default.class, Ore.Default::isGraded, ore ->
            Helpers.mapOfKeys(Ore.Grade.class, grade ->
                register(("ore/" + grade.name() + "_" + ore.name() + "/" + rock.name()).toLowerCase(), TFCOreBlock::new, TFCItemGroup.ORES)
            )
        )
    );
    public static final Map<Ore.Default, RegistryObject<Block>> SMALL_ORES = Helpers.mapOfKeys(Ore.Default.class, Ore.Default::isGraded, type ->
        register(("ore/small_" + type.name()).toLowerCase(), () -> GroundcoverBlock.looseOre(Properties.of(Material.GRASS).strength(0.05F, 0.0F).sound(SoundType.NETHER_ORE).noOcclusion()), TFCItemGroup.ORES)
    );


    // Rock Stuff

    public static final Map<Rock.Default, Map<Rock.BlockType, RegistryObject<Block>>> ROCK_BLOCKS = Helpers.mapOfKeys(Rock.Default.class, rock ->
        Helpers.mapOfKeys(Rock.BlockType.class, type ->
            register(("rock/" + type.name() + "/" + rock.name()).toLowerCase(), () -> type.create(rock), ROCK_STUFFS)
        )
    );

    public static final Map<Rock.Default, Map<Rock.BlockType, RegistryObject<Block>>> ROCK_STAIRS = Helpers.mapOfKeys(Rock.Default.class, rock ->
        Helpers.mapOfKeys(Rock.BlockType.class, Rock.BlockType::hasVariants, type ->
            register(("rock/" + type.name() + "/" + rock.name()).toLowerCase() + "_stairs", () -> new StairsBlock(Helpers.mapSupplier(ROCK_BLOCKS.get(rock).get(type), Block::defaultBlockState), Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)), ROCK_STUFFS)
        )
    );

    public static final Map<Rock.Default, Map<Rock.BlockType, RegistryObject<Block>>> ROCK_WALLS = Helpers.mapOfKeys(Rock.Default.class, rock ->
        Helpers.mapOfKeys(Rock.BlockType.class, Rock.BlockType::hasVariants, type ->
            register(("rock/" + type.name() + "/" + rock.name()).toLowerCase() + "_wall", () -> new WallBlock(Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)), ROCK_STUFFS)
        )
    );

    public static final Map<Rock.Default, Map<Rock.BlockType, RegistryObject<Block>>> ROCK_SLABS = Helpers.mapOfKeys(Rock.Default.class, rock ->
        Helpers.mapOfKeys(Rock.BlockType.class, Rock.BlockType::hasVariants, type ->
            register(("rock/" + type.name() + "/" + rock.name()).toLowerCase() + "_slab", () -> new SlabBlock(Properties.of(Material.STONE).sound(SoundType.STONE).strength(1.5f, 10).harvestLevel(0).harvestTool(ToolType.PICKAXE)), ROCK_STUFFS)
        )
    );

    // Metals

    public static final Map<Metal.Default, Map<Metal.BlockType, RegistryObject<Block>>> METALS = Helpers.mapOfKeys(Metal.Default.class, metal ->
        Helpers.mapOfKeys(Metal.BlockType.class, type -> type.hasMetal(metal), type ->
            register(("metal/" + type.name() + "/" + metal.name()).toLowerCase(), type.create(metal), METAL)
        )
    );

    // Wood

    public static final Map<Wood.Default, Map<Wood.BlockType, RegistryObject<Block>>> WOODS = Helpers.mapOfKeys(Wood.Default.class, wood ->
        Helpers.mapOfKeys(Wood.BlockType.class, type ->
            register(type.id(wood), type.create(wood), WOOD)
        )
    );

    // Flora

    // todo: flora

    // Misc

    public static final RegistryObject<Block> THATCH = register("thatch", () -> new ThatchBlock(new ForgeBlockProperties(Properties.of(Material.REPLACEABLE_PLANT).strength(0.6F, 0.4F).noOcclusion()).flammable(50, 100)), MISC);
    public static final RegistryObject<Block> THATCH_BED = register("thatch_bed", () -> new ThatchBedBlock(Properties.of(Material.REPLACEABLE_PLANT).strength(0.6F, 0.4F)), MISC);


    // Fluids

    public static final Map<Metal.Default, RegistryObject<FlowingFluidBlock>> METAL_FLUIDS = Helpers.mapOfKeys(Metal.Default.class, metal ->
        register("fluid/metal/" + metal.name().toLowerCase(), () -> new FlowingFluidBlock(TFCFluids.METALS.get(metal).getSecond(), Properties.of(TFCMaterials.MOLTEN_METAL).noCollission().strength(100f).noDrops()))
    );

    public static final RegistryObject<FlowingFluidBlock> SALT_WATER = register("fluid/salt_water", () -> new FlowingFluidBlock(TFCFluids.SALT_WATER.getSecond(), Properties.of(TFCMaterials.SALT_WATER).noCollission().strength(100f).noDrops()));
    public static final RegistryObject<FlowingFluidBlock> SPRING_WATER = register("fluid/spring_water", () -> new FlowingFluidBlock(TFCFluids.SPRING_WATER.getSecond(), Properties.of(TFCMaterials.SPRING_WATER).noCollission().strength(100f).noDrops()));

    public static void setup()
    {
        // Edit other block properties
        if (TFCConfig.SERVER.enableSnowMovementModifier.get())
        {
            AbstractBlockAccessor snowAccess = (AbstractBlockAccessor) Blocks.SNOW;
            snowAccess.accessor$getProperties().speedFactor(0.8f);
            snowAccess.accessor$setSpeedFactor(0.8f);
        }
    }

    public static boolean always(BlockState state, IBlockReader reader, BlockPos pos)
    {
        return true;
    }

    public static boolean never(BlockState state, IBlockReader reader, BlockPos pos)
    {
        return false;
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockSupplier)
    {
        return register(name, blockSupplier, block -> null, false);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockSupplier, ItemGroup group)
    {
        return register(name, blockSupplier, block -> new BlockItem(block, new Item.Properties().tab(group)), true);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockSupplier, Item.Properties blockItemProperties)
    {
        return register(name, blockSupplier, block -> new BlockItem(block, blockItemProperties), true);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockSupplier, Function<T, ? extends BlockItem> blockItemFactory, boolean hasItemBlock)
    {
        RegistryObject<T> block = BLOCKS.register(name, blockSupplier);
        if (hasItemBlock)
        {
            TFCItems.ITEMS.register(name, () -> blockItemFactory.apply(block.get()));
        }
        return block;
    }
}