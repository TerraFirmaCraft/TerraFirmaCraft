/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.common.blockentities.rotation.AxleBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.BladedAxleBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.ClutchBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.EncasedAxleBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.CrankshaftBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.GearBoxBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.HandWheelBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.PumpBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.TripHammerBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.WaterWheelBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.WindmillBlockEntity;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.registry.RegistrationHelpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("unused")
public final class TFCBlockEntities
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MOD_ID);

    public static final RegistryObject<BlockEntityType<FarmlandBlockEntity>> FARMLAND = register("farmland", FarmlandBlockEntity::new, TFCBlocks.SOIL.get(SoilBlockType.FARMLAND).values().stream());
    public static final RegistryObject<BlockEntityType<PileBlockEntity>> PILE = register("pile", PileBlockEntity::new, Stream.of(TFCBlocks.ICE_PILE, TFCBlocks.SNOW_PILE));
    public static final RegistryObject<BlockEntityType<FirepitBlockEntity>> FIREPIT = register("firepit", FirepitBlockEntity::new, TFCBlocks.FIREPIT);
    public static final RegistryObject<BlockEntityType<GrillBlockEntity>> GRILL = register("grill", GrillBlockEntity::new, TFCBlocks.GRILL);
    public static final RegistryObject<BlockEntityType<PotBlockEntity>> POT = register("pot", PotBlockEntity::new, TFCBlocks.POT);
    public static final RegistryObject<BlockEntityType<BowlBlockEntity>> BOWL = register("bowl", BowlBlockEntity::new, Stream.of(TFCBlocks.CERAMIC_BOWL, TFCBlocks.WOODEN_BOWL));
    public static final RegistryObject<BlockEntityType<HotPouredGlassBlockEntity>> HOT_POURED_GLASS = register("hot_poured_glass", HotPouredGlassBlockEntity::new, TFCBlocks.HOT_POURED_GLASS);
    public static final RegistryObject<BlockEntityType<GlassBasinBlockEntity>> GLASS_BASIN = register("glass_basin", GlassBasinBlockEntity::new, TFCBlocks.GLASS_BASIN);
    public static final RegistryObject<BlockEntityType<JarsBlockEntity>> JARS = register("jars", JarsBlockEntity::new, Stream.of(woodBlocks(Wood.BlockType.JAR_SHELF), TFCBlocks.JARS).<Supplier<? extends Block>>flatMap(Helpers::flatten));

    public static final RegistryObject<BlockEntityType<TickCounterBlockEntity>> TICK_COUNTER = register("tick_counter", TickCounterBlockEntity::new, Stream.of(
            TFCBlocks.WOODS.values().stream().map(map -> map.get(Wood.BlockType.SAPLING)),
            TFCBlocks.FRUIT_TREE_SAPLINGS.values(),
            TFCBlocks.FRUIT_TREE_GROWING_BRANCHES.values(),
            TFCBlocks.TORCH,
            TFCBlocks.WALL_TORCH,
            TFCBlocks.DEAD_BERRY_BUSH,
            TFCBlocks.DEAD_CANE,
            TFCBlocks.BANANA_SAPLING,
            TFCBlocks.DEAD_BANANA_PLANT,
            TFCBlocks.JACK_O_LANTERN,
            TFCBlocks.CANDLE,
            TFCBlocks.DYED_CANDLE.values(),
            TFCBlocks.DYED_CANDLE_CAKES.values(),
            TFCBlocks.SOIL.get(SoilBlockType.DRYING_BRICKS).values()
        ).<Supplier<? extends Block>>flatMap(Helpers::flatten)
    );

    public static final RegistryObject<BlockEntityType<LogPileBlockEntity>> LOG_PILE = register("log_pile", LogPileBlockEntity::new, TFCBlocks.LOG_PILE);
    public static final RegistryObject<BlockEntityType<BurningLogPileBlockEntity>> BURNING_LOG_PILE = register("burning_log_pile", BurningLogPileBlockEntity::new, TFCBlocks.BURNING_LOG_PILE);
    public static final RegistryObject<BlockEntityType<PlacedItemBlockEntity>> PLACED_ITEM = register("placed_item", PlacedItemBlockEntity::new, TFCBlocks.PLACED_ITEM);
    public static final RegistryObject<BlockEntityType<PitKilnBlockEntity>> PIT_KILN = register("pit_kiln", PitKilnBlockEntity::new, TFCBlocks.PIT_KILN);
    public static final RegistryObject<BlockEntityType<CharcoalForgeBlockEntity>> CHARCOAL_FORGE = register("charcoal_forge", CharcoalForgeBlockEntity::new, TFCBlocks.CHARCOAL_FORGE);
    public static final RegistryObject<BlockEntityType<QuernBlockEntity>> QUERN = register("quern", QuernBlockEntity::new, TFCBlocks.QUERN);
    public static final RegistryObject<BlockEntityType<ScrapingBlockEntity>> SCRAPING = register("scraping", ScrapingBlockEntity::new, TFCBlocks.SCRAPING);
    public static final RegistryObject<BlockEntityType<CrucibleBlockEntity>> CRUCIBLE = register("crucible", CrucibleBlockEntity::new, TFCBlocks.CRUCIBLE);
    public static final RegistryObject<BlockEntityType<BellowsBlockEntity>> BELLOWS = register("bellows", BellowsBlockEntity::new, TFCBlocks.BELLOWS);
    public static final RegistryObject<BlockEntityType<ComposterBlockEntity>> COMPOSTER = register("composter", ComposterBlockEntity::new, TFCBlocks.COMPOSTER);
    public static final RegistryObject<BlockEntityType<BloomeryBlockEntity>> BLOOMERY = register("bloomery", BloomeryBlockEntity::new, TFCBlocks.BLOOMERY);
    public static final RegistryObject<BlockEntityType<BloomBlockEntity>> BLOOM = register("bloom", BloomBlockEntity::new, TFCBlocks.BLOOM);
    public static final RegistryObject<BlockEntityType<PowderkegBlockEntity>> POWDERKEG = register("powderkeg", PowderkegBlockEntity::new, TFCBlocks.POWDERKEG);
    public static final RegistryObject<BlockEntityType<TFCChestBlockEntity>> CHEST = register("chest", TFCChestBlockEntity::new, woodBlocks(Wood.BlockType.CHEST));
    public static final RegistryObject<BlockEntityType<TFCTrappedChestBlockEntity>> TRAPPED_CHEST = register("trapped_chest", TFCTrappedChestBlockEntity::new, woodBlocks(Wood.BlockType.TRAPPED_CHEST));
    public static final RegistryObject<BlockEntityType<BarrelBlockEntity>> BARREL = register("barrel", BarrelBlockEntity::new, woodBlocks(Wood.BlockType.BARREL));
    public static final RegistryObject<BlockEntityType<LoomBlockEntity>> LOOM = register("loom", LoomBlockEntity::new, woodBlocks(Wood.BlockType.LOOM));
    public static final RegistryObject<BlockEntityType<SluiceBlockEntity>> SLUICE = register("sluice", SluiceBlockEntity::new, woodBlocks(Wood.BlockType.SLUICE));
    public static final RegistryObject<BlockEntityType<ToolRackBlockEntity>> TOOL_RACK = register("tool_rack", ToolRackBlockEntity::new, woodBlocks(Wood.BlockType.TOOL_RACK));
    public static final RegistryObject<BlockEntityType<BookshelfBlockEntity>> BOOKSHELF = register("bookshelf", BookshelfBlockEntity::new, woodBlocks(Wood.BlockType.BOOKSHELF));
    public static final RegistryObject<BlockEntityType<TFCSignBlockEntity>> SIGN = register("sign", TFCSignBlockEntity::new, Stream.concat(
        woodBlocks(Wood.BlockType.SIGN),
        woodBlocks(Wood.BlockType.WALL_SIGN)
    ));
    public static final RegistryObject<BlockEntityType<TFCHangingSignBlockEntity>> HANGING_SIGN = register("hanging_sign", TFCHangingSignBlockEntity::new, Stream.of(
        TFCBlocks.CEILING_HANGING_SIGNS,
        TFCBlocks.WALL_HANGING_SIGNS
    ).flatMap(woodMap -> woodMap.values().stream().flatMap(metalMap -> metalMap.values().stream())));
    public static final RegistryObject<BlockEntityType<LampBlockEntity>> LAMP = register("lamp", LampBlockEntity::new, TFCBlocks.METALS.values().stream().filter(map -> map.get(Metal.BlockType.LAMP) != null).map(map -> map.get(Metal.BlockType.LAMP)));
    public static final RegistryObject<BlockEntityType<ThatchBedBlockEntity>> THATCH_BED = register("thatch_bed", ThatchBedBlockEntity::new, TFCBlocks.THATCH_BED);
    public static final RegistryObject<BlockEntityType<BerryBushBlockEntity>> BERRY_BUSH = register("berry_bush", BerryBushBlockEntity::new, Stream.of(
        TFCBlocks.BANANA_PLANT,
        TFCBlocks.CRANBERRY_BUSH,
        TFCBlocks.SPREADING_BUSHES.values(),
        TFCBlocks.SPREADING_CANES.values(),
        TFCBlocks.STATIONARY_BUSHES.values(),
        TFCBlocks.FRUIT_TREE_LEAVES.values()
    ).<Supplier<? extends Block>>flatMap(Helpers::flatten));
    public static final RegistryObject<BlockEntityType<CropBlockEntity>> CROP = register("crop", CropBlockEntity::new, TFCBlocks.CROPS.values().stream());
    public static final RegistryObject<BlockEntityType<DecayingBlockEntity>> DECAYING = register("decaying", DecayingBlockEntity::new, Stream.of(TFCBlocks.MELON, TFCBlocks.PUMPKIN));
    public static final RegistryObject<BlockEntityType<NestBoxBlockEntity>> NEST_BOX = register("nest_box", NestBoxBlockEntity::new, TFCBlocks.NEST_BOX);
    public static final RegistryObject<BlockEntityType<LargeVesselBlockEntity>> LARGE_VESSEL = register("large_vessel", LargeVesselBlockEntity::new, Stream.of(TFCBlocks.LARGE_VESSEL, TFCBlocks.GLAZED_LARGE_VESSELS.values()).<Supplier<? extends Block>>flatMap(Helpers::flatten));
    public static final RegistryObject<BlockEntityType<LecternBlockEntity>> LECTERN = register("lectern", TFCLecternBlockEntity::new, woodBlocks(Wood.BlockType.LECTERN));
    public static final RegistryObject<BlockEntityType<AnvilBlockEntity>> ANVIL = register("anvil", AnvilBlockEntity::new, Stream.concat(
        TFCBlocks.ROCK_ANVILS.values().stream(),
        TFCBlocks.METALS.values().stream().map(m -> m.get(Metal.BlockType.ANVIL)).filter(Objects::nonNull)
    ));
    public static final RegistryObject<BlockEntityType<SheetPileBlockEntity>> SHEET_PILE = register("sheet_pile", SheetPileBlockEntity::new, TFCBlocks.SHEET_PILE);
    public static final RegistryObject<BlockEntityType<IngotPileBlockEntity>> INGOT_PILE = register("ingot_pile", IngotPileBlockEntity::new, Stream.of(TFCBlocks.INGOT_PILE, TFCBlocks.DOUBLE_INGOT_PILE));
    public static final RegistryObject<BlockEntityType<BlastFurnaceBlockEntity>> BLAST_FURNACE = register("blast_furnace", BlastFurnaceBlockEntity::new, TFCBlocks.BLAST_FURNACE);
    public static final RegistryObject<BlockEntityType<TFCBellBlockEntity>> BELL = register("bell", TFCBellBlockEntity::new, Stream.of(TFCBlocks.BRONZE_BELL, TFCBlocks.BRASS_BELL));
    public static final RegistryObject<BlockEntityType<AxleBlockEntity>> AXLE = register("axle", AxleBlockEntity::new, woodBlocks(Wood.BlockType.AXLE));
    public static final RegistryObject<BlockEntityType<BladedAxleBlockEntity>> BLADED_AXLE = register("bladed_axle", BladedAxleBlockEntity::new, woodBlocks(Wood.BlockType.BLADED_AXLE));
    public static final RegistryObject<BlockEntityType<AxleBlockEntity>> CLUTCH = register("clutch", ClutchBlockEntity::new, woodBlocks(Wood.BlockType.CLUTCH));
    public static final RegistryObject<BlockEntityType<EncasedAxleBlockEntity>> ENCASED_AXLE = register("encased_axle", EncasedAxleBlockEntity::new, woodBlocks(Wood.BlockType.ENCASED_AXLE));
    public static final RegistryObject<BlockEntityType<HandWheelBlockEntity>> HAND_WHEEL = register("hand_wheel", HandWheelBlockEntity::new, TFCBlocks.HAND_WHEEL_BASE);
    public static final RegistryObject<BlockEntityType<GearBoxBlockEntity>> GEAR_BOX = register("gear_box", GearBoxBlockEntity::new, woodBlocks(Wood.BlockType.GEAR_BOX));
    public static final RegistryObject<BlockEntityType<WindmillBlockEntity>> WINDMILL = register("windmill", WindmillBlockEntity::new, woodBlocks(Wood.BlockType.WINDMILL));
    public static final RegistryObject<BlockEntityType<WaterWheelBlockEntity>> WATER_WHEEL = register("water_wheel", WaterWheelBlockEntity::new, woodBlocks(Wood.BlockType.WATER_WHEEL));
    public static final RegistryObject<BlockEntityType<CrankshaftBlockEntity>> CRANKSHAFT = register("crankshaft", CrankshaftBlockEntity::new, TFCBlocks.CRANKSHAFT);
    public static final RegistryObject<BlockEntityType<TripHammerBlockEntity>> TRIP_HAMMER = register("trip_hammer", TripHammerBlockEntity::new, TFCBlocks.TRIP_HAMMER);
    public static final RegistryObject<BlockEntityType<PumpBlockEntity>> PUMP = register("pump", PumpBlockEntity::new, TFCBlocks.STEEL_PUMP);

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, Supplier<? extends Block> block)
    {
        return RegistrationHelpers.register(BLOCK_ENTITIES, name, factory, block);
    }

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, Stream<? extends Supplier<? extends Block>> blocks)
    {
        return RegistrationHelpers.register(BLOCK_ENTITIES, name, factory, blocks);
    }

    private static Stream<? extends Supplier<? extends Block>> woodBlocks(Wood.BlockType type)
    {
        return TFCBlocks.WOODS.values().stream().map(map -> map.get(type));
    }
}
