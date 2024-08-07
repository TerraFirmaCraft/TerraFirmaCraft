/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.common.blockentities.rotation.AxleBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.BladedAxleBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.ClutchBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.CrankshaftBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.EncasedAxleBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.GearBoxBlockEntity;
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
import net.dries007.tfc.util.registry.RegistryHolder;

import static net.dries007.tfc.TerraFirmaCraft.*;

@SuppressWarnings("unused")
public final class TFCBlockEntities
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MOD_ID);

    public static final Id<FarmlandBlockEntity> FARMLAND = register("farmland", FarmlandBlockEntity::new, TFCBlocks.SOIL.get(SoilBlockType.FARMLAND).values().stream());
    public static final Id<PileBlockEntity> PILE = register("pile", PileBlockEntity::new, Stream.of(TFCBlocks.ICE_PILE, TFCBlocks.SNOW_PILE));
    public static final Id<FirepitBlockEntity> FIREPIT = register("firepit", FirepitBlockEntity::new, TFCBlocks.FIREPIT);
    public static final Id<GrillBlockEntity> GRILL = register("grill", GrillBlockEntity::new, TFCBlocks.GRILL);
    public static final Id<PotBlockEntity> POT = register("pot", PotBlockEntity::new, TFCBlocks.POT);
    public static final Id<BowlBlockEntity> BOWL = register("bowl", BowlBlockEntity::new, Stream.of(TFCBlocks.CERAMIC_BOWL, TFCBlocks.WOODEN_BOWL));
    public static final Id<HotPouredGlassBlockEntity> HOT_POURED_GLASS = register("hot_poured_glass", HotPouredGlassBlockEntity::new, TFCBlocks.HOT_POURED_GLASS);
    public static final Id<GlassBasinBlockEntity> GLASS_BASIN = register("glass_basin", GlassBasinBlockEntity::new, TFCBlocks.GLASS_BASIN);
    public static final Id<JarsBlockEntity> JARS = register("jars", JarsBlockEntity::new, Stream.concat(
        woodBlocks(Wood.BlockType.JAR_SHELF),
        Stream.of(TFCBlocks.JARS)
    ));

    public static final Id<TickCounterBlockEntity> TICK_COUNTER = register("tick_counter", TickCounterBlockEntity::new, Stream.of(
            woodBlocks(Wood.BlockType.SAPLING),
            TFCBlocks.FRUIT_TREE_SAPLINGS.values().stream(),
            TFCBlocks.FRUIT_TREE_GROWING_BRANCHES.values().stream(),
            Stream.of(
                TFCBlocks.TORCH,
                TFCBlocks.WALL_TORCH,
                TFCBlocks.DEAD_BERRY_BUSH,
                TFCBlocks.DEAD_CANE,
                TFCBlocks.BANANA_SAPLING,
                TFCBlocks.DEAD_BANANA_PLANT,
                TFCBlocks.JACK_O_LANTERN,
                TFCBlocks.CANDLE
            ),
            TFCBlocks.DYED_CANDLE.values().stream(),
            TFCBlocks.DYED_CANDLE_CAKES.values().stream(),
            TFCBlocks.SOIL.get(SoilBlockType.DRYING_BRICKS).values().stream()
        ).flatMap(e -> e)
    );

    public static final Id<LogPileBlockEntity> LOG_PILE = register("log_pile", LogPileBlockEntity::new, TFCBlocks.LOG_PILE);
    public static final Id<BurningLogPileBlockEntity> BURNING_LOG_PILE = register("burning_log_pile", BurningLogPileBlockEntity::new, TFCBlocks.BURNING_LOG_PILE);
    public static final Id<PlacedItemBlockEntity> PLACED_ITEM = register("placed_item", PlacedItemBlockEntity::new, TFCBlocks.PLACED_ITEM);
    public static final Id<PitKilnBlockEntity> PIT_KILN = register("pit_kiln", PitKilnBlockEntity::new, TFCBlocks.PIT_KILN);
    public static final Id<CharcoalForgeBlockEntity> CHARCOAL_FORGE = register("charcoal_forge", CharcoalForgeBlockEntity::new, TFCBlocks.CHARCOAL_FORGE);
    public static final Id<QuernBlockEntity> QUERN = register("quern", QuernBlockEntity::new, TFCBlocks.QUERN);
    public static final Id<ScrapingBlockEntity> SCRAPING = register("scraping", ScrapingBlockEntity::new, TFCBlocks.SCRAPING);
    public static final Id<CrucibleBlockEntity> CRUCIBLE = register("crucible", CrucibleBlockEntity::new, TFCBlocks.CRUCIBLE);
    public static final Id<BellowsBlockEntity> BELLOWS = register("bellows", BellowsBlockEntity::new, TFCBlocks.BELLOWS);
    public static final Id<ComposterBlockEntity> COMPOSTER = register("composter", ComposterBlockEntity::new, TFCBlocks.COMPOSTER);
    public static final Id<BloomeryBlockEntity> BLOOMERY = register("bloomery", BloomeryBlockEntity::new, TFCBlocks.BLOOMERY);
    public static final Id<BloomBlockEntity> BLOOM = register("bloom", BloomBlockEntity::new, TFCBlocks.BLOOM);
    public static final Id<PowderkegBlockEntity> POWDERKEG = register("powderkeg", PowderkegBlockEntity::new, TFCBlocks.POWDERKEG);
    public static final Id<TFCChestBlockEntity> CHEST = register("chest", TFCChestBlockEntity::new, woodBlocks(Wood.BlockType.CHEST));
    public static final Id<TFCTrappedChestBlockEntity> TRAPPED_CHEST = register("trapped_chest", TFCTrappedChestBlockEntity::new, woodBlocks(Wood.BlockType.TRAPPED_CHEST));
    public static final Id<BarrelBlockEntity> BARREL = register("barrel", BarrelBlockEntity::new, woodBlocks(Wood.BlockType.BARREL));
    public static final Id<LoomBlockEntity> LOOM = register("loom", LoomBlockEntity::new, woodBlocks(Wood.BlockType.LOOM));
    public static final Id<SluiceBlockEntity> SLUICE = register("sluice", SluiceBlockEntity::new, woodBlocks(Wood.BlockType.SLUICE));
    public static final Id<ToolRackBlockEntity> TOOL_RACK = register("tool_rack", ToolRackBlockEntity::new, woodBlocks(Wood.BlockType.TOOL_RACK));
    public static final Id<BookshelfBlockEntity> BOOKSHELF = register("bookshelf", BookshelfBlockEntity::new, woodBlocks(Wood.BlockType.BOOKSHELF));
    public static final Id<TFCSignBlockEntity> SIGN = register("sign", TFCSignBlockEntity::new, Stream.concat(
        woodBlocks(Wood.BlockType.SIGN),
        woodBlocks(Wood.BlockType.WALL_SIGN)
    ));
    public static final Id<TFCHangingSignBlockEntity> HANGING_SIGN = register("hanging_sign", TFCHangingSignBlockEntity::new, Stream.of(
        TFCBlocks.CEILING_HANGING_SIGNS,
        TFCBlocks.WALL_HANGING_SIGNS
    ).flatMap(woodMap -> woodMap.values().stream().flatMap(metalMap -> metalMap.values().stream())));
    public static final Id<LampBlockEntity> LAMP = register("lamp", LampBlockEntity::new, TFCBlocks.METALS.values().stream().filter(map -> map.get(Metal.BlockType.LAMP) != null).map(map -> map.get(Metal.BlockType.LAMP)));
    public static final Id<ThatchBedBlockEntity> THATCH_BED = register("thatch_bed", ThatchBedBlockEntity::new, TFCBlocks.THATCH_BED);
    public static final Id<BerryBushBlockEntity> BERRY_BUSH = register("berry_bush", BerryBushBlockEntity::new, Stream.of(
        List.of(
            TFCBlocks.BANANA_PLANT,
            TFCBlocks.CRANBERRY_BUSH
        ),
        TFCBlocks.SPREADING_BUSHES.values(),
        TFCBlocks.SPREADING_CANES.values(),
        TFCBlocks.STATIONARY_BUSHES.values(),
        TFCBlocks.FRUIT_TREE_LEAVES.values()
    ).flatMap(Collection::stream));
    public static final Id<CropBlockEntity> CROP = register("crop", CropBlockEntity::new, TFCBlocks.CROPS.values().stream());
    public static final Id<DecayingBlockEntity> DECAYING = register("decaying", DecayingBlockEntity::new, Stream.of(TFCBlocks.MELON, TFCBlocks.PUMPKIN));
    public static final Id<NestBoxBlockEntity> NEST_BOX = register("nest_box", NestBoxBlockEntity::new, TFCBlocks.NEST_BOX);
    public static final Id<LargeVesselBlockEntity> LARGE_VESSEL = register("large_vessel", LargeVesselBlockEntity::new, Stream.concat(
        Stream.of(TFCBlocks.LARGE_VESSEL),
        TFCBlocks.GLAZED_LARGE_VESSELS.values().stream()
    ));
    public static final Id<LecternBlockEntity> LECTERN = register("lectern", TFCLecternBlockEntity::new, woodBlocks(Wood.BlockType.LECTERN));
    public static final Id<AnvilBlockEntity> ANVIL = register("anvil", AnvilBlockEntity::new, Stream.concat(
        TFCBlocks.ROCK_ANVILS.values().stream(),
        TFCBlocks.METALS.values().stream().map(m -> m.get(Metal.BlockType.ANVIL)).filter(Objects::nonNull)
    ));
    public static final Id<SheetPileBlockEntity> SHEET_PILE = register("sheet_pile", SheetPileBlockEntity::new, TFCBlocks.SHEET_PILE);
    public static final Id<IngotPileBlockEntity> INGOT_PILE = register("ingot_pile", IngotPileBlockEntity::new, Stream.of(TFCBlocks.INGOT_PILE, TFCBlocks.DOUBLE_INGOT_PILE));
    public static final Id<BlastFurnaceBlockEntity> BLAST_FURNACE = register("blast_furnace", BlastFurnaceBlockEntity::new, TFCBlocks.BLAST_FURNACE);
    public static final Id<TFCBellBlockEntity> BELL = register("bell", TFCBellBlockEntity::new, Stream.of(TFCBlocks.BRONZE_BELL, TFCBlocks.BRASS_BELL));
    public static final Id<AxleBlockEntity> AXLE = register("axle", AxleBlockEntity::new, woodBlocks(Wood.BlockType.AXLE));
    public static final Id<BladedAxleBlockEntity> BLADED_AXLE = register("bladed_axle", BladedAxleBlockEntity::new, woodBlocks(Wood.BlockType.BLADED_AXLE));
    public static final Id<AxleBlockEntity> CLUTCH = register("clutch", ClutchBlockEntity::new, woodBlocks(Wood.BlockType.CLUTCH));
    public static final Id<EncasedAxleBlockEntity> ENCASED_AXLE = register("encased_axle", EncasedAxleBlockEntity::new, woodBlocks(Wood.BlockType.ENCASED_AXLE));
    public static final Id<GearBoxBlockEntity> GEAR_BOX = register("gear_box", GearBoxBlockEntity::new, woodBlocks(Wood.BlockType.GEAR_BOX));
    public static final Id<WindmillBlockEntity> WINDMILL = register("windmill", WindmillBlockEntity::new, woodBlocks(Wood.BlockType.WINDMILL));
    public static final Id<WaterWheelBlockEntity> WATER_WHEEL = register("water_wheel", WaterWheelBlockEntity::new, woodBlocks(Wood.BlockType.WATER_WHEEL));
    public static final Id<CrankshaftBlockEntity> CRANKSHAFT = register("crankshaft", CrankshaftBlockEntity::new, TFCBlocks.CRANKSHAFT);
    public static final Id<TripHammerBlockEntity> TRIP_HAMMER = register("trip_hammer", TripHammerBlockEntity::new, TFCBlocks.TRIP_HAMMER);
    public static final Id<PumpBlockEntity> PUMP = register("pump", PumpBlockEntity::new, TFCBlocks.STEEL_PUMP);

    private static <T extends BlockEntity> Id<T> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, Supplier<? extends Block> block)
    {
        return new Id<>(RegistrationHelpers.register(BLOCK_ENTITIES, name, factory, block));
    }

    private static <T extends BlockEntity> Id<T> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, Stream<? extends Supplier<? extends Block>> blocks)
    {
        return new Id<>(RegistrationHelpers.register(BLOCK_ENTITIES, name, factory, blocks));
    }

    private static Stream<? extends Supplier<? extends Block>> woodBlocks(Wood.BlockType type)
    {
        return TFCBlocks.WOODS.values().stream().map(map -> map.get(type));
    }
    
    public record Id<T extends BlockEntity>(DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> holder)
        implements RegistryHolder<BlockEntityType<?>, BlockEntityType<T>> {}
}
