/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("unused")
public final class TFCBlockEntities
{
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MOD_ID);

    public static final RegistryObject<BlockEntityType<FarmlandBlockEntity>> FARMLAND = register("farmland", FarmlandBlockEntity::new, TFCBlocks.SOIL.get(SoilBlockType.FARMLAND).values().stream());
    public static final RegistryObject<BlockEntityType<PileBlockEntity>> PILE = register("pile", PileBlockEntity::new, Stream.of(TFCBlocks.ICE_PILE, TFCBlocks.SNOW_PILE));
    public static final RegistryObject<BlockEntityType<FirepitBlockEntity>> FIREPIT = register("firepit", FirepitBlockEntity::new, TFCBlocks.FIREPIT);
    public static final RegistryObject<BlockEntityType<GrillBlockEntity>> GRILL = register("grill", GrillBlockEntity::new, TFCBlocks.GRILL);
    public static final RegistryObject<BlockEntityType<PotBlockEntity>> POT = register("pot", PotBlockEntity::new, TFCBlocks.POT);

    public static final RegistryObject<BlockEntityType<TickCounterBlockEntity>> TICK_COUNTER = register("tick_counter", TickCounterBlockEntity::new, Stream.of(
        TFCBlocks.WOODS.values().stream().map(map -> map.get(Wood.BlockType.SAPLING)),
        TFCBlocks.FRUIT_TREE_SAPLINGS.values(),
        TFCBlocks.FRUIT_TREE_GROWING_BRANCHES.values(),
        TFCBlocks.TORCH,
        TFCBlocks.WALL_TORCH,
        TFCBlocks.DEAD_BERRY_BUSH,
        TFCBlocks.DEAD_CANE,
        TFCBlocks.BANANA_SAPLING,
        TFCBlocks.DEAD_BANANA_PLANT
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
    public static final RegistryObject<BlockEntityType<TFCChestBlockEntity>> CHEST = register("chest", TFCChestBlockEntity::new, TFCBlocks.WOODS.values().stream().map(map -> map.get(Wood.BlockType.CHEST)));
    public static final RegistryObject<BlockEntityType<TFCTrappedChestBlockEntity>> TRAPPED_CHEST = register("trapped_chest", TFCTrappedChestBlockEntity::new, TFCBlocks.WOODS.values().stream().map(map -> map.get(Wood.BlockType.TRAPPED_CHEST)));
    public static final RegistryObject<BlockEntityType<LoomBlockEntity>> LOOM = register("loom", LoomBlockEntity::new, TFCBlocks.WOODS.values().stream().map(map -> map.get(Wood.BlockType.LOOM)));
    public static final RegistryObject<BlockEntityType<SluiceBlockEntity>> SLUICE = register("sluice", SluiceBlockEntity::new, TFCBlocks.WOODS.values().stream().map(map -> map.get(Wood.BlockType.SLUICE)));
    public static final RegistryObject<BlockEntityType<ToolRackBlockEntity>> TOOL_RACK = register("tool_rack", ToolRackBlockEntity::new, TFCBlocks.WOODS.values().stream().map(map -> map.get(Wood.BlockType.TOOL_RACK)));
    public static final RegistryObject<BlockEntityType<TFCSignBlockEntity>> SIGN = register("sign", TFCSignBlockEntity::new, TFCBlocks.WOODS.values().stream().flatMap(map -> Stream.of(Wood.BlockType.SIGN, Wood.BlockType.WALL_SIGN).map(map::get)));
    public static final RegistryObject<BlockEntityType<LampBlockEntity>> LAMP = register("lamp", LampBlockEntity::new, TFCBlocks.METALS.values().stream().filter(map -> map.get(Metal.BlockType.LAMP) != null).map(map -> map.get(Metal.BlockType.LAMP)));
    public static final RegistryObject<BlockEntityType<ThatchBedBlockEntity>> THATCH_BED = register("thatch_bed", ThatchBedBlockEntity::new, TFCBlocks.THATCH_BED);

    public static final RegistryObject<BlockEntityType<BerryBushBlockEntity>> BERRY_BUSH = register("berry_bush", BerryBushBlockEntity::new, Stream.of(TFCBlocks.BANANA_PLANT, TFCBlocks.CRANBERRY_BUSH, TFCBlocks.SPREADING_BUSHES.values(), TFCBlocks.SPREADING_CANES.values(), TFCBlocks.STATIONARY_BUSHES.values(), TFCBlocks.FRUIT_TREE_LEAVES.values()).<Supplier<? extends Block>>flatMap(Helpers::flatten));
    public static final RegistryObject<BlockEntityType<CropBlockEntity>> CROP = register("crop", CropBlockEntity::new, TFCBlocks.CROPS.values().stream());

    private static final Logger LOGGER = LogManager.getLogger();

    public static boolean validateBlockEntities()
    {
        final List<Block> fbeButNoEbe = new ArrayList<>(), ebeButNoFbe = new ArrayList<>(), ebButNoEbe = new ArrayList<>();
        Helpers.streamOurs(ForgeRegistries.BLOCKS)
            .forEach(b -> {
                if (b instanceof IForgeBlockExtension ex && ex.getExtendedProperties().hasBlockEntity() && !(b instanceof EntityBlockExtension))
                {
                    fbeButNoEbe.add(b);
                }
                if (b instanceof EntityBlockExtension && (!(b instanceof IForgeBlockExtension ex) || !ex.getExtendedProperties().hasBlockEntity()))
                {
                    ebeButNoFbe.add(b);
                }
                if (b instanceof EntityBlock && !(b instanceof EntityBlockExtension))
                {
                    ebButNoEbe.add(b);
                }
            });

        return logValidationErrors("Blocks found that declare a block entity in IForgeBlockExtension but do not implement EntityBlockExtension", fbeButNoEbe)
            | logValidationErrors("Blocks found that implement EntityBlockExtension but do not declare a block entity in IForgeBlockExtension", ebeButNoFbe)
            | logValidationErrors("Blocks found that implement EntityBlock but do not implement EntityBlockExtension", ebButNoEbe);
    }

    private static boolean logValidationErrors(String error, List<Block> blocks)
    {
        if (!blocks.isEmpty())
        {
            LOGGER.error(error);
            blocks.forEach(b -> LOGGER.error("{} of {}", b.getRegistryName(), b.getClass().getSimpleName()));
            return true;
        }
        return false;
    }

    @SuppressWarnings("ConstantConditions")
    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, Supplier<? extends Block> block)
    {
        return TILE_ENTITIES.register(name, () -> BlockEntityType.Builder.of(factory, block.get()).build(null));
    }

    @SuppressWarnings("ConstantConditions")
    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, Stream<? extends Supplier<? extends Block>> blocks)
    {
        return TILE_ENTITIES.register(name, () -> BlockEntityType.Builder.of(factory, blocks.map(Supplier::get).toArray(Block[]::new)).build(null));
    }
}
