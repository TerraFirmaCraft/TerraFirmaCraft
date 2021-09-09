/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCItemGroup;
import net.dries007.tfc.common.blocks.devices.*;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.plant.coral.Coral;
import net.dries007.tfc.common.blocks.plant.coral.TFCSeaPickleBlock;
import net.dries007.tfc.common.blocks.plant.fruit.DeadBerryBushBlock;
import net.dries007.tfc.common.blocks.plant.fruit.DeadCaneBlock;
import net.dries007.tfc.common.blocks.plant.fruit.FruitBlocks;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.rock.RockAnvilBlock;
import net.dries007.tfc.common.blocks.rock.RockCategory;
import net.dries007.tfc.common.blocks.soil.ConnectedGrassBlock;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.tileentity.*;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;
import static net.dries007.tfc.common.TFCItemGroup.*;

/**
 * Collection of all TFC blocks.
 * Organized by {@link TFCItemGroup}
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
            register((type.name() + "/" + variant.name()), () -> type.create(variant), EARTH)
        )
    );

    public static final RegistryObject<Block> PEAT = register("peat", () -> new Block(Properties.of(Material.DIRT, MaterialColor.TERRACOTTA_BLACK).strength(0.6F).sound(TFCSounds.PEAT)), EARTH);
    public static final RegistryObject<Block> PEAT_GRASS = register("peat_grass", () -> new ConnectedGrassBlock(Properties.of(Material.GRASS).randomTicks().strength(0.6F).sound(TFCSounds.PEAT), PEAT, null, null), EARTH);

    public static final Map<SandBlockType, RegistryObject<Block>> SAND = Helpers.mapOfKeys(SandBlockType.class, type ->
        register(("sand/" + type.name()), type::create, EARTH)
    );

    public static final Map<SandBlockType, Map<SandstoneBlockType, RegistryObject<Block>>> SANDSTONE = Helpers.mapOfKeys(SandBlockType.class, color ->
        Helpers.mapOfKeys(SandstoneBlockType.class, type ->
            register((type.name() + "_sandstone/" + color.name()), () -> new Block(type.properties(color)), EARTH)
        )
    );

    public static final Map<SandBlockType, Map<SandstoneBlockType, DecorationBlockRegistryObject>> SANDSTONE_DECORATIONS = Helpers.mapOfKeys(SandBlockType.class, color ->
        Helpers.mapOfKeys(SandstoneBlockType.class, type -> new DecorationBlockRegistryObject(
            register((type.name() + "_sandstone/" + color.name() + "_slab"), () -> new SlabBlock(type.properties(color)), EARTH),
            register((type.name() + "_sandstone/" + color.name() + "_stairs"), () -> new StairBlock(() -> SANDSTONE.get(color).get(type).get().defaultBlockState(), type.properties(color)), EARTH),
            register((type.name() + "_sandstone/" + color.name() + "_wall"), () -> new WallBlock(type.properties(color)), EARTH)
        ))
    );

    public static final Map<GroundcoverBlockType, RegistryObject<Block>> GROUNDCOVER = Helpers.mapOfKeys(GroundcoverBlockType.class, type ->
        register(("groundcover/" + type.name()), () -> new GroundcoverBlock(type), block -> new BlockItem(block, new Item.Properties().tab(EARTH)), type.shouldCreateBlockItem())
    );

    public static final RegistryObject<Block> SEA_ICE = register("sea_ice", () -> new SeaIceBlock(BlockBehaviour.Properties.of(Material.ICE).friction(0.98f).randomTicks().strength(0.5f).sound(SoundType.GLASS).noOcclusion().isValidSpawn(TFCBlocks::onlyPolarBears)), EARTH);
    public static final RegistryObject<SnowPileBlock> SNOW_PILE = register("snow_pile", () -> new SnowPileBlock(new ForgeBlockProperties(Properties.copy(Blocks.SNOW)).blockEntity(TFCTileEntities.SNOW_PILE)), EARTH);
    public static final RegistryObject<ThinSpikeBlock> ICICLE = register("icicle", () -> new ThinSpikeBlock(Properties.of(Material.ICE).noDrops().strength(0.4f).sound(SoundType.GLASS).noOcclusion()));
    public static final RegistryObject<ThinSpikeBlock> CALCITE = register("calcite", () -> new ThinSpikeBlock(Properties.of(Material.GLASS).noDrops().strength(0.2f).sound(TFCSounds.THIN)));

    // Ores

    public static final Map<Rock, Map<Ore, RegistryObject<Block>>> ORES = Helpers.mapOfKeys(Rock.class, rock ->
        Helpers.mapOfKeys(Ore.class, ore -> !ore.isGraded(), ore ->
            register(("ore/" + ore.name() + "/" + rock.name()), () -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(3, 10).requiresCorrectToolForDrops()), TFCItemGroup.ORES)
        )
    );
    public static final Map<Rock, Map<Ore, Map<Ore.Grade, RegistryObject<Block>>>> GRADED_ORES = Helpers.mapOfKeys(Rock.class, rock ->
        Helpers.mapOfKeys(Ore.class, Ore::isGraded, ore ->
            Helpers.mapOfKeys(Ore.Grade.class, grade ->
                register(("ore/" + grade.name() + "_" + ore.name() + "/" + rock.name()), () -> new Block(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(3, 10).requiresCorrectToolForDrops()), TFCItemGroup.ORES)
            )
        )
    );
    public static final Map<Ore, RegistryObject<Block>> SMALL_ORES = Helpers.mapOfKeys(Ore.class, Ore::isGraded, type ->
        register(("ore/small_" + type.name()), () -> GroundcoverBlock.looseOre(Properties.of(Material.GRASS).strength(0.05F, 0.0F).sound(SoundType.NETHER_ORE).noOcclusion()), TFCItemGroup.ORES)
    );

    // Rock Stuff

    public static final Map<Rock, Map<Rock.BlockType, RegistryObject<Block>>> ROCK_BLOCKS = Helpers.mapOfKeys(Rock.class, rock ->
        Helpers.mapOfKeys(Rock.BlockType.class, type ->
            register(("rock/" + type.name() + "/" + rock.name()), () -> type.create(rock), ROCK_STUFFS)
        )
    );

    public static final Map<Rock, Map<Rock.BlockType, DecorationBlockRegistryObject>> ROCK_DECORATIONS = Helpers.mapOfKeys(Rock.class, rock ->
        Helpers.mapOfKeys(Rock.BlockType.class, Rock.BlockType::hasVariants, type -> new DecorationBlockRegistryObject(
            register(("rock/" + type.name() + "/" + rock.name()) + "_slab", () -> type.createSlab(rock), ROCK_STUFFS),
            register(("rock/" + type.name() + "/" + rock.name()) + "_stairs", () -> type.createStairs(rock), ROCK_STUFFS),
            register(("rock/" + type.name() + "/" + rock.name()) + "_wall", () -> type.createWall(rock), ROCK_STUFFS)
        ))
    );

    public static final Map<Rock, RegistryObject<Block>> ROCK_ANVILS = Helpers.mapOfKeys(Rock.class, rock -> rock.getCategory() == RockCategory.IGNEOUS_EXTRUSIVE || rock.getCategory() == RockCategory.IGNEOUS_INTRUSIVE, rock ->
        register("rock/anvil/" + rock.name(), () -> new RockAnvilBlock(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(2, 10).requiresCorrectToolForDrops(), TFCBlocks.ROCK_BLOCKS.get(rock).get(Rock.BlockType.RAW)), ROCK_STUFFS)
    );

    // Metals

    public static final Map<Metal.Default, Map<Metal.BlockType, RegistryObject<Block>>> METALS = Helpers.mapOfKeys(Metal.Default.class, metal ->
        Helpers.mapOfKeys(Metal.BlockType.class, type -> type.hasMetal(metal), type ->
            register(("metal/" + type.name() + "/" + metal.name()), type.create(metal), METAL)
        )
    );

    // Wood

    public static final Map<Wood, Map<Wood.BlockType, RegistryObject<Block>>> WOODS = Helpers.mapOfKeys(Wood.class, wood ->
        Helpers.mapOfKeys(Wood.BlockType.class, type ->
            type.needsItem() ? register(type.nameFor(wood), type.create(wood), WOOD) : register(type.nameFor(wood), type.create(wood))
        )
    );

    // Flora

    public static final Map<Plant, RegistryObject<Block>> PLANTS = Helpers.mapOfKeys(Plant.class, plant ->
        register(("plant/" + plant.name()), plant::create, block -> plant.createBlockItem(block, new Item.Properties().tab(FLORA)), plant.needsItem())
    );

    public static final Map<Coral, Map<Coral.BlockType, RegistryObject<Block>>> CORAL = Helpers.mapOfKeys(Coral.class, color ->
        Helpers.mapOfKeys(Coral.BlockType.class, type ->
            register("coral/" + color.toString() + "_" + type.toString(), type.create(color), type.createBlockItem(new Item.Properties().tab(FLORA)), type.needsItem())
        )
    );

    public static final RegistryObject<Block> SEA_PICKLE = register("sea_pickle", () -> new TFCSeaPickleBlock(BlockBehaviour.Properties.of(Material.WATER_PLANT, MaterialColor.COLOR_GREEN).lightLevel((state) -> TFCSeaPickleBlock.isDead(state) ? 0 : 3 + 3 * state.getValue(SeaPickleBlock.PICKLES)).sound(SoundType.SLIME_BLOCK).noOcclusion()), FLORA);

    public static final Map<FruitBlocks.StationaryBush, RegistryObject<Block>> STATIONARY_BUSHES = Helpers.mapOfKeys(FruitBlocks.StationaryBush.class, bush -> register("plant/" + bush.name() + "_bush", bush::create, FLORA));
    public static final Map<FruitBlocks.SpreadingBush, RegistryObject<Block>> SPREADING_CANES = Helpers.mapOfKeys(FruitBlocks.SpreadingBush.class, bush -> register("plant/" + bush.name() + "_bush_cane", bush::createCane));
    public static final Map<FruitBlocks.SpreadingBush, RegistryObject<Block>> SPREADING_BUSHES = Helpers.mapOfKeys(FruitBlocks.SpreadingBush.class, bush -> register("plant/" + bush.name() + "_bush", bush::createBush, FLORA));
    public static final RegistryObject<Block> CRANBERRY_BUSH = register("plant/cranberry_bush", FruitBlocks::createCranberry, FLORA);

    public static final RegistryObject<Block> DEAD_BERRY_BUSH = register("plant/dead_berry_bush", () -> new DeadBerryBushBlock(new ForgeBlockProperties(Properties.of(Material.LEAVES).strength(0.6f).noOcclusion().sound(SoundType.SWEET_BERRY_BUSH).randomTicks()).blockEntity(TFCTileEntities.TICK_COUNTER).flammable(120, 90)));
    public static final RegistryObject<Block> DEAD_CANE = register("plant/dead_cane", () -> new DeadCaneBlock(new ForgeBlockProperties(Properties.of(Material.LEAVES).strength(0.6f).noOcclusion().sound(SoundType.SWEET_BERRY_BUSH).randomTicks()).blockEntity(TFCTileEntities.TICK_COUNTER).flammable(120, 90)));
    public static final Map<FruitBlocks.Tree, RegistryObject<Block>> FRUIT_TREE_LEAVES = Helpers.mapOfKeys(FruitBlocks.Tree.class, tree -> register("plant/" + tree.name() + "_leaves", tree::createLeaves, FLORA));
    public static final Map<FruitBlocks.Tree, RegistryObject<Block>> FRUIT_TREE_BRANCHES = Helpers.mapOfKeys(FruitBlocks.Tree.class, tree -> register("plant/" + tree.name() + "_branch", tree::createBranch));
    public static final Map<FruitBlocks.Tree, RegistryObject<Block>> FRUIT_TREE_GROWING_BRANCHES = Helpers.mapOfKeys(FruitBlocks.Tree.class, tree -> register("plant/" + tree.name() + "_growing_branch", tree::createGrowingBranch));
    public static final Map<FruitBlocks.Tree, RegistryObject<Block>> FRUIT_TREE_SAPLINGS = Helpers.mapOfKeys(FruitBlocks.Tree.class, tree -> register("plant/" + tree.name() + "_sapling", tree::createSapling, FLORA));
    public static final RegistryObject<Block> BANANA_PLANT = register("plant/banana_plant", FruitBlocks::createBananaPlant);
    public static final RegistryObject<Block> BANANA_SAPLING = register("plant/banana_sapling", FruitBlocks::createBananaSapling, FLORA);

    // Decorations

    public static final RegistryObject<Block> PLAIN_ALABASTER = register("alabaster/raw/alabaster", () -> new Block(Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)), DECORATIONS);
    public static final RegistryObject<Block> PLAIN_ALABASTER_BRICKS = register("alabaster/raw/alabaster_bricks", () -> new Block(Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)), DECORATIONS);
    public static final RegistryObject<Block> PLAIN_POLISHED_ALABASTER = register("alabaster/raw/polished_alabaster", () -> new Block(Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)), DECORATIONS);

    public static final RegistryObject<Block> AGGREGATE = register("aggregate", () -> new GravelBlock(Properties.of(Material.SAND, MaterialColor.STONE).strength(0.6F).sound(SoundType.GRAVEL)), DECORATIONS);

    public static final Map<DyeColor, RegistryObject<Block>> RAW_ALABASTER = Helpers.mapOfKeys(DyeColor.class, color ->
        register(("alabaster/stained/" + color.getName()) + "_raw_alabaster", () -> new Block(BlockBehaviour.Properties.of(Material.STONE, color.getMaterialColor()).requiresCorrectToolForDrops().strength(1.0F, 6.0F)), DECORATIONS)
    );
    public static final Map<DyeColor, RegistryObject<Block>> ALABASTER_BRICKS = Helpers.mapOfKeys(DyeColor.class, color ->
        register(("alabaster/stained/" + color.getName()) + "_alabaster_bricks", () -> new Block(BlockBehaviour.Properties.of(Material.STONE, color.getMaterialColor()).requiresCorrectToolForDrops().strength(1.5F, 6.0F)), DECORATIONS)
    );
    public static final Map<DyeColor, RegistryObject<Block>> POLISHED_ALABASTER = Helpers.mapOfKeys(DyeColor.class, color ->
        register(("alabaster/stained/" + color.getName()) + "_polished_alabaster", () -> new Block(BlockBehaviour.Properties.of(Material.STONE, color.getMaterialColor()).requiresCorrectToolForDrops().strength(1.5F, 6.0F)), DECORATIONS)
    );

    public static final Map<DyeColor, DecorationBlockRegistryObject> ALABASTER_BRICK_DECORATIONS = Helpers.mapOfKeys(DyeColor.class, color -> new DecorationBlockRegistryObject(
            register(("alabaster/stained/" + color.getName() + "_alabaster_bricks_slab"), () -> new SlabBlock(BlockBehaviour.Properties.of(Material.STONE, color.getMaterialColor()).requiresCorrectToolForDrops().strength(1.5F, 6.0F)), DECORATIONS),
            register(("alabaster/stained/" + color.getName() + "_alabaster_bricks_stairs"), () -> new StairBlock(() -> ALABASTER_BRICKS.get(color).get().defaultBlockState(), BlockBehaviour.Properties.of(Material.STONE, color.getMaterialColor()).requiresCorrectToolForDrops().strength(1.5F, 6.0F)), DECORATIONS),
            register(("alabaster/stained/" + color.getName() + "_alabaster_bricks_wall"), () -> new WallBlock(BlockBehaviour.Properties.of(Material.STONE, color.getMaterialColor()).requiresCorrectToolForDrops().strength(1.5F, 6.0F)), DECORATIONS)
        )
    );

    public static final Map<DyeColor, DecorationBlockRegistryObject> ALABASTER_POLISHED_DECORATIONS = Helpers.mapOfKeys(DyeColor.class, color -> new DecorationBlockRegistryObject(
            register(("alabaster/stained/" + color.getName() + "_polished_alabaster_slab"), () -> new SlabBlock(BlockBehaviour.Properties.of(Material.STONE, color.getMaterialColor()).requiresCorrectToolForDrops().strength(1.5F, 6.0F)), DECORATIONS),
            register(("alabaster/stained/" + color.getName() + "_polished_alabaster_stairs"), () -> new StairBlock(() -> ALABASTER_BRICKS.get(color).get().defaultBlockState(), BlockBehaviour.Properties.of(Material.STONE, color.getMaterialColor()).requiresCorrectToolForDrops().strength(1.5F, 6.0F)), DECORATIONS),
            register(("alabaster/stained/" + color.getName() + "_polished_alabaster_wall"), () -> new WallBlock(BlockBehaviour.Properties.of(Material.STONE, color.getMaterialColor()).requiresCorrectToolForDrops().strength(1.5F, 6.0F)), DECORATIONS)
        )
    );

    public static final RegistryObject<Block> FIRE_BRICKS = register("fire_bricks", () -> new Block(Properties.of(Material.STONE, MaterialColor.COLOR_RED).requiresCorrectToolForDrops().strength(2.0F, 6.0F)), DECORATIONS);
    public static final RegistryObject<Block> FIRE_CLAY_BLOCK = register("fire_clay_block", () -> new Block(Properties.of(Material.CLAY).strength(0.6F).sound(SoundType.GRAVEL)), DECORATIONS);

    // Misc

    public static final RegistryObject<Block> THATCH = register("thatch", () -> new ThatchBlock(new ForgeBlockProperties(Properties.of(Material.PLANT).strength(0.6F, 0.4F).noOcclusion().sound(TFCSounds.THATCH)).flammable(50, 100)), MISC);
    public static final RegistryObject<Block> THATCH_BED = register("thatch_bed", () -> new ThatchBedBlock(Properties.of(Material.REPLACEABLE_PLANT).sound(TFCSounds.THATCH).strength(0.6F, 0.4F)));

    public static final RegistryObject<Block> BURNING_LOG_PILE = register("burning_log_pile", () -> new BurningLogPileBlock(new ForgeBlockProperties(BlockBehaviour.Properties.of(Material.WOOD).randomTicks().strength(0.6F).sound(SoundType.WOOD)).flammable(60, 30).blockEntity(TFCTileEntities.BURNING_LOG_PILE).serverTicks(BurningLogPileTileEntity::serverTick)));
    public static final RegistryObject<Block> LOG_PILE = register("log_pile", () -> new LogPileBlock(new ForgeBlockProperties(BlockBehaviour.Properties.of(Material.WOOD).strength(0.6F).sound(SoundType.WOOD)).flammable(60, 30).blockEntity(TFCTileEntities.LOG_PILE)));

    public static final RegistryObject<Block> FIREPIT = register("firepit", () -> new FirepitBlock(new ForgeBlockProperties(Properties.of(Material.DIRT).strength(0.4F, 0.4F).sound(SoundType.NETHER_WART).noOcclusion().lightLevel(litBlockEmission(15))).blockEntity(TFCTileEntities.FIREPIT).<AbstractFirepitTileEntity<?>>serverTicks(AbstractFirepitTileEntity::serverTick)), MISC);
    public static final RegistryObject<Block> GRILL = register("grill", () -> new GrillBlock(new ForgeBlockProperties(Properties.of(Material.DIRT).strength(0.4F, 0.4F).sound(SoundType.NETHER_WART).noOcclusion().lightLevel(litBlockEmission(15))).blockEntity(TFCTileEntities.GRILL).<AbstractFirepitTileEntity<?>>serverTicks(AbstractFirepitTileEntity::serverTick)), MISC);
    public static final RegistryObject<Block> POT = register("pot", () -> new PotBlock(new ForgeBlockProperties(Properties.of(Material.DIRT).strength(0.4F, 0.4F).sound(SoundType.NETHER_WART).noOcclusion().lightLevel(litBlockEmission(15))).blockEntity(TFCTileEntities.POT).<AbstractFirepitTileEntity<?>>serverTicks(AbstractFirepitTileEntity::serverTick)), MISC);

    public static final RegistryObject<Block> PLACED_ITEM = register("placed_item", () -> new PlacedItemBlock(new ForgeBlockProperties(Properties.of(Material.DECORATION).instabreak().sound(SoundType.STEM).noOcclusion()).blockEntity(TFCTileEntities.PLACED_ITEM)));
    public static final RegistryObject<Block> SCRAPING = register("scraping", () -> new ScrapingBlock(new ForgeBlockProperties(Properties.of(Material.DECORATION).strength(0.2F).sound(SoundType.STEM).noOcclusion()).blockEntity(TFCTileEntities.SCRAPING)));
    public static final RegistryObject<Block> PIT_KILN = register("pit_kiln", () -> new PitKilnBlock(new ForgeBlockProperties(Properties.of(Material.GLASS).sound(SoundType.WOOD).strength(0.6f).noOcclusion()).blockEntity(TFCTileEntities.PIT_KILN).serverTicks(PitKilnTileEntity::serverTick)));
    public static final RegistryObject<Block> QUERN = register("quern", () -> new QuernBlock(new ForgeBlockProperties(Properties.of(Material.STONE).strength(0.5F, 2.0F).sound(SoundType.BASALT).noOcclusion()).blockEntity(TFCTileEntities.QUERN).ticks(QuernTileEntity::serverTick, QuernTileEntity::clientTick)), MISC);

    public static final RegistryObject<Block> CHARCOAL_PILE = register("charcoal_pile", () -> new CharcoalPileBlock(Properties.of(Material.DIRT, MaterialColor.COLOR_BLACK).strength(0.2F).sound(TFCSounds.CHARCOAL)));
    public static final RegistryObject<Block> CHARCOAL_FORGE = register("charcoal_forge", () -> new CharcoalForgeBlock(new ForgeBlockProperties(Properties.of(Material.DIRT, MaterialColor.COLOR_BLACK).strength(0.2F).sound(TFCSounds.CHARCOAL).lightLevel(state -> state.getValue(CharcoalForgeBlock.HEAT) * 2)).blockEntity(TFCTileEntities.CHARCOAL_FORGE).serverTicks(CharcoalForgeTileEntity::serverTick)));

    public static final RegistryObject<Block> TORCH = register("torch", () -> new TFCTorchBlock(new ForgeBlockProperties(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().instabreak().randomTicks().lightLevel(state -> 14).sound(SoundType.WOOD)).blockEntity(TFCTileEntities.TICK_COUNTER), ParticleTypes.FLAME));
    public static final RegistryObject<Block> WALL_TORCH = register("wall_torch", () -> new TFCWallTorchBlock(new ForgeBlockProperties(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().instabreak().randomTicks().lightLevel(state -> 14).sound(SoundType.WOOD)).blockEntity(TFCTileEntities.TICK_COUNTER), ParticleTypes.FLAME));
    public static final RegistryObject<Block> DEAD_TORCH = register("dead_torch", () -> new DeadTorchBlock(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().instabreak().sound(SoundType.WOOD), ParticleTypes.FLAME));
    public static final RegistryObject<Block> DEAD_WALL_TORCH = register("dead_wall_torch", () -> new DeadWallTorchBlock(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().instabreak().sound(SoundType.WOOD), ParticleTypes.FLAME));

    // Fluids

    public static final Map<Metal.Default, RegistryObject<LiquidBlock>> METAL_FLUIDS = Helpers.mapOfKeys(Metal.Default.class, metal ->
        register("fluid/metal/" + metal.name(), () -> new LiquidBlock(TFCFluids.METALS.get(metal).getSecond(), Properties.of(TFCMaterials.MOLTEN_METAL).noCollission().strength(100f).noDrops()))
    );

    // todo: forge has broken fluid blocks, they crash all the time, fix these references after they fix their stuff
    public static final RegistryObject<LiquidBlock> SALT_WATER = RegistryObject.of(Blocks.WATER.getRegistryName(), ForgeRegistries.BLOCKS);
    public static final RegistryObject<LiquidBlock> SPRING_WATER = SALT_WATER;

    static
    {
        register("fluid/salt_water", () -> new LiquidBlock(TFCFluids.SALT_WATER.getSecond(), Properties.of(TFCMaterials.SALT_WATER).noCollission().strength(100f).noDrops()));
        register("fluid/spring_water", () -> new HotWaterBlock(TFCFluids.SPRING_WATER.getSecond(), Properties.of(TFCMaterials.SPRING_WATER).noCollission().strength(100f).noDrops()));
    }

    public static boolean always(BlockState state, BlockGetter world, BlockPos pos)
    {
        return true;
    }

    public static boolean never(BlockState state, BlockGetter world, BlockPos pos)
    {
        return false;
    }

    public static boolean onlyPolarBears(BlockState state, BlockGetter world, BlockPos pos, EntityType<?> type)
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

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockSupplier, CreativeModeTab group)
    {
        return register(name, blockSupplier, block -> new BlockItem(block, new Item.Properties().tab(group)), true);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockSupplier, Item.Properties blockItemProperties)
    {
        return register(name, blockSupplier, block -> new BlockItem(block, blockItemProperties), true);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockSupplier, Function<T, ? extends BlockItem> blockItemFactory, boolean hasItemBlock)
    {
        final String actualName = name.toLowerCase(Locale.ROOT);
        final RegistryObject<T> block = BLOCKS.register(actualName, blockSupplier);
        if (hasItemBlock)
        {
            TFCItems.ITEMS.register(actualName, () -> blockItemFactory.apply(block.get()));
        }
        return block;
    }
}