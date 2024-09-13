/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
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
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
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
import net.dries007.tfc.common.blockentities.LogPileBlockEntity;
import net.dries007.tfc.common.blockentities.NestBoxBlockEntity;
import net.dries007.tfc.common.blockentities.PitKilnBlockEntity;
import net.dries007.tfc.common.blockentities.PowderkegBlockEntity;
import net.dries007.tfc.common.blockentities.QuernBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.rotation.PumpBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.TripHammerBlockEntity;
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
import net.dries007.tfc.common.blocks.plant.KrummholzBlock;
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
import net.dries007.tfc.common.blocks.rotation.CrankshaftBlock;
import net.dries007.tfc.common.blocks.rotation.FluidPipeBlock;
import net.dries007.tfc.common.blocks.rotation.FluidPumpBlock;
import net.dries007.tfc.common.blocks.soil.ColoredBlock;
import net.dries007.tfc.common.blocks.soil.ConnectedGrassBlock;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.blocks.wood.TFCCeilingHangingSignBlock;
import net.dries007.tfc.common.blocks.wood.TFCSlabBlock;
import net.dries007.tfc.common.blocks.wood.TFCStairBlock;
import net.dries007.tfc.common.blocks.wood.TFCWallHangingSignBlock;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.CandleBlockItem;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.items.TooltipBlockItem;
import net.dries007.tfc.mixin.accessor.BlockBehaviourAccessor;
import net.dries007.tfc.mixin.accessor.BlockStateBaseAccessor;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.registry.RegistrationHelpers;
import net.dries007.tfc.util.registry.RegistryHolder;

import static net.dries007.tfc.TerraFirmaCraft.*;

/**
 * Collection of all TFC blocks.
 * Unused is as the registry object fields themselves may be unused but they are required to register each item.
 * Whenever possible, avoid using hardcoded references to these, prefer tags or recipes.
 */
@SuppressWarnings("unused")
public final class TFCBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MOD_ID);

    // Earth

    public static final Map<SoilBlockType, Map<SoilBlockType.Variant, Id<Block>>> SOIL = Helpers.mapOf(SoilBlockType.class, type ->
        Helpers.mapOf(SoilBlockType.Variant.class, variant ->
            register((type.name() + "/" + variant.name()), () -> type.create(variant))
        )
    );

    public static final Map<SoilBlockType.Variant, DecorationBlockHolder> MUD_BRICK_DECORATIONS = Helpers.mapOf(SoilBlockType.Variant.class, variant -> registerDecorations(
        "mud_bricks/" + variant.name(),
        () -> new SlabBlock(Properties.of().mapColor(MapColor.DIRT).strength(2.6f).sound(SoundType.WART_BLOCK)),
        () -> new StairBlock(SOIL.get(SoilBlockType.MUD_BRICKS).get(variant).get().defaultBlockState(), Properties.of().mapColor(MapColor.DIRT).strength(2.6f).sound(SoundType.WART_BLOCK).instrument(NoteBlockInstrument.BASEDRUM)),
        () -> new WallBlock(Properties.of().mapColor(MapColor.DIRT).strength(2.6f).sound(SoundType.WART_BLOCK)),
        new Item.Properties()
    ));

    public static final Id<Block> SMOOTH_MUD_BRICKS = register("smooth_mud_bricks", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).sound(SoundType.MUD_BRICKS).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.6f)));
    public static final Id<Block> PEAT = register("peat", () -> new Block(Properties.of().mapColor(MapColor.TERRACOTTA_BLACK).strength(3.0F).sound(TFCSounds.PEAT)));
    public static final Id<Block> PEAT_GRASS = register("peat_grass", () -> new ConnectedGrassBlock(Properties.of().mapColor(MapColor.GRASS).randomTicks().strength(3.0F).sound(TFCSounds.PEAT), PEAT, null, null));

    public static final Id<Block> RED_KAOLIN_CLAY = register("red_kaolin_clay", () -> new Block(Properties.of().mapColor(MapColor.COLOR_RED).strength(4.0F).sound(SoundType.GRAVEL)));
    public static final Id<Block> PINK_KAOLIN_CLAY = register("pink_kaolin_clay", () -> new Block(Properties.of().mapColor(MapColor.COLOR_PINK).strength(4.5F).sound(SoundType.GRAVEL)));
    public static final Id<Block> WHITE_KAOLIN_CLAY = register("white_kaolin_clay", () -> new Block(Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(5.0F).sound(SoundType.GRAVEL)));
    public static final Id<Block> KAOLIN_CLAY_GRASS = register("kaolin_clay_grass", () -> new ConnectedGrassBlock(Properties.of().mapColor(MapColor.GRASS).randomTicks().strength(5.0F).sound(SoundType.GRAVEL), RED_KAOLIN_CLAY, null, null));

    public static final Map<SandBlockType, Id<Block>> SAND = Helpers.mapOf(SandBlockType.class, type ->
        register(("sand/" + type.name()), type::create)
    );

    public static final Map<SandBlockType, Map<SandstoneBlockType, Id<Block>>> SANDSTONE = Helpers.mapOf(SandBlockType.class, color ->
        Helpers.mapOf(SandstoneBlockType.class, type ->
            register((type.name() + "_sandstone/" + color.name()), () -> new Block(type.properties(color)))
        )
    );

    public static final Map<SandBlockType, Map<SandstoneBlockType, DecorationBlockHolder>> SANDSTONE_DECORATIONS = Helpers.mapOf(SandBlockType.class, color ->
        Helpers.mapOf(SandstoneBlockType.class, type -> registerDecorations(
            type.name() + "_sandstone/" + color.name(),
            () -> new SlabBlock(type.properties(color)),
            () -> new StairBlock(SANDSTONE.get(color).get(type).get().defaultBlockState(), type.properties(color)),
            () -> new WallBlock(type.properties(color)),
            new Item.Properties()
        ))
    );

    public static final Map<GroundcoverBlockType, Id<Block>> GROUNDCOVER = Helpers.mapOf(GroundcoverBlockType.class, type ->
        register(("groundcover/" + type.name()), () -> new GroundcoverBlock(type), type.createBlockItem())
    );

    public static final Id<Block> SEA_ICE = register("sea_ice", () -> new SeaIceBlock(Properties.ofFullCopy(Blocks.ICE).isValidSpawn(TFCBlocks::onlyColdMobs)));

    public static final Id<SnowPileBlock> SNOW_PILE = register("snow_pile", () -> new SnowPileBlock(ExtendedProperties.of(Blocks.SNOW).randomTicks().blockEntity(TFCBlockEntities.PILE).cloneItem(Blocks.SNOW)));
    public static final Id<IcePileBlock> ICE_PILE = register("ice_pile", () -> new IcePileBlock(ExtendedProperties.of(Blocks.ICE).randomTicks().blockEntity(TFCBlockEntities.PILE).cloneItem(Blocks.ICE)));
    public static final Id<ThinSpikeBlock> ICICLE = register("icicle", () -> new IcicleBlock(Properties.of().mapColor(MapColor.ICE).pushReaction(PushReaction.DESTROY).noLootTable().strength(0.4f).sound(SoundType.GLASS).noOcclusion().randomTicks()));

    public static final Id<ThinSpikeBlock> CALCITE = register("calcite", () -> new ThinSpikeBlock(Properties.of().mapColor(MapColor.ICE).pushReaction(PushReaction.DESTROY).noLootTable().strength(0.2f).sound(TFCSounds.THIN)));

    // Ores

    public static final Map<Rock, Map<Ore, Id<Block>>> ORES = Helpers.mapOf(Rock.class, rock ->
        Helpers.mapOf(Ore.class, ore -> !ore.isGraded(), ore ->
            register(("ore/" + ore.name() + "/" + rock.name()), () -> ore.create(rock))
        )
    );
    public static final Map<Rock, Map<Ore, Map<Ore.Grade, Id<Block>>>> GRADED_ORES = Helpers.mapOf(Rock.class, rock ->
        Helpers.mapOf(Ore.class, Ore::isGraded, ore ->
            Helpers.mapOf(Ore.Grade.class, grade ->
                register(("ore/" + grade.name() + "_" + ore.name() + "/" + rock.name()), () -> ore.create(rock))
            )
        )
    );
    public static final Map<Ore, Id<Block>> SMALL_ORES = Helpers.mapOf(Ore.class, Ore::isGraded, type ->
        register(("ore/small_" + type.name()), () -> GroundcoverBlock.looseOre(Properties.of().mapColor(MapColor.GRASS).strength(0.05F, 0.0F).sound(SoundType.NETHER_ORE).noCollission().pushReaction(PushReaction.DESTROY)))
    );
    public static final Map<Rock, Map<OreDeposit, Id<Block>>> ORE_DEPOSITS = Helpers.mapOf(Rock.class, rock ->
        Helpers.mapOf(OreDeposit.class, ore ->
            register("deposit/" + ore.name() + "/" + rock.name(), () -> new Block(Block.Properties.of().mapColor(MapColor.STONE).sound(SoundType.GRAVEL).strength(rock.category().hardness(2.0f)))) // Same hardness as gravel
        )
    );

    public static final Map<Rock, Map<Rock.BlockType, Id<Block>>> ROCK_BLOCKS = Helpers.mapOf(Rock.class, rock ->
        Helpers.mapOf(Rock.BlockType.class, type ->
            register(("rock/" + type.name() + "/" + rock.name()), () -> type.create(rock), rock.createItemProperties())
        )
    );

    public static final Map<Rock, Map<Rock.BlockType, DecorationBlockHolder>> ROCK_DECORATIONS = Helpers.mapOf(Rock.class, rock ->
        Helpers.mapOf(Rock.BlockType.class, Rock.BlockType::hasVariants, type -> registerDecorations(
            "rock/" + type.name() + "/" + rock.name(),
            () -> type.createSlab(rock),
            () -> type.createStairs(rock),
            () -> type.createWall(rock),
            rock.createItemProperties()
        ))
    );

    public static final Map<Rock, Id<Block>> ROCK_ANVILS = Helpers.mapOf(Rock.class, rock -> rock.category() == RockCategory.IGNEOUS_EXTRUSIVE || rock.category() == RockCategory.IGNEOUS_INTRUSIVE, rock ->
        register("rock/anvil/" + rock.name(), () -> new RockAnvilBlock(ExtendedProperties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).strength(2, 10).requiresCorrectToolForDrops().cloneItem(TFCBlocks.ROCK_BLOCKS.get(rock).get(Rock.BlockType.RAW)).blockEntity(TFCBlockEntities.ANVIL)), b -> new BlockItem(b, rock.createItemProperties()))
    );

    public static final Map<Rock, Id<Block>> MAGMA_BLOCKS = Helpers.mapOf(Rock.class, rock -> rock.category() == RockCategory.IGNEOUS_EXTRUSIVE || rock.category() == RockCategory.IGNEOUS_INTRUSIVE, rock ->
        register("rock/magma/" + rock.name(), () -> new TFCMagmaBlock(Properties.of().mapColor(MapColor.NETHER).requiresCorrectToolForDrops().lightLevel(s -> 6).randomTicks().strength(0.5F).isValidSpawn((state, level, pos, type) -> type.fireImmune()).hasPostProcess(TFCBlocks::always)), b -> new BlockItem(b, rock.createItemProperties()))
    );

    // Metals

    public static final Map<Metal, Map<Metal.BlockType, Id<Block>>> METALS = Helpers.mapOf(Metal.class, metal ->
        Helpers.mapOf(Metal.BlockType.class, type -> type.has(metal), type ->
            register(type.createName(metal), type.create(metal), type.createBlockItem(new Item.Properties()))
        )
    );

    // Wood

    public static final Map<Wood, Map<Wood.BlockType, Id<Block>>> WOODS = Helpers.mapOf(Wood.class, wood ->
        Helpers.mapOf(Wood.BlockType.class, type ->
            register(type.nameFor(wood), type.create(wood), type.createBlockItem(wood, new Item.Properties()))
        )
    );

    public static final Map<Wood, Map<Metal, Id<TFCCeilingHangingSignBlock>>> CEILING_HANGING_SIGNS = registerHangingSigns("hanging_sign", TFCCeilingHangingSignBlock::new);
    public static final Map<Wood, Map<Metal, Id<TFCWallHangingSignBlock>>> WALL_HANGING_SIGNS = registerHangingSigns("wall_hanging_sign", TFCWallHangingSignBlock::new);

    private static <B extends SignBlock> Map<Wood, Map<Metal, Id<B>>> registerHangingSigns(String variant, BiFunction<ExtendedProperties, WoodType, B> factory)
    {
        return Helpers.mapOf(Wood.class, wood ->
            Helpers.mapOf(Metal.class, Metal::allParts, metal -> register(
                "wood/" + variant + "/" + metal.getSerializedName() + "/" + wood.getSerializedName(),
                () -> factory.apply(ExtendedProperties.of(wood.woodColor()).sound(SoundType.WOOD).noCollission().strength(1F).flammableLikePlanks().blockEntity(TFCBlockEntities.HANGING_SIGN).ticks(SignBlockEntity::tick), wood.getVanillaWoodType()),
                (Function<B, BlockItem>) null)
            )
        );
    }

    public static final Id<Block> PALM_MOSAIC = register("wood/planks/palm_mosaic", () -> new Block(Properties.ofFullCopy(Blocks.BAMBOO_MOSAIC)));
    public static final Id<Block> PALM_MOSAIC_STAIRS = register("wood/planks/palm_mosaic_stairs", () -> new TFCStairBlock(() -> PALM_MOSAIC.get().defaultBlockState(), ExtendedProperties.of(Blocks.BAMBOO_MOSAIC_STAIRS).flammableLikePlanks()));
    public static final Id<Block> PALM_MOSAIC_SLAB = register("wood/planks/palm_mosaic_slab", () -> new TFCSlabBlock(ExtendedProperties.of(Blocks.BAMBOO_MOSAIC_SLAB).flammableLikePlanks()));

    public static final Id<Block> TREE_ROOTS = register("tree_roots", () -> new TreeRootsBlock(ExtendedProperties.of().mapColor(MapColor.PODZOL).instrument(NoteBlockInstrument.BASS).strength(0.7F).randomTicks().sound(SoundType.MANGROVE_ROOTS).noOcclusion().isSuffocating(TFCBlocks::never).isViewBlocking(TFCBlocks::never).noOcclusion().ignitedByLava()));

    // Flora

    public static final Map<Plant, Id<Block>> PLANTS = Helpers.mapOf(Plant.class, plant ->
        register(("plant/" + plant.name()), plant::create, plant.createBlockItem(new Item.Properties()))
    );

    public static final Map<Plant, Id<Block>> POTTED_PLANTS = Helpers.mapOf(Plant.class, Plant::hasFlowerPot, plant ->
        registerNoItem(("plant/potted/" + plant.name()), () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, PLANTS.get(plant), BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_ACACIA_SAPLING)))
    );

    public static final Map<Crop, Id<Block>> CROPS = Helpers.mapOf(Crop.class, crop ->
        registerNoItem("crop/" + crop.name(), crop::create)
    );

    public static final Map<Crop, Id<Block>> DEAD_CROPS = Helpers.mapOf(Crop.class, crop ->
        registerNoItem("dead_crop/" + crop.name(), crop::createDead)
    );

    public static final Map<Crop, Id<Block>> WILD_CROPS = Helpers.mapOf(Crop.class, crop ->
        register("wild_crop/" + crop.name(), crop::createWild)
    );

    public static final Map<Coral, Map<Coral.BlockType, Id<Block>>> CORAL = Helpers.mapOf(Coral.class, color ->
        Helpers.mapOf(Coral.BlockType.class, type ->
            register("coral/" + color.toString() + "_" + type.toString(), type.create(color), type.createBlockItem(new Item.Properties()))
        )
    );

    public static final Id<Block> PINE_KRUMMHOLZ = register("plant/pine_krummholz", () -> new KrummholzBlock(ExtendedProperties.of().mapColor(MapColor.PLANT).strength(8f).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY).flammableLikeLogs()));
    public static final Id<Block> SPRUCE_KRUMMHOLZ = register("plant/spruce_krummholz", () -> new KrummholzBlock(ExtendedProperties.of().mapColor(MapColor.PLANT).strength(8f).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY).flammableLikeLogs()));
    public static final Id<Block> WHITE_CEDAR_KRUMMHOLZ = register("plant/white_cedar_krummholz", () -> new KrummholzBlock(ExtendedProperties.of().mapColor(MapColor.PLANT).strength(8f).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY).flammableLikeLogs()));
    public static final Id<Block> DOUGLAS_FIR_KRUMMHOLZ = register("plant/douglas_fir_krummholz", () -> new KrummholzBlock(ExtendedProperties.of().mapColor(MapColor.PLANT).strength(8f).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY).flammableLikeLogs()));
    public static final Id<Block> ASPEN_KRUMMHOLZ = register("plant/aspen_krummholz", () -> new KrummholzBlock(ExtendedProperties.of().mapColor(MapColor.PLANT).strength(8f).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY).flammableLikeLogs()));
    public static final Id<Block> POTTED_PINE_KRUMMHOLZ = registerNoItem("plant/potted/pine_krummholz", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, PINE_KRUMMHOLZ, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_ACACIA_SAPLING)));
    public static final Id<Block> POTTED_SPRUCE_KRUMMHOLZ = registerNoItem("plant/potted/spruce_krummholz", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, PINE_KRUMMHOLZ, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_ACACIA_SAPLING)));
    public static final Id<Block> POTTED_WHITE_CEDAR_KRUMMHOLZ = registerNoItem("plant/potted/white_cedar_krummholz", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, PINE_KRUMMHOLZ, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_ACACIA_SAPLING)));
    public static final Id<Block> POTTED_DOUGLAS_FIR_KRUMMHOLZ = registerNoItem("plant/potted/douglas_fir_krummholz", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, PINE_KRUMMHOLZ, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_ACACIA_SAPLING)));
    public static final Id<Block> POTTED_ASPEN_KRUMMHOLZ = registerNoItem("plant/potted/aspen_krummholz", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, PINE_KRUMMHOLZ, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_ACACIA_SAPLING)));

    public static final Id<Block> ROTTEN_PUMPKIN = registerNoItem("rotten_pumpkin", () -> new Block(Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(1.0F).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY)));
    public static final Id<Block> ROTTEN_MELON = registerNoItem("rotten_melon", () -> new Block(Properties.of().mapColor(MapColor.COLOR_GREEN).strength(1.0F).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY)));
    public static final Id<Block> PUMPKIN = register("pumpkin", () -> new TFCPumpkinBlock(ExtendedProperties.of(MapColor.COLOR_ORANGE).mapColor(MapColor.COLOR_ORANGE).strength(1.0F).sound(SoundType.WOOD).blockEntity(TFCBlockEntities.DECAYING).serverTicks(DecayingBlockEntity::serverTick).instrument(NoteBlockInstrument.DIDGERIDOO).pushReaction(PushReaction.DESTROY), ROTTEN_PUMPKIN), b -> new BlockItem(b, new Item.Properties()));
    public static final Id<Block> MELON = register("melon", () -> new DecayingBlock(ExtendedProperties.of(MapColor.COLOR_ORANGE).mapColor(MapColor.COLOR_GREEN).strength(1.0F).sound(SoundType.WOOD).blockEntity(TFCBlockEntities.DECAYING).serverTicks(DecayingBlockEntity::serverTick).instrument(NoteBlockInstrument.DIDGERIDOO).pushReaction(PushReaction.DESTROY), ROTTEN_MELON), b -> new BlockItem(b, new Item.Properties()));

    public static final Id<Block> SEA_PICKLE = register("sea_pickle", () -> new TFCSeaPickleBlock(Properties.of().pushReaction(PushReaction.DESTROY).mapColor(MapColor.COLOR_GREEN).pushReaction(PushReaction.DESTROY).lightLevel((state) -> TFCSeaPickleBlock.isDead(state) ? 0 : 3 + 3 * state.getValue(SeaPickleBlock.PICKLES)).sound(SoundType.SLIME_BLOCK).noOcclusion()));

    public static final Map<FruitBlocks.StationaryBush, Id<Block>> STATIONARY_BUSHES = Helpers.mapOf(FruitBlocks.StationaryBush.class, bush -> register("plant/" + bush.name() + "_bush", bush::create));
    public static final Map<FruitBlocks.SpreadingBush, Id<Block>> SPREADING_CANES = Helpers.mapOf(FruitBlocks.SpreadingBush.class, bush -> registerNoItem("plant/" + bush.name() + "_bush_cane", bush::createCane));
    public static final Map<FruitBlocks.SpreadingBush, Id<Block>> SPREADING_BUSHES = Helpers.mapOf(FruitBlocks.SpreadingBush.class, bush -> register("plant/" + bush.name() + "_bush", bush::createBush));
    public static final Id<Block> CRANBERRY_BUSH = register("plant/cranberry_bush", FruitBlocks::createCranberry);

    public static final Id<Block> DEAD_BERRY_BUSH = registerNoItem("plant/dead_berry_bush", () -> new DeadBerryBushBlock(ExtendedProperties.of(MapColor.PLANT).strength(0.6f).noOcclusion().sound(SoundType.SWEET_BERRY_BUSH).randomTicks().blockEntity(TFCBlockEntities.TICK_COUNTER).flammable(120, 90)));
    public static final Id<Block> DEAD_BANANA_PLANT = registerNoItem("plant/dead_banana_plant", () -> new DeadBananaPlantBlock(ExtendedProperties.of(MapColor.PLANT).strength(0.6f).noOcclusion().sound(SoundType.SWEET_BERRY_BUSH).blockEntity(TFCBlockEntities.TICK_COUNTER).flammable(120, 90)));
    public static final Id<Block> DEAD_CANE = registerNoItem("plant/dead_cane", () -> new DeadCaneBlock(ExtendedProperties.of(MapColor.PLANT).strength(0.6f).noOcclusion().sound(SoundType.SWEET_BERRY_BUSH).randomTicks().blockEntity(TFCBlockEntities.TICK_COUNTER).flammable(120, 90)));
    public static final Map<FruitBlocks.Tree, Id<Block>> FRUIT_TREE_LEAVES = Helpers.mapOf(FruitBlocks.Tree.class, tree -> register("plant/" + tree.name() + "_leaves", tree::createLeaves));
    public static final Map<FruitBlocks.Tree, Id<Block>> FRUIT_TREE_BRANCHES = Helpers.mapOf(FruitBlocks.Tree.class, tree -> registerNoItem("plant/" + tree.name() + "_branch", tree::createBranch));
    public static final Map<FruitBlocks.Tree, Id<Block>> FRUIT_TREE_GROWING_BRANCHES = Helpers.mapOf(FruitBlocks.Tree.class, tree -> registerNoItem("plant/" + tree.name() + "_growing_branch", tree::createGrowingBranch));
    public static final Map<FruitBlocks.Tree, Id<Block>> FRUIT_TREE_SAPLINGS = Helpers.mapOf(FruitBlocks.Tree.class, tree -> register("plant/" + tree.name() + "_sapling", tree::createSapling));
    public static final Map<FruitBlocks.Tree, Id<Block>> FRUIT_TREE_POTTED_SAPLINGS = Helpers.mapOf(FruitBlocks.Tree.class, tree -> registerNoItem("plant/potted/" + tree.name() + "_sapling", tree::createPottedSapling));
    public static final Id<Block> BANANA_PLANT = registerNoItem("plant/banana_plant", FruitBlocks::createBananaPlant);
    public static final Id<Block> BANANA_SAPLING = register("plant/banana_sapling", FruitBlocks::createBananaSapling);
    public static final Id<Block> BANANA_POTTED_SAPLING = registerNoItem("plant/potted/banana_sapling", FruitBlocks::createPottedBananaSapling);

    // Decorations

    public static final Id<Block> PLAIN_ALABASTER = register("alabaster/raw", () -> new Block(Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Id<Block> PLAIN_ALABASTER_BRICKS = register("alabaster/bricks", () -> new Block(Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
    public static final Id<Block> PLAIN_POLISHED_ALABASTER = register("alabaster/polished", () -> new Block(Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(1.5F, 6.0F)));

    public static final Id<Block> AGGREGATE = register("aggregate", () -> new ColoredBlock(-8356741, Properties.ofFullCopy(Blocks.GRAVEL).strength(0.6F).sound(SoundType.GRAVEL)));

    public static final Map<DyeColor, Id<Block>> RAW_ALABASTER = Helpers.mapOf(DyeColor.class, color ->
        register(("alabaster/raw/" + color.getName()), () -> new Block(Properties.of().mapColor(color.getMapColor()).requiresCorrectToolForDrops().strength(1.0F, 6.0F)))
    );
    public static final Map<DyeColor, Id<Block>> ALABASTER_BRICKS = Helpers.mapOf(DyeColor.class, color ->
        register(("alabaster/bricks/" + color.getName()), () -> new Block(Properties.of().mapColor(color.getMapColor()).requiresCorrectToolForDrops().strength(1.5F, 6.0F)))
    );
    public static final Map<DyeColor, Id<Block>> POLISHED_ALABASTER = Helpers.mapOf(DyeColor.class, color ->
        register(("alabaster/polished/" + color.getName()), () -> new Block(Properties.of().mapColor(color.getMapColor()).requiresCorrectToolForDrops().strength(1.5F, 6.0F)))
    );

    public static final Map<DyeColor, DecorationBlockHolder> ALABASTER_BRICK_DECORATIONS = Helpers.mapOf(DyeColor.class, color -> registerDecorations(
        "alabaster/bricks/" + color.getName(),
        () -> new SlabBlock(Properties.of().mapColor(color.getMapColor()).requiresCorrectToolForDrops().strength(1.5F, 6.0F)),
        () -> new StairBlock(ALABASTER_BRICKS.get(color).get().defaultBlockState(), Properties.of().mapColor(color.getMapColor()).requiresCorrectToolForDrops().strength(1.5F, 6.0F)),
        () -> new WallBlock(Properties.of().mapColor(color.getMapColor()).requiresCorrectToolForDrops().strength(1.5F, 6.0F)),
        new Item.Properties()
    ));

    public static final Map<DyeColor, DecorationBlockHolder> ALABASTER_POLISHED_DECORATIONS = Helpers.mapOf(DyeColor.class, color -> registerDecorations(
        "alabaster/polished/" + color.getName(),
        () -> new SlabBlock(Properties.of().mapColor(color.getMapColor()).requiresCorrectToolForDrops().strength(1.5F, 6.0F)),
        () -> new StairBlock(ALABASTER_BRICKS.get(color).get().defaultBlockState(), Properties.of().mapColor(color.getMapColor()).requiresCorrectToolForDrops().strength(1.5F, 6.0F)),
        () -> new WallBlock(Properties.of().mapColor(color.getMapColor()).requiresCorrectToolForDrops().strength(1.5F, 6.0F)),
        new Item.Properties()
    ));

    public static final Map<DyeColor, Id<Block>> COLORED_POURED_GLASS = Helpers.mapOf(DyeColor.class, color -> register(color.getSerializedName() + "_poured_glass", () -> new PouredGlassBlock(ExtendedProperties.of().pushReaction(PushReaction.DESTROY).strength(0.3F).sound(SoundType.GLASS).noOcclusion().requiresCorrectToolForDrops(), () -> PouredGlassBlock.getStainedGlass(color))));
    public static final Id<Block> POURED_GLASS = register("poured_glass", () -> new PouredGlassBlock(ExtendedProperties.of().strength(0.3F).sound(SoundType.GLASS).pushReaction(PushReaction.DESTROY).noOcclusion().requiresCorrectToolForDrops(), () -> Items.GLASS_PANE));
    public static final Id<Block> HOT_POURED_GLASS = registerNoItem("hot_poured_glass", () -> new HotPouredGlassBlock(ExtendedProperties.of().strength(0.3F).lightLevel(s -> 10).sound(SoundType.GLASS).pushReaction(PushReaction.DESTROY).noOcclusion().noLootTable().pathType(PathType.DANGER_FIRE).blockEntity(TFCBlockEntities.HOT_POURED_GLASS).ticks(HotPouredGlassBlockEntity::tick)));
    public static final Id<Block> GLASS_BASIN = registerNoItem("glass_basin", () -> new GlassBasinBlock(ExtendedProperties.of().strength(0.3f).lightLevel(s -> 10).sound(SoundType.GLASS).pushReaction(PushReaction.DESTROY).noOcclusion().noLootTable().pathType(PathType.DANGER_FIRE).blockEntity(TFCBlockEntities.GLASS_BASIN).dynamicShape().ticks(GlassBasinBlockEntity::ticks)));

    public static final Id<Block> FIRE_BRICKS = register("fire_bricks", () -> new Block(Properties.of().mapColor(MapColor.COLOR_RED).requiresCorrectToolForDrops().strength(2.0F, 6.0F)));
    public static final Id<Block> FIRE_CLAY_BLOCK = register("fire_clay_block", () -> new Block(Properties.of().mapColor(MapColor.CLAY).strength(0.6F).sound(SoundType.GRAVEL)));

    public static final Id<Block> WATTLE = register("wattle", () -> new WattleBlock(ExtendedProperties.of(MapColor.WOOD).strength(0.3F).noOcclusion().sound(SoundType.SCAFFOLDING).flammable(60, 30)));
    public static final Id<Block> UNSTAINED_WATTLE = register("wattle/unstained", () -> new StainedWattleBlock(ExtendedProperties.of(MapColor.WOOD).strength(0.3F).sound(SoundType.SCAFFOLDING).flammable(60, 30)));
    public static final Map<DyeColor, Id<Block>> STAINED_WATTLE = Helpers.mapOf(DyeColor.class, color ->
        register("wattle/" + color.getName(), () -> new StainedWattleBlock(ExtendedProperties.of(MapColor.WOOD).strength(0.3F).sound(SoundType.SCAFFOLDING).flammable(60, 30)))
    );

    // Misc

    public static final Id<Block> THATCH = register("thatch", () -> new ThatchBlock(ExtendedProperties.of(MapColor.SAND).strength(0.6F, 0.4F).noOcclusion().isViewBlocking(TFCBlocks::never).sound(TFCSounds.THATCH).flammable(50, 100)));
    public static final Id<Block> THATCH_BED = register("thatch_bed", () -> new ThatchBedBlock(ExtendedProperties.of(MapColor.SAND).sound(TFCSounds.THATCH).strength(0.6F, 0.4F).flammable(50, 100).pushReaction(PushReaction.DESTROY).blockEntity(TFCBlockEntities.THATCH_BED)), b -> new BedItem(b, new Item.Properties()));
    public static final Id<Block> LOG_PILE = registerNoItem("log_pile", () -> new LogPileBlock(ExtendedProperties.of(MapColor.WOOD).strength(0.6F).sound(SoundType.WOOD).flammable(60, 30).blockEntity(TFCBlockEntities.LOG_PILE)));
    public static final Id<Block> BURNING_LOG_PILE = registerNoItem("burning_log_pile", () -> new BurningLogPileBlock(ExtendedProperties.of(MapColor.WOOD).randomTicks().strength(0.6F).sound(SoundType.WOOD).flammableLikeLogs().blockEntity(TFCBlockEntities.BURNING_LOG_PILE).serverTicks(BurningLogPileBlockEntity::serverTick).cloneItem(Items.CHARCOAL).noOcclusion()));
    public static final Id<Block> FIREPIT = register("firepit", () -> new FirepitBlock(ExtendedProperties.of(MapColor.DIRT).strength(0.4F, 0.4F).sound(SoundType.NETHER_WART).randomTicks().noOcclusion().lightLevel(litBlockEmission(15)).blockEntity(TFCBlockEntities.FIREPIT).pathType(PathType.DAMAGE_FIRE).<AbstractFirepitBlockEntity<?>>ticks(AbstractFirepitBlockEntity::serverTick, AbstractFirepitBlockEntity::clientTick)));
    public static final Id<Block> GRILL = register("grill", () -> new GrillBlock(ExtendedProperties.of(MapColor.DIRT).strength(0.4F, 0.4F).sound(SoundType.NETHER_WART).randomTicks().noOcclusion().lightLevel(litBlockEmission(15)).blockEntity(TFCBlockEntities.GRILL).pathType(PathType.DAMAGE_FIRE).<AbstractFirepitBlockEntity<?>>ticks(AbstractFirepitBlockEntity::serverTick, AbstractFirepitBlockEntity::clientTick)));
    public static final Id<Block> POT = register("pot", () -> new PotBlock(ExtendedProperties.of(MapColor.DIRT).strength(0.4F, 0.4F).sound(SoundType.NETHER_WART).randomTicks().noOcclusion().lightLevel(litBlockEmission(15)).blockEntity(TFCBlockEntities.POT).pathType(PathType.DAMAGE_FIRE).<AbstractFirepitBlockEntity<?>>ticks(AbstractFirepitBlockEntity::serverTick, AbstractFirepitBlockEntity::clientTick)));
    public static final Id<Block> BELLOWS = register("bellows", () -> new BellowsBlock(ExtendedProperties.of(MapColor.WOOD).noOcclusion().dynamicShape().pushReaction(PushReaction.DESTROY).sound(SoundType.WOOD).strength(3.0f).blockEntity(TFCBlockEntities.BELLOWS).ticks(BellowsBlockEntity::tickBoth)));
    public static final Id<Block> POWDERKEG = register("powderkeg", () -> new PowderkegBlock(ExtendedProperties.of(MapColor.WOOD).noOcclusion().dynamicShape().sound(SoundType.WOOD).strength(2.5f).blockEntity(TFCBlockEntities.POWDERKEG).serverTicks(PowderkegBlockEntity::serverTick)), block -> new TooltipBlockItem(block, new Item.Properties()));
    public static final Id<Block> BARREL_RACK = register("barrel_rack", () -> new BarrelRackBlock(ExtendedProperties.of(MapColor.WOOD).sound(SoundType.WOOD).flammableLikePlanks().strength(4f)));

    public static final Id<Block> PLACED_ITEM = registerNoItem("placed_item", () -> new PlacedItemBlock(ExtendedProperties.of().instabreak().sound(SoundType.STEM).noOcclusion().blockEntity(TFCBlockEntities.PLACED_ITEM)));
    public static final Id<Block> SCRAPING = registerNoItem("scraping", () -> new ScrapingBlock(ExtendedProperties.of().strength(0.2F).sound(SoundType.STEM).noOcclusion().blockEntity(TFCBlockEntities.SCRAPING)));
    public static final Id<Block> PIT_KILN = registerNoItem("pit_kiln", () -> new PitKilnBlock(ExtendedProperties.of(MapColor.WOOD).sound(SoundType.WOOD).strength(0.6f).noOcclusion().blockEntity(TFCBlockEntities.PIT_KILN).serverTicks(PitKilnBlockEntity::serverTick)));
    public static final Id<Block> QUERN = register("quern", () -> new QuernBlock(ExtendedProperties.of().mapColor(MapColor.STONE).strength(0.5F, 2.0F).sound(SoundType.BASALT).noOcclusion().blockEntity(TFCBlockEntities.QUERN).ticks(QuernBlockEntity::serverTick, QuernBlockEntity::clientTick)));
    public static final Id<Block> CHARCOAL_PILE = registerNoItem("charcoal_pile", () -> new CharcoalPileBlock(Properties.of().mapColor(MapColor.COLOR_BLACK).strength(0.2F).sound(TFCSounds.CHARCOAL).pushReaction(PushReaction.DESTROY).isViewBlocking((state, level, pos) -> state.getValue(CharcoalPileBlock.LAYERS) >= 8).isSuffocating((state, level, pos) -> state.getValue(CharcoalPileBlock.LAYERS) >= 8)));
    public static final Id<Block> CHARCOAL_FORGE = registerNoItem("charcoal_forge", () -> new CharcoalForgeBlock(ExtendedProperties.of(MapColor.COLOR_BLACK).pushReaction(PushReaction.DESTROY).strength(0.2F).randomTicks().sound(TFCSounds.CHARCOAL).lightLevel(state -> state.getValue(CharcoalForgeBlock.HEAT) * 2).pathType(PathType.DAMAGE_FIRE).blockEntity(TFCBlockEntities.CHARCOAL_FORGE).serverTicks(CharcoalForgeBlockEntity::serverTick)));

    public static final Id<Block> TORCH = registerNoItem("torch", () -> new TFCTorchBlock(ExtendedProperties.of().noCollission().instabreak().randomTicks().lightLevel(state -> 14).sound(SoundType.WOOD).blockEntity(TFCBlockEntities.TICK_COUNTER), ParticleTypes.FLAME));
    public static final Id<Block> WALL_TORCH = registerNoItem("wall_torch", () -> new TFCWallTorchBlock(ExtendedProperties.of().noCollission().instabreak().randomTicks().lightLevel(state -> 14).sound(SoundType.WOOD).dropsLike(TORCH).blockEntity(TFCBlockEntities.TICK_COUNTER), ParticleTypes.FLAME));
    public static final Id<Block> DEAD_TORCH = registerNoItem("dead_torch", () -> new DeadTorchBlock(Properties.of().noCollission().instabreak().sound(SoundType.WOOD), ParticleTypes.FLAME));
    public static final Id<Block> DEAD_WALL_TORCH = registerNoItem("dead_wall_torch", () -> new DeadWallTorchBlock(Properties.of().noCollission().instabreak().sound(SoundType.WOOD).lootFrom(DEAD_TORCH), ParticleTypes.FLAME));
    public static final Id<Block> JACK_O_LANTERN = register("jack_o_lantern", () -> new JackOLanternBlock(ExtendedProperties.of(MapColor.COLOR_ORANGE).strength(1.0F).sound(SoundType.WOOD).randomTicks().lightLevel(alwaysLit()).blockEntity(TFCBlockEntities.TICK_COUNTER), () -> Blocks.CARVED_PUMPKIN));
    public static final Id<Block> BRONZE_BELL = register("bronze_bell", () -> new TFCBellBlock(ExtendedProperties.of(MapColor.GOLD).requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.ANVIL).blockEntity(TFCBlockEntities.BELL).ticks(BellBlockEntity::serverTick, BellBlockEntity::clientTick), 0.8f, "bronze"));
    public static final Id<Block> BRASS_BELL = register("brass_bell", () -> new TFCBellBlock(ExtendedProperties.of(MapColor.GOLD).requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.ANVIL).blockEntity(TFCBlockEntities.BELL).ticks(BellBlockEntity::serverTick, BellBlockEntity::clientTick), 0.6f, "brass"));

    public static final Id<Block> CRUCIBLE = register("crucible", () -> new CrucibleBlock(ExtendedProperties.of(MapColor.METAL).strength(3).sound(SoundType.METAL).blockEntity(TFCBlockEntities.CRUCIBLE).serverTicks(CrucibleBlockEntity::serverTick)), block -> new TooltipBlockItem(block, new Item.Properties()));
    public static final Id<Block> COMPOSTER = register("composter", () -> new TFCComposterBlock(ExtendedProperties.of(MapColor.WOOD).strength(0.6F).noOcclusion().sound(SoundType.WOOD).randomTicks().flammable(60, 90).blockEntity(TFCBlockEntities.COMPOSTER)));
    public static final Id<Block> BLOOMERY = register("bloomery", () -> new BloomeryBlock(ExtendedProperties.of(MapColor.METAL).strength(3).sound(SoundType.METAL).lightLevel(litBlockEmission(15)).blockEntity(TFCBlockEntities.BLOOMERY).serverTicks(BloomeryBlockEntity::serverTick)));
    public static final Id<Block> BLAST_FURNACE = register("blast_furnace", () -> new BlastFurnaceBlock(ExtendedProperties.of(MapColor.METAL).strength(5f).sound(SoundType.METAL).lightLevel(litBlockEmission(15)).blockEntity(TFCBlockEntities.BLAST_FURNACE).serverTicks(BlastFurnaceBlockEntity::serverTick)));
    public static final Id<Block> BLOOM = register("bloom", () -> new BloomBlock(ExtendedProperties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(3F, 6F).noOcclusion().blockEntity(TFCBlockEntities.BLOOM)));
    public static final Id<Block> MOLTEN = register("molten", () -> new MoltenBlock(ExtendedProperties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(-1.0F, 3600000.0F).noOcclusion().lightLevel(litBlockEmission(15)).pathType(PathType.DAMAGE_FIRE)));
    public static final Id<Block> WOODEN_BOWL = registerNoItem("wooden_bowl", () -> new BowlBlock(ExtendedProperties.of().mapColor(MapColor.WOOD).sound(SoundType.WOOD).strength(0.3f).noOcclusion().blockEntity(TFCBlockEntities.BOWL))); // No item, since we use the vanilla one
    public static final Id<Block> CERAMIC_BOWL = register("ceramic/bowl", () -> new BowlBlock(ExtendedProperties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).strength(0.3f).noOcclusion().blockEntity(TFCBlockEntities.BOWL)));

    public static final Id<Block> NEST_BOX = register("nest_box", () -> new NestBoxBlock(ExtendedProperties.of(MapColor.WOOD).strength(3f).noOcclusion().sound(TFCSounds.THATCH).blockEntity(TFCBlockEntities.NEST_BOX).serverTicks(NestBoxBlockEntity::serverTick).flammable(60, 30)));

    public static final Id<Block> LIGHT = register("light", () -> new TFCLightBlock(Properties.ofFullCopy(Blocks.LIGHT)
        .replaceable().lightLevel(state -> state.getValue(TFCLightBlock.LEVEL)).randomTicks()
        .mapColor(MapColor.NONE) // Need to override map color so it doesn't use the default (which checks waterlogged property)
    ));
    public static final Id<Block> FRESHWATER_BUBBLE_COLUMN = registerNoItem("freshwater_bubble_column", () -> new TFCBubbleColumnBlock(Properties.ofFullCopy(Blocks.BUBBLE_COLUMN).noCollission().noLootTable(), () -> Fluids.WATER));
    public static final Id<Block> SALTWATER_BUBBLE_COLUMN = registerNoItem("saltwater_bubble_column", () -> new TFCBubbleColumnBlock(Properties.ofFullCopy(Blocks.BUBBLE_COLUMN).noCollission().noLootTable(), TFCFluids.SALT_WATER::getSource));

    public static final Id<Block> SHEET_PILE = registerNoItem("sheet_pile", () -> new SheetPileBlock(ExtendedProperties.of(MapColor.METAL).strength(4, 60).sound(SoundType.METAL).noOcclusion().blockEntity(TFCBlockEntities.SHEET_PILE)));
    public static final Id<Block> INGOT_PILE = registerNoItem("ingot_pile", () -> new IngotPileBlock(ExtendedProperties.of(MapColor.METAL).strength(4, 60).sound(SoundType.METAL).noOcclusion().blockEntity(TFCBlockEntities.INGOT_PILE)));
    public static final Id<Block> DOUBLE_INGOT_PILE = registerNoItem("double_ingot_pile", () -> new DoubleIngotPileBlock(ExtendedProperties.of(MapColor.METAL).strength(4, 60).sound(SoundType.METAL).noOcclusion().blockEntity(TFCBlockEntities.INGOT_PILE)));

    public static final Id<Block> CAKE = register("cake", () -> new TFCCakeBlock(Properties.ofFullCopy(Blocks.CAKE).strength(0.5f).sound(SoundType.WOOL)));
    public static final Id<Block> CANDLE_CAKE = registerNoItem("candle_cake", () -> new TFCCandleCakeBlock(ExtendedProperties.of(Blocks.CANDLE_CAKE).strength(0.5f).sound(SoundType.WOOL).randomTicks().lightLevel(litBlockEmission(3)).blockEntity(TFCBlockEntities.TICK_COUNTER).cloneItem(Blocks.CAKE)));
    public static final Id<Block> CANDLE = register("candle", () -> new TFCCandleBlock(ExtendedProperties.of(Blocks.CANDLE).mapColor(MapColor.SAND).randomTicks().noOcclusion().strength(0.1F).sound(SoundType.CANDLE).lightLevel(TFCCandleBlock.LIGHTING_SCALE).blockEntity(TFCBlockEntities.TICK_COUNTER)), b -> new CandleBlockItem(new Item.Properties(), b, TFCBlocks.CANDLE_CAKE));

    public static final Id<Block> CRANKSHAFT = register("crankshaft", () -> new CrankshaftBlock(ExtendedProperties.of().sound(SoundType.METAL).strength(3f).noOcclusion().pushReaction(PushReaction.DESTROY).blockEntity(TFCBlockEntities.CRANKSHAFT)));
    public static final Id<Block> TRIP_HAMMER = register("trip_hammer", () -> new TripHammerBlock(ExtendedProperties.of().sound(SoundType.METAL).strength(3f).noOcclusion().pushReaction(PushReaction.DESTROY).blockEntity(TFCBlockEntities.TRIP_HAMMER).serverTicks(TripHammerBlockEntity::serverTick)));
    public static final Id<Block> STEEL_PIPE = register("steel_pipe", () -> new FluidPipeBlock(ExtendedProperties.of().strength(5f).sound(SoundType.METAL)));
    public static final Id<Block> STEEL_PUMP = register("steel_pump", () -> new FluidPumpBlock(ExtendedProperties.of().strength(5f).sound(SoundType.METAL).blockEntity(TFCBlockEntities.PUMP).serverTicks(PumpBlockEntity::serverTick).forceSolidOn()));

    public static final Map<DyeColor, Id<Block>> DYED_CANDLE_CAKES = Helpers.mapOf(DyeColor.class, color ->
        registerNoItem("candle_cake/" + color.getName(), () -> new TFCCandleCakeBlock(ExtendedProperties.of(MapColor.SAND).randomTicks().noOcclusion().strength(0.5F).sound(SoundType.WOOL).lightLevel(litBlockEmission(3)).blockEntity(TFCBlockEntities.TICK_COUNTER).cloneItem(Blocks.CAKE)))
    );
    public static final Map<DyeColor, Id<Block>> DYED_CANDLE = Helpers.mapOf(DyeColor.class, color ->
        register("candle/" + color.getName(), () -> new TFCCandleBlock(ExtendedProperties.of(MapColor.SAND).randomTicks().noOcclusion().strength(0.1F).sound(SoundType.CANDLE).lightLevel(TFCCandleBlock.LIGHTING_SCALE).blockEntity(TFCBlockEntities.TICK_COUNTER)), b -> new CandleBlockItem(new Item.Properties(), b, TFCBlocks.DYED_CANDLE_CAKES.get(color)))
    );

    public static final Id<Block> LARGE_VESSEL = register("ceramic/large_vessel", () -> new LargeVesselBlock(ExtendedProperties.of(MapColor.CLAY).strength(2.5F).noOcclusion().blockEntity(TFCBlockEntities.LARGE_VESSEL)), block -> new TooltipBlockItem(block, new Item.Properties()));
    public static final Map<DyeColor, Id<Block>> GLAZED_LARGE_VESSELS = Helpers.mapOf(DyeColor.class, color ->
        register("ceramic/large_vessel/" + color.getName(), () -> new LargeVesselBlock(ExtendedProperties.of(MapColor.CLAY).strength(2.5F).noOcclusion().blockEntity(TFCBlockEntities.LARGE_VESSEL)), block -> new TooltipBlockItem(block, new Item.Properties()))
    );

    // Fluids

    public static final Map<Metal, Id<LiquidBlock>> METAL_FLUIDS = Helpers.mapOf(Metal.class, metal ->
        registerNoItem("fluid/metal/" + metal.name(), () -> new LiquidBlock(TFCFluids.METALS.get(metal).getSource(), Properties.ofFullCopy(Blocks.LAVA).noLootTable()))
    );

    public static final Map<SimpleFluid, Id<LiquidBlock>> SIMPLE_FLUIDS = Helpers.mapOf(SimpleFluid.class, fluid ->
        registerNoItem("fluid/" + fluid.getId(), () -> new LiquidBlock(TFCFluids.SIMPLE_FLUIDS.get(fluid).getSource(), Properties.ofFullCopy(Blocks.WATER).noLootTable()))
    );

    public static final Map<DyeColor, Id<LiquidBlock>> COLORED_FLUIDS = Helpers.mapOf(DyeColor.class, fluid ->
        registerNoItem("fluid/" + fluid.getName() + "_dye", () -> new LiquidBlock(TFCFluids.COLORED_FLUIDS.get(fluid).getSource(), Properties.ofFullCopy(Blocks.WATER).noLootTable()))
    );

    public static final Id<LiquidBlock> SALT_WATER = registerNoItem("fluid/salt_water", () -> new LiquidBlock(TFCFluids.SALT_WATER.getFlowing(), Properties.ofFullCopy(Blocks.WATER).noLootTable()));
    public static final Id<LiquidBlock> SPRING_WATER = registerNoItem("fluid/spring_water", () -> new HotWaterBlock(TFCFluids.SPRING_WATER.source(), Properties.ofFullCopy(Blocks.WATER).noLootTable()));

    public static final Id<RiverWaterBlock> RIVER_WATER = registerNoItem("fluid/river_water", () -> new RiverWaterBlock(Properties.ofFullCopy(Blocks.WATER).noLootTable()));

    public static void registerFlowerPotFlowers()
    {
        FlowerPotBlock pot = (FlowerPotBlock) Blocks.FLOWER_POT;
        POTTED_PLANTS.forEach((plant, reg) -> pot.addPlant(PLANTS.get(plant).getId(), reg));
        WOODS.forEach((wood, map) -> pot.addPlant(map.get(Wood.BlockType.SAPLING).getId(), map.get(Wood.BlockType.POTTED_SAPLING)));
        FRUIT_TREE_POTTED_SAPLINGS.forEach((plant, reg) -> pot.addPlant(FRUIT_TREE_SAPLINGS.get(plant).getId(), reg));
        pot.addPlant(BANANA_SAPLING.getId(), BANANA_POTTED_SAPLING);
        pot.addPlant(PINE_KRUMMHOLZ.getId(), POTTED_PINE_KRUMMHOLZ);
        pot.addPlant(ASPEN_KRUMMHOLZ.getId(), POTTED_ASPEN_KRUMMHOLZ);
        pot.addPlant(WHITE_CEDAR_KRUMMHOLZ.getId(), POTTED_WHITE_CEDAR_KRUMMHOLZ);
        pot.addPlant(SPRUCE_KRUMMHOLZ.getId(), POTTED_SPRUCE_KRUMMHOLZ);
        pot.addPlant(DOUGLAS_FIR_KRUMMHOLZ.getId(), POTTED_DOUGLAS_FIR_KRUMMHOLZ);
    }

    public static void editBlockRequiredTools()
    {
        for (Block block : new Block[] {
            // All glass blocks are edited to require a tool (the gem saw), and loot tables that always drop themselves.
            // We have to edit their 'required tool'-ness here
            Blocks.GLASS,
            Blocks.GLASS_PANE,
            Blocks.TINTED_GLASS,
            Blocks.WHITE_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS, Blocks.LIME_STAINED_GLASS, Blocks.PINK_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS, Blocks.CYAN_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS, Blocks.RED_STAINED_GLASS, Blocks.BLACK_STAINED_GLASS,
            Blocks.WHITE_STAINED_GLASS_PANE, Blocks.ORANGE_STAINED_GLASS_PANE, Blocks.MAGENTA_STAINED_GLASS_PANE, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, Blocks.YELLOW_STAINED_GLASS_PANE, Blocks.LIME_STAINED_GLASS_PANE, Blocks.PINK_STAINED_GLASS_PANE, Blocks.GRAY_STAINED_GLASS_PANE, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, Blocks.CYAN_STAINED_GLASS_PANE, Blocks.PURPLE_STAINED_GLASS_PANE, Blocks.BLUE_STAINED_GLASS_PANE, Blocks.BROWN_STAINED_GLASS_PANE, Blocks.GREEN_STAINED_GLASS_PANE, Blocks.RED_STAINED_GLASS_PANE, Blocks.BLACK_STAINED_GLASS_PANE,
        })
        {
            // Need to do both the block settings and the block state since the value is copied there for every state
            ((BlockBehaviourAccessor) block).getProperties().requiresCorrectToolForDrops();
            for (BlockState state : block.getStateDefinition().getPossibleStates())
            {
                ((BlockStateBaseAccessor) state).setRequiresCorrectToolForDrops(true);
            }
        }
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


    private static <T extends Block> Id<T> registerNoItem(String name, Supplier<T> blockSupplier)
    {
        return register(name, blockSupplier, (Function<T, ? extends BlockItem>) null);
    }

    private static <T extends Block> Id<T> register(String name, Supplier<T> blockSupplier)
    {
        return register(name, blockSupplier, block -> new BlockItem(block, new Item.Properties()));
    }

    private static <T extends Block> Id<T> register(String name, Supplier<T> blockSupplier, Item.Properties blockItemProperties)
    {
        return register(name, blockSupplier, block -> new BlockItem(block, blockItemProperties));
    }

    private static <T extends Block> Id<T> register(String name, Supplier<T> blockSupplier, @Nullable Function<T, ? extends BlockItem> blockItemFactory)
    {
        return new Id<>(RegistrationHelpers.registerBlock(TFCBlocks.BLOCKS, TFCItems.ITEMS, name, blockSupplier, blockItemFactory));
    }

    private static <T1 extends SlabBlock, T2 extends StairBlock, T3 extends WallBlock> DecorationBlockHolder registerDecorations(String baseName, Supplier<T1> slab, Supplier<T2> stair, Supplier<T3> wall, Item.Properties properties)
    {
        return new DecorationBlockHolder(
            register(baseName + "_slab", slab, b -> new BlockItem(b, properties)),
            register(baseName + "_stairs", stair, b -> new BlockItem(b, properties)),
            register(baseName + "_wall", wall, b -> new BlockItem(b, properties))
        );
    }
    
    public record Id<T extends Block>(DeferredHolder<Block, T> holder) implements RegistryHolder<Block, T>, ItemLike
    {
        @Override
        public Item asItem()
        {
            return get().asItem();
        }
    }
}