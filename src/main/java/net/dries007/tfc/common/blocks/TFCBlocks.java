/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
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
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCItemGroup;
import net.dries007.tfc.common.blocks.berrybush.*;
import net.dries007.tfc.common.blocks.devices.*;
import net.dries007.tfc.common.blocks.fruittree.*;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.plant.coral.Coral;
import net.dries007.tfc.common.blocks.plant.coral.TFCSeaPickleBlock;
import net.dries007.tfc.common.blocks.soil.ConnectedGrassBlock;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.tileentity.*;
import net.dries007.tfc.common.types.Metal;
import net.dries007.tfc.common.types.Ore;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.Wood;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.dries007.tfc.common.TFCItemGroup.*;
import static net.dries007.tfc.common.blocks.berrybush.AbstractBerryBushBlock.Lifecycle.*;


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

    public static final Map<SandBlockType, Map<SandstoneBlockType, RegistryObject<Block>>> SANDSTONE = Helpers.mapOfKeys(SandBlockType.class, color ->
        Helpers.mapOfKeys(SandstoneBlockType.class, type ->
            register((type.name() + "_sandstone/" + color.name()).toLowerCase(), () -> new Block(type.properties(color)), EARTH)
        )
    );

    public static final Map<SandBlockType, Map<SandstoneBlockType, DecorationBlockRegistryObject>> SANDSTONE_DECORATIONS = Helpers.mapOfKeys(SandBlockType.class, color ->
        Helpers.mapOfKeys(SandstoneBlockType.class, type -> new DecorationBlockRegistryObject(
            register((type.name() + "_sandstone/" + color.name() + "_slab").toLowerCase(), () -> new SlabBlock(type.properties(color)), EARTH),
            register((type.name() + "_sandstone/" + color.name() + "_stairs").toLowerCase(), () -> new StairsBlock(() -> SANDSTONE.get(color).get(type).get().defaultBlockState(), type.properties(color)), EARTH),
            register((type.name() + "_sandstone/" + color.name() + "_wall").toLowerCase(), () -> new WallBlock(type.properties(color)), EARTH)
        ))
    );

    public static final Map<GroundcoverBlockType, RegistryObject<Block>> GROUNDCOVER = Helpers.mapOfKeys(GroundcoverBlockType.class, type ->
        register(("groundcover/" + type.name()).toLowerCase(), () -> new GroundcoverBlock(type), block -> new BlockItem(block, new Item.Properties().tab(EARTH)), type.shouldCreateBlockItem())
    );

    public static final RegistryObject<Block> SEA_ICE = register("sea_ice", () -> new SeaIceBlock(AbstractBlock.Properties.of(Material.ICE).friction(0.98f).randomTicks().strength(0.5f).sound(SoundType.GLASS).noOcclusion().isValidSpawn(TFCBlocks::onlyPolarBears)), EARTH);
    public static final RegistryObject<SnowPileBlock> SNOW_PILE = register("snow_pile", () -> new SnowPileBlock(new ForgeBlockProperties(Properties.copy(Blocks.SNOW).harvestTool(ToolType.SHOVEL).harvestLevel(0)).tileEntity(SnowPileTileEntity::new)), EARTH);
    public static final RegistryObject<ThinSpikeBlock> ICICLE = register("icicle", () -> new ThinSpikeBlock(Properties.of(Material.ICE).noDrops().strength(0.4f).sound(SoundType.GLASS).noOcclusion()));
    public static final RegistryObject<ThinSpikeBlock> CALCITE = register("calcite", () -> new ThinSpikeBlock(Properties.of(Material.GLASS).noDrops().strength(0.2f).sound(SoundType.BONE_BLOCK)));

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

    public static final Map<Rock.Default, Map<Rock.BlockType, DecorationBlockRegistryObject>> ROCK_DECORATIONS = Helpers.mapOfKeys(Rock.Default.class, rock ->
        Helpers.mapOfKeys(Rock.BlockType.class, Rock.BlockType::hasVariants, type -> new DecorationBlockRegistryObject(
            register(("rock/" + type.name() + "/" + rock.name()).toLowerCase() + "_slab", () -> type.createSlab(rock), ROCK_STUFFS),
            register(("rock/" + type.name() + "/" + rock.name()).toLowerCase() + "_stairs", () -> type.createStairs(rock), ROCK_STUFFS),
            register(("rock/" + type.name() + "/" + rock.name()).toLowerCase() + "_wall", () -> type.createWall(rock), ROCK_STUFFS)
        ))
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

    // Flora

    public static final Map<Plant, RegistryObject<Block>> PLANTS = Helpers.mapOfKeys(Plant.class, plant ->
        register(("plant/" + plant.name()).toLowerCase(), plant::create, block -> plant.createBlockItem(block, new Item.Properties().tab(FLORA)), plant.needsItem())
    );

    public static final Map<Coral, Map<Coral.BlockType, RegistryObject<Block>>> CORAL = Helpers.mapOfKeys(Coral.class, color ->
        Helpers.mapOfKeys(Coral.BlockType.class, type ->
            register("coral/" + color.toString().toLowerCase() + "_" + type.toString().toLowerCase(), type.create(color), type.createBlockItem(new Item.Properties().tab(FLORA)), type.needsItem())
        )
    );

    public static final RegistryObject<Block> SEA_PICKLE = register("sea_pickle", () -> new TFCSeaPickleBlock(AbstractBlock.Properties.of(Material.WATER_PLANT, MaterialColor.COLOR_GREEN).lightLevel((state) -> TFCSeaPickleBlock.isDead(state) ? 0 : 3 + 3 * state.getValue(SeaPickleBlock.PICKLES)).sound(SoundType.SLIME_BLOCK).noOcclusion()), FLORA);

    public static final Map<BerryBush.Default, RegistryObject<Block>> SPREADING_CANES = Helpers.mapOfKeys(BerryBush.Default.class, BerryBush.Default::isSpreading, berry ->
        register("berry_bush/" + berry.name().toLowerCase() + "_bush_cane", () -> new SpreadingCaneBlock(new ForgeBlockProperties(Properties.of(Material.LEAVES).strength(0.6f).noOcclusion().randomTicks().sound(SoundType.SWEET_BERRY_BUSH)).tileEntity(BerryBushTileEntity::new).flammable(60, 30), berry.getBush(), TFCBlocks.SPREADING_BUSHES.get(berry)))
    );
    public static final Map<BerryBush.Default, RegistryObject<Block>> SPREADING_BUSHES = Helpers.mapOfKeys(BerryBush.Default.class, BerryBush.Default::isSpreading, berry ->
        register("berry_bush/" + berry.name().toLowerCase() + "_bush", () -> new SpreadingBushBlock(new ForgeBlockProperties(Properties.of(Material.LEAVES).strength(0.6f).noOcclusion().randomTicks().sound(SoundType.SWEET_BERRY_BUSH)).tileEntity(BerryBushTileEntity::new).flammable(60, 30), berry.getBush(), TFCBlocks.SPREADING_CANES.get(berry)), FLORA)
    );
    public static final Map<BerryBush.Default, RegistryObject<Block>> STATIONARY_BUSHES = Helpers.mapOfKeys(BerryBush.Default.class, BerryBush.Default::isStationary, berry ->
        register("berry_bush/" + berry.name().toLowerCase() + "_bush", () -> new StationaryBerryBushBlock(new ForgeBlockProperties(Properties.of(Material.LEAVES).strength(0.6f).noOcclusion().randomTicks().sound(SoundType.SWEET_BERRY_BUSH)).tileEntity(BerryBushTileEntity::new).flammable(60, 30), berry.getBush()), FLORA)
    );

    public static final Map<BerryBush.Default, RegistryObject<Block>> WATERLOGGED_BUSHES = Helpers.mapOfKeys(BerryBush.Default.class, BerryBush.Default::isWaterlogged, berry ->
        register("berry_bush/" + berry.name().toLowerCase() + "_bush", () -> new WaterloggedBerryBushBlock(new ForgeBlockProperties(Properties.of(Material.LEAVES).strength(0.6f).noOcclusion().randomTicks().sound(SoundType.SWEET_BERRY_BUSH)).tileEntity(BerryBushTileEntity::new).flammable(60, 30), berry.getBush()), FLORA)
    );

    public static final RegistryObject<Block> DEAD_BERRY_BUSH = register("berry_bush/dead_bush", () -> new DeadBerryBushBlock(new ForgeBlockProperties(Properties.of(Material.LEAVES).strength(0.6f).noOcclusion().sound(SoundType.SWEET_BERRY_BUSH).randomTicks()).tileEntity(TickCounterTileEntity::new).flammable(120, 90)));
    public static final RegistryObject<Block> DEAD_CANE = register("berry_bush/dead_cane", () -> new DeadCaneBlock(new ForgeBlockProperties(Properties.of(Material.LEAVES).strength(0.6f).noOcclusion().sound(SoundType.SWEET_BERRY_BUSH).randomTicks()).tileEntity(TickCounterTileEntity::new).flammable(120, 90)));

    public static final Map<FruitTree.Default, RegistryObject<Block>> FRUIT_TREE_LEAVES = Helpers.mapOfKeys(FruitTree.Default.class, tree ->
        register("fruit_tree/" + tree.name().toLowerCase() + "_leaves", () -> new FruitTreeLeavesBlock(new ForgeBlockProperties(Block.Properties.of(Material.LEAVES).strength(0.5F).sound(SoundType.GRASS).randomTicks().noOcclusion()).tileEntity(FruitTreeLeavesTileEntity::new).flammable(90, 60), tree.getFruitTree()), FLORA)
    );

    public static final Map<FruitTree.Default, RegistryObject<Block>> FRUIT_TREE_BRANCHES = Helpers.mapOfKeys(FruitTree.Default.class, tree ->
        register("fruit_tree/" + tree.name().toLowerCase() + "_branch", () -> new FruitTreeBranchBlock(new ForgeBlockProperties(Properties.of(Material.WOOD).sound(SoundType.SCAFFOLDING).randomTicks().strength(1.0f)).flammable(60, 30)))
    );

    public static final Map<FruitTree.Default, RegistryObject<Block>> FRUIT_TREE_GROWING_BRANCHES = Helpers.mapOfKeys(FruitTree.Default.class, tree ->
        register("fruit_tree/" + tree.name().toLowerCase() + "_growing_branch", () -> new GrowingFruitTreeBranchBlock(new ForgeBlockProperties(Properties.of(Material.WOOD).sound(SoundType.SCAFFOLDING).randomTicks().strength(1.0f)).tileEntity(TickCounterTileEntity::new).flammable(60, 30), tree.getFruitTree(), TFCBlocks.FRUIT_TREE_BRANCHES.get(tree), TFCBlocks.FRUIT_TREE_LEAVES.get(tree)))
    );

    public static final Map<FruitTree.Default, RegistryObject<Block>> FRUIT_TREE_SAPLINGS = Helpers.mapOfKeys(FruitTree.Default.class, tree ->
        register("fruit_tree/" + tree.name().toLowerCase() + "_sapling", () -> new FruitTreeSaplingBlock(new ForgeBlockProperties(Block.Properties.of(Material.PLANT).noCollission().randomTicks().strength(0).sound(SoundType.GRASS)).tileEntity(TickCounterTileEntity::new).flammable(60, 30), tree.getFruitTree(), TFCBlocks.FRUIT_TREE_GROWING_BRANCHES.get(tree)), FLORA)
    );

    private static final BerryBush BANANA = new BerryBush(BerryBush.Type.STATIONARY, TFCItems.FRUITS.get(Fruit.BANANA), 23f, 35f, 280f, 480f, new AbstractBerryBushBlock.Lifecycle[] {DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT}, 10, 8);
    public static final RegistryObject<Block> BANANA_PLANT = register("fruit_tree/banana_plant", () -> new BananaPlantBlock(new ForgeBlockProperties(Block.Properties.of(Material.LEAVES).strength(0.5F).sound(SoundType.GRASS).randomTicks().noOcclusion()).tileEntity(BerryBushTileEntity::new).flammable(60, 30), BANANA));
    public static final RegistryObject<Block> BANANA_SAPLING = register("fruit_tree/banana_sapling", () -> new BananaSaplingBlock(new ForgeBlockProperties(Block.Properties.of(Material.PLANT).noCollission().randomTicks().strength(0).sound(SoundType.GRASS)).tileEntity(TickCounterTileEntity::new).flammable(60, 30), new FruitTree(BANANA, 6), BANANA_PLANT), FLORA);

    // Decorations

    public static final RegistryObject<Block> PLAIN_ALABASTER = register("alabaster/raw/alabaster", () -> new Block(Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)), DECORATIONS);
    public static final RegistryObject<Block> PLAIN_ALABASTER_BRICKS = register("alabaster/raw/alabaster_bricks", () -> new Block(Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)), DECORATIONS);
    public static final RegistryObject<Block> PLAIN_POLISHED_ALABASTER = register("alabaster/raw/polished_alabaster", () -> new Block(Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)), DECORATIONS);

    public static final RegistryObject<Block> BURNING_LOG_PILE = register("burning_log_pile", () -> new BurningLogPileBlock(new ForgeBlockProperties(AbstractBlock.Properties.of(Material.WOOD).randomTicks().strength(0.6F).sound(SoundType.WOOD)).flammable(60, 30).tileEntity(BurningLogPileTileEntity::new)));
    public static final RegistryObject<Block> LOG_PILE = register("log_pile", () -> new LogPileBlock(new ForgeBlockProperties(AbstractBlock.Properties.of(Material.WOOD).strength(0.6F).sound(SoundType.WOOD)).flammable(60, 30).tileEntity(LogPileTileEntity::new)));

    public static final Map<DyeColor, RegistryObject<Block>> RAW_ALABASTER = Helpers.mapOfKeys(DyeColor.class, color ->
        register(("alabaster/stained/" + color.getName()).toLowerCase() + "_raw_alabaster", () -> new Block(AbstractBlock.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1.0F, 6.0F)), DECORATIONS)
    );
    public static final Map<DyeColor, RegistryObject<Block>> ALABASTER_BRICKS = Helpers.mapOfKeys(DyeColor.class, color ->
        register(("alabaster/stained/" + color.getName()).toLowerCase() + "_alabaster_bricks", () -> new Block(AbstractBlock.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)), DECORATIONS)
    );
    public static final Map<DyeColor, RegistryObject<Block>> POLISHED_ALABASTER = Helpers.mapOfKeys(DyeColor.class, color ->
        register(("alabaster/stained/" + color.getName()).toLowerCase() + "_polished_alabaster", () -> new Block(AbstractBlock.Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)), DECORATIONS)
    );

    public static final RegistryObject<Block> FIREPIT = register("firepit", () -> new FirepitBlock(new ForgeBlockProperties(Properties.of(Material.DIRT).strength(0.4F, 0.4F).sound(SoundType.NETHER_WART).noOcclusion().lightLevel(litBlockEmission(15))).tileEntity(FirepitTileEntity::new)), DECORATIONS);
    public static final RegistryObject<Block> GRILL = register("grill", () -> new GrillBlock(new ForgeBlockProperties(Properties.of(Material.DIRT).strength(0.4F, 0.4F).sound(SoundType.NETHER_WART).noOcclusion().lightLevel(litBlockEmission(15))).tileEntity(GrillTileEntity::new)), DECORATIONS);
    public static final RegistryObject<Block> POT = register("pot", () -> new PotBlock(new ForgeBlockProperties(Properties.of(Material.DIRT).strength(0.4F, 0.4F).sound(SoundType.NETHER_WART).noOcclusion().lightLevel(litBlockEmission(15))).tileEntity(PotTileEntity::new)), DECORATIONS);

    public static final RegistryObject<Block> PLACED_ITEM = register("placed_item", () -> new PlacedItemBlock(new ForgeBlockProperties(Properties.of(Material.DECORATION).instabreak().sound(SoundType.STEM).noOcclusion()).tileEntity(PlacedItemTileEntity::new)));
    public static final RegistryObject<Block> PIT_KILN = register("pit_kiln", () -> new PitKilnBlock(new ForgeBlockProperties(Properties.of(Material.GLASS).sound(SoundType.WOOD).strength(0.6f).noOcclusion()).tileEntity(PitKilnTileEntity::new)));

    public static final RegistryObject<Block> AGGREGATE = register("aggregate", () -> new GravelBlock(Properties.of(Material.SAND, MaterialColor.STONE).strength(0.6F).sound(SoundType.GRAVEL)), DECORATIONS);
    public static final RegistryObject<Block> CHARCOAL_PILE = register("charcoal_pile", () -> new CharcoalPileBlock(Properties.of(Material.DIRT, MaterialColor.COLOR_BLACK).strength(0.2F).sound(TFCSounds.CHARCOAL)));
    public static final RegistryObject<Block> FIRE_BRICKS = register("fire_bricks", () -> new Block(Properties.of(Material.STONE, MaterialColor.COLOR_RED).requiresCorrectToolForDrops().strength(2.0F, 6.0F)), DECORATIONS);

    public static final RegistryObject<Block> FIRE_CLAY_BLOCK = register("fire_clay_block", () -> new Block(Properties.of(Material.CLAY).strength(0.6F).sound(SoundType.GRAVEL)), DECORATIONS);
    public static final RegistryObject<Block> THATCH = register("thatch", () -> new ThatchBlock(new ForgeBlockProperties(Properties.of(Material.PLANT).strength(0.6F, 0.4F).noOcclusion().sound(SoundType.GRASS)).flammable(50, 100)), DECORATIONS);
    public static final RegistryObject<Block> THATCH_BED = register("thatch_bed", () -> new ThatchBedBlock(Properties.of(Material.REPLACEABLE_PLANT).strength(0.6F, 0.4F)), DECORATIONS);

    public static final RegistryObject<Block> TORCH = register("torch", () -> new TFCTorchBlock(new ForgeBlockProperties(AbstractBlock.Properties.of(Material.DECORATION).noCollission().instabreak().randomTicks().lightLevel(state -> 14).sound(SoundType.WOOD)).tileEntity(TickCounterTileEntity::new), ParticleTypes.FLAME));
    public static final RegistryObject<Block> WALL_TORCH = register("wall_torch", () -> new TFCWallTorchBlock(new ForgeBlockProperties(AbstractBlock.Properties.of(Material.DECORATION).noCollission().instabreak().randomTicks().lightLevel(state -> 14).sound(SoundType.WOOD)).tileEntity(TickCounterTileEntity::new), ParticleTypes.FLAME));
    public static final RegistryObject<Block> DEAD_TORCH = register("dead_torch", () -> new DeadTorchBlock(AbstractBlock.Properties.of(Material.DECORATION).noCollission().instabreak().sound(SoundType.WOOD), ParticleTypes.FLAME));
    public static final RegistryObject<Block> DEAD_WALL_TORCH = register("dead_wall_torch", () -> new DeadWallTorchBlock(AbstractBlock.Properties.of(Material.DECORATION).noCollission().instabreak().sound(SoundType.WOOD), ParticleTypes.FLAME));

    // Fluids

    public static final Map<Metal.Default, RegistryObject<FlowingFluidBlock>> METAL_FLUIDS = Helpers.mapOfKeys(Metal.Default.class, metal ->
        register("fluid/metal/" + metal.name().toLowerCase(), () -> new FlowingFluidBlock(TFCFluids.METALS.get(metal).getSecond(), Properties.of(TFCMaterials.MOLTEN_METAL).noCollission().strength(100f).noDrops()))
    );
    public static final RegistryObject<FlowingFluidBlock> SALT_WATER = register("fluid/salt_water", () -> new FlowingFluidBlock(TFCFluids.SALT_WATER.getSecond(), Properties.of(TFCMaterials.SALT_WATER).noCollission().strength(100f).noDrops()));
    public static final RegistryObject<FlowingFluidBlock> SPRING_WATER = register("fluid/spring_water", () -> new HotWaterBlock(TFCFluids.SPRING_WATER.getSecond(), Properties.of(TFCMaterials.SPRING_WATER).noCollission().strength(100f).noDrops()));

    public static boolean always(BlockState state, IBlockReader world, BlockPos pos)
    {
        return true;
    }

    public static boolean never(BlockState state, IBlockReader world, BlockPos pos)
    {
        return false;
    }

    public static boolean onlyPolarBears(BlockState state, IBlockReader world, BlockPos pos, EntityType<?> type)
    {
        return type == EntityType.POLAR_BEAR; // todo: does this need to be expanded?
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