/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.dries007.tfc.common.blocks.wood.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.AbstractFirepitBlockEntity;
import net.dries007.tfc.common.blockentities.BellowsBlockEntity;
import net.dries007.tfc.common.blockentities.BlastFurnaceBlockEntity;
import net.dries007.tfc.common.blockentities.BloomeryBlockEntity;
import net.dries007.tfc.common.blockentities.BurningLogPileBlockEntity;
import net.dries007.tfc.common.blockentities.CharcoalForgeBlockEntity;
import net.dries007.tfc.common.blockentities.CrucibleBlockEntity;
import net.dries007.tfc.common.blockentities.DecayingBlockEntity;
import net.dries007.tfc.common.blockentities.GlassBasinBlockEntity;
import net.dries007.tfc.common.blockentities.HotPouredGlassBlockEntity;
import net.dries007.tfc.common.blockentities.NestBoxBlockEntity;
import net.dries007.tfc.common.blockentities.PitKilnBlockEntity;
import net.dries007.tfc.common.blockentities.PowderkegBlockEntity;
import net.dries007.tfc.common.blockentities.QuernBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blocks.crop.Crop;
import net.dries007.tfc.common.blocks.crop.DecayingBlock;
import net.dries007.tfc.common.blocks.crop.TFCPumpkinBlock;
import net.dries007.tfc.common.blocks.devices.BarrelRackBlock;
import net.dries007.tfc.common.blocks.devices.BellowsBlock;
import net.dries007.tfc.common.blocks.devices.BlastFurnaceBlock;
import net.dries007.tfc.common.blocks.devices.BloomeryBlock;
import net.dries007.tfc.common.blocks.devices.BurningLogPileBlock;
import net.dries007.tfc.common.blocks.devices.CharcoalForgeBlock;
import net.dries007.tfc.common.blocks.devices.CrucibleBlock;
import net.dries007.tfc.common.blocks.devices.DoubleIngotPileBlock;
import net.dries007.tfc.common.blocks.devices.FirepitBlock;
import net.dries007.tfc.common.blocks.devices.GrillBlock;
import net.dries007.tfc.common.blocks.devices.IngotPileBlock;
import net.dries007.tfc.common.blocks.devices.JackOLanternBlock;
import net.dries007.tfc.common.blocks.devices.LogPileBlock;
import net.dries007.tfc.common.blocks.devices.NestBoxBlock;
import net.dries007.tfc.common.blocks.devices.PitKilnBlock;
import net.dries007.tfc.common.blocks.devices.PlacedItemBlock;
import net.dries007.tfc.common.blocks.devices.PotBlock;
import net.dries007.tfc.common.blocks.devices.PowderkegBlock;
import net.dries007.tfc.common.blocks.devices.QuernBlock;
import net.dries007.tfc.common.blocks.devices.ScrapingBlock;
import net.dries007.tfc.common.blocks.devices.SheetPileBlock;
import net.dries007.tfc.common.blocks.devices.TFCComposterBlock;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.plant.coral.Coral;
import net.dries007.tfc.common.blocks.plant.coral.TFCSeaPickleBlock;
import net.dries007.tfc.common.blocks.plant.fruit.DeadBananaPlantBlock;
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
import net.dries007.tfc.common.fluids.Alcohol;
import net.dries007.tfc.common.fluids.FluidId;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.CandleBlockItem;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.items.TooltipBlockItem;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.registry.RegistrationHelpers;

import static net.dries007.tfc.TerraFirmaCraft.*;

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
            register((type.name() + "/" + variant.name()), () -> type.create(variant))
        )
    );

    public static final Map<SoilBlockType.Variant, DecorationBlockRegistryObject> MUD_BRICK_DECORATIONS = Helpers.mapOfKeys(SoilBlockType.Variant.class, variant -> new DecorationBlockRegistryObject(
        register(("mud_bricks/" + variant.name() + "_slab"), () -> new SlabBlock(Properties.of().mapColor(MapColor.DIRT).strength(2.6f).sound(SoundType.WART_BLOCK))),
        register(("mud_bricks/" + variant.name() + "_stairs"), () -> new StairBlock(() -> SOIL.get(SoilBlockType.MUD_BRICKS).get(variant).get().defaultBlockState(), Properties.of().mapColor(MapColor.DIRT).strength(2.6f).sound(SoundType.WART_BLOCK).instrument(NoteBlockInstrument.BASEDRUM))),
        register(("mud_bricks/" + variant.name() + "_wall"), () -> new WallBlock(Properties.of().mapColor(MapColor.DIRT).strength(2.6f).sound(SoundType.WART_BLOCK)))
    ));

    public static final RegistryObject<Block> PEAT = register("peat", () -> new Block(Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).strength(3.0F).sound(TFCSounds.PEAT)));
    public static final RegistryObject<Block> PEAT_GRASS = register("peat_grass", () -> new ConnectedGrassBlock(Properties.of().mapColor(MapColor.GRASS).randomTicks().strength(3.0F).sound(TFCSounds.PEAT), PEAT, null, null));

    public static final Map<SandBlockType, RegistryObject<Block>> SAND = Helpers.mapOfKeys(SandBlockType.class, type ->
        register(("sand/" + type.name()), type::create)
    );

    public static final Map<SandBlockType, Map<SandstoneBlockType, RegistryObject<Block>>> SANDSTONE = Helpers.mapOfKeys(SandBlockType.class, color ->
        Helpers.mapOfKeys(SandstoneBlockType.class, type ->
            register((type.name() + "_sandstone/" + color.name()), () -> new Block(type.properties(color)))
        )
    );

    public static final Map<SandBlockType, Map<SandstoneBlockType, DecorationBlockRegistryObject>> SANDSTONE_DECORATIONS = Helpers.mapOfKeys(SandBlockType.class, color ->
        Helpers.mapOfKeys(SandstoneBlockType.class, type -> new DecorationBlockRegistryObject(
            register((type.name() + "_sandstone/" + color.name() + "_slab"), () -> new SlabBlock(type.properties(color))),
            register((type.name() + "_sandstone/" + color.name() + "_stairs"), () -> new StairBlock(() -> SANDSTONE.get(color).get(type).get().defaultBlockState(), type.properties(color))),
            register((type.name() + "_sandstone/" + color.name() + "_wall"), () -> new WallBlock(type.properties(color)))
        ))
    );

    public static final Map<GroundcoverBlockType, RegistryObject<Block>> GROUNDCOVER = Helpers.mapOfKeys(GroundcoverBlockType.class, type ->
        register(("groundcover/" + type.name()), () -> new GroundcoverBlock(type), type.createBlockItem())
    );

    public static final RegistryObject<Block> SEA_ICE = register("sea_ice", () -> new SeaIceBlock(Properties.copy(Blocks.ICE).isValidSpawn(TFCBlocks::onlyColdMobs)));

    public static final RegistryObject<SnowPileBlock> SNOW_PILE = register("snow_pile", () -> new SnowPileBlock(ExtendedProperties.of(Blocks.SNOW).randomTicks().blockEntity(TFCBlockEntities.PILE)));
    public static final RegistryObject<IcePileBlock> ICE_PILE = register("ice_pile", () -> new IcePileBlock(ExtendedProperties.of(Blocks.ICE).randomTicks().blockEntity(TFCBlockEntities.PILE)));
    public static final RegistryObject<ThinSpikeBlock> ICICLE = register("icicle", () -> new IcicleBlock(Properties.copy(Blocks.ICE).noLootTable().strength(0.4f).sound(SoundType.GLASS).noOcclusion().randomTicks()));

    public static final RegistryObject<ThinSpikeBlock> CALCITE = register("calcite", () -> new ThinSpikeBlock(Properties.copy(Blocks.ICE).noLootTable().strength(0.2f).sound(TFCSounds.THIN)));

    // Ores

    public static final Map<Rock, Map<Ore, RegistryObject<Block>>> ORES = Helpers.mapOfKeys(Rock.class, rock ->
        Helpers.mapOfKeys(Ore.class, ore -> !ore.isGraded(), ore ->
            register(("ore/" + ore.name() + "/" + rock.name()), () -> ore.create(rock))
        )
    );
    public static final Map<Rock, Map<Ore, Map<Ore.Grade, RegistryObject<Block>>>> GRADED_ORES = Helpers.mapOfKeys(Rock.class, rock ->
        Helpers.mapOfKeys(Ore.class, Ore::isGraded, ore ->
            Helpers.mapOfKeys(Ore.Grade.class, grade ->
                register(("ore/" + grade.name() + "_" + ore.name() + "/" + rock.name()), () -> ore.create(rock))
            )
        )
    );
    public static final Map<Ore, RegistryObject<Block>> SMALL_ORES = Helpers.mapOfKeys(Ore.class, Ore::isGraded, type ->
        register(("ore/small_" + type.name()), () -> GroundcoverBlock.looseOre(Properties.of().mapColor(MapColor.GRASS).strength(0.05F, 0.0F).sound(SoundType.NETHER_ORE).noCollission().pushReaction(PushReaction.DESTROY)))
    );
    public static final Map<Rock, Map<OreDeposit, RegistryObject<Block>>> ORE_DEPOSITS = Helpers.mapOfKeys(Rock.class, rock ->
        Helpers.mapOfKeys(OreDeposit.class, ore ->
            register("deposit/" + ore.name() + "/" + rock.name(), () -> new OreDepositBlock(Block.Properties.of().mapColor(MapColor.STONE).sound(SoundType.GRAVEL).strength(rock.category().hardness(2.0f)), rock, ore)) // Same hardness as gravel
        )
    );

    // Rock Stuff

    public static final Map<Rock, Map<Rock.BlockType, RegistryObject<Block>>> ROCK_BLOCKS = Helpers.mapOfKeys(Rock.class, rock ->
        Helpers.mapOfKeys(Rock.BlockType.class, type ->
            register(("rock/" + type.name() + "/" + rock.name()), () -> type.create(rock))
        )
    );

    public static final Map<Rock, Map<Rock.BlockType, DecorationBlockRegistryObject>> ROCK_DECORATIONS = Helpers.mapOfKeys(Rock.class, rock ->
        Helpers.mapOfKeys(Rock.BlockType.class, Rock.BlockType::hasVariants, type -> new DecorationBlockRegistryObject(
            register(("rock/" + type.name() + "/" + rock.name()) + "_slab", () -> type.createSlab(rock)),
            register(("rock/" + type.name() + "/" + rock.name()) + "_stairs", () -> type.createStairs(rock)),
            register(("rock/" + type.name() + "/" + rock.name()) + "_wall", () -> type.createWall(rock))
        ))
    );

    public static final Map<Rock, RegistryObject<Block>> ROCK_ANVILS = Helpers.mapOfKeys(Rock.class, rock -> rock.category() == RockCategory.IGNEOUS_EXTRUSIVE || rock.category() == RockCategory.IGNEOUS_INTRUSIVE, rock ->
        register("rock/anvil/" + rock.name(), () -> new RockAnvilBlock(ExtendedProperties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).strength(2, 10).requiresCorrectToolForDrops().blockEntity(TFCBlockEntities.ANVIL), TFCBlocks.ROCK_BLOCKS.get(rock).get(Rock.BlockType.RAW)))
    );

    public static final Map<Rock, RegistryObject<Block>> MAGMA_BLOCKS = Helpers.mapOfKeys(Rock.class, rock -> rock.category() == RockCategory.IGNEOUS_EXTRUSIVE || rock.category() == RockCategory.IGNEOUS_INTRUSIVE, rock ->
        register("rock/magma/" + rock.name(), () -> new TFCMagmaBlock(Properties.of().mapColor(MapColor.NETHER).requiresCorrectToolForDrops().lightLevel(s -> 6).randomTicks().strength(0.5F).isValidSpawn((state, level, pos, type) -> type.fireImmune()).hasPostProcess(TFCBlocks::always)))
    );

    // Metals

    public static final Map<Metal.Default, Map<Metal.BlockType, RegistryObject<Block>>> METALS = Helpers.mapOfKeys(Metal.Default.class, metal ->
        Helpers.mapOfKeys(Metal.BlockType.class, type -> type.has(metal), type ->
            register(type.createName(metal), type.create(metal), type.createBlockItem(new Item.Properties()))
        )
    );

    public static final Map<FluidId, RegistryObject<FluidCauldronBlock>> CAULDRONS = FluidId.mapOf(fluid ->
        registerNoItem("cauldron/" + fluid.name(), () -> new FluidCauldronBlock(Properties.copy(Blocks.CAULDRON)))
    );

    // Wood

    public static final Map<Wood, Map<Wood.BlockType, RegistryObject<Block>>> WOODS = Helpers.mapOfKeys(Wood.class, wood ->
        Helpers.mapOfKeys(Wood.BlockType.class, type ->
            register(type.nameFor(wood), type.create(wood), type.createBlockItem(wood, new Item.Properties()))
        )
    );

    public static final Map<Wood, Map<Metal.Default, Map<Class<? extends ITFCHangingSignBlock>, RegistryObject<SignBlock>>>> HANGING_SIGNS = Helpers.mapOfKeys(Wood.class, wood -> {
        Supplier<ExtendedProperties> woodProps = () -> Wood.BlockType.properties(wood).noCollission().strength(1F).flammableLikePlanks().blockEntity(TFCBlockEntities.HANGING_SIGN).ticks(SignBlockEntity::tick);
        WoodType woodType = wood.getVanillaWoodType();
        return Helpers.mapOfKeys(Metal.Default.class, metal -> metal.metalTier() != Metal.Tier.TIER_0, metal -> {
                ResourceLocation resource = Helpers.identifier(metal.getSerializedName());
                return Map.of(
                    TFCCeilingHangingSignBlock.class, register("wood/planks/hanging_sign/" + metal.getSerializedName() + "/" + wood.getSerializedName(), () -> new TFCCeilingHangingSignBlock(woodProps.get(), woodType, resource), (Function<SignBlock, BlockItem>)null),
                    TFCWallHangingSignBlock.class, register("wood/planks/wall_hanging_sign/" + metal.getSerializedName() + "/" + wood.getSerializedName(), () -> new TFCWallHangingSignBlock(woodProps.get(), woodType, resource), (Function<SignBlock, BlockItem>)null)
                );
            });
        }
    );

    public static final RegistryObject<Block> PALM_MOSAIC = register("wood/planks/palm_mosaic", () -> new Block(Properties.copy(Blocks.BAMBOO_MOSAIC)));
    public static final RegistryObject<Block> PALM_MOSAIC_STAIRS = register("wood/planks/palm_mosaic_stairs", () -> new TFCStairBlock(() -> PALM_MOSAIC.get().defaultBlockState(), ExtendedProperties.of(Blocks.BAMBOO_MOSAIC_STAIRS).flammableLikePlanks()));
    public static final RegistryObject<Block> PALM_MOSAIC_SLAB = register("wood/planks/palm_mosaic_slab", () -> new TFCSlabBlock(ExtendedProperties.of(Blocks.BAMBOO_MOSAIC_SLAB).flammableLikePlanks()));

    // Flora

    public static final Map<Plant, RegistryObject<Block>> PLANTS = Helpers.mapOfKeys(Plant.class, plant ->
        register(("plant/" + plant.name()), plant::create, plant.createBlockItem(new Item.Properties()))
    );

    public static final Map<Plant, RegistryObject<Block>> POTTED_PLANTS = Helpers.mapOfKeys(Plant.class, Plant::hasFlowerPot, plant ->
        registerNoItem(("plant/potted/" + plant.name()), () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, PLANTS.get(plant), BlockBehaviour.Properties.copy(Blocks.POTTED_ACACIA_SAPLING)))
    );

    public static final Map<Crop, RegistryObject<Block>> CROPS = Helpers.mapOfKeys(Crop.class, crop ->
        registerNoItem("crop/" + crop.name(), crop::create)
    );

    public static final Map<Crop, RegistryObject<Block>> DEAD_CROPS = Helpers.mapOfKeys(Crop.class, crop ->
        registerNoItem("dead_crop/" + crop.name(), crop::createDead)
    );

    public static final Map<Crop, RegistryObject<Block>> WILD_CROPS = Helpers.mapOfKeys(Crop.class, crop ->
        register("wild_crop/" + crop.name(), crop::createWild)
    );

    public static final Map<Coral, Map<Coral.BlockType, RegistryObject<Block>>> CORAL = Helpers.mapOfKeys(Coral.class, color ->
        Helpers.mapOfKeys(Coral.BlockType.class, type ->
            register("coral/" + color.toString() + "_" + type.toString(), type.create(color), type.createBlockItem(new Item.Properties()))
        )
    );

    public static final RegistryObject<Block> ROTTEN_PUMPKIN = registerNoItem("rotten_pumpkin", () -> new Block(Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(1.0F).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY)));
    public static final RegistryObject<Block> ROTTEN_MELON = registerNoItem("rotten_melon", () -> new Block(Properties.of().mapColor(MapColor.COLOR_GREEN).strength(1.0F).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY)));
    public static final RegistryObject<Block> PUMPKIN = register("pumpkin", () -> new TFCPumpkinBlock(ExtendedProperties.of(MapColor.COLOR_ORANGE).mapColor(MapColor.COLOR_ORANGE).strength(1.0F).sound(SoundType.WOOD).blockEntity(TFCBlockEntities.DECAYING).serverTicks(DecayingBlockEntity::serverTick).instrument(NoteBlockInstrument.DIDGERIDOO).pushReaction(PushReaction.DESTROY), ROTTEN_PUMPKIN), b -> new BlockItem(b, new Item.Properties()));
    public static final RegistryObject<Block> MELON = register("melon", () -> new DecayingBlock(ExtendedProperties.of(MapColor.COLOR_ORANGE).mapColor(MapColor.COLOR_GREEN).strength(1.0F).sound(SoundType.WOOD).blockEntity(TFCBlockEntities.DECAYING).serverTicks(DecayingBlockEntity::serverTick).instrument(NoteBlockInstrument.DIDGERIDOO).pushReaction(PushReaction.DESTROY), ROTTEN_MELON), b -> new BlockItem(b, new Item.Properties()));

    public static final RegistryObject<Block> SEA_PICKLE = register("sea_pickle", () -> new TFCSeaPickleBlock(Properties.of().pushReaction(PushReaction.DESTROY).mapColor(MapColor.COLOR_GREEN).pushReaction(PushReaction.DESTROY).lightLevel((state) -> TFCSeaPickleBlock.isDead(state) ? 0 : 3 + 3 * state.getValue(SeaPickleBlock.PICKLES)).sound(SoundType.SLIME_BLOCK).noOcclusion()));

    public static final Map<FruitBlocks.StationaryBush, RegistryObject<Block>> STATIONARY_BUSHES = Helpers.mapOfKeys(FruitBlocks.StationaryBush.class, bush -> register("plant/" + bush.name() + "_bush", bush::create));
    public static final Map<FruitBlocks.SpreadingBush, RegistryObject<Block>> SPREADING_CANES = Helpers.mapOfKeys(FruitBlocks.SpreadingBush.class, bush -> registerNoItem("plant/" + bush.name() + "_bush_cane", bush::createCane));
    public static final Map<FruitBlocks.SpreadingBush, RegistryObject<Block>> SPREADING_BUSHES = Helpers.mapOfKeys(FruitBlocks.SpreadingBush.class, bush -> register("plant/" + bush.name() + "_bush", bush::createBush));
    public static final RegistryObject<Block> CRANBERRY_BUSH = register("plant/cranberry_bush", FruitBlocks::createCranberry);

    public static final RegistryObject<Block> DEAD_BERRY_BUSH = registerNoItem("plant/dead_berry_bush", () -> new DeadBerryBushBlock(ExtendedProperties.of(MapColor.PLANT).strength(0.6f).noOcclusion().sound(SoundType.SWEET_BERRY_BUSH).randomTicks().blockEntity(TFCBlockEntities.TICK_COUNTER).flammable(120, 90)));
    public static final RegistryObject<Block> DEAD_BANANA_PLANT = registerNoItem("plant/dead_banana_plant", () -> new DeadBananaPlantBlock(ExtendedProperties.of(MapColor.PLANT).strength(0.6f).noOcclusion().sound(SoundType.SWEET_BERRY_BUSH).blockEntity(TFCBlockEntities.TICK_COUNTER).flammable(120, 90)));
    public static final RegistryObject<Block> DEAD_CANE = registerNoItem("plant/dead_cane", () -> new DeadCaneBlock(ExtendedProperties.of(MapColor.PLANT).strength(0.6f).noOcclusion().sound(SoundType.SWEET_BERRY_BUSH).randomTicks().blockEntity(TFCBlockEntities.TICK_COUNTER).flammable(120, 90)));
    public static final Map<FruitBlocks.Tree, RegistryObject<Block>> FRUIT_TREE_LEAVES = Helpers.mapOfKeys(FruitBlocks.Tree.class, tree -> register("plant/" + tree.name() + "_leaves", tree::createLeaves));
    public static final Map<FruitBlocks.Tree, RegistryObject<Block>> FRUIT_TREE_BRANCHES = Helpers.mapOfKeys(FruitBlocks.Tree.class, tree -> registerNoItem("plant/" + tree.name() + "_branch", tree::createBranch));
    public static final Map<FruitBlocks.Tree, RegistryObject<Block>> FRUIT_TREE_GROWING_BRANCHES = Helpers.mapOfKeys(FruitBlocks.Tree.class, tree -> registerNoItem("plant/" + tree.name() + "_growing_branch", tree::createGrowingBranch));
    public static final Map<FruitBlocks.Tree, RegistryObject<Block>> FRUIT_TREE_SAPLINGS = Helpers.mapOfKeys(FruitBlocks.Tree.class, tree -> register("plant/" + tree.name() + "_sapling", tree::createSapling));
    public static final Map<FruitBlocks.Tree, RegistryObject<Block>> FRUIT_TREE_POTTED_SAPLINGS = Helpers.mapOfKeys(FruitBlocks.Tree.class, tree -> registerNoItem("plant/potted/" + tree.name() + "_sapling", tree::createPottedSapling));
    public static final RegistryObject<Block> BANANA_PLANT = registerNoItem("plant/banana_plant", FruitBlocks::createBananaPlant);
    public static final RegistryObject<Block> BANANA_SAPLING = register("plant/banana_sapling", FruitBlocks::createBananaSapling);
    public static final RegistryObject<Block> BANANA_POTTED_SAPLING = registerNoItem("plant/potted/banana_sapling", FruitBlocks::createPottedBananaSapling);

    // Decorations

    public static final RegistryObject<Block> PLAIN_ALABASTER = register("alabaster/raw", () -> new Block(Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> PLAIN_ALABASTER_BRICKS = register("alabaster/bricks", () -> new Block(Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final RegistryObject<Block> PLAIN_POLISHED_ALABASTER = register("alabaster/polished", () -> new Block(Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));

    public static final RegistryObject<Block> AGGREGATE = register("aggregate", () -> new GravelBlock(Properties.copy(Blocks.GRAVEL).strength(0.6F).sound(SoundType.GRAVEL)));

    public static final Map<DyeColor, RegistryObject<Block>> RAW_ALABASTER = Helpers.mapOfKeys(DyeColor.class, color ->
        register(("alabaster/raw/" + color.getName()), () -> new Block(Properties.of().mapColor(color.getMapColor()).requiresCorrectToolForDrops().strength(1.0F, 6.0F)))
    );
    public static final Map<DyeColor, RegistryObject<Block>> ALABASTER_BRICKS = Helpers.mapOfKeys(DyeColor.class, color ->
        register(("alabaster/bricks/" + color.getName()), () -> new Block(Properties.of().mapColor(color.getMapColor()).requiresCorrectToolForDrops().strength(1.5F, 6.0F)))
    );
    public static final Map<DyeColor, RegistryObject<Block>> POLISHED_ALABASTER = Helpers.mapOfKeys(DyeColor.class, color ->
        register(("alabaster/polished/" + color.getName()), () -> new Block(Properties.of().mapColor(color.getMapColor()).requiresCorrectToolForDrops().strength(1.5F, 6.0F)))
    );

    public static final Map<DyeColor, DecorationBlockRegistryObject> ALABASTER_BRICK_DECORATIONS = Helpers.mapOfKeys(DyeColor.class, color -> new DecorationBlockRegistryObject(
            register(("alabaster/bricks/" + color.getName() + "_slab"), () -> new SlabBlock(Properties.of().mapColor(color.getMapColor()).requiresCorrectToolForDrops().strength(1.5F, 6.0F))),
            register(("alabaster/bricks/" + color.getName() + "_stairs"), () -> new StairBlock(() -> ALABASTER_BRICKS.get(color).get().defaultBlockState(), Properties.of().mapColor(color.getMapColor()).requiresCorrectToolForDrops().strength(1.5F, 6.0F))),
            register(("alabaster/bricks/" + color.getName() + "_wall"), () -> new WallBlock(Properties.of().mapColor(color.getMapColor()).requiresCorrectToolForDrops().strength(1.5F, 6.0F)))
        )
    );

    public static final Map<DyeColor, DecorationBlockRegistryObject> ALABASTER_POLISHED_DECORATIONS = Helpers.mapOfKeys(DyeColor.class, color -> new DecorationBlockRegistryObject(
            register(("alabaster/polished/" + color.getName() + "_slab"), () -> new SlabBlock(Properties.of().mapColor(color.getMapColor()).requiresCorrectToolForDrops().strength(1.5F, 6.0F))),
            register(("alabaster/polished/" + color.getName() + "_stairs"), () -> new StairBlock(() -> ALABASTER_BRICKS.get(color).get().defaultBlockState(), Properties.of().mapColor(color.getMapColor()).requiresCorrectToolForDrops().strength(1.5F, 6.0F))),
            register(("alabaster/polished/" + color.getName() + "_wall"), () -> new WallBlock(Properties.of().mapColor(color.getMapColor()).requiresCorrectToolForDrops().strength(1.5F, 6.0F)))
        )
    );

    public static final Map<DyeColor, RegistryObject<Block>> COLORED_POURED_GLASS = Helpers.mapOfKeys(DyeColor.class, color -> register(color.getSerializedName() + "_poured_glass", () -> new PouredGlassBlock(ExtendedProperties.of().noLootTable().pushReaction(PushReaction.DESTROY).strength(0.3F).sound(SoundType.GLASS).noOcclusion(), () -> PouredGlassBlock.getStainedGlass(color))));
    public static final RegistryObject<Block> POURED_GLASS = register("poured_glass", () -> new PouredGlassBlock(ExtendedProperties.of().noLootTable().strength(0.3F).sound(SoundType.GLASS).pushReaction(PushReaction.DESTROY).noOcclusion(), () -> Items.GLASS_PANE));
    public static final RegistryObject<Block> HOT_POURED_GLASS = registerNoItem("hot_poured_glass", () -> new HotPouredGlassBlock(ExtendedProperties.of().strength(0.3F).lightLevel(s -> 10).sound(SoundType.GLASS).pushReaction(PushReaction.DESTROY).noOcclusion().noLootTable().pathType(BlockPathTypes.DANGER_FIRE).blockEntity(TFCBlockEntities.HOT_POURED_GLASS).ticks(HotPouredGlassBlockEntity::tick)));
    public static final RegistryObject<Block> GLASS_BASIN = registerNoItem("glass_basin", () -> new GlassBasinBlock(ExtendedProperties.of().strength(0.3f).lightLevel(s -> 10).sound(SoundType.GLASS).pushReaction(PushReaction.DESTROY).noOcclusion().noLootTable().pathType(BlockPathTypes.DANGER_FIRE).blockEntity(TFCBlockEntities.GLASS_BASIN).dynamicShape().ticks(GlassBasinBlockEntity::ticks)));

    public static final RegistryObject<Block> FIRE_BRICKS = register("fire_bricks", () -> new Block(Properties.of().mapColor(MapColor.COLOR_RED).requiresCorrectToolForDrops().strength(2.0F, 6.0F)));
    public static final RegistryObject<Block> FIRE_CLAY_BLOCK = register("fire_clay_block", () -> new Block(Properties.of().mapColor(MapColor.CLAY).strength(0.6F).sound(SoundType.GRAVEL)));

    public static final RegistryObject<Block> WATTLE = register("wattle", () -> new WattleBlock(ExtendedProperties.of(MapColor.WOOD).strength(0.3F).noOcclusion().sound(SoundType.SCAFFOLDING).flammable(60, 30)));
    public static final RegistryObject<Block> UNSTAINED_WATTLE = register("wattle/unstained", () -> new StainedWattleBlock(ExtendedProperties.of(MapColor.WOOD).strength(0.3F).sound(SoundType.SCAFFOLDING).flammable(60, 30)));
    public static final Map<DyeColor, RegistryObject<Block>> STAINED_WATTLE = Helpers.mapOfKeys(DyeColor.class, color ->
        register("wattle/" + color.getName(), () -> new StainedWattleBlock(ExtendedProperties.of(MapColor.WOOD).strength(0.3F).sound(SoundType.SCAFFOLDING).flammable(60, 30)))
    );

    // Misc

    public static final RegistryObject<Block> THATCH = register("thatch", () -> new ThatchBlock(ExtendedProperties.of(MapColor.SAND).strength(0.6F, 0.4F).noOcclusion().isViewBlocking(TFCBlocks::never).sound(TFCSounds.THATCH).flammable(50, 100)));
    public static final RegistryObject<Block> THATCH_BED = register("thatch_bed", () -> new ThatchBedBlock(ExtendedProperties.of(MapColor.SAND).sound(TFCSounds.THATCH).strength(0.6F, 0.4F).flammable(50, 100).blockEntity(TFCBlockEntities.THATCH_BED)), b -> new BedItem(b, new Item.Properties()));
    public static final RegistryObject<Block> LOG_PILE = registerNoItem("log_pile", () -> new LogPileBlock(ExtendedProperties.of(MapColor.WOOD).strength(0.6F).sound(SoundType.WOOD).flammable(60, 30).blockEntity(TFCBlockEntities.LOG_PILE)));
    public static final RegistryObject<Block> BURNING_LOG_PILE = registerNoItem("burning_log_pile", () -> new BurningLogPileBlock(ExtendedProperties.of(MapColor.WOOD).randomTicks().strength(0.6F).sound(SoundType.WOOD).flammable(60, 30).blockEntity(TFCBlockEntities.BURNING_LOG_PILE).serverTicks(BurningLogPileBlockEntity::serverTick)));
    public static final RegistryObject<Block> FIREPIT = register("firepit", () -> new FirepitBlock(ExtendedProperties.of(MapColor.DIRT).strength(0.4F, 0.4F).sound(SoundType.NETHER_WART).randomTicks().noOcclusion().lightLevel(litBlockEmission(15)).blockEntity(TFCBlockEntities.FIREPIT).pathType(BlockPathTypes.DAMAGE_FIRE).<AbstractFirepitBlockEntity<?>>ticks(AbstractFirepitBlockEntity::serverTick, AbstractFirepitBlockEntity::clientTick)));
    public static final RegistryObject<Block> GRILL = register("grill", () -> new GrillBlock(ExtendedProperties.of(MapColor.DIRT).strength(0.4F, 0.4F).sound(SoundType.NETHER_WART).randomTicks().noOcclusion().lightLevel(litBlockEmission(15)).blockEntity(TFCBlockEntities.GRILL).pathType(BlockPathTypes.DAMAGE_FIRE).<AbstractFirepitBlockEntity<?>>ticks(AbstractFirepitBlockEntity::serverTick, AbstractFirepitBlockEntity::clientTick)));
    public static final RegistryObject<Block> POT = register("pot", () -> new PotBlock(ExtendedProperties.of(MapColor.DIRT).strength(0.4F, 0.4F).sound(SoundType.NETHER_WART).randomTicks().noOcclusion().lightLevel(litBlockEmission(15)).blockEntity(TFCBlockEntities.POT).pathType(BlockPathTypes.DAMAGE_FIRE).<AbstractFirepitBlockEntity<?>>ticks(AbstractFirepitBlockEntity::serverTick, AbstractFirepitBlockEntity::clientTick)));
    public static final RegistryObject<Block> BELLOWS = register("bellows", () -> new BellowsBlock(ExtendedProperties.of(MapColor.WOOD).noOcclusion().dynamicShape().pushReaction(PushReaction.DESTROY).sound(SoundType.WOOD).strength(3.0f).blockEntity(TFCBlockEntities.BELLOWS).ticks(BellowsBlockEntity::tickBoth)));
    public static final RegistryObject<Block> POWDERKEG = register("powderkeg", () -> new PowderkegBlock(ExtendedProperties.of(MapColor.WOOD).noOcclusion().dynamicShape().sound(SoundType.WOOD).strength(2.5f).blockEntity(TFCBlockEntities.POWDERKEG).serverTicks(PowderkegBlockEntity::serverTick)), block -> new TooltipBlockItem(block, new Item.Properties()));
    public static final RegistryObject<Block> BARREL_RACK = register("barrel_rack", () -> new BarrelRackBlock(ExtendedProperties.of(MapColor.WOOD).sound(SoundType.WOOD).flammableLikePlanks().strength(4f)));

    public static final RegistryObject<Block> PLACED_ITEM = registerNoItem("placed_item", () -> new PlacedItemBlock(ExtendedProperties.of().instabreak().sound(SoundType.STEM).noOcclusion().blockEntity(TFCBlockEntities.PLACED_ITEM)));
    public static final RegistryObject<Block> SCRAPING = registerNoItem("scraping", () -> new ScrapingBlock(ExtendedProperties.of().strength(0.2F).sound(SoundType.STEM).noOcclusion().blockEntity(TFCBlockEntities.SCRAPING)));
    public static final RegistryObject<Block> PIT_KILN = registerNoItem("pit_kiln", () -> new PitKilnBlock(ExtendedProperties.of(MapColor.WOOD).sound(SoundType.WOOD).strength(0.6f).noOcclusion().blockEntity(TFCBlockEntities.PIT_KILN).serverTicks(PitKilnBlockEntity::serverTick)));
    public static final RegistryObject<Block> QUERN = register("quern", () -> new QuernBlock(ExtendedProperties.of().mapColor(MapColor.STONE).strength(0.5F, 2.0F).sound(SoundType.BASALT).noOcclusion().blockEntity(TFCBlockEntities.QUERN).ticks(QuernBlockEntity::serverTick, QuernBlockEntity::clientTick)));
    public static final RegistryObject<Block> CHARCOAL_PILE = registerNoItem("charcoal_pile", () -> new CharcoalPileBlock(Properties.of().mapColor(MapColor.COLOR_BLACK).strength(0.2F).sound(TFCSounds.CHARCOAL).pushReaction(PushReaction.DESTROY).isViewBlocking((state, level, pos) -> state.getValue(CharcoalPileBlock.LAYERS) >= 8).isSuffocating((state, level, pos) -> state.getValue(CharcoalPileBlock.LAYERS) >= 8)));
    public static final RegistryObject<Block> CHARCOAL_FORGE = registerNoItem("charcoal_forge", () -> new CharcoalForgeBlock(ExtendedProperties.of(MapColor.COLOR_BLACK).pushReaction(PushReaction.DESTROY).strength(0.2F).randomTicks().sound(TFCSounds.CHARCOAL).lightLevel(state -> state.getValue(CharcoalForgeBlock.HEAT) * 2).pathType(BlockPathTypes.DAMAGE_FIRE).blockEntity(TFCBlockEntities.CHARCOAL_FORGE).serverTicks(CharcoalForgeBlockEntity::serverTick)));

    public static final RegistryObject<Block> TORCH = registerNoItem("torch", () -> new TFCTorchBlock(ExtendedProperties.of().noCollission().instabreak().randomTicks().lightLevel(state -> 14).sound(SoundType.WOOD).blockEntity(TFCBlockEntities.TICK_COUNTER), ParticleTypes.FLAME));
    public static final RegistryObject<Block> WALL_TORCH = registerNoItem("wall_torch", () -> new TFCWallTorchBlock(ExtendedProperties.of().noCollission().instabreak().randomTicks().lightLevel(state -> 14).sound(SoundType.WOOD).dropsLike(TORCH).blockEntity(TFCBlockEntities.TICK_COUNTER), ParticleTypes.FLAME));
    public static final RegistryObject<Block> DEAD_TORCH = registerNoItem("dead_torch", () -> new DeadTorchBlock(Properties.of().noCollission().instabreak().sound(SoundType.WOOD), ParticleTypes.FLAME));
    public static final RegistryObject<Block> DEAD_WALL_TORCH = registerNoItem("dead_wall_torch", () -> new DeadWallTorchBlock(Properties.of().noCollission().instabreak().sound(SoundType.WOOD).lootFrom(DEAD_TORCH), ParticleTypes.FLAME));
    public static final RegistryObject<Block> JACK_O_LANTERN = register("jack_o_lantern", () -> new JackOLanternBlock(ExtendedProperties.of(MapColor.COLOR_ORANGE).strength(1.0F).sound(SoundType.WOOD).randomTicks().lightLevel(alwaysLit()).blockEntity(TFCBlockEntities.TICK_COUNTER), () -> Blocks.CARVED_PUMPKIN));
    public static final RegistryObject<Block> BRONZE_BELL = register("bronze_bell", () -> new TFCBellBlock(ExtendedProperties.of(MapColor.GOLD).requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.ANVIL).blockEntity(TFCBlockEntities.BELL).ticks(BellBlockEntity::serverTick, BellBlockEntity::clientTick), 0.8f, "bronze"));
    public static final RegistryObject<Block> BRASS_BELL = register("brass_bell", () -> new TFCBellBlock(ExtendedProperties.of(MapColor.GOLD).requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.ANVIL).blockEntity(TFCBlockEntities.BELL).ticks(BellBlockEntity::serverTick, BellBlockEntity::clientTick), 0.6f, "brass"));

    public static final RegistryObject<Block> CRUCIBLE = register("crucible", () -> new CrucibleBlock(ExtendedProperties.of(MapColor.METAL).strength(3).sound(SoundType.METAL).blockEntity(TFCBlockEntities.CRUCIBLE).serverTicks(CrucibleBlockEntity::serverTick)), block -> new TooltipBlockItem(block, new Item.Properties()));
    public static final RegistryObject<Block> COMPOSTER = register("composter", () -> new TFCComposterBlock(ExtendedProperties.of(MapColor.WOOD).strength(0.6F).noOcclusion().sound(SoundType.WOOD).randomTicks().flammable(60, 90).blockEntity(TFCBlockEntities.COMPOSTER)));
    public static final RegistryObject<Block> BLOOMERY = register("bloomery", () -> new BloomeryBlock(ExtendedProperties.of(MapColor.METAL).strength(3).sound(SoundType.METAL).lightLevel(litBlockEmission(15)).blockEntity(TFCBlockEntities.BLOOMERY).serverTicks(BloomeryBlockEntity::serverTick)));
    public static final RegistryObject<Block> BLAST_FURNACE = register("blast_furnace", () -> new BlastFurnaceBlock(ExtendedProperties.of(MapColor.METAL).strength(5f).sound(SoundType.METAL).lightLevel(litBlockEmission(15)).blockEntity(TFCBlockEntities.BLAST_FURNACE).serverTicks(BlastFurnaceBlockEntity::serverTick)));
    public static final RegistryObject<Block> BLOOM = register("bloom", () -> new BloomBlock(ExtendedProperties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(3F, 6F).noOcclusion().blockEntity(TFCBlockEntities.BLOOM)));
    public static final RegistryObject<Block> MOLTEN = register("molten", () -> new MoltenBlock(ExtendedProperties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(-1.0F, 3600000.0F).noOcclusion().lightLevel(litBlockEmission(15)).pathType(BlockPathTypes.DAMAGE_FIRE)));
    public static final RegistryObject<Block> POWDER_BOWL = register("powder_bowl", () -> new PowderBowlBlock(ExtendedProperties.of().mapColor(MapColor.STONE).strength(3f).noOcclusion().blockEntity(TFCBlockEntities.POWDER_BOWL)));

    public static final RegistryObject<Block> NEST_BOX = register("nest_box", () -> new NestBoxBlock(ExtendedProperties.of(MapColor.WOOD).strength(3f).noOcclusion().sound(TFCSounds.THATCH).blockEntity(TFCBlockEntities.NEST_BOX).serverTicks(NestBoxBlockEntity::serverTick).flammable(60, 30)));

    public static final RegistryObject<Block> LIGHT = register("light", () -> new TFCLightBlock(Properties.copy(Blocks.LIGHT).replaceable().lightLevel(state -> state.getValue(TFCLightBlock.LEVEL)).randomTicks()));
    public static final RegistryObject<Block> FRESHWATER_BUBBLE_COLUMN = registerNoItem("freshwater_bubble_column", () -> new TFCBubbleColumnBlock(Properties.copy(Blocks.BUBBLE_COLUMN).noCollission().noLootTable(), () -> Fluids.WATER));
    public static final RegistryObject<Block> SALTWATER_BUBBLE_COLUMN = registerNoItem("saltwater_bubble_column", () -> new TFCBubbleColumnBlock(Properties.copy(Blocks.BUBBLE_COLUMN).noCollission().noLootTable(), TFCFluids.SALT_WATER::getSource));

    public static final RegistryObject<Block> SHEET_PILE = registerNoItem("sheet_pile", () -> new SheetPileBlock(ExtendedProperties.of(MapColor.METAL).strength(4, 60).sound(SoundType.METAL).noOcclusion().blockEntity(TFCBlockEntities.SHEET_PILE)));
    public static final RegistryObject<Block> INGOT_PILE = registerNoItem("ingot_pile", () -> new IngotPileBlock(ExtendedProperties.of(MapColor.METAL).strength(4, 60).sound(SoundType.METAL).noOcclusion().blockEntity(TFCBlockEntities.INGOT_PILE)));
    public static final RegistryObject<Block> DOUBLE_INGOT_PILE = registerNoItem("double_ingot_pile", () -> new DoubleIngotPileBlock(ExtendedProperties.of(MapColor.METAL).strength(4, 60).sound(SoundType.METAL).noOcclusion().blockEntity(TFCBlockEntities.INGOT_PILE)));

    public static final RegistryObject<Block> CAKE = register("cake", () -> new TFCCakeBlock(Properties.copy(Blocks.CAKE).strength(0.5f).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CANDLE_CAKE = registerNoItem("candle_cake", () -> new TFCCandleCakeBlock(ExtendedProperties.of(Blocks.CANDLE_CAKE).strength(0.5f).sound(SoundType.WOOL).randomTicks().lightLevel(litBlockEmission(3)).blockEntity(TFCBlockEntities.TICK_COUNTER)));
    public static final RegistryObject<Block> CANDLE = register("candle", () -> new TFCCandleBlock(ExtendedProperties.of(Blocks.CANDLE).mapColor(MapColor.SAND).randomTicks().noOcclusion().strength(0.1F).sound(SoundType.CANDLE).lightLevel(TFCCandleBlock.LIGHTING_SCALE).blockEntity(TFCBlockEntities.TICK_COUNTER)), b -> new CandleBlockItem(new Item.Properties(), b, TFCBlocks.CANDLE_CAKE));

    public static final Map<DyeColor, RegistryObject<Block>> DYED_CANDLE_CAKES = Helpers.mapOfKeys(DyeColor.class, color ->
        registerNoItem("candle_cake/" + color.getName(), () -> new TFCCandleCakeBlock(ExtendedProperties.of(MapColor.SAND).randomTicks().noOcclusion().strength(0.5F).sound(SoundType.WOOL).lightLevel(litBlockEmission(3)).blockEntity(TFCBlockEntities.TICK_COUNTER)))
    );
    public static final Map<DyeColor, RegistryObject<Block>> DYED_CANDLE = Helpers.mapOfKeys(DyeColor.class, color ->
        register("candle/" + color.getName(), () -> new TFCCandleBlock(ExtendedProperties.of(MapColor.SAND).randomTicks().noOcclusion().strength(0.1F).sound(SoundType.CANDLE).lightLevel(TFCCandleBlock.LIGHTING_SCALE).blockEntity(TFCBlockEntities.TICK_COUNTER)), b -> new CandleBlockItem(new Item.Properties(), b, TFCBlocks.DYED_CANDLE_CAKES.get(color)))
    );

    public static final RegistryObject<Block> JARS = registerNoItem("jars", () -> new JarsBlock(ExtendedProperties.of().noOcclusion().instabreak().sound(SoundType.GLASS).randomTicks().blockEntity(TFCBlockEntities.JARS)));

    public static final RegistryObject<Block> LARGE_VESSEL = register("ceramic/large_vessel", () -> new LargeVesselBlock(ExtendedProperties.of(MapColor.CLAY).strength(2.5F).noOcclusion().blockEntity(TFCBlockEntities.LARGE_VESSEL)), block -> new TooltipBlockItem(block, new Item.Properties()));
    public static final Map<DyeColor, RegistryObject<Block>> GLAZED_LARGE_VESSELS = Helpers.mapOfKeys(DyeColor.class, color ->
        register("ceramic/large_vessel/" + color.getName(), () -> new LargeVesselBlock(ExtendedProperties.of(MapColor.CLAY).strength(2.5F).noOcclusion().blockEntity(TFCBlockEntities.LARGE_VESSEL)), block -> new TooltipBlockItem(block, new Item.Properties()))
    );

    // Fluids

    public static final Map<Metal.Default, RegistryObject<LiquidBlock>> METAL_FLUIDS = Helpers.mapOfKeys(Metal.Default.class, metal ->
        registerNoItem("fluid/metal/" + metal.name(), () -> new LiquidBlock(TFCFluids.METALS.get(metal).source(), Properties.copy(Blocks.LAVA).noLootTable()))
    );

    public static final Map<SimpleFluid, RegistryObject<LiquidBlock>> SIMPLE_FLUIDS = Helpers.mapOfKeys(SimpleFluid.class, fluid ->
        registerNoItem("fluid/" + fluid.getId(), () -> new LiquidBlock(TFCFluids.SIMPLE_FLUIDS.get(fluid).source(), Properties.copy(Blocks.WATER).noLootTable()))
    );

    public static final Map<DyeColor, RegistryObject<LiquidBlock>> COLORED_FLUIDS = Helpers.mapOfKeys(DyeColor.class, fluid ->
        registerNoItem("fluid/" + fluid.getName() + "_dye", () -> new LiquidBlock(TFCFluids.COLORED_FLUIDS.get(fluid).source(), Properties.copy(Blocks.WATER).noLootTable()))
    );

    public static final Map<Alcohol, RegistryObject<LiquidBlock>> ALCOHOLS = Helpers.mapOfKeys(Alcohol.class, fluid ->
        registerNoItem("fluid/" + fluid.getId(), () -> new LiquidBlock(TFCFluids.ALCOHOLS.get(fluid).source(), Properties.copy(Blocks.WATER)))
    );

    public static final RegistryObject<LiquidBlock> SALT_WATER = registerNoItem("fluid/salt_water", () -> new LiquidBlock(TFCFluids.SALT_WATER.flowing(), Properties.copy(Blocks.WATER).noLootTable()));
    public static final RegistryObject<LiquidBlock> SPRING_WATER = registerNoItem("fluid/spring_water", () -> new HotWaterBlock(TFCFluids.SPRING_WATER.source(), Properties.copy(Blocks.WATER).noLootTable()));

    public static final RegistryObject<RiverWaterBlock> RIVER_WATER = registerNoItem("fluid/river_water", () -> new RiverWaterBlock(Properties.copy(Blocks.WATER).noLootTable()));

    public static void registerFlowerPotFlowers()
    {
        FlowerPotBlock pot = (FlowerPotBlock) Blocks.FLOWER_POT;
        POTTED_PLANTS.forEach((plant, reg) -> pot.addPlant(PLANTS.get(plant).getId(), reg));
        WOODS.forEach((wood, map) -> pot.addPlant(map.get(Wood.BlockType.SAPLING).getId(), map.get(Wood.BlockType.POTTED_SAPLING)));
        FRUIT_TREE_POTTED_SAPLINGS.forEach((plant, reg) -> pot.addPlant(FRUIT_TREE_SAPLINGS.get(plant).getId(), reg));
        pot.addPlant(BANANA_SAPLING.getId(), BANANA_POTTED_SAPLING);
    }

    public static boolean always(BlockState state, BlockGetter level, BlockPos pos)
    {
        return true;
    }

    public static boolean never(BlockState state, BlockGetter level, BlockPos pos)
    {
        return false;
    }

    public static boolean never(BlockState state, BlockGetter world, BlockPos pos, EntityType<?> type)
    {
        return false;
    }

    public static boolean onlyColdMobs(BlockState state, BlockGetter world, BlockPos pos, EntityType<?> type)
    {
        return Helpers.isEntity(type, TFCTags.Entities.SPAWNS_ON_COLD_BLOCKS);
    }

    public static ToIntFunction<BlockState> alwaysLit()
    {
        return s -> 15;
    }

    public static ToIntFunction<BlockState> lavaLoggedBlockEmission()
    {
        // This is resolved only at registration time, so we can't use the fast check (.getFluid() == Fluids.LAVA) and we have to use the slow check instead
        return state -> state.getValue(TFCBlockStateProperties.WATER_AND_LAVA).is(((IFluidLoggable) state.getBlock()).getFluidProperty().keyFor(Fluids.LAVA)) ? 15 : 0;
    }

    public static ToIntFunction<BlockState> litBlockEmission(int lightValue)
    {
        return (state) -> state.getValue(BlockStateProperties.LIT) ? lightValue : 0;
    }

    private static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<T> blockSupplier)
    {
        return register(name, blockSupplier, (Function<T, ? extends BlockItem>) null);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockSupplier)
    {
        return register(name, blockSupplier, block -> new BlockItem(block, new Item.Properties()));
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockSupplier, Item.Properties blockItemProperties)
    {
        return register(name, blockSupplier, block -> new BlockItem(block, blockItemProperties));
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> blockSupplier, @Nullable Function<T, ? extends BlockItem> blockItemFactory)
    {
        return RegistrationHelpers.registerBlock(TFCBlocks.BLOCKS, TFCItems.ITEMS, name, blockSupplier, blockItemFactory);
    }
}