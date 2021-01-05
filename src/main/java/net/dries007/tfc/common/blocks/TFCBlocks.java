/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.blocks;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import net.minecraft.block.*;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.common.TFCItemGroup;
import net.dries007.tfc.common.blocks.devices.FirepitBlock;
import net.dries007.tfc.common.blocks.devices.GrillBlock;
import net.dries007.tfc.common.blocks.devices.PotBlock;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.soil.ConnectedGrassBlock;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.tileentity.FirepitTileEntity;
import net.dries007.tfc.common.tileentity.GrillTileEntity;
import net.dries007.tfc.common.tileentity.PotTileEntity;
import net.dries007.tfc.common.tileentity.SnowPileTileEntity;
import net.dries007.tfc.common.types.Metal;
import net.dries007.tfc.common.types.Ore;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.Wood;
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
        register(("sand/" + type.name()).toLowerCase(), type::create, EARTH)
    );

    public static final Map<GroundcoverBlockType, RegistryObject<Block>> GROUNDCOVER = Helpers.mapOfKeys(GroundcoverBlockType.class, type ->
        register(("groundcover/" + type.name()).toLowerCase(), () -> new GroundcoverBlock(type), block -> new BlockItem(block, new Item.Properties().tab(EARTH)), type.shouldCreateBlockItem())
    );

    // Ores

    public static final Map<Rock.Default, Map<Ore.Default, RegistryObject<Block>>> ORES = Helpers.mapOfKeys(Rock.Default.class, rock ->
        Helpers.mapOfKeys(Ore.Default.class, ore -> !ore.isGraded(), ore ->
            register(("ore/" + ore.name() + "/" + rock.name()).toLowerCase(), () -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(3, 10).harvestTool(ToolType.PICKAXE).harvestLevel(0)), TFCItemGroup.ORES)
        )
    );
    public static final Map<Rock.Default, Map<Ore.Default, Map<Ore.Grade, RegistryObject<Block>>>> GRADED_ORES = Helpers.mapOfKeys(Rock.Default.class, rock ->
        Helpers.mapOfKeys(Ore.Default.class, Ore.Default::isGraded, ore ->
            Helpers.mapOfKeys(Ore.Grade.class, grade ->
                register(("ore/" + grade.name() + "_" + ore.name() + "/" + rock.name()).toLowerCase(), () -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(3, 10).harvestTool(ToolType.PICKAXE).harvestLevel(0)), TFCItemGroup.ORES)
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

    public static final Map<Rock.Default, Map<Rock.BlockType, RegistryObject<SlabBlock>>> ROCK_SLABS = Helpers.mapOfKeys(Rock.Default.class, rock ->
        Helpers.mapOfKeys(Rock.BlockType.class, Rock.BlockType::hasVariants, type ->
            register(("rock/" + type.name() + "/" + rock.name()).toLowerCase() + "_slab", () -> type.createSlab(rock), ROCK_STUFFS)
        )
    );

    public static final Map<Rock.Default, Map<Rock.BlockType, RegistryObject<StairsBlock>>> ROCK_STAIRS = Helpers.mapOfKeys(Rock.Default.class, rock ->
        Helpers.mapOfKeys(Rock.BlockType.class, Rock.BlockType::hasVariants, type ->
            register(("rock/" + type.name() + "/" + rock.name()).toLowerCase() + "_stairs", () -> type.createStairs(rock), ROCK_STUFFS)
        )
    );

    public static final Map<Rock.Default, Map<Rock.BlockType, RegistryObject<Block>>> ROCK_WALLS = Helpers.mapOfKeys(Rock.Default.class, rock ->
        Helpers.mapOfKeys(Rock.BlockType.class, Rock.BlockType::hasVariants, type ->
            register(("rock/" + type.name() + "/" + rock.name()).toLowerCase() + "_wall", () -> type.createWall(rock), ROCK_STUFFS)
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
            register(type.nameFor(wood), type.create(wood), WOOD)
        )
    );

    // Devices

    public static final RegistryObject<Block> FIREPIT = register("firepit", () -> new FirepitBlock(new ForgeBlockProperties(Properties.of(Material.DIRT).strength(0.4F, 0.4F).sound(SoundType.NETHER_WART).noOcclusion().lightLevel(litBlockEmission(15))).tileEntity(FirepitTileEntity::new)), DECORATIONS);
    public static final RegistryObject<Block> GRILL = register("grill", () -> new GrillBlock(new ForgeBlockProperties(Properties.of(Material.DIRT).strength(0.4F, 0.4F).sound(SoundType.NETHER_WART).noOcclusion().lightLevel(litBlockEmission(15))).tileEntity(GrillTileEntity::new)), DECORATIONS);
    public static final RegistryObject<Block> POT = register("pot", () -> new PotBlock(new ForgeBlockProperties(Properties.of(Material.DIRT).strength(0.4F, 0.4F).sound(SoundType.NETHER_WART).noOcclusion().lightLevel(litBlockEmission(15))).tileEntity(PotTileEntity::new)), DECORATIONS);

    // Flora

    public static final Map<Plant, RegistryObject<Block>> PLANTS = Helpers.mapOfKeys(Plant.class, plant ->
        register(("plant/" + plant.name()).toLowerCase(), plant::create, block -> plant.createBlockItem(block, new Item.Properties().tab(FLORA)), true)
    );

    // Alabaster

    public static final RegistryObject<Block> PLAIN_ALABASTER = register("alabaster/raw/alabaster", () -> new Block(Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)), DECORATIONS);
    public static final RegistryObject<Block> PLAIN_ALABASTER_BRICKS = register("alabaster/raw/alabaster_bricks", () -> new Block(Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)), DECORATIONS);
    public static final RegistryObject<Block> PLAIN_POLISHED_ALABASTER = register("alabaster/raw/polished_alabaster", () -> new Block(Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)), DECORATIONS);

    public static final Map<DyeColor, RegistryObject<Block>> RAW_ALABASTER = Helpers.mapOfKeys(DyeColor.class, color ->
        register(("alabaster/stained/" + color.getName()).toLowerCase() + "_raw_alabaster", () -> new Block(AbstractBlock.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1.0F, 6.0F)), DECORATIONS)
    );

    public static final Map<DyeColor, RegistryObject<Block>> ALABASTER_BRICKS = Helpers.mapOfKeys(DyeColor.class, color ->
        register(("alabaster/stained/" + color.getName()).toLowerCase() + "_alabaster_bricks", () -> new Block(AbstractBlock.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)), DECORATIONS)
    );

    public static final Map<DyeColor, RegistryObject<Block>> POLISHED_ALABASTER = Helpers.mapOfKeys(DyeColor.class, color ->
        register(("alabaster/stained/" + color.getName()).toLowerCase() + "_polished_alabaster", () -> new Block(AbstractBlock.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)), DECORATIONS)
    );

    // Misc

    public static final RegistryObject<Block> AGGREGATE = register("aggregate", () -> new GravelBlock(Properties.of(Material.SAND, MaterialColor.STONE).strength(0.6F).sound(SoundType.GRAVEL)), DECORATIONS);
    public static final RegistryObject<CalciteBlock> CALCITE = register("calcite", () -> new CalciteBlock(Properties.of(Material.GLASS).noDrops().instabreak().sound(SoundType.GLASS)), DECORATIONS);
    public static final RegistryObject<Block> FIRE_BRICKS = register("fire_bricks", () -> new Block(Properties.of(Material.STONE, MaterialColor.COLOR_RED).requiresCorrectToolForDrops().strength(2.0F, 6.0F)), DECORATIONS);
    public static final RegistryObject<Block> FIRE_CLAY_BLOCK = register("fire_clay_block", () -> new Block(Properties.of(Material.CLAY).strength(0.6F).sound(SoundType.GRAVEL)), DECORATIONS);
    public static final RegistryObject<SnowPileBlock> SNOW_PILE = register("snow_pile", () -> new SnowPileBlock(new ForgeBlockProperties(Properties.copy(Blocks.SNOW)).tileEntity(SnowPileTileEntity::new)), DECORATIONS);
    public static final RegistryObject<Block> THATCH = register("thatch", () -> new ThatchBlock(new ForgeBlockProperties(Properties.of(Material.PLANT).strength(0.6F, 0.4F).noOcclusion().sound(SoundType.GRASS)).flammable(50, 100)), DECORATIONS);
    public static final RegistryObject<Block> THATCH_BED = register("thatch_bed", () -> new ThatchBedBlock(Properties.of(Material.REPLACEABLE_PLANT).strength(0.6F, 0.4F)), DECORATIONS);

    // Fluids

    public static final Map<Metal.Default, RegistryObject<FlowingFluidBlock>> METAL_FLUIDS = Helpers.mapOfKeys(Metal.Default.class, metal ->
        register("fluid/metal/" + metal.name().toLowerCase(), () -> new FlowingFluidBlock(TFCFluids.METALS.get(metal).getSecond(), Properties.of(TFCMaterials.MOLTEN_METAL).noCollission().strength(100f).noDrops()))
    );

    public static final RegistryObject<FlowingFluidBlock> SALT_WATER = register("fluid/salt_water", () -> new FlowingFluidBlock(TFCFluids.SALT_WATER.getSecond(), Properties.of(TFCMaterials.SALT_WATER).noCollission().strength(100f).noDrops()));
    public static final RegistryObject<FlowingFluidBlock> SPRING_WATER = register("fluid/spring_water", () -> new FlowingFluidBlock(TFCFluids.SPRING_WATER.getSecond(), Properties.of(TFCMaterials.SPRING_WATER).noCollission().strength(100f).noDrops()));

    public static boolean always(BlockState state, IBlockReader reader, BlockPos pos)
    {
        return true;
    }

    public static boolean never(BlockState state, IBlockReader reader, BlockPos pos)
    {
        return false;
    }

    private static ToIntFunction<BlockState> litBlockEmission(int lightValue)
    {
        return (state) -> state.getValue(TFCBlockStateProperties.LIT) ? lightValue : 0;
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